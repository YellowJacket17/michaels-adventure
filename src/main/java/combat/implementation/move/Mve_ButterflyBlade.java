package combat.implementation.move;

import combat.MoveBase;
import combat.enumeration.MoveCategory;
import combat.enumeration.MoveTargets;
import combat.implementation.action.Act_ReadMessage;
import core.GamePanel;
import entity.EntityBase;
import org.joml.Vector3f;

import java.util.ArrayList;

/**
 * This class defines a move (Butterfly Blade).
 */
public class Mve_ButterflyBlade extends MoveBase {

    // FIELDS
    private static final int mveId = 2;
    private static final String mveName = "Butterfly Blade";
    private static final String mveDescription = "Slashes at the target with a knife. Also lowers defense";
    private static final int mvePower = 60;
    private static final int mveAccuracy = 95;
    private static final int mveSkillPoints = 4;
    private static final Vector3f mveParticleEffectColor = new Vector3f(166, 208, 255);
    private static final String mveSoundEffect = "butterflyBlade";


    // CONSTRUCTOR
    public Mve_ButterflyBlade(GamePanel gp) {
        super(gp, mveId, MoveCategory.PHYSICAL, MoveTargets.OPPONENT, false);
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

        EntityBase targetEntity;
        ArrayList<Integer> affectedTargetEntityIds = new ArrayList<>();

        for (int targetEntityId : targetEntityIds) {

            targetEntity = gp.getEntityM().getEntityById(targetEntityId);

            if (targetEntity.changeDefenseStage(-1)) {

                affectedTargetEntityIds.add(targetEntityId);
            }
        }

        if (affectedTargetEntityIds.size() > 0) {

            gp.getCombatAnimationS().initiateCustomEffectAnimation(
                    targetEntityIds,
                    new Vector3f(255, 166, 190),
                    "attributeDecrease",
                    true,
                    0.4,
                    0.4);
            String message = buildEffectMessage(affectedTargetEntityIds);
            gp.getCombatM().addQueuedActionBack(
                    new Act_ReadMessage(gp, message, true, true));
        }
    }


    /**
     * Builds message for lowered defense effect.
     *
     * @param targetEntityIds IDs of affected entities
     * @return message
     */
    private String buildEffectMessage(ArrayList<Integer> targetEntityIds) {

        String build = "";
        int i = 0;

        for (int entityId : targetEntityIds) {

            if (i == (targetEntityIds.size() - 1)) {

                if (i > 0) {

                    build += "and ";
                }
                build += gp.getEntityM().getEntityById(entityId).getName() + "'s";
            } else {

                build += gp.getEntityM().getEntityById(entityId).getName();

                if (targetEntityIds.size() > 2) {

                    build += ", ";
                } else {

                    build += " ";
                }
            }
            i++;
        }
        build += " defense fell!";
        return build;
    }
}
