
package com.enosi.docktailor.docktailor.fx.internal;

import com.enosi.docktailor.common.util.CKit;
import com.enosi.docktailor.common.util.Parsers;
import com.enosi.docktailor.common.util.SB;
import com.enosi.docktailor.docktailor.fx.CssID;
import com.enosi.docktailor.docktailor.fx.CssPseudo;
import com.enosi.docktailor.docktailor.fx.CssStyle;
import javafx.scene.control.OverrunStyle;
import javafx.scene.control.ScrollPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.text.FontSmoothingType;
import java.text.DecimalFormat;


/**
 * Css Tools.
 */
public class CssTools {
    /**
     * bold type face
     */
    public static final CssStyle BOLD = new CssStyle();

    private static DecimalFormat decimalFormat;


    public static String toColor(Object x) {
        if (x == null) {
            return "null";
        } else if (x instanceof Integer) {
            int rgb = (Integer) x;
            int r = (rgb >> 16) & 0xff;
            int g = (rgb >> 8) & 0xff;
            int b = (rgb) & 0xff;

            return String.format("#%02x%02x%02x", r, g, b);
        } else if (x instanceof Color c) {
            double r = c.getRed();
            double g = c.getGreen();
            double b = c.getBlue();

            if (c.isOpaque()) {
                return String.format("#%02x%02x%02x", to8bit(r), to8bit(g), to8bit(b));
            } else {
                double a = c.getOpacity();
                return String.format("#%02x%02x%02x%02x", to8bit(r), to8bit(g), to8bit(b), to8bit(a));
//				return "rgba(" + r + "," + g + "," + b + "," + a + ")";
            }
        } else {
            return x.toString();
        }
    }


    // FIX this needs to support things like "c1 c1 c3 c4, c5 c6, c7"
    public static String toColors(Object... xs) {
        int sz = xs.length;
        if (sz == 1) {
            return toColor(xs[0]);
        }

        if ((sz % 4) != 0) {
            throw new Error("please specify colors in groups of four");
        }

        SB sb = new SB();
        for (int i = 0; i < sz; i++) {
            if (i == 0) {
                // nothing
            } else if ((i % 4) == 0) {
                sb.a(',');
            } else {
                sb.sp();
            }

            sb.a(toColor(xs[i]));
        }
        return sb.toString();
    }


    private static int to8bit(double x) {
        int v = (int) Math.round(255 * x);
        if (v < 0) {
            return 0;
        } else if (v > 255) {
            return 255;
        }
        return v;
    }


    public static String toValue(Object x) {
        if (x == null) {
            return "null";
        } else if (x instanceof Number n) {
            int vi = n.intValue();
            double vd = n.doubleValue();
            if (vd == vi) {
                return String.valueOf(vi);
            } else {
                return String.valueOf(vd);
            }
        } else if (x instanceof Color) {
            return toColor(x);
        } else {
            return x.toString();
        }
    }


    public static String toValue(FontSmoothingType t) {
        return switch (t) {
            case LCD -> "lcd";
            default -> "gray";
        };
    }


    public static String toValue(double x) {
        return String.valueOf(x);
    }


    public static String toValue(boolean x) {
        return x ? "true" : "false";
    }


    public static String toValue(OverrunStyle s) {
        return switch (s) {
            case CENTER_ELLIPSIS -> "center-ellipsis";
            case CENTER_WORD_ELLIPSIS -> "center-word-ellipsis";
            case CLIP -> "clip";
            case ELLIPSIS -> "ellipsis";
            case LEADING_ELLIPSIS -> "leading-ellipsis";
            case LEADING_WORD_ELLIPSIS -> "leading-word-ellipsis";
            case WORD_ELLIPSIS -> "word-ellipsis";
        };
    }


    public static String toValue(ScrollPane.ScrollBarPolicy x) {
        return switch (x) {
            case ALWAYS -> "always";
            case AS_NEEDED -> "as-needed";
            case NEVER -> "never";
        };
    }


    public static String toValue(StrokeLineCap x) {
        return switch (x) {
            case BUTT -> "butt";
            case ROUND -> "round";
            case SQUARE -> "square";
        };
    }


