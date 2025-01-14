package net.covers1624.curl4j.core;

import org.jetbrains.annotations.MustBeInvokedByOverriders;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Method;

/**
 * @author covers1624
 */
public abstract class Callback implements AutoCloseable {

    protected static final long ffi_type_pointer = ffi_type_pointer();
    protected static final long ffi_type_int = ffi_type_int();
    protected static final long ffi_type_long = ffi_type_long();

    private static final long builtin_callback = ffi_callback(Reflect.getDeclaredMethod(Callback.class, "ffi_callback", long.class, long.class));

    private final long cif;
    private final long callback;
    private final CallbackInterface delegate;

    private CallbackExceptionHandler exceptionHandler = CallbackExceptionHandler.DEFAULT;

    private long closure = Memory.NULL;
    private long delegateRef = Memory.NULL;
    private long code = Memory.NULL;

    /**
     * Construct a new callback, must be from an overridden class.
     * <p>
     * This method is an overload of {@link #Callback(long, long, CallbackInterface)}
     * which uses the built-in native callback function. This enables using {@link CallbackInterface#invoke(long, long)}.
     *
     * @param cif      The Callback interface for libffi to generate via {@link #ffi_prep_cif}.
     *                 This should be constructed and stored in a static variable.
     * @param delegate The delegate to call, if null is specified, it is expected that this class
     *                 implements {@link CallbackInterface}.
     */
    protected Callback(long cif, @Nullable CallbackInterface delegate) {
        this(cif, builtin_callback, delegate);
    }

    /**
     * Construct a new callback, must be from an overridden class.
     * <p>
     * In a future version, this method may become deprecated.
     *
     * @param cif      The Callback interface for libffi to generate via {@link #ffi_prep_cif}.
     *                 This should be constructed and stored in a static variable.
     * @param callback The native function pointer for libffi to invoke. It is recommended to use
     *                 {@link #Callback(long, CallbackInterface)} instead of custom native callbacks,
     *                 due to the automatic exception handling provided.
     * @param delegate The delegate to call, if null is specified, it is expected that this class
     *                 implements {@link CallbackInterface}.
     */
    protected Callback(long cif, long callback, @Nullable CallbackInterface delegate) {
        this.cif = cif;
        this.callback = callback;
        if (delegate == null) {
            if (!(this instanceof CallbackInterface)) {
                throw new IllegalArgumentException("When delegate is null, expected this object to be a CallbackInterface");
            }
            this.delegate = (CallbackInterface) this;
        } else {
            this.delegate = delegate;
        }
    }

    public final long getFunctionAddress() {
        if (closure == Memory.NULL) {
            try (Memory.Stack stack = Memory.pushStack()) {
                Pointer codePtr = stack.mallocPointer();
                closure = ffi_closure_alloc(codePtr.address);
                if (closure == Memory.NULL) throw new OutOfMemoryError("Unable to alloc ffi closure");

                code = codePtr.readAddress();
            }
            if (callback == builtin_callback) {
                delegateRef = Memory.newGlobalRef(this);
            } else {
                delegateRef = Memory.newGlobalRef(delegate);
            }
            int ret = ffi_prep_closure_loc(closure, cif, callback, delegateRef, code);
            if (ret != 0 /* FFI_OK */) {
                close();
                throw new RuntimeException("ffi_prep_closure_loc failed. Code: " + ret);
            }
        }
        return code;
    }

    /**
     * Set a custom exception handling policy for this callback.
     *
     * @param exceptionHandler The exception handler.
     */
    public final void setExceptionHandler(CallbackExceptionHandler exceptionHandler) {
        this.exceptionHandler = exceptionHandler;
    }

    @Override
    @MustBeInvokedByOverriders
    public void close() {
        if (closure != Memory.NULL) {
            ffi_closure_free(closure);
            closure = Memory.NULL;
            code = Memory.NULL;
            Memory.deleteGlobalRef(delegateRef);
            delegateRef = Memory.NULL;
        }
        if (delegate != this) {
            try {
                delegate.close();
            } catch (Throwable ex) {
                throwUnchecked(ex);
            }
        }
    }

