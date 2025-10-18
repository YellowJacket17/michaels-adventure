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

     * `numTilesRow` represents the number of tile rows that a landmark occupies/encompasses.
     * `numTilesCol` represents the number of tile columns that a landmark occupies/encompasses.
     * Note that a landmark does need to completely occupy a tile to occupy it.
     * For example, the top point of a tree could only actually cover half of a tile, but we would still include that
     * top tile that the point touches in our row count.
     *
     * `worldX` is the x-position in the world that the leftmost tile(s) of the landmark occupies/occupy (i.e., "root"
     * position).
     * `worldY` is the y-position in the world that the bottommost tile(s) of the landmark occupies/occupy (i.e., "root"
     * position).
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
     * The collision array (`collision`) sets which tiles that a landmark occupies should have collision with an entity.
     * In other words, it marks the tiles occupied by a landmark that are considered to be solid.
     * The first dimension of this array represent columns.
     * The second dimension of this array represents rows.
     * In other words, data is stored in this array as [col][row].
     * Note that the collision array is only initialized to be as large as `numTilesCol` and `numTilesRow`.
     * The first row in this array represents the bottommost row of tiles that a landmark occupies/encompasses.
     * The second row in this array represents the row of tile one above (negative y-direction) the bottommost row.
     * And so on....
     * Columns are read left-to-right, starting with the column of the leftmost tile(s) that a landmark occupies/
     * encompasses.
     *
     * For example, let's say that a landmark is a square that encompasses four tiles.
     * The bottom-leftmost tile that the landmark occupies is the "root" location of the landmark (i.e., `worldX` and
     * `worldY`).
     * `collision[0][0]` would be the collision of the bottom-left tile location.
     * `collision[0][1]` would be the collision of the top-left tile location.
     * `collision[1][0]` would be the collision of the bottom-right tile location.
     * `collision[1][1]` would be the collision of the top-right tile location.
     *
     * Note that landmarks are intended to remain static (i.e., not change world positions once instantiated).
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
     * Unique position ID of this landmark.
     * To be clear, this ID is unique to each landmark instance.
     * This ID is the String combination of this landmarks column and row that is then converted back to an integer.
     */
    protected final int positionId;

    /**
     * Landmark's world position.
     * This is the coordinate of the bottom-leftmost tile occupied by this landmark (i.e., "root" position).
     */
    protected final float worldX, worldY;

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
     * Data is stored in this array as [col][row].
     */
    protected final boolean[][] collision;

    /**
     * Passive animation group that this landmark is part of.
     * Passive animation groups can have values of 0, 1, 2, etc.
     * A default value of '-1' means that this landmark is not passively animated.
     */
    protected int passiveAnimationGroup = -1;

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
     * @param landmarkId landmark ID (unique to each subclass)
     * @param numTilesRow number of tiles this landmark occupies horizontally
     * @param numTilesCol number of tiles this landmark occupies vertically
     * @param col column of the bottom-leftmost tile occupied by this landmark (i.e., "root" location)
     * @param row row of the bottom-leftmost tile occupied by this landmark (i.e., "root" location)
     */
    public LandmarkBase(GamePanel gp, int landmarkId, int numTilesCol, int numTilesRow, int col, int row) {
        super();
        this.gp = gp;
        this.landmarkId = landmarkId;
        this.numTilesCol = numTilesCol;
        this.numTilesRow = numTilesRow;
        worldX = col * GamePanel.NATIVE_TILE_SIZE;
        worldY = row * GamePanel.NATIVE_TILE_SIZE;
        collision = new boolean[numTilesCol][numTilesRow];
        positionId = Integer.parseInt(Integer.toString(col) + row);
        setTileCollision();
        setSprites();
    }


    // METHODS
    /**
     * Updates the state of this landmark by one frame.
     *
     * @param dt time since last frame (seconds)
     */
    public void update(double dt) {

        updateInteractiveAnimation(dt);
    }


    /**
     * Adds this landmark to the render pipeline.
     *
     * @param renderer Renderer instance
     */
    public void addToRenderPipeline(Renderer renderer) {

        if (gp.isRenderWorld() && !gp.getIllustrationS().isIllustrationActive()) {

            try {

                if (!gp.getLandmarkM().getActiveInteractiveAnimations().containsKey(positionId)) {                      // Interactive animation takes precedence over passive animation.

                    int spriteNum = gp.getPassiveAnimationM().getSprite(passiveAnimationGroup, getCol(), getRow());     // Render appropriate landmark in passive animation cycle, if applicable.
                    sprite = sprites.get(spriteNum);
                }
                int worldYAdjustment = 0;                                                                               // `worldY` adjustment so that the sprite is rendered correctly.

                if (sprite.getNativeHeight() > GamePanel.NATIVE_TILE_SIZE) {

                    worldYAdjustment = -sprite.getNativeHeight() + GamePanel.NATIVE_TILE_SIZE;
                }
                transform.position.x = worldX;
                transform.position.y = worldY + worldYAdjustment;
                transform.scale.x = sprite.getNativeWidth();
                transform.scale.y = sprite.getNativeHeight();
                renderer.addDrawable(this, ZIndex.THIRD_LAYER);

            } catch (IndexOutOfBoundsException | NullPointerException e) {

                if (!renderError) {

                    UtilityTool.logError("Failed to add landmark '"
                            + this.getClass().getSimpleName()
                            + "' with ID '"
                            + landmarkId
                            + "' to the render pipeline: the map may contain a landmark that does not exist or a landmark "
                            + "may have been assigned an incorrect animation.");
                    renderError = true;
                }
            }
        }
    }


    /**
     * Updates an interactive animation playing for this landmark by one frame.
     * Override this method in implemented LandmarkBase classes if custom interactive animations are desired.
     *
     * @param dt time since last frame (seconds)
     */
    protected void updateInteractiveAnimation(double dt) {

        if (gp.getLandmarkM().getActiveInteractiveAnimations().containsKey(positionId)) {                               // Default behavior is to remove key if present, since default is no programmed interactive animation.

            exitConditionalAnimation();
        }
    }


    /**
     * Cleans up after a conditional animation has finished executing.
     * The landmark sprite is set to its default and is removed from the active conditional animation hashmap.
     * This should be the last method called in conditional animation update logic to clean-up.
     */
    protected void exitConditionalAnimation() {

        sprite = sprites.get(0);
        gp.getLandmarkM().getActiveInteractiveAnimations().remove(positionId);
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

    public int getPositionId() {
        return positionId;
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

    public int getPassiveAnimationGroup() {
        return passiveAnimationGroup;
    }
}
