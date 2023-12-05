package miscellaneous;

import core.GamePanel;
import entity.EntityBase;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import render.Renderer;
import render.ZIndex;
import render.drawable.Transform;

import java.util.Set;

/**
 * This class handles the drawing of all on-screen user interface (UI) elements.
 */
public class Ui {

    // FIELDS
    private final GamePanel gp;
    private Renderer renderer;

    /**
     * Counter to control the fade to/from effect of a transition screen.
     */
    private int transitionFrameCounter;


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
     * Constant that sets the number of item slot rows that can be displayed at once in the inventory menu.
     */
    private final int maxNumItemRow = 5;

    /**
     * Constant that sets the number of item slot columns that can be displayed at once in the inventory menu.
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


    // CONSTRUCTOR
    /**
     * Constructs a Ui instance.
     *
     * @param gp GamePanel instance
     */
    public Ui(GamePanel gp) {
        this.gp = gp;
        occupiedItemSlots = new boolean[maxNumItemRow][maxNumItemCol];
    }


    // METHODS
    /**
     * Adds all UI elements to the render pipeline.
     *
     * @param renderer Renderer instance
     * @param dt time since the last rendered frame (frame pacing)
     */
    public void addToRenderPipeline(Renderer renderer, double dt) {

        if (this.renderer != renderer) {
            this.renderer = renderer;                                                                                   // Makes it easier to access current renderer across entire class.
        }

        switch (gp.getGameState()) {
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
//                drawInventoryMenuScreen();
                break;
            case SETTINGS_MENU:
                renderInGameMenuMainWindowScreen();
//                drawSettingsMenuScreen();
                break;
            case TRANSITION:
                renderTransitionScreen();
                break;
            case SUB_MENU:
                renderSubMenuScreen();
                if (gp.getDialogueR().getCurrentConv() != null) {                                                       // Keep dialogue screen up if the sub-menu is being drawn while dialogue is also being displayed.
                    renderDialogueScreen();
                }
        }

        // DEBUG.
        if (gp.isDebugActive()) {
            renderDebug(dt);
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
        if (gp.getDialogueR().getDialogueEntityName() != null
                && !gp.getDialogueR().getDialogueEntityName().equals("")) {

            // Set position and dimensions of dialogue sub-window.
            float subWindowScreenX = 0.03f;
            float subWindowScreenTopBottomPadding = 0.02f;
            float subWindowScreenLeftRightPadding = 0.02f;
            String entityName = gp.getDialogueR().getDialogueEntityName();
            float entityNameWidth = 0;
            for (int i = 0; i < entityName.length(); i++) {
                char optionCharacter = entityName.charAt(i);
                entityNameWidth += renderer.getFont("Arimo").getCharacter(optionCharacter).getWidth() * fontScale;
            }
            float subWindowScreenWidth = gp.getCamera().worldWidthToScreenWidth(entityNameWidth) + (2 * subWindowScreenLeftRightPadding);
            float subWindowScreenHeight = characterScreenHeight + (2 * subWindowScreenTopBottomPadding);
            Vector2f subWindowScreenCoords = new Vector2f(subWindowScreenX, mainWindowScreenCoords.y - subWindowScreenHeight);

            // Render dialogue sub-window.
            Vector2f subWindowWorldCoords = gp.getCamera().screenCoordsToWorldCoords(subWindowScreenCoords);
            float subWindowWorldWidth = gp.getCamera().screenWidthToWorldWidth(subWindowScreenWidth);
            float subWindowWorldHeight = gp.getCamera().screenHeightToWorldHeight(subWindowScreenHeight);
            renderer.addRectangle(
                    new Vector4f(0, 0, 0, 180),
                    new Transform(subWindowWorldCoords, new Vector2f(subWindowWorldWidth, subWindowWorldHeight)),
                    ZIndex.SECOND_LAYER);

            // Set position of dialogue entity name.
            Vector2f subTextScreenCoords = new Vector2f(subWindowScreenX + subWindowScreenLeftRightPadding, subWindowScreenCoords.y + subWindowScreenTopBottomPadding);

            // Render dialogue entity name.
            renderString(gp.getDialogueR().getDialogueEntityName(), subTextScreenCoords, fontScale, new Vector3f(121, 149, 255), "Arimo");
        }

        // Dialogue progress arrow, if applicable.
        if ((gp.getDialogueR().isDialoguePaused())
                || (!gp.getDialogueR().isReadingDialogue() && (gp.getDialogueR().isAlwaysShowArrow()))) {
            Vector2f arrowScreenCoords = new Vector2f(mainWindowScreenCoords.x + mainWindowScreenWidth - 0.02f, mainWindowScreenCoords.y + mainWindowScreenHeight - 0.03f);
            gp.getDialogueA().addToRenderPipeline(renderer, arrowScreenCoords.x, arrowScreenCoords.y);
        }

        // Set position of main dialogue text (lines 1 and 2) and render it.
        float mainTextScreenLeftPadding = 0.03f;
        float mainTextScreenSpacing = (mainWindowScreenHeight - (2 * characterScreenHeight)) / 3;
        Vector2f mainTextScreenCoords = new Vector2f(mainTextScreenLeftPadding, mainWindowScreenCoords.y + mainTextScreenSpacing);
        renderString(gp.getDialogueR().getDialoguePrint1(), mainTextScreenCoords, fontScale, new Vector3f(255, 255, 255), "Arimo");
        mainTextScreenCoords.y += characterScreenHeight + mainTextScreenSpacing;
        renderString(gp.getDialogueR().getDialoguePrint2(), mainTextScreenCoords, fontScale, new Vector3f(255, 255, 255), "Arimo");
    }


    /**
     * Adds components of the main window for the in-game menu to the render pipeline.
     * Specifically, the window itself, header section icons, header section title, and header divider are added.
     * These are elements common to all sections of the in-game menu.
     */
    private void renderInGameMenuMainWindowScreen() {

        // Prepare main window position and dimensions.
        Vector2f mainWindowScreenCoords = new Vector2f(mainWindowScreenLeftRightPadding, mainWindowScreenTopBottomPadding);
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
        gp.getIconM().addToRenderPipeline(renderer, 2, menuIconScreenX, menuIconScreenY);                               // Settings menu icon.
        menuIconScreenX -= 0.05f;
        gp.getIconM().addToRenderPipeline(renderer, 1, menuIconScreenX, menuIconScreenY);                               // Inventory menu icon.
        menuIconScreenX -= 0.05f;
        gp.getIconM().addToRenderPipeline(renderer, 0, menuIconScreenX, menuIconScreenY);                               // Party menu icon.

        // Prepare header divider (horizontal line beneath header) position and dimensions and render.
        float dividerScreenThickness = 0.004f;                                                                          // Normalized (screen) thickness of horizontal line.
        float dividerScreenLeftRightGap = 0.055f;                                                                       // Normalized (screen) space on either side of horizontal line between main window edge.
        float dividerScreenWidth = 1 - (2 * mainWindowScreenLeftRightPadding) - (2 * dividerScreenLeftRightGap);
        float dividerWorldThickness = gp.getCamera().screenHeightToWorldHeight(dividerScreenThickness);
        float dividerWorldWidth = gp.getCamera().screenWidthToWorldWidth(dividerScreenWidth);
        float menuIconWorldHeight = gp.getIconM().getIconById(0).getNativeSpriteHeight();                               // Get native (world) height of menu icons; all are same height, so doesn't matter which is used here.
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
        float titleScreenTopBottomPadding = (dividerScreenCoords.y - mainWindowScreenTopBottomPadding - optionsCharacterScreenHeight) / 2;
        Vector2f titleScreenCoords = new Vector2f(
                mainWindowScreenLeftRightPadding + 0.065f,
                mainWindowScreenTopBottomPadding + titleScreenTopBottomPadding);
        String title = "???";
        switch (gp.getGameState()) {
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
     * This is to be rendered on top of the in-game menu main window.
     */
    private void renderPartyMemberStatusIcons() {

        // Prepare slot icon positions (i.e., background sprite of each stat icon).
        float slotIconWorldHeight = gp.getIconM().getIconById(3).getNativeSpriteHeight();                               // Get native (world) height of slot icons; all are same height, so doesn't matter which is used here.
        float slotIconScreenHeight = gp.getCamera().worldHeightToScreenHeight(slotIconWorldHeight);
        float bottomSlotIconScreenY = 1 - mainWindowScreenTopBottomPadding - 0.1f - slotIconScreenHeight;               // Normalized (screen) y-position of the bottommost slot.
        float slotIconScreenX = mainWindowScreenLeftRightPadding + 0.025f;
        float slotIconScreenY = bottomSlotIconScreenY;
        float slotIconVerticalSpacing = 0.1f;                                                                           // Normalized (screen) spacing between each slot (does not include height of slot icon itself).

        // Render slot icon 2 (bottommost).
        gp.getIconM().addToRenderPipeline(renderer, 5, slotIconScreenX, slotIconScreenY);

        // Render slot icon 1.
        slotIconScreenY -= slotIconVerticalSpacing + slotIconScreenHeight;
        gp.getIconM().addToRenderPipeline(renderer, 4, slotIconScreenX, slotIconScreenY);

        // Render slot icon 0 (topmost, player entity).
        slotIconScreenY -= slotIconVerticalSpacing + slotIconScreenHeight;
        gp.getIconM().addToRenderPipeline(renderer, 3, slotIconScreenX, slotIconScreenY);

        // Extract keys from party map.
        Set<Integer> keySet = gp.getParty().keySet();
        Integer[] keyArray = keySet.toArray(new Integer[keySet.size()]);

        // Prepare entity icon and status information text positions to render on top of respective slot icons.
        float entityIconScreenX = slotIconScreenX + 0.01f;
        float entityIconScreenY = bottomSlotIconScreenY + 0.012f;
        float statusInfoTextScreenX = slotIconScreenX + 0.07f;
        float statusInfoTextScreenY = bottomSlotIconScreenY + 0.01f;

        // Render entity icon 2 (bottommost).
        if ((gp.getParty().size() > 1) && (gp.getParty().get(keyArray[1]) != null)) {
            gp.getEntityIconM().addToRenderPipeline(renderer, gp.getParty().get(keyArray[1]).getEntityId(), entityIconScreenX, entityIconScreenY);
            renderPartyMemberStatusInformation(gp.getParty().get(keyArray[1]), statusInfoTextScreenX, statusInfoTextScreenY);
        }


        // Render entity icon 1.
        entityIconScreenY -= slotIconVerticalSpacing + slotIconScreenHeight;
        statusInfoTextScreenY -= slotIconVerticalSpacing + slotIconScreenHeight;
        if ((gp.getParty().size() > 0) && (gp.getParty().get(keyArray[0]) != null)) {
            gp.getEntityIconM().addToRenderPipeline(renderer, gp.getParty().get(keyArray[0]).getEntityId(), entityIconScreenX, entityIconScreenY);
            renderPartyMemberStatusInformation(gp.getParty().get(keyArray[0]), statusInfoTextScreenX, statusInfoTextScreenY);
        }

        // Render entity icon 0 (topmost, player entity).
        entityIconScreenY -= slotIconVerticalSpacing + slotIconScreenHeight;
        statusInfoTextScreenY -= slotIconVerticalSpacing + slotIconScreenHeight;
        gp.getEntityIconM().addToRenderPipeline(renderer, gp.getPlayer().getEntityId(), entityIconScreenX, entityIconScreenY);
        renderPartyMemberStatusInformation(gp.getPlayer(), statusInfoTextScreenX, statusInfoTextScreenY);
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
        renderStringShadow(name, textScreenCoords, new Vector3f(255, 255, 255), 0.11f, "Arimo Bold");
        textScreenCoords.y += 0.04f;
        renderStringShadow(level, textScreenCoords, new Vector3f(255, 255, 255), 0.11f, "Arimo Bold");
        textScreenCoords.y += 0.04f;
        renderStringShadow(lifeLabel, textScreenCoords, new Vector3f(255, 255, 255), 0.11f, "Arimo Bold");

        // Render life bar.
        float barScreenX = textScreenCoords.x + 0.03f;
        float barScreenY = textScreenCoords.y + 0.002f;
        float barScreenWidth = 0.055f;                                                                                   // The maximum normalized (screen) width of the life bar interior (corresponds with maximum life).
        float barScreenHeight = 0.023f;                                                                                  // The normalized (screen) thickness of the life bar.
        renderLifeBar(entity.getLife(), entity.getMaxLife(), barScreenWidth, barScreenHeight, barScreenX, barScreenY);

        // Draw remaining life points text with a shadowed effect.
        textScreenCoords.x += 0.074f;
        textScreenCoords.y -= 0.008f;
        renderStringShadow(lifeValue, textScreenCoords, new Vector3f(255, 255, 255), 0.08f, "Arimo Bold");
    }


    /**
     * Adds a life bar to the render pipeline.
     *
     * @param life number of remaining life points
     * @param maxLife maximum number of life points
     * @param screenWidth screen width of the life bar
     * @param screenHeight screen height of the life bar
     * @param screenX screen x-coordinate of the life bar (leftmost)
     * @param screenY screen y-coordinate of the life bar (topmost)
     */
    private void renderLifeBar(int life, int maxLife, float screenWidth, float screenHeight, float screenX, float screenY) {

        // Prepare life bar.
        float borderScreenThickness = screenWidth * 0.04f;                                                              // Normalized (screen) thickness of border surrounding life bar interior.
        float remainingLifePercentage = (float)life/ (float)maxLife;                                                    // Decimal percentage of life that the entity has relative to its maximum life.
        if (remainingLifePercentage > 1) {
            remainingLifePercentage = 1;                                                                                // Prevent the life bar from over-filling.
        } else if (remainingLifePercentage < 0) {
            remainingLifePercentage = 0;                                                                                // Prevent the life bar from using a negative value.
        }

        // Render life bar background/border.
        Vector4f color = new Vector4f(255, 255, 255, 255);
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
     * Draws the inventory menu screen (to be drawn on top of the core menu screen).
     */
    private void drawInventoryMenuScreen() {

        drawItemIcons();
        drawItemDetailText();
    }


    /**
     * Draws details about the currently selected item on the inventory menu screen.
     */
    private void drawItemDetailText() {

        // Initialize position and color.
        Vector3f nameColor = new Vector3f(121, 149, 255);
        Vector3f quantityColor = new Vector3f(211, 125, 45);
        int textX = (gp.getNativeScreenWidth() / 2) + 10;
//        int textY = mainWindowTopBottomPadding + 68;

        // Initialize text to draw.
        String name = gp.getPlayer().getInventory().get(inventoryIndexSelected).getName();
        String description = gp.getPlayer().getInventory().get(inventoryIndexSelected).getDescription();
        String quantity = "Quantity: " + gp.getPlayer().getInventory().get(inventoryIndexSelected).getAmount();

        // Draw item name, description, and quantity.
//        renderStringShadow(name, textX, textY, nameColor, 16F, "Arimo");
//        textY += 30;
//        renderStringShadow(quantity, textX, textY, quantityColor, 16F, "Arimo");
//        textY += 30;
//        drawStringBlock(description, textX, textY, 40, 18, new Vector3f(255, 255, 255), 16F, true);
    }


    /**
     * Draws icons for each item on the inventory menu screen.
     */
    private void drawItemIcons() {

        int verticalSpacing = 51;                                                                                       // Core vertical spacing between each item icon before scaling is applied.
        int horizontalSpacing = 51;                                                                                     // Core horizontal spacing between each item icon before scaling is applied.
        int numItems = gp.getPlayer().getInventory().size();                                                            // Number of items currently in the player's inventory.

        // Draw the backdrop for each item icon.
        int leftIconX = 15;                                                                                             // The x-position of the leftmost icon backdrops; used with `horizontalSpacing` to get the x-position of the icons to the right.
        int topIconY = 58;                                                                                              // The y-position of the topmost icon backdrops; used with `verticalSpacing` to get the y-position of lower icons.
        int row = 0;                                                                                                    // Initialize a variable to track which row is currently being worked on.
        int itemIndex = 0;                                                                                              // Initialize a variable to track which item is currently being worked on when looping through the player's inventory.

        while ((row < maxNumItemRow) && (itemIndex < numItems)) {

            int col = 0;                                                                                                // Initialize a variable to track which column in the current row is currently being worked on.

            while ((col < maxNumItemCol) && (itemIndex < numItems)) {

                int iconX = leftIconX + (horizontalSpacing * col);
//                int iconY = mainWindowTopBottomPadding + (topIconY + (verticalSpacing * row));

                if (gp.getPlayer().getInventory().get(itemIndex).isStackable()) {                                       // Switch which icon backdrop to draw based on whether the item is stackable or not.

//                    gp.getIconM().draw(g2, 6, iconX, iconY); // TODO : Replace with Renderer!
                } else {

//                    gp.getIconM().draw(g2, 7, iconX, iconY); // TODO : Replace with Renderer!
                }
                itemIndex++;                                                                                            // Iterate to the next item.
                col++;                                                                                                  // Iterate to the next column.
            }
            row++;                                                                                                      // Iterate to the next row.
        }

        // Draw item images on each item backdrop, using the items present in the player's inventory.
        // Also draw the item selector according to the currently selected row and column.
        // Also store the index of the currently selected item in the player's inventory.
        int leftImageX = leftIconX + 2;                                                                                 // The x-position of the leftmost item images; used with `horizontalSpacing` to get the x-position of the icons to the right.
        int topImageY = topIconY + 2;                                                                                   // The y-position of the topmost item images; used with `verticalSpacing` to get the y-position of the lower images.
        row = 0;                                                                                                        // Reset the variable that tracks which row is currently being worked on.
        itemIndex = 0;                                                                                                  // Reset the variable that tracks which item is currently being worked on when looping through the player's inventory.

        while ((row < maxNumItemRow) && (itemIndex < numItems)) {

            int col = 0;                                                                                                // Initialize a variable to track which column in the current row is currently being worked on.

            while ((col < maxNumItemCol) && (itemIndex < numItems)) {

                int imageX = leftImageX + (horizontalSpacing * col);
//                int imageY = mainWindowTopBottomPadding + (topImageY + (verticalSpacing * row));
//                gp.getPlayer().getInventory().get(itemIndex).draw(g2, imageX, imageY); // TODO : Replace with Renderer!

                if ((row == itemRowSelected) && (col == itemColSelected)) {

                    int selectorX = imageX - 4;
//                    int selectorY = imageY - 4;
//                    gp.getIconM().draw(g2, 8, selectorX, selectorY); // TODO : Replace with Renderer!                 // Draw the item selector on the selected item icon.
                }
                itemIndex++;                                                                                            // Iterate to the next item.
                col++;                                                                                                  // Iterate to the next column.
            }
            row++;                                                                                                      // Iterate to the next row.
        }

        // Draw the amounts in each stack of items.
        int leftQuantityX = leftIconX + 34;                                                                             // The x-position of the leftmost item quantities; used with `horizontalSpacing` to get the x-position of the quantities to the right.
        int topQuantityY = topIconY + 41;                                                                               // The y-position of the topmost item quantities; used with `verticalSpacing` to get the y-position of the lower quantities.
        row = 0;                                                                                                        // Reset the variable that tracks which row is currently being worked on.
        itemIndex = 0;                                                                                                  // Reset the variable that tracks which item is currently being worked on when looping through the player's inventory.

        while ((row < maxNumItemRow) && (itemIndex < numItems)) {

            int col = 0;                                                                                                // Initialize a variable to track which column in the current row is currently being worked on.

            while ((col < maxNumItemCol) && (itemIndex < numItems)) {

                if (gp.getPlayer().getInventory().get(itemIndex).isStackable()) {                                       // Only draw the item quantity if it's stackable.

                    String quantity = Integer.toString(gp.getPlayer().getInventory().get(itemIndex).getAmount());
                    int quantityX = leftQuantityX + (horizontalSpacing * col);
//                    int quantityY = mainWindowTopBottomPadding + (topQuantityY + (verticalSpacing * row));
//                    renderStringShadow(quantity, quantityX, quantityY, new Vector3f(255, 255, 255), 14F, "Arimo");
                }
                itemIndex++;                                                                                            // Iterate to the next item.
                col++;                                                                                                  // Iterate to the next column.
            }
            row++;                                                                                                      // Iterate to the next row.
        }
    }


    /**
     * Draws the settings menu screen (to be drawn on top of the core menu screen).
     */
    private void drawSettingsMenuScreen() {

        // Nothing here... yet.
    }


    /**
     * Adds transition screen components to the render pipeline (fade out to black, perform necessary loading, then fade
     * in from black).
     */
    private void renderTransitionScreen() {

        Vector2f worldCoords = new Vector2f(0, 0);
        float worldWidth = gp.getMaxWorldCol() * gp.getNativeTileSize();                                                // Overlaid black rectangle will span entire width of world.
        float worldHeight = gp.getMaxWorldRow() * gp.getNativeTileSize();                                               // Overlaid black rectangle will span entire height of world.

        switch (gp.getActiveTransitionPhase()) {
            case FADING_TO:                                                                                             // Phase 1: Fade screen to black.
                transitionFrameCounter++;
                renderer.addRectangle(new Vector4f(0, 0, 0, transitionFrameCounter * 10),
                        new Transform(new Vector2f(0, 0), new Vector2f(worldWidth, worldHeight)),
                        ZIndex.FIRST_LAYER);
                if (transitionFrameCounter == 25) {
                    transitionFrameCounter = 0;                                                                         // Reset `transitionFrameCounter` to prepare it for the second phase.
                    gp.setActiveTransitionPhase(TransitionPhase.LOADING);                                               // Proceed to the next (second) phase of the transition.
                }
                break;
            case LOADING:                                                                                               // Phase 2: Wait on black screen.
                transitionFrameCounter++;
                renderer.addRectangle(new Vector4f(0, 0, 0, 255),
                        new Transform(worldCoords, new Vector2f(worldWidth, worldHeight)),
                        ZIndex.FIRST_LAYER);
                if (transitionFrameCounter == 30) {                                                                     // At 60 FPS, this will amount to waiting on the black screen for 0.5 seconds.
                    transitionFrameCounter = 0;                                                                         // Reset `transitionFrameCounter` to prepare it for the final (third) phase.
                    gp.setActiveTransitionPhase(TransitionPhase.FADING_FROM);                                           // Proceed to the final (third) phase of the transition.
                }
                break;
            case FADING_FROM:                                                                                           // Phase 3: Fade from black.
                transitionFrameCounter++;
                renderer.addRectangle(new Vector4f(0, 0, 0, (250 - (transitionFrameCounter * 10))),
                        new Transform(worldCoords, new Vector2f(worldWidth, worldHeight)),
                        ZIndex.FIRST_LAYER);
                if (transitionFrameCounter == 25) {
                    transitionFrameCounter = 0;                                                                         // Reset `transitionFrameCounter` to its default value since the transition is complete.
                    gp.setActiveTransitionPhase(TransitionPhase.CLEANUP);
                }
                break;
        }
    }


    /**
     * Adds sub-menu components to the render pipeline.
     */
    private void renderSubMenuScreen() {

        // Prepare window position and dimensions (other than window width).
        // The following information is assuming use of the font Arimo (normal/non-bold).
        float fontScale = 0.15f;                                                                                        // Font size (multiplies native height).
        float optionsScreenTopBottomPadding = 0.02f;                                                                    // Normalized (screen) padding on top and bottom of sub-menu window.
        float optionsScreenSpacing = 0.022f;                                                                            // Normalized (screen) spacing between options text.
        float optionsCharacterWorldHeight = renderer.getFont("Arimo").getCharacter('A').getHeight() * fontScale;        // It doesn't matter which character is used, since all characters in a font have the same height.
        float optionsCharacterScreenHeight = gp.getCamera().worldHeightToScreenHeight(optionsCharacterWorldHeight);     // Normalized (screen) character height.
        float windowScreenHeight = (optionsScreenSpacing * (gp.getSubMenuH().getOptions().size() - 1))                  // Spacing between options text.
                + (2 * optionsScreenTopBottomPadding)                                                                   // Padding on top and bottom of sub-menu window.
                + (optionsCharacterScreenHeight * gp.getSubMenuH().getOptions().size());                                // Character height for each option.
        Vector2f windowScreenCoords = new Vector2f(gp.getSubMenuH().getSubMenuScreenX(), gp.getSubMenuH().getSubMenuScreenY());

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
        float windowScreenWidth = gp.getCamera().worldWidthToScreenWidth(maxOptionWorldWidth) + optionsScreenLeftPadding + optionsScreenRightPadding;

        // Render sub-menu window.
        Vector2f windowWorldCoords = gp.getCamera().screenCoordsToWorldCoords(windowScreenCoords);
        float windowWorldWidth = gp.getCamera().screenWidthToWorldWidth(windowScreenWidth);
        float windowWorldHeight = gp.getCamera().screenHeightToWorldHeight(windowScreenHeight);
        renderer.addRectangle(
                new Vector4f(0, 0, 0, 180),
                new Transform(windowWorldCoords, new Vector2f(windowWorldWidth, windowWorldHeight)),
                ZIndex.SECOND_LAYER);

        // Calculate position of text for first option.
        Vector2f optionsScreenCoords = new Vector2f(windowScreenCoords.x + optionsScreenLeftPadding, windowScreenCoords.y + optionsScreenTopBottomPadding);

        // Render text for each option and selection arrow next to selected option.
        for (int i = 0; i < gp.getSubMenuH().getOptions().size(); i++) {
            renderString(gp.getSubMenuH().getOptions().get(i), optionsScreenCoords, fontScale, new Vector3f(255, 255, 255), "Arimo");
            if (i == gp.getSubMenuH().getIndexSelected()) {
                float selectionArrowScreenHeight = gp.getCamera().worldHeightToScreenHeight(gp.getSelectionA().getNativeSpriteHeight());
                float selectionArrowScreenY = optionsScreenCoords.y + (optionsCharacterScreenHeight / 2) - (selectionArrowScreenHeight / 2);
                gp.getSelectionA().addToRenderPipeline(renderer, optionsScreenCoords.x - 0.02f, selectionArrowScreenY);
            }
            optionsScreenCoords.y += optionsCharacterScreenHeight + optionsScreenSpacing;
        }
    }


    /**
     * Updates which party member is selected in the party menu screen.
     */
    private void updateSelectedPartyMember() {

        Set<Integer> keySet = gp.getParty().keySet();
        Integer[] keyArray = keySet.toArray(new Integer[keySet.size()]);

        switch (partySlotSelected) {
            case 0:
                selectPartySlot0(true);                                                                                 // Set slot 0 (player entity) to selected.
                selectPartySlot1(false);                                                                                // Set slot 1 to not selected.
                selectPartySlot2(false);                                                                                // Set slot 2 to not selected.
                break;
            case 1:
                if ((gp.getParty().size() > 0) && (gp.getParty().get(keyArray[0]) != null)) {                           // Check whether a party member actually occupies this slot or not.
                    selectPartySlot0(false);                                                                            // Set slot 0 (player entity) to not selected.
                    selectPartySlot1(true);                                                                             // Set slot 1 to selected.
                    selectPartySlot2(false);                                                                            // Set slot 2 to not selected.
                } else {
                    setPartySlotSelected(partySlotSelected - 1);                                                        // No party member exists in this slot, so move up to the slot above.
                }
                break;
            case 2:
                if ((gp.getParty().size() > 1) && (gp.getParty().get(keyArray[1]) != null)) {                           // Check whether a part member actually occupies this slot or not.
                    selectPartySlot0(false);                                                                            // Set slot 0 (player entity) to not selected.
                    selectPartySlot1(false);                                                                            // Set slot 1 to not selected.
                    selectPartySlot2(true);                                                                             // Set slot 2 to selected.
                } else {
                    setPartySlotSelected(partySlotSelected - 1);                                                        // No party member exists in this slot, so move up to the slot above.
                }
                break;
        }
    }


    /**
     * Draws debug information.
     *
     * @param dt time since the last rendered frame (frame pacing)
     */
    private void renderDebug(double dt) {

        float fontScale = 0.18f;
        float screenX = 0.01f;

        // Memory usage by Java Runtime.
        Vector2f screenCoords = new Vector2f(screenX, 0.01f);
        Long totalMemoryBytes = Runtime.getRuntime().totalMemory();
        Long freeMemoryBytes = Runtime.getRuntime().freeMemory();
        Long usedMemoryMegabytes = (totalMemoryBytes - freeMemoryBytes) / 1000000;
        String memoryUsage = "JVM Memory Usage: " + usedMemoryMegabytes + " MB";
        renderStringShadow(memoryUsage, screenCoords, new Vector3f(255, 255, 255), fontScale, "Arimo");

        // Frame rate.
        screenCoords = new Vector2f(screenX, 0.07f);
        String fps = "FPS: " + (int)(1.0 / dt);
        renderStringShadow(fps, screenCoords, new Vector3f(255, 255, 255), fontScale, "Arimo");

        // Player column.
        screenCoords = new Vector2f(screenX, 0.13f);
        String col = "Player Col: " + gp.getPlayer().getCol();
        renderStringShadow(col, screenCoords, new Vector3f(255, 255, 255), fontScale, "Arimo");

        // Player row.
        screenCoords = new Vector2f(screenX, 0.19f);
        String row = "Player Row: " + gp.getPlayer().getRow();
        renderStringShadow(row, screenCoords, new Vector3f(255, 255, 255), fontScale, "Arimo");

        // Camera center (x).
        screenCoords = new Vector2f(screenX, 0.25f);
        String centerX = "Camera Center X: " + (gp.getCamera().getPositionMatrix().x + ((float)gp.getCamera().getScreenWidth() / 2));
        renderStringShadow(centerX, screenCoords, new Vector3f(255, 255, 255), fontScale, "Arimo");

        // Camera center (y).
        screenCoords = new Vector2f(screenX, 0.31f);
        String centerY = "Camera Center Y: " + (gp.getCamera().getPositionMatrix().y + ((float)gp.getCamera().getScreenHeight() / 2));
        renderStringShadow(centerY, screenCoords, new Vector3f(255, 255, 255), fontScale, "Arimo");
    }


    /**
     * Adds a string of text to the render pipeline.
     *
     * @param text text to be drawn
     * @param screenCoords screen coordinates of the text (leftmost and topmost, normalized between 0 and 1)
     * @param color text color (r, g, b)
     * @param size size at which to draw the text
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
     * @param color text color (r, g, b)
     * @param size size at which to draw the text
     */
    private void renderStringShadow(String text, Vector2f screenCoords, Vector3f color, float size, String font) {

        Vector2f shadowScreenCoords = new Vector2f(screenCoords.x + 0.0015f, screenCoords.y + 0.0015f);
        renderString(text, shadowScreenCoords, size, new Vector3f(0, 0, 0), font);
        renderString(text, screenCoords, size, color, font);
    }


    /**
     * Prints a block of text line-by-line according to a specified line character limit.
     * Text will be printed with the currently set font.
     *
     * @param text complete text to be printed
     * @param screenX x-coordinate of the printed text block (leftmost)
     * @param topScreenY y-coordinate of the top line of the printed text block (topmost)
     * @param maxLineLength maximum number of characters allowed in a printed line of text
     * @param lineSpacing space between each printed line of text
     * @param color color of the printed text (r, g, b)
     * @param size size at which to draw the text
     * @param dropShadow whether a drop shadow should be drawn (true) or not (false)
     */
    private void drawStringBlock(String text, int screenX, int topScreenY, int maxLineLength, int lineSpacing,
                                 Vector3f color, float size, boolean dropShadow) {

//        String[] words = text.split(" ");                                                                               // An array of each word in the complete text, split by spaces.
//        int wordsIndex = 0;                                                                                             // Track which index of the words array is currently being checked.
//        int screenY = topScreenY;
//
//        while (wordsIndex < words.length) {                                                                             // Print each line of text.
//
//            boolean limitExceeded = false;                                                                              // Track whether the maximum character length of a line has been exceeded (true) or not (false) yet.
//            String line = "";                                                                                           // Initialize the line of text that's being built.
//
//            while ((!limitExceeded) && (wordsIndex < words.length)) {                                                   // Add words to a line of text until either the maximum character length is exceeded OR there are no more words to print.
//
//                String build;                                                                                           // Create a string that will be a candidate for the next line of text to be printed.
//
//                if (line.equals("")) {
//
//                    build = words[wordsIndex];
//                } else {
//
//                    build = line + " " + words[wordsIndex];
//                }
//
//                if (build.length() > maxLineLength) {
//
//                    limitExceeded = true;                                                                               // Character length of the line has been exceeded.
//
//                    if (words[wordsIndex].length() > maxLineLength) {
//
//                        words[wordsIndex] = "???";                                                                      // If the number of characters in a single word exceeds the maximum number of characters that can be printed in a line of text, skip the word to avoid getting stuck in an infinite loop.
//                    }
//                } else {
//
//                    line = build;                                                                                       // Set the next line of text to be drawn.
//                    wordsIndex++;                                                                                       // Iterate to the next word.
//                }
//            }
//
//            if (dropShadow) {
//
//                renderStringShadow(line, screenX, screenY, color, size, "Arimo");                                       // Draw the line of text with a drop shadow.
//            } else {
//
//                renderString(line, screenX, screenY, size, color, "Arimo");                                             // Draw the line of text without a drop shadow.
//            }
//
//            if (wordsIndex != words.length) {
//
//                screenY += lineSpacing;                                                                                 // Spacing between lines of text.
//            }
//        }
    }


    /**
     * Sets whether slot 0 (player) in the party menu is selected or not.
     *
     * @param selected whether slot 0 is selected (true) or not (false)
     */
    private void selectPartySlot0(boolean selected) {

        gp.getEntityIconM().getEntityIconById(gp.getPlayer().getEntityId()).setSelected(selected);
        gp.getIconM().getIconById(3).setSelected(selected);
    }


    /**
     * Sets whether slot 1 in the party menu is selected or not.
     *
     * @param selected whether slot 1 is selected (true) or not (false)
     */
    private void selectPartySlot1(boolean selected) {

        Set<Integer> keySet = gp.getParty().keySet();
        Integer[] keyArray = keySet.toArray(new Integer[keySet.size()]);

        if ((gp.getParty().size() > 0) && (gp.getParty().get(keyArray[0]) != null)) {                                   // Safeguard in case the `party` map is either too small or contains a null value.

            gp.getEntityIconM().getEntityIconById(gp.getParty().get(keyArray[0]).getEntityId()).setSelected(selected);
        }
        gp.getIconM().getIconById(4).setSelected(selected);
    }


    /**
     * Sets whether slot 2 in the party menu is selected or not.
     *
     * @param selected whether slot 2 is selected (true) or not (false)
     */
    private void selectPartySlot2(boolean selected) {

        Set<Integer> keySet = gp.getParty().keySet();
        Integer[] keyArray = keySet.toArray(new Integer[keySet.size()]);

        if ((gp.getParty().size() > 1) && (gp.getParty().get(keyArray[1]) != null)) {                                   // Safeguard in case the `party` map is either too small or contains a null value.

            gp.getEntityIconM().getEntityIconById(gp.getParty().get(keyArray[1]).getEntityId()).setSelected(selected);
        }
        gp.getIconM().getIconById(5).setSelected(selected);
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


    // SETTERS
    public void setPartySlotSelected(int partySlotSelected) {
        if (partySlotSelected < 0) {
            this.partySlotSelected = 0;
        } else if (partySlotSelected > 2) {
            this.partySlotSelected = 2;
        } else {
            this.partySlotSelected = partySlotSelected;
        }
        updateSelectedPartyMember();
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
}
