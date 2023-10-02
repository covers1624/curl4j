package net.covers1624.curl4j;

/**
 * A function callback for receiving progress stats.
 * <p>
 * See the curl <a href="https://curl.se/libcurl/c/CURLOPT_XFERINFOFUNCTION.html">documentation</a>.
 *
 * @author covers1624
 * @see CurlXferInfoCallbackI
 */
public class CurlXferInfoCallback extends CurlCallback implements CurlXferInfoCallbackI {

    private static final long cif = ffi_prep_cif(
            ffi_type_int,
            ffi_type_pointer, ffi_type_long, ffi_type_long, ffi_type_long, ffi_type_long
    );

    public CurlXferInfoCallback(CurlXferInfoCallbackI delegate) {
        super(cif, delegate);
    }

    protected CurlXferInfoCallback() {
        super(cif, null);
    }

    @Override
    public int update(long ptr, long dltotal, long dlnow, long ultotal, long ulnow) {
        throw new UnsupportedOperationException("Not implemented. Override this function or provide a delegate.");
    }
}
