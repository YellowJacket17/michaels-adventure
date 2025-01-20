package combat.implementation.move;

import combat.MoveBase;
import combat.enumeration.MoveCategory;
import combat.enumeration.MoveTargets;
import core.GamePanel;

import java.util.List;

/**
 * This class defines a move (Tackle).
 */
public class Mve_Tackle extends MoveBase {

    // FIELDS
    private static final int mveId = 1;
    private static final String mveName = "Tackle";
    private static final String mveDescription = "Charge's the foe with a full-body tackle.";
    private static final int mvePower = 20;
    private static final int mveAccuracy = 95;
    private static final int mveSkillPoints = 1;


    // CONSTRUCTOR
    public Mve_Tackle(GamePanel gp) {
        super(gp, mveId, MoveCategory.PHYSICAL, MoveTargets.OPPONENT_ALLY);
        name = mveName;
        description = mveDescription;
        power = mvePower;
        accuracy = mveAccuracy;
        skillPoints = mveSkillPoints;
    }


    // METHOD
    @Override
    public void runEffects(int sourceEntityId, List<Integer> targetEntityIds) {}
}
