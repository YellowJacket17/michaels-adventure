package combat.support;

import combat.MoveBase;
import core.GamePanel;
import org.joml.Vector2f;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

/**
 * This class contains methods to facilitate animations during combat.
 */
public class CombatAnimationSupport {

    // BASIC FIELD
    private final GamePanel gp;


    // STANDARD MOVE ANIMATION FIELDS
    /**
     * Boolean indicating whether a standard move animation is staged/playing (true) or not (false).
     */
    private boolean standardMoveAnimationActive = false;

    /**
     * ID of the entity using animated move.
     */
    private int sourceEntityId;

    /**
     * Combat move being used for animation.
     */
    private MoveBase move;

    /**
     * UUIDs of particle effects being animated.
     */
    private List<UUID> particleEffectUuids = new ArrayList<>();

    /**
     * Calculated final life points of each targeted entity after applying damage; entity ID is the key, life points is
     * the value.
     */
    private HashMap<Integer, Integer> targetEntitiesFinalLife = new HashMap<>();

    /**
     * Non-whole number remainder of target entity life points to be carried over to the next update; entity ID is the key,
     * life is the value.
     * This exists because life values on entities are only updated by whole numbers.
     */
    private HashMap<Integer, Double> targetEntitiesDamageRemainder = new HashMap<>();

    /**
     * Number of life points that an entity will gain/lose per second while the life bar animation is playing.
     * Increasing this value will increase the speed of the animation.
     */
    private final double healthBarSpeed = 20.0;

    /**
     * Time to delay the start of the actual animation from when the `initiateStandardMoveAnimation()` method is called
     * (seconds).
     */
    private double standardMoveAnimationFrontDelay;

    /**
     * Time between when the actual animation is complete and control is handed off to the next combat action (seconds).
     * (seconds).
     */
    private double standardMoveAnimationBackDelay;


    // STANDARD FAINT ANIMATION FIELDS
    /**
     * Boolean indicating whether a standard faint animation is staged/playing (true) or not (false).
     */
    private boolean standardFaintAnimationActive = false;


    // CONSTRUCTOR
    /**
     * Constructs a CombatAnimationSupport instance.
     *
     * @param gp GamePanel instance
     */
    public CombatAnimationSupport(GamePanel gp) {
        this.gp = gp;
    }


