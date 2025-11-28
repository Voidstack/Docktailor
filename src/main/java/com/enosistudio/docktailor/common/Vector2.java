package com.enosistudio.docktailor.common;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@AllArgsConstructor
@Setter
@SuppressWarnings("unused")
public class Vector2 {
    public static final Vector2 ZERO = new Vector2(0, 0);
    public static final Vector2 ONE = new Vector2(1, 1);

    private double x;
    private double y;

    public void set(double x, double y) {
        this.x = x;
        this.y = y;
    }

    /**
     * Permet de trouver la distance entre deux Vector2D
     *
     * @param arg : Vector2D
     * @return : double
     */
    public double calculDistance(Vector2 arg) {
        double deltaX = this.getX() - arg.getX();
        double deltaY = this.getY() - arg.getY();
        return Math.sqrt(deltaX * deltaX + deltaY * deltaY);
    }

    /**
     * Permet de trouver le Vector2D au milieur de deux Vector2D
     *
     * @param arg : Vector2D
     * @return : Vector2D
     */
    public Vector2 calculMilieu(Vector2 arg) {
        double midX = (this.getX() + arg.getX()) / 2.0;
        double midY = (this.getY() + arg.getY()) / 2.0;
        return new Vector2(midX, midY);
    }

    @Override
    public String toString() {
        return "x: " + x + " --- y: " + y;
    }
}
