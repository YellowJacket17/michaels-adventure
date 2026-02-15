package combat;

import asset.Sound;
import combat.enumeration.MoveCategory;
import combat.enumeration.SubMenuType;
import combat.implementation.action.*;
import combat.implementation.move.Mve_BasicAttack;
import core.GamePanel;
import entity.enumeration.EntityStatus;
import event.enumeration.TransitionType;
import entity.EntityBase;
import entity.enumeration.EntityDirection;
import org.joml.Vector3f;
import submenu.SubMenuHandler;
import utility.LimitedArrayList;
import utility.UtilityTool;

import java.util.*;

/**
 * This class is used to control gameplay logic while in combat.
 */
public class CombatManager {

    /*
     * Please note the following about tracks:
     *    - When initiating combat, the playing track prior to combat will be swapped out and replaced with the track
     *      passed as argument to the `initiateCombat()` method.
     *    - Upon exiting combat, the track tied to the loaded map will be swapped back in, according to map state.
     *    - If Sound.RETAIN_TRACK is passed as argument to the `initiateCombat()` method, then the playing track prior
     *      to combat will keep playing during and after combat without interruption.
     *
     * A combating entity's hidden state determine whether it will actively participate in combat or not.
     * If a combating entity is hidden, its turn will be skipped.
     */

    // FIELDS
    private final GamePanel gp;

    /**
     * Boolean to set whether the game is in combat mode (true) or not (false).
     */
    private boolean combatActive = false;

    /**
     * Boolean tracking whether combat has been lost by the player (true) or not (false).
     */
    private boolean combatLost = false;

    /**
     * Unique ID of combat scenario currently being run.
     * To be clear, this ID is unique for each combat scenario in the game, as it primarily serves to determine what
     * custom logic to execute after combat has been exited (e.g., initiate a specific cutscene, etc.).
     * A default value of '-1' indicates that there is no combat scenario currently being executed.
     */
    private int combatId = -1;

    /**
     * Location of the center of the combat field.
     */
    private int fieldCenterCol, fieldCenterRow;

    /**
     * Map to store the column (i.e., world position) that all combating entities occupied before combat was initiated;
     * entity ID is the key, column is the value.
     */
    private final HashMap<Integer, Integer> storedEntityCols = new HashMap<>();

    /**
     * Map to store the rows (i.e., world position) that all combating entities occupied before combat was initiated;
     * entity ID is the key, row is the value.
     */
    private final HashMap<Integer, Integer> storedEntityRows = new HashMap<>();

    /**
     * Map to store the direction that all combating entities were facing in before combat was initiated; entity ID is
     * the key, direction is the value.
     */
    private final HashMap<Integer, EntityDirection> storedEntityDirections = new HashMap<>();

    /**
     * Map to store the hidden status of all combating entities before combat was initiated; entity ID is the key,
     * hidden (true or false) is the value.
     */
    private final HashMap<Integer, Boolean> storedEntityHidden = new HashMap<>();

    /**
     * List to store all non-party, non-combating followers of any combating entity (including the player entity) before
     * combat was initiated.
     */
    private final ArrayList<Integer> storedNonCombatingFollowers = new ArrayList<>();

    /**
     * List to store the original party entity ordering before combat was initiated.
     * The IDs of party members are stored in this list, with the front-most at index zero, etc.
     */
    private final ArrayList<Integer> partyOrdering = new ArrayList<>();

    /**
     * Set to store IDs of non-player-side entities involved in combat.
     * Non-player-side entities refers to combating entities fighting against the player's side.
     * A set is used to avoid having the same entity entered twice or thrice.
     */
    private final LinkedHashSet<Integer> nonPlayerSideEntities = new LinkedHashSet<>();

    /**
     * Boolean indicating whether the track playing before combat was retained (true) or not (false) when entering
     * combat.
     */
    private boolean retainPreCombatTrack = false;

    /**
     * List of queued turn order for entities involved in combat.
     * The entity at the front of the queue is the entity whose turn it is.
     */
    private final LinkedList<Integer> queuedEntityTurnOrder = new LinkedList<>();

    /**
     * List of queued actions to run in combat.
     */
    private final LinkedList<ActionBase> queuedActions = new LinkedList<>();

    /**
     * List to store root combat options (guard, attack, etc.).
     */
    private final List<String> rootCombatOptions;

    /**
     * List to log the last ten sub-menus generated in combat.
     * The oldest sub-menu is at index 0, the latest is at the top (size of list minus one).
     */
    private final LimitedArrayList<SubMenuMemory> subMenuLog = new LimitedArrayList<>(10);

    /**
     * Stores the last generated list of player-side entity (active, inactive, and player entity) options in combat.
     * The IDs of the player-side entities are stored in this list.
     * Player-side entities refer to combating entities (including the player entity) fighting on the player's side.
     */
    private final ArrayList<Integer> lastGeneratedPlayerSideOptions = new ArrayList<>();

    /**
     * Stores the last generated list of active player-side entity (including the player entity) options in combat.
     * The IDs of active entities are stored in this list.
     * Player-side entities refer to combating entities (including the player entity) fighting on the player's side.
     */
    private final ArrayList<Integer> lastGeneratedActivePlayerSideOptions = new ArrayList<>();

    /**
     * Stores the last generated list of inactive player-side entity options in combat.
     * The IDs of inactive entities are stored in this list.
     * Player-side entities refer to combating entities (including the player entity) fighting on the player's side.
     */
    private final ArrayList<Integer> lastGeneratedInactivePlayerSideOptions = new ArrayList<>();

    /**
     * Stores the last generated list of target options in combat (player-side and non-player-side).
     * The IDs of viable entities are stored in this list.
     */
    private final ArrayList<Integer> lastGeneratedTargetOptions = new ArrayList<>();

    /**
     * Set to store IDs of entities currently in a guarding state.
     * A set is used to avoid having the same entity entered twice or thrice.
     */
    private final LinkedHashSet<Integer> guardingEntities = new LinkedHashSet<>();

    /**
     * Map to store which entities will skip their next turn; entity ID is the key, number of turns remaining to be
     * skipped is the value.
     */
    private final HashMap<Integer, Integer> turnSkipEntities = new HashMap<>();

    /**
     * Variable to store ID of entity for target lock.
     * If an entity ID is stored here, then a move can only target this entity if that entity is a valid option.
     * The number of turns for this to last after activated must be set in the variable 'targetLockTurns'.
     * Note that an entity cannot lock onto itself (i.e., the locked on entity itself will still have access to the
     * normal full range of targets allowed by a move).
     * Note that an entity can always use a move on itself, regardless of if there is a locked target.
     * For example, if the locked target is an opponent and a move lets the user target either itself or any opponent,
     * the user will be able to select either themself or the locked target.
     * A default value of '-1' means that no entity is locked onto.
     */
    private int targetLockEntityId = -1;

    /**
     * Variable to store number of turns that target lock on an entity will last for after activated.
     */
    private int targetLockTurns = 0;

    /**
     * Boolean indicating whether the last action that was run was to generate a sub-menu.
     */
    private boolean lastActionSubmenu = false;

    /**
     * Boolean to set whether combat UI is rendered or not.
     */
    private boolean combatUiVisible = false;

    /**
     * Boolean indicating whether the root combat sub-menu has been displayed yet during an entity's turn.
     * This is used to control the character-by-character printing of the accompanying dialogue when the root combat
     * sub-menu is first displayed.
     */
    private boolean newTurnRootSubMenuDisplayed = false;

    /**
     * Default combat move if an entity has no assigned moves.
     */
    private final MoveBase defaultMove;


    // CONSTRUCTOR
    /**
     * Constructs a CombatManager instance.
     *
     * @param gp GamePanel instance
     */
    public CombatManager(GamePanel gp) {
        this.gp = gp;
        defaultMove = new Mve_BasicAttack(gp);
        rootCombatOptions = List.of("Guard", "Attack", "Skill", "Party");                                               // Immutable list.
    }


    // METHODS
    /**
     * Progresses combat after the previous action has finished.
     * This function serves as the main driver for progressing combat logic.
     * Action subclasses must call this function somewhere in their 'run()' function to hand off control to the next
     * queued action upon finishing execution.
     */
    public void progressCombat() {

        if (lastActionSubmenu) {                                                                                        // If last action was a sub-menu, handle result of user selection.

            runSubMenuSelection();
            runNextQueuedAction();
        } else if (!queuedActions.isEmpty()) {                                                                          // If there are still queued actions, run the next one.

            runNextQueuedAction();
        } else if (pollFainting()) {                                                                                    // No queued actions signal the end of an entity's turn; check whether any entities fainted as a result of an entity's turn; appropriate actions will be queued and immediately run if so.

            runNextQueuedAction();
        } else {                                                                                                        // No queued actions signal the end of an entity's turn; begin the turn of the next entity in line in turn order queue.

            endEntityTurn();

            if (UtilityTool.VERBOSE_LOGGING) {

                UtilityTool.logInfo("Starting combat turn of entity "
                        + (((gp.getEntityM().getEntityById(queuedEntityTurnOrder.peekFirst()).getName() != null)
                            && (!gp.getEntityM().getEntityById(queuedEntityTurnOrder.peekFirst()).getName().equals("")))
                            ? "'" + (gp.getEntityM().getEntityById(queuedEntityTurnOrder.peekFirst()).getName() + "' ")
                            : "")
                        + "with ID '"
                        + gp.getEntityM().getEntityById(queuedEntityTurnOrder.peekFirst()).getEntityId()
                        + "'.");
            }

            if ((turnSkipEntities.containsKey(queuedEntityTurnOrder.peekFirst()))
                    && (turnSkipEntities.get(queuedEntityTurnOrder.peekFirst()) > 0)) {                                 // If entity turn is being skipped.

                handleSkipTurn();
            } else if (nonPlayerSideEntities.contains(queuedEntityTurnOrder.peekFirst())) {                             // If entity at the front of the turn order queue is a non-player-side entity.

                generateNpcTurn();
            } else {                                                                                                    // If entity at the front of the turn order queue is a party member/player.

                generateRootSubMenuAction();
                runNextQueuedAction();
            }
        }
    }


    /**
     * Initiates combat fade-to-black transition and performs necessary loading.
     * Applicable entities will be set to a state of combating.
     * Note that any entities in a state of conversing will be removed from said state.
     * Note that an entity ID of '-1' is a reserved default for no entity and will not be considered.
     * Note that a combat ID of zero is a reserved default for no custom combat scenario logic to execute upon entering
     * and exiting combat.
     *
     * @param combatId ID of the combat scenario; this is primarily used to determine what custom logic should be
     *                 executed upon entering and exiting combat (pass a value of zero for no custom logic)
     * @param col center column of combat field
     * @param row center row of combat field
     * @param trackName name/title of track to be played during combat ('Sound.NO_TRACK' to swap to no track playing,
     *                  'Sound.RETAIN_TRACK' to retain current track playing upon entering and exiting combat)
     * @param entityId entity ID of non-player-side entity to be fought (opponent)
     * @throws IllegalArgumentException if no non-null opponents are available, if a party member is passed as an
     * opponent, or if the player entity is passed as an opponent
     */
    public void initiateCombat(int combatId, int col, int row, String trackName,
                               int entityId) {

        initiateCombat(combatId, col, row, trackName, entityId, -1, -1);
    }


    /**
     * Initiates entering combat with a fade-to-black transition and performs necessary loading.
     * Applicable entities will be set to a state of combating.
     * Note that any entities in a state of conversing will be removed from said state.
     * Note that an entity ID of '-1' is a reserved default for no entity and will not be considered.
     * Note that a combat ID of zero is a reserved default for no custom combat scenario logic to execute upon entering
     * and exiting combat.
     *
     * @param combatId ID of the combat scenario; this is primarily used to determine what custom logic should be
     *                 executed upon entering and exiting combat (pass a value of zero for no custom logic)
     * @param col center column of combat field
     * @param row center row of combat field
     * @param trackName name/title of track to be played during combat ('Sound.NO_TRACK' to swap to no track playing,
     *                  'Sound.RETAIN_TRACK' to retain current track playing upon entering and exiting combat)
     * @param entityId1 entity ID of first non-player-side entity to be fought (opponent 1)
     * @param entityId2 entity ID of second non-player-side entity to be fought (opponent 2)
     * @throws IllegalArgumentException if no non-null opponents are available, if a party member is passed as an
     * opponent, or if the player entity is passed as an opponent
     */
    public void initiateCombat(int combatId, int col, int row, String trackName,
                               int entityId1, int entityId2) {

        initiateCombat(combatId, col, row, trackName, entityId1, entityId2, -1);
    }


