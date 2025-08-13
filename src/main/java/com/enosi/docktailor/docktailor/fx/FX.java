package com.enosi.docktailor.docktailor.fx;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Tooltip;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.Window;

import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * A comprehensive utility class that simplifies JavaFX development by providing a wide range of
 * helper methods, constants, and convenience functions for common UI tasks.
 * <p>
 * This class serves as a centralized location for frequently used JavaFX operations, including:
 * - UI component creation and configuration
 * - Layout and styling utilities
 * - Event handling helpers
 * - Common transformations and calculations
 * - Thread-safe UI updates
 * <p>
 * Key features include:
 * - Simplified creation and configuration of common JavaFX controls
 * - Helper methods for working with styles, layouts, and properties
 * - Utilities for common UI patterns (dialogs, tooltips, etc.)
 * - Mathematical and geometric calculations for UI elements
 * - Convenience methods for working with colors, fonts, and other UI properties
 * <p>
 * The class is designed to be used statically and follows a fluent API pattern where possible
 * to enable method chaining and more readable code.
 * <p>
 * Example usage:
 * <pre>
 * {@code
 * // Create a styled label with tooltip
 * Label label = FX.label("Username:", CssStyle.TEXT_BOLD, Pos.CENTER_LEFT);
 *
 * // Run code on the JavaFX Application Thread
 * FX.runLater(() -> updateUI());
 *
 * // Get window containing a node
 * FxWindow window = FX.getWindow(someNode);
 * }
 * </pre>
 *
 * @see FxWindow
 * @see FxDialog
 * @see FxMenu
 * @see FxMenuItem
 */
public final class FX {
    public static final double TWO_PI = Math.PI + Math.PI;
    public static final double DEGREES_PER_RADIAN = 180.0 / Math.PI;
    public static final double GAMMA = 2.2;
    public static final double ONE_OVER_GAMMA = 1.0 / GAMMA;
    private static final Object PROP_TOOLTIP = new Object();
    private static final Object PROP_NAME = new Object();
    private static final Object PROP_SKIP_SETTINGS = new Object();

    public static FxWindow getWindow(Node n) {
        Scene sc = n.getScene();
        if (sc != null) {
            Window w = sc.getWindow();
            if (w instanceof FxWindow) {
                return (FxWindow) w;
            }
        }
        return null;
    }

    public static boolean isSkipSettings(Node n) {
        Object x = n.getProperties().get(PROP_SKIP_SETTINGS);
        return Boolean.TRUE.equals(x);
    }

    public static void setSkipSettings(Window w) {
        w.getProperties().put(PROP_SKIP_SETTINGS, Boolean.TRUE);
    }


    public static boolean isSkipSettings(Window w) {
        Object x = w.getProperties().get(PROP_SKIP_SETTINGS);
        return Boolean.TRUE.equals(x);
    }

    /**
     * Creates a simple color background.
     */
    public static Background background(Paint c) {
        if (c == null) {
            return null;
        }
        return Background.fill(c);
    }


    public static Color gray(int col) {
        return Color.rgb(col, col, col);
    }


    public static Color gray(int col, double alpha) {
        return Color.rgb(col, col, col, alpha);
    }


    /**
     * Creates Color from an RGB value.
     */
    public static Color rgb(int rgb) {
        int r = (rgb >> 16) & 0xff;
        int g = (rgb >> 8) & 0xff;
        int b = (rgb) & 0xff;
        return Color.rgb(r, g, b);
    }

    /**
     * Creates Color from an RGB value.
     */
    public static Color rgb(int red, int green, int blue) {
        return Color.rgb(red, green, blue);
    }

    /**
     * Creates Color from an RGB value + alpha.
     */
    public static Color rgb(int rgb, double alpha) {
        int r = (rgb >> 16) & 0xff;
        int g = (rgb >> 8) & 0xff;
        int b = (rgb) & 0xff;
        return Color.rgb(r, g, b, alpha);
    }


    /**
     * Creates Color from an RGB value + alpha
     */
    public static Color rgb(int red, int green, int blue, double alpha) {
        return Color.rgb(red, green, blue, alpha);
    }


    public static boolean contains(Node n, double screenx, double screeny) {
        if (n != null) {
            Point2D p = n.screenToLocal(screenx, screeny);
            if (p != null) {
                return n.contains(p);
            }
        }
        return false;
    }


