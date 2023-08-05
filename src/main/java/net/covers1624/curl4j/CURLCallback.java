package net.covers1624.curl4j;

import org.lwjgl.system.Callback;
import org.lwjgl.system.libffi.FFICIF;

/**
 * Created by covers1624 on 4/8/23.
 */
public abstract class CURLCallback extends Callback {

    protected CURLCallback(FFICIF cif) {
        super(cif);
    }

    protected CURLCallback(long address) {
        super(address);
    }
}
