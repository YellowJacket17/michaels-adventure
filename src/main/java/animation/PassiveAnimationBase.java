package animation;

/**
 * This abstract class defines base logic for continuous animation.
 */
public abstract class PassiveAnimationBase {
    
    // FIELDS
    /**
     * Core time counter (seconds) for controlling animation.
     */
    protected double counter;

    /**
     * Maximum allowed value for core animation time counter (seconds).
     */
    protected double counterMax;


    // CONSTRUCTOR
    /**
     * Constructs an Animation instance.
     * This class handles passive animations.
     *
     * @param counterMax maximum allowed value for core passive animation time counter (seconds).
     */
    public PassiveAnimationBase(double counterMax) {

        this.counterMax = counterMax;
    }


    // METHODS
    /**
     * Updates the animation counter, the value of which is used to determine the next image to be rendered for the
     * passive animation.
     *
     * @param dt time since last frame (seconds)
     */
    public void update(double dt) {

        counter += dt;

        while (counter >= counterMax) {

            counter -= counterMax;
        }
    }


    /**
     * Retrieves the next sprite to be rendered for the passive animation.
     *
     * @param worldCol world column position of tile
     * @param worldRow world row position of tile
     * @return index of the next sprite to be rendered
     */
    public abstract int getSprite(int worldCol, int worldRow);
}
