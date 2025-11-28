package com.enosistudio.docktailor.fx;

import com.enosistudio.docktailor.common.ParserUtils;
import javafx.beans.property.*;
import javafx.util.StringConverter;

import java.util.Locale;

/**
 * Converters.
 */
public class Converters {
    private static StringConverter<Boolean> booleanConverter;
    private static StringConverter<Integer> intConverter;
    private static StringConverter<Number> doubleNumberConverter;
    private static StringConverter<Number> intNumberConverter;
    private static StringConverter<String> stringConverter;
    private static StringConverter<Object> objectConverter;

    private Converters() {
    }

    @SuppressWarnings("unchecked")
    public static <T> StringConverter<T> get(Property<T> p) {
        if (p instanceof BooleanProperty) {
            return (StringConverter<T>) BOOLEAN();
        } else if (p instanceof IntegerProperty) {
            return (StringConverter<T>) INT();
        } else if (p instanceof DoubleProperty) {
            return (StringConverter<T>) NUMBER_DOUBLE();
        } else if (p instanceof StringProperty) {
            return (StringConverter<T>) STRING();
        } else {
            throw new IllegalArgumentException("?" + p);
        }
    }

    public static StringConverter<Boolean> BOOLEAN() {
        if (booleanConverter == null) {
            booleanConverter = new StringConverter<>() {
                @Override
                public Boolean fromString(String s) {
                    return ParserUtils.parseBoolean(s);
                }

                @Override
                public String toString(Boolean x) {
                    return Boolean.TRUE.equals(x) ? "true" : "false";
                }
            };
        }
        return booleanConverter;
    }


    public static StringConverter<Integer> INT() {
        if (intConverter == null) {
            intConverter = new StringConverter<>() {
                @Override
                public Integer fromString(String s) {
                    return ParserUtils.parseInt(s, 0);
                }

                @Override
                public String toString(Integer x) {
                    return String.valueOf(x);
                }
            };
        }
        return intConverter;
    }


    public static StringConverter<Number> NUMBER_INT() {
        if (intNumberConverter == null) {
            intNumberConverter = new StringConverter<>() {
                @Override
                public Number fromString(String s) {
                    return ParserUtils.parseInt(s, 0);
                }

                @Override
                public String toString(Number x) {
                    return String.valueOf(x);
                }
            };
        }
        return intNumberConverter;
    }


    public static StringConverter<Number> NUMBER_DOUBLE() {
        if (doubleNumberConverter == null) {
            doubleNumberConverter = new StringConverter<>() {
                @Override
                public Number fromString(String s) {
                    return ParserUtils.parseDouble(s, 0.0);
                }

                @Override
                public String toString(Number x) {
                    return String.valueOf(x);
                }
            };
        }
        return doubleNumberConverter;
    }


    public static StringConverter<String> STRING() {
        if (stringConverter == null) {
            stringConverter = new StringConverter<>() {
                @Override
                public String toString(String s) {
                    return s;
                }

                @Override
                public String fromString(String s) {
                    return s;
                }
            };
        }
        return stringConverter;
    }


    public static StringConverter<Object> OBJECT() {
        if (objectConverter == null) {
            objectConverter = new StringConverter<>() {
                @Override
                public String toString(Object x) {
                    return x == null ? null : x.toString();
                }

                @Override
                public Object fromString(String s) {
                    return s;
                }
            };
        }
        return objectConverter;
    }


    public static <T extends Enum<T>> StringConverter<T> enumConverter(Class<T> type) {
        return new StringConverter<>() {
            @Override
            public String toString(T v) {
                return v == null ? null : v.toString();
            }


            @Override
            public T fromString(String s) {
                try {
                    return s == null ? null : Enum.valueOf(type, s.toUpperCase(Locale.ENGLISH));
                } catch (Exception ignored) {
                }
                return null;
            }
        };
    }
}
