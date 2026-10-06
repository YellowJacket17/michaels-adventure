package combat.implementation.load;

import combat.CombatLoadBase;
import core.GamePanel;
import core.enumeration.PrimaryGameState;
import entity.enumeration.EntityDirection;
import utility.JsonParser;

public class Cbl_Scenario002 extends CombatLoadBase {

    // CONSTRUCTOR
    public Cbl_Scenario002(GamePanel gp) {
        super(gp);
    }


    // METHODS
    @Override
    public void handleEnterCombatTransitionLoading() {}


    @Override
    public void concludeEnterCombatTransition() {}


    @Override
    public void handleExitCombatTransitionLoading(boolean combatLost) {

        if (!combatLost) {

            gp.getEntityM().getPlayer().setCol(37);
            gp.getEntityM().getPlayer().setRow(49);
            gp.getEntityM().getPlayer().setDirectionCurrent(EntityDirection.RIGHT);

            gp.getEntityM().getEntityById(5).stopFollowingEntity();
            gp.getEntityM().getEntityById(5).setCol(37);
            gp.getEntityM().getEntityById(5).setRow(51);
            gp.getEntityM().getEntityById(5).setDirectionCurrent(EntityDirection.RIGHT);

            gp.getEntityM().getEntityById(9).setCol(41);
            gp.getEntityM().getEntityById(9).setRow(49);
            gp.getEntityM().getEntityById(9).setDirectionCurrent(EntityDirection.LEFT);

            gp.getEntityM().getEntityById(10).setCol(41);
            gp.getEntityM().getEntityById(10).setRow(51);
            gp.getEntityM().getEntityById(10).setDirectionCurrent(EntityDirection.LEFT);

            JsonParser.loadEntityJson(gp, 11);                                                                          // Load Logan for the post-combat cutscene.
            JsonParser.loadEntityJson(gp, 12);                                                                          // Load Joe for the post-combat cutscene.

            gp.getEntityM().getEntityById(11).stopFollowingEntity();                                                    // Just in case.
            gp.getEntityM().getEntityById(11).setCol(57);
            gp.getEntityM().getEntityById(11).setRow(49);
            gp.getEntityM().getEntityById(11).setDirectionCurrent(EntityDirection.LEFT);

            gp.getEntityM().getEntityById(12).stopFollowingEntity();                                                    // Just in case.
            gp.getEntityM().getEntityById(12).setCol(57);
            gp.getEntityM().getEntityById(12).setRow(51);
            gp.getEntityM().getEntityById(12).setDirectionCurrent(EntityDirection.LEFT);

            gp.getEntityM().getEntityById(11).setHidden(false);
            gp.getEntityM().getEntityById(12).setHidden(false);

            gp.getMapM().getLoadedMap().setMapState(2, false);                                                          // Set map to its post-combat (win) state.

            gp.getCameraS().setOverrideEntityTracking(true);
            gp.getCameraS().setCameraSnap(1200, 1600);
        } else {

            gp.getEntityM().getPlayer().setCol(22);
            gp.getEntityM().getPlayer().setRow(50);
            gp.getEntityM().getPlayer().setDirectionCurrent(EntityDirection.RIGHT);

            gp.getWarpS().warpActivePartyMembersToPlayer();
            gp.getWarpS().warpInactivePartyMembersToPlayer();

            gp.getEntityM().getEntityById(9).setCol(49);
            gp.getEntityM().getEntityById(9).setRow(49);
            gp.getEntityM().getEntityById(9).setDirectionCurrent(EntityDirection.LEFT);
            gp.getEntityM().getEntityById(9).resetPrimaryAttributes();                                                  // Restore primary attributes for re-fight.

            gp.getEntityM().getEntityById(10).setCol(49);
            gp.getEntityM().getEntityById(10).setRow(51);
            gp.getEntityM().getEntityById(10).setDirectionCurrent(EntityDirection.LEFT);
            gp.getEntityM().getEntityById(10).resetPrimaryAttributes();                                                 // Restore primary attributes for re-fight.

            gp.getMapM().getLoadedMap().setMapState(1, false);                                                          // Set map to its post-combat (lose) state.
        }
        gp.getEntityM().getPlayer().resetPrimaryAttributes();                                                           // Note: Secondary attributes of all combating entities is automatically reset in the CombatManager class.

        for (int entityId : gp.getEntityM().getParty().keySet()) {

            gp.getEntityM().getParty().get(entityId).resetPrimaryAttributes();
        }
    }


    @Override
    public void concludeExitCombatTransition(boolean combatLost) {

        if (!combatLost) {

            gp.getCutsceneM().initiateCutscene(7);
        } else {

            gp.getEntityM().getEntityById(5).startFollowingEntity(gp.getEntityM().getPlayer().getEntityId(), true);
            gp.setPrimaryGameState(PrimaryGameState.EXPLORE);
        }
    }
}
