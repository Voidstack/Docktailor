package com.enosi.docktailor.fxdock.internal;

import com.enosi.docktailor.fx.FX;
import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.List;


/**
 * Drop Operation - contains highlights and the code to handle the drop.
 */
public abstract class ADockDropOperation {
    private final Pane target;
    private final Object where;
    private List<Node> highlights;

    protected ADockDropOperation(Pane target, Object where) {
        this.target = target;
        this.where = where;
        this.highlights = new ArrayList<>();
    }

    protected abstract void executePrivate();

    public void execute() {
        executePrivate();
    }

    public boolean isSame(ADockDropOperation op) {
        return op != null && target == op.target && where.equals(op.where);
    }

    public void addRect(Node ref, double x, double y, double w, double h) {
        BoundingBox screenr = new BoundingBox(x, y, w, h);
        Bounds b = ref.localToScreen(screenr);
        b = target.screenToLocal(b);

        Region r = new Region();
        r.relocate(b.getMinX(), b.getMinY());
        r.resize(b.getWidth(), b.getHeight());
        r.setBackground(FX.background(Color.color(0, 0, 0, 0.3)));

        add(r);
    }

    protected void add(Node n) {
        highlights.add(n);
    }

    public void installHighlights() {
        if (target != null) target.getChildren().addAll(highlights);
        // Cannot highlight on null target.
    }

    public void removeHighlights() {
        if(target != null) target.getChildren().removeAll(highlights);
        // Cannot remove highlights from null target.
    }

    @Override
    public String toString() {
        return "op:" + where;
    }
}