package combat.implementation.move;

import combat.MoveBase;
import combat.enumeration.MoveCategory;
import combat.enumeration.MoveTargets;
import core.GamePanel;
import org.joml.Vector3f;

import java.util.HashMap;

/**
 * This class defines a move (Tricky Shot).
 */
public class Mve_TrickyShot extends MoveBase {

    // FIELDS
    private static final int mveId = 21;
    private static final String mveName = "Tricky Shot";
    private static final String mveDescription = "Skillfully strikes the target. Always does damage equal to this move's power.";
    private static final int mvePower = 45;
    private static final int mveAccuracy = 95;
    private static final int mveSkillPoints = 3;
    private static final Vector3f mveEffectColor = MoveBase.MAGIC_MOVE_COLOR;
    private static final String mveSoundEffect = "basicAttack";


    // CONSTRUCTOR
    public Mve_TrickyShot(GamePanel gp) {
        super(gp, mveId, MoveCategory.MAGIC, MoveTargets.OPPONENT, false);
        name = mveName;
        description = mveDescription;
        power = mvePower;
        accuracy = mveAccuracy;
        skillPoints = mveSkillPoints;
        effectColor = mveEffectColor;
        soundEffect = mveSoundEffect;
        damageEqualPower = true;
    }


    // METHOD
    @Override
    public void runEffects(int sourceEntityId, HashMap<Integer, Integer> targetEntityDeltaLife) {}
}
