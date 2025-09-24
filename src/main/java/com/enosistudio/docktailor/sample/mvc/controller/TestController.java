package com.enosistudio.docktailor.sample.mvc.controller;

import com.enosistudio.docktailor.fxdock.internal.AControllerDockPane;
import javafx.geometry.Side;
import javafx.scene.Node;
import javafx.scene.layout.Region;

public class TestController extends AControllerDockPane {
    @Override
    public Side getDefaultSide() {
        return Side.LEFT;
    }

    @Override
    public String getTabName() {
        return "TestControler";
    }

    @Override
    public String getTabIcon() {
        return "*";
    }

    @Override
    public Node getView() {
        return new Region();
    }

    @Override
    public String getInformation() {
        return "info";
    }
}
