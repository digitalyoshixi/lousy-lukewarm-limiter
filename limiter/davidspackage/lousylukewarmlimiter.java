package davidspackage;

// useful libraries
import java.util.Scanner; // input
import java.io.FileWriter;   // Import the FileWriter class
import java.io.FileInputStream; // reading file
import java.io.File; // file object
import java.time.Instant; // current time
import java.util.Arrays; // advanced lists
import java.util.List; // advanced lists
import java.util.Properties; // opening config file
// exceptions
import java.io.FileNotFoundException; // cannot find file
import java.io.IOException; // filo cannot write/read
// key input
import com.github.kwhat.jnativehook.GlobalScreen; // global keys
import com.github.kwhat.jnativehook.NativeHookException; // exception to cannot hook

class Main{
    
    // ---------------------------------------------------------------
    // ------------------- FUNCTIONS --------------------------------
    // ---------------------------------------------------------------

    // my DRY function. just sends a command to the command line
    public static void commandtoss(String command) throws IOException{
        Process powerShellProcess = Runtime.getRuntime().exec(command); // powershell runs a command
        powerShellProcess.getOutputStream().close(); // close powershell
    }

    // check if a array is a subset of another array
    public static boolean subsettest(List<String> arrlist, List<String> array){
        int arrlen = array.size(); // # of elements in array
        int counter = 0;
        // iterate through every key in keybind constant array
        for (String i : array){
            for (String v : arrlist){ // check every currently pressed key list for if equals to any of the smaller constant array keys
                if (v.equals(i)){ // we found a match
                    counter++; // increase match counter
                }
            }
        }
        if (counter == arrlen){ // is a subset. every key from the constant keybind array is inside the currently pressed keys array
            return true; 
        }
        else { // not a subset
            return false;
        }
    }

    public static int getlocalred(int redmin, int redmax, Float longitude){ 
        // grab the time of day from longitude and then return a color temperature accorind to that metric
        
        // grab UTC time
        Float timeoffset = longitude*24/360;
        String utc = Instant.now().toString();
        // utc to integers
        int utchour = Integer.parseInt(utc.substring(11, 13));
        int utcmin = Integer.parseInt(utc.substring(14, 16));
        int utcsec = Integer.parseInt(utc.substring(17, 19));

        // find timescale(x variable)
        Float localsecondstime = (utchour+timeoffset)*3600 + utcmin*60+utcsec; // local utc 24 hour current time in seconds
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
        // utc to ints
        int utchour = Integer.parseInt(utc.substring(11, 13));
        int utcmin = Integer.parseInt(utc.substring(14, 16));
        int utcsec = Integer.parseInt(utc.substring(17, 19));

        // find timescale(x variable)
        Float localsecondstime = (utchour+timeoffset)*3600 + utcmin*60+utcsec; // local utc 24 hour current time in seconds
        Float timescale = localsecondstime/86400; // 86400 seconds in a day. divided by local current time seconds
        
        // cosine graph to find color temperature
        Float amplitude = ((float)brightmax-(float)brightmin)/2;
        Float shift = (float)brightmax - amplitude;
        // color temperature
        int brightness = (int)Math.round(amplitude*Math.cos(2*3.1415*timescale)+shift);

        return brightness;
    }



