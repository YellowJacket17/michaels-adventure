package combat.implementation.action;

import combat.ActionBase;
import core.GamePanel;

/**
 * This class defines a combat action (read a message).
 */
public class Act_ReadMessage extends ActionBase {

    // FIELDS
    /**
     * Text to be read.
     */
    private final String message;

    /**
     * Boolean indicating whether player input is required to progress this message (true) or not (false).
     */
    private final boolean interactive;

    /**
     * Boolean indicating whether all visible text will be printed to the screen character by character (true) or
     * whether it will be printed all at once (false).
     */
    private final boolean charByChar;


    // CONSTRUCTOR
    public Act_ReadMessage(GamePanel gp, String message, boolean interactive, boolean charByChar) {
        super(gp);
        this.message = message;
        this.interactive = interactive;
        this.charByChar = charByChar;
    }


    // METHODS
    @Override
    public void run() {

        displayMessage();
    }


    /**
     * Stages and initiates a single message to be read to the dialogue screen during combat.
     */
    private void displayMessage() {

        if (interactive) {

            gp.getDialogueR().initiateInteractiveCombatMessage(message, charByChar);
        } else {

            gp.getDialogueR().initiateNoninteractiveCombatMessage(message, charByChar);
        }
    }


    // GETTERS
    public String getMessage() {
        return message;
    }

    public boolean isInteractive() {
        return interactive;
    }
}
