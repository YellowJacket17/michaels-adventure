package event.implementation.tutorial;

import core.GamePanel;
import event.EventTutorialBase;

/**
 * This class implements post-tutorial event logic for tutorial with ID 10.
 * Note that a tutorial ID of 10 is used for the party management tutorial (page 1).
 */
public class Evt_Tutorial010 extends EventTutorialBase {

    // CONSTRUCTOR
    public Evt_Tutorial010(GamePanel gp) {
        super(gp);
    }


    // METHOD
    @Override
    public void run() {

        generateTutorial();
    }


    /**
     * Generates and displays the party management tutorial (page 2).
     */
    private void generateTutorial() {

        String title = "Party Management Tutorial";
        String subtitle = "Combat";
        String content = "You now have more than two party members (excluding Mary)."
                + " The first two members are \"active\" party members."
                + " These members will be on the field during combat."
                + " The remaining members are \"reserve\" party members."
                + " These members will be on call during combat and can be swapped in for active members."
                + " " + gp.getEntityM().getPlayer().getName() + " cannot be swapped out of active combat."
                + " As a reminder, if a combatant swaps out of active combat, attribute buffs and debuffs will remain.";


        int currentPageNumber = 2;
        int totalPageNumbers = 3;
        gp.getTutorialH().generateTutorial(11, title, subtitle, content, currentPageNumber, totalPageNumbers);
    }
}
