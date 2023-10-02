package net.covers1624.curl4j;

/**
 * A function callback for handling curl headers.
 * <p>
 * See the curl <a href="https://curl.se/libcurl/c/CURLOPT_HEADERFUNCTION.html">documentation</a>.
 *
 * @author covers1624
 * @see CurlHeaderCallbackI
 */
public class CurlHeaderCallback extends CurlCallback implements CurlHeaderCallbackI {

    private static final long cif = ffi_prep_cif(
            ffi_type_pointer,
            ffi_type_pointer, ffi_type_pointer, ffi_type_pointer, ffi_type_pointer
    );

    public CurlHeaderCallback(CurlHeaderCallbackI delegate) {
        super(cif, delegate);
    }

    protected CurlHeaderCallback() {
        super(cif, null);
    }

    @Override
    public void onHeader(String header, long userdata) {
        throw new UnsupportedOperationException("Not implemented. Override this function or provide a delegate.");
    }
}
