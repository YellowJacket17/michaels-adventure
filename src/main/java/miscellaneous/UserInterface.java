package miscellaneous;

import core.GamePanel;
import entity.EntityBase;
import entity.enumeration.EntityStatus;
import event.enumeration.FadeState;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import render.Renderer;
import render.enumeration.ZIndex;
import render.drawable.Transform;

import java.util.Set;

/**
 * This class handles the drawing of all on-screen user interface (UI) elements.
 */
public class UserInterface {

    // FIELDS
    private final GamePanel gp;
    private Renderer renderer;

    /**
     * Variable to store current selected party member in the party menu.
     * The default value is zero (first slot).
     */
    private int partySlotSelected;

    /**
     * Top and bottom screen padding of the main window of the in-game menu.
     */
    private final float mainWindowScreenTopBottomPadding = 0.06f;

    /**
     * Left and right screen padding of the main window of the in-game menu.
     */
    private final float mainWindowScreenLeftRightPadding = 0.06f;

    /**
     * Sets the maximum number of item slot rows that can be displayed at once in the inventory menu.
     */
    private final int maxNumItemRow = 5;

    /**
     * Sets the maximum number of item slot columns (i.e., number of item slots in a row) that can be displayed at once.
     */
    private final int maxNumItemCol = 5;

    /**
     * Variable to store the current selected row of items in the inventory menu.
     * The default value is zero (leftmost row).
     */
    private int itemRowSelected;

    /**
     * Variable to store the current selected column of items in the inventory menu.
     * The default value is zero (topmost column).
     */
    private int itemColSelected;

    /**
     * Variable to store the index that the current selected item occupies in the `inventory` array of the Player class.
     * The default value is zero (first item).
     */
    private int inventoryIndexSelected;

    /**
     * Array tracking which item slots in the player's inventory (i.e., `inventory` array of the Player class) are
     * occupied (true) or not (false).
     */
    private boolean[][] occupiedItemSlots;

    /**
     * Variable to store the current selected system setting in the settings menu.
     */
    private int systemSettingSelected;

    /**
     * Variable to store the current selected option of the current selected system setting in the settings menu.
     */
    private int systemOptionSelected;

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
     * Constructs a Ui instance.
     *
     * @param gp GamePanel instance
     */
    public UserInterface(GamePanel gp) {
        this.gp = gp;
        occupiedItemSlots = new boolean[maxNumItemRow][maxNumItemCol];
    }


    // METHODS
    /**
     * Adds all UI elements to the render pipeline.
     *
     * @param renderer Renderer instance
     * @param dt time since last frame (seconds)
     */
    public void addToRenderPipeline(Renderer renderer, double dt) {

        fpsCounter -= dt;
        if (fpsCounter <= 0) {
            fpsTracker = (int)(1.0 / dt);
            fpsCounter += 1;                                                                                            // FPS displayed in debug mode will be updated every one second.
        }

        if (this.renderer != renderer) {
            this.renderer = renderer;                                                                                   // Makes it easier to access current renderer across entire class.
        }

        switch (gp.getPrimaryGameState()) {
            case EXPLORE:
                // Nothing here.
                break;
            case DIALOGUE:
                renderDialogueScreen();
                break;
            case PARTY_MENU:
                renderInGameMenuMainWindowScreen();
                renderPartyMenuScreen();
                break;
            case INVENTORY_MENU:
                renderInGameMenuMainWindowScreen();
                renderInventoryMenuScreen();
                break;
            case SETTINGS_MENU:
                renderInGameMenuMainWindowScreen();
                renderSettingsMenuScreen();
                break;
            case SUB_MENU:
                renderSubMenuScreen();
                if (gp.getDialogueR().getActiveConv() != null) {                                                        // Keep dialogue screen up if the sub-menu is being drawn while dialogue is also being displayed.
                    renderDialogueScreen();
                }
        }

        // FADE
        if ((gp.getFadeS().getState() != FadeState.INACTIVE) || (gp.getFadeS().isFlashActive())) {
            renderFadeScreen();
        }

        // COMBAT
        if (gp.getCombatM().isCombatActive()) {
            renderCombatScreen();
        }

        // DEBUG.
        if (gp.isDebugActive()) {
            renderDebug();
        }
    }


    /**
     * Adds dialogue components (window and text) to the render pipeline.
     */
    private void renderDialogueScreen() {

        // Font preparations.
        // The following information is assuming use of the font Arimo (normal/non-bold).
        float fontScale = 0.15f;                                                                                        // Font size (multiplies native height).
        float characterWorldHeight = renderer.getFont("Arimo").getCharacter('A').getHeight() * fontScale;               // It doesn't matter which character is used, since all characters in a font have the same height.
        float characterScreenHeight = gp.getCamera().worldHeightToScreenHeight(characterWorldHeight);                   // Normalized (screen) character height.

        // Set position and dimensions of main dialogue window.
        float mainWindowScreenWidth = 1f;                                                                               // Main dialogue window will span the entre width of the screen.
        float mainWindowScreenHeight = 0.2f;
        Vector2f mainWindowScreenCoords = new Vector2f(0f, 1 - mainWindowScreenHeight);

        // Render main dialogue window.
        Vector2f mainWindowWorldCoords = gp.getCamera().screenCoordsToWorldCoords(mainWindowScreenCoords);
        float mainWindowWorldWidth = gp.getCamera().screenWidthToWorldWidth(mainWindowScreenWidth);
        float mainWindowWorldHeight = gp.getCamera().screenHeightToWorldHeight(mainWindowScreenHeight);
        renderer.addRectangle(
                new Vector4f(0, 0, 0, 180),
                new Transform(mainWindowWorldCoords, new Vector2f(mainWindowWorldWidth, mainWindowWorldHeight)),
                ZIndex.SECOND_LAYER);

        // Dialogue sub-window and text, if applicable (i.e., area where speaker's name is printed).
        if (gp.getDialogueR().getActiveDialogueEntityName() != null
                && !gp.getDialogueR().getActiveDialogueEntityName().equals("")) {

            // Set position and dimensions of dialogue sub-window.
            float subWindowScreenX = 0.03f;
            float subWindowScreenTopBottomPadding = 0.02f;
            float subWindowScreenLeftRightPadding = 0.02f;
            String entityName = gp.getDialogueR().getActiveDialogueEntityName();
            float entityNameWidth = 0;
            for (int i = 0; i < entityName.length(); i++) {
                char optionCharacter = entityName.charAt(i);
                entityNameWidth += renderer.getFont("Arimo").getCharacter(optionCharacter).getWidth() * fontScale;
            }
            float subWindowScreenWidth = gp.getCamera().worldWidthToScreenWidth(entityNameWidth)
                    + (2 * subWindowScreenLeftRightPadding);
            float subWindowScreenHeight = characterScreenHeight + (2 * subWindowScreenTopBottomPadding);
            Vector2f subWindowScreenCoords = new Vector2f(subWindowScreenX,
                    mainWindowScreenCoords.y - subWindowScreenHeight);

            // Render dialogue sub-window.
            Vector2f subWindowWorldCoords = gp.getCamera().screenCoordsToWorldCoords(subWindowScreenCoords);
            float subWindowWorldWidth = gp.getCamera().screenWidthToWorldWidth(subWindowScreenWidth);
            float subWindowWorldHeight = gp.getCamera().screenHeightToWorldHeight(subWindowScreenHeight);
            renderer.addRectangle(
                    new Vector4f(0, 0, 0, 180),
                    new Transform(subWindowWorldCoords, new Vector2f(subWindowWorldWidth, subWindowWorldHeight)),
                    ZIndex.SECOND_LAYER);

            // Set position of dialogue entity name.
            Vector2f subTextScreenCoords = new Vector2f(subWindowScreenX + subWindowScreenLeftRightPadding,
                    subWindowScreenCoords.y + subWindowScreenTopBottomPadding);

            // Render dialogue entity name.
            renderString(gp.getDialogueR().getActiveDialogueEntityName(), subTextScreenCoords, fontScale,
                    new Vector3f(121, 149, 255), "Arimo");
        }

        // Dialogue progress arrow, if applicable.
        if ((gp.getDialogueR().isDialoguePaused())
                || (!gp.getDialogueR().isReadingDialogue() && (gp.getDialogueR().isAlwaysShowArrow()))) {
            Vector2f arrowScreenCoords = new Vector2f(mainWindowScreenCoords.x + mainWindowScreenWidth - 0.02f,
                    mainWindowScreenCoords.y + mainWindowScreenHeight - 0.03f);
            gp.getDialogueA().addToRenderPipeline(renderer, arrowScreenCoords.x, arrowScreenCoords.y);
        }

        // Set position of main dialogue text (lines 1 and 2) and render it.
        float mainTextScreenLeftPadding = 0.03f;
        float mainTextScreenSpacing = (mainWindowScreenHeight - (2 * characterScreenHeight)) / 3;
        Vector2f mainTextScreenCoords = new Vector2f(mainTextScreenLeftPadding, mainWindowScreenCoords.y + mainTextScreenSpacing);
        renderString(gp.getDialogueR().getDialoguePrint1(), mainTextScreenCoords, fontScale,
                new Vector3f(255, 255, 255), "Arimo");
        mainTextScreenCoords.y += characterScreenHeight + mainTextScreenSpacing;
        renderString(gp.getDialogueR().getDialoguePrint2(), mainTextScreenCoords, fontScale,
                new Vector3f(255, 255, 255), "Arimo");
    }


