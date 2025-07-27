package combat.implementation.move;

import combat.MoveBase;
import combat.enumeration.MoveCategory;
import combat.enumeration.MoveTargets;
import core.GamePanel;
import org.joml.Vector3f;

import java.util.ArrayList;

/**
 * This class defines a move (Earthquake).
 */
public class Mve_AnnoyingImpulse extends MoveBase {

    // FIELDS
    private static final int mveId = 5;
    private static final String mveName = "Annoying Impulse";
    private static final String mveDescription = "A powerful quake that hits all foes.";
    private static final int mvePower = 60;
    private static final int mveAccuracy = 95;
    private static final int mveSkillPoints = 6;
    private static final Vector3f mveParticleEffectColor = new Vector3f(176, 123, 123);
    private static final String mveSoundEffect = "butterflyBlade";


    // CONSTRUCTOR
    public Mve_AnnoyingImpulse(GamePanel gp) {
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
    public void runEffects(int sourceEntityId, ArrayList<Integer> targetEntityIds) {}
}
