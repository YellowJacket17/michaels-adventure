package landmark;

import core.GamePanel;
import render.Renderer;
import asset.Sprite;
import render.enumeration.ZIndex;
import render.drawable.Drawable;
import utility.UtilityTool;

import java.util.ArrayList;

/**
 * This abstract class defines base logic for a landmark (trees, buildings, etc.).
 */
public abstract class LandmarkBase extends Drawable {

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
     * Note that there's an adjustment to the `worldY` coordinate when rendering a landmark.
     * Since a landmark's `worldY` represents the bottom-left tile it occupies, we need to effectively move the landmark
     * sprite "up" slightly (in the negative y-direction) when rendering since rendering is done in respect to the
     * top-left coordinate of a sprite.
     * In other words, if a landmark is taller than a tile, the y-position needs to adjusted when rendering.
     * If this adjustment were to not happen, then the top-left corner of a landmark's sprite would be in the same
     * position as the top-left corner of the bottom-left tile it's meant to occupy.
     * This would cause the entire landmark sprite to render "lower" on the map (in the position y-direction) than it
     * should.
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
    protected float worldX, worldY;

    /**
     * List of landmark sprites.
     * Multiple sprites in the list can be used for animation.
     */
    protected final ArrayList<Sprite> sprites = new ArrayList<>();

    /**
     * Number of tiles this landmark occupies.
     */
    protected final int numTilesCol, numTilesRow;

    /**
     * Array setting which tiles this landmark occupies have collision (i.e., are solid).
     */
    protected boolean[][] collision;

    /**
     * Animation group that this landmark is part of.
     * Animation groups can have values of 0, 1, 2, etc.
     * A value of -1 means that this landmark is not animated.
     */
    private int animationGroup = -1;

    /**
     * Boolean to track whether a render error has occurred.
     * This prevents a render error from this landmark being printed to the console again.
     */
    protected boolean renderError = false;


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
        super();
        this.gp = gp;
        this.landmarkId = landmarkId;
        this.numTilesRow = numTilesRow;
        this.numTilesCol = numTilesCol;
        collision = new boolean[numTilesRow][numTilesCol];
        setTileCollision();
        setSprites();
    }


    // METHODS
    /**
     * Adds this landmark to the render pipeline.
     *
     * @param renderer Renderer instance
     */
    public void addToRenderPipeline(Renderer renderer) {

        if (gp.isRenderWorld() && !gp.getIllustrationS().isIllustrationActive()) {

            int spriteNum = gp.getAnimationM().getSprite(animationGroup, getCol(), getRow());                           // Render appropriate landmark in animation cycle, if applicable.

            if ((sprites.get(spriteNum) != null) && (spriteNum < sprites.size())) {

                sprite = sprites.get(spriteNum);
                int worldYAdjustment = 0;                                                                               // `worldY` adjustment so that the sprite is rendered correctly.

                if (sprite.getNativeHeight() > GamePanel.NATIVE_TILE_SIZE) {

                    worldYAdjustment = -sprite.getNativeHeight() + GamePanel.NATIVE_TILE_SIZE;
                }
                transform.position.x = worldX;
                transform.position.y = worldY + worldYAdjustment;
                transform.scale.x = sprite.getNativeWidth();
                transform.scale.y = sprite.getNativeHeight();
                renderer.addDrawable(this, ZIndex.THIRD_LAYER);
            } else if (!renderError) {

                UtilityTool.logError("Failed to add landmark "
                        + (((name != null) && (!name.equals(""))) ? ("'" + name + "' ") : "")
                        + "with ID "
                        + landmarkId
                        + " to the render pipeline: the map may contain a landmark that does not exist or a landmark "
                        + "may have been assigned to the incorrect animation group.");
                renderError = true;
            }
        }
    }


    /**
     * Sets which tiles occupied by the landmark have collision.
     */
    protected abstract void setTileCollision();


    /**
     * Sets loaded landmark sprites.
     * Default sprite, default width and height, default opacity (alpha), and animation group should be set here.
     * If not manually set, default opacity is completely opaque.
     * If not manually set, default animation group is no animation.
     */
    protected abstract void setSprites();


    // GETTERS
    public int getLandmarkId() {
        return landmarkId;
    }

    public String getName() {
        return name;
    }

    public float getWorldX() {
        return worldX;
    }

    public float getWorldY() {
        return worldY;
    }

    public int getCol() {
        return (int)(worldX / GamePanel.NATIVE_TILE_SIZE);
    }

    public int getRow() {
        return (int)(worldY / GamePanel.NATIVE_TILE_SIZE);
    }

    public ArrayList<Sprite> getSprites() {
        return sprites;
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

    public int getAnimationGroup() {
        return animationGroup;
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
        worldX = col * GamePanel.NATIVE_TILE_SIZE;
    }

    public void setRow(int row) {
        worldY = row * GamePanel.NATIVE_TILE_SIZE;
    }

    public void setAnimationGroup(int animationGroup) {
        this.animationGroup = animationGroup;
    }
}
