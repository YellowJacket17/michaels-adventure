package combat.implementation.action;

import combat.ActionBase;
import core.GamePanel;
import miscellaneous.GameState;

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


    // CONSTRUCTOR
    public Act_ReadMessage(GamePanel gp, String message, boolean interactive) {
        super(gp);
        this.message = message;
        this.interactive = interactive;
    }


    // METHODS
    @Override
    public void run() {

        displayMessage();
    }


    /**
     * Stages and initiates a single message to be read to the dialogue screen during combat.
     * The game state is set to dialogue.
     */
    public void displayMessage() {

        gp.setGameState(GameState.DIALOGUE);

        if (interactive) {

            gp.getDialogueR().initiateInteractiveCombatMessage(message);
        } else {

            gp.getDialogueR().initiateNoninteractiveCombatMessage(message);
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
