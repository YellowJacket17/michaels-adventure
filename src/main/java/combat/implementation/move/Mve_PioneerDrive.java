package combat.implementation.move;

import combat.MoveBase;
import combat.enumeration.MoveCategory;
import combat.enumeration.MoveTargets;
import core.GamePanel;
import org.joml.Vector3f;

import java.util.HashMap;

/**
 * This class defines a move (Pioneer Drive).
 */
public class Mve_PioneerDrive extends MoveBase {

    // FIELDS
    private static final int mveId = 20;
    private static final String mveName = "Pioneer Drive";
    private static final String mveDescription = "Drives a rock (not a boulder) into the target. High critical hit chance.";
    private static final int mvePower = 70;
    private static final int mveAccuracy = 75;
    private static final int mveSkillPoints = 4;
    private static final Vector3f mveEffectColor = MoveBase.PHYSICAL_MOVE_COLOR;
    private static final String mveSoundEffect = "pioneerDrive";


    // CONSTRUCTOR
    public Mve_PioneerDrive(GamePanel gp) {
        super(gp, mveId, MoveCategory.PHYSICAL, MoveTargets.OPPONENT, false);
        name = mveName;
        description = mveDescription;
        power = mvePower;
        accuracy = mveAccuracy;
        skillPoints = mveSkillPoints;
        effectColor = mveEffectColor;
        soundEffect = mveSoundEffect;
        highCriticalHit = true;
    }


    // METHOD
    @Override
    public void runEffects(int sourceEntityId, HashMap<Integer, Integer> targetEntityDeltaLife) {}
}
