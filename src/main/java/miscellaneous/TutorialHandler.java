package miscellaneous;

import core.GamePanel;
import core.enumeration.PrimaryGameState;
import utility.UtilityTool;

/**
 * This class handles the tutorial overlay that appears.
 */
public class TutorialHandler {

    // FIELDS
    private final GamePanel gp;

    /**
     * Unique ID of tutorial currently being displayed.
     * To be clear, this ID is unique for each tutorial in the game, as it determines what logic to run after the
     * tutorial has been progressed.
     * A default value of '-1' indicates that there is no tutorial currently being displayed.
     */
    private int tutorialId = -1;

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


    // CONSTRUCTOR
    /**
     * Constructs a TutorialHandler instance.
     *
     * @param gp GamePanel instance
     */
    public TutorialHandler(GamePanel gp) {
        this.gp = gp;
    }


    // METHODS
    /**
     * Generates and displays a tutorial. The primary game state is set to tutorial.
     *
     * @param tutorialId ID of the tutorial; this is used to determine what logic should be triggered upon progressing the tutorial
     * @param title title of the tutorial
     * @param content content of the tutorial
     * @param currentPageNumber current page number of this tutorial in its respective series
     * @param totalPageNumbers total page numbers of this tutorial series
     */
    public void generateTutorial(int tutorialId, String title, String content,
                                int currentPageNumber, int totalPageNumbers) {

        generateTutorial(tutorialId, title, "", content, currentPageNumber, totalPageNumbers);
    }


    /**
     * Generates and displays a tutorial. The primary game state is set to tutorial.
     *
     * @param tutorialId ID of the tutorial; this is used to determine what logic should be triggered upon progressing the tutorial
     * @param title title of the tutorial
     * @param subtitle subtitle of the tutorial page
     * @param content content of the tutorial
     * @param currentPageNumber current page number of this tutorial in its respective series
     * @param totalPageNumbers total page numbers of this tutorial series
     */
    public void generateTutorial(int tutorialId, String title, String subtitle, String content,
                                 int currentPageNumber, int totalPageNumbers) {

        if (UtilityTool.VERBOSE_LOGGING) {

            UtilityTool.logInfo("Generating tutorial with ID '" + tutorialId + "'.");
        }
        gp.setPrimaryGameState(PrimaryGameState.TUTORIAL);
        this.tutorialId = tutorialId;
        this.title = title;
        this.subtitle = subtitle;
        this.content = content;
        this.currentPageNumber = currentPageNumber;
        this.totalPageNumbers = totalPageNumbers;

        gp.getEntityM().getPlayer().setInteractionCountdown(
                gp.getEntityM().getPlayer().getStagedStandardInteractionCountdown());                                   // Player must wait before interacting with the generated tutorial (helps avoid accidental progression).
        gp.getUiTutorialS().markDirty();
    }


    /**
     * Resets TutorialHandler back to its default state.
     * Intended to be called to clean up after a tutorial series has been completed.
     */
    public void reset() {

        tutorialId = -1;
        title = "";
        subtitle = "";
        content = "";
        currentPageNumber = 0;
        totalPageNumbers = 0;
    }


    // GETTERS
    public int getTutorialId() {
        return tutorialId;
    }

    public String getTitle() {
        return title;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public String getContent() {
        return content;
    }

    public int getCurrentPageNumber() {
        return currentPageNumber;
    }

    public int getTotalPageNumbers() {
        return totalPageNumbers;
    }
}
