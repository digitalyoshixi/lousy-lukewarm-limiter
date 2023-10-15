package davidspackage;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import com.github.kwhat.jnativehook.GlobalScreen;
import com.github.kwhat.jnativehook.NativeHookException;
import com.github.kwhat.jnativehook.keyboard.NativeKeyEvent;
import com.github.kwhat.jnativehook.keyboard.NativeKeyListener;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

// Program to manually adjust the brightness, redness and monitor screen time. save the

class Main{
    
    
    // ---------------------------------------------------------------
    // ------------------- FUNCTIONS --------------------------------
    // ---------------------------------------------------------------

    // my DRY function. just sends a command to the command line
    public static void commandtoss(String command) throws IOException{
        Process powerShellProcess = Runtime.getRuntime().exec(command);
        powerShellProcess.getOutputStream().close();
    }

    // check if a array is a subset of another array
    public static boolean subsettest(List<String> arrlist, String[] array){
        int arrlen = array.length;
        int counter = 0;
        for (String i : array){
            for (String v : arrlist){
                if (v.equals(i)){
                    counter++;
                }
            }
        }
        if (counter == arrlen){
            return true;
        }
        else {
            return false;
        }
    }

	public static void main(String[] args) throws InterruptedException, IOException{

        // load in the key press tracker
        System.out.println("hello");
        try {
			GlobalScreen.registerNativeHook();
		}
		catch (NativeHookException ex) {
			System.err.println("There was a problem registering the native hook.");
			System.err.println(ex.getMessage());

			System.exit(1);
		}
        GlobalKeyListener ourglobalkey = new GlobalKeyListener();
		GlobalScreen.addNativeKeyListener(ourglobalkey);
        
        // ================== CONFIG FILE CONSTANTS ========


        // ------------------ CONSTANTS --------------------
        // mode changes
        boolean canchangemode = true;
        List<String> currkey = ourglobalkey.currkeys; // current keys pressed down
        String[] redshifton = {"Alt","Shift","N"};
        String[] redshiftoff = {"Alt","Shift","M"};
        String[] autobrighton = {"Alt","Shift","J"};
        String[] autobrightoff = {"Alt","Shift","K"};
        
        // ----------------- NON-CONSTANTS ----------------
        int brightness = 10;
        
        // -------------------------------------------------
        // ---------------- MAIN PROGRAM LOOP -------------
        // ------------------------------------------------
        while (true){
            Thread.sleep(50); // check every 50ms
            //System.out.println(currkey);
            
            
            // mode check
            if (currkey.size() == 0){
                canchangemode = true;
            }

            // keybind checks
            if (canchangemode) {
                // redshift on
                if (subsettest(currkey, redshifton)){
                    System.out.println("redshift on");
                    String pwd = System.getProperty("user.dir");
                    String command = String.format("powershell.exe & %s\\redshift\\redshift.exe -O 3700", pwd);
                    commandtoss(command);
                    canchangemode = false;
                }
                // redshift off
                else if (subsettest(currkey, redshiftoff)){
                    System.out.println("redshift off");
                    String pwd = System.getProperty("user.dir");
                    String command = String.format("powershell.exe & %s\\redshift\\redshift.exe -x", pwd);
                    commandtoss(command);
                    canchangemode = false;
                }
                // autobright on
                else if (subsettest(currkey, autobrighton)){
                    System.out.println("autobright on");
                    brightness = 50;
                    String command = "powershell.exe " + String.format("$brightness = %d;", brightness)
                + "$delay = 0;"
                + "$myMonitor = Get-WmiObject -Namespace root\\wmi -Class WmiMonitorBrightnessMethods;"
                + "$myMonitor.wmisetbrightness($delay, $brightness)";
                    commandtoss(command);
                    canchangemode = false;
                }
                // autobright off
                else if (subsettest(currkey, autobrightoff)){
                    System.out.println("autobright off");
                    brightness = 10;
                    String command = "powershell.exe " + String.format("$brightness = %d;", brightness)
                + "$delay = 0;"
                + "$myMonitor = Get-WmiObject -Namespace root\\wmi -Class WmiMonitorBrightnessMethods;"
                + "$myMonitor.wmisetbrightness($delay, $brightness)";
                    commandtoss(command);
                    canchangemode = false;
                }
            }
        }
	}
}