package event.implementation.tutorial;

import core.GamePanel;
import event.EventTutorialBase;

/**
 * This class implements post-tutorial event logic for tutorial with ID 7.
 * Note that a tutorial ID of 7 is used for the controls tutorial (page 6).
 */
public class Evt_Tutorial007 extends EventTutorialBase {

    // CONSTRUCTOR
    public Evt_Tutorial007(GamePanel gp) {
        super(gp);
    }


    // METHOD
    @Override
    public void run() {

        gp.getEventM().cleanupTutorial(1);
    }
}
