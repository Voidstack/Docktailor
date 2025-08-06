package com.enosi.docktailor.docktailor.fx.internal;

import com.enosi.docktailor.common.util.SB;

/**
 * Fx Css Property.
 */
public class FxCssProp {
    protected final String name;
    protected final Object value;


    public FxCssProp(String name, Object value) {
        this.name = name;
        this.value = value;
    }


    public void write(SB sb) {
        sb.a(name);
        sb.a(": ");
        sb.a(CssTools.toValue(value));
        sb.a("; ");
    }

    @Override
    public String toString() {
        SB sb = new SB();
        write(sb);
        return sb.toString();
    }
}
