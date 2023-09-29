package net.covers1624.curl4j.core;

import org.junit.jupiter.api.Test;

import java.nio.ByteBuffer;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Created by covers1624 on 17/8/23.
 */
public class MemoryTests {

    @Test
    public void testGlobalRef() {
        Object obj = new Object();
        long ref = Memory.newGlobalRef(obj);
        assertEquals(obj, Memory.getGlobalRefValue(ref));
        Memory.deleteGlobalRef(ref);
    }

    @Test
    public void testReadWrite() {
        ByteBuffer buffer = ByteBuffer.allocateDirect(64);
        long addr = Memory.getDirectByteBufferAddress(buffer);

        // Test with int if we actually got the buffers address, or garbage.
        buffer.putInt(0, 0xF0F0F0F0);
        assertEquals(buffer.getInt(0), Memory.getInt(addr));

        // Test each get/put individually
        Memory.putInt(addr, 0);
        assertFalse(Memory.getBoolean(addr));
        Memory.putInt(addr, 1);
        assertTrue(Memory.getBoolean(addr));

        Memory.putByte(addr, (byte) 0xEC);
        assertEquals((byte) 0xEC, Memory.getByte(addr));

        Memory.putShort(addr, (short) 0xECFA);
        assertEquals((short) 0xECFA, Memory.getShort(addr));

        Memory.putInt(addr, 0xECFA69B2);
        assertEquals(0xECFA69B2, Memory.getInt(addr));

        Memory.putLong(addr, 0x7CFA69B27D59AF1CL);
        assertEquals(0x7CFA69B27D59AF1CL, Memory.getLong(addr));

        Memory.putFloat(addr, 17.817649F);
        assertEquals(17.817649F, Memory.getFloat(addr));

        Memory.putDouble(addr, 17.817649374D);
        assertEquals(17.817649374D, Memory.getDouble(addr));

        Memory.putInt(addr, 0xECFA69B2);
        assertEquals(0xECFA69B2, Memory.getInt(addr));

        long clong = NativeTypes.CLONG_SIZE == 8 ? 0x7CFA69B27D59AF1CL : 0x7CFA69B2L;
        Memory.putCLong(addr, clong);
        assertEquals(clong, Memory.getCLong(addr));

        long sizeT = NativeTypes.SIZE_T_SIZE == 8 ? 0x7CFA69B27D59AF1CL : 0x7CFA69B2L;
        Memory.putSizeT(addr, sizeT);
        assertEquals(sizeT, Memory.getSizeT(addr));

        long address = NativeTypes.IS_64BIT ? 0x7CFA69B27D59AF1CL : 0xECFA69B2L;
        Memory.putAddress(addr, address);
        assertEquals(address, Memory.getAddress(addr));
    }

    @Test
    public void testStack() {
        try (Memory.Stack stack = Memory.pushStack()) {
            ByteBuffer buffer = stack.malloc(8);
            long addr = Memory.getDirectByteBufferAddress(buffer);
            buffer.putInt(0, 0xF0F0F0F0);
            assertEquals(buffer.getInt(0), Memory.getInt(addr));
        }
    }
}
