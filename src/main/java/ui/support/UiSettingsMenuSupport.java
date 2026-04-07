package ui.support;

import core.GamePanel;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import render.Renderer;
import render.drawable.Transform;
import render.enumeration.ZIndex;

import java.util.ArrayList;

/**
 * This class contains logic for rendering settings menu user interface components.
 * This is part of the primary menu.
 */
public class UiSettingsMenuSupport {

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

    private int systemSettingSelected;

    private int systemOptionSelected;

    private Transform tempWorldTransform;

    private float standardNormalCharScreenHeight;

    private Vector3f settingLabelTextInactiveColor;

    private Vector3f settingLabelTextActiveColor;

    private Vector3f settingValueTextColor;

    private Vector3f settingDescriptionTextColor;

    private Vector4f footerDividerColor;

    private float settingScreenVerticalSpacing;

    private float settingLabelScreenHeaderDividerBottomAdjustment;

    private Vector2f topSettingLabelScreenCoords;

    private ArrayList<Vector2f> settingLabelScreenCoords;

    private ArrayList<Vector2f> settingValueScreenCoords;

    private Vector2f rightScrollArrowScreenCoords;

    private Vector2f leftScrollArrowScreenCoords;

    private Vector2f scrollArrowScreenDimensions;

    private Transform footerDividerScreenTransform;

    private float settingDescriptionTextLineScreenVerticalSpacing;

    private float settingDescriptionTextLineScreenMaxWidth;

    private Vector2f settingDescriptionTextScreenCoords;


    // CONSTRUCTOR
    /**
     * Constructs a UiSettingsMenuSupport instance.
     *
     * @param gp GamePanel instance
     * @param renderer Renderer instance
     */
    public UiSettingsMenuSupport(GamePanel gp, Renderer renderer) {
        this.gp = gp;
        init(renderer);
    }


    // METHODS
    /**
     * Refreshes setting menu user interface components that may change while the game is running.
     * These user interface elements are the "core" aspects of the settings menu layout.
     * In this case, a refresh must be done if the number of settings displayed on screen at once changes.
     * Note that a refresh is not necessary if the contents/state of the user interface components change (e.g.,
     * scrolling through the different options of a setting, scrolling between settings).
     */
    public void refresh() {

        // Setting label coordinates.
        float settingValueWorldSettingLabelLeftAdjustment = 307.2f;
        float settingLabelValueScreenHorizontalSpacing =
                gp.getCamera().worldWidthToScreenWidth(settingValueWorldSettingLabelLeftAdjustment);

        float settingLabelScreenX = topSettingLabelScreenCoords.x;
        float settingValueScreenX = settingLabelScreenX + settingLabelValueScreenHorizontalSpacing;
        float settingScreenY;

        for (int i = 0; i < gp.getSystemSettingsSize(); i++) {

            settingScreenY = topSettingLabelScreenCoords.y + (settingScreenVerticalSpacing * i);
            settingLabelScreenCoords.add(new Vector2f(settingLabelScreenX, settingScreenY));
            settingValueScreenCoords.add(new Vector2f(settingValueScreenX, settingScreenY));
        }

        // Scroll arrow coordinates.
        float rightScrollArrowWorldSettingValueRightAdjustment = 15.4f;
        float rightScrollArrowScreenSettingValueRightAdjustment =
                gp.getCamera().worldWidthToScreenWidth(rightScrollArrowWorldSettingValueRightAdjustment);
        float leftScrollArrowWorldRightScrollArrowRightAdjustment = 15.4f;
        float leftScrollArrowScreenRightScrollArrowRightAdjustment =
                gp.getCamera().worldWidthToScreenWidth(leftScrollArrowWorldRightScrollArrowRightAdjustment);
        rightScrollArrowScreenCoords.x =
                settingValueScreenX - rightScrollArrowScreenSettingValueRightAdjustment - scrollArrowScreenDimensions.x;
        leftScrollArrowScreenCoords.x =
                rightScrollArrowScreenCoords.x
                        - leftScrollArrowScreenRightScrollArrowRightAdjustment
                        - scrollArrowScreenDimensions.x;

        // Footer divider coordinates.
        float footerDividerScreenY =
                topSettingLabelScreenCoords.y + (settingScreenVerticalSpacing * (gp.getSystemSettingsSize() - 1))
                        + standardNormalCharScreenHeight + settingLabelScreenHeaderDividerBottomAdjustment;
        footerDividerScreenTransform.position.y = footerDividerScreenY;

        // Selected setting description coordinates.
        float settingDescriptionTextScreenY = footerDividerScreenTransform.position.y
                + footerDividerScreenTransform.scale.y + settingLabelScreenHeaderDividerBottomAdjustment;
        settingDescriptionTextScreenCoords.y = settingDescriptionTextScreenY;

        // Dirty flag.
        dirty = false;
    }


