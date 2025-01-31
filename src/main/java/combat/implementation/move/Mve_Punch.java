package combat.implementation.move;

import combat.MoveBase;
import combat.enumeration.MoveCategory;
import combat.enumeration.MoveTargets;
import core.GamePanel;

import java.util.List;

/**
 * This class defines a move (Punch).
 */
public class Mve_Punch extends MoveBase {

    // FIELDS
    private static final int mveId = 2;
    private static final String mveName = "Punch";
    private static final String mveDescription = "The user punches the opponent.";
    private static final int mvePower = 25;
    private static final int mveAccuracy = 90;
    private static final int mveSkillPoints = 2;


    // CONSTRUCTOR
    public Mve_Punch(GamePanel gp) {
        super(gp, mveId, MoveCategory.PHYSICAL, MoveTargets.OPPONENT_ALLY_SELF);
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
