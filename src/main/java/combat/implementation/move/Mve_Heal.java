package combat.implementation.move;

import combat.MoveBase;
import combat.enumeration.MoveCategory;
import combat.enumeration.MoveTargets;
import combat.implementation.action.Act_CustomEffect;
import core.GamePanel;
import org.joml.Vector3f;
import utility.UtilityTool;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * This class defines a move (Heal).
 */
public class Mve_Heal extends MoveBase {

    // FIELDS
    private static final int mveId = -1;
    private static final String mveName = "Heal";
    private static final String mveDescription = "Restores 50% of an ally's HP.";
    private static final int mvePower = 0;
    private static final int mveAccuracy = 100;
    private static final int mveSkillPoints = 3;
    private static final Vector3f mveEffectColor = new Vector3f(166, 255, 168);
    private static final String mveSoundEffect = "heal";


    // CONSTRUCTOR
    public Mve_Heal(GamePanel gp) {
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

        ArrayList<Integer> targetEntityIds = UtilityTool.extractKeySetAsArrayList(targetEntityDeltaLife);
        HashMap<Integer, Integer> targetEntitiesFinalLife = new HashMap<>();
        int calculatedFinalLife;

        for (int entityId : targetEntityIds) {

            calculatedFinalLife = (int)Math.ceil((double)gp.getEntityM().getEntityById(entityId).getMaxLife() / 2.0)
                    + gp.getEntityM().getEntityById(entityId).getLife();

            if ((gp.getEntityM().getEntityById(entityId).getLife() + calculatedFinalLife)
                > gp.getEntityM().getEntityById(entityId).getMaxLife()) {

                calculatedFinalLife = gp.getEntityM().getEntityById(entityId).getMaxLife();
            }
            targetEntitiesFinalLife.put(entityId, calculatedFinalLife);
        }
        HashMap<Integer, Integer> entitiesFinalSkillPoints = new HashMap<>();
        entitiesFinalSkillPoints.put(
                sourceEntityId,
                gp.getEntityM().getEntityById(sourceEntityId).getSkill() - skillPoints);
        gp.getCombatM().addQueuedActionBack(
                new Act_CustomEffect(gp, targetEntitiesFinalLife, entitiesFinalSkillPoints, effectColor, soundEffect, true));
    }


    @Override
    public boolean verifyTarget(int entityId) {

        if (gp.getEntityM().getEntityById(entityId).getLife() < gp.getEntityM().getEntityById(entityId).getMaxLife()) {

            return true;
        }
        return false;
    }
}
