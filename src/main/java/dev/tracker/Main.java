package dev.tracker;

import java.util.Locale;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.Set;
import oshi.SystemInfo;
import oshi.software.os.OSProcess;
import oshi.software.os.OperatingSystem;

/**
 * Main entry point for the tracker-core application.
 * Schedules two tasks: a 2-second active window tracker and a 60-second usage summary printer.
 */
public class Main {
    /**
     * Application entry point. Detects OS, sets up probes, and starts scheduled tasks.
     * @param args command-line arguments (unused)
     */
    public static void main(String[] args) {
        ActiveWindowProbe probe;
        // Detect OS and select the appropriate probe implementation
        String os = System.getProperty("os.name").toLowerCase(Locale.ROOT);
        if (os.contains("win")) {
            probe = new WindowsActiveWindowProbe();
        } else if (os.contains("mac")) {
            probe = new MacActiveWindowProbe();
        } else {
            System.err.println("Unsupported OS: " + os);
            return;
        }

        AppUsageAggregator aggregator = new AppUsageAggregator();
        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

        // Define the set of apps to track regardless of focus
        Set<String> trackedApps = Set.of("Code", "Chrome", "Cursor");
        SystemInfo systemInfo = new SystemInfo();
        OperatingSystem operatingSystem = systemInfo.getOperatingSystem();

        // Task 1: Every 2 seconds, update app usage for tracked apps and print the active window
        scheduler.scheduleAtFixedRate(() -> {
            // Track all running instances of the selected apps
            for (OSProcess proc : operatingSystem.getProcesses(null, OperatingSystem.ProcessSorting.NAME_ASC, 0)) {
                String procName = proc.getName();
                if (trackedApps.contains(procName)) {
                    aggregator.update(procName);
                }
            }
            // Still print the focused window as before
            String name = probe.getActiveWindowProcessName();
            System.out.println("[2s interval] Active window: " + (name != null ? name : "(none)"));
        }, 0, 2, TimeUnit.SECONDS);

        // Task 2: Every 60 seconds, print a summary of app usage
        scheduler.scheduleAtFixedRate(() -> {
            System.out.println("--- App Usage Summary (seconds) ---");
            aggregator.getAppTimeSeconds().forEach((app, seconds) ->
                System.out.println(app + ": " + seconds)
            );
            System.out.println("-----------------------------------");
        }, 60, 60, TimeUnit.SECONDS);
    }
} 