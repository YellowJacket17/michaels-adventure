package event;

import event.enumeration.EventType;
import event.enumeration.StockStepInteractionType;
import event.implementation.submenu.Evt_SubMenu003;
import miscellaneous.CollisionInspector;
import core.enumeration.PrimaryGameState;
import entity.EntityBase;
import core.GamePanel;
import entity.enumeration.EntityDirection;
import event.implementation.map.Evt_Map001;
import event.implementation.submenu.Evt_SubMenu002;
import event.implementation.submenu.Evt_SubMenu001;

import java.util.ArrayList;
import java.util.List;

/**
 * This class is used to control events triggered by player interaction with the world.
 * The public methods in this class serve as primary endpoints to use when programming in-game events.
 */
public class EventManager {

    // BASIC FIELDS
    private final GamePanel gp;

    /**
     * Argument to be passed when more than 15 entities were found when checking an entity chain.
     */
    public static final int ENTITY_CHAIN_OVERFLOW = 90775748;


    // MAP EVENT FIELDS
    private final Evt_Map001 evt_map001;


    // CONVERSATION EVENT FIELDS
    // None here... yet.


    // SUB-MENU EVENT FIELDS
    private final Evt_SubMenu001 evt_subMenu001;
    private final Evt_SubMenu002 evt_subMenu002;
    private final Evt_SubMenu003 evt_subMenu003;


    // CONSTRUCTOR
    /**
     * Constructs an EventManager instance
     *
     * @param gp GamePanel instance
     */
    public EventManager(GamePanel gp) {
        this.gp = gp;

        evt_map001 = new Evt_Map001(gp);

        // No conversation events here... yet.

        evt_subMenu001 = new Evt_SubMenu001(gp);
        evt_subMenu002 = new Evt_SubMenu002(gp);
        evt_subMenu003 = new Evt_SubMenu003(gp);
    }


    // METHODS
    /**
     * Handles what to do if the player interacts with an NPC character entity that may trigger a unique event.
     *
     * @param dt time since last frame (seconds)
     * @param type whether an event is triggered by a click or step
     * @param targetCol column of the tile being checked
     * @param targetRow row of the tile being checked
     * @return whether an NPC event was triggered (true) or not (false)
     */
    public boolean handleNpcInteraction(double dt, EventType type, int targetCol, int targetRow) {

        int entityId = gp.getCollisionI().calculateCollisionEntity(
                targetCol, targetRow, gp.getEntityM().getPlayer(), false, true, false, true, true);                     // If there's collision with an NPC (i.e., NPC in front of player), retrieve the entity ID of the NPC.

        if (entityId != CollisionInspector.NO_COLLISION) {

            EntityBase target = gp.getEntityM().getNpc().get(entityId);                                                 // Retrieve the entity corresponding with the returned entity ID.

            if (!target.isMoving()) {                                                                                   // An NPC can only be interacted with if it's not currently in a state of motion.

                switch (gp.getMapM().getLoadedMap().getMapId()) {                                                       // Switch which map to check for interaction events on depending on the current loaded map.
                    case 1:
                        return evt_map001.npcInteraction(dt, type, target);
                }
            }
        }
        return false;
    }


    /**
     * Handles what to do if the player interacts with an object entity that may trigger a unique event.
     *
     * @param dt time since last frame (seconds)
     * @param type whether an event is triggered by a click or step
     * @param targetCol column of the tile being checked
     * @param targetRow row of the tile being checked
     * @return whether an object event was triggered (true) or not (false)
     */
    public boolean handleObjectInteraction(double dt, EventType type, int targetCol, int targetRow) {

        int entityId = gp.getCollisionI().calculateCollisionEntity(
                targetCol, targetRow, gp.getEntityM().getPlayer(), false, true, true, false, true);                     // If there's collision with an object (i.e., object in front of player entity), retrieve the entity ID of the object.

        if (entityId != CollisionInspector.NO_COLLISION) {

            EntityBase target = gp.getEntityM().getObj().get(entityId);                                                 // Retrieve the entity corresponding with the returned entity ID.

            if (!target.isMoving()) {                                                                                   // An object can only be interacted with if it's not currently in a state of motion.

                switch (gp.getMapM().getLoadedMap().getMapId()) {                                                       // Switch which map to check for interaction events on depending on the current loaded map.
                    case 1:
                        return evt_map001.objInteraction(dt, type, target);
                }
            }
        }
        return false;
    }


