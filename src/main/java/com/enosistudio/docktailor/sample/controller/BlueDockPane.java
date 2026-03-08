package com.enosistudio.docktailor.sample.controller;

import com.enosistudio.docktailor.fx.fxdock.internal.ADockPane;
import javafx.geometry.Side;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.layout.Region;
import lombok.extern.slf4j.Slf4j;

@Slf4j(topic = "BlueDockPane")
public class BlueDockPane extends ADockPane {
    @Override
    public Side getDefaultSide() {
        return Side.LEFT;
    }

    @Override
    public String getTabName() {
        return "Blue";
    }

    @Override
    public Node getTabIcon() {
        return null;
    }

    @Override
    public Parent loadView() {
        Region root = new Region();
        root.setStyle("-fx-background-color: darkblue;");
        return root;
    }

    @Override
    public String getInformation() {
        return "lorem ipsum";
    }

    @Override
    public void onClose() {
        super.onClose();
        log.info("onClose");
    }

    @Override
    public void onDragOutFromWindow() {
        super.onDragOutFromWindow();
        log.info("onDragOutFromWindow");
    }
}
