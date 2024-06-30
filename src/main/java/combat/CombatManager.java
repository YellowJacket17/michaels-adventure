package combat;

import asset.Sound;
import combat.enumeration.EnterCombatTransitionType;
import combat.enumeration.ExitCombatTransitionType;
import combat.enumeration.MoveTargets;
import combat.enumeration.SubMenuType;
import combat.implementation.action.*;
import combat.implementation.move.Mve_BasicAttack;
import core.GamePanel;
import entity.enumeration.EntityStatus;
import event.enumeration.TransitionType;
import entity.EntityBase;
import entity.enumeration.EntityDirection;
import org.joml.Vector3f;
import utility.LimitedArrayList;

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
     */

    // FIELDS
    private final GamePanel gp;

    /**
     * Boolean to set whether the game is in combat mode (true) or not (false).
     */
    private boolean combatActive = false;

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
     * Map to store the direction the all combating entities were facing in before combat was initiated; entity ID is
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
     * Variable to store the current enter combat transition type being performed (null if none).
     */
    private EnterCombatTransitionType activeEnterCombatTransitionType;

    /**
     * Variable to store the current exit combat transition type being performed (null if none).
     */
    private ExitCombatTransitionType activeExitCombatTransitionType;

    /**
     * List of queued turn order for entities involved in combat.
     * The entity at the front of the queue is the entity whose turn it currently is.
     */
    private final LinkedList<Integer> queuedEntityTurnOrder = new LinkedList<>();

    /**
     * List of queued actions to run in combat.
     */
    private final LinkedList<ActionBase> queuedActions = new LinkedList<>();

    /**
     * List to store root combat options (fight, inventory, etc.).
     */
    private final List<String> rootCombatOptions;

    /**
     * List to log the last ten sub-menus generated in combat.
     * The oldest sub-menu is at index 0, the latest is at the top (size of list minus one).
     */
    private final LimitedArrayList<SubMenuMemory> subMenuLog = new LimitedArrayList<>(10);

    /**
     * Store the last generated list of player-side entity (active, inactive, and player entity) options in combat.
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
     * Stores the last generated list of target options in combat.
     * The IDs of viable entities are stored in this list.
     */
    private final ArrayList<Integer> lastGeneratedTargetOptions = new ArrayList<>();

    /**
     * Set to store IDs of entities currently in a guarding state.
     * A set is used to avoid having the same entity entered twice or thrice.
     */
    private final LinkedHashSet<Integer> guardingEntities = new LinkedHashSet<>();

    /**
     * Boolean indicating whether the last action that was run was to generate a sub-menu.
     */
    private boolean lastActionSubmenu = false;

    /**
     * Boolean to set whether combat UI is rendered or not.
     */
    private boolean combatUiVisible = false;

    /**
     * Boolean to set whether the target selection arrow is rendered or not.
     */
    private boolean targetArrowVisible = false;

    /**
     * Default combat move if an entity has no assigned moves.
     */
    private final MoveBase defaultMove;

    /**
     * Color of the 'Back' option in combat sub-menus.
     */
    private final Vector3f backOptionColor = new Vector3f(255, 46, 102);

    /**
     * Color of a disabled option in combat sub-menus.
     */
    private final Vector3f disabledOptionColor = new Vector3f(112, 112, 112);


    // CONSTRUCTOR
    /**
     * Constructs a CombatManager instance.
     *
     * @param gp GamePanel instance
     */
    public CombatManager(GamePanel gp) {
        this.gp = gp;
        defaultMove = new Mve_BasicAttack(gp);
        rootCombatOptions = List.of("Guard", "Attack", "Skill", "Inventory", "Party", "Flee");                          // Immutable list.
    }


    // METHODS
    /**
     * Progresses combat after the previous action has finished.
     * This function serves as the main driver for progressing combat logic.
     * Action subclasses must call this function as the last call in their `run()` function if it is desired that they
     * immediately progress combat to the next queued action upon finishing execution.
     */
    public void progressCombat() {


        if (lastActionSubmenu) {                                                                                        // If last action was a sub-menu, handle result of user selection.

            runSubMenuSelection();
            runNextQueuedAction();
        } else if (!queuedActions.isEmpty()) {                                                                          // If there are still queued actions, run the next one.

            runNextQueuedAction();
        } else {                                                                                                        // Begin the turn of the entity at the front of the turn order queue.

            if (nonPlayerSideEntities.contains(queuedEntityTurnOrder.peekFirst())) {                                    // If entity at the front of the turn order queue is a non-player-side entity.

                generateNpcTurn();
            } else {                                                                                                    // If entity at the front of the turn order queue is a party member/player.

                String message = buildRootMenuPrompt(
                        gp.getEntityM().getEntityById(queuedEntityTurnOrder.peekFirst()).getName());
                addQueuedActionBack(new Act_ReadMessage(gp, message, false));
                generateRootSubMenuAction();
                runNextQueuedAction();
            }
        }
    }


    /**
     * Initiates combat fade-to-black transition and performs necessary loading.
     * Applicable entities will be set to a state of combating.
     * Note that any entities in a state of conversing will be removed from said state.
     *
     * @param col center column of combat field
     * @param row center row of combat field
     * @param type type of enter combat transition; see comments in the EnterCombatTransitionType enum for definitions
     *             of different types
     * @param trackName name/title of track to be played during combat (Sound.NO_TRACK to swap to no track playing,
     *                  Sound.RETAIN_TRACK to retain current track playing upon entering and exiting combat)
     * @param opponent non-player-side entity to be fought
     * @throws IllegalArgumentException if no non-null opponents are available, if a party member is passed as an
     * opponent, or if the player entity is passed as an opponent
     */
    public void initiateCombat(int col, int row, EnterCombatTransitionType type, String trackName,
                               EntityBase opponent) {

        initiateCombat(col, row, type, trackName, opponent, null, null);
    }


    /**
     * Initiates entering combat with a fade-to-black transition and performs necessary loading.
     * Applicable entities will be set to a state of combating.
     * Note that any entities in a state of conversing will be removed from said state.
     *
     * @param col center column of combat field
     * @param row center row of combat field
     * @param type type of enter combat transition; see comments in the EnterCombatTransitionType enum for definitions
     *             of different types
     * @param trackName name/title of track to be played during combat (Sound.NO_TRACK to swap to no track playing,
     *                  Sound.RETAIN_TRACK to retain current track playing upon entering and exiting combat)
     * @param opponent1 first non-player-side entity to be fought
     * @param opponent2 second non-player-side entity to be fought
     * @throws IllegalArgumentException if no non-null opponents are available, if a party member is passed as an
     * opponent, or if the player entity is passed as an opponent
     */
    public void initiateCombat(int col, int row, EnterCombatTransitionType type, String trackName,
                               EntityBase opponent1, EntityBase opponent2) {

        initiateCombat(col, row, type, trackName, opponent1, opponent2, null);
    }


    /**
     * Initiates combat with a fade-to-black transition and performs necessary loading.
     * Applicable entities will be set to a state of combating.
     * Note that any entities in a state of conversing will be removed from said state.
     *
     * @param col center column of combat field
     * @param row center row of combat field
     * @param type type of enter combat transition; see comments in the EnterCombatTransitionType enum for definitions
     *             of different types
     * @param trackName name/title of track to be played during combat (Sound.NO_TRACK to swap to no track playing,
     *                  Sound.RETAIN_TRACK to retain current track playing upon entering and exiting combat)
     * @param opponent1 first non-player-side entity to be fought
     * @param opponent2 second non-player-side entity to be fought
     * @param opponent3 third non-player-side entity to be fought
     * @throws IllegalArgumentException if no non-null opponents are available, if a party member is passed as an
     * opponent, or if the player entity is passed as an opponent
     */
    public void initiateCombat(int col, int row, EnterCombatTransitionType type, String trackName,
                               EntityBase opponent1, EntityBase opponent2, EntityBase opponent3) {

        boolean playerOpponent = false;
        boolean partyOpponent1 = false;
        boolean partyOpponent2 = false;
        boolean partyOpponent3 = false;

        if (opponent1 != null) {

            playerOpponent = (opponent1.getEntityId() == gp.getEntityM().getPlayer().getEntityId());
            partyOpponent1 = (gp.getEntityM().getParty().get(opponent1.getEntityId()) != null);
        }

        if (opponent2 != null) {

            if (!playerOpponent) {

                playerOpponent = (opponent2.getEntityId() == gp.getEntityM().getPlayer().getEntityId());
            }
            partyOpponent2 = (gp.getEntityM().getParty().get(opponent2.getEntityId()) != null);
        }

        if (opponent3 != null) {

            if (playerOpponent) {

                playerOpponent = (opponent3.getEntityId() == gp.getEntityM().getPlayer().getEntityId());
            }
            partyOpponent3 = (gp.getEntityM().getParty().get(opponent3.getEntityId()) != null);
        }

        if (!((opponent1 == null) && (opponent2 == null) && (opponent3 == null))
                && (!playerOpponent) && (!partyOpponent1) && (!partyOpponent2) && (!partyOpponent3)) {

            // Set combat as active.
            combatActive = true;

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
            activeEnterCombatTransitionType = type;                                                                     // Set the current enter combat transition type being used.

            // Set the center tile (column/row) of the combat field.
            fieldCenterCol = col;
            fieldCenterRow = row;

            // Store non-player-side entities to be called later.
            if (opponent1 != null) {
                nonPlayerSideEntities.add(opponent1.getEntityId());
            }

            if (opponent2 != null) {
                nonPlayerSideEntities.add(opponent2.getEntityId());
            }

            if (opponent3 != null) {
                nonPlayerSideEntities.add(opponent3.getEntityId());
            }
        } else {

            if ((opponent1 == null) && (opponent2 == null) && (opponent3 == null)) {

                throw new IllegalArgumentException("Attempted to initiate combat with no opponents");
            } else if (playerOpponent) {

                throw new IllegalArgumentException("Attempted to initiate combat with the player entity as an opponent");
            } else {

                throw new IllegalArgumentException("Attempted to initiate combat with a party member as an opponent");
            }
        }
    }


    /**
     * Initiates exiting combat with a fade-to-black transition and performs necessary loading.
     *
     * @param type type of exit combat transition; see comments in the ExitCombatTransitionType enum for definitions of
     *             different types
     */
    public void exitCombat(ExitCombatTransitionType type) {

        gp.getTransitionS().initiateTransition(TransitionType.EXIT_COMBAT);
        activeExitCombatTransitionType = type;                                                                          // Set the current exit combat transition type being used.
        gp.getEventM().cleanupConversation(1);                                                                          // Cleans up from the last combat message displayed and removes dialogue window from screen.
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
     */
    public void concludeEnterCombatTransition() {

        // Reset enter combat transition variable.
        activeEnterCombatTransitionType = null;

        // Build first message to display at the start of the fight.
        String build = "";
        String stagedName = "";
        int i = 0;

        for (int entityId : nonPlayerSideEntities) {

            if ((gp.getEntityM().getEntityById(entityId) != null)
                    && (!gp.getEntityM().getEntityById(entityId).getName().equals(""))) {

                stagedName = gp.getEntityM().getEntityById(entityId).getName();
            } else {

                stagedName = "???";
            }

            if (i == (nonPlayerSideEntities.size() - 1)) {

                if (i > 0) {

                    build += "and ";
                }
                build += stagedName;
            } else {

                build += stagedName;

                if (nonPlayerSideEntities.size() > 2) {

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
        addQueuedActionBack(new Act_ToggleCombatUi(gp, true));

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
        resetAllCombatingEntityStats();
        gp.getEntityM().clearCombatingEntities();
    }


    /**
     * Closes out an exit combat transition that has completed all of its phases (i.e., tidies up any variables).
     * This is to be run once an exit combat transition has fully completed.
     */
    public void concludeExitCombatTransition() {

        switch (activeExitCombatTransitionType) {
            case BASIC:
                concludeBasicExitCombatTransition();
                break;
        }
        reset();
        combatActive = false;
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

        if (getLatestSubMenuMemory() != null) {

            getLatestSubMenuMemory().setLastOfTurn(true);                                                               // Flag the last sub-menu memory as the last sub-menu to appear during the previous entity's turn.
        }
        boolean viableEntity = false;                                                                                   // A viable entity is a non-fainted one.
        boolean generateTurnOrderCalled = false;                                                                        // Boolean tracking whether the turn order has already been re-generated.

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
                    && (!gp.getEntityM().getEntityById(queuedEntityTurnOrder.peekFirst()).isHidden())) {                             // If hidden, entity is not actively participating in combat (ex. party member in reserve).

                viableEntity = true;

                if (guardingEntities.remove(queuedEntityTurnOrder.peekFirst())) {                                       // If the entity was in a guarding state, end it.

                    String message = gp.getEntityM().getEntityById(queuedEntityTurnOrder.peekFirst()).getName()
                            + " reverted from a defensive stance.";
                    addQueuedActionBack(new Act_ReadMessage(gp, message, true));
                }
            }
        }
    }


    /**
     * Polls whether any entities have just fainted; in other words, if any entities have zero
     * life but not a fainted status.
     * If one or more have and that causes either all party entities to be fainted or all opposing entities to have
     * fainted, then combat will be exited.
     */
    public void pollFainting() {

        checkJustFainted(gp.getEntityM().getPlayer().getEntityId());

        for (int entityId : gp.getEntityM().getParty().keySet()) {

            checkJustFainted(entityId);
        }

        for (int entityId : nonPlayerSideEntities) {

            checkJustFainted(entityId);
        }

        if (checkAllNonPlayerSideFainted()) {                                                                           // Combat is won if all non-player-side entities have fainted.

            String message = gp.getEntityM().getPlayer().getName() + " won the fight!";
            addQueuedActionFront(new Act_ExitCombat(gp, ExitCombatTransitionType.BASIC));
            addQueuedActionFront(new Act_ToggleCombatUi(gp, false));
            addQueuedActionFront(new Act_ReadMessage(gp, message, true));
        }

        if (checkPlayerFainted()) {                                                                                     // Combat is only lost if the player entity has fainted.

            String message = gp.getEntityM().getPlayer().getName() + " lost the fight.";
            addQueuedActionFront(new Act_ExitCombat(gp, ExitCombatTransitionType.BASIC));
            addQueuedActionFront(new Act_ToggleCombatUi(gp, false));
            addQueuedActionFront(new Act_ReadMessage(gp, message, true));
        }
    }


    /**
     * Checks whether the player entity has a fainted status or not.
     *
     * @return whether the player entity has fainted (true) or not (false)
     */
    public boolean checkPlayerFainted() {

        if (gp.getEntityM().getPlayer().getStatus() == EntityStatus.FAINT) {

            return true;
        }
        return false;
    }


    /**
     * Checks whether all party member entities (active and inactive, excluding the player entity) have a fainted status
     * or not.
     *
     * @return whether all party member entities have fainted (true) or not (false)
     */
    public boolean checkAllPartyFainted() {

        int allPartyCount = gp.getEntityM().getParty().size();
        int faintedPartyCount = 0;

        for (int entityId : gp.getEntityM().getParty().keySet()) {

            if (gp.getEntityM().getEntityById(entityId).getStatus() == EntityStatus.FAINT) {

                faintedPartyCount++;
            }
        }

        if (faintedPartyCount == allPartyCount) {

            return true;
        } else {

            return false;
        }
    }


    /**
     * Checks whether all non-player-side entities have a fainted status or not.
     *
     * @return whether all non-player-side entities have fainted (true) or not (false)
     */
    public boolean checkAllNonPlayerSideFainted() {

        int allNonPlayerSideCount = gp.getCombatM().getNonPlayerSideEntities().size();
        int faintedNonPlayerSideCount = 0;

        for (int entityId : gp.getCombatM().getNonPlayerSideEntities()) {

            if (gp.getEntityM().getEntityById(entityId).getStatus() == EntityStatus.FAINT) {

                faintedNonPlayerSideCount++;
            }
        }

        if (faintedNonPlayerSideCount == allNonPlayerSideCount) {

            return true;
        } else {

            return false;
        }
    }


    /**
     * Runs the action at the front of the queue of actions.
     * Once complete, the action is removed from the queue.
     */
    private void runNextQueuedAction() {

        if (queuedActions.peekFirst() != null) {
            queuedActions.pollFirst().run();
        } else {
            progressCombat();                                                                                           // If no actions are queued, force back to the root combat menu.
        }
    }


    /**
     * Runs logic based on the last sub-menu type that was generated and option that was selected in said sub-menu.
     */
    private void runSubMenuSelection() {

        lastActionSubmenu = false;

        switch (getLatestSubMenuMemory().getType()) {
            case ROOT:
                runRootSubMenuSelection();
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


    /**
     * Runs logic based on the last option that was selected in the root combat sub-menu.
     */
    private void runRootSubMenuSelection() {

        switch (getLatestSubMenuMemory().getSelectedOption()) {
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
                break;
            case 4:
                runRootSubMenuSelectionParty();
                break;
            case 5:
                runRootSubMenuSelectionFlee();
                break;
        }
    }


    /**
     * Runs logic pertaining to the selection of the 'Guard' option in the root combat sub-menu.
     */
    private void runRootSubMenuSelectionGuard() {

        guardingEntities.add(queuedEntityTurnOrder.peekFirst());
        String message = gp.getEntityM()
                .getEntityById(queuedEntityTurnOrder.peekFirst()).getName() + " assumed a defensive stance.";
        addQueuedActionBack(new Act_ReadMessage(gp, message, true));
        addQueuedActionBack(new Act_EndEntityTurn(gp));
    }


    /**
     * Runs logic pertaining to the selection of the 'Attack' option in the root combat sub-menu.
     */
    private void runRootSubMenuSelectionAttack() {

        generateTargetSelectSubMenuAction();
    }


    /**
     * Runs logic pertaining to the selection of the 'Skill' option in the root combat sub-menu.
     */
    private void runRootSubMenuSelectionSkill() {

        ArrayList<String> moveOptions = new ArrayList<>();
        HashMap<Integer, Vector3f> colors = new HashMap<>();
        HashSet<Integer> disabledOptions = new HashSet<>();

        for (MoveBase move : gp.getEntityM().getEntityById(queuedEntityTurnOrder.peekFirst()).getMoves()) {

            moveOptions.add(move.getName());

            if (move.getSkillPoints() > gp.getEntityM()
                    .getEntityById(queuedEntityTurnOrder.peekFirst()).getSkillPoints()) {

                colors.put(moveOptions.size() - 1, disabledOptionColor);
                disabledOptions.add(moveOptions.size() - 1);
            }
        }
        moveOptions.add("Back");
        colors.put(moveOptions.size() - 1, backOptionColor);
        addQueuedActionBack(new Act_GenerateSubMenu(gp, SubMenuType.SKILL, moveOptions, colors, disabledOptions));
    }


    /**
     * Runs logic pertaining to the selection of the 'Party' option in the root combat sub-menu.
     */
    private void runRootSubMenuSelectionParty() {

        // TODO : Consider disabling fainted party members.
        ArrayList<String> playerSideOptions = generatePlayerSideOptions();
        HashMap<Integer, Vector3f> colors = new HashMap<>();
        playerSideOptions.add("Back");
        colors.put(playerSideOptions.size() - 1, backOptionColor);
        addQueuedActionBack(new Act_GenerateSubMenu(gp, SubMenuType.PARTY, playerSideOptions, colors));
    }


    /**
     * Runs logic pertaining to the selection of the 'Flee' option in the root combat sub-menu.
     */
    private void runRootSubMenuSelectionFlee() {

        String message = "Fleeing the fight!";
        addQueuedActionBack(new Act_ReadMessage(gp, message, true));
        addQueuedActionBack(new Act_ToggleCombatUi(gp, false));
        addQueuedActionBack(new Act_ExitCombat(gp, ExitCombatTransitionType.BASIC));
    }


    /**
     * Runs logic based on the last option that was selected in the skill combat sub-menu.
     */
    private void runSkillSubMenuSelection() {

        if (getLatestSubMenuMemory().getSelectedOption() == (getLatestSubMenuMemory().getOptions().size() - 1)) {       // Determine whether the 'Back' option was selected or not.

            revertSubMenuSelection();
        } else {

            generateTargetSelectSubMenuAction();
        }
    }


    /**
     * Runs logic based on the last option that was selected in the party combat sub-menu.
     */
    private void runPartySubMenuSelection() {

        if (getLatestSubMenuMemory().getSelectedOption() == (getLatestSubMenuMemory().getOptions().size() - 1)) {       // Determine whether the 'Back' option was selected or not.

            revertSubMenuSelection();
        } else {

            ArrayList<String> playerSideOptions = new ArrayList<>();
            EntityBase entity = gp.getEntityM()
                    .getEntityById(lastGeneratedPlayerSideOptions.get(getLatestSubMenuMemory().getSelectedOption()));

            if (getLatestSubMenuMemory().getSelectedOption() <= gp.getEntityM().getNumActivePartyMembers()) {           // Determine if the selected entity is the player entity or an active party member.

                if (entity.getEntityId() != gp.getEntityM().getPlayer().getEntityId()) {

                    // TODO : Consider disabling option is entity is fainted.
                    playerSideOptions.add("Swap Out");
                }
            } else {

                // TODO : Consider disabling option is entity is fainted.
                playerSideOptions.add("Swap In");
            }
            playerSideOptions.add("Back");
            HashMap<Integer, Vector3f> colors = new HashMap<>();
            colors.put(playerSideOptions.size() - 1, backOptionColor);
            addQueuedActionBack(new Act_GenerateSubMenu(gp, SubMenuType.PLAYER_SIDE_MANAGE, playerSideOptions, colors));
        }
    }


    /**
     * Runs logic based on the last option that was selected in the player-side manage combat sub-menu.
     */
    private void runPlayerSideManageSubMenuSelection() {

        if (getLatestSubMenuMemory().getSelectedOption() == (getLatestSubMenuMemory().getOptions().size() - 1)) {       // Determine whether the 'Back' option was selected or not.

            revertSubMenuSelection();
        } else {

            String selectedSwapOption = getLatestSubMenuOptionSelection();

            if (selectedSwapOption.equals("Swap In")) {

                ArrayList<String> playerSideOptions = generateActivePlayerSideOptions();
                playerSideOptions.add("Back");
                HashMap<Integer, Vector3f> colors = new HashMap<>();
                colors.put(playerSideOptions.size() - 1, backOptionColor);
                addQueuedActionBack(new Act_GenerateSubMenu(gp, SubMenuType.PLAYER_SIDE_SWAP, playerSideOptions, colors));
            } else if (selectedSwapOption.equals("Swap Out")) {

                ArrayList<String> playerSideOptions = generatedInactivePlayerSideOptions();
                playerSideOptions.add("Back");
                HashMap<Integer, Vector3f> colors = new HashMap<>();
                colors.put(playerSideOptions.size() - 1, backOptionColor);
                addQueuedActionBack(new Act_GenerateSubMenu(gp, SubMenuType.PLAYER_SIDE_SWAP, playerSideOptions, colors));
            }
        }
    }


    /**
     * Runs logic based on the last option that was selected in the player-side swap combat sub-menu.
     */
    private void runPlayerSideSwapSubMenuSelection() {

        if (getLatestSubMenuMemory().getSelectedOption() == (getLatestSubMenuMemory().getOptions().size() - 1)) {       // Determine whether the 'Back' option was selected or not.

            revertSubMenuSelection();
        } else {

            int playerSideId1 = lastGeneratedPlayerSideOptions
                    .get(subMenuLog.get(subMenuLog.size() - 3).getSelectedOption());
            int playerSideId2 = -1;                                                                                     // Default entity ID to force error if invalid option somehow selected.
            String selectedSwapOption = subMenuLog.get(subMenuLog.size() - 2).getOptions().get(
                    subMenuLog.get(subMenuLog.size() - 2).getSelectedOption());

            if (selectedSwapOption.equals("Swap In")) {

                playerSideId2 = lastGeneratedActivePlayerSideOptions.get(getLatestSubMenuMemory().getSelectedOption());
            } else if (selectedSwapOption.equals("Swap Out")) {

                playerSideId2 = lastGeneratedInactivePlayerSideOptions
                        .get(getLatestSubMenuMemory().getSelectedOption());
            }
            addQueuedActionBack(new Act_SwapPlayerSideEntity(gp, playerSideId1, playerSideId2));
            addQueuedActionBack(new Act_EndEntityTurn(gp));
        }
    }


    /**
     * Runs logic based on the last option that was selected in the target select combat sub-menu.
     */
    private void runTargetSelectSubMenuSelection() {

        targetArrowVisible = false;                                                                                     // Target selection has been completed.

        if (getLatestSubMenuMemory().getSelectedOption() == (getLatestSubMenuMemory().getOptions().size() - 1)){        // Determine whether the 'Back' option was selected or not.

            revertSubMenuSelection();
        } else {                                                                                                        // Determine appropriate target entity that was selected.

            EntityBase sourceEntity = gp.getEntityM().getEntityById(queuedEntityTurnOrder.peekFirst());
            MoveBase move = null;

            switch (subMenuLog.get(subMenuLog.size() - 2).getType()) {
                case ROOT:                                                                                              // A basic attack must have been selected (i.e., 'Attack' option in root combat sub-menu).
                    move = defaultMove;
                    break;
                case SKILL:                                                                                             // A skill move must have been selected (i.e., 'Skill' option in root combat sub-menu).
                    move = sourceEntity.getMoves().get(subMenuLog.get(subMenuLog.size() - 2).getSelectedOption());
                    break;
            }
            EntityBase targetEntity = gp.getEntityM().getEntityById(
                    lastGeneratedTargetOptions.get(
                            getLatestSubMenuMemory().getSelectedOption()));
            String message;
            message = buildUseMoveMessage(sourceEntity.getName(), move.getName());
            addQueuedActionBack(new Act_ReadMessage(gp, message, true));
            addQueuedActionBack(new Act_UseMove(gp, move, sourceEntity.getEntityId(), targetEntity.getEntityId()));
            addQueuedActionBack(new Act_EndEntityTurn(gp));
        }
    }


    /**
     * Reverts (i.e., goes back) to the selection outcome of the third-to-latest SubMenuMemory instance in `subMenuLog`
     * (if able).
     * This method is intended to be used when the 'Back' option is selected in a combat sub-menu.
     * It effectively returns to the sub-menu that was displayed before the sub-menu where the 'Back' option
     * was selected.
     * In other words, it "undoes" a sub-menu selection.
     * However, sub-menus will only be reverted to those that displayed within the current entity's turn.
     */
    private void revertSubMenuSelection() {

        subMenuLog.remove(subMenuLog.size() - 1);                                                                       // Remove memory of the sub-menu where the 'Back' option was selected.

        if (subMenuLog.size() > 1) {

            subMenuLog.remove(subMenuLog.size() - 1);                                                                   // Remove memory of the sub-menu that preceded the sub-menu where the 'Back' option was selected.

            if (getLatestSubMenuMemory().isLastOfTurn()) {

                generateRootSubMenuAction();                                                                            // Cannot go back further due to entity turn change, so return to root sub-menu.
            } else {

                switch (getLatestSubMenuMemory().getType()) {                                                           // Return to the outcome of the sub-menu that generated the previous sub-menu IF within same entity turn.
                    case ROOT:
                        runRootSubMenuSelection();
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

            if (subMenuLog.size() > 0) {

                subMenuLog.remove(subMenuLog.size() - 1);                                                               // Remove memory of the sub-menu that preceded the sub-menu where the 'Back' option was selected.
            }
            generateRootSubMenuAction();                                                                                // Return to the root sub-menu since no previous sub-menu to where the 'Back' option was selected is recorded in memory.
        }
    }


    /**
     * Performs any loading needed to be done for the basic enter combat transition type.
     */
    private void handleBasicEnterCombatTransitionLoading() {

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
    }


    /**
     * Performs any loading needed to be done for the basic exit combat transition type.
     */
    private void handleBasicExitCombatTransitionLoading() {

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
        gp.getWarpS().warpFollowersToFollowed(gp.getEntityM().getPlayer(), gp.getEntityM().getParty());

        // Warp non-party member followers to the player entity.
        // Note that followers of all other combating entities are NOT warped to their followers and will be left at
        // their pre-combat positions.
        gp.getWarpS().warpFollowersToFollowed(gp.getEntityM().getPlayer(), gp.getEntityM().getNpc());
        gp.getWarpS().warpFollowersToFollowed(gp.getEntityM().getPlayer(), gp.getEntityM().getObj());

        // Restore pre-combat ordering of party members (run before restoring pre-combat party hidden states).
        for (int i = 0; i < partyOrdering.size(); i++) {
            gp.getPartyS().swapEntityInParty(partyOrdering.get(i),
                    (int)gp.getEntityM().getParty().keySet().toArray()[i]);
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
                    gp.getMapM().getLoadedMap().getTracks().get(gp.getMapM().getLoadedMap().getMapState()), true);
        }
    }


    /**
     * Closes out a basic exit combat transition that has completed all of its phases (i.e., tidies up any variables).
     * This is to be run once a basic exit combat transition has fully completed.
     */
    private void concludeBasicExitCombatTransition() {

        // Nothing here.
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
        gp.getEntityM().getPlayer().setCol(fieldCenterCol - 4);
        gp.getEntityM().getPlayer().setRow(fieldCenterRow);
        gp.getEntityM().getPlayer().setDirectionCurrent(EntityDirection.RIGHT);

        int placedPartyMembers = 0;

        for (EntityBase entity : gp.getEntityM().getParty().values()) {

            if (entity != null) {

                setCombating(entity);
                entity.setDirectionCurrent(EntityDirection.RIGHT);
                entity.setCol(fieldCenterCol - 5);

                if (placedPartyMembers == 0) {

                    entity.setRow(fieldCenterRow - 2);
                    entity.setHidden(false);
                } else if (placedPartyMembers == 1) {

                    entity.setRow(fieldCenterRow + 2);
                    entity.setHidden(false);
                } else {

                    entity.setHidden(true);                                                                             // Set inactive party members as hidden.
                }
                placedPartyMembers++;
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

        for (int entityId : nonPlayerSideEntities) {

            if (gp.getEntityM().getParty().get(entityId) == null) {

                EntityBase opponent = gp.getEntityM().getEntityById(entityId);
                setCombating(opponent);
                opponent.setDirectionCurrent(EntityDirection.LEFT);

                if (placedNonPlayerSideEntities == 0) {

                    opponent.setCol(fieldCenterCol + 4);
                    opponent.setRow(fieldCenterRow);
                } else if (placedNonPlayerSideEntities == 1) {

                    opponent.setCol(fieldCenterCol + 5);
                    opponent.setRow(fieldCenterRow - 2);
                } else {

                    opponent.setCol(fieldCenterCol + 5);
                    opponent.setRow(fieldCenterRow + 2);
                }
                opponent.setHidden(false);
                placedNonPlayerSideEntities++;
            } else {

                nonPlayerSideEntities.remove(entityId);
            }
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

            entitiesToPlace.put(entityId, gp.getEntityM().getEntityById(entityId).getAgility());
        }

        // Build the turn order based on entity agility attributes (higher agility moves sooner).
        // If two or more entities tie, then order for them will be randomly generated.
        // All combating entities are included, even those fainted or outside first two party slots.
        // If fainted, an entity's turn will be skipped.
        // If outside the first two party slots, an entity will be hidden, which will tell the combat loop to skip
        // its turn.
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

//                System.out.println("Entity ID: " + entitiesHighestAgility.get(0) + " -> Agility: " + gp.getEntityById(entitiesHighestAgility.get(0)).getAgility());
                queuedEntityTurnOrder.addLast(entitiesHighestAgility.get(0));
                entitiesToPlace.remove(entitiesHighestAgility.get(0));
            } else {

                // Add the entities to the turn list in a random order since they tied in agility.
                while (entitiesHighestAgility.size() > 0) {

                    Random random = new Random();
                    int i = random.nextInt(entitiesHighestAgility.size());                                              // Get a random number from 0 to the highest index in the list of entities with the highest agility.

//                    System.out.println("Entity ID: " + entitiesHighestAgility.get(i) + " -> Agility: " + gp.getEntityById(entitiesHighestAgility.get(i)).getAgility());
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
        possibleMoves.add(defaultMove);
        for (MoveBase move : sourceEntity.getMoves()) {
            if (move.skillPoints <= sourceEntity.getSkillPoints()) {
                possibleMoves.add(move);
            }
        }

        // Select random move for source entity to use.
        MoveBase move;
        Random random = new Random();
        int i = random.nextInt(possibleMoves.size());                                                                   // Generate random number from 0 to number of possible moves minus one (both inclusive).
        move = possibleMoves.get(i);

        // Generate random target entity.
        generateNonPlayerSideTargetOptions(move.getMoveTargets());
        i = random.nextInt(lastGeneratedTargetOptions.size());                                                          // Generate random number from 0 to number of selectable target entities (both inclusive)
        EntityBase targetEntity = gp.getEntityM().getEntityById(lastGeneratedTargetOptions.get(i));

        // Add move action.
        String message = buildUseMoveMessage(sourceEntity.getName(), move.getName());
        addQueuedActionBack(new Act_ReadMessage(gp, message, true));
        addQueuedActionBack(new Act_UseMove(gp, move, sourceEntity.getEntityId(), targetEntity.getEntityId()));
        addQueuedActionBack(new Act_EndEntityTurn(gp));
        runNextQueuedAction();
    }


    /**
     * Generates a list of names of all selectable player-side entities (active, inactive, and player entity) in combat.
     * The player entity is added to the list first, then all party member entities are added in same order as the party
     * map.
     * Player-side entities refer to combating entities (including the player entity) fighting on the player's side.
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
     * ('lastGeneratedActivePlayerSideOptions') is also refreshed by this method (same ordering as generated list of
     * names).
     *
     * @return list of names of selectable inactive player-side entities
     */
    private ArrayList<String> generatedInactivePlayerSideOptions() {

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
     * Generates a sub-menu action for the root sub-menu and adds it to the back of the queue of actions.
     */
    private void generateRootSubMenuAction() {

        HashSet<Integer> disabledOptions = new HashSet<>();
        disabledOptions.add(3);                                                                                         // Disable the 'Inventory' option since it's not been developed yet.
        HashMap<Integer, Vector3f> colors = new HashMap<>();
        colors.put(3, disabledOptionColor);
        addQueuedActionBack(new Act_GenerateSubMenu(gp, SubMenuType.ROOT, rootCombatOptions, colors, disabledOptions));
    }


    /**
     * Generates a sub-menu action for selecting targets and adds it to the back of the queue of actions.
     * The list of last generated selectable targets in combat is also refreshed.
     */
    private void generateTargetSelectSubMenuAction() {

        targetArrowVisible = true;                                                                                      // Target selection has been initiated.
        EntityBase sourceEntity = gp.getEntityM().getEntityById(queuedEntityTurnOrder.peekFirst());
        MoveTargets moveTargets = null;

        switch (getLatestSubMenuMemory().getType()) {
            case ROOT:                                                                                                  // A basic attack must have been selected (i.e., 'Attack' option in root combat sub-menu).
                moveTargets = defaultMove.getMoveTargets();
                break;
            case SKILL:                                                                                                 // A skill move must have been selected (i.e., 'Skill' option in root combat sub-menu).
                moveTargets = sourceEntity.getMoves().get(getLatestSubMenuMemory().getSelectedOption()).getMoveTargets();
                break;
        }
        ArrayList<String> targetOptions = generatePlayerSideTargetOptions(moveTargets);
        targetOptions.add("Back");
        HashMap<Integer, Vector3f> colors = new HashMap<>();
        colors.put(targetOptions.size() - 1, backOptionColor);
        addQueuedActionBack(new Act_GenerateSubMenu(gp, SubMenuType.TARGET_SELECT, targetOptions, colors));
    }


    /**
     * Generates a list of names of selectable targets in combat.
     * This method is to be used to generate selectable targets when a player-side entity is the entity whose turn it
     * is.
     * This method will not properly generate a list of selectable targets when a non-player-side entity is the entity whose
     * turn it is.
     * The "self" entity is that whose turn it currently is (may or may not be the player entity).
     * Non-player-side entities refers to combating entities fighting against the player's side.
     * Player-side entities refer to combating entities (including the player entity) fighting on the player's side.
     * Only non-fainted entities will be added to the list of selectable targets.
     * The list of entity IDs of last generated selectable targets ('lastGeneratedTargetOptions') is also refreshed by
     * this method (same ordering as generated list of names).
     *
     * @param moveTargets possible targets
     * @return list of names of selectable targets
     */
    private ArrayList<String> generatePlayerSideTargetOptions(MoveTargets moveTargets) {

        ArrayList<String> targetOptions = new ArrayList<>();
        lastGeneratedTargetOptions.clear();

        switch (moveTargets) {
            case OPPONENT:
                addNonPlayerSideEntitiesToTargetOptions(targetOptions);
                break;
            case ALLY:
                addPlayerSideEntitiesToTargetOptions(targetOptions);
                break;
            case SELF:
                addSelfEntityToTargetOptions(targetOptions);
                break;
            case OPPONENT_ALLY:
                addNonPlayerSideEntitiesToTargetOptions(targetOptions);
                addPlayerSideEntitiesToTargetOptions(targetOptions);
                break;
            case OPPONENT_SELF:
                addNonPlayerSideEntitiesToTargetOptions(targetOptions);
                addSelfEntityToTargetOptions(targetOptions);
                break;
            case ALLY_SELF:
                addPlayerSideEntitiesToTargetOptions(targetOptions);
                addSelfEntityToTargetOptions(targetOptions);
                break;
            case OPPONENT_ALLY_SELF:
                addNonPlayerSideEntitiesToTargetOptions(targetOptions);
                addPlayerSideEntitiesToTargetOptions(targetOptions);
                addSelfEntityToTargetOptions(targetOptions);
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
     * The "self" entity is that whose turn it currently is (may or may not be the player entity).
     * Non-player-side entities refers to combating entities fighting against the player's side.
     * Player-side-entities refer to combating entities (including the player entity) fighting on the player's side.
     * Only non-fainted entities will be added to the list of selectable targets.
     * The list of entity IDs of last generated selectable targets ('lastGeneratedTargetOptions') is also refreshed by
     * this method (same ordering as generated list of names).
     *
     * @param moveTargets possible targets
     * @return list of names of selectable targets
     */
    private ArrayList<String> generateNonPlayerSideTargetOptions(MoveTargets moveTargets) {


        ArrayList<String> targetOptions = new ArrayList<>();
        lastGeneratedTargetOptions.clear();

        switch (moveTargets) {
            case OPPONENT:
                addPlayerSideEntitiesToTargetOptions(targetOptions);
                break;
            case ALLY:
                addNonPlayerSideEntitiesToTargetOptions(targetOptions);
                break;
            case SELF:
                addSelfEntityToTargetOptions(targetOptions);
                break;
            case OPPONENT_ALLY:
                addPlayerSideEntitiesToTargetOptions(targetOptions);
                addNonPlayerSideEntitiesToTargetOptions(targetOptions);
                break;
            case OPPONENT_SELF:
                addPlayerSideEntitiesToTargetOptions(targetOptions);
                addSelfEntityToTargetOptions(targetOptions);
                break;
            case ALLY_SELF:
                addNonPlayerSideEntitiesToTargetOptions(targetOptions);
                addSelfEntityToTargetOptions(targetOptions);
                break;
            case OPPONENT_ALLY_SELF:
                addPlayerSideEntitiesToTargetOptions(targetOptions);
                addNonPlayerSideEntitiesToTargetOptions(targetOptions);
                addSelfEntityToTargetOptions(targetOptions);
                break;
        }
        return targetOptions;
    }


    /**
     * Adds non-player-side combating entity names to a list of selectable targets.
     * This method may be used for either a player-side entity's turn or a non-player-side entity's turn.
     * Non-player-side entities refers to combating entities fighting against the player's side.
     * Only non-fainted entities will be added.
     * The IDs of all added entities will also be added to the list of last generated selectable targets in the same
     * order.
     * Note that the list is pass-by-reference, so the original list passed is modified by this method with no need to
     * return a value.
     *
     * @param targetOptions list to add viable target options to (of String or Integer type)
     */
    private void addNonPlayerSideEntitiesToTargetOptions(ArrayList targetOptions) {

        for (int entityId : nonPlayerSideEntities) {

            if ((gp.getEntityM().getEntityById(queuedEntityTurnOrder.peekFirst()).getEntityId() != entityId)
                    && (gp.getEntityM().getEntityById(entityId).getStatus() != EntityStatus.FAINT)) {

                targetOptions.add(gp.getEntityM().getEntityById(entityId).getName());
                lastGeneratedTargetOptions.add(entityId);
            }
        }
    }


    /**
     * Adds player-side combating entity names to a list of selectable targets.
     * This method may be used for either a player-side entity's turn or a non-player-side entity's turn.
     * Player-side entities refer to combating entities (including the player entity) fighting on the player's side.
     * Only non-fainted entities will be added.
     * Additionally, if a player-side entity's turn is currently active, then said entity will not be added to the list.
     * The IDs of all added entities will also be added to the list of last generated selectable targets in the same
     * order.
     * Note that the list is pass-by-reference, so the original list passed is modified by this method with no need to
     * return a value.
     *
     * @param targetOptions list to add viable target options to (of String or Integer type)
     */
    private void addPlayerSideEntitiesToTargetOptions(ArrayList targetOptions) {

        if ((gp.getEntityM().getEntityById(queuedEntityTurnOrder.peekFirst()).getEntityId()
                    != gp.getEntityM().getPlayer().getEntityId())
                && (gp.getEntityM().getPlayer().getStatus() != EntityStatus.FAINT)) {

            targetOptions.add(gp.getEntityM().getPlayer().getName());
            lastGeneratedTargetOptions.add(gp.getEntityM().getPlayer().getEntityId());
        }
        int entityIndex = 0;

        for (int entityId : gp.getEntityM().getParty().keySet()) {

            if (entityIndex < gp.getEntityM().getNumActivePartyMembers()) {

                if ((gp.getEntityM().getEntityById(queuedEntityTurnOrder.peekFirst()).getEntityId() != entityId)
                        && (gp.getEntityM().getEntityById(entityId).getStatus() != EntityStatus.FAINT)) {

                    targetOptions.add(gp.getEntityM().getEntityById(entityId).getName());
                    lastGeneratedTargetOptions.add(entityId);
                }
            } else {

                break;
            }
            entityIndex++;
        }
    }


    /**
     * Adds the name of the combating entity whose turn it is to a list of selectable targets.
     * This method may be used for either a player-side entity's turn or a non-player-side entity's turn.
     * The entity added may be either the player entity or a party member entity.
     * Only non-fainted entities will be added.
     * The ID of the added entity will also be added to the list of last generated selectable targets.
     * Note that the list is pass-by-reference, so the original list passed is modified by this method with no need to
     * return a value.
     *
     * @param targetOptions list to add viable target option to (of String or Integer type)
     */
    private void addSelfEntityToTargetOptions(ArrayList targetOptions) {

        if ((gp.getEntityM().getEntityById(queuedEntityTurnOrder.peekFirst()).getEntityId()
                    == gp.getEntityM().getPlayer().getEntityId())                                                       // Check if player entity is the self.
                && (gp.getEntityM().getPlayer().getStatus() != EntityStatus.FAINT)) {

            targetOptions.add(gp.getEntityM().getPlayer().getName());
            lastGeneratedTargetOptions.add(gp.getEntityM().getPlayer().getEntityId());
            return;                                                                                                     // The single target entity has been added.
        }
        int entityIndex = 0;

        for (int entityId : gp.getEntityM().getParty().keySet()) {

            if (entityIndex < gp.getEntityM().getNumActivePartyMembers()) {

                if ((gp.getEntityM().getEntityById(queuedEntityTurnOrder.peekFirst()).getEntityId() == entityId)        // Check if a party member entity is the self.
                        && (gp.getEntityM().getEntityById(entityId).getStatus() != EntityStatus.FAINT)) {

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
     * Checks whether an entity has just fainted; in other words, if the entity has zero life but not a
     * fainted status.
     * If it has, its status is changed and a message is queued to display.
     *
     * @param entityId ID of entity to check
     */
    private void checkJustFainted(int entityId) {

        EntityBase targetEntity = gp.getEntityM().getEntityById(entityId);

        if ((targetEntity.getLife() <= 0)
                && (targetEntity.getStatus() != EntityStatus.FAINT)) {

            targetEntity.setStatus(EntityStatus.FAINT);
            String stagedName = "";

            if (targetEntity.getName().equals("")) {

                stagedName = "???";
            } else {

                stagedName = targetEntity.getName();
            }
            String message = stagedName + " has no energy left to fight!";
            addQueuedActionFront(new Act_ReadMessage(gp, message, true));
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

        if (!target.isCombating()) {

            target.setCombating(true);
            storedEntityCols.put(target.getEntityId(), target.getCol());
            storedEntityRows.put(target.getEntityId(), target.getRow());
            storedEntityDirections.put(target.getEntityId(), target.getDirectionCurrent());
            storedEntityHidden.put(target.getEntityId(), target.isHidden());
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
     * Builds a string for the use move message according to the passed entity name and move name.
     *
     * @param entityName attacking entity
     * @param moveName move being used
     * @return built string
     */
    private String buildUseMoveMessage(String entityName, String moveName) {

        String buildEntityName = "???";
        String buildMoveName = "???";

        if ((entityName != null) && (!entityName.equals(""))) {
            buildEntityName = entityName;
        }

        if ((moveName != null) && (!moveName.equals(""))) {
            buildMoveName = moveName;
        }

        return buildEntityName + " used " + buildMoveName + "!";
    }


    /**
     * Remove all buffs from stats (attack, defense, magic, agility) for all combating entities.
     */
    private void resetAllCombatingEntityStats() {

        for (int entityId : gp.getEntityM().getCombatingEntities()) {

            gp.getEntityM().getEntityById(entityId).resetStats();
        }
    }


    /**
     * Retrieves the latest SubMenuMemory instance added to `subMenuLog`.
     *
     * @return latest SubMenuMemory instance
     */
    private SubMenuMemory getLatestSubMenuMemory() {

        if (subMenuLog.size() > 0) {

            return subMenuLog.get(subMenuLog.size() - 1);
        } else {

            return null;
        }
    }


    /**
     * Return the value (not the index) of the latest sub-menu selection.
     *
     * @return last selected option
     */
    private String getLatestSubMenuOptionSelection() {

        return getLatestSubMenuMemory().getOptions().get(getLatestSubMenuMemory().getSelectedOption());
    }


    /**
     * Resets CombatManager back to its default state.
     * Intended to be called to clean up after combat has finished.
     */
    private void reset() {

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
        activeEnterCombatTransitionType = null;
        activeExitCombatTransitionType = null;
        queuedEntityTurnOrder.clear();
        queuedActions.clear();
        subMenuLog.clear();
        lastGeneratedPlayerSideOptions.clear();
        lastGeneratedActivePlayerSideOptions.clear();
        lastGeneratedInactivePlayerSideOptions.clear();
        lastGeneratedTargetOptions.clear();
        guardingEntities.clear();
        lastActionSubmenu = false;
        combatUiVisible = false;
    }


    // GETTERS
    public boolean isCombatActive() {
        return combatActive;
    }

    public LinkedHashSet<Integer> getNonPlayerSideEntities() {
        return nonPlayerSideEntities;
    }

    public boolean isCombatUiVisible() {
        return combatUiVisible;
    }

    public boolean isTargetArrowVisible() {
        return targetArrowVisible;
    }

    public LinkedHashSet<Integer> getGuardingEntities() {
        return guardingEntities;
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
        getLatestSubMenuMemory().setSelectedOption(selectedOption);
    }

    public void setLastActionSubmenu(boolean lastActionSubmenu) {
        this.lastActionSubmenu = lastActionSubmenu;
    }

    public void setCombatUiVisible(boolean combatUiVisible) {
        this.combatUiVisible = combatUiVisible;
    }
}
