package davidspackage;

import java.util.Scanner;
import java.util.concurrent.CompletableFuture;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.Instant;

import com.github.kwhat.jnativehook.GlobalScreen;
import com.github.kwhat.jnativehook.NativeHookException;
import com.github.kwhat.jnativehook.keyboard.NativeKeyEvent;
import com.github.kwhat.jnativehook.keyboard.NativeKeyListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

// Program to manually adjust the brightness, redness and monitor screen time. save the

// class RedSync implements Runnable{
//      public void run() {
//         System.out.println("Hello from a thread!");

//     }
// }

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

    public static int getlocalred(int redmin, int redmax, Float longitude){ 
        // grab the time of day from longitude and then return a color temperature accorind to that metric
        
        // grab UTC time
        Float timeoffset = longitude*24/360;
        String utc = Instant.now().toString();
        int utchour = Integer.parseInt(utc.substring(11, 13));
        int utcmin = Integer.parseInt(utc.substring(14, 16));
        int utcsec = Integer.parseInt(utc.substring(17, 19));

        // find timescale(x variable)
        Float localsecondstime = (utchour-timeoffset)*3600 + utcmin*60+utcsec;
        Float timescale = localsecondstime/86400; // 86400 seconds in a day. divided by local current time seconds
        
        // cosine graph to find color temperature
        Float amplitude = ((float)redmax-(float)redmin)/2;
        Float shift = (float)redmax - amplitude;
        // color temperature
        int colortemp = (int)Math.round(amplitude*Math.cos(2*3.1415*timescale)+shift);

        return colortemp;
    }

    public static int getlocalbright(int brightmin, int brightmax, Float longitude){ 
        // grab the time of day from longitude and then return a color temperature accorind to that metric
        
        // grab UTC time
        Float timeoffset = longitude*24/360;
        String utc = Instant.now().toString();
        int utchour = Integer.parseInt(utc.substring(11, 13));
        int utcmin = Integer.parseInt(utc.substring(14, 16));
        int utcsec = Integer.parseInt(utc.substring(17, 19));

        // find timescale(x variable)
        Float localsecondstime = (utchour-timeoffset)*3600 + utcmin*60+utcsec;
        Float timescale = localsecondstime/86400; // 86400 seconds in a day. divided by local current time seconds
        
        // cosine graph to find color temperature
        Float amplitude = ((float)brightmax-(float)brightmin)/2;
        Float shift = (float)brightmax - amplitude;
        // color temperature
        int brightness = (int)Math.round(amplitude*Math.cos(2*3.1415*timescale)+shift);

        return brightness;
    }

    public static void eyebeep(){

    }

    // |||||||||||||||||| ======================================================= |||||||||||||
    // |||||||||||||||||| ================ MAIN FUNCTION ======================== |||||||||||||
    // |||||||||||||||||| ======================================================= |||||||||||||
	public static void main(String[] args) throws InterruptedException, IOException{
        // load in the key press tracker
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
        boolean locationbased = false;
        List<String> currkey = ourglobalkey.currkeys; // current keys pressed down
        boolean redshiftstate = false;
        boolean autobrightstate = false;
        // our config variables
        // automatic variables
        int REDSHIFTMIN = 3300;
        int REDSHIFTCAP = 5500;
        int BRIGHTNESSMIN = 0;
        int BRIGHTNESSCAP = 40;
        // no automatic variables. The settings I like
        int NOAUTORED = 4500;
        int NOAUTOBRIGHT = 5;
        int BREAKDURATION;
        Float LONGITUDE = null; // default strawmen values
        
        List<String> REDSHIFTBIND = Arrays.asList("Alt","Shift","N"); // default keybind
        List<String> AUTOBRIGHTBIND = Arrays.asList("Alt","Shift","M"); // default keybind
        int UPDATECOUNTER = 200; // every 5 seconds.

        // ----------------- NON-CONSTANTS ----------------
        
        int currentcounter = 0; // counter for updatecounter

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
            // set the variable constants to config file
            REDSHIFTBIND= Arrays.asList(prop.getProperty("REDSHIFTBIND").substring(1, prop.getProperty("REDSHIFTBIND").length()-1).split(","));
            REDSHIFTMIN= Integer.parseInt(prop.getProperty("REDSHIFTMIN"));
            REDSHIFTCAP= Integer.parseInt(prop.getProperty("REDSHIFTCAP"));
            AUTOBRIGHTBIND= Arrays.asList(prop.getProperty("AUTOBRIGHTBIND").substring(1, prop.getProperty("AUTOBRIGHTBIND").length()-1).split(","));
            BRIGHTNESSMIN= Integer.parseInt(prop.getProperty("BRIGHTNESSMIN"));
            BRIGHTNESSCAP= Integer.parseInt(prop.getProperty("BRIGHTNESSCAP"));
            LONGITUDE = Float.parseFloat(prop.getProperty("LONGITUDE"));
            UPDATECOUNTER = (int)Math.round(Float.parseFloat(prop.getProperty("UPDATECOUNTER"))/0.05);
            BREAKDURATION = Integer.parseInt(prop.getProperty("BREAKDURATION"));
            System.out.println(BREAKDURATION);
            NOAUTORED =Integer.parseInt(prop.getProperty("NOAUTORED"));
            NOAUTOBRIGHT = Integer.parseInt(prop.getProperty("NOAUTOBRIGHT"));

            // run syncronous functions
            

        

        } catch (FileNotFoundException ex) {
            System.out.println("config file not found"); // FileNotFoundException catch is optional and can be collapsed
        } catch (IOException ex) {
            System.out.println("IO not resolved");
        }
        
        // --------------------------------------------------
        // ----------------- USER INTRODUCTION --------------
        // --------------------------------------------------
        System.out.println("=========================================================");
        System.out.println("------------------ LOUSY LUKEWARM LIMITER ---------------");
        System.out.println("=========================================================");
        System.out.println();
        System.out.println("This software is designed to protect your eyes from blue light");
        System.out.println("through location-based adjustments of your computer's color temperature and gamma");
        System.out.println("aswell as providing a built in eye timer, how handy!");
        System.out.println();
        if (LONGITUDE != null){ // another hacky solution. cann
            System.out.print("Would you like to enable location-based auto adjustments?(Y/N) ");
            // ask user
            Scanner input = new Scanner(System.in);
            String userinput = input.nextLine();
            while (!userinput.equals("Y") && !userinput.equals("N")) {
                System.out.print("Retype(Y/N) ");
                userinput = input.nextLine();
            }
            input.close(); // good samaratan!
            if (userinput.equals("Y")){
                locationbased = true;
            }
            else{
                locationbased = false;
            }
            
        }
        else {
            System.out.println("It seems you have not provided a configuration file. Software will be ran with defaults");
        }
        for (int i = 0; i < 4; i++){ // allow user to view option with a cool gui
            Thread.sleep(800); 
            System.out.print(".");
        }
        
        
        
        // clear screen
        System.out.print("\033[H\033[2J");  
        System.out.flush();  

        
        // final guide message
        System.out.println("Software is ready for you to use now.");
        System.out.println("Your Redshift Toggle key is: " + REDSHIFTBIND);
        System.out.println("Your Autobright Toggle key is: " + AUTOBRIGHTBIND);
        System.out.println();

        // ------------------------------------------------
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
                        if (!locationbased){
                            String pwd = System.getProperty("user.dir");
                            String command = String.format("powershell.exe & %s\\redshift\\redshift.exe -O %d", pwd, NOAUTORED);
                            commandtoss(command);
                        }
                    }
                    else{
                        System.out.println("redshift off");
                        if (!locationbased){
                            String pwd = System.getProperty("user.dir");
                            String command = String.format("powershell.exe & %s\\redshift\\redshift.exe -x", pwd);
                            commandtoss(command);
                        }
                    }
                    
                }
                // autobright toggle
                else if (subsettest(currkey, AUTOBRIGHTBIND)){
                    canchangemode = false;
                    autobrightstate = !autobrightstate;
                    if (autobrightstate == true){
                        System.out.println("autobright on");
                        if (!locationbased){
                            String command = "powershell.exe " + String.format("$brightness = %d;", NOAUTOBRIGHT)
                            + "$delay = 0;"
                            + "$myMonitor = Get-WmiObject -Namespace root\\wmi -Class WmiMonitorBrightnessMethods;"
                            + "$myMonitor.wmisetbrightness($delay, $brightness)";
                            commandtoss(command);
                        }
                    }
                    else {
                        System.out.println("autobright off");
                        if (!locationbased){
                            
                            String command = "powershell.exe " + String.format("$brightness = %d;", NOAUTOBRIGHT)
                            + "$delay = 0;"
                            + "$myMonitor = Get-WmiObject -Namespace root\\wmi -Class WmiMonitorBrightnessMethods;"
                            + "$myMonitor.wmisetbrightness($delay, $brightness)";
                            commandtoss(command);
                        }
                    }
                    
                }
             
            }
            
            // location based auto changes
            if (currentcounter >= UPDATECOUNTER){
                currentcounter = 0;
            
                if (locationbased){
                    if (redshiftstate){
                        // gets progressively more red as day goes to night. follows cosine curve
                        int redhue = getlocalred(REDSHIFTMIN, REDSHIFTCAP, LONGITUDE);
                        System.out.println(redhue);
                        String pwd = System.getProperty("user.dir");
                        String command = String.format("powershell.exe & %s\\redshift\\redshift.exe -O %d", pwd, redhue);
                        commandtoss(command);
                    }
                    if (autobrightstate){
                        // gets progressively more dark as day goes to night
                        int brightness = getlocalbright(BRIGHTNESSMIN, BRIGHTNESSCAP, LONGITUDE);
                        System.out.println(brightness);
                        String command = "powershell.exe " + String.format("$brightness = %d;", brightness)
                            + "$delay = 0;"
                            + "$myMonitor = Get-WmiObject -Namespace root\\wmi -Class WmiMonitorBrightnessMethods;"
                            + "$myMonitor.wmisetbrightness($delay, $brightness)";
                            commandtoss(command);
                    }

                }
            }
        currentcounter++; // update our frame counter.
        }
        
        
    }
}