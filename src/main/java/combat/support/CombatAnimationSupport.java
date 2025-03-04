package combat.support;

import combat.MoveBase;
import combat.implementation.action.Act_EndEntityTurn;
import core.GamePanel;
import entity.enumeration.EntityStatus;
import org.joml.Vector2f;
import org.joml.Vector3f;
import utility.LimitedArrayList;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

/**
 * This class contains methods to facilitate animations during combat.
 */
public class CombatAnimationSupport {

    // BASIC FIELD
    private final GamePanel gp;


    // STANDARD MOVE ANIMATION (SMA) FIELDS
    /**
     * Boolean indicating whether a standard move animation is staged/playing (true) or not (false).
     */
    private boolean standardMoveAnimationActive = false;

    /**
     * ID of the entity using animated move.
     */
    private int smaSourceEntityId;

    /**
     * Combat move being used for animation.
     */
    private MoveBase smaMove;

    /**
     * UUIDs of particle effects being animated.
     */
    private ArrayList<UUID> smaParticleEffectUuids = new ArrayList<>();

    /**
     * Calculated final life points of each targeted entity after applying damage; entity ID is the key, life points is
     * the value.
     */
    private HashMap<Integer, Integer> smaTargetEntitiesFinalLife = new HashMap<>();

    /**
     * Non-whole number remainder of target entity life points to be carried over to the next update; entity ID is the key,
     * life is the value.
     * This exists because life values on entities are only updated by whole numbers.
     */
    private HashMap<Integer, Double> smaTargetEntitiesDamageRemainder = new HashMap<>();

    /**
     * Number of life points that an entity will gain/lose per second while the life bar animation is playing.
     * Increasing this value will increase the speed of the animation.
     */
    private final double smaHealthBarSpeed = 20.0;

    /**
     * Time to delay the start of the actual standard move animation from when the `initiateStandardMoveAnimation()`
     * method is called (seconds).
     */
    private double smaFrontDelay;

    /**
     * Time between when the actual standard move animation is complete and control is handed off to the next combat
     * action (seconds).
     */
    private double smaBackDelay;


    // STANDARD PARTY SWAP ANIMATION (SPSA) FIELDS
    /**
     * Boolean indicating whether a standard party swap animation is staged/playing (true) or not (false).
     */
    private boolean standardPartySwapAnimationActive = false;

    /**
     * List to store the IDs of the entities involved in a standard party swap animation.
     */
    private final LimitedArrayList<Integer> spsaSwappingEntityIds = new LimitedArrayList<>(2);

    /**
     * Time to delay the start of the actual standard party swap animation from when the
     * `initiateStandardPartySwapAnimation()` method ia called (seconds).
     */
    private double spsaFrontDelay;

    /**
     * Time between when the actual standard party swap animation is complete and control is handed off to the next
     * combat action (seconds).
     */
    private double spsaBackDelay;


    // STANDARD FAINT ANIMATION (SFA) FIELDS
    /**
     * Boolean indicating whether a standard faint animation is staged/playing (true) or not (false).
     */
    private boolean standardFaintAnimationActive = false;

    private ArrayList<Integer> sfaFaintingEntityIds = new ArrayList<>();

    /**
     * Time to delay the start of the actual standard faint animation from when the `initiateStandardFaintAnimation()`
     * method ia called (seconds).
     */
    private double sfaFrontDelay;

    /**
     * Time between when the actual standard faint animation is complete and control is handed off to the next combat
     * action (seconds).
     */
    private double sfaBackDelay;


    // CONSTRUCTOR
    /**
     * Constructs a CombatAnimationSupport instance.
     *
     * @param gp GamePanel instance
     */
    public CombatAnimationSupport(GamePanel gp) {
        this.gp = gp;
    }


    // TODO : Modify standard move animations to handle restoring health.

    // TODO : Add standard revive animation to handle reviving an entity during combat.

    // TODO : Consider modifying standard move animation to only finish once sound effect is complete.


