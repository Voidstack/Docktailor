package com.enosistudio.docktailor.fxdock.internal;

import com.enosistudio.docktailor.fx.FX;
import com.enosistudio.docktailor.fx.DocktailorUtility;
import com.enosistudio.docktailor.fx.WindowMonitor;
import com.enosistudio.docktailor.fxdock.FxDockPane;
import com.enosistudio.docktailor.fxdock.FxDockWindow;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Window;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.IntConsumer;
import java.util.function.IntFunction;


/**
 * Docking Framework Tools.
 */
@Slf4j(topic = "DockTools")
public class DockTools {
    private DockTools() {
    }

    private static boolean isEmpty(Node n) {
        if (n == null) {
            return true;
        } else if (n instanceof FxDockRootPane p) {
            return isEmpty(p.getContent());
        } else if (n instanceof FxDockBorderPane) {
            return false;
        } else if (n instanceof FxDockEmptyPane) {
            return true;
        } else if (n instanceof FxDockSplitPane p) {
            return (p.getPaneCount() == 0);
        } else if (n instanceof FxDockTabPane p) {
            return (p.getTabCount() == 0);
        }
        return true;
    }


    public static void setParent(Node p, Node child) {
        ReadOnlyObjectWrapper<Node> prop = getParentProperty(child);
        if (prop != null) {
            Node oldp = prop.get();
            if (oldp != null) {
                if (oldp == p) {
                    log.warn(String.format("same parent: %s", p)); // FIX ???
                } else {
                    int ix = indexInParent(child);
                    if (ix >= 0) {
                        replaceChild(oldp, ix, new DeletedPane());
                    }
                }
            }
            prop.set(p);
        }
    }


    private static ReadOnlyObjectWrapper<Node> getParentProperty(Node n) {
        if (n instanceof FxDockBorderPane p) {
            return p.parent;
        } else if (n instanceof FxDockSplitPane p) {
            return p.parent;
        } else if (n instanceof FxDockTabPane p) {
            return p.parent;
        } else if (n instanceof FxDockEmptyPane p) {
            return p.parent;
        } else if (n instanceof DeletedPane) {
            return null;
        }
//		return null;
        throw new IllegalArgumentException("?" + n);
    }


    public static void remove(Node n) {
        Node p = getParent(n);
        remove(p, n);
        collapseEmptySpace(p);
    }


    private static void remove(Node parent, Node child) {
        if (parent instanceof FxDockTabPane p) {
            p.removeTab(child);
        } else if (parent instanceof FxDockSplitPane p) {
            p.removePane(child);
        } else if (parent instanceof Pane p) {
            p.getChildren().remove(child);
        } else {
            throw new IllegalArgumentException("?" + parent);
        }
    }

    public static Node getParent(Node n) {
        if (n instanceof FxDockBorderPane p) {
            return p.parent.get();
        } else if (n instanceof FxDockSplitPane p) {
            return p.parent.get();
        } else if (n instanceof FxDockTabPane p) {
            return p.parent.get();
        } else if (n instanceof FxDockEmptyPane p) {
            return p.parent.get();
        }
        return null;
    }


    public static FxDockWindow findWindow(double screenx, double screeny) {
        List<FxDockWindow> list = null;
        for (Window w : Window.getWindows()) {
            if (w instanceof FxDockWindow dw && !dw.isIconified() && contains(dw, screenx, screeny)) {
                if (list == null) {
                    list = new ArrayList<>();
                }
                list.add(dw);
            }
        }

        if (list == null) {
            return null;
        } else if (list.size() == 1) {
            return list.get(0);
        } else {
            return WindowMonitor.findTopWindow(list);
        }
    }

    /**
     * returns a list of visible windows, topmost window first
     */
    public static List<FxDockWindow> getWindows() {
        List<Window> ws = WindowMonitor.getWindowStack();
        int sz = ws.size();
        List<FxDockWindow> rv = new ArrayList<>(sz);
        for (int i = sz - 1; i >= 0; i--) {
            Window w = ws.get(i);
            if (w instanceof FxDockWindow dw) {
                rv.add(dw);
            }
        }
        return rv;
    }

