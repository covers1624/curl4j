package net.covers1624.curl4j.core;

/**
 * Created by covers1624 on 14/8/23.
 */
public abstract class Library {

    public final String name;
    public final long address;

    protected Library(String name) throws UnsatisfiedLinkError {
        this.name = name;
        address = open(name);
    }

    public final long getFunction(String name) {
        long func = lookupAddr(name);
        if (func == Memory.NULL) throw new UnsatisfiedLinkError("Unable to find required function: " + name + " in library: " + this.name);

        return func;
    }

    public final long getOptionalFunction(String name) {
        return lookupAddr(name);
    }

    protected abstract long lookupAddr(String name);

    public void free() {
        close();
    }

    /**
     * Open the Library.
     *
     * @param name The library file name or full path.
     * @return The library handle.
     */
    protected abstract long open(String name) throws UnsatisfiedLinkError;

    protected abstract void close();

    public static abstract class UnixLibrary extends Library {

        private static final int RTLD_LAZY = 0x00001;    /* Lazy function call binding.  */
        private static final int RTLD_NOW = 0x00002;    /* Immediate function call binding.  */
        private static final int RTLD_BINDING_MASK = 0x3;    /* Mask of binding time value.  */
        private static final int RTLD_NOLOAD = 0x00004;    /* Do not load the object.  */
        private static final int RTLD_DEEPBIND = 0x00008;    /* Use deep binding.  */

        public UnixLibrary(String name) throws UnsatisfiedLinkError {
            super(name);
        }

        private static native long dlopen(String name, int mode);

        private static native long dlsym(long handle, String name);

        private static native int dlclose(long handle);

        private static native String dlerror();

        @Override
        protected long open(String name) throws UnsatisfiedLinkError {
            long handle = dlopen(name, RTLD_LAZY);
            if (handle == Memory.NULL) {
                throw new UnsatisfiedLinkError("Failed to load dynamically linked library: '" + name + "', Error: " + dlerror());
            }
            return handle;
        }

        @Override
        protected void close() {
            dlclose(address);
        }

        @Override
        protected long lookupAddr(String name) {
            return dlsym(address, name);
        }
    }

    public static class LinuxLibrary extends UnixLibrary {

        public LinuxLibrary(String name) throws UnsatisfiedLinkError {
            super(name);
        }
    }

    public static class MacosLibrary extends UnixLibrary {

        public MacosLibrary(String name) throws UnsatisfiedLinkError {
            super(name);
        }
    }

    public static class WindowsLibrary extends Library {

        public WindowsLibrary(String name) throws UnsatisfiedLinkError {
            super(name);
        }

        @Override
        protected long lookupAddr(String name) {
            return 0;
        }

        @Override
        protected long open(String name) throws UnsatisfiedLinkError {
            return 0;
        }

        @Override
        protected void close() {

        }
    }
}
