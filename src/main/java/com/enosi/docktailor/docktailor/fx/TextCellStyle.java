// Copyright © 2019-2025 Andy Goryachev <andy@goryachev.com>
package com.enosi.docktailor.docktailor.fx;

import com.enosi.docktailor.common.util.CKit;
import com.enosi.docktailor.common.util.FH;
import com.enosi.docktailor.common.util.SB;
import com.enosi.docktailor.docktailor.fx.internal.StandardFxProperties;
import javafx.scene.paint.Color;
import lombok.Getter;

/**
 * (Monospaced) text cell style.
 */
public class TextCellStyle implements Cloneable {
    public static final TextCellStyle NONE = new TextCellStyle();

    @Getter
    private Color textColor;
    @Getter
    private Color backgroundColor;
    @Getter
    private boolean bold;
    @Getter
    private boolean italic;
    @Getter
    private boolean strikeThrough;
    @Getter
    private boolean underscore;
    private String style;

    public TextCellStyle(Color fg, Color bg, boolean bold, boolean italic, boolean strikeThrough, boolean underscore) {
        this.textColor = fg;
        this.backgroundColor = bg;
        this.bold = bold;
        this.italic = italic;
        this.strikeThrough = strikeThrough;
        this.underscore = underscore;
    }

    public TextCellStyle(Color fg) {
        this.textColor = fg;
    }

    public TextCellStyle() {
    }

    public TextCellStyle copy() {
        return (TextCellStyle) clone();
    }

    @Override
    public Object clone() {
        return new TextCellStyle(textColor, backgroundColor, bold, italic, strikeThrough, underscore);
    }

    public boolean isEmpty() {
        return
                !bold &&
                        !italic &&
                        !strikeThrough &&
                        !underscore &&
                        (backgroundColor == null) &&
                        (textColor == null);
    }


    public void init(TextCellStyle x) {
        this.backgroundColor = x.backgroundColor;
        this.textColor = x.textColor;
        this.bold = x.bold;
        this.italic = x.italic;
        this.strikeThrough = x.strikeThrough;
        this.underscore = x.underscore;
    }


    @Override
    public boolean equals(Object x) {
        if (x == this) {
            return true;
        } else if (x instanceof TextCellStyle c) {
            return
                    (bold == c.bold) &&
                            (italic == c.italic) &&
                            (strikeThrough == c.strikeThrough) &&
                            (underscore == c.underscore) &&
                            CKit.equals(textColor, c.textColor) &&
                            CKit.equals(backgroundColor, c.backgroundColor);
        }
        return false;
    }


    @Override
    public int hashCode() {
        int h = FH.hash(TextCellStyle.class);
        h = FH.hash(h, backgroundColor);
        h = FH.hash(h, textColor);
        h = FH.hash(h, bold);
        h = FH.hash(h, italic);
        h = FH.hash(h, strikeThrough);
        h = FH.hash(h, underscore);
        return h;
    }


    public void clear() {
        this.backgroundColor = null;
        this.textColor = null;
        this.bold = false;
        this.italic = false;
        this.strikeThrough = false;
        this.underscore = false;
    }


    public void setBackgroundColor(Color c) {
        backgroundColor = c;
        style = null;
    }


    public void setTextColor(Color c) {
        textColor = c;
        style = null;
    }


    public void setBold(boolean on) {
        bold = on;
        style = null;
    }


    public void setItalic(boolean on) {
        italic = on;
        style = null;
    }


    public void setStrikeThrough(boolean on) {
        strikeThrough = on;
        style = null;
    }


    public void setUnderscore(boolean on) {
        underscore = on;
        style = null;
    }


    public String getStyle() {
        if (style == null) {
            SB sb = new SB(128);

            if (textColor != null) {
                StandardFxProperties.textFill(textColor).write(sb);
            }
//			private Color backgroundColor; TODO

            if (bold) {
                StandardFxProperties.fontWeight(StandardFxProperties.BOLD).write(sb);
            }

            if (italic) {
                StandardFxProperties.fontStyle("italic").write(sb);
            }

//			private boolean strikeThrough; // TODO
//			private boolean underscore;

            style = sb.toString();
        }
        return style;
    }
}