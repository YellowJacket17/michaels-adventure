package item;

import core.GamePanel;
import entity.EntityBase;
import org.joml.Vector2f;
import render.Renderer;
import asset.Sprite;
import render.enumeration.ZIndex;
import render.drawable.Drawable;
import utility.UtilityTool;

/**
 * This abstract class defines base logic for an item that can be stored in the player's inventory.
 */
public abstract class ItemBase extends Drawable {

    /*
     * An item represents something that the player can hold in their inventory.
     *
     * Each type of Item (i.e., each ItemBase subclass) has a unique ID defined in its subclass definition.
     * This ID is NOT unique for each ItemBase instance; all ItemBase instances of the same subclass share the same ID.
     */

    // FIELDS
    protected final GamePanel gp;

    /**
     * Item ID.
     * To be clear, this ID is NOT unique for each ItemBase instance.
     * Each ItemBase subclass has its own unique ID, and all instances of that subclass share that ID.
     */
    protected final int itemId;

    /**
     * Item name.
     */
    protected String name;

    /**
     * Item description.
     */
    protected String description;

    /**
     * Variable setting the maximum number of items that can be in a stack if this item is stackable.
     */
    protected final int maxStackAmount = 99;

    /**
     * Current number of items in this item stack.
     */
    protected int amount = 1;

    /**
     * Boolean indicating whether this item is stackable in the player's inventory or not.
     */
    protected final boolean stackable;

    /**
     * Boolean to track whether a render error has occurred.
     * This prevents a render error from this item being printed to the console again.
     */
    protected boolean renderError = false;


    // CONSTRUCTOR
    /**
     * Constructs an ItemBase instance.
     *
     * @param gp GamePanel instance
     * @param itemId item ID
     * @param stackable whether this item is stackable in the player's inventory or not
     */
    public ItemBase(GamePanel gp, int itemId, boolean stackable) {
        super();
        this.gp = gp;
        this.itemId = itemId;
        this.stackable = stackable;
        setSprite();
    }


    // METHODS
    /**
     * Adds an item to the render pipeline.
     *
     * @param renderer Renderer instance
     * @param screenX screen x-coordinate of the item (leftmost, normalized from 0 to 1, both inclusive)
     * @param screenY screen y-coordinate of the item (topmost, normalized from 0 to 1, both inclusive)
     */
    public void addToRenderPipeline(Renderer renderer, float screenX, float screenY) {

        if (sprite != null) {

            Vector2f worldCoords = gp.getCamera().screenCoordsToWorldCoords(new Vector2f(screenX, screenY));
            this.transform.position.x = worldCoords.x;
            this.transform.position.y = worldCoords.y;
            renderer.addDrawable(this, ZIndex.FIRST_LAYER);
        } else if (!renderError) {

            UtilityTool.logError("Failed to add item "
                    + (((name != null) && (!name.equals(""))) ? ("'" + name + "' ") : "")
                    + "with ID '"
                    + itemId
                    + "' to the render pipeline: sprite may not have been properly loaded upon item initialization.");
            renderError = true;
        }
    }


    /**
     * Executes appropriate logic when an item is used in the field.
     *
     * @param user entity using the item
     * @return whether the item was successfully used (true) or not (false)
     */
    public abstract boolean useField(EntityBase user);


    /**
     * Executes appropriate logic when an item is used in combat.
     *
     * @param user entity using the item
     * @return whether the item was successfully used (true) or not (false)
     */
    public abstract boolean useCombat(EntityBase user);


    /**
     * Sets loaded item sprite.
     * Default sprite and default width and height should be set here.
     */
    protected abstract void setSprite();


    // GETTERS
    public int getItemId() {
        return itemId;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public Sprite getSprite() {
        return sprite;
    }

    public int getMaxStackAmount() {
        return maxStackAmount;
    }

    public int getAmount() {
        return amount;
    }

    public boolean isStackable() {
        return stackable;
    }


    // SETTER
    public void setAmount(int amount) {
        if ((stackable) && (amount >= 0) && (amount <= maxStackAmount)) {
            this.amount = amount;
        }
    }
}
