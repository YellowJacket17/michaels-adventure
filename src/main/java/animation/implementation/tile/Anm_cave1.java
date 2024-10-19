package animation.implementation.tile;

import animation.AnimationBase;

/**
 * This class handles animation of the "cave1" animation group.
 */
public class Anm_cave1 extends AnimationBase {

    // CONSTRUCTOR
    public Anm_cave1(double counterMax) {
        super(counterMax);
    }


    // METHOD
    @Override
    public int getSprite(int worldCol, int worldRow) {

        if (counter <= 3.0) {
            return 0;

        } else if (counter <= 3.075) {
            return 1;

        } else if (counter <= 3.15) {
            return 2;

        } else if (counter <=  3.5) {
            return 3;

        } else if (counter <=  3.575) {
            return 2;

        } else if (counter <=  3.65) {
            return 1;

        } else {
            return 0;                                                                                                   // Default return sprite.
        }
    }
}
