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

import javax.swing.text.html.parser.Entity;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * This class defines a move (Pickpocket).
 */
public class Mve_Pickpocket extends MoveBase {

    // FIELDS
    private static final int mveId = 1;
    private static final String mveName = "Pickpocket";
    private static final String mveDescription = "The user deceives the opponent, both attacking and stealing five skill points";
    private static final int mvePower = 45;
    private static final int mveAccuracy = 85;
    private static final int mveSkillPoints = 2;
    private static final Vector3f mveParticleEffectColor = new Vector3f(88, 92, 141);
    private static final String mveSoundEffect = "pickpocket";


    // CONSTRUCTOR
    public Mve_Pickpocket(GamePanel gp) {
        super(gp, mveId, MoveCategory.PHYSICAL, MoveTargets.OPPONENT, false);
        name = mveName;
        description = mveDescription;
        power = mvePower;
        accuracy = mveAccuracy;
        skillPoints = mveSkillPoints;
        particleEffectColor = mveParticleEffectColor;
        soundEffect = mveSoundEffect;
    }


    // METHOD
    @Override
    public void runEffects(int sourceEntityId, HashMap<Integer, Integer> targetEntityDeltaLife) {

        int skillStolenPerEntity = 5;
        EntityBase targetEntity;
        int numEntitiesSkillStolen = 0;
        HashMap<Integer, Integer> entitiesFinalSkill = new HashMap<>();
        ArrayList<Integer> targetEntityIds = UtilityTool.extractKeySetAsArrayList(targetEntityDeltaLife);

        for (int targetEntityId : targetEntityIds) {

            targetEntity = gp.getEntityM().getEntityById(targetEntityId);

            if (targetEntity.getSkill() >= skillStolenPerEntity) {

                numEntitiesSkillStolen++;
                entitiesFinalSkill.put(
                        targetEntityId,
                        gp.getEntityM().getEntityById(sourceEntityId).getSkill() - skillStolenPerEntity);
            }
        }

        if (numEntitiesSkillStolen > 0) {

            entitiesFinalSkill.put(
                    sourceEntityId,
                    gp.getEntityM().getEntityById(sourceEntityId).getSkill()
                            + (skillStolenPerEntity * numEntitiesSkillStolen));
            gp.getCombatM().addQueuedActionBack(
                    new Act_CustomEffect(gp, entitiesFinalSkill, new Vector3f(166, 172, 255), "heal", true));
            String message = gp.getEntityM().getEntityById(sourceEntityId).getName()
                    + " stole "
                    + skillStolenPerEntity * numEntitiesSkillStolen
                    + " skill points!";
            gp.getCombatM().addQueuedActionBack(
                    new Act_ReadMessage(gp, message, true, true));
        }
    }
}
