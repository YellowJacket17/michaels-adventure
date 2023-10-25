package entity.implementation.player;

import combat.implementation.attack.Atk_Punch;
import combat.implementation.attack.Atk_Tackle;
import core.GameState;
import entity.EntityBase;
import entity.EntityDirection;
import entity.EntityType;
import core.GamePanel;
import core.KeyHandler;
import interaction.InteractionType;
import item.implementation.Itm_Controller;
import item.implementation.Itm_Key;
import item.ItemBase;
import submenu.SubMenuHandler;
import utility.LimitedArrayList;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

/**
 * This class defines the player entity that the player controls.
 * Root camera control is also defined here.
 */
public class Player extends EntityBase {

    // FIELDS
    private final KeyHandler keyH;

    /**
     * Indicates where the center of the screen is.
     */
    private final int centerScreenX, centerScreenY;

    /**
     * Indicates where the player is drawn on the screen.
     */
    private int playerScreenX, playerScreenY;

    /**
     * Indicates the offset of the camera from where the player is drawn.
     */
    private int cameraOffsetX, cameraOffsetY;

    /**
     * Rectangle around this entity to represent collision.
     */
    private Rectangle solidArea;

    /**
     * Number of frames that the player has to change direction while retaining momentum upon leaving a state of motion.
     * This lets the player entity instantly being walking in a new direction if they're already moving, versus stopping
     * and then turning.
     * The value of this variable will be set to `moveCountdown` whenever the player entity exits a state of motion.
     */
    private final int stagedMoveCountdown = 2;

    /**
     * Number of frames that must pass before the player can press a key (same or different as previous) to interact
     * with a menu option.
     * Essentially, this control the sensitivity of the cursor in menus.
     * The value of this variable will be set to `interactionCountdown` whenever a pause in menu interaction is needed.
     */
    private final int stagedMenuInteractionCountdown = 9;

    /**
     * Variable to store the number of frames that the player has to change direction while retaining momentum upon
     * leaving a state of motion.
     * This makes turning while walking seamless AND makes it possible to change direction from a static state without
     * changing tile location.
     * On each frame where `updatePlayerInput()` is called, this variable is decremented by one if greater than zero.
     */
    private int moveCountdown;

    /**
     * Variable to store the number of frames that must pass before the player can press a key (same or different as
     * previous) to interact with a menu option.
     * On each frame where appropriate update input methods are called, this variable is decremented by one if greater
     * than zero.
     * This variable may also be set by other classes via its setter.
     */
    private int interactionCountdown;

    /**
     * Variable to store whether the menu (i.e., main menu containing party, inventory, and settings) has already been
     * opened/closed on the current Space key press.
     * This is set to true when the Space key is pressed and false when released.
     * The use for this is, when opening the menu via the Space key, the menu cannot be manually closed until the user
     * releases the Space key and presses it again (reverse is also true for closing the menu).
     * This prevents the menu from flickering if the user keeps the Space key held down.
     */
    private boolean menuActioned = false;

    /**
     * Variable to store whether the debug mode has been enabled/disabled on the current Q key press.
     * This is set to true when the Q key is pressed and false when released.
     * The use for this is, when enabling debug mode via the Q key, debug mode cannot be manually disabled until the
     * user releases the Q key and presses it again (reverse is also true for disabling debug mode).
     * This prevents debug mode from flickering on/off if the user keeps the Q key held down.
     */
    private boolean debugActioned = false;

    /**
     * List representing the player's inventory, in which items are stored.
     * The maximum allowed capacity of this list represents the maximum allowed size of the player's inventory.
     */
    private final LimitedArrayList<ItemBase> inventory = new LimitedArrayList<>(20);


    // CONSTRUCTOR
    public Player(GamePanel gp, KeyHandler keyH) {
        super(gp, 0, EntityType.CHARACTER);

        this.keyH = keyH;

        centerScreenX = (gp.getScreenWidth() / 2) - (gp.getTileSize() / 2);                                             // Draw player at the center of the screen (x); the minus half a tile offset puts the center of the player sprite at the center of the screen.
        centerScreenY = (gp.getScreenHeight() / 2);                                                                     // Draw the player at the center of the screen (y).

        setCamera();

        setCollisionRectangle();
        setDefaultValues();                                                                                             // Set default player values when a player instance is created.
        getImage();                                                                                                     // Load player sprites when a player instance is created.
    }


