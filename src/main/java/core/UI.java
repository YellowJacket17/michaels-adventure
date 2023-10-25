package core;

import entity.EntityBase;
import utility.exceptions.AssetLoadException;

import java.awt.*;
import java.io.InputStream;
import java.util.Set;

/**
 * This class handles the drawing of all on-screen user interface (UI) elements.
 */
public class UI {

    /*
     * Note that all lengths given here (such as `windowScreenX`, `windowScreenY`, etc.) are in terms of `scale` in
     * GamePanel being set to 1. If the scale is changed, all lengths are multiplied by `scale` accordingly.
     */

    // FIELDS
    private final GamePanel gp;
    private Graphics2D g2;

    /**
     * Normal Arimo font.
     */
    private final Font fontArimo;

    /**
     * Bold Arimo font.
     */
    private final Font fontArimoBold;

    /**
     * Counter to control the fade to/from effect of a transition screen.
     */
    private int frameCounter;

    /**
     * Constant that sets the size of the gap between the screen edges at the top and bottom of the central menu window.
     * The value stored here already accounts for scaling set in GamePanel upon construction.
     */
    private final int coreMenuEdge;

    /**
     * Variable to store current selected party member in the party menu.
     * The default value is zero (first slot).
     */
    private int partySlotSelected;

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
     * Constructs a UI instance.
     *
     * @param gp GamePanel instance
     */
    public UI(GamePanel gp) {
        this.gp = gp;
        fontArimo = setupFont("Arimo-mO92.ttf");
        fontArimoBold = setupFont("ArimoBold-dVDx.ttf");
        coreMenuEdge = gp.getTileSize();
        occupiedItemSlots = new boolean[maxNumItemRow][maxNumItemCol];
    }


    // METHODS
    /**
     * Draws the UI.
     *
     * @param g2 Graphics2D instance
     */
    public void draw(Graphics2D g2) {

        this.g2 = g2;

        // Here, the default font to be used for all text drawn is set.
        // If any text needs to use a different font, it will set it manually in one of the methods called below.
        // At the end of said methods, the font will be reverted to the default set here.
        g2.setFont(fontArimo);

        switch (gp.getGameState()) {
            case EXPLORE:
                // Nothing here.
                break;
            case DIALOGUE:
                drawDialogueScreen();
                break;
            case PARTY_MENU:
                drawCoreMenuScreen();
                drawMenuSectionName("Party");
                drawPartyMenuScreen();
                break;
            case INVENTORY_MENU:
                drawCoreMenuScreen();
                drawMenuSectionName("Inventory");
                drawInventoryMenuScreen();
                break;
            case SETTINGS_MENU:
                drawCoreMenuScreen();
                drawMenuSectionName("Settings");
                drawSettingsMenuScreen();
                break;
            case TRANSITION:
                drawTransitionScreen();
                break;
            case SUB_MENU:
                drawSubMenuScreen();
                if (gp.getDialogueR().getCurrentConv() != null) {                                                       // Keep dialogue screen up if the sub-menu is being drawn while dialogue is also being displayed.
                    drawDialogueScreen();
                }
        }

        // DEBUG.
        if (gp.isDebugActive()) {
            drawDebug();
        }
    }


    /**
     * Loads a font.
     * Recommended file type is TTF.
     *
     * @param fileName file name of font, located in resources/fonts directory
     * @return loaded font
     * @throws AssetLoadException if an error occurs while loading a font
     */
    private Font setupFont(String fileName) {

        String completeFilePath = "/fonts/" + fileName;

        try {

            InputStream is = getClass().getResourceAsStream(completeFilePath);                                          // Open input stream to load font from directory.
            Font font = Font.createFont(Font.TRUETYPE_FONT, is);
            is.close();
            return font;

        } catch (Exception e) {

            throw new AssetLoadException("Could not load font from " + completeFilePath);
        }
    }


