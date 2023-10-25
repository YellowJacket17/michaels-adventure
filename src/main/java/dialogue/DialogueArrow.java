package dialogue;

import core.GamePanel;
import utility.UtilityTool;
import utility.exceptions.AssetLoadException;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.InputStream;

/**
 * This class defines the dialogue progression arrow, which appears when a piece of dialogue has more text to be read
 * that did not all fit on the screen all at once.
 */
public class DialogueArrow {

    // FIELDS
    GamePanel gp;

    /**
     * Loaded arrow sprite.
     */
    private BufferedImage image;

    /**
     * Controls the number of frames between the animation of the arrow moving up and down.
     */
    private int rest;

    /**
     * Boolean tracking the up-and-down movement of the dialogue arrow when being drawn.
     */
    private boolean isUpPosition = false;

    /**
     * Boolean tracking whether a draw error has occurred.
     * If true, this prevents a draw error from repeatedly being printed to the console.
     */
    private boolean drawError = false;


    // CONSTRUCTOR
    /**
     * Constructs a DialogueArrow instance.
     *
     * @param gp GamePanel instance
     */
    public DialogueArrow(GamePanel gp) {
        this.gp = gp;
        getImage();
    }


    // METHODS
    /**
     * Draws the dialogue arrow.
     *
     * @param g2 Graphics2D instance
     * @param screenX x-coordinate of the dialogue arrow (left side)
     * @param screenY y-coordinate of the dialogue arrow (top side)
     */
    public void draw(Graphics2D g2, int screenX, int screenY) {

        if (rest == 0) {

            isUpPosition = !isUpPosition;                                                                               // Switch `isUpPosition` to the opposite boolean value.
            rest = 30;                                                                                                  // Force the arrow to wait 30 frames before moving again.
        } else {

            rest--;                                                                                                     // Decrease frame countdown for animation by one each time a new frame with the arrow is drawn.
        }

        if (!isUpPosition) {

            screenY += 3 * gp.getScale();                                                                               // Set the dialogue arrow to its "down" position.
        }

        if (image != null) {

            g2.drawImage(image, screenX, screenY, null);
        } else if (!drawError) {

            UtilityTool.logError("Failed to draw dialogue arrow: image may not have been properly loaded upon initialization.");
            drawError = true;
        }
    }


    /**
     * Resets the dialogue arrow to its default state.
     */
    public void reset() {

        isUpPosition = false;
        rest = 0;
    }


    /**
     * Stages sprite to load from resources directory.
     */
    private void getImage() {

        image = setupImage("/miscellaneous/dialogue_arrow.png");
    }


    /**
     * Loads and scales the dialogue arrow sprite.
     * Recommended file type is PNG.
     *
     * @param filePath file path of sprite from resources directory
     * @return loaded sprite
     * @throws AssetLoadException if an error occurs while loading the dialogue arrow sprite
     */
    private BufferedImage setupImage(String filePath) {

        BufferedImage image;

        try (InputStream is = getClass().getResourceAsStream(filePath)) {

            image = ImageIO.read(is);
            image = UtilityTool.scaleImage(image, 10 * gp.getScale(), 6 * gp.getScale());                               // 10 is the native width in pixels of the dialogue arrow sprite; 6 is the native height.

        } catch (Exception e) {

            throw new AssetLoadException("Could not load dialogue arrow sprite from " + filePath);
        }
        return image;
    }
}
