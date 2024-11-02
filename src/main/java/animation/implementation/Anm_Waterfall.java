package animation.implementation;

import animation.AnimationBase;

/**
 * This class handles the animation of the "waterfall" animation group.
 */
public class Anm_Waterfall extends AnimationBase {

    // FIELD
    /**
     * Amount of time that each frame of the animation will be display for (seconds).
     * In practice, each frame of an animation will display for an equal amount of time.
     * That equal amount of time is equal to `maxCounter` divided by 21, since it is assumed that there are 21 frames
     * of animation.
     */
    private final double unit;


    // CONSTRUCTOR
    public Anm_Waterfall(double counterMax) {
        super(counterMax);
        unit = counterMax / 21.0;
    }


    // METHOD
    @Override
    public int getSprite(int worldCol, int worldRow) {

        if (counter <= unit) {
            return 0;

        } else if (counter <= (unit * 2)) {
            return 1;

        } else if (counter <= (unit * 3)) {
            return 2;

        } else if (counter <= (unit * 4)) {
            return 3;

        } else if (counter <= (unit * 5)) {
            return 4;

        } else if (counter <= (unit * 6)) {
            return 5;

        } else if (counter <= (unit * 7)) {
            return 6;

        } else if (counter <= (unit * 8)) {
            return 7;

        } else if (counter <= (unit * 9)) {
            return 8;

        } else if (counter <= (unit * 10)) {
            return 9;

        } else if (counter <= (unit * 11)) {
            return 10;

        } else if (counter <= (unit * 12)) {
            return 11;

        } else if (counter <= (unit * 13)) {
            return 12;

        } else if (counter <= (unit * 14)) {
            return 13;

        } else if (counter <= (unit * 15)) {
            return 14;

        } else if (counter <= (unit * 16)) {
            return 15;

        } else if (counter <= (unit * 17)) {
            return 16;

        } else if (counter <= (unit * 18)) {
            return 17;

        } else if (counter <= (unit * 19)) {
            return 18;

        } else if (counter <= (unit * 20)) {
            return 19;

        } else if (counter <= (unit * 21)) {
            return 20;
        }
        return 0;
    }
}
