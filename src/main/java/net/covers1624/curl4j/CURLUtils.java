package net.covers1624.curl4j;

/**
 * Created by covers1624 on 4/8/23.
 */
public class CURLUtils {

    /**
     * Can be used to override the CURL library name.
     * <p>
     * May also be set to an absolute path to use a specific curl library.
     */
    public static String CURL_LIBRARY_NAME = System.getProperty("net.covers1624.curl4j.libname", "curl");

    /**
     * Re-throw a Throwable unchecked.
     *
     * @param ex The Throwable to throw.
     */
    @SuppressWarnings ("unchecked")
    public static <T extends Throwable> void throwUnchecked(Throwable ex) throws T {
        throw (T) ex;
    }
}
