package com.enosi.docktailor.docktailor.fx;

import com.enosi.docktailor.common.util.CSorter;
import javafx.beans.property.Property;
import javafx.scene.Node;
import javafx.scene.control.*;

import java.util.List;


/**
 * Convenient MenuBar.
 */
public class FxMenuBar extends MenuBar {
    public FxMenuBar() {

    }


    public void add(FxMenu m) {
        getMenus().add(m);
    }


    public FxMenu menu(String text) {
        FxMenu m = new FxMenu(text);
        getMenus().add(m);
        return m;
    }

    public void add(Node n) {
        Menu m = new Menu();
//		m.setDisable(true);
        m.setGraphic(n);
        getMenus().add(m);
    }


    public void separator() {
        lastMenu().separator();
    }


    public FxMenu lastMenu() {
        List<Menu> ms = getMenus();
        return (FxMenu) ms.get(ms.size() - 1);
    }


    public FxMenuItem item(String name) {
        FxMenuItem m = new FxMenuItem(name);
        m.setDisable(true);
        add(m);
        return m;
    }


    public FxMenuItem item(String name, FxAction a) {
        FxMenuItem m = new FxMenuItem(name, a);
        add(m);
        return m;
    }

    public FxMenuItem item(String name, Runnable r) {
        FxMenuItem m = new FxMenuItem(name, r);
        add(m);
        return m;
    }

    public FxMenu item(FxMenu m) {
        lastMenu().add(m);
        return m;
    }

    public CustomMenuItem item(CustomMenuItem m) {
        lastMenu().add(m);
        return m;
    }


    public FxCheckMenuItem item(String text, Property<Boolean> prop) {
        return lastMenu().item(text, prop);
    }


    public void add(MenuItem m) {
        lastMenu().add(m);
    }


    /**
     * collates the last menu
     */
    public void collate() {
        CSorter.collate(lastMenu().getItems(), MenuItem::getText);
    }
}