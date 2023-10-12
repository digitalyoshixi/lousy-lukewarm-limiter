import com.sun.jna.platform.win32.Advapi32Util;
import com.sun.jna.platform.win32.WinReg;

class ColorTemperature {
    public static void main(String[] args) {
        String key = "SOFTWARE\\Microsoft\\Windows\\CurrentVersion\\CloudStore\\Store\\DefaultAccount\\Current\\default$windows.data.bluelightreduction.bluelightreductionstate\\windows.data.bluelightreduction.bluelightreductionstate";
        System.out.println(key);
        String value = "Data";
        int data = Advapi32Util.registryGetIntValue(WinReg.HKEY_CURRENT_USER, key, value);
        int red = (data & 0xFF0000) >> 16;
        int green = (data & 0xFF00) >> 8;
        int blue = data & 0xFF;
        int newRed = red + 10;
        int newGreen = green - 5;
        int newBlue = blue - 5;
        int newData = (newRed << 16) | (newGreen << 8) | newBlue;
        Advapi32Util.registrySetIntValue(WinReg.HKEY_CURRENT_USER, key, value, newData);
    }
}
