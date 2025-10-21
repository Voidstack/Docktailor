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
 * FxDockSplitPane - Un conteneur divisé pour gérer les panneaux dockables.
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
     * Supprime les panneaux vides qui sont trop petits après un redimensionnement.
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
     * Récupère un panneau à l'index spécifié.
     */
    public Node getPane(int index) {
        if (index < 0) {
            return null;
        }

        ObservableList<Node> panes = getItems();
        return index < panes.size() ? panes.get(index) : null;
    }

    /**
     * Ajoute un panneau à la fin du conteneur. Si le panneau est un FxDockPane, il est automatiquement encapsulé dans
     * un FxDockTabPane.
     */
    public void addPane(Node pane) {
        addPane(this.getPaneCount(), pane);
    }

    /**
     * Insère un panneau à l'index spécifié.
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
                // Aucune action à effectuer
            }
        }
    }

    /**
     * Ajoute un panneau avec une animation du divider pour simuler un glissement depuis la gauche/début. Le panneau
     * commence à une taille quasi nulle et s'expand progressivement.
     *
     * @param pane Le panneau à ajouter
     */
    public void addPaneWithSlideFromStart(Node pane) {
        addPaneWithSlideAnimation(pane, true);
    }

    /**
     * Ajoute un panneau avec une animation du divider pour simuler un glissement depuis la droite/fin. Le panneau
     * existant se réduit progressivement pour faire place au nouveau.
     *
     * @param pane Le panneau à ajouter
     */
    public void addPaneWithSlideFromEnd(Node pane) {
        addPaneWithSlideAnimation(pane, false);
    }

    /**
     * Logique commune pour l'animation d'ajout de panneau.
     *
     * @param pane      Le panneau à ajouter
     * @param fromStart true pour ajouter au début, false pour ajouter à la fin
     */
    private void addPaneWithSlideAnimation(Node pane, boolean fromStart) {
        Node preparedPane = wrapIfNeeded(pane);
        preparedPane = DockTools.prepareToAdd(preparedPane);

        int paneCount = getPaneCount();

        if (fromStart) {
            // Ajoute au début
            getItems().add(0, preparedPane);
            DockTools.setParent(this, preparedPane);

            if (paneCount > 0) {
                // Anime le premier divider de 0.0 (panneau invisible) à 0.3 (30% de l'espace)
                animateDivider(0, 0.1, 0.3);
            }
        } else {
            // Ajoute à la fin
            getItems().add(preparedPane);
            DockTools.setParent(this, preparedPane);

            if (paneCount > 0) {
                // Anime le dernier divider de 1.0 (panneau invisible) à 0.7 (30% de l'espace pour le nouveau)
                int dividerIndex = paneCount - 1;
                animateDivider(dividerIndex, 0.9, 0.7);
            }
        }
    }

    /**
     * Anime la position d'un divider spécifique.
     *
     * @param dividerIndex L'index du divider à animer
     * @param fromPosition Position initiale (0.0 à 1.0)
     * @param toPosition   Position finale (0.0 à 1.0)
     */
    private void animateDivider(int dividerIndex, double fromPosition, double toPosition) {
        // Applique la position initiale immédiatement
        setDividerPosition(dividerIndex, fromPosition);

        // Crée l'animation vers la position finale
        Timeline timeline = new Timeline(new KeyFrame(Duration.ZERO, new KeyValue(getDividers().get(dividerIndex).positionProperty(), fromPosition)), new KeyFrame(Duration.millis(300), new KeyValue(getDividers().get(dividerIndex).positionProperty(), toPosition)));

        timeline.play();
    }

/**
 * ============
 */


    /**
     * Encapsule un FxDockPane dans un FxDockTabPane si nécessaire.
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
     * Remplace un panneau à l'index spécifié.
     */
    public void setPane(int index, Node pane) {
        removePane(index);
        addPane(index, pane);
    }

    /**
     * Supprime et retourne le panneau à l'index spécifié.
     */
    public Node removePane(int index) {
        Node removedPane = getItems().remove(index);
        DockTools.setParent(null, removedPane);
        return removedPane;
    }

    /**
     * Supprime un panneau spécifique.
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