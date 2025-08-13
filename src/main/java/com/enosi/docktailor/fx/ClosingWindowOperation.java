package com.enosi.docktailor.fx;

/**
 * Closing Window Operation.
 */
@FunctionalInterface
public interface ClosingWindowOperation {
    /**
     * Valid inputs: DISCARD_ALL, SAVE_ALL, UNDEFINED. Valid outputs: CANCEL, CONTINUE, DISCARD_ALL, SAVE_ALL
     */
    EShutdownChoice confirmClosing(boolean exiting, boolean multiple, EShutdownChoice choice);
}