    /**
     * Handles what to do if the player interacts with a party member character entity that may trigger a unique event.
     *
     * @param dt time since last frame (seconds)
     * @param type whether an event is triggered by a click or step
     * @param targetCol column of the tile being checked
     * @param targetRow row of the tile being checked
     * @return whether a party member event was triggered (true) or not (false)
     */
    public boolean handlePartyInteraction(double dt, EventType type, int targetCol, int targetRow) {

        int entityId = gp.getCollisionI().calculateCollisionEntity(
                targetCol, targetRow, gp.getEntityM().getPlayer(), false, true, true, true, false);                     // If there's collision with a party member (i.e., party member in front of player entity), retrieve the entity ID of the party member.

        if (entityId != CollisionInspector.NO_COLLISION) {

            EntityBase target = gp.getEntityM().getParty().get(entityId);                                               // Retrieve the entity corresponding with the returned entity ID.

            if (!target.isMoving()) {                                                                                   // A party member can only be interacted with if it's not currently in a state of motion.

                switch (gp.getMapM().getLoadedMap().getMapId()) {                                                       // Switch which map to check for interaction events on depending on the current loaded map.
                    case 1:
                        return evt_map001.partyInteraction(dt, type, target);
                }
            }
        }
        return false;
    }


    /**
     * Handles what to do if the player interacts with a tile that may trigger a unique event.
     *
     * @param dt time since last frame (seconds)
     * @param type whether an event is triggered by a click or step
     * @param targetCol column of the tile being checked
     * @param targetRow row of the tile being checked
     * @return whether a tile event (map-specific and/or tile-specific) was triggered (true) or not (false)
     */
    public boolean handleTileInteraction(double dt, EventType type, int targetCol, int targetRow) {

        if ((type == EventType.STEP) && (gp.getEntityM().getPlayer().isColliding())) {                                  // Ensure that the player entity can occupy the target tile.

            return false;
        }

        // Map-specific events.
        switch (gp.getMapM().getLoadedMap().getMapId()) {                                                               // Switch which map to check for interaction events on depending on the current loaded map.
                case 1:
                    return evt_map001.tileInteraction(dt, type, targetCol, targetRow,
                            gp.getEntityM().getPlayer().getDirectionCurrent());
            }

        return false;
    }


    /**
     * Handles what to do if an entity steps onto a tile that may trigger a stock event.
     * These types of events are intended to occur whenever any tile/landmark of a particular type is interacted with.
     * An example is a grass landmark rustling each time an entity takes a step through it.
     * Another example is a puddle tile making a splashing sound effect each time an entity takes a step through it.
     *
     * @param targetCol column of the tile being checked
     * @param targetRow row of the tile being checked
     * @param entityId ID of interacting entity
     * @param type type of stock step interaction to query
     * @return whether a stock step interaction was/may be triggered (true) or not (false)
     */
    public boolean handleStockStepInteraction(int targetCol, int targetRow, int entityId,
                                              StockStepInteractionType type) {

        switch (type) {
            case GRASS_RUSTLE:
                return handleStockStepInteractionTallGrass(targetCol, targetRow, entityId);
            case LEDGE_HOP:
                return handleStockStepInteractionLedge(targetCol, targetRow, entityId);
        }

        return false;
    }


    /**
     * Handles any further logic that may be executed after a conversation ends.
     * If not, all entities are removed from a conversing state and the primary game state is set to explore.
     *
     * @param convId ID of the conversation that just ended
     */
    public void handlePostConversation(int convId) {

        switch (convId) {
            case 0:
                cleanupConversation(3);
                break;
            case 1:
                cleanupConversation(3);
                break;
            case 2:
                cleanupConversation(3);
                break;
            case 3:
                cleanupConversation(3);
                break;
            case 4:
                cleanupConversation(3);
                break;
            case 5:
                cleanupConversation(3);
                break;
            case 6:
                cleanupConversation(3);
                break;
            default:
                cleanupConversation(1);
        }
    }


