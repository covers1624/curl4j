package net.covers1624.curl4j;

import java.io.IOException;

/**
 * A function callback for reading POST/PUT data.
 * <p>
 * See the curl <a href="https://curl.se/libcurl/c/CURLOPT_READFUNCTION.html">documentation</a>.
 *
 * @author covers1624
 * @see CurlReadCallbackI
 */
public class CurlReadCallback extends CurlCallback implements CurlReadCallbackI {

    private static final long cif = ffi_prep_cif(
            ffi_type_pointer,
            ffi_type_pointer, ffi_type_pointer, ffi_type_pointer, ffi_type_pointer
    );

    public CurlReadCallback(CurlReadCallbackI delegate) {
        super(cif, delegate);
    }

    protected CurlReadCallback() {
        super(cif, null);
    }

    @Override
    public long read(long ptr, long size, long nmemb, long userdata) throws IOException {
        throw new UnsupportedOperationException("Not implemented. Override this function or provide a delegate.");
    }
}
