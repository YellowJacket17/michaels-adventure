package item;

import core.GamePanel;
import item.implementation.Itm_Controller;
import item.implementation.Itm_Key;

import java.util.HashMap;

/**
 * This class manages all available item types.
 */
public class ItemManager {

    /*
     * The appropriate methods in this class should be called whenever a new item is to be instantiated.
     * Any new ItemBase subclasses should be added to switch statement logic in the `retrieveNewInstance()` method, as
     * well as to the `referenceItems` list.
     * The initial capacity of the `referenceItems` list should match the number of reference items that will be added.
     */

    // FIELDS
    private final GamePanel gp;

    /**
     * List to store a single dummy instance of each item type that can be referenced for properties.
     */
    HashMap<Integer, ItemBase> referenceItems = new HashMap<>(2);


    // CONSTRUCTOR
    /**
     * Constructs an ItemManager instance
     *
     * @param gp GamePanel instance
     */
    public ItemManager(GamePanel gp) {
        this.gp = gp;
        referenceItems.put(0, retrieveNewInstance(0));
        referenceItems.put(1, retrieveNewInstance(1));
    }


    // METHODS
    /**
     * Instantiates and returns a new item of the specified type.
     *
     * @param itemId ID of item type to instantiate
     * @return item
     */
    public ItemBase retrieveNewInstance(int itemId) {

        switch (itemId) {
            case 0:
                return new Itm_Key(gp);
            case 1:
                return new Itm_Controller(gp);
            default:
                return null;
        }
    }


    /**
     * Checks the name of a specified item type.
     *
     * @param itemId ID of item type to check
     * @return name of specified item type
     */
    public String checkName(int itemId) {

        return referenceItems.get(itemId).getName();
    }


    /**
     * Checks the description of a specified item type.
     *
     * @param itemId ID of item type to check
     * @return description of specified item type
     */
    public String checkDescription(int itemId) {

        return referenceItems.get(itemId).getDescription();
    }


    /**
     * Checks whether a specified item type is stackable or not.
     *
     * @param itemId ID of item type to check
     * @return whether the specified item type is stackable (true) or not (false)
     */
    public boolean checkStackable(int itemId) {

        return referenceItems.get(itemId).isStackable();
    }
}
