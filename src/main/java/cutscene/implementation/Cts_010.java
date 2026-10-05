package cutscene.implementation;

import core.GamePanel;
import core.enumeration.PrimaryGameState;
import cutscene.CutsceneBase;
import entity.enumeration.EntityDirection;
import event.enumeration.FadeState;
import landmark.enumeration.TallGrassColor;
import landmark.implementation.Ldm_TallGrass1;
import org.joml.Vector3f;
import utility.JsonParser;

/**
 * This class defines logic for immediately loading area 3 from the title screen.
 */
public class Cts_010 extends CutsceneBase {

    // FIELD
    private double counter = 0;


    // CONSTRUCTOR
    public Cts_010(GamePanel gp) {
        super(gp);
    }


    // METHOD
    @Override
    public void run(double dt) {

        switch (scenePhase) {

            case 0:
                gp.getMapM().setSavedMapState(1, 2);
                gp.getMapM().setSavedMapState(2, 2);
                Ldm_TallGrass1.setInstantiationColor(TallGrassColor.GREEN);
                gp.getMapM().loadMap(3, 0, false);
                JsonParser.loadEntityJson(gp, 5);
                JsonParser.loadEntityJson(gp, 11);
                JsonParser.loadEntityJson(gp, 12);
                gp.getEntityM().getPlayer().setCol(14);
                gp.getEntityM().getPlayer().setRow(52);
                gp.getEntityM().getEntityById(5).setCol(14);
                gp.getEntityM().getEntityById(5).setRow(52);
                gp.getEntityM().getEntityById(11).setCol(14);
                gp.getEntityM().getEntityById(11).setRow(52);
                gp.getEntityM().getEntityById(12).setCol(14);
                gp.getEntityM().getEntityById(12).setRow(52);
                gp.getEntityM().getPlayer().setDirectionCurrent(EntityDirection.RIGHT);
                gp.getEntityM().getEntityById(5).setDirectionCurrent(EntityDirection.RIGHT);
                gp.getEntityM().getEntityById(11).setDirectionCurrent(EntityDirection.RIGHT);
                gp.getEntityM().getEntityById(12).setDirectionCurrent(EntityDirection.RIGHT);
                gp.getPartyS().addEntityToParty(5, false);
                gp.getPartyS().addEntityToParty(11, false);
                gp.getPartyS().addEntityToParty(12, false);
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
