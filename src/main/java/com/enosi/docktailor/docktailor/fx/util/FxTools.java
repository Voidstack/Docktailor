// Copyright © 2022-2025 Andy Goryachev <andy@goryachev.com>
package com.enosi.docktailor.docktailor.fx.util;

import com.enosi.docktailor.common.util.CKit;
import com.enosi.docktailor.docktailor.fx.FX;
import javafx.stage.Stage;
import javafx.stage.Window;

import java.util.List;


/**
 * FxTools.
 */
public class FxTools {
    public static String describe(Window w) {
        if (w == null) {
            return "<null>";
        }

        if (w instanceof Stage s) {
            String title = s.getTitle();
            if (CKit.isNotBlank(title)) {
                return title;
            }
        }
        return w.getClass().getSimpleName() + "(" + FX.getName(w) + ")";
    }
}