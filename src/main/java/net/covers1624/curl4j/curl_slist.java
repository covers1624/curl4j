package net.covers1624.curl4j;

import org.jetbrains.annotations.Nullable;
import org.lwjgl.system.Struct;

import java.nio.ByteBuffer;

import static org.lwjgl.system.MemoryUtil.*;

/**
 * Created by covers1624 on 4/8/23.
 */
public class curl_slist extends Struct {

    public static final int SIZEOF;
    public static final int ALIGNOF;

    public static final int DATA;
    public static final int NEXT;

    static {
        Layout layout = __struct(
                __member(POINTER_SIZE),
                __member(POINTER_SIZE)
        );

        SIZEOF = layout.getSize();
        ALIGNOF = layout.getAlignment();

        DATA = layout.offsetof(0);
        NEXT = layout.offsetof(1);
    }

    public curl_slist(ByteBuffer container) {
        super(memAddress(container), __checkContainer(container, SIZEOF));
    }

    @Override
    public int sizeof() {
        return SIZEOF;
    }

    public static curl_slist create(long address) {
        return wrap(curl_slist.class, address);
    }

    @Nullable
    public static curl_slist createSafe(long address) {
        return address == NULL ? null : wrap(curl_slist.class, address);
    }

    @Nullable
    public String data() {
        return memUTF8Safe(address() + DATA);
    }

    @Nullable
    public curl_slist next() {
        return createSafe(address() + NEXT);
    }
}
