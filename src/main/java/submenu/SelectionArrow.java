package submenu;

import core.GamePanel;
import utility.UtilityTool;
import utility.exceptions.AssetLoadException;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.InputStream;

/**
 * This class defines the sub-menu selection arrow, which appears when the sub-menu appears with a list of options for
 * the player to select.
 */
public class SelectionArrow {

    // FIELDS
    GamePanel gp;
    /**
     * Loaded arrow sprite.
     */
    private BufferedImage image;

    /**
     * Boolean tracking whether a draw error has occurred. If true, this prevents a draw error from repeatedly being
     * printed to the console.
     */
    private boolean drawError = false;


    // CONSTRUCTOR
    /**
     * Constructs a SelectionArrow instance.
     *
     * @param gp GamePanel instance
     */
    public SelectionArrow(GamePanel gp) {
        this.gp = gp;
        getImage();
    }


    // METHODS
    /**
     * Draws the selection arrow.
     *
     * @param g2 Graphics2D instance
     * @param screenX x-coordinate of the selection arrow (left side)
     * @param screenY y-coordinate of the selection arrow (top side)
     */
    public void draw(Graphics2D g2, int screenX, int screenY) {

        if (image != null) {

            g2.drawImage(image, screenX, screenY, null);
        } else if (!drawError) {

            UtilityTool.logError("Failed to draw selection arrow: image may not have been properly loaded upon initialization.");
            drawError = true;
        }
    }


    /**
     * Stages sprite to load from resources directory.
     */
    private void getImage() {

        image = setupImage("/miscellaneous/selection_arrow.png");
    }


    /**
     * Loads and scales the selection arrow sprite.
     * Recommended file format is PNG.
     *
     * @param filePath file path of sprite from resources directory
     * @return loaded sprite
     * @throws AssetLoadException if an error occurs while loading the selection arrow sprite
     */
    private BufferedImage setupImage(String filePath) {

        BufferedImage image;

        try (InputStream is = getClass().getResourceAsStream(filePath)) {

            image = ImageIO.read(is);
            image = UtilityTool.scaleImage(image, 6 * gp.getScale(), 10 * gp.getScale());                               // 6 is the native width in pixels of the selection arrow sprite, 10 is the native height.

        } catch (Exception e) {

            throw new AssetLoadException("Could not load selection arrow sprite from " + filePath);
        }
        return image;
    }
}
