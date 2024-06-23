package event.support;

import asset.AssetPool;
import asset.Illustration;
import asset.Sprite;
import core.GamePanel;
import org.joml.Vector2f;
import render.Renderer;
import render.drawable.Drawable;
import render.enumeration.ZIndex;

/**
 * This class contains methods to facilitate displaying illustrations.
 * The public methods in this class serve as primary endpoints to use when programming in-game events.
 */
public class IllustrationSupport {

    // FIELDS
    private final GamePanel gp;

    /**
     * Boolean to set whether the game is displaying an illustration (true) or not (false).
     */
    private boolean illustrationActive = false;

    /**
     * Drawable of the displayed illustration.
     */
    private final Drawable drawable = new Drawable();

    /**
     * Normalized screen coordinates (leftmost x and topmost y).
     * Screen coordinates will be set when rendering depending on the aspect ratio of the illustration being displayed.
     */
    private final Vector2f screenCoords = new Vector2f(0, 0);

    /**
     * Name of illustration that is currently being displayed.
     */
    private String displayedIllustrationName = Illustration.NO_ILLUSTRATION;


    // CONSTRUCTOR
    /**
     * Constructs an Illustration instance.
     *
     * @param gp GamePanel instance
     */
    public IllustrationSupport(GamePanel gp) {
        this.gp = gp;
    }


    // METHODS
    /**
     * Adds the displayed illustration to the render pipeline.
     *
     * @param renderer Renderer instance
     */
    public void addToRenderPipeline(Renderer renderer) {

        if (!displayedIllustrationName.equals(Illustration.NO_ILLUSTRATION)) {

            if (drawable.transform.scale.x < GamePanel.NATIVE_SCREEN_WIDTH) {

                float difference = GamePanel.NATIVE_SCREEN_WIDTH - drawable.transform.scale.x;
                screenCoords.x = (difference / 2) / GamePanel.NATIVE_SCREEN_WIDTH;
            }

            if (drawable.transform.scale.y < GamePanel.NATIVE_SCREEN_HEIGHT) {

                float difference = GamePanel.NATIVE_SCREEN_HEIGHT - drawable.transform.scale.y;
                screenCoords.y = (difference / 2) / GamePanel.NATIVE_SCREEN_HEIGHT;
            }
            Vector2f worldCoords = gp.getCamera().screenCoordsToWorldCoords(screenCoords);
            drawable.transform.position.x = worldCoords.x;
            drawable.transform.position.y = worldCoords.y;
            renderer.addDrawable(drawable, ZIndex.SECOND_LAYER);
        }
    }


    /**
     * Displays an illustration.
     *
     * @param resourceName name/title of illustration to display
     */
    public void displayIllustration(String resourceName) {

        illustrationActive = true;
        displayedIllustrationName = resourceName;
        double nativeScreenWidthHeightRatio = (double)GamePanel.NATIVE_SCREEN_WIDTH / GamePanel.NATIVE_SCREEN_HEIGHT;
        double nativeIllustrationWidthHeightRatio =
                (double)AssetPool.getIllustration(resourceName).getSprite().getNativeWidth()
                        / AssetPool.getIllustration(resourceName).getSprite().getNativeHeight();

        if (nativeScreenWidthHeightRatio < nativeIllustrationWidthHeightRatio) {

            drawable.transform.scale.x = GamePanel.NATIVE_SCREEN_WIDTH;
            drawable.transform.scale.y = GamePanel.NATIVE_SCREEN_WIDTH / (float)nativeIllustrationWidthHeightRatio;
        } else {

            drawable.transform.scale.x = GamePanel.NATIVE_SCREEN_HEIGHT * (float)nativeIllustrationWidthHeightRatio;
            drawable.transform.scale.y = GamePanel.NATIVE_SCREEN_HEIGHT;
        }
        drawable.setSprite(AssetPool.getIllustration(resourceName).getSprite());
    }


    /**
     * Removes the illustration being displayed.
     * If no illustration is being displayed, nothing will happen.
     */
    public void removeIllustration() {

        illustrationActive = false;
        displayedIllustrationName = Illustration.NO_ILLUSTRATION;
        drawable.setSprite(new Sprite());
    }


    // GETTER
    public boolean isIllustrationActive() {
        return illustrationActive;
    }

    public String getDisplayedIllustrationName() {
        return displayedIllustrationName;
    }
}
