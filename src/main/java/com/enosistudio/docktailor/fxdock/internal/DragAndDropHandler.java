package com.enosistudio.docktailor.fxdock.internal;

import com.enosistudio.docktailor.fx.FX;
import com.enosistudio.docktailor.fxdock.FxDockPane;
import com.enosistudio.docktailor.fxdock.FxDockWindow;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.SnapshotParameters;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;

import java.util.List;

/**
 * Dock Drag and Drop handler.
 */
public class DragAndDropHandler {
    public static final double DRAG_WINDOW_OPACITY = 0.5;
    public static final double SPLIT_COLLAPSE_THRESHOLD = 6;
    public static final double PANE_EDGE_HORIZONTAL = 0.3;
    public static final double PANE_EDGE_VERTICAL = 0.3;
    public static final double WINDOW_EDGE_HORIZONTAL = 30;
    public static final double WINDOW_EDGE_VERTICAL = 30;
    protected static double deltax;
    protected static double deltay;
    protected static Stage dragWindow;
    protected static ADockDropOperation ADockDropOperation;

    private DragAndDropHandler() {
    }

    public static void attach(Node n, FxDockPane client) {
        n.addEventHandler(MouseEvent.DRAG_DETECTED, (ev) -> onDragDetected(ev, client));
        n.addEventHandler(MouseEvent.MOUSE_DRAGGED, (ev) -> onMouseDragged(ev, client));
        n.addEventHandler(MouseEvent.MOUSE_RELEASED, (ev) -> onMouseReleased());
    }

    protected static void onDragDetected(MouseEvent ev, FxDockPane client) {
        if (dragWindow == null) {
            double x = ev.getScreenX();
            double y = ev.getScreenY();

            Point2D p = client.screenToLocal(x, y);
            deltax = p.getX();
            deltay = p.getY(); // gets modified in createDragWindow()

            dragWindow = createDragWindow(client);
            dragWindow.addEventHandler(KeyEvent.KEY_PRESSED, (ke) -> cancelDrag());

            dragWindow.setX(x - deltax);
            dragWindow.setY(y - deltay);

            dragWindow.show();

            ev.consume();
        }
    }

    protected static void cancelDrag() {
        // any key cancels the drag operation
        stopDrag();
        ADockDropOperation = null;
    }

    protected static void onMouseDragged(MouseEvent ev, FxDockPane client) {
        if (dragWindow != null) {
            double x = ev.getScreenX();
            double y = ev.getScreenY();

            dragWindow.setX(x - deltax);
            dragWindow.setY(y - deltay);

            ADockDropOperation op = createDropOp(client, x, y);
            setDropOp(op);
        }
    }

    protected static void setDropOp(ADockDropOperation op) {
        if (ADockDropOperation != null) {
            if (ADockDropOperation.isSame(op)) {
                return;
            } else {
                ADockDropOperation.removeHighlights();
            }
        }

        ADockDropOperation = op;

        if (op != null) {
            op.installHighlights();
        }
    }

    protected static void onMouseReleased() {
        // remove drag window
        stopDrag();

        if (ADockDropOperation != null) {
            ADockDropOperation.execute();
            ADockDropOperation = null;
        }
    }

    protected static void stopDrag() {
        // delete the drag window
        if (dragWindow != null) {
            dragWindow.hide();
            dragWindow = null;
        }

        // remove the highlights but not the op
        if (ADockDropOperation != null) {
            ADockDropOperation.removeHighlights();
        }
    }

    protected static Stage createDragWindow(FxDockPane client) {
        Window owner = FX.getParentWindow(client);
        Image im = createDragImage(client);
        Pane p = new Pane(new ImageView(im));
        Stage s = new Stage(StageStyle.TRANSPARENT);
        s.initOwner(owner);
        Scene sc = new Scene(p, im.getWidth(), im.getHeight());
        s.setScene(sc);

        // FxTooltipDebugCss.install(sc);

        s.setOpacity(DRAG_WINDOW_OPACITY);
        return s;
    }


    protected static Image createDragImage(FxDockPane client) {
        SnapshotParameters sp = new SnapshotParameters();
        sp.setFill(Color.TRANSPARENT);

        WritableImage im = client.snapshot(sp, null);

        if (client.isPaneMode()) {
            return im;
        }

        // include selected tab
        FxDockTabPane tp = (FxDockTabPane) DockTools.getParent(client);
        Node n = tp.lookup(".tab:selected");
        WritableImage tim = n.snapshot(sp, null);

        double dy = tim.getHeight();

        // must adjust for the tab
        deltay += dy;

        double w = Math.max(im.getWidth(), tim.getWidth());
        double h = im.getHeight() + dy;
        Canvas c = new Canvas(w, h);
        GraphicsContext g = c.getGraphicsContext2D();
        g.drawImage(tim, 0, 0);
        g.drawImage(im, 0, dy);
        return c.snapshot(sp, null);
    }

