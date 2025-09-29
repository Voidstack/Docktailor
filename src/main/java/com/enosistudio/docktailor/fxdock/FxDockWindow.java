package com.enosistudio.docktailor.fxdock;

import com.enosistudio.docktailor.fx.FxTooltipDebugCss;
import com.enosistudio.docktailor.fx.FxWindow;
import com.enosistudio.docktailor.fxdock.internal.FxDockEmptyPane;
import com.enosistudio.docktailor.fxdock.internal.FxDockRootPane;
import com.enosistudio.docktailor.fxdock.internal.FxDockSplitPane;
import com.enosistudio.docktailor.fxdock.internal.FxDockTabPane;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;


/**
 * Base class for docking framework Stage.
 */
public class FxDockWindow extends FxWindow {
    private final BorderPane frame;
    private final FxDockRootPane root;


    public FxDockWindow(String name) {
        super(name);

        root = new FxDockRootPane(this);
        frame = new BorderPane(root);

        Scene sc = new Scene(frame);

        FxTooltipDebugCss.install(sc);

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

    @Override
    public final Node getTop() {
        return frame.getTop();
    }

    @Override
    public final void setTop(Node n) {
        frame.setTop(n);
    }

    @Override
    public final Node getBottom() {
        return frame.getBottom();
    }

    @Override
    public final void setBottom(Node n) {
        frame.setBottom(n);
    }

    @Override
    public final Node getLeft() {
        return frame.getLeft();
    }

    @Override
    public final void setLeft(Node n) {
        frame.setLeft(n);
    }

    @Override
    public final Node getRight() {
        return frame.getRight();
    }

    @Override
    public final void setRight(Node n) {
        frame.setRight(n);
    }
}