package com.enosistudio.docktailor.fx;

import com.enosistudio.docktailor.DocktailorService;
import com.enosistudio.docktailor.common.DocktailorEvent;
import javafx.application.Platform;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.beans.property.ReadOnlyDoubleWrapper;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.value.ChangeListener;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Tooltip;
import javafx.stage.Stage;
import javafx.stage.Window;
import javafx.stage.WindowEvent;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.*;


/**
 * Window Monitor. Remembers the location/size and attributes of windows. Keeps track of Z order of open windows.
 */
@Slf4j
@SuppressWarnings("unused")
public class WindowMonitor {
    private static final String SEPARATOR = "_";
    private static final Object PROP_CLOSING = new Object();
    private static final Object PROP_MONITOR = new Object();
    private static final Object PROP_NON_ESSENTIAL = new Object();
    /**
     * in reverse order: top window is last
     */
    private static final ObservableList<Window> stack = FXCollections.observableArrayList();
    private static final ObservableList<Window> unmodifiableStack = FXCollections.unmodifiableObservableList(stack);
    private static final DocktailorEvent<Void> onAllWindowsClosed = new DocktailorEvent<>();
    private static final ReadOnlyObjectWrapper<Node> lastFocusOwner = new ReadOnlyObjectWrapper<>();
    private static boolean exiting;
    private static EShutdownChoice shutdownChoice;

    static {
        init();
    }

    @Getter
    private final Window window;
    @Getter
    private final String id;
    private final ReadOnlyDoubleWrapper x = new ReadOnlyDoubleWrapper();
    private final ReadOnlyDoubleWrapper y = new ReadOnlyDoubleWrapper();
    private final ReadOnlyDoubleWrapper width = new ReadOnlyDoubleWrapper();
    private final ReadOnlyDoubleWrapper height = new ReadOnlyDoubleWrapper();
    private double xNorm, yNorm, widthNorm, heightNorm;

    public WindowMonitor(Window w, String id) {
        this.window = w;
        this.id = id;

        x.set(w.getX());
        y.set(w.getY());
        width.set(w.getWidth());
        height.set(w.getHeight());

        ChangeListener<Node> focusListener = (s, p, c) -> updateFocusOwner(c);

        // FIX does not work
        w.sceneProperty().addListener((src, prev, cur) -> {
            if (prev != null) {
                prev.focusOwnerProperty().removeListener(focusListener);
            }

            if (cur != null) {
                cur.focusOwnerProperty().addListener(focusListener);
            }
        });
        if (w.getScene() != null && w.getScene().getFocusOwner() != null) {
            updateFocusOwner(w.getScene().getFocusOwner());
        }

        w.focusedProperty().addListener((src, prev, cur) -> {
            if (cur) {
                updateFocusedWindow(w);
            }
        });

        w.xProperty().addListener(p -> {
            xNorm = x.get();
            x.set(w.getX());
        });

        w.yProperty().addListener(p -> {
            yNorm = y.get();
            y.set(w.getY());
        });

        w.widthProperty().addListener(p -> {
            widthNorm = width.get();
            width.set(w.getWidth());
        });

        w.heightProperty().addListener(p -> {
            heightNorm = height.get();
            height.set(w.getHeight());
        });

        if (w instanceof Stage s) {
            s.iconifiedProperty().addListener(p -> {
                if (s.isIconified()) {
                    x.set(xNorm);
                    y.set(yNorm);
                }
            });

            s.maximizedProperty().addListener(p -> {
                if (s.isMaximized()) {
                    x.set(xNorm);
                    y.set(yNorm);
                }
            });

            s.fullScreenProperty().addListener(p -> {
                if (s.isFullScreen()) {
                    x.set(xNorm);
                    y.set(yNorm);
                    width.set(widthNorm);
                    height.set(heightNorm);
                }
            });
        }
    }

    public double getX() { return x.get(); }
    public ReadOnlyDoubleProperty xProperty() { return x.getReadOnlyProperty(); }

    public double getY() { return y.get(); }
    public ReadOnlyDoubleProperty yProperty() { return y.getReadOnlyProperty(); }

    public double getWidth() { return width.get(); }
    public ReadOnlyDoubleProperty widthProperty() { return width.getReadOnlyProperty(); }

    public double getHeight() { return height.get(); }
    public ReadOnlyDoubleProperty heightProperty() { return height.getReadOnlyProperty(); }

