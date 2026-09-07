package cutscene.implementation;

import core.GamePanel;
import core.enumeration.PrimaryGameState;
import cutscene.CutsceneBase;
import entity.enumeration.EntityDirection;
import entity.enumeration.FadeEffectType;
import event.enumeration.FadeState;
import org.joml.Vector3f;

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
                    gp.getDialogueR().initiateConversation(3);
                    progressCutscene();
                }
                break;

            case 3:
                if (gp.getDialogueR().getActiveConv() == null) {
                    gp.getCameraS().setCameraScroll(1520, 224, 2.6f);
                    progressCutscene();
                }
                break;

            case 4:
                if (!gp.getCameraS().isCameraScrolling()) {
                    progressCutscene();
                }
                break;

            case 5:
                counter += dt;
                if (counter>= 0.25) {
                    gp.getEntityM().getEntityById(4).autoStep(EntityDirection.UP, false);
                    progressCutscene();
                    counter = 0;
                }
                break;

            case 6:
                if (!gp.getEntityM().getEntityById(4).isMoving()) {
                    progressCutscene();
                }
                break;

            case 7:
                counter += dt;
                if (counter >= 0.25) {
                    gp.getEntityM().getEntityById(5).setSpeed(240);
                    gp.getEntityM().getEntityById(5).startFollowingPath(49, 8);
                    progressCutscene();
                    counter = 0;
                }
                break;

            case 8:
                if (!gp.getEntityM().getEntityById(5).isOnPath()) {
                    progressCutscene();
                }
                break;

            case 9:
                counter += dt;
                if (counter >= 0.25) {
                    gp.getEntityM().getEntityById(5).initiateFadeEffect(FadeEffectType.FADE_DOWN, 0.20, true);
                    gp.getEntityM().getEntityById(5).setSpeed(120);
                    gp.getSoundS().playEffect("footsteps");
                    progressCutscene();
                    counter = 0;
                }
                break;

            case 10:
                counter += dt;
                if (counter >= 1.5) {
                    gp.getCameraS().setOverrideEntityTracking(false);
                    gp.getCameraS().setTrackedEntity(4);
                    gp.getEntityM().getEntityById(4).setDirectionCurrent(EntityDirection.LEFT);
                    progressCutscene();
                    counter = 0;
                }
                break;

            case 11:
                counter += dt;
                if (counter >= 1.0) {
                    gp.getEntityM().getEntityById(4).setDirectionCurrent(EntityDirection.DOWN);
                    progressCutscene();
                    counter = 0;
                }
                break;

            case 12:
                counter += dt;
                if (counter >= 1.0) {
                    gp.getEntityM().getEntityById(4).startFollowingPath(47, 12);
                    gp.getEntityM().getPlayer().autoStep(EntityDirection.DOWN, true);
                    gp.setLockPlayerControl(true);
                    gp.getDialogueR().initiateConversation(4);
                    progressCutscene();
                    counter = 0;
                }
                break;

            case 13:
                if (!gp.getEntityM().getEntityById(4).isOnPath()) {
                    gp.setLockPlayerControl(false);
                    progressCutscene();
                }
                break;

            case 14:
                if (gp.getDialogueR().getActiveConv() == null) {
                    gp.getDialogueR().initiateConversation(5);
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
                if (!gp.getEntityM().getPlayer().isMoving() && !gp.getEntityM().getEntityById(4).isMoving()) {
                    gp.getDialogueR().initiateConversation(6);
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
                if (!gp.getEntityM().getPlayer().isMoving() && !gp.getEntityM().getEntityById(4).isMoving()) {
                    gp.getDialogueR().initiateConversation(7);
                    progressCutscene();
                }
                break;

            case 19:
                if (gp.getDialogueR().getActiveConv() == null) {
                    gp.getEntityM().getPlayer().autoStep(EntityDirection.DOWN, true);
                    gp.getEntityM().getEntityById(4).autoStep(EntityDirection.DOWN, false);
                    progressCutscene();
                }
                break;

            case 20:
                if (!gp.getEntityM().getPlayer().isMoving() && !gp.getEntityM().getEntityById(4).isMoving()) {
                    gp.getDialogueR().initiateConversation(8);
                    progressCutscene();
                }
                break;

            case 21:
                if (gp.getDialogueR().getActiveConv() == null) {
                    gp.getEntityM().getPlayer().autoStep(EntityDirection.DOWN, true);
                    gp.getEntityM().getEntityById(4).autoStep(EntityDirection.DOWN, false);
                    progressCutscene();
                }
                break;

            case 22:
                if (!gp.getEntityM().getPlayer().isMoving() && !gp.getEntityM().getEntityById(4).isMoving()) {
                    gp.getDialogueR().initiateConversation(9);
                    progressCutscene();
                }
                break;

            case 23:
                if (gp.getDialogueR().getActiveConv() == null) {
                    gp.getEntityM().getPlayer().autoStep(EntityDirection.DOWN, true);
                    gp.getEntityM().getEntityById(4).autoStep(EntityDirection.DOWN, false);
                    progressCutscene();
                }
                break;

            case 24:
                if (!gp.getEntityM().getPlayer().isMoving() && !gp.getEntityM().getEntityById(4).isMoving()) {
                    gp.getDialogueR().initiateConversation(10);
                    progressCutscene();
                }
                break;

            case 25:
                if (gp.getDialogueR().getActiveConv() == null) {
                    gp.getEntityM().getPlayer().autoStep(EntityDirection.UP, false);
                    progressCutscene();
                }
                break;

            case 26:
                if (!gp.getEntityM().getPlayer().isMoving() && !gp.getEntityM().getEntityById(4).isMoving()) {
                    gp.getDialogueR().initiateConversation(11);
                    progressCutscene();
                }
                break;

            case 27:
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
