package ui;

import combat.enumeration.BannerColor;
import combat.enumeration.SubMenuType;
import core.GamePanel;
import ui.enumeration.PartyMenuSlot;
import ui.enumeration.PrimaryMenuState;
import core.enumeration.PrimaryGameState;
import entity.EntityBase;
import entity.enumeration.EntityStatus;
import event.enumeration.FadeState;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import render.Renderer;
import render.enumeration.ZIndex;
import render.drawable.Transform;

/**
 * This class handles the rendering of all user interface (UI) components.
 */
public class UserInterface {

    // TODO : Create a 'UiCombatSupport' class that handles everything invoked by 'addCombatUiComponentsToRenderPipeline()'.

    // TODO : The UserInterface class could generally benefit from being cleaned up and optimized.

    // TODO : Research String.intern() for possible efficiency gains.

    // FIELDS
    private final GamePanel gp;

    private Renderer renderer;

    /**
     * Variable to store which primary menu state that the game is in.
     * This will affect which menu is rendered, if any.
     * This is not to be confused with sub-menus, which are not affected by this.
     * This will also not affect how player inputs function (i.e., only affects what is rendered, not how it's
     * interacted with).
     */
    private PrimaryMenuState primaryMenuState = PrimaryMenuState.INACTIVE;

    /**
     * Standard normal font to use when rendering UI text (dialogue, sub-menu, etc.).
     */
    private final String standardNormalFont = "Pixel Operator";

    /**
     * Standard bold font to use when rendering UI text (dialogue, sub-menu, etc.).
     */
    private final String standardBoldFont = "Pixel Operator Bold";

    /**
     * Standard scaling applied to native font size when rendering UI text (dialogue, sub-menu, etc.).
     * This value multiplies the native height of the rendered font.
     * The larger the value, the larger the rendered text.
     * Note that this variable does not apply to "specialized" UI text ("HP" label next to life bar, etc.).
     */
    private final float standardFontScale = 0.18f;

    /**
     * Default opacity for rendered user interface window elements (dialogue window, sub-menu window, etc.).
     */
    private final float defaultWindowOpacity = 220;

    /**
     * Opacity for rendered user interface window elements (dialogue window, sub-menu window, etc.).
     */
    private float windowOpacity = defaultWindowOpacity;

    /**
     * Variable to track the FPS value displayed in debug mode.
     * This value is updated every one second with the frame rate at that instant, regardless of whether debug mode is
     * active or not.
     */
    private int fpsTracker;

    /**
     * Core time counter (seconds) for controlling when the FPS tracker variable is updated.
     */
    private double fpsCounter;


    // CONSTRUCTOR
    /**
     * Constructs a UserInterface instance.
     *
     * @param gp GamePanel instance
     */
    public UserInterface(GamePanel gp) {
        this.gp = gp;
    }


    // METHODS
    /**
     * Updates the state of the user interface by one frame.
     * This includes user interface support classes.
     *
     * @param dt time since last frame (seconds)
     */
    public void update(double dt) {

        fpsCounter -= dt;
        if (fpsCounter <= 0) {
            fpsTracker = (int)(1.0 / dt);
            fpsCounter += 1;                                                                                            // FPS displayed in debug mode will be updated every one second.
        }

        // PRIMARY MENU
        switch (primaryMenuState) {
            case PARTY:
                if (gp.getUiPrimaryMenuFrameS().isDirty()) {
                    gp.getUiPrimaryMenuFrameS().refresh();
                }
                if (gp.getUiPartyMenuS().isDirty()) {
                    gp.getUiPartyMenuS().refresh();
                }
                break;
            case INVENTORY:
                if (gp.getUiPrimaryMenuFrameS().isDirty()) {
                    gp.getUiPrimaryMenuFrameS().refresh();
                }
                break;
            case SETTINGS:
                if (gp.getUiPrimaryMenuFrameS().isDirty()) {
                    gp.getUiPrimaryMenuFrameS().refresh();
                }
                if (gp.getUiSettingsMenuS().isDirty()) {
                    gp.getUiSettingsMenuS().refresh();
                }
                break;
        }

        // DIALOGUE
        if (gp.getUiDialogueS().isDirty()) {
            if (gp.getUiDialogueS().isDirty()) {
                gp.getUiDialogueS().refresh(renderer);
            }
        }

        // SUB-MENU
        if (gp.getUiSubMenuS().isDirty()) {
            if (gp.getUiSubMenuS().isDirty()) {
                gp.getUiSubMenuS().refresh();
            }
        }
    }


