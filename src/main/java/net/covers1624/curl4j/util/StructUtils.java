package net.covers1624.curl4j.util;

import java.lang.foreign.MemoryLayout;
import java.lang.foreign.StructLayout;
import java.lang.foreign.ValueLayout;
import java.util.ArrayList;
import java.util.List;

/**
 * @author covers1624
 */
public class StructUtils {

    /**
     * Creates a struct as per {@link MemoryLayout#structLayout}, however,
     * automatically pads each element to the platform address size.
     *
     * @param elements The elements.
     * @return The struct.
     * @see MemoryLayout#structLayout
     */
    public static StructLayout paddedLayout(MemoryLayout... elements) {
        long padSize = ValueLayout.ADDRESS.byteSize();
        List<MemoryLayout> built = new ArrayList<>(elements.length);

        for (MemoryLayout element : elements) {
            built.add(element);
            long r = element.byteSize() % padSize;
            if (r != 0) {
                built.add(MemoryLayout.paddingLayout(r));
            }
        }
        return MemoryLayout.structLayout(built.toArray(MemoryLayout[]::new));
    }
}
