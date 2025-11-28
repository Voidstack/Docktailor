package com.enosistudio.docktailor.utils;

import com.enosistudio.docktailor.DocktailorService;
import com.enosistudio.docktailor.fx.FX;
import com.enosistudio.docktailor.fx.fxdock.FxDockPane;
import com.enosistudio.docktailor.fx.fxdock.FxDockWindow;
import com.enosistudio.docktailor.fx.fxdock.internal.*;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.layout.Pane;
import javafx.stage.Window;
import lombok.experimental.UtilityClass;

import java.util.Objects;

/**
 * Orchestrates layout construction and pane movement operations in the docking system.
 * Responsible for creating splits, tabs, and moving panes between containers.
 */
@UtilityClass
public class LayoutComposerUtils {

    /**
     * Inserts a client pane into a root pane at the specified location, creating splits if necessary.
     *
     * @param client the pane to insert
     * @param target the root pane to insert into
     * @param where  the location to insert at
     */
    public static void insertPane(FxDockPane client, Node target, EWhere where) {
        if (target instanceof FxDockRootPane rp) {

            BeforeDrop b = new BeforeDrop(client, target);
            boolean makesplit = true;
            Node old = rp.getContent();
            if (old instanceof FxDockSplitPane fxDockSplitPane && insertSplit(client, fxDockSplitPane, where)) {
                makesplit = false;
            }

            if (makesplit) {
                rp.setContent(makeSplit(client, old, where));
            }

            HierarchyCleanupUtils.collapseEmptySpace(b.clientParent);
            b.adjustSplits();
        } else if (target instanceof FxDockPane) {
            // FIX
            throw new IllegalArgumentException("replace with split or tab");
        } else {
            // TODO
            throw new IllegalArgumentException("?" + target);
        }
    }

    /**
     * Creates a new FxDockWindow using the default window creation utility.
     *
     * @return a new FxDockWindow instance
     */
    public static FxDockWindow createWindow() {
        return DocktailorService.getSchema().createDefaultWindow();
    }

    /**
     * Moves the specified client pane to a new window at the given screen coordinates.
     *
     * @param client  the pane to move
     * @param screenx the x-coordinate for the new window
     * @param screeny the y-coordinate for the new window
     */
    public static void moveToNewWindow(FxDockPane client, double screenx, double screeny) {
        Node p = ParentTrackerUtils.getParent(client);
        double width = client.getWidth();
        double height = client.getHeight();

        FxDockWindow w = createWindow();
        FxDockTabPane fxDockRootPane = new FxDockTabPane(client);
        w.setContent(fxDockRootPane);

        w.setX(screenx - 10000);
        w.setY(screeny);

        // moving window after show() seems to cause flicker
        double op = w.getOpacity();

        w.open();

        // take into account window decorations
        // apparently, this is available only after show()
        Node n = w.getDockRootPane().getCenter();
        Insets m = FX.getInsetsInWindow(w, n);

        // this may still cause flicker
        w.setX(screenx - m.getLeft());
        w.setY(screeny - m.getTop());
        w.setWidth(width + m.getLeft() + m.getRight());
        w.setHeight(height + m.getTop() + m.getBottom());

        w.setOpacity(op);

        HierarchyCleanupUtils.collapseEmptySpace(p);

        // trick unflicker
        Platform.runLater(() -> w.setX(screenx));
    }

    /**
     * Moves the specified client pane to a new window, preserving its screen position and size.
     *
     * @param client the pane to move
     */
    public static void moveToNewWindow(FxDockPane client) {
        Node p = ParentTrackerUtils.getParent(client);
        Window clientWindow = WindowLocatorUtils.getWindow(client);

        // TODO still not correct
        Insets m = GeometryUtils.getWindowInsets(clientWindow);
        Point2D pos = client.localToScreen(0, 0);

        FxDockWindow w = createWindow();

        w.setContent(client);
        w.setX(pos.getX() - m.getLeft());
        w.setY(pos.getY() - m.getTop());
        w.setWidth(client.getWidth() + m.getRight() + m.getLeft());
        w.setHeight(client.getHeight() + m.getTop() + m.getBottom());
        w.open();

        HierarchyCleanupUtils.collapseEmptySpace(p);
    }

    /**
     * Moves the specified client pane to a split pane at the given index.
     *
     * @param client the pane to move
     * @param sp     the split pane to move into
     * @param index  the index at which to insert the pane
     */
    public static void moveToSplit(FxDockPane client, FxDockSplitPane sp, int index) {
        BeforeDrop b = new BeforeDrop(client, sp);
        sp.addPaneWithAnimationAt(index, client);
        HierarchyCleanupUtils.collapseEmptySpace(b.clientParent);
        b.adjustSplits();
    }

    /**
     * Moves the specified client pane to a new position within the target pane, creating necessary splits or tabs.
     *
     * @param client the FxDockPane to be moved.
     * @param target the Pane where the client will be moved to.
     * @param where  the position relative to the target where the client should be placed.
     * @throws IllegalArgumentException if the target's parent is not a recognized type.
     */
    public static void moveToPane(FxDockPane client, Pane target, EWhere where) {
        BeforeDrop b = new BeforeDrop(client, target);
        Node targetParent = ParentTrackerUtils.getParent(target);

        // handle the case where client and target are the same and where is CENTER
        if (client == target && where == EWhere.CENTER) {
            return;
        }

        if (targetParent instanceof FxDockSplitPane fxDockSplitPane) {
            int targetIndex = DockNodeUtils.indexInParent(target);
            addToSplitPane(client, target, fxDockSplitPane, targetIndex, where);
        } else if (targetParent instanceof FxDockTabPane fxDockTabPane) {
            int targetIndex = DockNodeUtils.indexInParent(target);
            addToTabPane(client, fxDockTabPane, targetIndex, where);
        } else if (targetParent instanceof FxDockRootPane fxDockRootPane) {
            addToRootPane(client, fxDockRootPane, where);
        } else {
            throw new IllegalArgumentException("?" + targetParent);
        }
        HierarchyCleanupUtils.collapseEmptySpace(b.clientParent);
        b.adjustSplits();
    }

