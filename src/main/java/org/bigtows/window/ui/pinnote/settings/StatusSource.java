package org.bigtows.window.ui.pinnote.settings;

/**
 * Enum of work-status source
 */
public enum StatusSource {
    /**
     * Source enabled and functionally
     */
    ENABLED,
    /**
     * Source disable and not used
     */
    DISABLED,

    /**
     * Source contains some problems for correct work.
     * <p>
     * Example: Invalid token, for connection.
     * </p>
     */
    HAS_PROBLEM
}
