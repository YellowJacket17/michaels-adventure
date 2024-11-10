package miscellaneous;

import asset.AssetPool;
import core.GamePanel;
import org.joml.Vector2f;
import render.Renderer;
import render.drawable.Drawable;
import render.enumeration.ZIndex;
import utility.UtilityTool;

/**
 * This class defines the ellipsis that appears when scrolling is available in the party menu screen.
 */
public class ScrollEllipsis extends Drawable {

    // FIELDS
    GamePanel gp;

    /**
     * Boolean tracking whether a render error has occurred.
     * If true, this prevents a render error from repeatedly being printed to the console.
     */
    private boolean renderError = false;


    // CONSTRUCTOR

    /**
     * Constructs a ScrollEllipses instance.
     *
     * @param gp GamePanel instance
     */
    public ScrollEllipsis(GamePanel gp) {
        this.gp = gp;
        this.sprite = AssetPool.getSpritesheet("miscellaneous").getSprite(3);
        this.transform.scale.x = this.sprite.getNativeWidth();
        this.transform.scale.y = this.sprite.getNativeHeight();
    }


    // METHOD
    /**
     * Adds the scroll ellipses to the render pipeline.
     *
     * @param renderer Renderer instance
     * @param screenX screen x-coordinate of the ellipses (leftmost, normalized from 0 to 1, both inclusive)
     * @param screenY screen y-coordinate of the ellipses (topmost, normalized from 0 to 1, both inclusive)
     */
    public void addToRenderPipeline(Renderer renderer, float screenX, float screenY) {

        if (sprite != null) {

            Vector2f worldCoords = gp.getCamera().screenCoordsToWorldCoords(new Vector2f(screenX, screenY));
            this.transform.position.x = worldCoords.x;
            this.transform.position.y = worldCoords.y;
            renderer.addDrawable(this, ZIndex.FIRST_LAYER);
        } else if (!renderError) {

            UtilityTool.logError("Failed to add scroll ellipses to the render pipeline: sprite may not have been properly loaded upon initialization.");
            renderError = true;
        }
    }
}
