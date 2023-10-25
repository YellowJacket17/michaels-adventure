package combat;

import combat.implementation.action.*;
import core.GamePanel;
import core.GameState;
import core.TransitionType;
import entity.EntityBase;
import entity.EntityDirection;
import submenu.SubMenuHandler;
import utility.UtilityTool;

import java.util.*;

/**
 * This class is used to control gameplay logic while in combat.
 */
public class CombatManager {

    // FIELDS
    private final GamePanel gp;
    private int fieldCenterCol, fieldCenterRow;                                                                         // Set the center column/row of the combat field.
    private int storedTrackIndex = -1;                                                                                  // Stores the music track that was playing when combat was initiated; a value of -1 means that no music track was playing.
    private final HashMap<Integer, Integer> storedEntityCols = new HashMap<>();                                         // Stores the columns that all combating entities occupied when combat was initiated; entity ID is the key, column is the value.
    private final HashMap<Integer, Integer> storedEntityRows = new HashMap<>();                                         // Stores the rows that all combating entities occupied when combat was initiated; entity ID is the key, row is the value.
    private final HashMap<Integer, EntityDirection> storedEntityDirections = new HashMap<>();                           // Stores the direction that all combating entities were facing when combat was initiated; entity ID is the key, entity direction is the value.
    private final HashSet<Integer> opposingEntities = new HashSet<>();                                                  // Stores the IDs of opposing entities during combat.
    private EnterCombatTransitionType activeEnterCombatTransitionType;                                                  // Set which specific enter combat transition type is currently being performed.
    private ExitCombatTransitionType activeExitCombatTransitionType;                                                    // Set which specific exit combat transition type is currently being performed.
    private final LinkedList<Integer> queuedEntityTurnOrder = new LinkedList<>();                                       // List of queued turn order for entities to move in; the entity at the front of the queue is the one whose turn it currently is.
    private final LinkedList<ActionBase> queuedActions = new LinkedList<>();                                            // List of queued actions to run during a turn in combat.
    private final List<String> rootCombatOptions;                                                                       // List to store root combat options (fight, inventory, etc.).
    private int lastSelectedSubMenuOption = -1;                                                                         // Stores the last sub-menu option (index) that was selected; a value of -1 is the default.
    private SubMenuType lastSubMenuType;                                                                                // Stores the last sub-menu type that was actioned.
    private boolean lastActionSubmenu = false;                                                                          // Stores whether the last action that was run was one to generate a sub-menu.


    // CONSTRUCTOR
    /**
     * Constructs a CombatManager instance.
     *
     * @param gp GamePanel instance
     */
    public CombatManager(GamePanel gp) {
        this.gp = gp;

        rootCombatOptions = List.of("Guard", "Attack", "Skill", "Inventory", "Party", "Flee");                          // Immutable list.
    }


    // METHODS
    /**
     * Progresses combat after the previous action has finished.
     * This function serves as the main driver for progressing combat logic.
     */
    public void progressCombat() {

        // If last action was a sub-menu, handle result of user selection.
        if (lastActionSubmenu) {

            runSubMenuSelection();
            runNextQueuedAction();

        // If there are still queued actions, run the next one.
        } else if (!queuedActions.isEmpty()) {

            runNextQueuedAction();

        // Begin the turn of the entity at the front of the turn order queue.
        } else {

            // TODO : Branch logic depending on whether it's a party or opposing entity.
            //  For an opposing entity, decision will be made automatically by the program in a separate method.
            String message = buildRootMenuPrompt(gp.getEntityById(queuedEntityTurnOrder.peekFirst()).getName());
            addQueuedActionBack(new Act_ReadMessage(gp, message, false));
            addQueuedActionBack(new Act_GenerateSubMenu(gp, SubMenuType.ROOT, rootCombatOptions, SubMenuHandler.widthCombat1));
            runNextQueuedAction();
        }
    }


