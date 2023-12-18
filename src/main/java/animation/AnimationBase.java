package animation;

/**
 * This abstract class defines base logic for tile animation.
 */
public abstract class AnimationBase {
    
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
     *
     * @param counterMax maximum allowed value for core animation time counter (seconds).
     */
    public AnimationBase(double counterMax) {

        this.counterMax = counterMax;
    }


    // METHODS
    /**
     * Updates the animation counter, the value of which is used to determine the next image to be drawn.
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
     * Retrieves the next sprite to be drawn for th animation.
     *
     * @return index of the next sprite to be drawn
     */
    public abstract int getSprite();
}