    /**
     * Adds all UI elements to the render pipeline.
     * This includes user interface support classes.
     *
     * @param renderer Renderer instance
     */
    public void addToRenderPipeline(Renderer renderer) {

        if (this.renderer != renderer) {
            this.renderer = renderer;                                                                                   // Makes it easier to access current renderer across entire class.
        }

        // FADE
        if ((gp.getFadeS().getState() != FadeState.INACTIVE) || (gp.getFadeS().isFlashActive())) {
            addFadeUiComponentsToRenderPipeline();
        }

        // PRIMARY MENU
        switch (primaryMenuState) {
            case PARTY:
                gp.getUiPrimaryMenuFrameS().addToRenderPipeline(renderer);
                gp.getUiPartyMenuS().addToRenderPipeline(renderer);
                break;
            case INVENTORY:
                gp.getUiPrimaryMenuFrameS().addToRenderPipeline(renderer);
                gp.getUiInventoryMenuS().addToRenderPipeline(renderer);
                break;
            case SETTINGS:
                gp.getUiPrimaryMenuFrameS().addToRenderPipeline(renderer);
                gp.getUiSettingsMenuS().addToRenderPipeline(renderer);
                break;
        }

        // COMBAT
        if (gp.getCombatM().isCombatActive()) {
            addCombatUiComponentsToRenderPipeline();
        }

        // DIALOGUE
        if (gp.getDialogueR().getActiveConv() != null) {
            gp.getUiDialogueS().addToRenderPipeline(renderer);
        }

        // SUB-MENU
        if (gp.getSubMenuH().getSubMenuId() != -1) {
            gp.getUiSubMenuS().addToRenderPipeline(renderer);
        }

        // DEBUG
        if (gp.isDebugActive()) {
            addDebugToRenderPipeline();
        }
    }


    /**
     * Calculates the world length of a string of text.
     * Text is assumed to be rendered horizontally (screen width).
     *
     * @param text text whose world length is to be calculated
     * @param scale scale factor at which to render text compared to native font size
     * @param font name of font to use
     */
    public float calculateStringWorldLength(String text, float scale, String font) {

        float textWorldWidth = 0;

        for (int i = 0; i < text.length(); i++) {

            char character = text.charAt(i);
            textWorldWidth += renderer.getFont(font).getCharacter(character).getWidth() * scale;
        }
        return textWorldWidth;
    }


    /**
     * Calculates the normalized (screen) length of a string of text.
     * Text is assumed to be rendered horizontally (screen width).
     *
     * @param text text whose screen length is to be calculated
     * @param scale scale factor at which to render text compared to native font size
     * @param font name of font to use
     */
    public float calculateStringScreenLength(String text, float scale, String font) {

        return gp.getCamera().worldWidthToScreenWidth(calculateStringWorldLength(text, scale, font));
    }


    /**
     * Adds a life bar to the render pipeline.
     * Note that this renders both the border and the "fill" of the life bar.
     *
     * @param life number of remaining life points
     * @param maxLife maximum number of life points
     * @param screenWidth normalized (screen) width of the life bar
     * @param screenX screen x-coordinate of the life bar (leftmost)
     * @param screenY screen y-coordinate of the life bar (topmost)
     */
    public void addLifeBarToRenderPipeline(int life, int maxLife, float screenWidth, float screenX, float screenY) {

        // Calculate remaining life percent (0 to 1, both inclusive).
        float remainingLifePercentage = (float)life/ (float)maxLife;                                                    // Decimal percentage of life that the entity has relative to its maximum life.
        if (remainingLifePercentage > 1) {
            remainingLifePercentage = 1;                                                                                // Prevent the life bar from over-filling.
        } else if (remainingLifePercentage < 0) {
            remainingLifePercentage = 0;                                                                                // Prevent the life bar from using a negative value.
        }

        // Calculate exterior (i.e., border) dimensions and color.
        Vector2f screenCoordsExterior = new Vector2f(screenX, screenY);
        Vector2f worldCoordsExterior = gp.getCamera().screenCoordsToWorldCoords(screenCoordsExterior);
        float worldWidthExterior = gp.getCamera().screenWidthToWorldWidth(screenWidth);
        float worldHeightExterior = 6.0f;
        Vector4f colorExterior = new Vector4f(230, 250, 255, 255);

        // Calculate interior (i.e., fill) dimensions and color (both primary and secondary).
        // Primary is bar that changes with life.
        // Secondary is background to primary that occupies void left by depleted life.
        Vector2f worldCoordsInterior = new Vector2f(worldCoordsExterior.x + 1.0f, worldCoordsExterior.y + 1.0f);
        float worldWidthInterior = worldWidthExterior - 2.0f;
        float worldHeightInterior = 4.0f;
        Vector4f colorInteriorPrimary;
        if (remainingLifePercentage <= 0.125f) {                                                                        // Eighth of life or less remaining.
            colorInteriorPrimary = new Vector4f(255, 46, 102, 220);
        } else if (remainingLifePercentage <= 0.5f) {                                                                   // Half of life or less remaining.

            colorInteriorPrimary = new Vector4f(255, 242, 78, 220);
        } else {                                                                                                        // Greater than half of life remaining.

            colorInteriorPrimary = new Vector4f(46, 255, 139, 220);
        }
        Vector4f colorInteriorSecondary = new Vector4f(53, 64, 68, 255);

        // Add exterior and interior (primary and secondary) to render pipeline.
        renderer.addRectangle(                                                                                          // Render life bar top/bottom border (exterior).
                colorExterior,
                new Transform(
                        worldCoordsExterior,
                        new Vector2f(worldWidthExterior, worldHeightExterior)
                ),
                ZIndex.SECOND_LAYER
        );
        renderer.addRectangle(                                                                                          // Render life bar fill (interior).
                colorInteriorSecondary,
                new Transform(
                        worldCoordsInterior,
                        new Vector2f(worldWidthInterior, worldHeightInterior)
                ),
                ZIndex.SECOND_LAYER
        );
        renderer.addRectangle(                                                                                          // Render life bar fill (interior).
                colorInteriorPrimary,
                new Transform(
                        worldCoordsInterior,
                        new Vector2f(worldWidthInterior * remainingLifePercentage, worldHeightInterior)
                ),
                ZIndex.SECOND_LAYER
        );
    }


