package combat.implementation.attack;

import combat.AttackBase;
import combat.AttackType;
import core.GamePanel;

/**
 * This class defines an attack (Struggle).
 * Note that this is the default move when an entity cannot otherwise attack.
 */
public class Atk_Struggle extends AttackBase {

    // Fields.
    private static final int atkId = 0;
    private static final String atkName = "Struggle";
    private static final String atkDescription = "Default attack.";
    private static final int atkPower = 10;
    private static final int atkSkillPoints = 0;


    // Constructor.
    public Atk_Struggle(GamePanel gp) {
        super(gp, atkId, AttackType.PHYSICAL);
        name = atkName;
        description = atkDescription;
        power = atkPower;
        skillPoints = atkSkillPoints;
    }
}
