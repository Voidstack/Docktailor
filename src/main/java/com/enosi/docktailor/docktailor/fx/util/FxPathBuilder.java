package com.enosi.docktailor.docktailor.fx.util;

import com.enosi.docktailor.common.util.CList;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.PathElement;

import java.util.List;


/**
 * Utility to simplify code for building complex paths.
 */
public class FxPathBuilder {
    private final CList<PathElement> path = new CList<>();


    public FxPathBuilder() {
    }


    public void moveto(double x, double y) {
        add(new MoveTo(x, y));
    }


    public void lineto(double x, double y) {
        add(new LineTo(x, y));
    }


    public List<PathElement> getPath() {
        return path;
    }


    public void add(PathElement em) {
        path.add(em);
    }


    public void addAll(PathElement[] elements) {
        path.addAll(elements);
    }
}
