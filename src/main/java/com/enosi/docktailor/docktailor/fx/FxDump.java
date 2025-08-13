package com.enosi.docktailor.docktailor.fx;

import com.enosi.docktailor.common.util.CComparator;
import com.enosi.docktailor.common.util.CKit;
import com.enosi.docktailor.common.util.SB;
import com.enosi.docktailor.common.util.TextTools;
import javafx.css.CssMetaData;
import javafx.css.PseudoClass;
import javafx.css.Styleable;
import javafx.scene.Node;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.PickResult;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Window;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

/**
 * Diagnostic tool to dump FX style information with a key press.
 */
@Slf4j(topic = "FxDump")
public class FxDump {
    private final KeyCode trigger;
    /**
     * -- SETTER -- controls whether to show null properties
     */
    @Setter
    private boolean showNulls;
    private double x;
    private double y;
    private PickResult r;
    private CComparator<CssMetaData<? extends Styleable, ?>> sorter;

    public FxDump(KeyCode trigger) {
        this.trigger = trigger;
    }


    public FxDump() {
        this(KeyCode.BACK_QUOTE);
    }


    /**
     * a shortcut to attach a default FxDump
     */
    public static void attach(Node n) {
        new FxDump().attachNode(n);
    }


    /**
     * a shortcut to attach a default FxDump
     */
    public static void attach(Window n) {
        new FxDump().attachWindow(n);
    }

    public void attachWindow(Window s) {
        attachNode(s.getScene().getRoot());
    }


    public void attachNode(Node n) {
        // I don't know how to listen to global mouse or key events in FX...
        n.getScene().addEventFilter(KeyEvent.KEY_PRESSED, (ev) -> handleKeyPress(ev));
        n.getScene().addEventFilter(MouseEvent.ANY, (ev) -> handleMouseEvent(ev));
    }


    protected void handleMouseEvent(MouseEvent ev) {
        x = ev.getScreenX();
        y = ev.getScreenY();
        r = ev.getPickResult();
    }


    protected void handleKeyPress(KeyEvent ev) {
        if (ev.getCode() == trigger) {
            dump();
        }
    }


    protected void dump() {
        if (r != null) {
            Node n = r.getIntersectedNode();
            if (n != null) {
                dump(n);
            }
        }
    }


    protected void sort(List<CssMetaData<? extends Styleable, ?>> list) {
        if (sorter == null) {
            sorter = new CComparator<>() {
                @Override
                public int compare(CssMetaData<? extends Styleable, ?> a, CssMetaData<? extends Styleable, ?> b) {
                    return compareAsStrings(a.getProperty(), b.getProperty());
                }
            };
        }

        sorter.sort(list);
    }


    protected boolean shouldShow(Object x) {
        if (x == null) {
            return showNulls;
        }
        return true;
    }


    protected String describeBackground(Background b) {
        SB sb = new SB();
        sb.a("Background<");
        boolean sep = false;
        for (BackgroundFill f : b.getFills()) {
            if (sep) {
                sb.a(",");
            } else {
                sep = true;
            }
            sb.a(describe(f));
        }
        sb.a(">");
        return sb.toString();
    }


    protected String describeBackgroundFill(BackgroundFill f) {
        SB sb = new SB();
        sb.a("Fill<").a(describe(f.getFill())).a(",");
        sb.a(describe(f.getInsets())).a(", ");
        sb.a(describe(f.getRadii())).a(">");
        return sb.toString();
    }


    protected String describeDouble(Double x) {
        if (x == null) {
            return "null";
        }

        double v = x.doubleValue();
        if (v == Double.MAX_VALUE) {
            return "MAX_VALUE";
        } else if (v == Double.MIN_VALUE) {
            return "MAX_VALUE";
        } else if (Double.isNaN(v)) {
            return "NaN";
        } else {
            long n = x.longValue();
            if (n == v) {
                return String.valueOf(n);
            } else {
                return String.valueOf(v);
            }
        }
    }


    protected String describeFont(Font f) {
        SB sb = new SB();
        sb.a(f.getName()).sp().a(f.getStyle()).sp().a(f.getSize());
        return sb.toString();
    }


    protected Object describe(Object x) {
        if (x instanceof Double) {
            return describeDouble((Double) x);
        } else if (x instanceof Double[]) {
            Double[] v = (Double[]) x;

            SB sb = new SB();
            sb.a("<");
            boolean sep = false;
            for (int i = 0; i < v.length; i++) {
                if (sep) {
                    sb.a(", ");
                } else {
                    sep = true;
                }
                sb.a(describeDouble(v[i]));
            }
            sb.a(">");
            return sb.toString();
        } else if (x instanceof Background) {
            return describeBackground((Background) x);
        } else if (x instanceof BackgroundFill) {
            return describeBackgroundFill((BackgroundFill) x);
        } else if (x instanceof Font) {
            return describeFont((Font) x);
        }

        return x;
    }


    protected void dump(Node n) {
        SB sb = new SB(4096);
        sb.nl();

        while (n != null) {
            sb.a(CKit.getSimpleName(n));

            String id = n.getId();
            if (CKit.isNotBlank(id)) {
                sb.a(" #");
                sb.a(id);
            }

            for (String s : n.getStyleClass()) {
                sb.a(" .").a(s);
            }

            for (PseudoClass c : n.getPseudoClassStates()) {
                sb.a(" :").a(c);
            }

            sb.nl();

            if (n instanceof Text) {
                sb.sp(4);
                sb.a("text: ");
                sb.a(TextTools.escapeControlsForPrintout(((Text) n).getText()));
                sb.nl();
            }

            List<CssMetaData<? extends Styleable, ?>> md = new ArrayList<>(n.getCssMetaData());
            sort(md);

            for (CssMetaData d : md) {
                String k = d.getProperty();
                Object v = d.getStyleableProperty(n).getValue();
                if (shouldShow(v)) {
                    Object val = describe(v);
                    sb.sp(4).a(k);
                    sb.sp().a(val);
                    if (d.isInherits()) {
                        sb.a(" *");
                    }
                    sb.nl();
                }
            }

            n = n.getParent();
        }

        log.info(sb.toString());
    }
}
