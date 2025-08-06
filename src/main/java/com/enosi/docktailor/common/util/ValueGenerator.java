package com.enosi.docktailor.common.util;


/**
 * Value Generator.
 */
@FunctionalInterface
public interface ValueGenerator<T> {
    public T generate() throws Throwable;
}
