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
     * Generates and displays the controls tutorial (page 6).
     */
    private void generateTutorial() {

        String title = "Controls Tutorial";
        String subtitle = "Main Menu - Settings";
        String content = "'W' key - Shift setting selection upward.\n"
                + "'S' key - Shift setting selection downward.\n"
                + "'A' key - Toggle selected setting leftward.\n"
                + "'D' key - Toggle selected setting rightward.\n"
                + "'Q' key - Shift to inventory menu.\n"
                + "'E' key - Shift to party menu.\n"
                + "'Space' key - Close main menu.";
        int currentPageNumber = 6;
        int totalPageNumbers = 6;
        gp.getTutorialH().generateTutorial(7, title, subtitle, content, currentPageNumber, totalPageNumbers);
    }
}