package com.enosistudio.docktailor.fx;

import javafx.application.Platform;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.Background;
import javafx.scene.paint.Paint;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.Window;

import java.util.Objects;

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
 *
 * @see FxMenuItem
 */
public final class FX {
    private static final Object PROP_NAME = new Object();
    private static final Object PROP_SKIP_SETTINGS = new Object();

    private FX(){}

    public static Stage getWindow(Node n) {
        Scene sc = n.getScene();
        if (sc != null) {
            Window w = sc.getWindow();
            if (w instanceof Stage fxWindow) {
                return fxWindow;
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
            throw new IllegalArgumentException("node, window, or menu item " + x);
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
     * avoid ambiguous signature warning when using addListener
     */
    public static <T> void addChangeListener(ObservableList<T> list, ListChangeListener<? super T> li) {
        list.addListener(li);
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
}