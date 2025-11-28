package com.enosistudio.docktailor.utils;

import com.enosistudio.docktailor.fx.fxdock.internal.FxDockSplitPane;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Window;
import lombok.experimental.UtilityClass;

import java.util.ArrayList;
import java.util.List;

/**
 * Utility class for geometric calculations in the docking system.
 * Provides pure functions for spatial computations without state.
 */
@UtilityClass
public class GeometryUtils {
    /**
     * Checks if a point is above the diagonal of a rectangle defined by two points.
     *
     * @param x  the x-coordinate of the point
     * @param y  the y-coordinate of the point
     * @param x0 the x-coordinate of the first rectangle corner
     * @param y0 the y-coordinate of the first rectangle corner
     * @param x1 the x-coordinate of the second rectangle corner
     * @param y1 the y-coordinate of the second rectangle corner
     * @return true if the point is above the diagonal, false otherwise
     */
    public static boolean aboveDiagonal(double x, double y, double x0, double y0, double x1, double y1) {
        double liney = y0 + (y1 - y0) * (x - x0) / (x1 - x0);
        return y < liney;
    }

    /**
     * Checks if a window contains a point at the specified screen coordinates.
     *
     * @param w       the window to check
     * @param screenx the x-coordinate on the screen
     * @param screeny the y-coordinate on the screen
     * @return true if the window contains the point, false otherwise
     */
    public static boolean contains(Window w, double screenx, double screeny) {
        double x = w.getX();
        if (screenx < x || screenx > (x + w.getWidth())) {
            return false;
        }

        double y = w.getY();
        if (screeny < y) {
            return false;
        } else return screeny <= (y + w.getHeight());
    }

    /**
     * Collects all divider panes from the specified split pane.
     *
     * @param sp the FxDockSplitPane to collect dividers from
     * @return a list of divider panes
     */
    public static List<Pane> collectDividers(FxDockSplitPane sp) {
        List<Pane> rv = new ArrayList<>();
        for (Node n : sp.lookupAll(".split-pane-divider")) {
            if (n instanceof Pane p && p.getParent() == sp) {
                rv.add(p);
            }
        }
        return rv;
    }

    /**
     * Calculates the insets (decoration sizes) of a window.
     *
     * @param w the window to calculate insets for
     * @return the insets representing the window decorations
     */
    public static Insets getWindowInsets(Window w) {
        Scene s = w.getScene();

        double left = s.getX();
        double top = s.getY();
        double right = w.getWidth() - s.getWidth() - left;
        double bottom = w.getHeight() - s.getHeight() - top;

        return new Insets(top, right, bottom, left);
    }
}
