package combat.implementation.action;

import combat.ActionBase;
import core.GamePanel;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * This class defines a combat action (custom effect animation).
 */
public class Act_CustomEffect extends ActionBase {

    // FIELDS
    /**
     * ID of entities participating in the animation.
     */
    private final ArrayList<Integer> entityIds = new ArrayList<>();

    /**
     * Calculated final life points of each entity participating in the animation; entity ID is the key, life points is
     * the value.
     */
    private final HashMap<Integer, Integer> entitiesFinalLifePoints = new HashMap<>();

    /**
     * Calculated final skill points of each participating entity; entity ID is the key, skill points is the value.
     */
    private final HashMap<Integer, Integer> entitiesFinalSkillPoints = new HashMap<>();

    /**
     * Particle effect color (r, g, b).
     */
    private final Vector3f particleEffectColor = new Vector3f();

    /**
     * Sound effect resource name.
     */
    private final String soundEffectResourceName;

    /**
     * whether to wait to hand off control to the next queued combat action until all other running animations in the
     * CombatAnimationSupport class are complete (true) or not (false, i.e., just progress combat after this animation
     * is complete).
     * The former allows multiple animations to run concurrently.
     */
    private final boolean waitToProgressCombat;


    // CONSTRUCTORS
    public Act_CustomEffect(GamePanel gp, ArrayList<Integer> entityIds, Vector3f particleEffectColor,
                            String soundResourceName, boolean waitToProgressCombat) {
        super(gp);
        for (int entityId : entityIds) {this.entityIds.add(entityId);}
        this.particleEffectColor.x = particleEffectColor.x;
        this.particleEffectColor.y = particleEffectColor.y;
        this.particleEffectColor.z = particleEffectColor.z;
        this.soundEffectResourceName = soundResourceName;
        this.waitToProgressCombat = waitToProgressCombat;
    }


    public Act_CustomEffect(GamePanel gp, HashMap<Integer, Integer> entitiesFinalSkillPoints,
                            Vector3f particleEffectColor, String soundResourceName, boolean waitToProgressCombat) {
        super(gp);
        for (int entityId : entitiesFinalSkillPoints.keySet()) {
            this.entityIds.add(entityId);
            this.entitiesFinalSkillPoints.put(entityId, entitiesFinalSkillPoints.get(entityId));
        }
        this.particleEffectColor.x = particleEffectColor.x;
        this.particleEffectColor.y = particleEffectColor.y;
        this.particleEffectColor.z = particleEffectColor.z;
        this.soundEffectResourceName = soundResourceName;
        this.waitToProgressCombat = waitToProgressCombat;
    }


    public Act_CustomEffect(GamePanel gp, HashMap<Integer, Integer> entitiesFinalLifePoints,
                            HashMap<Integer, Integer> entitiesFinalSkillPoints, Vector3f particleEffectColor,
                            String soundResourceName, boolean waitToProgressCombat) {
        super(gp);
        for (int entityId : entitiesFinalLifePoints.keySet()) {
            this.entityIds.add(entityId);
            this.entitiesFinalLifePoints.put(entityId, entitiesFinalLifePoints.get(entityId));
        }
        for (int entityId : entitiesFinalSkillPoints.keySet()) {
            this.entitiesFinalSkillPoints.put(entityId, entitiesFinalSkillPoints.get(entityId));
        }
        this.particleEffectColor.x = particleEffectColor.x;
        this.particleEffectColor.y = particleEffectColor.y;
        this.particleEffectColor.z = particleEffectColor.z;
        this.soundEffectResourceName = soundResourceName;
        this.waitToProgressCombat = waitToProgressCombat;
    }


    // METHOD
    @Override
    public void run() {

        if (entitiesFinalLifePoints.isEmpty() && entitiesFinalSkillPoints.isEmpty()) {

            gp.getCombatAnimationS().initiateCustomEffectAnimation(
                    entityIds, particleEffectColor, soundEffectResourceName, waitToProgressCombat, 0.4, 0.4);

        } else if (entitiesFinalSkillPoints.isEmpty()) {

            gp.getCombatAnimationS().initiateCustomEffectAnimation(
                    entitiesFinalLifePoints, false, particleEffectColor, soundEffectResourceName, waitToProgressCombat, 0.4, 0.4);
        } else if (entitiesFinalLifePoints.isEmpty()) {

            gp.getCombatAnimationS().initiateCustomEffectAnimation(
                    entitiesFinalSkillPoints, true, particleEffectColor, soundEffectResourceName, waitToProgressCombat, 0.4, 0.4);
        } else {

            gp.getCombatAnimationS().initiateCustomEffectAnimation(
                    entitiesFinalLifePoints, entitiesFinalSkillPoints, particleEffectColor, soundEffectResourceName, waitToProgressCombat, 0.4, 0.4);
        }

        // NOTE: The `progressCombat()` method in CombatManager to hand off control to the next queued action will
        // automatically be called once this effect animation in the `updateCustomEffectAnimation()` method in
        // CombatAnimationSupport is complete, hence why it is not called here.
    }
}
