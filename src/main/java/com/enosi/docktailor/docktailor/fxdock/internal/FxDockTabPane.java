package com.enosi.docktailor.docktailor.fxdock.internal;

import com.enosi.docktailor.common.util.CList;
import com.enosi.docktailor.docktailor.fxdock.FxDockPane;
import com.enosi.docktailor.docktailor.fxdock.FxDockStyles;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyProperty;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;

import java.util.List;


/**
 * FxDockTabPane.
 */
public class FxDockTabPane
        extends TabPane {
    protected final ReadOnlyObjectWrapper<Node> parent = new ReadOnlyObjectWrapper<Node>();


    public FxDockTabPane() {
        FxDockStyles.FX_TAB_PANE.set(this);
    }

    public FxDockTabPane(Node n) {
        this();
        addTab(n);
    }


    public final ReadOnlyProperty<Node> dockParentProperty() {
        return parent.getReadOnlyProperty();
    }


    public Node getTab(int ix) {
        if (ix >= 0) {
            ObservableList<Tab> ps = getTabs();
            if (ix < ps.size()) {
                return ps.get(ix).getContent();
            }
        }
        return null;
    }


    public Node getSelectedTab() {
        int ix = getSelectedTabIndex();
        return getTab(ix);
    }


    public int getSelectedTabIndex() {
        return getSelectionModel().getSelectedIndex();
    }


    /**
     * @param nd : instanceof
     * @return
     */
    protected Tab newTab(Node nd) {
        Node n = DockTools.prepareToAdd(nd);

        Tab t = new Tab(null, n);
        if (n instanceof FxDockPane p) {
            // t.setGraphic(p.getTitleField());
            t.setGraphic(p.getCustomTab());

            t.setOnClosed((ev) ->
            {
                Node pp = DockTools.getParent(this);
                DockTools.collapseEmptySpace(pp);
            });
        }
        return t;
    }


    public void addTab(Node n) {
        if (this.getTabs().size() != 1 || !this.getTabs().get(0).getTabPane().getTabs().get(0).getContent().equals(n)) {
            Tab t = newTab(n);
            getTabs().add(t);
            DockTools.setParent(this, n);
        }
        // Pour éviter d'ajouter une fenêtre vide lorsqu'on drag&drop une fenêtre sur elle-même.
        // ca provoque des bugs de placement un peu bizarre mais flemme.
    }


    public void addTab(int ix, Node n) {
        Tab t = newTab(n);
        getTabs().add(ix, t);
        DockTools.setParent(this, n);
    }


    public void removeTab(int ix) {
        Tab t = getTabs().remove(ix);
        Node n = t.getContent();
        DockTools.setParent(null, n);
    }


    public void removeTab(Node n) {
        int ix = indexOfTab(n);
        if (ix >= 0) {
            removeTab(ix);
        }
    }


    public void setTab(int ix, Node n) {
        removeTab(ix);
        addTab(ix, n);
    }


    public int getTabCount() {
        return getTabs().size();
    }


    public List<Node> getPanes() {
        CList<Node> rv = new CList<>(getTabCount());
        for (Tab t : getTabs()) {
            rv.add(t.getContent());
        }
        return rv;
    }


    public int indexOfTab(Node n) {
        ObservableList<Tab> ts = getTabs();
        for (int i = ts.size() - 1; i >= 0; --i) {
            Tab t = ts.get(i);
            if (t.getContent() == n) {
                return i;
            }
        }
        return -1;
    }


    public void select(Node n) {
        int ix = indexOfTab(n);
        if (ix >= 0) {
            getSelectionModel().select(ix);
        }
    }


    public void select(int ix) {
        if ((ix >= 0) && (ix < getTabCount())) {
            getSelectionModel().select(ix);
        }
    }
}
