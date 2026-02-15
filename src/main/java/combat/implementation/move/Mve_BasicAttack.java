package combat.implementation.move;

import combat.MoveBase;
import combat.enumeration.MoveCategory;
import combat.enumeration.MoveTargets;
import core.GamePanel;
import org.joml.Vector3f;

import java.util.HashMap;

/**
 * This class defines a move (Basic Attack).
 * Note that this is the default move when an entity cannot otherwise move.
 */
public class Mve_BasicAttack extends MoveBase {

    // FIELDS
    private static final int mveId = 0;
    private static final String mveName = "Basic Attack";
    private static final String mveDescription = "Default attack.";
    private static final int mvePower = 40;
    private static final int mveAccuracy = 100;
    private static final int mveSkillPoints = 0;
    private static final Vector3f mveEffectColor = new Vector3f(228, 166, 255);
    private static final String mveSoundEffect = "sneakstrike";


    // CONSTRUCTOR
    public Mve_BasicAttack(GamePanel gp) {
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
    public void runEffects(int sourceEntityId, HashMap<Integer, Integer> targetEntityDeltaLife) {}
}
