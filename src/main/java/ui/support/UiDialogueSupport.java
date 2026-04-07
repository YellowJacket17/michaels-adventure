package ui.support;

import core.GamePanel;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import render.Renderer;
import render.drawable.Transform;
import render.enumeration.ZIndex;

/**
 * This class contains logic for  rendering user interface components related to dialogue.
 */
public class UiDialogueSupport {

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

    private Transform tempWorldTransform;
    
    private Vector4f windowColor;

    private Vector3f dialogueTextColor;

    private Vector3f speakerTextColor;

    private float dialogueWindowScreenLeftRightPadding;
    
    private Transform dialogueWindowScreenTransform;

    private float dialogueTextScreenVerticalSpacing;

    private Vector2f dialogueTextScreenCoords;

    private float speakerWindowScreenLeftRightPadding;
    
    private Transform speakerWindowScreenTransform;

    private Vector2f speakerTextScreenCoords;

    private Vector2f dialogueArrowScreenCoords;


    // CONSTRUCTOR
    /**
     * Constructs a UiDialogueSupport instance.
     *
     * @param gp GamePanel instance
     * @param renderer Renderer instance
     */
    public UiDialogueSupport(GamePanel gp, Renderer renderer) {
        this.gp = gp;
        init(renderer);
    }


    // METHODS
    /**
     * Refreshes dialogue user interface components that may change while the game is running.
     * In this case, a refresh must be done if the piece of dialogue being read is changed or if the window opacity is
     * changed.
     *
     * @param renderer Renderer instance
     */
    public void refresh(Renderer renderer) {

        // Dialogue window.
        if (gp.getDialogueR().getActiveDialogueSpeaker() != null
                && !gp.getDialogueR().getActiveDialogueSpeaker().equals("")) {

            String speakerName = gp.getDialogueR().getActiveDialogueSpeaker();
            float speakerNameWorldWidth = 0;

            for (int i = 0; i < speakerName.length(); i++) {

                char speakerChar = speakerName.charAt(i);
                speakerNameWorldWidth += renderer.getFont(gp.getUi().getStandardNormalFont())
                        .getCharacter(speakerChar).getWidth() * gp.getUi().getStandardFontScale();
            }
            float speakerNameScreenWidth = gp.getCamera().worldWidthToScreenWidth(speakerNameWorldWidth);
            float speakerWindowScreenWidth = speakerNameScreenWidth + (2 * speakerWindowScreenLeftRightPadding);
            speakerWindowScreenTransform.scale.x = speakerWindowScreenWidth;
        }

        // Dialogue and speaker window color.
        windowColor.w = gp.getUi().getWindowOpacity();

        // Dirty flag.
        dirty = false;
    }


