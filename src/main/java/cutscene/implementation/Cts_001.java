package cutscene.implementation;

import combat.enumeration.EnterCombatTransitionType;
import core.enumeration.PrimaryGameState;
import cutscene.CutsceneBase;
import entity.EntityBase;
import core.GamePanel;
import entity.enumeration.DefaultAction;

/**
 * This class defines logic for a test cutscene.
 */
public class Cts_001 extends CutsceneBase {

    // FIELD
    private double counter = 0;


    // CONSTRUCTOR
    public Cts_001(GamePanel gp) {
        super(gp);
    }


    // METHOD
    @Override
    public void run(double dt) {

        switch (scenePhase) {
            case 0:
                gp.setPrimaryGameState(PrimaryGameState.DIALOGUE);
                progressCutscene();
                break;
            case 1:
                if (!gp.getEntityM().getPlayer().isMoving()) {
                    gp.getCameraS().setCameraScroll(1520, 256, 1.5f);
                    progressCutscene();
                }
                break;
            case 2:
                if (!gp.getCameraS().isCameraScrolling()) {
                    gp.getCameraS().setOverrideEntityTracking(false);
                    gp.getCameraS().setTrackedEntity(1);
                    gp.getDialogueR().initiateConversation(4);
                    progressCutscene();
                }
                break;
            case 3:
                if (gp.getDialogueR().getActiveConv() == null) {
                    gp.getEntityM().getEntityById(1).setDefaultAction(DefaultAction.STATIC);
                    gp.getEntityM().getEntityById(1).startFollowingPath(47, 12);
                    gp.setLockPlayerControl(true);
                    gp.getDialogueR().initiateConversation(5);
                    progressCutscene();
                }
                break;
            case 4:
                if (!gp.getEntityM().getEntityById(1).isOnPath()) {
                    gp.setLockPlayerControl(false);
                    progressCutscene();
                }
                break;
            case 5:
                if (gp.getDialogueR().getActiveConv() == null) {
                    gp.getDialogueR().initiateConversation(6);
                    progressCutscene();
                }
                break;
            case 6:
                if (gp.getDialogueR().getActiveConv() == null) {
                    gp.getCombatM().initiateCombat(43, 9, EnterCombatTransitionType.BASIC, "runningLate",
                            gp.getEntityM().getEntityById(1));
                    exitCutscene();
                    resetCutscene();
                }
        }
    }
}
