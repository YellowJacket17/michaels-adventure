package cutscene.implementation;

import core.GamePanel;
import core.enumeration.PrimaryGameState;
import cutscene.CutsceneBase;
import event.enumeration.FadeState;
import org.joml.Vector3f;

/**
 * This class defines logic for directly entering gameplay (skipping opening cutscene).
 */
public class Cts_001 extends CutsceneBase {

    // FIELD
    private double counter = 0;


    // CONSTRUCTOR
    public Cts_001(GamePanel gp) {
        super(gp);
    }


    // METHOD
    @Override
    public void run(double dt) {

        switch (scenePhase) {
            case 0:
                gp.getMapM().loadMap(1, 0, true);
                progressCutscene();
                break;
            case 1:
                counter += dt;
                if (counter >= 1) {
                    gp.getEntityM().getPlayer().setHidden(false);
//                    gp.getFadeS().displayColor(new Vector3f(0, 0, 0));
                    gp.getFadeS().initiateFadeFrom(2.5);
                    progressCutscene();
                    counter = 0;
                }
                break;
            case 2:
                if (gp.getFadeS().getState() == FadeState.INACTIVE) {
                    gp.setPrimaryGameState(PrimaryGameState.EXPLORE);
                    exitCutscene();
                    resetCutscene();
                }
                break;
        }
    }
}
