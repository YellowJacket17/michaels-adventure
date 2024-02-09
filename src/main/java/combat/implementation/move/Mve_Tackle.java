package combat.implementation.move;

import combat.MoveBase;
import combat.MoveCategory;
import core.GamePanel;

/**
 * This class defines a move (Tackle).
 */
public class Mve_Tackle extends MoveBase {

    // FIELDS
    private static final int mveId = 1;
    private static final String mveName = "Tackle";
    private static final String mveDescription = "Charge's the foe with a full-body tackle.";
    private static final int mvePower = 20;
    private static final int mveSkillPoints = 1;


    // CONSTRUCTOR
    public Mve_Tackle(GamePanel gp) {
        super(gp, mveId, MoveCategory.PHYSICAL);
        name = mveName;
        description = mveDescription;
        power = mvePower;
        skillPoints = mveSkillPoints;
    }


    // METHOD
    @Override
    public void runEffects() {}
}
