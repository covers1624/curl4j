package net.covers1624.curl4j.core;

/**
 * Created by covers1624 on 15/8/23.
 */
public abstract class Callback implements AutoCloseable {

    protected static final long ffi_type_pointer = ffi_type_pointer();
    protected static final long ffi_type_int = ffi_type_int();
    protected static final long ffi_type_long = ffi_type_long();

    private final long cif;
    private final long callback;
    private final CallbackInterface delegate;

    private long closure = Memory.NULL;
    private long delegateRef = Memory.NULL;
    private long code = Memory.NULL;

    protected Callback(long cif, long callback, CallbackInterface delegate) {
        this.cif = cif;
        this.callback = callback;
        this.delegate = delegate;
    }

    public final long getFunctionAddress() {
        if (closure == Memory.NULL) {
            try (Memory.Stack stack = Memory.pushStack()) {
                Pointer codePtr = stack.mallocPointer();
                closure = ffi_closure_alloc(codePtr.address);
                if (closure == Memory.NULL) throw new OutOfMemoryError("Unable to alloc ffi closure");

                code = codePtr.readAddress();
            }
            delegateRef = Memory.newGlobalRef(delegate);
            int ret = ffi_prep_closure_loc(closure, cif, callback, delegateRef, code);
            if (ret != 0 /* FFI_OK */) {
                close();
                throw new RuntimeException("ffi_prep_closure_loc failed. Code: " + ret);
            }
        }
        return code;
    }

    @Override
    public final void close() {
        if (closure != Memory.NULL) {
            ffi_closure_free(closure);
            closure = Memory.NULL;
            code = Memory.NULL;
            Memory.deleteGlobalRef(delegateRef);
            delegateRef = Memory.NULL;
        }
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

    public interface CallbackInterface {
    }
}