    /**
     * Handles what logic should be run based on the selection the player made in a sub-menu.
     * If a valid sub-menu ID is not passed, the primary game state defaults to an explore state and nothing else
     * happens.
     *
     * @param subMenuId ID of sub-menu being handled
     * @param selectedIndex index of selected sub-menu option
     */
    public void handlePostSubMenu(int subMenuId, int selectedIndex) {

        if (!gp.getSubMenuH().getDisabledOptions().contains(selectedIndex)) {

            switch (subMenuId) {
                case 1:
                    evt_subMenu001.run(selectedIndex);
                    break;
                case 2:
                    evt_subMenu002.run(selectedIndex);
                    break;
                case 3:
                    evt_subMenu003.run(selectedIndex);
                    break;
                default:
                    cleanupSubmenu(1);
            }
        }
    }


    /**
     * Sets the player entity and a target entity to a conversing state.
     * If either the player or target entity is already in a conversing state, it will remain as such.
     *
     * @param target ID of the entity entering a conversation with the player entity
     */
    public void setConversing(EntityBase target) {

        if (!gp.getEntityM().getPlayer().isConversing()) {

            gp.getEntityM().getPlayer().setConversing(true);                                                            // The player has begun conversing with an entity.
        }

        if (!target.isConversing()) {

            target.setConversing(true);                                                                                 // An NPC has begun conversing with the player.
        }
    }


    /**
     * Initiates a player conversation with a target entity.
     * Both the target entity and player entity will be set to face each other.
     *
     * @param target ID of the entity entering a conversation with the player entity
     * @param convId ID of the conversation to be initiated
     */
    public void talkToNpc(EntityBase target, int convId) {

        setConversing(target);                                                                                          // Sets both NPC and player entity to states of conversing.
        EntityBase player = gp.getEntityM().getPlayer();

        if ((target.getRow() > player.getRow()) && (target.getCol() == player.getCol())) {                              // Have both NPC and player entity face eac other.
            target.setDirectionCurrent(EntityDirection.UP);
            gp.getEntityM().getPlayer().setDirectionCurrent(EntityDirection.DOWN);

        } else if ((target.getRow() < player.getRow()) && (target.getCol() == player.getCol())) {
            target.setDirectionCurrent(EntityDirection.DOWN);
            gp.getEntityM().getPlayer().setDirectionCurrent(EntityDirection.UP);

        } else if ((target.getCol() > player.getCol()) && (target.getRow() == player.getRow())) {
            target.setDirectionCurrent(EntityDirection.LEFT);
            gp.getEntityM().getPlayer().setDirectionCurrent(EntityDirection.RIGHT);

        } else if ((target.getCol() < player.getCol()) && (target.getRow() == player.getRow())) {
            target.setDirectionCurrent(EntityDirection.RIGHT);
            gp.getEntityM().getPlayer().setDirectionCurrent(EntityDirection.LEFT);
        }
        gp.getDialogueR().initiateConversation(convId);                                                                 // Trigger the appropriate conversation with the NPC.
    }


    /**
     * Cleans up after a conversation has completed.
     * The type of cleanup done depends on the inputted mode.
     * If a mode does not automatically set the primary game state itself, the primary game state must be manually set
     * after this function call (directly or via another function call) avoid complications in game logic.
     *
     * @param mode type of cleanup to perform: (1) reset fields in DialogueReader, remove all entities from a conversing
     *             state, and set the primary game state to explore; (2) reset fields in DialogueReader and remove all
     *             entities from a conversing state; (3) reset fields in DialogueReader
     * @throws IllegalArgumentException if an illegal mode is passed as argument
     */
    public void cleanupConversation(int mode) {

        gp.getDialogueR().reset();                                                                                      // Reset the DialogueReader's fields back to their default values.

        switch (mode) {
            case 1:
                gp.getEntityM().clearConversingEntities();                                                              // All conversations between entities have ended.
                gp.getEntityM().getPlayer().setInteractionCountdown(
                        gp.getEntityM().getPlayer().getStagedStandardInteractionCountdown());                           // Player must wait before interacting with something else (prevents player from getting stuck in interaction loop).
                gp.setPrimaryGameState(PrimaryGameState.EXPLORE);                                                       // Return control back to the player.
                break;
            case 2:
                gp.getEntityM().clearConversingEntities();                                                              // All conversations between entities have ended.
                break;
            case 3:
                // Nothing here.
                break;
            default:
                throw new IllegalArgumentException("Attempted to cleanup a conversation using an illegal mode '"
                        + mode
                        + "'");
        }
    }


