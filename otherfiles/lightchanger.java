import com.sun.jna.Library;
import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinReg;
import com.sun.jna.Native;
import com.sun.jna.Platform;
import com.sun.jna.platform.win32.WinDef;
import com.sun.jna.platform.win32.WinDef.LPARAM;
import com.sun.jna.platform.win32.WinDef.BOOL;
import com.sun.jna.platform.win32.WinDef.HDC;
import com.sun.jna.platform.win32.WinUser.HMONITOR;
import com.sun.jna.platform.win32.WinUser.MONITORINFOEX;
import com.sun.jna.platform.win32.WinUser.MONITORENUMPROC;
import com.sun.jna.platform.win32.Dxva2;
import com.sun.jna.platform.win32.WinUser;
import com.sun.jna.platform.win32.HighLevelMonitorConfigurationAPI.MC_COLOR_TEMPERATURE;
import com.sun.jna.platform.win32.WinNT.HANDLE;
import com.sun.jna.platform.win32.WinNT.HANDLEByReference;
import com.sun.jna.platform.win32.WinDef.POINT;
import com.sun.jna.platform.win32.PhysicalMonitorEnumerationAPI.PHYSICAL_MONITOR;
import com.sun.jna.Pointer;

class ChangeColorTemp {
    public static class MonitorEnumProc implements MONITORENUMPROC {
        public HMONITOR hMonitor = null;
        @Override public int apply(HMONITOR monitor, HDC hdcMonitor, WinDef.RECT info, LPARAM data) {   
            if ((WinUser.MONITORINFOF_PRIMARY) != 0) {
                hMonitor = monitor;
                return 0;
            }
            return 1;
        }
    }

    public static void main(String[] args) {
        //HMONITOR myhMonitor = User32.INSTANCE.MonitorFromWindow(User32.INSTANCE.GetForegroundWindow(), WinUser.MONITOR_DEFAULTTOPRIMARY);
        System.out.println("helo");
        int myvariable = 0;
        //POINT.ByValue pt = new POINT.ByValue(0, 0); 
        //int dwFlags = User32.MONITOR_DEFAULTTONEAREST;
        //HMONITOR hMonitor = User32.INSTANCE.MonitorFromPoint(pt, dwFlags);
        MonitorEnumProc callback = new MonitorEnumProc();
        User32.INSTANCE.EnumDisplayMonitors(null, null, callback, null);
        HMONITOR hMonitor = callback.hMonitor;
        System.out.println(hMonitor);
        // User32.INSTANCE.EnumDisplayMonitors(null, null, new MONITORENUMPROC() {
        //     @Override public int apply(HMONITOR hMonitor, HDC hdcMonitor, WinDef.RECT rect, LPARAM data) {
        //         System.out.println("HMONITOR of the main monitor: " + hMonitor);
        //         return 1;
        //     }
        // }, null);


        PHYSICAL_MONITOR[] physicalMonitors = new PHYSICAL_MONITOR[1];
        BOOL result = Dxva2.INSTANCE.GetPhysicalMonitorsFromHMONITOR(hMonitor, 1, physicalMonitors);
        System.out.println(result);
        System.out.println(physicalMonitors[0]);
        System.out.println(physicalMonitors[0].hPhysicalMonitor);
        Dxva2.INSTANCE.SetMonitorColorTemperature(physicalMonitors[0].hPhysicalMonitor, MC_COLOR_TEMPERATURE.MC_COLOR_TEMPERATURE_4000K);
        
    }
}