    /**
     * Adds settings menu user interface components to the render pipeline.
     * Note that the contents of the user interface components will automatically reflect their latest states.
     *
     * @param renderer Renderer instance
     */
    public void addToRenderPipeline(Renderer renderer) {

        // Setting labels and values.
        for (int i = 0; i < gp.getSystemSettingsSize(); i++) {

            gp.getCamera().screenCoordsToWorldCoords(settingLabelScreenCoords.get(i), tempWorldTransform.position);

            if (i == systemSettingSelected) {

                // Setting label.
                renderer.addString(
                        gp.getSystemSetting(i).getLabel(),
                        tempWorldTransform.position.x,
                        tempWorldTransform.position.y,
                        gp.getUi().getStandardFontScale(),
                        settingLabelTextActiveColor,
                        gp.getUi().getStandardBoldFont(),
                        ZIndex.SECOND_LAYER
                );

                // Scroll arrows.
                rightScrollArrowScreenCoords.y =
                        settingValueScreenCoords.get(i).y
                                + ((standardNormalCharScreenHeight - scrollArrowScreenDimensions.y) / 2);
                leftScrollArrowScreenCoords.y = rightScrollArrowScreenCoords.y;
                gp.getGuiIconM().addToRenderPipeline(
                        renderer,
                        9,
                        leftScrollArrowScreenCoords.x,
                        leftScrollArrowScreenCoords.y
                );
                gp.getGuiIconM().addToRenderPipeline(
                        renderer,
                        10,
                        rightScrollArrowScreenCoords.x,
                        rightScrollArrowScreenCoords.y
                );
            } else {

                // Setting label.
                renderer.addString(
                        gp.getSystemSetting(i).getLabel(),
                        tempWorldTransform.position.x,
                        tempWorldTransform.position.y,
                        gp.getUi().getStandardFontScale(),
                        settingLabelTextInactiveColor,
                        gp.getUi().getStandardNormalFont(),
                        ZIndex.SECOND_LAYER
                );
            }

            // Setting value.
            gp.getCamera().screenCoordsToWorldCoords(settingValueScreenCoords.get(i), tempWorldTransform.position);
            renderer.addString(
                    gp.getSystemSetting(i).getOption(gp.getSystemSetting(i).getActiveOption()),
                    tempWorldTransform.position.x,
                    tempWorldTransform.position.y,
                    gp.getUi().getStandardFontScale(),
                    settingValueTextColor,
                    gp.getUi().getStandardNormalFont(),
                    ZIndex.SECOND_LAYER
            );
        }

        // Footer divider.
        gp.getCamera().screenCoordsToWorldCoords(footerDividerScreenTransform.position, tempWorldTransform.position);
        gp.getCamera().screenDimensionsToWorldDimensions(footerDividerScreenTransform.scale, tempWorldTransform.scale);
        renderer.addRectangle(
                footerDividerColor,
                tempWorldTransform,
                ZIndex.SECOND_LAYER
        );

        // Selected setting description.
        gp.getUi().addStringBlockToRenderPipeline(
                gp.getSystemSetting(systemSettingSelected).getDescription(),
                settingDescriptionTextScreenCoords.x,
                settingDescriptionTextScreenCoords.y,
                settingDescriptionTextLineScreenMaxWidth,
                settingDescriptionTextLineScreenVerticalSpacing,
                gp.getUi().getStandardFontScale(),
                settingDescriptionTextColor,
                gp.getUi().getStandardNormalFont(),
                ZIndex.SECOND_LAYER,
                true
        );
    }


