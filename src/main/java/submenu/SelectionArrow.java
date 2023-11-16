package submenu;

import core.GamePanel;
import org.joml.Vector2f;
import render.Renderer;
import render.drawable.Drawable;
import utility.AssetPool;
import utility.UtilityTool;

/**
 * This class defines the sub-menu selection arrow, which appears when the sub-menu appears with a list of options for
 * the player to select.
 */
public class SelectionArrow extends Drawable {

    // FIELDS
    GamePanel gp;

    /**
     * Boolean tracking whether a draw error has occurred. If true, this prevents a draw error from repeatedly being
     * printed to the console.
     */
    private boolean drawError = false;


    // CONSTRUCTOR
    /**
     * Constructs a SelectionArrow instance.
     *
     * @param gp GamePanel instance
     */
    public SelectionArrow(GamePanel gp) {
        super();
        this.gp = gp;
        this.sprite = AssetPool.getSpritesheet(6).getSprite(0);
        this.transform.scale.x = this.sprite.getNativeWidth();
        this.transform.scale.y = this.sprite.getNativeHeight();
    }


    // METHOD
    /**
     * Adds the selection arrow to the render pipeline.
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
            renderer.addDrawable(this);
        } else if (!drawError) {

            UtilityTool.logError("Failed to add selection arrow to the render pipeline: sprite may not have been properly loaded upon initialization.");
            drawError = true;
        }
    }
}