    /**
     * Draws the complete dialogue screen with corresponding text.
     */
    private void drawDialogueScreen() {

        // Set position and dimensions of main dialogue window.
        int x_main = 0;                                                                                                 // Amount of space between the dialogue box and screen edge on the left, right, and bottom sides.
        int height_main = 80 * gp.getScale();
        int y_main = gp.getScreenHeight() - height_main;                                                                // Amount of space between the dialogue box and the screen edge on the top side.
        int width_main = gp.getScreenWidth();

        // Draw main dialogue window.
        drawMainDialogueWindow(x_main, y_main, width_main, height_main);

        // Draw sub-window if applicable.
        if (gp.getDialogueR().getDialogueEntityName() != null
                && !gp.getDialogueR().getDialogueEntityName().equals("")) {

            // Set position and dimensions of sub-window for speaking entity name.
            int x_sub = (int)Math.ceil(gp.getScreenWidth() * 0.03);
            int height_sub = 30 * gp.getScale();
            int y_sub = y_main - height_sub;
            int width_sub = (int)(width_main * 0.30);

            // Draw sub-window.
            drawSubDialogueWindow(x_sub, y_sub, width_sub, height_sub);

            // Set position and color of dialogue entity name.
            Color nameColor = new Color(121, 149, 255);
            x_sub += (int)Math.ceil(gp.getScreenWidth() * 0.03);                                                        // Set starting position of dialogue speaking entity name in the main dialogue window (x).
            y_sub += 22 * gp.getScale();                                                                                // Set starting position of dialogue speaking entity name in the sub-window (y).

            // Draw dialogue entity name.
            drawString(gp.getDialogueR().getDialogueEntityName(), x_sub, y_sub, nameColor, 17F * gp.getScale());
        }

        // Set position of dialogue progress arrow and draw it.
        if ((gp.getDialogueR().isDialoguePaused())
                || (!gp.getDialogueR().isReadingDialogue() && (gp.getDialogueR().isAlwaysShowArrow()))) {
            int x_arrow = x_main + width_main - (20 * gp.getScale());
            int y_arrow = y_main + height_main - (17 * gp.getScale());
            gp.getDialogueA().draw(g2, x_arrow, y_arrow);
        }

        // Set position and font of dialogue text and draw it.
        x_main += (int)Math.ceil(gp.getScreenWidth() * 0.06);                                                           // Set starting position of dialogue text in the main dialogue window (x, line 1).
        y_main += 31 * gp.getScale();                                                                                   // Set starting position of dialogue text in the main dialogue window (y, line 1).
        drawString(gp.getDialogueR().getDialoguePrint1(), x_main, y_main, Color.white, 17F * gp.getScale());

        y_main += 30 * gp.getScale();                                                                                   // Set starting position of dialogue text in the dialogue window (y, line 2).
        drawString(gp.getDialogueR().getDialoguePrint2(), x_main, y_main, Color.white, 17F * gp.getScale());
    }


    /**
     * Draws the dialogue sub-window where the name of the speaking entity is displayed.
     *
     * @param screenX x-coordinate of the right side of the window
     * @param screenY y-coordinate of the top side of the window
     * @param width width of the window
     * @param height height of the window
     */
    private void drawSubDialogueWindow(int screenX, int screenY, int width, int height) {

        // Draw the interior of the name window.
        Color color = new Color(0, 0, 0, 180);
        g2.setColor(color);
        g2.fillRoundRect(screenX, screenY, width, height, 0, 0);

        // Draw the border of the name window.
//        color = new Color(0, 48, 128);
//        g2.setStroke(new BasicStroke(gp.getScale() * 2));
//        g2.setColor(color);
//        g2.drawRoundRect(x, y, width, height, arcWidth, arcHeight);
    }


    /**
     * Draws the main dialogue window where the dialogue text is displayed.
     *
     * @param screenX x-coordinate of the right side of the window
     * @param screenY y-coordinate of the top side of the window
     * @param width width of the window
     * @param height height of the window
     */
    private void drawMainDialogueWindow(int screenX, int screenY, int width, int height) {

        // Draw the interior of the main dialogue window.
        Color color = new Color(0, 0, 0, 180);
        g2.setColor(color);
        g2.fillRoundRect(screenX, screenY, width, height, 0, 0);

        // Draw the border of the main dialogue window.
//        color = new Color(0, 48, 128);
//        g2.setStroke(new BasicStroke(gp.getScale() * 2));
//        g2.setColor(color);
//        g2.drawRoundRect(x, y, width, height, arcWidth, arcHeight);
    }