    /**
     * returns true if (x,y) point in eventSource coordinates is contained by eventTarget node
     */
    public static boolean contains(Node eventSource, Node eventTarget, double x, double y) {
        Point2D p = eventSource.localToScreen(x, y);
        if (p != null) {
            p = eventTarget.screenToLocal(p);
            if (p != null) {
                return eventTarget.contains(p);
            }
        }
        return false;
    }

    public static void setProperty(Node n, Object k, Object v) {
        if (v == null) {
            n.getProperties().remove(k);
        } else {
            n.getProperties().put(k, v);
        }
    }

    public static Object getProperty(Node n, Object k) {
        return n.getProperties().get(k);
    }


    /**
     * Returns parent window or null. Accepts either a Node, a Window, or a MenuItem.
     */
    public static Window getParentWindow(Object x) {
        if (x == null) {
            return null;
        } else if (x instanceof Window w) {
            return w;
        } else if (x instanceof Node n) {
            Scene s = n.getScene();
            if (s != null) {
                return s.getWindow();
            }
            return null;
        } else if (x instanceof MenuItem m) {
            ContextMenu cm = m.getParentPopup();
            return cm == null ? null : cm.getOwnerWindow();
        } else {
            throw new Error("node, window, or menu item " + x);
        }
    }


    /**
     * shortcut for Platform.runLater()
     */
    public static void later(Runnable r) {
        Platform.runLater(r);
    }

    /**
     * alias for Platform.isFxApplicationThread()
     */
    public static boolean isFX() {
        return Platform.isFxApplicationThread();
    }


    /**
     * swing invokeAndWait() analog.  if called from an FX application thread, simply invokes the producer.
     */
    public static <T> T invokeAndWait(Callable<T> producer) throws Exception {
        if (Platform.isFxApplicationThread()) {
            return producer.call();
        } else {
            FutureTask<T> t = new FutureTask<>(producer);
            FX.later(t);
            return t.get();
        }
    }


    /**
     * swing invokeAndWait() analog.  if called from an FX application thread, simply invokes the producer.
     */
    public static void invokeAndWait(Runnable action) throws Exception {
        if (Platform.isFxApplicationThread()) {
            action.run();
        } else {
            FutureTask<Boolean> t = new FutureTask<>(() ->
            {
                action.run();
                return Boolean.TRUE;
            });
            FX.later(t);
            t.get();
        }
    }

    /**
     * returns margin between the node and its containing window. WARNING: does not check if window is indeed a right
     * one.
     */
    public static Insets getInsetsInWindow(Window w, Node n) {
        Bounds b = n.localToScreen(n.getBoundsInLocal());

        double left = b.getMinX() - w.getX();
        double top = b.getMinY() - w.getY();
        double right = w.getX() + w.getWidth() - b.getMaxX();
        double bottom = w.getY() + w.getHeight() - b.getMaxY();

        return new Insets(top, right, bottom, left);
    }


    /**
     * returns true if the coordinates belong to one of the Screens
     */
    public static boolean isValidCoordinates(double x, double y) {
        for (Screen screen : Screen.getScreens()) {
            Rectangle2D r = screen.getVisualBounds();
            if (r.contains(x, y)) {
                return true;
            }
        }
        return false;
    }


    /**
     * converts degrees to radians
     */
    public static double toRadians(double degrees) {
        return degrees / DEGREES_PER_RADIAN;
    }


    /**
     * converts radians to degrees
     */
    public static double toDegrees(double radians) {
        return radians * DEGREES_PER_RADIAN;
    }


    /**
     * sets an opacity value for a color
     */
    public static Color alpha(Color c, double opacity) {
        return new Color(c.getRed(), c.getGreen(), c.getBlue(), opacity);
    }


    /**
     * adds a fraction of color to the base, using standard gamma value
     * <a href="https://en.wikipedia.org/wiki/Alpha_compositing">...</a>
     */
    public static Color mix(Color base, Color over, double fraction) {
        if (fraction <= 0.0) {
            return base;
        }

        if (base == null) {
            if (over == null) {
                return null;
            } else {
                return new Color(over.getRed(), over.getGreen(), over.getBlue(), over.getOpacity() * fraction);
            }
        } else if (over == null) {
            return base;
        }

        if (base.isOpaque()) {
            if (over.isOpaque()) {
                // simplified case of both colors opaque
                double r = mix(base.getRed(), over.getRed(), fraction);
                double g = mix(base.getGreen(), over.getGreen(), fraction);
                double b = mix(base.getBlue(), over.getBlue(), fraction);
                return new Color(r, g, b, 1.0);
            }
        }

        // full alpha blending
        double opacityBase = base.getOpacity();
        double opacityOver = clip(over.getOpacity() * fraction);

        double alpha = opacityOver + (opacityBase * (1.0 - opacityOver));
        if (alpha < 0.00001) {
            return new Color(0, 0, 0, 0);
        }

        double r = mix(base.getRed(), over.getRed(), opacityOver, alpha);
        double g = mix(base.getGreen(), over.getGreen(), opacityOver, alpha);
        double b = mix(base.getBlue(), over.getBlue(), opacityOver, alpha);
        return new Color(r, g, b, alpha);
    }


