import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

// program to allow reshift to be used with java.
class Main {
    
    // my DRY function. just sends a command to the command line
    public static void commandtoss(String command) throws IOException{
        Process powerShellProcess = Runtime.getRuntime().exec(command);
        powerShellProcess.getOutputStream().close();
    }

    // building upon the commandtoss function
    public static void main(String[]args)throws IOException{
        System.out.println("hello? this thing on?");
        String pwd = System.getProperty("user.dir");
        String command = String.format("powershell.exe & %s\\redshift\\redshift.exe -O 3700", pwd);
        commandtoss(command);
    }
}
