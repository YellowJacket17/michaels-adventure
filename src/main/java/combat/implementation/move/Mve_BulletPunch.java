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
import java.util.Random;

/**
 * This class defines a move (Bullet Punch).
 */
public class Mve_BulletPunch extends MoveBase {

    // FIELDS
    private static final int mveId = 15;
    private static final String mveName = "Bullet Punch";
    private static final String mveDescription = "Hits the target with a rapid punch. May raise the user's agility.";
    private static final int mvePower = 40;
    private static final int mveAccuracy = 100;
    private static final int mveSkillPoints = 2;
    private static final Vector3f mveEffectColor = MoveBase.PHYSICAL_MOVE_COLOR;
    private static final String mveSoundEffect = "bulletPunch";


    // CONSTRUCTOR
    public Mve_BulletPunch(GamePanel gp) {
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

        if (rollAttribute()) {

            if (gp.getEntityM().getEntityById(sourceEntityId).changeAgilityStage(1)){

                ArrayList<Integer> affectedEntityIds = new ArrayList<>();
                affectedEntityIds.add(sourceEntityId);
                gp.getCombatM().addQueuedActionBack(
                        new Act_CustomEffect(gp, affectedEntityIds, MoveBase.ATTRIBUTE_INCREASE_COLOR, "attributeIncrease", true));
                String message = buildEffectMessage(sourceEntityId);
                gp.getCombatM().addQueuedActionBack(
                        new Act_ReadMessage(gp, message, true, true));
            }
        }
    }


    /**
     * Rolls to determine whether this move will increase the user's agility attribute or not.

     * @return whether this move will increase the user's agility attribute (true) or not (false)
     */
    private boolean rollAttribute() {

        Random random = new Random();
        int i = random.nextInt(100);                                                                                    // Generate random number from 0 (inclusive) to 100 (exclusive).

        if (i < 20) {

            return true;
        } else {

            return false;
        }
    }


    /**
     * Builds message for increased agility effect.
     *
     * @param targetEntityId ID of affected entity
     * @return message
     */
    private String buildEffectMessage(int targetEntityId) {

        String message =
                UtilityTool.appendEntityNameApostropheS(gp.getEntityM().getEntityById(targetEntityId).getName());
        message += " agility rose!";
        return message;
    }
}
