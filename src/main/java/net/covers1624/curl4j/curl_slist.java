package net.covers1624.curl4j;

import net.covers1624.curl4j.core.Pointer;
import net.covers1624.curl4j.core.Struct;
import org.jetbrains.annotations.Nullable;

/**
 * curl_slist struct.
 *
 * @author covers1624
 * @see CURL#curl_slist_append
 * @see CURL#curl_slist_free_all
 */
public class curl_slist extends Pointer {

    private static final Struct STRUCT = new Struct();

    public static final Struct.Member<@Nullable String> DATA = STRUCT.stringMember("data");
    public static final Struct.Member<@Nullable curl_slist> NEXT = STRUCT.structPointerMember("next", p -> new curl_slist(p.address));

    public curl_slist(long address) {
        super(address);
    }

    @Nullable
    public String data() {
        return DATA.read(this);
    }

    @Nullable
    public curl_slist next() {
        return NEXT.read(this);
    }
}