    /**
     * Cleans up after a sub-menu option has been selected and the sub-menu is to be closed.
     * The type of cleanup done depends on the inputted mode.
     * If a mode does not automatically set the primary game state itself, the primary game state must be manually set
     * after this function call (directly or via another function call) avoid complications in game logic.
     *
     * @param mode type of cleanup to perform: (1) reset fields in SubMenuHandler, reset fields in DialogueReader if
     *             applicable, and set the primary game state to explore; (2) reset fields in SubMenuHandler and reset
     *             fields in DialogueReader if applicable; (3) reset fields in SubMenuHandler
     * @throws IllegalArgumentException if an illegal mode is passed as argument
     */
    public void cleanupSubmenu(int mode) {

        gp.getSubMenuH().reset();                                                                                       // Reset the SubMenuHandler's fields back to their default values.

        switch (mode) {
            case 1:
                if (gp.getDialogueR().getActiveConv() != null) {
                    gp.getDialogueR().reset();                                                                          // Reset the DialogueReader's fields back to their default values.
                }
                gp.setPrimaryGameState(PrimaryGameState.EXPLORE);                                                       // No further logic will run.
                break;
            case 2:
                if (gp.getDialogueR().getActiveConv() != null) {
                    gp.getDialogueR().reset();                                                                          // Reset the DialogueReader's fields back to their default values.
                }
                break;
            case 3:
                // Nothing here.
                break;
            default:
                throw new IllegalArgumentException("Attempted to cleanup a sub-menu using an illegal mode '"
                        + mode
                        + "'");
        }
    }


    /**
     * Sets an entity to find and follow a path towards a goal.
     *
     * @param entityId ID of the entity to follow the path
     * @param goalCol column of the goal tile
     * @param goalRow row of the goal tile
     */
    public void setEntityFollowPath(int entityId, int goalCol, int goalRow) {

        gp.getEntityM().getEntityById(entityId).startFollowingPath(goalCol, goalRow);
    }


    /**
     * Sets an entity to follow a target entity.
     *
     * @param followerId ID of the follower entity
     * @param followedId ID of the entity to be followed
     */
    public void setEntityFollowTarget(int followerId, int followedId) {

        EntityBase follower = gp.getEntityM().getEntityById(followerId);
        EntityBase target = gp.getEntityM().getEntityById(followedId);

        if ((follower != null) && (target != null) && (followerId != followedId)) {

            follower.startFollowingEntity(followedId);
        } else if (follower == null) {

            throw new IllegalArgumentException("Attempted to have an entity with ID '"
                    + followerId
                    + "' that does not exist follow another entity");
        } else if (target == null) {

            throw new IllegalArgumentException("Attempted to have an entity with ID '"
                    + followedId
                    + "' that does not exist be followed by another entity");
        } else if (followerId == followedId) {

            throw new IllegalArgumentException("Attempted to have an entity with ID '"
                    + followerId
                    + "' follow itself");
        }
    }


    /**
     * Sets an entity to a walking overworld speed.
     *
     * @param entityId ID of the entity to walk
     */
    public void setEntityWalking(int entityId) {

        gp.getEntityM().getEntityById(entityId).setSpeed(2);
    }


    /**
     * Sets an entity to a running overworld speed.
     *
     * @param entityId ID of the entity to run
     */
    public void setEntityRunning(int entityId) {

        gp.getEntityM().getEntityById(entityId).setSpeed(4);
    }


