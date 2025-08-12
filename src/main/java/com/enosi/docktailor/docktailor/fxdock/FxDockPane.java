package com.enosi.docktailor.docktailor.fxdock;

import com.enosi.docktailor.docktailor.fx.FxAction;
import com.enosi.docktailor.docktailor.fxdock.internal.DockTools;
import com.enosi.docktailor.docktailor.fxdock.internal.DragAndDropHandler;
import com.enosi.docktailor.docktailor.fxdock.internal.FxDockBorderPane;
import com.enosi.docktailor.docktailor.fxdock.internal.FxDockTabPane;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyBooleanWrapper;
import javafx.beans.property.SimpleStringProperty;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.HBox;
import lombok.Getter;

/**
 * FxDockPane is a base class for all panes that can be placed in the docking framework. When restoring the layout,
 * concrete instances of FxDockPanes are created by the application FxDockWindow implementation.
 */
public abstract class FxDockPane
        extends FxDockBorderPane {
    public final FxAction closeAction = new FxAction(this::actionClose);
    public final FxAction popToWindowAction = new FxAction(this::actionPopToWindow);
    @Getter
    protected final Label titleField;
    private final ReadOnlyBooleanWrapper tabMode = new ReadOnlyBooleanWrapper();
    private final SimpleStringProperty title = new SimpleStringProperty();

    @Getter
    protected String type;

    public FxDockPane(String type) {
        this.type = type;

        FxDockStyles.FX_DOCK_PANE.set(this);

        titleField = new Label();
        FxDockStyles.TOOLBAR_TITLE.set(titleField);
        titleField.textProperty().bindBidirectional(titleProperty());

        DragAndDropHandler.attach(titleField, this);

        parent.addListener((s, old, cur) -> setTabMode(cur instanceof FxDockTabPane));
    }

    public final String getDockPaneType() {
        return type;
    }

    public final boolean isTabMode() {
        return tabModeProperty().get();
    }

    protected final void setTabMode(boolean on) {
        tabMode.set(on);

        Node tb = createToolBar(on);
        setTop(tb);
    }

    public final boolean isPaneMode() {
        return !tabModeProperty().get();
    }


    public final ReadOnlyBooleanProperty tabModeProperty() {
        return tabMode.getReadOnlyProperty();
    }


    public HBox getCustomTab() {
        HBox h = new HBox(titleField);
        h.setAlignment(Pos.CENTER_LEFT);
        h.setSpacing(5);
        return h;
    }

    /**
     * override to create your own toolbar.css, possibly with custom icons and buttons
     */
    protected Node createToolBar(boolean tabMode) {
        if (tabMode) {
            return null;
        } else {
            Button b = new Button("✕");
            b.prefHeightProperty().bind(b.widthProperty());
            b.minHeightProperty().bind(b.widthProperty());
            b.maxHeightProperty().bind(b.widthProperty());
            b.setStyle("-fx-background-color: -fx-background !important; -fx-padding : 0px 3px");
            FxDockStyles.TOOLBAR_CLOSE_BUTTON.set(b);
            closeAction.attach(b);

            ToolBar t = new ToolBar();
            FxDockStyles.TOOLBAR.set(t);
            t.getItems().addAll(titleField, b);
            return t;
        }
    }

    public final String getTitle() {
        return titleProperty().get();
    }

    public final void setTitle(String s) {
        titleProperty().set(s);
    }

    public final SimpleStringProperty titleProperty() {
        return title;
    }


    /**
     * while FxDockPane extends BorderPane, it's better to insert a custom content using this method only, because the
     * top is used by the toolbar.css.
     */
    public void setContent(Node n) {
        setCenter(n);
    }


    public void actionClose() {
        DockTools.remove(this);
    }


    public void actionPopToWindow() {
        DockTools.moveToNewWindow(this);
    }
}
