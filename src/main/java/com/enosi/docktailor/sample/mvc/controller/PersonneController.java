package com.enosi.docktailor.sample.mvc.controller;

import com.enosi.docktailor.docktailor.dock.AControllerDockPane;
import com.enosi.docktailor.utils.R;
import javafx.geometry.Side;
import javafx.scene.Parent;

public class PersonneController extends AControllerDockPane {
    @Override
    public Side getDefaultSide() {
        return Side.LEFT;
    }

    @Override
    public String getTabName() {
        return "Personne";
    }

    @Override
    public String getTabIcon() {
        return "*";
    }

    @Override
    public Parent getView() {
        return R.loadParentFromFxml("fxml/personne.fxml");
    }

    @Override
    public String getInformation() {
        return "lorem ipsum";
    }
}