    public static FxDockWindow getWindow(Node n) {
        if (n != null) {
            Scene sc = n.getScene();
            if (sc != null) {
                Window w = sc.getWindow();
                if (w instanceof FxDockWindow fxDockWindow) {
                    return fxDockWindow;
                }
            }
        }
        return null;
    }

    // returns true if the window has been closed
    public static boolean closeWindowUnlessLast(Node n) {
        log.debug(n.toString());
        FxDockWindow w = getWindow(n);
        if (w != null && getWindows().size() > 1) {
            // do not store the empty window
            FX.setSkipSettings(w);
            w.close();
            return true;
        }
        return false;
    }


    public static boolean aboveDiagonal(double x, double y, double x0, double y0, double x1, double y1) {
        double liney = y0 + (y1 - y0) * (x - x0) / (x1 - x0);
        return y < liney;
    }


    public static boolean contains(Window w, double screenx, double screeny) {
        double x = w.getX();
        if (screenx < x || screenx > (x + w.getWidth())) {
            return false;
        }

        double y = w.getY();
        if (screeny < y) {
            return false;
        } else return screeny <= (y + w.getHeight());
    }

    public static List<Pane> collectDividers(FxDockSplitPane sp) {
        List<Pane> rv = new ArrayList<>();
        for (Node n : sp.lookupAll(".split-pane-divider")) {
            if (n instanceof Pane p && p.getParent() == sp) {
                rv.add(p);
            }
        }
        return rv;
    }

    public static Node findDockElement(Node n, double screenx, double screeny) {
        if (n != null) {
            Point2D pt = n.screenToLocal(screenx, screeny);
            if (n.contains(pt)) {
                if (n instanceof FxDockPane) {
                    return n;
                } else if (n instanceof FxDockEmptyPane) {
                    return n;
                } else if (n instanceof FxDockSplitPane p) {
                    Node ch = findDockElement(p.getPanes(), screenx, screeny);
                    // on a divider
                    return Objects.requireNonNullElse(ch, n);
                } else if (n instanceof FxDockTabPane t) {
                    return t.getSelectedTab();
                }

                if (n instanceof Parent) {
                    return findDockElement(((Parent) n).getChildrenUnmodifiable(), screenx, screeny);
                }
            }
        }
        return null;
    }

    private static Node findDockElement(List<Node> ns, double screenx, double screeny) {
        for (Node ch : ns) {
            Node found = findDockElement(ch, screenx, screeny);
            if (found != null) {
                return found;
            }
        }
        return null;
    }

    private static void unlink(Node node) {
        Node n = getParent(node);
        if (n == null) {
            return; // Fix: added return statement
        } else if (n instanceof FxDockSplitPane p) {
            // make sure an empty pane is left in place
            int ix = indexInParent(node);
            p.setPane(ix, new DeletedPane());
        } else if (n instanceof FxDockTabPane p) {
            // make sure an empty pane is left in place
            //        int ix = indexInParent(node);
            //       p.setTab(ix, node);
        } else if (n instanceof FxDockRootPane p) {
            p.setContent(new DeletedPane());
        } else if (n instanceof DeletedPane) {
            return; // Fix: added return statement
        } else {
            throw new IllegalArgumentException("?" + n);
        }
    }

    public static Node prepareToAdd(Node n) {
        if (n == null) {
            return new FxDockEmptyPane();
        } else {
            // TODO this does not look right - use unlink before starting the move
            unlink(n);
            return n;
        }
    }

