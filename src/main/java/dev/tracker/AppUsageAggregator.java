package dev.tracker;

import java.util.HashMap;
import java.util.Map;

/**
 * Aggregates application usage time in seconds for the current day.
 * Thread-safe for single-threaded scheduled usage.
 */
public class AppUsageAggregator {
    /**
     * Map of app name to total usage time in seconds for today.
     */
    private final Map<String, Integer> appTimeSeconds = new HashMap<>();

    /**
     * Increments the usage time for the given app by 2 seconds.
     * Called at a 2-second tick from the scheduler.
     * @param appName the name of the application
     */
    public void update(String appName) {
        appTimeSeconds.put(appName, appTimeSeconds.getOrDefault(appName, 0) + 2);
    }

    /**
     * Returns the current map of app usage times (in seconds).
     * @return map of app name to seconds used
     */
    public Map<String, Integer> getAppTimeSeconds() {
        return appTimeSeconds;
    }
} 