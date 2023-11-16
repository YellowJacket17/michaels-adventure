package item;

import core.GamePanel;
import entity.EntityBase;
import render.Renderer;
import render.Sprite;
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
    protected final int maxStackAmount = 99;                                                                            // Sets the maximum number of items that can be in a stack if the item is stackable.

    /**
     * Current number of items in this item stack.
     */
    protected int amount = 1;                                                                                           // Current amount of the item in the stack; default is 1.

    /**
     * Boolean indicating whether this item is stackable in the player's inventory or not.
     */
    protected final boolean stackable;                                                                                  // Whether an item is stackable in the inventory or not; not initialized here since it's final and a constructor argument.

    /**
     * Boolean to track whether a render error has occurred.
     * This prevents a render error from this item being printed to the console again.
     */
    protected boolean renderError = false;                                                                                // If a draw error occurs, this will be marked as true; this prevents a draw error from being printed to the console again.


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
    }


    /**
     * Adds an item to the render pipeline.
     *
     * @param renderer Renderer instance
     * @param screenX screen x-coordinate of the dialogue arrow (leftmost, normalized between 0 and 1)
     * @param screenY screen y-coordinate of the dialogue arrow (topmost, normalized between 0 and 1)
     */
    public void addToRenderPipeline(Renderer renderer, float screenX, float screenY) {

        if (sprite != null) {

            this.transform.position.x = screenX;
            this.transform.position.y = screenY;
            renderer.addDrawable(this);
        } else if (!renderError) {

            UtilityTool.logError("Failed to add item "
                    + (((name != null) && (!name.equals(""))) ? (name + " ") : "")
                    + "with ID "
                    + itemId
                    + " to the render pipeline: sprite may not have been properly loaded upon item initialization.");
            renderError = true;
        }
    }


    /**
     * Executes appropriate logic when an item is used.
     *
     * @param user entity using the item
     * @return whether the item was successfully used (true) or not (false)
     */
    public abstract boolean use(EntityBase user);


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
