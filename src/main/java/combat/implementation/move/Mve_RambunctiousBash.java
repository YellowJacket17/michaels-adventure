package combat.implementation.move;

import combat.MoveBase;
import combat.enumeration.MoveCategory;
import combat.enumeration.MoveTargets;
import core.GamePanel;
import org.joml.Vector3f;

import java.util.HashMap;

/**
 * This class defines a move (Rambunctious Bash).
 */
public class Mve_RambunctiousBash extends MoveBase {

    // FIELDS
    private static final int mveId = 16;
    private static final String mveName = "Rambunctious Bash";
    private static final String mveDescription = "Charges swiftly and slams the target. Higher user agility buff, higher damage.";
    private static final int mvePower = 30;
    private static final int mveAccuracy = 95;
    private static final int mveSkillPoints = 3;
    private static final Vector3f mveEffectColor = MoveBase.PHYSICAL_MOVE_COLOR;
    private static final String mveSoundEffect = "basicAttack";


    // CONSTRUCTOR
    public Mve_RambunctiousBash(GamePanel gp) {
        super(gp, mveId, MoveCategory.PHYSICAL, MoveTargets.OPPONENT, false);
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


    @Override
    public int calculateBonusDamage(int sourceEntityId, int targetEntityId) {

        if (gp.getEntityM().getEntityById(sourceEntityId).getAgilityBuff() > 0) {

            int baseBonusPower = 40;
            int sourceEntityAttack = gp.getEntityM().getEntityById(sourceEntityId).getBaseAttack()
                    + (int)(gp.getEntityM().getEntityById(sourceEntityId).getBaseAttack()
                    * gp.getEntityM().getEntityById(sourceEntityId).getAttackBuff());
            int targetEntityDefense = gp.getEntityM().getEntityById(targetEntityId).getBaseDefense()
                    + (int)(gp.getEntityM().getEntityById(targetEntityId).getBaseDefense()
                    * gp.getEntityM().getEntityById(targetEntityId).getDefenseBuff());
            return (int)Math.ceil(baseBonusPower * ((float)sourceEntityAttack / targetEntityDefense)
                    * gp.getEntityM().getEntityById(sourceEntityId).getAgilityBuff());
        } else {

            return 0;
        }
    }
}