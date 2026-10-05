package cutscene.implementation;

import combat.MoveBase;
import core.GamePanel;
import core.enumeration.PrimaryGameState;
import cutscene.CutsceneBase;
import entity.enumeration.EntityDirection;
import event.enumeration.FadeState;
import org.joml.Vector2f;
import org.joml.Vector3f;

import java.util.UUID;

/**
 * This class defines logic for a post-combat cutscene (second area, fight against Rambunctious Shadow and Righteous
 * Shadow).
 */
public class Cts_007 extends CutsceneBase {

    // FIELDS
    private double counter = 0;
    private UUID particleEffectUuid;
    private boolean nickFinalStepTaken = false;
    private boolean loganFinalStepTaken = false;
    private boolean joeFinalStepTaken = false;


    // CONSTRUCTOR
    public Cts_007(GamePanel gp) {
        super(gp);
    }


    // METHODS
    @Override
    public void run(double dt) {

        switch (scenePhase) {

            case 0:
                gp.setPrimaryGameState(PrimaryGameState.DIALOGUE);
                progressCutscene();
                break;

            case 1:
                counter += dt;
                if (counter >= 1.25) {
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
                    gp.getEntityM().getEntityById(9).setHidden(true);
                    gp.getEntityM().getEntityById(10).setHidden(true);
                    gp.getEntityM().getEntityById(9).setCol(37);
                    gp.getEntityM().getEntityById(9).setRow(50);
                    gp.getCameraS().setOverrideEntityTracking(false);
                    gp.getCameraS().setTrackedEntity(9);                                                                // This entity will be used to create a few camera scroll effects.
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
                    gp.getEntityM().getPlayer().setDirectionCurrent(EntityDirection.DOWN);
                    progressCutscene();
                    counter = 0;
                }
                break;

            case 6:
                counter += dt;
                if (counter >= 0.5) {
                    gp.getEntityM().getEntityById(5).setDirectionCurrent(EntityDirection.UP);
                    progressCutscene();
                    counter = 0;
                }
                break;

            case 7:
                counter += dt;
                if (counter >= 1.0) {
                    gp.getDialogueR().initiateConversation(26);
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
                    gp.getEntityM().getPlayer().setDirectionCurrent(EntityDirection.RIGHT);
                    progressCutscene();
                    counter = 0;
                }
                break;

            case 10:
                counter += dt;
                if (counter >= 1.0) {
                    gp.getDialogueR().initiateConversation(27);
                    progressCutscene();
                    counter = 0;
                }
                break;

            case 11:
                if (gp.getDialogueR().getActiveConv() == null) {
                    gp.getEntityM().getEntityById(5).setDirectionCurrent(EntityDirection.RIGHT);
                    gp.getDialogueR().initiateConversation(28);
                    progressCutscene();
                }
                break;

            case 12:
                if (gp.getDialogueR().getActiveConv() == null) {
                    gp.getEntityM().getPlayer().startFollowingPath(45, 49);
                    gp.getEntityM().getEntityById(5).startFollowingPath(45, 51);
                    gp.getEntityM().getEntityById(9).startFollowingPath(45, 50);                                        // For camera scroll effect.
                    progressCutscene();
                }
                break;

            case 13:
                if (gp.getEntityM().getPlayer().getCol() >= 39) {
                    gp.getEntityM().getEntityById(11).startFollowingPath(53, 49);
                    gp.getEntityM().getEntityById(12).startFollowingPath(53, 51);
                    progressCutscene();
                }
                break;

            case 14:
                if (!gp.getEntityM().getPlayer().isOnPath()
                        && !gp.getEntityM().getEntityById(5).isOnPath()
                        && !gp.getEntityM().getEntityById(11).isOnPath()
                        && !gp.getEntityM().getEntityById(12).isOnPath()) {
                    progressCutscene();
                }
                break;

            case 15:
                counter += dt;
                if (counter >= 0.5) {
                    gp.getDialogueR().initiateConversation(29);
                    progressCutscene();
                    counter = 0;
                }
                break;

            case 16:
                if (gp.getDialogueR().getActiveConv() == null) {
                    gp.getEntityM().getEntityById(5).setSpeed(240);
                    gp.getEntityM().getEntityById(5).startFollowingPath(52, 49);
                    progressCutscene();
                }
                break;

            case 17:
                if (gp.getEntityM().getEntityById(5).getCol() >= 47) {
                    gp.setLockPlayerControl(true);
                    gp.getDialogueR().initiateConversation(30);
                    progressCutscene();
                }

            case 18:
                if (!gp.getEntityM().getEntityById(5).isOnPath()) {
                    gp.getEntityM().getEntityById(5).setSpeed(120);
                    gp.setLockPlayerControl(false);
                    progressCutscene();
                }
                break;

            case 19:
                if (gp.getDialogueR().getActiveConv() == null) {
                    progressCutscene();
                }
                break;

            case 20:
                counter += dt;
                if (counter >= 0.5) {
                    gp.getEntityM().getEntityById(12).setDirectionCurrent(EntityDirection.UP);
                    progressCutscene();
                    counter = 0;
                }
                break;

            case 21:
                counter += dt;
                if (counter >= 0.5) {
                    gp.getDialogueR().initiateConversation(31);
                    progressCutscene();
                    counter = 0;
                }
                break;

            case 22:
                if (gp.getDialogueR().getActiveConv() == null) {
                    progressCutscene();
                }
                break;

            case 23:
                counter += dt;
                if (counter >= 0.5) {
                    gp.getEntityM().getEntityById(5).setDirectionCurrent(EntityDirection.DOWN);
                    progressCutscene();
                    counter = 0;
                }
                break;

            case 24:
                counter += dt;
                if (counter >= 1.0) {
                    gp.getEntityM().getEntityById(5).autoStep(EntityDirection.LEFT, true);
                    progressCutscene();
                    counter = 0;
                }
                break;

            case 25:
                if (!gp.getEntityM().getEntityById(5).isMoving()) {
                    gp.getDialogueR().initiateConversation(32);
                    progressCutscene();
                }
                break;

            case 26:
                if (gp.getDialogueR().getActiveConv() == null) {
                    gp.getEntityM().getPlayer().startFollowingPath(45, 51);
                    progressCutscene();
                }
                break;

            case 27:
                if (!gp.getEntityM().getPlayer().isMoving()) {
                    gp.getEntityM().getPlayer().startFollowingPath(51, 51);
                    gp.getEntityM().getEntityById(9).startFollowingPath(52, 50);                                        // For camera scroll effect.
                    progressCutscene();
                }
                break;

            case 28:
                if (gp.getEntityM().getPlayer().getCol() >= 47) {
                    gp.getEntityM().getEntityById(12).setDirectionCurrent(EntityDirection.LEFT);
                    progressCutscene();
                }
                break;

            case 29:
                if (!gp.getEntityM().getPlayer().isMoving() && !gp.getEntityM().getEntityById(9).isMoving()) {
                    gp.getCameraS().setOverrideEntityTracking(true);                                                    // No longer needed for camera scroll effect.
                    progressCutscene();
                }
                break;

            case 30:
                counter += dt;
                if (counter >= 0.25) {
                    gp.getDialogueR().initiateConversation(33);
                    progressCutscene();
                    counter = 0;
                }
                break;

            case 31:
                if (gp.getDialogueR().getActiveConv() == null) {
                    progressCutscene();
                }
                break;

            case 32:
                counter += dt;
                if (counter >= 0.5) {
                    gp.getEntityM().getEntityById(11).setDirectionCurrent(EntityDirection.DOWN);
                    progressCutscene();
                    counter = 0;
                }
                break;

            case 33:
                counter += dt;
                if (counter >= 0.5) {
                    gp.getDialogueR().initiateConversation(34);
                    progressCutscene();
                    counter = 0;
                }
                break;

            case 34:
                if (gp.getDialogueR().getActiveConv() == null) {
                    progressCutscene();
                }
                break;

            case 35:
                counter += dt;
                if (counter >= 0.5) {
                    gp.getEntityM().getPlayer().setDirectionCurrent(EntityDirection.UP);
                    progressCutscene();
                    counter = 0;
                }
                break;

            case 36:
                counter += dt;
                if (counter >= 1.0) {
                    gp.getEntityM().getPlayer().setDirectionCurrent(EntityDirection.RIGHT);
                    progressCutscene();
                    counter = 0;
                }
                break;

            case 37:
                counter += dt;
                if (counter >= 0.5) {
                    gp.getDialogueR().initiateConversation(35);
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
                    gp.getEntityM().getEntityById(11).setDirectionCurrent(EntityDirection.UP);
                    progressCutscene();
                    counter = 0;
                }
                break;

            case 40:
                counter += dt;
                if (counter >= 0.15) {
                    gp.getEntityM().getEntityById(11).setDirectionCurrent(EntityDirection.DOWN);
                    progressCutscene();
                    counter = 0;
                }
                break;

            case 41:
                counter += dt;
                if (counter >= 0.15) {
                    gp.getEntityM().getEntityById(11).setDirectionCurrent(EntityDirection.RIGHT);
                    progressCutscene();
                    counter = 0;
                }
                break;

            case 42:
                counter += dt;
                if (counter >= 0.15) {
                    gp.getEntityM().getEntityById(11).setDirectionCurrent(EntityDirection.LEFT);
                    progressCutscene();
                    counter = 0;
                }
                break;

            case 43:
                counter += dt;
                if (counter >= 0.5) {
                    gp.getDialogueR().initiateConversation(36);
                    progressCutscene();
                    counter = 0;
                }
                break;

            case 44:
                if (gp.getDialogueR().getActiveConv() == null) {
                    progressCutscene();
                }
                break;

            case 45:
                counter += dt;
                if (counter >= 0.0) {
                    gp.getEntityM().getEntityById(5).autoStep(EntityDirection.RIGHT, false);
                    progressCutscene();
                    counter = 0;
                }
                break;

            case 46:
                if (!gp.getEntityM().getEntityById(5).isMoving()) {
                    gp.getEntityM().getEntityById(11).setSpeed(240);
                    gp.getEntityM().getEntityById(11).autoStep(EntityDirection.RIGHT, true);
                    gp.getEntityM().getEntityById(5).autoStep(EntityDirection.LEFT, true);
                    gp.getEntityM().getEntityById(12).setDirectionCurrent(EntityDirection.UP);
                    gp.getSoundS().playEffect("thud");
                    progressCutscene();
                }
                break;

            case 47:
                if (!gp.getEntityM().getEntityById(11).isMoving()) {
                    gp.getEntityM().getEntityById(11).setSpeed(120);
                    progressCutscene();
                }
                break;

            case 48:
                counter += dt;
                if (counter >= 0.5) {
                    gp.getEntityM().getEntityById(11).autoStep(EntityDirection.LEFT, false);
                    progressCutscene();
                    counter = 0;
                }
                break;

            case 49:
                if (!gp.getEntityM().getEntityById(11).isMoving()) {
                    progressCutscene();
                }
                break;

            case 50:
                counter += dt;
                if (counter >= 0.0) {
                    gp.getDialogueR().initiateConversation(37);
                    progressCutscene();
                    counter = 0;
                }
                break;

            case 51:
                if (gp.getDialogueR().getActiveConv() == null) {
                    progressCutscene();
                }
                break;

            case 52:
                counter += dt;
                if (counter >= 0.5) {
                    gp.getEntityM().getEntityById(12).setDirectionCurrent(EntityDirection.LEFT);
                    progressCutscene();
                    counter = 0;
                }
                break;

            case 53:
                counter += dt;
                if (counter >= 0.5) {
                    gp.getDialogueR().initiateConversation(38);
                    progressCutscene();
                    counter = 0;
                }
                break;

            case 54:
                if (gp.getDialogueR().getActiveConv() == null) {
                    progressCutscene();
                }
                break;

            case 55:
                counter += dt;
                if (counter >= 0.0) {
                    gp.getEntityM().getEntityById(12).autoStep(EntityDirection.LEFT, false);
                    progressCutscene();
                    counter = 0;
                }
                break;

            case 56:
                if (!gp.getEntityM().getEntityById(5).isMoving()) {
                    progressCutscene();
                }
                break;

            case 57:
                counter += dt;
                if (counter >= 0.5) {
                    gp.getDialogueR().initiateConversation(39);
                    progressCutscene();
                    counter = 0;
                }
                break;

            case 58:
                if (gp.getDialogueR().getActiveConv() == null) {
                    counter += dt;
                    if (counter >= 0.75) {
                        particleEffectUuid = gp.getParticleEffectM().addParticleEffect(                                                          // Play particle effect animation on target entities.
                                new Vector2f(
                                        gp.getEntityM().getPlayer().getWorldX() + (GamePanel.NATIVE_TILE_SIZE / 2),
                                        gp.getEntityM().getPlayer().getWorldY() + (GamePanel.NATIVE_TILE_SIZE / 4)),
                                MoveBase.ATTRIBUTE_INCREASE_COLOR,
                                4.0f
                        );
                        gp.getSoundS().playEffect("healingSparks");
                        progressCutscene();
                        counter = 0;
                    }
                }

                break;

            case 59:
                if (gp.getParticleEffectM().getParticleEffectByUuid(particleEffectUuid) == null) {
                    progressCutscene();
                }
                break;

            case 60:
                counter += dt;
                if (counter >= 0.5) {
                    gp.getEntityM().getEntityById(12).autoStep(EntityDirection.RIGHT, true);
                    progressCutscene();
                    counter = 0;
                }
                break;

            case 61:
                counter += dt;
                if (counter >= 0.5) {
                    gp.getEntityM().getPlayer().setDirectionCurrent(EntityDirection.DOWN);
                    progressCutscene();
                    counter = 0;
                }
                break;

            case 62:
                counter += dt;
                if (counter >= 0.5) {
                    gp.getEntityM().getPlayer().setDirectionCurrent(EntityDirection.UP);
                    progressCutscene();
                    counter = 0;
                }
                break;

            case 63:
                counter += dt;
                if (counter >= 0.5) {
                    gp.getEntityM().getPlayer().setDirectionCurrent(EntityDirection.RIGHT);
                    progressCutscene();
                    counter = 0;
                }
                break;

            case 64:
                counter += dt;
                if (counter >= 0.5) {
                    gp.getDialogueR().initiateConversation(40);
                    progressCutscene();
                    counter = 0;
                }
                break;

            case 65:
                if (gp.getDialogueR().getActiveConv() == null) {
                    gp.getFadeS().initiateFadeTo(1.0, new Vector3f(0, 0, 0));
                    progressCutscene();
                }
                break;

            case 66:
                if (gp.getFadeS().getState() == FadeState.ACTIVE) {
                    progressCutscene();
                }
                break;

            case 67:
                counter += dt;
                if (counter >= 0.5) {
                    gp.getSoundS().playEffect("heal");
                    progressCutscene();
                    counter = 0;
                }
                break;

            case 68:
                counter += dt;
                if (counter >= 2.5) {
                    gp.getFadeS().initiateFadeFrom(2.0);
                    progressCutscene();
                    counter = 0;
                }
                break;

            case 69:
                if (gp.getFadeS().getState() == FadeState.INACTIVE) {
                    progressCutscene();
                }
                break;

            case 70:
                counter += dt;
                if (counter >= 0.5) {
                    gp.getDialogueR().initiateConversation(41);
                    progressCutscene();
                    counter = 0;
                }
                break;

            case 71:
                if (gp.getDialogueR().getActiveConv() == null) {
                    progressCutscene();
                }
                break;

            case 72:
                counter += dt;
                if (counter >= 0.5) {
                    gp.getEntityM().getEntityById(5).setDirectionCurrent(EntityDirection.DOWN);
                    progressCutscene();
                    counter = 0;
                }
                break;

            case 73:
                counter += dt;
                if (counter >= 0.5) {
                    gp.getDialogueR().initiateConversation(42);
                    progressCutscene();
                    counter = 0;
                }
                break;

            case 74:
                if (gp.getDialogueR().getActiveConv() == null) {
                    progressCutscene();
                }
                break;

            case 75:
                counter += dt;
                if (counter >= 0.5) {
                    gp.getEntityM().getPlayer().setDirectionCurrent(EntityDirection.UP);
                    progressCutscene();
                    counter = 0;
                }
                break;

            case 76:
                counter += dt;
                if (counter >= 0.5) {
                    gp.getEntityM().getPlayer().setDirectionCurrent(EntityDirection.RIGHT);
                    progressCutscene();
                    counter = 0;
                }
                break;

            case 77:
                counter += dt;
                if (counter >= 0.5) {
                    gp.getEntityM().getPlayer().setDirectionCurrent(EntityDirection.UP);
                    progressCutscene();
                    counter = 0;
                }
                break;

            case 78:
                counter += dt;
                if (counter >= 0.5) {
                    gp.getEntityM().getPlayer().setDirectionCurrent(EntityDirection.RIGHT);
                    progressCutscene();
                    counter = 0;
                }
                break;

            case 79:
                counter += dt;
                if (counter >= 0.5) {
                    gp.getDialogueR().initiateConversation(43);
                    progressCutscene();
                    counter = 0;
                }
                break;

            case 80:
                if (gp.getDialogueR().getActiveConv() == null) {
                    gp.getCameraS().setTrackedEntity(gp.getEntityM().getPlayer().getEntityId());
                    gp.getCameraS().resetCameraScroll(1.0f);
                    progressCutscene();
                }
                break;

            case 81:
                if (!gp.getCameraS().isCameraScrolling()) {
                    progressCutscene();
                }
                break;

            case 82:
                counter += dt;
                if (counter >= 0.0) {
                    gp.getEntityM().getEntityById(5).startFollowingPath(49, 51);
                    progressCutscene();
                    counter = 0;
                }
                break;

            case 83:
                initiateFinalStep();
                counter += dt;
                if (counter >= 0.75) {
                    gp.getEntityM().getEntityById(11).startFollowingPath(48, 51);
                    progressCutscene();
                    counter = 0;
                }
                break;

            case 84:
                initiateFinalStep();
                counter += dt;
                if (counter >= 0.75) {
                    gp.getEntityM().getEntityById(12).startFollowingPath(47, 51);
                    progressCutscene();
                    counter = 0;
                }
                break;

            case 85:
                initiateFinalStep();
                if (!gp.getEntityM().getEntityById(5).isMoving()
                        && !gp.getEntityM().getEntityById(11).isMoving()
                        && !gp.getEntityM().getEntityById(12).isMoving()) {
                    if (gp.isTutorialsEnabled()) {
                        generateTutorial();
                        if (gp.getSystemSetting(5).getActiveOption() == 1) {
                            gp.getSoundS().playEffect("progress");
                        }
                    }
                    exitCutscene();
                    resetCutscene();
                }
                break;
        }
    }


