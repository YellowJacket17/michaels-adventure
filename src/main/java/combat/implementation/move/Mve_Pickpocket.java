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
 * This class defines a move (Pickpocket).
 */
public class Mve_Pickpocket extends MoveBase {

    // FIELDS
    private static final int mveId = 1;
    private static final String mveName = "Pickpocket";
    private static final String mveDescription = "Deceives the target and strikes. Steals five skill points.";
    private static final int mvePower = 30;
    private static final int mveAccuracy = 95;
    private static final int mveSkillPoints = 2;
    private static final Vector3f mveEffectColor = MoveBase.PHYSICAL_MOVE_COLOR;
    private static final String mveSoundEffect = "pickpocket";


    // CONSTRUCTOR
    public Mve_Pickpocket(GamePanel gp) {
        super(gp, mveId, MoveCategory.PHYSICAL, MoveTargets.OPPONENT, false);
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

        int skillStolenPerEntity = 5;
        EntityBase targetEntity;
        int totalSkillStolen = 0;
        HashMap<Integer, Integer> entitiesFinalSkill = new HashMap<>();
        ArrayList<Integer> targetEntityIds = UtilityTool.extractKeySetAsArrayList(targetEntityDeltaLife);

        for (int targetEntityId : targetEntityIds) {

            targetEntity = gp.getEntityM().getEntityById(targetEntityId);

            if (targetEntity.getSkill() >= skillStolenPerEntity) {

                totalSkillStolen += skillStolenPerEntity;
                gp.getEntityM().getEntityById(targetEntityId).setSkill(targetEntity.getSkill() - skillStolenPerEntity); // Subtract skill immediately since no custom effect animation is desired for targeted entity.

//                entitiesFinalSkill.put(
//                        targetEntityId,
//                        gp.getEntityM().getEntityById(targetEntityId).getSkill() - skillStolenPerEntity);
            } else {

                totalSkillStolen += targetEntity.getSkill();

                gp.getEntityM().getEntityById(targetEntityId).setSkill(0);                                              // Subtract skill immediately since no custom effect animation is desired for targeted entity.

//                entitiesFinalSkill.put(
//                        targetEntityId,
//                        gp.getEntityM().getEntityById(targetEntityId).getSkill() - targetEntity.getSkill());
            }
        }

        if (totalSkillStolen > 0) {

            entitiesFinalSkill.put(
                    sourceEntityId,
                    gp.getEntityM().getEntityById(sourceEntityId).getSkill() + totalSkillStolen);
            gp.getCombatM().addQueuedActionBack(
                    new Act_CustomEffect(gp, entitiesFinalSkill, true,
                            MoveBase.SKILL_RECOVERY_COLOR, "heal", true));
            String message = gp.getEntityM().getEntityById(sourceEntityId).getName()
                    + " stole "
                    + totalSkillStolen
                    + " skill points!";
            gp.getCombatM().addQueuedActionBack(
                    new Act_ReadMessage(gp, message, true, true));
        }
    }
}
