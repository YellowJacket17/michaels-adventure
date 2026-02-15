package combat.implementation.action;

import combat.ActionBase;
import combat.MoveBase;
import combat.enumeration.MoveCategory;
import core.GamePanel;
import utility.LimitedArrayList;
import utility.UtilityTool;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

/**
 * This class defines a combat action (use a combat move).
 * Note that if any affected entities faint, further actions will be queued as applicable.
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
    private final ArrayList<Integer> targetEntityIds = new ArrayList<>();


    // CONSTRUCTORS
    public Act_UseMove(GamePanel gp, MoveBase move, int sourceEntityId, int targetEntityId) {
        super(gp);
        this.move = move;
        this.sourceEntityId = sourceEntityId;
        this.targetEntityIds.add(targetEntityId);
    }


    public Act_UseMove(GamePanel gp, MoveBase move, int sourceEntityId, ArrayList<Integer> targetEntityIds) {
        super(gp);
        this.move = move;
        this.sourceEntityId = sourceEntityId;
        for (int entityId : targetEntityIds) {
            this.targetEntityIds.add(entityId);
        }
    }


    // METHODS
    @Override
    public void run() {

        if (UtilityTool.VERBOSE_LOGGING) {

            UtilityTool.logInfo("Running combat move "
                    + (((move.getName() != null)
                    && (!move.getName().equals("")))
                    ? "'" + (move.getName() + "' ")
                    : "")
                    + "with ID '"
                    + move.getMoveId()
                    + "'.");
        }

        if (move.getCategory() == MoveCategory.SUPPORT) {

            HashMap<Integer, Integer> targetEntitiesDeltaLife = new HashMap<>();

            for (int targetEntityId : targetEntityIds) {

                targetEntitiesDeltaLife.put(targetEntityId, 0);
            }
            move.runEffects(sourceEntityId, targetEntitiesDeltaLife);
            gp.getCombatM().progressCombat();
        } else {

            HashMap<Integer, Integer> targetEntitiesFinalLife = new HashMap<>();
            LimitedArrayList<Integer> evadedTargetEntityIds = new LimitedArrayList<>(targetEntityIds.size());
            LimitedArrayList<Integer> criticalHitTargetEntityIds = new LimitedArrayList<>(targetEntityIds.size());
            boolean criticalHit;

            for (int targetEntityId : targetEntityIds) {

                if (rollAccuracy()) {

                    if (!gp.getCombatM().getGuardingEntities().contains(targetEntityId)) {                              // Critical hit only possible if target is not guarding.

                        criticalHit = rollCritical();
                    } else {

                        criticalHit = false;
                    }

                    if (criticalHit) {

                        criticalHitTargetEntityIds.add(targetEntityId);
                    }
                    targetEntitiesFinalLife.put(targetEntityId, calculateDamage(targetEntityId, criticalHit));          // Calculate (but don't apply) final life value of the target entity.
                } else {

                    evadedTargetEntityIds.add(targetEntityId);
                }
            }

            if (evadedTargetEntityIds.size() > 0) {

                gp.getCombatM().addQueuedActionBack(
                        new Act_ReadMessage(gp, buildEvasionMessage(evadedTargetEntityIds), true, true));
            }

            if (criticalHitTargetEntityIds.size() > 0) {

                gp.getCombatM().addQueuedActionBack(
                        new Act_ReadMessage(gp, buildCriticalHitMessage(criticalHitTargetEntityIds), true, true));
            }

            if (move.isFlashAnimation()) {

                gp.getCombatAnimationS().initiateFlashMoveAnimation(
                        sourceEntityId, targetEntitiesFinalLife, move, 0.4, 0.4);                                       // Play flash attack animation (fainting + move affects applied afterward).
            } else {

                gp.getCombatAnimationS().initiateStandardMoveAnimation(
                        sourceEntityId, targetEntitiesFinalLife, move, 0.4, 0.4);                                       // Play standard attack animation (fainting + move affects applied afterward).
            }

            // NOTE: The `progressCombat()` method in CombatManager to hand off control to the next queued action will
            // automatically be called once this move animation in the `updateStandardMoveAnimation()` method in
            // CombatAnimationSupport is complete, hence why it is not called here.
        }
    }


    /**
     * Calculates damage to an entity targeted by this move.
     * Note that this method does not actually apply damage to the entity.
     *
     * @param targetEntityId ID of the entity to calculate damage for
     * @param criticalHit whether to calculate damage as a critical hit (true) or not (false)
     * @return change in life points of the targeted entity after applying damage
     */
    private int calculateDamage(int targetEntityId, boolean criticalHit) {

        float rawTargetDamage;
        int actTargetDamage;

        if (move.getCategory() == MoveCategory.PHYSICAL) {

            int sourceEntityAttack = gp.getEntityM().getEntityById(sourceEntityId).getBaseAttack()
                    + (int)(gp.getEntityM().getEntityById(sourceEntityId).getBaseAttack()
                    * gp.getEntityM().getEntityById(sourceEntityId).getAttackBuff());
            int targetEntityDefense = gp.getEntityM().getEntityById(targetEntityId).getBaseDefense()
                    + (int)(gp.getEntityM().getEntityById(targetEntityId).getBaseDefense()
                    * ((criticalHit && (gp.getEntityM().getEntityById(targetEntityId).getDefenseBuff() > 0))
                        ? 1 : gp.getEntityM().getEntityById(targetEntityId).getDefenseBuff()));                         // Ignore target entity defense buff (not debuff) if critical hit.
            rawTargetDamage = move.getPower()
                    * ((float)sourceEntityAttack / targetEntityDefense)
                    * (criticalHit ? 1.3f : 1);
            actTargetDamage = (int)Math.ceil(rawTargetDamage - (rawTargetDamage * rollDamageVariance()));

        } else if (move.getCategory() == MoveCategory.MAGIC) {

            int sourceEntityMagic = gp.getEntityM().getEntityById(sourceEntityId).getBaseMagic()
                    + (int)(gp.getEntityM().getEntityById(sourceEntityId).getBaseMagic()
                    * gp.getEntityM().getEntityById(sourceEntityId).getMagicBuff());
            int targetEntityMagic = gp.getEntityM().getEntityById(targetEntityId).getBaseMagic()
                    + (int)(gp.getEntityM().getEntityById(targetEntityId).getBaseMagic()
                    * ((criticalHit && (gp.getEntityM().getEntityById(targetEntityId).getMagicBuff() > 0))
                        ? 1 : gp.getEntityM().getEntityById(targetEntityId).getMagicBuff()));                           // Ignore target entity magic buff (not debuff) if critical hit.
            rawTargetDamage = move.getPower()
                    * ((float)sourceEntityMagic / targetEntityMagic)
                    * (criticalHit ? 1.3f : 1);
            actTargetDamage = (int)Math.ceil(rawTargetDamage - (rawTargetDamage * rollDamageVariance()));

        } else {

            actTargetDamage = 0;
        }

        if (move.getCategory() != MoveCategory.SUPPORT) {

            if (gp.getCombatM().getGuardingEntities().contains(targetEntityId)) {                                       // Determine if the target entity is in a guarding state.

                actTargetDamage /= 2;
            }

            if (actTargetDamage <= 0) {

                actTargetDamage = 1;                                                                                    // Guarantee that at least one life point is taken for non-support moves.
            }
        }
        return actTargetDamage;
    }


    /**
     * Builds entity move evasion message.
     *
     * @param evadedTargetEntityIds IDs of entities that evaded the move
     * @return message
     */
    private String buildEvasionMessage(LimitedArrayList<Integer> evadedTargetEntityIds) {

        LimitedArrayList<String> evadedTargetEntityNames = new LimitedArrayList<>(evadedTargetEntityIds.size());

        for (int entityId : evadedTargetEntityIds) {

            evadedTargetEntityNames.add(gp.getEntityM().getEntityById(entityId).getName());
        }
        return UtilityTool.buildEntityListMessage(evadedTargetEntityNames, false) + " evaded the attack!";
    }


    /**
     * Builds critical hit message.
     *
     * @param criticalHitTargetEntityIds IDs of entities on which a critical hit was landed
     * @return message
     */
    private String buildCriticalHitMessage(LimitedArrayList<Integer> criticalHitTargetEntityIds) {

        LimitedArrayList<String> evadedTargetEntityNames = new LimitedArrayList<>(criticalHitTargetEntityIds.size());

        for (int entityId : criticalHitTargetEntityIds) {

            evadedTargetEntityNames.add(gp.getEntityM().getEntityById(entityId).getName());
        }
        return "Critical hit on " + UtilityTool.buildEntityListMessage(evadedTargetEntityNames, false) + "!";
    }


    /**
     * Rolls to determine whether this move will land or not.
     *
     * @return whether this move will land (true) or not (false)
     */
    private boolean rollAccuracy() {

        Random random = new Random();
        int i = random.nextInt(101);                                                                                    // Generate random number from 0 (inclusive) to 100 (inclusive, since 101 is exclusive).

        if (i <= move.getAccuracy()) {

            return true;
        } else {

            return false;
        }
    }


    /**
     * Rolls to determine whether this move will land a critical hit or not.

     * @return whether this move will land a critical hit (true) or not (false)
     */
    private boolean rollCritical() {

        Random random = new Random();
        int i = random.nextInt(101);                                                                                    // Generate random number from 0 (inclusive) to 100 (inclusive, since 101 is exclusive).

        if ((i <= 4) || ((i <= 30) && move.isHighCriticalHit())) {

            return true;
        } else {

            return false;
        }
    }


    /**
     * Rools to determine random variance to calculate damage.
     * @return percent variance to subtract from calculated damage
     */
    private float rollDamageVariance() {

        Random random = new Random();
        return random.nextInt(11) / 100.0f;                                                                             // Generate random number from 0 (inclusive) to 10 (inclusive, since 11 is exclusive), then divide by 100.
    }


    // GETTERS
    public MoveBase getMove() {
        return move;
    }

    public int getSourceEntityId() {
        return sourceEntityId;
    }

    public ArrayList<Integer> getTargetEntityIds() {
        return targetEntityIds;
    }
}
