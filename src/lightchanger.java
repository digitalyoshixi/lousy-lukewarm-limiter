import java.util.ArrayList;
import java.util.List;

import com.sun.jna.Native;
import com.sun.jna.Structure;
import com.sun.jna.platform.win32.Advapi32Util;
import com.sun.jna.platform.win32.WinReg.HKEY;

class Main {
    public static void main(String[] args) {
        // Set brightness to 80% (value range: 0-100)
        Advapi32Util.registrySetIntValue(
            Advapi32Util.HKEY.HKEY_LOCAL_MACHINE,
            "SOFTWARE\\Microsoft\\Windows\\CurrentVersion\\Policies\\System",
            "EnableBrightnessPolicy",
            80
        );

        System.out.println("Brightness set to 80%.");
    }
}
