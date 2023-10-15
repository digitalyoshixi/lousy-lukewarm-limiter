package davidspackage;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import com.github.kwhat.jnativehook.GlobalScreen;
import com.github.kwhat.jnativehook.NativeHookException;
import com.github.kwhat.jnativehook.keyboard.NativeKeyEvent;
import com.github.kwhat.jnativehook.keyboard.NativeKeyListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

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
    public static boolean subsettest(List<String> arrlist, List<String> array){
        int arrlen = array.size();
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
        
       


        // ------------------ CONSTANTS --------------------
        // mode changes
        boolean canchangemode = true;
        List<String> currkey = ourglobalkey.currkeys; // current keys pressed down
        boolean redshiftstate = false;
        boolean autobrightstate = false;
        // our config variables
        int REDSHIFTMIN,REDSHIFTCAP,BRIGHTNESSMIN,BRIGHTNESSCAP;
        String EYESFX;
        List<String> REDSHIFTBIND = Arrays.asList("Alt","Shift","N");
        List<String> AUTOBRIGHTBIND = Arrays.asList("Alt","Shift","M");
        
        // ----------------- NON-CONSTANTS ----------------
        int brightness = 10;

        // ================== CONFIG FILE READING ========
        String optionfilepath = "";
        // fetch the config file path from command line
        if (args.length > 0){ // if we have arguments:
            // search for argument flag '-options'
            for (int i = 0; i < args.length; i++){
                if ((args[i].equals("-config")) && (i < (args.length-1))){
                    optionfilepath = args[i+1];
                }
            }

        }
        
        

        Properties prop = new Properties();
        try (FileInputStream fis = new FileInputStream(optionfilepath)) {
            prop.load(fis);
            // sucessful!
            REDSHIFTBIND= Arrays.asList(prop.getProperty("REDSHIFTBIND").substring(1, prop.getProperty("REDSHIFTBIND").length()-1).split(","));
            REDSHIFTMIN= Integer.parseInt(prop.getProperty("REDSHIFTMIN"));
            REDSHIFTCAP= Integer.parseInt(prop.getProperty("REDSHIFTCAP"));
            AUTOBRIGHTBIND= Arrays.asList(prop.getProperty("AUTOBRIGHTBIND").substring(1, prop.getProperty("AUTOBRIGHTBIND").length()-1).split(","));
            BRIGHTNESSMIN= Integer.parseInt(prop.getProperty("BRIGHTNESSMIN"));
            BRIGHTNESSCAP= Integer.parseInt(prop.getProperty("BRIGHTNESSCAP"));
            EYESFX =prop.getProperty("EYESFX");

            


        } catch (FileNotFoundException ex) {
            System.out.println("config file not found"); // FileNotFoundException catch is optional and can be collapsed
        } catch (IOException ex) {
            System.out.println("IO not resolved");
        }
        
        
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
                // redshift toggle
                if (subsettest(currkey, REDSHIFTBIND)){
                    canchangemode = false;
                    redshiftstate = !redshiftstate;
                    if (redshiftstate == true){
                        System.out.println("redshift on");
                        String pwd = System.getProperty("user.dir");
                        String command = String.format("powershell.exe & %s\\redshift\\redshift.exe -O 3700", pwd);
                        commandtoss(command);
                    }
                    else{
                        System.out.println("redshift off");
                        String pwd = System.getProperty("user.dir");
                        String command = String.format("powershell.exe & %s\\redshift\\redshift.exe -x", pwd);
                        commandtoss(command);
                    }
                    
                }
                
                // autobright toggle
                else if (subsettest(currkey, AUTOBRIGHTBIND)){
                    canchangemode = false;
                    autobrightstate = !autobrightstate;
                    if (autobrightstate == true){
                        System.out.println("autobright on");
                        brightness = 50;
                        String command = "powershell.exe " + String.format("$brightness = %d;", brightness)
                        + "$delay = 0;"
                        + "$myMonitor = Get-WmiObject -Namespace root\\wmi -Class WmiMonitorBrightnessMethods;"
                        + "$myMonitor.wmisetbrightness($delay, $brightness)";
                        commandtoss(command);
                    }
                    else {
                        System.out.println("autobright off");
                        brightness = 10;
                        String command = "powershell.exe " + String.format("$brightness = %d;", brightness)
                        + "$delay = 0;"
                        + "$myMonitor = Get-WmiObject -Namespace root\\wmi -Class WmiMonitorBrightnessMethods;"
                        + "$myMonitor.wmisetbrightness($delay, $brightness)";
                        commandtoss(command);
                    }
                    
                }
             
            }
        }
	}
}