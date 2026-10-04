package event.implementation.tutorial;

import core.GamePanel;
import event.EventTutorialBase;

/**
 * This class implements post-tutorial event logic for tutorial with ID 4.
 * Note that a tutorial ID of 4 is used for the controls tutorial (page 3).
 */
public class Evt_Tutorial004 extends EventTutorialBase {

    // CONSTRUCTOR
    public Evt_Tutorial004(GamePanel gp) {
        super(gp);
    }


    // METHODS
    @Override
    public void run() {

        generateTutorial();
    }


    /**
     * Generates and displays the controls tutorial (page 4).
     */
    private void generateTutorial() {

        String title = "Controls Tutorial";
        String subtitle = "Main Menu - General";
        String content = "'Q' key - Shift active menu section left (Party, Inventory, Settings).\n"
                + "'E' key - Shift active menu section right (Party, Inventory, Settings).\n"
                + "'Space' key - Close main menu.";
        int currentPageNumber = 4;
        int totalPageNumbers = 7;
        gp.getTutorialH().generateTutorial(5, title, subtitle, content, currentPageNumber, totalPageNumbers);
    }
}
