package net.covers1624.curl4j.core;

import org.junit.jupiter.api.Test;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

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
    public void testStrings() {
        ByteBuffer buffer = ByteBuffer.allocateDirect(32);
        long addr = Memory.getDirectByteBufferAddress(buffer);
        buffer.put((byte) 'H')
                .put((byte) 'e')
                .put((byte) 'l')
                .put((byte) 'l')
                .put((byte) 'o')
                .put((byte) ' ')
                .put((byte) 'W')
                .put((byte) 'o')
                .put((byte) 'r')
                .put((byte) 'l')
                .put((byte) 'd')
                .put((byte) '\0');
        buffer.position(0);

        assertEquals("Hello World", Memory.readUtf8(addr));

        buffer.put(11, (byte) 'A'); // Destroy the null byte.
        assertEquals("Hello World", Memory.readUtf8(addr, 11));
    }

    @Test
    public void regressionStringReadOverflow() {
        try (Memory.Stack stack = Memory.pushStack()) {
            byte[] garbageBytes = "Hello World Hello World".getBytes(StandardCharsets.UTF_8);
            // We write some non-null terminated data onto the stack.
            stack.malloc(garbageBytes.length).put(garbageBytes);

            // Create a new non-null terminated buffer a string.
            ByteBuffer buff = ByteBuffer.allocateDirect(12);
            buff.put("Hello World".getBytes(StandardCharsets.UTF_8));
            // Read 12 bytes from the address of the buff.
            // This should copy the data onto the stack, then append a null byte and let jni read the string.
            assertEquals("Hello World", Memory.readUtf8(Memory.getDirectByteBufferAddress(buff), 12));
        }
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
