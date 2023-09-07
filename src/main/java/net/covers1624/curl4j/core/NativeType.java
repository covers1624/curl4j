package net.covers1624.curl4j.core;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * @author covers1624
 */
@Retention(RetentionPolicy.CLASS)
public @interface NativeType {

    String value();
}
