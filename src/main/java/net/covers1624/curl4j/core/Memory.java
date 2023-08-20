package net.covers1624.curl4j.core;

import sun.misc.Unsafe;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.nio.ByteBuffer;

/**
 * Created by covers1624 on 14/8/23.
 */
public final class Memory {

    private static final Unsafe UNSAFE;

    // 64k per stack.
    private static final ThreadLocal<Stack> STACKS = ThreadLocal.withInitial(() -> new Stack(64 * 1024));

    static {
        LibraryLoader.initialize();
        UNSAFE = getUnsafe();
    }

    /**
     * The NULL native pointer address.
     */
    public static final long NULL = 0L;

    private Memory() { }

    /**
     * Get a stack for the current thread.
     *
     * @return The stack.
     */
    public static Stack getStack() {
        return STACKS.get();
    }

    /**
     * Get a stack for the current thread and push.
     *
     * @return The stack.
     */
    public static Stack pushStack() {
        return STACKS.get().push();
    }

    // @formatter:off
    public static boolean getBoolean(long ptr) { return UNSAFE.getByte(null, ptr) != 0; }
    public static byte getByte(long ptr) { return UNSAFE.getByte(null, ptr); }
    public static short getShort(long ptr) { return UNSAFE.getShort(null, ptr); }
    public static int getInt(long ptr) { return UNSAFE.getInt(null, ptr); }
    public static long getLong(long ptr) { return UNSAFE.getLong(null, ptr); }
    public static float getFloat(long ptr) { return UNSAFE.getFloat(null, ptr); }
    public static double getDouble(long ptr) { return UNSAFE.getDouble(null, ptr); }
    public static long getCLong(long ptr) { return NativeTypes.CLONG_SIZE == 8 ? getLong(ptr) : getInt(ptr); }
    public static long getAddress(long ptr) { return NativeTypes.IS_64BIT ? getLong(ptr) : getInt(ptr); }
    public static void putByte(long ptr, byte value) { UNSAFE.putByte(null, ptr, value); }
    public static void putShort(long ptr, short value) { UNSAFE.putShort(null, ptr, value); }
    public static void putInt(long ptr, int value) { UNSAFE.putInt(null, ptr, value); }
    public static void putLong(long ptr, long value) { UNSAFE.putLong(null, ptr, value); }
    public static void putFloat(long ptr, float value) { UNSAFE.putFloat(null, ptr, value); }
    public static void putDouble(long ptr, double value) { UNSAFE.putDouble(null, ptr, value); }
    public static void putCLong(long ptr, long value) { if (NativeTypes.CLONG_SIZE == 8) putLong(ptr, value); else putInt(ptr, (int) value); }
    public static void putAddress(long ptr, long value) { if (NativeTypes.IS_64BIT) putLong(ptr, value); else putInt(ptr, (int) value); }
    // @formatter:on

    /**
     * Read the given null terminated UTF-8 string into a
     * regular Java String.
     *
     * @param buffer The string to read.
     * @return The
     */
    public static native String readUtf8(long buffer);

    /**
     * Create a new DirectByteBuffer with the given address and capacity.
     *
     * @param address  The address.
     * @param capacity The capacity.
     * @return The buffer.
     */
    public static native ByteBuffer newDirectByteBuffer(long address, int capacity);

    /**
     * Get the native address of a given DirectByteBuffer.
     *
     * @param buffer The buffer.
     * @return The address.
     */
    public static native long getDirectByteBufferAddress(ByteBuffer buffer);

    public static native long newGlobalRef(Object obj);

    public static native void deleteGlobalRef(long ref);

    public static native <T> T getGlobalRefValue(long ref);

    /**
     * A simple memory stack with AutoClosable/Try-With-Resources support.
     */
    public static class Stack implements AutoCloseable {

        // Hold buffer, DirectByteBuffer deallocates its native memory when its GC'd
        @SuppressWarnings ("FieldCanBeLocal")
        private final ByteBuffer data;
        public final long address;
        public final int size;

        private int ptr;

        private int stackIdx;
        private final int[] stack = new int[Integer.getInteger("net.covers1624.libcurl4j.stack_size", 16)];

        private Stack(int size) {
            this(ByteBuffer.allocateDirect(size));
        }

        private Stack(ByteBuffer data) {
            this.data = data;
            address = getDirectByteBufferAddress(data);
            size = data.remaining();

            ptr = size;
        }

        public Stack push() {
            if (stackIdx == stack.length) {
                throw new IndexOutOfBoundsException("Stack too deep. Expand with 'net.covers1624.libcurl4j.stack_size' System Property.");
            }
            stack[stackIdx++] = ptr;
            return this;
        }

        public Stack pop() {
            if (stackIdx == 0) {
                throw new IndexOutOfBoundsException("Can't pop and unpushed stack. You have misaligned push/pop's");
            }
            ptr = stack[--stackIdx];
            return this;
        }

        @Override
        @SuppressWarnings ("resource") // Go away.
        public void close() {
            pop();
        }

        /**
         * How many bytes the stack has remaining.
         *
         * @return The number of remaining bytes.
         */
        public int remainingBytes() {
            return ptr;
        }

        /**
         * Get the current mem address in the stack.
         *
         * @return The address.
         */
        public long getPointerAddress() {
            return address + ptr;
        }

        /**
         * Allocate a block of memory off the stack.
         *
         * @param size The amount of bytes to allocate.
         * @return Pointer to allocated block.
         */
        public long nmalloc(int size) {
            if (ptr - size < 0) {
                throw new OutOfMemoryError("Out of stack space.");
            }
            ptr -= size;
            return getPointerAddress();
        }

        /**
         * Create a buffer of a specific size on this stack.
         *
         * @param size The size.
         * @return The buffer.
         */
        public ByteBuffer malloc(int size) {
            return newDirectByteBuffer(nmalloc(size), size);
        }

        /**
         * Allocate a single pointer on the stack.
         *
         * @return The pointer.
         */
        public Pointer mallocPointer() {
            return new Pointer(nmalloc(NativeTypes.POINTER_SIZE));
        }
    }

    private static Unsafe getUnsafe() {
        try {
            int mod = Modifier.STATIC | Modifier.FINAL;
            for (Field field : Unsafe.class.getDeclaredFields()) {
                if ((field.getModifiers() & mod) != mod) continue;
                if (!field.getType().equals(Unsafe.class)) continue;

                field.setAccessible(true);
                return (Unsafe) field.get(null);
            }
        } catch (Throwable ex) {
            throw new UnsupportedOperationException("Failed to get Unsafe instance.", ex);
        }
        throw new UnsupportedOperationException("libcurl4j requires sun.misc.Unsafe.");
    }
}
