package event.implementation.tutorial;

import core.GamePanel;
import event.EventTutorialBase;

/**
 * This class implements post-tutorial event logic for tutorial with ID 2.
 * Note that a tutorial ID of 2 is used for the controls tutorial (page 1).
 */
public class Evt_Tutorial002 extends EventTutorialBase {

    // CONSTRUCTOR
    public Evt_Tutorial002(GamePanel gp) {
        super(gp);
    }


    // METHODS
    @Override
    public void run() {

        generateTutorial();
    }


    /**
     * Generates and displays the controls tutorial (page 2).
     */
    private void generateTutorial() {

        String title = "Controls Tutorial";
        String subtitle = "Exploration";
        String content = "'W' key - Move Mary upward.\n"
                + "'S' key - Move Mary downward.\n"
                + "'A' key - Move Mary leftward.\n"
                + "'D' key - Move Mary rightward.\n"
                + "'Enter' key - Interact with world.\n"
                + "'Space' key - Open main menu.";
        int currentPageNumber = 2;
        int totalPageNumbers = 6;
        gp.getTutorialH().generateTutorial(3, title, subtitle, content, currentPageNumber, totalPageNumbers);
    }
}