    @SuppressWarnings ("unchecked")
    private static <T extends Throwable> void throwUnchecked(Throwable t) throws T {
        throw (T) t;
    }

    private static native long ffi_type_pointer();

    private static native long ffi_type_int();

    private static native long ffi_type_long();

    private static native long ffi_cif_alloc();

    protected static long ffi_prep_cif(long rtype, long... atypes) {
        long cif = ffi_cif_alloc();
        if (cif == Memory.NULL) throw new OutOfMemoryError();
        int ret = ffi_prep_cif(cif, rtype, atypes);
        if (ret != 0 /* FFI_OK */) {
            // This function is called from static initializers, but lets cleanup
            // our mess anyway.
            ffi_cif_free(cif);
            throw new IllegalStateException("ffi_prep_cif returned code: " + ret);
        }
        return cif;
    }

    protected static native void ffi_cif_free(long cif);

    private static native int ffi_prep_cif(long cif, long rtype, long... atypes);

    private static native long ffi_closure_alloc(long code);

    private static native int ffi_prep_closure_loc(long closure, long cif, long callback, long userdata, long code);

    private static native void ffi_closure_free(long closure);

    private static native long ffi_callback(Method method);

    // Invoked by JNI in native land, simply forwards the callback through.
    private void ffi_callback(long ret, long args) throws Throwable {
        if (delegate == null) {
            throw new IllegalStateException("'delegate' must not be null.");
        }
        try {
            delegate.invoke(ret, args);
        } catch (Throwable ex) {
            exceptionHandler.onException(ex);
        }
    }

    public interface CallbackInterface extends AutoCloseable {

        /**
         * Called from libffi when the callback is invoked.
         * <p>
         * This is only called when the built-in native callback is used.
         * <p>
         * Implementors of {@link CallbackInterface} are expected to implement this if they
         * are using the built-in native callback.
         * <p>
         * It is expected that the built-in callback will become mandatory at some point.
         *
         * @param ret  The return pointer.
         * @param args The pointer to the argument pointers.
         */
        default void invoke(@NativeType ("void *") long ret, @NativeType ("void **") long args) throws Throwable {
            throw new UnsupportedOperationException("Callback must override this function to use the built-in callback.");
        }

        @Override
        default void close() throws Exception { }
    }

    /**
     * Handler for exceptions inside callback functions.
     */
    public interface CallbackExceptionHandler {

        /**
         * The default callback implementation, just log and take no further action.
         * <p>
         * Generally this is okay, but the user should choose a more strict exception policy,
         * such as {@link #RETHROW}, or a custom policy to abort the operation.
         */
        CallbackExceptionHandler DEFAULT = ex -> {
            System.err.println("[curl4j] Exception thrown inside callback, this will silently be ignored.");
            ex.printStackTrace(System.err);
        };

        /**
         * Simply rethrows the exception and lets it propagate through native land.
         * <p>
         * If the callback this is attached to is not a Java owned thread, this policy
         * is not sufficient.
         * <p>
         * Note: Native land DOES NOT know about these exceptions, the underlying native
         * application will continue running, potentially calling the same callback again,
         * resulting in further exceptions. This should ONLY be used in cases where the thread
         * starting the native operation, and the thread which will fire the callbacks, are the
         * same thread, threads not owned by Java will NEVER have this propagate out.
         */
        CallbackExceptionHandler RETHROW = ex -> { throw ex; };

        /**
         * Called when an exception is thrown inside a Callback.
         * <p>
         * It is the callers choice what to do with the exception,
         * silently handle it, abort the native operation, kill the app,
         * rethrow, etc.
         *
         * @param ex The exception.
         */
        void onException(Throwable ex) throws Throwable;
    }
}