    /**
     * Sets which system setting is active in the settings menu.
     *
     * @param systemSettingSelected index of system setting to select
     */
    public void setSystemSettingSelected(int systemSettingSelected) {

        if ((systemSettingSelected >= 0) && (systemSettingSelected < gp.getSystemSettingsSize())) {

            this.systemSettingSelected = systemSettingSelected;
            systemOptionSelected = gp.getSystemSetting(systemSettingSelected).getActiveOption();
            systemOptionSelected = gp.getSystemSetting(systemSettingSelected).getActiveOption();
            refreshScrollArrows();
        }
    }


    /**
     * Sets which system setting option (i.e., the value of the selected/active system setting) is active in the
     * settings menu.
     *
     * @param systemOptionSelected index of system setting option to select
     */
    public void setSystemOptionSelected(int systemOptionSelected) {
        if ((systemOptionSelected >= 0)
                && (systemOptionSelected < gp.getSystemSetting(systemSettingSelected).getOptionsSize())) {
            this.systemOptionSelected = systemOptionSelected;
            gp.getSystemSetting(systemSettingSelected).setActiveOption(systemOptionSelected);
            refreshScrollArrows();
        }
    }


    /**
     * Marks this user interface component as "dirty" to indicate that it must be refreshed to display updated
     * information.
     * In this case, a refresh must be done if the number of settings displayed on screen at once changes.
     * Note that a refresh is not necessary if the contents/state of the user interface components change (e.g.,
     * scrolling through the different options of a setting, scrolling between settings).
     */
    public void markDirty() {

        dirty = true;
    }


