package cutscene.implementation;

import core.GamePanel;
import cutscene.CutsceneBase;
import event.enumeration.FadeState;
import miscellaneous.KeyListener;
import org.joml.Vector3f;

import java.util.List;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_E;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_ENTER;

/**
 * This class defines logic for progressing past the title screen.
 */
public class Cts_003 extends CutsceneBase {

    // FIELD
    private double counter = 0;


    // CONSTRUCTOR
    public Cts_003(GamePanel gp) {
        super(gp);
    }


    // METHOD
    @Override
    public void run(double dt) {

        switch (scenePhase) {
            case 0:
                gp.getFadeS().initiateFadeTo(1.5, new Vector3f(0, 0, 0));
                progressCutscene();
                break;
            case 1:
                if (gp.getFadeS().getState() == FadeState.ACTIVE) {
                    gp.getIllustrationS().removeIllustration();
                    gp.getSoundS().stopTrack(true);
                    progressCutscene();
                }
                break;
            case 2:
                if (gp.getFadeS().getState() == FadeState.ACTIVE) {
                    counter += dt;
                    if (counter >= 0.5) {
                        List<String> options = List.of("Yes", "No");                                                    // Immutable list.
                        String prompt = "Would you like to view the opening cutscene?";
                        gp.getSubMenuS().displaySubMenuPrompt(prompt, options, 4, true);
                        exitCutscene();
                        resetCutscene();
                        counter = 0;
                    }
                }
                break;
        }
    }
}