    /**
     * Initiates combat with a fade-to-black transition and performs necessary loading.
     * Applicable entities will be set to a state of combating.
     * Note that any entities in a state of conversing will be removed from said state.
     * Note that an entity ID of '-1' is a reserved default for no entity and will not be considered.
     * Note that a combat ID of zero is a reserved default for no custom combat scenario logic to execute upon entering
     * and exiting combat.
     *
     * @param combatId ID of the combat scenario; this is primarily used to determine what custom logic should be
     *                 executed upon entering and exiting combat (pass a value of zero for no custom logic)
     * @param col center column of combat field
     * @param row center row of combat field
     * @param trackName name/title of track to be played during combat ('Sound.NO_TRACK' to swap to no track playing,
     *                  'Sound.RETAIN_TRACK' to retain current track playing upon entering and exiting combat)
     * @param entityId1 entity ID of first non-player-side entity to be fought (opponent 1)
     * @param entityId2 entity ID of second non-player-side entity to be fought (opponent 2)
     * @param entityId3 entity ID of third non-player-side entity to be fought (opponent 3)
     * @throws IllegalArgumentException if no non-null opponents are available, if a party member is passed as an
     * opponent, or if the player entity is passed as an opponent
     */
    public void initiateCombat(int combatId, int col, int row, String trackName,
                               int entityId1, int entityId2, int entityId3) {

        EntityBase opponent1 = null;
        EntityBase opponent2 = null;
        EntityBase opponent3 = null;

        if (entityId1 != -1) {

             opponent1 = gp.getEntityM().getEntityById(entityId1);
        }

        if (entityId2 != -1) {

             opponent2 = gp.getEntityM().getEntityById(entityId2);
        }

        if (entityId3 != -1) {

             opponent3 = gp.getEntityM().getEntityById(entityId3);
        }

        boolean playerOpponent = false;
        boolean partyOpponent1 = false;
        boolean partyOpponent2 = false;
        boolean partyOpponent3 = false;

        if (opponent1 != null) {

            playerOpponent = (entityId1 == gp.getEntityM().getPlayer().getEntityId());
            partyOpponent1 = (gp.getEntityM().getParty().get(entityId1) != null);
        }

        if (opponent2 != null) {

            if (!playerOpponent) {

                playerOpponent = (entityId2 == gp.getEntityM().getPlayer().getEntityId());
            }
            partyOpponent2 = (gp.getEntityM().getParty().get(entityId2) != null);
        }

        if (opponent3 != null) {

            if (playerOpponent) {

                playerOpponent = (entityId3 == gp.getEntityM().getPlayer().getEntityId());
            }
            partyOpponent3 = (gp.getEntityM().getParty().get(entityId3) != null);
        }

        if (!((opponent1 == null) && (opponent2 == null) && (opponent3 == null))
                && (!playerOpponent) && (!partyOpponent1) && (!partyOpponent2) && (!partyOpponent3)) {

            // Set combat as active.
            if (UtilityTool.VERBOSE_LOGGING) {

                UtilityTool.logInfo("Entering combat state with combat ID '" + combatId + "'.");
            }
            combatActive = true;
            this.combatId = combatId;

            // Clear any conversing entities.
            gp.getEntityM().clearConversingEntities();

            // Play combat music.
            if (trackName.equals(Sound.NO_TRACK)) {

                gp.getSoundS().stopTrack(false);
            } else if (trackName.equals(Sound.RETAIN_TRACK)) {

                retainPreCombatTrack = true;
            } else {

                gp.getSoundS().swapTrack(trackName, false);
            }

            // Initiate transition into combat.
            gp.getTransitionS().initiateTransition(TransitionType.ENTER_COMBAT);

            // Set the center tile (column/row) of the combat field.
            fieldCenterCol = col;
            fieldCenterRow = row;

            // Store non-player-side entities to be called later.
            if (opponent1 != null) {
                nonPlayerSideEntities.add(entityId1);
            }

            if (opponent2 != null) {
                nonPlayerSideEntities.add(entityId2);
            }

            if (opponent3 != null) {
                nonPlayerSideEntities.add(entityId3);
            }
        } else {

            if ((opponent1 == null) && (opponent2 == null) && (opponent3 == null)) {

                throw new IllegalArgumentException("Attempted to initiate combat with no non-null opponents");
            } else if (playerOpponent) {

                throw new IllegalArgumentException("Attempted to initiate combat with the player entity as an opponent");
            } else {

                throw new IllegalArgumentException("Attempted to initiate combat with a party member as an opponent");
            }
        }
    }


    /**
     * Initiates exiting combat with a fade-to-black transition and performs necessary loading.
     */
    public void exitCombat() {

        gp.getTransitionS().initiateTransition(TransitionType.EXIT_COMBAT);
        gp.getEventM().cleanupConversation(1);                                                                          // Cleans up from the last combat message displayed and removes dialogue window from screen.
    }


    /**
     * Performs any loading that needs to be done once the screen fades to black during an enter combat transition.
     */
    public void handleEnterCombatTransitionLoading() {

        // Override camera tracking (we'd like to manually position it for combat).
        gp.getCameraS().setOverrideEntityTracking(true);

        // Prepare entities for combat.
        preparePlayerSideEntities();
        prepareNonPlayerSideEntities();

        // Generate turn order.
        generateTurnOrder();

        // Set camera to center on the combat field.
        gp.getCameraS().setCameraSnap((fieldCenterCol * GamePanel.NATIVE_TILE_SIZE) + (GamePanel.NATIVE_TILE_SIZE / 2),
                fieldCenterRow * GamePanel.NATIVE_TILE_SIZE);

        // Execute custom logic.
        gp.getCombatLoadS().handleEnterCombatTransitionLoading(combatId);
    }


    /**
     * Closes out an enter combat transition that has completed all of its phases (i.e., tidies up any variables and
     * runs any additional custom logic).
     * This is to be run once an enter combat transition has fully completed (i.e., faded from black).
     */
    public void concludeEnterCombatTransition() {

        // Build first message to display at the start of the fight.
        ArrayList<String> nonPlayerSideEntityNames = new ArrayList<>();

        for (int entityId : nonPlayerSideEntities) {

            nonPlayerSideEntityNames.add(gp.getEntityM().getEntityById(entityId).getName());
        }
        String message = UtilityTool.buildEntityListMessage(nonPlayerSideEntityNames, false);

        // Stage a message and root combat sub-menu action.
        message += " would like to fight!";
        addQueuedActionBack(new Act_ReadMessage(gp, message, true, true));
        addQueuedActionBack(new Act_ToggleCombatUi(gp, true));
        generateRootSubMenuAction();

        // Execute custom logic.
        gp.getCombatLoadS().concludeEnterCombatTransition(combatId);

        // Enter the main method for progressing combat.
        progressCombat();
    }


    /**
     * Performs any loading that needs to be done once the screen fades to black during an exit combat transition.
     */
    public void handleExitCombatTransitionLoading() {

        // Reset camera.
        gp.getCameraS().resetCameraSnap();

        // Reset all non-party member combating entities back to pre-combat positions.
        // Note that this includes the player entity.
        for (int entityId : storedEntityCols.keySet()) {
            if (!gp.getEntityM().getParty().containsKey(entityId)) {
                gp.getEntityM().getEntityById(entityId).setCol(storedEntityCols.get(entityId));
            }
        }
        for (int entityId : storedEntityRows.keySet()) {
            if (!gp.getEntityM().getParty().containsKey(entityId)) {
                gp.getEntityM().getEntityById(entityId).setRow(storedEntityRows.get(entityId));
            }
        }

        // Reset all non-party member combating entities back to pre-combat directions.
        // Note that this includes the player entity.
        for (int entityId : storedEntityDirections.keySet()) {
            if (!gp.getEntityM().getParty().containsKey(entityId)) {
                gp.getEntityM().getEntityById(entityId).setDirectionCurrent(storedEntityDirections.get(entityId));
            }
        }

        // Transfer non-combating followers of all combating entities back from the standby entity map.
        for (int entityId : storedNonCombatingFollowers) {
            switch (gp.getEntityM().getStandby().get(entityId).getType()) {
                case OBJECT:
                    gp.getEntityM().transferEntity(gp.getEntityM().getStandby(), gp.getEntityM().getObj(), entityId);
                    break;
                case CHARACTER:
                    gp.getEntityM().transferEntity(gp.getEntityM().getStandby(), gp.getEntityM().getNpc(), entityId);
                    break;
            }
        }

        // Warp party members to player entity.
        // Must run before restoring pre-combat party ordering, or active party may automatically walk away from player
        // entity towards combat location after transition is complete.
        // This is because, when the follower chain is rebuilt in `swapEntityInParty()`, the last tile position of the
        // followed is set to the follower's combat position since the warp to player hasn't happened yet, causing this
        // issue.
        gp.getWarpS().warpActivePartyMembersToPlayer();
        gp.getWarpS().warpInactivePartyMembersToPlayer();

        // Warp non-party member followers to the player entity.
        // Note that followers of all other combating entities are NOT warped to their followers and will be left at
        // their pre-combat positions.
        gp.getWarpS().warpFollowersToFollowed(gp.getEntityM().getPlayer(), gp.getEntityM().getNpc());
        gp.getWarpS().warpFollowersToFollowed(gp.getEntityM().getPlayer(), gp.getEntityM().getObj());

        // Restore pre-combat ordering of party members (run before restoring pre-combat party hidden states).
        for (int i = 0; i < partyOrdering.size(); i++) {
            gp.getPartyS().swapEntityInParty(partyOrdering.get(i),
                    (int)gp.getEntityM().getParty().keySet().toArray()[i],
                    false);
        }

        // Restore pre-combat positions and directions of inactive party members.
        // Must run after warping party members to player entity and restoring pre-combat ordering of party members.
        if (gp.getEntityM().getParty().size() > gp.getEntityM().getNumActivePartyMembers()) {
            LimitedArrayList<Integer> inactivePartyMembersIds =
                    new LimitedArrayList<>(gp.getEntityM().getParty().size()
                            - gp.getEntityM().getNumActivePartyMembers());
            int i = 0;
            for (EntityBase entity : gp.getEntityM().getParty().values()) {
                if ((i >= gp.getEntityM().getNumActivePartyMembers()) && (entity != null)) {
                    inactivePartyMembersIds.add(entity.getEntityId());
                }
                i++;
            }
            for (int entityId : storedEntityCols.keySet()) {
                if (inactivePartyMembersIds.contains(entityId)) {
                    gp.getEntityM().getEntityById(entityId).setCol(storedEntityCols.get(entityId));
                }
            }
            for (int entityId : storedEntityRows.keySet()) {
                if (inactivePartyMembersIds.contains(entityId)) {
                    gp.getEntityM().getEntityById(entityId).setRow(storedEntityRows.get(entityId));
                }
            }
            for (int entityId : storedEntityDirections.keySet()) {
                if (inactivePartyMembersIds.contains(entityId)) {
                    gp.getEntityM().getEntityById(entityId).setDirectionCurrent(storedEntityDirections.get(entityId));
                }
            }
        }

        // Reset all combating entities back to pre-combat hidden state.
        // Must run after restoring pre-combat party ordering, or party members may have incorrect hidden status after
        // transition is complete.
        for (int entityId : storedEntityHidden.keySet()) {
            gp.getEntityM().getEntityById(entityId).setHidden(storedEntityHidden.get(entityId));
        }

        // Swap music, if applicable.
        if (!retainPreCombatTrack) {
            gp.getSoundS().swapTrack(
                    gp.getMapM().getLoadedMap().getTrack(gp.getMapM().getLoadedMap().getMapState()), true);
        }

        // Reset combatting entities.
        resetAllCombatingEntitySecondaryAttributes();
        gp.getEntityM().clearCombatingEntities();

        // Execute custom logic.
        gp.getCombatLoadS().handleExitCombatTransitionLoading(combatId, combatLost);
    }


    /**
     * Closes out an exit combat transition that has completed all of its phases (i.e., tidies up any variables and runs
     * any additional custom logic).
     * This is to be run once an exit combat transition has fully completed (i.e., faded from black).
     */
    public void concludeExitCombatTransition() {

        boolean tempCombatLost = combatLost;                                                                            // Temporarily store `combatLost` since it will be reset.
        int tempCombatId = combatId;                                                                                    // Temporarily store `combatId` since it will be reset.
        reset();

        if (UtilityTool.VERBOSE_LOGGING) {

            UtilityTool.logInfo("Exiting combat state.");
        }
        combatActive = false;

        gp.getCombatLoadS().concludeExitCombatTransition(tempCombatId, tempCombatLost);
    }


    /**
     * Adds an action to the end of the queue of actions.
     *
     * @param action action to add to the queue
     */
    public void addQueuedActionBack(ActionBase action) {

        queuedActions.addLast(action);
    }


    /**
     * Adds an action to the front of the queue of actions.
     *
     * @param action action to add to the queue
     */
    public void addQueuedActionFront(ActionBase action) {

        queuedActions.addFirst(action);
    }


