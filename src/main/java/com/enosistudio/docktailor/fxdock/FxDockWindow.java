package com.enosistudio.docktailor.fxdock;

import com.enosistudio.docktailor.fx.FX;
import com.enosistudio.docktailor.fxdock.internal.FxDockEmptyPane;
import com.enosistudio.docktailor.fxdock.internal.FxDockRootPane;
import com.enosistudio.docktailor.fxdock.internal.FxDockSplitPane;
import com.enosistudio.docktailor.fxdock.internal.FxDockTabPane;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import lombok.Getter;


/**
 * Base class for docking framework Stage.
 */
public class FxDockWindow extends Stage {
    @Getter
    private final StackPane parentStackPane;
    private final BorderPane frame;
    private final FxDockRootPane root;

    public FxDockWindow(String name) {
        FX.setName(this, name);

        root = new FxDockRootPane(this);
        frame = new BorderPane(root);
        parentStackPane = new StackPane(frame);

        Scene sc = new Scene(parentStackPane);

        // FxTooltipDebugCss.install(sc);

        setScene(sc);
    }

    public void addDockPane(FxDockPane newDockPane) {
        if (getContent() instanceof FxDockEmptyPane) {
            FxDockTabPane dock = new FxDockTabPane(newDockPane);
            this.setContent(dock);
        } else if (getContent() instanceof FxDockPane fxDockPane) {
            FxDockTabPane dock = new FxDockTabPane(fxDockPane);
            dock.addTab(newDockPane);
            this.setContent(dock);
        } else if (getContent() instanceof FxDockSplitPane fxDockSplitPane) {
            fxDockSplitPane.addPane(newDockPane);
        } else if (getContent() instanceof FxDockTabPane fxDockPane) {
            fxDockPane.addTab(newDockPane);
        } else {
            throw new IllegalArgumentException("Impossible d'ajouter la node car le parent n'est pas géré");
        }
    }

    public final FxDockRootPane getDockRootPane() {
        return root;
    }

    public final Node getContent() {
        return root.getContent();
    }

    public final void setContent(Node n) {
        root.setContent(n);
    }

    public void open(){
        show();
    }

    public final Node getTop() {
        return frame.getTop();
    }

    public final void setTop(Node n) {
        frame.setTop(n);
    }

    public final Node getBottom() {
        return frame.getBottom();
    }


    public final void setBottom(Node n) {
        frame.setBottom(n);
    }


    public final Node getLeft() {
        return frame.getLeft();
    }

    public final void setLeft(Node n) {
        frame.setLeft(n);
    }

    public final Node getRight() {
        return frame.getRight();
    }

    public final void setRight(Node n) {
        frame.setRight(n);
    }
}