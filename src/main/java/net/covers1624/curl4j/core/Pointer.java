package net.covers1624.curl4j.core;

/**
 * Created by covers1624 on 16/8/23.
 */
public class Pointer {

    public final long address;

    public Pointer(long address) {
        this.address = address;
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
