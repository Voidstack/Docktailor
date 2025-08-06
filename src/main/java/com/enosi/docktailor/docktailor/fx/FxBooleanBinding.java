// Copyright © 2019-2025 Andy Goryachev <andy@goryachev.com>
package org.esioc.docktailor.fx;

import javafx.beans.Observable;
import javafx.beans.binding.BooleanBinding;


/**
 * FxBooleanBinding.
 */
public abstract class FxBooleanBinding
        extends BooleanBinding {
    public FxBooleanBinding(Observable... dependencies) {
        bind(dependencies);
    }

    @Override
    protected abstract boolean computeValue();
}