    /**
     * Adds components of the main window for the in-game menu to the render pipeline.
     * Specifically, the window itself, header section icons, header section title, and header divider are added.
     * These are elements common to all sections of the in-game menu.
     */
    private void renderInGameMenuMainWindowScreen() {

        // Prepare main window position and dimensions.
        Vector2f mainWindowScreenCoords = new Vector2f(
                mainWindowScreenLeftRightPadding,
                mainWindowScreenTopBottomPadding);
        float mainWindowScreenWidth = 1 - (2 * mainWindowScreenLeftRightPadding);
        float mainWindowScreenHeight = 1 - (2 * mainWindowScreenTopBottomPadding);

        // Render main window.
        Vector2f mainWindowWorldCoords = gp.getCamera().screenCoordsToWorldCoords(mainWindowScreenCoords);
        float mainWindowWorldWidth = gp.getCamera().screenWidthToWorldWidth(mainWindowScreenWidth);
        float mainWindowWorldHeight = gp.getCamera().screenHeightToWorldHeight(mainWindowScreenHeight);
        renderer.addRoundRectangle(
                new Vector4f(0, 0, 0, 220),
                new Transform(mainWindowWorldCoords, new Vector2f(mainWindowWorldWidth, mainWindowWorldHeight)),
                ZIndex.SECOND_LAYER,
                (int)mainWindowWorldHeight / 16);

        // Prepare header section icon (party, inventory, and settings) positions and render.
        float menuIconScreenX = 1 - mainWindowScreenLeftRightPadding - 0.1f;
        float menuIconScreenY = mainWindowScreenTopBottomPadding + 0.03f;
        gp.getGuiIconM().addToRenderPipeline(renderer, 2, menuIconScreenX, menuIconScreenY);                            // Settings menu icon.
        menuIconScreenX -= 0.05f;
        gp.getGuiIconM().addToRenderPipeline(renderer, 1, menuIconScreenX, menuIconScreenY);                            // Inventory menu icon.
        menuIconScreenX -= 0.05f;
        gp.getGuiIconM().addToRenderPipeline(renderer, 0, menuIconScreenX, menuIconScreenY);                            // Party menu icon.

        // Prepare header divider (horizontal line beneath header) position and dimensions and render.
        float dividerScreenThickness = 0.004f;                                                                          // Normalized (screen) thickness of horizontal line.
        float dividerScreenLeftRightGap = 0.055f;                                                                       // Normalized (screen) space on either side of horizontal line between main window edge.
        float dividerScreenWidth = 1 - (2 * mainWindowScreenLeftRightPadding) - (2 * dividerScreenLeftRightGap);
        float dividerWorldThickness = gp.getCamera().screenHeightToWorldHeight(dividerScreenThickness);
        float dividerWorldWidth = gp.getCamera().screenWidthToWorldWidth(dividerScreenWidth);
        float menuIconWorldHeight = gp.getGuiIconM().getIconById(0).getNativeSpriteHeight();                            // Get native (world) height of menu icons; all are same height, so doesn't matter which is used here.
        float menuIconScreenHeight = gp.getCamera().worldHeightToScreenHeight(menuIconWorldHeight);
        Vector2f dividerScreenCoords = new Vector2f(
                mainWindowScreenLeftRightPadding + dividerScreenLeftRightGap,
                (menuIconScreenY + menuIconScreenHeight + (menuIconScreenY - mainWindowScreenTopBottomPadding)));
        Vector2f dividerWorldCoords = gp.getCamera().screenCoordsToWorldCoords(dividerScreenCoords);
        renderer.addRectangle(
                new Vector4f(255, 255, 255, 255),
                new Transform(dividerWorldCoords, new Vector2f(dividerWorldWidth, dividerWorldThickness)),
                ZIndex.FIRST_LAYER);

        // Prepare menu section title position and dimensions and render.
        float fontScale = 0.17f;                                                                                        // Font size (multiplies native height).
        float optionsCharacterWorldHeight = renderer.getFont("Arimo").getCharacter('A').getHeight() * fontScale;        // It doesn't matter which character is used, since all characters in a font have the same height.
        float optionsCharacterScreenHeight = gp.getCamera().worldHeightToScreenHeight(optionsCharacterWorldHeight);     // Normalized (screen) character height.
        float titleScreenTopBottomPadding = (dividerScreenCoords.y - mainWindowScreenTopBottomPadding
                - optionsCharacterScreenHeight) / 2;
        Vector2f titleScreenCoords = new Vector2f(
                mainWindowScreenLeftRightPadding + 0.065f,
                mainWindowScreenTopBottomPadding + titleScreenTopBottomPadding);
        String title = "???";
        switch (gp.getPrimaryGameState()) {
            case PARTY_MENU:
                title = "PARTY";
                break;
            case INVENTORY_MENU:
                title = "INVENTORY";
                break;
            case SETTINGS_MENU:
                title = "SETTINGS";
                break;
        }
        renderString(title, titleScreenCoords, fontScale, new Vector3f(121, 149, 255), "Arimo");
    }


    /**
     * Adds components of the party menu section of the in-game menu to the render pipeline.
     * This is to be rendered on top of the in-game menu main window.
     */
    private void renderPartyMenuScreen() {

        renderPartyMemberStatusIcons();
    }


