package combat.implementation.move;

import combat.MoveBase;
import combat.enumeration.MoveCategory;
import combat.enumeration.MoveTargets;
import combat.implementation.action.Act_CustomEffect;
import combat.implementation.action.Act_ReadMessage;
import core.GamePanel;
import entity.EntityBase;
import org.joml.Vector3f;
import utility.UtilityTool;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * This class defines a move (Whirlwind Spring).
 */
public class Mve_WhirlwindSprint extends MoveBase {

    // FIELDS
    private static final int mveId = 8;
    private static final String mveName = "Whirlwind Sprint";
    private static final String mveDescription = "Running blindingly fast in circles, generates a whirlwind that hits all active opponents.";
    private static final int mvePower = 50;
    private static final int mveAccuracy = 100;
    private static final int mveSkillPoints = 6;
    private static final Vector3f mveEffectColor = MoveBase.PHYSICAL_MOVE_COLOR;
    private static final String mveSoundEffect = "basicAttack";


    // CONSTRUCTOR
    public Mve_WhirlwindSprint(GamePanel gp) {
        super(gp, mveId, MoveCategory.PHYSICAL, MoveTargets.OPPONENT, true);
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
}
