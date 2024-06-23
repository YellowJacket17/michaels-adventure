package combat.implementation.move;

import combat.MoveBase;
import combat.enumeration.MoveCategory;
import combat.enumeration.MoveTargets;
import core.GamePanel;

import java.util.List;

/**
 * This class defines a move (Struggle).
 * Note that this is the default move when an entity cannot otherwise move.
 */
public class Mve_BasicAttack extends MoveBase {

    // FIELDS
    private static final int mveId = 0;
    private static final String mveName = "Basic Attack";
    private static final String mveDescription = "Default attack.";
    private static final int mvePower = 10;
    private static final int mveSkillPoints = 0;


    // CONSTRUCTOR
    public Mve_BasicAttack(GamePanel gp) {
        super(gp, mveId, MoveCategory.PHYSICAL, MoveTargets.OPPONENT);
        name = mveName;
        description = mveDescription;
        power = mvePower;
        skillPoints = mveSkillPoints;
    }


    // METHOD
    @Override
    public void runEffects(int sourceEntityId, List<Integer> targetEntityIds) {}
}