    /**
     * Initiates combat fade-to-black transition and performs necessary loading.
     * The game state is set to transition.
     *
     * @param col center column of combat field
     * @param row center row of combat field
     * @param type type of enter combat transition; see comments in the EnterCombatTransitionType enum for definitions of different types
     * @param trackIndex music track index in the `musicURL` array in the Sound class; input -1 if no music swap is desired
     * @param opponent opposing entity to be fought
     */
    public void initiateCombat(int col, int row, EnterCombatTransitionType type, int trackIndex,
                               EntityBase opponent) {

        initiateCombat(col, row, type, trackIndex, opponent, null, null);
    }


    /**
     * Initiates entering combat with a fade-to-black transition and performs necessary loading.
     * The game state is set to transition.
     *
     * @param col center column of combat field
     * @param row center row of combat field
     * @param type type of enter combat transition; see comments in the EnterCombatTransitionType enum for definitions of different types
     * @param opponent1 first opposing entity to be fought
     * @param opponent2 second opposing entity to be fought
     */
    public void initiateCombat(int col, int row, EnterCombatTransitionType type, int trackIndex,
                               EntityBase opponent1, EntityBase opponent2) {

        initiateCombat(col, row, type, trackIndex, opponent1, opponent2, null);
    }


    /**
     * Initiates combat with a fade-to-black transition.
     * The game state is set to transition.
     *
     * @param col center column of combat field
     * @param row center row of combat field
     * @param type type of enter combat transition; see comments in the EnterCombatTransitionType enum for definitions of different types
     * @param trackIndex music track index in the `musicURL` array in the Sound class; input -1 if no music swap is desired
     * @param opponent1 first opposing entity to be fought
     * @param opponent2 second opposing entity to be fought
     * @param opponent3 third opposing entity to be fought
     */
    public void initiateCombat(int col, int row, EnterCombatTransitionType type, int trackIndex,
                               EntityBase opponent1, EntityBase opponent2, EntityBase opponent3) {

        if (((opponent1 == null) && (opponent2 == null) && (opponent3 == null)) || (type != null)) {

            // Clear any conversing entities.
            gp.clearConversingEntities();

            // Store music that was staged before initiating combat.
            storedTrackIndex = gp.getStagedMusic();

            // Play combat music.
            if (trackIndex >= 0) {
                gp.playMusic(trackIndex);
            }

            // Set the game state.
            gp.setCombatActive(true);                                                                                   // Set combat mode as active.
            gp.setGameState(GameState.TRANSITION);                                                                      // Set the game to a transition state.
            gp.setActiveTransitionType(TransitionType.ENTER_COMBAT);                                                    // Set the overarching transition type as enter combat (different from EnterCombatTransitionType).
            gp.setActiveTransitionPhase(1);                                                                             // Set the first phase of the transition (fade out to black); this will actually trigger the transition (fade-to-black effect) when the game state is in a transition state.
            activeEnterCombatTransitionType = type;                                                                     // Set the current enter combat transition type being used.

            // Set the center tile (column/row) of the combat field.
            fieldCenterCol = col;
            fieldCenterRow = row;

            // Store opposing entities to be called later.
            if (opponent1 != null) {
                opposingEntities.add(opponent1.getEntityId());
            }

            if (opponent2 != null) {
                opposingEntities.add(opponent2.getEntityId());
            }

            if (opponent3 != null) {
                opposingEntities.add(opponent3.getEntityId());
            }
        } else {

            if ((opponent1 == null) && (opponent2 == null) && (opponent3 == null)) {

                UtilityTool.logWarning("Attempted to initiate combat with no opponents.");
            } else {

                UtilityTool.logWarning("Attempted to initiate combat with a null enter combat transition type.");
            }
        }
    }


