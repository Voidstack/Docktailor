package com.enosi.docktailor.fx;

import javafx.scene.Node;
import javafx.scene.control.*;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Convenient MenuBar.
 */
@NoArgsConstructor
public class FxMenuBar extends MenuBar {
    public void add(Menu m) {
        getMenus().add(m);
    }

    public Menu menu(String text) {
        Menu m = new Menu(text);
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
        SeparatorMenuItem m = new SeparatorMenuItem();
        lastMenu().getItems().add(m);
    }

    public Menu lastMenu() {
        List<Menu> ms = getMenus();
        return ms.get(ms.size() - 1);
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

    public CustomMenuItem item(CustomMenuItem m) {
        lastMenu().getItems().add(m);
        return m;
    }

    public void add(MenuItem m) {
        lastMenu().getItems().add(m);
    }
}