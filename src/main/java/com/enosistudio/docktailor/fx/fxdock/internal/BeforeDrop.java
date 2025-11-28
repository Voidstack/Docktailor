package com.enosistudio.docktailor.fx.fxdock.internal;

import com.enosistudio.docktailor.utils.ParentTrackerUtils;
import javafx.scene.Node;
import javafx.scene.layout.Region;

import java.util.HashMap;
import java.util.Map;

/**
 * BeforeDrop.
 */
public class BeforeDrop {
    public final Node client;
    public final Node clientParent;
    private final Map<Node, Object> data = new HashMap<>();

    public BeforeDrop(Node client, Node target) {
        this.client = client;
        this.clientParent = ParentTrackerUtils.getParent(client);

        collectSizes(client);
        collectSizes(target);
    }

    protected void collectSizes(Node n) {
        while (n != null) {
            if (n instanceof FxDockSplitPane p) {
                sizeSplit(p);
            } else if (n instanceof FxDockRootPane) {
                return;
            } else {
                sizeNode(n);
            }

            n = ParentTrackerUtils.getParent(n);
        }
    }

    protected void sizeSplit(FxDockSplitPane p) {
        SplitSize s = new SplitSize();
        s.childCount = p.getPaneCount();
        s.dividers = p.getDividerPositions();
        data.put(p, s);
    }

    protected void sizeNode(Node n) {
        if (n instanceof Region r) {
            data.put(n, new DockSize(r.getWidth(), r.getHeight()));
        }
    }

    public void adjustSplits() {
        adjustSplits(client);
    }

    protected void adjustSplits(Node n) {
        while (n != null) {
            if (n instanceof FxDockSplitPane p) {
                restoreSplits(p);
            } else if (n instanceof FxDockRootPane) {
                return;
            }

            n = ParentTrackerUtils.getParent(n);
        }
    }

    /**
     * Restores split pane divider positions after a drop operation.
     * Only restores if the pane count hasn't changed; otherwise, divider positions
     * are handled by FxDockSplitPane.initializeDividersRecursively().
     */
    protected void restoreSplits(FxDockSplitPane p) {
        Object x = data.get(p);
        if (x != null) {
            SplitSize s = (SplitSize) x;
            if (s.childCount == p.getPaneCount()) {
                // Restore previous configuration only if pane count unchanged
                p.setDividerPositions(s.dividers);
            }
            // If pane count changed, dividers are initialized by FxDockSplitPane.initializeDividersRecursively()
        }
        // If no saved data, dividers are initialized by FxDockSplitPane.initializeDividersRecursively()
    }

    static class SplitSize {
        public int childCount;
        public double[] dividers;
    }

    private record DockSize(double width, double height) {

    }
}