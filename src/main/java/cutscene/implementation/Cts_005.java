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
                if (counter >= 0.5) {
                    progressCutscene();
                    counter = 0;
                }
                break;
            case 2:
                gp.getFadeS().initiateFlash(0.2, 0.2, 0.2, new Vector3f(255, 255, 255));
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
                if (counter >= 0.5f) {
                    gp.getEntityM().getPlayer().autoStep(EntityDirection.LEFT, true);
                    progressCutscene();
                    counter = 0;
                }
                break;
            case 6:
                if (!gp.getEntityM().getPlayer().isMoving()) {
                    gp.getDialogueR().initiateConversation(13);
                    progressCutscene();
                }
                break;
            case 7:
                if (gp.getDialogueR().getActiveConv() == null) {
                    gp.getEntityM().getPlayer().autoStep(EntityDirection.RIGHT, false);
                    progressCutscene();
                }
                break;
            case 8:
                if (!gp.getEntityM().getPlayer().isMoving()) {
                    gp.getDialogueR().initiateConversation(14);
                    progressCutscene();
                }
                break;
            case 9:
                if (gp.getDialogueR().getActiveConv() == null) {
                    progressCutscene();
                }
                break;
            case 10:
                gp.getEntityM().getEntityById(5).autoStep(EntityDirection.LEFT, false);
                progressCutscene();
                break;
            case 11:
                if (gp.getEntityM().getEntityById(5).isMoving()) {
                    progressCutscene();
                }
            case 12:
                counter += dt;
                if (counter >= 0.5f) {
                    progressCutscene();
                    counter = 0;
                }
                break;
            case 13:
                if (gp.getDialogueR().getActiveConv() == null) {
                    gp.getFadeS().initiateFlash(0.2, 0.2, 0.2, MoveBase.ATTRIBUTE_INCREASE_COLOR);
                    gp.getSoundS().playEffect("heal");
                    progressCutscene();
                }
                break;
            case 14:
                if (gp.getFadeS().getState() == FadeState.INACTIVE) {
                    progressCutscene();
                }
                break;
            case 15:
                counter += dt;
                if (counter >= 0.5f) {
                    gp.getEntityM().getEntityById(5).autoStep(EntityDirection.RIGHT, true);
                    progressCutscene();
                    counter = 0;
                }
                break;
            case 16:
                if (!gp.getEntityM().getEntityById(5).isMoving()) {
                    progressCutscene();
                }
                break;
            case 17:
                counter += dt;
                if (counter >= 0.5f) {
                    gp.getEntityM().getPlayer().setDirectionCurrent(EntityDirection.DOWN);
                    progressCutscene();
                    counter = 0;
                }
                break;
            case 18:
                counter += dt;
                if (counter >= 1.0f) {
                    gp.getEntityM().getPlayer().setDirectionCurrent(EntityDirection.UP);
                    progressCutscene();
                    counter = 0;
                }
                break;
            case 19:
                counter += dt;
                if (counter >= 1.0f) {
                    gp.getEntityM().getPlayer().setDirectionCurrent(EntityDirection.RIGHT);
                    gp.getDialogueR().initiateConversation(15);
                    progressCutscene();
                    counter = 0;
                }
                break;
            case 20:
                if (gp.getDialogueR().getActiveConv() == null) {
                    gp.getEntityM().getEntityById(5).setDirectionCurrent(EntityDirection.RIGHT);
                    gp.getDialogueR().initiateConversation(16);
                    progressCutscene();
                }
                break;
            case 21:
                if (gp.getDialogueR().getActiveConv() == null) {
                    gp.getEntityM().getEntityById(5).setDirectionCurrent(EntityDirection.LEFT);
                    gp.getDialogueR().initiateConversation(17);
                    progressCutscene();
                }
                break;
            case 22:
                if (gp.getDialogueR().getActiveConv() == null) {
                    gp.getEntityM().getEntityById(5).setDirectionCurrent(EntityDirection.RIGHT);
                    progressCutscene();
                }
                break;
            case 23:
                counter += dt;
                if (counter >= 1.5) {
                    gp.getEntityM().getEntityById(5).setDirectionCurrent(EntityDirection.LEFT);
                    progressCutscene();
                    counter = 0;
                }
                break;
            case 24:
                counter += dt;
                if (counter >= 0.5) {
                    gp.getDialogueR().initiateConversation(18);
                    progressCutscene();
                    counter = 0;
                }
                break;
            case 25:
                if (gp.getDialogueR().getActiveConv() == null) {
                    gp.getEntityM().getPlayer().startFollowingPath(45, 8);
                    progressCutscene();
                }
                break;
            case 26:
                if (!gp.getEntityM().getPlayer().isOnPath()) {
                    gp.getEntityM().getEntityById(5).setDirectionCurrent(EntityDirection.RIGHT);
                    gp.getCameraS().resetCameraScroll(1.0f);
                    progressCutscene();
                }
                break;
            case 27:
                if (!gp.getCameraS().isCameraScrolling()) {
                    gp.getDialogueR().initiateConversation(19);
                    progressCutscene();
                }
                break;
            case 28:
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
