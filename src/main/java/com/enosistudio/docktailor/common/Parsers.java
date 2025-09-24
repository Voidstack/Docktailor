package com.enosistudio.docktailor.common;

import lombok.extern.slf4j.Slf4j;

import java.util.Objects;

/**
 * Convenience methods that attempt to extract requested value, returning null if the said value can not be extracted.
 */
@Slf4j(topic = "Parsers")
public class Parsers {
    private Parsers() {}

    public static Double parseDouble(Object x) {
        if (x instanceof Number xNumber) {
            return xNumber.doubleValue();
        } else if (x != null) {
            try {
                String s = x.toString();
                s = s.trim();
                return Double.parseDouble(s);
            } catch (Exception e) {
                log.error(e.getMessage());
            }
        }
        return null;
    }


    public static double parseDouble(Object x, double defaultValue) {
        Double d = parseDouble(x);
        return Objects.requireNonNullElse(d, defaultValue);
    }


    public static int parseInt(Object x, int defaultValue) {
        if (x instanceof Number xNumber) {
            return xNumber.intValue();
        } else if (x != null) {
            try {
                String s = x.toString();
                s = s.trim();
                return Integer.parseInt(s);
            } catch (Exception e) {
                log.error(e.getMessage());
            }
        }
        return defaultValue;
    }


    public static Boolean parseBoolean(Object x) {
        if (x instanceof Boolean bool) {
            return bool;
        } else if (x != null) {
            String s = x.toString();
            return "true".equalsIgnoreCase(s) || "y".equalsIgnoreCase(s) || "1".equals(s);
        }
        return false;
    }
}