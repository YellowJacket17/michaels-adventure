package event.implementation.tutorial;

import core.GamePanel;
import event.EventTutorialBase;

/**
 * This class implements post-tutorial event logic for tutorial with ID 12.
 * Note that a tutorial ID of 12 is used for the party management tutorial (page 3).
 */
public class Evt_Tutorial012 extends EventTutorialBase {

    // CONSTRUCTOR
    public Evt_Tutorial012(GamePanel gp) {
        super(gp);
    }


    // METHOD
    @Override
    public void run() {

        gp.getPartyS().addEntityToParty(11, true);
        gp.getPartyS().addEntityToParty(12, true);
        gp.getEventM().cleanupTutorial(1);
    }
}
