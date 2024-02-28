package combat.implementation.action;

import combat.ActionBase;
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

        for (int targetEntityId : targetEntityIds) {

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
        move.runEffects(sourceEntityId, targetEntityIds);

        // TODO : Additional logic would be added here for how to handle if an attacked entity faints, etc.
        //  For now, we'll just end the current entity's turn.
        //  Also, should fainting be polled before running effects as well?

        pollFainted();
        gp.getCombatM().addQueuedActionBack(new Act_EndEntityTurn(gp));
        gp.getCombatM().progressCombat();
    }


    /**
     * Polls whether any entities involved in this action have fainted since the last poll.
     */
    private void pollFainted() {

        checkEntityFainted(sourceEntityId);

        for (int targetEntityId : targetEntityIds) {

            checkEntityFainted(targetEntityId);
        }
    }


    /**
     * Checks whether an entity has fainted.
     * If it has, its status is changed and a message is queued to display.
     *
     * @param entityId ID of entity to check
     */
    private void checkEntityFainted(int entityId) {

        EntityBase targetEntity = gp.getEntityById(entityId);

        if ((targetEntity.getLife() <= 0)
                && (targetEntity.getStatus() != EntityStatus.FAINT)) {

            targetEntity.setStatus(EntityStatus.FAINT);
            String stagedName = "";

            if (!targetEntity.getName().equals("")) {

                stagedName = "???";
            } else {

                targetEntity.getName();
            }
            String message = stagedName + " has no energy left to fight!";
            gp.getCombatM().addQueuedActionBack(new Act_ReadMessage(gp, message, true));
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