    /**
     * Initiates exiting combat with a fade-to-black transition and performs necessary loading.
     *
     * @param type type of exit combat transition; see comments in the ExitCombatTransitionType enum for definitions of different types
     */
    public void exitCombat(ExitCombatTransitionType type) {

        gp.setGameState(GameState.TRANSITION);                                                                          // Set the game to a transition state.
        gp.setActiveTransitionType(TransitionType.EXIT_COMBAT);                                                         // Set the overarching transition type as enter combat (different from ExitCombatTransitionType).
        gp.setActiveTransitionPhase(1);                                                                                 // Set the first phase of the transition (fade out to black); this will actually trigger the transition (fade-to-black effect) when the game state is in a transition state.
        activeExitCombatTransitionType = type;                                                                          // Set the current exit combat transition type being used.
    }



    /**
     * Performs any loading that needs to be done once the screen fades to black during an enter combat transition.
     */
    public void handleEnterCombatTransitionLoading() {

        switch (activeEnterCombatTransitionType) {

            case BASIC:
                handleBasicEnterCombatTransitionLoading();
                break;
        }
    }



    /**
     * Closes out an enter combat transition that has completed all of its phases (i.e., tidies up any variables).
     * This is to be run once an enter combat transition has fully completed.
     * The game state is set to dialogue to introduce the combat.
     */
    public void concludeEnterCombatTransition() {

        // Reset enter combat transition variable.
        activeEnterCombatTransitionType = null;

        // Build first message to display at the start of the fight.
        String build = "";

        String stagedName = "";

        int i = 0;

        for (int entityId : opposingEntities) {

            if ((gp.getEntityById(entityId) != null) && (!gp.getEntityById(entityId).getName().equals(""))) {

                stagedName = gp.getEntityById(entityId).getName();
            } else {

                stagedName = "???";
            }

            if (i == (opposingEntities.size() - 1)) {

                if (i > 0) {

                    build += "and ";
                }

                build += stagedName;
            } else {

                build += stagedName;

                if (opposingEntities.size() > 2) {

                    build += ", ";
                } else {

                    build += " ";
                }
            }

            i++;
        }

        // Stage a message action.
        String message = build + " would like to fight!";
        addQueuedActionBack(new Act_ReadMessage(gp, message, true));

        // Enter the main method for progressing combat.
        progressCombat();
    }


    /**
     * Performs any loading that needs to be done once the screen fades to black during an exit combat transition.
     */
    public void handleExitCombatTransitionLoading() {

        switch (activeExitCombatTransitionType) {

            case BASIC:
                handleBasicExitCombatTransitionLoading();
                break;
        }
    }


    /**
     * Closes out an exit combat transition that has completed all of its phases (i.e., tidies up any variables).
     * This is to be run once an exit combat transition has fully completed.
     * The game state is set depending on the type of exit combat transition that was used.
     */
    public void concludeExitCombatTransition() {

        switch (activeExitCombatTransitionType) {
            case BASIC:
                concludeBasicExitCombatTransition();
                break;
        }

        reset();
        resetAllCombatingEntityStats();
        gp.clearCombatingEntities();
        gp.getDialogueR().reset();
        gp.setCombatActive(false);
    }


    /**
     * Removes the entity at the front of the turn order queue.
     * This will set the next entity in the queue to have its turn.
     * If the queue is empty, the turn order will be regenerated.
     */
    public void endEntityTurn() {

        if (queuedEntityTurnOrder.peekFirst() != null) {

            queuedEntityTurnOrder.removeFirst();

            if (queuedEntityTurnOrder.peekFirst() == null) {

                generateTurnOrder();
            }
        } else {

            generateTurnOrder();
        }
    }


    /**
     * Generates and queues actions for the entity whose turn it currently is.
     */
    private void generateTurnActions() {

        // TODO : Last action must ALWAYS be to end the current entity's turn!
    }


    /**
     * Runs logic based on the last sub-menu type that was generated and option that was selected in said sub-menu.
     */
    private void runSubMenuSelection() {

        lastActionSubmenu = false;

        switch (lastSubMenuType) {
            case ROOT:
                runRootSubMenuSelection();
                break;
            case FIGHT:
                runFightSubMenuSelection();
                break;
        }
    }


