package cutscene;

import core.GamePanel;
import miscellaneous.GameState;

/**
 * This abstract class defines base logic for a cutscene.
 */
public abstract class CutsceneBase {

    /*
     * When programming a cutscene, remember the following:
     *    - When temporarily existing a CUTSCENE game state to run other logic that's part of the cutscene logic (such
     *      as dialogue), the `pauseCutscene()` method must be called.
     *      This will automatically set the game state to EXPLORE, but logic to change the game state to a desired one
     *      can be called after calling `pauseCutscene()`, such as calling `displayMessage()` in InteractionManager.
     *    - When completely finishing a cutscene, the `endCutscene()` method must be one of the last
     *      things called.
     *      This will automatically set the game state to EXPLORE, but logic to change the game state to a desired one
     *      can be called after calling `endCutscene()`, such as calling `displayMessage()` in InteractionManager.
     */

    // FIELDS
    protected final GamePanel gp;

    /**
     * Stores the phase this cutscene is currently in (i.e., current point in the cutscene's sequence of events).
     */
    protected int scenePhase;

    /**
     * Boolean determining whether this cutscene is able to be triggered or not.
     */
    protected boolean triggerable = true;


    // CONSTRUCTOR
    /**
     * Constructs a CutsceneBase instance.
     *
     * @param gp GamePanel instance
     */
    public CutsceneBase(GamePanel gp) {
        this.gp = gp;
    }


    // METHODS
    /**
     * Plays the cutscene according to its current phase.
     */
    public abstract void run();


    /**
     * Pauses this cutscene (i.e., exits this cutscene state while retaining which phase the cutscene is currently on).
     * Useful if the game must switch to a dialogue state in the middle of a cutscene, for example.
     *
     * @param gameState
     */
    protected void pauseCutscene(GameState gameState) {

        gp.setGameState(gameState);
    }


    /**
     * Closes out this cutscene once it has completed all of its phases (i.e., tidies up any variables).
     * This should be one of the last things run after a cutscene has gone through all of its phases.
     * The game state is set to explore.
     *
     * @param triggerable whether this cutscene is able to be triggered again (true) or not (false)
     */
    protected void endCutscene(boolean triggerable) {

        scenePhase = 0;                                                                                                 // Reset cutscene phase back to zero (housekeeping).
        this.triggerable = triggerable;                                                                                 // Set whether the cutscene can be triggered again.
        gp.getCutsceneM().exitCutscene();
    }


    // GETTERS
    public int getScenePhase() {
        return scenePhase;
    }

    public boolean isTriggerable() {
        return triggerable;
    }
}
