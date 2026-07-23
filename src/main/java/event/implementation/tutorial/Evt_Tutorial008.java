package event.implementation.tutorial;

import core.GamePanel;
import event.EventTutorialBase;

/**
 * This class implements post-tutorial event logic for tutorial with ID 8.
 * Note that a tutorial ID of 8 is used for the item tutorial (page 1).
 */
public class Evt_Tutorial008 extends EventTutorialBase {

    // CONSTRUCTOR
    public Evt_Tutorial008(GamePanel gp) {
        super(gp);
    }


    // METHOD
    @Override
    public void run() {

        gp.getEventM().cleanupTutorial(1);
    }
}
