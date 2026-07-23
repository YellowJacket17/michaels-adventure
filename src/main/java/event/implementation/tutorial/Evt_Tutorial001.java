package event.implementation.tutorial;

import core.GamePanel;
import event.EventTutorialBase;

/**
 * This class implements post-tutorial event logic for tutorial with ID 1.
 * Note that a tutorial ID of 1 is used for combat tutorials.
 */
public class Evt_Tutorial001 extends EventTutorialBase {

    // CONSTRUCTOR
    public Evt_Tutorial001(GamePanel gp) {
        super(gp);
    }


    // METHOD
    @Override
    public void run() {

        gp.getEventM().cleanupTutorial(2);
        gp.getCombatM().progressCombat();
    }
}