    private static void init() {
        FX.addChangeListener(Window.getWindows(), ch -> {
            boolean save = false;

            while (ch.next()) {
                if (ch.wasAdded()) {
                    for (Window w : ch.getAddedSubList()) {
                        if (!isIgnore(w)) {
                            log.debug(String.format("added: %s", w));
                            DocktailorService.getSchema().restoreWindow(w);
                            // applyStyleSheet(w);
                        }
                    }
                } else if (ch.wasRemoved()) {
                    for (Window w : ch.getRemoved()) {
                        if (!isIgnore(w)) {
                            log.debug(String.format("removed: %s", w));
                            // the only problem here is that window is already hidden - does it matter?
                            // if it does, need to listen to WindowEvent.WINDOW_HIDING event
                            //	FxFramework.store(w);
                            stack.remove(w);
                            save = true;
                        }
                    }
                    if (stack.isEmpty()) {
                        onAllWindowsClosed.invoke(null);
                    }
                }

                if (save) {
                    // ne JAMAIS sauvegarder ici.
                    // FxFramework.save();
                }
            }
        });

        stack.addAll(Window.getWindows());
        dumpStack();
    }

    private static void dumpStack() {
        log.debug(stack.stream().map(FxTools::describe).toList().toString());
    }

    private static String createID(Window win, String useID) {
        String name = FX.getName(win);
        if (name != null) {
            // collect existing ids
            Set<String> ids = new HashSet<>();
            for (Window w : Window.getWindows()) {
                if (w != win) {
                    WindowMonitor m = get(w);
                    if (m != null) {
                        String id = m.getId();
                        if (id.startsWith(name)) {
                            ids.add(id);
                        }
                    }
                }
            }

            if (useID != null) {
                // check if this combination does not exist already
                String id = name + SEPARATOR + useID;
                if (ids.contains(id)) {
                    // this should not happen if FxSettings.openLayout() is called once at the launch
                    throw new IllegalStateException("duplicate id:" + id);
                }
                return id;
            }

            for (int i = 0; i < 200_000; i++) {
                String id = name + SEPARATOR + i;
                if (!ids.contains(id)) {
                    return id;
                }
            }
        }
        return null;
    }

    public static WindowMonitor forWindow(Window w) {
        return forWindow(w, null);
    }

    public static WindowMonitor forWindow(Window w, String useID) {
        if (w != null) {
            if (isIgnore(w)) {
                return null;
            }

            WindowMonitor m = get(w);
            if (m == null) {
                String id = createID(w, useID);
                if (id != null) {
                    m = new WindowMonitor(w, id);
                    set(w, m);
                }
            }
            return m;
        }
        return null;
    }

    static boolean isIgnore(Window w) {
        if (w instanceof Tooltip) {
            return true;
        } else return w instanceof ContextMenu;
    }

    private static WindowMonitor get(Window w) {
        Object x = w.getProperties().get(PROP_MONITOR);
        if (x instanceof WindowMonitor m) {
            return m;
        }
        return null;
    }

    private static void set(Window w, WindowMonitor m) {
        w.getProperties().put(PROP_MONITOR, m);
    }

    public static WindowMonitor forNode(Node n) {
        Scene s = n.getScene();
        if (s != null) {
            Window w = s.getWindow();
            if (w != null) {
                return forWindow(w);
            }
        }
        return null;
    }

    private static void applyStyleSheet(Window w) {
        try {
            String style = "";
            FX.applyStyleSheet(w, null, style);
        } catch (Exception e) {
            log.error("", e);
        }
    }

    /**
     * Returns a snapshot of the window stack in reverse Z-order (top window is last).
     */
    public static List<Window> getWindowStack() {
        return new ArrayList<>(stack);
    }

    /**
     * Returns an unmodifiable observable list of windows in reverse Z-order (top window is last).
     * Listeners attached to this list will be notified when windows are added or removed.
     */
    public static ObservableList<Window> getObservableWindowStack() {
        return unmodifiableStack;
    }

    /**
     * Returns the event fired when all Docktailor windows have been closed.
     */
    public static DocktailorEvent<Void> getOnAllWindowsClosed() {
        return onAllWindowsClosed;
    }

