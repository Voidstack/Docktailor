package com.enosi.docktailor.fx;

import javafx.scene.control.MenuItem;

/**
 * A more convenient MenuItem.
 */
public class FxMenuItem extends MenuItem {

    public FxMenuItem(String text, FxAction a) {
        super(text);

        if (a == null) {
            setDisable(true);
        } else {
            a.attach(this);
        }
    }

    public FxMenuItem(String text, Runnable r) {
        super(text);
        new FxAction(r).attach(this);
    }

    public FxMenuItem(String text) {
        super(text);
        setDisable(true);
    }
}