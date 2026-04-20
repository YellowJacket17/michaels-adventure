package ui.support;

import core.GamePanel;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import render.Renderer;
import render.drawable.Transform;
import render.enumeration.ZIndex;
import utility.LimitedLinkedHashMap;

/**
 * This class contains logic for rendering user interface components related to sub-menus.
 */
public class UiSubMenuSupport {

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

    private float standardNormalCharScreenHeight;

    private float standardNormalCharScreenHeightHalf;

    private Transform tempWorldTransform;

    private Vector4f windowColor;

    private LimitedLinkedHashMap<Integer, Vector3f> optionTextColor;

    private Vector3f defaultTextColor;

    private float subMenuWindowScreenTopBottomPadding;

    private float subMenuWindowScreenLeftPadding;

    private float subMenuWindowScreenRightPadding;

    private Transform subMenuWindowScreenTransform;

    private float optionTextScreenVerticalSpacing;

    private Vector2f optionTextScreenCoords;

    private LimitedLinkedHashMap<Integer, String> optionText;

    private float selectionArrowScreenHeightHalf;

    private Vector2f selectionArrowScreenCoords;


    // CONSTRUCTOR
    /**
     * Constructs a UiSubMenuSupport instance.
     *
     * @param gp GamePanel instance
     * @param renderer Renderer instance
     */
    public UiSubMenuSupport(GamePanel gp, Renderer renderer) {
        this.gp = gp;
        init(renderer);
    }


    // METHODS
    /**
     * Refreshes sub-menu user interface components that may change while the game is running.
     * In this case, a refresh must be done if a new sub-menu is generated or if the window opacity is changed.
     */
    public void refresh() {

        // Sub-menu window dimensions.
        float maxTextWorldWidth = 0;
        float textWorldWidth = 0;

        for (int i = 0; i < gp.getSubMenuH().getOptions().size(); i++) {

            textWorldWidth = gp.getUi().calculateStringWorldWidth(
                    gp.getSubMenuH().getOptions().get(i),
                    gp.getUi().getStandardFontScale(),
                    gp.getUi().getStandardNormalFont());

            if (textWorldWidth > maxTextWorldWidth) {

                maxTextWorldWidth = textWorldWidth;
            }
        }
        float subMenuWindowScreenWidth = gp.getCamera().worldWidthToScreenWidth(maxTextWorldWidth)
                + subMenuWindowScreenLeftPadding + subMenuWindowScreenRightPadding;
        float subMenuWindowScreenHeight = (optionTextScreenVerticalSpacing * (gp.getSubMenuH().getOptions().size() - 1))
                + (2* subMenuWindowScreenTopBottomPadding)
                + (standardNormalCharScreenHeight * gp.getSubMenuH().getOptions().size());
        subMenuWindowScreenTransform.scale.x = subMenuWindowScreenWidth;
        subMenuWindowScreenTransform.scale.y = subMenuWindowScreenHeight;

        // Sub-menu window coordinates.
        float subMenuWindowScreenX;
        float subMenuWindowScreenY;

        if (gp.getSubMenuH().isSubMenuDefaultPosition()) {

            float subMenuWindowWorldRightAdjustment = 23.0f;
            float subMenuWindowScreenRightAdjustment =
                    gp.getCamera().worldWidthToScreenWidth(subMenuWindowWorldRightAdjustment);
            float subMenuWindowWorldDialogueWindowBottomAdjustment = 13.0f;
            float subMenuWindowScreenDialogueWindowBottomAdjustment =
                    gp.getCamera().worldHeightToScreenHeight(subMenuWindowWorldDialogueWindowBottomAdjustment);
            subMenuWindowScreenX =
                    1 - subMenuWindowScreenRightAdjustment - subMenuWindowScreenWidth;
            subMenuWindowScreenY = gp.getUiDialogueS().getDialogueWindowScreenY()
                    - subMenuWindowScreenDialogueWindowBottomAdjustment - subMenuWindowScreenHeight;
        } else {

            subMenuWindowScreenX = gp.getSubMenuH().getSubMenuScreenX();
            subMenuWindowScreenY = gp.getSubMenuH().getSubMenuScreenY();
        }
        subMenuWindowScreenTransform.position.x = subMenuWindowScreenX;
        subMenuWindowScreenTransform.position.y = subMenuWindowScreenY;

        // Option text content and colors.
        optionText.clear();
        optionTextColor.clear();

        for (int i = 0; i < gp.getSubMenuH().getOptions().size(); i++) {

            optionText.put(i, gp.getSubMenuH().getOptions().get(i));

            if (gp.getSubMenuH().getColors().get(i) != null) {

                optionTextColor.put(i, gp.getSubMenuH().getColors().get(i));
            } else {

                optionTextColor.put(i, defaultTextColor);
            }
        }

        // Header text coordinates.
        optionTextScreenCoords.x = subMenuWindowScreenX + subMenuWindowScreenLeftPadding;
        optionTextScreenCoords.y = 0.0f;                                                                                // y-value is placeholder (will change while rendering).

        // Selection arrow coordinates.
        float selectionArrowWorldLeftTextOffset = 15.36f;
        float selectionArrowScreenLeftTextOffset =
                gp.getCamera().worldWidthToScreenWidth(selectionArrowWorldLeftTextOffset);
        selectionArrowScreenCoords.x = optionTextScreenCoords.x - selectionArrowScreenLeftTextOffset;
        selectionArrowScreenCoords.y = 0.0f;                                                                            // y-value is placeholder (will change while rendering).

        // Sub-menu window color.
        windowColor.w = gp.getUi().getWindowOpacity();

        // Dirty flag.
        dirty = false;
    }


