package entity.implementation.player;

import combat.implementation.move.Mve_Punch;
import combat.implementation.move.Mve_Tackle;
import miscellaneous.GameState;
import miscellaneous.KeyListener;
import entity.EntityBase;
import entity.EntityDirection;
import entity.EntityType;
import core.GamePanel;
import event.EventType;
import item.implementation.Itm_Controller;
import item.implementation.Itm_Key;
import item.ItemBase;
import org.joml.Vector2f;
import asset.AssetPool;
import utility.LimitedArrayList;

import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.glfw.GLFW.*;

/**
 * This class defines the player entity that the player controls.
 * Player inputs are also handled here.
 */
public class Player extends EntityBase {

    // FIELDS
    /**
     * Number of seconds that the player has to change direction while retaining momentum upon leaving a state of motion.
     * This lets the player entity instantly being walking in a new direction if they're already moving, versus stopping
     * and then turning.
     * The value of this variable will be set to `moveCountdown` whenever the player entity exits a state of motion.
     * This value will have a noticeable impact on the "feel" of the game while walking.
     */
    private final double stagedMoveCountdown = 0.033;

    /**
     * Number of seconds that must pass before the player can press a key (same or different as previous) to interact
     * with a menu option.
     * Essentially, this control the sensitivity of the cursor in menus.
     * The value of this variable will be set to `interactionCountdown` whenever a pause in menu interaction is needed.
     * This value will have a noticeable impact on the "feel" of the game while in menus.
     * As a reference, note that 0.16 seconds is ~10 frames at 60 FPS.
     */
    private final double stagedMenuInteractionCountdown = 0.16;

    /**
     * Variable to store the number of seconds that the player has to change direction while retaining momentum upon
     * leaving a state of motion.
     * This makes turning while walking seamless AND makes it possible to change direction from a static state without
     * changing tile location.
     * On each frame where `updatePlayerInput()` is called, this variable is decremented by one if greater than zero.
     */
    private double moveCountdown;

    /**
     * Variable to store the number of seconds that must pass before the player can press a key (same or different as
     * previous) to interact with a menu option.
     * On each frame where appropriate update input methods are called, this variable is decremented by one if greater
     * than zero.
     * This variable may also be set by other classes via its setter.
     */
    private double interactionCountdown;

    /**
     * Variable to store the number of seconds that must pass before the player can press a key (same or different as
     * previous) to toggle full screen mode.
     * On each frame where appropriate update input methods are called, this variable is decremented by one if greater
     * than zero.
     * This variable may also be set by other classes via its setter.
     */
    private double fullScreenCountdown;

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
    public Player(GamePanel gp) {
        super(gp, 0, EntityType.CHARACTER);
        setDefaultValues();                                                                                             // Set default player values when a player instance is created.
        setupSprite();                                                                                                  // Load player sprites when a player instance is created.
    }


