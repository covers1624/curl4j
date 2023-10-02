package net.covers1624.curl4j;

import net.covers1624.curl4j.core.Callback;

/**
 * Parent class to all CURL callbacks. Mostly to simplify function sigs.
 *
 * @author covers1624
 */
public abstract class CurlCallback extends Callback {

    protected CurlCallback(long cif, CallbackInterface delegate) {
        super(cif, delegate);
    }

    protected CurlCallback(long cif, long callback, CallbackInterface delegate) {
        super(cif, callback, delegate);
    }
}