    // METHODS
    /**
     * Updates the state of the game per player input by one frame.
     */
    public void updatePlayerInput() {

        if (interactionCountdown > 0) {                                                                                 // Decrease interaction frame countdown by one each time a new frame is drawn.
            interactionCountdown--;
        }

        if (moveCountdown > 0) {
            moveCountdown--;                                                                                            // Decrease move frame countdown by one each time a new frame is drawn.
        }

        if ((menuActioned) && (!keyH.isSpacePressed())) {
            menuActioned = false;                                                                                       // Enable the ability of the player to open/close the menu (party, inventory, settings) by pressing the Space key.
        }

        switch (gp.getGameState()) {
            case EXPLORE:
                updateExploreInput();
                break;
            case DIALOGUE:
                updateDialogueInput();
                break;
            case TRANSITION:
                break;
            case CUTSCENE:
                break;
            case PARTY_MENU:
                updatePartyMenuInput();
                break;
            case INVENTORY_MENU:
                updateInventoryMenuInput();
                break;
            case SETTINGS_MENU:
                updateSettingsMenuInput();
                break;
            case SUB_MENU:
                updateSubMenuInput();
                break;
        }
        updateDebugInput();
    }


    @Override
    public void update() {

        // These are core actions that take precedent over all others.
        if (onEntity) {
            actionFollowEntity(onEntityId);
            return;
        }
        if (onPath) {
            actionPath(onPathGoalCol, onPathGoalRow);
            return;
        }

        // Set other actions.
        switch (gp.getGameState()) {
            case EXPLORE:
                updateAction();
                break;
            case DIALOGUE:
                updateAction();
                break;
            case TRANSITION:
                updateAction();
                break;
            case CUTSCENE:
                updateAction();
                break;
            case PARTY_MENU:
                break;
            case INVENTORY_MENU:
                break;
            case SETTINGS_MENU:
                break;
            case SUB_MENU:
                break;
        }
    }


    /**
     * Draws the player entity.
     */
    public void draw(Graphics2D g2) {

        BufferedImage image = retrieveSprite();                                                                         // Retrieve the sprite to be drawn.

        int height = image.getHeight();                                                                                 // Height of the sprite.
        int adjustment = 0;                                                                                             // Amount in the y-direction that the sprite needs to be adjusted when drawn.

        if (height > gp.getTileSize()) {

            adjustment = height - gp.getTileSize();
        }
        g2.drawImage(image, playerScreenX, playerScreenY - adjustment, null);                                           // Note that the coordinates are for the top-left corner of the image; the y-coordinate is adjusted so that the bottom of the player sprite lines up with the bottom of the tile that the player is occupying.

        // DEBUG.
//        if (gp.isDebugVisible() == true) {                                                                              // Draw collision box for debugging.
//
//            g2.setColor(Color.red);
//            g2.drawRect(screenX + solidArea.x, screenY + solidArea.y, solidArea.width, solidArea.height);
//        }
    }


    /**
     * Updates the player entity as part of transition type STEP_PORTAL.
     * Note that this type is where the player entity takes a step into a portal before initiating a transition to
     * another location according to the `warpTransition()` method in EventHandler.
     * The player entity loads into an idle sprite and is not in a state of motion when the transition is complete.
     */
    public void updateWarpTransitionStepPortal() {

        switch (gp.getActiveTransitionPhase()) {
            case 1:                                                                                                     // Phase 1: Set the player to a walking sprite.
                moving = false;                                                                                         // Cancel the player movement that triggered this transition event.
                setWalkingSprite();
                updateWorldPosition();                                                                                  // Update the world position of the player by one unit of its speed in the current direction.
                break;
            case 2:                                                                                                     // Phase 2: Set the player to an idle sprite.
                setIdleSprite();
                directionCurrent = directionCandidate;                                                                  // Set the direction the player will be facing when loaded into the new map.
                directionLast = directionCandidate;
                break;
        }
    }


