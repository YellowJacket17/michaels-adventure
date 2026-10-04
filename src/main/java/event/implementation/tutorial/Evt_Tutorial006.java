package event.implementation.tutorial;

import core.GamePanel;
import event.EventTutorialBase;

/**
 * This class implements post-tutorial event logic for tutorial with ID 6.
 * Note that a tutorial ID of 6 is used for the controls tutorial (page 5).
 */
public class Evt_Tutorial006 extends EventTutorialBase {

    // CONSTRUCTOR
    public Evt_Tutorial006(GamePanel gp) {
        super(gp);
    }


    // METHODS
    @Override
    public void run() {

        generateTutorial();
    }


    /**
     * Generates and displays the controls tutorial (page 5).
     */
    private void generateTutorial() {

        String title = "Controls Tutorial";
        String subtitle = "Main Menu - Inventory";
        String content = "'W' key - Shift item selection upward.\n"
                + "'S' key - Shift item selection downward.\n"
                + "'A' key - Shift item selection leftward.\n"
                + "'D' key - Shift item selection rightward.";
        int currentPageNumber = 6;
        int totalPageNumbers = 7;
        gp.getTutorialH().generateTutorial(7, title, subtitle, content, currentPageNumber, totalPageNumbers);
    }
}