    /**
     * Draws the core of the menu screen.
     */
    private void drawCoreMenuScreen() {

        // Create the frame.
        final int frameX = 0;                                                                                           // The position of the right side of the frame (x).
        final int frameY = coreMenuEdge;                                                                                // The position of the top of the frame (y).
        final int frameWidth = gp.getScreenWidth();
        final int frameHeight = gp.getScreenHeight() - (2 * coreMenuEdge);

        // Draw the frame.
        drawCoreMenuWindow(frameX, frameY, frameWidth, frameHeight);

        // Draw menu icons (party, inventory, and settings).
        int menuIconScreenY = coreMenuEdge + (9 * gp.getScale());

        int menuIconScreenX = gp.getScreenWidth() - (120 * gp.getScale());
        gp.getIconM().draw(g2, 0, menuIconScreenX, menuIconScreenY);

        menuIconScreenX = gp.getScreenWidth() - (80 * gp.getScale());
        gp.getIconM().draw(g2, 1, menuIconScreenX, menuIconScreenY);

        menuIconScreenX = gp.getScreenWidth() - (40 * gp.getScale());
        gp.getIconM().draw(g2, 2, menuIconScreenX, menuIconScreenY);
    }


    /**
     * Draws the core menu window.
     *
     * @param screenX x-coordinate of the left side of the window
     * @param screenY y-coordinate of the top side of the window
     * @param width width of the window
     * @param height height of the window
     */
    private void drawCoreMenuWindow(int screenX, int screenY, int width, int height) {

        // Draw the interior of the window.
        Color color = new Color(0, 0, 0, 220);
        g2.setColor(color);
        g2.fillRoundRect(screenX, screenY, width, height, 0, 0);

        // Set the border of the window.
        color = new Color(255, 255, 255);
        g2.setStroke(new BasicStroke(2 * gp.getScale()));
        g2.setColor(color);

        // Draw the top border of the window.
        g2.drawRoundRect(screenX, screenY + (5 * gp.getScale()), width, 0, 0, 0);

        // Draw the middle border of the window.
        g2.drawRoundRect(screenX, screenY + (41 * gp.getScale()), width, 0, 0, 0);

        // Draw the bottom border of the window.
        g2.drawRoundRect(screenX, screenY + height - (5 * gp.getScale()), width, 0, 0, 0);
    }


    /**
     * Draws the name of the menu section currently active (Party, Inventory, Settings).
     *
     * @param name name of the active section
     */
    private void drawMenuSectionName(String name) {

        // Set position and color of the menu label.
        Color sectionNameColor = new Color(121, 149, 255);
        int x_label = 15 * gp.getScale();                                                                               // Set position of text (right, x).
        int y_label = coreMenuEdge + (29 * gp.getScale());                                                              // Set position of text (top, y).

        // Draw the menu label.
        drawString(name, x_label, y_label, sectionNameColor, 20F * gp.getScale());
    }


    /**
     * Draws the party menu screen (to be drawn on top of the core menu screen).
     */
    private void drawPartyMenuScreen() {

        drawPartyMemberStatIcons();
    }


