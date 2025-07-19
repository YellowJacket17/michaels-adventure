package combat;

import asset.AssetPool;
import combat.enumeration.BannerColor;
import core.GamePanel;
import org.joml.Vector2f;
import render.Renderer;
import render.drawable.Drawable;
import render.enumeration.ZIndex;
import utility.UtilityTool;

/**
 * This class defines the life banner background, which appears above an entity during combat as the background of their
 * life point meter.
 */
public class LifeBannerBackground extends Drawable {

    // FIELDS
    private final GamePanel gp;

    /**
     * Last rendered banner color.
     */
    private BannerColor lastBannerColor = BannerColor.STANDARD;

    /**
     * Boolean tracking whether a render error has occurred. If true, this prevents a render error from repeatedly being
     * printed to the console.
     */
    private boolean renderError = false;


    // CONSTRUCTOR
    /**
     * Constructs a LifeBannerBackground instance.
     *
     * @param gp GamePanel instance
     */
    public LifeBannerBackground(GamePanel gp) {
        super();
        this.gp = gp;
        this.sprite = AssetPool.getSpritesheet("miscellaneous").getSprite(1);
        this.transform.scale.x = this.sprite.getNativeWidth();
        this.transform.scale.y = this.sprite.getNativeHeight();
        this.color.x = 20;
        this.color.y = 20;
        this.color.z = 20;
        this.color.w = 220;
    }


    // METHOD
    /**
     * Adds the life banner background to the render pipeline.
     *
     * @param renderer Renderer instance
     * @param screenX screen x-coordinate of the background (leftmost, normalized from 0 to 1, both inclusive)
     * @param screenY screen y-coordinate of the background (topmost, normalized from 0 to 1, both inclusive)
     * @param bannerColor banner background color
     */
    public void addToRenderPipeline(Renderer renderer, float screenX, float screenY, BannerColor bannerColor) {

        if (sprite != null) {

            if (lastBannerColor != bannerColor) {

                switch (bannerColor) {
                    case STANDARD:
                        this.color.x = 20;
                        this.color.y = 20;
                        this.color.z = 20;
                        break;
                    case TARGET:
                        this.color.x = 255;
                        this.color.y = 170;
                        this.color.z = 100;
                        break;
                    case TURN:
                        this.color.x = 100;
                        this.color.y = 193;
                        this.color.z = 255;
                        break;
                }
                lastBannerColor = bannerColor;
            }
            Vector2f worldCoords = gp.getCamera().screenCoordsToWorldCoords(new Vector2f(screenX, screenY));
            this.transform.position.x = worldCoords.x;
            this.transform.position.y = worldCoords.y;
            renderer.addDrawable(this, ZIndex.SECOND_LAYER);
        } else if (!renderError) {

            UtilityTool.logError("Failed to add life banner background to the render pipeline: sprite may not have been properly loaded upon initialization.");
            renderError = true;
        }
    }
}
