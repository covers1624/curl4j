package net.covers1624.curl4j.core;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by covers1624 on 17/8/23.
 */
@Retention(RetentionPolicy.CLASS)
public @interface NativeType {

    String value();
}