    /**
     * Adds dialogue user interface components to the render pipeline.
     *
     * @param renderer Renderer instance
     */
    public void addToRenderPipeline(Renderer renderer) {

        // Dialogue window.
        gp.getCamera().screenCoordsToWorldCoords(dialogueWindowScreenTransform.position, tempWorldTransform.position);
        gp.getCamera().screenDimensionsToWorldDimensions(dialogueWindowScreenTransform.scale, tempWorldTransform.scale);
        renderer.addRectangle(windowColor, tempWorldTransform, ZIndex.FIRST_LAYER);

        // Dialogue text.
        dialogueTextScreenCoords.y = dialogueWindowScreenTransform.position.y + dialogueTextScreenVerticalSpacing;
        gp.getCamera().screenCoordsToWorldCoords(dialogueTextScreenCoords, tempWorldTransform.position);

        for (int key = 0; key < gp.getDialogueR().getMaxNumPrintLines(); key++) {

            renderer.addString(
                    gp.getDialogueR().getDialoguePrint(key),
                    tempWorldTransform.position.x,
                    tempWorldTransform.position.y,
                    gp.getUi().getStandardFontScale(),
                    dialogueTextColor,
                    gp.getUi().getStandardNormalFont(),
                    ZIndex.FIRST_LAYER
            );
            dialogueTextScreenCoords.y += standardNormalCharScreenHeight + dialogueTextScreenVerticalSpacing;
        }

        // Speaker window and speaker text.
        if (gp.getDialogueR().getActiveDialogueSpeaker() != null
                && !gp.getDialogueR().getActiveDialogueSpeaker().equals("")) {

            gp.getCamera().screenCoordsToWorldCoords(
                    speakerWindowScreenTransform.position, tempWorldTransform.position);
            gp.getCamera().screenDimensionsToWorldDimensions(
                    speakerWindowScreenTransform.scale, tempWorldTransform.scale);
            renderer.addRectangle(windowColor, tempWorldTransform, ZIndex.FIRST_LAYER);

            gp.getCamera().screenCoordsToWorldCoords(speakerTextScreenCoords, tempWorldTransform.position);
            renderer.addString(
                    gp.getDialogueR().getActiveDialogueSpeaker(),
                    tempWorldTransform.position.x,
                    tempWorldTransform.position.y,
                    gp.getUi().getStandardFontScale(),
                    speakerTextColor,
                    gp.getUi().getStandardNormalFont(),
                    ZIndex.FIRST_LAYER
            );
        }

        // Dialogue arrow.
        if ((!gp.getDialogueR().isReadingDialogue()
                    && gp.getDialogueR().isDialoguePaused()
                    && (gp.getEntityM().getPlayer().getInteractionCountdown() <= 0))
                || (!gp.getDialogueR().isReadingDialogue()
                    && (gp.getDialogueR().isAlwaysShowArrow())
                    && (gp.getEntityM().getPlayer().getInteractionCountdown() <= 0))) {

            gp.getDialogueA().addToRenderPipeline(renderer, dialogueArrowScreenCoords.x, dialogueArrowScreenCoords.y);
        }
    }


    /**
     * Marks this user interface component as "dirty" to indicate that it must be refreshed to display updated
     * information.
     * In this case, a refresh must be done if the piece of dialogue being read is changed or if the window opacity is
     * changed.
     */
    public void markDirty() {

        dirty = true;
    }


