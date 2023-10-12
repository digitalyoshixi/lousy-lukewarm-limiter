import com.sun.jna.Library;
import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinReg;
import com.sun.jna.Native;
import com.sun.jna.Platform;
import com.sun.jna.platform.win32.WinDef.BOOL;
import com.sun.jna.platform.win32.WinDef.HDC;
import com.sun.jna.platform.win32.WinUser.HMONITOR;
import com.sun.jna.platform.win32.Dxva2;
import com.sun.jna.platform.win32.WinUser;
import com.sun.jna.platform.win32.HighLevelMonitorConfigurationAPI.MC_COLOR_TEMPERATURE;
import com.sun.jna.platform.win32.WinNT.HANDLE;
import com.sun.jna.platform.win32.WinNT.HANDLEByReference;
import com.sun.jna.platform.win32.WinDef.POINT.ByValue;
import com.sun.jna.platform.win32.PhysicalMonitorEnumerationAPI.PHYSICAL_MONITOR;
import com.sun.jna.Pointer;

class ChangeColorTemp {
    public static void main(String[] args) {
        System.out.println("helo");
        ByValue pt = new ByValue(); 
        pt.x = 0;
        pt.y = 0;
        int dwFlags = User32.MONITOR_DEFAULTTONEAREST;
        HMONITOR hMonitor = User32.INSTANCE.MonitorFromPoint(pt, dwFlags);
        HANDLE hMonitorHandle = new HANDLE(new Pointer(hMonitor));
        
        Dxva2.INSTANCE.SetMonitorColorTemperature(hMonitorHandle, MC_COLOR_TEMPERATURE.MC_COLOR_TEMPERATURE_4000K);
        
    }
}



