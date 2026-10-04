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


    // METHODS
    @Override
    public void run() {

        generateTutorial();
    }


    /**
     * Generates and displays the controls tutorial (page 7).
     */
    private void generateTutorial() {

        String title = "Controls Tutorial";
        String subtitle = "Main Menu - Settings";
        String content = "'W' key - Shift setting selection upward.\n"
                + "'S' key - Shift setting selection downward.\n"
                + "'A' key - Toggle selected setting leftward.\n"
                + "'D' key - Toggle selected setting rightward.";
        int currentPageNumber = 7;
        int totalPageNumbers = 7;
        gp.getTutorialH().generateTutorial(8, title, subtitle, content, currentPageNumber, totalPageNumbers);
    }
}
