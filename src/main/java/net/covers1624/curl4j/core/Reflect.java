package net.covers1624.curl4j.core;

import java.lang.reflect.Method;

/**
 * @author covers1624
 */
public final class Reflect {

    public static Method getMethod(Class<?> clazz, String name, Class<?>... params) {
        try {
            return clazz.getMethod(name, params);
        } catch (NoSuchMethodException ex) {
            throw new IllegalStateException("Failed to find method " + name + ".", ex);
        }
    }
}
