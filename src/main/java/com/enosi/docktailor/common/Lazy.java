package com.enosi.docktailor.common;

import java.util.Objects;
import java.util.function.Supplier;

public class Lazy<T> {
    private volatile Supplier<T> supplier;
    private T value;

    public Lazy(Supplier<T> supplier) {
        this.supplier = Objects.requireNonNull(supplier);
    }

    public static <T> Lazy<T> of(Supplier<T> supplier) {
        return new Lazy<>(supplier);
    }

    public T get() {
        Supplier<T> s = supplier;
        if (s != null) { // première vérification sans verrou
            synchronized (this) {
                if (supplier != null) { // double-check
                    value = supplier.get();
                    supplier = null; // libère la référence au supplier
                }
            }
        }
        return value;
    }

    public boolean isInitialized() {
        return supplier == null;
    }
}