    /**
     * Runs logic based on the last option that was selected in the root combat sub-menu.
     */
    private void runRootSubMenuSelection() {

        switch (lastSelectedSubMenuOption) {
            case 0:
                break;
            case 1:
                break;
            case 2:
                List<String> attackOptions = new ArrayList<>();
                for (AttackBase attack : gp.getEntityById(queuedEntityTurnOrder.peekFirst()).getAttacks()) {
                    attackOptions.add(attack.getName());
                }
                addQueuedActionBack(new Act_GenerateSubMenu(gp, SubMenuType.FIGHT, attackOptions, SubMenuHandler.widthCombat1));
                break;
            case 3:
                break;
            case 4:
                break;
            case 5:
                String message = "Fleeing the fight!";
                addQueuedActionBack(new Act_ReadMessage(gp, message, true));
                addQueuedActionBack(new Act_ExitCombat(gp, ExitCombatTransitionType.BASIC));
                break;
        }
    }


    /**
     * Runs logic based on the last option that was selected in the fight combat sub-menu.
     */
    private void runFightSubMenuSelection() {

        EntityBase entity = gp.getEntityById(queuedEntityTurnOrder.peekFirst());
        AttackBase attack = entity.getAttacks().get(lastSelectedSubMenuOption);
        String message = buildUseAttackMessage(entity.getName(), attack.getName());
        addQueuedActionBack(new Act_ReadMessage(gp, message, true));

        // TODO : We'll call generateTurnActions() to calculate the series of actions that will result from this
        //        attack being used; perhaps the above action can also be generated placed in generateTurnActions()?
    }


    /**
     * Performs any loading needed to be done for the basic enter combat transition type.
     */
    private void handleBasicEnterCombatTransitionLoading() {

        // Set the player entity as combating and store its pre-combat world position.
        setCombating(gp.getPlayer());

        // Set the player's position and image on the combat field.
        gp.getPlayer().setCol(fieldCenterCol - 4);
        gp.getPlayer().setRow(fieldCenterRow);
        gp.getPlayer().cancelAction();
        gp.getPlayer().setDirectionCurrent(EntityDirection.RIGHT);

        // Set party member entities as combating and store their pre-combat world positions.
        // Also set party member positions and images on the combat field.
        int placedPartyMembers = 0;

        for (EntityBase entity : gp.getParty().values()) {

            if (entity != null) {

                setCombating(entity);
                entity.cancelAction();
                entity.setDirectionCurrent(EntityDirection.RIGHT);
                entity.setCol(fieldCenterCol - 5);

                if (placedPartyMembers == 0) {

                    entity.setRow(fieldCenterRow - 2);
                } else {

                    entity.setRow(fieldCenterRow + 2);
                }

                placedPartyMembers++;
            }
        }

        // Set opposing entities as combating and store their pre-combat world positions.
        // Also set opponent positions and images on the combat field.
        int placedOpponents = 0;

        for (int entityId : opposingEntities) {

            if (gp.getParty().get(entityId) == null) {

                EntityBase opponent = gp.getEntityById(entityId);
                setCombating(opponent);
                opponent.cancelAction();
                opponent.setDirectionCurrent(EntityDirection.LEFT);

                if (placedOpponents == 0) {

                    opponent.setCol(fieldCenterCol + 4);
                    opponent.setRow(fieldCenterRow);
                } else if (placedOpponents == 1) {

                    opponent.setCol(fieldCenterCol + 5);
                    opponent.setRow(fieldCenterRow - 2);
                } else {

                    opponent.setCol(fieldCenterCol + 5);
                    opponent.setRow(fieldCenterRow + 2);
                }

                placedOpponents++;
            } else {

                opposingEntities.remove(entityId);
            }
        }

        // Generate turn order.
        generateTurnOrder();

        // Set camera to center on the combat field.
        gp.getCameraS().setCameraSnap((fieldCenterCol * gp.getTileSize()) + (gp.getTileSize() / 2) ,
                fieldCenterRow * gp.getTileSize());
    }