    /**
     * Stops an entity from following a target entity.
     *
     * @param followerId ID of the follower entity
     */
    public void stopEntityFollowTarget(int followerId) {

        gp.getEntityM().getEntityById(followerId).stopFollowingEntity();                                                // Exit a following state.;
    }


    /**
     * Checks if an entity (follower) is following another entity (followedCandidate) via A* pathfinding, either
     * directly or in a chain.
     *
     * @param followedCandidate entity being checked to see if the follower is following it
     * @param follower follower entity
     * @return whether the follower entity is following the followed entity (true) or not (false)
     */
    public boolean checkEntityChainUp(EntityBase followedCandidate, EntityBase follower) {

        if (follower.isOnEntity()) {

            int iteration = 0;                                                                                          // The iteration we are in the chain away from the follower entity (going up the chain to the leader).
            int maxIteration = 15;                                                                                      // Cap the number of entities we check in a chain to avoid getting stuck in an infinite loop.
            int entityId = follower.getOnEntityId();                                                                    // The first check will be the entity directly in front of the follower entity in the chain; we will check if that is the followed candidate entity; if not, we will move one spot up the chain in the next iteration.

            while (iteration < maxIteration) {

                if (entityId == followedCandidate.getEntityId()) {

                    return true;                                                                                        // The follower entity is following the followed candidate entity.
                } else {

                    EntityBase entity = gp.getEntityM().getEntityById(entityId);

                    if (entity.isOnEntity()) {                                                                          // Check if the current entity being checked is itself following another entity in a chain.

                        entityId = entity.getOnEntityId();                                                              // Get the next entity up the chain.
                        iteration++;                                                                                    // Go one up the chain to the next entity.
                    } else {                                                                                            // The follower entity is not following the followed candidate entity.

                        return false;                                                                                   // Force the `while` loop to end.
                    }
                }
            }
        }
        return false;
    }


    /**
     * Retrieves the ID of the last entity in the chain of followers behind the target entity (target).
     *
     * @param target entity whose last follower is being retrieved
     * @return ID of the last entity in the chain; returns EventManager.ENTITY_CHAIN_OVERFLOW if more than 15 followers
     * exist behind the target entity
     */
    public int checkEntityChainDown(EntityBase target) {

        int iteration = 0;                                                                                              // The iteration we are in the chain away from the target entity (going down the chain to the last entity).
        int maxIteration = 15;                                                                                          // Cap the number of entities we check in a chain to avoid getting stuck in an infinite loop.
        int entityId = target.getEntityId();                                                                            // The first check will be to see if the target entity is being followed; if so, we will move one spot down the chain in the next iteration to see if the entity following the target entity is being followed itself, etc.
        boolean followed;                                                                                               // Declare a variable to track whether the entity being checked in the current iteration is being followed.
        int i;                                                                                                          // Declare a loop control variable for checking all entities.
        List<EntityBase> allEntities = gp.getEntityM().getAllEntities();

        while ((iteration < maxIteration)) {

            followed = false;                                                                                           // Reset back to false for the current iteration.
            i = 0;                                                                                                      // Reset back to zero for the current iteration.

            while ((i < allEntities.size()) && (!followed)) {                                                           // Check all entities to see if one is following the current entity being checked; if a follower is found, break the loop.

                if ((allEntities.get(i).getEntityId() != entityId)
                        && (allEntities.get(i).getOnEntityId() == entityId)) {

                    iteration++;
                    followed = true;
                    entityId = allEntities.get(i).getEntityId();                                                        // Set the new last entity in the chain to the follower that was just found; this new last entity in the chain is what will be checked in the next iteration to see if it itself has any followers.
                } else {

                    i++;
                }
            }

            if (!followed) {

                return entityId;                                                                                        // No follower was found, meaning the entity being checked in the current iteration is the last in the chain.
            }
        }
        return ENTITY_CHAIN_OVERFLOW;                                                                                   // Signal that we ran out of iterations.
    }


