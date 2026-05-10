package cutscene.implementation;

import core.GamePanel;
import core.enumeration.PrimaryGameState;
import cutscene.CutsceneBase;
import entity.enumeration.EntityDirection;

/**
 * This class defines logic for a pre-combat cutscene (first area, fight against Peculiar Shadow).
 */
public class Cts_004 extends CutsceneBase {

    // FIELD
    private double counter = 0;


    // CONSTRUCTOR
    public Cts_004(GamePanel gp) {
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
                    gp.getCameraS().setCameraScroll(1520, 256, 2.5f);
                    progressCutscene();
                }
                break;
            case 3:
                if (!gp.getCameraS().isCameraScrolling()) {
                    gp.getCameraS().setOverrideEntityTracking(false);
                    gp.getCameraS().setTrackedEntity(4);
                    gp.getDialogueR().initiateConversation(4);
                    progressCutscene();
                }
                break;
            case 4:
                if (gp.getDialogueR().getActiveConv() == null) {
                    counter += dt;
                    if (counter >= 0.75) {
                        gp.getEntityM().getEntityById(4).setDirectionCurrent(EntityDirection.LEFT);
                        progressCutscene();
                        counter = 0;
                    }
                }
                break;
            case 5:
                counter += dt;
                if (counter >= 0.75) {
                    gp.getEntityM().getEntityById(4).setDirectionCurrent(EntityDirection.DOWN);
                    progressCutscene();
                    counter = 0;
                }
                break;
            case 6:
                counter += dt;
                if (counter >= 0.75) {
                    gp.getEntityM().getEntityById(4).startFollowingPath(47, 12);
                    gp.getEntityM().getPlayer().autoStep(EntityDirection.DOWN, true);
                    gp.setLockPlayerControl(true);
                    gp.getDialogueR().initiateConversation(5);
                    progressCutscene();
                    counter = 0;
                }
                break;
            case 7:
                if (!gp.getEntityM().getEntityById(4).isOnPath()) {
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
                    gp.getEntityM().getEntityById(4).autoStep(EntityDirection.DOWN, false);
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
                    gp.getEntityM().getEntityById(4).autoStep(EntityDirection.DOWN, false);
                    progressCutscene();
                }
                break;
            case 12:
                if (!gp.getEntityM().getPlayer().isMoving() && !gp.getEntityM().getEntityById(4).isMoving()) {
                    gp.getDialogueR().initiateConversation(8);
                    progressCutscene();
                }
                break;
            case 13:
                if (gp.getDialogueR().getActiveConv() == null) {
                    gp.getEntityM().getPlayer().autoStep(EntityDirection.DOWN, true);
                    gp.getEntityM().getEntityById(4).autoStep(EntityDirection.DOWN, false);
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
                    gp.getEntityM().getPlayer().autoStep(EntityDirection.DOWN, true);
                    gp.getEntityM().getEntityById(4).autoStep(EntityDirection.DOWN, false);
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
                    gp.getEntityM().getPlayer().autoStep(EntityDirection.DOWN, true);
                    gp.getEntityM().getEntityById(4).autoStep(EntityDirection.DOWN, false);
                    progressCutscene();
                }
                break;
            case 18:
                if (!gp.getEntityM().getPlayer().isMoving() && !gp.getEntityM().getEntityById(1).isMoving()) {
                    gp.getDialogueR().initiateConversation(11);
                    progressCutscene();
                }
                break;
            case 19:
                if (gp.getDialogueR().getActiveConv() == null) {
                    gp.getEntityM().getPlayer().autoStep(EntityDirection.UP, false);
                    progressCutscene();
                }
                break;
            case 20:
                if (!gp.getEntityM().getPlayer().isMoving() && !gp.getEntityM().getEntityById(1).isMoving()) {
                    gp.getDialogueR().initiateConversation(12);
                    progressCutscene();
                }
                break;
            case 21:
                if (gp.getDialogueR().getActiveConv() == null) {
                    gp.getCameraS().setTrackedEntity(gp.getEntityM().getPlayer().getEntityId());
                    gp.getCameraS().setOverrideEntityTracking(true);                                                    // Prevent camera from immediately snapping to player entity.
                    gp.getCombatM().initiateCombat(1, 43, 9, "runningLate", 4);
                    exitCutscene();
                    resetCutscene();
                }
        }
    }
}
