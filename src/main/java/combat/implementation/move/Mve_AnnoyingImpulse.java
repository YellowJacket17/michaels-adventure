package combat.implementation.move;

import combat.MoveBase;
import combat.enumeration.MoveCategory;
import combat.enumeration.MoveTargets;
import combat.implementation.action.Act_ReadMessage;
import core.GamePanel;
import org.joml.Vector3f;

import java.util.ArrayList;

/**
 * This class defines a move (Earthquake).
 */
public class Mve_AnnoyingImpulse extends MoveBase {

    // FIELDS
    private static final int mveId = 5;
    private static final String mveName = "Annoying Impulse";
    private static final String mveDescription = "Directs all attacks for the next turn towards the user. Raises defense.";
    private static final int mvePower = 60;
    private static final int mveAccuracy = 95;
    private static final int mveSkillPoints = 6;
    private static final Vector3f mveParticleEffectColor = new Vector3f(176, 123, 123);
    private static final String mveSoundEffect = "butterflyBlade";


    // CONSTRUCTOR
    public Mve_AnnoyingImpulse(GamePanel gp) {
        super(gp, mveId, MoveCategory.SUPPORT, MoveTargets.SELF, true);
        name = mveName;
        description = mveDescription;
        power = mvePower;
        accuracy = mveAccuracy;
        skillPoints = mveSkillPoints;
        particleEffectColor = mveParticleEffectColor;
        soundEffect = mveSoundEffect;
    }


    // METHOD
    @Override
    public void runEffects(int sourceEntityId, ArrayList<Integer> targetEntityIds) {

        gp.getCombatM().setTargetLockEntityId(sourceEntityId);
        gp.getCombatM().setTargetLockTurns(1);

        if (gp.getEntityM().getEntityById(sourceEntityId).changeDefenseStage(1)) {

            gp.getCombatAnimationS().initiateCustomEffectAnimation(
                    targetEntityIds,
                    new Vector3f(166, 255, 168),
                    "heal",
                    true,
                    0.4,
                    0.4);
            String message = buildEffectMessageDefenseIncrease(sourceEntityId);
            gp.getCombatM().addQueuedActionBack(
                    new Act_ReadMessage(gp, message, true, true));
        }
        String message = buildEffectMessageTargetLock(sourceEntityId);
        gp.getCombatM().addQueuedActionBack(
                new Act_ReadMessage(gp, message, true, true));
    }


    /**
     * Builds message for increased defense effect.
     *
     * @param entityId ID of affected entity
     * @return message
     */
    private String buildEffectMessageDefenseIncrease(int entityId) {

        return (gp.getEntityM().getEntityById(entityId).getName() + "'s defense rose!");
    }


    /**
     * Builds message for locked on entity.
     *
     * @param entityId ID of entity locked onto.
     * @return message
     */
    private String buildEffectMessageTargetLock(int entityId) {

        return (gp.getEntityM().getEntityById(entityId).getName() + " is now the center of attention!");
    }
}
