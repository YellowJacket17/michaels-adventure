package combat.implementation.move;

import combat.MoveBase;
import combat.enumeration.MoveCategory;
import combat.enumeration.MoveTargets;
import combat.implementation.action.Act_CustomEffect;
import combat.implementation.action.Act_ReadMessage;
import core.GamePanel;
import entity.EntityBase;
import org.joml.Vector3f;
import utility.UtilityTool;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * This class defines a move (Vitality Drain).
 */
public class Mve_VitalityDrain extends MoveBase {

    // FIELDS
    private static final int mveId = 14;
    private static final String mveName = "Vitality Drain";
    private static final String mveDescription = "Steals HP from the target. Evenly restores half of damage dealt among active allies (including self).";
    private static final int mvePower = 60;
    private static final int mveAccuracy = 100;
    private static final int mveSkillPoints = 6;
    private static final Vector3f mveEffectColor = MoveBase.MAGIC_MOVE_COLOR;
    private static final String mveSoundEffect = "vitalityDrain";


    // CONSTRUCTOR
    public Mve_VitalityDrain(GamePanel gp) {
        super(gp, mveId, MoveCategory.MAGIC, MoveTargets.OPPONENT, false);
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

        ArrayList<EntityBase> allyEntities = gp.getCombatM().retrieveAllyEntities(sourceEntityId);
        HashMap<Integer, Integer> allyEntitiesFinalLife = new HashMap<>();
        int totalTargetEntityDeltaLife = 0;

        for (int deltaLife : targetEntityDeltaLife.values()) {

            totalTargetEntityDeltaLife += deltaLife;
        }
        int lifeGainPerAlly = ((int)Math.floor(((float)totalTargetEntityDeltaLife / 2) / (allyEntities.size() + 1)));   // Added one to include the source entity.

        if (lifeGainPerAlly <= 0) {

            lifeGainPerAlly = 1;                                                                                        // Guarantee that at least one life point is restored per ally.
        }

        if (gp.getEntityM().getEntityById(sourceEntityId).getLife()
                != gp.getEntityM().getEntityById(sourceEntityId).getMaxLife()) {

            allyEntitiesFinalLife.put(sourceEntityId,
                    gp.getEntityM().getEntityById(sourceEntityId).getLife() + lifeGainPerAlly);
        }

        for (EntityBase allyEntity : allyEntities) {

            if (allyEntity.getLife() != allyEntity.getMaxLife()) {

                allyEntitiesFinalLife.put(allyEntity.getEntityId(), allyEntity.getLife() + lifeGainPerAlly);
            }
        }

        if (!allyEntitiesFinalLife.isEmpty()) {

            gp.getCombatM().addQueuedActionBack(
                    new Act_CustomEffect(gp, allyEntitiesFinalLife, false,
                            MoveBase.ATTRIBUTE_INCREASE_COLOR, "heal", true));
            String message = buildEffectMessage(allyEntitiesFinalLife);
            gp.getCombatM().addQueuedActionBack(
                    new Act_ReadMessage(gp, message, true, true));
        }
    }


    /**
     * Builds message for heal effect.
     *
     * @param entitiesFinalLife map of final entities life (entity ID is the key, life points is the value)
     * @return message
     */
    private String buildEffectMessage(HashMap<Integer, Integer> entitiesFinalLife) {

        ArrayList<String> targetEntityNames = new ArrayList<>();

        for (int entityId : entitiesFinalLife.keySet()) {

            targetEntityNames.add(gp.getEntityM().getEntityById(entityId).getName());
        }
        String message = UtilityTool.buildEntityListMessage(targetEntityNames, false);
        message += " restored health!";
        return message;
    }
}
