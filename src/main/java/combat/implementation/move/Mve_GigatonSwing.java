package combat.implementation.move;

import combat.MoveBase;
import combat.enumeration.MoveCategory;
import combat.enumeration.MoveTargets;
import combat.implementation.action.Act_ReadMessage;
import core.GamePanel;
import org.joml.Vector3f;
import utility.UtilityTool;

import java.util.HashMap;

/**
 * This class defines a move (Gigaton Swing).
 */
public class Mve_GigatonSwing extends MoveBase {

    // FIELDS
    private static final int mveId = 19;
    private static final String mveName = "Gigaton Swing";
    private static final String mveDescription = "Winds up and takes a killing shot at the target. The user must recharge on their next turn.";
    private static final int mvePower = 85;
    private static final int mveAccuracy = 80;
    private static final int mveSkillPoints = 5;
    private static final Vector3f mveEffectColor = MoveBase.PHYSICAL_MOVE_COLOR;
    private static final String mveSoundEffect = "gigatonSwing";


    // CONSTRUCTOR
    public Mve_GigatonSwing(GamePanel gp) {
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

        gp.getCombatM().addSkipTurnEntity(sourceEntityId, 1);
        String message = buildEffectMessage(sourceEntityId);
        gp.getCombatM().addQueuedActionBack(new Act_ReadMessage(gp, message, true, true));
    }


    /**
     * Builds message for recharge effect.
     *
     * @param targetEntityId ID of affected entity
     * @return message
     */
    private String buildEffectMessage(int targetEntityId) {

        String message =
                UtilityTool.appendEntityNameApostropheS(gp.getEntityM().getEntityById(targetEntityId).getName());
        message += " must recharge on their next turn!";
        return message;
    }
}