    /**
     * Adds a skill bar to the render pipeline.
     * Note that this renders both the border and the "fill" of the skill bar.
     *
     * @param skill number of remaining skill points
     * @param maxSkill maximum number of skill points
     * @param screenWidth normalized (screen) width of the skill bar
     * @param screenX screen x-coordinate of the skill bar (leftmost)
     * @param screenY screen y-coordinate of the skill bar (topmost)
     */
    public void addSkillBarToRenderPipeline(int skill, int maxSkill, float screenWidth, float screenX, float screenY) {

        // Calculate remaining skill percent (0 to 1, both inclusive).
        float remainingLifePercentage = (float)skill/ (float)maxSkill;                                                  // Decimal percentage of life that the entity has relative to its maximum skill.
        if (remainingLifePercentage > 1) {
            remainingLifePercentage = 1;                                                                                // Prevent the skill bar from over-filling.
        } else if (remainingLifePercentage < 0) {
            remainingLifePercentage = 0;                                                                                // Prevent the skill bar from using a negative value.
        }

        // Calculate exterior (i.e., border) dimensions and color.
        Vector2f screenCoordsExterior = new Vector2f(screenX, screenY);
        Vector2f worldCoordsExterior = gp.getCamera().screenCoordsToWorldCoords(screenCoordsExterior);
        float worldWidthExterior = gp.getCamera().screenWidthToWorldWidth(screenWidth);
        float worldHeightExterior = 6.0f;
        Vector4f colorExterior = new Vector4f(230, 250, 255, 255);

        // Calculate interior (i.e., fill) dimensions and color (both primary and secondary).
        // Primary is bar that changes with skill.
        // Secondary is background to primary that occupies void left by depleted skill.
        Vector2f worldCoordsInterior = new Vector2f(worldCoordsExterior.x + 1.0f, worldCoordsExterior.y + 1.0f);
        float worldWidthInterior = worldWidthExterior - 2.0f;
        float worldHeightInterior = 4.0f;
        Vector4f colorInteriorPrimary = new Vector4f(192, 47, 255, 255);
        Vector4f colorInteriorSecondary = new Vector4f(53, 64, 68, 255);

        // Add exterior and interior (primary and secondary) to render pipeline.
        renderer.addRectangle(                                                                                          // Render life bar top/bottom border (exterior).
                colorExterior,
                new Transform(
                        worldCoordsExterior,
                        new Vector2f(worldWidthExterior, worldHeightExterior)
                ),
                ZIndex.SECOND_LAYER
        );
        renderer.addRectangle(                                                                                          // Render life bar fill (interior).
                colorInteriorSecondary,
                new Transform(
                        worldCoordsInterior,
                        new Vector2f(worldWidthInterior, worldHeightInterior)
                ),
                ZIndex.SECOND_LAYER
        );
        renderer.addRectangle(                                                                                          // Render life bar fill (interior).
                colorInteriorPrimary,
                new Transform(
                        worldCoordsInterior,
                        new Vector2f(worldWidthInterior * remainingLifePercentage, worldHeightInterior)
                ),
                ZIndex.SECOND_LAYER
        );
    }


    /**
     * Adds a string of text to the render pipeline.
     *
     * @param text text to be printed
     * @param screenX screen x-coordinate of the text block (leftmost, normalized from 0 to 1, both inclusive)
     * @param screenY screen y-coordinate of the text block (topmost, normalized from 0 to 1, both inclusive)
     * @param scale scale factor at which to render text compared to native font size
     * @param color text color (r, g, b)
     * @param font name of font to use
     * @param zIndex layer on which to render; strings will always be rendered after other drawables on the same layer,
     *               regardless of the order in which they were added to the render pipeline
     */
    private void addStringToRenderPipeline(String text, float screenX, float screenY, float scale,
                                           Vector3f color, String font, ZIndex zIndex) {

        renderer.addString(text, gp.getCamera().screenXToWorldX(screenX), gp.getCamera().screenYToWorldY(screenY),
                scale, color, font, zIndex
        );
    }


    /**
     * Adds a string of text with a black drop shadow to the render pipeline.
     *
     * @param text text to be printed
     * @param screenX screen x-coordinate of the text (leftmost, normalized from 0 to 1, both inclusive)
     * @param screenY screen y-coordinate of the text (topmost, normalized from 0 to 1, both inclusive)
     * @param scale scale factor at which to render text compared to native font size
     * @param color text color (r, g, b)
     * @param font name of font to use
     * @param zIndex layer on which to render; strings will always be rendered after other drawables on the same layer,
     *               regardless of the order in which they were added to the render pipeline
     */
    public void addStringShadowToRenderPipeline(String text, float screenX, float screenY, float scale,
                                                Vector3f color, String font, ZIndex zIndex) {

        Vector2f shadowScreenCoords = new Vector2f(
                screenX + gp.getCamera().worldWidthToScreenWidth(0.8f),                                                 // Hard coded as an absolute (non-screen) width since shadow cast is fixed, regardless of native screen width.
                screenY + gp.getCamera().worldHeightToScreenHeight(0.8f)                                                // Hard coded as an absolute (non-screen) height since shadow cast is fixed, regardless of native screen height.
        );
        addStringToRenderPipeline(text, shadowScreenCoords.x, shadowScreenCoords.y,
                scale, new Vector3f(0, 0, 0), font, zIndex
        );
        addStringToRenderPipeline(text, screenX, screenY, scale, color, font, zIndex
        );
    }


