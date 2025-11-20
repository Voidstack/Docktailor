package com.enosistudio.docktailor.sample.controller;

import com.enosistudio.docktailor.fx.fxdock.internal.ADockPane;
import com.enosistudio.docktailor.generated.R;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Side;
import javafx.scene.Node;
import javafx.scene.Parent;

import java.io.IOException;

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
        try {
            return FXMLLoader.load(R.com.enosistudio.docktailor.fxml.personneFxml.getURL());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
//        return R.loadParentFromFxml("docktailor/fxml/personne.fxml");
    }

    @Override
    public String getInformation() {
        return "lorem ipsum";
    }
}
