package net.covers1624.curl4j.core;

import org.jetbrains.annotations.Nullable;

import java.nio.ByteBuffer;

/**
 * Created by covers1624 on 16/8/23.
 */
public class Pointer {

    // Used for pointers which require automatic native memory cleanup.
    public final @Nullable ByteBuffer buf;
    public final long address;

    /**
     * New pointer with an address not managed by Java.
     *
     * @param address The address.
     */
    public Pointer(long address) {
        buf = null;
        this.address = address;
    }

    /**
     * New pointer with an address managed by Java.
     * <p>
     * These {@link ByteBuffer} instances should come from {@link ByteBuffer#allocateDirect}
     *
     * @param buf The buffer.
     */
    public Pointer(ByteBuffer buf) {
        if (!buf.isDirect()) throw new IllegalArgumentException("Must be a direct buffer");
        this.buf = buf;
        address = Memory.getDirectByteBufferAddress(buf);
    }

    // @formatter:off
    public boolean readBoolean() { return Memory.getBoolean(address); }
    public byte readByte() { return Memory.getByte(address); }
    public short readShort() { return Memory.getShort(address); }
    public int readInt() { return Memory.getInt(address); }
    public long readLong() { return Memory.getLong(address); }
    public float readFloat() { return Memory.getFloat(address); }
    public double readDouble() { return Memory.getDouble(address); }
    public long readCLong() { return Memory.getCLong(address); }
    public long readSizeT() { return Memory.getSizeT(address); }
    public long readAddress() { return Memory.getAddress(address); }
    public Pointer readPointer() { return new Pointer(readAddress()); }
    public String readUtf8Safe() { return Memory.readUtf8(address); }
    // @formatter:on
}
