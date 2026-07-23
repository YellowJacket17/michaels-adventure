package event.support;

import core.GamePanel;

/**
 * This class contains methods to facilitate items.
 * The public methods in this class serve as primary endpoints to use when programming in-game events.
 */
public class ItemSupport {


    // FIELDS
    private final GamePanel gp;

    /**
     * Boolean to track whether the item tutorial has been triggered (true) or not (false).
     */
    private boolean itemTutorialTriggered = false;


    // CONSTRUCTOR
    /**
     * Constructs an ItemSupport instance.
     *
     * @param gp GamePanel instance
     */
    public ItemSupport(GamePanel gp) {
        this.gp = gp;
    }


    // METHODS
    /**
     * Initiates the player to pick up an item and add it to the player's inventory.
     * The primary game state is set to dialogue.
     *
     * @param itemId ID of item to be added to the player's inventory
     * @return whether the item was added to the player's inventory (true) or not (false)
     */
    public boolean pickupItemStandard(int itemId) {

        boolean added = gp.getEntityM().getPlayer().addItemToInventory(itemId);

        if (added) {

            gp.getSoundS().playEffect("obtain");
            gp.getEventM().displayMessage(buildItemAddedMessage(itemId), true);
        } else {

            gp.getEventM().displayMessage(buildInventoryFullMessage(), true);
        }
        return added;
    }


    /**
     * Initiates the player to pick up an item and add it to the player's inventory.
     * Additionally, the item tutorial will be generated afterward regardless of whether it has already been triggered
     * or not.
     * The primary game state is set to dialogue.
     *
     * @param itemId ID of item to be added to the player's inventory
     * @return whether the item was added to the player's inventory (true) or not (false)
     */
    public boolean pickupItemTutorial(int itemId) {

        boolean added = gp.getEntityM().getPlayer().addItemToInventory(itemId);

        if (added) {

            gp.getSoundS().playEffect("obtain");
            gp.getDialogueR().initiateItemTutorialMessage(buildItemAddedMessage(itemId), true, true);
        } else {

            gp.getDialogueR().initiateItemTutorialMessage(buildInventoryFullMessage(), true, true);
        }
        return added;
    }


    /**
     * Initiates the player to pick up an item and add it to the player's inventory.
     * Additionally, the item tutorial will be generated if it has not already been triggered.
     * The primary game state is set to dialogue.
     *
     * @param itemId ID of item to be added to the player's inventory
     * @return whether the item was added to the player's inventory (true) or not (false)
     */
    public boolean pickupItemToggleTutorial(int itemId) {

        if (!itemTutorialTriggered) {

            itemTutorialTriggered = true;
            return pickupItemTutorial(itemId);
        } else {

            return pickupItemStandard(itemId);
        }
    }


    /**
     * Generates and displays the item tutorial (page 1).
     */
    public void generateTutorial() {

        String title = "Item Tutorial";
        String subtitle = "You found an item!";
        String content = "As you explore, you'll find various items scattered throughout the world."
                + " There are three items in each area, excluding the final area."
                + " While the items will not help you on your adventure, be sure to search for them!";
        int currentPageNumber = 1;
        int totalPageNumbers = 1;
        gp.getTutorialH().generateTutorial(8, title, subtitle, content, currentPageNumber, totalPageNumbers);
    }


    /**
     * Builds message for item pickup.
     *
     * @param itemId ID of item to be added to the player's inventory
     * @return message
     */
    private String buildItemAddedMessage(int itemId) {

        return gp.getEntityM().getPlayer().getName() + " got a " + gp.getItemM().checkName(itemId) + "!";
    }


    /**
     * Builds message for inventory full.
     *
     * @return message
     */
    private String buildInventoryFullMessage() {

        return gp.getEntityM().getPlayer().getName() + " cannot carry anymore!";
    }


    // GETTERS
    public boolean isItemTutorialTriggered() {
        return itemTutorialTriggered;
    }
}
