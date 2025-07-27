package cutscene.implementation;

import combat.enumeration.EnterCombatTransitionType;
import core.enumeration.PrimaryGameState;
import cutscene.CutsceneBase;
import entity.EntityBase;
import core.GamePanel;
import entity.enumeration.DefaultAction;
import entity.enumeration.EntityDirection;

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
                counter += dt;
                if (counter >= 0.5) {
                    progressCutscene();
                    counter = 0;
                }
                break;
            case 2:
                if (!gp.getEntityM().getPlayer().isMoving()) {
                    gp.getCameraS().setCameraScroll(1520, 256, 3.0f);
                    progressCutscene();
                }
                break;
            case 3:
                if (!gp.getCameraS().isCameraScrolling()) {
                    gp.getCameraS().setOverrideEntityTracking(false);
                    gp.getCameraS().setTrackedEntity(1);
                    gp.getDialogueR().initiateConversation(4);
                    progressCutscene();
                }
                break;
            case 4:
                if (gp.getDialogueR().getActiveConv() == null) {
                    counter += dt;
                    if (counter >= 1.0) {
                        gp.getEntityM().getEntityById(1).setDirectionCurrent(EntityDirection.LEFT);
                        progressCutscene();
                        counter = 0;
                    }
                }
                break;
            case 5:
                counter += dt;
                if (counter >= 1.0) {
                    gp.getEntityM().getEntityById(1).setDirectionCurrent(EntityDirection.DOWN);
                    progressCutscene();
                    counter = 0;
                }
                break;
            case 6:
                counter += dt;
                if (counter >= 1.0) {
                    gp.getEntityM().getEntityById(1).startFollowingPath(47, 12);
                    gp.getEntityM().getPlayer().autoStep(EntityDirection.DOWN, true);
                    gp.setLockPlayerControl(true);
                    gp.getDialogueR().initiateConversation(5);
                    progressCutscene();
                    counter = 0;
                }
                break;
            case 7:
                if (!gp.getEntityM().getEntityById(1).isOnPath()) {
                    gp.setLockPlayerControl(false);
                    progressCutscene();
                }
                break;
            case 8:
                if (gp.getDialogueR().getActiveConv() == null) {
                    gp.getDialogueR().initiateConversation(6);
                    progressCutscene();
                }
                break;
            case 9:
                if (gp.getDialogueR().getActiveConv() == null) {
                    gp.getEntityM().getPlayer().autoStep(EntityDirection.DOWN, true);
                    gp.getEntityM().getEntityById(1).autoStep(EntityDirection.DOWN, false);
                    progressCutscene();
                }
                break;
            case 10:
                if (!gp.getEntityM().getPlayer().isMoving() && !gp.getEntityM().getEntityById(1).isMoving()) {
                    gp.getDialogueR().initiateConversation(7);
                    progressCutscene();
                }
                break;
            case 11:
                if (gp.getDialogueR().getActiveConv() == null) {
                    gp.getEntityM().getPlayer().autoStep(EntityDirection.DOWN, true);
                    gp.getEntityM().getEntityById(1).autoStep(EntityDirection.DOWN, false);
                    progressCutscene();
                }
                break;
            case 12:
                if (!gp.getEntityM().getPlayer().isMoving() && !gp.getEntityM().getEntityById(1).isMoving()) {
                    gp.getDialogueR().initiateConversation(8);
                    progressCutscene();
                }
                break;
            case 13:
                if (gp.getDialogueR().getActiveConv() == null) {
                    gp.getEntityM().getPlayer().autoStep(EntityDirection.DOWN, true);
                    gp.getEntityM().getEntityById(1).autoStep(EntityDirection.DOWN, false);
                    progressCutscene();
                }
                break;
            case 14:
                if (!gp.getEntityM().getPlayer().isMoving() && !gp.getEntityM().getEntityById(1).isMoving()) {
                    gp.getDialogueR().initiateConversation(9);
                    progressCutscene();
                }
                break;
            case 15:
                if (gp.getDialogueR().getActiveConv() == null) {
                    gp.getEntityM().getPlayer().autoStep(EntityDirection.UP, false);
                    progressCutscene();
                }
                break;
            case 16:
                if (!gp.getEntityM().getPlayer().isMoving() && !gp.getEntityM().getEntityById(1).isMoving()) {
                    gp.getDialogueR().initiateConversation(10);
                    progressCutscene();
                }
                break;
            case 17:
                if (gp.getDialogueR().getActiveConv() == null) {
                    gp.getCameraS().setTrackedEntity(gp.getEntityM().getPlayer().getEntityId());
                    gp.getCameraS().setOverrideEntityTracking(true);                                                    // Prevent camera from immediately snapping to player entity.
                    gp.getCombatM().initiateCombat(43, 9, EnterCombatTransitionType.BASIC, "runningLate",
                            gp.getEntityM().getEntityById(1));
                    // TODO : Make sure this cutscene cannot be triggered again.
                    //  If te player loses the fight, a different, quicker cutscene should be triggered.
                    //  If the player wins the fight, no cutscene should be triggered.
                    exitCutscene();
                    resetCutscene();
                }
        }
    }
}
