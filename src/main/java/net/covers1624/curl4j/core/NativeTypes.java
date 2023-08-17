package net.covers1624.curl4j.core;

/**
 * Created by covers1624 on 14/8/23.
 */
public final class NativeTypes {

    private NativeTypes() { }

    static {
        LibraryLoader.initialize();
    }

    public static final int POINTER_SIZE = pointerSize();
    public static final int CINT_SIZE = getIntSize();
    public static final int CLONG_SIZE = getLongSize();
    public static final int SIZE_T_SIZE = getSizeTSize();

    public static final boolean IS_32BIT = POINTER_SIZE * 8 == 32;
    public static final boolean IS_64BIT = POINTER_SIZE * 8 == 64;

    // sizeof(void *)
    private static native int pointerSize();

    // sizeof(int)
    private static native int getIntSize();

    // sizeof(long)
    private static native int getLongSize();

    // sizeof(size_t)
    private static native int getSizeTSize();
}