    /**
     * Stops all entities behind the target entity in a follower chain from being followers.
     * This target entity will not be removed as a follower if it itself is following another entity.
     * Only entities following the target entity are affected.
     */
    public void breakFollowerChain(EntityBase target) {

        boolean followers = true;

        while (followers) {

            int lastEntityId = gp.getEventM().checkEntityChainDown(target);                                             // Work from the back of the chain up to this entity.

            if (lastEntityId != target.getEntityId()) {

                gp.getEntityM().getEntityById(lastEntityId).stopFollowingEntity();
            } else {

                followers = false;
            }
        }
    }


    /**
     * Adds a given amount of an item to the player's inventory.
     *
     * @param itemId ID of item to be incremented in the player's inventory
     * @param amount amount to increment the item by
     * @return whether the full amount of the item was added to the player's inventory (true) or not (false)
     */
    public boolean incrementItem(int itemId, int amount) {

        boolean allAdded = true;
        boolean singleAdded;

        for (int i = 0; i < amount; i++) {

            singleAdded = gp.getEntityM().getPlayer().addItemToInventory(itemId);

            if (!singleAdded) {

                allAdded = false;
                break;
            }
        }
        return allAdded;
    }


    /**
     * Removes a given amount of an item from the player's inventory.
     *
     * @param itemId ID of item to be decremented in the player's inventory
     * @param amount amount to decrement the item by
     * @return whether the full amount of the item was removed from the player's inventory (true) or not (false)
     */
    public boolean decrementItem(int itemId, int amount) {

        boolean allRemoved = true;
        boolean singleRemoved;

        for (int i = 0; i < amount; i++) {

            singleRemoved = gp.getEntityM().getPlayer().removeItemFromInventory(itemId);

            if (!singleRemoved) {

                allRemoved = false;
                break;
            }
        }
        return allRemoved;
    }


    /**
     * Initiates the player to pick up an item and add it to the player's inventory.
     * The primary game state is set to dialogue.
     *
     * @param itemId ID of item to be added to the player's inventory
     * @return whether the item was added to the player's inventory (true) or not (false)
     */
    public boolean pickupItem(int itemId) {

        String text = "";
        boolean added = gp.getEntityM().getPlayer().addItemToInventory(itemId);

        if (added) {

            gp.getSoundS().playEffect("obtain");
            text = gp.getEntityM().getPlayer().getName() + " got a " + gp.getItemM().checkName(itemId) + "!";
        } else {

            text = gp.getEntityM().getPlayer().getName() + " cannot carry anymore!";
        }
        displayMessage(text, true);
        return added;
    }


    /**
     * Initiates a single message to display on the screen.
     *
     * @param message text to be displayed
     * @param charByChar whether visible text will be printed character by character (true) or all at once (false)
     */
    public void displayMessage(String message, boolean charByChar) {

        gp.getDialogueR().initiateStandardMessage(message, charByChar);
    }


    /**
     * Initiates a single message to display on the screen.
     *
     * @param message text to be displayed
     * @param charByChar whether visible text will be printed character by character (true) or all at once (false)
     * @param showArrow whether the dialogue arrow should be drawn on screen (true) or not (false), regardless of pause
     */
    public void displayMessage(String message, boolean charByChar, boolean showArrow) {

        gp.getDialogueR().initiateStandardMessage(message, charByChar, showArrow);
    }


    /**
     * Sets all non-party followers of the player entity as visible.
     */
    public void showAllNonPartyFollowers() {

        ArrayList<Integer> nonPartyFollowers = seekFollowers(gp.getEntityM().getPlayer(), true, true, false, false);

        for (int entityId : nonPartyFollowers) {

            gp.getEntityM().getEntityById(entityId).setHidden(false);
        }
    }


    /**
     * Sets all non-party followers of the player entity as hidden.
     */
    public void hideAllNonPartyFollowers() {

        ArrayList<Integer> nonPartyFollowers = seekFollowers(gp.getEntityM().getPlayer(), true, true, false, false);

        for (int entityId : nonPartyFollowers) {

            gp.getEntityM().getEntityById(entityId).setHidden(true);
        }
    }


