package cutscene.implementation;

import core.GamePanel;
import core.enumeration.PrimaryGameState;
import cutscene.CutsceneBase;
import entity.enumeration.EntityDirection;
import event.enumeration.FadeState;
import org.joml.Vector3f;
import utility.JsonParser;

/**
 * This class defines logic for immediately loading area 2 from the title screen.
 */
public class Cts_009 extends CutsceneBase {

    // FIELD
    private double counter = 0;


    // CONSTRUCTOR
    public Cts_009(GamePanel gp) {
        super(gp);
    }


    // METHOD
    @Override
    public void run(double dt) {

        switch (scenePhase) {

            case 0:
                gp.getMapM().setSavedMapState(1, 2);
                gp.getMapM().loadMap(2, 0, false);
                JsonParser.loadEntityJson(gp, 5);
                gp.getEntityM().getPlayer().setCol(57);
                gp.getEntityM().getPlayer().setRow(6);
                gp.getEntityM().getEntityById(5).setCol(57);
                gp.getEntityM().getEntityById(5).setRow(6);
                gp.getEntityM().getPlayer().setDirectionCurrent(EntityDirection.LEFT);
                gp.getEntityM().getEntityById(5).setDirectionCurrent(EntityDirection.LEFT);
                gp.getPartyS().addEntityToParty(5, false);
                progressCutscene();
                break;

            case 1:
                counter += dt;
                if (counter >= 1.0) {
                    gp.getSoundS().playTrack(
                            gp.getMapM().getLoadedMap().getTrack(gp.getMapM().getLoadedMap().getMapState()));           // Start playing track here to ensure it doesn't start playing too early.
                    gp.getCameraS().setTrackedEntity(gp.getEntityM().getPlayer().getEntityId());
                    gp.getCameraS().resetCameraSnap();
                    gp.getEntityM().getPlayer().setHidden(false);
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
