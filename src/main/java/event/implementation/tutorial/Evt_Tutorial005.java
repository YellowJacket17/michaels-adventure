package event.implementation.tutorial;

import core.GamePanel;
import event.EventTutorialBase;

/**
 * This class implements post-tutorial event logic for tutorial with ID 5.
 * Note that a tutorial ID of 5 is used for the controls tutorial (page 4).
 */
public class Evt_Tutorial005 extends EventTutorialBase {

    // CONSTRUCTOR
    public Evt_Tutorial005(GamePanel gp) {
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
                + "'D' key - Shift item selection rightward.\n"
                + "'Q' key - Shift to party menu.\n"
                + "'E' key - Shift to settings menu.\n"
                + "'Space' key - Close main menu.";
        int currentPageNumber = 5;
        int totalPageNumbers = 6;
        gp.getTutorialH().generateTutorial(6, title, subtitle, content, currentPageNumber, totalPageNumbers);
    }
}
