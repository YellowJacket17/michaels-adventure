package combat.implementation.move;

import combat.MoveBase;
import combat.enumeration.MoveCategory;
import combat.enumeration.MoveTargets;
import combat.implementation.action.Act_CustomEffect;
import core.GamePanel;
import entity.enumeration.EntityStatus;
import org.joml.Vector3f;
import utility.UtilityTool;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * This class defines a move (Revive).
 */
public class Mve_Revive extends MoveBase {

    // FIELDS
    private static final int mveId = -2;
    private static final String mveName = "Revive";
    private static final String mveDescription = "Revives a fallen ally.";
    private static final int mvePower = 0;
    private static final int mveAccuracy = 100;
    private static final int mveSkillPoints = 2;
    private static final Vector3f mveEffectColor = new Vector3f(166, 255, 168);
    private static final String mveSoundEffect = "heal";


    // CONSTRUCTOR
    public Mve_Revive(GamePanel gp) {
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

        ArrayList<Integer> targetEntityIds = UtilityTool.extractKeySetAsArrayList(targetEntityDeltaLife);
        HashMap<Integer, Integer> entitiesFinalSkillPoints = new HashMap<>();
        entitiesFinalSkillPoints.put(
                sourceEntityId,
                gp.getEntityM().getEntityById(sourceEntityId).getSkill() - skillPoints);
        gp.getCombatM().addQueuedActionBack(
                new Act_CustomEffect(gp, entitiesFinalSkillPoints, effectColor, soundEffect, true));
        gp.getCombatAnimationS().initiateStandardReviveAnimation(targetEntityIds, 0.4, 0.4);
    }


    @Override
    public boolean verifyTarget(int entityId) {

        if (gp.getEntityM().getEntityById(entityId).getStatus() == EntityStatus.FAINT) {

            return true;
        }
        return false;
    }
}
