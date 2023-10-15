package davidspackage;
import com.github.kwhat.jnativehook.GlobalScreen;
import com.github.kwhat.jnativehook.NativeHookException;
import com.github.kwhat.jnativehook.keyboard.NativeKeyEvent;
import com.github.kwhat.jnativehook.keyboard.NativeKeyListener;
import java.util.ArrayList;
import java.util.List;

class GlobalKeyListener implements NativeKeyListener {
    List<String> currkeys = new ArrayList<>();

	public void nativeKeyPressed(NativeKeyEvent e) {
		//System.out.println("Key Pressed: " + NativeKeyEvent.getKeyText(e.getKeyCode()));
		String curentkey = NativeKeyEvent.getKeyText(e.getKeyCode());
		if ((currkeys.contains(curentkey)) == false) { // current key pressed not in keys list
    		currkeys.add(curentkey);  
			//System.out.println(currkeys);
		}
		
		//if (e.getKeyCode() == NativeKeyEvent.VC_ESCAPE) {
        //    		try {
        //        		GlobalScreen.unregisterNativeHook();
        //    		} catch (NativeHookException nativeHookException) {
        //        		nativeHookException.printStackTrace();
        //    		}
        //	}
	}

	public void nativeKeyReleased(NativeKeyEvent e) {
		//System.out.println("Key Released: " + NativeKeyEvent.getKeyText(e.getKeyCode()));
		String curentkey = NativeKeyEvent.getKeyText(e.getKeyCode());
		if (currkeys.contains(curentkey)) { // current key pressed not in keys list
    		int curentkeyindex = currkeys.indexOf(curentkey);
			currkeys.remove(curentkeyindex);
			//System.out.println(currkeys);
		}
		
	}

	//public void nativeKeyTyped(NativeKeyEvent e) {
		//System.out.println("Key Typed: " + e.getKeyText(e.getKeyCode()));
	//}

	public static void main(String[] args) {
		try {
			GlobalScreen.registerNativeHook();
		}
		catch (NativeHookException ex) {
			System.err.println("There was a problem registering the native hook.");
			System.err.println(ex.getMessage());

			System.exit(1);
		}

		GlobalScreen.addNativeKeyListener(new GlobalKeyListener());
	}
}