package cutscene.implementation;

import combat.MoveBase;
import core.GamePanel;
import core.enumeration.PrimaryGameState;
import cutscene.CutsceneBase;
import entity.enumeration.EntityDirection;
import entity.enumeration.FadeEffectType;
import event.enumeration.FadeState;
import org.joml.Vector2f;
import org.joml.Vector3f;

import java.util.UUID;

/**
 * This class defines logic for a post-combat cutscene (first area, fight against Peculiar Shadow).
 */
public class Cts_005 extends CutsceneBase {

    // FIELDS
    private double counter = 0;
    private UUID particleEffectUuid;


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
                gp.getFadeS().initiateFlash(0.2, 0.2, 0.1, new Vector3f(0, 0, 0));
                gp.getSoundS().playEffect("vanish");
                progressCutscene();
                break;

            case 3:
                if (gp.getFadeS().getState() == FadeState.ACTIVE) {
                    gp.getEntityM().getEntityById(4).setHidden(true);
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
                if (counter >= 1.5) {
                    gp.getEntityM().getEntityById(5).initiateFadeEffect(FadeEffectType.FADE_UP, 0.20, true);
//                    gp.getSoundS().playEffect("footsteps");
                    progressCutscene();
                    counter = 0;
                }
                break;

            case 6:
                counter += dt;
                if (counter >= 1.0) {
                    gp.getDialogueR().initiateConversation(12);
                    progressCutscene();
                    counter = 0;
                }
                break;

            case 7:
                if (gp.getDialogueR().getActiveConv() == null) {
                    gp.getEntityM().getPlayer().autoStep(EntityDirection.LEFT, true);
                    progressCutscene();
                }
                break;

            case 8:
                if (!gp.getEntityM().getPlayer().isMoving()) {
                    progressCutscene();
                }
                break;

            case 9:
                counter += dt;
                if (counter >= 0.5) {
                    gp.getDialogueR().initiateConversation(13);
                    progressCutscene();
                    counter = 0;
                }
                break;

            case 10:
                if (gp.getDialogueR().getActiveConv() == null) {
                    gp.getEntityM().getEntityById(5).startFollowingPath(45, 9);
                    progressCutscene();
                }
                break;

            case 11:
                counter += dt;
                if (counter >= 0.25) {
                    gp.getEntityM().getPlayer().autoStep(EntityDirection.RIGHT, false);
                    progressCutscene();
                    counter = 0;
                }
                break;

            case 12:
                if (!gp.getEntityM().getPlayer().isMoving() && !gp.getEntityM().getEntityById(5).isMoving()) {
                    progressCutscene();
                }
                break;

            case 13:
                counter += dt;
                if (counter >= 0.25) {
                    gp.getEntityM().getEntityById(5).setDirectionCurrent(EntityDirection.RIGHT);
                    progressCutscene();
                    counter = 0;
                }
                break;

            case 14:
                counter += dt;
                if (counter >= 0.5) {
                    gp.getEntityM().getEntityById(5).setDirectionCurrent(EntityDirection.DOWN);
                    progressCutscene();
                    counter = 0;
                }
                break;

            case 15:
                counter += dt;
                if (counter >= 0.5) {
                    gp.getEntityM().getEntityById(5).setDirectionCurrent(EntityDirection.LEFT);
                    gp.getDialogueR().initiateConversation(14);
                    progressCutscene();
                    counter = 0;
                }
                break;

            case 16:
                if (gp.getDialogueR().getActiveConv() == null) {
                    progressCutscene();
                }
                break;

            case 17:
                counter += dt;
                if (counter >= 0.5) {
                    gp.getEntityM().getPlayer().setDirectionCurrent(EntityDirection.LEFT);
                    progressCutscene();
                    counter = 0;
                }
                break;

            case 18:
                counter += dt;
                if (counter >= 1.5) {
                    gp.getEntityM().getPlayer().setDirectionCurrent(EntityDirection.RIGHT);
                    progressCutscene();
                    counter = 0;
                }
                break;

            case 19:
                counter += dt;
                if (counter >= 0.5) {
                    gp.getDialogueR().initiateConversation(15);
                    progressCutscene();
                    counter = 0;
                }
                break;

            case 20:
                if (gp.getDialogueR().getActiveConv() == null) {
                    gp.getEntityM().getEntityById(5).autoStep(EntityDirection.LEFT, false);
                    gp.getFadeS().initiateFadeTo(0.5, new Vector3f(0, 0, 0));
                    progressCutscene();
                }
                break;

            case 21:
                if (gp.getFadeS().getState() == FadeState.ACTIVE) {
                    progressCutscene();
                }
                break;

            case 22:
                counter += dt;
                if (counter >= 0.5) {
                    gp.getEntityM().getEntityById(5).setCol(45);
                    gp.getSoundS().playEffect("heal");
                    progressCutscene();
                    counter = 0;
                }
                break;

            case 23:
                counter += dt;
                if (counter >= 1.0) {
                    gp.getFadeS().initiateFadeFrom(0.5);
                    progressCutscene();
                    counter = 0;
                }
                break;

            case 24:
                if (gp.getFadeS().getState() == FadeState.INACTIVE) {
                    progressCutscene();
                }
                break;

            case 25:
                counter += dt;
                if (counter >= 1.0) {
                    gp.getDialogueR().initiateConversation(16);
                    progressCutscene();
                    counter = 0;
                }
                break;

            case 26:
                if (gp.getDialogueR().getActiveConv() == null) {
                    gp.getEntityM().getPlayer().autoStep(EntityDirection.RIGHT, false);
                    progressCutscene();
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
                    gp.getDialogueR().initiateConversation(17);
                    progressCutscene();
                    counter = 0;
                }

            case 29:
                if (gp.getDialogueR().getActiveConv() == null) {
                    gp.getEntityM().getPlayer().autoStep(EntityDirection.LEFT, true);
                    progressCutscene();
                }
                break;

            case 30:
                if (!gp.getEntityM().getPlayer().isMoving()) {
                    progressCutscene();
                }
                break;

            case 31:
                counter += dt;
                if (counter >= 0.5) {
                    gp.getDialogueR().initiateConversation(18);
                    progressCutscene();
                    counter = 0;
                }

            case 32:
                if (gp.getDialogueR().getActiveConv() == null) {
                    gp.getEntityM().getEntityById(5).setDirectionCurrent(EntityDirection.UP);
                    progressCutscene();
                }
                break;

            case 33:
                counter += dt;
                if (counter >= 0.25) {
                    gp.getEntityM().getPlayer().setDirectionCurrent(EntityDirection.UP);
                    progressCutscene();
                    counter = 0;
                }
                break;

            case 34:
                counter += dt;
                if (counter >= 0.75) {
                    particleEffectUuid = gp.getParticleEffectM().addParticleEffect(                                                          // Play particle effect animation on target entities.
                            new Vector2f(
                                    gp.getEntityM().getEntityById(5).getWorldX() + (GamePanel.NATIVE_TILE_SIZE / 2),
                                    gp.getEntityM().getEntityById(5).getWorldY() - (GamePanel.NATIVE_TILE_SIZE * 2)),
                            MoveBase.MAGIC_MOVE_COLOR,
                            4.0f
                    );
                    gp.getSoundS().playEffect("psychicSwipe");
                    progressCutscene();
                    counter = 0;
                }
                break;

            case 35:
                if (gp.getParticleEffectM().getParticleEffectByUuid(particleEffectUuid) == null) {
                    progressCutscene();
                }
                break;

            case 36:
                counter += dt;
                if (counter >= 1.25) {
                    gp.getEntityM().getPlayer().setDirectionCurrent(EntityDirection.RIGHT);
                    gp.getEntityM().getEntityById(5).setDirectionCurrent(EntityDirection.LEFT);
                    progressCutscene();
                    counter = 0;
                }
                break;

            case 37:
                counter += dt;
                if (counter >= 1.0) {
                    gp.getDialogueR().initiateConversation(19);
                    progressCutscene();
                    counter = 0;
                }
                break;

            case 38:
                if (gp.getDialogueR().getActiveConv() == null) {
                    progressCutscene();
                }
                break;

            case 39:
                counter += dt;
                if (counter >= 0.5) {
                    gp.getEntityM().getEntityById(5).setDirectionCurrent(EntityDirection.DOWN);
                    progressCutscene();
                    counter = 0;
                }
                break;

            case 40:
                counter += dt;
                if (counter >= 1.0) {
                    gp.getEntityM().getEntityById(5).setDirectionCurrent(EntityDirection.UP);
                    progressCutscene();
                    counter = 0;
                }
                break;

            case 41:
                counter += dt;
                if (counter >= 1.0) {
                    gp.getEntityM().getEntityById(5).setDirectionCurrent(EntityDirection.LEFT);
                    progressCutscene();
                    counter = 0;
                }
                break;

            case 42:
                counter += dt;
                if (counter >= 0.0) {
                    gp.getDialogueR().initiateConversation(20);
                    progressCutscene();
                    counter = 0;
                }
                break;

            case 43:
                if (gp.getDialogueR().getActiveConv() == null) {
                    progressCutscene();
                }
                break;

            case 44:
                counter += dt;
                if (counter >= 0.5) {
                    gp.getEntityM().getEntityById(5).setDirectionCurrent(EntityDirection.RIGHT);
                    progressCutscene();
                    counter = 0;
                }
                break;

            case 45:
                counter += dt;
                if (counter >= 1.5) {
                    gp.getEntityM().getEntityById(5).setDirectionCurrent(EntityDirection.LEFT);
                    progressCutscene();
                    counter = 0;
                }
                break;

            case 46:
                counter += dt;
                if (counter >= 0.5) {
                    gp.getDialogueR().initiateConversation(21);
                    progressCutscene();
                    counter = 0;
                }
                break;

            case 47:
                if (gp.getDialogueR().getActiveConv() == null) {
                    progressCutscene();
                }
                break;

            case 48:
                counter += dt;
                if (counter >= 0.0) {
                    gp.getEntityM().getPlayer().startFollowingPath(45, 8);
                    progressCutscene();
                    counter = 0;
                }
                break;

            case 49:
                if (!gp.getEntityM().getPlayer().isOnPath()) {
                    gp.getEntityM().getEntityById(5).setDirectionCurrent(EntityDirection.RIGHT);
                    gp.getCameraS().resetCameraScroll(1.0f);
                    progressCutscene();
                }
                break;

            case 50:
                if (!gp.getCameraS().isCameraScrolling()) {
                    progressCutscene();
                }
                break;

            case 51:
                counter += dt;
                if (counter >= 0.0) {
                    gp.getDialogueR().initiateConversation(22);
                    progressCutscene();
                    counter = 0;
                }
                break;

            case 52:
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
