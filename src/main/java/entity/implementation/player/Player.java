package entity.implementation.player;

import combat.implementation.move.*;
import core.enumeration.PrimaryGameState;
import entity.enumeration.DefaultAction;
import event.enumeration.StockStepInteractionType;
import miscellaneous.KeyListener;
import entity.EntityBase;
import entity.enumeration.EntityDirection;
import entity.enumeration.EntityType;
import core.GamePanel;
import event.enumeration.EventType;
import item.ItemBase;
import org.joml.Vector3f;
import ui.enumeration.PrimaryMenuState;
import org.joml.Vector2f;
import asset.AssetPool;
import utility.LimitedArrayList;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import static org.lwjgl.glfw.GLFW.*;

/**
 * This class defines the player entity that the player controls.
 * Player inputs are also handled here.
 */
public class Player extends EntityBase {

    // FIELDS
    /**
     * Travel direction that this entity may move in.
     * This may differ from current direction (i.e., direction that the entity sprite is facing) (ex., entity walks
     * backwards).
     */
    private EntityDirection travelDirection;

    /**
     * Number of seconds that the player has to change direction while retaining momentum upon leaving a state of motion.
     * This lets the player entity instantly being walking in a new direction if they're already moving, versus stopping
     * and then turning.
     * The value of this variable will be set to `moveCountdown` whenever the player entity exits a state of motion.
     * This value will have a noticeable impact on the "feel" of the game while walking.
     */
    private final double stagedMoveCountdown = 0.033;