    // METHODS
    /**
     * Updates the state of any active combat animation by one frame.
     *
     * @param dt time since last frame (seconds)
     */
    public void update (double dt) {

        // Standard move animation.
        if (standardMoveAnimationActive) {
            if (smaFrontDelay > 0) {
                smaFrontDelay -= dt;
                if (smaFrontDelay <= 0) {
                    kickoffStandardMoveAnimation();
                }
            }
            if (smaFrontDelay <= 0) {
                updateStandardMoveAnimation(dt);
            }
        }

        // Standard party swap animation.
        if (standardPartySwapAnimationActive) {
            if (spsaFrontDelay > 0) {
                spsaFrontDelay -= dt;
                if (spsaFrontDelay <= 0) {
                    kickoffStandardPartySwapAnimation();
                }
            }
            if (spsaFrontDelay <= 0) {
                updateStandardPartySwapAnimation(dt);
            }
        }

        // Standard faint animation.
        if (standardFaintAnimationActive) {
            if (sfaFrontDelay > 0) {
                sfaFrontDelay -= dt;
                if (sfaFrontDelay <= 0) {
                    kickoffStandardFaintAnimation();
                }
            }
            if (sfaFrontDelay <= 0) {
                updateStandardFaintAnimation(dt);
            }
        }
    }


    /**
     * Initiates a standard move animation to play.
     * The remaining life points of the target entities will be modified by this animation to their final values.
     * The remaining skill points of the source entity will be modified by this animation to its final value as
     * determined by the move being used.
     * The sound effect associated with the move is also played.
     * Only moves that subtract health from target entities are supported.
     * Once the animation is complete and effects/fainting have been polled, an action to end the current turn will be
     * added to the end of the queue of combat actions.
     * If a standard move animation is already active, nothing will happen.
     *
     * @param sourceEntityId ID of entity using the animated combat move
     * @param targetEntitiesFinalLife calculated final life points of each targeted entity after applying damage; entity
     *                                ID is the key, life points is the value
     * @param move combat move being used for animation
     */
    public void initiateStandardMoveAnimation(int sourceEntityId, HashMap<Integer, Integer> targetEntitiesFinalLife,
                                              MoveBase move) {

        if (!standardMoveAnimationActive) {

            this.smaSourceEntityId = sourceEntityId;

            for (int targetEntityId : targetEntitiesFinalLife.keySet()) {

                this.smaTargetEntitiesFinalLife.put(targetEntityId, targetEntitiesFinalLife.get(targetEntityId));
                this.smaTargetEntitiesDamageRemainder.put(targetEntityId, 0.0);
            }
            this.smaMove = move;
            standardMoveAnimationActive = true;
            smaFrontDelay = 0.4;                                                                                        // If 'smaFrontDelay' is kept at zero, this line must be replaced with 'initiateStandardMoveAnimation();'.
            smaBackDelay = 0.4;
        }
    }


    /**
     * Initiates a standard party swap animation to play.
     * The fade state and position of each target entity will be modified by this animation.
     * If a standard party swap animation is already active, nothing will happen.
     *
     * @param entityId1 ID of entity to swap
     * @param entityId2 ID of entity to swap
     */
    public void initiateStandardPartySwapAnimation(int entityId1, int entityId2) {

        if (!standardPartySwapAnimationActive) {

            spsaSwappingEntityIds.add(entityId1);
            spsaSwappingEntityIds.add(entityId2);
            standardPartySwapAnimationActive = true;
            kickoffStandardPartySwapAnimation();                                                                        // If 'smaFrontDelay' is set to non-zero number X, this line must be replaced with 'smaFrontDelay = X;'.
            spsaBackDelay = 0.1;
        }
    }


    /**
     * Initiates a standard faint animation to play.
     * The status of each target entity will be modified to `FAINT` by this animation.
     * If a standard faint animation is already active, nothing will happen.
     *
     * @param entityIds IDs of entities to participate in this batch of faint animations
     */
    public void initiateStandardFaintAnimation(ArrayList<Integer> entityIds) {

        if (!standardFaintAnimationActive) {

            for (int entityId : entityIds) {

                sfaFaintingEntityIds.add(entityId);
            }
            standardFaintAnimationActive = true;
            kickoffStandardFaintAnimation();
            sfaBackDelay = 0.1;
        }
    }


