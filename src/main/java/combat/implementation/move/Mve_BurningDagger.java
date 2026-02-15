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
 * This class defines a move (Burning Dagger).
 */
public class Mve_BurningDagger extends MoveBase {

    // FIELDS
    private static final int mveId = 3;
    private static final String mveName = "Burning Dagger";
    private static final String mveDescription = "Slashes at the target with a burning knife. Also lowers attack.";
    private static final int mvePower = 70;
    private static final int mveAccuracy = 90;
    private static final int mveSkillPoints = 4;
    private static final Vector3f mveEffectColor = new Vector3f(255, 190, 166);
    private static final String mveSoundEffect = "burningDagger";


    // CONSTRUCTOR
    public Mve_BurningDagger(GamePanel gp) {
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

        EntityBase targetEntity;
        ArrayList<Integer> affectedTargetEntityIds = new ArrayList<>();
        ArrayList<Integer> targetEntityIds = UtilityTool.extractKeySetAsArrayList(targetEntityDeltaLife);

        for (int targetEntityId : targetEntityIds) {

            targetEntity = gp.getEntityM().getEntityById(targetEntityId);

            if (targetEntity.changeAttackStage(-1)) {

                affectedTargetEntityIds.add(targetEntityId);
            }
        }

        if (affectedTargetEntityIds.size() > 0) {

            gp.getCombatM().addQueuedActionBack(
                    new Act_CustomEffect(gp, targetEntityIds, MoveBase.ATTRIBUTE_DECREASE_COLOR, "attributeDecrease", true));
            String message = buildEffectMessage(affectedTargetEntityIds);
            gp.getCombatM().addQueuedActionBack(
                    new Act_ReadMessage(gp, message, true, true));
        }
    }


    /**
     * Builds message for lowered defense effect.
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
        message += " attack fell!";
        return message;
    }
}