    // TODO : Consider refactoring the string block method in the future (heavy on memory usage each frame).
    //  Perhaps create a 'StringBlock' class that stores the full text content, text content by line, and max line screen length.
    //  A new StringBlock instance would be instantiated each frame a new, unique block of text is rendered.
    //  When this method is invoked, the inputted arguments would be first be compared to see if a StringBlock instance matches said parameters.
    //  If the inputted arguments match, then simply render based off of the matching StringBlock instance.
    //  Flag that the StringBlock instance was used (include as a field to a StringBlock, and reset all to 'false' upon each 'update()' call for the UserInterface class.
    //  If a StringBlock instance is still 'false' at the end of the beginning of the next 'update()' call, then purge it before resetting remaining StringBlock instances back to 'false'.
    /**
     * Adds a block of text with a specified line character limit to the render pipeline.
     * Note that any '\n' characters will force a new line in the rendered block of text.
     *
     * @param text block of text to be printed
     * @param screenX screen x-coordinate of the text block (leftmost, normalized from 0 to 1, both inclusive)
     * @param screenY screen y-coordinate of the text block (topmost, normalized from 0 to 1, both inclusive)
     * @param maxLineScreenLength maximum normalized (screen) length of text permitted in a printed line of text
     * @param lineScreenSpacing normalized (screen) space between each printed line of text (normalized from 0 to 1, both inclusive)
     * @param scale scale factor at which to render text compared to native font size
     * @param color color of the printed text (r, g, b)
     * @param font name of font to use
     * @param zIndex layer on which to render; strings will always be rendered after other drawables on the same layer,
     *               regardless of the order in which they were added to the render pipeline
     * @param dropShadow whether a drop shadow should be drawn (true) or not (false)
     */
    public void addStringBlockToRenderPipeline(String text, float screenX, float screenY, float maxLineScreenLength,
                                               float lineScreenSpacing, float scale, Vector3f color, String font,
                                               ZIndex zIndex, boolean dropShadow) {

        String parsedText = "";
        int charIndex = 0;

        while (charIndex < text.length()) {

            if (text.charAt(charIndex) == '\n') {                                                                       // Check if "\n" appears anywhere in the text; if so, it will be parsed out as its own word for later use.

                parsedText += " \\n ";
            } else {

                parsedText += text.charAt(charIndex);
            }
            charIndex++;
        }
        String[] words = parsedText.split(" ");                                                                         // An array of each word in the complete text, split by spaces.
        String lineFinal;                                                                                               // Declare string to store the line of text that's being built.
        String lineCandidate;                                                                                           // Declare string that will be a candidate for the line of text being built (check length measurements before adding to actual line of text).
        int wordsIndex = 0;                                                                                             // Track which index of the words array is currently being checked.
        boolean limitExceeded;                                                                                          // Declare variable to track whether the maximum character length of a line has been exceeded (true) or not (false) yet.

        while (wordsIndex < words.length) {                                                                             // Print each line of text.

            limitExceeded = false;                                                                                      // Reset variable tracking whether the maximum character length of a line has been exceeded (true) or not (false) yet.
            lineFinal = "";                                                                                             // Initialize the line of text that's being built.

            while ((!limitExceeded) && (wordsIndex < words.length)) {                                                   // Add words to a line of text until either the maximum character length is exceeded OR there are no more words to print.

                if (words[wordsIndex].equals("\\n")) {                                                                  // Check if a new line is forced.

                    limitExceeded = true;
                    wordsIndex++;
                } else {

                    if (lineFinal.equals("") && !words[wordsIndex].equals("")) {

                        lineCandidate = words[wordsIndex];
                    } else {

                        lineCandidate = lineFinal + " " + words[wordsIndex];
                    }

                    if (calculateStringScreenLength(lineCandidate, scale, font) > maxLineScreenLength) {

                        limitExceeded = true;                                                                           // Character length of the line has been exceeded.

//                        if (calculateStringScreenLength(words[wordsIndex], scale, font) > maxLineScreenLength) {
//
//                            words[wordsIndex] = "???";                                                                // If the number of characters in a single word exceeds the maximum number of characters that can be printed in a line of text, skip the word to avoid getting stuck in an infinite loop.
//                        }
                    } else {

                        lineFinal = lineCandidate;                                                                      // Set the next line of text to be rendered.
                        wordsIndex++;                                                                                   // Iterate to the next word.
                    }
                }
            }

            if (dropShadow) {

                addStringShadowToRenderPipeline(lineFinal, screenX, screenY, scale, color, font, zIndex);               // Render the line of text with a drop shadow.
            } else {

                addStringToRenderPipeline(lineFinal, screenX, screenY, scale, color, font, zIndex);                     // Render the line of text without a drop shadow.
            }

            if (wordsIndex != words.length) {

                screenY += lineScreenSpacing;                                                                           // Spacing between lines of text.
            }
        }
    }


