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
 * This class defines a move (Healing Sparks).
 */
public class Mve_HealingSparks extends MoveBase {

    // FIELDS
    private static final int mveId = 9;
    private static final String mveName = "Healing Sparks";
    private static final String mveDescription = "Provides healing magic to an ally, restoring 30% of their health.";
    private static final int mvePower = 0;
    private static final int mveAccuracy = 100;
    private static final int mveSkillPoints = 6;
    private static final Vector3f mveEffectColor = MoveBase.ATTRIBUTE_INCREASE_COLOR;
    private static final String mveSoundEffect = "healingSparks";


    // CONSTRUCTOR
    public Mve_HealingSparks(GamePanel gp) {
        super(gp, mveId, MoveCategory.SUPPORT, MoveTargets.ALLY_SELF, false);
        name = mveName;
        description = mveDescription;
        power = mvePower;
        accuracy = mveAccuracy;
        skillPoints = mveSkillPoints;
        effectColor = mveEffectColor;
        soundEffect = mveSoundEffect;
    }


    // METHOD
    @Override
    public void runEffects(int sourceEntityId, HashMap<Integer, Integer> targetEntityDeltaLife) {

        ArrayList<Integer> affectedTargetEntityIds = new ArrayList<>();
        ArrayList<Integer> targetEntityIds = UtilityTool.extractKeySetAsArrayList(targetEntityDeltaLife);
        HashMap<Integer, Integer> targetEntitiesFinalLife = new HashMap<>();
        int calculatedFinalLife;

        for (int targetEntityId : targetEntityIds) {

            calculatedFinalLife = (int)((0.3f) * Math.ceil(gp.getEntityM().getEntityById(targetEntityId).getMaxLife()))
                    + gp.getEntityM().getEntityById(targetEntityId).getLife();

            if (gp.getEntityM().getEntityById(targetEntityId).getLife()
                    < gp.getEntityM().getEntityById(targetEntityId).getMaxLife()) {

                if (calculatedFinalLife
                        > gp.getEntityM().getEntityById(targetEntityId).getMaxLife()) {

                    calculatedFinalLife = gp.getEntityM().getEntityById(targetEntityId).getMaxLife();
                }
                targetEntitiesFinalLife.put(targetEntityId, calculatedFinalLife);
                affectedTargetEntityIds.add(targetEntityId);
            }
        }

        if (affectedTargetEntityIds.size() > 0) {

            HashMap<Integer, Integer> entitiesFinalSkillPoints = new HashMap<>();
            entitiesFinalSkillPoints.put(
                    sourceEntityId,
                    gp.getEntityM().getEntityById(sourceEntityId).getSkill() - skillPoints);
            gp.getCombatM().addQueuedActionBack(
                    new Act_CustomEffect(gp, targetEntitiesFinalLife, entitiesFinalSkillPoints, effectColor, soundEffect, true));
            String message = buildEffectMessage(affectedTargetEntityIds);
            gp.getCombatM().addQueuedActionBack(
                    new Act_ReadMessage(gp, message, true, true));
        }
    }


    @Override
    public boolean verifyTarget(int targetEntityId) {

        if ((gp.getEntityM().getEntityById(targetEntityId).getStatus() != EntityStatus.FAINT)
                && (gp.getEntityM().getEntityById(targetEntityId).getLife()
                    < gp.getEntityM().getEntityById(targetEntityId).getMaxLife())) {

            return true;
        }
        return false;
    }


    /**
     * Builds message for heal effect.
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
        message += " restored health!";
        return message;
    }
}