    // |||||||||||||||||| ======================================================= |||||||||||||
    // |||||||||||||||||| ================ MAIN FUNCTION ======================== |||||||||||||
    // |||||||||||||||||| ======================================================= |||||||||||||
	public static void main(String[] args) throws InterruptedException, IOException{
        // load in the key press tracker
        try {
			GlobalScreen.registerNativeHook(); // register our hook
		}
		catch (NativeHookException ex) {
			System.err.println("There was a problem registering the native hook.");
			System.err.println(ex.getMessage()); // print error
			System.exit(1); // exit. no use, if we cannot keybinds
		}
        GlobalKeyListener ourglobalkey = new GlobalKeyListener(); // instantiate
		GlobalScreen.addNativeKeyListener(ourglobalkey); // add key listener. it can listen now
        
        // ------------------ CONSTANTS --------------------
        // mode changes
        boolean locationbased = false; // if program effects are based off locaiton or not
        List<String> currkey = ourglobalkey.currkeys; // current keys pressed down

        String pwd = System.getProperty("user.dir"); // current working directory
        
        
        // our config variables
        // automatic variables
        int REDSHIFTMIN = 3300; // junk value
        int REDSHIFTCAP = 5500; // junk value
        int BRIGHTNESSMIN = 0; // junk value
        int BRIGHTNESSCAP = 40; // junk value
        // no automatic variables. The settings I like
        int NOAUTORED = 4500; // default noautored
        int NOAUTOBRIGHT = 5; // defualt noautobright
        Float LONGITUDE = null; // default strawmen values
        // logfile
        String LOGFILE = ""; // filename for log file. relative name, not file location. default is "" to check if its existance
        String[][] metricslist = new String[100][3]; // log file matrix
        List<String> REDSHIFTBIND = Arrays.asList("Alt","Shift","N"); // default keybind
        List<String> AUTOBRIGHTBIND = Arrays.asList("Alt","Shift","M"); // default keybind
        int UPDATECOUNTER = 200; // every 5 seconds.

        // ----------------- NON-CONSTANTS ----------------
        // mode changes
        boolean canchangemode = true; // debounce for keybinds. must release all keys before key switch
        boolean redshiftstate = false; // redshift state
        boolean autobrightstate = false; // brightness state
        int redhue = NOAUTORED; // junk value
        int brightness = NOAUTOBRIGHT; // junk value
        int currentcounter = 0; // counter for updatecounter
        // logging
        int logcounter = 0; // alternate to counter. for checkinf if hour has passed
        int logline = 0; // to write to a current line of the file.
        // ================== CONFIG FILE READING ========
        String optionfilepath = ""; // file path of config file
        // fetch the config file path from command line
        if (args.length > 0){ // if we have arguments:
            // search for argument flag '-config'
            for (int i = 0; i < args.length; i++){
                if ((args[i].equals("-config")) && (i < (args.length-1))){ // if -config exists, and there is an parameter after it
                    optionfilepath = args[i+1]; // filepath is the argument after
                }
            }

        }
        // read from config file
        Properties prop = new Properties(); // file format for config file
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
            NOAUTORED =Integer.parseInt(prop.getProperty("NOAUTORED"));
            NOAUTOBRIGHT = Integer.parseInt(prop.getProperty("NOAUTOBRIGHT"));
            // log file
            LOGFILE = prop.getProperty("LOGFILE");
        } catch (Exception ex) {
            System.out.println("config file not found"); // FileNotFoundException catch is optional and can be collapsed
        } 

