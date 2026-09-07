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
import java.util.Random;

/**
 * This class defines a move (Shadow Power).
 */
public class Mve_ShadowPower extends MoveBase {

    // FIELDS
    private static final int mveId = 18;
    private static final String mveName = "Shadow Power";
    private static final String mveDescription = "Draws power from the shadows. Raises attack, defense, magic, or agility.";
    private static final int mvePower = 0;
    private static final int mveAccuracy = 100;
    private static final int mveSkillPoints = 1;
    private static final Vector3f mveEffectColor = MoveBase.SUPPORT_MOVE_COLOR;
    private static final String mveSoundEffect = "shadowPower";


    // CONSTRUCTOR
    public Mve_ShadowPower(GamePanel gp) {
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
        ArrayList<Integer> possibleAttributes = new ArrayList<>();
        Random random = new Random();
        int selectedIndex;

        for (int targetEntityId : targetEntityIds) {

            if (gp.getEntityM().getEntityById(targetEntityId).getAttackBuff() != EntityBase.MAX_ATTACK_BUFF) {

                possibleAttributes.add(0);
            }

            if (gp.getEntityM().getEntityById(targetEntityId).getDefenseBuff() != EntityBase.MAX_DEFENSE_BUFF) {

                possibleAttributes.add(1);
            }

            if (gp.getEntityM().getEntityById(targetEntityId).getMagicBuff() != EntityBase.MAX_MAGIC_BUFF) {

                possibleAttributes.add(2);
            }

            if (gp.getEntityM().getEntityById(targetEntityId).getAgilityBuff() != EntityBase.MAX_AGILITY_BUFF) {

                possibleAttributes.add(3);
            }

            if (!possibleAttributes.isEmpty()) {

                selectedIndex = random.nextInt(possibleAttributes.size());                                              // Generate random number from 0 to number of possible attributes minus one (both inclusive).
                affectedTargetEntityIds.add(targetEntityId);
                affectedTargetEntityAttributes.put(targetEntityId, possibleAttributes.get(selectedIndex));
            }
            possibleAttributes.clear();
        }
        gp.getCombatM().addQueuedActionBack(
                new Act_CustomEffect(gp, targetEntityIds, effectColor, soundEffect, true));

        if (affectedTargetEntityIds.size() > 0) {

            for (int targetEntityId : affectedTargetEntityIds) {

                switch(affectedTargetEntityAttributes.get(targetEntityId)) {
                    case 0:
                        gp.getEntityM().getEntityById(targetEntityId).changeAttackStage(1);
                        break;
                    case 1:
                        gp.getEntityM().getEntityById(targetEntityId).changeDefenseStage(1);
                        break;
                    case 2:
                        gp.getEntityM().getEntityById(targetEntityId).changeMagicStage(1);
                        break;
                    case 3:
                        gp.getEntityM().getEntityById(targetEntityId).changeAgilityStage(1);
                        break;
                }
            }
            gp.getCombatM().addQueuedActionBack(
                    new Act_CustomEffect(gp, affectedTargetEntityIds,
                            MoveBase.ATTRIBUTE_INCREASE_COLOR, "attributeIncrease", true));
            String message = "";

            for (int targetEntityId : affectedTargetEntityIds) {

                switch(affectedTargetEntityAttributes.get(targetEntityId)) {
                    case 0:
                        message = buildEffectMessage(targetEntityId, "attack");
                        break;
                    case 1:
                        message = buildEffectMessage(targetEntityId, "defense");
                        break;
                    case 2:
                        message = buildEffectMessage(targetEntityId, "magic");
                        break;
                    case 3:
                        message = buildEffectMessage(targetEntityId, "agility");
                        break;
                }
                gp.getCombatM().addQueuedActionBack(new Act_ReadMessage(gp, message, true, true));
            }
        } else {

            String message = name + " failed!";
            gp.getCombatM().addQueuedActionBack(new Act_ReadMessage(gp, message, true, true));
        }
    }


    @Override
    public boolean verifyTarget(int targetEntityId) {

        if ((gp.getEntityM().getEntityById(targetEntityId).getStatus() != EntityStatus.FAINT)
                && !((gp.getEntityM().getEntityById(targetEntityId).getAttackBuff() == EntityBase.MAX_ATTACK_BUFF)
                && (gp.getEntityM().getEntityById(targetEntityId).getDefenseBuff() == EntityBase.MAX_DEFENSE_BUFF)
                && (gp.getEntityM().getEntityById(targetEntityId).getMagicBuff() == EntityBase.MAX_MAGIC_BUFF)
                && (gp.getEntityM().getEntityById(targetEntityId).getAgilityBuff() == EntityBase.MAX_AGILITY_BUFF))) {

            return true;
        }
        return false;
    }


    /**
     * Builds message for increased attribute effect.
     *
     * @param targetEntityId ID of affected entity
     * @param attribute affected attribute
     * @return message
     */
    private String buildEffectMessage(int targetEntityId, String attribute) {

        String message =
                UtilityTool.appendEntityNameApostropheS(gp.getEntityM().getEntityById(targetEntityId).getName());
        message += " " + attribute;
        message += " rose!";
        return message;
    }
}