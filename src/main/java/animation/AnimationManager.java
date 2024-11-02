package animation;

import animation.implementation.Anm_Lilypad;
import animation.implementation.Anm_Sparkle;
import animation.implementation.Anm_Foam;
import animation.implementation.Anm_Waterfall;

/**
 * This class handles the animation of tiles that have multiple sprites attached to them.
 */
public class AnimationManager {

    // ANIMATION FIELDS
    /**
     * Sparkle animation group.
     */
    private final Anm_Sparkle anm_sparkle;

    /**
     * Waterfall animation group.
     */
    private final Anm_Waterfall anm_waterfall;


    /**
     * Foam animation group.
     */
    private final Anm_Foam anm_foam;


    /**
     * Lilypad animation group.
     */
    private final Anm_Lilypad anm_lilypad;


    // CONSTRUCTOR
    /**
     * Constructs an AnimationManager instance.
     */
    public AnimationManager() {
        anm_sparkle = new Anm_Sparkle(6.0, 0.15, 0.3);
        anm_waterfall = new Anm_Waterfall(1.26);
        anm_foam = new Anm_Foam(0.45);
        anm_lilypad = new Anm_Lilypad(10);
    }


    // METHODS
    /**
     * Updates all tile and landmark animations by one frame.
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
     * Calls animation logic for the inputted animation group to determine which tile sprite in an animation sequence
     * should be drawn next.
     * Thw switch statement in this method determines what each animation group value actually does.
     *
     * @param group animation group to be checked
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
