package ui.support;

import core.GamePanel;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import render.Renderer;
import render.drawable.Transform;
import render.enumeration.ZIndex;

/**
 * This class contains logic for rendering user interface components related to the tutorial window.
 */
public class UiTutorialSupport {

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

    private float titleFontScale;

    private float titleCharScreenHeight;

    private Transform tempWorldTransform;

    private float standardBoldCharScreenHeight;

    private Vector4f windowColor;

    private Vector4f titleDividerColor;

    private Vector3f titleTextColor;

    private Vector3f subtitleTextColor;

    private Vector3f contentTextColor;

    private Vector3f pageNumberColor;

    private float tutorialWindowWorldCornerRadius;

    private Transform tutorialWindowScreenTransform;

    private Transform titleDividerScreenTransform;

    private Vector2f titleTextScreenCoords;

    private String titleText;

    private float subtitleTextScreenTitleDividerBottomAdjustment;

    private Vector2f subtitleTextScreenCoords;

    private String subtitleText;

    private float contentTextLineScreenVerticalSpacing;

    private float contentTextLineScreenMaxWidth;

    private Vector2f contentTextScreenCoords;

    private String contentText;

    private float pageNumberScreenTutorialWindowRightAdjustment;

    private Vector2f pageNumberScreenCoords;

    private String pageNumber;


    // CONSTRUCTOR
    /**
     * Constructs a UiTutorialSupport instance.
     *
     * @param gp GamePanel instance
     */
    public UiTutorialSupport(GamePanel gp) {
        this.gp = gp;
        init();
    }


    // METHODS
    /**
     * Refreshes tutorial window user interface components that may change while the game is running.
     * In this case, a refresh must be done if the specific tutorial being shown is changed or if the window opacity is
     * changed.
     */
    public void refresh() {

        // Tutorial data.
        titleText = gp.getTutorialH().getTitle();
        subtitleText = gp.getTutorialH().getSubtitle();
        contentText = gp.getTutorialH().getContent();
        pageNumber = gp.getTutorialH().getCurrentPageNumber() + "/" + gp.getTutorialH().getTotalPageNumbers();
        float pageNumberScreenWidth = gp.getUi().calculateStringScreenWidth(
                pageNumber, gp.getUi().getStandardFontScale(), gp.getUi().getStandardNormalFont());
        float pageNumberScreenX = getTutorialWindowScreenX() + getTutorialWindowScreenWidth()
                - pageNumberScreenTutorialWindowRightAdjustment - pageNumberScreenWidth;
        pageNumberScreenCoords.x = pageNumberScreenX;

        // Content and subtitle
        if (subtitleText.isEmpty()) {

            contentTextScreenCoords.y = subtitleTextScreenCoords.y;
        } else {

            contentTextScreenCoords.y = subtitleTextScreenCoords.y + standardBoldCharScreenHeight
                    + subtitleTextScreenTitleDividerBottomAdjustment;
        }


        // Tutorial window color.
        windowColor.w = gp.getUi().getWindowOpacity();

        // Dirty flag.
        dirty = false;
    }


    /**
     * Adds tutorial window user interface components to the render pipeline.
     */
    public void addToRenderPipeline() {

        // Tutorial window.
        gp.getCamera().screenCoordsToWorldCoords(tutorialWindowScreenTransform.position, tempWorldTransform.position);
        gp.getCamera().screenDimensionsToWorldDimensions(tutorialWindowScreenTransform.scale, tempWorldTransform.scale);
        gp.getRenderer().addRoundRectangle(
                windowColor,
                tempWorldTransform,
                ZIndex.SECOND_LAYER,
                tutorialWindowWorldCornerRadius
        );

        // Title divider.
        gp.getCamera().screenCoordsToWorldCoords(titleDividerScreenTransform.position, tempWorldTransform.position);
        gp.getCamera().screenDimensionsToWorldDimensions(titleDividerScreenTransform.scale, tempWorldTransform.scale);
        gp.getRenderer().addRectangle(titleDividerColor, tempWorldTransform, ZIndex.SECOND_LAYER);

        // Title text.
        gp.getCamera().screenCoordsToWorldCoords(titleTextScreenCoords, tempWorldTransform.position);
        gp.getRenderer().addString(
                titleText,
                tempWorldTransform.position.x,
                tempWorldTransform.position.y,
                titleFontScale,
                titleTextColor,
                gp.getUi().getStandardBoldFont(),
                ZIndex.SECOND_LAYER
        );

        // Subtitle text.
        if (!subtitleText.isEmpty()) {

            gp.getCamera().screenCoordsToWorldCoords(subtitleTextScreenCoords, tempWorldTransform.position);
            gp.getRenderer().addString(
                    subtitleText,
                    tempWorldTransform.position.x,
                    tempWorldTransform.position.y,
                    gp.getUi().getStandardFontScale(),
                    subtitleTextColor,
                    gp.getUi().getStandardBoldFont(),
                    ZIndex.SECOND_LAYER
            );
        }

        // Content text.
        gp.getUi().addStringBlockToRenderPipeline(
                contentText,
                contentTextScreenCoords.x,
                contentTextScreenCoords.y,
                contentTextLineScreenMaxWidth,
                contentTextLineScreenVerticalSpacing,
                gp.getUi().getStandardFontScale(),
                contentTextColor,
                gp.getUi().getStandardNormalFont(),
                ZIndex.SECOND_LAYER,
                true
        );

        // Page number.
        gp.getCamera().screenCoordsToWorldCoords(pageNumberScreenCoords, tempWorldTransform.position);
        gp.getRenderer().addString(
                pageNumber,
                tempWorldTransform.position.x,
                tempWorldTransform.position.y,
                gp.getUi().getStandardFontScale(),
                pageNumberColor,
                gp.getUi().getStandardNormalFont(),
                ZIndex.SECOND_LAYER
        );
    }


