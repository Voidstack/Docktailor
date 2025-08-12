// Copyright © 2024-2025 Andy Goryachev <andy@goryachev.com>
package com.enosi.docktailor.docktailor.fx;

/**
 * User choice for saving/discarding modified content in multiple windows when exiting the application.
 */
public enum ShutdownChoice {
    CANCEL,
    CONTINUE,
    DISCARD_ALL,
    SAVE_ALL,
    UNDEFINED;
}