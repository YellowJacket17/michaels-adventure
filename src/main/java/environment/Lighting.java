package environment;

import core.GamePanel;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * This class handles lighting effects.
 */
public class Lighting {

    // FIELDS
    /**
     * Image of the effect to overlay.
     */
    private BufferedImage darknessFilter;

    /**
     * Variable tracking how many frames day or night has been in effect for.
     */
    private int dayCounter;

    /**
     * Variable setting the transparency of the `darknessFilter` image overlay.
     * A value of zero means that `darknessFilter` is completely transparent.
     */
    private float filterAlpha = 0f;

    /**
     * Variable defining day state.
     */
    private final int day = 0;

    /**
     * Variable defining dusk state.
     */
    private final int dusk = 1;

    /**
     * Variable defining night state.
     */
    private final int night = 2;

    /**
     * Variable defining dawn state.
     */
    private final int dawn = 3;

    /**
     * Variable tracking active day state.
     */
    private int dayState = day;


    // CONSTRUCTOR
    /**
     * Constructs a Lighting instance.
     *
     * @param gp GamePanel instance
     * @param circleSize diameter of the circle representing the lighted area.
     */
    public Lighting(GamePanel gp, int circleSize) {

        // Create a buffered image.
        darknessFilter = new BufferedImage(GamePanel.NATIVE_SCREEN_WIDTH, GamePanel.NATIVE_SCREEN_HEIGHT, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = (Graphics2D)darknessFilter.getGraphics();                                                       // Everything this `g2` draws will be recorded on this `darknessFilter`.

        // Calculate the center of the light circle (x and y).
//        int centerX = gp.getPlayer().getCenterScreenX() + (gp.getNativeTileSize() / 2);
//        int centerY = gp.getPlayer().getCenterScreenY() + (gp.getNativeTileSize() / 4);

        // Create a gradation effect within the light circle.
        Color[] color = new Color[12];                                                                                  // The number determines the number of divided levels of the gradation.
        float[] fraction = new float[12];                                                                               // Must be the same number as the previous Color array.

        color[0] = new Color(0, 0, 0.1f, 0.3f);                                                                         // Set opacity of innermost color (black).
        color[1] = new Color(0, 0, 0.1f, 0.42f);                                                                        // Set opacity of second color (black).
        color[2] = new Color(0, 0, 0.1f, 0.52f);
        color[3] = new Color(0, 0, 0.1f, 0.61f);
        color[4] = new Color(0, 0, 0.1f, 0.69f);
        color[5] = new Color(0, 0, 0.1f, 0.76f);
        color[6] = new Color(0, 0, 0.1f, 0.82f);
        color[7] = new Color(0, 0, 0.1f, 0.87f);
        color[8] = new Color(0, 0, 0.1f, 0.91f);
        color[9] = new Color(0, 0, 0.1f, 0.94f);
        color[10] = new Color(0, 0, 0.1f, 0.96f);
        color[11] = new Color(0, 0, 0.1f, 0.98f);                                                                       // Set the opacity of the outermost color (black).

        fraction[0] = 0f;                                                                                               // Distance from center of light circle of the innermost color (0 means the center).
        fraction[1] = 0.4f;                                                                                             // Distance from center of light circle of the second color.
        fraction[2] = 0.5f;
        fraction[3] = 0.6f;
        fraction[4] = 0.65f;
        fraction[5] = 0.7f;
        fraction[6] = 0.75f;
        fraction[7] = 0.8f;
        fraction[8] = 0.85f;
        fraction[9] = 0.9f;
        fraction[10] = 0.95f;
        fraction[11] = 1f;                                                                                              // Distance from center of light circle of the outermost color (1 means the edge).

        // Create a gradation paint setting for the light circle.
//        RadialGradientPaint gPaint = new RadialGradientPaint(centerX, centerY, (circleSize / 2), fraction, color);      // Will create circular gradation paint data, which we can use as a paint setting for Graphics2D.

        // Set the gradation data on `g2`.
//        g2.setPaint(gPaint);

        // Draw the rectangle.
        g2.fillRect(0, 0, GamePanel.NATIVE_SCREEN_WIDTH, GamePanel.NATIVE_SCREEN_HEIGHT);

        g2.dispose();
    }


    // METHODS
    /**
     * Updates lighting effects by one frame.
     */
    public void update() {

        // Check the state of the day.
        switch (dayState) {
            case day:
                dayCounter++;                                                                                           // Iterate the day counter.
                if (dayCounter > 600) {                                                                                 // How many frames the day will last for.
                    dayState = dusk;                                                                                    // Switch from day to dusk.
                    dayCounter = 0;                                                                                     // Reset the day counter.
                }
                break;
            case dusk:
                filterAlpha += 0.001f;                                                                                  // It's getting darker, so increase the `filterAlpha` value.
                if (filterAlpha > 1f) {                                                                                 // If `filterAlpha` is greater than 1, this means the darkness filter is completely opaque, hence the transition from day to night is complete.
                    filterAlpha = 1f;                                                                                   // Set `filterAlpha to 1 to avoid an error (the maximum value is 1).
                    dayState = night;                                                                                   // Switch from dusk to night.
                }
                break;
            case (night) :
                dayCounter++;                                                                                           // Iterate the day counter.
                if (dayCounter > 600) {                                                                                 // How many frames the night will last for.
                    dayState = dawn;                                                                                    // Switch from night to dawn.
                    dayCounter = 0;                                                                                     // Reset the day counter.
                }
                break;
            case (dawn):
                filterAlpha -= 0.001f;                                                                                  // It's getting lighter, so increase the `filterAlpha` value.
                if (filterAlpha < 0f) {                                                                                 // If `filterAlpha` is less than 0, this means that the darkness filter is completely transparent, hence the transition from night to day is complete.
                    filterAlpha = 0f;                                                                                   // Set `filterAlpha to 0 to avoid an error (the minimum value is 0).
                    dayState = day;                                                                                     // Switch from dawn to day.
                }
                break;
        }
    }


    /**
     * Draws lighting effects.
     *
     * @param g2 Graphics2D instance
     */
    public void draw(Graphics2D g2) {

        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, filterAlpha));                              // Set the transparency of the darkness filter (used for transitioning time of day).
        g2.drawImage(darknessFilter, 0, 0, null);                                                                       // Draws a full-screen rectangle with gradation effects for lighting.
        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));                                       // Reset the transparency of the darkness filter to 1 (completely opaque).
    }


    // GETTERS
    public boolean isDay() {
        return dayState == day;
    }

    public boolean isDusk() {
        return dayState == dusk;
    }

    public boolean isNight() {
        return dayState == night;
    }

    public boolean isDawn() {
        return dayState == dawn;
    }
}
