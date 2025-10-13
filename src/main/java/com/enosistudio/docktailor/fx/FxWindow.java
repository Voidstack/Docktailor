package com.enosistudio.docktailor.fx;

import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import lombok.extern.slf4j.Slf4j;


/**
 * Convenient FX Stage.
 */
@Slf4j(topic = "FxWindow")
public class FxWindow extends Stage {
    private final BorderPane borderPane;

    public FxWindow(String name) {
        this.borderPane = new BorderPane();
        FX.setName(this, name);

        Scene sc = new Scene(borderPane);

        // FxTooltipDebugCss.install(sc);

        setScene(sc);
    }

    protected BorderPane getBorderPane() {
        return borderPane;
    }

    public void open() {
        show();
    }

    public Node getTop() {
        return borderPane.getTop();
    }

    public void setTop(Node n) {
        borderPane.setTop(n);
    }

    public Node getBottom() {
        return borderPane.getBottom();
    }

    public void setBottom(Node n) {
        borderPane.setBottom(n);
    }

    public Node getLeft() {
        return borderPane.getLeft();
    }

    public void setLeft(Node n) {
        borderPane.setLeft(n);
    }

    public Node getRight() {
        return borderPane.getRight();
    }

    public void setRight(Node n) {
        borderPane.setRight(n);
    }

    public Node getCenter() {
        return borderPane.getCenter();
    }

    public void setCenter(Node n) {
        borderPane.setCenter(n);
    }

    public void setSize(double width, double height) {
        setWidth(width);
        setHeight(height);
    }


    public void setMinSize(double width, double height) {
        setMinWidth(width);
        setMinHeight(height);
    }


    public void setMaxSize(double width, double height) {
        setMaxWidth(width);
        setMaxHeight(height);
    }


    public void setClosingWindowOperation(ClosingWindowOperation op) {
        WindowMonitor.setClosingWindowOperation(this, op);
    }


    public void setNonEssentialWindow() {
        WindowMonitor.setNonEssentialWindow(this);
    }


    @Override
    public String toString() {
        return "FxWindow{" + FX.getName(this) + "." + hashCode() + "}";
    }
}
