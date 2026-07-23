package cutscene.implementation;

import combat.MoveBase;
import core.GamePanel;
import core.enumeration.PrimaryGameState;
import cutscene.CutsceneBase;
import entity.enumeration.EntityDirection;
import event.enumeration.FadeState;
import org.joml.Vector3f;

/**
 * This class defines logic for a post-combat cutscene (first area, fight against Peculiar Shadow).
 */
public class Cts_005 extends CutsceneBase {

    // FIELDS
    private double counter = 0;


    // CONSTRUCTOR
    public Cts_005(GamePanel gp) {
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
                if (counter >= 1.0) {
                    progressCutscene();
                    counter = 0;
                }
                break;

            case 2:
                gp.getFadeS().initiateFlash(0.2, 0.2, 0.1, new Vector3f(255, 255, 255));
                progressCutscene();
                break;

            case 3:
                if (gp.getFadeS().getState() == FadeState.ACTIVE) {
                    gp.getEntityM().getEntityById(4).setHidden(true);
                    gp.getEntityM().getEntityById(5).setHidden(false);
                    progressCutscene();
                }
                break;

            case 4:
                if (gp.getFadeS().getState() == FadeState.INACTIVE) {
                    progressCutscene();
                }
                break;

            case 5:
                counter += dt;
                if (counter >= 0.5) {
                    gp.getEntityM().getPlayer().autoStep(EntityDirection.LEFT, true);
                    progressCutscene();
                    counter = 0;
                }
                break;

            case 6:
                if (!gp.getEntityM().getPlayer().isMoving()) {
                    progressCutscene();
                }
                break;

            case 7:
                counter += dt;
                if (counter >= 0.0) {
                    gp.getDialogueR().initiateConversation(12);
                    progressCutscene();
                    counter = 0;
                }
                break;

            case 8:
                if (gp.getDialogueR().getActiveConv() == null) {
                    progressCutscene();
                }
                break;

            case 9:
                counter += dt;
                if (counter >= 0.5) {
                    gp.getEntityM().getEntityById(5).setDirectionCurrent(EntityDirection.DOWN);
                    progressCutscene();
                    counter = 0;
                }
                break;

            case 10:
                counter += dt;
                if (counter >= 0.75) {
                    gp.getEntityM().getEntityById(5).setDirectionCurrent(EntityDirection.UP);
                    progressCutscene();
                    counter = 0;
                }
                break;

            case 11:
                counter += dt;
                if (counter >= 0.75) {
                    gp.getEntityM().getEntityById(5).setDirectionCurrent(EntityDirection.LEFT);
                    progressCutscene();
                    counter = 0;
                }
                break;

            case 12:
                counter += dt;
                if (counter >= 0.5) {
                    gp.getDialogueR().initiateConversation(13);
                    progressCutscene();
                    counter = 0;
                }
                break;

            case 13:
                if (gp.getDialogueR().getActiveConv() == null) {
                    progressCutscene();
                }
                break;

            case 14:
                counter += dt;
                if (counter >= 0.0) {
                    gp.getEntityM().getPlayer().autoStep(EntityDirection.RIGHT, false);
                    progressCutscene();
                    counter = 0;
                }
                break;

            case 15:
                if (!gp.getEntityM().getPlayer().isMoving()) {
                    progressCutscene();
                    counter = 0;
                }
                break;

            case 16:
                counter += dt;
                if (counter >= 0.0) {
                    gp.getDialogueR().initiateConversation(14);
                    progressCutscene();
                    counter = 0;
                }
                break;

            case 17:
                if (gp.getDialogueR().getActiveConv() == null) {
                    progressCutscene();
                }
                break;

            case 18:
                counter += dt;
                if (counter >= 0.5) {
                    gp.getEntityM().getPlayer().autoStep(EntityDirection.LEFT, true);
                    progressCutscene();
                    counter = 0;
                }
                break;

            case 19:
                if (!gp.getEntityM().getPlayer().isMoving()) {
                    progressCutscene();
                }
                break;

            case 20:
                counter += dt;
                if (counter >= 0.5) {
                    gp.getDialogueR().initiateConversation(15);
                    progressCutscene();
                    counter = 0;
                }
                break;

            case 21:
                if (gp.getDialogueR().getActiveConv() == null) {
                    progressCutscene();
                }
                break;

            case 22:
                counter += dt;
                if (counter >= 0.5) {
                    gp.getFadeS().initiateFlash(0.2, 0.2, 0.1, MoveBase.ATTRIBUTE_INCREASE_COLOR);
                    gp.getSoundS().playEffect("heal");
                    progressCutscene();
                    counter = 0;
                }
                break;

            case 23:
                if (gp.getFadeS().getState() == FadeState.INACTIVE) {
                    progressCutscene();
                }
                break;

            case 24:
                counter += dt;
                if (counter >= 0.5) {
                    gp.getEntityM().getPlayer().setDirectionCurrent(EntityDirection.UP);
                    progressCutscene();
                    counter = 0;
                }
                break;

            case 25:
                counter += dt;
                if (counter >= 1.0) {
                    gp.getEntityM().getPlayer().setDirectionCurrent(EntityDirection.DOWN);
                    progressCutscene();
                    counter = 0;
                }
                break;

            case 26:
                counter += dt;
                if (counter >= 1.0) {
                    gp.getEntityM().getPlayer().autoStep(EntityDirection.RIGHT, false);
                    progressCutscene();
                    counter = 0;
                }
                break;

            case 27:
                if (!gp.getEntityM().getPlayer().isMoving()) {
                    progressCutscene();
                }
                break;

            case 28:
                counter += dt;
                if (counter >= 0.5) {
                    gp.getDialogueR().initiateConversation(16);
                    progressCutscene();
                    counter = 0;
                }
                break;

            case 29:
                if (gp.getDialogueR().getActiveConv() == null) {
                    progressCutscene();
                }
                break;

            case 30:
                counter += dt;
                if (counter >= 0.5) {
                    gp.getEntityM().getEntityById(5).setDirectionCurrent(EntityDirection.RIGHT);
                    progressCutscene();
                    counter = 0;
                }
                break;

            case 31:
                counter += dt;
                if (counter >= 1.5) {
                    gp.getEntityM().getEntityById(5).setDirectionCurrent(EntityDirection.LEFT);
                    progressCutscene();
                    counter = 0;
                }
                break;

            case 32:
                counter += dt;
                if (counter >= 0.5) {
                    gp.getDialogueR().initiateConversation(17);
                    progressCutscene();
                    counter = 0;
                }
                break;

            case 33:
                if (gp.getDialogueR().getActiveConv() == null) {
                    progressCutscene();
                }
                break;

            case 34:
                counter += dt;
                if (counter >= 0.0) {
                    gp.getEntityM().getPlayer().startFollowingPath(45, 8);
                    progressCutscene();
                    counter = 0;
                }
                break;

            case 35:
                if (!gp.getEntityM().getPlayer().isOnPath()) {
                    gp.getEntityM().getEntityById(5).setDirectionCurrent(EntityDirection.RIGHT);
                    gp.getCameraS().resetCameraScroll(1.0f);
                    progressCutscene();
                }
                break;

            case 36:
                if (!gp.getCameraS().isCameraScrolling()) {
                    progressCutscene();
                }
                break;

            case 37:
                counter += dt;
                if (counter >= 0.0) {
                    gp.getDialogueR().initiateConversation(18);
                    progressCutscene();
                    counter = 0;
                }
                break;

            case 38:
                if (gp.getDialogueR().getActiveConv() == null) {
                    gp.getPartyS().addEntityToParty(5, true);
                    gp.getPartyS().showActivePartyMembers(true);                                                        // Just in case there are additional party members for whatever reason.
                    gp.getCameraS().setOverrideEntityTracking(false);
                    gp.setPrimaryGameState(PrimaryGameState.EXPLORE);
                    exitCutscene();
                    resetCutscene();
                }
                break;
        }
    }
}
