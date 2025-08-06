package com.enosi.docktailor.docktailor.fx;

import java.util.concurrent.atomic.AtomicLong;

/**
 * Css ID
 */
public class CssID {
    private static final AtomicLong seq = new AtomicLong();
    private final String id;


    public CssID(String id) {
        this.id = id + "_" + seq.incrementAndGet();
    }


    public CssID() {
        this("");
    }


    public String getID() {
        return id;
    }


    @Override
    public String toString() {
        return getID();
    }
}
