// Copyright © 1996-2025 Andy Goryachev <andy@goryachev.com>
package com.enosi.docktailor.common.util;

import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j(topic = "CKit")
public final class CKit {
    public static final char BOM = '\ufeff';
    public static final String[] emptyStringArray = new String[0];
    public static final Charset CHARSET_UTF8 = StandardCharsets.UTF_8;
    private static final AtomicInteger id = new AtomicInteger();
    private static final double LOW_MEMORY_CHECK_THRESHOLD = 0.9;
    private static final double LOW_MEMORY_FAIL_AFTER_GC_THRESHOLD = 0.87;

    public static void close(Closeable x) {
        try {
            if (x != null) {
                x.close();
            }
        } catch (Throwable ignore) {
        }
    }

    public static boolean equals(Object a, Object b) {
        if (a == b) {
            return true;
        }

        if (a == null) {
            return false;
        } else if (b == null) {
            return false;
        } else {
            Class<?> ca = a.getClass();
            Class<?> cb = b.getClass();
            if (ca.isArray() && cb.isArray()) {
                Class<?> ta = ca.getComponentType();
                Class<?> tb = cb.getComponentType();

                if (ta.isPrimitive() || tb.isPrimitive()) {
                    if (ta.equals(tb)) {
                        if (ta == byte.class) {
                            return Arrays.equals((byte[]) a, (byte[]) b);
                        } else if (ta == boolean.class) {
                            return Arrays.equals((boolean[]) a, (boolean[]) b);
                        } else if (ta == char.class) {
                            return Arrays.equals((char[]) a, (char[]) b);
                        } else if (ta == short.class) {
                            return Arrays.equals((short[]) a, (short[]) b);
                        } else if (ta == int.class) {
                            return Arrays.equals((int[]) a, (int[]) b);
                        } else if (ta == long.class) {
                            return Arrays.equals((long[]) a, (long[]) b);
                        } else if (ta == float.class) {
                            return Arrays.equals((float[]) a, (float[]) b);
                        } else if (ta == double.class) {
                            return Arrays.equals((double[]) a, (double[]) b);
                        } else {
                            return false;
                        }
                    } else {
                        return false;
                    }
                } else {
                    return Arrays.deepEquals((Object[]) a, (Object[]) b);
                }
            } else {
                return a.equals(b);
            }
        }
    }

    public static boolean notEquals(Object a, Object b) {
        return !equals(a, b);
    }


    /**
     * returns true if the character is either: - a whitespace - a space character (e.g. 0x00a0) - is ASCII control
     * character or space (< 0x20)
     */
    public static boolean isBlank(int c) {
        if (c <= 0x20) {
            return true;
        } else if (Character.isWhitespace(c)) {
            return true;
        } else return Character.isSpaceChar(c);
    }

    public static boolean isBlank(Object x) {
        if (x == null) {
            return true;
        } else if (x instanceof char[]) {
            return ((char[]) x).length == 0;
        } else {
            // without trim() and allocating a new string
            String s = x.toString();
            int beg = 0;
            int end = s.length();

            while ((beg < end) && isBlank(s.charAt(beg))) {
                beg++;
            }
            while ((beg < end) && isBlank(s.charAt(end - 1))) {
                end--;
            }
            return beg == end;
        }
    }


    public static boolean isNotBlank(Object x) {
        return !isBlank(x);
    }


    public static boolean isEmpty(Collection<?> x) {
        if (x != null) {
            return x.isEmpty();
        }
        return true;
    }

    public static String readString(File f, Charset cs) throws Exception {
        return readString(new FileInputStream(f), cs);
    }



    public static String readString(File f) throws Exception {
        return readString(f, CHARSET_UTF8);
    }


    public static String readString(InputStream is, Charset cs) throws Exception {
        return readString(is, Integer.MAX_VALUE, cs);
    }


    public static String readString(InputStream is, int max, Charset cs) throws Exception {
        if (!(is instanceof BufferedInputStream)) {
            is = new BufferedInputStream(is);
        }

        Reader in = new InputStreamReader(is, cs);
        try {
            return readString(in, max);
        } finally {
            close(is);
        }
    }


    public static String readString(Reader in, int max) throws Exception {
        try {
            boolean first = true;
            StringBuilder sb = new StringBuilder(16384);
            int c;
            while ((c = in.read()) != -1) {
                if (first) {
                    first = false;
                    if (c == BOM) {
                        continue;
                    }
                }

                if (sb.length() >= max) {
                    break;
                }

                sb.append((char) c);
            }
            return sb.toString();
        } finally {
            close(in);
        }
    }

