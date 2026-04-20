package cutscene.implementation;

import core.GamePanel;
import core.enumeration.PrimaryGameState;
import cutscene.CutsceneBase;
import event.enumeration.FadeState;
import org.joml.Vector3f;

/**
 * This class defines logic for the opening cutscene.
 */
public class Cts_000 extends CutsceneBase {

    // FIELD
    private double counter = 0;


    // CONSTRUCTOR
    public Cts_000(GamePanel gp) {
        super(gp);
    }


    // METHOD
    @Override
    public void run(double dt) {

        switch (scenePhase) {
            case 0:
                gp.getDialogueR().loadConversation(0);
                gp.getDialogueR().loadConversation(1);
                gp.getDialogueR().loadConversation(2);
                gp.getDialogueR().loadConversation(3);
                progressCutscene();
                break;
            case 1:
                counter += dt;
                if (counter >= 1) {
                    gp.setPrimaryGameState(PrimaryGameState.DIALOGUE);
                    gp.getSoundS().playTrack("desolate");
//                    gp.getDialogueR().setStagedPrintCountdown(0.032);
                    gp.getUi().setWindowOpacity(150);
//                    gp.getFadeS().displayColor(new Vector3f(0, 0, 0));
                    gp.getIllustrationS().displayIllustration("illustration1");
                    gp.getFadeS().initiateFadeFrom(3);
                    progressCutscene();
                    counter = 0;
                }
                break;
            case 2:
                if (gp.getFadeS().getState() == FadeState.INACTIVE) {
                    counter += dt;
                    if (counter >= 1) {
                        gp.getDialogueR().initiateConversation(0);
                        progressCutscene();
                        counter = 0;
                    }
                }
                break;
            case 3:
                if (gp.getDialogueR().getActiveConv() == null) {
                    gp.getFadeS().initiateFadeTo(1, new Vector3f(0, 0, 0));
                    progressCutscene();
                }
                break;
            case 4:
                if (gp.getFadeS().getState() == FadeState.ACTIVE) {
                    counter += dt;
                    if (counter >= 0.4) {
                        gp.getIllustrationS().displayIllustration("illustration2");
                        gp.getFadeS().initiateFadeFrom(1);
                        progressCutscene();
                        counter = 0;
                    }
                }
                break;
            case 5:
                if (gp.getFadeS().getState() == FadeState.INACTIVE) {
                    counter += dt;
                    if (counter >= 0.5) {
                        gp.getDialogueR().initiateConversation(1);
                        progressCutscene();
                        counter = 0;
                    }
                }
                break;
            case 6:
                if (gp.getDialogueR().getActiveConv() == null) {
                    gp.getFadeS().initiateFadeTo(1, new Vector3f(0, 0, 0));
                    progressCutscene();
                }
                break;
            case 7:
                if (gp.getFadeS().getState() == FadeState.ACTIVE) {
                    counter += dt;
                    if (counter >= 0.4) {
                        gp.getIllustrationS().displayIllustration("illustration1");
                        gp.getFadeS().initiateFadeFrom(1);
                        progressCutscene();
                        counter = 0;
                    }
                }
                break;
            case 8:
                if (gp.getFadeS().getState() == FadeState.INACTIVE) {
                    counter += dt;
                    if (counter >= 0.5) {
                        gp.getDialogueR().initiateConversation(2);
                        progressCutscene();
                        counter = 0;
                    }
                }
                break;
            case 9:
                if (gp.getDialogueR().getActiveConv() == null) {
                    gp.getFadeS().initiateFadeTo(1, new Vector3f(0, 0, 0));
                    progressCutscene();
                }
                break;
            case 10:
                if (gp.getFadeS().getState() == FadeState.ACTIVE) {
                    counter += dt;
                    if (counter >= 0.4) {
                        gp.getIllustrationS().displayIllustration("illustration3");
                        gp.getFadeS().initiateFadeFrom(1);
                        progressCutscene();
                        counter = 0;
                    }
                }
                break;
            case 11:
                if (gp.getFadeS().getState() == FadeState.INACTIVE) {
                    counter += dt;
                    if (counter >= 0.5) {
                        gp.getDialogueR().initiateConversation(3);
                        progressCutscene();
                        counter = 0;
                    }
                }
                break;
            case 12:
                if (gp.getDialogueR().getActiveConv() == null) {
                    counter += dt;
                    if (counter >= 2) {
                        gp.getFadeS().initiateFadeTo(3, new Vector3f(0, 0, 0));
                        progressCutscene();
                        counter = 0;
                    }
                }
                break;
            case 13:
                if (gp.getFadeS().getState() == FadeState.ACTIVE) {
                    counter += dt;
                    if (counter >= 1) {
                        gp.getIllustrationS().removeIllustration();
                        gp.getEntityM().getPlayer().setHidden(false);
                        gp.getMapM().loadMap(1, 0, true);
                        gp.getFadeS().initiateFadeFrom(2.5);
                        progressCutscene();
                        counter = 0;
                    }
                }
                break;
            case 14:
                if (gp.getFadeS().getState() == FadeState.INACTIVE) {
//                    gp.getDialogueR().resetStagedPrintCountdown();
                    gp.getUi().resetWindowOpacity();
                    gp.setPrimaryGameState(PrimaryGameState.EXPLORE);
                    exitCutscene();
                    resetCutscene();
                }
        }
    }
}