    /**
     * Checks if there is space in the player's inventory to add a unit of an item if possible.
     * If the item is stackable, check if we can stack either add to an existing stack or create a new stack.
     * If the item is not stackable, check if there's space in the inventory to add a new item.
     *
     * @param item item to be added
     * @return whether a unit of the item was successfully added to the player's inventory (true) or not (false)
     */
    public boolean addItemToInventory(ItemBase item) {

        boolean obtainable = false;

        if (item.isStackable()) {                                                                                       // Logic for if the item is stackable.

            int index = searchPartiallyFilledItemStack(item.getItemId());                                               // If the item is already in the player's inventory, retrieve the index that the item occupies in the `inventory` array; this is NOT the item ID.

            if (index != -1) {                                                                                          // Item already exists in the inventory, so try to stack it.

                int currentAmount = inventory.get(index).getAmount();                                                   // Get the amount of the item currently in the player's inventory.

                if (currentAmount != inventory.get(index).getMaxStackAmount()) {                                        // Only add another of the item if the maximum stackable amount hasn't already been reached.

                    inventory.get(index).setAmount(currentAmount + 1);                                                  // Add another of the item to the player's inventory.
                    obtainable = true;
                }
            } else {                                                                                                    // Item does not already exist in the inventory, so begin a new stack.

                if (inventory.size() != inventory.maxCapacity()) {                                                      // If the player's inventory is already full, don't add the item.

                    inventory.add(item);
                    obtainable = true;
                }
            }
        } else {                                                                                                        // Logic for if the item is not stackable.

            if (inventory.size() != inventory.maxCapacity()) {                                                          // If the player's inventory is already full, don't add the item.

                inventory.add(item);
                obtainable = true;
            }
        }
        return obtainable;
    }


    /**
     * Checks if there is an instance of an item to remove a unit from in the player's inventory.
     *
     * @param item item to be removed
     * @return whether a unit of the item was successfully removed to the player's inventory (true) or not (false)
     */
    public boolean removeItemFromInventory(ItemBase item) {

        boolean removable = false;

        int index = searchLastInstanceOfItem(item.getItemId());

        if (index != -1) {

            int currentAmount = inventory.get(index).getAmount();

            inventory.get(index).setAmount(currentAmount - 1);

            if (inventory.get(index).getAmount() == 0) {

                inventory.remove(index);
            }
            removable = true;
        }
        return removable;
    }