    /**
     * Draws stat icons for each party member on the party menu screen.
     */
    private void drawPartyMemberStatIcons() {

        int verticalSpacing = 83;                                                                                       // Core vertical spacing between each party member icon before scaling is applied.

        // Draw the background icon for each party member.
        int iconX = 15 * gp.getScale();                                                                                 // The x-position of each icon.
        int topIconY = 67;                                                                                              // The y-position of the top icon; used with `verticalSpacing` to get the y-position of lower icons.

        // Slot 0 (player entity).
        int iconY = coreMenuEdge + (topIconY * gp.getScale());
        gp.getIconM().draw(g2, 3, iconX, iconY);

        // Slot 1.
        iconY = coreMenuEdge + ((topIconY + verticalSpacing) * gp.getScale());
        gp.getIconM().draw(g2, 4, iconX, iconY);

        // Slot 2.
        iconY = coreMenuEdge + ((topIconY + (2 * verticalSpacing)) * gp.getScale());
        gp.getIconM().draw(g2, 5, iconX, iconY);

        // Draw the entity icon and corresponding text for each party member.
        int entityIconX = 23 * gp.getScale();                                                                           // The x-position of each entity icon.
        int topEntityIconY = 72;                                                                                        // The y-position of the top entity icon; used with `verticalSpacing` to get the y-position of lower icons.
        int textX = 68 * gp.getScale();                                                                                 // The x-position of the text in each party member icon.
        int topTextY = 82;                                                                                              // The y-position of the text in the top party member icon; used with `verticalSpacing` to get the y-position of lower icons.

        // Slot 0 (player entity).
        int entityIconY = coreMenuEdge + (topEntityIconY * gp.getScale());
        int textY = coreMenuEdge + (topTextY * gp.getScale());
        gp.getEntityIconM().draw(g2, gp.getPlayer().getEntityId(), entityIconX, entityIconY);
        drawPartyMemberIconText(gp.getPlayer(), textX, textY);

        // Extract keys from party map to prepare for remaining slots.
        Set<Integer> keySet = gp.getParty().keySet();
        Integer[] keyArray = keySet.toArray(new Integer[keySet.size()]);

        // Slot 1.
        if ((gp.getParty().size() > 0) && (gp.getParty().get(keyArray[0]) != null)) {                                   // Safeguard in case the `party` map is either too small or contains a null entry.
            entityIconY = coreMenuEdge + (topEntityIconY + verticalSpacing) * gp.getScale();
            textY = coreMenuEdge + (topTextY + verticalSpacing) * gp.getScale();
            gp.getEntityIconM().draw(g2, gp.getParty().get(keyArray[0]).getEntityId(), entityIconX, entityIconY);
            drawPartyMemberIconText(gp.getParty().get(keyArray[0]), textX, textY);
        }

        // Slot 2.
        if ((gp.getParty().size() > 1) && (gp.getParty().get(keyArray[1]) != null)) {                                   // Safeguard in case the `party` map is either too small or contains a null entry.
            entityIconY = coreMenuEdge + (topEntityIconY + (2 * verticalSpacing)) * gp.getScale();
            textY = coreMenuEdge + (topTextY + (2 * verticalSpacing)) * gp.getScale();
            gp.getEntityIconM().draw(g2, gp.getParty().get(keyArray[1]).getEntityId(), entityIconX, entityIconY);
            drawPartyMemberIconText(gp.getParty().get(keyArray[1]), textX, textY);
        }
    }


    /**
     * Draws text on the stat icon for a party member.
     *
     * @param entity party member whose stat icon is being drawn
     * @param textX x-coordinate of all lines of text
     * @param topTextY y-coordinate of the top line of text being drawn
     */
    private void drawPartyMemberIconText(EntityBase entity, int textX, int topTextY) {

        int textY = topTextY;

        // Set font to bold for text within the party menu icon.
        g2.setFont(fontArimoBold);

        // Initialize text to draw.
        String name = entity.getName();
        String level = "Lv." + entity.getLevel();
        String lifeLabel = "HP";
        String lifeValue = entity.getLife() + "/" + entity.getMaxLife();

        // Draw entity name, level, and life label.
        drawStringShadow(name, textX, textY, Color.white, 14F * gp.getScale());
        textY += 18 * gp.getScale();
        drawStringShadow(level, textX, textY, Color.white, 14F * gp.getScale());
        textY += 18 * gp.getScale();
        drawStringShadow(lifeLabel, textX, textY, Color.white, 14F * gp.getScale());

        // Draw health bar.
        int barX = textX + (22 * gp.getScale());
        int barY = textY - (9 * gp.getScale());
        int barWidth = 40 * gp.getScale();                                                                              // The maximum width of the life bar interior (corresponds with maximum life).
        int barHeight = 8 * gp.getScale();                                                                              // The thickness of the life bar.
        drawLifeBar(entity.getLife(), entity.getMaxLife(), barWidth, barHeight, barX, barY);

        // Draw remaining life points text with a shadowed effect.
        textX += barWidth + (13 * gp.getScale());
        textY -= 6 * gp.getScale();
        drawStringShadow(lifeValue, textX, textY, Color.white, 12F * gp.getScale());

        // Reset font back to normal.
        g2.setFont(fontArimo);
    }


