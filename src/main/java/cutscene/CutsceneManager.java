package cutscene;

import core.GamePanel;
import cutscene.implementation.Cts_000;
import cutscene.implementation.Cts_001;
import utility.UtilityTool;

/**
 * This class handles operations related to in-game cutscenes.
 */
public class CutsceneManager {

    /*
     * Cutscene subclasses must be added to logic in this management class to be runnable
     */

    // BASIC FIELDS
    private final GamePanel gp;

    /**
     * Boolean to set whether the game is actioning a cutscene (true) or not (false).
     */
    private boolean cutsceneActive = false;

    /**
     * Variable tracking cutscene is currently active.
     * A value of zero means that no cutscene is active.
     * Scene numbers are assigned to cutscenes in the switch statement in the `draw()` method of this class.
     */
    private int activeCutsceneNum;


    // CUTSCENE FIELDS
    private final Cts_001 cts_001;
    private final Cts_000 cts_000;


    // CONSTRUCTOR
    /**
     * Constructs a CutsceneManager instance.
     *
     * @param gp GamePanel instance
     */
    public CutsceneManager(GamePanel gp) {
        this.gp = gp;

        cts_000 = new Cts_000(gp);
        cts_001 = new Cts_001(gp);
    }


    /**
     * Updates an active cutscene by one frame.
     * Note that the active cutscene is set by the `sceneNum` field in this class.
     * If `sceneNum` is set to zero, nothing will happen.
     *
     * @param dt time since last frame (seconds)
     */
    public void update(double dt) {

        if (cutsceneActive) {

            switch(activeCutsceneNum) {
                case 0:
                    if (cts_000.isTriggerable()) {
                        cts_000.run(dt);
                    }
                    break;
                case 1:
                    if (cts_001.isTriggerable()) {
                        cts_001.run(dt);
                    }
                    break;
            }
        }
    }


    /**
     * Triggers the specified cutscene to run, either from the beginning or from the phase it was last paused at.
     * Note that control is not automatically unlocked/revoked from the player and must be done so manually if needed.
     *
     * @param cutsceneNum number of the cutscene to be triggered
     */
    public void initiateCutscene(int cutsceneNum) {

        cutsceneActive = true;
        activeCutsceneNum = cutsceneNum;
        gp.setLockPlayerControl(true);
    }


    /**
     * Stops/pauses a cutscene from running.
     * Note that this does not reset the cutscene.
     * The cutscene is halted in whatever phase it was last executing.
     * Note that control is not automatically unlocked/returned to the player and must be done so manually if needed.
     */
    public void exitCutscene() {

        cutsceneActive = false;
        activeCutsceneNum = 0;
        gp.setLockPlayerControl(false);
    }


    // GETTERS
    public boolean isCutsceneActive() {
        return cutsceneActive;
    }

    public int getActiveCutsceneNum() {
        return activeCutsceneNum;
    }
}
