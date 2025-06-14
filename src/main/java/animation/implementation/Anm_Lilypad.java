package animation.implementation;

import animation.PassiveAnimationBase;

/**
 * This class handles animation of the lilypad passive animation group.
 */
public class Anm_Lilypad extends PassiveAnimationBase {

    // FIELDS
    /**
     * Array of base tiles that control the sparkle animation depending on column and row.
     * The values contained within the array are the start times (seconds) of the sparkle animation for various column
     * and row combinations, relative to the `counter` value.
     * In practice, these start times will repeat for each 10x10 chunk of tiles on a map.
     */
    private final double[][] baseTiles = new double[10][10];


    // CONSTRUCTOR
    public Anm_Lilypad(double counterMax) {
        super(counterMax);
        initBaseArray(counterMax);
    }


    // METHODS
    @Override
    public int getSprite(int worldCol, int worldRow) {

        int moduloCol = worldCol % 10;
        int moduloRow = worldRow % 10;
        double difference;

        if (counter < baseTiles[moduloCol][moduloRow]) {

            difference = (counterMax - baseTiles[moduloCol][moduloRow]) + counter;
        } else {

            difference = counter - baseTiles[moduloCol][moduloRow];
        }

         if (difference > 0) {

            if (difference <= counterMax * 0.015) {
                return 1;

            } else if (difference <= counterMax * 0.5) {
                return 2;

            } else if (difference <= counterMax * 0.515) {
                return 1;

            } else {
                return 0;
            }
        }
        return 0;
    }


    /**
     * Initializes the array of base tiles that control the animation depending on column and row.
     *
     * @param counterMax Maximum allowed value for core animation time counter (seconds).
     */
    public void initBaseArray(double counterMax) {

        float unit = (float)counterMax / 10.0f;

        baseTiles[0][0] = unit * 1.1;
        baseTiles[1][0] = unit * 3.5;
        baseTiles[2][0] = unit * 0.4;
        baseTiles[3][0] = unit * 4.2;
        baseTiles[4][0] = unit * 7.6;
        baseTiles[5][0] = unit * 2.8;
        baseTiles[6][0] = unit * 5.3;
        baseTiles[7][0] = unit * 9.9;
        baseTiles[8][0] = unit * 6.7;
        baseTiles[9][0] = unit * 8.0;

        baseTiles[0][1] = unit * 5.3;
        baseTiles[1][1] = unit * 0.0;
        baseTiles[2][1] = unit * 1.7;
        baseTiles[3][1] = unit * 2.8;
        baseTiles[4][1] = unit * 9.9;
        baseTiles[5][1] = unit * 8.1;
        baseTiles[6][1] = unit * 3.5;
        baseTiles[7][1] = unit * 6.4;
        baseTiles[8][1] = unit * 4.2;
        baseTiles[9][1] = unit * 7.6;

        baseTiles[0][2] = unit * 4.0;
        baseTiles[1][2] = unit * 7.1;
        baseTiles[2][2] = unit * 3.3;
        baseTiles[3][2] = unit * 5.6;
        baseTiles[4][2] = unit * 0.7;
        baseTiles[5][2] = unit * 6.5;
        baseTiles[6][2] = unit * 2.4;
        baseTiles[7][2] = unit * 8.2;
        baseTiles[8][2] = unit * 9.8;
        baseTiles[9][2] = unit * 1.9;

        baseTiles[0][3] = unit * 2.4;
        baseTiles[1][3] = unit * 8.2;
        baseTiles[2][3] = unit * 6.5;
        baseTiles[3][3] = unit * 9.9;
        baseTiles[4][3] = unit * 1.8;
        baseTiles[5][3] = unit * 4.6;
        baseTiles[6][3] = unit * 7.0;
        baseTiles[7][3] = unit * 0.3;
        baseTiles[8][3] = unit * 3.1;
        baseTiles[9][3] = unit * 5.7;

        baseTiles[0][4] = unit * 6.7;
        baseTiles[1][4] = unit * 9.9;
        baseTiles[2][4] = unit * 7.0;
        baseTiles[3][4] = unit * 8.1;
        baseTiles[4][4] = unit * 5.5;
        baseTiles[5][4] = unit * 3.3;
        baseTiles[6][4] = unit * 1.6;
        baseTiles[7][4] = unit * 4.8;
        baseTiles[8][4] = unit * 0.4;
        baseTiles[9][4] = unit * 2.2;

        baseTiles[0][5] = unit * 8.2;
        baseTiles[1][5] = unit * 1.8;
        baseTiles[2][5] = unit * 5.6;
        baseTiles[3][5] = unit * 6.4;
        baseTiles[4][5] = unit * 3.3;
        baseTiles[5][5] = unit * 0.0;
        baseTiles[6][5] = unit * 9.7;
        baseTiles[7][5] = unit * 7.1;
        baseTiles[8][5] = unit * 2.9;
        baseTiles[9][5] = unit * 4.5;

        baseTiles[0][6] = unit * 3.5;
        baseTiles[1][6] = unit * 5.3;
        baseTiles[2][6] = unit * 4.2;
        baseTiles[3][6] = unit * 0.7;
        baseTiles[4][6] = unit * 6.1;
        baseTiles[5][6] = unit * 7.9;
        baseTiles[6][6] = unit * 8.8;
        baseTiles[7][6] = unit * 2.0;
        baseTiles[8][6] = unit * 1.6;
        baseTiles[9][6] = unit * 9.4;

        baseTiles[0][7] = unit * 9.9;
        baseTiles[1][7] = unit * 4.6;
        baseTiles[2][7] = unit * 2.8;
        baseTiles[3][7] = unit * 3.0;
        baseTiles[4][7] = unit * 8.4;
        baseTiles[5][7] = unit * 1.7;
        baseTiles[6][7] = unit * 0.2;
        baseTiles[7][7] = unit * 5.5;
        baseTiles[8][7] = unit * 7.3;
        baseTiles[9][7] = unit * 6.1;

        baseTiles[0][8] = unit * 7.6;
        baseTiles[1][8] = unit * 2.4;
        baseTiles[2][8] = unit * 8.9;
        baseTiles[3][8] = unit * 1.3;
        baseTiles[4][8] = unit * 4.0;
        baseTiles[5][8] = unit * 9.2;
        baseTiles[6][8] = unit * 6.1;
        baseTiles[7][8] = unit * 3.7;
        baseTiles[8][8] = unit * 5.5;
        baseTiles[9][8] = unit * 0.8;

        baseTiles[0][9] = unit * 0.8;
        baseTiles[1][9] = unit * 6.7;
        baseTiles[2][9] = unit * 9.1;
        baseTiles[3][9] = unit * 7.5;
        baseTiles[4][9] = unit * 2.2;
        baseTiles[5][9] = unit * 5.4;
        baseTiles[6][9] = unit * 4.9;
        baseTiles[7][9] = unit * 1.6;
        baseTiles[8][9] = unit * 8.0;
        baseTiles[9][9] = unit * 3.3;
    }
}