    /**
     * Removes the entity at the front of the turn order queue.
     * This will set the next entity in the queue to have its turn if able.
     * If the queue is empty, the turn order will be regenerated with all combating entities.
     *
     * @throws IllegalStateException if the turn order is generated with no viable entities
     */
    public void endEntityTurn() {

        if (getSubMenuMemory(1) != null) {

            getSubMenuMemory(1).setLastOfTurn(true);                                                                    // Flag the last sub-menu memory as the last sub-menu to appear during the previous entity's turn.
        }
        boolean viableEntity = false;                                                                                   // A viable entity is a non-fainted one.
        boolean generateTurnOrderCalled = false;                                                                        // Boolean tracking whether the turn order has already been re-generated.
        newTurnRootSubMenuDisplayed = false;                                                                            // Reset variable tracking whether root combat sub-menu has been displayed this turn yet or not.

        if (targetLockEntityId != -1) {

            if (targetLockTurns <= 0) {

                String message =
                        gp.getEntityM().getEntityById(targetLockEntityId).getName()
                                + " is no longer the center of attention.";
                addQueuedActionBack(new Act_ReadMessage(
                        gp,
                        message,
                        true,
                        true));
                resetTargetLockEntity();
            } else {

                targetLockTurns--;
            }
        }

        while (!viableEntity) {

            queuedEntityTurnOrder.removeFirst();

            if (queuedEntityTurnOrder.peekFirst() == null) {

                if (!generateTurnOrderCalled) {

                    generateTurnOrder();
                    generateTurnOrderCalled = true;
                } else {

                    throw new IllegalStateException("Generated combat turn order with no viable entities");
                }
            }

            if ((gp.getEntityM().getEntityById(queuedEntityTurnOrder.peekFirst()).getStatus() != EntityStatus.FAINT)
                    && (!gp.getEntityM().getEntityById(queuedEntityTurnOrder.peekFirst()).isHidden())) {                // If hidden, entity is not actively participating in combat (ex. party member in reserve).

                viableEntity = true;
                guardingEntities.remove(queuedEntityTurnOrder.peekFirst());                                             // If guarding, remove the entity whose turn it now is from a guarding state.
            }
        }
    }


    /**
     * Polls whether any entities have just fainted; in other words, if any entities have zero
     * life but not a fainted status.
     * If one or more have and that causes either all party entities to be fainted or all opposing entities to have
     * fainted, then combat will be exited.
     *
     * @return whether any entities have just fainted (true) or not (false)
     */
    public boolean pollFainting() {

        boolean justFainted = false;

        if (actionPreFaint(gp.getEntityM().getPlayer().getEntityId())) {

            justFainted = true;
        };

        for (int entityId : gp.getEntityM().getParty().keySet()) {

            if (actionPreFaint(entityId) && !justFainted) {

                justFainted = true;
            };
        }

        for (int entityId : nonPlayerSideEntities) {

            if (actionPreFaint(entityId) && !justFainted) {

                justFainted = true;
            }
        }

        if (checkAllNonPlayerSideZeroLife()) {                                                                          // Combat is won if all non-player-side entities have fainted.

            String message = "Player won the fight!";
            addQueuedActionBack(new Act_ReadMessage(gp, message, true, true));
            addQueuedActionBack(new Act_ToggleCombatUi(gp, false));
            addQueuedActionBack(new Act_ExitCombat(gp));
        }

        if (checkPlayerZeroLife()) {                                                                                    // Combat is only lost if the player entity has fainted.

            combatLost = true;

            if (!checkAllPartyZeroLife()) {

                String messageOne = "Without " + gp.getEntityM().getPlayer().getName() + " to lead, the remaining party"
                        + " fell into disarray.";
                addQueuedActionBack(new Act_ReadMessage(gp, messageOne, true, true));
            }
            String messageTwo = "Player lost the fight.";
            addQueuedActionBack(new Act_ReadMessage(gp, messageTwo, true, true));
            addQueuedActionBack(new Act_ToggleCombatUi(gp, false));
            addQueuedActionBack(new Act_ExitCombat(gp));
        }
        return justFainted;
    }


    /**
     * Checks whether the player entity has zero life or not.
     *
     * @return whether the player entity has zero life (true) or not (false)
     */
    public boolean checkPlayerZeroLife() {

        if (gp.getEntityM().getPlayer().getLife() <= 0) {

            return true;
        }
        return false;
    }


    /**
     * Checks whether all active party member entities (excluding the player entity) have zero life or not.
     *
     * @return whether all active party member entities have zero life (true) or not (false)
     */
    public boolean CheckAllActivePartyZeroLife() {

        int zeroLifeActivePartyCount = 0;
        int entityIndex = 0;

        for (int entityId : gp.getEntityM().getParty().keySet()) {

            if (entityIndex < gp.getEntityM().getNumActivePartyMembers()) {

                if (gp.getEntityM().getEntityById(entityId).getLife() <= 0) {

                    zeroLifeActivePartyCount++;
                }
            }
            entityIndex++;
        }

        if (zeroLifeActivePartyCount == entityIndex) {

            return true;
        } else {

            return false;
        }
    }


    /**
     * Checks whether all party member entities (active and inactive, excluding the player entity) have zero life or
     * not.
     *
     * @return whether all party member entities have zero life (true) or not (false)
     */
    public boolean checkAllPartyZeroLife() {

        int zeroLifePartyCount = 0;

        for (int entityId : gp.getEntityM().getParty().keySet()) {

            if (gp.getEntityM().getEntityById(entityId).getLife() <= 0) {

                zeroLifePartyCount++;
            }
        }

        if (zeroLifePartyCount == gp.getEntityM().getParty().size()) {

            return true;
        } else {

            return false;
        }
    }


    /**
     * Checks whether all non-player-side entities have zero life or not.
     *
     * @return whether all non-player-side entities have zero life (true) or not (false)
     */
    public boolean checkAllNonPlayerSideZeroLife() {

        int zeroLifeNonPlayerSideCount = 0;

        for (int entityId : gp.getCombatM().getNonPlayerSideEntities()) {

            if (gp.getEntityM().getEntityById(entityId).getLife() <= 0) {

                zeroLifeNonPlayerSideCount++;
            }
        }

        if (zeroLifeNonPlayerSideCount == gp.getCombatM().getNonPlayerSideEntities().size()) {

            return true;
        } else {

            return false;
        }
    }


    /**
     * Retrieves all ally entities.
     * This method works for both player-side and non-player-side entities.
     *
     * @param sourceEntityId ID of entity using move
     * @return ally entities
     */
    public ArrayList<EntityBase> retrieveAllyEntities(int sourceEntityId) {

        ArrayList<EntityBase> allyEntities = new ArrayList<>();

        if ((sourceEntityId == gp.getEntityM().getPlayer().getEntityId())
                || (gp.getEntityM().getParty().containsKey(sourceEntityId))) {                                          // See if a player-side entity used the move.

            if (sourceEntityId != gp.getEntityM().getPlayer().getEntityId()) {

                allyEntities.add(gp.getEntityM().getPlayer());
            }

            int entityIndex = 0;

            for (EntityBase entity : gp.getEntityM().getParty().values()) {

                if ((entity.getEntityId() != sourceEntityId) && (entity.getStatus() != EntityStatus.FAINT)) {

                    allyEntities.add(entity);
                }
                entityIndex++;

                if (entityIndex >= gp.getEntityM().getNumActivePartyMembers()) {

                    break;
                }
            }
        } else {                                                                                                        // A non-player-side entity must have used the move.

            EntityBase allyEntity;

            for (int entityId : gp.getCombatM().getNonPlayerSideEntities()) {

                allyEntity = gp.getEntityM().getEntityById(entityId);

                if ((allyEntity.getEntityId() != sourceEntityId) && (allyEntity.getStatus() != EntityStatus.FAINT)) {

                    allyEntities.add(allyEntity);
                }
            }
        }
        return allyEntities;
    }


    /**
     * Builds and refreshes message for the skill and player-side skills combat sub-menus.
     */
    public void refreshSkillSubMenuDialogue() {

        if (getSubMenuMemory(1).getType() == SubMenuType.SKILL) {

            gp.getDialogueR().initiatePlaceholderMessage(
                    buildSkillSubMenuDialogue(gp.getEntityM().getEntityById(queuedEntityTurnOrder.peekFirst()),
                            gp.getSubMenuH().getIndexSelected()), false);
        } else if (getSubMenuMemory(1).getType() == SubMenuType.PLAYER_SIDE_SKILLS) {

            gp.getDialogueR().initiatePlaceholderMessage(
                    buildSkillSubMenuDialogue(
                            gp.getEntityM().getEntityById(lastGeneratedPlayerSideOptions
                                    .get(getSubMenuMemory(3).getSelectedOption())),
                            gp.getSubMenuH().getIndexSelected()), false);
        }
    }


    /**
     * Resets the target lock entity to its default value (i.e., no target lock entity).
     */
    public void resetTargetLockEntity() {

        targetLockEntityId = -1;
        targetLockTurns = 0;
    }


    /**
     * Runs the action at the front of the queue of actions.
     * Once complete, the action is removed from the queue.
     */
    private void runNextQueuedAction() {

        if (queuedActions.peekFirst() != null) {

            if (UtilityTool.VERBOSE_LOGGING) {

                UtilityTool.logInfo("Running combat action: " + queuedActions.peekFirst().getClass().getSimpleName());
            }
            queuedActions.pollFirst().run();
        } else {
            progressCombat();                                                                                           // If no actions are queued, force back to the root combat sub-menu.
        }
    }


    /**
     * Runs logic based on the last sub-menu type that was generated and option that was selected in said sub-menu.
     */
    private void runSubMenuSelection() {

        lastActionSubmenu = false;

        switch (getSubMenuMemory(1).getType()) {
            case ROOT:
                runRootSubMenuSelection();
                break;
            case GUARD:
                runGuardSubMenuSelection();
                break;
            case SKILL:
                runSkillSubMenuSelection();
                break;
            case TARGET_SELECT:
                runTargetSelectSubMenuSelection();
                break;
            case TARGET_CONFIRM:
                runTargetConfirmSubMenuSelection();
                break;
            case PARTY:
                runPartySubMenuSelection();
                break;
            case PLAYER_SIDE_MANAGE:
                runPlayerSideManageSubMenuSelection();
                break;
            case PLAYER_SIDE_ATTRIBUTES:
                runPlayerSideAttributesSubMenuSelection();
                break;
            case PLAYER_SIDE_SKILLS:
                runPlayerSideSkillsSubmenuSelection();
                break;
            case PLAYER_SIDE_SWAP:
                runPlayerSideSwapSubMenuSelection();
                break;
        }
    }


    /**
     * Runs logic based on the last option that was selected in the root combat sub-menu.
     */
    private void runRootSubMenuSelection() {

        switch (getSubMenuMemory(1).getSelectedOption()) {
            case 0:
                runRootSubMenuSelectionGuard();
                break;
            case 1:
                runRootSubMenuSelectionAttack();
                break;
            case 2:
                runRootSubMenuSelectionSkill();
                break;
            case 3:
                runRootSubMenuSelectionParty();
                break;
//            case 4:
//                runRootSubMenuSelectionFlee();
//                break;
        }
    }


    /**
     * Runs logic pertaining to the selection of the 'Guard' option in the root combat sub-menu.
     */
    private void runRootSubMenuSelectionGuard() {

        ArrayList<String> guardOptions = new ArrayList<>();
        HashMap<Integer, Vector3f> colors = new HashMap<>();
        guardOptions.add("Confirm");
        guardOptions.add("Back");
        colors.put(guardOptions.size() - 1, SubMenuHandler.BACK_OPTION_COLOR);
        addQueuedActionBack(new Act_ReadMessage(gp, buildGuardSubMenuDialogue(), false, false));
        addQueuedActionBack(new Act_GenerateSubMenu(gp, SubMenuType.GUARD, guardOptions, colors));
    }


    /**
     * Runs logic pertaining to the selection of the 'Attack' option in the root combat sub-menu.
     */
    private void runRootSubMenuSelectionAttack() {

        if (defaultMove.isHitAllTargets()) {

            generateTargetConfirmSubMenuAction(defaultMove);
        } else {

            generateTargetSelectSubMenuAction(defaultMove);
        }
    }


