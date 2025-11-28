package com.enosistudio.docktailor.fx.fxdock.internal;

import org.jetbrains.annotations.NotNull;

/**
 * Drop zone position in the screen coordinates.
 */
public record WhereScreen(double screenx, double screeny) {
    @Override
    public boolean equals(Object x) {
        if (x == this) {
            return true;
        } else if (x instanceof WhereScreen(double screenx1, double screeny1)) {
            return (screenx == screenx1) && (screeny == screeny1);
        } else {
            return false;
        }
    }

    @Override
    public @NotNull String toString() {
        return "(" + screenx + "," + screeny + ")";
    }
}
