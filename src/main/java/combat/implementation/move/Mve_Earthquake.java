package combat.implementation.move;

import combat.MoveBase;
import combat.enumeration.MoveCategory;
import combat.enumeration.MoveTargets;
import core.GamePanel;
import org.joml.Vector3f;

import java.util.List;

/**
 * This class defines a move (Earthquake).
 */
public class Mve_Earthquake extends MoveBase {

    // FIELDS
    private static final int mveId = 1;
    private static final String mveName = "Earthquake";
    private static final String mveDescription = "A powerful quake that hits all foes.";
    private static final int mvePower = 35;
    private static final int mveAccuracy = 95;
    private static final int mveSkillPoints = 6;
    private static final Vector3f mveParticleEffectColor = new Vector3f(176, 123, 123);
    private static final String mveSoundEffect = "testEffect4";


    // CONSTRUCTOR
    public Mve_Earthquake(GamePanel gp) {
        super(gp, mveId, MoveCategory.PHYSICAL, MoveTargets.OPPONENT_ALLY, true);
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
    public void runEffects(int sourceEntityId, List<Integer> targetEntityIds) {}
}