    private static Node makeSplit(Node client, Node old, EWhere EWhere) {
        if (client == old) {
            old = new FxDockEmptyPane();
        }

        // check if nested splits are not needed
        Node p = getParent(old);
        if (p instanceof FxDockSplitPane sp) {
            if (sp.getOrientation() == Orientation.HORIZONTAL) {
                switch (EWhere) {
                    case LEFT:
                        sp.addPane(0, client);
                        return sp;
                    case RIGHT:
                        sp.addPane(client);
                        return sp;
                    default:
                        break;
                }
            } else {
                switch (EWhere) {
                    case TOP:
                        sp.addPane(0, client);
                        return sp;
                    case BOTTOM:
                        sp.addPane(client);
                        return sp;
                    default:
                        break;
                }
            }
        }

        return switch (EWhere) {
            case BOTTOM -> new FxDockSplitPane(Orientation.VERTICAL, old, client);
            case LEFT -> new FxDockSplitPane(Orientation.HORIZONTAL, client, old);
            case RIGHT -> new FxDockSplitPane(Orientation.HORIZONTAL, old, client);
            case TOP -> new FxDockSplitPane(Orientation.VERTICAL, client, old);
            default -> throw new IllegalArgumentException("?" + EWhere);
        };
    }


    private static FxDockTabPane makeTab(Node old, Node client) {
        FxDockTabPane t = new FxDockTabPane();
        t.addTab(old);
        t.addTab(client);
        t.select(client);
        return t;
    }


    public static int indexInParent(Node node) {
        Node n = getParent(node);
        if (n instanceof FxDockSplitPane p) {
            return p.indexOfPane(node);
        } else if (n instanceof FxDockTabPane p) {
            return p.indexOfTab(node);
        }
        return -1;
    }

    /**
     * Replaces a child node at a specified index within a parent node with a new child node.
     *
     * @param parent   the parent node containing the child to be replaced. Must be an instance of FxDockSplitPane,
     *                 FxDockTabPane, or FxDockRootPane.
     * @param index    the index of the child node to be replaced within the parent node.
     * @param newChild the new child node to replace the existing child node. If the parent is an FxDockRootPane and the
     *                 new child is an instance of FxDockPane, it will be wrapped in an FxDockTabPane.
     * @throws IllegalArgumentException if the parent node is not a recognized type.
     */
    private static void replaceChild(Node parent, int index, Node newChild) {
        if (parent instanceof FxDockSplitPane p) {
            p.setPane(index, newChild);
        } else if (parent instanceof FxDockTabPane p) {
            p.setTab(index, newChild);
        } else if (parent instanceof FxDockRootPane p) {
            if (newChild instanceof FxDockPane) {
                p.setContent(new FxDockTabPane(newChild));
            } else {
                p.setContent(newChild);
            }
        } else {
            throw new IllegalArgumentException("?" + parent);
        }
    }

    public static void collapseEmptySpace(Node parent) {
        if (parent instanceof FxDockSplitPane sp) {
            processCollapseEmptySpace(sp.getPaneCount(), sp::getPane, sp::removePane, parent);
        } else if (parent instanceof FxDockTabPane tp) {
            processCollapseEmptySpace(tp.getTabCount(), tp::getTab, tp::removeTab, parent);
        } else if (parent instanceof FxDockRootPane rp && isEmpty(rp.getContent()) && !closeWindowUnlessLast(parent))
            rp.setContent(null);
    }

    /**
     * Processes and collapses empty spaces within a parent node by removing unnecessary nodes and merging adjacent
     * empty spaces.
     *
     * @param count  the number of child nodes within the parent node.
     * @param get    a function to retrieve a child node at a specified index.
     * @param remove a consumer to remove a child node at a specified index.
     * @param parent the parent node containing the child nodes to be processed.
     */
    private static void processCollapseEmptySpace(int count, IntFunction<Node> get, IntConsumer remove, Node parent) {
        // Supprimer DeletedPane / null / FxDockTabPane vide
        for (int i = count - 1; i >= 0; i--) {
            Node n = get.apply(i);
            if (n == null || n instanceof DeletedPane || (n instanceof FxDockTabPane fx && fx.getTabs().isEmpty()))
                remove.accept(i);
        }
        // Fusionner espaces vides
        boolean empty = false;
        int ct = 0;
        int idx = -1;
        for (int i = count - 1; i >= 0; i--) {
            Node n = get.apply(i);
            if (isEmpty(n)) {
                if (empty) remove.accept(i);
                empty = !empty;
            } else {
                empty = false;
                ct++;
                idx = i;
            }
        }
        // Cas final : remplacer ou aplatir
        if (ct < 2) {
            Node pp = getParent(parent);
            switch (ct) {
                case 0 -> {
                    replaceChild(pp, indexInParent(parent), new DeletedPane());
                    collapseEmptySpace(pp);
                }
                case 1 -> {
                    if (parent instanceof FxDockSplitPane) replaceChild(pp, indexInParent(parent), get.apply(idx));
                }
                default -> throw new IllegalArgumentException("?" + ct);
            }
        }
    }