    /**
     * Runs logic pertaining to the selection of the 'Skill' option in the root combat sub-menu.
     */
    private void runRootSubMenuSelectionSkill() {

        ArrayList<String> moveOptions = new ArrayList<>();
        HashMap<Integer, Vector3f> colors = new HashMap<>();
        HashSet<Integer> disabledOptions = new HashSet<>();
        HashMap<Integer, String> optionDescriptions = new HashMap<>();

        for (MoveBase move : gp.getEntityM().getEntityById(queuedEntityTurnOrder.peekFirst()).getMoves()) {

            moveOptions.add(move.getName());

            if (move.getSkillPoints() > gp.getEntityM()
                    .getEntityById(queuedEntityTurnOrder.peekFirst()).getSkill()) {

                colors.put(moveOptions.size() - 1, SubMenuHandler.DISABLED_OPTION_COLOR);
                disabledOptions.add(moveOptions.size() - 1);
            }
            optionDescriptions.put(moveOptions.size() - 1,
                    buildSkillSubMenuDescription(
                            gp.getEntityM().getEntityById(queuedEntityTurnOrder.peekFirst()), move));
        }
        moveOptions.add("Back");
        colors.put(moveOptions.size() - 1, SubMenuHandler.BACK_OPTION_COLOR);
        addQueuedActionBack(new Act_ReadMessage(gp,
                buildSkillSubMenuDialogue(gp.getEntityM().getEntityById(queuedEntityTurnOrder.peekFirst()), 0),
                false, false));
        addQueuedActionBack(new Act_GenerateSubMenu(
                gp, SubMenuType.SKILL, moveOptions, colors, disabledOptions, optionDescriptions));
    }


    /**
     * Runs logic pertaining to the selection of the 'Party' option in the root combat sub-menu.
     */
    private void runRootSubMenuSelectionParty() {

        ArrayList<String> playerSideOptions = generatePlayerSideOptions();
        HashMap<Integer, Vector3f> colors = new HashMap<>();
        playerSideOptions.add("Back");
        colors.put(playerSideOptions.size() - 1, SubMenuHandler.BACK_OPTION_COLOR);
        addQueuedActionBack(new Act_ReadMessage(gp, buildPartySubMenuDialogue(), false, false));
        addQueuedActionBack(new Act_GenerateSubMenu(
                gp, SubMenuType.PARTY, playerSideOptions, colors,
                buildPartySubMenuDescriptions(lastGeneratedPlayerSideOptions)));
    }


    /**
     * Runs logic pertaining to the selection of the 'Flee' option in the root combat sub-menu.
     */
    private void runRootSubMenuSelectionFlee() {

        String message = "Fleeing the fight!";
        addQueuedActionBack(new Act_ReadMessage(gp, message, true, true));
        addQueuedActionBack(new Act_ToggleCombatUi(gp, false));
        addQueuedActionBack(new Act_ExitCombat(gp));
    }


    /**
     * Runs logic based on the last option that was selected in the guard combat sub-menu.
     */
    private void runGuardSubMenuSelection() {

        if (getSubMenuMemory(1).getSelectedOption() == (getSubMenuMemory(1).getOptions().size() - 1)) {                 // Determine whether the 'Back' option was selected or not.

            revertSubMenuSelection();
        } else {

            guardingEntities.add(queuedEntityTurnOrder.peekFirst());
        }
    }


    /**
     * Runs logic based on the last option that was selected in the skill combat sub-menu.
     */
    private void runSkillSubMenuSelection() {

        if (getSubMenuMemory(1).getSelectedOption() == (getSubMenuMemory(1).getOptions().size() - 1)) {                 // Determine whether the 'Back' option was selected or not.

            revertSubMenuSelection();
        } else {

            MoveBase move = gp.getEntityM().getEntityById(queuedEntityTurnOrder.peekFirst())
                    .getMoves().get(getSubMenuMemory(1).getSelectedOption());

            if (move.isHitAllTargets()) {

                generateTargetConfirmSubMenuAction(move);
            } else {

                generateTargetSelectSubMenuAction(move);
            }
        }
    }


    /**
     * Runs logic based on the last option that was selected in the party combat sub-menu.
     */
    private void runPartySubMenuSelection() {

        if (getSubMenuMemory(1).getSelectedOption() == (getSubMenuMemory(1).getOptions().size() - 1)) {                 // Determine whether the 'Back' option was selected or not.

            revertSubMenuSelection();
        } else {

            generatePlayerSideManageSubMenuAction(gp.getEntityM().getEntityById(
                    lastGeneratedPlayerSideOptions.get(getSubMenuMemory(1).getSelectedOption())));
        }
    }


    /**
     * Runs logic based on the last option that was selected in the player-side manage combat sub-menu.
     */
    private void runPlayerSideManageSubMenuSelection() {

        if (getSubMenuMemory(1).getSelectedOption() == (getSubMenuMemory(1).getOptions().size() - 1)) {                 // Determine whether the 'Back' option was selected or not.

            revertSubMenuSelection();
        } else {

            if (getSubMenuMemory(1).getSelectedOption() == 0) {                                                         // "View Attributes" was selected.

                generatePlayerSideAttributesSubMenuAction(gp.getEntityM().getEntityById(
                        lastGeneratedPlayerSideOptions.get(getSubMenuMemory(2).getSelectedOption())));

            } else if (getSubMenuMemory(1).getSelectedOption() == 1) {                                                  // "View Skills" was selected.

                generatePlayerSideSkillsSubMenuAction(gp.getEntityM().getEntityById(
                        lastGeneratedPlayerSideOptions.get(getSubMenuMemory(2).getSelectedOption())));
            } else {                                                                                                    // "Swap In/Out" was selected.

                String selectedOption = getSubMenuMemory(1).getOptions().get(getSubMenuMemory(1).getSelectedOption());

                if (selectedOption.equals("Swap In")) {

                    generatePlayerSideSwapInSubMenuAction(gp.getEntityM().getEntityById(lastGeneratedPlayerSideOptions
                            .get(getSubMenuMemory(2).getSelectedOption())));
                } else if (selectedOption.equals("Swap Out")) {

                    generatePlayerSideSwapOutSubMenuAction(gp.getEntityM().getEntityById(lastGeneratedPlayerSideOptions
                            .get(getSubMenuMemory(2).getSelectedOption())));
                }
            }
        }
    }


    /**
     * Runs logic based on the last option that was selected in the player-side attributes combat sub-menu.
     */
    private void runPlayerSideAttributesSubMenuSelection() {

        revertSubMenuSelection();
    }


    /**
     * Runs logic based on the last option that was selected in the player-side skills combat sub-menu.
     */
    private void runPlayerSideSkillsSubmenuSelection() {

        revertSubMenuSelection();
    }


    /**
     * Runs logic based on the last option that was selected in the player-side swap combat sub-menu.
     */
    private void runPlayerSideSwapSubMenuSelection() {

        if (getSubMenuMemory(1).getSelectedOption() == (getSubMenuMemory(1).getOptions().size() - 1)) {                 // Determine whether the 'Back' option was selected or not.

            revertSubMenuSelection();
        } else {

            int playerSideId1 = lastGeneratedPlayerSideOptions.get(getSubMenuMemory(3).getSelectedOption());
            int playerSideId2 = -1;                                                                                     // Default entity ID to force error if invalid option somehow selected.
            String selectedSwapOption = getSubMenuMemory(2).getOptions().get(getSubMenuMemory(2).getSelectedOption());

            if (selectedSwapOption.equals("Swap In")) {

                playerSideId2 = lastGeneratedActivePlayerSideOptions.get(getSubMenuMemory(1).getSelectedOption());
            } else if (selectedSwapOption.equals("Swap Out")) {

                playerSideId2 = lastGeneratedInactivePlayerSideOptions
                        .get(getSubMenuMemory(1).getSelectedOption());
            }
            addQueuedActionBack(new Act_SwapPlayerSideEntity(gp, playerSideId1, playerSideId2));
        }
    }


    /**
     * Runs logic based on the last option that was selected in the target select combat sub-menu.
     */
    private void runTargetSelectSubMenuSelection() {

        if (getSubMenuMemory(1).getSelectedOption() == (getSubMenuMemory(1).getOptions().size() - 1)) {                 // Determine whether the 'Back' option was selected or not.

            revertSubMenuSelection();
        } else {                                                                                                        // Determine appropriate target entity that was selected.

            EntityBase sourceEntity = gp.getEntityM().getEntityById(queuedEntityTurnOrder.peekFirst());
            MoveBase move = null;

            switch (getSubMenuMemory(2).getType()) {
                case ROOT:                                                                                              // A basic attack must have been selected (i.e., 'Attack' option in root combat sub-menu).
                    move = defaultMove;
                    break;
                case SKILL:                                                                                             // A skill move must have been selected (i.e., 'Skill' option in root combat sub-menu).
                    move = sourceEntity.getMoves().get(getSubMenuMemory(2).getSelectedOption());
                    break;
            }
            EntityBase targetEntity = gp.getEntityM().getEntityById(
                    lastGeneratedTargetOptions.get(getSubMenuMemory(1).getSelectedOption()));
            String message = buildUseMoveDialogue(sourceEntity.getName(), move.getName(), move.getMoveId());
            addQueuedActionBack(new Act_ReadMessage(gp, message, false, true));
            addQueuedActionBack(new Act_UseMove(gp, move, sourceEntity.getEntityId(), targetEntity.getEntityId()));
        }
    }


    /**
     * Runs logic based on the last option that was selected in the target confirm combat sub-menu.
     */
    private void runTargetConfirmSubMenuSelection() {

        if (getSubMenuMemory(1).getSelectedOption() == (getSubMenuMemory(1).getOptions().size() - 1)){                  // Determine whether the 'Back' option was selected or not.

            revertSubMenuSelection();
        } else {                                                                                                        // Determine appropriate target entity that was selected.

            EntityBase sourceEntity = gp.getEntityM().getEntityById(queuedEntityTurnOrder.peekFirst());
            MoveBase move = null;

            switch (getSubMenuMemory(2).getType()) {
                case ROOT:                                                                                              // A basic attack must have been selected (i.e., 'Attack' option in root combat sub-menu).
                    move = defaultMove;
                    break;
                case SKILL:                                                                                             // A skill move must have been selected (i.e., 'Skill' option in root combat sub-menu).
                    move = sourceEntity.getMoves().get(getSubMenuMemory(2).getSelectedOption());
                    break;
            }
            String message = buildUseMoveDialogue(sourceEntity.getName(), move.getName(), move.getMoveId());
            addQueuedActionBack(new Act_ReadMessage(gp, message, false, true));
            addQueuedActionBack(new Act_UseMove(gp, move, sourceEntity.getEntityId(), lastGeneratedTargetOptions));
        }
    }


    /**
     * Generates a sub-menu action for the root sub-menu and adds it to the back of the queue of actions.
     */
    private void generateRootSubMenuAction() {

        HashMap<Integer, Vector3f> colors = new HashMap<>();
        HashSet<Integer> disabledOptions = new HashSet<>();

        if (gp.getEntityM().getEntityById(queuedEntityTurnOrder.peekFirst()).getMoves().isEmpty()) {

            colors.put(2, SubMenuHandler.DISABLED_OPTION_COLOR);
            disabledOptions.add(2);
        }
        HashMap<Integer, String> optionDescriptions = buildRootSubMenuDescriptions();
        addQueuedActionBack(new Act_ReadMessage(
                gp,
                buildRootSubMenuDialogue(gp.getEntityM().getEntityById(queuedEntityTurnOrder.peekFirst())),
                false,
                !newTurnRootSubMenuDisplayed));
        addQueuedActionBack(new Act_GenerateSubMenu(gp, SubMenuType.ROOT,
                rootCombatOptions, colors, disabledOptions, optionDescriptions));

        if (!newTurnRootSubMenuDisplayed) {

            newTurnRootSubMenuDisplayed = true;
        }
    }


    /**
     * Generates a sub-menu action for selecting targets and adds it to the back of the queue of actions.
     * The list of last generated selectable targets in combat is also refreshed.
     *
     * @param move move for which targets will be generated
     */
    private void generateTargetSelectSubMenuAction(MoveBase move) {

        ArrayList<String> targetOptions = generatePlayerSideTargetOptions(move);
        HashMap<Integer, Vector3f> colors = new HashMap<>();
        targetOptions.add("Back");
        colors.put(targetOptions.size() - 1, SubMenuHandler.BACK_OPTION_COLOR);
        addQueuedActionBack(new Act_ReadMessage(
                gp, buildTargetSelectSubMenuDialogue(move), false, false));
        addQueuedActionBack(new Act_GenerateSubMenu(
                gp, SubMenuType.TARGET_SELECT, targetOptions, colors));
    }


