package animation;

import animation.implementation.tile.Anm_cave1;
import animation.implementation.tile.Anm_water1;

/**
 * This class handles the animation of tiles that have multiple sprites attached to them.
 */
public class AnimationManager {

    // ANIMATION FIELDS
    /**
     * "water1" animation group.
     */
    private final Anm_water1 anm_water1;

    /**
     * "cave1" animation group.
     */
    private final Anm_cave1 anm_cave1;


    // CONSTRUCTOR
    /**
     * Constructs an AnimationManager instance.
     */
    public AnimationManager() {
        anm_water1 = new Anm_water1(2.65);
        anm_cave1 = new Anm_cave1(3.65);
    }


    // METHODS
    /**
     * Updates all tile and landmark animations by one frame.
     *
     * @param dt time since last frame (seconds)
     */
    public void update(double dt) {

        anm_water1.update(dt);
        anm_cave1.update(dt);
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
                return anm_water1.getSprite(worldCol, worldRow);
            case 1:
                return anm_cave1.getSprite(worldCol, worldRow);
            default:
                return 0;
        }
    }
}
