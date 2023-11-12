package animation;

import animation.implementation.tile.Anm_water1;

/**
 * This class handles the animation of tiles that have multiple sprites attached to them.
 */
public class AnimationManager {

    // ANIMATION FIELDS
    private final Anm_water1 anm_water1;


    // CONSTRUCTOR
    /**
     * Constructs an AnimationManager instance.
     */
    public AnimationManager() {
        anm_water1 = new Anm_water1(1);
    }


    // METHODS
    /**
     * Updates all tile animations by one frame.
     */
    public void update() {

        anm_water1.update();
    }


    /**
     * Calls animation logic for the inputted animation group to determine which tile sprite in an animation sequence
     * should be drawn next.
     * Thw switch statement in this method determines what each animation group value actually does.
     *
     * @param group animation group to be checked
     * @return index of the next tile sprite to be drawn
     */
    public int getSprite(int group) {

        switch (group) {
            case 0:
                return anm_water1.getSprite();
            default:
                return 0;
        }
    }
}
