package net.covers1624.curl4j;

import java.io.IOException;

/**
 * A function callback for seeking the curl input.
 * <p>
 * See the curl <a href="https://curl.se/libcurl/c/CURLOPT_SEEKFUNCTION.html">documentation</a>.
 *
 * @author covers1624
 * @see CurlSeekCallbackI
 */
public class CurlSeekCallback extends CurlCallback implements CurlSeekCallbackI {

    private static final long cif = ffi_prep_cif(
            ffi_type_int,
            ffi_type_pointer, ffi_type_pointer, ffi_type_int
    );

    public CurlSeekCallback(CurlSeekCallbackI delegate) {
        super(cif, delegate);
    }

    protected CurlSeekCallback() {
        super(cif, null);
    }

    @Override
    public int seek(long userdata, long offset, int origin) throws IOException {
        throw new UnsupportedOperationException("Not implemented. Override this function or provide a delegate.");
    }
}