    private static double mix(double base, double over, double fraction) {
        double v = Math.pow(over, GAMMA) * fraction + Math.pow(base, GAMMA) * (1.0 - fraction);
        v = Math.pow(v, ONE_OVER_GAMMA);
        return clip(v);
    }


    private static double mix(double base, double over, double opacityOver, double alpha) {
        double v = Math.pow(over, GAMMA) * opacityOver + Math.pow(base, GAMMA) * (1.0 - opacityOver);
        v = v / alpha;
        v = Math.pow(v, ONE_OVER_GAMMA);
        return clip(v);
    }


    public static Color mix(Color[] colors, double gamma) {
        int sz = colors.length;

        double red = 0.0;
        double green = 0.0;
        double blue = 0.0;

        for (Color c : colors) {
            double op = c.getOpacity();

            double r = c.getRed();
            red += (Math.pow(r, gamma) * op);

            double g = c.getGreen();
            green += (Math.pow(g, gamma) * op);

            double b = c.getBlue();
            blue += (Math.pow(b, gamma) * op);
        }

        double oneOverGamma = 1.0 / gamma;
        red = clip(Math.pow(red / sz, oneOverGamma));
        green = clip(Math.pow(green / sz, oneOverGamma));
        blue = clip(Math.pow(blue / sz, oneOverGamma));

        return Color.color(red, green, blue);
    }


    public static Color mix(Color[] colors) {
        return mix(colors, GAMMA);
    }


    private static double clip(double c) {
        if (c < 0) {
            return 0;
        } else if (c >= 1.0) {
            return 1.0;
        }
        return c;
    }


    /**
     * deiconify and toFront()
     */
    public static void toFront(Stage w) {
        if (w.isIconified()) {
            w.setIconified(false);
        }

        w.toFront();
    }

    /**
     * attaches or removes (text=null) the Node's tooltip
     */
    public static void setTooltip(Node n, String text) {
        if (n != null) {
            if (text == null) {
                Tooltip t = getTooltip(n);
                Tooltip.uninstall(n, t);
                n.getProperties().remove(PROP_TOOLTIP);
            } else {
                Tooltip t = new Tooltip(text);
                Tooltip.install(n, t);
                n.getProperties().put(PROP_TOOLTIP, t);
            }
        }
    }

    private static Tooltip getTooltip(Node n) {
        if (n != null) {
            return (Tooltip) n.getProperties().get(PROP_TOOLTIP);
        }
        return null;
    }

    public static <T> ObservableList<T> observableArrayList() {
        return FXCollections.observableArrayList();
    }

    /**
     * attach a popup menu to a node. WARNING: sometimes, as the case is with TableView/FxTable header, the requested
     * node gets created by the skin at some later time. In this case, additional dance must be performed, see for
     * example FxTable.setHeaderPopupMenu()
     */
    public static void setPopupMenu(Node owner, Supplier<ContextMenu> generator) {
        if (owner == null) {
            throw new NullPointerException("cannot attach popup menu to null");
        }

        owner.setOnContextMenuRequested((ev) ->
        {
            if (generator != null) {
                ContextMenu m = generator.get();
                if (m != null) {
                    if (!m.getItems().isEmpty()) {
                        FX.later(() ->
                        {
                            // javafx does not dismiss the popup when the user
                            // clicks on the owner node
                            EventHandler<MouseEvent> li = new EventHandler<>() {
                                @Override
                                public void handle(MouseEvent event) {
                                    m.hide();
                                    owner.removeEventFilter(MouseEvent.MOUSE_PRESSED, this);
                                    event.consume();
                                }
                            };

                            owner.addEventFilter(MouseEvent.MOUSE_PRESSED, li);
                            m.show(owner, ev.getScreenX(), ev.getScreenY());
                        });
                        ev.consume();
                    }
                }
            }
            ev.consume();
        });
    }


    public static void checkThread() {
        if (!Platform.isFxApplicationThread()) {
            throw new Error("must be called from an FX application thread");
        }
    }

