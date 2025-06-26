package dev.tracker;

import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinDef;
import com.sun.jna.ptr.IntByReference;
import oshi.SystemInfo;
import oshi.software.os.OSProcess;
import oshi.software.os.OperatingSystem;

/**
 * Probes the active window on Windows using JNA.
 * Uses GetForegroundWindow and maps HWND to PID to process name.
 * Thread-safe.
 */
public class WindowsActiveWindowProbe implements ActiveWindowProbe {
    private final SystemInfo systemInfo = new SystemInfo();
    private final OperatingSystem os = systemInfo.getOperatingSystem();

    /**
     * Gets the process name of the currently active window on Windows.
     * Uses User32.GetForegroundWindow and PID mapping.
     * @return process name or null if unavailable
     */
    @Override
    public String getActiveWindowProcessName() {
        // Get the handle to the foreground window using JNA
        WinDef.HWND hwnd = User32.INSTANCE.GetForegroundWindow();
        if (hwnd == null) {
            return null;
        }
        // Map window handle to process ID
        IntByReference pidRef = new IntByReference();
        User32.INSTANCE.GetWindowThreadProcessId(hwnd, pidRef);
        int pid = pidRef.getValue();
        if (pid == 0) {
            return null;
        }
        // Lookup process by PID
        OSProcess process = os.getProcess(pid);
        if (process == null) {
            return null;
        }
        return process.getName();
    }
} 