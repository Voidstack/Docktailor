package org.esioc.docktailor.fx;

import javafx.util.StringConverter;


/**
 * A StringConverter extension.
 */
public abstract class FxFormatter
        extends StringConverter<Object> {
    public FxFormatter() {
    }

    //

    @Override
    public abstract String toString(Object x);

    @Override
    public Object fromString(String string) {
        throw new Error("FxFormatter: fromString not supported");
    }


    public String format(Object x) {
        return toString(x);
    }
}
