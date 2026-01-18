package animation.implementation;

import animation.PassiveAnimationBase;

/**
 * This class handles animation of the stream passive animation group.
 */
public class Anm_Stream extends PassiveAnimationBase {

    // FIELD
    /**
     * Amount of time that each frame of the animation will be display for (seconds).
     * In practice, each frame of an animation will display for an equal amount of time.
     * That equal amount of time is equal to `maxCounter` divided by 2, since it is assumed that there are 2 frames
     * of animation.
     */
    private final double unit;


    // CONSTRUCTOR
    public Anm_Stream(double counterMax) {
        super(counterMax);
        unit = counterMax / 2.0;
    }


    // METHOD
    @Override
    public int getSprite(int worldCol, int worldRow) {

        if (counter <= unit) {
            return 0;

        } else if (counter <= counterMax) {
            return 1;
        }
        return 0;
    }
}
