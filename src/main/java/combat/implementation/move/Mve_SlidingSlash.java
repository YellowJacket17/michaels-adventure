package combat.implementation.move;

import combat.MoveBase;
import combat.enumeration.MoveCategory;
import combat.enumeration.MoveTargets;
import combat.implementation.action.Act_CustomEffect;
import combat.implementation.action.Act_ReadMessage;
import core.GamePanel;
import entity.EntityBase;
import entity.enumeration.EntityStatus;
import org.joml.Vector3f;
import utility.UtilityTool;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * This class defines a move (Sliding Slash).
 */
public class Mve_SlidingSlash extends MoveBase {

    // FIELDS
    private static final int mveId = 7;
    private static final String mveName = "Sliding Slash";
    private static final String mveDescription = "Runs at the target and slides at their feet while slashing with a sword. Raises allies' attack.";
    private static final int mvePower = 60;
    private static final int mveAccuracy = 95;
    private static final int mveSkillPoints = 6;
    private static final Vector3f mveParticleEffectColor = new Vector3f(255, 255, 255);
    private static final String mveSoundEffect = "sneakstrike";


    // CONSTRUCTOR
    public Mve_SlidingSlash(GamePanel gp) {
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

        ArrayList<EntityBase> allyEntities = retrieveAllyEntities(sourceEntityId);
        ArrayList<Integer> affectedAllyEntityIds = new ArrayList<>();

        for (EntityBase allyEntity : allyEntities) {

            if (allyEntity.changeAttackStage(1)) {

                affectedAllyEntityIds.add(allyEntity.getEntityId());
            }
        }

        if (affectedAllyEntityIds.size() > 0) {

            gp.getCombatM().addQueuedActionBack(
                    new Act_CustomEffect(gp, affectedAllyEntityIds, new Vector3f(166, 255, 168), "heal", true));
            String message = buildEffectMessage(affectedAllyEntityIds);
            gp.getCombatM().addQueuedActionBack(
                    new Act_ReadMessage(gp, message, true, true));
        }
    }


    /**
     * Retrieves all ally entities.
     *
     * @param sourceEntityId ID of entity using move
     * @return ally entities
     */
    private ArrayList<EntityBase> retrieveAllyEntities(int sourceEntityId) {

        ArrayList<EntityBase> allyEntities = new ArrayList<>();

        if ((sourceEntityId == gp.getEntityM().getPlayer().getEntityId())
                || (gp.getEntityM().getParty().containsKey(sourceEntityId))) {                                          // See if a player-side entity used the move.

            if (sourceEntityId != gp.getEntityM().getPlayer().getEntityId()) {

                allyEntities.add(gp.getEntityM().getPlayer());
            }

            int entityIndex = 0;

            for (EntityBase entity : gp.getEntityM().getParty().values()) {

                if ((entity.getEntityId() != sourceEntityId) && (entity.getStatus() != EntityStatus.FAINT)) {

                    allyEntities.add(entity);
                }
                entityIndex++;

                if (entityIndex >= gp.getEntityM().getNumActivePartyMembers()) {

                    break;
                }
            }
        } else {                                                                                                        // A non-player-side entity must have used the move.

            EntityBase allyEntity;

            for (int entityId : gp.getCombatM().getNonPlayerSideEntities()) {

                allyEntity = gp.getEntityM().getEntityById(entityId);

                if ((allyEntity.getEntityId() != sourceEntityId) && (allyEntity.getStatus() != EntityStatus.FAINT)) {

                    allyEntities.add(allyEntity);
                }
            }
        }
        return allyEntities;
    }


    /**
     * Builds message for raised defense effect.
     *
     * @param targetEntityIds IDs of affected entities
     * @return message
     */
    private String buildEffectMessage(ArrayList<Integer> targetEntityIds) {

        ArrayList<String> targetEntityNames = new ArrayList<>();

        for (int entityId : targetEntityIds) {

            targetEntityNames.add(gp.getEntityM().getEntityById(entityId).getName());
        }
        String message = UtilityTool.buildEntityListMessage(targetEntityNames);
        message += " attack rose!";
        return message;
    }
}