    public static void write(File f, String text) throws Exception {
        write(f, text, CHARSET_UTF8);
    }

    public static void write(File f, String text, Charset encoding) throws Exception {
        if (f != null) {
            File folder = f.getParentFile();
            if (folder != null) {
                folder.mkdirs();
            }
        }

        FileOutputStream out = new FileOutputStream(f);
        try {
            if (text != null) {
                out.write(text.getBytes(encoding));
            }
        } finally {
            close(out);
        }
    }

    /**
     * Splits a string.  Works slightly different than String.split(): 1. does not use regex pattern and therefore
     * faster 2. splits ("a,", ",") -> String[] { "a", "" } while the regular split omits the empty string Always
     * returns a non-null value.
     */
    public static String[] split(String s, String delim) {
        List<String> list = new ArrayList<>();

        if (s != null) {
            int start = 0;
            for (; ; ) {
                int ix = s.indexOf(delim, start);
                if (ix >= 0) {
                    list.add(s.substring(start, ix));
                    start = ix + delim.length();
                } else {
                    list.add(s.substring(start));
                    break;
                }
            }
        }

        return list.toArray(new String[0]);
    }


    // similar split, using single char delimiter
    // 1. does not use regex pattern and therefore faster
    // 2. splits ("a,", ",") -> String[] { "a", "" }
    //    while the regular split omits the empty string
    public static String[] split(CharSequence s, char delim) {
        return split(s, delim, false);
    }


    public static String[] split(CharSequence s, char delim, boolean includeDelimiter) {
        List<String> a = new ArrayList<>();

        if (s != null) {
            int start = 0;
            for (; ; ) {
                int ix = indexOf(s, delim, start);
                if (ix >= 0) {
                    a.add(s.subSequence(start, ix).toString());
                    if (includeDelimiter) {
                        a.add(s.subSequence(ix, ix + 1).toString());
                    }
                    start = ix + 1;
                } else {
                    a.add(s.subSequence(start, s.length()).toString());
                    break;
                }
            }
        }

        return a.toArray(new String[a.size()]);
    }


    public static int indexOf(CharSequence s, char ch, int start) {
        int len = s.length();
        for (int i = start; i < len; i++) {
            char c = s.charAt(i);
            if (c == ch) {
                return i;
            }
        }
        return -1;
    }


    public static String stackTrace(Throwable e) {
        if (e == null) {
            return null;
        }

        return stackTrace(e, 0);
    }


    public static String stackTrace(Throwable e, int level) {
        SB sb = new SB();
        printStackTrace(sb, e, level);
        return sb.toString();
    }


    private static void printStackTrace(SB sb, Throwable e, int level) {
        sb.a(e).nl();

        StackTraceElement[] trace = e.getStackTrace();
        for (int i = level; i < trace.length; i++) {
            StackTraceElement em = trace[i];
            sb.a("\tat ").a(em).nl();
        }

        Throwable cause = e.getCause();
        if (cause != null) {
            printEnclosedStackTrace(sb, cause, trace);
        }
    }


    private static void printEnclosedStackTrace(SB sb, Throwable e, StackTraceElement[] enclosingTrace) {
        // Compute number of frames in common between this and enclosing trace
        StackTraceElement[] trace = e.getStackTrace();
        int m = trace.length - 1;
        int n = enclosingTrace.length - 1;
        while (m >= 0 && n >= 0 && trace[m].equals(enclosingTrace[n])) {
            m--;
            n--;
        }
        int framesInCommon = trace.length - 1 - m;

        sb.a("Caused by: ").a(e).nl();

        for (int i = 0; i <= m; i++) {
            sb.a("\tat ").a(trace[i]).nl();
        }

        if (framesInCommon != 0) {
            sb.a("\t... ").a(framesInCommon).a(" more").nl();
        }

        Throwable ourCause = e.getCause();
        if (ourCause != null) {
            printEnclosedStackTrace(sb, ourCause, trace);
        }
    }


    public static String getSimpleName(Object x) {
        return Dump.simpleName(x);
    }


    /**
     * copies input stream into the output stream using 64K buffer.  returns the number of bytes copied.  supports
     * cancellation
     */
    public static long copy(InputStream in, OutputStream out) throws Exception {
        return copy(in, out, 65536);
    }


