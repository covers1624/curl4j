package net.covers1624.curl4j;

import org.jetbrains.annotations.Nullable;

import java.lang.foreign.MemorySegment;
import java.lang.foreign.StructLayout;
import java.lang.invoke.VarHandle;

import static java.lang.foreign.MemoryLayout.PathElement.groupElement;
import static net.covers1624.curl4j.util.ForeignUtils.readNTString;

/**
 * curl_slist struct.
 *
 * @author covers1624
 * @see CURL#curl_slist_append
 * @see CURL#curl_slist_free_all
 */
public record curl_slist(MemorySegment address) {

    public static final StructLayout CURL_SLIST = LibCurl.STRUCT_PARSER.parseStruct("""
            struct curl_slist {
                char *data;
                struct curl_slist *next;
            };
            """);

    public static final VarHandle DATA = CURL_SLIST.varHandle(groupElement("data"));
    public static final VarHandle NEXT = CURL_SLIST.varHandle(groupElement("next"));

    public curl_slist {
        address = address.reinterpret(CURL_SLIST.byteSize());
    }

    public String data() {
        return readNTString((MemorySegment) DATA.get(address, 0));
    }

    @Nullable
    public curl_slist next() {
        MemorySegment nextPtr = (MemorySegment) NEXT.get(address, 0);
        return !nextPtr.equals(MemorySegment.NULL) ? new curl_slist(nextPtr) : null;
    }
}
