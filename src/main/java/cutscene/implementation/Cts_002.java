package cutscene.implementation;

import core.GamePanel;
import cutscene.CutsceneBase;
import event.enumeration.FadeState;
import miscellaneous.KeyListener;
import org.joml.Vector3f;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_E;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_ENTER;

/**
 * This class defines logic for a test cutscene.
 */
public class Cts_002 extends CutsceneBase {

    // FIELD
    private double counter = 0;


    // CONSTRUCTOR
    public Cts_002(GamePanel gp) {
        super(gp);
    }


    // METHOD
    @Override
    public void run(double dt) {

        switch (scenePhase) {
            case 0:
                gp.setLockPlayerControl(true);
                gp.getFadeS().initiateFlash(0.15, 0.15, 0.02, new Vector3f(0, 0, 0));
                progressCutscene();
            case 1:
                if (gp.getFadeS().getState() == FadeState.ACTIVE) {
                    gp.getIllustrationS().displayIllustration("illustration3");
                    progressCutscene();
                }
                break;
            case 2:
                counter += dt;
                if (counter >= 0.3) {
                    progressCutscene();
                    counter = 0;
                }
                break;
            case 3:
                if (KeyListener.isKeyPressed(GLFW_KEY_ENTER)
                        || ((gp.getSystemSetting(4).getActiveOption() == 1) && KeyListener.isKeyPressed(GLFW_KEY_E))) {
                    gp.getFadeS().initiateFlash(0.15, 0.15, 0.02, new Vector3f(0, 0, 0));
                    progressCutscene();
                }
                break;
            case 4:
                if (gp.getFadeS().getState() == FadeState.ACTIVE) {
                    gp.getIllustrationS().removeIllustration();
                    progressCutscene();
                }
                break;
            case 5:
                if (!gp.getFadeS().isFlashActive()) {
                    gp.setLockPlayerControl(false);
                    gp.getEntityM().getPlayer().setInteractionCountdown(
                            gp.getEntityM().getPlayer().getStagedStandardInteractionCountdown());
                    exitCutscene();
                    resetCutscene();
                }
                break;
        }
    }
}