    /**
     * Performs any loading needed to be done for the basic exit combat transition type.
     */
    private void handleBasicExitCombatTransitionLoading() {

        // Reset camera to player.
        gp.getCameraS().resetCameraSnap();

        // Reset all non-party member combating entities back to pre-combat positions.
        for (int entityId : storedEntityCols.keySet()) {
            if (!gp.getParty().containsKey(entityId)) {
                gp.getEntityById(entityId).setCol(storedEntityCols.get(entityId));
            }

        }

        for (int entityId : storedEntityRows.keySet()) {
            if (!gp.getParty().containsKey(entityId)) {
                gp.getEntityById(entityId).setRow(storedEntityRows.get(entityId));
            }
        }

        // Reset all non-party member combating entities back to pre-combat directions.
        for (int entityId : storedEntityDirections.keySet()) {
            if (!gp.getParty().containsKey(entityId)) {
                gp.getEntityById(entityId).setDirectionCurrent(storedEntityDirections.get(entityId));
            }
        }

        // Warp party members to player.
        gp.getWarpS().warpFollowersToPlayer(gp.getParty());

        // Swap music.
        if (storedTrackIndex >= 0) {
            gp.swapMusic(storedTrackIndex);
        }
    }


    /**
     * Closes out a basic exit combat transition that has completed all of its phases (i.e., tidies up any variables).
     * This is to be run once a basic exit combat transition has fully completed.
     * The game state is set to explore to return control to the player.
     */
    private void concludeBasicExitCombatTransition() {

        gp.setGameState(GameState.EXPLORE);
    }


    /**
     * Generates the order in which combating entities will take their turns.
     */
    private void generateTurnOrder() {

        // Create a HashMap of combating entities, where entity ID is the key and agility is the value.
        HashMap<Integer, Integer> entitiesToPlace = new HashMap<>();

        // Add all combating entities to the new list of combating entities
        for (int entityId : gp.getCombatingEntities()) {

            entitiesToPlace.put(entityId, gp.getEntityById(entityId).getAgility());
        }

        // Build the turn order based on entity agility attributes (higher agility moves sooner).
        // If two or more entities tie, then order for them will be randomly generated.
        while (entitiesToPlace.size() > 0) {

            int highestAgility = 0;

            // Find the highest agility in the remaining entities to place.
            for (int agility : entitiesToPlace.values()) {

                if (agility > highestAgility) {

                    highestAgility = agility;
                }
            }

            // Find remaining entities to place that have the highest agility.
            ArrayList<Integer> entitiesHighestAgility = new ArrayList<>();

            for (int entityId : entitiesToPlace.keySet()) {

                if (entitiesToPlace.get(entityId) == highestAgility) {

                    entitiesHighestAgility.add(entityId);
                }
            }

            // Add entities with the highest agility to the list of entity turn order.
            if (entitiesHighestAgility.size() == 1) {

//                System.out.println("Entity ID: " + entitiesHighestAgility.get(0) + " -> Agility: " + gp.getEntityById(entitiesHighestAgility.get(0)).getAgility());
                queuedEntityTurnOrder.addLast(entitiesHighestAgility.get(0));
                entitiesToPlace.remove(entitiesHighestAgility.get(0));
            } else {

                // Add the entities to the turn list in a random order since they tied in agility.
                while (entitiesHighestAgility.size() > 0) {

                    Random random = new Random();
                    int i = random.nextInt(entitiesHighestAgility.size());                                              // Get a random number from 0 to the highest index in the list of entities with the highest agility.

//                    System.out.println("Entity ID: " + entitiesHighestAgility.get(i) + " -> Agility: " + gp.getEntityById(entitiesHighestAgility.get(i)).getAgility());
                    queuedEntityTurnOrder.addLast(entitiesHighestAgility.get(i));                                             // Add the randomly selected entity as the next in the turn list.
                    entitiesToPlace.remove(entitiesHighestAgility.get(i));
                    entitiesHighestAgility.remove(i);                                                                   // Remove the randomly selected entity from the list of entities with the highest agility.
                }
            }
        }
    }


