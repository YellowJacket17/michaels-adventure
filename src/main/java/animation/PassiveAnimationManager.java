package animation;

import animation.implementation.Anm_Lilypad;
import animation.implementation.Anm_Sparkle;
import animation.implementation.Anm_Foam;
import animation.implementation.Anm_Waterfall;

/**
 * This class handles the passive animation of overworld elements that have multiple sprites attached to them.
 */
public class PassiveAnimationManager {

    // ANIMATION FIELDS
    /**
     * Sparkle passive animation group.
     */
    private final Anm_Sparkle anm_sparkle;

    /**
     * Waterfall passive animation group.
     */
    private final Anm_Waterfall anm_waterfall;


    /**
     * Foam passive animation group.
     */
    private final Anm_Foam anm_foam;


    /**
     * Lilypad passive animation group.
     */
    private final Anm_Lilypad anm_lilypad;


    // CONSTRUCTOR
    /**
     * Constructs an AnimationManager instance.
     * This class handles passive animations.
     */
    public PassiveAnimationManager() {
        anm_sparkle = new Anm_Sparkle(6.0, 0.15, 0.3);
        anm_waterfall = new Anm_Waterfall(1.26);
        anm_foam = new Anm_Foam(0.45);
        anm_lilypad = new Anm_Lilypad(10);
    }


    // METHODS
    /**
     * Updates all passive animations by one frame.
     *
     * @param dt time since last frame (seconds)
     */
    public void update(double dt) {

        anm_sparkle.update(dt);
        anm_waterfall.update(dt);
        anm_foam.update(dt);
        anm_lilypad.update(dt);
    }


    /**
     * Calls passive animation logic for the inputted passive animation group to determine which sprite in an animation
     * sequence should be rendered next.
     *
     * @param group passive animation group to be checked
     * @param worldCol world column position of tile
     * @param worldRow world row position of tile
     * @return index of the next tile sprite to be drawn
     */
    public int getSprite(int group, int worldCol, int worldRow) {

        switch (group) {
            case 0:
                return anm_sparkle.getSprite(worldCol, worldRow);
            case 1:
                return anm_waterfall.getSprite(worldCol, worldRow);
            case 2:
                return anm_foam.getSprite(worldCol, worldRow);
            case 3:
                return anm_lilypad.getSprite(worldCol, worldRow);
            default:
                return 0;
        }
    }
}
