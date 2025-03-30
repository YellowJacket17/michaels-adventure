package combat.support;

import combat.MoveBase;
import core.GamePanel;
import entity.enumeration.EntityStatus;
import entity.enumeration.FadeEffectType;
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
     * ID of the entity using a move during a standard move animation.
     */
    private int smaSourceEntityId;

    /**
     * Combat move being used during a standard move animation.
     */
    private MoveBase smaMove;

    /**
     * Calculated final life points of each targeted entity after applying damage/restoration for a standard move
     * animation; entity ID is the key, life points is the value.
     */
    private final HashMap<Integer, Integer> smaTargetEntitiesFinalLife = new HashMap<>();

    /**
     * Non-whole number remainder of target entity life points to be carried over to the next update during a standard
     * move animation; entity ID is the key, life is the value.
     * This exists because life values on entities are only updated by whole numbers.
     */
    private final HashMap<Integer, Double> smaTargetEntitiesDamageRemainder = new HashMap<>();

    /**
     * Indicates whether an entity's life will increase (true) or decrease (false) as a result of a standard move
     * animation; entity ID is the key, life increase/decrease is the value.
     */
    private final HashMap<Integer, Boolean> smaTargetEntitiesLifeIncrease = new HashMap<>();

    /**
     * UUIDs of particle effects being animated during a standard move animation.
     */
    private final ArrayList<UUID> smaParticleEffectUuids = new ArrayList<>();

    /**
     * Number of life points that an entity will gain/lose per second while a standard move animation is playing.
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

    /**
     * List to store the IDs of the entities involved in a standard faint animation.
     */
    private final ArrayList<Integer> sfaFaintingEntityIds = new ArrayList<>();

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


    // STANDARD REVIVE ANIMATION (SRA) FIELDS
    /**
     * Boolean indicating whether a standard revive animation is staged/playing (true) or not (false).
     */
    private boolean standardReviveAnimationActive = false;

    /**
     * List to store the IDs of the entities involved in a standard revive animation.
     */
    private final ArrayList<Integer> sraRevivingEntityIds = new ArrayList<>();

    /**
     * Time to delay the start of the actual standard revive animation from when the `initiateStandardReviveAnimation()`
     * method ia called (seconds).
     */
    private double sraFrontDelay;

    /**
     * Time between when the actual standard revive animation is complete and control is handed off to the next combat
     * action (seconds).
     */
    private double sraBackDelay;


    // CUSTOM EFFECT ANIMATION (CEA) FIELDS
    /**
     * Boolean indicating whether a custom effect animation is staged/playing (true) or not (false).
     */
    private boolean customEffectAnimationActive = false;

    /**
     * List to store the IDs of the entities participating in a custom effect animation.
     */
    private final ArrayList<Integer> ceaEntityIds = new ArrayList<>();

    /**
     * Calculated final life points of each participating entity after applying damage/restoration for a custom effect
     * animation, if applicable; entity ID is the key, life points is the value.
     */
    private final HashMap<Integer, Integer> ceaEntitiesFinalLife = new HashMap<>();

    /**
     * Non-whole number remainder of participating entity life points to be carried over to the next update during a
     * custom effect animation; entity ID is the key, life is the value.
     * This exists because life values on entities are only updated by whole numbers.
     */
    private final HashMap<Integer, Double> ceaEntitiesDamageRemainder = new HashMap<>();

    /**
     * Indicates whether an entity's life will increase (true) or decrease (false) as a result of a custom effect
     * animation; entity ID is the key, life increase/decrease is the value.
     */
    private final HashMap<Integer, Boolean> ceaEntitiesLifeIncrease = new HashMap<>();

    /**
     * Calculated final skill points of each entity participating in a custom effect animation; entity ID is the key,
     * skill points is the value; if a participating entity has no skill point modification, then in may be omitted from
     * this map.
     */
    private final HashMap<Integer, Integer> ceaEntitiesFinalSkillPoints = new HashMap<>();

    /**
     * Particle effect color for a custom effect animation (r, g, b).
     */
    private final Vector3f ceaParticleEffectColor = new Vector3f(255, 255, 255);

    /**
     * UUIDs of particle effects being animated during a custom effect animation.
     */
    private final ArrayList<UUID> ceaParticleEffectUuids = new ArrayList<>();

    /**
     * Sound effect resource name for a custom effect animation.
     */
    private String ceaSoundEffectResourceName = "";

    /**
     * Number of life points that an entity will gain/lose per second while a custom effect animation is playing.
     * Increasing this value will increase the speed of the animation.
     */
    private final double ceaHealthBarSpeed = 20.0;

    /**
     * Boolean indicating whether to wait to hand off control to the next queued combat action until all other running
     * animations in the CombatAnimationSupport class are complete (true) or not (false).
     * The former allows multiple animations to run concurrently.
     * The latter just progresses combat after the custom effect animation is complete.
     */
    private boolean ceaWaitToProgressCombat = false;

    /**
     * Time to delay the start of the actual custom effect animation from when the `initiateCustomEffectAnimation()`
     * method ia called (seconds).
     */
    private double ceaFrontDelay;

    /**
     * Time between when the actual custom effect animation is complete and control is handed off to the next combat
     * action (seconds).
     */
    private double ceaBackDelay;


    // CONSTRUCTOR
    /**
     * Constructs a CombatAnimationSupport instance.
     *
     * @param gp GamePanel instance
     */
    public CombatAnimationSupport(GamePanel gp) {
        this.gp = gp;
    }


    // TODO : Consider modifying standard move animation to only finish once sound effect is complete.


    // TODO : Consider making versions of `initiateCustomEffectAnimation()` that do not require skill points as arguments.


    // TODO : Consider making smooth skill point bar animation, similar to health bar animation.


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

        // Standard revive animation.
        if (standardReviveAnimationActive) {
            if (sraFrontDelay > 0) {
                sraFrontDelay -= dt;
                if (sraFrontDelay <= 0) {
                    kickoffStandardReviveAnimation();
                }
            }
            if (sraFrontDelay <= 0) {
                updateStandardReviveAnimation(dt);
            }
        }

        // Custom effect animation.
        if (customEffectAnimationActive) {
            if (ceaFrontDelay > 0) {
                ceaFrontDelay -= dt;
                if (ceaFrontDelay <= 0) {
                    kickoffCustomEffectAnimation();
                }
            }
            if (ceaFrontDelay <= 0) {
                updateCustomEffectAnimation(dt);
            }
        }
    }


    /**
     * Initiates a standard move animation to play.
     * The remaining life points of the target entities will be modified by this animation to their final values.
     * The remaining skill points of the source entity will be modified by this animation to its final value as
     * determined by the move being used.
     * The sound effect associated with the move is also played.
     * Both life addition and subtraction from target entities are supported.
     * Once the animation is complete and effects/fainting have been polled, combat will be progressed to the next
     * queued action.
     * If a standard move animation is already active, nothing will happen.
     *
     * @param sourceEntityId ID of entity using the animated combat move
     * @param targetEntitiesFinalLife calculated final life points of each targeted entity after applying damage; entity
     *                                ID is the key, life points is the value
     * @param move combat move being used for animation
     * @param frontDelay time to delay the start of the actual animation from when this method ia called (seconds)
     * @param backDelay time between when the actual animation is complete and control is handed off to the next queued
     *                  combat action (seconds)
     */
    public void initiateStandardMoveAnimation(int sourceEntityId, HashMap<Integer, Integer> targetEntitiesFinalLife,
                                              MoveBase move, double frontDelay, double backDelay) {

        if (!standardMoveAnimationActive) {

            smaSourceEntityId = sourceEntityId;

            for (int targetEntityId : targetEntitiesFinalLife.keySet()) {

                smaTargetEntitiesFinalLife.put(targetEntityId, targetEntitiesFinalLife.get(targetEntityId));
                smaTargetEntitiesDamageRemainder.put(targetEntityId, 0.0);

                if (targetEntitiesFinalLife.get(targetEntityId)
                        > gp.getEntityM().getEntityById(targetEntityId).getLife()) {

                    smaTargetEntitiesLifeIncrease.put(targetEntityId, true);
                } else {
                    smaTargetEntitiesLifeIncrease.put(targetEntityId, false);
                }
            }
            smaMove = move;
            standardMoveAnimationActive = true;

            if (frontDelay <= 0) {

                kickoffStandardMoveAnimation();
            } else {

                smaFrontDelay = frontDelay;
            }
            smaBackDelay = backDelay;
        }
    }


    /**
     * Initiates a standard party swap animation to play.
     * The fade state and position of each target entity will be modified by this animation.
     * Once the animation is complete, combat will be progressed to the next queued action.
     * If a standard party swap animation is already active, nothing will happen.
     *
     * @param entityId1 ID of entity to swap
     * @param entityId2 ID of entity to swap
     * @param frontDelay time to delay the start of the actual animation from when this method ia called (seconds)
     * @param backDelay time between when the actual animation is complete and control is handed off to the next queued
     *                  combat action (seconds)
     */
    public void initiateStandardPartySwapAnimation(int entityId1, int entityId2, double frontDelay, double backDelay) {

        if (!standardPartySwapAnimationActive) {

            spsaSwappingEntityIds.add(entityId1);
            spsaSwappingEntityIds.add(entityId2);
            standardPartySwapAnimationActive = true;

            if (frontDelay <= 0) {

                kickoffStandardPartySwapAnimation();
            } else {

                spsaFrontDelay = frontDelay;
            }
            spsaBackDelay = backDelay;
        }
    }


    /**
     * Initiates a standard faint animation to play.
     * The status of each target entity will be modified to `FAINT` by this animation.
     * Once the animation is complete, control will be handed off to the next queued combat action.
     * If a standard faint animation is already active, nothing will happen.
     *
     * @param entityIds IDs of entities to participate in this batch of faint animations
     * @param frontDelay time to delay the start of the actual animation from when this method ia called (seconds)
     * @param backDelay time between when the actual animation is complete and control is handed off to the next queued
     *                  combat action (seconds)
     */
    public void initiateStandardFaintAnimation(ArrayList<Integer> entityIds, double frontDelay, double backDelay) {

        if (!standardFaintAnimationActive) {

            for (int entityId : entityIds) {

                sfaFaintingEntityIds.add(entityId);
            }
            standardFaintAnimationActive = true;

            if (frontDelay <= 0) {

                kickoffStandardFaintAnimation();
            } else {

                sfaFrontDelay = frontDelay;
            }
            sfaBackDelay = backDelay;
        }
    }


    /**
     * Initiates a standard revive animation to play.
     * The status of each target entity will be modified to `HEALTHY` by this animation.
     * The entity will automatically be revived with one life point if a previous higher value has not already been set.
     * If a target entity does not have a `FAINT` status, nothing will happen.
     * Once the animation is complete, control will be handed off to the next queued combat action.
     * If a standard revive animation is already active, nothing will happen.
     *
     * @param entityIds IDs of entities to participate in this batch of revive animations
     * @param frontDelay time to delay the start of the actual animation from when this method ia called (seconds)
     * @param backDelay time between when the actual animation is complete and control is handed off to the next queued
     *                  combat action (seconds)
     */
    public void initiateStandardReviveAnimation(ArrayList<Integer> entityIds, double frontDelay, double backDelay) {

        if (!standardReviveAnimationActive) {

            for (int entityId : entityIds) {

                if (gp.getEntityM().getEntityById(entityId).getStatus() == EntityStatus.FAINT) {

                    sraRevivingEntityIds.add(entityId);
                }
            }
            standardReviveAnimationActive = true;

            if (frontDelay <= 0) {

                kickoffStandardReviveAnimation();
            } else {

                sraFrontDelay = frontDelay;
            }
            sraBackDelay = backDelay;
        }
    }


    /**
     * Initiates a custom effect animation to play.
     * If a custom effect animation is already active, nothing will happen.
     *
     * @param entityIds IDs of entities participating in the animation
     * @param entitiesFinalSkillPoints calculated final skill points of each participating entity; entity ID is the key,
     *                                 skill points is the value; if a participating entity has no skill point
     *                                 modification, then in may be omitted from this map
     * @param particleEffectColor particle effect color (r, g, b)
     * @param soundEffectResourceName sound effect resource name
     * @param waitToProgressCombat whether to wait to hand off control to the next queued combat action until all other
     *                             running animations in the CombatAnimationSupport class are complete (true) or not
     *                             (false, i.e., just progress combat after this animation is complete); the former
     *                             allows multiple animations to run concurrently
     * @param frontDelay time to delay the start of the actual animation from when this method ia called (seconds)
     * @param backDelay time between when the actual animation is complete and control is handed off to the next queued
     *                  combat action (seconds)
     */
    public void initiateCustomEffectAnimation(ArrayList<Integer> entityIds,
                                              HashMap<Integer, Integer> entitiesFinalSkillPoints,
                                              Vector3f particleEffectColor,
                                              String soundEffectResourceName,
                                              boolean waitToProgressCombat,
                                              double frontDelay, double backDelay) {

        if (!customEffectAnimationActive) {

            for (int entityId : entityIds) {

                ceaEntityIds.add(entityId);
            }

            for (int entityId : entitiesFinalSkillPoints.keySet()) {

                ceaEntitiesFinalSkillPoints.put(entityId, entitiesFinalSkillPoints.get(entityId));
            }
            ceaParticleEffectColor.x = particleEffectColor.x;
            ceaParticleEffectColor.y = particleEffectColor.y;
            ceaParticleEffectColor.z = particleEffectColor.z;
            ceaSoundEffectResourceName = soundEffectResourceName;
            ceaWaitToProgressCombat = waitToProgressCombat;
            customEffectAnimationActive = true;

            if (frontDelay <= 0) {

                kickoffCustomEffectAnimation();
            } else {

                ceaFrontDelay = frontDelay;
            }
            ceaBackDelay = backDelay;
        }
    }


    /**
     * Initiates a custom effect animation to play.
     * The remaining life points of the target entities will be modified by this animation to their final values.
     * Both life addition and subtraction from target entities are supported.
     * If a custom effect animation is already active, nothing will happen.
     *
     * @param entitiesFinalLife calculated final life points of each entity participating in the animation; entity ID is
     *                          the key, life points is the value
     * @param entitiesFinalSkillPoints calculated final skill points of each participating entity; entity ID is the key,
     *                                 skill points is the value; if a participating entity has no skill point
     *                                 modification, then in may be omitted from this map
     * @param particleEffectColor particle effect color (r, g, b)
     * @param soundEffectResourceName sound effect resource name
     * @param waitToProgressCombat whether to wait to hand off control to the next queued combat action until all other
     *                             running animations in the CombatAnimationSupport class are complete (true) or not
     *                             (false, i.e., just progress combat after this animation is complete); the former
     *                             allows multiple animations to run concurrently
     * @param frontDelay time to delay the start of the actual animation from when this method ia called (seconds)
     * @param backDelay time between when the actual animation is complete and control is handed off to the next queued
     *                  combat action (seconds)
     */
    public void initiateCustomEffectAnimation(HashMap<Integer, Integer> entitiesFinalLife,
                                              HashMap<Integer, Integer> entitiesFinalSkillPoints,
                                              Vector3f particleEffectColor,
                                              String soundEffectResourceName,
                                              boolean waitToProgressCombat,
                                              double frontDelay, double backDelay) {

        if (!customEffectAnimationActive) {

            for (int entityId : entitiesFinalLife.keySet()) {

                ceaEntityIds.add(entityId);
                ceaEntitiesFinalLife.put(entityId, entitiesFinalLife.get(entityId));
                ceaEntitiesDamageRemainder.put(entityId, 0.0);

                if (entitiesFinalLife.get(entityId)
                        > gp.getEntityM().getEntityById(entityId).getLife()) {

                    ceaEntitiesLifeIncrease.put(entityId, true);
                } else {
                    ceaEntitiesLifeIncrease.put(entityId, false);
                }
            }

            for (int entityId : entitiesFinalSkillPoints.keySet()) {

                ceaEntitiesFinalSkillPoints.put(entityId, entitiesFinalSkillPoints.get(entityId));
            }
            ceaParticleEffectColor.x = particleEffectColor.x;
            ceaParticleEffectColor.y = particleEffectColor.y;
            ceaParticleEffectColor.z = particleEffectColor.z;
            ceaSoundEffectResourceName = soundEffectResourceName;
            ceaWaitToProgressCombat = waitToProgressCombat;
            customEffectAnimationActive = true;

            if (frontDelay <= 0) {

                kickoffCustomEffectAnimation();
            } else {

                ceaFrontDelay = frontDelay;
            }
            ceaBackDelay = backDelay;
        }
    }


    /**
     * Kicks off the staged standard move animation.
     */
    private void kickoffStandardMoveAnimation() {

        gp.getEntityM().getEntityById(smaSourceEntityId).initiateCombatAttackAnimation();                               // Play attack animation on source entity.
        gp.getEntityM().getEntityById(smaSourceEntityId).subtractSkillPoints(smaMove.getSkillPoints());                 // Subtract skill points used by this move.

        if ((smaMove.getSoundEffect() != null) && (!smaTargetEntitiesFinalLife.isEmpty())) {                            // Play move sound effect (as long as there are targets being hit).

            gp.getSoundS().playEffect(smaMove.getSoundEffect());
        }

        for (int targetEntityId : smaTargetEntitiesFinalLife.keySet()) {

            smaParticleEffectUuids.add(
                    gp.getParticleEffectM().addParticleEffect(                                                          // Play particle effect animation on target entities.
                    new Vector2f(
                            gp.getEntityM().getEntityById(targetEntityId).getWorldX() + (GamePanel.NATIVE_TILE_SIZE / 2),
                            gp.getEntityM().getEntityById(targetEntityId).getWorldY() + (GamePanel.NATIVE_TILE_SIZE / 4)),
                    smaMove.getParticleEffectColor() != null ? smaMove.getParticleEffectColor() : new Vector3f(255, 255, 255),
                    4.0f)
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
     * Kicks off the staged standard revive animation.
     */
    private void kickoffStandardReviveAnimation() {

        for (int entityId : sraRevivingEntityIds) {

            gp.getEntityM().getEntityById(entityId).initiateFadeEffect(
                    FadeEffectType.FADE_DOWN, gp.getPartyS().getStandardFadeEffectDuration(), false);
        }
    }


    /**
     * Kicks off the staged custom effect animation.
     */
    private void kickoffCustomEffectAnimation() {

        for (int entityId : ceaEntitiesFinalSkillPoints.keySet()) {

            gp.getEntityM().getEntityById(entityId).setSkill(ceaEntitiesFinalSkillPoints.get(entityId));
        }

        gp.getSoundS().playEffect(ceaSoundEffectResourceName);

        for (int entityId : ceaEntityIds) {

            ceaParticleEffectUuids.add(
                    gp.getParticleEffectM().addParticleEffect(
                            new Vector2f(
                                    gp.getEntityM().getEntityById(entityId).getWorldX() + (GamePanel.NATIVE_TILE_SIZE / 2),
                                    gp.getEntityM().getEntityById(entityId).getWorldY() + (GamePanel.NATIVE_TILE_SIZE / 4)),
                            ceaParticleEffectColor,
                            4.0f)
            );
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

        boolean healthBarsComplete = progressAndCheckHealthBarAnimations(
                smaTargetEntitiesFinalLife,
                smaTargetEntitiesDamageRemainder,
                smaTargetEntitiesLifeIncrease,
                smaHealthBarSpeed, dt);                                                                                 // Check if life bar animation for each target entity is complete.

        boolean particleEffectsComplete = checkParticleEffectAnimations(smaParticleEffectUuids);                        // Check if all particle effect animations are complete.

        boolean soundEffectComplete =
                smaMove.getSoundEffect() != null ? (!gp.getSoundS().isSoundPlaying(smaMove.getSoundEffect())) : true;   // Check if sound effect is complete.

        if (sourceAttackComplete && healthBarsComplete && particleEffectsComplete && soundEffectComplete) {             // Check if all animations and sound effect have completed; if so, combat can be progressed to the next action.

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
                resetStandardMoveAnimation();

                if (!ceaWaitToProgressCombat) {

                    gp.getCombatM().progressCombat();
                }
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

            if (gp.getEntityM().getEntityById(entityId).getActiveFadeEffect() != FadeEffectType.NONE) {

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

                if (!ceaWaitToProgressCombat) {

                    gp.getCombatM().progressCombat();
                }
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

                if (!ceaWaitToProgressCombat) {

                    gp.getCombatM().progressCombat();
                }
            }
        }
    }


    /**
     * Updates the state of the active standard revive animation by one frame.
     *
     * @param dt time since last frame (seconds)
     */
    private void updateStandardReviveAnimation(double dt) {

        boolean reviveAnimationsComplete = true;

        for (int entityId : sraRevivingEntityIds) {

            if ((gp.getEntityM().getEntityById(entityId).getActiveFadeEffect() == FadeEffectType.NONE)
                    && (gp.getEntityM().getEntityById(entityId).getStatus() == EntityStatus.FAINT)) {

                gp.getEntityM().getEntityById(entityId).setStatus(EntityStatus.HEALTHY);
                gp.getEntityM().getEntityById(entityId).initiateFadeEffect(
                        FadeEffectType.FADE_UP, gp.getPartyS().getStandardFadeEffectDuration(), false);

                if (gp.getEntityM().getEntityById(entityId).getLife() < 1) {

                    gp.getEntityM().getEntityById(entityId).setLife(1);
                }
            }

            if (gp.getEntityM().getEntityById(entityId).getActiveFadeEffect() != FadeEffectType.NONE) {

                reviveAnimationsComplete = false;
                break;
            }
        }

        if (reviveAnimationsComplete) {

            if (sraBackDelay > 0) {

                sraBackDelay -= dt;
            }

            if (sraBackDelay <= 0) {

                resetStandardReviveAnimation();

                if (!ceaWaitToProgressCombat) {

                    gp.getCombatM().progressCombat();
                }
            }
        }
    }


    /**
     * Updates the state of the active custom effect animation by one frame.
     *
     * @param dt time since last frame (seconds)
     */
    private void updateCustomEffectAnimation(double dt) {

        boolean healthBarsComplete = progressAndCheckHealthBarAnimations(
                ceaEntitiesFinalLife,
                ceaEntitiesDamageRemainder,
                ceaEntitiesLifeIncrease,
                ceaHealthBarSpeed, dt);                                                                                 // Check if life bar animation for each target entity is complete.

        boolean particleEffectsComplete = checkParticleEffectAnimations(ceaParticleEffectUuids);                        // Check if all particle effect animations are complete.

        if (healthBarsComplete && particleEffectsComplete) {

            if (ceaBackDelay > 0) {

                ceaBackDelay -= dt;
            }

            if (ceaBackDelay <= 0) {

                if ((!ceaWaitToProgressCombat) || (ceaWaitToProgressCombat
                        && !standardMoveAnimationActive
                        && !standardPartySwapAnimationActive
                        && !standardFaintAnimationActive
                        && !standardReviveAnimationActive)) {                                                           // Check if waiting for all other active animations to complete before progressing combat.

                    gp.getCombatM().pollFainting();                                                                     // Check whether any entities fainted as a result of this move; appropriate actions will be queued if so.
                    resetCustomEffectAnimation();
                    gp.getCombatM().progressCombat();
                }
            }
        }
    }


    /**
     * Updates and determines whether all health bar animations are complete or not.
     *
     * @param targetEntitiesFinalLife calculated final life points of each targeted entity after applying
     *                                damage/restoration for an animation; entity ID is the key, life points is the
     *                                value
     * @param targetEntitiesDamageRemainder non-whole number remainder of target entity life points to be carried over
     *                                      to the next update during an animation; entity ID is the key, life is the
     *                                      value
     * @param targetEntitiesLifeIncrease Indicates whether an entity's life will increase (true) or decrease (false) as
     *                                   a result of an animation; entity ID is the key, life increase/decrease is the
     *                                   value
     * @param healthBarSpeed number of life points that an entity will gain/lose per second during an animation
     * @param dt time since last frame (seconds)
     * @return whether all health bar animations are complete (true) or not (false)
     */
    private boolean progressAndCheckHealthBarAnimations(HashMap<Integer, Integer> targetEntitiesFinalLife,
                                                        HashMap<Integer, Double> targetEntitiesDamageRemainder,
                                                        HashMap<Integer, Boolean> targetEntitiesLifeIncrease,
                                                        double healthBarSpeed, double dt) {

        int lifeChangeCandidate;
        boolean healthBarsComplete = true;

        for (int entityId : targetEntitiesFinalLife.keySet()) {                                                         // Check if life bar animation for each target entity is complete.

            if (targetEntitiesLifeIncrease.get(entityId)) {

                if (targetEntitiesFinalLife.get(entityId) >= gp.getEntityM().getEntityById(entityId).getLife()) {       // Update target entity life if not already at final value.

                    targetEntitiesDamageRemainder.put(
                            entityId,
                            targetEntitiesDamageRemainder.get(entityId) + (healthBarSpeed * dt));

                    lifeChangeCandidate = (int)Math.floor(targetEntitiesDamageRemainder.get(entityId));

                    if (((gp.getEntityM().getEntityById(entityId).getLife() + lifeChangeCandidate)
                            >= targetEntitiesFinalLife.get(entityId))
                            || ((gp.getEntityM().getEntityById(entityId).getLife() + lifeChangeCandidate)
                            > gp.getEntityM().getEntityById(entityId).getMaxLife())) {

                        gp.getEntityM().getEntityById(entityId).setLife(targetEntitiesFinalLife.get(entityId));
                    } else {

                        gp.getEntityM().getEntityById(entityId).addLife(lifeChangeCandidate);
                        healthBarsComplete = false;                                                                     // A life bar was found that has not completed its animation.
                    }
                    targetEntitiesDamageRemainder.put(
                            entityId,
                            targetEntitiesDamageRemainder.get(entityId) - lifeChangeCandidate);
                }
            } else {

                if (targetEntitiesFinalLife.get(entityId) <= gp.getEntityM().getEntityById(entityId).getLife()) {       // Update target entity life if not already at final value.

                    targetEntitiesDamageRemainder.put(
                            entityId,
                            targetEntitiesDamageRemainder.get(entityId) + (healthBarSpeed * dt));

                    lifeChangeCandidate = (int)Math.floor(targetEntitiesDamageRemainder.get(entityId));

                    if (((gp.getEntityM().getEntityById(entityId).getLife() - lifeChangeCandidate)
                            <= targetEntitiesFinalLife.get(entityId))
                            || ((gp.getEntityM().getEntityById(entityId).getLife() - lifeChangeCandidate) < 0)) {

                        gp.getEntityM().getEntityById(entityId).setLife(targetEntitiesFinalLife.get(entityId));
                    } else {

                        gp.getEntityM().getEntityById(entityId).subtractLife(lifeChangeCandidate);
                        healthBarsComplete = false;                                                                     // A life bar was found that has not completed its animation.
                    }
                    targetEntitiesDamageRemainder.put(
                            entityId,
                            targetEntitiesDamageRemainder.get(entityId) - lifeChangeCandidate);
                }
            }
        }
        return healthBarsComplete;
    }


    /**
     * Determines whether all particle effect animations are complete or not.
     *
     * @param particleEffectUuids UUIDs of particle effects being animated
     * @return whether all particle effect animations are complete (true) or not (false)
     */
    private boolean checkParticleEffectAnimations(ArrayList<UUID> particleEffectUuids) {

        boolean particleEffectsComplete = true;

        for (UUID uuid : particleEffectUuids) {

            if (gp.getParticleEffectM().getParticleEffectByUuid(uuid) != null) {

                particleEffectsComplete = false;
                break;
            }
        }
        return particleEffectsComplete;
    }


    /**
     * Resets CombatAnimationSupport standard move animation fields back to their default state.
     * Intended to be called to clean up after a standard move animation has completed.
     */
    private void resetStandardMoveAnimation() {

        standardMoveAnimationActive = false;
        smaSourceEntityId = 0;
        smaMove = null;
        smaTargetEntitiesFinalLife.clear();
        smaTargetEntitiesDamageRemainder.clear();
        smaTargetEntitiesLifeIncrease.clear();
        smaParticleEffectUuids.clear();
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


    /**
     * Resets CombatAnimationSupport standard revive animation fields back to their default state.
     * Intended to be called to clean up after a standard revive animation has completed.
     */
    private void resetStandardReviveAnimation() {

        standardReviveAnimationActive = false;
        sraRevivingEntityIds.clear();
        sraFrontDelay = 0;
        sraBackDelay = 0;
    }


    /**
     * Resets CombatAnimationSupport custom effect animation fields back to their default state.
     * Intended to be called to clean up after a custom effect animation has completed.
     */
    public void resetCustomEffectAnimation() {

        customEffectAnimationActive = false;
        ceaEntityIds.clear();
        ceaEntitiesFinalLife.clear();
        ceaEntitiesDamageRemainder.clear();
        ceaEntitiesLifeIncrease.clear();
        ceaEntitiesFinalSkillPoints.clear();
        ceaParticleEffectColor.x = 255;
        ceaParticleEffectColor.y = 255;
        ceaParticleEffectColor.z = 255;
        ceaParticleEffectUuids.clear();
        ceaSoundEffectResourceName = "";
        ceaWaitToProgressCombat = false;
    }


    // GETTERS
    public boolean isAnimationActive() {
        return (standardMoveAnimationActive
                && standardPartySwapAnimationActive
                && standardFaintAnimationActive
                && standardReviveAnimationActive
                && customEffectAnimationActive);
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

    public boolean isStandardReviveAnimationActive() {
        return standardReviveAnimationActive;
    }

    public boolean isCustomEffectAnimationActive() {
        return customEffectAnimationActive;
    }
}
