// Copyright © 2009-2025 Andy Goryachev <andy@goryachev.com>
package com.enosi.docktailor.common.util;

import java.text.Collator;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;


public abstract class CComparator<T>
        implements Comparator<T> {
    private Collator collator;

    public CComparator() {
    }

    public static int compareAsStrings(Object a, Object b) {
        String sa = toString(a);
        String sb = toString(b);

        if (sa == null) {
            return sb == null ? 0 : -1;
        } else if (sb == null) {
            return 1;
        } else {
            return sa.compareTo(sb);
        }
    }

    public static String toString(Object x) {
        return x == null ? null : x.toString();
    }

    public static List<String> sortStrings(List<String> items) {
        if (items != null) {
            items.sort(Collator.getInstance());
        }
        return items;
    }

    @Override
    public abstract int compare(T a, T b);

    /**
     * use collate()
     */
    @Deprecated
    protected int compareText(Object a, Object b) {
        return collate(a, b);
    }

    protected int collate(Object a, Object b) {
        String sa = toString(a);
        String sb = toString(b);

        if (sa == null) {
            return sb == null ? 0 : -1;
        } else if (sb == null) {
            return 1;
        } else {
            return collator().compare(sa, sb);
        }
    }

    protected Collator collator() {
        if (collator == null) {
            collator = Collator.getInstance();
        }
        return collator;
    }

    public List<? extends T> sort(List<? extends T> items) {
        if (items != null) {
            Collections.sort(items, this);
        }
        return items;
    }
}
