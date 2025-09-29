package com.enosistudio.docktailor.sample.mvc.controller;

import com.enosistudio.docktailor.fxdock.internal.ADockPane;
import com.enosistudio.docktailor.fx.R;
import javafx.geometry.Side;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Label;

public class PersonDockPane extends ADockPane {
    @Override
    public Side getDefaultSide() {
        return Side.LEFT;
    }

    @Override
    public String getTabName() {
        return "Personne";
    }

    @Override
    public Node getTabIcon() {
        return null;
    }

    @Override
    public Parent loadView() {
        return R.loadParentFromFxml("fxml/personne.fxml");
    }

    @Override
    public String getInformation() {
        return "lorem ipsum";
    }
}
