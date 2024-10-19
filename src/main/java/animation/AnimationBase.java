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
     * Retrieves the next sprite to be drawn for the animation.
     *
     * @param worldCol world column position of tile
     * @param worldRow world row position of tile
     * @return index of the next sprite to be rendered
     */
    public abstract int getSprite(int worldCol, int worldRow);
}
