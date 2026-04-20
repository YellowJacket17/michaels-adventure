package cutscene.implementation;

import core.enumeration.PrimaryGameState;
import cutscene.CutsceneBase;
import core.GamePanel;
import event.enumeration.FadeState;
import org.joml.Vector2f;
import org.joml.Vector3f;
import render.enumeration.ZIndex;
import render.font.Text;

/**
 * This class defines logic for initial loading sequence of game.
 */
public class Cts_002 extends CutsceneBase {

    // FIELDS
    private double counter = 0;

    private Text engineLogoText;


    // CONSTRUCTOR
    public Cts_002(GamePanel gp) {
        super(gp);
    }


    // METHODS
    @Override
    public void run(double dt) {

        switch (scenePhase) {
            case 0:
                buildEngineLogoText();
                gp.getFadeS().displayColor(new Vector3f(255, 255, 255));
                gp.getFadeS().initiateFadeFrom(1.5);
                stageEngineLogoText();
                progressCutscene();
                break;
            case 1:
                stageEngineLogoText();
                if (gp.getFadeS().getState() == FadeState.INACTIVE) {
                    counter += dt;
                    if (counter >= 2.5) {
                        gp.getFadeS().initiateFadeTo(1.5, new Vector3f(0, 0, 0));
                        progressCutscene();
                        counter = 0;
                    }
                }
                break;
            case 2:
                stageEngineLogoText();
                if (gp.getFadeS().getState() == FadeState.ACTIVE) {
                    progressCutscene();
                }
                break;
            case 3:
                counter += dt;
                if (counter >= 1) {
                    gp.setPrimaryGameState(PrimaryGameState.TITLE);
                    gp.getIllustrationS().displayIllustration("illustration0");
                    gp.getFadeS().initiateFadeFrom(1.5);
                    gp.getSoundS().playTrack("outOfTheBlue");
                    exitCutscene();
                    resetCutscene();
                    counter = 0;
                }
                break;
        }
    }


    /**
     * Builds engine logo text.
     */
    private void buildEngineLogoText() {

        String engineLogoTextContent = "Yellow Jacket Engine";
        float engineLogoTextScale = 0.30f;
        String engineLogoTextFont = gp.getUi().getStandardBoldFont();
        Vector2f engineLogoTextScreenCoords = new Vector2f();
        gp.getUi().calculateStringCenteredScreenCoords(
                engineLogoTextContent,
                engineLogoTextScale,
                engineLogoTextFont,
                engineLogoTextScreenCoords
        );
        Vector3f engineLogoTextColor = new Vector3f(255, 236, 100);
        engineLogoText = new Text(
                engineLogoTextContent,
                engineLogoTextScreenCoords.x,
                engineLogoTextScreenCoords.y,
                engineLogoTextScale,
                engineLogoTextColor,
                engineLogoTextFont,
                ZIndex.SECOND_LAYER
        );
    }


    /**
     * Stages engine logo text to be added to the render pipeline.
     */
    private void stageEngineLogoText() {

        gp.getUi().stageNonUiText(
                engineLogoText.getText(),
                engineLogoText.getX(),
                engineLogoText.getY(),
                engineLogoText.getScale(),
                engineLogoText.getColor(),
                engineLogoText.getFont(),
                engineLogoText.getzIndex()
        );
    }
}