    @Override
    protected void updateAction() {

        if (moving) {                                                                                                   // This will execute if the player character is currently in a state of motion.

            if (!colliding) {                                                                                           // If collision is false, the player can move.

                switch (directionCurrent) {
                    case UP:
                        if ((directionCurrent == directionLast) || (moveCountdown > 0)) {                               // Only move (i.e., change tiles) if it's in the same direction as the last movement OR if the player is still within the frame buffer from the last state of motion.
                            worldY -= speed * gp.getScale();                                                            // Speed will scale with the window size.
                            if (moveCountdown > 0) {
                                directionLast = directionCurrent;
                                moveCountdown = 0;                                                                      // Reset frame buffer.
                            }
                        }
                        break;
                    case DOWN:
                        if ((directionCurrent == directionLast) || (moveCountdown > 0)) {                               // Only move if it's in the same direction as the last movement OR if the player is still within the frame buffer from the last state of motion.
                            worldY += speed * gp.getScale();                                                            // Speed will scale with the window size.
                            if (moveCountdown > 0) {
                                directionLast = directionCurrent;
                                moveCountdown = 0;                                                                      // Reset frame buffer.
                            }
                        }
                        break;
                    case LEFT:
                        if ((directionCurrent == directionLast) || (moveCountdown > 0)) {                               // Only move if it's in the same direction as the last movement OR if the player is still within the frame buffer from the last state of motion.
                            worldX -= speed * gp.getScale();                                                            // Speed will scale with the window size.
                            if (moveCountdown > 0) {
                                directionLast = directionCurrent;
                                moveCountdown = 0;                                                                      // Reset frame buffer.
                            }
                        }
                        break;
                    case RIGHT:
                        if ((directionCurrent == directionLast) || (moveCountdown > 0)) {                               // Only move if it's in the same direction as the last movement OR if the player is still within the frame buffer from the last state of motion.
                            worldX += speed * gp.getScale();                                                            // Speed will scale with the window size.
                            if (moveCountdown > 0) {
                                directionLast = directionCurrent;
                                moveCountdown = 0;                                                                      // Reset frame buffer.
                            }
                        }
                        break;
                }
            }

            if (pixelCounter <= gp.getTileSize() / 2) {                                                                 // Walking animation; player will have a foot forward for half of the pixels traversed.

                if (spriteNumLast == 2) {

                    spriteNumCurrent = 3;
                } else {

                    spriteNumCurrent = 2;
                }
            } else {

                spriteNumCurrent = 1;
            }

            if (turning) {

                pixelCounter += gp.getTileSize() / 8;                                                                   // If the player is turning from a static position, the pixel counter will increase faster to make the act of turning seem faster to the player; simulates "moving" a tile's length at a faster speed (8 frames).
            } else {

                pixelCounter += speed * gp.getScale();                                                                  // Add to the number of pixels the player has moved while in the current state of motion.
            }

            if (pixelCounter >= gp.getTileSize()) {                                                                     // Check if the player character has moved a number of pixels equal to a tile size in the current state of motion.

                moving = false;                                                                                         // If we've moved a tile's length, the player character exits a state of motion and can again be controlled.
                pixelCounter = 0;                                                                                       // Reset the pixel counter.
                moveCountdown = stagedMoveCountdown;                                                                    // Provide a 2 frame buffer for the player to change direction and keep momentum upon exiting the current state of motion.

                if ((!colliding) && (!turning)) {

                    worldXLast = worldXStart;                                                                           // Store the last position the player character was at (x).
                    worldYLast = worldYStart;                                                                           // Store the last position the player character was at (y).
                    worldXStart = worldX;                                                                               // The player character moved a tile, so set new position (x).
                    worldYStart = worldY;                                                                               // The player character moved a tile, so set new position (y).
                } else {

                    worldXEnd = worldX;                                                                                 // The player character did not move a tile, so keep original position (x).
                    worldYEnd = worldY;                                                                                 // The player character did not move a tile, so keep original position (y).
                }

                if (turning) {                                                                                          // If the player was turning, exit a state of turning.

                    turning = false;
                    directionLast = directionCurrent;
                }

                if (spriteNumLast == 2) {                                                                               // Swap which foot will step forward for the next walking cycle.

                    spriteNumLast = 3;
                } else {

                    spriteNumLast = 2;
                }
            }
        }
    }


    /**
     * Stages player entity sprites to load from resources directory.
     */
    private void getImage() {

        up1 = setupImage("/characters/michael/up1.png");
        up2 = setupImage("/characters/michael/up2.png");
        up3 = setupImage("/characters/michael/up3.png");

//        down1 = setupImage("/characters/test_player/down1.png");
//        down2 = setupImage("/characters/test_player/down2.png");
//        down3 = setupImage("/characters/test_player/down3.png");

        down1 = setupImage("/characters/michael/down1.png");
        down2 = setupImage("/characters/michael/down2.png");
        down3 = setupImage("/characters/michael/down3.png");

        left1 = setupImage("/characters/michael/left1.png");
        left2 = setupImage("/characters/michael/left2.png");
        left3 = setupImage("/characters/michael/left3.png");

        right1 = setupImage("/characters/michael/right1.png");
        right2 = setupImage("/characters/michael/right2.png");
        right3 = setupImage("/characters/michael/right3.png");
    }


    /**
     * Initializes the camera position with respect to the player entity.
     */
    private void setCamera() {

        cameraOffsetX = 0;
        cameraOffsetY = 0;

        playerScreenX = centerScreenX + cameraOffsetX;
        playerScreenY = centerScreenY + cameraOffsetY;
    }


    /**
     * Initializes the debug box representing collision drawn around the player entity.
     */
    private void setCollisionRectangle() {

        solidArea = new Rectangle();                                                                                    // Set the area of the collision rectangle in the character; arguments are `(x, y, width, height)`; x and y coordinates are top left of rectangle and y+ is down.
        solidArea.x = 1 * gp.getScale();                                                                                // Top right corner of rectangle (local coordinate system).
        solidArea.y = 1 * gp.getScale();                                                                                // Top right corner of rectangle (local coordinate system).
        solidArea.width = gp.getTileSize() - (2 * gp.getScale());                                                       // The collision box is the size of the entire tile minus 1 pixel on each side (pixel subtraction scales with `gp.getScale()`).
        solidArea.height = gp.getTileSize() - (2 * gp.getScale());                                                      // The collision box is the size of the entire tile minus 1 pixel on each side (pixel subtraction scales with `gp.getScale()`).
    }


