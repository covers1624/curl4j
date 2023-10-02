package net.covers1624.curl4j;

import java.io.IOException;

/**
 * A function callback for writing data.
 * <p>
 * See the curl <a href="https://curl.se/libcurl/c/CURLOPT_WRITEFUNCTION.html">documentation</a>.
 *
 * @author covers1624
 * @see CurlWriteCallbackI
 */
public class CurlWriteCallback extends CurlCallback implements CurlWriteCallbackI {

    private static final long cif = ffi_prep_cif(
            ffi_type_pointer,
            ffi_type_pointer, ffi_type_pointer, ffi_type_pointer, ffi_type_pointer
    );

    public CurlWriteCallback(CurlWriteCallbackI delegate) {
        super(cif, delegate);
    }

    protected CurlWriteCallback() {
        super(cif, null);
    }

    @Override
    public long write(long ptr, long size, long nmemb, long userdata) throws IOException {
        throw new UnsupportedOperationException("Not implemented. Override this function or provide a delegate.");
    }
}
