package ui.support;

import core.GamePanel;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import render.Renderer;
import render.drawable.Transform;
import render.enumeration.ZIndex;

/**
 * This class contains logic for rendering user interface components related to the primary menu frame.
 * The primary menu frame represents components of the primary menu that are the same across each of the primary menu
 * states.
 */
public class UiPrimaryMenuFrameSupport {

    /*
     * Note that performing calculations once during initialization or minimally during refreshes prevents calculations
     * from being unnecessarily repeated each time these user interface components are added to the render pipeline,
     * hence improving efficiency / memory usage.
     *
     * The goal is to have no / minimal calculations performed during the 'addToRenderPipeline()' method pertaining to
     * layout / positioning of user interface components.
     */

    // FIELDS
    private final GamePanel gp;

    private boolean dirty = true;

    private float headerFontScale;

    private float headerCharScreenHeight;

    private Transform tempWorldTransform;

    private Vector4f windowColor;

    private Vector4f headerDividerColor;

    private Vector3f headerTextColor;

    private float primaryWindowWorldCornerRadius;

    private Transform primaryWindowScreenTransform;

    private Vector2f partyIconScreenCoords;

    private Vector2f inventoryIconScreenCoords;

    private Vector2f settingsIconScreenCoords;

    private Transform headerDividerScreenTransform;

    private Vector2f headerTextScreenCoords;

    private String headerText;


    // CONSTRUCTOR
    /**
     * Constructs a UiPrimaryMenuFrameSupport instance.
     *
     * @param gp GamePanel instance
     * @param renderer Renderer instance
     */
    public UiPrimaryMenuFrameSupport(GamePanel gp, Renderer renderer) {
        this.gp = gp;
        init(renderer);
    }


    // METHODS
    /**
     * Refreshes primary window frame user interface components that may change while the game is running.
     * In this case, a refresh must be done if the primary menu state is changed or if the window opacity is changed.
     */
    public void refresh() {

        // Header text content.
        switch (gp.getUi().getPrimaryMenuState()) {
            case PARTY:
                headerText = "PARTY";
                break;
            case INVENTORY:
                headerText = "INVENTORY";
                break;
            case SETTINGS:
                headerText = "SETTINGS";
                break;
            default:
                headerText = "???";
        }

        // Primary window color.
        windowColor.w = gp.getUi().getWindowOpacity();

        // Dirty flag.
        dirty = false;
    }


    /**
     * Adds primary window frame user interface components to the render pipeline.
     *
     * @param renderer Renderer instance
     */
    public void addToRenderPipeline(Renderer renderer) {

        // Primary window.
        gp.getCamera().screenCoordsToWorldCoords(primaryWindowScreenTransform.position, tempWorldTransform.position);
        gp.getCamera().screenDimensionsToWorldDimensions(primaryWindowScreenTransform.scale, tempWorldTransform.scale);
        renderer.addRoundRectangle(
                windowColor,
                tempWorldTransform,
                ZIndex.SECOND_LAYER,
                primaryWindowWorldCornerRadius
        );

        // State icons (party, inventory, settings).
        gp.getGuiIconM().addToRenderPipeline(renderer, 0, partyIconScreenCoords.x, partyIconScreenCoords.y);
        gp.getGuiIconM().addToRenderPipeline(renderer, 1, inventoryIconScreenCoords.x, inventoryIconScreenCoords.y);
        gp.getGuiIconM().addToRenderPipeline(renderer, 2, settingsIconScreenCoords.x, settingsIconScreenCoords.y);

        // Header divider.
        gp.getCamera().screenCoordsToWorldCoords(headerDividerScreenTransform.position, tempWorldTransform.position);
        gp.getCamera().screenDimensionsToWorldDimensions(headerDividerScreenTransform.scale, tempWorldTransform.scale);
        renderer.addRectangle(headerDividerColor, tempWorldTransform, ZIndex.SECOND_LAYER);

        // Header text.
        gp.getCamera().screenCoordsToWorldCoords(headerTextScreenCoords, tempWorldTransform.position);
        renderer.addString(
                headerText,
                tempWorldTransform.position.x,
                tempWorldTransform.position.y,
                headerFontScale,
                headerTextColor,
                gp.getUi().getStandardBoldFont(),
                ZIndex.SECOND_LAYER
        );
    }


