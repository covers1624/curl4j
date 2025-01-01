package net.covers1624.curl4j;

import java.lang.foreign.MemorySegment;
import java.lang.foreign.StructLayout;
import java.lang.invoke.VarHandle;

import static java.lang.foreign.MemoryLayout.PathElement.groupElement;

/**
 * @author covers1624
 */
public record CURLMsg(MemorySegment address) {

    // TODO support Unions in structs.
    public static final StructLayout CURL_MSG = LibCurl.STRUCT_PARSER.parseStruct("""
            struct CURLMsg {
                CURLMSG msg;       /* what this message means */
                CURL *easy_handle; /* the handle it concerns */
                jlong data;
                //union {
                //  void *whatever;    /* message-specific data */
                //  CURLcode result;   /* return code for transfer */
                //} data;
            };
            """);

    public static final VarHandle MSG = CURL_MSG.varHandle(groupElement("msg"));
    public static final VarHandle EASY_HANDLE = CURL_MSG.varHandle(groupElement("easy_handle"));
    public static final VarHandle DATA = CURL_MSG.varHandle(groupElement("data"));

    public CURLMsg {
        address = address.reinterpret(CURL_MSG.byteSize());
    }

    // @formatter:off
    public int msg() { return (int) MSG.get(address, 0); }
    public MemorySegment easy_handle() { return (MemorySegment) EASY_HANDLE.get(address, 0); }
    public long data() { return (long) DATA.get(address, 0); }
    // @formatter:on
}
