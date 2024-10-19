package animation.implementation.tile;

import animation.AnimationBase;

/**
 * This class handles animation of the "water1" animation group.
 */
public class Anm_water1 extends AnimationBase {

    // CONSTRUCTOR
    public Anm_water1(double counterMax) {
        super(counterMax);
    }


    // METHOD
    @Override
    public int getSprite(int worldCol, int worldRow) {

        if (counter <= 2.0) {
            return 3;

        } else if (counter <= 2.075) {
            return 2;

        } else if (counter <= 2.15) {
            return 1;

        } else if (counter <=  2.5) {
            return 0;

        } else if (counter <=  2.575) {
            return 1;

        } else if (counter <=  2.65) {
            return 2;

        } else {
            return 3;                                                                                                   // Default return sprite.
        }
    }
}