    private static boolean insertSplit(FxDockPane client, FxDockSplitPane sp, Object object) {
        if (object instanceof EWhere EWhere) {
            Orientation or = sp.getOrientation();
            switch (EWhere) {
                case LEFT:
                    if (or == Orientation.HORIZONTAL) {
                        sp.addPane(0, client);
                        return true;
                    }
                    break;
                case RIGHT:
                    if (or == Orientation.HORIZONTAL) {
                        sp.addPane(client);
                        return true;
                    }
                    break;
                case TOP:
                    if (or == Orientation.VERTICAL) {
                        sp.addPane(0, client);
                        return true;
                    }
                    break;
                case BOTTOM:
                    if (or == Orientation.VERTICAL) {
                        sp.addPane(client);
                        return true;
                    }
                    break;
                default:
                    break;
            }
        }
        return false;
    }


    public static void insertPane(FxDockPane client, Node target, Object where) {
        if (target instanceof FxDockRootPane rp) {

            BeforeDrop b = new BeforeDrop(client, target);
            boolean makesplit = true;
            Node old = rp.getContent();
            if (old instanceof FxDockSplitPane fxDockSplitPane && insertSplit(client, fxDockSplitPane, where)) {
                makesplit = false;
            }

            if (makesplit) {
                rp.setContent(makeSplit(client, old, (EWhere) where));
            }

            collapseEmptySpace(b.clientParent);
            b.adjustSplits();
        } else if (target instanceof FxDockPane) {
            // FIX
            throw new IllegalArgumentException("replace with split or tab");
        } else {
            // TODO
            throw new IllegalArgumentException("?" + target);
        }
    }

    public static FxDockWindow createWindow() {
        return (FxDockWindow) DocktailorUtility.createDefaultWindow();
    }

    public static void moveToNewWindow(FxDockPane client, double screenx, double screeny) {
        Node p = getParent(client);
        double width = client.getWidth();
        double height = client.getHeight();

        FxDockWindow w = createWindow();
        FxDockTabPane fxDockRootPane = new FxDockTabPane(client);
        w.setContent(fxDockRootPane);

        w.setX(screenx);
        w.setY(screeny);

        // moving window after show() seems to cause flicker
        double op = w.getOpacity();
        w.setOpacity(0);

        w.open();

        // take into account window decorations
        // apparently, this is available only after show()
        Node n = w.getDockRootPane().getCenter();
        Insets m = FX.getInsetsInWindow(w, n);

        // this may still cause flicker
        w.setX(screenx - m.getLeft());
        w.setY(screeny - m.getTop());
        w.setWidth(width + m.getLeft() + m.getRight());
        w.setHeight(height + m.getTop() + m.getBottom());

        w.setOpacity(op);

        collapseEmptySpace(p);
    }


    public static void moveToNewWindow(FxDockPane client) {
        Node p = getParent(client);
        Window clientWindow = getWindow(client);

        // TODO still not correct
        Insets m = getWindowInsets(clientWindow);
        Point2D pos = client.localToScreen(0, 0);

        FxDockWindow w = createWindow();

        w.setContent(client);
        w.setX(pos.getX() - m.getLeft());
        w.setY(pos.getY() - m.getTop());
        w.setWidth(client.getWidth() + m.getRight() + m.getLeft());
        w.setHeight(client.getHeight() + m.getTop() + m.getBottom());
        w.open();

        collapseEmptySpace(p);
    }


