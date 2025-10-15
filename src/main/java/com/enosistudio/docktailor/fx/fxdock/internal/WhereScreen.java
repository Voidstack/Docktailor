package com.enosistudio.docktailor.fx.fxdock.internal;

/**
 * Drop zone position in the screen coordinates.
 */
public record WhereScreen(double screenx, double screeny) {
    @Override
    public boolean equals(Object x) {
        if (x == this) {
            return true;
        } else if (x instanceof WhereScreen w) {
            return
                    (screenx == w.screenx) &&
                            (screeny == w.screeny);
        } else {
            return false;
        }
    }

    @Override
    public String toString() {
        return "(" + screenx + "," + screeny + ")";
    }
}