    /**
     * Adds fade user interface components to the render pipeline (fade out to color, wait on color, fade in from
     * color).
     */
    private void addFadeUiComponentsToRenderPipeline() {

        Vector2f worldCoords = gp.getCamera().screenCoordsToWorldCoords(new Vector2f(0, 0));
        float worldWidth = GamePanel.NATIVE_SCREEN_WIDTH;
        float worldHeight = GamePanel.NATIVE_SCREEN_HEIGHT;
        float alpha = 255;

        switch (gp.getFadeS().getState()) {
            case FADE_TO:                                                                                               // Fade screen to color.
                alpha = (float)(gp.getFadeS().getFadeCounter() / gp.getFadeS().getFadeCounterFadeToMax()) * 255;
                renderer.addRectangle(
                        new Vector4f(
                                gp.getFadeS().getColor().x,
                                gp.getFadeS().getColor().y,
                                gp.getFadeS().getColor().z,
                                alpha),
                        new Transform(worldCoords, new Vector2f(worldWidth, worldHeight)),
                        ZIndex.FIRST_LAYER);
                break;
            case ACTIVE:                                                                                                // Wait on colored screen.
                renderer.addRectangle(
                        new Vector4f(
                                gp.getFadeS().getColor().x,
                                gp.getFadeS().getColor().y,
                                gp.getFadeS().getColor().z,
                                alpha),
                        new Transform(worldCoords, new Vector2f(worldWidth, worldHeight)),
                        ZIndex.FIRST_LAYER);
                break;
            case FADE_FROM:                                                                                             // Fade from color.
                alpha = 255 - ((float)(gp.getFadeS().getFadeCounter()
                        / gp.getFadeS().getFadeCounterFadeFromMax()) * 255);
                renderer.addRectangle(
                        new Vector4f(
                                gp.getFadeS().getColor().x,
                                gp.getFadeS().getColor().y,
                                gp.getFadeS().getColor().z,
                                alpha),
                        new Transform(worldCoords, new Vector2f(worldWidth, worldHeight)),
                        ZIndex.FIRST_LAYER);
                break;
        }
    }


    /**
     * Adds combat user interface components to the render pipeline.
     */
    private void addCombatUiComponentsToRenderPipeline() {

        // Combat banners.
        if (gp.getCombatM().isCombatUiVisible()) {
            addCombatStatusBannersToRenderPipeline();
        }

        // Sub-menu option descriptions.
        if (gp.getSubMenuH().getSubMenuId() != -1) {
            addCombatSubMenuDescriptionToRenderPipeline();
        }
    }


    /**
     * Adds combat status banners (i.e., banner above entity during combat displaying life, etc.) for all
     * combating entities to the render pipeline.
     */
    private void addCombatStatusBannersToRenderPipeline() {

        // Player entity.
        if (gp.getEntityM().getPlayer().getStatus() != EntityStatus.FAINT) {
            addCombatStatusBannerToRenderPipeline(gp.getEntityM().getPlayer().getEntityId(), true);
        }

        // Party entities.
        int entityIndex = 0;
        for (int entityId : gp.getEntityM().getParty().keySet()) {                                                      // Only render banners for active party members.
            if (entityIndex < gp.getEntityM().getNumActivePartyMembers()) {
                if (gp.getEntityM().getEntityById(entityId).getStatus() != EntityStatus.FAINT) {
                    addCombatStatusBannerToRenderPipeline(gp.getEntityM().getEntityById(entityId).getEntityId(), true);
                }
                entityIndex++;
            } else {
                break;
            }
        }

        // Opposing entities.
        for (int entityId : gp.getCombatM().getNonPlayerSideEntities()) {
            if (gp.getEntityM().getEntityById(entityId).getStatus() != EntityStatus.FAINT) {
                addCombatStatusBannerToRenderPipeline(gp.getEntityM().getEntityById(entityId).getEntityId(), false);
            }
        }
    }


