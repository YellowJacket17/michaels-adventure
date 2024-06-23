package cutscene;

import core.GamePanel;

/**
 * This abstract class defines base logic for a cutscene.
 */
public abstract class CutsceneBase {

    // FIELDS
    protected final GamePanel gp;

    /**
     * Stores the phase this cutscene is currently in (i.e., current point in the cutscene's sequence of events).
     */
    protected int scenePhase;

    /**
     * Boolean determining whether this cutscene is able to be triggered or not.
     * For example, this could be useful if a tile is able to be stepped on multiple times by the player, but a cutscene
     * should only be triggered on the first step.
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
     * Plays this cutscene according to its current phase.
     *
     * @param dt time since last frame (seconds)
     */
    public abstract void run(double dt);


    /**
     * Progresses this cutscene to the next phase.
     */
    public void progressCutscene() {

        scenePhase++;
    }


    /**
     * Resets this cutscene to its first phase.
     */
    protected void resetCutscene() {

        scenePhase = 0;
    }


    /**
     * Stops/pauses this cutscene from running.
     * Note that this does not reset the cutscene.
     * The cutscene is halted in whatever phase it was last executing.
     * This should be one of the last methods called after a cutscene has finished executing to run cleanup.
     */
    protected void exitCutscene() {

        gp.getCutsceneM().exitCutscene();
    }


    // GETTERS
    public int getScenePhase() {
        return scenePhase;
    }

    public boolean isTriggerable() {
        return triggerable;
    }


    // SETTER
    public void setTriggerable(boolean triggerable) {
        this.triggerable = triggerable;
    }
}