    // METHODS
    /**
     * Updates the state of any active combat animation by one frame.
     *
     * @param dt time since last frame (seconds)
     */
    public void update (double dt) {

        // Standard move animation.
        if (standardMoveAnimationActive) {
            if (standardMoveAnimationFrontDelay > 0) {
                standardMoveAnimationFrontDelay -= dt;
                if (standardMoveAnimationFrontDelay <= 0) {
                    kickoffStandardMoveAnimation();
                }
            }
            if (standardMoveAnimationFrontDelay <= 0) {
                updateStandardAttackAnimation(dt);
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

            this.sourceEntityId = sourceEntityId;

            for (int targetEntityId : targetEntitiesFinalLife.keySet()) {

                this.targetEntitiesFinalLife.put(targetEntityId, targetEntitiesFinalLife.get(targetEntityId));
                this.targetEntitiesDamageRemainder.put(targetEntityId, 0.0);
            }
            this.move = move;
            standardMoveAnimationActive = true;
            standardMoveAnimationFrontDelay = 0.4;
            standardMoveAnimationBackDelay = 0.4;
        }
    }


    /**
     * Kicks off the staged standard move animation.
     */
    private void kickoffStandardMoveAnimation() {

        gp.getEntityM().getEntityById(sourceEntityId).initiateCombatAttackAnimation();                                  // Play attack animation on source entity.
        gp.getEntityM().getEntityById(sourceEntityId).subtractSkillPoints(move.getSkillPoints());                       // Subtract skill points used by this move.

        if (move.getSoundEffect() != null) {                                                                            // Play move sound effect.

            gp.getSoundS().playEffect(move.getSoundEffect());
        } else {

            gp.getSoundS().playEffect("testEffect2");
        }

        for (int targetEntityId : targetEntitiesFinalLife.keySet()) {

            gp.getParticleEffectM().addParticleEffect(                                                                  // Play particle effect animation on target entities.
                    new Vector2f(
                            gp.getEntityM().getEntityById(targetEntityId).getWorldX() + (GamePanel.NATIVE_TILE_SIZE / 2),
                            gp.getEntityM().getEntityById(targetEntityId).getWorldY() + (GamePanel.NATIVE_TILE_SIZE / 4)),
                    move.getParticleEffectColor() != null ? move.getParticleEffectColor() : new Vector3f(255, 255, 255),
                    4.0f
            );
        }
    }


    /**
     * Updates the state of the active standard combat attack animation by one frame.
     *
     * @param dt time since last frame (seconds)
     */
    private void updateStandardAttackAnimation(double dt) {

        boolean sourceAttackComplete = !gp.getEntityM().getEntityById(sourceEntityId).isPlayingCombatAttackAnimation(); // Check if attack animation for source entity is complete.
        boolean healthBarsComplete = true;
        boolean particleEffectsComplete = true;
        int lifeSubtractionCandidate;

        for (int entityId : targetEntitiesFinalLife.keySet()) {                                                         // Check if life bar animation for each target entity is complete.

            if (targetEntitiesFinalLife.get(entityId) <= gp.getEntityM().getEntityById(entityId).getLife()) {           // Update target entity life if not already at final value.

                targetEntitiesDamageRemainder.put(
                        entityId,
                        targetEntitiesDamageRemainder.get(entityId) + (healthBarSpeed * dt));

                lifeSubtractionCandidate = (int)Math.floor(targetEntitiesDamageRemainder.get(entityId));

                if (((gp.getEntityM().getEntityById(entityId).getLife() - lifeSubtractionCandidate)
                        <= targetEntitiesFinalLife.get(entityId))
                        || ((gp.getEntityM().getEntityById(entityId).getLife() - lifeSubtractionCandidate) < 0)) {

                    gp.getEntityM().getEntityById(entityId).setLife(targetEntitiesFinalLife.get(entityId));
                } else {

                    gp.getEntityM().getEntityById(entityId).subtractLife(lifeSubtractionCandidate);
                    healthBarsComplete = false;                                                                         // A life bar was found that has not completed its animation.
                }
                targetEntitiesDamageRemainder.put(
                        entityId,
                        targetEntitiesDamageRemainder.get(entityId) - lifeSubtractionCandidate);
            }
        }

        for (UUID uuid : particleEffectUuids) {                                                                         // Check if all particle effect animations are complete.

            if (gp.getParticleEffectM().getParticleEffectByUuid(uuid) != null) {

                particleEffectsComplete = false;                                                                        // A particle effect was found that has not completed its animation.
                break;
            }
        }

        if (sourceAttackComplete && healthBarsComplete && particleEffectsComplete) {                                    // Check if all animations have completed; if so, combat can be progressed to the next action.

            if (standardMoveAnimationBackDelay > 0) {

                standardMoveAnimationBackDelay -= dt;
            }

            if (standardMoveAnimationBackDelay <= 0) {

                List<Integer> targetEntityIds = new ArrayList<>();

                for (int targetEntityId : targetEntitiesFinalLife.keySet()) {

                    targetEntityIds.add(targetEntityId);
                }
                move.runEffects(sourceEntityId, targetEntityIds);                                                       // Apply any additional affects that this move may have.
                gp.getCombatM().pollFainting();                                                                         // Check whether any entities fainted as a result of this move; appropriate actions will be queued if so.
                resetStandardMoveAnimation();
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
        sourceEntityId = 0;
        move = null;
        particleEffectUuids.clear();
        targetEntitiesFinalLife.clear();
        targetEntitiesDamageRemainder.clear();
        standardMoveAnimationFrontDelay = 0;
        standardMoveAnimationBackDelay = 0;
    }


    // GETTER
    public boolean isAnimationActive() {
        return (standardMoveAnimationActive && standardFaintAnimationActive);
    }
}
