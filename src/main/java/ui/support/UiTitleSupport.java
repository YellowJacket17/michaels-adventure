package ui.support;

import core.GamePanel;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import render.Renderer;
import render.drawable.Transform;
import render.enumeration.ZIndex;

/**
 * This class contains logic for rendering user interface components related to the title screen.
 */
public class UiTitleSupport {

    // FIELDS
    private final GamePanel gp;

    private float startTextFontScale;

    private Transform tempWorldTransform;

    private Vector4f startWindowColor;

    private Vector3f startTextColorBoundOne;

    private Vector3f startTextColorBoundTwo;

    private Vector3f startTextColor;

    private Vector2f startTextScreenCoords;

    private String startText;

    private float startTextColorWeightChangeRate;

    private float startTextColorWeightUpdateRemaining;

    private float startTextColorWeight;

    private boolean startTextColorWeightIncreasing;


    // CONSTRUCTOR
    /**
     * Constructs a UiTitleSupport instance.
     *
     * @param gp GamePanel instance
     * @param renderer Renderer instance
     */
    public UiTitleSupport(GamePanel gp, Renderer renderer) {
        this.gp = gp;
        init(renderer);
    }


    // METHODS
    /**
     * Updates the state of title user interface components by one frame.
     *
     * @param dt time since last frame (seconds)
     */
    public void update(double dt) {

        startTextColorWeightUpdateRemaining = startTextColorWeightChangeRate * (float)dt;                               // Amount that the start text color weight must change by this frame (oscillating between zero and one, both inclusive).

        if (startTextColorWeightUpdateRemaining < 2) {                                                                  // Ensure an abnormally large change does not cause a long loop of calculations to occur.

            while (startTextColorWeightUpdateRemaining > 0) {

                if (startTextColorWeightIncreasing) {

                    startTextColorWeight += startTextColorWeightUpdateRemaining;

                    if (startTextColorWeight >= 1) {

                        startTextColorWeightUpdateRemaining = startTextColorWeight - 1;
                        startTextColorWeightIncreasing = false;
                        startTextColorWeight = 1;
                    } else {

                        startTextColorWeightUpdateRemaining = 0;
                    }
                } else {
                    startTextColorWeight -= startTextColorWeightUpdateRemaining;

                    if (startTextColorWeight < 0) {

                        startTextColorWeightUpdateRemaining = Math.abs(startTextColorWeight);
                        startTextColorWeightIncreasing = true;
                        startTextColorWeight = 0;
                    } else {

                        startTextColorWeightUpdateRemaining = 0;
                    }
                }
            }
            startTextColor.x = (startTextColorBoundOne.x * (startTextColorWeight))
                    + (startTextColorBoundTwo.x * (1 - startTextColorWeight));
            startTextColor.y = (startTextColorBoundOne.y * (startTextColorWeight))
                    + (startTextColorBoundTwo.y * (1 - startTextColorWeight));
            startTextColor.z = (startTextColorBoundOne.z * (startTextColorWeight))
                    + (startTextColorBoundTwo.z * (1 - startTextColorWeight));
        } else {

            startTextColorWeightUpdateRemaining = 0;
        }
    }


    /**
     * Adds title user interface components to the render pipeline
     *
     * @param renderer Renderer instance
     */
    public void addToRenderPipeline(Renderer renderer) {

        // Start text.
        gp.getCamera().screenCoordsToWorldCoords(startTextScreenCoords, tempWorldTransform.position);
        renderer.addString(
                startText,
                tempWorldTransform.position.x,
                tempWorldTransform.position.y,
                startTextFontScale,
                startTextColor,
                gp.getUi().getStandardBoldFont(),
                ZIndex.SECOND_LAYER
        );
    }


    /**
     * Initializes title user interface components that will not change while the game is running.
     * @param renderer Renderer instance
     */
    private void init(Renderer renderer) {

        // Temporary world coordinates and dimensions.
        Vector2f tempWorldCoords = new Vector2f(0.0f, 0.0f);
        Vector2f tempWorldDimensions = new Vector2f(0.0f, 0.0f);
        tempWorldTransform = new Transform(tempWorldCoords, tempWorldDimensions);

        // Color.
        startWindowColor = new Vector4f(20, 20, 20, 150);
        startTextColorBoundOne = new Vector3f(130, 158, 227);                                                           // One extreme of start text color as it oscillates between two colors.
        startTextColorBoundTwo = new Vector3f(255, 255, 255);                                                           // ^^^
        startTextColor = new Vector3f(0, 0, 0);// "True" start text color that will be displayed.
        startTextColorWeightChangeRate = 0.25f;                                                                         // Start text weight color change per second.
        startTextColorWeightUpdateRemaining = 0;                                                                        // Initialize to zero.
        startTextColorWeight = 1;                                                                                       // Only between zero and one (both inclusive).

        // Text sizing.
        startTextFontScale = 0.20f;
        float startTextCharWorldHeight = renderer.getFont(gp.getUi().getStandardBoldFont())
                .getCharacter('A').getHeight() * gp.getUi().getStandardFontScale();                                     // It doesn't matter which character is used, since all characters in a font have the same height.
        float startTextCharScreenHeight = gp.getCamera().worldHeightToScreenHeight(startTextCharWorldHeight);

        // Start text coordinates and content.
        startText = "PRESS ENTER";
        float startTextWorldWidth =
                gp.getUi().calculateStringWorldWidth(startText, startTextFontScale, gp.getUi().getStandardBoldFont());
        float startTextScreenWidth = gp.getCamera().worldWidthToScreenWidth(startTextWorldWidth);
        float startTextWorldRightAdjustment = 15.0f;
        float startTextScreenRightAdjustment = gp.getCamera().worldWidthToScreenWidth(startTextWorldRightAdjustment);
        float startTextWorldBottomAdjustment = 15.0f;
        float startTextScreenBottomAdjustment =
                gp.getCamera().worldHeightToScreenHeight(startTextWorldBottomAdjustment);
        float startTextScreenX = 1 - startTextScreenWidth - startTextScreenRightAdjustment;
        float startTextScreenY = 1 - startTextCharScreenHeight - startTextScreenBottomAdjustment;
        startTextScreenCoords = new Vector2f(startTextScreenX, startTextScreenY);
    }
}
