// Copyright © 2004-2025 Andy Goryachev <andy@goryachev.com>
package com.enosi.docktailor.common.util;

import lombok.extern.slf4j.Slf4j;

import java.util.function.Supplier;

/**
 * converts various objects to their printable user-friendly representation for debugging purposes
 */
@Slf4j(topic = "Dump")
public class Dump {

    /**
     * returns simple name of an object's class
     */
    public static String simpleName(Object x) {
        if (x == null) {
            return "<null>";
        } else if (x instanceof Class<?> clazz) {
            return clazz.getSimpleName() + ".class";
        } else {
            Class<?> c = x.getClass();
            return c.getSimpleName();
        }
    }

    // TODO I wonder if I should switch to JSON instead
    // with JSON, it will be absolutely clear whether the value is "null" or null.
    private static String f(Object x) {
        return x == null ? "<null>" : String.valueOf(x);
    }

    public static Supplier<String> spaces(Object a, Object b) {
        return () -> f(a) + " " + f(b);
    }
}