    protected static ADockDropOperation checkWindowEdge(FxDockPane client, FxDockRootPane root, double screenx, double screeny) {
        // TODO allow to create a split
        if (root.getContent() == client) {
            // don't drop on itself
            return null;
        }

        double w = root.getWidth();
        if (w <= WINDOW_EDGE_HORIZONTAL + WINDOW_EDGE_HORIZONTAL) {
            // too narrow
            return null;
        }

        double h = root.getHeight();
        if (w <= WINDOW_EDGE_VERTICAL + WINDOW_EDGE_VERTICAL) {
            // too short
            return null;
        }

        Point2D p = root.screenToLocal(screenx, screeny);
        double x = p.getX();
        if (x < WINDOW_EDGE_HORIZONTAL) {
            ADockDropOperation op = new ADockDropOperation(root, EWhereEdge.LEFT) {
                @Override
                public void executePrivate() {
                    DockTools.insertPane(client, root, EWhere.LEFT);
                }
            };
            op.addRect(root, 0, 0, WINDOW_EDGE_HORIZONTAL, h);
            return op;
        } else if (x > (w - WINDOW_EDGE_HORIZONTAL)) {
            ADockDropOperation op = new ADockDropOperation(root, EWhereEdge.RIGHT) {
                @Override
                public void executePrivate() {
                    DockTools.insertPane(client, root, EWhere.RIGHT);
                }
            };
            op.addRect(root, w - WINDOW_EDGE_HORIZONTAL, 0, WINDOW_EDGE_HORIZONTAL, h);
            return op;
        }

        double y = p.getY();
        if (y < WINDOW_EDGE_VERTICAL) {
            ADockDropOperation op = new ADockDropOperation(root, EWhereEdge.TOP) {
                @Override
                public void executePrivate() {
                    DockTools.insertPane(client, root, EWhere.TOP);
                }
            };
            op.addRect(root, 0, 0, w, WINDOW_EDGE_VERTICAL);
            return op;
        } else if (y > (h - WINDOW_EDGE_VERTICAL)) {
            ADockDropOperation op = new ADockDropOperation(root, EWhereEdge.BOTTOM) {
                @Override
                public void executePrivate() {
                    DockTools.insertPane(client, root, EWhere.BOTTOM);
                }
            };
            op.addRect(root, 0, h - WINDOW_EDGE_VERTICAL, w, WINDOW_EDGE_VERTICAL);
            return op;
        }

        return null;
    }


    protected static ADockDropOperation createDropToNewWindow(FxDockPane client, double screenx, double screeny) {
        return new ADockDropOperation(null, new WhereScreen(screenx, screeny)) {
            @Override
            public void executePrivate() {
                DockTools.moveToNewWindow(client, screenx - deltax, screeny - deltay);
            }
        };
    }

    protected static ADockDropOperation createDropOnPane(FxDockPane client, Pane target, double screenx, double screeny) {
        Point2D p = target.screenToLocal(screenx, screeny);
        double x = p.getX();
        double y = p.getY();
        double w = target.getWidth();
        double h = target.getHeight();
        double x1 = w * PANE_EDGE_HORIZONTAL;
        double x2 = w - w * PANE_EDGE_HORIZONTAL;
        double y1 = h * PANE_EDGE_VERTICAL;
        double y2 = h - h * PANE_EDGE_VERTICAL;

        EWhere EWhere = determineWhere(x, y, x1, x2, y1, y2, w, h);

        ADockDropOperation op = new ADockDropOperation(target, EWhere) {
            @Override
            public void executePrivate() {
                DockTools.moveToPane(client, target, EWhere);
            }
        };

        addRectangleToDropOperation(op, target, EWhere, x1, x2, y1, y2, w, h);
        return op;
    }

