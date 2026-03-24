package com.enosistudio.docktailor.utils;

import com.enosistudio.docktailor.fx.fxdock.internal.DeletedPane;
import com.enosistudio.docktailor.fx.fxdock.internal.FxDockSplitPane;
import com.enosistudio.docktailor.fx.fxdock.internal.IFxDockPane;
import javafx.scene.Node;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

import java.util.function.Consumer;

/**
 * Manages parent-child relationships in the docking system.
 * Provides exclusive responsibility for tracking and maintaining node hierarchy.
 */
@Slf4j
@UtilityClass
public class ParentTrackerUtils {
    /**
     * Sets the parent of a child node, replacing the old parent if necessary.
     *
     * @param p     the new parent node
     * @param child the child node to set the parent for
     */
    public static void setParent(Node p, Node child) {
        if (child instanceof IFxDockPane pane) {
            Node oldp = pane.dockParentProperty().get();
            if (oldp != null) {
                if (oldp == p) {
                    log.warn(String.format("same parent: %s", p)); // FIX ???
                } else {
                    int ix = DockNodeUtils.indexInParent(child);
                    if (ix >= 0) {
                        DockNodeUtils.replaceChild(oldp, ix, new DeletedPane());
                    }
                }
            }
            pane.setDockParent(p);
        } else if (!(child instanceof DeletedPane)) {
            throw new IllegalArgumentException("?" + child);
        }
    }

    /**
     * Retrieves the parent of a given node.
     *
     * @param n the node to retrieve the parent for
     * @return the parent node, or null if the node has no parent
     */
    public static Node getParent(Node n) {
        if (n instanceof IFxDockPane p) {
            return p.dockParentProperty().get();
        }
        return null;
    }

    /**
     * Applies an action to all parent FxDockSplitPane instances in the hierarchy.
     * Walks up the parent chain from the given node and applies the action to each FxDockSplitPane encountered.
     *
     * @param node   the starting node
     * @param action the action to apply to each parent split pane
     */
    public static void applyToParentSplits(Node node, Consumer<FxDockSplitPane> action) {
        Node parent = getParent(node);
        while (parent != null) {
            if (parent instanceof FxDockSplitPane parentSplit) {
                action.accept(parentSplit);
            }
            parent = getParent(parent);
        }
    }
}
