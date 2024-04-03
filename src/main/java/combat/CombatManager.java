package combat;

import combat.implementation.action.*;
import combat.implementation.move.Mve_BasicAttack;
import core.GamePanel;
import entity.EntityStatus;
import miscellaneous.GameState;
import miscellaneous.TransitionType;
import entity.EntityBase;
import entity.EntityDirection;
import org.joml.Vector3f;
import utility.LimitedArrayList;
import utility.UtilityTool;

import java.util.*;

/**
 * This class is used to control gameplay logic while in combat.
 */
public class CombatManager {

    // FIELDS
    private final GamePanel gp;

    /**
     * Location of the center of the combat field.
     */
    private int fieldCenterCol, fieldCenterRow;

    /**
     * Stored music track that was playing when combat was initiated.
     * A value of -1 means that no music track was playing.
     */
    private int storedTrackIndex = -1;

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
     * List to store the original party entity ordering before combat was initiated.
     * The IDs of party members are stored in this list, with the front-most at index zero, etc.
     */
    private final ArrayList<Integer> partyOrdering = new ArrayList<>();

    /**
     * Set to store IDs of opposing entities involved in combat.
     * Opposing refers to combating entities fighting against the player's side.
     * A set is used to avoid having the same entity entered twice or thrice.
     */
    private final LinkedHashSet<Integer> opposingEntities = new LinkedHashSet<>();

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
     * Store the last generated list of ally entity (active, inactive, and player entity) options in combat.
     * The IDs of the ally entities are stored in this list.
     * Allies refer to combating entities (including the player entity) fighting on the player's side.
     */
    private final ArrayList<Integer> lastGeneratedAllyOptions = new ArrayList<>();

    /**
     * Stores the last generated list of active ally entity (including the player entity) options in combat.
     * The IDs of active entities are stored in this list.
     * Allies refer to combating entities (including the player entity) fighting on the player's side.
     */
    private final ArrayList<Integer> lastGeneratedActiveAllyOptions = new ArrayList<>();

    /**
     * Stores the last generated list of inactive ally entity options in combat.
     * The IDs of inactive entities are stored in this list.
     * Allies refer to combating entities (including the player entity) fighting on the player's side.
     */
    private final ArrayList<Integer> lastGeneratedInactiveAllyOptions = new ArrayList<>();

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

            if (opposingEntities.contains(queuedEntityTurnOrder.peekFirst())) {                                         // If entity at the front of the turn order queue is an opposing entity.

                generateNpcTurn();
            } else {                                                                                                    // If entity at the front of the turn order queue is a party member/player.

                String message = buildRootMenuPrompt(gp.getEntityById(queuedEntityTurnOrder.peekFirst()).getName());
                addQueuedActionBack(new Act_ReadMessage(gp, message, false));
                generateRootSubMenuAction();
                runNextQueuedAction();
            }
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
            gp.initiateTransition(TransitionType.ENTER_COMBAT);
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

        gp.initiateTransition(TransitionType.EXIT_COMBAT);
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
     * @throws IllegalStateException if the turn order is generated with no available viable entities
     */
    public void endEntityTurn() {

        boolean viableEntity = false;                                                                                   // A viable entity is a non-fainted one.
        boolean generateTurnOrderCalled = false;                                                                        // Boolean tracking whether the turn order has already been re-generated.

        while (!viableEntity) {

            queuedEntityTurnOrder.removeFirst();

            if (queuedEntityTurnOrder.peekFirst() == null) {

                if (!generateTurnOrderCalled) {

                    generateTurnOrder();
                    generateTurnOrderCalled = true;
                } else {

                    throw new IllegalStateException("Generated combat turn order with no available viable entities");
                }
            }

            if ((gp.getEntityById(queuedEntityTurnOrder.peekFirst()).getStatus() != EntityStatus.FAINT)
                    && (!gp.getEntityById(queuedEntityTurnOrder.peekFirst()).isHidden())) {                             // If hidden, entity is not actively participating in combat (ex. party member in reserve).

                viableEntity = true;

                if (guardingEntities.remove(queuedEntityTurnOrder.peekFirst())) {                                       // If the entity was in a guarding state, end it.

                    String message = gp.getEntityById(queuedEntityTurnOrder.peekFirst()).getName()
                            + " reverted their defensive stance.";
                    addQueuedActionBack(new Act_ReadMessage(gp, message, true));
                }
            }
        }
    }


