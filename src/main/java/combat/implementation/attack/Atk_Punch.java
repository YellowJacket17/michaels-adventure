package combat.implementation.attack;

import combat.AttackBase;
import combat.AttackType;
import core.GamePanel;

/**
 * This class defines an attack (Punch).
 */
public class Atk_Punch extends AttackBase {

    // Fields.
    private static final int atkId = 2;
    private static final String atkName = "Punch";
    private static final String atkDescription = "The user punches the opponent.";
    private static final int atkPower = 25;
    private static final int atkSkillPoints = 2;


    // Constructor.
    public Atk_Punch(GamePanel gp) {
        super(gp, atkId, AttackType.PHYSICAL);
        name = atkName;
        description = atkDescription;
        power = atkPower;
        skillPoints = atkSkillPoints;
    }
}