    /**
     * Sets default values for the player entity.
     */
    private void setDefaultValues() {

        // World position.
        setCol(23);                                                                                                     // Initial x position on map.  23  11
        setRow(25);                                                                                                     // Initial y position on map.  25  22

        // Basic attributes.
        setName("Player");
        setSpeed(2);                                                                                                    // ENSURE THE SPEED IS A FACTOR OF THE TILE SIZE!

        // Combat attributes.
        setMaxLife(299);
        setLife(299);
        setMaxSkillPoints(50);
        setSkillPoints(50);
        setBaseAttack(1);
        setBaseDefense(1);
        setBaseMagic(1);
        setBaseAgility(10);
        setExp(0);
        setLevel(12);
        setNextLevelExp(5);

        // Combat attacks.
        attacks.add(new Atk_Tackle(gp));
        attacks.add(new Atk_Punch(gp));

        // Items.
        ItemBase item;

        for (int i = 0; i < 600; i++) {

            item = new Itm_Key(gp);
            addItemToInventory(item);
        }

        for (int i = 0; i < 1; i++) {

            item = new Itm_Controller(gp);
            addItemToInventory(item);
        }
    }


    /**
     * Updates the state of the game per player input by one frame when in explore state.
     * Checks for player key input for world interaction, movement, and opening the main menu if the player entity is
     * not in a state of motion.
     */
    private void updateExploreInput() {

        if ((keyH.isSpacePressed()) && (!menuActioned) && (!moving)) {

            gp.setGameState(GameState.PARTY_MENU);
            menuActioned = true;                                                                                        // Disable the ability of the player to close the menu (party, inventory, settings) by pressing the Space key.
        }
        else if (!moving) {                                                                                             // If the player is moving, they will not stop until they move the tile length (for grid-based movement); so, only accept key inputs when the player is not moving.

            boolean interaction = false;                                                                                // Initialize a variable to determine if an object or npc is being interacted with or not.

            if ((keyH.isEnterPressed()) && (interactionCountdown <= 0)) {

                interaction = checkClickInteraction();                                                                  // Check if any interactions triggered by a click (i.e., hitting the Enter key or other manual selection) have been hit.
            }

            if (!interaction) {                                                                                         // If nothing is being interacted with, continue with logic to check if player will move.

                if ((keyH.iswPressed()) || (keyH.issPressed()) ||
                        (keyH.isaPressed()) || (keyH.isdPressed())) {

                    if (keyH.iswPressed()) {
                        directionCurrent = EntityDirection.UP;
                        directionCandidate = EntityDirection.UP;
                        worldXEnd = worldX;
                        worldYEnd = worldY - gp.getTileSize();

                    } else if (keyH.issPressed()) {
                        directionCurrent = EntityDirection.DOWN;
                        directionCandidate = EntityDirection.DOWN;
                        worldXEnd = worldX;
                        worldYEnd = worldY + gp.getTileSize();

                    } else if (keyH.isaPressed()) {
                        directionCurrent = EntityDirection.LEFT;
                        directionCandidate = EntityDirection.LEFT;
                        worldXEnd = worldX - gp.getTileSize();
                        worldYEnd = worldY;

                    } else if (keyH.isdPressed()) {
                        directionCurrent = EntityDirection.RIGHT;
                        directionCandidate = EntityDirection.RIGHT;
                        worldXEnd = worldX + gp.getTileSize();
                        worldYEnd = worldY;
                    }
                    moving = true;                                                                                      // When a direction key is pressed, the player character enters a state of motion.
                    worldXStart = worldX;                                                                               // Record current position before moving (x).
                    worldYStart = worldY;                                                                               // Record current position before moving (y).

                    if ((directionCurrent.equals(directionLast)) || (moveCountdown > 0)) {                              // The if statement ensures that simply changing direction to face a tile doesn't trigger an interaction.

                        checkStepInteraction();                                                                         // Check if any interactions triggered by a step have been hit.
                    } else {

                        turning = true;                                                                                 // Enter a state of turning since the frame buffer has lapsed (meaning the player is currently static) AND the new direction is different from the last.
                    }
                    checkColliding();                                                                                   // Check collision.
                } else {                                                                                                // If player is not moving, set idle sprite.

                    spriteNumCurrent = 1;
                }
            }
        }
    }