    public static String toValues(Object... xs) {
        int sz = xs.length;
        if (sz == 1) {
            return toValue(xs[0]);
        }

        if ((sz % 4) != 0) {
            throw new Error("please specify values in groups of four");
        }

        SB sb = new SB();
        for (int i = 0; i < sz; i++) {
            if (i == 0) {
                // nothing
            } else if ((i % 4) == 0) {
                sb.a(',');
            } else {
                sb.sp();
            }

            sb.a(toValue(xs[i]));
        }
        return sb.toString();
    }


    public static String list(String separator, Object[] items) {
        SB sb = new SB();
        boolean sep = false;
        for (Object x : items) {
            if (sep) {
                sb.a(separator);
            } else {
                sep = true;
            }
            sb.a(toValue(x));
        }
        return sb.toString();
    }


    /**
     * constructs selector string
     */
    public static String selector(Object[] sel) {
        SB sb = new SB();
        for (Object x : sel) {
            addSelector(sb, x);
        }
        return sb.toString();
    }


    private static void addSelector(SB sb, Object x) {
        if (x instanceof CssStyle s) {
            if (sb.isNotEmpty()) {
                sb.a(' ');
            }

            sb.a('.');
            sb.a(s.getName());
        } else if (x instanceof CssID s) {
            if (sb.isNotEmpty()) {
                sb.a(' ');
            }

            sb.a('#');
            sb.a(s.getID());
        } else if (x instanceof String s) {
            if (!s.startsWith(":")) {
                if (sb.isNotEmpty()) {
                    sb.a(' ');
                }
            }

            sb.a(s);
        } else if (x instanceof CssPseudo s) {
            sb.a(s.getName());
        } else {
            throw new Error("?" + x);
        }
    }


    public static String toQuotedString(Object x) {
        if (x == null) {
            return "null";
        }

        String s = x.toString();
        if (s.startsWith("\"") && s.endsWith("\"")) {
            return s;
        } else {
            return "\"" + s + "\"";
        }
    }


    /**
     * formats an CSS string using the following rules:
     * <p>
     * - %s inserts an argument - %1s,%2s,%3s,... inserts 1st (at index 0), 2nd, 3rd, etc. argument - %% inserts a
     * single '%' - \[n,r,t] inserts a NL, CR, TAB char correspondingly - a string argument is quoted if it contains
     * spaces - a double argument is formatted to "#0.##" specification
     */
    public static String format(String format, Object... args) {
        int sz = format.length();
        SB sb = new SB(sz * 3);

        for (int i = 0; i < sz; i++) {
            char c = format.charAt(i);
            switch (c) {
                case '%':
                    i++;
                    int ix = format.indexOf('s', i);
                    if (ix < 0) {
                        sb.append(format, i, sz);
                        i = sz;
                        continue;
                    } else if (ix == 0) {
                        String a = formatArgument(args[0]);
                        sb.append(a);
                    } else {
                        String n = format.substring(i, ix);
                        i = ix;

                        if (n.isEmpty()) {
                            ix = 1;
                        } else {
                            ix = Parsers.parseInteger(n);
                        }
                        String a = formatArgument(args[ix - 1]);
                        sb.append(a);
                    }
                    break;
                case '\\':
                    i++;
                    c = format.charAt(i);
                    switch (c) {
                        case 'n':
                            sb.append('\n');
                            break;
                        case 'r':
                            sb.append('\r');
                            break;
                        case 't':
                            sb.append('\t');
                            break;
                        default:
                            sb.append(c);
                    }
                    break;
                default:
                    sb.append(c);
            }
        }

        return sb.toString();
    }

    private static String formatArgument(Object x) {
        if (x == null) {
            return "";
        } else if (x instanceof Number) {
            if (decimalFormat == null) {
                decimalFormat = new DecimalFormat("#0.##");
            }
            return decimalFormat.format(x);
        } else {
            String s = x.toString();
            if (CKit.containsAny(s, " ")) {
                return '"' + s + '"';
            }
            return s;
        }
    }
}