    /**
     * Kicks off the staged standard move animation.
     */
    private void kickoffStandardMoveAnimation() {

        gp.getEntityM().getEntityById(smaSourceEntityId).initiateCombatAttackAnimation();                               // Play attack animation on source entity.
        gp.getEntityM().getEntityById(smaSourceEntityId).subtractSkillPoints(smaMove.getSkillPoints());                 // Subtract skill points used by this move.

        if (smaMove.getSoundEffect() != null) {                                                                         // Play move sound effect.

            gp.getSoundS().playEffect(smaMove.getSoundEffect());
        } else {

            gp.getSoundS().playEffect("testEffect2");
        }

        for (int targetEntityId : smaTargetEntitiesFinalLife.keySet()) {

            gp.getParticleEffectM().addParticleEffect(                                                                  // Play particle effect animation on target entities.
                    new Vector2f(
                            gp.getEntityM().getEntityById(targetEntityId).getWorldX() + (GamePanel.NATIVE_TILE_SIZE / 2),
                            gp.getEntityM().getEntityById(targetEntityId).getWorldY() + (GamePanel.NATIVE_TILE_SIZE / 4)),
                    smaMove.getParticleEffectColor() != null ? smaMove.getParticleEffectColor() : new Vector3f(255, 255, 255),
                    4.0f
            );
        }
    }


    /**
     * Kicks off the staged standard party swap animation.
     */
    private void kickoffStandardPartySwapAnimation() {

        gp.getPartyS().swapEntityInParty(spsaSwappingEntityIds.get(0), spsaSwappingEntityIds.get(1), true);
    }


    /**
     * Kicks off the staged standard faint animation.
     */
    private void kickoffStandardFaintAnimation() {

        for (int entityId : sfaFaintingEntityIds) {

            gp.getEntityM().getEntityById(entityId).setStatus(EntityStatus.FAINT);
            gp.getEntityM().getEntityById(entityId).initiateCombatFaintAnimation();
        }
    }


    /**
     * Updates the state of the active standard combat move animation by one frame.
     *
     * @param dt time since last frame (seconds)
     */
    private void updateStandardMoveAnimation(double dt) {

        boolean sourceAttackComplete =
                !gp.getEntityM().getEntityById(smaSourceEntityId).isPlayingCombatAttackAnimation();                     // Check if attack animation for source entity is complete.
        boolean healthBarsComplete = true;
        boolean particleEffectsComplete = true;
        int lifeSubtractionCandidate;

        for (int entityId : smaTargetEntitiesFinalLife.keySet()) {                                                      // Check if life bar animation for each target entity is complete.

            if (smaTargetEntitiesFinalLife.get(entityId) <= gp.getEntityM().getEntityById(entityId).getLife()) {        // Update target entity life if not already at final value.

                smaTargetEntitiesDamageRemainder.put(
                        entityId,
                        smaTargetEntitiesDamageRemainder.get(entityId) + (smaHealthBarSpeed * dt));

                lifeSubtractionCandidate = (int)Math.floor(smaTargetEntitiesDamageRemainder.get(entityId));

                if (((gp.getEntityM().getEntityById(entityId).getLife() - lifeSubtractionCandidate)
                        <= smaTargetEntitiesFinalLife.get(entityId))
                        || ((gp.getEntityM().getEntityById(entityId).getLife() - lifeSubtractionCandidate) < 0)) {

                    gp.getEntityM().getEntityById(entityId).setLife(smaTargetEntitiesFinalLife.get(entityId));
                } else {

                    gp.getEntityM().getEntityById(entityId).subtractLife(lifeSubtractionCandidate);
                    healthBarsComplete = false;                                                                         // A life bar was found that has not completed its animation.
                }
                smaTargetEntitiesDamageRemainder.put(
                        entityId,
                        smaTargetEntitiesDamageRemainder.get(entityId) - lifeSubtractionCandidate);
            }
        }

        for (UUID uuid : smaParticleEffectUuids) {                                                                      // Check if all particle effect animations are complete.

            if (gp.getParticleEffectM().getParticleEffectByUuid(uuid) != null) {

                particleEffectsComplete = false;                                                                        // A particle effect was found that has not completed its animation.
                break;
            }
        }

        if (sourceAttackComplete && healthBarsComplete && particleEffectsComplete) {                                    // Check if all animations have completed; if so, combat can be progressed to the next action.

            if (smaBackDelay > 0) {

                smaBackDelay -= dt;
            }

            if (smaBackDelay <= 0) {

                ArrayList<Integer> targetEntityIds = new ArrayList<>();

                for (int targetEntityId : smaTargetEntitiesFinalLife.keySet()) {

                    targetEntityIds.add(targetEntityId);
                }
                smaMove.runEffects(smaSourceEntityId, targetEntityIds);                                                 // Apply any additional affects that this move may have.
                gp.getCombatM().pollFainting();                                                                         // Check whether any entities fainted as a result of this move; appropriate actions will be queued if so.
                gp.getCombatM().addQueuedActionBack(new Act_EndEntityTurn(gp));                                         // End the current entity's turn.
                resetStandardMoveAnimation();
                gp.getCombatM().progressCombat();
            }
        }
    }


