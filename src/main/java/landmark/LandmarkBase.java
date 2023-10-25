package landmark;

import core.GamePanel;
import utility.UtilityTool;
import utility.exceptions.AssetLoadException;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.util.ArrayList;

/**
 * This abstract class defines base logic for a landmark (trees, buildings, etc.).
 */
public abstract class LandmarkBase {

    /*
     * A landmark represents something on the map that is part of the map's environment that takes up more than one tile
     * space and has layering effects (trees, statues, set pieces, etc.).
     *
     * Each type of Landmark (i.e., each Landmark subclass) has a unique ID defined in its subclass definition.
     * This ID is NOT unique for each Landmark instance; all Landmark instances of the same subclass share the same ID.

     * `numTilesRow` represents the number of tile rows that a landmark encompasses.
     * `numTilesCol` represents the number of tile columns that a landmark encompasses.
     * Note that it does need to completely occupy a tile to encompass it.
     * For example, the top point of a tree could only actually cover half of a tile, but we would still include that
     * top tile that the point touches in our row count.
     *
     * `worldX` is the x-position in the world that the leftmost tile(s) of the landmark occupies/occupy.
     * `worldY` is the y-position in the world that the bottommost tile(s) of the landmark occupies/occupy.
     *
     * The collision array (`collision`) sets which tiles that a landmark encompasses should have collision with an entity.
     * The first dimension of this array represents rows.
     * The second dimension of this array represents columns.
     * The first row in this array represents the bottommost row of tiles that the landmark encompasses.
     * The second row in this array represents the row of tiles one above the bottommost row.
     * And so on.
     * Columns are read left-to-right, starting with the column of the leftmost tile(s) that the landmark occupies/occupy.
     * For example, `collision[0][0]` would be the collision of the bottom-leftmost tile that the landmark encompasses.
     * `collision[1][0]` would be the collision of the tile directly above the bottom-leftmost tile that the landmark encompasses.
     * `collision[0][1]` would be the collision of the tile directly to the right of the bottom-leftmost tile that the landmark encompasses.
     */

    // FIELDS
    protected final GamePanel gp;

    /**
     * Landmark ID.
     * To be clear, this ID is NOT unique for each LandmarkBase instance. Each LandmarkBase subclasses has its own
     * unique ID, and all instances of that subclass share that ID.
     */
    protected final int landmarkId;

    /**
     * Landmark name (means of identifying it).
     */
    protected String name;

    /**
     * Landmark's world position.
     * This is the coordinate of the bottom-leftmost tile occupied by this landmark.
     */
    protected int worldX, worldY;

    /**
     * List of landmark sprites.
     * Multiple sprites in the list can be used for animation.
     */
    protected final ArrayList<BufferedImage> images = new ArrayList<>();

    /**
     * Number of tiles this landmark occupies.
     */
    protected final int numTilesCol, numTilesRow;

    /**
     * Array setting which tiles this landmark occupies have collision (i.e., are solid).
     */
    protected boolean[][] collision;

    /**
     * Boolean to track whether a draw error has occurred.
     * This prevents a draw error from this landmark being printed to the console again.
     */
    protected boolean drawError = false;


    // CONSTRUCTOR
    /**
     * Constructs a LandmarkBase instance.
     *
     * @param gp GamePanel instance
     * @param landmarkId landmark ID
     * @param numTilesRow number of tiles this landmark occupies horizontally
     * @param numTilesCol number of tiles this landmark occupies vertically
     */
    public LandmarkBase(GamePanel gp, int landmarkId, int numTilesRow, int numTilesCol) {
        this.gp = gp;
        this.landmarkId = landmarkId;
        this.numTilesRow = numTilesRow;
        this.numTilesCol = numTilesCol;
        collision = new boolean[numTilesRow][numTilesCol];
        setTileCollision();
    }


