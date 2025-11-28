package com.enosistudio.docktailor.utils;

import com.enosistudio.docktailor.fx.fxdock.FxDockPane;
import com.enosistudio.docktailor.fx.fxdock.internal.FxDockRootPane;
import com.enosistudio.docktailor.fx.fxdock.internal.FxDockSplitPane;
import com.enosistudio.docktailor.fx.fxdock.internal.FxDockTabPane;
import javafx.scene.Node;
import lombok.experimental.UtilityClass;

/**
 * Low-level utility class for node manipulation in the docking system.
 * Provides helper methods for finding indices and replacing children.
 */
@UtilityClass
public class DockNodeUtils {
    /**
     * Finds the index of a node within its parent container.
     *
     * @param node the node to find the index of
     * @return the index of the node within its parent, or -1 if not found or parent is not a recognized type
     */
    public static int indexInParent(Node node) {
        Node n = ParentTrackerUtils.getParent(node);
        if (n instanceof FxDockSplitPane p) {
            return p.indexOfPane(node);
        } else if (n instanceof FxDockTabPane p) {
            return p.indexOfTab(node);
        }
        return -1;
    }

    /**
     * Replaces a child node at a specified index within a parent node with a new child node.
     *
     * @param parent   the parent node containing the child to be replaced. Must be an instance of FxDockSplitPane,
     *                 FxDockTabPane, or FxDockRootPane.
     * @param index    the index of the child node to be replaced within the parent node.
     * @param newChild the new child node to replace the existing child node. If the parent is an FxDockRootPane and the
     *                 new child is an instance of FxDockPane, it will be wrapped in an FxDockTabPane.
     * @throws IllegalArgumentException if the parent node is not a recognized type.
     */
    static void replaceChild(Node parent, int index, Node newChild) {
        if (parent instanceof FxDockSplitPane p) {
            p.setPane(index, newChild);
        } else if (parent instanceof FxDockTabPane p) {
            p.setTab(index, newChild);
        } else if (parent instanceof FxDockRootPane p) {
            if (newChild instanceof FxDockPane) {
                p.setContent(new FxDockTabPane(newChild));
            } else {
                p.setContent(newChild);
            }
        } else {
            throw new IllegalArgumentException("?" + parent);
        }
    }
}
