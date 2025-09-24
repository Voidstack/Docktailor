package com.enosistudio.docktailor.common;

import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

@Slf4j(topic = "CKit")
public final class CKit {
    public static final char BOM = '\ufeff';
    public static final Charset CHARSET_UTF8 = StandardCharsets.UTF_8;

    private CKit() {}

    public static void close(Closeable x) {
        try {
            if (x != null) {
                x.close();
            }
        } catch (Exception e) {
            log.error("Error closing resource: {}", x, e);
        }
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
        } else if (x instanceof char[] xChar) {
            return xChar.length == 0;
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

    public static String readString(File f, Charset cs) throws IOException {
        return readString(new FileInputStream(f), cs);
    }

    public static String readString(File f) throws IOException {
        return readString(f, CHARSET_UTF8);
    }

    public static String readString(InputStream is, Charset cs) throws IOException {
        return readString(is, Integer.MAX_VALUE, cs);
    }

    public static String readString(InputStream is, int max, Charset cs) throws IOException {
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


    public static String readString(Reader in, int max) throws IOException {
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

    public static void write(File f, String text) throws IOException {
        write(f, text, CHARSET_UTF8);
    }

    public static void write(File f, String text, Charset encoding) throws IOException {
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
}