package combat.implementation.move;

import combat.MoveBase;
import combat.enumeration.MoveCategory;
import combat.enumeration.MoveTargets;
import core.GamePanel;
import org.joml.Vector3f;

import java.util.HashMap;

/**
 * This class defines a move (Shadow Strike).
 */
public class Mve_ShadowStrike extends MoveBase {

    // FIELDS
    private static final int mveId = 23;
    private static final String mveName = "Shadow Strike";
    private static final String mveDescription = "Draws power from the shadows to strike a target.";
    private static final int mvePower = 70;
    private static final int mveAccuracy = 100;
    private static final int mveSkillPoints = 1;
    private static final Vector3f mveEffectColor = MoveBase.MAGIC_MOVE_COLOR;
    private static final String mveSoundEffect = "shadowStrike";


    // CONSTRUCTOR
    public Mve_ShadowStrike(GamePanel gp) {
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
    public void runEffects(int sourceEntityId, HashMap<Integer, Integer> targetEntityDeltaLife) {}
}
