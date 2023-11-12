package animation.implementation.tile;

import animation.AnimationBase;

/**
 * This class handles animation of the "water1" animation group.
 */
public class Anm_water1 extends AnimationBase {

    // CONSTRUCTOR
    public Anm_water1(int speed) {
        super(speed);
    }


    // METHOD
    @Override
    public int getSprite() {

        int control1 = 7;
        int control2 = 12;

        if (counter < (control1 * control2)) {
            return 0;

        } else if (counter < (control1 * (control2 + 1))) {
            return 1;

        } else if (counter < (control1 * (control2 + 2))) {
            return 2;

        } else if (counter <  (control1 * ((control2 * 2) + 2))) {
            return 3;

        } else if (counter <  (control1 * ((control2 * 2) + 3))) {
            return 2;

        } else if (counter <  (control1 * ((control2 * 2) + 4))) {
            return 1;

        } else {
            counter = 0;
            return 0;
        }
    }
}
