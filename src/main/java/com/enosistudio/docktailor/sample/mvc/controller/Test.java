package com.enosistudio.docktailor.sample.mvc.controller;

import com.enosistudio.docktailor.fxdock.internal.ADockPane;
import com.enosistudio.docktailor.svg.SVGRegion;
import javafx.geometry.Side;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.Region;
import lombok.Getter;

@Getter
public class Test extends ADockPane {
    private final Side defaultSide = Side.LEFT;
    private final String tabName = "TestControler";
    private final String information = "Ceci est un test de controller";

    @Override
    public Node getTabIcon() {
        return new SVGRegion("svg/fontawesome/magnifying-glass.svg", 12);
    }

    @Override
    public Node loadView() {
        return new Region();
    }

}
