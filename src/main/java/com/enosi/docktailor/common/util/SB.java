// Copyright © 2010-2025 Andy Goryachev <andy@goryachev.com>
package com.enosi.docktailor.common.util;

import java.util.*;

/**
 * An extended version of StringBuilder
 */
public class SB implements Appendable, CharSequence {
    protected StringBuilder sb;

    public SB(int capacity) {
        sb = new StringBuilder(capacity);
    }

    public SB() {
        this(32);
    }

    public SB(String s) {
        sb = new StringBuilder(s);
    }

    public SB(CharSequence cs) {
        sb = new StringBuilder(cs);
    }

    public SB nl() {
        sb.append("\n");
        return this;
    }

    /**
     * append an object, separating it with the specified delimiter if the buffer is not empty
     */
    public SB a(char delimiter, Object x) {
        if (isNotEmpty()) {
            sb.append(delimiter);
        }

        return a(x);
    }

    public SB a(Object x) {
        if (x != null) {
            sb.append(x);
        }
        return this;
    }

    public SB a(char c) {
        sb.append(c);
        return this;
    }

    public SB sp() {
        sb.append(" ");
        return this;
    }

    public SB sp(int count) {
        sb.append(" ".repeat(Math.max(0, count)));
        return this;
    }

    public SB append(char c, int count) {
        sb.append(String.valueOf(c).repeat(Math.max(0, count)));
        return this;
    }

    public SB append(Object x) {
        sb.append(x);
        return this;
    }

    public SB append(String s) {
        sb.append(s);
        return this;
    }

    @Override
    public Appendable append(CharSequence cs) {
        sb.append(cs);
        return this;
    }

    @Override
    public SB append(CharSequence cs, int start, int end) {
        sb.append(cs, start, end);
        return this;
    }

    public SB append(char str[]) {
        sb.append(str);
        return this;
    }

    public SB append(char str[], int offset, int len) {
        sb.append(str, offset, len);
        return this;
    }

    public SB append(boolean x) {
        sb.append(x);
        return this;
    }

    @Override
    public SB append(char x) {
        sb.append(x);
        return this;
    }

    public SB append(int x) {
        sb.append(x);
        return this;
    }

    public SB append(long x) {
        sb.append(x);
        return this;
    }

    public SB append(float x) {
        sb.append(x);
        return this;
    }

    public SB append(double d) {
        sb.append(d);
        return this;
    }

    public void appendCodePoint(int codePoint) {
        sb.appendCodePoint(codePoint);
    }

    public void replace(int start, int end, String str) {
        sb.replace(start, end, str);
    }

    @Override
    public String toString() {
        return sb.toString();
    }

    @Override
    public int length() {
        return getLength();
    }

    public int getLength() {
        return sb.length();
    }

    public void setLength(int length) {
        sb.setLength(length);
    }


    public void clear() {
        sb.setLength(0);
    }


    @Override
    public char charAt(int ix) {
        return sb.charAt(ix);
    }

    @Override
    public boolean isEmpty() {
        return sb.isEmpty();
    }

    public boolean isNotEmpty() {
        return !sb.isEmpty();
    }

    public String getAndClear() {
        String s = sb.toString();
        sb.setLength(0);
        return s;
    }

    public int indexOfIgnoreCase(String pattern, int fromIndex) {
        int len = sb.length();
        int plen = pattern.length();

        if (fromIndex >= len) {
            return (plen == 0 ? len : -1);
        }
        if (fromIndex < 0) {
            fromIndex = 0;
        }
        if (plen == 0) {
            return fromIndex;
        }

        char c0 = pattern.charAt(0);
        int max = (len - plen);

        for (int i = fromIndex; i <= max; i++) {
            if (!TextTools.isSameIgnoreCase(sb.charAt(i), c0)) {
                while ((++i <= max) && (!TextTools.isSameIgnoreCase(sb.charAt(i), c0))) {
                }
            }

            if (i <= max) {
                int j = i + 1;
                int end = j + plen - 1;
                for (int k = 1; ((j < end) && (TextTools.isSameIgnoreCase(sb.charAt(j), pattern.charAt(k)))); j++, k++) {
                }

                if (j == end) {
                    return i;
                }
            }
        }
        return -1;
    }


    public void replace(String old, String newText) {
        int start = 0;
        for (; ; ) {
            int ix = sb.indexOf(old, start);
            if (ix < 0) {
                return;
            }

            sb.replace(ix, ix + old.length(), newText);

            start = ix + newText.length();
        }
    }

    public void replace(char old, char newChar) {
        for (int i = sb.length() - 1; i >= 0; i--) {
            char c = sb.charAt(i);
            if (c == old) {
                sb.setCharAt(i, newChar);
            }
        }
    }

    /**
     * append all items separated by the separator (all nulls are treated as empty strings)
     */
    public void addAll(Object[] ss, Object sep) {
        boolean first = true;
        for (Object s : ss) {
            if (first) {
                first = false;
            } else {
                a(sep);
            }

            a(s);
        }
    }

    @Override
    public CharSequence subSequence(int start, int end) {
        return sb.subSequence(start, end);
    }

    public SB list(Collection<?> items, char delimiter) {
        if (items != null) {
            boolean sep = false;

            for (Object x : items) {
                if (sep) {
                    sb.append(delimiter);
                } else {
                    sep = true;
                }
                sb.append(x);
            }
        }
        return this;
    }


    public SB list(Object[] items, char delimiter) {
        if (items != null) {
            boolean sep = false;

            for (Object x : items) {
                if (sep) {
                    sb.append(delimiter);
                } else {
                    sep = true;
                }
                sb.append(x);
            }
        }
        return this;
    }

    public SB list(Map<?, ?> items, char delimiter) {
        if (items != null) {
            boolean sep = false;
            // would be nice to sort, but keys may not be sortable
            for (Object k : items.keySet()) {
                if (sep) {
                    sb.append(delimiter);
                } else {
                    sep = true;
                }

                Object v = items.get(k);
                sb.append(k);
                sb.append('=');
                sb.append(v);
            }
        }
        return this;
    }
}