    /**
     * Updates the state of the dialogue screen per player input by one frame when in dialogue state.
     * Checks for player key input if dialogue needs to be progressed.
     */
    private void updateDialogueInput() {

        if ((keyH.isEnterPressed())
                && (gp.getDialogueR().getCurrentConv() != null)
                && (!gp.getDialogueR().isReadingDialogue())
                && (interactionCountdown <= 0)) {

            if ((!gp.getDialogueR().isReadingConversation())
                    && (gp.getDialogueR().getCurrentConv().isPlayerInputToEnd())) {                                     // If no longer reading a conversation AND player input is required to end the conversation.

                if (gp.getDialogueR().getCurrentConv().getConvId() == -3) {

                    gp.getCombatM().progressCombat();                                                                   // The conversation was an interactive combat message and has finished; check what logic to run next in combat.
                    interactionCountdown = 10;                                                                     // Player must wait 10 frames before interacting with another action, for example (prevents instantly progressing next action that appears).
                } else {

                    gp.getInteractionM().handlePostConversation(gp.getDialogueR().getCurrentConv().getConvId());        // Check to see if any events will be triggered once the conversation has finished.
                }
            } else {

                gp.getDialogueR().progressConversation();                                                               // Read the next piece of dialogue in the staged conversation.
            }
        }
    }


    /**
     * Updates the state of the party menu screen per player input by one frame when in party menu state.
     * Checks for player key input if menu is closed, changed, or otherwise interacted with.
     */
    private void updatePartyMenuInput() {

        if (interactionCountdown <= 0) {

            if ((keyH.isSpacePressed()) && (!menuActioned)) {
                gp.setGameState(GameState.EXPLORE);
                menuActioned = true;                                                                                    // Disable the ability of the player to open the menu (party, inventory, settings) by pressing the Space key.
            }

            else if (keyH.isTwoPressed()) {
                gp.setGameState(GameState.INVENTORY_MENU);
                interactionCountdown = stagedMenuInteractionCountdown;
            }

            else if (keyH.isThreePressed()) {
                gp.setGameState(GameState.SETTINGS_MENU);
                interactionCountdown = stagedMenuInteractionCountdown;
            }

            else if (keyH.iswPressed()) {
                gp.getUi().setPartySlotSelected(gp.getUi().getPartySlotSelected() - 1);
                interactionCountdown = stagedMenuInteractionCountdown;
            }

            else if (keyH.issPressed()) {
                gp.getUi().setPartySlotSelected(gp.getUi().getPartySlotSelected() + 1);
                interactionCountdown = stagedMenuInteractionCountdown;
            }
        }
    }


    /**
     * Updates the state of the inventory menu screen per player input by one frame when in inventory menu state.
     * Checks for player key input if menu is closed, changed, or otherwise interacted with.
     */
    private void updateInventoryMenuInput() {

        if (interactionCountdown <= 0) {

            if ((keyH.isSpacePressed()) && (!menuActioned)) {
                gp.setGameState(GameState.EXPLORE);
                menuActioned = true;                                                                                    // Disable the ability of the player to open the menu (party, inventory, settings) by pressing the Space key.
            }

            if (keyH.isOnePressed()) {
                gp.setGameState(GameState.PARTY_MENU);
                interactionCountdown = stagedMenuInteractionCountdown;
            }

            else if (keyH.isThreePressed()) {
                gp.setGameState(GameState.SETTINGS_MENU);
                interactionCountdown = stagedMenuInteractionCountdown;
            }

            else if (keyH.iswPressed()) {
                gp.getUi().setItemRowSelected(gp.getUi().getItemRowSelected() - 1);
                interactionCountdown = stagedMenuInteractionCountdown;
            }

            else if (keyH.isaPressed()) {
                gp.getUi().setItemColSelected(gp.getUi().getItemColSelected() - 1);
                interactionCountdown = stagedMenuInteractionCountdown;
            }

            else if (keyH.issPressed()) {
                gp.getUi().setItemRowSelected(gp.getUi().getItemRowSelected() + 1);
                interactionCountdown = stagedMenuInteractionCountdown;
            }

            else if (keyH.isdPressed()) {
                gp.getUi().setItemColSelected(gp.getUi().getItemColSelected() + 1);
                interactionCountdown = stagedMenuInteractionCountdown;
            }
        }
    }