        // LOG FILE
        // grab the log file if there is any.
        try {
            File logfile = new File(LOGFILE); // from the filemake variable, grab logfile as file
            Scanner myreader = new Scanner(logfile); // scanner to read logfile line by line
            // populate our metricslist with the log file info
            while (myreader.hasNextLine()) { // every line loop
                String data = myreader.nextLine(); // data this line
                String[] datalist = data.split(","); // split via delimiter
                if (datalist.length == 3){ // if there are 3 datas(sometimes its bugged if user edits file)
                    metricslist[logline] = datalist; // matrics line is te current row
                }
                else{
                    // user edited it, make tis one equal to null. Redo the metrics list from above to now.
                    metricslist[logline][0] = null;
                    metricslist[logline][1] = null;
                    metricslist[logline][2] = null;
                }
                logline++; // for writing. write to the most recent new line. One that is no edited hopefully
            }
            myreader.close(); // do gooder
        }
        catch (Exception  e){ // failed to find log file, or dubious error.
            System.out.println("No log file provided. Making a new one");
            File logfile = new File("logfile.txt"); // file object
            logfile.createNewFile(); // amke the log file
            LOGFILE = "logfile.txt";  // redeclare logfile name
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
        if (LONGITUDE != null){ // if they provided longitude
            System.out.print("Would you like to enable location-based auto adjustments?(Y/N) ");
            // ask user
            Scanner input = new Scanner(System.in);
            String userinput = input.nextLine(); // user inputs
            while (!userinput.equals("Y") && !userinput.equals("N")) { // keep going until user inputs Y or N
                System.out.print("Retype(Y/N) ");
                userinput = input.nextLine(); // grab input again
            }
            input.close(); // good samaratan!
            // adjust their location based choices
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
        for (int i = 0; i < 4; i++){ // allow user to view option with a cool animation
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
            
            // mode check
            if (currkey.size() == 0){ // if no key is being pressed
                canchangemode = true; // they are allowed to change the mode now.
            }

            // keybind checks
            if (canchangemode) {
                // redshift toggle
                if (subsettest(currkey, REDSHIFTBIND)){
                    canchangemode = false; // debounce
                    redshiftstate = !redshiftstate; // flip state
                    if (redshiftstate == true){
                        System.out.println("redshift on");
                        if (!locationbased){
                            // send non-location based command.
                            String command = String.format("powershell.exe & %s\\redshift\\redshift.exe -O %d", pwd, NOAUTORED);
                            commandtoss(command);
                        }
                    }
                    else{
                        // reset redhisft. just like 6000 color temp
                        System.out.println("redshift off");
                        String command = String.format("powershell.exe & %s\\redshift\\redshift.exe -x", pwd);
                        commandtoss(command);

                    }
                    
                }
                // autobright toggle
                else if (subsettest(currkey, AUTOBRIGHTBIND)){
                    canchangemode = false; // debounce
                    autobrightstate = !autobrightstate; // flip state
                    if (autobrightstate == true){
                        System.out.println("autobright on");
                        if (!locationbased){
                            // adjust to non-location brightness
                            String command = "powershell.exe " + String.format("$brightness = %d;", NOAUTOBRIGHT)
                            + "$delay = 0;"
                            + "$myMonitor = Get-WmiObject -Namespace root\\wmi -Class WmiMonitorBrightnessMethods;"
                            + "$myMonitor.wmisetbrightness($delay, $brightness)";
                            commandtoss(command);
                        }
                    }
                    else {
                        // reset to default brightness
                        System.out.println("autobright off");
                        String command = "powershell.exe " + String.format("$brightness = %d;", NOAUTOBRIGHT+10)
                        + "$delay = 0;"
                        + "$myMonitor = Get-WmiObject -Namespace root\\wmi -Class WmiMonitorBrightnessMethods;"
                        + "$myMonitor.wmisetbrightness($delay, $brightness)";
                        commandtoss(command);

                    }
                    
                }
             
            }
            
            // location based auto changes
            if (currentcounter >= UPDATECOUNTER){
                currentcounter = 0; // reset counter for timer.
                if (locationbased){
                    if (redshiftstate){
                        // gets progressively more red as day goes to night. follows cosine curve
                        redhue = getlocalred(REDSHIFTMIN, REDSHIFTCAP, LONGITUDE);
                        // send the command
                        String command = String.format("powershell.exe %s\\\\redshift\\\\redshift.exe -x | %s\\redshift\\redshift.exe -O %d",pwd, pwd, redhue);
                        commandtoss(command);
                    }
                    if (autobrightstate){
                        // gets progressively more dark as day goes to night
                        brightness = getlocalbright(BRIGHTNESSMIN, BRIGHTNESSCAP, LONGITUDE);
                        // send the command
                        String command = "powershell.exe " + String.format("$brightness = %d;", brightness)
                            + "$delay = 0;"
                            + "$myMonitor = Get-WmiObject -Namespace root\\wmi -Class WmiMonitorBrightnessMethods;"
                            + "$myMonitor.wmisetbrightness($delay, $brightness)";
                            commandtoss(command);
                    }
                }
            }

            // log saving
            if (logcounter >= 72000){ // 72000 is 1 hour
                logcounter = 0;
                if (logline >= 99){ // only store 100 logs at a time.
                    logline = 0;
                }
                // metric list update
                String utc = Instant.now().toString().substring(0,18);
                metricslist[logline][0] = utc;
                metricslist[logline][1] = Integer.toString(redhue);
                metricslist[logline][2] = Integer.toString(brightness);
                logline++;
                // save the list to the file.
                try {
                    FileWriter myWriter = new FileWriter(LOGFILE);
                    String writtenstring = "";
                    // populate the written string
                    boolean breakbool = false;
                    for (String[] row : metricslist){
                        if (breakbool){
                            break;
                        }
                        for (String cell : row){
                            if (cell == null){ // null row, dont continue.
                                breakbool = true; // break for the parent for loop
                                break; // exit this early. parent for loop will break
                            }
                            writtenstring += cell + ","; // delimiter
                        }
                        writtenstring += "\n"; // new line. for next hour
                    }
                    myWriter.write(writtenstring); // write tothe file
                    myWriter.close(); // close the file. good lad
                    } 
                    catch (Exception e) { } // just toss the exception. we dont care what it is, we just dont want to program to break 
            }
            logcounter++; // log counter specific to hours
            currentcounter++; // update our frame counter.
        }
    }
}