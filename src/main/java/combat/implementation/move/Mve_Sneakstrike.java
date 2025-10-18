package combat.implementation.move;

import combat.MoveBase;
import combat.enumeration.MoveCategory;
import combat.enumeration.MoveTargets;
import core.GamePanel;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * This class defines a move (Sneakstrike).
 */
public class Mve_Sneakstrike extends MoveBase {

    // FIELDS
    private static final int mveId = 4;
    private static final String mveName = "Sneakstrike";
    private static final String mveDescription = ". . .";
    private static final int mvePower = 50;
    private static final int mveAccuracy = 85;
    private static final int mveSkillPoints = 2;
    private static final Vector3f mveParticleEffectColor = new Vector3f(211, 186, 235);
    private static final String mveSoundEffect = "sneakstrike";


    // CONSTRUCTOR
    public Mve_Sneakstrike(GamePanel gp) {
        super(gp, mveId, MoveCategory.PHYSICAL, MoveTargets.OPPONENT, false);
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
    public void runEffects(int sourceEntityId, HashMap<Integer, Integer> targetEntityDeltaLife) {}
}
