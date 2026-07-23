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
        String subtitle = "Main Menu - Party";
        String content = "'W' key - Shift character selection upward.\n"
                + "'S' key - Shift character selection downward.\n"
                + "'A' key - Shift skill selection leftward.\n"
                + "'D' key - Shift skill selection rightward.\n"
                + "'Q' key - Shift to settings menu.\n"
                + "'E' key - Shift to inventory menu.\n"
                + "'Space' key - Close main menu.";
        int currentPageNumber = 4;
        int totalPageNumbers = 6;
        gp.getTutorialH().generateTutorial(5, title, subtitle, content, currentPageNumber, totalPageNumbers);
    }
}