    /**
     * Initializes settings menu user interface components that will not change while the game is running.
     * These user interface components are the "core" aspects of the settings menu layout, such as the positioning of
     * topmost setting listed.
     *
     * @param renderer Renderer instance
     */
    private void init(Renderer renderer) {

        // Selection management.
        systemSettingSelected = 0;

        // Temporary world coordinates and dimensions.
        Vector2f tempWorldCoords = new Vector2f(0.0f, 0.0f);
        Vector2f tempWorldDimensions = new Vector2f(0.0f, 0.0f);
        tempWorldTransform = new Transform(tempWorldCoords, tempWorldDimensions);

        // Colors.
        settingLabelTextInactiveColor = new Vector3f(255, 255, 255);
        settingLabelTextActiveColor = new Vector3f(244, 154, 45);
        settingValueTextColor = new Vector3f(255, 255, 255);
        footerDividerColor = new Vector4f(255, 255, 255, 255);
        settingDescriptionTextColor = new Vector3f(255, 255, 255);

        // Text sizing.
        float standardNormalCharWorldHeight = renderer.getFont(gp.getUi().getStandardNormalFont())
                .getCharacter('A').getHeight() * gp.getUi().getStandardFontScale();                                     // It doesn't matter which character is used, since all characters in a font have the same height.
        standardNormalCharScreenHeight =
                gp.getCamera().worldHeightToScreenHeight(standardNormalCharWorldHeight);

        // Setting label and value coordinates.
        float settingWorldVerticalSpacing = 35.4f;
        settingScreenVerticalSpacing = gp.getCamera().worldHeightToScreenHeight(settingWorldVerticalSpacing);

        float settingLabelScreenPrimaryWindowLeftAdjustment = gp.getUiPrimaryMenuFrameS().getHeaderDividerScreenX()
                - gp.getUiPrimaryMenuFrameS().getPrimaryWindowScreenX();
        float settingLabelWorldPrimaryWindowLeftAdjustment =
                gp.getCamera().screenWidthToWorldWidth(settingLabelScreenPrimaryWindowLeftAdjustment);

        settingLabelScreenHeaderDividerBottomAdjustment =
                gp.getCamera().worldHeightToScreenHeight(settingLabelWorldPrimaryWindowLeftAdjustment) / 2;

        float topSettingLabelScreenX =
                gp.getUiPrimaryMenuFrameS().getPrimaryWindowScreenX() + settingLabelScreenPrimaryWindowLeftAdjustment;
        float topSettingLabelScreenY = gp.getUiPrimaryMenuFrameS().getHeaderDividerScreenY()
                + gp.getUiPrimaryMenuFrameS().getHeaderDividerScreenHeight()
                + settingLabelScreenHeaderDividerBottomAdjustment;
        topSettingLabelScreenCoords = new Vector2f(topSettingLabelScreenX, topSettingLabelScreenY);

        settingLabelScreenCoords = new ArrayList<>(5);
        settingValueScreenCoords = new ArrayList<>(5);

        // Scroll arrow dimensions.
        float scrollArrowWorldWidth = gp.getGuiIconM().getIconById(9).getNativeSpriteWidth();                           // Both the left and right scroll arrows have the same width, so either can be referenced.
        float scrollArrowScreenWidth = gp.getCamera().worldWidthToScreenWidth(scrollArrowWorldWidth);
        float scrollArrowWorldHeight = gp.getGuiIconM().getIconById(9).getNativeSpriteHeight();                         // Both the left and right scroll arrows have the same height, so either can be referenced.
        float scrollArrowScreenHeight = gp.getCamera().worldHeightToScreenHeight(scrollArrowWorldHeight);
        scrollArrowScreenDimensions = new Vector2f(scrollArrowScreenWidth, scrollArrowScreenHeight);

        rightScrollArrowScreenCoords = new Vector2f(0.0f);
        leftScrollArrowScreenCoords = new Vector2f(0.0f);

        // Footer divider coordinates and dimensions.
        float footerDividerScreenX = gp.getUiPrimaryMenuFrameS().getHeaderDividerScreenX();
        Vector2f footerDividerScreenCoords = new Vector2f(footerDividerScreenX, 0.0f);                                  // y-value is placeholder (will change while refreshing).

        float footerDividerScreenWidth = gp.getUiPrimaryMenuFrameS().getHeaderDividerScreenWidth();
        float footerDividerScreenHeight = gp.getUiPrimaryMenuFrameS().getHeaderDividerScreenHeight();

        Vector2f footerDividerScreenDimensions = new Vector2f(footerDividerScreenWidth, footerDividerScreenHeight);
        footerDividerScreenTransform = new Transform(footerDividerScreenCoords, footerDividerScreenDimensions);

        // Selected setting description coordinates and dimensions.
        float settingDescriptionTextLineWorldVerticalSpacing = 28.0f;
        settingDescriptionTextLineScreenVerticalSpacing =
                gp.getCamera().worldHeightToScreenHeight(settingDescriptionTextLineWorldVerticalSpacing);

        settingDescriptionTextLineScreenMaxWidth = footerDividerScreenWidth;
        float settingDescriptionTextScreenX = footerDividerScreenX;

        settingDescriptionTextScreenCoords = new Vector2f(settingDescriptionTextScreenX, 0.0f);                         // y-value is placeholder (will change while refreshing).
    }


    /**
     * Refreshes the scroll arrows (both left and right) in the settings menu.
     * This affects whether each appears as active or not.
     */
    private void refreshScrollArrows() {

        if (systemOptionSelected > 0) {                                                                                 // Left arrow.

            gp.getGuiIconM().getIconById(9).setSelected(true);
        } else {

            gp.getGuiIconM().getIconById(9).setSelected(false);
        }

        if (systemOptionSelected < gp.getSystemSetting(systemSettingSelected).getOptionsSize() - 1) {                   // Right arrow.

            gp.getGuiIconM().getIconById(10).setSelected(true);
        } else {

            gp.getGuiIconM().getIconById(10).setSelected(false);
        }
    }


    // GETTERS
    public boolean isDirty() {
        return dirty;
    }

    public int getSystemSettingSelected() {
        return systemSettingSelected;
    }

    public int getSystemOptionSelected() {
        return systemOptionSelected;
    }


}