    /**
     * Checks whether the player entity has a fainted status or not.
     *
     * @return whether the player entity has fainted (true) or not (false)
     */
    public boolean checkPlayerFainted() {

        if (gp.getPlayer().getStatus() == EntityStatus.FAINT) {

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

        int allPartyCount = gp.getParty().size();
        int faintedPartyCount = 0;

        for (int entityId : gp.getParty().keySet()) {

            if (gp.getEntityById(entityId).getStatus() == EntityStatus.FAINT) {

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
     * Checks whether all opposing entities have a fainted status or not.
     *
     * @return whether all opposing entities have fainted (true) or not (false)
     */
    public boolean checkAllOpposingFainted() {

        int allOpposingCount = gp.getCombatM().getOpposingEntities().size();
        int faintedOpposingCount = 0;

        for (int entityId : gp.getCombatM().getOpposingEntities()) {

            if (gp.getEntityById(entityId).getStatus() == EntityStatus.FAINT) {

                faintedOpposingCount++;
            }
        }

        if (faintedOpposingCount == allOpposingCount) {

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
            case ALLY_MANAGE:
                runAllyManageSubMenuSelection();
                break;
            case ALLY_SWAP:
                runAllySwapSubMenuSelection();
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
        String message = gp.getEntityById(queuedEntityTurnOrder.peekFirst()).getName() + " assumed a defensive stance.";
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

        for (MoveBase move : gp.getEntityById(queuedEntityTurnOrder.peekFirst()).getMoves()) {

            moveOptions.add(move.getName());

            if (move.getSkillPoints() > gp.getEntityById(queuedEntityTurnOrder.peekFirst()).getSkillPoints()) {

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
        ArrayList<String> allyOptions = generateAllyOptions();
        HashMap<Integer, Vector3f> colors = new HashMap<>();
        allyOptions.add("Back");
        colors.put(allyOptions.size() - 1, backOptionColor);
        addQueuedActionBack(new Act_GenerateSubMenu(gp, SubMenuType.PARTY, allyOptions, colors));
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

            ArrayList<String> allyOptions = new ArrayList<>();
            EntityBase entity = gp.getEntityById(lastGeneratedAllyOptions.get(getLatestSubMenuMemory().getSelectedOption()));

            if (getLatestSubMenuMemory().getSelectedOption() <= gp.getNumActivePartyMembers()) {                        // Determine if the selected entity is the player entity or an active party member.

                if (entity.getEntityId() != gp.getPlayer().getEntityId()) {

                    // TODO : Consider disabling option is entity is fainted.
                    allyOptions.add("Swap Out");
                }
            } else {

                // TODO : Consider disabling option is entity is fainted.
                allyOptions.add("Swap In");
            }
            allyOptions.add("Back");
            HashMap<Integer, Vector3f> colors = new HashMap<>();
            colors.put(allyOptions.size() - 1, backOptionColor);
            addQueuedActionBack(new Act_GenerateSubMenu(gp, SubMenuType.ALLY_MANAGE, allyOptions, colors));
        }
    }


    /**
     * Runs logic based on the last option that was selected in the ally manage combat sub-menu.
     */
    private void runAllyManageSubMenuSelection() {

        if (getLatestSubMenuMemory().getSelectedOption() == (getLatestSubMenuMemory().getOptions().size() - 1)) {       // Determine whether the 'Back' option was selected or not.

            revertSubMenuSelection();
        } else {

            String selectedSwapOption = getLatestSubMenuOptionSelection();

            if (selectedSwapOption.equals("Swap In")) {

                ArrayList<String> allyOptions = generateActiveAllyOptions();
                allyOptions.add("Back");
                HashMap<Integer, Vector3f> colors = new HashMap<>();
                colors.put(allyOptions.size() - 1, backOptionColor);
                addQueuedActionBack(new Act_GenerateSubMenu(gp, SubMenuType.ALLY_SWAP, allyOptions, colors));
            } else if (selectedSwapOption.equals("Swap Out")) {

                ArrayList<String> allyOptions = generatedInactiveAllyOptions();
                allyOptions.add("Back");
                HashMap<Integer, Vector3f> colors = new HashMap<>();
                colors.put(allyOptions.size() - 1, backOptionColor);
                addQueuedActionBack(new Act_GenerateSubMenu(gp, SubMenuType.ALLY_SWAP, allyOptions, colors));
            }
        }
    }


    /**
     * Runs logic based on the last option that was selected in the ally swap combat sub-menu.
     */
    private void runAllySwapSubMenuSelection() {

        if (getLatestSubMenuMemory().getSelectedOption() == (getLatestSubMenuMemory().getOptions().size() - 1)) {       // Determine whether the 'Back' option was selected or not.

            revertSubMenuSelection();
        } else {

            int allyId1 = lastGeneratedAllyOptions.get(subMenuLog.get(subMenuLog.size() - 3).getSelectedOption());
            int allyId2 = -1;                                                                                           // Default entity ID to force error if invalid option somehow selected.
            String selectedSwapOption = subMenuLog.get(subMenuLog.size() - 2).getOptions().get(
                    subMenuLog.get(subMenuLog.size() - 2).getSelectedOption());

            if (selectedSwapOption.equals("Swap In")) {

                allyId2 = lastGeneratedActiveAllyOptions.get(getLatestSubMenuMemory().getSelectedOption());
            } else if (selectedSwapOption.equals("Swap Out")) {

                allyId2 = lastGeneratedInactiveAllyOptions.get(getLatestSubMenuMemory().getSelectedOption());
            }
            addQueuedActionBack(new Act_SwapAlly(gp, allyId1, allyId2));
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

            EntityBase sourceEntity = gp.getEntityById(queuedEntityTurnOrder.peekFirst());
            MoveBase move = null;

            switch (subMenuLog.get(subMenuLog.size() - 2).getType()) {
                case ROOT:                                                                                              // A basic attack must have been selected (i.e., 'Attack' option in root combat sub-menu).
                    move = defaultMove;
                    break;
                case SKILL:                                                                                             // A skill move must have been selected (i.e., 'Skill' option in root combat sub-menu).
                    move = sourceEntity.getMoves().get(subMenuLog.get(subMenuLog.size() - 2).getSelectedOption());
                    break;
            }
            EntityBase targetEntity = gp.getEntityById(
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
     */
    private void revertSubMenuSelection() {

        subMenuLog.remove(subMenuLog.size() - 1);                                                                       // Remove memory of the sub-menu where the 'Back' option was selected.

        if (subMenuLog.size() > 1) {

            subMenuLog.remove(subMenuLog.size() - 1);                                                                   // Remove memory of the sub-menu that preceded the sub-menu where the 'Back' option was selected.

            switch (getLatestSubMenuMemory().getType()) {                                                               // Return to the outcome of the sub-menu that generated the previous sub-menu IF within same turn.
                case ROOT:
                    runRootSubMenuSelection();
                    break;
                case SKILL:
                    runSkillSubMenuSelection();
                    break;
                case TARGET_SELECT:
                    generateRootSubMenuAction();                                                                        // End of its respective branch of sub-menus, so a new turn must have commenced since.
                    break;
                case PARTY:
                    runPartySubMenuSelection();
                    break;
                case ALLY_MANAGE:
                    runAllyManageSubMenuSelection();
                    break;
                case ALLY_SWAP:
                    generateRootSubMenuAction();                                                                        // End of its respective branch of sub-menus, so a new turn must have commenced since.
                    break;
            }
        } else {

            if (subMenuLog.size() > 0) {

                subMenuLog.remove(subMenuLog.size() - 1);                                                               // Remove memory of the sub-menu that preceded the sub-menu where the 'Back' option was selected.
            }
            generateRootSubMenuAction();                                                                                // Return to the root sub-menu since no previous sub-menu to were the 'Back' option was selected is recorded in memory.
        }
    }


    /**
     * Performs any loading needed to be done for the basic enter combat transition type.
     */
    private void handleBasicEnterCombatTransitionLoading() {

        // Override camera tracking (we'd like to manually position it for combat).
        gp.getCameraS().setOverrideEntityTracking(true);

        // Store original party member ordering.
        for (int entityId : gp.getParty().keySet()) {

            partyOrdering.add(entityId);
        }

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
                } else if (placedPartyMembers == 1) {

                    entity.setRow(fieldCenterRow + 2);
                } else {

                    entity.setHidden(true);
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

        // Reset all combating entities back to pre-combat hidden state.
        for (int entityId : storedEntityHidden.keySet()) {
            gp.getEntityById(entityId).setHidden(storedEntityHidden.get(entityId));
        }

        // Restore pre-combat ordering of party members.
        for (int i = 0; i < partyOrdering.size(); i++) {
            gp.getPartyS().swapEntityInParty(partyOrdering.get(i), i);
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

        // Add all combating entities to the new list of combating entities, even if fainted or outside the first
        // two party slots.

        for (int entityId : gp.getCombatingEntities()) {

            entitiesToPlace.put(entityId, gp.getEntityById(entityId).getAgility());
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
        EntityBase sourceEntity = gp.getEntityById(queuedEntityTurnOrder.peekFirst());

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
        generateOpposingTargetOptions(move.getMoveTargets());
        i = random.nextInt(lastGeneratedTargetOptions.size());                                                          // Generate random number from 0 to number of selectable target entities (both inclusive)
        EntityBase targetEntity = gp.getEntityById(lastGeneratedTargetOptions.get(i));

        // Add move action.
        String message = buildUseMoveMessage(sourceEntity.getName(), move.getName());
        addQueuedActionBack(new Act_ReadMessage(gp, message, true));
        addQueuedActionBack(new Act_UseMove(gp, move, sourceEntity.getEntityId(), targetEntity.getEntityId()));
        addQueuedActionBack(new Act_EndEntityTurn(gp));
        runNextQueuedAction();
    }


    /**
     * Generates a list of names of all selectable ally entities (active, inactive, and player entity) in combat.
     * The player entity is added to the list first, then all party member entities are added in same order as the party
     * map.
     * Allies refer to combating entities (including the player entity) fighting on the player's side.
     *
     * @return list of names of selectables allies
     */
    private ArrayList<String> generateAllyOptions() {

        ArrayList<String> allies = new ArrayList<>();
        lastGeneratedAllyOptions.clear();
        allies.add(gp.getPlayer().getName());
        lastGeneratedAllyOptions.add(gp.getPlayer().getEntityId());

        for (EntityBase entity : gp.getParty().values()) {

            allies.add(entity.getName());
            lastGeneratedAllyOptions.add(entity.getEntityId());
        }
        return allies;
    }


    /**
     * Generates a list of names of selectable active ally entities (including the player entity) in combat.
     * Allies refer to combating entities (including the player entity) fighting on the player's side.
     * The list of entity IDs of last generated selectable active ally entities ('lastGeneratedActiveAllyOptions') is
     * also refreshed by this method (same ordering as generated list of names).
     *
     * @return list of names of selectable active allies
     */
    private ArrayList<String> generateActiveAllyOptions() {

        ArrayList<String> activeAllies = new ArrayList<>();
        lastGeneratedActiveAllyOptions.clear();
        int entityIndex = 0;

        for (EntityBase entity : gp.getParty().values()) {                                                              // Populate options with active party member entities.

            if (entityIndex < gp.getNumActivePartyMembers()) {

                activeAllies.add(entity.getName());
                lastGeneratedActiveAllyOptions.add(entity.getEntityId());
                entityIndex++;
            } else {

                break;
            }
        }
        return activeAllies;
    }


    /**
     * Generates a list of names of selectable inactive ally entities in combat.
     * Allies refer to combating entities (including the player entity) fighting on the player's side.
     * The list of entity IDs of last generated selectable inactive ally entities ('lastGeneratedActiveAllyOptions') is
     * also refreshed by this method (same ordering as generated list of names).
     *
     * @return list of names of selectable inactive allies
     */
    private ArrayList<String> generatedInactiveAllyOptions() {

        ArrayList<String> inactiveAllies = new ArrayList<>();
        lastGeneratedInactiveAllyOptions.clear();
        int entityIndex = 0;

        for (EntityBase entity : gp.getParty().values()) {                                                              // Populate options with inactive party member entities.

            if (entityIndex >= gp.getNumActivePartyMembers()) {

                inactiveAllies.add(entity.getName());
                lastGeneratedInactiveAllyOptions.add(entity.getEntityId());
            }
            entityIndex++;
        }
        return inactiveAllies;
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
        EntityBase sourceEntity = gp.getEntityById(queuedEntityTurnOrder.peekFirst());
        MoveTargets moveTargets = null;

        switch (getLatestSubMenuMemory().getType()) {
            case ROOT:                                                                                                  // A basic attack must have been selected (i.e., 'Attack' option in root combat sub-menu).
                moveTargets = defaultMove.getMoveTargets();
                break;
            case SKILL:                                                                                                 // A skill move must have been selected (i.e., 'Skill' option in root combat sub-menu).
                moveTargets = sourceEntity.getMoves().get(getLatestSubMenuMemory().getSelectedOption()).getMoveTargets();
                break;
        }
        ArrayList<String> targetOptions = generateAllyTargetOptions(moveTargets);
        targetOptions.add("Back");
        HashMap<Integer, Vector3f> colors = new HashMap<>();
        colors.put(targetOptions.size() - 1, backOptionColor);
        addQueuedActionBack(new Act_GenerateSubMenu(gp, SubMenuType.TARGET_SELECT, targetOptions, colors));
    }


    /**
     * Generates a list of names of selectable targets in combat.
     * This method is to be used to generate selectable targets when an ally entity is the entity whose turn it is.
     * This method will not properly generate a list of selectable targets when an opposing entity is the entity whose
     * turn it is.
     * The "self" entity is that whose turn it currently is (may or may not be the player entity).
     * Opposing refers to combating entities fighting against the player's side.
     * Allies refer to combating entities (including the player entity) fighting on the player's side.
     * Only non-fainted entities will be added to the list of selectable targets.
     * The list of entity IDs of last generated selectable targets ('lastGeneratedTargetOptions') is also refreshed by
     * this method (same ordering as generated list of names).
     *
     * @param moveTargets possible targets
     * @return list of names of selectable targets
     */
    private ArrayList<String> generateAllyTargetOptions(MoveTargets moveTargets) {

        ArrayList<String> targetOptions = new ArrayList<>();
        lastGeneratedTargetOptions.clear();

        switch (moveTargets) {
            case OPPOSING:
                addOpposingEntitiesToTargetOptions(targetOptions);
                break;
            case ALLY:
                addAllyEntitiesToTargetOptions(targetOptions);
                break;
            case SELF:
                addSelfEntityToTargetOptions(targetOptions);
                break;
            case OPPOSING_ALLY:
                addOpposingEntitiesToTargetOptions(targetOptions);
                addAllyEntitiesToTargetOptions(targetOptions);
                break;
            case OPPOSING_SELF:
                addOpposingEntitiesToTargetOptions(targetOptions);
                addSelfEntityToTargetOptions(targetOptions);
                break;
            case ALLY_SELF:
                addAllyEntitiesToTargetOptions(targetOptions);
                addSelfEntityToTargetOptions(targetOptions);
                break;
            case OPPOSING_ALLY_SELF:
                addOpposingEntitiesToTargetOptions(targetOptions);
                addAllyEntitiesToTargetOptions(targetOptions);
                addSelfEntityToTargetOptions(targetOptions);
                break;
        }
        return targetOptions;
    }


    /**
     * Generates a list of names of selectable targets in combat.
     * This method is to be used to generate selectable targets when an opposing entity is the entity whose turn it is.
     * This method will not properly generate a lit of selectable targets when an ally entity is the entity whose turn
     * it is.
     * The "self" entity is that whose turn it currently is (may or may not be the player entity).
     * Opposing refers to combating entities fighting against the player's side.
     * Allies refer to combating entities (including the player entity) fighting on the player's side.
     * Only non-fainted entities will be added to the list of selectable targets.
     * The list of entity IDs of last generated selectable targets ('lastGeneratedTargetOptions') is also refreshed by
     * this method (same ordering as generated list of names).
     *
     * @param moveTargets possible targets
     * @return list of names of selectable targets
     */
    private ArrayList<String> generateOpposingTargetOptions(MoveTargets moveTargets) {

        // IMPORTANT NOTE!
        // The enum MoveTargets refers to OPPOSING, ALLY, and SELF.
        // Throughout this program, OPPOSING is the language used to refer to combating entities not on the player's
        // side, and ALLY is the language used to refer to combating entities on the player's side.
        // That said, in this method only, the definitions of ALLY and OPPOSING are flipped.
        // In other words, if a move targets OPPOSING entities, then entities on the player's side will be targeted
        // since they are opposing to the entity using the move.
        // If a move targets ALLY entities, then entities on the side of the entity using the move will be
        // targeted since they are allies to the entity using the move.
        // This is done so that this enum can be re-used in this method, which is meant for generating targets for an
        // entity not on the player's side.

        ArrayList<String> targetOptions = new ArrayList<>();
        lastGeneratedTargetOptions.clear();

        switch (moveTargets) {
            case OPPOSING:
                addAllyEntitiesToTargetOptions(targetOptions);
                break;
            case ALLY:
                addOpposingEntitiesToTargetOptions(targetOptions);
                break;
            case SELF:
                addSelfEntityToTargetOptions(targetOptions);
                break;
            case OPPOSING_ALLY:
                addAllyEntitiesToTargetOptions(targetOptions);
                addOpposingEntitiesToTargetOptions(targetOptions);
                break;
            case OPPOSING_SELF:
                addAllyEntitiesToTargetOptions(targetOptions);
                addSelfEntityToTargetOptions(targetOptions);
                break;
            case ALLY_SELF:
                addOpposingEntitiesToTargetOptions(targetOptions);
                addSelfEntityToTargetOptions(targetOptions);
                break;
            case OPPOSING_ALLY_SELF:
                addAllyEntitiesToTargetOptions(targetOptions);
                addOpposingEntitiesToTargetOptions(targetOptions);
                addSelfEntityToTargetOptions(targetOptions);
                break;
        }
        return targetOptions;
    }


    /**
     * Adds opposing combating entity names to a list of selectable targets.
     * This method may be used for either an ally entity's turn or an opposing entity's turn.
     * Opposing refers to combating entities fighting against the player's side.
     * Only non-fainted entities will be added.
     * The IDs of all added entities will also be added to the list of last generated selectable targets in the same
     * order.
     * Note that the list is pass-by-reference, so the original list passed is modified by this method with no need to
     * return a value.
     *
     * @param targetOptions list to add viable target options to (of String or Integer type)
     */
    private void addOpposingEntitiesToTargetOptions(ArrayList targetOptions) {

        for (int entityId : opposingEntities) {

            if ((gp.getEntityById(queuedEntityTurnOrder.peekFirst()).getEntityId() != entityId)
                    && (gp.getEntityById(entityId).getStatus() != EntityStatus.FAINT)) {

                targetOptions.add(gp.getEntityById(entityId).getName());
                lastGeneratedTargetOptions.add(entityId);
            }
        }
    }


    /**
     * Adds ally combating entity names to a list of selectable targets.
     * This method may be used for either an ally entity's turn or an opposing entity's turn.
     * Allies refer to combating entities (including the player entity) fighting on the player's side.
     * Only non-fainted entities will be added.
     * Additionally, if an ally entity's turn is currently active, then said entity will not be added to the list.
     * The IDs of all added entities will also be added to the list of last generated selectable targets in the same
     * order.
     * Note that the list is pass-by-reference, so the original list passed is modified by this method with no need to
     * return a value.
     *
     * @param targetOptions list to add viable target options to (of String or Integer type)
     */
    private void addAllyEntitiesToTargetOptions(ArrayList targetOptions) {

        if ((gp.getEntityById(queuedEntityTurnOrder.peekFirst()).getEntityId() != gp.getPlayer().getEntityId())
                && (gp.getPlayer().getStatus() != EntityStatus.FAINT)) {

            targetOptions.add(gp.getPlayer().getName());
            lastGeneratedTargetOptions.add(gp.getPlayer().getEntityId());
        }
        int entityIndex = 0;

        for (int entityId : gp.getParty().keySet()) {

            if (entityIndex < gp.getNumActivePartyMembers()) {

                if ((gp.getEntityById(queuedEntityTurnOrder.peekFirst()).getEntityId() != entityId)
                        && (gp.getEntityById(entityId).getStatus() != EntityStatus.FAINT)) {

                    targetOptions.add(gp.getEntityById(entityId).getName());
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
     * This method may be used for either an ally entity's turn or an opposing entity's turn.
     * The entity added may be either the player entity or a party member entity.
     * Only non-fainted entities will be added.
     * The ID of the added entity will also be added to the list of last generated selectable targets.
     * Note that the list is pass-by-reference, so the original list passed is modified by this method with no need to
     * return a value.
     *
     * @param targetOptions list to add viable target option to (of String or Integer type)
     */
    private void addSelfEntityToTargetOptions(ArrayList targetOptions) {

        if ((gp.getEntityById(queuedEntityTurnOrder.peekFirst()).getEntityId() == gp.getPlayer().getEntityId())         // Check if player entity is the self.
                && (gp.getPlayer().getStatus() != EntityStatus.FAINT)) {

            targetOptions.add(gp.getPlayer().getName());
            lastGeneratedTargetOptions.add(gp.getPlayer().getEntityId());
            return;                                                                                                     // The single target entity has been added.
        }
        int entityIndex = 0;

        for (int entityId : gp.getParty().keySet()) {

            if (entityIndex < gp.getNumActivePartyMembers()) {

                if ((gp.getEntityById(queuedEntityTurnOrder.peekFirst()).getEntityId() == entityId)                     // Check if a party member entity is the self.
                        && (gp.getEntityById(entityId).getStatus() != EntityStatus.FAINT)) {

                    targetOptions.add(gp.getEntityById(entityId).getName());
                    lastGeneratedTargetOptions.add(entityId);
                    return;                                                                                             // The single target entity has been added.
                }
            } else {

                break;
            }
            entityIndex++;
        }

        for (int entityId : opposingEntities) {

            if ((gp.getEntityById(queuedEntityTurnOrder.peekFirst()).getEntityId() == entityId)                         // Check if an opposing entity is the self.
                    && (gp.getEntityById(entityId).getStatus() != EntityStatus.FAINT)) {

                targetOptions.add(gp.getEntityById(entityId).getName());
                lastGeneratedTargetOptions.add(entityId);
                return;                                                                                                 // The single target entity has been added.
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
            storedEntityHidden.put(target.getEntityId(), target.isHidden());
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

        for (int entityId : gp.getCombatingEntities()) {

            gp.getEntityById(entityId).resetStats();
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
        storedTrackIndex = -1;
        storedEntityCols.clear();
        storedEntityRows.clear();
        storedEntityDirections.clear();
        storedEntityHidden.clear();
        partyOrdering.clear();
        opposingEntities.clear();
        activeEnterCombatTransitionType = null;
        activeExitCombatTransitionType = null;
        queuedEntityTurnOrder.clear();
        queuedActions.clear();
        subMenuLog.clear();
        lastGeneratedAllyOptions.clear();
        lastGeneratedActiveAllyOptions.clear();
        lastGeneratedInactiveAllyOptions.clear();
        lastGeneratedTargetOptions.clear();
        guardingEntities.clear();
        lastActionSubmenu = false;
        combatUiVisible = false;
    }


    // GETTERS
    public LinkedHashSet<Integer> getOpposingEntities() {
        return opposingEntities;
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
