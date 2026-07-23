package cutscene.implementation;

import asset.AssetPool;
import core.enumeration.PrimaryGameState;
import cutscene.CutsceneBase;
import core.GamePanel;
import event.enumeration.FadeState;
import org.joml.Vector2f;
import org.joml.Vector3f;
import render.drawable.Drawable;
import render.enumeration.ZIndex;
import render.font.Text;

/**
 * This class defines logic for initial loading sequence of game.
 */
public class Cts_002 extends CutsceneBase {

    // FIELDS
    private double counter = 0;

    private Text engineLogoText;

    private Drawable engineLogoImage;


    // CONSTRUCTOR
    public Cts_002(GamePanel gp) {
        super(gp);
    }


    // METHODS
    @Override
    public void run(double dt) {

        switch (scenePhase) {

            case 0:
                initEngineLogoText();
                initEngineLogoImage();
                gp.getFadeS().displayColor(new Vector3f(255, 255, 255));
                gp.getFadeS().initiateFadeFrom(1);
                stageEngineLogoText();
                stageEngineLogoImage();
                progressCutscene();
                break;

            case 1:
                stageEngineLogoText();
                stageEngineLogoImage();
                if (gp.getFadeS().getState() == FadeState.INACTIVE) {
                    counter += dt;
                    if (counter >= 3.0) {
                        gp.getFadeS().initiateFadeTo(1, new Vector3f(0, 0, 0));
                        progressCutscene();
                        counter = 0;
                    }
                }
                break;

            case 2:
                stageEngineLogoText();
                stageEngineLogoImage();
                if (gp.getFadeS().getState() == FadeState.ACTIVE) {
                    progressCutscene();
                }
                break;

            case 3:
                counter += dt;
                if (counter >= 1.0) {
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
     * Initializes engine logo text.
     */
    private void initEngineLogoText() {

        String engineLogoTextContent = "Yellow Cherry Engine";
        float engineLogoTextScale = 0.30f;
        String engineLogoTextFont = gp.getUi().getStandardBoldFont();
        Vector2f engineLogoTextScreenCoords = new Vector2f();
        engineLogoTextScreenCoords.x =
                gp.getUi().calculateStringCenteredScreenX(
                        engineLogoTextContent,
                        engineLogoTextScale,
                        engineLogoTextFont
                );
        float standardBoldCharWorldHeight = gp.getRenderer().getFont(gp.getUi().getStandardBoldFont())
                .getCharacter('A').getHeight() * gp.getUi().getStandardFontScale();                                     // It doesn't matter which character is used, since all characters in a font have the same height.
        float standardBoldCharScreenHeight =
                gp.getCamera().worldHeightToScreenHeight(standardBoldCharWorldHeight);
        engineLogoTextScreenCoords.y = 0.42f - standardBoldCharScreenHeight;
        Vector3f engineLogoTextColor = new Vector3f(149, 223, 255); // 255, 236, 100
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


    /**
     * Initializes engine logo image.
     */
    private void initEngineLogoImage() {

        engineLogoImage = new Drawable(AssetPool.getSpritesheet("miscellaneous").getSprite(6));
        engineLogoImage.transform.scale.x = engineLogoImage.getNativeSpriteWidth() * 2;
        engineLogoImage.transform.scale.y = engineLogoImage.getNativeSpriteHeight() * 2;
    }


    /**
     * Stages engine logo image to be added to the render pipeline.
     */
    private void stageEngineLogoImage() {

        engineLogoImage.transform.position.x =
                gp.getCamera().screenXToWorldX(
                        (1 - gp.getCamera().worldWidthToScreenWidth(engineLogoImage.transform.scale.x)) / 2
                );
        engineLogoImage.transform.position.y =
                gp.getCamera().screenYToWorldY(0.50f);
        gp.getUi().stageNonUiDrawable(engineLogoImage);
    }
}