    /**
     * Marks this user interface component as "dirty" to indicate that it must be refreshed to display updated
     * information.
     * In this case, a refresh must be done if the specific tutorial being shown is changed or if the window opacity is
     * changed.
     */
    public void markDirty() {

        dirty = true;
    }


    /**
     * Initializes tutorial window user interface components that will not change while the game is running.
     */
    private void init() {

        // Temporary world coordinates and dimensions.
        Vector2f tempWorldCoords = new Vector2f(0.0f, 0.0f);
        Vector2f tempWorldDimensions = new Vector2f(0.0f, 0.0f);
        tempWorldTransform = new Transform(tempWorldCoords, tempWorldDimensions);

        // Colors.
        windowColor = new Vector4f(20, 20, 20, 255);
        titleDividerColor = new Vector4f(255, 255, 255, 255);
        titleTextColor = new Vector3f(244, 154, 45);
        subtitleTextColor = new Vector3f(121, 255, 218);
        contentTextColor = new Vector3f(255, 255, 255);
        pageNumberColor = new Vector3f(121, 185, 255);

        // Text sizing.
        float standardNormalCharWorldHeight = gp.getRenderer().getFont(gp.getUi().getStandardNormalFont())
                .getCharacter('A').getHeight() * gp.getUi().getStandardFontScale();                                     // It doesn't matter which character is used, since all characters in a font have the same height.
        float standardNormalCharScreenHeight =
                gp.getCamera().worldHeightToScreenHeight(standardNormalCharWorldHeight);

        float standardBoldCharWorldHeight = gp.getRenderer().getFont(gp.getUi().getStandardBoldFont())
                .getCharacter('A').getHeight() * gp.getUi().getStandardFontScale();                                     // It doesn't matter which character is used, since all characters in a font have the same height.
        standardBoldCharScreenHeight =
                gp.getCamera().worldHeightToScreenHeight(standardBoldCharWorldHeight);

        titleFontScale = 0.17f;
        float headerCharWorldHeight =
                gp.getRenderer().getFont(gp.getUi().getStandardBoldFont())
                        .getCharacter('A').getHeight() * titleFontScale;                                                // It doesn't matter which character is used, since all characters in a font have the same height.
        titleCharScreenHeight =
                gp.getCamera().worldHeightToScreenHeight(headerCharWorldHeight);

        // Tutorial window coordinates and dimensions.
        float tutorialWindowWorldWidth = 676.0f;
        float tutorialWindowScreenWidth = gp.getCamera().worldWidthToScreenWidth(tutorialWindowWorldWidth);
        float tutorialWindowWorldHeight = 380.0f;
        float tutorialWindowScreenHeight = gp.getCamera().worldHeightToScreenHeight(tutorialWindowWorldHeight);
        float tutorialWindowScreenX = 0.5f - (tutorialWindowScreenWidth / 2);
        float tutorialWindowScreenY = 0.5f - (tutorialWindowScreenHeight / 2);
        Vector2f tutorialWindowScreenCoords = new Vector2f(tutorialWindowScreenX, tutorialWindowScreenY);
        Vector2f tutorialWindowScreenDimensions = new Vector2f(tutorialWindowScreenWidth, tutorialWindowScreenHeight);
        tutorialWindowScreenTransform = new Transform(tutorialWindowScreenCoords, tutorialWindowScreenDimensions);
        tutorialWindowWorldCornerRadius = tutorialWindowWorldHeight / 16.0f;

        // Title divider coordinates and dimensions.
        float tutorialWindowScreenLeftRightAdjustment = (1 - tutorialWindowScreenWidth) / 2;
        float tutorialWindowScreenTopBottomAdjustment = (1 - tutorialWindowScreenHeight) / 2;
        float titleDividerWorldTutorialWindowLeftRightAdjustment = 42.0f;
        float titleDividerScreenTutorialWindowLeftRightAdjustment =
                gp.getCamera().worldWidthToScreenWidth(titleDividerWorldTutorialWindowLeftRightAdjustment);
        float titleDividerScreenWidth =
                1 - (2 * tutorialWindowScreenLeftRightAdjustment)
                        - (2 * titleDividerScreenTutorialWindowLeftRightAdjustment);
        float titleDividerWorldHeight = 0.96f;
        float titleDividerScreenHeight = gp.getCamera().worldHeightToScreenHeight(titleDividerWorldHeight);
        float titleDividerWorldTutorialWindowTopAdjustment = 54.0f;                                                     // This value was selected to provide the same header spacing as in the primary menu frame.
        float titleDividerScreenTutorialWindowTopAdjustment =
                gp.getCamera().worldHeightToScreenHeight(titleDividerWorldTutorialWindowTopAdjustment);
        float titleDividerScreenX =
                tutorialWindowScreenLeftRightAdjustment + titleDividerScreenTutorialWindowLeftRightAdjustment;
        float titleDividerScreenY =
                tutorialWindowScreenY + titleDividerScreenTutorialWindowTopAdjustment;
        Vector2f titleDividerScreenCoords = new Vector2f(titleDividerScreenX, titleDividerScreenY);
        Vector2f titleDividerScreenDimensions = new Vector2f(titleDividerScreenWidth, titleDividerScreenHeight);
        titleDividerScreenTransform = new Transform(titleDividerScreenCoords, titleDividerScreenDimensions);

        // Title text coordinates and content.
        float titleTextWorldTutorialWindowLeftAdjustment = 50.0f;
        float titleTextScreenTutorialWindowLeftAdjustment =
                gp.getCamera().worldWidthToScreenWidth(titleTextWorldTutorialWindowLeftAdjustment);
        float titleTextScreenTutorialWindowTopAdjustment =
                (titleDividerScreenY - tutorialWindowScreenTopBottomAdjustment - titleCharScreenHeight) / 2;
        float titleTextScreenX = tutorialWindowScreenLeftRightAdjustment + titleTextScreenTutorialWindowLeftAdjustment;
        float titleTextScreenY = tutorialWindowScreenTopBottomAdjustment + titleTextScreenTutorialWindowTopAdjustment;
        titleTextScreenCoords = new Vector2f(titleTextScreenX, titleTextScreenY);
        titleText = "???";

        // Subtitle text coordinates.
        subtitleTextScreenTitleDividerBottomAdjustment =
                gp.getCamera().worldHeightToScreenHeight(titleDividerWorldTutorialWindowLeftRightAdjustment) / 2;
        float subtitleTextScreenX = titleTextScreenX;
        float subtitleTextScreenY = titleDividerScreenY + titleDividerScreenHeight
                + subtitleTextScreenTitleDividerBottomAdjustment;
        subtitleTextScreenCoords = new Vector2f(subtitleTextScreenX, subtitleTextScreenY);

        // Content coordinates and dimensions.
        float contentTextLineWorldVerticalSpacing = standardNormalCharWorldHeight * 1.75f;
        contentTextLineScreenVerticalSpacing =
                gp.getCamera().worldHeightToScreenHeight(contentTextLineWorldVerticalSpacing);
        contentTextLineScreenMaxWidth = tutorialWindowScreenWidth - (2 * titleTextScreenTutorialWindowLeftAdjustment);
        float contentTextScreenX = titleTextScreenX;
        contentTextScreenCoords = new Vector2f(contentTextScreenX, 0.0f);                                               // y-value is placeholder (will change while refreshing).

        // Page number coordinates.
        float pageNumberWorldTutorialWindowRightAdjustment = 10.0f;
        pageNumberScreenTutorialWindowRightAdjustment = gp.getCamera().worldWidthToScreenWidth(pageNumberWorldTutorialWindowRightAdjustment);
        float pageNumberWorldTutorialWindowBottomAdjustment = 10.0f;
        float pageNumberScreenTutorialWindowBottomAdjustment =
                gp.getCamera().worldHeightToScreenHeight(pageNumberWorldTutorialWindowBottomAdjustment);
        float pageNumberScreenY = tutorialWindowScreenY + tutorialWindowScreenHeight
                - pageNumberScreenTutorialWindowBottomAdjustment - standardNormalCharScreenHeight;
        pageNumberScreenCoords = new Vector2f(0.0f, pageNumberScreenY);                                                 // x-value is placeholder (will change while refreshing).
    }


    // GETTERS
    public boolean isDirty() {
        return dirty;
    }

    public float getTutorialWindowScreenX() {

        return tutorialWindowScreenTransform.position.x;
    }

    public float getTutorialWindowScreenY() {

        return tutorialWindowScreenTransform.position.y;
    }

    public float getTutorialWindowScreenWidth() {

        return tutorialWindowScreenTransform.scale.x;
    }

    public float getTutorialWindowScreenHeight() {

        return tutorialWindowScreenTransform.scale.y;
    }
}