    /**
     * Adds components of the status icons for each party member in the party menu section of the in-game menu to the
     * render pipeline.
     */
    private void renderPartyMemberStatusIcons() {

        // Prepare slot icon positions (i.e., background sprite of each stat icon).
        float slotIconWorldHeight = gp.getGuiIconM().getIconById(3).getNativeSpriteHeight();                            // Get native (world) height of slot icons; all are same height, so doesn't matter which is used here.
        float slotIconScreenHeight = gp.getCamera().worldHeightToScreenHeight(slotIconWorldHeight);
        float bottomSlotIconScreenY = 1 - mainWindowScreenTopBottomPadding - 0.081f - slotIconScreenHeight;             // Normalized (screen) y-position of the bottommost slot.
        float slotIconScreenX = mainWindowScreenLeftRightPadding + 0.055f;
        float slotIconScreenY = bottomSlotIconScreenY;
        float slotIconVerticalSpacing = 0.1f;                                                                           // Normalized (screen) spacing between each slot (does not include height of slot icon itself).

        // Render slot icon 2 (bottommost).
        gp.getGuiIconM().addToRenderPipeline(renderer, 5, slotIconScreenX, slotIconScreenY);

        // Render slot icon 1.
        slotIconScreenY -= slotIconVerticalSpacing + slotIconScreenHeight;
        gp.getGuiIconM().addToRenderPipeline(renderer, 4, slotIconScreenX, slotIconScreenY);

        // Render slot icon 0 (topmost, player entity).
        slotIconScreenY -= slotIconVerticalSpacing + slotIconScreenHeight;
        gp.getGuiIconM().addToRenderPipeline(renderer, 3, slotIconScreenX, slotIconScreenY);

        // Extract keys from party map.
        Set<Integer> keySet = gp.getEntityM().getParty().keySet();
        Integer[] keyArray = keySet.toArray(new Integer[keySet.size()]);

        // Prepare entity icon and status information text positions to render on top of respective slot icons.
        float entityIconScreenX = slotIconScreenX + 0.01f;
        float entityIconScreenY = bottomSlotIconScreenY - 0.009f;
        float statusInfoTextScreenX = slotIconScreenX + 0.07f;
        float statusInfoTextScreenY = bottomSlotIconScreenY + 0.014f;

        // Render entity icon 2 (bottommost).
        if ((gp.getEntityM().getParty().size() > 1) && (gp.getEntityM().getParty().get(keyArray[1]) != null)) {
            gp.getEntityIconM().addToRenderPipeline(renderer, gp.getEntityM().getParty().get(keyArray[1]).getEntityId(),
                    entityIconScreenX, entityIconScreenY);
            renderPartyMemberStatusInformation(gp.getEntityM().getParty().get(keyArray[1]),
                    statusInfoTextScreenX, statusInfoTextScreenY);
        }

        // Render entity icon 1.
        entityIconScreenY -= slotIconVerticalSpacing + slotIconScreenHeight;
        statusInfoTextScreenY -= slotIconVerticalSpacing + slotIconScreenHeight;
        if ((gp.getEntityM().getParty().size() > 0) && (gp.getEntityM().getParty().get(keyArray[0]) != null)) {
            gp.getEntityIconM().addToRenderPipeline(renderer, gp.getEntityM().getParty().get(keyArray[0]).getEntityId(),
                    entityIconScreenX, entityIconScreenY);
            renderPartyMemberStatusInformation(gp.getEntityM().getParty().get(keyArray[0]),
                    statusInfoTextScreenX, statusInfoTextScreenY);
        }

        // Render entity icon 0 (topmost, player entity).
        entityIconScreenY -= slotIconVerticalSpacing + slotIconScreenHeight;
        statusInfoTextScreenY -= slotIconVerticalSpacing + slotIconScreenHeight;
        gp.getEntityIconM().addToRenderPipeline(renderer, gp.getEntityM().getPlayer().getEntityId(),
                entityIconScreenX, entityIconScreenY);
        renderPartyMemberStatusInformation(gp.getEntityM().getPlayer(), statusInfoTextScreenX, statusInfoTextScreenY);
    }


    /**
     * Adds components of the status information to the status icon of a party member in the party menu section of the
     * in-game menu to the render pipeline.
     * Specifically, the party member's name, level, and health bar are added.
     * This is to be rendered on top of a slot icon.
     *
     * @param entity party member whose status information to render
     * @param textScreenX screen x-coordinate of all lines of text to render within the status information
     * @param topTextScreenY screen y-coordinate of the topmost line of text to render within the status information
     */
    private void renderPartyMemberStatusInformation(EntityBase entity, float textScreenX, float topTextScreenY) {

        // Prepare text.
        String name = entity.getName();
        String level = "Lv." + entity.getLevel();
        String lifeLabel = "HP";
        String lifeValue = entity.getLife() + "/" + entity.getMaxLife();
        Vector2f textScreenCoords = new Vector2f(textScreenX, topTextScreenY);

        // Render text for name, level, and life label.
        renderStringShadow(name, textScreenCoords, 0.11f, new Vector3f(255, 255, 255), "Arimo Bold");
        textScreenCoords.y += 0.04f;
        renderStringShadow(level, textScreenCoords, 0.11f, new Vector3f(255, 255, 255), "Arimo Bold");
        textScreenCoords.y += 0.04f;
        renderStringShadow(lifeLabel, textScreenCoords, 0.11f, new Vector3f(255, 255, 255), "Arimo Bold");

        // Render life bar.
        float barScreenX = textScreenCoords.x + 0.03f;
        float barScreenY = textScreenCoords.y + 0.002f;
        float barScreenWidth = 0.055f;                                                                                  // The maximum normalized (screen) width of the life bar interior (corresponds with maximum life).
        float barScreenHeight = 0.023f;                                                                                 // The normalized (screen) thickness of the life bar.
        Vector4f barBorderColor = new Vector4f(255, 255, 255, 255);
        renderLifeBar(entity.getLife(), entity.getMaxLife(), barScreenWidth, barScreenHeight,
                barScreenX, barScreenY, barBorderColor);

        // Draw remaining life points text with a shadowed effect.
        textScreenCoords.x += 0.074f;
        textScreenCoords.y -= 0.008f;
        renderStringShadow(lifeValue, textScreenCoords, 0.08f, new Vector3f(255, 255, 255), "Arimo Bold");
    }


    /**
     * Adds a life bar to the render pipeline.
     *
     * @param life number of remaining life points
     * @param maxLife maximum number of life points
     * @param screenWidth normalized (screen) width of the life bar
     * @param screenHeight normalized (screen) height of the life bar
     * @param screenX screen x-coordinate of the life bar (leftmost)
     * @param screenY screen y-coordinate of the life bar (topmost)
     * @param borderColor bar border color (r, g, b, a)
     */
    private void renderLifeBar(int life, int maxLife, float screenWidth, float screenHeight,
                               float screenX, float screenY, Vector4f borderColor) {

        // Prepare life bar.
        float borderScreenThickness = screenWidth * 0.04f;                                                              // Normalized (screen) thickness of border surrounding life bar interior.
        float remainingLifePercentage = (float)life/ (float)maxLife;                                                    // Decimal percentage of life that the entity has relative to its maximum life.
        if (remainingLifePercentage > 1) {
            remainingLifePercentage = 1;                                                                                // Prevent the life bar from over-filling.
        } else if (remainingLifePercentage < 0) {
            remainingLifePercentage = 0;                                                                                // Prevent the life bar from using a negative value.
        }

        // Render life bar background/border.
        Vector4f color = borderColor;
        Vector2f screenCoords = new Vector2f(screenX, screenY);
        Vector2f worldCoords = gp.getCamera().screenCoordsToWorldCoords(screenCoords);
        float worldWidth = gp.getCamera().screenWidthToWorldWidth(screenWidth);
        float worldHeight = gp.getCamera().screenHeightToWorldHeight(screenHeight);
        renderer.addRectangle(
                color,
                new Transform(
                        worldCoords,
                        new Vector2f(worldWidth, worldHeight)
                ),
                ZIndex.FIRST_LAYER
        );

        // Render empty life bar interior.
        color = new Vector4f(0, 0, 0, 255);
        screenCoords = new Vector2f(screenX + borderScreenThickness, screenY + borderScreenThickness);
        worldCoords = gp.getCamera().screenCoordsToWorldCoords(screenCoords);
        worldWidth = gp.getCamera().screenWidthToWorldWidth(screenWidth - (borderScreenThickness * 2));
        worldHeight = gp.getCamera().screenHeightToWorldHeight(screenHeight - (borderScreenThickness * 2));
        renderer.addRectangle(
                color,
                new Transform(
                        worldCoords,
                        new Vector2f(worldWidth, worldHeight)
                ),
                ZIndex.FIRST_LAYER
        );

        // Render filled life bar interior.
        worldWidth = worldWidth * remainingLifePercentage;
        if (remainingLifePercentage <= 0.125f) {                                                                        // Eighth of life or less remaining.
            color = new Vector4f(255, 46, 102, 220);
        } else if (remainingLifePercentage <= 0.5f) {                                                                   // Half of life or less remaining.
            color = new Vector4f(255, 251, 78, 220);
        } else {                                                                                                        // Greater than half of life remaining.
            color = new Vector4f(46, 255, 139, 220);
        }
        renderer.addRectangle(
                color,
                new Transform(
                        worldCoords,
                        new Vector2f(worldWidth, worldHeight)
                ),
                ZIndex.FIRST_LAYER
        );
    }