    /**
     * Generates a sub-menu action for managing a player-side entity and adds it to the back of the queue of actions.
     *
     * @param entity player-side entity to display options for
     */
    private void generatePlayerSideManageSubMenuAction(EntityBase entity) {

        ArrayList<String> playerSideOptions = new ArrayList<>();
        HashMap<Integer, Vector3f> colors = new HashMap<>();
        HashSet<Integer> disabledOptions = new HashSet<>();
        playerSideOptions.add("View Attributes");
        playerSideOptions.add("View Skills");

        if (entity.getMoves().isEmpty()) {                                                                              // Disable option if no moves are present.

            colors.put(1, SubMenuHandler.DISABLED_OPTION_COLOR);
            disabledOptions.add(1);
        }

        if (gp.getEntityM().getParty().size() >= gp.getEntityM().getNumActivePartyMembers()) {                          // Determine if there are enough party members for a swap in/out action to take place.

            if (getSubMenuMemory(1).getSelectedOption() <= gp.getEntityM().getNumActivePartyMembers()) {                // Determine if the selected entity is currently in the active party.

                if (entity.getEntityId() != gp.getEntityM().getPlayer().getEntityId()) {                                // Determine if the selected entity is the player entity (which can't be swapped out) or an active party member.

                    playerSideOptions.add("Swap Out");
                }
            } else {

                playerSideOptions.add("Swap In");
            }
        }
        playerSideOptions.add("Back");
        colors.put(playerSideOptions.size() - 1, SubMenuHandler.BACK_OPTION_COLOR);
        addQueuedActionBack(new Act_ReadMessage(
                gp, buildPlayerSideManageSubMenuDialogue(entity), false, false));
        addQueuedActionBack(new Act_GenerateSubMenu(
                gp, SubMenuType.PLAYER_SIDE_MANAGE, playerSideOptions, colors, disabledOptions));
    }


    /**
     * Generates a sub-menu action for viewing a player-side entity's attributes and adds it to the back of the queue of
     * actions.
     *
     * @param entity player-side entity to display attributes for
     */
    private void generatePlayerSideAttributesSubMenuAction(EntityBase entity) {

        ArrayList<String> options = new ArrayList<>();
        HashSet<Integer> disabledOptions = new HashSet<>();
        HashMap<Integer, Vector3f> colors = new HashMap<>();
        HashMap<Integer, String> optionDescriptions = buildPlayerSideAttributesSubMenuDescription(entity);
        options.add("Page 1");
        options.add("Page 2");
        options.add("Back");
        colors.put(0, SubMenuHandler.INFO_OPTION_COLOR);
        colors.put(1, SubMenuHandler.INFO_OPTION_COLOR);
        colors.put(options.size() - 1, SubMenuHandler.BACK_OPTION_COLOR);
        disabledOptions.add(0);
        disabledOptions.add(1);
        addQueuedActionBack(new Act_ReadMessage(
                gp, buildPlayerSideAttributesSubMenuDialogue(entity), false, false));
        addQueuedActionBack(new Act_GenerateSubMenu(
                gp, SubMenuType.PLAYER_SIDE_ATTRIBUTES, options,
                colors, disabledOptions, optionDescriptions));
    }


    /**
     * Generates a sub-menu action for viewing a player-side entity's moves (i.e., skills), for information, and adds it
     * to the back of the queue of actions.
     *
     * @param entity player-side entity to display skills for
     */
    private void generatePlayerSideSkillsSubMenuAction(EntityBase entity) {

        ArrayList<String> options = new ArrayList<>();
        HashMap<Integer, Vector3f> colors = new HashMap<>();
        HashSet<Integer> disabledOptions = new HashSet<>();
        HashMap<Integer, String> optionDescriptions = new HashMap<>();

        for (MoveBase move : entity.getMoves()) {

            options.add(move.getName());
            colors.put(options.size() - 1, SubMenuHandler.INFO_OPTION_COLOR);
            disabledOptions.add(options.size() - 1);
            optionDescriptions.put(options.size() - 1, buildSkillSubMenuDescription(entity, move));
        }
        options.add("Back");
        colors.put(options.size() - 1, SubMenuHandler.BACK_OPTION_COLOR);
        addQueuedActionBack(new Act_ReadMessage(gp, buildSkillSubMenuDialogue(entity, 0), false, false));
        addQueuedActionBack(new Act_GenerateSubMenu(
                gp, SubMenuType.PLAYER_SIDE_SKILLS, options, colors, disabledOptions, optionDescriptions));
    }


    /**
     * Generates a sub-menu action for swapping in a reserve (inactive) party member adds it to the back of the queue of
     * actions.
     * The generated list of options will display active party members available to swap out.
     *
     * @param entity reserve (inactive) player-side entity to swap in
     */
    private void generatePlayerSideSwapInSubMenuAction(EntityBase entity) {

        ArrayList<String> options = generateActivePlayerSideOptions();
        HashMap<Integer, Vector3f> colors = new HashMap<>();
        options.add("Back");
        colors.put(options.size() - 1, SubMenuHandler.BACK_OPTION_COLOR);
        addQueuedActionBack(new Act_ReadMessage(
                gp, buildPlayerSideSwapInSubMenuDialogue(entity), false, false));
        addQueuedActionBack(new Act_GenerateSubMenu(
                gp, SubMenuType.PLAYER_SIDE_SWAP, options, colors));
    }


    /**
     * Generates a sub-menu action for swapping out an active party member adds it to the back of the queue of actions.
     * The generated list of options will display reserve (inactive) party members available to swap in.
     *
     * @param entity active player-side entity to swap out
     */
    private void generatePlayerSideSwapOutSubMenuAction(EntityBase entity) {

        ArrayList<String> options = generateInactivePlayerSideOptions();
        HashMap<Integer, Vector3f> colors = new HashMap<>();
        options.add("Back");
        colors.put(options.size() - 1, SubMenuHandler.BACK_OPTION_COLOR);
        addQueuedActionBack(new Act_ReadMessage(
                gp, buildPlayerSideSwapOutSubMenuDialogue(entity), false, false));
        addQueuedActionBack(new Act_GenerateSubMenu(
                gp, SubMenuType.PLAYER_SIDE_SWAP, options, colors));
    }


    /**
     * Generates a sub-menu action for confirming the pre-determined targets that a move will hit and adds it to the
     * back of the queue of actions.
     * The list of last generated selectable targets in combat is also refreshed.
     *
     * @param move move to use
     */
    private void generateTargetConfirmSubMenuAction(MoveBase move) {

        generatePlayerSideTargetOptions(move);
        ArrayList<String> targetOptions = new ArrayList<>();
        HashMap<Integer, Vector3f> colors = new HashMap<>();
        targetOptions.add("Confirm");
        targetOptions.add("Back");
        colors.put(targetOptions.size() - 1, SubMenuHandler.BACK_OPTION_COLOR);
        addQueuedActionBack(new Act_ReadMessage(gp, buildTargetConfirmSubMenuDialogue(move), false, false));
        addQueuedActionBack(new Act_GenerateSubMenu(gp, SubMenuType.TARGET_CONFIRM, targetOptions, colors));
    }


    /**
     * Reverts (i.e., goes back) to the selection outcome of the third-to-latest SubMenuMemory instance in 'subMenuLog'
     * (if able).
     * This method is intended to be used when the 'Back' option is selected in a combat sub-menu.
     * It effectively returns to the sub-menu that was displayed before the sub-menu where the 'Back' option
     * was selected.
     * In other words, it "undoes" a sub-menu selection.
     * However, sub-menus will only be reverted to those that displayed within the current entity's turn.
     */
    private void revertSubMenuSelection() {

        subMenuLog.remove(subMenuLog.size() - 1);                                                                       // Remove memory of the sub-menu where the 'Back' option was selected.

        if (subMenuLog.size() > 1) {                                                                                    // If there are at least two more sub-menu memories remaining.

            subMenuLog.remove(subMenuLog.size() - 1);                                                                   // Remove memory of the sub-menu that preceded the sub-menu where the 'Back' option was selected; it will be regenerated.

            if (getSubMenuMemory(1).isLastOfTurn()) {

                generateRootSubMenuAction();                                                                            // Cannot go back further due to entity turn change, so return to root sub-menu.
            } else {

                switch (getSubMenuMemory(1).getType()) {                                                                // Regenerate the sub-menu that then generated the sub-menu where the 'Back' option was selected IF within same entity turn.
                    case ROOT:
                        runRootSubMenuSelection();
                        break;
                    case GUARD:
                        runGuardSubMenuSelection();
                        break;
                    case SKILL:
                        runSkillSubMenuSelection();
                        break;
                    case TARGET_SELECT:
                        runTargetSelectSubMenuSelection();
                        break;
                    case PARTY:
                        runPartySubMenuSelection();
                        break;
                    case PLAYER_SIDE_MANAGE:
                        runPlayerSideManageSubMenuSelection();
                        break;
                    case PLAYER_SIDE_SWAP:
                        runPlayerSideSwapSubMenuSelection();
                        break;
                }
            }
        } else {

            if (subMenuLog.size() > 0) {                                                                                // If there is at least one sub-menu memory remaining.

                subMenuLog.remove(subMenuLog.size() - 1);                                                               // Remove memory of the sub-menu that preceded the sub-menu where the 'Back' option was selected; it will be regenerated.
            }
            generateRootSubMenuAction();                                                                                // Regenerate the root sub-menu since no previous sub-menu to where the 'Back' option was selected is recorded in memory.
        }
    }


