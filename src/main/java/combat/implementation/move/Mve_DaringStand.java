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
 * This class defines a move (Daring Stand).
 */
public class Mve_DaringStand extends MoveBase {

    // FIELDS
    private static final int mveId = 5;
    private static final String mveName = "Daring Stand";
    private static final String mveDescription = "Directs all attacks for the next turn towards the user. Raises defense.";
    private static final int mvePower = 0;
    private static final int mveAccuracy = 100;
    private static final int mveSkillPoints = 3;
    private static final Vector3f mveEffectColor = MoveBase.SUPPORT_MOVE_COLOR;
    private static final String mveSoundEffect = "basicAttack";


    // CONSTRUCTOR
    public Mve_DaringStand(GamePanel gp) {
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

        ArrayList<Integer> targetEntityIds = UtilityTool.extractKeySetAsArrayList(targetEntityDeltaLife);
        boolean alreadyLocked = gp.getCombatM().getTargetLockEntityId() == sourceEntityId;
        gp.getCombatM().setTargetLockEntityId(sourceEntityId);
        gp.getCombatM().setTargetLockTurns(1);
        gp.getCombatM().addQueuedActionBack(
                new Act_CustomEffect(gp, targetEntityIds, effectColor, soundEffect, true));

        if (gp.getEntityM().getEntityById(sourceEntityId).changeDefenseStage(1)) {

            gp.getCombatM().addQueuedActionBack(
                    new Act_CustomEffect(gp, targetEntityIds,
                            MoveBase.ATTRIBUTE_INCREASE_COLOR, "attributeIncrease", true));
            String message = buildEffectMessageDefenseIncrease(sourceEntityId);
            gp.getCombatM().addQueuedActionBack(
                    new Act_ReadMessage(gp, message, true, true));
        }
        String message = buildEffectMessageTargetLock(sourceEntityId, alreadyLocked);
        gp.getCombatM().addQueuedActionBack(
                new Act_ReadMessage(gp, message, true, true));
    }


    @Override
    public boolean verifyTarget(int targetEntityId) {

        if ((gp.getEntityM().getEntityById(targetEntityId).getStatus() != EntityStatus.FAINT)
                && (gp.getCombatM().getTargetLockEntityId() != targetEntityId)) {

            return true;
        }
        return false;
    }


    /**
     * Builds message for raised defense effect.
     *
     * @param entityId ID of affected entity
     * @return message
     */
    private String buildEffectMessageDefenseIncrease(int entityId) {

        return (UtilityTool.appendEntityNameApostropheS(gp.getEntityM().getEntityById(entityId).getName()) + " defense rose!");
    }


    /**
     * Builds message for locked on entity.
     *
     * @param entityId ID of entity locked onto.
     * @param alreadyLocked whether the entity was already locked onto prior to using this move (true) or not (false)
     * @return message
     */
    private String buildEffectMessageTargetLock(int entityId, boolean alreadyLocked) {

        if (alreadyLocked) {

            return (gp.getEntityM().getEntityById(entityId).getName() + " continues to be the center of attention!");
        } else {

            return (gp.getEntityM().getEntityById(entityId).getName() + " became the center of attention!");
        }
    }
}
