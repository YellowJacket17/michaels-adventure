package combat.implementation.action;

import combat.ActionBase;
import combat.ExitCombatTransitionType;
import combat.MoveBase;
import combat.MoveCategory;
import core.GamePanel;
import entity.EntityBase;
import entity.EntityStatus;
import utility.LimitedArrayList;

import java.util.List;

/**
 * This class defines a combat action (use a combat move).
 */
public class Act_UseMove extends ActionBase {

    // FIELDS
    /**
     * Move to be used.
     */
    private final MoveBase move;

    /**
     * ID of the entity using the move.
     */
    private final int sourceEntityId;

    /**
     * IDs of the entities targeted by the move.
     */
    private final List<Integer> targetEntityIds;


    // CONSTRUCTORS
    public Act_UseMove(GamePanel gp, MoveBase move, int sourceEntityId, int targetEntityId) {
        super(gp);
        this.move = move;
        this.sourceEntityId = sourceEntityId;
        this.targetEntityIds = List.of(targetEntityId);                                                                 // Immutable list.
    }


    public Act_UseMove(GamePanel gp, MoveBase move, int sourceEntityId, int targetEntityId1, int targetEntityId2) {
        super(gp);
        this.move = move;
        this.sourceEntityId = sourceEntityId;
        this.targetEntityIds = List.of(targetEntityId1, targetEntityId2);                                               // Immutable list.
    }


    public Act_UseMove(GamePanel gp, MoveBase move, int sourceEntityId, int targetEntityId1, int targetEntityId2,
                       int targetEntityId3) {
        super(gp);
        this.move = move;
        this.sourceEntityId = sourceEntityId;
        this.targetEntityIds = List.of(targetEntityId1, targetEntityId2, targetEntityId3);                              // Immutable list.
    }


    public Act_UseMove(GamePanel gp, MoveBase move, int sourceEntityId, int targetEntityId1, int targetEntityId2,
                       int targetEntityId3, int targetEntityId4) {
        super(gp);
        this.move = move;
        this.sourceEntityId = sourceEntityId;
        this.targetEntityIds = List.of(targetEntityId1, targetEntityId2, targetEntityId3, targetEntityId4);             // Immutable list.
    }


    public Act_UseMove(GamePanel gp, MoveBase move, int sourceEntityId, int targetEntityId1, int targetEntityId2,
                       int targetEntityId3, int targetEntityId4, int targetEntityId5) {
        super(gp);
        this.move = move;
        this.sourceEntityId = sourceEntityId;
        this.targetEntityIds = List.of(targetEntityId1, targetEntityId2, targetEntityId3, targetEntityId4,
                targetEntityId5);                                                                                       // Immutable list.
    }


    public Act_UseMove(GamePanel gp, MoveBase move, int sourceEntityId, int targetEntityId1, int targetEntityId2,
                       int targetEntityId3, int targetEntityId4, int targetEntityId5, int targetEntityId6) {
        super(gp);
        this.move = move;
        this.sourceEntityId = sourceEntityId;
        this.targetEntityIds = List.of(targetEntityId1, targetEntityId2, targetEntityId3, targetEntityId4,
                targetEntityId5, targetEntityId6);                                                                      // Immutable list.
    }


    // METHOD
    @Override
    public void run() {

        LimitedArrayList<Integer> allDamage = new LimitedArrayList<>(6);
        int sourceEntityAttack = gp.getEntityById(sourceEntityId).getAttack();
        int sourceEntityMagic = gp.getEntityById(sourceEntityId).getMagic();

        for (int targetEntityId : targetEntityIds) {                                                                    // Calculate and apply damage dealt to each target entity.

            int targetDamage = 0;
            if (move.getCategory() == MoveCategory.PHYSICAL) {

                int targetEntityDefense = gp.getEntityById(targetEntityId).getDefense();
                targetDamage = move.getPower() * (sourceEntityAttack / targetEntityDefense);
                allDamage.add(targetDamage);
            } else if (move.getCategory() == MoveCategory.MAGIC) {

                int targetEntityMagic = gp.getEntityById(targetEntityId).getMagic();
                targetDamage = move.getPower() * (sourceEntityMagic / targetEntityMagic);
                allDamage.add(targetDamage);
            }
            gp.getEntityById(targetEntityId).subtractLife(targetDamage);
        }
        gp.getEntityById(sourceEntityId).subtractSkillPoints(move.getSkillPoints());                                    // Subtract skill points used by this move.
        move.runEffects(sourceEntityId, targetEntityIds);                                                               // Apply any additional affects that this move may have.

        // TODO : Should fainting be polled before running effects as well?

        pollJustFainted();
        gp.getCombatM().addQueuedActionBack(new Act_EndEntityTurn(gp));
        gp.getCombatM().progressCombat();
    }


    /**
     * Polls whether any entities involved in this action have fainted since the last poll.
     */
    private void pollJustFainted() {

        checkJustFainted(sourceEntityId);

        for (int targetEntityId : targetEntityIds) {

            checkJustFainted(targetEntityId);
        }

        if (checkAllOpposingFainted()) {

            String message = gp.getPlayer().getName() + " won the fight!";
            gp.getCombatM().addQueuedActionBack(new Act_ReadMessage(gp, message, true));
            gp.getCombatM().addQueuedActionBack(new Act_ToggleCombatUi(gp, false));
            gp.getCombatM().addQueuedActionBack(new Act_ExitCombat(gp, ExitCombatTransitionType.BASIC));
        }

        if (checkAllPartyFainted()) {

            String message = gp.getPlayer().getName() + " lost the fight.";
            gp.getCombatM().addQueuedActionBack(new Act_ReadMessage(gp, message, true));
            gp.getCombatM().addQueuedActionBack(new Act_ToggleCombatUi(gp, false));
            gp.getCombatM().addQueuedActionBack(new Act_ExitCombat(gp, ExitCombatTransitionType.BASIC));
        }
    }


    /**
     * Checks whether an entity has just fainted in this action; in other words, if the entity has zero life but not a
     * fainted status.
     * If it has, its status is changed and a message is queued to display.
     *
     * @param entityId ID of entity to check
     */
    private void checkJustFainted(int entityId) {

        EntityBase targetEntity = gp.getEntityById(entityId);

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
            gp.getCombatM().addQueuedActionBack(new Act_ReadMessage(gp, message, true));
        }
    }


    /**
     * Checks whether the entire party (including the player entity) has a fainted status.
     *
     * @return whether the entire party has fainted (true) or not (false)
     */
    private boolean checkAllPartyFainted() {

        int allPartyCount = gp.getParty().size() + 1;
        int faintedPartyCount = 0;

        if (gp.getPlayer().getStatus() == EntityStatus.FAINT) {
            faintedPartyCount++;
        }

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
     * Checks whether the all opposing entities have a fainted status.
     *
     * @return whether all opposing entities have fainted (true) or not (false)
     */
    private boolean checkAllOpposingFainted() {

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


    // GETTERS
    public MoveBase getMove() {
        return move;
    }

    public int getSourceEntityId() {
        return sourceEntityId;
    }

    public List<Integer> getTargetEntityIds() {
        return targetEntityIds;
    }
}