    /**
     * Updates the state of the settings menu screen per player input by one frame when in settings menu state.
     * Checks for player key input if menu is closed, changed, or otherwise interacted with.
     */
    private void updateSettingsMenuInput() {

        if (interactionCountdown <= 0) {

            if ((keyH.isSpacePressed()) && (!menuActioned)) {
                gp.setGameState(GameState.EXPLORE);
                menuActioned = true;                                                                                    // Disable the ability of the player to open the menu (party, inventory, settings) by pressing the Space key.
            }

            else if (keyH.isOnePressed()) {
                gp.setGameState(GameState.PARTY_MENU);
                interactionCountdown = stagedMenuInteractionCountdown;
            }

            else if (keyH.isTwoPressed()) {
                gp.setGameState(GameState.INVENTORY_MENU);
                interactionCountdown = stagedMenuInteractionCountdown;
            }
        }
    }


    /**
     * Updates the state of the sub-menu screen per player input by one frame when in sub-menu state.
     * Checks for player key input if sub-menu option is changed or selected.
     */
    private void updateSubMenuInput() {

        if (interactionCountdown <= 0) {

            if (keyH.iswPressed()) {
                gp.getSubMenuH().setIndexSelected(gp.getSubMenuH().getIndexSelected() - 1);                             // Validation for whether this is an acceptable value is done in the `setIndexSelected()` method in SubMenuHandler.
                interactionCountdown = stagedMenuInteractionCountdown;
            }

            else if (keyH.issPressed()) {
                gp.getSubMenuH().setIndexSelected(gp.getSubMenuH().getIndexSelected() + 1);                             // Validation for whether this is an acceptable value is done in the `setIndexSelected()` method in SubMenuHandler.
                interactionCountdown = stagedMenuInteractionCountdown;
            }

            else if (keyH.isEnterPressed()) {
                gp.getInteractionM().handlePostSubMenu(gp.getSubMenuH().getSubMenuId(), gp.getSubMenuH().getIndexSelected());
                interactionCountdown = 10;
            }
        }
    }


    /**
     * Updates the state of the debug mode by one frame.
     * Checks whether debug mode is toggled on/off and whether the camera is panned when debug mode is enabled.
     *
     */
    private void updateDebugInput() {

        if ((debugActioned) && (!keyH.isqPressed())) {
            debugActioned = false;                                                                                      // Enable the ability of the player to enable the debug mode by pressing the Q key.
        }

        if ((keyH.isqPressed()) && (!debugActioned)) {

            if ((gp.isDebugActive())
                    && (gp.getGameState() == GameState.EXPLORE)
                    && ((gp.getPlayer().getCameraOffsetX() != 0) || (gp.getPlayer().getCameraOffsetY() != 0))) {

                List<String> options = List.of("Yes", "No");                                                            // Immutable list.
                String prompt = "Reset camera back to player?";
                gp.getSubMenuS().displaySubMenuPrompt(prompt, options, 0, SubMenuHandler.widthYesNo);
            }

            gp.setDebugActive(!gp.isDebugActive());
            debugActioned = true;                                                                                       // Disable the ability of the player to enable the debug mode by pressing the Q key.
        }

        else if (gp.isDebugActive()) {

            if (keyH.isUpArrowPressed()) {
                gp.getPlayer().setCameraOffsetY(gp.getPlayer().getCameraOffsetY() + 4);
            }

            if (keyH.isDownArrowPressed()) {
                gp.getPlayer().setCameraOffsetY(gp.getPlayer().getCameraOffsetY() - 4);
            }

            if (keyH.isLeftArrowPressed()) {
                gp.getPlayer().setCameraOffsetX(gp.getPlayer().getCameraOffsetX() + 4);
            }

            if (keyH.isRightArrowPressed()) {
                gp.getPlayer().setCameraOffsetX(gp.getPlayer().getCameraOffsetX() - 4);
            }
        }
    }