    /**
     * copies input stream into the output stream.  returns the number of bytes copied.  supports cancellation
     */
    public static long copy(InputStream in, OutputStream out, int bufferSize) throws Exception {
        if (bufferSize < 1) {
            throw new IllegalArgumentException("invalid bufferSize=" + bufferSize);
        }

        if (in == null) {
            return 0;
        }

        byte[] buf = new byte[bufferSize];
        long count = 0;
        for (; ; ) {
            checkCancelled();

            int rd = in.read(buf);
            if (rd < 0) {
                out.flush();
                return count;
            } else if (rd > 0) {
                out.write(buf, 0, rd);
                count += rd;
            }
        }
    }


    public static boolean isEven(int sz) {
        return (sz & 1) == 0;
    }


    public static boolean isOdd(int sz) {
        return !isEven(sz);
    }


    public static int id() {
        return id.getAndIncrement();
    }


    public static void todo() {
        throw new Error("(to be implemented)");
    }


    /**
     * checks whether the current thread has been interrupted or low memory condition exists. if interrupted - throws
     * CancelledException if low memory condition - throws LowMemoryException
     */
    public static void checkCancelled() throws CancelledException, LowMemoryException {
        if (Thread.interrupted()) {
            throw new CancelledException();
        }

        if (isLowMemory()) {
            throw new LowMemoryException();
        }
    }


    public static boolean isLowMemory() {
        return isLowMemory(LOW_MEMORY_CHECK_THRESHOLD, LOW_MEMORY_FAIL_AFTER_GC_THRESHOLD);
    }


    public static boolean isLowMemory(double triggerThreshold, double failThreshold) {
        Runtime r = Runtime.getRuntime();

        long total = r.totalMemory();
        long used = total - r.freeMemory();
        long max = r.maxMemory();

        if (used > (long) (max * triggerThreshold)) {
            // let's see if gc can help
            System.gc();

            total = r.totalMemory();
            used = total - r.freeMemory();
            return used > (long) (max * failThreshold);
        }
        return false;
    }


    /**
     * returns true if text string contains any character from the pattern string
     */
    public static boolean containsAny(String text, String pattern) {
        if (text != null) {
            for (int i = 0; i < pattern.length(); i++) {
                char c = pattern.charAt(i);
                if (text.indexOf(c) >= 0) {
                    return true;
                }
            }
        }
        return false;
    }


    /**
     * convert to lower case in ENGLISH locale in order to remove dependency on particular platform
     */
    public static String toLowerCase(Object x) {
        if (x == null) {
            return null;
        } else {
            return x.toString().toLowerCase(Locale.ENGLISH);
        }
    }


    public static String getPercentString(double value, int significantDigits) {
        MathContext mc = new MathContext(significantDigits, RoundingMode.HALF_DOWN);
        BigDecimal d = new BigDecimal(100.0 * value, mc);
        return d.toPlainString() + "%";
    }


    private static void append(SB sb, int n, int precision) {
        String s = String.valueOf(n);
        n = precision - s.length();
        if (n > 0) {
            sb.append("0000000000", 0, n);
        }
        sb.append(s);
    }


    /**
     * determines the number of bins required to divide items into the specified number of bins
     */
    public static int binCount(int itemCount, int binSize) {
        if (itemCount == 0) {
            return 0;
        } else if (binSize == 0) {
            return itemCount;
        } else {
            return 1 + (itemCount - 1) / binSize;
        }
    }


    /**
     * returns UTF-8 bytes
     */
    public static byte[] getBytes(String s) {
        if (s == null) {
            return null;
        } else {
            return s.getBytes(CHARSET_UTF8);
        }
    }


    /**
     * alias to Math.round() typecast returns int
     */
    public static int round(double x) {
        return (int) Math.round(x);
    }


    /**
     * alias to Math.ceil() typecast returns int
     */
    public static int ceil(double x) {
        return (int) Math.ceil(x);
    }


    /**
     * alias to Math.floor() typecast returns int
     */
    public static int floor(double x) {
        return (int) Math.floor(x);
    }

    /**
     * utility method converts a String Collection to a String[]. returns null if input is null
     */
    public static String[] toArray(Collection<String> coll) {
        if (coll == null) {
            return null;
        }
        return coll.toArray(new String[0]);
    }
}