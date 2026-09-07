package combat.implementation.move;

import combat.MoveBase;
import combat.enumeration.MoveCategory;
import combat.enumeration.MoveTargets;
import core.GamePanel;
import org.joml.Vector3f;

import java.util.HashMap;

/**
 * This class defines a move (Psychic Swipe).
 */
public class Mve_PsychicSwipe extends MoveBase {

    // FIELDS
    private static final int mveId = 13;
    private static final String mveName = "Psychic Swipe";
    private static final String mveDescription = "With a flick of the hand, hits the target with a stream of psychic energy.";
    private static final int mvePower = 75;
    private static final int mveAccuracy = 95;
    private static final int mveSkillPoints = 4;
    private static final Vector3f mveEffectColor = MoveBase.MAGIC_MOVE_COLOR;
    private static final String mveSoundEffect = "psychicSwipe";


    // CONSTRUCTOR
    public Mve_PsychicSwipe(GamePanel gp) {
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