    /**
     * Sets a target entity to a combating state.
     * If ethe target entity is already in a combating state, it will remain as such.
     * The target entity's pre-combat world position is also stored for later use (if needed post-combat).
     *
     * @param target ID of the entity entering combat
     */
    private void setCombating(EntityBase target) {

        if (!gp.getCombatingEntities().contains(target.getEntityId())) {

            gp.getCombatingEntities().add(target.getEntityId());
            storedEntityCols.put(target.getEntityId(), target.getCol());
            storedEntityRows.put(target.getEntityId(), target.getRow());
            storedEntityDirections.put(target.getEntityId(), target.getDirectionCurrent());
        }
    }


    /**
     * Builds a string for the root combat menu prompt according to the passed entity name.
     *
     * @param entityName entity whose turn it is
     * @return built string
     */
    private String buildRootMenuPrompt(String entityName) {

        if ((entityName != null) && (!entityName.equals(""))) {

            return "What will " + entityName + " do?";
        } else {

            return "What will ??? do?";
        }
    }


    /**
     * Builds a string for the use attack message according to the passed entity name and attack name.
     *
     * @param entityName attacking entity
     * @param attackName attack being used
     * @return built string
     */
    private String buildUseAttackMessage(String entityName, String attackName) {

        String buildEntityName = "???";
        String buildAttackName = "???";

        if ((entityName != null) && (!entityName.equals(""))) {
            buildEntityName = entityName;
        }

        if ((attackName != null) && (!attackName.equals(""))) {
            buildAttackName = attackName;
        }

        return buildEntityName + " used " + buildAttackName + "!";
    }


    /**
     * Adds an action to the end of the queue of actions.
     *
     * @param action action to add to the queue
     */
    private void addQueuedActionBack(ActionBase action) {

        queuedActions.addLast(action);
    }


    /**
     * Adds an action to the front of the queue of actions.
     *
     * @param action action to add to the queue
     */
    private void addQueuedActionFront(ActionBase action) {

        queuedActions.addFirst(action);
    }


    /**
     * Runs the action at the front of the queue of actions.
     * Once complete, the action is removed from the queue.
     */
    private void runNextQueuedAction() {

        if (queuedActions.peekFirst() != null) {
            queuedActions.pollFirst().run();
        }
    }


    /**
     * Remove all buffs from stats (attack, defense, magic, agility) for all combating entities.
     */
    private void resetAllCombatingEntityStats() {

        for (int entityId : gp.getCombatingEntities()) {

            gp.getEntityById(entityId).resetStats();
        }
    }


    /**
     * Resets CombatManager back to its default state.
     * Intended to be called to clean up after combat has finished.
     */
    private void reset() {

        fieldCenterCol = 0;
        fieldCenterRow = 0;
        storedTrackIndex = -1;
        storedEntityCols.clear();
        storedEntityRows.clear();
        storedEntityDirections.clear();
        opposingEntities.clear();
        activeEnterCombatTransitionType = null;
        activeExitCombatTransitionType = null;
        queuedEntityTurnOrder.clear();
        queuedActions.clear();
        lastSelectedSubMenuOption = -1;
        lastSubMenuType = null;
        lastActionSubmenu = false;
    }


    // TODO : Add apprpriate getters/setters.
    // SETTERS
    public void setLastSelectedSubMenuOption(int lastSelectedSubMenuOption) {
        this.lastSelectedSubMenuOption = lastSelectedSubMenuOption;
    }

    public void setLastSubMenuType(SubMenuType lastSubMenuType) {
        this.lastSubMenuType = lastSubMenuType;
    }

    public void setLastActionSubmenu(boolean lastActionSubmenu) {
        this.lastActionSubmenu = lastActionSubmenu;
    }
}