    /**
     * Standard number of seconds (i.e., "cooldown" period) that must pass before the player can press a key (same or
     * different as previous) to interact with the world or a menu option
     * Essentially, this control the sensitivity of key presses in menus, etc.
     * This value will have a noticeable impact on the "feel" of the game while in menus.
     * As a reference, note that 0.16 seconds is ~10 frames at 60 FPS.
     */
    private final double stagedStandardInteractionCountdown = 0.16;

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
        setInitialValues();                                                                                             // Set default player values when a player instance is created.
        setSprites();                                                                                                   // Load player sprites when a player instance is created.
    }


    // METHODS
    /**
     * Updates player input per the current frame.
     *
     * @param dt time since last frame (seconds)
     */
    public void updateInput(double dt) {

        if (moveCountdown > 0) {
            moveCountdown -= dt;                                                                                        // Decrease move frame countdown by one each time a new frame is drawn.
        }

        if (interactionCountdown > 0) {                                                                                 // Decrease interaction frame countdown by one each time a new frame is drawn.
            interactionCountdown -= dt;
            if ((interactionCountdown <= 0) && (gp.getDialogueR().getActiveConv() != null)) {                           // Reset dialogue arrow position to display, if applicable.
                gp.getDialogueA().reset();
            }
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
            fullScreenCountdown = stagedStandardInteractionCountdown;
        } else if ((fullScreenCountdown <= 0)
                && (gp.getSystemSetting(3).getActiveOption() == 1)
                && (KeyListener.isKeyPressed(GLFW_KEY_ESCAPE) || KeyListener.isKeyPressed(GLFW_KEY_F11))) {
            gp.getSystemSetting(3).setActiveOption(0);                                                                  // Exit full screen mode if enabled.
            if (gp.getUi().getSystemSettingSelected() == 3) {
                gp.getUi().setSystemOptionSelected(0);
            }
            fullScreenCountdown = stagedStandardInteractionCountdown;
        }

        if (!gp.isLockPlayerControl()) {

            switch (gp.getPrimaryGameState()) {
                case EXPLORE:
                    updateExploreInput(dt);
                    break;
                case DIALOGUE:
                    updateDialogueInput();
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
        }
        updateDebugInput();
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

        switch (gp.getTransitionS().getState()) {
            case FADE_TO:                                                                                               // Phase 1: Set the player to a walking sprite.
                moving = false;                                                                                         // Cancel the player movement that triggered this transition event.
                stageWalkingSprite();
                updateWorldPosition(dt);                                                                                // Update the world position of the player by one unit of its speed in the current direction.
                break;
            case ACTIVE:                                                                                                // Phase 2: Set the player to an idle sprite.
                stageIdleSprite();
                directionCurrent = directionCandidate;                                                                  // Set the direction the player will be facing when loaded into the new map.
                directionLast = directionCandidate;
                break;
        }
    }


    /**
     * Checks if there is space in the player's inventory to add a unit of an item if possible.
     * If the item is stackable, a check is done to see if it can either be added to an existing stack or used to create
     * a new stack if there's space in the inventory.
     * If the item is not stackable, a check will be done to see if there's space in the inventory to add a new item.
     *
     * @param itemId ID of item to be added
     * @return whether a unit of the item was successfully added to the player's inventory (true) or not (false)
     */
    public boolean addItemToInventory(int itemId) {

        boolean obtainable = false;

        if (gp.getItemM().checkStackable(itemId)) {                                                                     // Logic for if the item is stackable.

            int index = searchPartiallyFilledItemStack(itemId);                                                         // If the item is already in the player's inventory, retrieve the index that the item occupies in the `inventory` array; this is NOT the item ID.

            if (index != -1) {                                                                                          // Item already exists in the inventory, so try to stack it.

                int currentAmount = inventory.get(index).getAmount();                                                   // Get the amount of the item currently in the player's inventory.

                if (currentAmount != inventory.get(index).getMaxStackAmount()) {                                        // Only add another of the item if the maximum stackable amount hasn't already been reached.

                    inventory.get(index).setAmount(currentAmount + 1);                                                  // Add another of the item to the player's inventory.
                    obtainable = true;
                }
            } else {                                                                                                    // Item does not already exist in the inventory, so begin a new stack.

                if (inventory.size() != inventory.maxCapacity()) {                                                      // If the player's inventory is already full, don't add the item.

                    inventory.add(gp.getItemM().retrieveNewInstance(itemId));
                    obtainable = true;
                }
            }
        } else {                                                                                                        // Logic for if the item is not stackable.

            if (inventory.size() != inventory.maxCapacity()) {                                                          // If the player's inventory is already full, don't add the item.

                inventory.add(gp.getItemM().retrieveNewInstance(itemId));
                obtainable = true;
            }
        }
        return obtainable;
    }


    /**
     * Checks if there is an instance of an item to remove a unit from in the player's inventory.
     *
     * @param itemId ID of item to be removed
     * @return whether a unit of the item was successfully removed to the player's inventory (true) or not (false)
     */
    public boolean removeItemFromInventory(int itemId) {

        boolean removable = false;

        int index = searchLastInstanceOfItem(itemId);

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


    /**
     * Searches the player's inventory to see if a particular item is present.
     *
     * @param itemId ID of the target item
     * @return whether the item is present (true) or not (false)
     */
    public boolean searchItemInventory(int itemId) {

        boolean present = false;

        for (ItemBase item : inventory) {

            if (item.getItemId() == itemId) {

                present = true;
                break;
            }
        }
        return present;
    }


    @Override
    protected void updateMotionState(double dt) {

        if (moving) {                                                                                                   // This will execute if the player entity is currently in a state of motion.

            worldCounter += speed * dt;                                                                                 // Add to the number of world units the player entity has moved while in the current state of motion.

            if (!colliding) {                                                                                           // If collision is false, the player can move.

                if (worldXStart == worldXEnd) {

                    if (worldYStart > worldYEnd) {

                        travelDirection = EntityDirection.UP;
                    } else {

                        travelDirection = EntityDirection.DOWN;
                    }
                } else {

                    if (worldXStart > worldXEnd) {

                        travelDirection = EntityDirection.LEFT;
                    } else {

                        travelDirection = EntityDirection.RIGHT;
                    }
                }

                if (travelDirection == directionCurrent) {                                                              // Player entity is walking forwards; the player may be controlling movement.

                    switch (directionCurrent) {
                        case UP:
                            if ((directionCurrent == directionLast) || (moveCountdown > 0)) {                           // Only move (i.e., change tiles) if it's in the same direction as the last movement OR if the player is still within the countdown from the last state of motion.
                                worldY -= speed * dt;
                                if (moveCountdown > 0) {
                                    directionLast = directionCurrent;
                                    moveCountdown = 0;                                                                  // Reset frame buffer.
                                }
                            }
                            break;
                        case DOWN:
                            if ((directionCurrent == directionLast) || (moveCountdown > 0)) {                           // Only move if it's in the same direction as the last movement OR if the player entity is still within the countdown from the last state of motion.
                                worldY += speed * dt;
                                if (moveCountdown > 0) {
                                    directionLast = directionCurrent;
                                    moveCountdown = 0;                                                                  // Reset frame buffer.
                                }
                            }
                            break;
                        case LEFT:
                            if ((directionCurrent == directionLast) || (moveCountdown > 0)) {                           // Only move if it's in the same direction as the last movement OR if the player entity is still within the fcountdown from the last state of motion.
                                worldX -= speed * dt;
                                if (moveCountdown > 0) {
                                    directionLast = directionCurrent;
                                    moveCountdown = 0;                                                                  // Reset frame buffer.
                                }
                            }
                            break;
                        case RIGHT:
                            if ((directionCurrent == directionLast) || (moveCountdown > 0)) {                           // Only move if it's in the same direction as the last movement OR if the player is still within the countdown from the last state of motion.
                                worldX += speed * dt;
                                if (moveCountdown > 0) {
                                    directionLast = directionCurrent;
                                    moveCountdown = 0;                                                                  // Reset frame buffer.
                                }
                            }
                            break;
                    }
                } else {                                                                                                // Player entity may be walking backwards or sideways; it is not possible for the player to control movement in this way (i.e., non-forward movement only possible when scripted).

                    updateWorldPosition(dt);
                }
            }

            if ((turning && (worldCounter <= 16 / 2)) || (worldCounter <= GamePanel.NATIVE_TILE_SIZE / 2)) {            // Walking animation; player entity will have a foot forward for half of the world units traversed.

                if (walkSpriteNumLast == 2) {

                    walkSpriteNumCurrent = 3;
                } else {

                    walkSpriteNumCurrent = 2;
                }
            } else {

                walkSpriteNumCurrent = 1;
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

                if (walkSpriteNumLast == 2) {                                                                           // Swap which foot will step forward for the next walking cycle.

                    walkSpriteNumLast = 3;
                } else {

                    walkSpriteNumLast = 2;
                }
            }
        }
    }


    @Override
    protected void setSprites() {

        idleDown = AssetPool.getSpritesheet("characters").getSprite(0);
        walkDown1 = AssetPool.getSpritesheet("characters").getSprite(1);
        walkDown2 = AssetPool.getSpritesheet("characters").getSprite(2);

        idleUp = AssetPool.getSpritesheet("characters").getSprite(3);
        walkUp1 = AssetPool.getSpritesheet("characters").getSprite(4);
        walkUp2 = AssetPool.getSpritesheet("characters").getSprite(5);

        idleLeft = AssetPool.getSpritesheet("characters").getSprite(6);
        walkLeft1 = AssetPool.getSpritesheet("characters").getSprite(7);
        walkLeft2 = AssetPool.getSpritesheet("characters").getSprite(8);

        idleRight = AssetPool.getSpritesheet("characters").getSprite(9);
        walkRight1 = AssetPool.getSpritesheet("characters").getSprite(10);
        walkRight2 = AssetPool.getSpritesheet("characters").getSprite(11);

        combatStanceLeft1 = AssetPool.getSpritesheet("characters").getSprite(12);
        combatStanceLeft2 = AssetPool.getSpritesheet("characters").getSprite(13);

        combatStanceRight1 = AssetPool.getSpritesheet("characters").getSprite(15);
        combatStanceRight2 = AssetPool.getSpritesheet("characters").getSprite(16);

        combatAttackLeft = AssetPool.getSpritesheet("characters").getSprite(14);
        combatAttackRight = AssetPool.getSpritesheet("characters").getSprite(17);

        combatFaintLeft1_1 = AssetPool.getSpritesheet("characters").getSprite(18);
        combatFaintLeft2_1 = AssetPool.getSpritesheet("characters").getSprite(20);
        combatFaintLeft3_1 = AssetPool.getSpritesheet("characters").getSprite(22);
        combatFaintLeft4_1 = AssetPool.getSpritesheet("characters").getSprite(24);
        combatFaintLeft5_1 = AssetPool.getSpritesheet("characters").getSprite(26);
        combatFaintLeft6_1 = AssetPool.getSpritesheet("characters").getSprite(28);

        combatFaintLeft1_2 = AssetPool.getSpritesheet("characters").getSprite(19);
        combatFaintLeft2_2 = AssetPool.getSpritesheet("characters").getSprite(21);
        combatFaintLeft3_2 = AssetPool.getSpritesheet("characters").getSprite(23);
        combatFaintLeft4_2 = AssetPool.getSpritesheet("characters").getSprite(25);
        combatFaintLeft5_2 = AssetPool.getSpritesheet("characters").getSprite(27);
        combatFaintLeft6_2 = AssetPool.getSpritesheet("characters").getSprite(29);

        combatFaintRight1_1 = AssetPool.getSpritesheet("characters").getSprite(30);
        combatFaintRight2_1 = AssetPool.getSpritesheet("characters").getSprite(32);
        combatFaintRight3_1 = AssetPool.getSpritesheet("characters").getSprite(34);
        combatFaintRight4_1 = AssetPool.getSpritesheet("characters").getSprite(36);
        combatFaintRight5_1 = AssetPool.getSpritesheet("characters").getSprite(38);
        combatFaintRight6_1 = AssetPool.getSpritesheet("characters").getSprite(40);

        combatFaintRight1_2 = AssetPool.getSpritesheet("characters").getSprite(31);
        combatFaintRight2_2 = AssetPool.getSpritesheet("characters").getSprite(33);
        combatFaintRight3_2 = AssetPool.getSpritesheet("characters").getSprite(35);
        combatFaintRight4_2 = AssetPool.getSpritesheet("characters").getSprite(37);
        combatFaintRight5_2 = AssetPool.getSpritesheet("characters").getSprite(39);
        combatFaintRight5_2 = AssetPool.getSpritesheet("characters").getSprite(41);

        sprite = idleDown;
        transform.scale.x = sprite.getNativeWidth();
        transform.scale.y = sprite.getNativeHeight();
    }


    /**
     * Sets initial values for the player entity.
     */
    private void setInitialValues() {

        // World position.
        setCol(47); //6
        setRow(17);//45

        // Direction.
        setDirectionCurrent(EntityDirection.UP);

        // Basic attributes.
        setName("Mary");
        setSpeed(120);

        // Combat attributes.
        setMaxLife(300);
        setLife(300);
        setMaxSkill(50);
        setSkill(50);
        setBaseAttack(75);
        setBaseDefense(85);
        setBaseMagic(20);
        setBaseAgility(120);

        // Combat moves.
        moves.add(new Mve_Pickpocket(gp));
        moves.add(new Mve_ButterflyBlade(gp));
        moves.add(new Mve_BurningDagger(gp));
        moves.add(new Mve_Sneakstrike(gp));

        // Items.
//        for (int i = 0; i < 600; i++) {
//            addItemToInventory(0);
//        }
//        for (int i = 0; i < 1; i++) {
//            addItemToInventory(1);
//        }
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

            gp.setPrimaryGameState(PrimaryGameState.PARTY_MENU);
            gp.getUi().setPrimaryMenuState(PrimaryMenuState.PARTY);
            menuActioned = true;                                                                                        // Disable the ability of the player to close the menu (party, inventory, settings) by pressing the Space key.
        }
        else if (!moving) {                                                                                             // If the player is moving, they will not stop until they move the tile length (for grid-based movement); so, only accept key inputs when the player is not moving.

            boolean interaction = false;                                                                                // Initialize a variable to determine if an object or npc is being interacted with or not.

            if ((KeyListener.isKeyPressed(GLFW_KEY_ENTER)
                    || ((gp.getSystemSetting(4).getActiveOption() == 1) && KeyListener.isKeyPressed(GLFW_KEY_E)))
                    && (interactionCountdown <= 0)) {

                interaction = checkClickInteraction(dt, directionCurrent);                                              // Check if any interactions triggered by a click (i.e., hitting the Enter key or other manual selection) have been hit.
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
                    updateCollisionState();                                                                             // Check and update colliding state of entity.

                    if ((directionCurrent.equals(directionLast)) || (moveCountdown > 0)) {                              // The if statement ensures that simply changing direction to face a tile doesn't trigger an interaction.

                        checkStepInteraction(dt, getColEnd(), getRowEnd());                                             // Check if any interactions triggered by a step have been hit.
                    } else {

                        turning = true;                                                                                 // Enter a state of turning since the frame buffer has lapsed (meaning the player is currently static) AND the new direction is different from the last.
                    }
                } else {                                                                                                // If player is not moving, set idle sprite.

                    walkSpriteNumCurrent = 1;
                }
            }
        }
    }


    /**
     * Updates the state of the dialogue screen per player input by one frame when in dialogue state.
     * Checks for player key input if dialogue needs to be progressed.
     */
    private void updateDialogueInput() {

        if ((KeyListener.isKeyPressed(GLFW_KEY_ENTER)
                || ((gp.getSystemSetting(4).getActiveOption() == 1) && KeyListener.isKeyPressed(GLFW_KEY_E)))
                && (gp.getDialogueR().getActiveConv() != null)
                && (!gp.getDialogueR().isReadingDialogue())
                && (interactionCountdown <= 0)) {

            if ((!gp.getDialogueR().isReadingConversation())
                    && (gp.getDialogueR().getActiveConv().isPlayerInputToEnd())) {                                      // If no longer reading a conversation AND player input is required to end the conversation.

                if (gp.getDialogueR().getActiveConv().getConvId() == -3) {                                              // Check if the conversation was an interactive combat message that has finished;

                    gp.getDialogueR().convertToPlaceholderMessage();                                                    // Convert to placeholder message to ensure that `progressCombat()` can only be triggered by player input from this message once.
                    setInteractionCountdown(stagedStandardInteractionCountdown);                                        // Player must wait before interacting with another combat action, for example (prevents instantly progressing next action that appears).
                    gp.getCombatM().progressCombat();                                                                   // Check what logic to run next in combat (i.e., progress combat loop).
                } else {

                    setInteractionCountdown(stagedStandardInteractionCountdown);
                    gp.getEventM().handlePostConversation(gp.getDialogueR().getActiveConv().getConvId());               // Check if any events will be triggered once the conversation has finished.
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
                gp.setPrimaryGameState(PrimaryGameState.EXPLORE);
                gp.getUi().setPrimaryMenuState(PrimaryMenuState.INACTIVE);
                menuActioned = true;                                                                                    // Disable the ability of the player to open the menu (party, inventory, settings) by pressing the Space key.
            }

            else if (KeyListener.isKeyPressed(GLFW_KEY_ENTER)) {

                generatePartySwapSubMenuPrompt();
            }

            else if (KeyListener.isKeyPressed(GLFW_KEY_Q)) {
                gp.setPrimaryGameState(PrimaryGameState.SETTINGS_MENU);
                gp.getUi().setPrimaryMenuState(PrimaryMenuState.SETTINGS);
                setInteractionCountdown(stagedStandardInteractionCountdown);
            }

            else if ((gp.getSystemSetting(4).getActiveOption() == 0) && KeyListener.isKeyPressed(GLFW_KEY_E)) {
                gp.setPrimaryGameState(PrimaryGameState.INVENTORY_MENU);
                gp.getUi().setPrimaryMenuState(PrimaryMenuState.INVENTORY);
                setInteractionCountdown(stagedStandardInteractionCountdown);
            }

            else if (KeyListener.isKeyPressed(GLFW_KEY_W)) {
                if (gp.getUi().getPartySlotSelected() == 0) {
                    gp.getUi().setPartyMenuScrollLevel(gp.getUi().getPartyMenuScrollLevel() - 1);
                } else {
                    gp.getUi().setPartySlotSelected(gp.getUi().getPartySlotSelected() - 1);
                }
                setInteractionCountdown(stagedStandardInteractionCountdown);
            }

            else if (KeyListener.isKeyPressed(GLFW_KEY_S)) {
                if (gp.getUi().getPartySlotSelected() == 2) {
                    gp.getUi().setPartyMenuScrollLevel(gp.getUi().getPartyMenuScrollLevel() + 1);
                } else {
                    gp.getUi().setPartySlotSelected(gp.getUi().getPartySlotSelected() + 1);
                }
                setInteractionCountdown(stagedStandardInteractionCountdown);
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
                gp.setPrimaryGameState(PrimaryGameState.EXPLORE);
                gp.getUi().setPrimaryMenuState(PrimaryMenuState.INACTIVE);
                menuActioned = true;                                                                                    // Disable the ability of the player to open the menu (party, inventory, settings) by pressing the Space key.
            }

            if (KeyListener.isKeyPressed(GLFW_KEY_Q)) {
                gp.setPrimaryGameState(PrimaryGameState.PARTY_MENU);
                gp.getUi().setPrimaryMenuState(PrimaryMenuState.PARTY);
                setInteractionCountdown(stagedStandardInteractionCountdown);
            }

            else if ((gp.getSystemSetting(4).getActiveOption() == 0) && KeyListener.isKeyPressed(GLFW_KEY_E)) {
                gp.setPrimaryGameState(PrimaryGameState.SETTINGS_MENU);
                gp.getUi().setPrimaryMenuState(PrimaryMenuState.SETTINGS);
                setInteractionCountdown(stagedStandardInteractionCountdown);
            }

            else if (KeyListener.isKeyPressed(GLFW_KEY_W)) {
                gp.getUi().setItemRowSelected(gp.getUi().getItemRowSelected() - 1);
                setInteractionCountdown(stagedStandardInteractionCountdown);
            }

            else if (KeyListener.isKeyPressed(GLFW_KEY_A)) {
                gp.getUi().setItemColSelected(gp.getUi().getItemColSelected() - 1);
                setInteractionCountdown(stagedStandardInteractionCountdown);
            }

            else if (KeyListener.isKeyPressed(GLFW_KEY_S)) {
                gp.getUi().setItemRowSelected(gp.getUi().getItemRowSelected() + 1);
                setInteractionCountdown(stagedStandardInteractionCountdown);
            }

            else if (KeyListener.isKeyPressed(GLFW_KEY_D)) {
                gp.getUi().setItemColSelected(gp.getUi().getItemColSelected() + 1);
                setInteractionCountdown(stagedStandardInteractionCountdown);
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
                gp.setPrimaryGameState(PrimaryGameState.EXPLORE);
                gp.getUi().setPrimaryMenuState(PrimaryMenuState.INACTIVE);
                menuActioned = true;                                                                                    // Disable the ability of the player to open the menu (party, inventory, settings) by pressing the Space key.
            }

            else if ((gp.getSystemSetting(4).getActiveOption() == 0) && KeyListener.isKeyPressed(GLFW_KEY_E)) {
                gp.setPrimaryGameState(PrimaryGameState.PARTY_MENU);
                gp.getUi().setPrimaryMenuState(PrimaryMenuState.PARTY);
                setInteractionCountdown(stagedStandardInteractionCountdown);
            }

            else if (KeyListener.isKeyPressed(GLFW_KEY_Q)) {
                gp.setPrimaryGameState(PrimaryGameState.INVENTORY_MENU);
                gp.getUi().setPrimaryMenuState(PrimaryMenuState.INVENTORY);
                setInteractionCountdown(stagedStandardInteractionCountdown);
            }

            else if (KeyListener.isKeyPressed(GLFW_KEY_W)) {
                gp.getUi().setSystemSettingSelected(gp.getUi().getSystemSettingSelected() - 1);
                setInteractionCountdown(stagedStandardInteractionCountdown);
            }

            else if (KeyListener.isKeyPressed(GLFW_KEY_S)) {
                gp.getUi().setSystemSettingSelected(gp.getUi().getSystemSettingSelected() + 1);
                setInteractionCountdown(stagedStandardInteractionCountdown);
            }

            else if (KeyListener.isKeyPressed(GLFW_KEY_A)) {
                gp.getUi().setSystemOptionSelected(gp.getUi().getSystemOptionSelected() - 1);
                setInteractionCountdown(stagedStandardInteractionCountdown);
            }

            else if (KeyListener.isKeyPressed(GLFW_KEY_D)) {
                gp.getUi().setSystemOptionSelected(gp.getUi().getSystemOptionSelected() + 1);
                setInteractionCountdown(stagedStandardInteractionCountdown);
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
                if (gp.getCombatM().isCombatActive()) {gp.getCombatM().refreshSkillSubMenuDialogue();}                  // Refresh skill combat sub-menu message, if applicable.
                setInteractionCountdown(stagedStandardInteractionCountdown);
            }

            else if (KeyListener.isKeyPressed(GLFW_KEY_S)) {
                gp.getSubMenuH().setIndexSelected(gp.getSubMenuH().getIndexSelected() + 1);                             // Validation for whether this is an acceptable value is done in the `setIndexSelected()` method in SubMenuHandler.
                if (gp.getCombatM().isCombatActive()) {gp.getCombatM().refreshSkillSubMenuDialogue();}                  // Refresh skill combat sub-menu message, if applicable.
                setInteractionCountdown(stagedStandardInteractionCountdown);
            }

            else if (KeyListener.isKeyPressed(GLFW_KEY_ENTER)
                    || ((gp.getSystemSetting(4).getActiveOption() == 1) && KeyListener.isKeyPressed(GLFW_KEY_E))) {
                gp.getEventM().handlePostSubMenu(gp.getSubMenuH().getSubMenuId(), gp.getSubMenuH().getIndexSelected());
                setInteractionCountdown(stagedStandardInteractionCountdown);
            }
        }
    }


    /**
     * Updates the state of the debug mode by one frame.
     * Checks whether debug mode is toggled on/off and whether the camera is panned when debug mode is enabled.
     *
     */
    private void updateDebugInput() {

        if ((debugActioned) && (!KeyListener.isKeyPressed(GLFW_KEY_P))) {
            debugActioned = false;                                                                                      // Enable the ability of the player to enable the debug mode by pressing the P key.
        }

        if ((KeyListener.isKeyPressed(GLFW_KEY_P)) && (!debugActioned)) {

            if ((gp.isDebugActive())
                    && (gp.getPrimaryGameState() == PrimaryGameState.EXPLORE)
                    && (gp.getCameraS().isOverrideEntityTracking())) {

                List<String> options = List.of("Yes", "No");                                                            // Immutable list.
                String prompt = "Reset camera back to tracked entity?";
                gp.getSubMenuS().displaySubMenuPrompt(prompt, options, 2, false);
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
     * @param direction direction with respect to this entity of tile being check for click interaction
     * @return whether an interaction has been triggered (true) or not (false)
     */
    private boolean checkClickInteraction(double dt, EntityDirection direction) {

        int targetCol = getCol();
        int targetRow = getRow();

        switch (direction) {
            case UP:
                targetRow -= 1;
                break;
            case DOWN:
                targetRow += 1;
                break;
            case LEFT:
                targetCol -= 1;
                break;
            case RIGHT:
                targetCol += 1;
                break;
        }

        boolean interaction = gp.getEventM().handleNpcInteraction(dt, EventType.CLICK, targetCol, targetRow);           // Check in an NPC is being interacted with via a click.

        if (!interaction) {

            interaction = gp.getEventM().handleObjectInteraction(dt, EventType.CLICK, targetCol, targetRow);            // If an NPC isn't being interacted with, check if an object is being interacted with via a click.
        }

        if (!interaction) {

            interaction = gp.getEventM().handlePartyInteraction(dt, EventType.CLICK, targetCol, targetRow);             // If an object isn't being interacted with, check if a party member is being interacted with via a click.
        }

        if (!interaction) {

            interaction = gp.getEventM().handleTileInteraction(dt, EventType.CLICK, targetCol, targetRow);              // If a party member isn't being interacted with, check to see if a tile is being interacted with via a click.
        }

        return interaction;
    }


    /**
     * Checks if any interactions triggered by a step have been hit.
     *
     * @param targetCol column of the tile being checked
     * @param targetRow row of the tile being checked
     * @param dt time since last frame (seconds)
     * @return whether an interaction has been triggered (true) or not (false)
     */
    private boolean checkStepInteraction(double dt, int targetCol, int targetRow) {

        boolean interaction = gp.getEventM().handleNpcInteraction(dt, EventType.STEP, targetCol, targetRow);            // Check in an NPC is being interacted with via a step.

        if (!interaction) {

            interaction = gp.getEventM().handleObjectInteraction(dt, EventType.STEP, targetCol, targetRow);             // If an NPC isn't being interacted with, check if an object is being interacted with via a step.
        }

        if (!interaction) {

            interaction = gp.getEventM().handleTileInteraction(dt, EventType.STEP, targetCol, targetRow);               // If an object isn't being interacted with, check if a tile is being interacted with (map-specific) via a step.
        }

        if (!interaction) {

            interaction = gp.getEventM().handlePartyInteraction(dt, EventType.STEP, targetCol, targetRow);              // If a tile isn't being interacted with, check to see if a party member is being interacted with via a step.
        }

        gp.getEventM().handleStockStepInteraction(targetCol, targetRow, entityId,
                StockStepInteractionType.GRASS_RUSTLE);                                                                 // Check if a tile is being interacted with (type-specific) via a step; may occur regardless of other interactions occurring via a step.

        gp.getEventM().handleStockStepInteraction(targetCol, targetRow, entityId,
                StockStepInteractionType.LEDGE_HOP);                                                                    // ^^^

        return interaction;
    }


    /**
     * Generates a sub-menu prompt for swapping party members in the party menu screen.
     */
    private void generatePartySwapSubMenuPrompt() {

        if ((gp.getEntityM().getParty().size() > 1)
                && !((gp.getUi().getPartyMenuScrollLevel() == 0) && (gp.getUi().getPartySlotSelected() == 0))           // Ensure that player entity is not selected (i.e., `partyMenuScrollLevel` and `partySlotSelected` do not both equal zero).
                && gp.getPartyS().isActionComplete()) {                                                                 // Only generate if no entities party management operation is already occurring.

            List<String> options = new ArrayList<>();
            Set<Integer> keySet = gp.getEntityM().getParty().keySet();                                                  // Extract keys from party map.
            Integer[] keyArray = keySet.toArray(new Integer[keySet.size()]);                                            // Convert set of keys to array of keys.
            EntityBase selectedEntity =
                    gp.getEntityM().getParty().get(keyArray[
                            gp.getUi().getPartyMenuScrollLevel() + (gp.getUi().getPartySlotSelected() - 1)]);

            for (EntityBase candidateEntity : gp.getEntityM().getParty().values()) {

                if (candidateEntity.getEntityId() != selectedEntity.getEntityId()) {

                    options.add(candidateEntity.getName());
                }
            }
            options.add("Cancel");
            HashMap<Integer, Vector3f> colors = new HashMap<>();
            colors.put(options.size() - 1, new Vector3f(255, 46, 102));
            String prompt = "Swap " + selectedEntity.getName() + " with who?";
            gp.getSubMenuS().displaySubMenuPrompt(prompt, options, 3, false, colors);
        }
    }


    // GETTERS
    public double getStagedStandardInteractionCountdown() {
        return stagedStandardInteractionCountdown;
    }

    public double getInteractionCountdown() {
        return interactionCountdown;
    }

    public ArrayList<ItemBase> getInventory() {
        return inventory;
    }


    // SETTER
    public void setInteractionCountdown(double interactionCountdown) {
        if (this.interactionCountdown < interactionCountdown) {                                                         // Ensures that previous set countdown does not end prematurely.
            this.interactionCountdown = interactionCountdown;
        }
    }
}