    /**
     * Searches the player's inventory to see if a partially filled stack of a target item is present.
     *
     * @param itemId ID of the target item
     * @return index that the partially filled item stack occupies in the player's inventory; will return -1 if no match was found
     */
    private int searchPartiallyFilledItemStack(int itemId) {

        int index = -1;                                                                                                 // Initialize with a default value; note that -1 is somewhat arbitrary, as long as it's a number guaranteed to be outside the inventory array.

        for (int i = 0; i < inventory.size(); i++) {

            if ((inventory.get(i) != null)
                    && (inventory.get(i).getItemId() == itemId)
                    && (inventory.get(i).getAmount() != inventory.get(i).getMaxStackAmount())) {

                index = i;
                break;                                                                                                  // The item was found AND does not have a full stack.
            }
        }
        return index;
    }


    /**
     * Searches the player's inventory to find the instance of an item closest to the bottom of the inventory.
     *
     * @param itemId ID of the target item
     * @return index of the item instance closest to the bottom of the player's inventory; will return -1 if no match was found
     */
    private int searchLastInstanceOfItem(int itemId) {

        int index = -1;

        for (int i = 0; i < inventory.size(); i++) {

            if ((inventory.get(i) != null) && (inventory.get(i).getItemId() == itemId)) {

                index = i;
            }
        }
        return index;
    }


    /**
     * Checks if any interactions triggered by a click have been hit.
     *
     * @return whether an interaction has been triggered (true) or not (false)
     */
    private boolean checkClickInteraction() {

        boolean interaction = gp.getInteractionM().handleNpcInteraction(InteractionType.CLICK);                         // Check in an NPC is being interacted with via a click.

        if (!interaction) {

            interaction = gp.getInteractionM().handleObjectInteraction(InteractionType.CLICK);                          // If an NPC isn't being interacted with, check if an object is being interacted with via a click.
        }

        if (!interaction) {

            interaction = gp.getInteractionM().handlePartyInteraction(InteractionType.CLICK);                           // If an object isn't being interacted with, check if a party member is being interacted with via a click.
        }

        if (!interaction) {

            interaction = gp.getInteractionM().handleTileInteraction(InteractionType.CLICK);                            // If a party member isn't being interacted with, check to see if a tile is being interacted with via a click.
        }

        return interaction;
    }


    /**
     * Checks if any interactions triggered by a step have been hit.
     *
     * @return whether an interaction has been triggered (true) or not (false)
     */
    private boolean checkStepInteraction() {

        boolean interaction = gp.getInteractionM().handleNpcInteraction(InteractionType.STEP);                          // Check in an NPC is being interacted with via a step.

        if (!interaction) {

            interaction = gp.getInteractionM().handleObjectInteraction(InteractionType.STEP);                           // If an NPC isn't being interacted with, check if an object is being interacted with via a step..
        }

        if (!interaction) {

            interaction = gp.getInteractionM().handleTileInteraction(InteractionType.STEP);                             // If an object isn't being interacted with, check if a tile is being interacted with via a step.
        }

        if (!interaction) {

            interaction = gp.getInteractionM().handlePartyInteraction(InteractionType.STEP);                            // If a tile isn't being interacted with, check to see if a party member is being interacted with via a step.
        }

        return interaction;
    }


    // GETTERS
    public int getCenterScreenX() {
        return centerScreenX;
    }

    public int getCenterScreenY() {
        return centerScreenY;
    }

    public int getPlayerScreenX() {
        return playerScreenX;
    }

    public int getPlayerScreenY() {
        return playerScreenY;
    }

    public int getCameraOffsetX() {
        return cameraOffsetX;
    }

    public int getCameraOffsetY() {
        return cameraOffsetY;
    }

    public ArrayList<ItemBase> getInventory() {
        return inventory;
    }


    // SETTERS
    public void setCameraOffsetX(int cameraOffsetX) {
        this.cameraOffsetX = cameraOffsetX;
        playerScreenX = centerScreenX + cameraOffsetX;
    }

    public void setCameraOffsetY(int cameraOffsetY) {
        this.cameraOffsetY = cameraOffsetY;
        playerScreenY = centerScreenY + cameraOffsetY;
    }

    public void setInteractionCountdown(int interactionCountdown) {
        this.interactionCountdown = interactionCountdown;
    }
}
