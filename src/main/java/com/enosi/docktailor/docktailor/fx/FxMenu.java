package com.enosi.docktailor.docktailor.fx;

import com.enosi.docktailor.docktailor.dock.IControllerDockPane;
import javafx.beans.property.Property;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;

/**
 * FX Menu.
 */
public class FxMenu extends Menu {
    public FxMenu(String text) {
        super(text);
    }


    public FxMenu(String text, FxAction a) {
        super(text);
        a.attach(this);
    }


    public FxMenu(String text, Runnable r) {
        super(text);
        new FxAction(r).attach(this);
    }


    public SeparatorMenuItem separator() {
        SeparatorMenuItem m = new SeparatorMenuItem();
        getItems().add(m);
        return m;
    }


    public FxMenuItem item(String text, FxAction a) {
        FxMenuItem m = new FxMenuItem(text, a);
        getItems().add(m);
        return m;
    }


    public FxMenuItem item(String text, Runnable r) {
        FxMenuItem m = new FxMenuItem(text, r);
        getItems().add(m);
        return m;
    }

    /**
     * @param iControllerDockPane : IDockablePane<?>
     * @param r:                      OnClick
     * @return : FxMenuItem
     */
    public FxMenuItem item(IControllerDockPane iControllerDockPane, Runnable r) {
        FxMenuItem m = new FxMenuItem(iControllerDockPane.getTabName(), r);
        m.getStyleClass().add("item-gap");

//		SVGRegion svgRegion = new SVGRegion(iDockablePane.getTabIcon(), 12);

//		svgRegion.setPadding(new Insets(0, 0, 0, 50));
//		svgRegion.setPrefWidth(svgRegion.getWidth() + 500);

//		m.setGraphic(svgRegion);
        getItems().add(m);
        return m;
    }


    public FxMenu menu(String text) {
        FxMenu m = new FxMenu(text);
        getItems().add(m);
        return m;
    }


    public FxCheckMenuItem item(String text, Property<Boolean> prop) {
        FxCheckMenuItem m = new FxCheckMenuItem(text, prop);
        getItems().add(m);
        return m;
    }


    /**
     * adds a disabled menu item
     */
    public MenuItem item(String text) {
        FxMenuItem m = new FxMenuItem(text);
        m.setDisable(true);
        return add(m);
    }


    public <M extends MenuItem> M add(M item) {
        getItems().add(item);
        return item;
    }

    /**
     * remove all menu items
     */
    public void clear() {
        getItems().clear();
    }
}