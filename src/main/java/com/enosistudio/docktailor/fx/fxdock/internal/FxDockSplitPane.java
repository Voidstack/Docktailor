package com.enosistudio.docktailor.fx.fxdock.internal;

import com.enosistudio.docktailor.fx.fxdock.FxDockPane;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.ObservableList;
import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.control.SplitPane;
import javafx.scene.input.MouseEvent;
import javafx.util.Duration;

/**
 * FxDockSplitPane - A split container for managing dockable panels.
 */
public class FxDockSplitPane extends SplitPane {
    protected final ReadOnlyObjectWrapper<Node> parentNode = new ReadOnlyObjectWrapper<>();

    public FxDockSplitPane() {
        initializeEventHandlers();
    }

    public FxDockSplitPane(EWhere orientation, Node firstPane, Node secondPane) {
        this();
        if (orientation == EWhere.BOTTOM || orientation == EWhere.TOP) setOrientation(Orientation.VERTICAL);
        else setOrientation(Orientation.HORIZONTAL);

        addPane(firstPane);

        addPaneWithSlide(orientation, secondPane);
    }

    private void initializeEventHandlers() {
        addEventFilter(MouseEvent.MOUSE_RELEASED, event -> collapseEmptyPanes());
    }

    /**
     * Removes empty panes that are too small after resizing.
     */
    protected void collapseEmptyPanes() {
        Orientation orientation = getOrientation();

        for (int index = getPaneCount() - 1; index >= 0; index--) {
            Node pane = getPane(index);

            if (pane instanceof FxDockEmptyPane emptyPane) {
                double paneSize = orientation == Orientation.HORIZONTAL ? emptyPane.getWidth() : emptyPane.getHeight();

                if (paneSize < DragAndDropHandler.SPLIT_COLLAPSE_THRESHOLD) {
                    removePane(index);
                }
            }
        }
    }

    /**
     * Gets a pane at the specified index.
     */
    public Node getPane(int index) {
        if (index < 0) {
            return null;
        }

        ObservableList<Node> panes = getItems();
        return index < panes.size() ? panes.get(index) : null;
    }

    /**
     * Adds a pane to the end of the container. If the pane is a FxDockPane, it is automatically wrapped in
     * a FxDockTabPane.
     */
    public void addPane(Node pane) {
        addPane(this.getPaneCount(), pane);
    }

    /**
     * Inserts a pane at the specified index.
     */
    public void addPane(int index, Node pane) {
        Node preparedPane = wrapIfNeeded(pane);
        preparedPane = DockTools.prepareToAdd(preparedPane);

        getItems().add(index, preparedPane);
        DockTools.setParent(this, preparedPane);
    }

    public boolean addPane(EWhere where, Node pane) {
        if (this.getOrientation() == Orientation.HORIZONTAL) {
            switch (where) {
                case LEFT:
                    this.addPane(0, pane);
                    return true;
                case RIGHT:
                    this.addPane(pane);
                    return true;
                default:
                    break;
            }
        } else {
            switch (where) {
                case TOP:
                    this.addPane(0, pane);
                    return true;
                case BOTTOM:
                    this.addPane(pane);
                    return true;
                default:
                    break;
            }
        }
        return false;
    }

    public void addPaneWithSlide(EWhere where, Node pane) {
        switch (where) {
            case TOP, LEFT -> addPaneWithSlideFromStart(pane);
            case RIGHT, BOTTOM -> addPaneWithSlideFromEnd(pane);
            case CENTER -> {
                // No action required
            }
        }
    }

    /**
     * Adds a pane with a divider animation to simulate sliding from the left/start. The pane
     * starts at almost zero size and expands progressively.
     *
     * @param pane The pane to add
     */
    public void addPaneWithSlideFromStart(Node pane) {
        addPaneWithSlideAnimation(pane, true);
    }

    /**
     * Adds a pane with a divider animation to simulate sliding from the right/end. The existing
     * pane shrinks progressively to make room for the new one.
     *
     * @param pane The pane to add
     */
    public void addPaneWithSlideFromEnd(Node pane) {
        addPaneWithSlideAnimation(pane, false);
    }

    /**
     * Common logic for pane addition animation.
     *
     * @param pane      The pane to add
     * @param fromStart true to add at the start, false to add at the end
     */
    private void addPaneWithSlideAnimation(Node pane, boolean fromStart) {
        Node preparedPane = wrapIfNeeded(pane);
        preparedPane = DockTools.prepareToAdd(preparedPane);

        int paneCount = getPaneCount();

        if (fromStart) {
            // Add at the start
            getItems().add(0, preparedPane);
            DockTools.setParent(this, preparedPane);

            if (paneCount > 0) {
                // Animate the first divider from 0.0 (invisible pane) to 0.3 (30% of space)
                animateDivider(0, 0.1, 0.3);
            }
        } else {
            // Add at the end
            getItems().add(preparedPane);
            DockTools.setParent(this, preparedPane);

            if (paneCount > 0) {
                // Animate the last divider from 1.0 (invisible pane) to 0.7 (30% of space for the new one)
                int dividerIndex = paneCount - 1;
                animateDivider(dividerIndex, 0.9, 0.7);
            }
        }
    }

    /**
     * Animates the position of a specific divider.
     *
     * @param dividerIndex The index of the divider to animate
     * @param fromPosition Initial position (0.0 to 1.0)
     * @param toPosition   Final position (0.0 to 1.0)
     */
    private void animateDivider(int dividerIndex, double fromPosition, double toPosition) {
        // Apply the initial position immediately
        setDividerPosition(dividerIndex, fromPosition);

        // Create the animation to the final position
        Timeline timeline = new Timeline(new KeyFrame(Duration.ZERO, new KeyValue(getDividers().get(dividerIndex).positionProperty(), fromPosition)), new KeyFrame(Duration.millis(300), new KeyValue(getDividers().get(dividerIndex).positionProperty(), toPosition)));

        timeline.play();
    }

/**
 * ============
 */


    /**
     * Wraps a FxDockPane in a FxDockTabPane if necessary.
     */
    private Node wrapIfNeeded(Node pane) {
        if (pane instanceof FxDockPane) {
            FxDockTabPane tabPane = new FxDockTabPane();
            tabPane.addTab(pane);
            return tabPane;
        }
        return pane;
    }

    /**
     * Replaces a pane at the specified index.
     */
    public void setPane(int index, Node pane) {
        removePane(index);
        addPane(index, pane);
    }

    /**
     * Removes and returns the pane at the specified index.
     */
    public Node removePane(int index) {
        Node removedPane = getItems().remove(index);
        DockTools.setParent(null, removedPane);
        return removedPane;
    }

    /**
     * Removes a specific pane.
     */
    public void removePane(Node pane) {
        int index = indexOfPane(pane);
        if (index >= 0) {
            removePane(index);
        }
    }

    public int getPaneCount() {
        return getItems().size();
    }

    public ObservableList<Node> getPanes() {
        return getItems();
    }

    public int indexOfPane(Node pane) {
        return getItems().indexOf(pane);
    }
}