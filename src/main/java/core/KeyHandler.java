package core;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

/**
 * This class handles the sensing of keyboard input.
 */
public class KeyHandler implements KeyListener {

    // FIELDS
    /**
     * Boolean to track WASD input.
     */
    private boolean wPressed, sPressed, aPressed, dPressed;

    /**
     * Boolean to track number input.
     */
    private boolean onePressed, twoPressed, threePressed;

    /**
     * Boolean to track directional arrow input.
     */
    private boolean upArrowPressed, downArrowPressed, leftArrowPressed, rightArrowPressed;

    /**
     * Boolean to track Enter key input.
     */
    private boolean enterPressed;

    /**
     * Boolean to track Space key input.
     */
    private boolean spacePressed;

    /**
     * Boolean to track Q key input.
     */
    private boolean qPressed;


    // METHODS
    /**
     * This method was inherited from implementation of the KeyListener class and is not used in this program.
     *
     * @param e KeyEvent instance
     */
    @Override
    public void keyTyped(KeyEvent e) {

        // Not used.
    }


    /**
     * Senses when a key is pressed.
     *
     * @param e KeyEvent instance
     */
    @Override
    public void keyPressed(KeyEvent e) {

        int code = e.getKeyCode();                                                                                      // Returns the integer keyCode associated with the key in this event.

        if (code == KeyEvent.VK_W) {                                                                                    // If `W` key is pressed.
            wPressed = true;
        }

        if (code == KeyEvent.VK_A) {                                                                                    // If `A` key is pressed.
            aPressed = true;
        }

        if (code == KeyEvent.VK_S) {                                                                                    // If `S` key is pressed.
            sPressed = true;
        }

        if (code == KeyEvent.VK_D) {                                                                                    // If `D` key is pressed.
            dPressed = true;
        }

        if (code == KeyEvent.VK_ENTER) {                                                                                // If `Enter` key is pressed.
            enterPressed = true;
        }

        if (code == KeyEvent.VK_SPACE) {                                                                                // If `Space` key is pressed.
            spacePressed = true;
        }

        if (code == KeyEvent.VK_Q) {                                                                                    // If `Q` key is pressed.
            qPressed = true;
        }

        if (code == KeyEvent.VK_1) {                                                                                    // If `1` key is pressed.
            onePressed = true;
        }

        if (code == KeyEvent.VK_2) {                                                                                    // If `2` key is pressed.
            twoPressed = true;
        }

        if (code == KeyEvent.VK_3) {                                                                                    // If `3` key is pressed.
            threePressed = true;
        }

        if (code == KeyEvent.VK_UP) {                                                                                   // If `Up Arrow` key is pressed.
            upArrowPressed = true;
        }

        if (code == KeyEvent.VK_DOWN) {                                                                                 // If `Down Arrow` key is pressed.
            downArrowPressed = true;
        }

        if (code == KeyEvent.VK_LEFT) {                                                                                 // If `Left Arrow` key is pressed.
            leftArrowPressed = true;
        }

        if (code == KeyEvent.VK_RIGHT) {                                                                                // If `Right Arrow` key is pressed.
            rightArrowPressed = true;
        }
    }


    /**
     * Senses when a key is released.
     */
    @Override
    public void keyReleased(KeyEvent e) {

        int code = e.getKeyCode();

        if (code == KeyEvent.VK_W) {                                                                                    // If `W` key is released.
            wPressed = false;
        }

        if (code == KeyEvent.VK_A) {                                                                                    // If `A` key is released.
            aPressed = false;
        }

        if (code == KeyEvent.VK_S) {                                                                                    // If `S` key is released.
            sPressed = false;
        }

        if (code == KeyEvent.VK_D) {                                                                                    // If `D` key is released.
            dPressed = false;
        }

        if (code == KeyEvent.VK_ENTER) {                                                                                // If `Enter` key is released.
            enterPressed = false;
        }

        if (code == KeyEvent.VK_SPACE) {                                                                                // If `Space` key is released.
            spacePressed = false;
        }

        if (code == KeyEvent.VK_Q) {                                                                                    // If `Q` key is released.
            qPressed = false;
        }

        if (code == KeyEvent.VK_1) {                                                                                    // If `1` key is released.
            onePressed = false;
        }

        if (code == KeyEvent.VK_2) {                                                                                    // If `2` key is released.
            twoPressed = false;
        }

        if (code == KeyEvent.VK_3) {                                                                                    // If `3` key is released.
            threePressed = false;
        }

        if (code == KeyEvent.VK_UP) {                                                                                   // If `Up Arrow` key is released.
            upArrowPressed = false;
        }

        if (code == KeyEvent.VK_DOWN) {                                                                                 // If `Down Arrow` key is released.
            downArrowPressed = false;
        }

        if (code == KeyEvent.VK_LEFT) {                                                                                 // If `Left Arrow` key is released.
            leftArrowPressed = false;
        }

        if (code == KeyEvent.VK_RIGHT) {                                                                                // If `Right Arrow` key is released.
            rightArrowPressed = false;
        }
    }


    // GETTERS
    public boolean iswPressed() {
        return wPressed;
    }

    public boolean issPressed() {
        return sPressed;
    }

    public boolean isaPressed() {
        return aPressed;
    }

    public boolean isdPressed() {
        return dPressed;
    }

    public boolean isEnterPressed() {
        return enterPressed;
    }

    public boolean isSpacePressed() {
        return spacePressed;
    }

    public boolean isqPressed() {
        return qPressed;
    }

    public boolean isOnePressed() {
        return onePressed;
    }

    public boolean isTwoPressed() {
        return twoPressed;
    }

    public boolean isThreePressed() {
        return threePressed;
    }

    public boolean isUpArrowPressed() {
        return upArrowPressed;
    }

    public boolean isDownArrowPressed() {
        return downArrowPressed;
    }

    public boolean isLeftArrowPressed() {
        return leftArrowPressed;
    }

    public boolean isRightArrowPressed() {
        return rightArrowPressed;
    }
}
