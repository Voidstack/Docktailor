package com.enosistudio.docktailor.sample;

import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.MenuBar;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import net.yetihafen.javafx.customcaption.DragRegion;

public class CustomMenuBar extends DragRegion {
    private final HBox right = new HBox();
    private final DocktailorSaveBtn saveBtn =new DocktailorSaveBtn();


    public CustomMenuBar(HBox hbox, MenuBar menuBar) {
        super(hbox);

        right.setAlignment(Pos.CENTER_RIGHT);
        HBox.setHgrow(right, Priority.ALWAYS);

        initRight();
        hbox.getChildren().addAll( right);

        HBox box = (HBox)menuBar.getChildrenUnmodifiable().get(0);

        for (Node node : box.getChildrenUnmodifiable()) {
            this.addExcludeBounds(node);
        }

        for (Node node : right.getChildrenUnmodifiable()) {
            this.addExcludeBounds(node);
        }
    }

    private void initRight() {
        right.getChildren().add(saveBtn);
    }

    public void displayBtnSave(boolean bool){
        saveBtn.displayBtnSave(bool);
    }
}
