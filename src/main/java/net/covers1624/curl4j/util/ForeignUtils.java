package net.covers1624.curl4j.util;

import org.jetbrains.annotations.Contract;

import java.lang.foreign.MemorySegment;
import java.util.LinkedHashSet;
import java.util.Set;

import static java.lang.foreign.ValueLayout.ADDRESS;

/**
 * @author covers1624
 */
public class ForeignUtils {

    /**
     * Read a null-terminated UTF-8 string from the given memory address.
     * <p>
     * The caller is responsible for ensuring the string has a null terminator, otherwise a
     * native exception could be raised.
     *
     * @param seg A pointer to the first character in the string.
     * @return The string.
     */
    public static String readNTString(MemorySegment seg) {
        return seg.reinterpret(Long.MAX_VALUE).getString(0);
    }

    /**
     * Reads an array of null-terminated UTF-8 Strings from the given memory address.
     *
     * @param seg The pointer to the first string pointer in the list.
     * @return The resulting strings.
     */
    public static Set<String> readNTStringArray(MemorySegment seg) {
        seg = seg.reinterpret(Long.MAX_VALUE);
        Set<String> protocols = new LinkedHashSet<>();
        for (int i = 0; true; i++) {
            MemorySegment addr = seg.getAtIndex(ADDRESS, i);
            if (addr.address() == 0) break;
            protocols.add(readNTString(addr));
        }
        return protocols;
    }

    /**
     * Rethrows the given checked or unchecked exception as unchecked.
     * <p>
     * THIS METHOD NEVER RETURNS ANYTHING.
     * <p>
     * This method does not return a {@link RuntimeException}, this
     * return type is provided for the convenience of writing
     * {@code throw rethrowUnchecked(ex);}
     *
     * @param ex The exception to rethrow.
     * @return Never returns anything.
     */
    @Contract ("_->fail")
    public static <T extends Throwable> RuntimeException rethrowUnchecked(Throwable ex) throws T {
        throw (T) ex;
    }
}
