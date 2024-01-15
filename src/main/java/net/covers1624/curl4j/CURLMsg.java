package net.covers1624.curl4j;

import net.covers1624.curl4j.core.Pointer;
import net.covers1624.curl4j.core.Struct;

/**
 * @author covers1624
 */
public class CURLMsg extends Pointer {

    private static final Struct STRUCT = new Struct("CURLMsg");

    public final Struct.Member<Integer> MSG = STRUCT.intMember("msg");
    public final Struct.Member<Pointer> EASY_HANDLE = STRUCT.pointerMember("easy_handle");
    // TODO this is actually a union of void * and CURLcode, we should somehow support these.
    //      This is kinda cursed, we just treat it as a long as that's the width of the current union.
    public final Struct.Member<Long> DATA = STRUCT.longMember("data");

    public CURLMsg(long address) {
        super(address);
    }

    // @formatter:off
    public int msg() { return MSG.read(this); }
    public long easy_handle() { return EASY_HANDLE.read(this).address; }
    public long data() { return DATA.read(this); }
    // @formatter:on
}
