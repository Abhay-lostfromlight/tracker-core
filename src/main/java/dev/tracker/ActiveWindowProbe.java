package dev.tracker;

/**
 * Interface for probing the currently active (foreground) application window.
 * Implementations should be thread-safe.
 */
public interface ActiveWindowProbe {
    /**
     * Returns the process name of the currently active (foreground) window, or null if unavailable.
     * @return the process name of the active window, or null
     */
    String getActiveWindowProcessName();
} 