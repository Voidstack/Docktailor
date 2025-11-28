package com.enosistudio.docktailor.sample.controller;

import com.enosistudio.docktailor.fx.fxdock.internal.ADockPane;
import com.enosistudio.docktailor.fx.svg.SVGRegion;
import com.enosistudio.docktailor.generated.R;
import javafx.geometry.Side;
import javafx.scene.Node;
import javafx.scene.layout.Region;
import lombok.Getter;

@Getter
public class TestDockPane extends ADockPane {
    private final Side defaultSide = Side.LEFT;
    private final String tabName = "TestControler";
    private final String information = "This is a test controller";

    private final String svgIconFile = R.com.enosistudio.docktailor.fontawesome.magnifyingGlassSvg.getResourcePath();

    @Override
    public Node getTabIcon() {
        return new SVGRegion(svgIconFile, 12);
    }

    @Override
    public Node loadView() {
        return new Region();
    }

}
