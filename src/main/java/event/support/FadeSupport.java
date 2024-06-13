package event.support;

import core.GamePanel;
import event.FadeState;
import org.joml.Vector3f;

/**
 * This class contains methods to facilitate fading the screen to and from a solid color.
 * The public methods in this class serve as primary endpoints to use when programming in-game events.
 */
public class FadeSupport {

    // FIELDS
    private final GamePanel gp;

    /**
     * Variable to store the current fade state.
     */
    private FadeState state = FadeState.INACTIVE;

    /**
     * Boolean indicating whether a flash effect is active (true) or not (false).
     * A flash effect is a fade effect to a color, retain said color, then fade effect from said color automatically.
     */
    private boolean flashActive = false;

    /**
     * Duration of a fade to effect (seconds).
     */
    private double fadeCounterFadeToMax;

    /**
     * Duration of a fade from effect (seconds).
     */
    private double fadeCounterFadeFromMax;

    /**
     * Duration of color retention (seconds).
     */
    private double fadeCounterActiveMax;

    /**
     * Counter tracking progress of a fade effect / color retention (seconds).
     * The fade counter is rolled back for each state.
     * For example, if a fade to effect completes and a fade from effect is initiated next, the counter will be reset.
     */
    private double fadeCounter;

    /**
     * Color of a fade effect (r, g, b).
     */
    private Vector3f color;


    // CONSTRUCTOR
    /**
     * Constructs a FadeSupport instance.
     *
     * @param gp GamePanel instance
     */
    public FadeSupport(GamePanel gp) {
        this.gp = gp;
    }


    // METHODS
    /**
     * Updates the state of any active fade effect by one frame.
     *
     * @param dt time since last frame (seconds)
     */
    public void update(double dt) {

        switch (state) {
            case FADE_TO:
                fadeCounter += dt;
                if (fadeCounter >= fadeCounterFadeToMax) {
                    state = FadeState.ACTIVE;
                    if (flashActive) {
                        fadeCounter -= fadeCounterFadeToMax;
                    }
                }
                break;
            case ACTIVE:
                if (flashActive) {
                    fadeCounter += dt;
                    if (fadeCounter >= fadeCounterActiveMax) {
                        state = FadeState.FADE_FROM;
                        if (flashActive) {
                            fadeCounter -= fadeCounterActiveMax;
                        }
                    }
                }
                break;
            case FADE_FROM:
                fadeCounter += dt;
                if (fadeCounter >= fadeCounterFadeFromMax) {
                    fadeCounter -= fadeCounterFadeFromMax;
                    state = FadeState.INACTIVE;
                    if (flashActive) {
                        flashActive = false;
                    }
                }
                break;
        }
    }


    /**
     * Initiates a flash effect (fade effect to a color, retain said color, then fade effect from said color
     * automatically).
     * If any other fade effect is already in effect, nothing will happen.
     *
     * @param fadeToDuration duration of fade to effect (seconds)
     * @param fadeFromDuration duration of fade from effect (seconds)
     * @param activeDuration duration of color retention (seconds)
     * @param color color of flash effect (r, g, b).
     */
    public void initiateFlash(double fadeToDuration, double fadeFromDuration, double activeDuration, Vector3f color) {

        if ((state == FadeState.INACTIVE) && (!flashActive)) {

            flashActive = true;
            state = FadeState.FADE_TO;
            fadeCounter = 0;                                                                                            // Reset in case not already.
            fadeCounterFadeToMax = fadeToDuration;
            fadeCounterFadeFromMax = fadeFromDuration;
            fadeCounterActiveMax = activeDuration;
            this.color = color;
        }
    }


    /**
     * Initiates a fade effect to a color.
     * If a flash effect is active, nothing will happen.
     *
     * @param fadeToDuration duration of fade to effect (seconds)
     * @param color color of fade to effect (r, g, b)
     */
    public void initiateFadeTo(double fadeToDuration, Vector3f color) {

        if ((state == FadeState.INACTIVE) && (!flashActive)) {

            state = FadeState.FADE_TO;
            fadeCounter = 0;                                                                                            // Reset in case not already.
            fadeCounterFadeToMax = fadeToDuration;
            this.color = color;
        }
    }


    /**
     * Initiates a fade effect from a color.
     * If a flash effect is active, nothing will happen.
     *
     * @param fadeFromDuration duration of fade from effect (seconds)
     */
    public void initiateFadeFrom(double fadeFromDuration) {

        if ((state == FadeState.ACTIVE) && (!flashActive)) {

            state = FadeState.FADE_FROM;
            fadeCounter = 0;                                                                                            // Reset in case not already.
            fadeCounterFadeFromMax = fadeFromDuration;
        }
    }


    /**
     * Displays a color on the screen.
     * If a flash effect is active, nothing will happen.
     *
     * @param color color to display (r, g, b)
     */
    public void displayColor(Vector3f color) {

        if ((state != FadeState.FADE_TO) && (state != FadeState.FADE_FROM) && (!flashActive)) {

            state = FadeState.ACTIVE;
            this.color = color;
        }
    }


    /**
     * Resets FadeSupport back to its default state.
     * If a fade/flash effect is active, nothing will happen.
     */
    public void reset() {

        if (((state == FadeState.ACTIVE) || (state == FadeState.INACTIVE)) && (!flashActive)) {

            state = FadeState.INACTIVE;
            fadeCounterFadeToMax = 0;
            fadeCounterFadeFromMax = 0;
            fadeCounterActiveMax = 0;
            fadeCounter = 0;
        }
    }


    // GETTERS
    public FadeState getState() {
        return state;
    }

    public boolean isFlashActive() {
        return flashActive;
    }

    public double getFadeCounterFadeToMax() {
        return fadeCounterFadeToMax;
    }

    public double getFadeCounterFadeFromMax() {
        return fadeCounterFadeFromMax;
    }

    public double getFadeCounterActiveMax() {
        return fadeCounterActiveMax;
    }

    public double getFadeCounter() {
        return fadeCounter;
    }

    public Vector3f getColor() {
        return color;
    }
}
