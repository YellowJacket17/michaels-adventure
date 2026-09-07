package combat.implementation.move;

import combat.MoveBase;
import combat.enumeration.MoveCategory;
import combat.enumeration.MoveTargets;
import combat.implementation.action.Act_CustomEffect;
import combat.implementation.action.Act_ReadMessage;
import core.GamePanel;
import entity.enumeration.MoveWeakness;
import org.joml.Vector3f;
import utility.UtilityTool;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Random;

/**
 * This class defines a move (Actuary Blast).
 */
public class Mve_ActuaryBlast extends MoveBase {

    // FIELDS
    private static final int mveId = 12;
    private static final String mveName = "Actuary Blast";
    private static final String mveDescription = "Materializes and shoots an energized number (one to five). Higher number, higher bonus damage.";
    private static final int mvePower = 40;
    private static final int mveAccuracy = 100;
    private static final int mveSkillPoints = 3;
    private static final Vector3f mveEffectColor = MoveBase.MAGIC_MOVE_COLOR;
    private static final String mveSoundEffect = "actuaryBlast";
    private LinkedHashMap<Integer, Integer> rolledNumbers = new LinkedHashMap<>();


    // CONSTRUCTOR
    public Mve_ActuaryBlast(GamePanel gp) {
        super(gp, mveId, MoveCategory.MAGIC, MoveTargets.OPPONENT, false);
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
    public void runEffects(int sourceEntityId, HashMap<Integer, Integer> targetEntityDeltaLife) {

        String message;

        for (int targetEntityId : rolledNumbers.keySet()) {

            switch (rolledNumbers.get(targetEntityId)) {
                case 1:
                    message = buildEffectMessageOne(targetEntityId);
                    gp.getCombatM().addQueuedActionBack(
                            new Act_ReadMessage(gp, message, true, true));
                    break;
                case 2:
                    message = buildEffectMessageTwo(targetEntityId);
                    gp.getCombatM().addQueuedActionBack(
                            new Act_ReadMessage(gp, message, true, true));
                    break;
                case 3:
                    message = buildEffectMessageThree(targetEntityId);
                    gp.getCombatM().addQueuedActionBack(
                            new Act_ReadMessage(gp, message, true, true));
                    break;
                case 4:
                    message = buildEffectMessageFour(targetEntityId);
                    gp.getCombatM().addQueuedActionBack(
                            new Act_ReadMessage(gp, message, true, true));
                    break;
                case 5:
                    message = buildEffectMessageFive(targetEntityId);
                    gp.getCombatM().addQueuedActionBack(
                            new Act_ReadMessage(gp, message, true, true));
                    break;
            }
        }
        rolledNumbers.clear();                                                                                          // Clear for the next time this move is used.
    }


    @Override
    public int calculateBonusDamage(int sourceEntityId, int targetEntityId) {

        int rolledNumber = rollNumber();
        rolledNumbers.put(targetEntityId, rolledNumber);
        int bonusBasePower = 10;
        int sourceEntityMagic = gp.getEntityM().getEntityById(sourceEntityId).getBaseMagic()
                + (int)(gp.getEntityM().getEntityById(sourceEntityId).getBaseMagic()
                * gp.getEntityM().getEntityById(sourceEntityId).getMagicBuff());
        int targetEntityDefense = gp.getEntityM().getEntityById(targetEntityId).getBaseDefense()
                + (int)(gp.getEntityM().getEntityById(targetEntityId).getBaseDefense()
                * gp.getEntityM().getEntityById(targetEntityId).getDefenseBuff());
        return (int)Math.ceil(bonusBasePower * ((float)sourceEntityMagic / targetEntityDefense) * (rolledNumber - 1));
    }


    /**
     * Rolls to determine random number.
     *
     * @return random number from 1 (inclusive) to 5 (inclusive).
     */
    private int rollNumber() {

        Random random = new Random();
        return random.nextInt(5) + 1;                                                                                   // Generate random number from 0 (inclusive) to 5 (exclusive), then adds one.
    }


    /**
     * Builds message for number effect (one).
     *
     * @param targetEntityId ID of affected entity
     * @return message
     */
    private String buildEffectMessageOne(int targetEntityId) {

        return gp.getEntityM().getEntityById(targetEntityId).getName()
                + " was struck by a wimpy one, causing no bonus damage!";
    }


    /**
     * Builds message for number effect (two).
     *
     * @param targetEntityId ID of affected entity
     * @return message
     */
    private String buildEffectMessageTwo(int targetEntityId) {

        return gp.getEntityM().getEntityById(targetEntityId).getName()
                + " was struck by a typical two, causing a little bonus damage!";
    }


    /**
     * Builds message for number effect (three).
     *
     * @param targetEntityId ID of affected entity
     * @return message
     */
    private String buildEffectMessageThree(int targetEntityId) {

        return gp.getEntityM().getEntityById(targetEntityId).getName()
                + " was struck by a thumping three, causing moderate bonus damage!";
    }


    /**
     * Builds message for number effect (four).
     *
     * @param targetEntityId ID of affected entity
     * @return message
     */
    private String buildEffectMessageFour(int targetEntityId) {

        return gp.getEntityM().getEntityById(targetEntityId).getName()
                + " was struck by a fervent four, causing considerable bonus damage!";
    }


    /**
     * Builds message for number effect (five).
     *
     * @param targetEntityId ID of affected entity
     * @return message
     */
    private String buildEffectMessageFive(int targetEntityId) {

        return gp.getEntityM().getEntityById(targetEntityId).getName()
                + " was struck by a feisty five, causing significant bonus damage!";
    }
}
