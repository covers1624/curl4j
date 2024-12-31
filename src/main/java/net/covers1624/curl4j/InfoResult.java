package net.covers1624.curl4j;

/**
 * Created by covers1624 on 12/31/24.
 */
public record InfoResult<T>(int curlCode, T result) {
}
