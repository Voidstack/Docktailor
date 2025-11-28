package com.enosistudio.docktailor.utils;

import com.enosistudio.docktailor.fx.FX;
import com.enosistudio.docktailor.fx.WindowMonitor;
import com.enosistudio.docktailor.fx.fxdock.FxDockPane;
import com.enosistudio.docktailor.fx.fxdock.FxDockWindow;
import com.enosistudio.docktailor.fx.fxdock.internal.FxDockEmptyPane;
import com.enosistudio.docktailor.fx.fxdock.internal.FxDockSplitPane;
import com.enosistudio.docktailor.fx.fxdock.internal.FxDockTabPane;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Window;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Utility class for locating windows and dock elements in the docking system.
 * Provides spatial discovery of UI components based on screen coordinates.
 */
@Slf4j(topic = "WindowLocator")
@UtilityClass
public class WindowLocatorUtils {
    /**
     * Finds the topmost window at the specified screen coordinates.
     *
     * @param screenx the x-coordinate on the screen
     * @param screeny the y-coordinate on the screen
     * @return the topmost FxDockWindow at the specified coordinates, or null if none is found
     */
    public static FxDockWindow findWindow(double screenx, double screeny) {
        List<FxDockWindow> list = null;
        for (Window w : Window.getWindows()) {
            if (w instanceof FxDockWindow dw && !dw.isIconified() && GeometryUtils.contains(dw, screenx, screeny)) {
                if (list == null) {
                    list = new ArrayList<>();
                }
                list.add(dw);
            }
        }

        if (list == null) {
            return null;
        } else if (list.size() == 1) {
            return list.get(0);
        } else {
            return WindowMonitor.findTopWindow(list);
        }
    }

    /**
     * Retrieves a list of visible FxDockWindows, ordered from topmost to bottommost.
     *
     * @return a list of visible FxDockWindows
     */
    public static List<FxDockWindow> getWindows() {
        List<Window> ws = WindowMonitor.getWindowStack();
        int sz = ws.size();
        List<FxDockWindow> rv = new ArrayList<>(sz);
        for (int i = sz - 1; i >= 0; i--) {
            Window w = ws.get(i);
            if (w instanceof FxDockWindow dw) {
                rv.add(dw);
            }
        }
        return rv;
    }

    /**
     * Retrieves the FxDockWindow associated with a given node.
     *
     * @param n the node to retrieve the window for
     * @return the FxDockWindow associated with the node, or null if none is found
     */
    public static FxDockWindow getWindow(Node n) {
        if (n != null) {
            Scene sc = n.getScene();
            if (sc != null) {
                Window w = sc.getWindow();
                if (w instanceof FxDockWindow fxDockWindow) {
                    return fxDockWindow;
                }
            }
        }
        return null;
    }

    /**
     * Closes the window containing the specified node unless it is the last window.
     *
     * @param n the node whose window should be closed
     * @return true if the window was closed, false otherwise
     */
    public static boolean closeWindowUnlessLast(Node n) {
        log.debug(n.toString());
        FxDockWindow w = getWindow(n);
        if (w != null && getWindows().size() > 1) {
            // do not store the empty window
            FX.setSkipSettings(w);
            w.close();
            return true;
        }
        return false;
    }

    /**
     * Finds the dock element at the specified screen coordinates within the given node.
     * Returns the MOST SPECIFIC (deepest) element under the cursor.
     *
     * @param n       the node to search within
     * @param screenx the x-coordinate on the screen
     * @param screeny the y-coordinate on the screen
     * @return the dock element node at the specified coordinates, or null if none is found
     */
    public static Node findDockElement(Node n, double screenx, double screeny) {
        if (n != null) {
            Point2D pt = n.screenToLocal(screenx, screeny);
            if (n.contains(pt)) {
                if (n instanceof FxDockPane) {
                    return n;
                } else if (n instanceof FxDockEmptyPane) {
                    return n;
                } else if (n instanceof FxDockSplitPane p) {
                    // Search children in REVERSE order to prioritize rightmost/bottommost panes
                    // This fixes issues with nested splits where the wrong pane is detected
                    Node ch = findDockElementReverse(p.getPanes(), screenx, screeny);
                    // on a divider or no child contains the point
                    return Objects.requireNonNullElse(ch, n);
                } else if (n instanceof FxDockTabPane t) {
                    return t.getSelectedTab();
                }

                if (n instanceof Parent) {
                    return findDockElement(((Parent) n).getChildrenUnmodifiable(), screenx, screeny);
                }
            }
        }
        return null;
    }

    /**
     * Helper method to find dock element within a list of nodes.
     *
     * @param ns      the list of nodes to search
     * @param screenx the x-coordinate on the screen
     * @param screeny the y-coordinate on the screen
     * @return the dock element found, or null if none
     */
    private static Node findDockElement(List<Node> ns, double screenx, double screeny) {
        for (Node ch : ns) {
            Node found = findDockElement(ch, screenx, screeny);
            if (found != null) {
                return found;
            }
        }
        return null;
    }

    /**
     * Finds dock element by searching in reverse order (right-to-left or bottom-to-top).
     * This ensures that when multiple panes overlap in screen coordinates (due to nesting),
     * we return the most specific (rightmost/bottommost) one.
     *
     * @param ns      the list of nodes to search in reverse
     * @param screenx the x-coordinate on the screen
     * @param screeny the y-coordinate on the screen
     * @return the dock element found, or null if none
     */
    private static Node findDockElementReverse(List<Node> ns, double screenx, double screeny) {
        // Iterate in reverse to check rightmost/bottommost panes first
        for (int i = ns.size() - 1; i >= 0; i--) {
            Node ch = ns.get(i);
            Node found = findDockElement(ch, screenx, screeny);
            if (found != null) {
                return found;
            }
        }
        return null;
    }
}
