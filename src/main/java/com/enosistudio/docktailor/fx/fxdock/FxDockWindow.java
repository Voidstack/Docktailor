package com.enosistudio.docktailor.fx.fxdock;

import com.enosistudio.docktailor.common.DocktailorEvent;
import com.enosistudio.docktailor.fx.FX;
import com.enosistudio.docktailor.fx.fxdock.internal.FxDockEmptyPane;
import com.enosistudio.docktailor.fx.fxdock.internal.FxDockRootPane;
import com.enosistudio.docktailor.fx.fxdock.internal.FxDockSplitPane;
import com.enosistudio.docktailor.fx.fxdock.internal.FxDockTabPane;
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

    @Getter
    private final DocktailorEvent onDocktailorEvent = new DocktailorEvent();

    public FxDockWindow(String name) {
        FX.setName(this, name);

        root = new FxDockRootPane(this);
        frame = new BorderPane(root);
        parentStackPane = new StackPane(frame);

        Scene sc = new Scene(parentStackPane);

        // FxTooltipDebugCss.install(sc);
        setScene(sc);

        initDockEvent();
    }

    private void initDockEvent() {
        this.widthProperty().addListener((obs, oldVal, newVal) -> onDocktailorEvent.invoke());
        this.heightProperty().addListener((obs, oldVal, newVal) -> onDocktailorEvent.invoke());
        this.xProperty().addListener((obs, oldVal, newVal) -> onDocktailorEvent.invoke());
        this.yProperty().addListener((obs, oldVal, newVal) -> onDocktailorEvent.invoke());
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
            throw new IllegalArgumentException("Cannot add node because the parent type is not supported");
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