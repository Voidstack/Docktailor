package com.enosistudio.docktailor.fx;

import com.enosistudio.docktailor.common.CKit;
import javafx.stage.Stage;
import javafx.stage.Window;


/**
 * FxTools.
 */
public class FxTools {
    private FxTools(){}

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