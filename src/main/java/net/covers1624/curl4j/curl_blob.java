package net.covers1624.curl4j;

import net.covers1624.curl4j.core.Pointer;
import net.covers1624.curl4j.core.Struct;

import java.nio.ByteBuffer;

/**
 * @author covers1624
 */
public class curl_blob extends Pointer {

    /**
     * Tell libcurl to copy the data
     */
    public static final int CURL_BLOB_COPY = 1;
    /**
     * Tell libcurl to NOT copy the data
     */
    public static final int CURL_BLOB_NOCOPY = 0;

    private static final Struct STRUCT = new Struct("curl_blob");

    public static final Struct.Member<Pointer> DATA = STRUCT.pointerMember("data");
    public static final Struct.Member<Long> LEN = STRUCT.sizeTMember("len");
    public static final Struct.Member<Integer> FLAGS = STRUCT.intMember("flags");

    // Held onto to avoid GCing Pointer's which are managed.
    private Pointer data;

    public curl_blob() {
        super(ByteBuffer.allocateDirect(STRUCT.getSize()));
    }

    public curl_blob(long address) {
        super(address);
    }

    // @formatter:off
    public Long getLen() { return LEN.read(this); }
    public Integer getFlags() { return FLAGS.read(this); }
    public void setLen(Long value) { LEN.write(this, value); }
    public void setFlags(Integer value) { FLAGS.write(this, value); }
    // @formatter:on

    public Pointer getData() {
        Pointer read = DATA.read(this);
        if (data != null && read.address == data.address) {
            return data;
        }
        data = null;
        return read;
    }

    public void setData(Pointer value) {
        data = value;
        DATA.write(this, value);
    }
}
