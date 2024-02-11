package combat.implementation.move;

import combat.MoveBase;
import combat.MoveCategory;
import core.GamePanel;

import java.util.List;

/**
 * This class defines a move (Struggle).
 * Note that this is the default move when an entity cannot otherwise move.
 */
public class Mve_Struggle extends MoveBase {

    // FIELDS
    private static final int mveId = 0;
    private static final String mveName = "Struggle";
    private static final String mveDescription = "Default attack.";
    private static final int mvePower = 10;
    private static final int mveSkillPoints = 0;


    // CONSTRUCTOR
    public Mve_Struggle(GamePanel gp) {
        super(gp, mveId, MoveCategory.PHYSICAL);
        name = mveName;
        description = mveDescription;
        power = mvePower;
        skillPoints = mveSkillPoints;
    }


    // METHOD
    @Override
    public void runEffects(int sourceEntityId, List<Integer> targetEntityIds) {}
}
