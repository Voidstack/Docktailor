package com.enosistudio.docktailor.sample.controller;

import com.enosistudio.docktailor.fx.fxdock.internal.ADockPane;
import javafx.geometry.Side;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.layout.Region;

public class GreenDockPane extends ADockPane {
    @Override
    public Side getDefaultSide() {
        return Side.LEFT;
    }

    @Override
    public String getTabName() {
        return "Green";
    }

    @Override
    public Node getTabIcon() {
        return null;
    }

    @Override
    public Parent loadView() {
        Region root = new Region();
        root.setStyle("-fx-background-color: darkgreen;");
        return root;
    }

    @Override
    public String getInformation() {
        return "lorem ipsum";
    }
}