    public static Insets getWindowInsets(Window w) {
        Scene s = w.getScene();

        double left = s.getX();
        double top = s.getY();
        double right = w.getWidth() - s.getWidth() - left;
        double bottom = w.getHeight() - s.getHeight() - top;

        return new Insets(top, right, bottom, left);
    }


    public static void moveToSplit(FxDockPane client, FxDockSplitPane sp, int index) {
        BeforeDrop b = new BeforeDrop(client, sp);
        sp.addPane(index, client);
        collapseEmptySpace(b.clientParent);
        b.adjustSplits();
    }

    private static void addToRootPane(FxDockPane client, FxDockRootPane rp, EWhere EWhere) {
        Node old = rp.getContent();

        if (Objects.requireNonNull(EWhere) == EWhere.CENTER) {
            rp.setContent(makeTab(old, client));
        } else {
            rp.setContent(makeSplit(client, old, EWhere));
        }
    }

    private static void addToTabPane(FxDockPane client, FxDockTabPane tp, int index, EWhere EWhere) {
        if (Objects.requireNonNull(EWhere) == EWhere.CENTER) {
            tp.addTab(client);
            tp.select(client);
        } else {
            Node p = getParent(tp);
            int ix = indexInParent(tp);
            Node sp = makeSplit(client, tp, EWhere);
            if (p != sp) {
                replaceChild(p, ix, sp);
            }
        }
    }


    private static void addToSplitPane(FxDockPane client, Pane target, FxDockSplitPane sp, int index, EWhere EWhere) {
        // determine index from where and sp orientation
        int ix;
        if (sp.getOrientation() == Orientation.HORIZONTAL) {
            ix = switch (EWhere) {
                case LEFT -> index;
                case RIGHT -> index + 1;
                default -> -1;
            };
        } else {
            ix = switch (EWhere) {
                case BOTTOM -> index + 1;
                case TOP -> index;
                default -> -1;
            };
        }

        if (ix < 0) {
            Node p = getParent(target);
            int ip = indexInParent(target);

            if (EWhere == EWhere.CENTER) {
                if (target instanceof FxDockEmptyPane) {
                    sp.setPane(ip, client);
                } else {
                    Node t = makeTab(target, client);
                    replaceChild(p, ip, t);
                }
            } else {
                Node n = makeSplit(client, target, EWhere);
                replaceChild(p, ip, n);
            }
        } else {
            // simply insert another pane into this split pane
            sp.addPane(ix, client);
        }
    }


    /**
     * Moves the specified client pane to a new position within the target pane, creating necessary splits or tabs.
     *
     * @param client the FxDockPane to be moved.
     * @param target the Pane where the client will be moved to.
     * @param EWhere  the position relative to the target where the client should be placed.
     * @throws IllegalArgumentException if the target's parent is not a recognized type.
     */
    public static void moveToPane(FxDockPane client, Pane target, EWhere EWhere) {
        BeforeDrop b = new BeforeDrop(client, target);
        Node targetParent = getParent(target);

        // handle the case where client and target are the same and where is CENTER
        if (client == target && EWhere == EWhere.CENTER) {
            return;
        }

        if (targetParent instanceof FxDockSplitPane fxDockSplitPane) {
            int targetIndex = indexInParent(target);
            addToSplitPane(client, target, fxDockSplitPane, targetIndex, EWhere);
        } else if (targetParent instanceof FxDockTabPane fxDockTabPane) {
            int targetIndex = indexInParent(target);
            addToTabPane(client, fxDockTabPane, targetIndex, EWhere);
        } else if (targetParent instanceof FxDockRootPane fxDockRootPane) {
            addToRootPane(client, fxDockRootPane, EWhere);
        } else {
            throw new IllegalArgumentException("?" + targetParent);
        }
        collapseEmptySpace(b.clientParent);
        b.adjustSplits();
    }
}