    /**
     * Determines the location on a pane where a drop operation should occur based on the given coordinates.
     *
     * @param x  The x-coordinate of the point being evaluated.
     * @param y  The y-coordinate of the point being evaluated.
     * @param x1 The x-coordinate threshold for the left edge of the pane.
     * @param x2 The x-coordinate threshold for the right edge of the pane.
     * @param y1 The y-coordinate threshold for the top edge of the pane.
     * @param y2 The y-coordinate threshold for the bottom edge of the pane.
     * @param w  The width of the pane.
     * @param h  The height of the pane.
     * @return A {@code Where} enum value indicating the location on the pane (e.g., TOP, BOTTOM, LEFT, RIGHT, CENTER)
     *         where the drop operation should occur.
     */
    private static EWhere determineWhere(double x, double y, double x1, double x2, double y1, double y2, double w, double h) {
        if (x < x1) {
            if (y < y1) {
                return DockTools.aboveDiagonal(x, y, 0, 0, x1, y1) ? EWhere.TOP : EWhere.LEFT;
            } else if (y < y2) {
                return EWhere.LEFT;
            } else {
                return DockTools.aboveDiagonal(x, y, 0, h, x1, y2) ? EWhere.LEFT : EWhere.BOTTOM;
            }
        } else if (x < x2) {
            if (y < y1) {
                return EWhere.TOP;
            } else if (y < y2) {
                return EWhere.CENTER;
            } else {
                return EWhere.BOTTOM;
            }
        } else {
            if (y < y1) {
                return DockTools.aboveDiagonal(x, y, x2, y1, w, 0) ? EWhere.TOP : EWhere.RIGHT;
            } else if (y < y2) {
                return EWhere.RIGHT;
            } else {
                return DockTools.aboveDiagonal(x, y, x2, y2, w, h) ? EWhere.RIGHT : EWhere.BOTTOM;
            }
        }
    }

    /**
     * Adds a rectangle to the drop operation based on the specified location.
     *
     * @param op     The drop operation to which the rectangle will be added.
     * @param target The target pane where the rectangle will be highlighted.
     * @param EWhere  The location on the pane where the rectangle should be added (e.g., BOTTOM, CENTER, LEFT, RIGHT, TOP).
     * @param x1     The x-coordinate for the left edge of the rectangle.
     * @param x2     The x-coordinate for the right edge of the rectangle.
     * @param y1     The y-coordinate for the top edge of the rectangle.
     * @param y2     The y-coordinate for the bottom edge of the rectangle.
     * @param w      The width of the target pane.
     * @param h      The height of the target pane.
     */
    private static void addRectangleToDropOperation(ADockDropOperation op, Pane target, EWhere EWhere, double x1, double x2, double y1, double y2, double w, double h) {
        switch (EWhere) {
            case BOTTOM:
                op.addRect(target, 0, y2, w, h - y2);
                break;
            case CENTER:
                op.addRect(target, 0, 0, w, h);
                break;
            case LEFT:
                op.addRect(target, 0, 0, x1, h);
                break;
            case RIGHT:
                op.addRect(target, x2, 0, w - x2, h);
                break;
            case TOP:
                op.addRect(target, 0, 0, w, y1);
                break;
        }
    }

    protected static ADockDropOperation createDropOnDivider(FxDockPane client, FxDockSplitPane parent, double screenx, double screeny) {
        List<Pane> splits = DockTools.collectDividers(parent);
        int sz = splits.size();
        for (int i = 0; i < sz; i++) {
            Pane n = splits.get(i);
            if (FX.contains(n, screenx, screeny)) {
                // new pane index
                int ix = i + 1;

                // found the divider, check if it's adjacent
                Node pc = DockTools.getParent(client);
                if (pc == parent) {
                    // do not allow to drop on adjacent split
                    int d = ix - DockTools.indexInParent(client);
                    switch (d) {
                        case 0, 1:
                            return null;
                        default:
                            break;
                    }
                }

                ADockDropOperation op = new ADockDropOperation(n, ix) {
                    @Override
                    public void executePrivate() {
                        DockTools.moveToSplit(client, parent, ix);
                    }
                };
                op.addRect(n, 0, 0, n.getWidth(), n.getHeight());
                return op;
            }
        }
        return null;
    }


    public static ADockDropOperation createDropOp(FxDockPane client, double screenx, double screeny) {
        // first, check if we are outside of any window
        FxDockWindow w = DockTools.findWindow(screenx, screeny);
        if (w == null) {
            return createDropToNewWindow(client, screenx, screeny);
        }

        // check if we can drop to window edge
        ADockDropOperation op = checkWindowEdge(client, w.getDockRootPane(), screenx, screeny);
        if (op != null) {
            return op;
        }

        // find the topmost docking element to accept the drop
        Node n = DockTools.findDockElement(w.getContent(), screenx, screeny);
        if (n == null) {
            return createDropToNewWindow(client, screenx, screeny);
        } else if (n instanceof FxDockSplitPane fxDockSplitPane) {
            // drop on a divider
            return createDropOnDivider(client, fxDockSplitPane, screenx, screeny);
        } else if (n instanceof Pane) {
            return createDropOnPane(client, (Pane) n, screenx, screeny);
        } else {
            throw new IllegalArgumentException("?" + n);
        }
    }
}