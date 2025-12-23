package com.enosistudio.docktailor.sample;

import com.enosistudio.docktailor.DocktailorService;
import com.enosistudio.docktailor.DocktailorUtility;
import javafx.animation.FadeTransition;
import javafx.animation.Interpolator;
import javafx.animation.ParallelTransition;
import javafx.animation.TranslateTransition;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.Tooltip;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.List;

/**
 * Simple button that allows saving. Also saves instances to enable grouped hiding during save.
 */
public class DocktailorSaveBtn extends Button {

    private static final List<DocktailorSaveBtn> instances = new ArrayList<>();


    private final FadeTransition fadeIn = new FadeTransition(Duration.millis(300), this);
    private final FadeTransition fadeOut = new FadeTransition(Duration.millis(300), this);
    private final TranslateTransition tt = new TranslateTransition(Duration.millis(300), this);
    private final ParallelTransition inTransition = new ParallelTransition(fadeIn, tt);

    // Method used for saving
    private final EventHandler<ActionEvent> onSave = event -> {
        DocktailorUtility.storeLayout(DocktailorService.getInstance().getLastUIConfigUsed());
        DocktailorService.getInstance().getConfigDocktailor().save();
        displayBtnSave(false);
    };

    DocktailorSaveBtn() {
        super("\uD83D\uDCBE");
        instances.add(this);
        initAnimation();

        this.setOnAction(onSave);
        displayBtnSave(false);
        this.setTooltip(new Tooltip("Save the current UI configuration."));
    }

    private void initAnimation() {

        fadeIn.setFromValue(0);
        fadeIn.setToValue(1);
        fadeOut.setFromValue(1);
        fadeOut.setToValue(0);
        tt.setFromY(-25);
        tt.setToY(0);
        tt.setInterpolator(Interpolator.EASE_OUT);
    }

    public void displayBtnSave(boolean b) {
        for (DocktailorSaveBtn instance : instances) {

            if (instance.isMouseTransparent() == !b)
                continue;
            instance.setMouseTransparent(!b);
            (b ? instance.inTransition : instance.fadeOut).play();
        }
    }
}