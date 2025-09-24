package com.enosistudio.docktailor.common;


public class Hex {
    public static final String HEX = "0123456789ABCDEF";
    private Hex() {}

    public static String toHexString(int d, int digits) {
        char[] buf = new char[digits];
        while (--digits >= 0) {
            buf[digits] = HEX.charAt(d & 0x0f);
            d >>= 4;
        }
        return new String(buf);
    }

    public static String toHexString(short d) {
        return toHexString(d, 4);
    }

    public static String toHexByte(int x) {
        char[] cs = new char[2];
        cs[0] = HEX.charAt((x >> 4) & 0x0f);
        cs[1] = HEX.charAt(x & 0x0f);
        return new String(cs);
    }

    /**
     * returns hex value of a char in the range 0..15, throws an exception if not a hex char
     */
    public static int parseHexChar(char c) throws IllegalArgumentException {
        int x = parseHexCharPrivate(c);
        if (x < 0) {
            throw new IllegalArgumentException("not a hexadecimal character: " + c);
        }
        return x;
    }


    public static int parseHexCharPrivate(char c) {
        return HEX.indexOf(Character.toUpperCase(c));
    }


    /**
     * parses a two-symbol string as a hex byte value
     */
    public static byte parseByte(String s) {
        int d = parseHexChar(s.charAt(0)) << 4;
        return (byte) (d | parseHexChar(s.charAt(1)));
    }
}