    /**
     * Updates the state of the active standard party swap animation by one frame.
     *
     * @param dt time since last frame (seconds)
     */
    private void updateStandardPartySwapAnimation(double dt) {

        boolean swappingEntitiesNeutralFadeState = true;

        for (int entityId : spsaSwappingEntityIds) {

            if (gp.getEntityM().getEntityById(entityId).getActiveFadeEffect() != null) {

                swappingEntitiesNeutralFadeState = false;
                break;
            }
        }

        if (swappingEntitiesNeutralFadeState && gp.getPartyS().isStagedEntityFadeUpEffectsEmpty()) {

            if (spsaBackDelay > 0) {

                spsaBackDelay -= dt;
            }

            if (spsaBackDelay <= 0) {

                resetStandardPartySwapAnimation();
                gp.getCombatM().progressCombat();
            }
        }
    }


    /**
     * Updates the state of the active standard faint animation by one frame.
     *
     * @param dt time since last frame (seconds)
     */
    private void updateStandardFaintAnimation(double dt) {

        boolean faintAnimationsComplete = true;

        for (int entityId : sfaFaintingEntityIds) {

            if (gp.getEntityM().getEntityById(entityId).isPlayingCombatFaintAnimation()) {

                faintAnimationsComplete = false;
                break;
            }
        }

        if (faintAnimationsComplete) {

            if (sfaBackDelay > 0) {

                sfaBackDelay -= dt;
            }

            if (sfaBackDelay <= 0) {

                resetStandardFaintAnimation();
                gp.getCombatM().progressCombat();
            }
        }
    }


    /**
     * Resets CombatAnimationSupport standard move animation fields back to their default state.
     * Intended to be called to clean up after a standard move animation has completed.
     */
    private void resetStandardMoveAnimation() {

        standardMoveAnimationActive = false;
        smaSourceEntityId = 0;
        smaMove = null;
        smaParticleEffectUuids.clear();
        smaTargetEntitiesFinalLife.clear();
        smaTargetEntitiesDamageRemainder.clear();
        smaFrontDelay = 0;
        smaBackDelay = 0;
    }


    /**
     * Resets CombatAnimationSupport standard party swap animation fields back to their default state.
     * Intended to be called to clean up after a standard party swap animation has completed.
     */
    private void resetStandardPartySwapAnimation() {

        standardPartySwapAnimationActive = false;
        spsaSwappingEntityIds.clear();
        spsaFrontDelay = 0;
        spsaBackDelay = 0;
    }


    /**
     * Resets CombatAnimationSupport standard faint animation fields back to their default state.
     * Intended to be called to clean up after a standard faint animation has completed.
     */
    private void resetStandardFaintAnimation() {

        standardFaintAnimationActive = false;
        sfaFaintingEntityIds.clear();
        sfaFrontDelay = 0;
        sfaBackDelay = 0;
    }


    // GETTER
    public boolean isAnimationActive() {
        return (standardMoveAnimationActive && standardPartySwapAnimationActive && standardFaintAnimationActive);
    }

    public boolean isStandardMoveAnimationActive() {
        return standardMoveAnimationActive;
    }

    public boolean isStandardFaintAnimationActive() {
        return standardFaintAnimationActive;
    }

    public boolean isStandardPartySwapAnimationActive() {
        return standardPartySwapAnimationActive;
    }
}
