package miscellaneous;

import utility.UtilityTool;

import java.util.HashSet;

import static org.lwjgl.glfw.GLFW.GLFW_PRESS;
import static org.lwjgl.glfw.GLFW.GLFW_RELEASE;

/**
 * This class handles the sensing of keyboard input.
 */
public class KeyListener {

    // FIELDS
    /**
     * Singleton of this class (i.e., only one instance of this class is ever needed).
     */
    private static KeyListener keyListener;

    /**
     * Boolean tracking which keys are currently pressed.
     */
    private final boolean[] keyPressed = new boolean[350];

    /**
     * Set to store unrecognized key errors.
     * If an unrecognized key is detected, the code associated with the key will be added to this set.
     * This prevents a key error from that key being printed to the console again.
     */
    private static HashSet<Integer> keyErrors = new HashSet<>();


    // CONSTRUCTOR
    /**
     * Constructs a KeyListener instance.
     */
    public KeyListener() {}


    // METHODS
    /**
     * Retrieves the KeyListener singleton.
     *
     * @return KeyListener singleton
     */
    public static KeyListener get() {

        if (KeyListener.keyListener == null) {

            KeyListener.keyListener = new KeyListener();
        }
        return KeyListener.keyListener;
    }


    /**
     * Senses when a key is pressed or released.
     *
     * @param window
     * @param key
     * @param scancode
     * @param action
     * @param mods
     */
    public static void keyCallback(long window, int key, int scancode, int action, int mods) {

        try {

            if (action == GLFW_PRESS) {                                                                                 // If key pressed.

                get().keyPressed[key] = true;
            } else if (action == GLFW_RELEASE) {                                                                        // If key released.

                get().keyPressed[key] = false;
            }

        } catch (ArrayIndexOutOfBoundsException e) {

            if (!keyErrors.contains(key)) {
                UtilityTool.logWarning("Unrecognized key with code '"
                        + key
                        + "' was detected.");
                keyErrors.add(key);
            }
        }
    }


    /**
     * Retrieves whether a target key is pressed or not.
     *
     * @param keyCode key code of target key
     * @return whether the target key is pressed (true) or not (false)
     */
    public static boolean isKeyPressed(int keyCode) {

         return get().keyPressed[keyCode];
    }
}