    /**
     * Adds a combat status banner (i.e., banner above an entity during combat displaying life, etc.) for an entity to
     * the render pipeline.
     *
     * @param entityId ID of entity to render banner for
     * @param includeSkill whether the entity's skill point bar will be included (true) or not (false) in the banner
     */
    private void addCombatStatusBannerToRenderPipeline(int entityId, boolean includeSkill) {

        EntityBase entity = gp.getEntityM().getEntityById(entityId);
        float bannerWorldX;
        float bannerWorldY;

        if (includeSkill) {

            bannerWorldX = entity.getWorldX() + (entity.getNativeSpriteWidth() / 2)
                    - (gp.getLifeSkillBannerBackground().getNativeSpriteWidth() / 2);                                   // Render banner centered horizontally above target entity sprite.
            bannerWorldY = entity.getWorldY() - entity.getNativeSpriteHeight()
                    + GamePanel.NATIVE_TILE_SIZE - gp.getLifeSkillBannerBackground().getNativeSpriteHeight() + 2;       // Render banner directly above target entity sprite (minus 2 pixels down).
        } else {

            bannerWorldX = entity.getWorldX() + (entity.getNativeSpriteWidth() / 2)
                    - (gp.getLifeBannerBackground().getNativeSpriteWidth() / 2);                                        // Render banner centered horizontally above target entity sprite.
            bannerWorldY = entity.getWorldY() - entity.getNativeSpriteHeight()
                    + GamePanel.NATIVE_TILE_SIZE - gp.getLifeBannerBackground().getNativeSpriteHeight() + 2;            // Render banner directly above target entity sprite (minus 2 pixels down).
        }
        Vector2f bannerWorldCoords = new Vector2f(bannerWorldX, bannerWorldY);
        Vector2f bannerScreenCoords = gp.getCamera().worldCoordsToScreenCoords(bannerWorldCoords);
        Vector2f lifeBarWorldCoords = new Vector2f(bannerWorldX + 16.0f, bannerWorldY + 2.0f);
        Vector2f lifeBarScreenCoords = gp.getCamera().worldCoordsToScreenCoords(lifeBarWorldCoords);
        BannerColor bannerColor = BannerColor.STANDARD;

        if ((gp.getCombatM().getTurnEntityId() == entityId)
                && (gp.getPrimaryGameState() == PrimaryGameState.SUB_MENU)) {

            bannerColor = BannerColor.TURN;
        }

        if ((gp.getCombatM().getLatestSubMenuType() == SubMenuType.TARGET_SELECT)
                && (gp.getPrimaryGameState() == PrimaryGameState.SUB_MENU)) {

            if ((gp.getSubMenuH().getIndexSelected() < (gp.getSubMenuH().getOptions().size() - 1))                      // Minus one is to account for 'Back' option.
                    && (gp.getCombatM().getLastGeneratedTargetOptions().get(gp.getSubMenuH().getIndexSelected())
                    == entityId)) {

                bannerColor = BannerColor.TARGET;
            }
        } else if ((gp.getCombatM().getLatestSubMenuType() == SubMenuType.TARGET_CONFIRM)
                && (gp.getPrimaryGameState() == PrimaryGameState.SUB_MENU)) {

            if (gp.getCombatM().getLastGeneratedTargetOptions().contains(entityId)) {

                bannerColor = BannerColor.TARGET;
            }
        }

        if (includeSkill) {

            gp.getLifeSkillBannerBackground().addToRenderPipeline(
                    renderer, bannerScreenCoords.x, bannerScreenCoords.y, bannerColor);
        }else {

            gp.getLifeBannerBackground().addToRenderPipeline(
                    renderer, bannerScreenCoords.x, bannerScreenCoords.y, bannerColor);
        }
        addLifeBarToRenderPipeline(
                entity.getLife(),
                entity.getMaxLife(),
                gp.getCamera().worldWidthToScreenWidth(30.0f),
                lifeBarScreenCoords.x,
                lifeBarScreenCoords.y);

        Vector2f lifeLabelWorldCoords = new Vector2f(bannerWorldX + 1.5f, bannerWorldY);
        Vector2f lifeLabelScreenCoords = gp.getCamera().worldCoordsToScreenCoords(lifeLabelWorldCoords);
        addStringToRenderPipeline("HP", lifeLabelScreenCoords.x, lifeLabelScreenCoords.y, 0.1f,
                new Vector3f(255, 255, 255), standardBoldFont, ZIndex.SECOND_LAYER);
        // NOTE: If "HP" label is increased to scale 0.12f, then world coords needs to be adjusted by -1.0f.

        if (includeSkill) {

            Vector2f skillBarWorldCoords = new Vector2f(bannerWorldX + 16.0f, bannerWorldY + 12.0f);
            Vector2f skillBarScreenCoords = gp.getCamera().worldCoordsToScreenCoords(skillBarWorldCoords);

            addSkillBarToRenderPipeline(
                    entity.getSkill(),
                    entity.getMaxSkill(),
                    gp.getCamera().worldWidthToScreenWidth(30.0f),
                    skillBarScreenCoords.x,
                    skillBarScreenCoords.y);

            Vector2f skillLabelWorldCoords = new Vector2f(bannerWorldX + 1.5f, bannerWorldY + 10.0f);
            Vector2f skillLabelScreenCoords = gp.getCamera().worldCoordsToScreenCoords(skillLabelWorldCoords);
            addStringToRenderPipeline("SP", skillLabelScreenCoords.x, skillLabelScreenCoords.y, 0.1f,
                    new Vector3f(255, 255, 255), standardBoldFont, ZIndex.SECOND_LAYER);
        }

        if (gp.getCombatM().getGuardingEntities().contains(entityId)) {

            Vector2f shieldWorldCoords = new Vector2f(bannerWorldX + 46.0f, bannerWorldY);
            Vector2f shieldScreenCoords = gp.getCamera().worldCoordsToScreenCoords(shieldWorldCoords);
            gp.getGuardingShield().addToRenderPipeline(renderer, shieldScreenCoords.x, shieldScreenCoords.y);
        }
    }


