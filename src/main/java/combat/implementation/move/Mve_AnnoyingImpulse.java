package combat.implementation.move;

import combat.MoveBase;
import combat.enumeration.MoveCategory;
import combat.enumeration.MoveTargets;
import combat.implementation.action.Act_CustomEffect;
import combat.implementation.action.Act_ReadMessage;
import core.GamePanel;
import org.joml.Vector3f;
import utility.UtilityTool;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * This class defines a move (Annoying Impulse).
 */
public class Mve_AnnoyingImpulse extends MoveBase {

    // FIELDS
    private static final int mveId = 5;
    private static final String mveName = "Annoying Impulse";
    private static final String mveDescription = "Directs all attacks for the next two turns towards the user. Raises defense.";
    private static final int mvePower = 60;
    private static final int mveAccuracy = 95;
    private static final int mveSkillPoints = 6;
    private static final Vector3f mveEffectColor = new Vector3f(255, 255, 255);
    private static final String mveSoundEffect = "sneakstrike";


    // CONSTRUCTOR
    public Mve_AnnoyingImpulse(GamePanel gp) {
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
        gp.getCombatM().setTargetLockTurns(2);

        if (gp.getEntityM().getEntityById(sourceEntityId).changeDefenseStage(1)) {

            gp.getCombatM().addQueuedActionBack(
                    new Act_CustomEffect(gp, targetEntityIds, new Vector3f(255, 255, 255), "hop", true));
            gp.getCombatM().addQueuedActionBack(
                    new Act_CustomEffect(gp, targetEntityIds, MoveBase.ATTRIBUTE_INCREASE_COLOR, "heal", true));
            String message = buildEffectMessageDefenseIncrease(sourceEntityId);
            gp.getCombatM().addQueuedActionBack(
                    new Act_ReadMessage(gp, message, true, true));
        }
        String message = buildEffectMessageTargetLock(sourceEntityId, alreadyLocked);
        gp.getCombatM().addQueuedActionBack(
                new Act_ReadMessage(gp, message, true, true));
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

            return (gp.getEntityM().getEntityById(entityId).getName() + " reinvigorated everyone's interest as the center of attention!");
        } else {

            return (gp.getEntityM().getEntityById(entityId).getName() + " became the center of attention!");
        }
    }
}
