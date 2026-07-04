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
 * This class defines a move (Empowering Strike).
 */
public class Mve_EmpoweringStrike extends MoveBase {

    // FIELDS
    private static final int mveId = 7;
    private static final String mveName = "Empowering Strike";
    private static final String mveDescription = "Runs at the target and strikes while cheering on the team. Raises all allies' attack.";
    private static final int mvePower = 50;
    private static final int mveAccuracy = 95;
    private static final int mveSkillPoints = 3;
    private static final Vector3f mveEffectColor = new Vector3f(228, 215, 192);
    private static final String mveSoundEffect = "empoweringStrike";


    // CONSTRUCTOR
    public Mve_EmpoweringStrike(GamePanel gp) {
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

        ArrayList<EntityBase> allyEntities = gp.getCombatM().retrieveAllyEntities(sourceEntityId);
        ArrayList<Integer> affectedAllyEntityIds = new ArrayList<>();

        for (EntityBase allyEntity : allyEntities) {

            if (allyEntity.changeAttackStage(1)) {

                affectedAllyEntityIds.add(allyEntity.getEntityId());
            }
        }

        if (affectedAllyEntityIds.size() > 0) {

            gp.getCombatM().addQueuedActionBack(
                    new Act_CustomEffect(gp, affectedAllyEntityIds,
                            ATTRIBUTE_INCREASE_COLOR, "attributeIncrease", true));
            String message = buildEffectMessage(affectedAllyEntityIds);
            gp.getCombatM().addQueuedActionBack(
                    new Act_ReadMessage(gp, message, true, true));
        }
    }


    /**
     * Builds message for raised defense effect.
     *
     * @param targetEntityIds IDs of affected entities
     * @return message
     */
    private String buildEffectMessage(ArrayList<Integer> targetEntityIds) {

        ArrayList<String> targetEntityNames = new ArrayList<>();

        for (int entityId : targetEntityIds) {

            targetEntityNames.add(gp.getEntityM().getEntityById(entityId).getName());
        }
        String message = UtilityTool.buildEntityListMessage(targetEntityNames, true);
        message += " attack rose!";
        return message;
    }
}