    /**
     * Generates a list of entities following the followed entity, either directly or in a chain.
     * Note that only the specified entity maps will be polled for followers.
     *
     * @param followed followed entity
     * @param npc whether to poll the `npc` entity map (true) or not (false)
     * @param obj whether to poll the `obj` entity map (true) or not (false)
     * @param party whether to poll the `party` entity map (true) or not (false)
     * @param standby whether to poll the `standby` entity map (true) or not (false)
     * @return list of entity IDs of followers
     */
    public ArrayList<Integer> seekFollowers(EntityBase followed,
                                            boolean npc, boolean obj, boolean party, boolean standby) {

        ArrayList<Integer> followers = new ArrayList<>();                                                               // List to store IDs of all followers of the target entity to use when rebuilding chain.

        if (npc) {

            for (EntityBase candidate : gp.getEntityM().getNpc().values()) {                                            // Record any NPC followers.

                if (gp.getEventM().checkEntityChainUp(followed, candidate)) {

                    followers.add(candidate.getEntityId());
                }
            }
        }


        if (obj) {

            for (EntityBase candidate : gp.getEntityM().getObj().values()) {                                            // Record any object followers.

                if (gp.getEventM().checkEntityChainUp(followed, candidate)) {

                    followers.add(candidate.getEntityId());
                }
            }
        }

        if (party) {

            for (EntityBase candidate : gp.getEntityM().getParty().values()) {                                          // Record any standby followers.

                if (gp.getEventM().checkEntityChainUp(followed, candidate)) {

                    followers.add(candidate.getEntityId());
                }
            }
        }

        if (standby) {

            for (EntityBase candidate : gp.getEntityM().getStandby().values()) {                                        // Record any standby followers.

                if (gp.getEventM().checkEntityChainUp(followed, candidate)) {

                    followers.add(candidate.getEntityId());
                }
            }
        }
        return followers;
    }


    /**
     * Handles what to do if an entity steps onto a tile that may trigger a stock event (tall grass rustle).
     *
     * @param targetCol column of the tile being checked
     * @param targetRow row of the tile being checked
     * @param entityId ID of interacting entity
     * @return whether a stock step interaction (grass rustle) was/may be triggered (true) or not (false)
     */
    private boolean handleStockStepInteractionTallGrass(int targetCol, int targetRow, int entityId) {

        try {

            EntityBase entity = gp.getEntityM().getEntityById(entityId);

            if ((!entity.isColliding())                                                                                 // Ensure that the entity can occupy the target tile.
                    && (gp.getMapM().getLoadedMap().getMapLandmarkNum()[targetCol][targetRow] == 6)) {

                gp.getLandmarkM().initiateConditionalAnimation(targetCol, targetRow);
                return true;
            }
        } catch (ArrayIndexOutOfBoundsException e) {}

        return false;
    }


    /**
     * Handles what to do if an entity steps onto a tile that may trigger a stock event (ledge hop).
     *
     * @param targetCol column of the tile/node being checked
     * @param targetRow row of the tile/node being checked
     * @param entityId ID of interacting entity
     * @return whether a stock step interaction (ledge hop) was/may be triggered (true) or not (false)
     */
    private boolean handleStockStepInteractionLedge(int targetCol, int targetRow, int entityId) {

        try {

            EntityBase entity = gp.getEntityM().getEntityById(entityId);
            int tileNumTarget = gp.getMapM().getLoadedMap().getMapTileNum()[targetCol][targetRow];

            if ((entity.getDirectionCurrent() == EntityDirection.DOWN)                                                  // Only downward ledge hops are supported.
                    && ((tileNumTarget == 132) || (tileNumTarget == 133)
                    || (tileNumTarget == 134) || (tileNumTarget == 135))
                    && (!gp.getCollisionI().calculateCollisionAll(targetCol, targetRow + 1, entity, true))) {           // Ensure tile being hopped onto is not solid.

                entity.initiateHop();
                return true;
            }

        } catch (ArrayIndexOutOfBoundsException e) {}

        return false;
    }
}
