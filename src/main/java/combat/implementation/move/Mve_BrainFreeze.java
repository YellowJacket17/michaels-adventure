package combat.implementation.move;

import combat.MoveBase;
import combat.enumeration.MoveCategory;
import combat.enumeration.MoveTargets;
import combat.implementation.action.Act_ReadMessage;
import core.GamePanel;
import org.joml.Vector3f;
import utility.UtilityTool;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

/**
 * This class defines a move (Brain Freeze).
 */
public class Mve_BrainFreeze extends MoveBase {

    // FIELDS
    private static final int mveId = 11;
    private static final String mveName = "Brain Freeze";
    private static final String mveDescription = "Concentrates psychic energy around the target's head. May immobilize the target on their next turn.";
    private static final int mvePower = 40;
    private static final int mveAccuracy = 100;
    private static final int mveSkillPoints = 2;
    private static final Vector3f mveEffectColor = MoveBase.MAGIC_MOVE_COLOR;
    private static final String mveSoundEffect = "brainFreeze";


    // CONSTRUCTOR
    public Mve_BrainFreeze(GamePanel gp) {
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

        ArrayList<Integer> affectedTargetEntityIds = new ArrayList<>();
        ArrayList<Integer> targetEntityIds = UtilityTool.extractKeySetAsArrayList(targetEntityDeltaLife);

        for (int targetEntityId : targetEntityIds) {

            if ((rollFlinch()) && (!gp.getCombatM().getTurnSkipEntities().containsKey(targetEntityId))
                    && (gp.getEntityM().getEntityById(targetEntityId).getLife() > 0)) {                                 // Only flinch if not already skipping turns and if life is greater than zero.

                gp.getCombatM().addSkipTurnEntity(targetEntityId, 1);
                affectedTargetEntityIds.add(targetEntityId);
            }
        }

        if (affectedTargetEntityIds.size() > 0) {

            String message = buildEffectMessage(affectedTargetEntityIds);
            gp.getCombatM().addQueuedActionBack(
                    new Act_ReadMessage(gp, message, true, true));
        }
    }


    /**
     * Rolls to determine whether this move will cause the target to flinch or not.

     * @return whether this move will cause the target to flinch (true) or not (false)
     */
    private boolean rollFlinch() {

        Random random = new Random();
        int i = random.nextInt(100);                                                                                    // Generate random number from 0 (inclusive) to 100 (exclusive).

        if (i < 20) {

            return true;
        } else {

            return false;
        }
    }


    /**
     * Builds message for flinch effect.
     *
     * @param targetEntityIds IDs of affected entities
     * @return message
     */
    private String buildEffectMessage(ArrayList<Integer> targetEntityIds) {

        ArrayList<String> targetEntityNames = new ArrayList<>();

        for (int entityId : targetEntityIds) {

            targetEntityNames.add(gp.getEntityM().getEntityById(entityId).getName());
        }
        String message = UtilityTool.buildEntityListMessage(targetEntityNames, false);
        message += " flinched and will be unable to move on their next turn!";
        return message;
    }
}
