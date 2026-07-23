package combat.implementation.action;

import combat.ActionBase;
import combat.SubMenuMemory;
import core.GamePanel;

/**
 * This class defines a combat action (generate a tutorial).
 */
public class Act_GenerateTutorial extends ActionBase {

    // FIELDS
    /**
     * Title of this tutorial.
     */
    private String title;

    /**
     * Subtitle of this tutorial page.
     */
    private String subtitle;

    /**
     * Content of this tutorial.
     */
    private String content;

    /**
     * Current page number of this tutorial in its respective series.
     */
    private int currentPageNumber = 0;

    /**
     * Total page numbers of this tutorial series.
     */
    private int totalPageNumbers = 0;


    // CONSTRUCTORS
    public Act_GenerateTutorial(GamePanel gp, String title, String subtitle, String content,
                                int currentPageNumber, int totalPageNumbers) {
        super(gp);
        this.title = title;
        this.subtitle = subtitle;
        this.content = content;
        this.currentPageNumber = currentPageNumber;
        this.totalPageNumbers = totalPageNumbers;
    }


    public Act_GenerateTutorial(GamePanel gp, String title, String content,
                                int currentPageNumber, int totalPageNumbers) {
        this(gp, title, "", content, currentPageNumber, totalPageNumbers);
    }


    // METHODS
    @Override
    public void run() {

        if (gp.getDialogueR().getActiveConv() != null) {

            gp.getEventM().cleanupConversation(2);                                                                      // Cleans up from the last combat message displayed and removes dialogue window from screen.
        }
        displayTutorial();

        // NOTE: The `progressCombat()` method in CombatManager to hand off control to the next queued action will
        // automatically be called once this tutorial is progressed in the `run()` method in Evt_Tutorial001, hence
        // why it is not called here.
    }


    /**
     * Stages and initiates a tutorial to appear during combat.
     * The tutorial ID is set to 1 to indicate that this is a combat tutorial.
     */
    private void displayTutorial() {

        gp.getTutorialH().generateTutorial(1, title, subtitle, content, currentPageNumber, totalPageNumbers);
    }
}
