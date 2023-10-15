package davidspackage;
import com.github.kwhat.jnativehook.GlobalScreen;
import com.github.kwhat.jnativehook.NativeHookException;
import com.github.kwhat.jnativehook.keyboard.NativeKeyEvent;
import com.github.kwhat.jnativehook.keyboard.NativeKeyListener;
import java.util.ArrayList;
import java.util.List;

class GlobalKeyListener implements NativeKeyListener {
	// my bag of keys
    List<String> currkeys = new ArrayList<>();

	// key pressed event
	public void nativeKeyPressed(NativeKeyEvent e) {
		//System.out.println("Key Pressed: " + NativeKeyEvent.getKeyText(e.getKeyCode()));
		String curentkey = NativeKeyEvent.getKeyText(e.getKeyCode()); // key code pressed
		if ((currkeys.contains(curentkey)) == false) { // current key pressed not in keys list
    		currkeys.add(curentkey);  // add to bag of keys
		}

	}
	// key released event
	public void nativeKeyReleased(NativeKeyEvent e) {
		//System.out.println("Key Released: " + NativeKeyEvent.getKeyText(e.getKeyCode()));
		String curentkey = NativeKeyEvent.getKeyText(e.getKeyCode()); // key code released
		if (currkeys.contains(curentkey)) { // current key pressed not in keys list
    		// remove from bag of keys
			int curentkeyindex = currkeys.indexOf(curentkey);
			currkeys.remove(curentkeyindex);
		}
	}

	public static void main(String[] args) {
		// start the listener
		try { // try to register this new hook to override the old hook.
			GlobalScreen.registerNativeHook();
		}
		catch (NativeHookException ex) { // if the above exception occurs, there is an error
			System.err.println("There was a problem registering the native hook.");
			System.err.println(ex.getMessage());

			System.exit(1);
		}
		// enable listener
		GlobalScreen.addNativeKeyListener(new GlobalKeyListener());
	}
}