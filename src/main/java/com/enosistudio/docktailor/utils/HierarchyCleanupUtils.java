package com.enosistudio.docktailor.utils;

import com.enosistudio.docktailor.fx.fxdock.internal.*;
import javafx.scene.Node;
import javafx.scene.layout.Pane;
import lombok.experimental.UtilityClass;

import java.util.function.IntConsumer;
import java.util.function.IntFunction;

/**
 * Manages node removal and hierarchy cleanup operations in the docking system.
 * Responsible for removing nodes, collapsing empty spaces, and maintaining a clean hierarchy.
 */
@UtilityClass
public class HierarchyCleanupUtils {

    /**
     * Removes a node from its parent and collapses any resulting empty space.
     *
     * @param n the node to remove
     */
    public static void remove(Node n) {
        Node p = ParentTrackerUtils.getParent(n);
        remove(p, n);
        collapseEmptySpace(p);
    }

    /**
     * Prepares a node to be added to the docking layout. If the node is null, returns a new FxDockEmptyPane. Otherwise,
     * unlinks the node from its current parent before returning it.
     *
     * @param n the node to prepare
     * @return the prepared node
     */
    public static Node prepareToAdd(Node n) {
        if (n == null) {
            return new FxDockEmptyPane();
        } else {
            // TODO this does not look right - use unlink before starting the move
            unlink(n);
            return n;
        }
    }

    /**
     * Collapses empty spaces within a parent node by removing unnecessary nodes.
     *
     * @param parent the parent node to collapse empty spaces in
     */
    public static void collapseEmptySpace(Node parent) {
        if (parent instanceof FxDockSplitPane sp) {
            processCollapseEmptySpace(sp.getPaneCount(), sp::getPane, sp::removePane, parent);
        } else if (parent instanceof FxDockTabPane tp) {
            processCollapseEmptySpace(tp.getTabCount(), tp::getTab, tp::removeTab, parent);
        } else if (parent instanceof FxDockRootPane rp && isEmpty(rp.getContent()) && !WindowLocatorUtils.closeWindowUnlessLast(parent))
            rp.setContent(null);
    }

    /**
     * Removes a child node from its parent.
     *
     * @param parent the parent node
     * @param child  the child node to remove
     * @throws IllegalArgumentException if the parent type is not recognized
     */
    private static void remove(Node parent, Node child) {
        if (parent instanceof FxDockTabPane p) {
            p.removeTab(child);
        } else if (parent instanceof FxDockSplitPane p) {
            p.removePane(child);
        } else if (parent instanceof Pane p) {
            p.getChildren().remove(child);
        } else {
            throw new IllegalArgumentException("?" + parent);
        }
    }

    /**
     * Unlinks a node from its current parent by replacing it with a DeletedPane.
     *
     * @param node the node to unlink
     */
    private static void unlink(Node node) {
        Node parent = ParentTrackerUtils.getParent(node);
        if (parent == null) return;

        if (parent instanceof FxDockSplitPane splitPane) {
            // Replace the node with a DeletedPane in the split pane
            splitPane.setPane(DockNodeUtils.indexInParent(node), new DeletedPane());
        } else if (parent instanceof FxDockRootPane rootPane) {
            // Replace the content of the root pane with a DeletedPane
            rootPane.setContent(new DeletedPane());
        } else if (!(parent instanceof FxDockTabPane || parent instanceof DeletedPane)) {
            // Throw an exception for unexpected parent types
            throw new IllegalArgumentException("Unexpected parent: " + parent);
        }
    }

    /**
     * Processes and collapses empty spaces within a parent node by removing unnecessary nodes and merging adjacent
     * empty spaces.
     *
     * @param count  the number of child nodes within the parent node.
     * @param get    a function to retrieve a child node at a specified index.
     * @param remove a consumer to remove a child node at a specified index.
     * @param parent the parent node containing the child nodes to be processed.
     */
    private static void processCollapseEmptySpace(int count, IntFunction<Node> get, IntConsumer remove, Node parent) {
        // Remove DeletedPane / null / empty FxDockTabPane
        for (int i = count - 1; i >= 0; i--) {
            Node n = get.apply(i);
            if (n == null || n instanceof DeletedPane || (n instanceof FxDockTabPane fx && fx.getTabs().isEmpty()))
                remove.accept(i);
        }
        // Merge adjacent empty spaces
        boolean empty = false;
        int ct = 0;
        int idx = -1;
        for (int i = count - 1; i >= 0; i--) {
            Node n = get.apply(i);
            if (isEmpty(n)) {
                if (empty) remove.accept(i);
                empty = !empty;
            } else {
                empty = false;
                ct++;
                idx = i;
            }
        }
        // Final case: replace or flatten
        if (ct < 2) {
            Node pp = ParentTrackerUtils.getParent(parent);
            switch (ct) {
                case 0 -> {
                    DockNodeUtils.replaceChild(pp, DockNodeUtils.indexInParent(parent), new DeletedPane());
                    collapseEmptySpace(pp);
                }
                case 1 -> {
                    if (parent instanceof FxDockSplitPane)
                        DockNodeUtils.replaceChild(pp, DockNodeUtils.indexInParent(parent), get.apply(idx));
                }
                default -> throw new IllegalArgumentException("?" + ct);
            }
        }
    }

    /**
     * Checks if a node is considered empty.
     *
     * @param n the node to check
     * @return true if the node is empty, false otherwise
     */
    private static boolean isEmpty(Node n) {
        if (n == null) {
            return true;
        } else if (n instanceof FxDockRootPane p) {
            return isEmpty(p.getContent());
        } else if (n instanceof FxDockBorderPane) {
            return false;
        } else if (n instanceof FxDockEmptyPane) {
            return true;
        } else if (n instanceof FxDockSplitPane p) {
            return (p.getPaneCount() == 0);
        } else if (n instanceof FxDockTabPane p) {
            return (p.getTabCount() == 0);
        }
        return true;
    }
}