    /**
     * Adds components of the inventory menu section of the in-game menu to the render pipeline.
     * This is to be rendered on top of the in-game menu main window.
     */
    private void renderInventoryMenuScreen() {

        renderItemIcons();
        renderSelectedItemDescription();
    }


    /**
     * Adds components of the icons for each item in the inventory menu section of the in-game menu to the render
     * pipeline.
     */
    private void renderItemIcons() {

        // Initializations.
        int itemBackdropWorldWidth = gp.getGuiIconM().getIconById(6).getNativeSpriteWidth();                            // Both stackable and non-stackable background icons have same width, so doesn't matter which is used here.
        int itemBackdropWorldHeight = gp.getGuiIconM().getIconById(6).getNativeSpriteHeight();                          // Both stackable and non-stackable background icons have same height, so doesn't matter which is used here.
        float itemBackdropScreenWidth = gp.getCamera().worldWidthToScreenWidth(itemBackdropWorldWidth);                 // Normalized (screen) width of the item/slot backdrop (both stackable and non-stackable are the same).
        float itemBackdropScreenHeight = gp.getCamera().worldHeightToScreenHeight(itemBackdropWorldHeight);             // Normalized (screen) height of the item/slot backdrop (both stackable and non-stackable are the same).
        float horizontalScreenSpacing = itemBackdropScreenWidth + 0.03f;                                                // Normalized (screen) horizontal spacing between each item slot/icon backdrop.
        float verticalScreenSpacing = itemBackdropScreenHeight + 0.05f;                                                 // Normalized (screen) vertical spacing between each item slot/icon backdrop.
        float leftmostItemBackdropScreenX = mainWindowScreenLeftRightPadding + 0.055f;                                  // Normalized (screen) x-position of the leftmost item slot/icon backdrop.
        float topmostItemBackdropScreenY = 1 - mainWindowScreenTopBottomPadding - 0.7f;                                 // Normalized (screen) y-position of the topmost item slot/icon backdrop.
        float itemBackdropScreenX;
        float itemBackdropScreenY;
        float itemIconScreenX;
        float itemIconScreenY;
        int itemIconWorldWidth;
        int itemIconWorldHeight;
        float itemIconScreenWidth;
        float itemIconScreenHeight;
        String quantity;
        Vector2f quantityScreenCoords = new Vector2f();
        int numItems = gp.getEntityM().getPlayer().getInventory().size();                                               // Number of items currently in the player's inventory.

        // Render each item slot/icon (backdrop, icon, amount, and selector).
        int row = 0;                                                                                                    // Variable to track which row is currently being rendered.
        int itemIndex = 0;                                                                                              // Variable to track which item slot/icon is currently being rendered when looping through the player's inventory.

        while ((row < maxNumItemRow) && (itemIndex < numItems)) {

            int col = 0;                                                                                                // Variable to track which column is currently being rendered.
            itemBackdropScreenY = topmostItemBackdropScreenY + (verticalScreenSpacing * row);

            while ((col < maxNumItemCol) && (itemIndex < numItems)) {

                itemIconWorldWidth = gp.getEntityM().getPlayer().getInventory().get(itemIndex).getNativeSpriteWidth();
                itemIconWorldHeight = gp.getEntityM().getPlayer().getInventory().get(itemIndex).getNativeSpriteHeight();
                itemIconScreenWidth = gp.getCamera().worldWidthToScreenWidth(itemIconWorldWidth);
                itemIconScreenHeight = gp.getCamera().worldHeightToScreenHeight(itemIconWorldHeight);

                itemBackdropScreenX = leftmostItemBackdropScreenX + (horizontalScreenSpacing * col);
                itemIconScreenX = itemBackdropScreenX + ((itemBackdropScreenWidth - itemIconScreenWidth) / 2);          // Normalized (screen) width of the item/slot icon.
                itemIconScreenY = itemBackdropScreenY + ((itemBackdropScreenHeight - itemIconScreenHeight) / 2);        // Normalized (screen) height of the item/slot icon.

                if (gp.getEntityM().getPlayer().getInventory().get(itemIndex).isStackable()) {

                    quantity = Integer.toString(gp.getEntityM().getPlayer().getInventory().get(itemIndex).getAmount());
                    quantityScreenCoords.x = itemBackdropScreenX + (itemBackdropScreenWidth * 0.9f);
                    quantityScreenCoords.y = itemBackdropScreenY + (itemBackdropScreenHeight * 0.9f);

                    gp.getGuiIconM().addToRenderPipeline(renderer, 6, itemBackdropScreenX, itemBackdropScreenY);
                    gp.getEntityM().getPlayer().getInventory().get(itemIndex)
                            .addToRenderPipeline(renderer, itemIconScreenX, itemIconScreenY);
                    renderStringShadow(
                            quantity, quantityScreenCoords, 0.12f, new Vector3f(255, 255, 255), "Arimo Bold");
                } else {

                    gp.getGuiIconM().addToRenderPipeline(renderer, 7, itemBackdropScreenX, itemBackdropScreenY);
                    gp.getEntityM().getPlayer().getInventory().get(itemIndex)
                            .addToRenderPipeline(renderer, itemIconScreenX, itemIconScreenY);
                }

                if ((itemRowSelected == row) && (itemColSelected == col)) {

                    int selectorWorldWidth = gp.getGuiIconM().getIconById(8).getNativeSpriteWidth();                    // Note that `getNativeSpriteWidth()` returns the width of either the active or inactive sprite, depending on whether the icon is selected or not.
                    int selectorWorldHeight = gp.getGuiIconM().getIconById(8).getNativeSpriteHeight();                  // Note that `getNativeSpriteHeight()` returns the height of either the active or inactive sprite, depending on whether the icon is selected or not.
                    float selectorScreenWidth = gp.getCamera().worldWidthToScreenWidth(selectorWorldWidth);
                    float selectorScreenHeight = gp.getCamera().worldHeightToScreenHeight(selectorWorldHeight);
                    float selectorScreenX = itemBackdropScreenX
                            - ((selectorScreenWidth - itemBackdropScreenWidth) / 2);
                    float selectorScreenY = itemBackdropScreenY
                            - ((selectorScreenHeight - itemBackdropScreenHeight) / 2);
                    gp.getGuiIconM().addToRenderPipeline(renderer, 8, selectorScreenX, selectorScreenY);
                }
                itemIndex++;
                col++;
            }
            row++;
        }
    }


