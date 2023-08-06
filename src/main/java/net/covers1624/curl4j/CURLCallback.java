package net.covers1624.curl4j;

import org.lwjgl.system.Callback;
import org.lwjgl.system.libffi.FFICIF;

/**
 * Parent class to all CURL callbacks. Mostly to simplify function sigs.
 *
 * @author covers1624
 */
public abstract class CURLCallback extends Callback {

    protected CURLCallback(FFICIF cif) {
        super(cif);
    }

    protected CURLCallback(long address) {
        super(address);
    }
}
