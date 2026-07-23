package event.implementation.tutorial;

import core.GamePanel;
import event.EventTutorialBase;

/**
 * This class implements post-tutorial event logic for tutorial with ID 3.
 * Note that a tutorial ID of 3 is used for the controls tutorial (page 2).
 */
public class Evt_Tutorial003 extends EventTutorialBase {

    // CONSTRUCTOR
    public Evt_Tutorial003(GamePanel gp) {
        super(gp);
    }


    // METHODS
    @Override
    public void run() {

        generateTutorial();
    }


    /**
     * Generates and displays the controls tutorial (page 3).
     */
    private void generateTutorial() {

        String title = "Controls Tutorial";
        String subtitle = "List of Options";
        String content = "'W' key - Shift selection upward.\n"
                + "'S' key - Shift selection downward.\n"
                + "'Enter' key - Confirm selection.";
        int currentPageNumber = 3;
        int totalPageNumbers = 6;
        gp.getTutorialH().generateTutorial(4, title, subtitle, content, currentPageNumber, totalPageNumbers);
    }
}