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
import java.util.Random;

/**
 * This class defines a move (Sneakstrike).
 */
public class Mve_Sneakstrike extends MoveBase {

    // FIELDS
    private static final int mveId = 4;
    private static final String mveName = "Sneakstrike";
    private static final String mveDescription = "Sneaks up on the target and strikes. Ignores the target's attribute buffs.";
    private static final int mvePower = 35;
    private static final int mveAccuracy = 100;
    private static final int mveSkillPoints = 2;
    private static final Vector3f mveEffectColor = MoveBase.PHYSICAL_MOVE_COLOR;
    private static final String mveSoundEffect = "sneakstrike";


    // CONSTRUCTOR
    public Mve_Sneakstrike(GamePanel gp) {
        super(gp, mveId, MoveCategory.PHYSICAL, MoveTargets.OPPONENT, false);
        name = mveName;
        description = mveDescription;
        power = mvePower;
        accuracy = mveAccuracy;
        skillPoints = mveSkillPoints;
        effectColor = mveEffectColor;
        soundEffect = mveSoundEffect;
        ignoreAttributeBuffs = true;
    }


    // METHOD
    @Override
    public void runEffects(int sourceEntityId, HashMap<Integer, Integer> targetEntityDeltaLife) {}


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