    /**
     * Initializes dialogue user interface components that will not change while the game is running.
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
        dialogueTextColor = new Vector3f(255, 255, 255);
        speakerTextColor = new Vector3f(121, 149, 255);

        // Text sizing.
        float standardNormalCharWorldHeight = renderer.getFont(gp.getUi().getStandardNormalFont())
                .getCharacter('A').getHeight() * gp.getUi().getStandardFontScale();                                     // It doesn't matter which character is used, since all characters in a font have the same height.
        standardNormalCharScreenHeight =
                gp.getCamera().worldHeightToScreenHeight(standardNormalCharWorldHeight);

        // Dialogue window coordinates and dimensions.
        float dialogueWindowWorldLeftRightPadding = 23.0f;
        dialogueWindowScreenLeftRightPadding =
                gp.getCamera().worldWidthToScreenWidth(dialogueWindowWorldLeftRightPadding);
        float dialogueWindowScreenWidth = 1.0f;
        float dialogueWindowWorldHeight = 86.4f;
        float dialogueWindowScreenHeight = gp.getCamera().worldHeightToScreenHeight(dialogueWindowWorldHeight);
        float dialogueWindowScreenX = 0.0f;
        float dialogueWindowScreenY = 1 - dialogueWindowScreenHeight;
        Vector2f dialogueWindowScreenCoords = new Vector2f(dialogueWindowScreenX, dialogueWindowScreenY);
        Vector2f dialogueWindowScreenDimensions = new Vector2f(dialogueWindowScreenWidth, dialogueWindowScreenHeight);
        dialogueWindowScreenTransform = new Transform(dialogueWindowScreenCoords, dialogueWindowScreenDimensions);

        // Dialogue text coordinates.
        dialogueTextScreenVerticalSpacing = (dialogueWindowScreenHeight - (2 * standardNormalCharScreenHeight))
                / (gp.getDialogueR().getMaxNumPrintLines() + 1);
        float dialogueTextScreenX = dialogueWindowScreenX + dialogueWindowScreenLeftRightPadding;
        float dialogueTextScreenY = 0.0f;                                                                               // y-value is placeholder (will change while rendering).
        dialogueTextScreenCoords = new Vector2f(dialogueTextScreenX, dialogueTextScreenY);

        // Speaker window coordinates and dimensions.
        float speakerWindowWorldLeftRightPadding = 15.3f;
        speakerWindowScreenLeftRightPadding =
                gp.getCamera().worldWidthToScreenWidth(speakerWindowWorldLeftRightPadding);
        float speakerWindowWorldTopBottomPadding = 8.6f;
        float speakerWindowScreenTopBottomPadding =
                gp.getCamera().worldHeightToScreenHeight(speakerWindowWorldTopBottomPadding);
        float speakerWindowScreenHeight = standardNormalCharScreenHeight + (2 * speakerWindowScreenTopBottomPadding);
        float speakerWindowWorldX = 23.0f;
        float speakerWindowScreenX = gp.getCamera().worldWidthToScreenWidth(speakerWindowWorldX);
        float speakerWindowScreenY = dialogueWindowScreenCoords.y - speakerWindowScreenHeight;
        Vector2f speakerWindowScreenCoords = new Vector2f(speakerWindowScreenX, speakerWindowScreenY);
        Vector2f speakerWindowScreenDimensions = new Vector2f(0.0f, speakerWindowScreenHeight);                         // x-value is placeholder (will change while refreshing).
        speakerWindowScreenTransform = new Transform(speakerWindowScreenCoords, speakerWindowScreenDimensions);

        // Speaker text coordinates.
        float speakerTextScreenX = speakerWindowScreenX + speakerWindowScreenLeftRightPadding;
        float speakerTextScreenY = speakerWindowScreenY + speakerWindowScreenTopBottomPadding;
        speakerTextScreenCoords = new Vector2f(speakerTextScreenX, speakerTextScreenY);

        // Dialogue arrow coordinates.
        float dialogueArrowWorldDialogueWindowRightAdjustment = 15.4f;
        float dialogueArrowScreenDialogueWindowRightAdjustment =
                gp.getCamera().worldWidthToScreenWidth(dialogueArrowWorldDialogueWindowRightAdjustment);
        float dialogueArrowWorldDialogueArrowBottomAdjustment = 13.0f;
        float dialogueArrowScreenDialogueArrowBottomAdjustment =
                gp.getCamera().worldHeightToScreenHeight(dialogueArrowWorldDialogueArrowBottomAdjustment);
        float dialogueArrowScreenX =
                dialogueWindowScreenCoords.x + dialogueWindowScreenWidth
                        - dialogueArrowScreenDialogueWindowRightAdjustment;
        float dialogueArrowScreenY =
                dialogueWindowScreenCoords.y + dialogueWindowScreenHeight
                        - dialogueArrowScreenDialogueArrowBottomAdjustment;
        dialogueArrowScreenCoords = new Vector2f(dialogueArrowScreenX, dialogueArrowScreenY);
    }


    // GETTERS
    public boolean isDirty() {
        return dirty;
    }

    public float getDialogueWindowScreenX() {

        return dialogueWindowScreenTransform.position.x;
    }

    public float getDialogueWindowScreenY() {

        return dialogueWindowScreenTransform.position.y;
    }

    public float getDialogueWindowScreenWidth() {

        return dialogueWindowScreenTransform.scale.x;
    }

    public float getDialogueWindowScreenHeight() {

        return dialogueWindowScreenTransform.scale.y;
    }

    public float getSpeakerWindowScreenX() {

        return speakerWindowScreenTransform.position.x;
    }

    public float getSpeakerWindowScreenY() {

        return speakerWindowScreenTransform.position.y;
    }

    public float getSpeakerWindowScreenWidth() {

        return speakerWindowScreenTransform.scale.x;
    }

    public float getSpeakerWindowScreenHeight() {

        return speakerWindowScreenTransform.scale.y;
    }
}