    // METHODS
    /**
     * Updates the state of the game per player input by one frame.
     *
     * @param dt time since last frame (seconds)
     */
    public void updatePlayerInput(double dt) {

        if (moveCountdown > 0) {
            moveCountdown -= dt;                                                                                        // Decrease move frame countdown by one each time a new frame is drawn.
        }

        if (interactionCountdown > 0) {                                                                                 // Decrease interaction frame countdown by one each time a new frame is drawn.
            interactionCountdown -= dt;
        }

        if (fullScreenCountdown > 0) {                                                                                  // Decrease full screen mode frame countdown by one each time a new frame is drawn.
            fullScreenCountdown -= dt;
        }

        if ((menuActioned) && (!KeyListener.isKeyPressed(GLFW_KEY_SPACE))) {
            menuActioned = false;                                                                                       // Enable the ability of the player to open/close the menu (party, inventory, settings) by pressing the Space key.
        }

        if ((fullScreenCountdown <= 0)
                && (gp.getSystemSetting(3).getActiveOption() == 0)
                && (KeyListener.isKeyPressed(GLFW_KEY_F11))) {
            gp.getSystemSetting(3).setActiveOption(1);                                                                  // Enter full screen mode if disabled.
            if (gp.getUi().getSystemSettingSelected() == 3) {
                gp.getUi().setSystemOptionSelected(1);
            }
            fullScreenCountdown = stagedMenuInteractionCountdown;
        } else if ((fullScreenCountdown <= 0)
                && (gp.getSystemSetting(3).getActiveOption() == 1)
                && (KeyListener.isKeyPressed(GLFW_KEY_ESCAPE) || KeyListener.isKeyPressed(GLFW_KEY_F11))) {
            gp.getSystemSetting(3).setActiveOption(0);                                                                  // Exit full screen mode if enabled.
            if (gp.getUi().getSystemSettingSelected() == 3) {
                gp.getUi().setSystemOptionSelected(0);
            }
            fullScreenCountdown = stagedMenuInteractionCountdown;
        }

        switch (gp.getGameState()) {
            case EXPLORE:
                updateExploreInput(dt);
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
    public void update(double dt) {

        // These are core actions that take precedent over all others.
        if (onEntity) {
            actionFollowEntity(dt, onEntityId);
            return;
        }
        if (onPath) {
            actionPath(dt, onPathGoalCol, onPathGoalRow);
            return;
        }

        // Set other actions.
        switch (gp.getGameState()) {
            case EXPLORE:
                updateAction(dt);
                break;
            case DIALOGUE:
                updateAction(dt);
                break;
            case TRANSITION:
                updateAction(dt);
                break;
            case CUTSCENE:
                updateAction(dt);
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
     * Updates the player entity as part of transition type STEP_PORTAL.
     * Note that this type is where the player entity takes a step into a portal before initiating a transition to
     * another location according to the `warpTransition()` method in EventHandler.
     * The player entity loads into an idle sprite and is not in a state of motion when the transition is complete.
     *
     * @param dt time since last frame (seconds)
     */
    public void updateWarpTransitionStepPortal(double dt) {

        switch (gp.getActiveTransitionPhase()) {
            case FADING_TO:                                                                                             // Phase 1: Set the player to a walking sprite.
                moving = false;                                                                                         // Cancel the player movement that triggered this transition event.
                setWalkingSprite();
                updateWorldPosition(dt);                                                                                // Update the world position of the player by one unit of its speed in the current direction.
                break;
            case LOADING:                                                                                               // Phase 2: Set the player to an idle sprite.
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
    protected void updateAction(double dt) {

        if (moving) {                                                                                                   // This will execute if the player entity is currently in a state of motion.

            worldCounter += speed * dt;                                                                                 // Add to the number of world units the player entity has moved while in the current state of motion.

            if (!colliding) {                                                                                           // If collision is false, the player can move.

                switch (directionCurrent) {
                    case UP:
                        if ((directionCurrent == directionLast) || (moveCountdown > 0)) {                               // Only move (i.e., change tiles) if it's in the same direction as the last movement OR if the player is still within the countdown from the last state of motion.
                            worldY -= speed * dt;
                            if (moveCountdown > 0) {
                                directionLast = directionCurrent;
                                moveCountdown = 0;                                                                      // Reset frame buffer.
                            }
                        }
                        break;
                    case DOWN:
                        if ((directionCurrent == directionLast) || (moveCountdown > 0)) {                               // Only move if it's in the same direction as the last movement OR if the player entity is still within the countdown from the last state of motion.
                            worldY += speed * dt;
                            if (moveCountdown > 0) {
                                directionLast = directionCurrent;
                                moveCountdown = 0;                                                                      // Reset frame buffer.
                            }
                        }
                        break;
                    case LEFT:
                        if ((directionCurrent == directionLast) || (moveCountdown > 0)) {                               // Only move if it's in the same direction as the last movement OR if the player entity is still within the fcountdown from the last state of motion.
                            worldX -= speed * dt;
                            if (moveCountdown > 0) {
                                directionLast = directionCurrent;
                                moveCountdown = 0;                                                                      // Reset frame buffer.
                            }
                        }
                        break;
                    case RIGHT:
                        if ((directionCurrent == directionLast) || (moveCountdown > 0)) {                               // Only move if it's in the same direction as the last movement OR if the player is still within the countdown from the last state of motion.
                            worldX += speed * dt;
                            if (moveCountdown > 0) {
                                directionLast = directionCurrent;
                                moveCountdown = 0;                                                                      // Reset frame buffer.
                            }
                        }
                        break;
                }
            }

            if ((turning && (worldCounter <= 16 / 2)) || (worldCounter <= GamePanel.NATIVE_TILE_SIZE / 2)) {            // Walking animation; player entity will have a foot forward for half of the world units traversed.

                if (spriteNumLast == 2) {

                    spriteNumCurrent = 3;
                } else {

                    spriteNumCurrent = 2;
                }
            } else {

                spriteNumCurrent = 1;
            }

            if ((turning && (worldCounter >= 16)) || (worldCounter >= GamePanel.NATIVE_TILE_SIZE)) {                    // Check if the world unit counter meets criteria to finish turning OR check if the player entity has moved a number of world units equal to a tile size in the current state of motion.
                moving = false;                                                                                         // If we've moved a tile's length, the player character exits a state of motion and can again be controlled.
                worldCounter = 0;                                                                                       // Reset the world unit counter.
                moveCountdown = stagedMoveCountdown;                                                                    // Provide a 2 frame buffer for the player to change direction and keep momentum upon exiting the current state of motion.

                if ((!colliding) && (!turning)) {

                    worldX = worldXEnd;                                                                                 // Set player entity position as target position (in case it was slightly overshot).
                    worldY = worldYEnd;                                                                                 // ^^^
                    worldXLast = worldXStart;                                                                           // Store the last position the player entity was at.
                    worldYLast = worldYStart;                                                                           // ^^^
                    worldXStart = worldX;                                                                               // The player entity moved a tile, so set new position.
                    worldYStart = worldY;                                                                               // ^^^
                } else {

                    worldXEnd = worldX;                                                                                 // The player entity did not move a tile, so keep original position (x).
                    worldYEnd = worldY;                                                                                 // The player entity did not move a tile, so keep original position (y).
                }

                if (turning) {                                                                                          // If the player entity was turning, exit a state of turning.

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
     * Sets loaded entity sprites.
     */
    private void setupSprite() {

        down1 = AssetPool.getSpritesheet("characters").getSprite(24);
        down2 = AssetPool.getSpritesheet("characters").getSprite(25);
        down3 = AssetPool.getSpritesheet("characters").getSprite(26);

        up1 = AssetPool.getSpritesheet("characters").getSprite(27);
        up2 = AssetPool.getSpritesheet("characters").getSprite(28);
        up3 = AssetPool.getSpritesheet("characters").getSprite(29);

        left1 = AssetPool.getSpritesheet("characters").getSprite(30);
        left2 = AssetPool.getSpritesheet("characters").getSprite(31);
        left3 = AssetPool.getSpritesheet("characters").getSprite(32);

        right1 = AssetPool.getSpritesheet("characters").getSprite(33);
        right2 = AssetPool.getSpritesheet("characters").getSprite(34);
        right3 = AssetPool.getSpritesheet("characters").getSprite(35);

        sprite = down1;
        transform.scale.x = sprite.getNativeWidth();
        transform.scale.y = sprite.getNativeHeight();
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
        setSpeed(120);

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

        // Combat moves.
        moves.add(new Mve_Tackle(gp));
        moves.add(new Mve_Punch(gp));

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
     *
     * @param dt time since last frame (seconds)
     */
    private void updateExploreInput(double dt) {

        if ((KeyListener.isKeyPressed(GLFW_KEY_SPACE)) && (!menuActioned) && (!moving)) {

            gp.setGameState(GameState.PARTY_MENU);
            menuActioned = true;                                                                                        // Disable the ability of the player to close the menu (party, inventory, settings) by pressing the Space key.
        }
        else if (!moving) {                                                                                             // If the player is moving, they will not stop until they move the tile length (for grid-based movement); so, only accept key inputs when the player is not moving.

            boolean interaction = false;                                                                                // Initialize a variable to determine if an object or npc is being interacted with or not.

            if ((KeyListener.isKeyPressed(GLFW_KEY_ENTER)) && (interactionCountdown <= 0)) {

                interaction = checkClickInteraction(dt);                                                                // Check if any interactions triggered by a click (i.e., hitting the Enter key or other manual selection) have been hit.
            }

            if (!interaction) {                                                                                         // If nothing is being interacted with, continue with logic to check if player will move.

                if ((KeyListener.isKeyPressed(GLFW_KEY_W)) || (KeyListener.isKeyPressed(GLFW_KEY_S)) ||
                        (KeyListener.isKeyPressed(GLFW_KEY_A)) || (KeyListener.isKeyPressed(GLFW_KEY_D))) {

                    if (KeyListener.isKeyPressed(GLFW_KEY_W)) {
                        directionCurrent = EntityDirection.UP;
                        directionCandidate = EntityDirection.UP;
                        worldXEnd = worldX;
                        worldYEnd = worldY - GamePanel.NATIVE_TILE_SIZE;

                    } else if (KeyListener.isKeyPressed(GLFW_KEY_S)) {
                        directionCurrent = EntityDirection.DOWN;
                        directionCandidate = EntityDirection.DOWN;
                        worldXEnd = worldX;
                        worldYEnd = worldY + GamePanel.NATIVE_TILE_SIZE;

                    } else if (KeyListener.isKeyPressed(GLFW_KEY_A)) {
                        directionCurrent = EntityDirection.LEFT;
                        directionCandidate = EntityDirection.LEFT;
                        worldXEnd = worldX - GamePanel.NATIVE_TILE_SIZE;
                        worldYEnd = worldY;

                    } else if (KeyListener.isKeyPressed(GLFW_KEY_D)) {
                        directionCurrent = EntityDirection.RIGHT;
                        directionCandidate = EntityDirection.RIGHT;
                        worldXEnd = worldX + GamePanel.NATIVE_TILE_SIZE;
                        worldYEnd = worldY;
                    }
                    moving = true;                                                                                      // When a direction key is pressed, the player character enters a state of motion.
                    worldXStart = worldX;                                                                               // Record current position before moving (x).
                    worldYStart = worldY;                                                                               // Record current position before moving (y).

                    if ((directionCurrent.equals(directionLast)) || (moveCountdown > 0)) {                              // The if statement ensures that simply changing direction to face a tile doesn't trigger an interaction.

                        checkStepInteraction(dt);                                                                       // Check if any interactions triggered by a step have been hit.
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

        if ((KeyListener.isKeyPressed(GLFW_KEY_ENTER))
                && (gp.getDialogueR().getCurrentConv() != null)
                && (!gp.getDialogueR().isReadingDialogue())
                && (interactionCountdown <= 0)) {

            if ((!gp.getDialogueR().isReadingConversation())
                    && (gp.getDialogueR().getCurrentConv().isPlayerInputToEnd())) {                                     // If no longer reading a conversation AND player input is required to end the conversation.

                if (gp.getDialogueR().getCurrentConv().getConvId() == -3) {

                    gp.getCombatM().progressCombat();                                                                   // The conversation was an interactive combat message and has finished; check what logic to run next in combat.
                    interactionCountdown = stagedMenuInteractionCountdown;                                              // Player must wait before interacting with another action, for example (prevents instantly progressing next action that appears).
                } else {

                    gp.getEventM().handlePostConversation(gp.getDialogueR().getCurrentConv().getConvId());              // Check to see if any events will be triggered once the conversation has finished.
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

            if ((KeyListener.isKeyPressed(GLFW_KEY_SPACE)) && (!menuActioned)) {
                gp.setGameState(GameState.EXPLORE);
                menuActioned = true;                                                                                    // Disable the ability of the player to open the menu (party, inventory, settings) by pressing the Space key.
            }

            else if (KeyListener.isKeyPressed(GLFW_KEY_2)) {
                gp.setGameState(GameState.INVENTORY_MENU);
                interactionCountdown = stagedMenuInteractionCountdown;
            }

            else if (KeyListener.isKeyPressed(GLFW_KEY_3)) {
                gp.setGameState(GameState.SETTINGS_MENU);
                interactionCountdown = stagedMenuInteractionCountdown;
            }

            else if (KeyListener.isKeyPressed(GLFW_KEY_W)) {
                gp.getUi().setPartySlotSelected(gp.getUi().getPartySlotSelected() - 1);
                interactionCountdown = stagedMenuInteractionCountdown;
            }

            else if (KeyListener.isKeyPressed(GLFW_KEY_S)) {
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

            if ((KeyListener.isKeyPressed(GLFW_KEY_SPACE)) && (!menuActioned)) {
                gp.setGameState(GameState.EXPLORE);
                menuActioned = true;                                                                                    // Disable the ability of the player to open the menu (party, inventory, settings) by pressing the Space key.
            }

            if (KeyListener.isKeyPressed(GLFW_KEY_1)) {
                gp.setGameState(GameState.PARTY_MENU);
                interactionCountdown = stagedMenuInteractionCountdown;
            }

            else if (KeyListener.isKeyPressed(GLFW_KEY_3)) {
                gp.setGameState(GameState.SETTINGS_MENU);
                interactionCountdown = stagedMenuInteractionCountdown;
            }

            else if (KeyListener.isKeyPressed(GLFW_KEY_W)) {
                gp.getUi().setItemRowSelected(gp.getUi().getItemRowSelected() - 1);
                interactionCountdown = stagedMenuInteractionCountdown;
            }

            else if (KeyListener.isKeyPressed(GLFW_KEY_A)) {
                gp.getUi().setItemColSelected(gp.getUi().getItemColSelected() - 1);
                interactionCountdown = stagedMenuInteractionCountdown;
            }

            else if (KeyListener.isKeyPressed(GLFW_KEY_S)) {
                gp.getUi().setItemRowSelected(gp.getUi().getItemRowSelected() + 1);
                interactionCountdown = stagedMenuInteractionCountdown;
            }

            else if (KeyListener.isKeyPressed(GLFW_KEY_D)) {
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

            if ((KeyListener.isKeyPressed(GLFW_KEY_SPACE)) && (!menuActioned)) {
                gp.setGameState(GameState.EXPLORE);
                menuActioned = true;                                                                                    // Disable the ability of the player to open the menu (party, inventory, settings) by pressing the Space key.
            }

            else if (KeyListener.isKeyPressed(GLFW_KEY_1)) {
                gp.setGameState(GameState.PARTY_MENU);
                interactionCountdown = stagedMenuInteractionCountdown;
            }

            else if (KeyListener.isKeyPressed(GLFW_KEY_2)) {
                gp.setGameState(GameState.INVENTORY_MENU);
                interactionCountdown = stagedMenuInteractionCountdown;
            }

            else if (KeyListener.isKeyPressed(GLFW_KEY_W)) {
                gp.getUi().setSystemSettingSelected(gp.getUi().getSystemSettingSelected() - 1);
                interactionCountdown = stagedMenuInteractionCountdown;
            }

            else if (KeyListener.isKeyPressed(GLFW_KEY_S)) {
                gp.getUi().setSystemSettingSelected(gp.getUi().getSystemSettingSelected() + 1);
                interactionCountdown = stagedMenuInteractionCountdown;
            }

            else if (KeyListener.isKeyPressed(GLFW_KEY_A)) {
                gp.getUi().setSystemOptionSelected(gp.getUi().getSystemOptionSelected() - 1);
                interactionCountdown = stagedMenuInteractionCountdown;
            }

            else if (KeyListener.isKeyPressed(GLFW_KEY_D)) {
                gp.getUi().setSystemOptionSelected(gp.getUi().getSystemOptionSelected() + 1);
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

            if (KeyListener.isKeyPressed(GLFW_KEY_W)) {
                gp.getSubMenuH().setIndexSelected(gp.getSubMenuH().getIndexSelected() - 1);                             // Validation for whether this is an acceptable value is done in the `setIndexSelected()` method in SubMenuHandler.
                interactionCountdown = stagedMenuInteractionCountdown;
            }

            else if (KeyListener.isKeyPressed(GLFW_KEY_S)) {
                gp.getSubMenuH().setIndexSelected(gp.getSubMenuH().getIndexSelected() + 1);                             // Validation for whether this is an acceptable value is done in the `setIndexSelected()` method in SubMenuHandler.
                interactionCountdown = stagedMenuInteractionCountdown;
            }

            else if (KeyListener.isKeyPressed(GLFW_KEY_ENTER)) {
                gp.getEventM().handlePostSubMenu(gp.getSubMenuH().getSubMenuId(), gp.getSubMenuH().getIndexSelected());
                interactionCountdown = stagedMenuInteractionCountdown;
            }
        }
    }


    /**
     * Updates the state of the debug mode by one frame.
     * Checks whether debug mode is toggled on/off and whether the camera is panned when debug mode is enabled.
     *
     */
    private void updateDebugInput() {

        if ((debugActioned) && (!KeyListener.isKeyPressed(GLFW_KEY_Q))) {
            debugActioned = false;                                                                                      // Enable the ability of the player to enable the debug mode by pressing the Q key.
        }

        if ((KeyListener.isKeyPressed(GLFW_KEY_Q)) && (!debugActioned)) {

            if ((gp.isDebugActive())
                    && (gp.getGameState() == GameState.EXPLORE)
                    && (gp.getCameraS().isOverrideEntityTracking())) {

                List<String> options = List.of("Yes", "No");                                                            // Immutable list.
                String prompt = "Reset camera back to player?";
                gp.getSubMenuS().displaySubMenuPrompt(prompt, options, 0);
            }
            gp.setDebugActive(!gp.isDebugActive());
            debugActioned = true;                                                                                       // Disable the ability of the player to enable the debug mode by pressing the Q key.
        }

        else if (gp.isDebugActive()) {

            if (KeyListener.isKeyPressed(GLFW_KEY_UP)) {
                if (!gp.getCameraS().isOverrideEntityTracking()) {
                    gp.getCameraS().setOverrideEntityTracking(true);
                }
                gp.getCamera().adjustPosition(
                        new Vector2f(
                                gp.getCamera().getPositionMatrix().x,
                                gp.getCamera().getPositionMatrix().y - 4
                        )
                );
            }

            if (KeyListener.isKeyPressed(GLFW_KEY_DOWN)) {
                if (!gp.getCameraS().isOverrideEntityTracking()) {
                    gp.getCameraS().setOverrideEntityTracking(true);
                }
                gp.getCamera().adjustPosition(
                        new Vector2f(
                                gp.getCamera().getPositionMatrix().x,
                                gp.getCamera().getPositionMatrix().y + 4
                        )
                );
            }

            if (KeyListener.isKeyPressed(GLFW_KEY_LEFT)) {
                if (!gp.getCameraS().isOverrideEntityTracking()) {
                    gp.getCameraS().setOverrideEntityTracking(true);
                }
                gp.getCamera().adjustPosition(
                        new Vector2f(
                                gp.getCamera().getPositionMatrix().x - 4,
                                gp.getCamera().getPositionMatrix().y
                        )
                );
            }

            if (KeyListener.isKeyPressed(GLFW_KEY_RIGHT)) {
                if (!gp.getCameraS().isOverrideEntityTracking()) {
                    gp.getCameraS().setOverrideEntityTracking(true);
                }
                gp.getCamera().adjustPosition(
                        new Vector2f(
                                gp.getCamera().getPositionMatrix().x + 4,
                                gp.getCamera().getPositionMatrix().y
                        )
                );
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
     * @param dt time since last frame (seconds)
     * @return whether an interaction has been triggered (true) or not (false)
     */
    private boolean checkClickInteraction(double dt) {

        boolean interaction = gp.getEventM().handleNpcInteraction(dt, EventType.CLICK);                                 // Check in an NPC is being interacted with via a click.

        if (!interaction) {

            interaction = gp.getEventM().handleObjectInteraction(dt, EventType.CLICK);                                  // If an NPC isn't being interacted with, check if an object is being interacted with via a click.
        }

        if (!interaction) {

            interaction = gp.getEventM().handlePartyInteraction(dt, EventType.CLICK);                                   // If an object isn't being interacted with, check if a party member is being interacted with via a click.
        }

        if (!interaction) {

            interaction = gp.getEventM().handleTileInteraction(dt, EventType.CLICK);                                    // If a party member isn't being interacted with, check to see if a tile is being interacted with via a click.
        }

        return interaction;
    }


    /**
     * Checks if any interactions triggered by a step have been hit.
     *
     * @param dt time since last frame (seconds)
     * @return whether an interaction has been triggered (true) or not (false)
     */
    private boolean checkStepInteraction(double dt) {

        boolean interaction = gp.getEventM().handleNpcInteraction(dt, EventType.STEP);                                  // Check in an NPC is being interacted with via a step.

        if (!interaction) {

            interaction = gp.getEventM().handleObjectInteraction(dt, EventType.STEP);                                   // If an NPC isn't being interacted with, check if an object is being interacted with via a step..
        }

        if (!interaction) {

            interaction = gp.getEventM().handleTileInteraction(dt, EventType.STEP);                                     // If an object isn't being interacted with, check if a tile is being interacted with via a step.
        }

        if (!interaction) {

            interaction = gp.getEventM().handlePartyInteraction(dt, EventType.STEP);                                    // If a tile isn't being interacted with, check to see if a party member is being interacted with via a step.
        }

        return interaction;
    }


    // GETTER
    public ArrayList<ItemBase> getInventory() {
        return inventory;
    }


    // SETTER
    public void setInteractionCountdown(double interactionCountdown) {
        this.interactionCountdown = interactionCountdown;
    }
}
