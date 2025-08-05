// Copyright © 2013-2025 Andy Goryachev <andy@goryachev.com>
package com.enosi.docktailor.common.util;

import lombok.Getter;

@Getter
public class Progress {
    public static final Progress UNKNOWN = new Progress(0.0);
    public static final Progress DONE = new Progress(1.0);

    private final double progress;

    public Progress(double progress) {
        this.progress = progress;
    }

    public Progress(int count, int total) {
        this.progress = (total == 0 ? 0.0 : count / (double) total);
    }

    public Progress(long count, long total) {
        this.progress = (total == 0 ? 0.0 : count / (double) total);
    }

    @Override
    public boolean equals(Object x) {
        if (x == this) {
            return true;
        } else if (x instanceof Progress) {
            return progress == ((Progress) x).progress;
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        int h = FH.hash(0, Progress.class);
        return FH.hash(h, progress);
    }

    @Override
    public String toString() {
        return "Progress:" + progress;
    }
}