    /**
     * Draws a life bar.
     *
     * @param life number of remaining life points
     * @param maxLife maximum number of life points
     * @param maxWidth width of the life bar when full
     * @param height height of the life bar
     * @param barX x-coordinate of the left side of the life bar
     * @param barY y-coordinate of the top side of the life bar
     */
    private void drawLifeBar(int life, int maxLife, int maxWidth, int height, int barX, int barY) {

        double proportion = ((double)(life) / (double)(maxLife));                                                       // The decimal percentage of life that the entity has relative to its maximum life.
        int adjustedWidth = (int)Math.ceil(proportion * maxWidth);                                                      // The width of the filled-in area of the life bar representing remaining life points.

        if (adjustedWidth > maxWidth) {
            adjustedWidth = maxWidth;                                                                                   // Prevent the life bar from over-filling.
        } else if (adjustedWidth < 0) {
            adjustedWidth = 0;                                                                                          // Prevent the life bar from using a negative value.
        }

        // Draw the background of the life bar.
        Color color = new Color(0, 0, 0);
        g2.setColor(color);
        g2.fillRoundRect(barX, barY, maxWidth, height, 0, 0);

        // Draw the interior of the life bar.
        int halfLife = (int)Math.ceil((double)(maxWidth) / (double)(2));
        int eighthLife = (int)Math.ceil((double)(maxWidth) / (double)(8));

        if (adjustedWidth <= eighthLife) {
            color = new Color(255, 46, 102, 220);
        } else if (adjustedWidth <= halfLife) {
            color = new Color(255, 251, 78, 220);
        } else {
            color = new Color(46, 255, 139, 220);
        }

        g2.setColor(color);
        g2.fillRoundRect(barX, barY, adjustedWidth, height, 0, 0);

        // Draw the border of the life bar.
        color = new Color(255, 255, 255);
        g2.setColor(color);
        g2.drawRoundRect(barX, barY, maxWidth, height, 3, 3);
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
        Color nameColor = new Color(121, 149, 255);
        Color quantityColor = new Color(211, 125, 45);
        int textX = (gp.getScreenWidth() / 2) + (10 * gp.getScale());
        int textY = coreMenuEdge + (68 * gp.getScale());

        // Initialize text to draw.
        String name = gp.getPlayer().getInventory().get(inventoryIndexSelected).getName();
        String description = gp.getPlayer().getInventory().get(inventoryIndexSelected).getDescription();
        String quantity = "Quantity: " + gp.getPlayer().getInventory().get(inventoryIndexSelected).getAmount();

        // Draw item name, description, and quantity.
        drawStringShadow(name, textX, textY, nameColor, 16F * gp.getScale() );
        textY += 30 * gp.getScale();
        drawStringShadow(quantity, textX, textY, quantityColor, 16F * gp.getScale());
        textY += 30 * gp.getScale();
        drawStringBlock(description, textX, textY, 40, 18 * gp.getScale(), Color.white, 16F * gp.getScale(), true);
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

                int iconX = (leftIconX + (horizontalSpacing * col)) * gp.getScale();
                int iconY = coreMenuEdge + (topIconY + (verticalSpacing * row)) * gp.getScale();

                if (gp.getPlayer().getInventory().get(itemIndex).isStackable()) {                                       // Switch which icon backdrop to draw based on whether the item is stackable or not.

                    gp.getIconM().draw(g2, 6, iconX, iconY);
                } else {

                    gp.getIconM().draw(g2, 7, iconX, iconY);
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

                int imageX = (leftImageX + (horizontalSpacing * col)) * gp.getScale();
                int imageY = coreMenuEdge + (topImageY + (verticalSpacing * row)) * gp.getScale();
                gp.getPlayer().getInventory().get(itemIndex).draw(g2, imageX, imageY);

                if ((row == itemRowSelected) && (col == itemColSelected)) {

                    int selectorX = imageX - (4 * gp.getScale());
                    int selectorY = imageY - (4 * gp.getScale());
                    gp.getIconM().draw(g2, 8, selectorX, selectorY);                                                    // Draw the item selector on the selected item icon.
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
                    int quantityX = (leftQuantityX + (horizontalSpacing * col)) * gp.getScale();
                    int quantityY = coreMenuEdge + (topQuantityY + (verticalSpacing * row)) * gp.getScale();
                    drawStringShadow(quantity, quantityX, quantityY, Color.white, 14F * gp.getScale());
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
     * Draws the transition screen (fade out to black, perform necessary loading, then fade in from black).
     */
    private void drawTransitionScreen() {

        switch (gp.getActiveTransitionPhase()) {
            case 1:                                                                                                     // Phase 1: Fade screen to black.
                frameCounter++;
                g2.setColor(new Color(0, 0, 0, frameCounter * 10));
                g2.fillRect(0, 0, gp.getScreenWidth(), gp.getScreenHeight());
                if (frameCounter == 25) {
                    frameCounter = 0;                                                                                   // Reset `frameCounter` to prepare it for the second phase.
                    gp.setActiveTransitionPhase(2);                                                                     // Proceed to the next (second) phase of the transition.
                    gp.handleTransitionLoading();                                                                       // Now that the screen's faded to black, perform any necessary loading in the background.
                }
                break;
            case 2:                                                                                                     // Phase 2: Wait on black screen.
                frameCounter++;
                g2.setColor(new Color(0, 0, 0, 255));
                g2.fillRect(0, 0, gp.getScreenWidth(), gp.getScreenHeight());
                if (frameCounter == 30) {                                                                               // At 60 FPS, this will amount to waiting on the black screen for 0.5 seconds.
                    frameCounter = 0;                                                                                   // Reset `frameCounter` to prepare it for the final (third) phase.
                    gp.setActiveTransitionPhase(3);                                                                     // Proceed to the final (third) phase of the transition.
                }
                break;
            case 3:                                                                                                     // Phase 3: Fade from black.
                frameCounter++;
                g2.setColor(new Color(0, 0, 0, (250 - (frameCounter * 10))));
                g2.fillRect(0, 0, gp.getScreenWidth(), gp.getScreenHeight());
                if (frameCounter == 25) {
                    frameCounter = 0;                                                                                   // Reset `frameCounter` to its default value since the transition is complete.
                    gp.concludeTransition();                                                                             // Reset the transition type to neutral and the transition phase to the beginning.
                }
                break;
        }
    }


    /**
     * Draws a sub-menu.
     */
    private void drawSubMenuScreen() {

        // Initialize window location and dimensions.
        // The following information is for the font Arimo (normal/non-bold).
        // Physical total height of the options text at size 20F is 22.
        // Physical ascent (from baseline) is 17.
        // Physical descent (from baseline) is 5.
        // This is why 22 is used in `windowHeight`.
        int windowScreenX = gp.getSubMenuH().getSubMenuScreenX() * gp.getScale();
        int windowScreenY = gp.getSubMenuH().getSubMenuScreenY() * gp.getScale();
        int windowWidth = gp.getSubMenuH().getSubMenuWidth() * gp.getScale();
        int windowHeight = (22 * gp.getSubMenuH().getOptions().size() * gp.getScale()) + (6 * gp.getScale());

        // Draw the interior of the sub-menu window.
        Color color = new Color(0, 0, 0, 180);
        g2.setColor(color);
        g2.fillRoundRect(windowScreenX, windowScreenY, windowWidth, windowHeight, 0, 0);

        // Calculate location of text.
        int optionsScreenX = windowScreenX + (20 * gp.getScale());
        int optionsScreenY = windowScreenY + (20 * gp.getScale());

        // Draw text for each option and selection arrow next to selected option.
        for (int i = 0; i < gp.getSubMenuH().getOptions().size(); i++) {
            drawString(gp.getSubMenuH().getOptions().get(i), optionsScreenX, optionsScreenY, Color.white, 17F * gp.getScale());
            if (i == gp.getSubMenuH().getIndexSelected()) {
                gp.getSelectionA().draw(g2, (optionsScreenX - (14 * gp.getScale())), (optionsScreenY - (11 * gp.getScale())));
            }
            optionsScreenY += 22 * gp.getScale();
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
     */
    private void drawDebug() {

        // Memory usage by Java Runtime.
        Long totalMemoryBytes = Runtime.getRuntime().totalMemory();
        Long freeMemoryBytes = Runtime.getRuntime().freeMemory();
        Long usedMemoryMegabytes = (totalMemoryBytes - freeMemoryBytes) / 1000000;
        String memoryUsage = "JVM Memory Usage: " + usedMemoryMegabytes + " MB";
        drawStringShadow(memoryUsage, 5, 20, Color.white, 18F);

        // Frame rate.
        String fps = "FPS: " + gp.getFpsTracker();
        drawStringShadow(fps, 5, 45, Color.white, 18F);

        // Player column.
        String col = "Player Col: " + (gp.getPlayer().getWorldX() / gp.getTileSize());
        drawStringShadow(col, 5, 70, Color.white, 18F);

        // Player row.
        String row = "Player Row: " + (gp.getPlayer().getWorldY() / gp.getTileSize());
        drawStringShadow(row, 5, 95, Color.white, 18F);

        // Camera center (x).
        String centerX = "Camera Center X: " + (gp.getPlayer().getWorldX() + (gp.getTileSize() / 2) - gp.getPlayer().getCameraOffsetX());
        drawStringShadow(centerX, 5, 120, Color.white, 18F);

        // Camera center (y).
        String centerY = "Camera Center Y: " + (gp.getPlayer().getWorldY() - gp.getPlayer().getCameraOffsetY());
        drawStringShadow(centerY, 5, 145, Color.white, 18F);

        // Camera offset (x).
        String offsetX = "Camera Offset X: " + gp.getPlayer().getCameraOffsetX();
        drawStringShadow(offsetX, 5, 170, Color.white, 18F);

        // Camera offset (y).
        String offsetY = "Camera Offset Y: " + gp.getPlayer().getCameraOffsetY();
        drawStringShadow(offsetY, 5, 195, Color.white, 18F);
    }


    /**
     * Draw text to the screen.
     * Text will be drawn with the currently set font.
     *
     * @param text text to be drawn
     * @param screenX x-coordinate of the text
     * @param screenY y-coordinate of the text
     * @param color text color
     * @param size size at which to draw the text
     */
    private void drawString(String text, int screenX, int screenY, Color color, float size) {

        if (text != null) {

            g2.setFont(g2.getFont().deriveFont(Font.PLAIN, size));
            g2.setColor(color);
            g2.drawString(text, screenX, screenY);
        }
    }


    /**
     * Draws text (color of choice) with a black drop shadow.
     * Text will be printed with the currently set font.
     * Note that this effect works better with thicker fonts.
     *
     * @param text text to be drawn
     * @param screenX x-coordinate of the text
     * @param screenY y-coordinate of the text
     * @param color text color
     * @param size size at which to draw the text
     */
    private void drawStringShadow(String text, int screenX, int screenY, Color color, float size) {

        int shadowTextX = screenX + (1 * gp.getScale());
        int shadowTextY = screenY + (1 * gp.getScale());
        drawString(text, shadowTextX, shadowTextY, Color.black, size);
        drawString(text, screenX, screenY, color, size);
    }


    /**
     * Prints a block of text line-by-line according to a specified line character limit.
     * Text will be printed with the currently set font.
     *
     * @param text complete text to be printed
     * @param screenX x-coordinate of the printed text block
     * @param topScreenY y-coordinate of the top line of the printed text block
     * @param maxLineLength maximum number of characters allowed in a printed line of text
     * @param lineSpacing space between each printed line of text
     * @param color color of the printed text
     * @param size size at which to draw the text
     * @param dropShadow whether a drop shadow should be drawn (true) or not (false)
     */
    private void drawStringBlock(String text, int screenX, int topScreenY, int maxLineLength, int lineSpacing,
                                 Color color, float size, boolean dropShadow) {

        String[] words = text.split(" ");                                                                               // An array of each word in the complete text, split by spaces.
        int wordsIndex = 0;                                                                                             // Track which index of the words array is currently being checked.
        int screenY = topScreenY;

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

                    line = build;                                                                                       // Set the next line of text to be drawn.
                    wordsIndex++;                                                                                       // Iterate to the next word.
                }
            }

            if (dropShadow) {

                drawStringShadow(line, screenX, screenY, color, size);                                                  // Draw the line of text with a drop shadow.
            } else {

                drawString(line, screenX, screenY, Color.white, size);                                                  // Draw the line of text without a drop shadow.
            }

            if (wordsIndex != words.length) {

                screenY += lineSpacing;                                                                                 // Spacing between lines of text.
            }
        }
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
