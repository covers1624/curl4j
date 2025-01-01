package net.covers1624.curl4j;

import java.lang.foreign.Arena;
import java.lang.foreign.MemorySegment;
import java.lang.foreign.StructLayout;
import java.lang.invoke.VarHandle;

import static java.lang.foreign.MemoryLayout.PathElement.groupElement;

/**
 * @author covers1624
 */
public record curl_blob(MemorySegment address) {

    /**
     * Tell libcurl to copy the data
     */
    public static final int CURL_BLOB_COPY = 1;
    /**
     * Tell libcurl to NOT copy the data
     */
    public static final int CURL_BLOB_NOCOPY = 0;

    public static StructLayout CURL_BLOB = LibCurl.STRUCT_PARSER.parseStruct("""
            struct curl_blob {
                void *data;
                size_t len;
                unsigned int flags; /* bit 0 is defined, the rest are reserved and should be left zeroes */
            };
            """);

    public static final VarHandle DATA = CURL_BLOB.varHandle(groupElement("data"));
    public static final VarHandle LEN = CURL_BLOB.varHandle(groupElement("len"));
    public static final VarHandle FLAGS = CURL_BLOB.varHandle(groupElement("flags"));

    public curl_blob {
        address = address.reinterpret(CURL_BLOB.byteSize());
    }

    public curl_blob(Arena arena) {
        this(arena.allocate(CURL_BLOB));
    }

    // @formatter:off
    public MemorySegment getData() { return (MemorySegment) DATA.get(address, 0); }
    public long getLen() { return (long) LEN.get(address, 0); }
    public int getFlags() { return (int) FLAGS.get(address, 0); }
    public void setData(MemorySegment value) { DATA.set(address, 0, value); }
    public void setLen(Long value) { LEN.set(address, 0, value); }
    public void setFlags(Integer value) { FLAGS.set(address, 0, value); }
    // @formatter:on
}