    /**
     * Marks this user interface component as "dirty" to indicate that it must be refreshed to display updated
     * information.
     * In this case, a refresh must be done if the primary menu state is changed or if the window opacity is changed.
     */
    public void markDirty() {

        dirty = true;
    }


    /**
     * Initializes primary menu frame user interface components that will not change while the game is running.
     *
     * @param renderer Renderer instance
     */
    private void init(Renderer renderer) {

        // Temporary world coordinates and dimensions.
        Vector2f tempWorldCoords = new Vector2f(0.0f, 0.0f);
        Vector2f tempWorldDimensions = new Vector2f(0.0f, 0.0f);
        tempWorldTransform = new Transform(tempWorldCoords, tempWorldDimensions);

        // Colors.
        windowColor = new Vector4f(20, 20, 20, 255);
        headerDividerColor = new Vector4f(255, 255, 255, 255);
        headerTextColor = new Vector3f(121, 149, 255);

        // Text sizing.
        headerFontScale = 0.17f;
        float headerCharWorldHeight =
                renderer.getFont(gp.getUi().getStandardBoldFont()).getCharacter('A').getHeight() * headerFontScale;     // It doesn't matter which character is used, since all characters in a font have the same height.
        headerCharScreenHeight =
                gp.getCamera().worldHeightToScreenHeight(headerCharWorldHeight);

        // Primary window coordinates and dimensions.
        float primaryWindowWorldWidth = 676.0f;
        float primaryWindowScreenWidth = gp.getCamera().worldWidthToScreenWidth(primaryWindowWorldWidth);
        float primaryWindowWorldHeight = 380.0f;
        float primaryWindowScreenHeight = gp.getCamera().worldHeightToScreenHeight(primaryWindowWorldHeight);
        float primaryWindowScreenX = 0.5f - (primaryWindowScreenWidth / 2);
        float primaryWindowScreenY = 0.5f - (primaryWindowScreenHeight / 2);
        Vector2f primaryWindowScreenCoords = new Vector2f(primaryWindowScreenX, primaryWindowScreenY);
        Vector2f primaryWindowScreenDimensions = new Vector2f(primaryWindowScreenWidth, primaryWindowScreenHeight);
        primaryWindowScreenTransform = new Transform(primaryWindowScreenCoords, primaryWindowScreenDimensions);
        primaryWindowWorldCornerRadius = primaryWindowWorldHeight / 16.0f;

        // State icons (party, inventory, settings) coordinates.
        float primaryWindowScreenLeftRightAdjustment = (1 - primaryWindowScreenWidth) / 2;
        float primaryWindowScreenTopBottomAdjustment = (1 - primaryWindowScreenHeight) / 2;
        float iconWorldHorizontalSpacing = 45.0f;
        float iconScreenHorizontalSpacing = gp.getCamera().worldWidthToScreenWidth(iconWorldHorizontalSpacing);
        float settingsIconWorldPrimaryWindowRightEdgeAdjustment = 76.8f;
        float settingsIconScreenPrimaryWindowRightEdgeAdjustment =
                gp.getCamera().worldWidthToScreenWidth(settingsIconWorldPrimaryWindowRightEdgeAdjustment);
        float settingsIconScreenX =
                1 - primaryWindowScreenLeftRightAdjustment - settingsIconScreenPrimaryWindowRightEdgeAdjustment;
        float iconWorldPrimaryWindowTopEdgeAdjustment = 13.0f;
        float iconScreenPrimaryWindowTopEdgeAdjustment =
                gp.getCamera().worldHeightToScreenHeight(iconWorldPrimaryWindowTopEdgeAdjustment);
        float iconScreenY = primaryWindowScreenTopBottomAdjustment + iconScreenPrimaryWindowTopEdgeAdjustment;
        settingsIconScreenCoords = new Vector2f(settingsIconScreenX, iconScreenY);
        float inventoryIconScreenX = settingsIconScreenX - iconScreenHorizontalSpacing;
        inventoryIconScreenCoords = new Vector2f(inventoryIconScreenX, iconScreenY);
        float partyIconScreenX = inventoryIconScreenX - iconScreenHorizontalSpacing;
        partyIconScreenCoords = new Vector2f(partyIconScreenX, iconScreenY);

        // Header divider coordinates and dimensions.
        float headerDividerWorldPrimaryWindowLeftRightAdjustment = 42.0f;
        float headerDividerScreenPrimaryWindowLeftRightAdjustment =
                gp.getCamera().worldWidthToScreenWidth(headerDividerWorldPrimaryWindowLeftRightAdjustment);
        float headerDividerScreenWidth =
                1 - (2 * primaryWindowScreenLeftRightAdjustment)
                        - (2 * headerDividerScreenPrimaryWindowLeftRightAdjustment);
        float headerDividerWorldHeight = 1.7f;
        float headerDividerScreenHeight = gp.getCamera().worldWidthToScreenWidth(headerDividerWorldHeight);
        float iconWorldHeight = gp.getGuiIconM().getIconById(0).getNativeSpriteHeight();                                // It doesn't matter which of the menu icons is used here, since all are same height.
        float iconScreenHeight = gp.getCamera().worldHeightToScreenHeight(iconWorldHeight);
        float headerDividerScreenX =
                primaryWindowScreenLeftRightAdjustment + headerDividerScreenPrimaryWindowLeftRightAdjustment;
        float headerDividerScreenY =
                iconScreenY + iconScreenHeight + (iconScreenY - primaryWindowScreenTopBottomAdjustment);
        Vector2f headerDividerScreenCoords = new Vector2f(headerDividerScreenX, headerDividerScreenY);
        Vector2f headerDividerScreenDimensions = new Vector2f(headerDividerScreenWidth, headerDividerScreenHeight);
        headerDividerScreenTransform = new Transform(headerDividerScreenCoords, headerDividerScreenDimensions);

        // Header text coordinates and content.
        float headerWorldPrimaryWindowLeftAdjustment = 50.0f;
        float headerScreenPrimaryWindowLeftAdjustment =
                gp.getCamera().worldWidthToScreenWidth(headerWorldPrimaryWindowLeftAdjustment);
        float headerScreenPrimaryWindowTopAdjustment =
                (headerDividerScreenY - primaryWindowScreenTopBottomAdjustment - headerCharScreenHeight) / 2;
        float headerScreenX = primaryWindowScreenLeftRightAdjustment + headerScreenPrimaryWindowLeftAdjustment;
        float headerScreenY = primaryWindowScreenTopBottomAdjustment + headerScreenPrimaryWindowTopAdjustment;
        headerTextScreenCoords = new Vector2f(headerScreenX, headerScreenY);
        headerText = "???";
    }


    // GETTERS
    public boolean isDirty() {
        return dirty;
    }

    public float getPrimaryWindowScreenX() {

        return primaryWindowScreenTransform.position.x;
    }

    public float getPrimaryWindowScreenY() {

        return primaryWindowScreenTransform.position.y;
    }

    public float getPrimaryWindowScreenWidth() {

        return primaryWindowScreenTransform.scale.x;
    }

    public float getPrimaryWindowScreenHeight() {

        return primaryWindowScreenTransform.scale.y;
    }

    public float getHeaderDividerScreenX() {

        return headerDividerScreenTransform.position.x;
    }

    public float getHeaderDividerScreenY() {

        return headerDividerScreenTransform.position.y;
    }

    public float getHeaderDividerScreenWidth() {

        return headerDividerScreenTransform.scale.x;
    }

    public float getHeaderDividerScreenHeight() {

        return headerDividerScreenTransform.scale.y;
    }
}
