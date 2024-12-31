package net.covers1624.curl4j.util;

import java.lang.foreign.MemorySegment;
import java.lang.foreign.SymbolLookup;
import java.lang.invoke.MethodHandle;

import static net.covers1624.curl4j.util.ForeignUtils.readNTString;
import static net.covers1624.curl4j.util.ForeignUtils.rethrowUnchecked;

/**
 * @author covers1624
 */
public class LibCurl {

    public final SymbolLookup lookup;
    private final CLikeSymbolLinker linker;

    public final MethodHandle curl_version;

    public LibCurl(SymbolLookup lookup) {
        this.lookup = lookup;
        linker = new CLikeSymbolLinker(lookup);

        curl_version = linker.link("char *curl_version(void);");
    }

    public final String curl_version() {
        try {
            return readNTString((MemorySegment) curl_version.invokeExact());
        } catch (Throwable ex) {
            throw rethrowUnchecked(ex);
        }
    }
}
