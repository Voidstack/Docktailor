package com.enosistudio.docktailor.common;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/**
 * String Stream.
 */
public class SStream implements Iterable<String> {
    private final List<String> list = new ArrayList<>();
    private int pos;

    public SStream() {

    }

    public SStream(String[] ss) {
        if(ss != null)
            list.addAll(Arrays.asList(ss));
    }

    public int size() {
        return list.size();
    }

    public String getValue(int ix) {
        return list.get(ix);
    }

    public void add(Object x) {
        list.add(x == null ? null : x.toString());
    }


    public void add(double x) {
        if ((long) x == x) {
            list.add(Long.toString((long) x));
        } else {
            list.add(Double.toString(x));
        }
    }


    public void addAll(double[] xs) {
        for (double x : xs) {
            add(x);
        }
    }

    @Override
    public Iterator<String> iterator() {
        return list.iterator();
    }


    public String nextString() {
        if (pos < list.size()) {
            return list.get(pos++);
        }
        return null;
    }


    public String nextString(String defaultValue) {
        String s = nextString();
        if (s == null) {
            return defaultValue;
        }
        return s;
    }


    public double nextDouble(double defaultValue) {
        return Parsers.parseDouble(nextString(), defaultValue);
    }


    public double nextDouble() {
        return nextDouble(-1.0);
    }


    public int nextInt(int defaultValue) {
        return Parsers.parseInt(nextString(), defaultValue);
    }

    public int nextInt() {
        return nextInt(-1);
    }

    public String[] toArray() {
        return list.toArray(new String[0]);
    }

    @Override
    public String toString() {
        return list.toString();
    }
}
