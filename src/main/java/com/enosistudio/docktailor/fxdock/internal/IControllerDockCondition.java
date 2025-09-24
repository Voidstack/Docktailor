package com.enosistudio.docktailor.fxdock.internal;

/**
 * Définis une condition qui à tester pour savoir si la fenêtre dois exister. Par example pour une fenetre qui doit
 * exister uniquement en debug mode.
 */
public interface IControllerDockCondition {
    /**
     * Condition qui définis l'existance de la fenêtre dans le contexte.
     *
     * @return boolean
     */
    boolean isExistDansLeContext();
}
