package cutscene;

import core.GamePanel;
import miscellaneous.GameState;
import cutscene.implementation.Cts_Test1;
import utility.UtilityTool;

import java.awt.*;

/**
 * This class handles operations related to in-game cutscenes.
 */
public class CutsceneManager {

    /*
     * To trigger a cutscene, use the `triggerCutscene()` method to set which cutscene to play.
     * When the `draw()` method is called by GamePanel, the appropriate cutscene will play and the
     * game state will automatically be changed to CUTSCENE.
     *
     * Note that the cutscene will pick up on its last recorded phase.
     * This allows a cutscene to resume if it was paused to run other game logic.
     * For example, a cutscene may include dialogue halfway through it.
     */

    // BASIC FIELDS
    private final GamePanel gp;
    private Graphics2D g2;

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

        if (gp.getGameState() == GameState.CUTSCENE) {

            switch(activeSceneNum) {
                case 1:
                    if (cts_test1.isTriggerable()) {
                        cts_test1.run(dt);
                    }
                    break;
                default:
                    UtilityTool.logWarning("Attempted to play a cutscene that does not exist; setting the game back to an explore state.");
                    gp.setGameState(GameState.EXPLORE);
            }
        }
    }


    /**
     * Triggers the specified cutscene to run., either from the beginning or from the phase it was paused at.
     * The cutscene will automatically be started when the `draw()` method for CutsceneManager is called in GamePanel.
     * The game state is set to cutscene.
     *
     * @param sceneNum number of the cutscene to be triggered
     */
    public void triggerCutscene(int sceneNum) {

        this.activeSceneNum = sceneNum;
        gp.setGameState(GameState.CUTSCENE);
    }


    /**
     * Stops a cutscene from playing.
     * Note that this does not reset the cutscene.
     * The cutscene is halted in whatever phase it was last executing.
     * The game state is set to explore.
     */
    public void exitCutscene() {

        activeSceneNum = 0;
        gp.setGameState(GameState.EXPLORE);
    }


    // GETTER
    public int getActiveSceneNum() {
        return activeSceneNum;
    }
}
