package combat.implementation.move;

import combat.MoveBase;
import combat.enumeration.MoveCategory;
import combat.enumeration.MoveTargets;
import combat.implementation.action.Act_CustomEffect;
import combat.implementation.action.Act_ReadMessage;
import core.GamePanel;
import entity.enumeration.EntityStatus;
import org.joml.Vector3f;
import utility.UtilityTool;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * This class defines a move (Revitalize).
 */
public class Mve_Revitalize extends MoveBase {

    // FIELDS
    private static final int mveId = 10;
    private static final String mveName = "Revitalize";
    private static final String mveDescription = "Provides powerful healing magic to a fallen ally, reviving and restoring 25% of their health.";
    private static final int mvePower = 0;
    private static final int mveAccuracy = 100;
    private static final int mveSkillPoints = 9;
    private static final Vector3f mveEffectColor = new Vector3f(166, 255, 219);
    private static final String mveSoundEffect = "revitalize";


    // CONSTRUCTOR
    public Mve_Revitalize(GamePanel gp) {
        super(gp, mveId, MoveCategory.SUPPORT, MoveTargets.ALLY, false);
        name = mveName;
        description = mveDescription;
        power = mvePower;
        accuracy = mveAccuracy;
        skillPoints = mveSkillPoints;
        effectColor = mveEffectColor;
        soundEffect = mveSoundEffect;
    }


    // METHODS
    @Override
    public void runEffects(int sourceEntityId, HashMap<Integer, Integer> targetEntityDeltaLife) {

        ArrayList<Integer> affectedTargetEntityIds = new ArrayList<>();
        ArrayList<Integer> targetEntityIds = UtilityTool.extractKeySetAsArrayList(targetEntityDeltaLife);

        for (int targetEntityId : targetEntityIds) {

            if (gp.getEntityM().getEntityById(targetEntityId).getStatus() == EntityStatus.FAINT) {

                affectedTargetEntityIds.add(targetEntityId);
            }
        }

        if (affectedTargetEntityIds.size() > 0) {

            HashMap<Integer, Integer> entitiesFinalSkillPoints = new HashMap<>();
            entitiesFinalSkillPoints.put(
                    sourceEntityId,
                    gp.getEntityM().getEntityById(sourceEntityId).getSkill() - skillPoints);
            gp.getCombatM().addQueuedActionBack(
                    new Act_CustomEffect(gp, entitiesFinalSkillPoints, effectColor, soundEffect, true));
            gp.getCombatAnimationS().initiateStandardReviveAnimation(targetEntityIds, 0.4, 0.4);
            String message = buildEffectMessage(targetEntityIds);
            gp.getCombatM().addQueuedActionBack(
                    new Act_ReadMessage(gp, message, true, true));
        }
    }


    @Override
    public boolean verifyTarget(int entityId) {

        if (gp.getEntityM().getEntityById(entityId).getStatus() == EntityStatus.FAINT) {

            return true;
        }
        return false;
    }


    /**
     * Builds message for revive effect.
     *
     * @param targetEntityIds IDs of affected entities
     * @return message
     */
    private String buildEffectMessage(ArrayList<Integer> targetEntityIds) {

        ArrayList<String> targetEntityNames = new ArrayList<>();

        for (int entityId : targetEntityIds) {

            targetEntityNames.add(gp.getEntityM().getEntityById(entityId).getName());
        }
        String message = UtilityTool.buildEntityListMessage(targetEntityNames, false);
        message += (targetEntityNames.size() > 1) ? " are" : " is";
        message += " back in the fight!";
        return message;
    }
}
