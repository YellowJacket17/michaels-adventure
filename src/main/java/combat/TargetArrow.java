package combat;

import core.GamePanel;
import org.joml.Vector2f;
import render.Renderer;
import render.ZIndex;
import render.drawable.Drawable;
import asset.AssetPool;
import utility.UtilityTool;

/**
 * This class defines the target selection arrow, which appears when selecting an entity to target with a move.
 */
public class TargetArrow extends Drawable {

    // FIELDS
    GamePanel gp;

    /**
     * Boolean tracking whether a render error has occurred. If true, this prevents a render error from repeatedly being
     * printed to the console.
     */
    private boolean renderError = false;


    // CONSTRUCTOR
    /**
     * Constructs a TargetArrow instance.
     *
     * @param gp GamePanel instance
     */
    public TargetArrow(GamePanel gp) {
        super();
        this.gp = gp;
        this.sprite = AssetPool.getSpritesheet(6).getSprite(2);
        this.transform.scale.x = this.sprite.getNativeWidth();
        this.transform.scale.y = this.sprite.getNativeHeight();
    }


    // METHOD
    /**
     * Adds the target arrow to the render pipeline.
     *
     * @param renderer Renderer instance
     * @param screenX screen x-coordinate of the selection arrow (leftmost, normalized between 0 and 1)
     * @param screenY screen y-coordinate of the selection arrow (topmost, normalized between 0 and 1)
     */
    public void addToRenderPipeline(Renderer renderer, float screenX, float screenY) {

        if (sprite != null) {

            Vector2f worldCoords = gp.getCamera().screenCoordsToWorldCoords(new Vector2f(screenX, screenY));
            this.transform.position.x = worldCoords.x;
            this.transform.position.y = worldCoords.y;
            renderer.addDrawable(this, ZIndex.SECOND_LAYER);
        } else if (!renderError) {

            UtilityTool.logError("Failed to add target arrow to the render pipeline: sprite may not have been properly loaded upon initialization.");
            renderError = true;
        }
    }
}