    /**
     * Adds sub-menu user interface components to the render pipeline.
     *
     * @param renderer Renderer instance
     */
    public void addToRenderPipeline(Renderer renderer) {

        // Sub-menu window.
        gp.getCamera().screenCoordsToWorldCoords(subMenuWindowScreenTransform.position, tempWorldTransform.position);
        gp.getCamera().screenDimensionsToWorldDimensions(subMenuWindowScreenTransform.scale, tempWorldTransform.scale);
        renderer.addRectangle(windowColor, tempWorldTransform, ZIndex.FIRST_LAYER);

        // Option text.
        optionTextScreenCoords.y = subMenuWindowScreenTransform.position.y + subMenuWindowScreenTopBottomPadding;

        for (int i = 0; i < gp.getSubMenuH().getOptions().size(); i++) {

            gp.getCamera().screenCoordsToWorldCoords(optionTextScreenCoords, tempWorldTransform.position);
            renderer.addString(
                    optionText.get(i),
                    tempWorldTransform.position.x,
                    tempWorldTransform.position.y,
                    gp.getUi().getStandardFontScale(),
                    optionTextColor.get(i),
                    gp.getUi().getStandardNormalFont(),
                    ZIndex.FIRST_LAYER
            );

            // Selection arrow.
            if (i == gp.getSubMenuH().getIndexSelected()) {

                selectionArrowScreenCoords.y = optionTextScreenCoords.y
                        + (standardNormalCharScreenHeightHalf)
                        - (selectionArrowScreenHeightHalf);
                gp.getSelectionA().addToRenderPipeline(
                        renderer, selectionArrowScreenCoords.x, selectionArrowScreenCoords.y);
            }
            optionTextScreenCoords.y += standardNormalCharScreenHeight + optionTextScreenVerticalSpacing;
        }
    }


    /**
     * Marks this user interface component as "dirty" to indicate that it must be refreshed to display updated
     * information.
     * In this case, a refresh must be done if a new sub-menu is generated or if the window opacity is changed.
     */
    public void markDirty() {

        dirty = true;
    }


    /**
     * Initializes sub-menu user interface components that will not change while the game is running.
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
        optionTextColor = new LimitedLinkedHashMap<>(gp.getSubMenuH().getMaxOptionsSize());
        defaultTextColor = new Vector3f(255, 255, 255);

        // Text sizing.
        float standardNormalCharWorldHeight = renderer.getFont(gp.getUi().getStandardNormalFont())
                .getCharacter('A').getHeight() * gp.getUi().getStandardFontScale();                                     // It doesn't matter which character is used, since all characters in a font have the same height.
        standardNormalCharScreenHeight =
                gp.getCamera().worldHeightToScreenHeight(standardNormalCharWorldHeight);
        standardNormalCharScreenHeightHalf = standardNormalCharScreenHeight / 2;

        // Sub-menu window coordinates and dimensions.
        Vector2f subMenuWindowScreenCoords = new Vector2f(0.0f, 0.0f);                                                  // Values are placeholders (will change while refreshing).
        Vector2f subMenuWindowScreenDimensions = new Vector2f(0.0f, 0.0f);                                              // Values are placeholders (will change while refreshing).
        subMenuWindowScreenTransform = new Transform(subMenuWindowScreenCoords, subMenuWindowScreenDimensions);

        // Miscellaneous initializations.
        float subMenuWindowWorldTopBottomPadding = 8.6f;
        subMenuWindowScreenTopBottomPadding = gp.getCamera().worldHeightToScreenHeight(subMenuWindowWorldTopBottomPadding);

        float subMenuWindowWorldLeftPadding = 23.0f;
        subMenuWindowScreenLeftPadding = gp.getCamera().worldWidthToScreenWidth(subMenuWindowWorldLeftPadding);
        float subMenuWindowWorldRightPadding = 15.36f;
        subMenuWindowScreenRightPadding = gp.getCamera().worldWidthToScreenWidth(subMenuWindowWorldRightPadding);

        float optionTextWorldVerticalSpacing = 9.5f;
        optionTextScreenVerticalSpacing = gp.getCamera().worldHeightToScreenHeight(optionTextWorldVerticalSpacing);
        optionText = new LimitedLinkedHashMap<>(gp.getSubMenuH().getMaxOptionsSize());
        optionTextScreenCoords = new Vector2f(0.0f, 0.0f);                                                              // Values are placeholders (will change while refreshing/rendering).

        selectionArrowScreenHeightHalf =
                gp.getCamera().worldHeightToScreenHeight(gp.getSelectionA().getNativeSpriteHeight()) / 2;
        selectionArrowScreenCoords = new Vector2f(0.0f, 0.0f);                                                          // Values are placeholders (will change while refreshing/rendering).
    }


    // GETTERS
    public boolean isDirty() {
        return dirty;
    }

    public float getSubMenuWindowScreenX() {

        return subMenuWindowScreenTransform.position.x;
    }

    public float getSubMenuWindowScreenY() {

        return subMenuWindowScreenTransform.position.y;
    }

    public float getSubMenuWindowScreenWidth() {

        return subMenuWindowScreenTransform.scale.x;
    }

    public float getSubMenuWindowScreenHeight() {

        return subMenuWindowScreenTransform.scale.y;
    }
}
