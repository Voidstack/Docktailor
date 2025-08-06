// Copyright © 2024-2025 Andy Goryachev <andy@goryachev.com>
package com.enosi.docktailor.docktailor.fx.input;

import com.enosi.docktailor.common.util.CPlatform;
import javafx.scene.control.Control;

import java.util.function.BooleanSupplier;


/**
 * Behavior Base.
 */
public abstract class BehaviorBase<C extends Control> {
    private final C control;
    private SkinInputMap skinInputMap;
    public BehaviorBase(C c) {
        this.control = c;
    }

    /**
     * Implementation should register the skin's function ids, key bindings, and event handlers inside of this method.
     */
    protected abstract void populateSkinInputMap();

    public C control() {
        return control;
    }


    public SkinInputMap getSkinInputMap() {
        if (skinInputMap == null) {
            skinInputMap = new SkinInputMap();
            populateSkinInputMap();
        }
        return skinInputMap;
    }


    public void func(Func f, Runnable r) {
        getSkinInputMap().func(f, r);
    }


    public void func(Func f, BooleanSupplier r) {
        getSkinInputMap().func(f, r);
    }


    public void key(KB k, Func f) {
        getSkinInputMap().key(k, f);
    }


    public boolean isMac() {
        return CPlatform.isMac();
    }


    public boolean isLinux() {
        return CPlatform.isLinux();
    }


    public boolean isWindows() {
        return CPlatform.isWindows();
    }
}
