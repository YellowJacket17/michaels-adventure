package combat.implementation.load;

import combat.CombatLoadBase;
import core.GamePanel;
import entity.EntityBase;
import entity.enumeration.EntityDirection;
import utility.JsonParser;

public class Cbl_Scenario001 extends CombatLoadBase {

    // CONSTRUCTOR
    public Cbl_Scenario001(GamePanel gp) {
        super(gp);
    }


    // FIELDS
    @Override
    public void handleEnterCombatTransitionLoading() {}


    @Override
    public void concludeEnterCombatTransition() {}


    @Override
    public void handleExitCombatTransitionLoading(boolean combatLost) {

        if (!combatLost) {

            gp.getEntityM().getPlayer().setCol(43);
            gp.getEntityM().getPlayer().setRow(9);
            gp.getEntityM().getPlayer().setDirectionCurrent(EntityDirection.RIGHT);

            gp.getEntityM().getEntityById(4).setCol(45);
            gp.getEntityM().getEntityById(4).setRow(9);
            gp.getEntityM().getEntityById(4).setDirectionCurrent(EntityDirection.LEFT);

            gp.getEntityM().getEntityById(5).setCol(45);
            gp.getEntityM().getEntityById(5).setRow(9);
            gp.getEntityM().getEntityById(5).setDirectionCurrent(EntityDirection.LEFT);

            JsonParser.loadEntityJson(gp, 5);                                                                           // Load Joe for the post-combat cutscene.
            gp.getMapM().getLoadedMap().setMapState(2, false);                                                          // Set map to its post-combat (win) state.
            gp.getPartyS().hideAllPartyMembers(false);                                                                  // Just in case there are party members for whatever reason.

            gp.getCameraS().setOverrideEntityTracking(true);
            gp.getCameraS().setCameraSnap(1424, 288);
        } else {

            gp.getEntityM().getPlayer().setCol(47);
            gp.getEntityM().getPlayer().setRow(25);
            gp.getEntityM().getPlayer().setDirectionCurrent(EntityDirection.UP);

            gp.getEntityM().getEntityById(4).setCol(47);
            gp.getEntityM().getEntityById(4).setRow(8);
            gp.getEntityM().getEntityById(4).setDirectionCurrent(EntityDirection.UP);
            gp.getEntityM().getEntityById(4).resetPrimaryAttributes();                                                  // Restore primary attributes for re-fight.

            gp.getEntityM().getEntityById(5).setCol(47);
            gp.getEntityM().getEntityById(5).setRow(6);
            gp.getEntityM().getEntityById(5).setDirectionCurrent(EntityDirection.DOWN);

            gp.getMapM().getLoadedMap().setMapState(1, false);                                                          // Set map to its post-combat (lose) state.
        }
        gp.getEntityM().getPlayer().resetPrimaryAttributes();                                                           // Note: Secondary attributes of all combating entities is automatically reset in the CombatManager class.

        for (int entityId : gp.getEntityM().getParty().keySet()) {                                                      // Just in case there are party members for whatever reason.

            gp.getEntityM().getParty().get(entityId).resetPrimaryAttributes();
        }
    }


    @Override
    public void concludeExitCombatTransition(boolean combatLost) {

        if (!combatLost) {

            gp.getCutsceneM().initiateCutscene(5);
        }
    }
}
