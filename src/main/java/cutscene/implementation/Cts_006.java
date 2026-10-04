package cutscene.implementation;

import core.GamePanel;
import core.enumeration.PrimaryGameState;
import cutscene.CutsceneBase;
import entity.enumeration.EntityDirection;

/**
 * This class defines logic for a pre-combat cutscene (second area, fight against Rambunctious Shadow and Righteous
 * Shadow).
 */
public class Cts_006 extends CutsceneBase {

    // FIELDS
    private double counter = 0;
    private boolean cameraScrollInitiated = false;
    private double cameraScrollDelayCounter = 0;


    // CONSTRUCTOR
    public Cts_006(GamePanel gp) {
        super(gp);
    }


    // METHODS
    @Override
    public void run(double dt) {

        switch (scenePhase) {

            case 0:
                initiateCameraScroll(dt);
                gp.setPrimaryGameState(PrimaryGameState.DIALOGUE);
                gp.getEntityM().getEntityById(5).stopFollowingEntity();
                if (gp.getEntityM().getEntityById(5).getRow() == gp.getEntityM().getPlayer().getRow()) {
                        gp.getEntityM().getEntityById(5).startFollowingPath(
                                38, gp.getEntityM().getPlayer().getRow());
                }
                progressCutscene();
                break;

            case 1:
                initiateCameraScroll(dt);
                if (!gp.getEntityM().getEntityById(5).isOnPath()) {
                    if (gp.getEntityM().getPlayer().getRow() >= 50) {
                        gp.getEntityM().getEntityById(5).startFollowingPath(
                                39, gp.getEntityM().getPlayer().getRow() - 1);
                    } else {
                        gp.getEntityM().getEntityById(5).startFollowingPath(
                                39, gp.getEntityM().getPlayer().getRow() + 1);
                    }
                    progressCutscene();
                }
                break;

            case 2:
                initiateCameraScroll(dt);
                if (!gp.getEntityM().getEntityById(5).isOnPath() && !gp.getCameraS().isCameraScrolling()
                        && cameraScrollInitiated) {
                    progressCutscene();
                }
                break;

            case 3:
                counter += dt;
                if (counter >= 1.0) {
                    gp.getDialogueR().initiateConversation(23);
                    progressCutscene();
                    counter = 0;
                }
                break;

            case 4:
                if (gp.getDialogueR().getActiveConv() == null) {
                    progressCutscene();
                }
                break;

            case 5:
                counter += dt;
                if (counter >= 0.5) {
                    if (gp.getEntityM().getEntityById(5).getRow() > gp.getEntityM().getPlayer().getRow()) {
                        gp.getEntityM().getEntityById(5).setDirectionCurrent(EntityDirection.UP);
                    } else {
                        gp.getEntityM().getEntityById(5).setDirectionCurrent(EntityDirection.DOWN);
                    }
                    progressCutscene();
                    counter = 0;
                }
                break;

            case 6:
                counter += dt;
                if (counter >= 0.5) {
                    gp.getDialogueR().initiateConversation(24);
                    progressCutscene();
                    counter = 0;
                }
                break;

            case 7:
                if (gp.getDialogueR().getActiveConv() == null) {
                    progressCutscene();
                }
                break;

            case 8:
                counter += dt;
                if (counter >= 0.5) {
                    gp.getEntityM().getEntityById(5).setDirectionCurrent(EntityDirection.RIGHT);
                    progressCutscene();
                    counter = 0;
                }
                break;

            case 9:
                counter += dt;
                if (counter >= 1.0) {
                    gp.getDialogueR().initiateConversation(25);
                    progressCutscene();
                    counter = 0;
                }
                break;

            case 10:
                if (gp.getDialogueR().getActiveConv() == null) {
                    gp.getCombatM().initiateCombat(2, 39, 50, "runningLate", 9, 10);
                    exitCutscene();
                    resetCutscene();
                }
                break;
        }
    }


    /**
     * Initiates camera scroll effect if not already initiated and the player entity is not moving.
     *
     * @param dt time since last frame (seconds)
     */
    private void initiateCameraScroll(double dt) {

        if (!cameraScrollInitiated && !gp.getEntityM().getPlayer().isMoving()) {

            cameraScrollDelayCounter += dt;

            if (cameraScrollDelayCounter > 0.5) {

                gp.getCameraS().setCameraScroll(1424, 1600, 2.2f); // 1.4 seconds to match player speed
                cameraScrollInitiated = true;
            }
        }
    }
}
