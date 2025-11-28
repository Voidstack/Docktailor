package com.enosistudio.docktailor.fx.fxdock.internal;

import com.enosistudio.docktailor.fx.fxdock.FxDockPane;
import com.enosistudio.docktailor.utils.HierarchyCleanupUtils;
import com.enosistudio.docktailor.utils.ParentTrackerUtils;
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
import lombok.Getter;

/**
 * FxDockSplitPane - A split container for managing dockable panels.
 */
public class FxDockSplitPane extends SplitPane implements IFxDockPane {
    // Animation constants
    private static final Duration ANIMATION_DURATION = Duration.millis(300);
    private static final double COMPRESSED_START_POSITION = 0.1;
    private static final double COMPRESSED_END_POSITION = 0.9;
    private static final double MIDDLE_OFFSET = 0.05;

    @Getter
    protected final ReadOnlyObjectWrapper<Node> dockParent = new ReadOnlyObjectWrapper<>();

    public FxDockSplitPane() {
        initializeEventHandlers();
    }

    public FxDockSplitPane(EWhere orientation, Node firstPane, Node secondPane) {
        this();
        if (orientation == EWhere.BOTTOM || orientation == EWhere.TOP) setOrientation(Orientation.VERTICAL);
        else setOrientation(Orientation.HORIZONTAL);

        // For TOP/LEFT: firstPane is the dragged client, it should animate
        // For BOTTOM/RIGHT: secondPane is the dragged client, it should animate
        if (orientation == EWhere.TOP || orientation == EWhere.LEFT) {
            addPane(secondPane);
            addPaneWithSlide(orientation, firstPane);
        } else {
            addPane(firstPane);
            addPaneWithSlide(orientation, secondPane);
        }
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
     * Note: This method does not animate. For user-initiated additions, use addPaneWithAnimation().
     */
    public void addPane(Node pane) {
        addPane(this.getPaneCount(), pane);
    }

    /**
     * Adds a pane to the end with animation (for user-initiated actions).
     * This provides better visual feedback when users add panes via menu items.
     */
    public void addPaneWithAnimation(Node pane) {
        addPaneWithSlideFromEnd(pane);
    }

    /**
     * Inserts a pane at the specified index.
     */
    public void addPane(int index, Node pane) {
        Node preparedPane = wrapIfNeeded(pane);
        preparedPane = HierarchyCleanupUtils.prepareToAdd(preparedPane);

        int paneCount = getPaneCount();
        getItems().add(index, preparedPane);
        ParentTrackerUtils.setParent(this, preparedPane);

        // Initialize dividers after adding a new pane
        if (paneCount > 0) {
            // Fix dividers for this split and all parent splits up the hierarchy
            initializeDividersRecursively();
        }
    }

    /**
     * Inserts a pane at the specified index with smooth animation.
     * The new pane slides in and all dividers smoothly redistribute equally.
     */
    public void addPaneWithAnimationAt(int index, Node pane) {
        Node preparedPane = wrapIfNeeded(pane);
        preparedPane = HierarchyCleanupUtils.prepareToAdd(preparedPane);

        int paneCount = getPaneCount();
        getItems().add(index, preparedPane);
        ParentTrackerUtils.setParent(this, preparedPane);

        if (paneCount > 0) {
            // Animate dividers to slide in the new pane and redistribute smoothly
            animateInsertionAtIndex(index, paneCount);
        }
    }

    /**
     * Animates the insertion of a pane at a specific index.
     * Creates a slide-in effect followed by smooth redistribution of all dividers.
     *
     * @param insertIndex the index where the pane was inserted
     * @param oldPaneCount the number of panes before insertion
     */
    private void animateInsertionAtIndex(int insertIndex, int oldPaneCount) {
        int newDividerCount = getDividers().size();

        if (newDividerCount == 0) return;

        // Calculate which divider is affected by the insertion
        // If we insert at index 2 in a list of 3 items [0,1,2], we affect divider index 1 (between old items 1 and 2)
        int affectedDividerIndex = Math.min(insertIndex, newDividerCount - 1);

        // Set initial position for the affected divider (compressed state)
        // The new pane starts very small (10% space)
        double initialPosition;
        if (insertIndex == 0) {
            // Inserted at start - compress to near 0
            initialPosition = COMPRESSED_START_POSITION;
        } else if (insertIndex >= oldPaneCount) {
            // Inserted at end - compress to near 1
            initialPosition = COMPRESSED_END_POSITION;
        } else {
            // Inserted in middle - start at the position of the divider before it
            if (affectedDividerIndex > 0) {
                initialPosition = getDividers().get(affectedDividerIndex - 1).getPosition() + MIDDLE_OFFSET;
            } else {
                initialPosition = COMPRESSED_START_POSITION;
            }
        }

        setDividerPosition(affectedDividerIndex, initialPosition);

        // Create timeline to animate to equal distribution
        Timeline timeline = new Timeline();

        // Calculate final equal positions for ALL dividers
        double[] finalPositions = calculateEqualPositions(newDividerCount);

        // Add keyframes for all dividers to animate to their final positions
        for (int i = 0; i < newDividerCount; i++) {
            KeyValue keyValue = new KeyValue(
                getDividers().get(i).positionProperty(),
                finalPositions[i]
            );
            timeline.getKeyFrames().add(new KeyFrame(ANIMATION_DURATION, keyValue));
        }

        // After animation completes, also animate parent splits
        timeline.setOnFinished(event -> ParentTrackerUtils.applyToParentSplits(this, FxDockSplitPane::animateDividersEqually));

        timeline.play();
    }

    /**
     * Calculates equal positions for dividers to distribute space evenly.
     *
     * @param dividerCount the number of dividers
     * @return array of positions (0.0 to 1.0) for equal distribution
     */
    private static double[] calculateEqualPositions(int dividerCount) {
        double[] positions = new double[dividerCount];
        for (int i = 0; i < dividerCount; i++) {
            positions[i] = (i + 1.0) / (dividerCount + 1.0);
        }
        return positions;
    }

    /**
     * Initializes divider positions to distribute space equally among all panes.
     */
    private void initializeDividersEqually() {
        int dividerCount = getDividers().size();
        if (dividerCount > 0) {
            setDividerPositions(calculateEqualPositions(dividerCount));
        }
    }

    /**
     * Recursively initializes dividers for this split pane and all parent FxDockSplitPane ancestors.
     * This handles nested split panes where parent dividers might also be at invalid positions.
     */
    private void initializeDividersRecursively() {
        // First, fix this split pane's dividers
        initializeDividersEqually();

        // Then walk up the parent chain and fix any parent splits
        ParentTrackerUtils.applyToParentSplits(this, FxDockSplitPane::initializeDividersEqually);
    }

    public boolean addPane(EWhere where, Node pane) {
        if (this.getOrientation() == Orientation.HORIZONTAL) {
            switch (where) {
                case LEFT:
                    addPaneWithSlideFromStart(pane);
                    return true;
                case RIGHT:
                    addPaneWithSlideFromEnd(pane);
                    return true;
                default:
                    break;
            }
        } else {
            switch (where) {
                case TOP:
                    addPaneWithSlideFromStart(pane);
                    return true;
                case BOTTOM:
                    addPaneWithSlideFromEnd(pane);
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
        preparedPane = HierarchyCleanupUtils.prepareToAdd(preparedPane);

        int paneCount = getPaneCount();

        if (fromStart) {
            // Add at the start
            getItems().add(0, preparedPane);
            ParentTrackerUtils.setParent(this, preparedPane);

            if (paneCount > 0) {
                // Animate the first divider from small initial space to equal distribution
                double targetPosition = 1.0 / (paneCount + 1.0);
                animateDividerWithRedistribution(0, COMPRESSED_START_POSITION, targetPosition);
            }
        } else {
            // Add at the end
            getItems().add(preparedPane);
            ParentTrackerUtils.setParent(this, preparedPane);

            if (paneCount > 0) {
                // Animate the last divider from small initial space to equal distribution
                int dividerIndex = paneCount - 1;
                double targetPosition = paneCount / (paneCount + 1.0);
                animateDividerWithRedistribution(dividerIndex, COMPRESSED_END_POSITION, targetPosition);
            }
        }
    }

    /**
     * Animates a divider and then redistributes ALL dividers equally after the animation completes.
     * This ensures that when adding new panes, all existing panes get resized proportionally
     * instead of just the last one being affected.
     *
     * @param dividerIndex The index of the divider to animate
     * @param fromPosition Initial position (0.0 to 1.0)
     * @param toPosition   Intermediate position for the slide animation
     */
    private void animateDividerWithRedistribution(int dividerIndex, double fromPosition, double toPosition) {
        // Apply the initial position immediately
        setDividerPosition(dividerIndex, fromPosition);

        // Create the slide animation
        Timeline timeline = new Timeline(
            new KeyFrame(Duration.ZERO, new KeyValue(getDividers().get(dividerIndex).positionProperty(), fromPosition)),
            new KeyFrame(ANIMATION_DURATION, new KeyValue(getDividers().get(dividerIndex).positionProperty(), toPosition))
        );

        // After slide animation completes, smoothly redistribute ALL dividers equally (including parent splits)
        timeline.setOnFinished(event -> animateDividersRecursively());

        timeline.play();
    }

    /**
     * Animates divider positions to distribute space equally among all panes.
     * This creates a smooth visual transition instead of an instant snap.
     */
    private void animateDividersEqually() {
        int dividerCount = getDividers().size();
        if (dividerCount > 0) {
            // Calculate target equal positions
            double[] targetPositions = calculateEqualPositions(dividerCount);

            // Create timeline with all divider animations
            Timeline timeline = new Timeline();
            for (int i = 0; i < dividerCount; i++) {
                KeyValue keyValue = new KeyValue(
                    getDividers().get(i).positionProperty(),
                    targetPositions[i]
                );
                timeline.getKeyFrames().add(new KeyFrame(ANIMATION_DURATION, keyValue));
            }

            timeline.play();
        }
    }

    /**
     * Recursively animates dividers for this split pane and all parent FxDockSplitPane ancestors.
     * This creates a smooth cascading redistribution effect throughout the entire split hierarchy.
     */
    private void animateDividersRecursively() {
        // First, animate this split pane's dividers
        animateDividersEqually();

        // Then walk up the parent chain and animate parent splits
        ParentTrackerUtils.applyToParentSplits(this, FxDockSplitPane::animateDividersEqually);
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
    public void removePane(int index) {
        Node removedPane = getItems().remove(index);
        ParentTrackerUtils.setParent(null, removedPane);
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