    /**
     * Adds combat sub-menu option descriptions to the render pipeline.
     */
    private void addCombatSubMenuDescriptionToRenderPipeline() {

        if (!gp.getCombatM().getLatestSubMenuDescriptionByIndex(gp.getSubMenuH().getIndexSelected()).equals("")) {

            // Prepare window dimensions.
            float windowScreenLeftRightPadding = gp.getCamera().worldHeightToScreenHeight(7.0f);                        // Normalized (screen) padding on left and right of description window between window and text.
            float windowScreenTopBottomPadding = gp.getCamera().worldHeightToScreenHeight(8.6f);                        // Normalized (screen) padding on top and bottom of description window between window and text.
            float textScreenSpacing = gp.getCamera().worldHeightToScreenHeight(9.5f);                                   // Normalized (screen) vertical spacing between line of description text (does NOT include character height).
            float textCharacterWorldHeight =
                    renderer.getFont(standardNormalFont).getCharacter('A').getHeight() * standardFontScale;             // It doesn't matter which character is used, since all characters in a font have the same height.
            float textCharacterScreenHeight =
                    gp.getCamera().worldHeightToScreenHeight(textCharacterWorldHeight);                                 // Normalized (screen) character height.
            float windowWorldWidth = 179.0f;                                                                            // World (absolute) total width of the description window.
            float windowScreenWidth = gp.getCamera().worldWidthToScreenWidth(windowWorldWidth);
            float windowScreenHeight = (textScreenSpacing * 2)                                                          // Spacing between description text (assuming three lines of text).
                    + (2 * windowScreenTopBottomPadding)                                                                // Padding on top and bottom of description window.
                    + (textCharacterScreenHeight * 3);                                                                  // Character height for each option (assuming three lines of text)
            float windowWorldHeight = gp.getCamera().screenHeightToWorldHeight(windowScreenHeight);

            // Calculate window position.
            Vector2f windowScreenCoords = new Vector2f(
                    gp.getCamera().worldWidthToScreenWidth(23.0f),
                    1 - gp.getCamera().worldHeightToScreenHeight(86.4f + 13.0f) - windowScreenHeight);                  // The 86.4f in the y-component is the height of the main dialogue window in the `renderDialogueScreen()` method.

            // Render description window.
            Vector2f windowWorldCoords = gp.getCamera().screenCoordsToWorldCoords(windowScreenCoords);
            renderer.addRectangle(
                    new Vector4f(20, 20, 20, windowOpacity),
                    new Transform(windowWorldCoords, new Vector2f(windowWorldWidth, windowWorldHeight)),
                    ZIndex.FIRST_LAYER);

            // Render text.
            Vector2f textScreenCoords = new Vector2f(
                    windowScreenCoords.x + windowScreenLeftRightPadding,
                    windowScreenCoords.y + windowScreenTopBottomPadding);
            addStringBlockToRenderPipeline(
                    gp.getCombatM().getLatestSubMenuDescriptionByIndex(gp.getSubMenuH().getIndexSelected()),
                    textScreenCoords.x,
                    textScreenCoords.y,
                    windowScreenWidth - (windowScreenLeftRightPadding * 2),
                    textScreenSpacing + textCharacterScreenHeight,
                    standardFontScale,
                    new Vector3f(255, 255, 255),
                    standardNormalFont,
                    ZIndex.FIRST_LAYER,
                    false);
        }
    }


    /**
     * Adds debug information user interface components to the render pipeline.
     */
    private void addDebugToRenderPipeline() {

        float screenX = gp.getCamera().worldWidthToScreenWidth(7.7f);
        float screenY = gp.getCamera().worldHeightToScreenHeight(7.7f);
        float spacingScreenY = gp.getCamera().worldHeightToScreenHeight(25.9f);
        Vector3f color = new Vector3f(255, 255, 255);

        // Memory usage by Java Runtime.
        Long totalMemoryBytes = Runtime.getRuntime().totalMemory();
        Long freeMemoryBytes = Runtime.getRuntime().freeMemory();
        Long usedMemoryMegabytes = (totalMemoryBytes - freeMemoryBytes) / 1000000;
        String memoryUsage = "JVM Memory Usage: " + usedMemoryMegabytes + " MB";
        addStringShadowToRenderPipeline(memoryUsage, screenX, screenY, standardFontScale,
                color, standardNormalFont, ZIndex.FIRST_LAYER);

        // VSync.
        screenY += spacingScreenY;
        String vSync = "VSync: " + ((gp.getSystemSetting(0).getActiveOption() == 0) ? "Disabled" : "Enabled");
        addStringShadowToRenderPipeline(vSync, screenX, screenY, standardFontScale,
                color, standardNormalFont, ZIndex.FIRST_LAYER);

        // Frame rate.
        screenY += spacingScreenY;
        String fps = "FPS: " + fpsTracker;
        addStringShadowToRenderPipeline(fps, screenX, screenY, standardFontScale,
                color, standardNormalFont, ZIndex.FIRST_LAYER);

        // Player column.
        screenY += spacingScreenY;
        String col = "Player Col: " + gp.getEntityM().getPlayer().getCol();
        addStringShadowToRenderPipeline(col, screenX, screenY, standardFontScale,
                color, standardNormalFont, ZIndex.FIRST_LAYER);

        // Player row.
        screenY += spacingScreenY;
        String row = "Player Row: " + gp.getEntityM().getPlayer().getRow();
        addStringShadowToRenderPipeline(row, screenX, screenY, standardFontScale,
                color, standardNormalFont, ZIndex.FIRST_LAYER);

        // Camera center (x).
        screenY += spacingScreenY;
        String centerX = "Camera Center X: "
                + (gp.getCamera().getPositionMatrix().x + ((float)gp.getCamera().getScreenWidth() / 2));
        addStringShadowToRenderPipeline(centerX, screenX, screenY, standardFontScale,
                color, standardNormalFont, ZIndex.FIRST_LAYER);

        // Camera center (y).
        screenY += spacingScreenY;
        String centerY = "Camera Center Y: "
                + (gp.getCamera().getPositionMatrix().y + ((float)gp.getCamera().getScreenHeight() / 2));
        addStringShadowToRenderPipeline(centerY, screenX, screenY, standardFontScale,
                color, standardNormalFont, ZIndex.FIRST_LAYER);

        // Primary game state.
        screenY += spacingScreenY;
        String state = "Primary Game State: "
                + (gp.getPrimaryGameState().toString());
        addStringShadowToRenderPipeline(state, screenX, screenY, standardFontScale,
                color, standardNormalFont, ZIndex.FIRST_LAYER);
    }


