// Copyright © 2011-2025 Andy Goryachev <andy@goryachev.com>
package com.enosi.docktailor.common.util;

import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.util.Objects;

/**
 * Convenience methods that attempt to extract requested value, returning null if the said value can not be extracted.
 */
@Slf4j(topic = "Parsers")
public class Parsers {
    public static Double parseDouble(Object x) {
        if (x instanceof Number) {
            return ((Number) x).doubleValue();
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


    public static Integer parseInteger(Object x) {
        if (x instanceof Number) {
            return ((Number) x).intValue();
        } else if (x != null) {
            try {
                String s = x.toString();
                s = s.trim();
                return Integer.parseInt(s);
            } catch (Exception e) {
                log.error(e.getMessage());
            }
        }
        return null;
    }


    public static int parseInt(Object x, int defaultValue) {
        if (x instanceof Number) {
            return ((Number) x).intValue();
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


    public static String parseString(Object x) {
        if (x == null) {
            return null;
        } else if (x instanceof String) {
            return (String) x;
        } else if (x instanceof char[]) {
            return new String((char[]) x);
        } else {
            return x.toString();
        }
    }


    public static Boolean parseBoolean(Object x) {
        if (x instanceof Boolean) {
            return (Boolean) x;
        } else if (x != null) {
            String s = x.toString();
            return "true".equalsIgnoreCase(s) || "y".equalsIgnoreCase(s) || "1".equals(s);
        }
        return null;
    }


    public static File parseFile(Object x) {
        if (x != null) {
            try {
                if (x instanceof File) {
                    return (File) x;
                } else if (x instanceof String) {
                    return new File((String) x);
                }
            } catch (Exception e) {
                log.error(e.getMessage());
            }
        }
        return null;
    }
}