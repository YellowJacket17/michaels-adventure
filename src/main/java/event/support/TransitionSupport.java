package event.support;

import core.GamePanel;
import org.joml.Vector3f;

/**
 * This class contains methods to facilitate transitions, defined as a dressed-up as a fade to/from loading screen that
 * may involve warping the player, loading a new map, etc.
 * The public methods in this class serve as primary endpoints to use when programming in-game events.
 */
public class TransitionSupport {

    // FIELDS
    private final GamePanel gp;

    /**
     * Boolean to set whether the game is actioning a transition (true) or not (false).
     */
    private boolean transitionActive = false;

    /**
     * Variable to store the current transition type being performed.
     */
    private TransitionType activeTransitionType = TransitionType.DEFAULT;

    /**
     * Variable to store  of the state current transition being performed.
     */
    private FadeState state = FadeState.INACTIVE;

    /**
     * Duration of fade to for transition (seconds).
     */
    private final double transitionFadeToDuration = 0.5;

    /**
     * Duration of load for transition (seconds).
     */
    private final double transitionLoadDuration = 0.25;

    /**
     * Duration of fade from for transition (seconds).
     */
    private final double transitionFadeFromDuration = 0.5;


    // CONSTRUCTOR
    /**
     * Construct a TransitionSupport instance.
     *
     * @param gp GamePanel instance
     */
    public TransitionSupport(GamePanel gp) {
        this.gp = gp;
    }


    // METHODS
    /**
     * Updates the state of any active transition by one frame.
     * For example, performs any loading or other logic that needs to be run immediately after a new transition phase is
     * entered.
     *
     * @param dt time since last frame (seconds)
     */
    public void update(double dt) {

        if ((transitionActive) && (state != gp.getFadeS().getState())) {

            state = gp.getFadeS().getState();

            switch (state) {
                case ACTIVE:
                    handleTransitionLoading(dt);
                    System.gc();                                                                                        // Now is a good time for garbage collection by the JVM.
                    break;
                case INACTIVE:
                    concludeTransition();
                    break;
            }
        }
    }


    /**
     * Initiates a transition.
     * Remember to also set a sub-transition type directly after calling this method, if applicable (ex. basic enter
     * combat transition subtype of the enter combat transition type).
     * Control is locked/revoked from the player.
     *
     * @param transitionType transition type
     */
    public void initiateTransition(TransitionType transitionType) {

        gp.setLockPlayerControl(true);                                                                                  // Prevent player from providing inputs.
        transitionActive = true;                                                                                        // Indicate that a transition is being actioned.
        activeTransitionType = transitionType;                                                                          // Set the overarching transition type.
        state = FadeState.FADE_TO;                                                                      // All transitions start with a fade to.
        gp.getFadeS().initiateFlash(transitionFadeToDuration, transitionFadeFromDuration, transitionLoadDuration,
                new Vector3f(0, 0, 0));                                                                                 // Initiate a fade to and fade from black.
    }


    /**
     * Performs any loading that needs to be done once the screen is completely faded during a transition of any type.
     *
     * @param dt time since last frame (seconds)
     */
    private void handleTransitionLoading(double dt) {

        switch (activeTransitionType) {
            case WARP:
                gp.getWarpS().handleWarpTransitionLoading(dt);
                break;
            case ENTER_COMBAT:
                gp.getCombatM().handleEnterCombatTransitionLoading();
                break;
            case EXIT_COMBAT:
                gp.getCombatM().handleExitCombatTransitionLoading();
                break;
        }
    }


    /**
     * Closes out a transition of any type that has completed all of its phases (i.e., tidies up any variables).
     * Control is unlocked/returned to the player.
     */
    private void concludeTransition() {

        switch (activeTransitionType) {
            case WARP:
                gp.getWarpS().concludeWarpTransition();
                break;
            case ENTER_COMBAT:
                gp.getCombatM().concludeEnterCombatTransition();
                break;
            case EXIT_COMBAT:
                gp.getCombatM().concludeExitCombatTransition();
                break;
        }
        gp.getFadeS().reset();                                                                                          // Reset.
        activeTransitionType = TransitionType.DEFAULT;                                                                  // Reset.
        transitionActive = false;                                                                                       // Indicate that a transition is no longer being actioned.
        gp.setLockPlayerControl(false);                                                                                 // Allow player to provide inputs.
    }


    // GETTERS
    public boolean isTransitionActive() {
        return transitionActive;
    }

    public TransitionType getActiveTransitionType() {
        return activeTransitionType;
    }

    public FadeState getState() {
        return state;
    }
}
