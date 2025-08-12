package com.enosi.docktailor.common.util;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

/**
 * Unsynchronized List of WeakListeners.
 */
public class WeakList<T> {
    private final List<WeakReference<T>> list;

    public WeakList() {
        this(8);
    }

    public WeakList(int size) {
        list = new ArrayList<>(size);
    }

    public void gc() {
        int sz = list.size();
        for (int i = sz - 1; i >= 0; i--) {
            WeakReference<T> ref = list.get(i);
            T item = ref.get();
            if (item == null) {
                list.remove(i);
            }
        }
    }

    public T get(int ix) {
        return list.get(ix).get();
    }


    public void add(T item) {
        list.add(new WeakReference<>(item));
    }


    public void add(int index, T item) {
        list.add(index, new WeakReference<>(item));
    }


    public int size() {
        return list.size();
    }


    public void remove(T item) {
        int sz = list.size();
        for (int i = sz - 1; i >= 0; i--) {
            WeakReference<T> ref = list.get(i);
            T x = ref.get();
            if (x == null || item == x) {
                list.remove(i);
            }
        }
    }


    public T remove(int ix) {
        WeakReference<T> ref = list.remove(ix);
        return ref.get();
    }


    public void clear() {
        list.clear();
    }


    public WeakReference<T> getRef(int ix) {
        return list.get(ix);
    }


    @Override
    public String toString() {
        int sz = list.size();
        SB sb = new SB(sz * 8);
        sb.append("[");

        for (int i = 0; i < sz; i++) {
            if (i > 0) {
                sb.append(",");
            }

            WeakReference<T> ref = list.get(i);
            T item = ref.get();
            if (item == null) {
                sb.append("<null>");
            } else {
                sb.append(item);
            }
        }

        sb.append("]");
        return sb.toString();
    }
}