    /**
     * Performs necessary preparations when switching to a new primary menu state.
     *
     * @param outgoingPrimaryMenuState primary menu state being switched from
     * @param incomingPrimaryMenuState primary menu state being switched to
     */
    private void preparePrimaryMenuState(PrimaryMenuState outgoingPrimaryMenuState,
                                         PrimaryMenuState incomingPrimaryMenuState) {

        // Tidy up the state being switching from.
        switch (outgoingPrimaryMenuState) {

            case PARTY:
                gp.getGuiIconM().getIconById(0).setSelected(false);                                                     // Deselect the party menu icon.
                gp.getUiPartyMenuS().setPartyMenuSlotSelected(PartyMenuSlot.SLOT_0);                                    // Set the selected party member stat icon back to its default.
                gp.getUiPartyMenuS().setPartyMenuScrollLevel(0);                                                        // Set the list of party members back to its default scroll level.
                gp.getEntityIconM().purgeAllEntityIcons();
                break;

            case INVENTORY:
                gp.getGuiIconM().getIconById(1).setSelected(false);                                                     // Deselected the inventory menu icon.
                gp.getUiInventoryMenuS().setItemColSelected(0);                                                         // Set the item slot back to its default column.
                gp.getUiInventoryMenuS().setItemRowSelected(0);                                                         // Set the item slot back to its default row.
                break;

            case SETTINGS:
                gp.getGuiIconM().getIconById(2).setSelected(false);                                                     // Deselect the settings menu icon.
                gp.getUiSettingsMenuS().setSystemSettingSelected(0);                                                    // Reset selected setting to default.
                gp.getUiSettingsMenuS().setSystemOptionSelected(gp.getSystemSetting(0).getActiveOption());              // Reset selected option to default (i.e., active option of the default setting).
                break;
        }

        // Prepare for the state being switching to.
        switch (incomingPrimaryMenuState) {

            case PARTY:
                gp.getGuiIconM().getIconById(0).setSelected(true);                                                      // Select the party menu icon.
                gp.getEntityIconM().createPartyEntityIcons();                                                           // Create entity icons for the party members.
                gp.getEntityIconM().getEntityIconById(gp.getEntityM().getPlayer().getEntityId()).setSelected(true);     // Set the player icon as being selected (will animate the player icon).
                gp.getGuiIconM().getIconById(3).setSelected(true);                                                      // Set the background icon for the player as being selected (will darken the background).
                gp.getUiPartyMenuS().setPartyMenuSlotSelected(PartyMenuSlot.SLOT_0);                                    // Set the player entity's party member stat icon as being selected in the UI.
                gp.getUiPartyMenuS().setPartyMenuScrollLevel(0);                                                        // Set the list of party members back to the top.
                gp.getUiPartyMenuS().markDirty();
                break;

            case INVENTORY:
                gp.getGuiIconM().getIconById(1).setSelected(true);                                                      // Select the inventory menu icon.
                gp.getUiInventoryMenuS().setItemColSelected(0);                                                         // Set the top-left item icon as being selected.
                gp.getUiInventoryMenuS().setItemRowSelected(0);                                                         // ^^^
                break;

            case SETTINGS:
                gp.getGuiIconM().getIconById(2).setSelected(true);                                                      // Select the settings menu icon.
                gp.getUiSettingsMenuS().setSystemSettingSelected(0);
                gp.getUiSettingsMenuS().setSystemOptionSelected(gp.getSystemSetting(0).getActiveOption());
                gp.getUiSettingsMenuS().markDirty();
                break;
        }
        gp.getUiPrimaryMenuFrameS().markDirty();
    }


    // GETTERS
    public PrimaryMenuState getPrimaryMenuState() {
        return primaryMenuState;
    }

    public String getStandardNormalFont() {
        return standardNormalFont;
    }

    public String getStandardBoldFont() {
        return standardBoldFont;
    }

    public float getStandardFontScale() {
        return standardFontScale;
    }

    public float getWindowOpacity() {
        return windowOpacity;
    }


    // SETTERS
    public void setPrimaryMenuState(PrimaryMenuState primaryMenuState) {
        preparePrimaryMenuState(this.primaryMenuState, primaryMenuState);
        this.primaryMenuState = primaryMenuState;
    }

    public void setWindowOpacity(float windowOpacity) {
        this.windowOpacity = windowOpacity;
        if (primaryMenuState != PrimaryMenuState.INACTIVE) {
            gp.getUiPrimaryMenuFrameS().markDirty();
        }
        if (gp.getDialogueR().getActiveConv() != null) {
            gp.getUiDialogueS().markDirty();
        }
        if (gp.getSubMenuH().getSubMenuId() != -1) {
            gp.getUiSubMenuS().markDirty();
        }
    }

    public void resetWindowOpacity() {
        this.windowOpacity = defaultWindowOpacity;
    }
}
