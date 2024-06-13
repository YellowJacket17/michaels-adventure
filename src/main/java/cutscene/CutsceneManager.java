package cutscene;

import core.GamePanel;
import cutscene.implementation.Cts_Test1;
import utility.UtilityTool;

/**
 * This class handles operations related to in-game cutscenes.
 */
public class CutsceneManager {

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
    private int activeSceneNum;


    // SCENE FIELDS
    private final Cts_Test1 cts_test1;


    // CONSTRUCTOR
    /**
     * Constructs a CutsceneManager instance.
     *
     * @param gp GamePanel instance
     */
    public CutsceneManager(GamePanel gp) {
        this.gp = gp;

        cts_test1 = new Cts_Test1(gp);
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

            switch(activeSceneNum) {
                case 1:
                    if (cts_test1.isTriggerable()) {
                        cts_test1.run(dt);
                    }
                    break;
                default:
                    UtilityTool.logWarning("Attempted to play a cutscene that does not exist.");
                    exitCutscene();
            }
        }
    }


    /**
     * Triggers the specified cutscene to run, either from the beginning or from the phase it was last paused at.
     * Note that control is not automatically unlocked/revoked from the player and must be done so manually if needed.
     *
     * @param sceneNum number of the cutscene to be triggered
     */
    public void initiateCutscene(int sceneNum) {

        cutsceneActive = true;
        activeSceneNum = sceneNum;
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
        activeSceneNum = 0;
        gp.setLockPlayerControl(false);
    }


    // GETTERS
    public boolean isCutsceneActive() {
        return cutsceneActive;
    }

    public int getActiveSceneNum() {
        return activeSceneNum;
    }
}
