package item;

import core.GamePanel;
import entity.EntityBase;
import utility.UtilityTool;
import utility.exceptions.AssetLoadException;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.InputStream;

/**
 * This abstract class defines base logic for an item that can be stored in the player's inventory.
 */
public abstract class ItemBase {

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
     * Item sprite.
     */
    protected BufferedImage image;

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
     * Boolean to track whether a draw error has occurred.
     * This prevents a draw error from this item being printed to the console again.
     */
    protected boolean drawError = false;                                                                                // If a draw error occurs, this will be marked as true; this prevents a draw error from being printed to the console again.


    // CONSTRUCTOR
    /**
     * Constructs an ItemBase instance.
     *
     * @param gp GamePanel instance
     * @param itemId item ID
     * @param stackable whether this item is stackable in the player's inventory or not
     */
    public ItemBase(GamePanel gp, int itemId, boolean stackable) {
        this.gp = gp;
        this.itemId = itemId;
        this.stackable = stackable;
    }


    /**
     * Draws an item.
     *
     * @param g2 Graphics2D instance
     * @param screenX x-coordinate (left side) where the item sprite will be drawn
     * @param screenY y-coordinate (top side) where the item sprite will be drawn
     */
    public void draw(Graphics2D g2, int screenX, int screenY) {

        if (image != null) {

            g2.drawImage(image, screenX, screenY, null);
        } else if (!drawError) {

            UtilityTool.logError("Failed to draw item "
                    + (((name != null) && (!name.equals(""))) ? (name + " ") : "")
                    + "with ID "
                    + itemId
                    + ": image may not have been properly loaded upon item initialization.");
            drawError = true;
        }
    }


    /**
     * Executes appropriate logic when an item is used.
     *
     * @param user entity using the item
     * @return whether the item was successfully used (true) or not (false)
     */
    public abstract boolean use(EntityBase user);


    /**
     * Loads and scales an item sprite.
     * Recommended file type is PNG.
     *
     * @param fileName file name of sprite, located in resources/items directory
     * @return loaded sprite
     * @throws AssetLoadException if an error occurs while loading an item sprite
     */
    protected BufferedImage setupImage(String fileName) {

        BufferedImage image;
        String completeFilePath = "/items/" + fileName;

        try (InputStream is = getClass().getResourceAsStream(completeFilePath)) {

            image = ImageIO.read(is);
            image = UtilityTool.scaleImage(image, image.getWidth() * gp.getScale(), image.getHeight() * gp.getScale());

        } catch (Exception e) {

            throw new AssetLoadException("Could not load item sprite from " + completeFilePath);
        }
        return image;
    }


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

    public BufferedImage getImage() {
        return image;
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
