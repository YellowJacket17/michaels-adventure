package cutscene.implementation;

import core.GamePanel;
import cutscene.CutsceneBase;
import event.enumeration.FadeState;
import org.joml.Vector3f;

import java.util.List;

/**
 * This class defines logic for progressing past the title screen specially (i.e., selecting a specific state to load
 * the game in).
 */
public class Cts_008 extends CutsceneBase {

    // FIELD
    private double counter = 0;


    // CONSTRUCTOR
    public Cts_008(GamePanel gp) {
        super(gp);
    }


    // METHOD
    @Override
    public void run(double dt) {

        switch (scenePhase) {

            case 0:
                gp.getFadeS().initiateFadeTo(1, new Vector3f(0, 0, 0));
                gp.getSoundS().stopTrack(true);
                gp.getSoundS().playEffect("titleSelect");
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
                    if (counter >= 2.0) {
                        List<String> options = List.of("Area 1 (Waterfall)", "Area 2 (Lake)", "Area 3 (River)");                                                    // Immutable list.
                        String prompt = "Select an area of the game to load."
                                + " (All applicable story progress will be included.)";
                        gp.getSubMenuS().displaySubMenuPrompt(prompt, options, 5, true);
                        exitCutscene();
                        resetCutscene();
                        counter = 0;
                    }
                }
                break;
        }
    }
}
