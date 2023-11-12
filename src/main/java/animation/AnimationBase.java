package animation;

/**
 * This abstract class defines base logic for tile animation.
 */
public abstract class AnimationBase {
    
    // FIELDS
    /**
     * Core counter for controlling animation.
     */
    protected int counter;

    /**
     * Controls rate at which `counter` increases.
     * A higher number causes `counter` to increase faster, making transitions between images quicker.
     */
    protected int speed;


    // CONSTRUCTOR
    /**
     * Constructs an Animation instance.
     *
     * @param speed initial rate at which to update animations
     */
    public AnimationBase(int speed) {
        this.speed = speed;
    }


    // METHODS
    /**
     * Updates the animation counter, the value of which is used to determine the next image to be drawn.
     */
    public void update() {

        counter = (counter + 1) * speed;
    }


    /**
     * Retrieves the next sprite to be drawn for th animation.
     *
     * @return index of the next sprite to be drawn
     */
    public abstract int getSprite();
}