    @SuppressWarnings("unchecked")
    public static <W extends Window> W findTopWindow(List<? extends Window> list) {
        for (int i = stack.size() - 1; i >= 0; i--) {
            Window w = stack.get(i);
            if (list.contains(w)) {
                return (W) w;         // cast required
            }
        }
        return null;
    }

    static void updateFocusedWindow(Window w) {
        log.debug(w.toString());
        stack.remove(w);
        stack.add(w);
        dumpStack();
    }

    static void updateFocusOwner(Node n) {
        if (n != null) {
            log.debug(n.toString());
            lastFocusOwner.set(n);
        }
    }

    public static Node getLastFocusOwner() {
        return lastFocusOwner.get();
    }

    public static ReadOnlyObjectProperty<Node> lastFocusOwnerProperty() {
        return lastFocusOwner.getReadOnlyProperty();
    }

    public static void setClosingWindowOperation(Window w, ClosingWindowOperation op) {
        if (op == null) {
            Object x = w.getProperties().remove(PROP_CLOSING);
            if (x instanceof CloseRequestListener old) {
                w.removeEventHandler(WindowEvent.WINDOW_CLOSE_REQUEST, old);
            }
        } else {
            CloseRequestListener c = new CloseRequestListener(op);
            Object x = w.getProperties().put(PROP_CLOSING, c);
            if (x instanceof CloseRequestListener old) {
                w.removeEventHandler(WindowEvent.WINDOW_CLOSE_REQUEST, old);
            }
            w.addEventHandler(WindowEvent.WINDOW_CLOSE_REQUEST, c);
        }
    }

    private static ClosingWindowOperation getClosingWindowOperation(Window w) {
        Object x = w.getProperties().get(PROP_CLOSING);
        if (x instanceof CloseRequestListener op) {
            return op.operation;
        }
        return null;
    }

    public static void setNonEssentialWindow(Window w) {
        w.getProperties().put(PROP_NON_ESSENTIAL, Boolean.TRUE);
    }

    private static boolean isNonEssentialWindow(Window w) {
        return Boolean.TRUE.equals(w.getProperties().get(PROP_NON_ESSENTIAL));
    }

    private static int countEssentialWindows(Object caller) {
        int count = 0;
        for (Window w : stack) {
            if ((caller == w) || isNonEssentialWindow(w)) {
                continue;
            }
            count++;
        }
        return count;
    }

    public static void exit() {
        if (!exiting) {
            exiting = true;
            shutdownChoice = EShutdownChoice.UNDEFINED;

            if (confirmExit()) {
                doExit();
            } else {
                exiting = false;
            }
        }
    }

    private static void doExit() {
        Platform.exit();
        System.exit(0);
    }

    /**
     * returns true when ok to exit
     */
    private static boolean confirmExit() {
        for (int i = stack.size() - 1; i >= 0; i--) {
            Window w = stack.get(i);
            ClosingWindowOperation op = getClosingWindowOperation(w);
            if (op != null) {
                int count = countEssentialWindows(null);
                boolean multiple = count > 1;
                EShutdownChoice rsp = op.confirmClosing(exiting, multiple, shutdownChoice);
                switch (rsp) {
                    case DISCARD_ALL, SAVE_ALL:
                        shutdownChoice = rsp;
                        break;
                    case CONTINUE:
                        break;
                    case CANCEL, UNDEFINED:
                    default:
                        return false;
                }
            }
        }
        return true;
    }

    /**
     * strips the window name and the separator, returning the id part only
     */
    public String getIDPart() {
        int ix = id.lastIndexOf(SEPARATOR);
        if (ix < 0) {
            throw new IllegalStateException("no id: " + id);
        }
        return id.substring(ix + 1);
    }

    private static class CloseRequestListener implements EventHandler<WindowEvent> {
        public final ClosingWindowOperation operation;


        public CloseRequestListener(ClosingWindowOperation op) {
            this.operation = op;
        }


        @Override
        public void handle(WindowEvent ev) {
            if (exiting) {
                // application exit, handled in exit()
                return;
            }

            EShutdownChoice rsp = operation.confirmClosing(exiting, false, EShutdownChoice.UNDEFINED);
            if (Objects.requireNonNull(rsp) == EShutdownChoice.CANCEL) {
                ev.consume();
                return;
            }

            if (countEssentialWindows(ev.getSource()) == 0) {
                doExit();
            }
        }
    }
}