    /**
     * Initiates the final step that each non-player entity takes at the end of the cutscene.
     */
    private void initiateFinalStep() {

        if ((gp.getEntityM().getEntityById(5).getCol() == 49)
                && (gp.getEntityM().getEntityById(5).getRow() == 51)
                && !nickFinalStepTaken) {

            gp.getEntityM().getEntityById(5).autoStep(EntityDirection.RIGHT, false);
            nickFinalStepTaken = true;
        }

        if ((gp.getEntityM().getEntityById(11).getCol() == 48)
                && (gp.getEntityM().getEntityById(11).getRow() == 51)
                && !loganFinalStepTaken) {

            gp.getEntityM().getEntityById(11).autoStep(EntityDirection.RIGHT, false);
            loganFinalStepTaken = true;
        }

        if ((gp.getEntityM().getEntityById(12).getCol() == 47)
                && (gp.getEntityM().getEntityById(12).getRow() == 51)
                && !joeFinalStepTaken) {

            gp.getEntityM().getEntityById(12).autoStep(EntityDirection.RIGHT, false);
            joeFinalStepTaken = true;
        }
    }


    /**
     * Generates and displays the party management tutorial (page 1).
     */
    private void generateTutorial() {

        String title = "Party Management Tutorial";
        String subtitle = "Your party's filling up!";
        String content = "This brief tutorial will teach you how to manage multiple party members.";
        int currentPageNumber = 1;
        int totalPageNumbers = 3;
        gp.getTutorialH().generateTutorial(10, title, subtitle, content, currentPageNumber, totalPageNumbers);
    }
}
