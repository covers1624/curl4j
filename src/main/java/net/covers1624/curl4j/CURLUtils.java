package net.covers1624.curl4j;

/**
 * Created by covers1624 on 4/8/23.
 */
class CURLUtils {

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