    /**
     * Creates a split pane containing the client and old nodes.
     *
     * @param client the new pane to add
     * @param old    the existing pane
     * @param where  the position of the new pane relative to the old one
     * @return the split pane or the parent split if insertion was optimized
     */
    private static Node makeSplit(Node client, Node old, EWhere where) {
        if (client == old) {
            old = new FxDockEmptyPane();
        }

        // check if nested splits are not needed - if parent is already a split with matching orientation,
        // insert at the correct index relative to the old node
        Node p = ParentTrackerUtils.getParent(old);
        if (p instanceof FxDockSplitPane sp) {
            Orientation parentOrientation = sp.getOrientation();
            boolean canInsertDirectly = false;
            int insertIndex = -1;

            // Check if the split orientation matches the drop direction
            if (parentOrientation == Orientation.HORIZONTAL && (where == EWhere.LEFT || where == EWhere.RIGHT)) {
                canInsertDirectly = true;
                int oldIndex = sp.indexOfPane(old);
                insertIndex = (where == EWhere.LEFT) ? oldIndex : oldIndex + 1;
            } else if (parentOrientation == Orientation.VERTICAL && (where == EWhere.TOP || where == EWhere.BOTTOM)) {
                canInsertDirectly = true;
                int oldIndex = sp.indexOfPane(old);
                insertIndex = (where == EWhere.TOP) ? oldIndex : oldIndex + 1;
            }

            if (canInsertDirectly) {
                sp.addPaneWithAnimationAt(insertIndex, client);
                return sp;
            }
        }

        return switch (where) {
            case BOTTOM, RIGHT -> new FxDockSplitPane(where, old, client);
            case LEFT, TOP -> new FxDockSplitPane(where, client, old);
            default -> throw new IllegalArgumentException("?" + where);
        };
    }

    /**
     * Creates a tab pane containing the old and client nodes.
     *
     * @param old    the existing pane
     * @param client the new pane to add
     * @return a new FxDockTabPane with both panes
     */
    private static FxDockTabPane makeTab(Node old, Node client) {
        FxDockTabPane t = new FxDockTabPane();
        t.addTab(old);
        t.addTab(client);
        t.select(client);
        return t;
    }

    /**
     * Inserts a client pane into an existing split pane.
     *
     * @param client the pane to insert
     * @param sp     the split pane to insert into
     * @param where  the position to insert at
     * @return true if the insertion was successful
     */
    private static boolean insertSplit(FxDockPane client, FxDockSplitPane sp, EWhere where) {
        return sp.addPane(where, client);
    }

    /**
     * Adds a client pane to a root pane at the specified position.
     *
     * @param client the pane to add
     * @param rp     the root pane
     * @param where  the position to add at
     */
    private static void addToRootPane(FxDockPane client, FxDockRootPane rp, EWhere where) {
        Node old = rp.getContent();

        if (Objects.requireNonNull(where) == EWhere.CENTER) {
            rp.setContent(makeTab(old, client));
        } else {
            rp.setContent(makeSplit(client, old, where));
        }
    }

    /**
     * Adds a client pane to a tab pane.
     *
     * @param client the pane to add
     * @param tp     the tab pane
     * @param index  the index of the target tab
     * @param where  the position to add at
     */
    private static void addToTabPane(FxDockPane client, FxDockTabPane tp, int index, EWhere where) {
        if (Objects.requireNonNull(where) == EWhere.CENTER) {
            tp.addTab(client);
            tp.select(client);
        } else {
            Node p = ParentTrackerUtils.getParent(tp);
            int ix = DockNodeUtils.indexInParent(tp);
            Node sp = makeSplit(client, tp, where);
            if (p != sp) {
                DockNodeUtils.replaceChild(p, ix, sp);
            }
        }
    }

    /**
     * Adds a client pane to a split pane.
     *
     * @param client the pane to add
     * @param target the target pane within the split
     * @param sp     the split pane containing the target
     * @param index  the index of the target pane
     * @param where  the position to add at
     */
    private static void addToSplitPane(FxDockPane client, Pane target, FxDockSplitPane sp, int index, EWhere where) {
        // determine index from where and sp orientation
        int ix;
        if (sp.getOrientation() == Orientation.HORIZONTAL) {
            ix = switch (where) {
                case LEFT -> index;
                case RIGHT -> index + 1;
                default -> -1;
            };
        } else {
            ix = switch (where) {
                case BOTTOM -> index + 1;
                case TOP -> index;
                default -> -1;
            };
        }

        if (ix < 0) {
            Node p = ParentTrackerUtils.getParent(target);
            int ip = DockNodeUtils.indexInParent(target);

            if (where == EWhere.CENTER) {
                if (target instanceof FxDockEmptyPane) {
                    sp.setPane(ip, client);
                } else {
                    Node t = makeTab(target, client);
                    DockNodeUtils.replaceChild(p, ip, t);
                }
            } else {
                Node n = makeSplit(client, target, where);
                DockNodeUtils.replaceChild(p, ip, n);
            }
        } else {
            // simply insert another pane into this split pane with animation
            sp.addPaneWithAnimationAt(ix, client);
        }
    }
}
