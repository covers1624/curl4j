/*
 * This file is part of Quack and is Licensed under the MIT License.
 */
package net.covers1624.curl4j.httpapi;

import net.covers1624.curl4j.core.Memory;
import net.covers1624.curl4j.core.Pointer;

import java.nio.ByteBuffer;

/**
 * Created by covers1624 on 30/1/24.
 */
class NativeBuffer extends Pointer implements AutoCloseable {

    public final long len;
    public final ByteBuffer buffer;

    public NativeBuffer(long size) {
        this(Memory.malloc(size), size);
    }

    private NativeBuffer(long address, long len) {
        super(address);
        this.len = len;
        buffer = Memory.newDirectByteBuffer(address, (int) this.len);
    }

    public NativeBuffer newRealloc(long newSize) {
        return new NativeBuffer(Memory.realloc(address, newSize), newSize);
    }

    @Override
    public void close() {
        Memory.free(address);
    }
}