    /**
     * Adds components of description block of currently selected item in the inventory menu section of the in-game menu
     * to the render pipeline.
     */
    private void renderSelectedItemDescription() {

        if (gp.getEntityM().getPlayer().getInventory().size() != 0) {

            // Initializations.
            Vector3f nameColor = new Vector3f(121, 149, 255);
            Vector3f quantityColor = new Vector3f(244, 154, 45);
            Vector3f descriptionColor = new Vector3f(255, 255, 255);
            float leftmostScreenX = mainWindowScreenLeftRightPadding
                    + ((1 - (2 * mainWindowScreenLeftRightPadding)) / 2) + 0.05f;
            float topmostScreenY = 1 - mainWindowScreenTopBottomPadding - 0.7f;
            Vector2f screenCoords = new Vector2f(leftmostScreenX, topmostScreenY);
            String name = gp.getEntityM().getPlayer().getInventory().get(inventoryIndexSelected).getName();
            String quantity = "Quantity: "
                    + gp.getEntityM().getPlayer().getInventory().get(inventoryIndexSelected).getAmount();
            String description = gp.getEntityM().getPlayer().getInventory().get(inventoryIndexSelected).getDescription();

            // Render name, quantity, and description.
            renderStringShadow(name, screenCoords, 0.15f, nameColor, "Arimo Bold");
            screenCoords.y += 0.090f;
            renderStringShadow(quantity, screenCoords, 0.15f, quantityColor, "Arimo Bold");
            screenCoords.y += 0.090f;
            renderStringBlock(description, screenCoords, 23, 0.065f, 0.15f, descriptionColor, "Arimo", true);
        }
    }


    /**
     * Adds components of the settings menu section of the in-game menu to the render pipeline.
     * This is to be rendered on top of the in-game menu main window.
     */
    private void renderSettingsMenuScreen() {

        // Initializations.
        float fontScale = 0.15f;                                                                                        // Font size (multiplies native height).
        float settingLabelLeftScreenPadding = mainWindowScreenLeftRightPadding + 0.055f;                                // Normalized (screen) padding between the leftmost edge of the screen and setting labels.
        float settingLabelTopmostScreenY = 1 - mainWindowScreenTopBottomPadding - 0.7f;                                 // Normalized (screen) y-position of topmost setting.
        float settingScreenSpacing = 0.082f;                                                                            // Normalized (screen) vertical spacing between each setting.
        float settingLabelValueScreenGap = 0.4f;                                                                        // Normalized (screen) horizontal gap between a setting's label and its active option.
        float scrollArrowWorldWidth = gp.getGuiIconM().getIconById(9).getNativeSpriteWidth();                           // Both the left and right scroll arrows have the same width, so either height can be retrieved.
        float scrollArrowScreenWidth = gp.getCamera().worldWidthToScreenWidth(scrollArrowWorldWidth);                   // Normalized (screen) width of scroll arrows.
        float scrollArrowWorldHeight = gp.getGuiIconM().getIconById(9).getNativeSpriteHeight();                         // Both the left and right scroll arrows have the same height, so either height can be retrieved.
        float scrollArrowScreenHeight = gp.getCamera().worldHeightToScreenHeight(scrollArrowWorldHeight);               // Normalized (screen) height of scroll arrows.
        Vector2f settingLabelScreenCoords = new Vector2f(
                settingLabelLeftScreenPadding,
                settingLabelTopmostScreenY);
        Vector2f settingValueScreenCoords = new Vector2f(
                settingLabelScreenCoords.x + settingLabelValueScreenGap,
                settingLabelScreenCoords.y);

        // First setting (VSync).
        renderSystemSetting(0, settingLabelScreenCoords, settingValueScreenCoords, fontScale,
                scrollArrowScreenWidth, scrollArrowScreenHeight);

        // Second setting (frame rate limit).
        settingLabelScreenCoords.y += settingScreenSpacing;
        settingValueScreenCoords.y += settingScreenSpacing;
        renderSystemSetting(1, settingLabelScreenCoords, settingValueScreenCoords, fontScale,
                scrollArrowScreenWidth, scrollArrowScreenHeight);

        // Third setting (tether game speed).
        settingLabelScreenCoords.y += settingScreenSpacing;
        settingValueScreenCoords.y += settingScreenSpacing;
        renderSystemSetting(2, settingLabelScreenCoords, settingValueScreenCoords, fontScale,
                scrollArrowScreenWidth, scrollArrowScreenHeight);

        // Fourth setting (full screen).
        settingLabelScreenCoords.y += settingScreenSpacing;
        settingValueScreenCoords.y += settingScreenSpacing;
        renderSystemSetting(3, settingLabelScreenCoords, settingValueScreenCoords, fontScale,
                scrollArrowScreenWidth, scrollArrowScreenHeight);
    }


    /**
     * Adds components of a system setting to the render pipeline.
     *
     * @param index system setting index
     * @param settingLabelScreenCoords normalized (screen) coordinates of system setting label
     * @param settingValueScreenCoords normalized (screen) coordinates of system setting value
     * @param fontScale font size (multiplies native height)
     * @param scrollArrowScreenWidth normalized (screen) width of left and right scroll arrows (both assumed to be same size)
     * @param scrollArrowScreenHeight normalized (screen) height of left and right scroll arrows (both assumed to be same size)
     */
    private void renderSystemSetting(int index, Vector2f settingLabelScreenCoords, Vector2f settingValueScreenCoords,
                                     float fontScale, float scrollArrowScreenWidth, float scrollArrowScreenHeight) {

        Setting setting = gp.getSystemSetting(index);
        if (systemSettingSelected == index) {
            renderString(setting.getLabel(), settingLabelScreenCoords, fontScale,
                    new Vector3f(244, 154, 45), "Arimo Bold");
        } else {
            renderString(setting.getLabel(), settingLabelScreenCoords, fontScale,
                    new Vector3f(255, 255, 255), "Arimo");
        }
        renderString(setting.getOption(setting.getActiveOption()), settingValueScreenCoords, fontScale,
                new Vector3f(255, 255, 255), "Arimo");
        if (systemSettingSelected == index) {
            float characterWorldHeight = renderer.getFont("Arimo").getCharacter('A').getHeight() * fontScale;
            float characterScreenHeight = gp.getCamera().worldHeightToScreenHeight(characterWorldHeight);
            float scrollArrowScreenY = settingValueScreenCoords.y
                    + ((characterScreenHeight - scrollArrowScreenHeight) / 2);
            float rightScrollArrowScreenX = settingValueScreenCoords.x - 0.02f - scrollArrowScreenWidth;
            float leftScrollArrowScreenX = rightScrollArrowScreenX - 0.015f - scrollArrowScreenWidth;
            gp.getGuiIconM().addToRenderPipeline(renderer, 9, leftScrollArrowScreenX, scrollArrowScreenY);
            gp.getGuiIconM().addToRenderPipeline(renderer, 10, rightScrollArrowScreenX, scrollArrowScreenY);
        }
    }


