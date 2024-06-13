package cutscene.implementation;

import cutscene.CutsceneBase;
import entity.EntityBase;
import core.GamePanel;

/**
 * This class define logic for a test cutscene.
 */
public class Cts_Test1 extends CutsceneBase {

    // CONSTRUCTOR
    public Cts_Test1(GamePanel gp) {
        super(gp);
    }


    // METHOD
    @Override
    public void run(double dt) {

        EntityBase entity = gp.getEntityM().getEntityById(4);

        if (entity != null) {                                                                                           // Ensure the target entity continues to exist, otherwise the cutscene will automatically end as a precaution.

            switch (scenePhase) {

                case 0:
                    gp.getEventM().setEntityFollowPath(4, 27, 23);
                    scenePhase = 1;
                    break;

                case 1:
                    if (!entity.isOnPath()) {
                        gp.getSoundS().playEffect("testEffect1");
                        gp.getEventM().displayMessage("Cutscene test is complete!");
                        exitCutscene();
                        resetCutscene();
                    }
                    break;
            }
        } else {

            exitCutscene();
            resetCutscene();
        }
    }
}
