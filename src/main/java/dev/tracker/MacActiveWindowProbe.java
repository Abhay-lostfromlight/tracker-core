package dev.tracker;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import oshi.SystemInfo;
import oshi.software.os.OSProcess;
import oshi.software.os.OperatingSystem;

/**
 * Probes the active window on macOS.
 * Uses osascript to call NSWorkspace.sharedWorkspace().frontmostApplication().
 * Thread-safe.
 */
public class MacActiveWindowProbe implements ActiveWindowProbe {
    private final SystemInfo systemInfo = new SystemInfo();
    private final OperatingSystem os = systemInfo.getOperatingSystem();

    /**
     * Gets the process name of the currently active window on macOS.
     * Uses osascript to query the frontmost application.
     * @return process name or null if unavailable
     */
    @Override
    public String getActiveWindowProcessName() {
        try {
            // Use osascript to get the name of the frontmost application
            String[] cmd = {"osascript", "-e", "tell application \"System Events\" to get name of first process whose frontmost is true"};
            Process process = Runtime.getRuntime().exec(cmd);
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String appName = reader.readLine();
            process.waitFor();
            if (appName == null || appName.isEmpty()) {
                return null;
            }
            // Match the app name to a process (handles .app suffix)
            for (OSProcess proc : os.getProcesses(null, OperatingSystem.ProcessSorting.NAME_ASC, 0)) {
                if (appName.equals(proc.getName()) || appName.equals(proc.getName().replaceFirst("\\.app$", ""))) {
                    return proc.getName();
                }
            }
            return appName;
        } catch (Exception e) {
            return null;
        }
    }
} 