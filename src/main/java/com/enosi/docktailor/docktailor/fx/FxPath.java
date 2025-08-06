// Copyright © 2019-2025 Andy Goryachev <andy@goryachev.com>
package com.enosi.docktailor.docktailor.fx;

import javafx.scene.shape.*;

import java.util.Collection;

/**
 * Path with convenience methods.
 */
public class FxPath
        extends Path {
    public FxPath() {
    }

    public FxPath(Collection<? extends PathElement> elements) {
        super(elements);
    }

    public FxPath(PathElement... elements) {
        super(elements);
    }

    public void moveto(double x, double y) {
        add(new MoveTo(x, y));
    }

    public void lineto(double x, double y) {
        add(new LineTo(x, y));
    }

    public void close() {
        add(new ClosePath());
    }

    public void add(PathElement em) {
        getElements().add(em);
    }

    public void addAll(PathElement... elements) {
        getElements().addAll(elements);
    }

    public void addAll(Collection<? extends PathElement> elements) {
        getElements().addAll(elements);
    }
}