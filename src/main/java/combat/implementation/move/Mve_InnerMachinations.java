package combat.implementation.move;

import combat.MoveBase;
import combat.enumeration.MoveCategory;
import combat.enumeration.MoveTargets;
import combat.implementation.action.Act_CustomEffect;
import combat.implementation.action.Act_ReadMessage;
import core.GamePanel;
import entity.EntityBase;
import entity.enumeration.EntityStatus;
import org.joml.Vector3f;
import utility.UtilityTool;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * This class defines a move (Inner Machinations).
 */
public class Mve_InnerMachinations extends MoveBase {

    // FIELDS
    private static final int mveId = 17;
    private static final String mveName = "Inner Machinations";
    private static final String mveDescription = "Spaces out the user for a moment before snapping them back to reality. Raises attack and agility.";
    private static final int mvePower = 0;
    private static final int mveAccuracy = 100;
    private static final int mveSkillPoints = 3;
    private static final Vector3f mveEffectColor = MoveBase.SUPPORT_MOVE_COLOR;
    private static final String mveSoundEffect = "innerMachinations";


    // CONSTRUCTOR
    public Mve_InnerMachinations(GamePanel gp) {
        super(gp, mveId, MoveCategory.SUPPORT, MoveTargets.SELF, false);
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
        HashMap<Integer, Integer> affectedTargetEntityAttributes = new HashMap<>();
        ArrayList<Integer> targetEntityIds = UtilityTool.extractKeySetAsArrayList(targetEntityDeltaLife);

        for (int targetEntityId : targetEntityIds) {

            if ((gp.getEntityM().getEntityById(targetEntityId).getAttackBuff() != EntityBase.MAX_ATTACK_BUFF)
                    && (gp.getEntityM().getEntityById(targetEntityId).getAgilityBuff() != EntityBase.MAX_AGILITY_BUFF)) {

                gp.getEntityM().getEntityById(targetEntityId).changeAttackStage(1);
                gp.getEntityM().getEntityById(targetEntityId).changeAgilityStage(1);
                affectedTargetEntityIds.add(targetEntityId);
                affectedTargetEntityAttributes.put(targetEntityId, 0);
            } else if (gp.getEntityM().getEntityById(targetEntityId).getAttackBuff() != EntityBase.MAX_ATTACK_BUFF) {

                gp.getEntityM().getEntityById(targetEntityId).changeAttackStage(1);
                affectedTargetEntityIds.add(targetEntityId);
                affectedTargetEntityAttributes.put(targetEntityId, 1);
            } else if (gp.getEntityM().getEntityById(targetEntityId).getAgilityBuff() != EntityBase.MAX_AGILITY_BUFF) {

                gp.getEntityM().getEntityById(targetEntityId).changeAgilityStage(1);
                affectedTargetEntityIds.add(targetEntityId);
                affectedTargetEntityAttributes.put(targetEntityId, 2);
            }
        }
        gp.getCombatM().addQueuedActionBack(
                new Act_CustomEffect(gp, targetEntityIds, effectColor, soundEffect, true));

        if (affectedTargetEntityIds.size() > 0) {

            gp.getCombatM().addQueuedActionBack(
                    new Act_CustomEffect(gp, affectedTargetEntityIds,
                            MoveBase.ATTRIBUTE_INCREASE_COLOR, "attributeIncrease", true));
            String message = "";

            for (int targetEntityId : affectedTargetEntityIds) {

                switch(affectedTargetEntityAttributes.get(targetEntityId)) {
                    case 0:
                        message = buildAttackAgilityEffectMessage(targetEntityId);
                        break;
                    case 1:
                        message = buildAttackEffectMessage(targetEntityId);
                        break;
                    case 2:
                        message = buildAgilityEffectMessage(targetEntityId);
                        break;
                }
                gp.getCombatM().addQueuedActionBack(new Act_ReadMessage(gp, message, true, true));
            }
        }
    }


    @Override
    public boolean verifyTarget(int targetEntityId) {

        if ((gp.getEntityM().getEntityById(targetEntityId).getStatus() != EntityStatus.FAINT)
                && !((gp.getEntityM().getEntityById(targetEntityId).getAttackBuff() == EntityBase.MAX_ATTACK_BUFF)
                && (gp.getEntityM().getEntityById(targetEntityId).getAgilityBuff() == EntityBase.MAX_AGILITY_BUFF))) {

            return true;
        }
        return false;
    }


    /**
     * Builds message for increased attack and agility effect.
     *
     * @param targetEntityId ID of affected entity
     * @return message
     */
    private String buildAttackAgilityEffectMessage(int targetEntityId) {

        String message =
                UtilityTool.appendEntityNameApostropheS(gp.getEntityM().getEntityById(targetEntityId).getName());
        message += " attack and agility rose!";
        return message;
    }


    /**
     * Builds message for increased attack effect.
     *
     * @param targetEntityId ID of affected entity
     * @return message
     */
    private String buildAttackEffectMessage(int targetEntityId) {

        String message =
                UtilityTool.appendEntityNameApostropheS(gp.getEntityM().getEntityById(targetEntityId).getName());
        message += " attack rose!";
        return message;
    }


    /**
     * Builds message for increased agility effect.
     *
     * @param targetEntityId ID of affected entity
     * @return message
     */
    private String buildAgilityEffectMessage(int targetEntityId) {

        String message =
                UtilityTool.appendEntityNameApostropheS(gp.getEntityM().getEntityById(targetEntityId).getName());
        message += " agility rose!";
        return message;
    }
}
