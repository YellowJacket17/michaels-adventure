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

        EntityBase entity = gp.getEntityById(4);

        if (entity != null) {                                                                                           // Ensure the target entity continues to exist, otherwise the cutscene will automatically end as a precaution.

            switch (scenePhase) {

                case 0:
                    gp.getInteractionM().setEntityFollowPath(4, 27, 23);
                    scenePhase = 1;
                    break;

                case 1:
                    if (!entity.isOnPath()) {
                        endCutscene(true);
                        gp.playSE(0);
                        gp.getInteractionM().displayMessage("Cutscene test is complete!");
                    }
                    break;
            }
        } else {

            endCutscene(false);
        }
    }
}