    // METHODS
    /**
     * Draws landmarks for the loaded map.
     *
     * @param g2 Graphics2D instance
     */
    public void draw(Graphics2D g2) {

        // TODO : Add logic to retrieve other images in a landmark for animation.

        int image = 0;                                                                                                  // Select which image from the landmark will be drawn.
        int centerScreenX = gp.getPlayer().getCenterScreenX();
        int centerScreenY = gp.getPlayer().getCenterScreenY();
        int cameraOffsetX = gp.getPlayer().getCameraOffsetX();
        int cameraOffsetY = gp.getPlayer().getCameraOffsetY();

        // Improve rendering efficiency by only drawing landmarks visible on the screen.
        if (worldX + (numTilesCol * gp.getTileSize()) > gp.getPlayer().getWorldX() - centerScreenX - cameraOffsetX &&                               // Left side of screen.
                worldX - gp.getTileSize() < gp.getPlayer().getWorldX() + (gp.getScreenWidth() - centerScreenX) - cameraOffsetX &&                   // Right side of screen.
                worldY + gp.getTileSize() > gp.getPlayer().getWorldY() - centerScreenY - cameraOffsetY &&                                           // Top side of screen.
                worldY - (numTilesRow * gp.getTileSize()) < gp.getPlayer().getWorldY() + (gp.getScreenHeight() - centerScreenY) - cameraOffsetY) {  // Bottom side of screen.

            if ((images.get(image) != null) && (image < images.size())) {

                int adjustedWorldY = worldY - images.get(image).getHeight() + gp.getTileSize();                         // Since `worldY` on the landmark represents the bottom-left tile of the landmark, we need to adjust to the top-left tile since to coordinates that the drawing function requires are those of the top-left of the image.
                int screenX = worldX - gp.getPlayer().getWorldX() + gp.getPlayer().getPlayerScreenX();                  // Determine where on the screen to draw landmark (x).
                int screenY = adjustedWorldY - gp.getPlayer().getWorldY() + gp.getPlayer().getPlayerScreenY();          // Determine where on the screen to draw landmark (y).
                g2.drawImage(images.get(image), screenX, screenY, null);
            } else if (!drawError) {

                UtilityTool.logError("Failed to draw landmark "
                        + (((name != null) && (!name.equals(""))) ? (name + " ") : "")
                        + "with ID "
                        + landmarkId
                        + ": images may not have been properly loaded upon landmark initialization.");
                drawError = true;
            }
        }
    }


    /**
     * Loads and scales a landmark sprite.
     * Recommended file type is PNG.
     *
     * @param fileName file name of sprite, located in resources/landmarks directory
     * @return loaded sprite
     * @throws AssetLoadException if an error occurs while loading a landmark sprite
     */
    protected BufferedImage setupImage(String fileName) {

        BufferedImage image;
        String completeFilePath = "/landmarks/" + fileName;

        try (InputStream is = getClass().getResourceAsStream(completeFilePath)) {

            image = ImageIO.read(is);
            image = UtilityTool.scaleImage(image, image.getWidth() * gp.getScale(), image.getHeight() * gp.getScale());

        } catch (Exception e) {

            throw new AssetLoadException("Could not load landmark sprite from " + completeFilePath);
        }
        return image;
    }


    /**
     * Sets which tiles occupied by the landmark have collision.
     */
    protected abstract void setTileCollision();


    // GETTERS
    public int getLandmarkId() {
        return landmarkId;
    }

    public String getName() {
        return name;
    }

    public int getWorldX() {
        return worldX;
    }

    public int getWorldY() {
        return worldY;
    }

    public int getCol() {
        return worldX / gp.getTileSize();
    }

    public int getRow() {
        return worldY / gp.getTileSize();
    }

    public ArrayList<BufferedImage> getImages() {
        return images;
    }

    public int getNumTilesCol() {
        return numTilesCol;
    }

    public int getNumTilesRow() {
        return numTilesRow;
    }

    public boolean[][] getCollision() {
        return collision;
    }


    // SETTERS
    public void setName(String name) {
        this.name = name;
    }

    public void setWorldX(int worldX) {
        this.worldX = worldX;
    }

    public void setWorldY(int worldY) {
        this.worldY = worldY;
    }

    public void setCol(int col) {
        worldX = col * gp.getTileSize();
    }

    public void setRow(int row) {
        worldY = row * gp.getTileSize();
    }
}
