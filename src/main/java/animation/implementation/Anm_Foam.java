package animation.implementation;

import animation.AnimationBase;

/**
 * This class handles animation of the foam animation group.
 */
public class Anm_Foam extends AnimationBase {

    // FIELD
    /**
     * Amount of time that each frame of the animation will be display for (seconds).
     * In practice, each frame of an animation will display for an equal amount of time.
     * That equal amount of time is equal to `maxCounter` divided by 4, since it is assumed that there are 4 frames
     * of animation.
     */
    private final double unit;


    // CONSTRUCTOR
    public Anm_Foam(double counterMax) {
        super(counterMax);
        unit = counterMax / 4.0;
    }


    // METHOD
    @Override
    public int getSprite(int worldCol, int worldRow) {

        if (counter <= unit) {
            return 0;

        } else if (counter <= (unit * 2)) {
            return 1;

        }else if (counter <= (unit * 3)) {
            return 2;

        }else if (counter <= (unit * 4)) {
            return 3;
        }
        return 0;
    }
}
