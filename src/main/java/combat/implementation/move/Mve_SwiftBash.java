package combat.implementation.move;

import combat.MoveBase;
import combat.enumeration.MoveCategory;
import combat.enumeration.MoveTargets;
import core.GamePanel;
import org.joml.Vector3f;

import java.util.HashMap;

/**
 * This class defines a move (Swift Bash).
 */
public class Mve_SwiftBash extends MoveBase {

    // FIELDS
    private static final int mveId = 16;
    private static final String mveName = "Swift Bash";
    private static final String mveDescription = "Charges swiftly and slams the target. Higher user agility, higher damage.";
    private static final int mvePower = 40;
    private static final int mveAccuracy = 95;
    private static final int mveSkillPoints = 3;
    private static final Vector3f mveEffectColor = MoveBase.PHYSICAL_MOVE_COLOR;
    private static final String mveSoundEffect = "swiftBash";


    // CONSTRUCTOR
    public Mve_SwiftBash(GamePanel gp) {
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

            int baseBonusPower = 15;
            int sourceEntityAgility = gp.getEntityM().getEntityById(sourceEntityId).getBaseAgility()
                    + (int)(gp.getEntityM().getEntityById(sourceEntityId).getBaseAgility()
                    * gp.getEntityM().getEntityById(sourceEntityId).getAgilityBuff());
            int targetEntityDefense = gp.getEntityM().getEntityById(targetEntityId).getBaseDefense()
                    + (int)(gp.getEntityM().getEntityById(targetEntityId).getBaseDefense()
                    * gp.getEntityM().getEntityById(targetEntityId).getDefenseBuff());
            return (int)Math.ceil(baseBonusPower * ((float)sourceEntityAgility / targetEntityDefense));
        } else {

            return 0;
        }
    }
}