package event.implementation.tutorial;

import core.GamePanel;
import event.EventTutorialBase;

/**
 * This class implements post-tutorial event logic for tutorial with ID 11.
 * Note that a tutorial ID of 11 is used for the party management tutorial (page 2).
 */
public class Evt_Tutorial011 extends EventTutorialBase {

    // CONSTRUCTOR
    public Evt_Tutorial011(GamePanel gp) {
        super(gp);
    }


    // METHOD
    @Override
    public void run() {

        generateTutorial();
    }


    /**
     * Generates and displays the party management tutorial (page 3).
     */
    private void generateTutorial() {

        String title = "Party Management Tutorial";
        String subtitle = "Organization";
        String content = "Party members may be ordered in the Party section of the main menu."
                + " The members in the top three slots (including " + gp.getEntityM().getPlayer().getName() + ") will be active during combat."
                + " To change a party member's position, shift the character selection ('W' and 'S' keys) and press the 'Enter' key."
                + " As in combat, " + gp.getEntityM().getPlayer().getName() + " cannot change position.";
        int currentPageNumber = 3;
        int totalPageNumbers = 3;
        gp.getTutorialH().generateTutorial(12, title, subtitle, content, currentPageNumber, totalPageNumbers);
    }
}
