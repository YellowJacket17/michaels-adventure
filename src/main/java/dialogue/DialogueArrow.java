package dialogue;

import core.GamePanel;
import org.joml.Vector2f;
import render.Renderer;
import render.enumeration.ZIndex;
import render.drawable.Drawable;
import asset.AssetPool;
import utility.UtilityTool;

/**
 * This class defines the dialogue progression arrow, which appears when a piece of dialogue has more text to be read
 * that did not all fit on the screen all at once.
 */
public class DialogueArrow extends Drawable {

    // FIELDS
    GamePanel gp;

    /**
     * Controls the up-and-down animation of the dialogue arrow.
     */
    private double rest;

    /**
     * Boolean tracking the up-and-down movement of the dialogue arrow when being drawn.
     */
    private boolean isUpPosition = false;

    /**
     * Boolean tracking whether a render error has occurred.
     * If true, this prevents a render error from repeatedly being printed to the console.
     */
    private boolean renderError = false;


    // CONSTRUCTOR
    /**
     * Constructs a DialogueArrow instance.
     *
     * @param gp GamePanel instance
     */
    public DialogueArrow(GamePanel gp) {
        super();
        this.gp = gp;
        this.sprite = AssetPool.getSpritesheet("miscellaneous").getSprite(1);
        this.transform.scale.x = this.sprite.getNativeWidth();
        this.transform.scale.y = this.sprite.getNativeHeight();
    }


    // METHODS
    /**
     * Updates the state of the dialogue arrow by one frame.
     *
     * @param dt time since last frame (seconds)
     */
    public void update(double dt) {

        rest -= dt;

        if (rest <= 0) {

            while (rest <= 0) {

                isUpPosition = !isUpPosition;
                rest += 0.5;                                                                                            // Force the arrow to wait 0.5 seconds before moving again.
            }
        }
    }


    /**
     * Adds the dialogue arrow to the render pipeline.
     *
     * @param renderer Renderer instance
     * @param screenX screen x-coordinate of the dialogue arrow (leftmost, normalized between 0 and 1)
     * @param screenY screen y-coordinate of the dialogue arrow (topmost, normalized between 0 and 1)
     */
    public void addToRenderPipeline(Renderer renderer, float screenX, float screenY) {

        if (!isUpPosition) {

            screenY += 0.005;                                                                                           // Set the dialogue arrow to its "down" position.
        }

        if (sprite != null) {

            Vector2f worldCoords = gp.getCamera().screenCoordsToWorldCoords(new Vector2f(screenX, screenY));
            this.transform.position.x = worldCoords.x;
            this.transform.position.y = worldCoords.y;
            renderer.addDrawable(this, ZIndex.SECOND_LAYER);
        } else if (!renderError) {

            UtilityTool.logError("Failed to add dialogue arrow to the render pipeline: sprite may not have been properly loaded upon initialization.");
            renderError = true;
        }
    }


    /**
     * Resets the dialogue arrow to its default state.
     */
    public void reset() {

        isUpPosition = false;
        rest = 0;
    }
}
