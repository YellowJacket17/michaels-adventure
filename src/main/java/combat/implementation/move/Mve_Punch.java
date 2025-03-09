package combat.implementation.move;

import combat.MoveBase;
import combat.enumeration.MoveCategory;
import combat.enumeration.MoveTargets;
import core.GamePanel;
import org.joml.Vector3f;

import java.util.ArrayList;

/**
 * This class defines a move (Punch).
 */
public class Mve_Punch extends MoveBase {

    // FIELDS
    private static final int mveId = 2;
    private static final String mveName = "Punch";
    private static final String mveDescription = "The user punches the opponent.";
    private static final int mvePower = 30;
    private static final int mveAccuracy = 85;
    private static final int mveSkillPoints = 2;
    private static final Vector3f mveParticleEffectColor = new Vector3f(255, 186, 166);
    private static final String mveSoundEffect = "testEffect3";


    // CONSTRUCTOR
    public Mve_Punch(GamePanel gp) {
        super(gp, mveId, MoveCategory.PHYSICAL, MoveTargets.OPPONENT_ALLY_SELF, false);
        name = mveName;
        description = mveDescription;
        power = mvePower;
        accuracy = mveAccuracy;
        skillPoints = mveSkillPoints;
        particleEffectColor = mveParticleEffectColor;
        soundEffect = mveSoundEffect;
    }


    // METHOD
    @Override
    public void runEffects(int sourceEntityId, ArrayList<Integer> targetEntityIds) {}
}
