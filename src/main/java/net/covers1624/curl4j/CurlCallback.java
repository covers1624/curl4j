package net.covers1624.curl4j;

import java.lang.foreign.Arena;
import java.lang.foreign.FunctionDescriptor;
import java.lang.foreign.Linker;
import java.lang.foreign.MemorySegment;
import java.lang.invoke.MethodHandle;
import java.util.function.Consumer;

/**
 * Parent class to all CURL callbacks.
 *
 * @author covers1624
 */
public abstract class CurlCallback {

    private static final Linker LINKER = Linker.nativeLinker();

    // Keep arena in scope for GC cleanup.
    @SuppressWarnings ("FieldCanBeLocal")
    private final Arena arena = Arena.ofAuto();
    private final MemorySegment address;

    protected Consumer<Throwable> exceptionHandler = Throwable::printStackTrace;

    protected CurlCallback(CallbackDescriptor desc) {
        address = LINKER.upcallStub(desc.callback.bindTo(this), desc.nativeFunction, arena);
    }

    /**
     * Override the default exception handler for this callback.
     * <p>
     * This handler must not re-throw the exception, during callbacks from native,
     * any unhandled exceptions will force the VM to exit.
     *
     * @param ex The callback.
     */
    public void setExceptionHandler(Consumer<Throwable> ex) {
        exceptionHandler = ex;
    }

    /**
     * @return the native function address for this callback.
     */
    public MemorySegment address() {
        return address;
    }

    /**
     * Called when an exception is thrown whilst executing a callback.
     * <p>
     * This may be overridden for a custom implementation, however, must NEVER throw.
     * If this function throws an exception, THE VM WILL CRASH.
     *
     * @param ex The exception.
     */
    protected void handleCallbackException(Throwable ex) {
        try {
            exceptionHandler.accept(ex);
        } catch (Throwable ex2) {
            // TODO log this better somehow?
            ex2.printStackTrace();
        }
    }

    /**
     * Represents the callbacks and descriptors for a native callback.
     *
     * @param nativeFunction The native function descriptor.
     * @param callback       The callback method to invoke.
     *                       This will be bound with the {@link CurlCallback} instance.
     */
    protected record CallbackDescriptor(
            FunctionDescriptor nativeFunction,
            MethodHandle callback
    ) {

        public static CallbackDescriptor create(String prototype, Factory factory) {
            try {
                return new CallbackDescriptor(
                        LibCurl.SYMBOL_RESOLVER.resolveFunction(LibCurl.SYMBOL_RESOLVER.parse(prototype, "")),
                        factory.getHandle()
                );
            } catch (IllegalAccessException | NoSuchMethodException ex) {
                throw new RuntimeException("Unable to construct callback descriptor.", ex);
            }
        }
    }

    protected interface Factory {

        MethodHandle getHandle() throws IllegalAccessException, NoSuchMethodException;
    }
}