    /**
     * avoid ambiguous signature warning when using addListener
     */
    public static <T> void addChangeListener(ObservableList<T> list, ListChangeListener<? super T> li) {
        list.addListener(li);
    }


    /**
     * avoid ambiguous signature warning when using addListener
     */
    public static <T> void addChangeListener(ObservableList<T> list, Runnable callback) {
        list.addListener((ListChangeListener.Change<? extends T> ch) -> callback.run());
    }


    /**
     * avoid ambiguous signature warning when using addListener
     */
    public static <T> void addChangeListener(ObservableList<T> list, boolean fireImmediately, Runnable callback) {
        list.addListener((ListChangeListener.Change<? extends T> ch) -> callback.run());

        if (fireImmediately) {
            callback.run();
        }
    }


    public static <T> void addChangeListener(ObservableValue<T> prop, ChangeListener<? super T> li) {
        prop.addListener(li);
    }


    /**
     * simplified version of addChangeListener that only accepts the current value
     */
    public static <T> void addChangeListener(ObservableValue<T> prop, Consumer<? super T> li) {
        prop.addListener((s, p, current) -> li.accept(current));
    }


    /**
     * simplified version of addChangeListener that only invokes the callback on change
     */
    public static <T> void addChangeListener(ObservableValue<T> prop, Runnable callback) {
        prop.addListener((s, p, current) -> callback.run());
    }


    /**
     * simplified version of addChangeListener that only invokes the callback on change
     */
    public static <T> void addChangeListener(ObservableValue<T> prop, boolean fireImmediately, Runnable callback) {
        prop.addListener((s, p, current) -> callback.run());

        if (fireImmediately) {
            callback.run();
        }
    }


    /**
     * simplified version of addChangeListener that only accepts the current value
     */
    public static <T> void addChangeListener(ObservableValue<T> prop, boolean fireImmediately, Consumer<? super T> li) {
        prop.addListener((s, p, current) -> li.accept(current));

        if (fireImmediately) {
            li.accept(prop.getValue());
        }
    }

    public static Insets insets(double top, double right, double bottom, double left) {
        return new Insets(top, right, bottom, left);
    }


    public static Insets insets(double vert, double hor) {
        return new Insets(vert, hor, vert, hor);
    }


    public static Insets insets(double gap) {
        return new Insets(gap);
    }

    /**
     * applies global stylesheet on top of the javafx one
     */
    public static void applyStyleSheet(String old, String cur) {
        for (Window w : Window.getWindows()) {
            applyStyleSheet(w, old, cur);
        }
    }

    /**
     * applies global stylesheet to a specific window on top of the javafx one
     */
    public static void applyStyleSheet(Window w, String old, String cur) {
        if (cur != null) {
            Scene scene = w.getScene();
            if (scene != null) {
                if (old != null) {
                    scene.getStylesheets().remove(old);
                }

                scene.getStylesheets().add(cur);
            }
        }
    }

    public static void setName(Node n, String name) {
        n.getProperties().put(PROP_NAME, name);
    }


    public static String getName(Node n) {
        Object x = n.getProperties().get(PROP_NAME);
        if (x instanceof String s) {
            return s;
        }
        return null;
    }

    public static void setName(Window w, String name) {
        Objects.nonNull(name);
        w.getProperties().put(PROP_NAME, name);
    }

    public static String getName(Window w) {
        Object x = w.getProperties().get(PROP_NAME);
        if (x instanceof String s) {
            return s;
        }
        return null;
    }

    public static void center(Window window) {
        if (window instanceof Stage w) {
            if (w.getOwner() instanceof Stage owner) {
                Parent root = w.getScene().getRoot();
                root.applyCss();
                root.layout();

                double width = root.prefWidth(-1);
                double height = root.prefHeight(width);

                Scene ownerScene = owner.getScene();
                double ownerWidth = ownerScene.getRoot().prefWidth(-1);
                double ownerHeight = ownerScene.getRoot().prefHeight(ownerWidth);
                double cascadeOffset = 20;

                double x;
                if (width < ownerWidth) {
                    x = owner.getX() + (ownerScene.getWidth() - width) / 2.0;
                } else {
                    x = owner.getX() + cascadeOffset;
                    w.setWidth(width);
                }

                double y;
                if (height < ownerHeight) {
                    double titleBarHeight = ownerScene.getY();
                    y = owner.getY() + (titleBarHeight + ownerScene.getHeight() - height) / 2.0;
                } else {
                    y = owner.getY() + cascadeOffset;
                }

                w.setX(x);
                w.setY(y);
            }
        }
    }
}