    /**
     * Generates the order in which combating entities will take their turns.
     */
    private void generateTurnOrder() {

        // Create a HashMap of combating entities, where entity ID is the key and agility is the value.
        HashMap<Integer, Integer> entitiesToPlace = new HashMap<>();

        // Add all combating entities to the new list of combating entities, even if fainted or outside the first
        // two party slots.
        for (int entityId : gp.getEntityM().getCombatingEntities()) {

            entitiesToPlace.put(
                    entityId,
                    gp.getEntityM().getEntityById(entityId).getBaseAgility()
                            + (int)(gp.getEntityM().getEntityById(entityId).getBaseAgility()
                            * gp.getEntityM().getEntityById(entityId).getAgilityBuff()));
        }

        // Build the turn order based on entity agility attributes (higher agility moves sooner).
        // If two or more entities tie, then order for them will be randomly generated.
        // All combating entities are included, even those fainted or outside first two party slots.
        // If fainted, an entity's turn will be skipped.
        // If outside the first two party slots, an entity will be hidden, which will tell the combat loop to skip turn.
        // This makes it easier to swap party members in and out of combat, since their turn order is already generated.
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

//                System.out.println("Entity ID: " + entitiesHighestAgility.get(0) + " -> Agility: "
//                + gp.getEntityById(entitiesHighestAgility.get(0)).getAgility());
                queuedEntityTurnOrder.addLast(entitiesHighestAgility.get(0));
                entitiesToPlace.remove(entitiesHighestAgility.get(0));
            } else {

                // Add the entities to the turn list in a random order since they tied in agility.
                while (entitiesHighestAgility.size() > 0) {

                    Random random = new Random();
                    int i = random.nextInt(entitiesHighestAgility.size());                                              // Get a random number from 0 to the highest index in the list of entities with the highest agility.

//                    System.out.println("Entity ID: " + entitiesHighestAgility.get(i) + " -> Agility: "
//                    + gp.getEntityById(entitiesHighestAgility.get(i)).getAgility());
                    queuedEntityTurnOrder.addLast(entitiesHighestAgility.get(i));                                       // Add the randomly selected entity as the next in the turn list.
                    entitiesToPlace.remove(entitiesHighestAgility.get(i));
                    entitiesHighestAgility.remove(i);                                                                   // Remove the randomly selected entity from the list of entities with the highest agility.
                }
            }
        }
    }


    /**
     * Generates the actions that an NPC combating entity will take during its turn.
     * This method serves as the root of NPC combat AI.
     */
    private void generateNpcTurn() {

        // Get source entity.
        EntityBase sourceEntity = gp.getEntityM().getEntityById(queuedEntityTurnOrder.peekFirst());

        // Generate possible moves for source entity to use
        ArrayList<MoveBase> possibleMoves = new ArrayList<>();
        for (MoveBase move : sourceEntity.getMoves()) {
            if (move.skillPoints <= sourceEntity.getSkill()) {
                possibleMoves.add(move);
            }
        }

        // Select random move for source entity to use and target(s).
        MoveBase move = null;
        Random random = new Random();
        ArrayList<Integer> targetEntityIds = new ArrayList<>();
        boolean validMove = false;                                                                                      // Boolean to control whether a move with more than zero possible targets was selected.
        int i;
        while (!validMove && possibleMoves.size() > 0) {
            i = random.nextInt(possibleMoves.size());                                                                   // Generate random number from 0 to number of possible moves minus one (both inclusive).
            move = possibleMoves.get(i);
            generateNonPlayerSideTargetOptions(move);
            if (lastGeneratedTargetOptions.size() > 0) {
                validMove = true;
            } else {
                possibleMoves.remove(i);
            }
        }
        if (!validMove) {
            move = defaultMove;                                                                                         // No valid move was found, so default to basic attack.
            generateNonPlayerSideTargetOptions(move);
        }
        if (move.isHitAllTargets()) {                                                                                   // Move hits all possible targets.
            for (int entityId : lastGeneratedTargetOptions) {
                targetEntityIds.add(entityId);
            }
        } else {                                                                                                        // Move only hits one possible target.
            i = random.nextInt(lastGeneratedTargetOptions.size());                                                      // Generate random number from 0 to number of selectable target entities (both inclusive)
            targetEntityIds.add(lastGeneratedTargetOptions.get(i));
        }

        // Add move action.
        String message = buildUseMoveDialogue(sourceEntity.getName(), move.getName(), move.getMoveId());
        addQueuedActionBack(new Act_ReadMessage(gp, message, false, true));
        addQueuedActionBack(new Act_UseMove(gp, move, sourceEntity.getEntityId(), targetEntityIds));
        runNextQueuedAction();
    }


    /**
     * Generates a list of names of all selectable player-side entities (active, inactive, and player entity) in combat.
     * The player entity is added to the list first, then all party member entities are added in same order as the party
     * map.
     * Player-side entities refer to combating entities (including the player entity) fighting on the player's side.
     * The list of entity IDs of last generated selectable player-side entities ('lastGeneratedPlayerSideOptions')
     * is also refreshed by this method (same ordering as generated list of names).
     *
     * @return list of names of selectables player-side entities
     */
    private ArrayList<String> generatePlayerSideOptions() {

        ArrayList<String> playerSideOptions = new ArrayList<>();
        lastGeneratedPlayerSideOptions.clear();
        playerSideOptions.add(gp.getEntityM().getPlayer().getName());
        lastGeneratedPlayerSideOptions.add(gp.getEntityM().getPlayer().getEntityId());

        for (EntityBase entity : gp.getEntityM().getParty().values()) {

            playerSideOptions.add(entity.getName());
            lastGeneratedPlayerSideOptions.add(entity.getEntityId());
        }
        return playerSideOptions;
    }


    /**
     * Generates a list of names of selectable active player-side entities (including the player entity) in combat.
     * Player-side entities refer to combating entities (including the player entity) fighting on the player's side.
     * The list of entity IDs of last generated selectable active player-side entities
     * ('lastGeneratedActivePlayerSideOptions') is also refreshed by this method (same ordering as generated list of
     * names).
     *
     * @return list of names of selectable active player-side entities
     */
    private ArrayList<String> generateActivePlayerSideOptions() {

        ArrayList<String> activePlayerSideOptions = new ArrayList<>();
        lastGeneratedActivePlayerSideOptions.clear();
        int entityIndex = 0;

        for (EntityBase entity : gp.getEntityM().getParty().values()) {                                                 // Populate options with active party member entities.

            if (entityIndex < gp.getEntityM().getNumActivePartyMembers()) {

                activePlayerSideOptions.add(entity.getName());
                lastGeneratedActivePlayerSideOptions.add(entity.getEntityId());
                entityIndex++;
            } else {

                break;
            }
        }
        return activePlayerSideOptions;
    }


    /**
     * Generates a list of names of selectable inactive player-side entities in combat.
     * Player-side entities refer to combating entities (including the player entity) fighting on the player's side.
     * The list of entity IDs of last generated selectable inactive player-side entities
     * ('lastGeneratedInactivePlayerSideOptions') is also refreshed by this method (same ordering as generated list of
     * names).
     *
     * @return list of names of selectable inactive player-side entities
     */
    private ArrayList<String> generateInactivePlayerSideOptions() {

        ArrayList<String> inactivePlayerSideOptions = new ArrayList<>();
        lastGeneratedInactivePlayerSideOptions.clear();
        int entityIndex = 0;

        for (EntityBase entity : gp.getEntityM().getParty().values()) {                                                 // Populate options with inactive party member entities.

            if (entityIndex >= gp.getEntityM().getNumActivePartyMembers()) {

                inactivePlayerSideOptions.add(entity.getName());
                lastGeneratedInactivePlayerSideOptions.add(entity.getEntityId());
            }
            entityIndex++;
        }
        return inactivePlayerSideOptions;
    }


    /**
     * Generates a list of names of selectable targets in combat.
     * This method is to be used to generate selectable targets when a player-side entity is the entity whose turn it
     * is.
     * This method will not properly generate a list of selectable targets when a non-player-side entity is the entity
     * whose turn it is.
     * The "self" entity is that whose turn it is (may or may not be the player entity).
     * Non-player-side entities refers to combating entities fighting against the player's side.
     * Player-side entities refer to combating entities (including the player entity) fighting on the player's side.
     * Only non-fainted entities will be added to the list of selectable targets.
     * The list of entity IDs of last generated selectable targets ('lastGeneratedTargetOptions') is also refreshed by
     * this method (same ordering as generated list of names).
     *
     * @param move move for which targets are being generated
     * @return list of names of selectable targets
     */
    private ArrayList<String> generatePlayerSideTargetOptions(MoveBase move) {

        ArrayList<String> targetOptions = new ArrayList<>();
        lastGeneratedTargetOptions.clear();

        switch (move.getMoveTargets()) {
            case OPPONENT:
                addNonPlayerSideEntitiesToTargetOptions(targetOptions, move, false);
                break;
            case ALLY:
                addPlayerSideEntitiesToTargetOptions(targetOptions, move, false);
                break;
            case SELF:
                addSelfEntityToTargetOptions(targetOptions, move);
                break;
            case OPPONENT_ALLY:
                addPlayerSideEntitiesToTargetOptions(targetOptions, move, false);
                addNonPlayerSideEntitiesToTargetOptions(targetOptions, move, false);
                break;
            case OPPONENT_SELF:
                addSelfEntityToTargetOptions(targetOptions, move);
                addNonPlayerSideEntitiesToTargetOptions(targetOptions, move, false);
                break;
            case ALLY_SELF:
                addPlayerSideEntitiesToTargetOptions(targetOptions, move, true);
                break;
            case OPPONENT_ALLY_SELF:
                addPlayerSideEntitiesToTargetOptions(targetOptions, move, true);
                addNonPlayerSideEntitiesToTargetOptions(targetOptions, move, false);
                break;
        }
        return targetOptions;
    }


    /**
     * Generates a list of names of selectable targets in combat.
     * This method is to be used to generate selectable targets when a non-player-side entity is the entity whose turn
     * it is.
     * This method will not properly generate a lit of selectable targets when a player-side entity is the entity whose
     * turn it is.
     * The "self" entity is that whose turn it is.
     * Non-player-side entities refers to combating entities fighting against the player's side.
     * Player-side-entities refer to combating entities (including the player entity) fighting on the player's side.
     * Only non-fainted entities will be added to the list of selectable targets.
     * The list of entity IDs of last generated selectable targets ('lastGeneratedTargetOptions') is also refreshed by
     * this method (same ordering as generated list of names).
     *
     * @param move move for which targets are being generated
     * @return list of names of selectable targets
     */
    private ArrayList<String> generateNonPlayerSideTargetOptions(MoveBase move) {


        ArrayList<String> targetOptions = new ArrayList<>();
        lastGeneratedTargetOptions.clear();

        switch (move.getMoveTargets()) {
            case OPPONENT:
                addPlayerSideEntitiesToTargetOptions(targetOptions, move, false);
                break;
            case ALLY:
                addNonPlayerSideEntitiesToTargetOptions(targetOptions, move, false);
                break;
            case SELF:
                addSelfEntityToTargetOptions(targetOptions, move);
                break;
            case OPPONENT_ALLY:
                addNonPlayerSideEntitiesToTargetOptions(targetOptions, move, false);
                addPlayerSideEntitiesToTargetOptions(targetOptions, move, false);
                break;
            case OPPONENT_SELF:
                addSelfEntityToTargetOptions(targetOptions, move);
                addPlayerSideEntitiesToTargetOptions(targetOptions, move, false);
                break;
            case ALLY_SELF:
                addNonPlayerSideEntitiesToTargetOptions(targetOptions, move, true);
                break;
            case OPPONENT_ALLY_SELF:
                addNonPlayerSideEntitiesToTargetOptions(targetOptions, move, true);
                addPlayerSideEntitiesToTargetOptions(targetOptions, move, false);
                break;
        }
        return targetOptions;
    }


    /**
     * Adds non-player-side combating entity names to a list of selectable targets.
     * This method may be used for either a player-side entity's turn or a non-player-side entity's turn.
     * Non-player-side entities refers to combating entities fighting against the player's side.
     * Only valid entities per the move rules will be added.
     * The IDs of all added entities will also be added to the list of last generated selectable targets in the same
     * order.
     * Note that the list is pass-by-reference, so the original 'targetOptions' list passed is modified by this method
     * with no need to return a value.
     *
     * @param targetOptions list to add viable target options to (of String type)
     * @param move move for which targets are being generated
     * @param includeSelf whether the entity whose turn it is will be added to the list (true) or not (false)
     */
    private void addNonPlayerSideEntitiesToTargetOptions(ArrayList targetOptions, MoveBase move, boolean includeSelf) {

        ArrayList<EntityBase> tempNonPlayerSideEntities = new ArrayList<>();

        for (int entityId : nonPlayerSideEntities) {

            tempNonPlayerSideEntities.add(gp.getEntityM().getEntityById(entityId));
        }
        Collections.sort(tempNonPlayerSideEntities,
                (o1, o2) -> (int)(o1.getWorldY() - o2.getWorldY()));

        if ((targetLockEntityId != -1)
                && (tempNonPlayerSideEntities.contains(gp.getEntityM().getEntityById(targetLockEntityId)))
                && (move.verifyTarget(targetLockEntityId))
                && (gp.getEntityM().getEntityById(queuedEntityTurnOrder.peekFirst()).getEntityId()
                    != targetLockEntityId)) {                                                                           // An entity cannot lock onto itself.

            targetOptions.add(gp.getEntityM().getEntityById(targetLockEntityId).getName());
            lastGeneratedTargetOptions.add(targetLockEntityId);
        } else {

            for (EntityBase entity : tempNonPlayerSideEntities) {

                if ((includeSelf                                                                                        // Check if entity whose turn it is (i.e., "self") is selectable.
                        || (gp.getEntityM().getEntityById(queuedEntityTurnOrder.peekFirst()).getEntityId()
                        != entity.getEntityId()))                                                                       // If "self" is not selectable, ensure entity is not "self".
                        && (move.verifyTarget(entity.getEntityId()))) {
                    targetOptions.add(entity.getName());
                    lastGeneratedTargetOptions.add(entity.getEntityId());

                }
            }
        }
    }


    /**
     * Adds player-side combating entity names to a list of selectable targets.
     * This method may be used for either a player-side entity's turn or a non-player-side entity's turn.
     * Player-side entities refer to combating entities (including the player entity) fighting on the player's side.
     * Only valid entities per the move rules will be added.
     * The IDs of all added entities will also be added to the list of last generated selectable targets in the same
     * order.
     * Note that the list is pass-by-reference, so the original 'targetOptions' list passed is modified by this method
     * with no need to return a value.
     *
     * @param targetOptions list to add viable target options to (of String type)
     * @param move move for which targets are being generated
     * @param includeSelf whether the entity whose turn it is will be added to the list (true) or not (false)
     */
    private void addPlayerSideEntitiesToTargetOptions(ArrayList targetOptions, MoveBase move, boolean includeSelf) {

        ArrayList<EntityBase> tempPlayerSideEntities = new ArrayList<>();
        tempPlayerSideEntities.add(gp.getEntityM().getPlayer());
        int entityIndex = 0;

        for (int entityId : gp.getEntityM().getParty().keySet()) {

            if (entityIndex < gp.getEntityM().getNumActivePartyMembers()) {

                tempPlayerSideEntities.add(gp.getEntityM().getParty().get(entityId));
            } else {

                break;
            }
            entityIndex++;
        }
        Collections.sort(tempPlayerSideEntities, (o1, o2) -> (int)(o1.getWorldY() - o2.getWorldY()));

        if ((targetLockEntityId != -1)
                && (tempPlayerSideEntities.contains(gp.getEntityM().getEntityById(targetLockEntityId)))
                && (move.verifyTarget(targetLockEntityId))
                && (gp.getEntityM().getEntityById(queuedEntityTurnOrder.peekFirst()).getEntityId()
                    != targetLockEntityId)) {                                                                           // An entity cannot lock onto itself.

            targetOptions.add(gp.getEntityM().getEntityById(targetLockEntityId).getName());
            lastGeneratedTargetOptions.add(targetLockEntityId);
        } else {

            for (EntityBase entity : tempPlayerSideEntities) {

                if ((includeSelf                                                                                        // Check if entity whose turn it is (i.e., "self") is selectable.
                        || (gp.getEntityM().getEntityById(queuedEntityTurnOrder.peekFirst()).getEntityId()
                        != entity.getEntityId()))                                                                       // If "self" is not selectable, ensure entity is not "self".
                        && (move.verifyTarget(entity.getEntityId()))) {

                    targetOptions.add(entity.getName());
                    lastGeneratedTargetOptions.add(entity.getEntityId());
                }
            }
        }
    }


    /**
     * Adds the name of the combating entity whose turn it is to a list of selectable targets.
     * This method may be used for either a player-side entity's turn or a non-player-side entity's turn.
     * The entity added may be either the player entity or a party member entity.
     * The entity will only be added if validated per the move rules.
     * The ID of the added entity will also be added to the list of last generated selectable targets.
     * Note that the list is pass-by-reference, so the original 'targetOptions' list passed is modified by this method with no need to
     * return a value.
     *
     * @param targetOptions list to add viable target option to (of String type)
     * @param move move for which targets are being generated
     */
    private void addSelfEntityToTargetOptions(ArrayList targetOptions, MoveBase move) {

        if ((gp.getEntityM().getEntityById(queuedEntityTurnOrder.peekFirst()).getEntityId()
                    == gp.getEntityM().getPlayer().getEntityId())                                                       // Check if player entity is the self.
                && (move.verifyTarget(gp.getEntityM().getPlayer().getEntityId()))) {

            targetOptions.add(gp.getEntityM().getPlayer().getName());
            lastGeneratedTargetOptions.add(gp.getEntityM().getPlayer().getEntityId());
            return;                                                                                                     // The single target entity has been added.
        }
        int entityIndex = 0;

        for (int entityId : gp.getEntityM().getParty().keySet()) {

            if (entityIndex < gp.getEntityM().getNumActivePartyMembers()) {

                if ((gp.getEntityM().getEntityById(queuedEntityTurnOrder.peekFirst()).getEntityId() == entityId)        // Check if a party member entity is the self.
                        && (move.verifyTarget(gp.getEntityM().getEntityById(entityId).getEntityId()))) {

                    targetOptions.add(gp.getEntityM().getEntityById(entityId).getName());
                    lastGeneratedTargetOptions.add(entityId);
                    return;                                                                                             // The single target entity has been added.
                }
            } else {

                break;
            }
            entityIndex++;
        }

        for (int entityId : nonPlayerSideEntities) {

            if ((gp.getEntityM().getEntityById(queuedEntityTurnOrder.peekFirst()).getEntityId() == entityId)            // Check if a non-player-side entity is the self.
                    && (gp.getEntityM().getEntityById(entityId).getStatus() != EntityStatus.FAINT)) {

                targetOptions.add(gp.getEntityM().getEntityById(entityId).getName());
                lastGeneratedTargetOptions.add(entityId);
                return;                                                                                                 // The single target entity has been added.
            }
        }
    }


    /**
     * Sets a target entity to a combating state.
     * If the target entity is already in a combating state, it will remain as such.
     * The target entity's pre-combat world position is also stored for later use (if needed post-combat).
     *
     * @param target ID of the entity entering combat
     */
    private void setCombating(EntityBase target) {

        if (!target.isCombating()) {

            target.setCombating(true);
            storedEntityCols.put(target.getEntityId(), target.getCol());
            storedEntityRows.put(target.getEntityId(), target.getRow());
            storedEntityDirections.put(target.getEntityId(), target.getDirectionCurrent());
            storedEntityHidden.put(target.getEntityId(), target.isHidden());
        }
    }


    /**
     * Prepares player-side entities (including the player entity) for combat.
     * This includes setting entities as combating, setting their combat sprites, and storing their pre-combat world
     * positions.
     */
    private void preparePlayerSideEntities() {

        for (int entityId : gp.getEntityM().getParty().keySet()) {                                                      // Store original party member ordering.

            partyOrdering.add(entityId);
        }
        handleNonCombatingFollowers(gp.getEntityM().getPlayer());

        for (EntityBase partyEntityId : gp.getEntityM().getParty().values()) {

            handleNonCombatingFollowers(partyEntityId);
        }
        setCombating(gp.getEntityM().getPlayer());
        gp.getEntityM().getPlayer().setCol(fieldCenterCol - 3);
        gp.getEntityM().getPlayer().setDirectionCurrent(EntityDirection.RIGHT);

        int numActivePartyMembers;

        if (gp.getEntityM().getParty().size() >= gp.getEntityM().getNumActivePartyMembers()) {

            numActivePartyMembers = gp.getEntityM().getNumActivePartyMembers();
        } else {

            numActivePartyMembers = gp.getEntityM().getParty().size();
        }
        int colOffsetFromPlayerEntity;                                                                                  // Offset of position that active party member will be placed relative to player entity.
        int rowOffsetFromPlayerEntity;                                                                                  // ^^^

        if ((numActivePartyMembers % 2) == 0) {

            gp.getEntityM().getPlayer().setRow(fieldCenterRow);
            colOffsetFromPlayerEntity = 1;
            rowOffsetFromPlayerEntity = 2;
        } else {

            gp.getEntityM().getPlayer().setRow(fieldCenterRow - 1);
            colOffsetFromPlayerEntity = 0;
            rowOffsetFromPlayerEntity = 2;
        }

        int placedPartyMembers = 0;

        for (EntityBase entity : gp.getEntityM().getParty().values()) {

            if (entity != null) {

                setCombating(entity);
                entity.setDirectionCurrent(EntityDirection.RIGHT);

                if (placedPartyMembers >= gp.getEntityM().getNumActivePartyMembers()) {

                    entity.setCol(gp.getEntityM().getPlayer().getCol());                                                // For non-active party members, set default position to be player entity's position.
                    entity.setRow(gp.getEntityM().getPlayer().getRow());                                                // ^^^
                    entity.setHidden(true);                                                                             // Set inactive party members as hidden.
                } else if ((placedPartyMembers % 2) == 0) {

                    entity.setCol(gp.getEntityM().getPlayer().getCol() - colOffsetFromPlayerEntity);
                    entity.setRow(gp.getEntityM().getPlayer().getRow() + rowOffsetFromPlayerEntity);
                    entity.setHidden(false);
                } else {

                    entity.setCol(gp.getEntityM().getPlayer().getCol() - colOffsetFromPlayerEntity);
                    entity.setRow(gp.getEntityM().getPlayer().getRow() - rowOffsetFromPlayerEntity);
                    entity.setHidden(false);
                }
                placedPartyMembers++;

                if (((numActivePartyMembers % 2) == 0) && (placedPartyMembers % 2) == 0) {

                    colOffsetFromPlayerEntity += 1;                                                                     // Iterate offset from player entity to stagger active party member placement.
                    rowOffsetFromPlayerEntity += 2;                                                                     // ^^^
                } else if ((numActivePartyMembers % 2) == 1) {

                    if ((placedPartyMembers % 2) == 1) {

                        colOffsetFromPlayerEntity += 1;
                    } else {

                        rowOffsetFromPlayerEntity += 2;
                    }
                }
            }
        }
    }


    /**
     * Prepares non-player-side entities for combat.
     * This includes setting entities as combating, setting their combat sprites, and storing their pre-combat world
     * positions.
     */
    private void prepareNonPlayerSideEntities() {


        for (int nonPlayerSideEntityId : nonPlayerSideEntities) {

            handleNonCombatingFollowers(gp.getEntityM().getEntityById(nonPlayerSideEntityId));
        }
        int placedNonPlayerSideEntities = 0;
        int colOffsetFromFirstEntity = 0;                                                                               // Offset of position that the entity will be placed relative to first placed entity.
        int rowOffsetFromFirstEntity = 0;                                                                               // ^^^
        int firstEntityCol = fieldCenterCol + 3;
        int firstEntityRow;

        if ((nonPlayerSideEntities.size() % 2) == 0) {

            firstEntityRow = fieldCenterRow - 1;
        } else {

            firstEntityRow = fieldCenterRow;
        }

        for (int entityId : nonPlayerSideEntities) {

            if (gp.getEntityM().getParty().get(entityId) == null) {

                EntityBase opponent = gp.getEntityM().getEntityById(entityId);
                setCombating(opponent);
                opponent.setDirectionCurrent(EntityDirection.LEFT);

                if (placedNonPlayerSideEntities == 0) {

                    opponent.setCol(firstEntityCol);
                    opponent.setRow(firstEntityRow);
                } else if ((placedNonPlayerSideEntities % 2) == 0) {

                    opponent.setCol(firstEntityCol + colOffsetFromFirstEntity);
                    opponent.setRow(firstEntityRow - rowOffsetFromFirstEntity);
                } else {

                    opponent.setCol(firstEntityCol + colOffsetFromFirstEntity);
                    opponent.setRow(firstEntityRow + rowOffsetFromFirstEntity);
                }
                opponent.setHidden(false);
                placedNonPlayerSideEntities++;

                if (((nonPlayerSideEntities.size() % 2) == 1) && (placedNonPlayerSideEntities % 2) == 1) {

                    colOffsetFromFirstEntity += 1;                                                                      // Iterate offset from first placed entity to stagger entity placement.
                    rowOffsetFromFirstEntity += 2;                                                                      // ^^^
                } else if ((nonPlayerSideEntities.size() % 2) == 0) {

                    if ((placedNonPlayerSideEntities % 2) == 0) {

                        colOffsetFromFirstEntity += 1;
                    } else {

                        rowOffsetFromFirstEntity += 2;
                    }
                }
            } else {

                nonPlayerSideEntities.remove(entityId);                                                                 // Cleanup any null (non-existent) entities.
            }
        }
    }


    /**
     * Checks if a combating entity has any followers that are not participating in combat.
     * If any of said followers are not participating, then they are temporarily transferred to the `standby` entity map
     * during combat to avoid any combat interference (walking towards the followed entity during combat, etc.).
     *
     * @param followed combating entity to check
     */
    private void handleNonCombatingFollowers(EntityBase followed) {

        ArrayList<Integer> followers = gp.getEventM().seekFollowers(followed, true, true, false, false);
        boolean match;

        for (int followerId : followers) {                                                                              // Check each follower of the followed entity.

            match = false;

            for (int nonPlayerSideEntityId : nonPlayerSideEntities) {                                                   // Check each non-player-side combating entity against the current follower.

                if (followerId == nonPlayerSideEntityId) {

                    match = true;                                                                                       // The follower is a non-player-side combating entity.
                    break;                                                                                              // No need to check against any further non-player-side entities against the current follower.
                }
            }

            if (!match) {                                                                                                // If the follower was not found to be a non-player-side combating entity, then handle it.

                switch (gp.getEntityM().getEntityById(followerId).getType()) {
                    case OBJECT:
                        gp.getEntityM()
                                .transferEntity(gp.getEntityM().getObj(), gp.getEntityM().getStandby(), followerId);    // Store as standby entity during combat to prevent interference (walking towards combatant, etc.).
                        break;
                    case CHARACTER:
                        gp.getEntityM()
                                .transferEntity(gp.getEntityM().getNpc(), gp.getEntityM().getStandby(), followerId);    // Store as standby entity during combat to prevent interference (walking towards combatant, etc.)..
                        break;
                }
                storedNonCombatingFollowers.add(followerId);
            }
        }
    }


    /**
     * Handles skipping the turn of the entity whose turn it is.
     */
    private void handleSkipTurn() {

        String message = gp.getEntityM().getEntityById(queuedEntityTurnOrder.peekFirst()).getName()
                + " is not able to move for "
                + turnSkipEntities.get(queuedEntityTurnOrder.peekFirst())
                + " more turn"
                + (turnSkipEntities.get(queuedEntityTurnOrder.peekFirst()) == 1 ? "" : "s")
                + ".";
        addQueuedActionBack(new Act_ReadMessage(gp, message, true, true));
        turnSkipEntities.put(
                queuedEntityTurnOrder.peekFirst(), turnSkipEntities.get(queuedEntityTurnOrder.peekFirst()) - 1);

        if (turnSkipEntities.get(queuedEntityTurnOrder.peekFirst()) <= 0) {

            turnSkipEntities.remove(queuedEntityTurnOrder.peekFirst());
        }
        runNextQueuedAction();
    }


    /**
     * Checks whether an entity has just fainted; in other words, if the entity has zero life but not a
     * fainted status.
     * If it has, then both its faint animation and a message are added to the end of the queue of actions.
     *
     * @param entityId ID of entity to check
     * @return whether the entity has just fainted (true) or not (false)
     */
    private boolean actionPreFaint(int entityId) {

        EntityBase targetEntity = gp.getEntityM().getEntityById(entityId);

        if ((targetEntity.getLife() <= 0)
                && (targetEntity.getStatus() != EntityStatus.FAINT)) {

            if (UtilityTool.VERBOSE_LOGGING) {

                UtilityTool.logInfo("Staging entity "
                        + (((targetEntity.getName() != null)
                        && (!targetEntity.getName().equals("")))
                        ? "'" + (targetEntity.getName() + "' ")
                        : "")
                        + "with ID '"
                        + targetEntity.getEntityId()
                        + "' to faint in combat.");
            }
            addQueuedActionBack(new Act_FaintEntity(gp, entityId));
            String message = targetEntity.getName() + " has no energy left to fight!";
            addQueuedActionBack(new Act_ReadMessage(gp, message, true, true));
            return true;
        }
        return false;
    }


    /**
     * Builds a message for the root combat sub-menu prompt.
     *
     * @param entity entity whose turn it is
     * @return message
     */
    private String buildRootSubMenuDialogue(EntityBase entity) {

        return "What will " + (entity.getName().equals("") ? "???" : entity.getName()) + " do?";
    }


    /**
     * Builds a message for using a move.
     *
     * @param entityName attacking entity
     * @param moveName name of move being used
     * @param moveId ID of move being used
     * @return message
     */
    private String buildUseMoveDialogue(String entityName, String moveName, int moveId) {

        String buildEntityName = "???";
        String buildMoveName = "???";

        if (!entityName.equals("")) {
            buildEntityName = entityName;
        }

        if (moveId == 0) {
            return buildEntityName + " attacked!";                                                                      // Basic attack has different message.

        } else {
            if (!moveName.equals("")) {
                buildMoveName = moveName;
            }
            return buildEntityName + " used " + buildMoveName + "!";
        }
    }


    /**
     * Builds option descriptions for the root combat sub-menu.
     *
     * @return map of option descriptions
     */
    private HashMap<Integer, String> buildRootSubMenuDescriptions() {

        HashMap<Integer, String> rootSubMenuDescriptions = new HashMap<>();
        rootSubMenuDescriptions.put(0,
                "Defend to reduce damage taken.");
        rootSubMenuDescriptions.put(1,
                "Attack an enemy.");
        rootSubMenuDescriptions.put(2,
                "Use a special skill.");
        rootSubMenuDescriptions.put(3,
                "Manage party members.");
//        rootSubMenuDescriptions.put(4,
//                "Flee from combat.");
        return rootSubMenuDescriptions;
    }


    /**
     * Builds message for the guard combat sub-menu.
     *
     * @return message
     */
    private String buildGuardSubMenuDialogue() {

        return "Assume a guarding stance?";
    }


    /**
     * Builds message for the skill and player-side skills combat sub-menus.
     *
     * @param entity entity to build message for (move list is polled to see which lines up with the selected index)
     * @param selectedIndex selected index in the skill or player-side skills combat sub-menus
     * @return message
     */
    private String buildSkillSubMenuDialogue(EntityBase entity, int selectedIndex) {

        if (selectedIndex < entity.getMoves().size()) {

            return entity.getMoves().get(gp.getSubMenuH().getIndexSelected()).getDescription();
        } else {

            return "Return to previous menu.";
        }
    }


    /**
     * Builds option description for a move in the skills and player-side skills combat sub-menus.
     *
     * @param entity entity to build description for (affects remaining skill points)
     * @param move move to build description for
     * @return description
     */
    private String buildSkillSubMenuDescription(EntityBase entity, MoveBase move) {

        String categoryAbbreviation;
        switch (move.getCategory()) {
            case PHYSICAL:
                categoryAbbreviation = "PHY";
                break;
            case MAGIC:
                categoryAbbreviation = "MAG";
                break;
            case SUPPORT:
                categoryAbbreviation = "SUP";
                break;
            default:
                categoryAbbreviation = "???";
        }
        return "Power: " + (move.getCategory() == MoveCategory.SUPPORT ? "--" : move.getPower()) + " [" + categoryAbbreviation + "]\n"
                + "Accuracy: " + (move.getCategory() == MoveCategory.SUPPORT ? "--" :move.getAccuracy()) + "\n"
                + "Skill: " + move.getSkillPoints() + "/" + entity.getSkill();
    }


    /**
     * Builds message for the target select combat sub-menu.
     *
     * @param move move to be used on target
     * @return message
     */
    private String buildTargetSelectSubMenuDialogue(MoveBase move) {

        String description = "";

        if (move.getMoveId() == 0) {                                                                                    // Unique message for base attack.

            description = "Attack who?";
        } else {

            description = "Use " + move.getName() + " on who?";
        }
        return description;
    }


    /**
     * Builds message for the target confirm sub-menu.
     *
     * @param move move to be used on targets
     * @return message
     */
    private String buildTargetConfirmSubMenuDialogue(MoveBase move) {

        String description = "";

        if (move.getMoveId() == 0) {                                                                                    // Unique message for base attack.

            description = "Attack the selected targets?";
        } else {

            description = "Use " + move.getName() + " on the selected targets?";
        }
        return description;
    }


    /**
     * Builds message for the party combat sub-menu.
     *
     * @return message
     */
    private String buildPartySubMenuDialogue() {

        return "View which party member?";
    }


    /**
     * Builds option descriptions for the party combat sub-menu.
     *
     * @param entityIds IDs of entities to build option descriptions for
     * @return map of option descriptions
     */
    private HashMap<Integer, String> buildPartySubMenuDescriptions(ArrayList<Integer> entityIds) {

        HashMap<Integer, String> descriptions = new HashMap<>();
        EntityBase entity;
        int i = 0;

        for (int entityId : entityIds) {

            entity = gp.getEntityM().getEntityById(entityId);
            descriptions.put(i,
                    entity.getName() + "\n"
                            + "HP: " + entity.getLife() + "/" + entity.getMaxLife() + "\n"
                            + "SP: " + entity.getSkill() + "/" + entity.getMaxSkill());
            i++;
        }
        return descriptions;
    }


    /**
     * Builds message for the player-side manage combat sub-menu.
     *
     * @param entity selected entity to manage
     * @return message
     */
    private String buildPlayerSideManageSubMenuDialogue(EntityBase entity) {

        return "Do what with " + entity.getName() + "?";
    }


    /**
     * Builds message for the player-side attributes combat sub-menu.
     *
     * @param entity selected entity to view attributes of
     * @return message
     */
    private String buildPlayerSideAttributesSubMenuDialogue(EntityBase entity) {

        return "Viewing " + UtilityTool.appendEntityNameApostropheS(entity.getName()) + " attributes.";
    }


    /**
     * Builds an option description the player-side attributes combat sub-menu.
     *
     * @param entity selected entity to view attributes of
     * @return map of option descriptions
     */
    private HashMap<Integer, String> buildPlayerSideAttributesSubMenuDescription(EntityBase entity) {

        HashMap<Integer, String> descriptions = new HashMap<>();

        String agilityBuff = "";

        if (entity.getAgilityBuff() > 0) {

            agilityBuff = "+";
        } else if (entity.getAgilityBuff() < 0) {

            agilityBuff = "-";
        }
        descriptions.put(0, "HP: " + entity.getLife() + "/" + entity.getMaxLife() + "\n"
                + "SP: " + entity.getSkill() + "/" + entity.getMaxSkill() + "\n"
                + "Agility: " + entity.getBaseAgility() + agilityBuff);

        String attackBuff = "";

        if (entity.getAttackBuff() > 0) {

            attackBuff = "+";
        } else if (entity.getAttackBuff() < 0) {

            attackBuff = "-";
        }
        String defenseBuff = "";

        if (entity.getDefenseBuff() > 0) {

            defenseBuff = "+";
        } else if (entity.getDefenseBuff() < 0) {

            defenseBuff = "-";
        }
        String magicBuff = "";

        if (entity.getMagicBuff() > 0) {

            magicBuff = "+";
        } else if (entity.getMagicBuff() < 0) {

            magicBuff = "-";
        }
        descriptions.put(1, "Attack: " + entity.getBaseAttack() + attackBuff + "\n"
                + "Defense: " + entity.getBaseDefense() + defenseBuff + "\n"
                + "Magic: " + entity.getBaseMagic() + magicBuff);

        return descriptions;
    }


    /**
     * Builds message for the player-side swap combat sub-menu (swap in).
     *
     * @param entity reserve (inactive) player-side entity to swap in
     * @return message
     */
    private String buildPlayerSideSwapInSubMenuDialogue(EntityBase entity) {

        return "Swap " + entity.getName() + " in for who?";
    }


    /**
     * Builds message for the player-side swap combat sub-menu (swap out).
     *
     * @param entity active player-side entity to swap out
     * @return message
     */
    private String buildPlayerSideSwapOutSubMenuDialogue(EntityBase entity) {

        return "Swap " + entity.getName() + " out for who?";
    }


    /**
     * Remove all buffs from secondary attributes (attack, defense, magic, agility) for all combating entities.
     */
    private void resetAllCombatingEntitySecondaryAttributes() {

        for (int entityId : gp.getEntityM().getCombatingEntities()) {

            gp.getEntityM().getEntityById(entityId).resetSecondaryAttributes();
        }
    }


    /**
     * Retrieves the specified SubMenuMemory instance added to 'subMenuLog'.
     *
     * @param numberBack how far back the target SubMenuMemory instance is in the 'subMenuLog' history; for reference,
     *                   the latest SubMenuMemory instance (i.e., that of the latest displayed sub-menu) will have a
     *                   value of '1'
     * @return SubMenuMemory instance
     */
    private SubMenuMemory getSubMenuMemory(int numberBack) {

        if ((numberBack > 0) && (subMenuLog.size() >= numberBack)) {

            return subMenuLog.get(subMenuLog.size() - numberBack);
        } else {

            return null;
        }
    }


    /**
     * Resets CombatManager back to its default state.
     * Intended to be called to clean up after combat has finished.
     * An exception is that `combatActive` is not reset to false.
     */
    private void reset() {

        combatLost = false;
        combatId = -1;
        fieldCenterCol = 0;
        fieldCenterRow = 0;
        storedEntityCols.clear();
        storedEntityRows.clear();
        storedEntityDirections.clear();
        storedEntityHidden.clear();
        storedNonCombatingFollowers.clear();
        partyOrdering.clear();
        nonPlayerSideEntities.clear();
        retainPreCombatTrack = false;
        queuedEntityTurnOrder.clear();
        queuedActions.clear();
        subMenuLog.clear();
        lastGeneratedPlayerSideOptions.clear();
        lastGeneratedActivePlayerSideOptions.clear();
        lastGeneratedInactivePlayerSideOptions.clear();
        lastGeneratedTargetOptions.clear();
        guardingEntities.clear();
        turnSkipEntities.clear();
        resetTargetLockEntity();
        lastActionSubmenu = false;
        combatUiVisible = false;
        newTurnRootSubMenuDisplayed = false;
    }


    // GETTERS
    public boolean isCombatActive() {
        return combatActive;
    }

    public int getCombatId() {
        return combatId;
    }

    public LinkedHashSet<Integer> getNonPlayerSideEntities() {
        return nonPlayerSideEntities;
    }

    public int getTurnEntityId() {
        return queuedEntityTurnOrder.peekFirst();
    }

    public SubMenuType getLatestSubMenuType() {
        if (getSubMenuMemory(1) != null) {
            return getSubMenuMemory(1).getType();
        } else {
            return null;
        }
    }

    public String getLatestSubMenuDescriptionByIndex(int index) {
        if ((getSubMenuMemory(1) != null) && (getSubMenuMemory(1).getDescriptions().get(index) != null)) {
            return getSubMenuMemory(1).getDescriptions().get(index);
        } else {
            return "";
        }
    }

    public boolean isCombatUiVisible() {
        return combatUiVisible;
    }

    public LinkedHashSet<Integer> getGuardingEntities() {
        return guardingEntities;
    }

    public HashMap<Integer, Integer> getTurnSkipEntities() {
        return turnSkipEntities;
    }

    public int getTargetLockEntityId() {
        return targetLockEntityId;
    }

    public int getTargetLockTurns() {
        return targetLockTurns;
    }

    public ArrayList<Integer> getLastGeneratedTargetOptions() {
        return lastGeneratedTargetOptions;
    }


    // SETTERS
    public void addSubMenuMemory(SubMenuMemory subMenuMemory) {
        if (subMenuLog.size() == subMenuLog.maxCapacity()) {
            subMenuLog.remove(0);
        }
        subMenuLog.add(subMenuMemory);
    }

    public void setLatestSubMenuSelectedOption(int selectedOption) {
        getSubMenuMemory(1).setSelectedOption(selectedOption);
    }

    public void setTargetLockEntityId(int targetLockEntityId) {
        this.targetLockEntityId = targetLockEntityId;
    }

    public void setTargetLockTurns(int targetLockTurns) {
        if (targetLockTurns > 0) {
            this.targetLockTurns = targetLockTurns;
        }
    }

    public void setLastActionSubmenu(boolean lastActionSubmenu) {
        this.lastActionSubmenu = lastActionSubmenu;
    }

    public void setCombatUiVisible(boolean combatUiVisible) {
        this.combatUiVisible = combatUiVisible;
    }
}
