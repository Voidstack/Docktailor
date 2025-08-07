package com.enosi.docktailor.dock;

import com.enosi.docktailor.common.util.Hex;
import com.enosi.docktailor.docktailor.fx.FX;
import com.enosi.docktailor.docktailor.fx.FxPopupMenu;
import com.enosi.docktailor.docktailor.fx.HPane;
import com.enosi.docktailor.docktailor.fxdock.FxDockPane;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;


/**
 * An example of an FxDockPane.
 */
public class DemoPane
        extends FxDockPane {
    /**
     * sequence number for debugging
     */
    private static int seq = 1;
    private int pseq;


    public DemoPane(String type) {
        super(type);

        CheckBox cb = new CheckBox("boolean property");
        FX.setName(cb, "CHECKBOX");

        TextField textField = new TextField();
        FX.setName(textField, "TEXTFIELD");

//		VPane vb = new VPane();
//		a(vb, 2, 0.25, 0.25, HPane.FILL);
//		a(vb, 2, 30, 30, 100);
//		a(vb, 2, 0.2, 0.2, 0.6);
//		a(vb, 2, HPane.PREF, HPane.FILL, HPane.PREF);
//		a(vb, 2, HPane.FILL, HPane.FILL, HPane.FILL);
//		a(vb, 2, 20, HPane.FILL, 20);
//		a(vb, 2, HPane.FILL, HPane.FILL, HPane.FILL, HPane.FILL, HPane.FILL, HPane.FILL, HPane.FILL, HPane.FILL, HPane.FILL, HPane.FILL);
//		a(vb, 2, 50, HPane.FILL, HPane.FILL, 50);

        HPane vb = new HPane(2);
        vb.add(cb);
        vb.add(textField);

        BorderPane bp = new BorderPane();
        bp.setCenter(createColorNode(type));
        bp.setBottom(vb);

        setCenter(bp);
        this.pseq = seq++;
        setTitle("pane " + pseq);

        // set up context menu off the title field
        FX.setPopupMenu(titleField, this::createTitleFieldPopupMenu);
    }

    protected FxPopupMenu createTitleFieldPopupMenu() {
        FxPopupMenu m = new FxPopupMenu();
        m.item("Pop up in Window", popToWindowAction);
        m.item("Close", closeAction);
        return m;
    }

    private Node createColorNode(String c) {
        int rgb = Hex.parseInt(c, 0);
        Region r = new Region();
        r.setBackground(FX.background(FX.rgb(rgb)));
        return r;
    }
}