    /**
     * Adds fade screen components to the render pipeline (fade out to color, wait on color, fade in from color).
     */
    private void renderFadeScreen() {

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
                        ZIndex.SECOND_LAYER);
                break;
            case ACTIVE:                                                                                                // Wait on colored screen.
                renderer.addRectangle(
                        new Vector4f(
                                gp.getFadeS().getColor().x,
                                gp.getFadeS().getColor().y,
                                gp.getFadeS().getColor().z,
                                alpha),
                        new Transform(worldCoords, new Vector2f(worldWidth, worldHeight)),
                        ZIndex.SECOND_LAYER);
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
                        ZIndex.SECOND_LAYER);
                break;
        }
    }


    /**
     * Adds sub-menu components to the render pipeline.
     */
    private void renderSubMenuScreen() {

        // Prepare window dimensions (other than window width).
        // The following information is assuming use of the font Arimo (normal/non-bold).
        float fontScale = 0.15f;                                                                                        // Font size (multiplies native height).
        float optionsScreenTopBottomPadding = 0.02f;                                                                    // Normalized (screen) padding on top and bottom of sub-menu window.
        float optionsScreenSpacing = 0.022f;                                                                            // Normalized (screen) spacing between options text.
        float optionsCharacterWorldHeight = renderer.getFont("Arimo").getCharacter('A').getHeight() * fontScale;        // It doesn't matter which character is used, since all characters in a font have the same height.
        float optionsCharacterScreenHeight = gp.getCamera().worldHeightToScreenHeight(optionsCharacterWorldHeight);     // Normalized (screen) character height.
        float windowScreenHeight = (optionsScreenSpacing * (gp.getSubMenuH().getOptions().size() - 1))                  // Spacing between options text.
                + (2 * optionsScreenTopBottomPadding)                                                                   // Padding on top and bottom of sub-menu window.
                + (optionsCharacterScreenHeight * gp.getSubMenuH().getOptions().size());                                // Character height for each option.

        // Prepare window width to width of widest option.
        // The following information is assuming use of the font Arimo (normal/non-bold).
        float optionsScreenLeftPadding = 0.03f;
        float optionsScreenRightPadding = 0.02f;
        float maxOptionWorldWidth = 0;
        for (int i = 0; i < gp.getSubMenuH().getOptions().size(); i++) {
            String option = gp.getSubMenuH().getOptions().get(i);
            float optionWorldWidth = 0;
            for (int j = 0; j < option.length(); j++) {
                char optionCharacter = option.charAt(j);
                optionWorldWidth += renderer.getFont("Arimo").getCharacter(optionCharacter).getWidth() * fontScale;
            }
            if (optionWorldWidth > maxOptionWorldWidth) {
                maxOptionWorldWidth = optionWorldWidth;
            }
        }
        float windowScreenWidth = gp.getCamera().worldWidthToScreenWidth(maxOptionWorldWidth)
                + optionsScreenLeftPadding + optionsScreenRightPadding;

        // Calculate window position.
        Vector2f windowScreenCoords;                                                                                    // Declare variable to store window screen coordinates (initialized immediately below).
        if (gp.getSubMenuH().isSubMenuDefaultPosition()) {                                                              // This is where the default position of the sub-menu window is defined.
            windowScreenCoords = new Vector2f(1 - 0.03f - windowScreenWidth, 1 - 0.2f - 0.03f - windowScreenHeight);    // The 0.2f in the y-component is the height of the main dialogue window in the `renderDialogueScreen()` method.
        } else {
            windowScreenCoords = new Vector2f(
                    gp.getSubMenuH().getSubMenuScreenX(), gp.getSubMenuH().getSubMenuScreenY());
        }

        // Render sub-menu window.
        Vector2f windowWorldCoords = gp.getCamera().screenCoordsToWorldCoords(windowScreenCoords);
        float windowWorldWidth = gp.getCamera().screenWidthToWorldWidth(windowScreenWidth);
        float windowWorldHeight = gp.getCamera().screenHeightToWorldHeight(windowScreenHeight);
        renderer.addRectangle(
                new Vector4f(0, 0, 0, 180),
                new Transform(windowWorldCoords, new Vector2f(windowWorldWidth, windowWorldHeight)),
                ZIndex.SECOND_LAYER);

        // Calculate position of text for first option.
        Vector2f optionsScreenCoords = new Vector2f(
                windowScreenCoords.x + optionsScreenLeftPadding,
                windowScreenCoords.y + optionsScreenTopBottomPadding);

        // Render text for each option and selection arrow next to selected option.
        Vector3f color;
        for (int i = 0; i < gp.getSubMenuH().getOptions().size(); i++) {
            if (gp.getSubMenuH().getColors().get(i) != null) {
                color = gp.getSubMenuH().getColors().get(i);
            } else {
                color = new Vector3f(255, 255, 255);
            }
            renderString(gp.getSubMenuH().getOptions().get(i), optionsScreenCoords, fontScale, color, "Arimo");
            if (i == gp.getSubMenuH().getIndexSelected()) {
                float selectionArrowScreenHeight = gp.getCamera().worldHeightToScreenHeight(
                        gp.getSelectionA().getNativeSpriteHeight());
                float selectionArrowScreenY = optionsScreenCoords.y + (optionsCharacterScreenHeight / 2)
                        - (selectionArrowScreenHeight / 2);
                gp.getSelectionA().addToRenderPipeline(renderer, optionsScreenCoords.x - 0.02f, selectionArrowScreenY);
            }
            optionsScreenCoords.y += optionsCharacterScreenHeight + optionsScreenSpacing;
        }
    }


    /**
     * Adds combat UI components to the render pipeline.
     */
    private void renderCombatScreen() {

        // Combat banners.
        if (gp.getCombatM().isCombatUiVisible()) {
            renderEntityCombatBanners();
        }

        // Target arrow.
        if (gp.getCombatM().isTargetArrowVisible()) {
            renderCombatTargetArrow();
        }
    }


    /**
     * Adds entity combat banners (i.e., rectangle that contains name, health bar, etc. during combat) for all combating
     * entities to the render pipeline.
     */
    private void renderEntityCombatBanners() {

        // Opposing entities.
        float bannerScreenY = 0;
        for (int entityId : gp.getCombatM().getNonPlayerSideEntities()) {
            EntityBase entity = gp.getEntityM().getEntityById(entityId);
            if (entity.getStatus() != EntityStatus.FAINT) {
                renderEntityCombatBanner(entity.getEntityId(), 1 - 0.15f, bannerScreenY);
            }
            bannerScreenY += 0.08f;
        }

        // Player entity.
        bannerScreenY = 0;
        if (gp.getEntityM().getPlayer().getStatus() != EntityStatus.FAINT) {
            renderEntityCombatBanner(gp.getEntityM().getPlayer().getEntityId(), 0, 0);
        }
        bannerScreenY += 0.08f;

        // Party members.
        int entityIndex = 0;
        for (EntityBase entity : gp.getEntityM().getParty().values()) {                                                 // Only render banners for active party members.
            if (entityIndex < gp.getEntityM().getNumActivePartyMembers()) {
                if (entity.getStatus() != EntityStatus.FAINT) {
                    renderEntityCombatBanner(entity.getEntityId(), 0, bannerScreenY);
                }
                bannerScreenY += 0.08f;
                entityIndex++;
            } else {
                break;
            }
        }
    }


    /**
     * Adds an entity combat banner (i.e., rectangle that contains name, health bar, etc. during combat) to the render
     * pipeline.
     *
     * @param entityId ID of entity whose banner to render
     * @param bannerScreenX screen x-coordinate of the banner (leftmost)
     * @param bannerScreenY screen y-coordinate of the banner (topmost)
     */
    private void renderEntityCombatBanner(int entityId, float bannerScreenX, float bannerScreenY) {

        Vector2f bannerScreenCoords = new Vector2f(bannerScreenX, bannerScreenY);
        Vector2f bannerWorldCoords = gp.getCamera().screenCoordsToWorldCoords(bannerScreenCoords);
        float bannerScreenWidth = 0.15f;
        float bannerScreenHeight = 0.07f;
        float bannerWorldWidth = gp.getCamera().screenWidthToWorldWidth(bannerScreenWidth);
        float bannerWorldHeight = gp.getCamera().screenHeightToWorldHeight(bannerScreenHeight);

        renderer.addRectangle(
                new Vector4f(255, 255, 255, 255),
                new Transform(
                        bannerWorldCoords,
                        new Vector2f(bannerWorldWidth, bannerWorldHeight)
                ),
                ZIndex.THIRD_LAYER
        );
        Vector2f nameScreenCoords = new Vector2f(bannerScreenX, bannerScreenY);
        renderString(gp.getEntityM().getEntityById(entityId).getName(),
                nameScreenCoords, 0.11f, new Vector3f(0, 0, 0), "Arimo Bold");

        float barScreenWidth = 0.11f;
        float barScreenHeight = 0.03f;
        float barScreenX = bannerScreenX + 0.03f;
        float barScreenY = bannerScreenY + bannerScreenHeight - barScreenHeight;
        renderLifeBar(gp.getEntityM().getEntityById(entityId).getLife(),
                gp.getEntityM().getEntityById(entityId).getMaxLife(), barScreenWidth, barScreenHeight,
                barScreenX, barScreenY, new Vector4f(0, 0, 0, 255));

        Vector2f lifeLabelScreenCoords = new Vector2f(bannerScreenX, barScreenY);
        renderString("HP", lifeLabelScreenCoords, 0.11f, new Vector3f(190, 97, 104), "Arimo Bold");
        renderString(gp.getEntityM().getEntityById(entityId).getName(),
                nameScreenCoords, 0.11f, new Vector3f(0, 0, 0), "Arimo Bold");
    }


    /**
     * Adds the combat target selection arrow to the render pipeline.
     */
    private void renderCombatTargetArrow() {

        int i = 0;

        for (int entityId : gp.getCombatM().getLastGeneratedTargetOptions()) {

            if (i == gp.getSubMenuH().getIndexSelected()) {                                                             // If combating entity that's currently being considered to target.

                float entityWorldX = gp.getEntityM().getEntityById(entityId).getWorldX();
                float entityWorldY = gp.getEntityM().getEntityById(entityId).getWorldY();
                float entitySpriteHeight = gp.getEntityM().getEntityById(entityId).getNativeSpriteHeight();
                float targetArrowWorldX = entityWorldX + (GamePanel.NATIVE_TILE_SIZE / 2)
                        - (gp.getTargetA().getNativeSpriteWidth() / 2);                                                 // Render arrow centered horizontally above target entity sprite.
                float targetArrowWorldY = entityWorldY + GamePanel.NATIVE_TILE_SIZE - entitySpriteHeight
                        - gp.getTargetA().getNativeSpriteHeight() - 4;                                                  // Render arrow slightly above target entity sprite.
                Vector2f targetArrowWorldCoords = new Vector2f(
                        targetArrowWorldX,
                        targetArrowWorldY);
                Vector2f targetArrowScreenCoords = gp.getCamera().worldCoordsToScreenCoords(targetArrowWorldCoords);
                gp.getTargetA().addToRenderPipeline(renderer, targetArrowScreenCoords.x, targetArrowScreenCoords.y);
                break;
            } else {

                i++;
            }
        }
    }


    /**
     * Adds debug information components to the render pipeline.
     */
    private void renderDebug() {

        float fontScale = 0.18f;
        float screenX = 0.01f;

        // Memory usage by Java Runtime.
        Vector2f screenCoords = new Vector2f(screenX, 0.01f);
        Long totalMemoryBytes = Runtime.getRuntime().totalMemory();
        Long freeMemoryBytes = Runtime.getRuntime().freeMemory();
        Long usedMemoryMegabytes = (totalMemoryBytes - freeMemoryBytes) / 1000000;
        String memoryUsage = "JVM Memory Usage: " + usedMemoryMegabytes + " MB";
        renderStringShadow(memoryUsage, screenCoords, fontScale, new Vector3f(255, 255, 255), "Arimo");

        // VSync.
        screenCoords = new Vector2f(screenX, 0.07f);
        String vSync = "VSync: " + ((gp.getSystemSetting(0).getActiveOption() == 0) ? "Disabled" : "Enabled");
        renderStringShadow(vSync, screenCoords, fontScale, new Vector3f(255, 255, 255), "Arimo");

        // Frame rate.
        screenCoords = new Vector2f(screenX, 0.13f);
        String fps = "FPS: " + fpsTracker;
        renderStringShadow(fps, screenCoords, fontScale, new Vector3f(255, 255, 255), "Arimo");

        // Player column.
        screenCoords = new Vector2f(screenX, 0.19f);
        String col = "Player Col: " + gp.getEntityM().getPlayer().getCol();
        renderStringShadow(col, screenCoords, fontScale, new Vector3f(255, 255, 255), "Arimo");

        // Player row.
        screenCoords = new Vector2f(screenX, 0.25f);
        String row = "Player Row: " + gp.getEntityM().getPlayer().getRow();
        renderStringShadow(row, screenCoords, fontScale, new Vector3f(255, 255, 255), "Arimo");

        // Camera center (x).
        screenCoords = new Vector2f(screenX, 0.31f);
        String centerX = "Camera Center X: "
                + (gp.getCamera().getPositionMatrix().x + ((float)gp.getCamera().getScreenWidth() / 2));
        renderStringShadow(centerX, screenCoords, fontScale, new Vector3f(255, 255, 255), "Arimo");

        // Camera center (y).
        screenCoords = new Vector2f(screenX, 0.37f);
        String centerY = "Camera Center Y: "
                + (gp.getCamera().getPositionMatrix().y + ((float)gp.getCamera().getScreenHeight() / 2));
        renderStringShadow(centerY, screenCoords, fontScale, new Vector3f(255, 255, 255), "Arimo");
    }


    /**
     * Adds a string of text to the render pipeline.
     *
     * @param text text to be drawn
     * @param screenCoords screen coordinates of the text (leftmost and topmost, normalized between 0 and 1)
     * @param size size at which to draw the text
     * @param color text color (r, g, b)
     * @param font name of font to use
     */
    private void renderString(String text, Vector2f screenCoords, float size, Vector3f color, String font) {

        if (text != null) {

            Vector2f worldCoords = gp.getCamera().screenCoordsToWorldCoords(screenCoords);
            renderer.addString(text, worldCoords.x, worldCoords.y, size, color, font);
        }
    }


    /**
     * Adds a string of text with a black drop shadow to the render pipeline.
     *
     * @param text text to be drawn
     * @param screenCoords screen coordinates of the text (leftmost and topmost, normalized between 0 and 1)
     * @param size size at which to draw the text
     * @param color text color (r, g, b)
     * @param font name of font to use
     */
    private void renderStringShadow(String text, Vector2f screenCoords, float size, Vector3f color, String font) {

        Vector2f shadowScreenCoords = new Vector2f(screenCoords.x + 0.0015f, screenCoords.y + 0.0015f);
        renderString(text, shadowScreenCoords, size, new Vector3f(0, 0, 0), font);
        renderString(text, screenCoords, size, color, font);
    }


    /**
     * Adds a block of text with a specified line character limit to the render pipeline.
     *
     * @param text complete text to be printed
     * @param screenCoords screen coordinates of the text block (lefmost and topmost, normalized between 0 and 1).
     * @param maxLineLength maximum number of characters allowed in a printed line of text
     * @param lineSpacing space between each printed line of text
     * @param size size at which to draw the text
     * @param color color of the printed text (r, g, b)
     * @param font name of font to use
     * @param dropShadow whether a drop shadow should be drawn (true) or not (false)
     */
    private void renderStringBlock(String text, Vector2f screenCoords, int maxLineLength, float lineSpacing,
                                   float size, Vector3f color, String font, boolean dropShadow) {

        String[] words = text.split(" ");                                                                               // An array of each word in the complete text, split by spaces.
        int wordsIndex = 0;                                                                                             // Track which index of the words array is currently being checked.

        while (wordsIndex < words.length) {                                                                             // Print each line of text.

            boolean limitExceeded = false;                                                                              // Track whether the maximum character length of a line has been exceeded (true) or not (false) yet.
            String line = "";                                                                                           // Initialize the line of text that's being built.

            while ((!limitExceeded) && (wordsIndex < words.length)) {                                                   // Add words to a line of text until either the maximum character length is exceeded OR there are no more words to print.

                String build;                                                                                           // Create a string that will be a candidate for the next line of text to be printed.

                if (line.equals("")) {

                    build = words[wordsIndex];
                } else {

                    build = line + " " + words[wordsIndex];
                }

                if (build.length() > maxLineLength) {

                    limitExceeded = true;                                                                               // Character length of the line has been exceeded.

                    if (words[wordsIndex].length() > maxLineLength) {

                        words[wordsIndex] = "???";                                                                      // If the number of characters in a single word exceeds the maximum number of characters that can be printed in a line of text, skip the word to avoid getting stuck in an infinite loop.
                    }
                } else {

                    line = build;                                                                                       // Set the next line of text to be rendered.
                    wordsIndex++;                                                                                       // Iterate to the next word.
                }
            }

            if (dropShadow) {

                renderStringShadow(line, screenCoords, size, color, font);                                              // Render the line of text with a drop shadow.
            } else {

                renderString(line, screenCoords, size, color, font);                                                    // Render the line of text without a drop shadow.
            }

            if (wordsIndex != words.length) {

                screenCoords.y += lineSpacing;                                                                          // Spacing between lines of text.
            }
        }
    }


    /**
     * Updates which party member is active in the party menu screen.
     */
    private void updateSelectedPartyMenuEntity() {

        Set<Integer> keySet = gp.getEntityM().getParty().keySet();
        Integer[] keyArray = keySet.toArray(new Integer[keySet.size()]);

        switch (partySlotSelected) {
            case 0:
                setSelectionStatusPartySlot0(true);                                                                     // Set slot 0 (player entity) as selected.
                setSelectionStatusPartySlot1(false);                                                                    // Set slot 1 as not selected.
                setSelectionStatusPartySlot2(false);                                                                    // Set slot 2 as not selected.
                break;
            case 1:
                if ((gp.getEntityM().getParty().size() > 0) && (gp.getEntityM().getParty().get(keyArray[0]) != null)) { // Check whether a party member actually occupies this slot or not.
                    setSelectionStatusPartySlot0(false);                                                                // Set slot 0 (player entity) as not selected.
                    setSelectionStatusPartySlot1(true);                                                                 // Set slot 1 as selected.
                    setSelectionStatusPartySlot2(false);                                                                // Set slot 2 as not selected.
                } else {
                    setPartySlotSelected(partySlotSelected - 1);                                                        // No party member exists in this slot, so move up to the slot above.
                }
                break;
            case 2:
                if ((gp.getEntityM().getParty().size() > 1) && (gp.getEntityM().getParty().get(keyArray[1]) != null)) { // Check whether a part member actually occupies this slot or not.
                    setSelectionStatusPartySlot0(false);                                                                // Set slot 0 (player entity) as not selected.
                    setSelectionStatusPartySlot1(false);                                                                // Set slot 1 as not selected.
                    setSelectionStatusPartySlot2(true);                                                                 // Set slot 2 as selected.
                } else {
                    setPartySlotSelected(partySlotSelected - 1);                                                        // No party member exists in this slot, so move up to the slot above.
                }
                break;
        }
    }


    /**
     * Sets whether slot 0 (player entity) in the party menu is selected or not.
     *
     * @param selected whether slot 0 is selected (true) or not (false)
     */
    private void setSelectionStatusPartySlot0(boolean selected) {

        gp.getEntityIconM().getEntityIconById(gp.getEntityM().getPlayer().getEntityId()).setSelected(selected);
        gp.getGuiIconM().getIconById(3).setSelected(selected);
    }


    /**
     * Sets whether slot 1 in the party menu is selected or not.
     *
     * @param selected whether slot 1 is selected (true) or not (false)
     */
    private void setSelectionStatusPartySlot1(boolean selected) {

        Set<Integer> keySet = gp.getEntityM().getParty().keySet();
        Integer[] keyArray = keySet.toArray(new Integer[keySet.size()]);

        if ((gp.getEntityM().getParty().size() > 0) && (gp.getEntityM().getParty().get(keyArray[0]) != null)) {         // Safeguard in case the `party` map is either too small or contains a null value.

            gp.getEntityIconM().getEntityIconById(gp.getEntityM().getParty().get(keyArray[0]).getEntityId())
                    .setSelected(selected);
        }
        gp.getGuiIconM().getIconById(4).setSelected(selected);
    }


    /**
     * Sets whether slot 2 in the party menu is selected or not.
     *
     * @param selected whether slot 2 is selected (true) or not (false)
     */
    private void setSelectionStatusPartySlot2(boolean selected) {

        Set<Integer> keySet = gp.getEntityM().getParty().keySet();
        Integer[] keyArray = keySet.toArray(new Integer[keySet.size()]);

        if ((gp.getEntityM().getParty().size() > 1) && (gp.getEntityM().getParty().get(keyArray[1]) != null)) {         // Safeguard in case the `party` map is either too small or contains a null value.

            gp.getEntityIconM().getEntityIconById(gp.getEntityM().getParty().get(keyArray[1]).getEntityId())
                    .setSelected(selected);
        }
        gp.getGuiIconM().getIconById(5).setSelected(selected);
    }


    /**
     * Updates whether the scroll arrows (both left and right) appear as active or not in the settings menu screen.
     */
    private void setActiveScrollArrows() {

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
    public int getPartySlotSelected() {
        return partySlotSelected;
    }

    public int getMaxNumItemRow() {
        return maxNumItemRow;
    }

    public int getMaxNumItemCol() {
        return maxNumItemCol;
    }

    public int getItemRowSelected() {
        return itemRowSelected;
    }

    public int getItemColSelected() {
        return itemColSelected;
    }

    public int getInventoryIndexSelected() {
        return inventoryIndexSelected;
    }

    public boolean[][] getOccupiedItemSlots() {
        return occupiedItemSlots;
    }

    public int getSystemSettingSelected() {
        return systemSettingSelected;
    }

    public int getSystemOptionSelected() {
        return systemOptionSelected;
    }


    // SETTERS
    public void setPartySlotSelected(int partySlotSelected) {
        if (partySlotSelected < 0) {
            this.partySlotSelected = 0;
        } else if (partySlotSelected > 2) {
            this.partySlotSelected = 2;
        } else {
            this.partySlotSelected = partySlotSelected;
        }
        updateSelectedPartyMenuEntity();
    }

    public void setItemRowSelected(int itemRowSelected) {
        if ((itemRowSelected >= 0) && (itemRowSelected < maxNumItemRow)) {
            if (occupiedItemSlots[itemRowSelected][itemColSelected]) {
                this.itemRowSelected = itemRowSelected;
                inventoryIndexSelected = (maxNumItemRow * itemRowSelected) + (itemColSelected);
            }
        }
    }

    public void setItemColSelected(int itemColSelected) {
        if ((itemColSelected >= 0) && (itemColSelected < maxNumItemCol)) {
            if (occupiedItemSlots[itemRowSelected][itemColSelected]) {
                this.itemColSelected = itemColSelected;
                inventoryIndexSelected = (maxNumItemRow * itemRowSelected) + (itemColSelected);
            }
        }
    }

    public void setSystemSettingSelected(int systemSettingSelected) {
        if ((systemSettingSelected >= 0) && (systemSettingSelected < gp.getSystemSettingsSize())) {
            this.systemSettingSelected = systemSettingSelected;
            systemOptionSelected = gp.getSystemSetting(systemSettingSelected).getActiveOption();
            systemOptionSelected = gp.getSystemSetting(systemSettingSelected).getActiveOption();
            setActiveScrollArrows();
        }
    }

    public void setSystemOptionSelected(int systemOptionSelected) {
        if ((systemOptionSelected >= 0) && (systemOptionSelected < gp.getSystemSetting(systemSettingSelected).getOptionsSize())) {
            this.systemOptionSelected = systemOptionSelected;
            gp.getSystemSetting(systemSettingSelected).setActiveOption(systemOptionSelected);
            setActiveScrollArrows();
        }
    }
}
