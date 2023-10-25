package combat.implementation.attack;

import combat.AttackBase;
import combat.AttackType;
import core.GamePanel;

/**
 * This class defines an attack (Tackle).
 */
public class Atk_Tackle extends AttackBase {

    // Fields.
    private static final int atkId = 1;
    private static final String atkName = "Tackle";
    private static final String atkDescription = "Charge's the foe with a full-body tackle.";
    private static final int atkPower = 20;
    private static final int atkSkillPoints = 1;


    // Constructor.
    public Atk_Tackle(GamePanel gp) {
        super(gp, atkId, AttackType.PHYSICAL);
        name = atkName;
        description = atkDescription;
        power = atkPower;
        skillPoints = atkSkillPoints;
    }
}
