package ui.support;

import asset.Sprite;
import core.GamePanel;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import render.Renderer;
import render.drawable.Transform;
import render.enumeration.ZIndex;
import utility.LimitedArrayList;
import utility.LimitedLinkedHashMap;

import java.util.HashMap;
import java.util.Set;

/**
 * This class contains logic for rendering inventory menu user interface components.
 * This is part of the primary menu.
 */
public class UiInventoryMenuSupport {

    /*
     * Note that performing calculations once during initialization prevents calculations from being unnecessarily
     * repeated each time these user interface components are added to the render pipeline, hence improving efficiency /
     * memory usage.
     *
     * The goal is to have no / minimal calculations performed during the 'addToRenderPipeline()' method pertaining to
     * layout / positioning of user interface components.
     */

    // FIELDS
    private final GamePanel gp;

    private int maxNumItemSlotCols;

    private int maxNumItemSlotRows;

    private int itemColSelected;

    private int itemRowSelected;

    private int inventoryIndexSelected;

    private Vector4f verticalDividerColor;

    private Vector3f itemNameTextColor;

    private Vector3f itemDescriptionTextColor;

    private Vector3f emptyTextColor;

    private float itemSlotQuantityFontScale;

    private Transform tempWorldTransform;

    private Vector2f tempScreenCoords;

    private String tempItemSlotQuantity;

    private Vector2f itemSlotScreenDimensions;

    private Vector2f topLeftItemSlotScreenCoords;

    private LimitedArrayList<Vector2f> itemSlotScreenCoords;

    private LimitedLinkedHashMap<Integer, Vector2f> itemIconScreenItemSlotOffset;

    private LimitedArrayList<Vector2f> itemSlotQuantityScreenCoords;

    private Vector2f selectorScreenDimensions;

    private float itemDescriptionTextLineScreenVerticalSpacing;

    private float itemDescriptionTextLineScreenMaxWidth;

    private Vector2f itemNameTextScreenCoords;

    private Vector2f itemDescriptionTextScreenCoords;

    private Transform verticalDividerScreenTransform;

    private Vector2f emptyTextScreenCoords;

    private String emptyText;


    // CONSTRUCTOR
    /**
     * Constructs a UiInventoryMenuSupport instance.
     *
     * @param gp GamePanel instance
     */
    public UiInventoryMenuSupport(GamePanel gp) {
        this.gp = gp;
        init();
    }


    // METHODS
    /**
     * Adds inventory menu user interface components to the render pipeline.
     * Note that the contents of the user interface components will automatically reflect their last known states.
     */
    public void addToRenderPipeline() {

        if (gp.getEntityM().getPlayer().getInventory().size() == 0) {

            gp.getCamera().screenCoordsToWorldCoords(emptyTextScreenCoords, tempWorldTransform.position);
            gp.getRenderer().addString(
                    emptyText,
                    tempWorldTransform.position.x,
                    tempWorldTransform.position.y,
                    gp.getUi().getStandardFontScale(),
                    emptyTextColor,
                    gp.getUi().getStandardNormalFont(),
                    ZIndex.SECOND_LAYER);
        } else {

            // Vertical divider.
            gp.getCamera().screenCoordsToWorldCoords(
                    verticalDividerScreenTransform.position, tempWorldTransform.position);
            gp.getCamera().screenDimensionsToWorldDimensions(
                    verticalDividerScreenTransform.scale, tempWorldTransform.scale);
            gp.getRenderer().addRectangle(verticalDividerColor, tempWorldTransform, ZIndex.SECOND_LAYER);

            // Slot icons and content.
            int row = 0;
            int col = 0;
            int itemIndex = 0;
            int numItems = gp.getEntityM().getPlayer().getInventory().size();

            while ((row < maxNumItemSlotRows) && (itemIndex < numItems)) {

                while ((col < maxNumItemSlotCols) && (itemIndex < numItems)) {

                    if (gp.getEntityM().getPlayer().getInventory().get(itemIndex).isStackable()) {

                        // Item slot.
                        gp.getGuiIconM().addToRenderPipeline(
                                6,
                                itemSlotScreenCoords.get(itemIndex).x,
                                itemSlotScreenCoords.get(itemIndex).y
                        );

                        // Item icon.
                        tempScreenCoords.x = itemSlotScreenCoords.get(itemIndex).x + itemIconScreenItemSlotOffset.get(
                                gp.getEntityM().getPlayer().getInventory().get(itemIndex).getItemId()).x;
                        tempScreenCoords.y = itemSlotScreenCoords.get(itemIndex).y + itemIconScreenItemSlotOffset.get(
                                gp.getEntityM().getPlayer().getInventory().get(itemIndex).getItemId()).y;
                        gp.getEntityM().getPlayer().getInventory().get(itemIndex).addToRenderPipeline(
                                tempScreenCoords.x,
                                tempScreenCoords.y
                        );

                        // Item slot quantity.
                        tempItemSlotQuantity =
                                Integer.toString(gp.getEntityM().getPlayer().getInventory().get(itemIndex).getAmount());
                        gp.getUi().addStringShadowToRenderPipeline(
                                tempItemSlotQuantity,
                                itemSlotQuantityScreenCoords.get(itemIndex).x,
                                itemSlotQuantityScreenCoords.get(itemIndex).y,
                                itemSlotQuantityFontScale,
                                new Vector3f(255, 255, 255),
                                gp.getUi().getStandardBoldFont(),
                                ZIndex.SECOND_LAYER
                        );
                    } else {

                        // Item slot.
                        gp.getGuiIconM().addToRenderPipeline(
                                7,
                                itemSlotScreenCoords.get(itemIndex).x,
                                itemSlotScreenCoords.get(itemIndex).y
                        );

                        // Item icon.
                        tempScreenCoords.x = itemSlotScreenCoords.get(itemIndex).x + itemIconScreenItemSlotOffset.get(
                                gp.getEntityM().getPlayer().getInventory().get(itemIndex).getItemId()).x;
                        tempScreenCoords.y = itemSlotScreenCoords.get(itemIndex).y + itemIconScreenItemSlotOffset.get(
                                gp.getEntityM().getPlayer().getInventory().get(itemIndex).getItemId()).y;
                        gp.getEntityM().getPlayer().getInventory().get(itemIndex).addToRenderPipeline(
                                tempScreenCoords.x,
                                tempScreenCoords.y
                        );
                    }

                    // Selector.
                    if ((itemColSelected == col) && (itemRowSelected == row)) {

                        tempScreenCoords.x = itemSlotScreenCoords.get(itemIndex).x
                                - ((selectorScreenDimensions.x - itemSlotScreenDimensions.x) / 2);
                        tempScreenCoords.y = itemSlotScreenCoords.get(itemIndex).y
                                - ((selectorScreenDimensions.y - itemSlotScreenDimensions.y) / 2);
                        gp.getGuiIconM().addToRenderPipeline(
                                8,
                                tempScreenCoords.x,
                                tempScreenCoords.y
                        );
                    }
                    itemIndex++;
                    col++;
                }
                col = 0;
                row++;
            }

            // Selected item content.
            if (inventoryIndexSelected < gp.getEntityM().getPlayer().getInventory().size()) {

                // Selected item name.
                gp.getCamera().screenCoordsToWorldCoords(itemNameTextScreenCoords, tempWorldTransform.position);
                gp.getRenderer().addString(
                        gp.getEntityM().getPlayer().getInventory().get(inventoryIndexSelected).getName(),
                        tempWorldTransform.position.x,
                        tempWorldTransform.position.y,
                        gp.getUi().getStandardFontScale(),
                        itemNameTextColor,
                        gp.getUi().getStandardBoldFont(),
                        ZIndex.SECOND_LAYER);

                // Selected item description.
                gp.getUi().addStringBlockToRenderPipeline(
                        gp.getEntityM().getPlayer().getInventory().get(inventoryIndexSelected).getDescription(),
                        itemDescriptionTextScreenCoords.x,
                        itemDescriptionTextScreenCoords.y,
                        itemDescriptionTextLineScreenMaxWidth,
                        itemDescriptionTextLineScreenVerticalSpacing,
                        gp.getUi().getStandardFontScale(),
                        itemDescriptionTextColor,
                        gp.getUi().getStandardNormalFont(),
                        ZIndex.SECOND_LAYER,
                        true
                );
            }
        }
    }


    /**
     * Set which column of the inventory menu is selected.
     * If the inputted column exceeds the maximum number of inventory columns or would cause the selected item index is
     * out of bounds of the number of items in the player entity's inventory (except for zero), then nothing will
     * happen.
     *
     * @param itemColSelected inventory menu column to select
     * @return whether the column was changed (true) or not (false)
     */
    public boolean setItemColSelected(int itemColSelected) {

        if ((itemColSelected >= 0) && (itemColSelected < maxNumItemSlotCols)) {

            int numItems = gp.getEntityM().getPlayer().getInventory().size();
            int inventoryIndexCandidate = (maxNumItemSlotRows * itemRowSelected) + (itemColSelected);

            if ((inventoryIndexCandidate < numItems) || (itemColSelected == 0)) {

                this.itemColSelected = itemColSelected;
                inventoryIndexSelected = (maxNumItemSlotRows * itemRowSelected) + (itemColSelected);
                return true;
            }
            return false;
        }
        return false;
    }


    /**
     * Set which row of the inventory menu is selected.
     * If the inputted row exceeds the maximum number of inventory rows or would cause the selected item index is out of
     * bounds of the number of items in the player entity's inventory (except for zero), then nothing will
     * happen.
     *
     * @param itemRowSelected inventory menu row to select
     * @return whether the row was changed (true) or not (false)
     */
    public boolean setItemRowSelected(int itemRowSelected) {

        if ((itemRowSelected >= 0) && (itemRowSelected < maxNumItemSlotRows)) {

            int numItems = gp.getEntityM().getPlayer().getInventory().size();
            int inventoryIndexCandidate = (maxNumItemSlotRows * itemRowSelected) + (itemColSelected);

            if ((inventoryIndexCandidate < numItems) || (itemRowSelected == 0)) {

                this.itemRowSelected = itemRowSelected;
                inventoryIndexSelected = (maxNumItemSlotRows * itemRowSelected) + (itemColSelected);
                return true;
            }
            return false;
        }
        return false;
    }


    /**
     * Initializes inventory menu user interface components that will not change while the game is running.
     * These user interface components are the "core" aspects of the inventory menu layout, such as the positioning of
     * the slot icons.
     */
    private void init() {

        // Selection management.
        maxNumItemSlotRows = 5;                                                                                         // Maximum number of rows of item slot (i.e., not the number of items slots in a row).
        maxNumItemSlotCols = 5;                                                                                         // Maximum number of columns of item slot (i.e., not the number of items slots in a column).

        itemColSelected = 0;
        itemRowSelected = 0;
        inventoryIndexSelected = 0;

        // Colors.
        verticalDividerColor = new Vector4f(147, 182, 220, 255);
        itemNameTextColor = new Vector3f(121, 255, 218);
        itemDescriptionTextColor = new Vector3f(255, 255, 255);
        emptyTextColor = new Vector3f(160, 160, 160);

        // Text sizing.
        itemSlotQuantityFontScale = 0.12f;
        float standardNormalCharWorldHeight = gp.getRenderer().getFont(gp.getUi().getStandardNormalFont())
                .getCharacter('A').getHeight() * gp.getUi().getStandardFontScale();                                     // It doesn't matter which character is used, since all characters in a font have the same height.
        float standardBoldCharWorldHeight = gp.getRenderer().getFont(gp.getUi().getStandardBoldFont())
                .getCharacter('A').getHeight() * gp.getUi().getStandardFontScale();                                     // It doesn't matter which character is used, since all characters in a font have the same height.
        float standardBoldCharScreenHeight =
                gp.getCamera().worldHeightToScreenHeight(standardBoldCharWorldHeight);

        // Temporary coordinates.
        Vector2f tempWorldCoords = new Vector2f(0.0f, 0.0f);                                                            // Values are placeholders (will change while rendering).
        Vector2f tempWorldDimensions = new Vector2f(0.0f, 0.0f);                                                        // Values are placeholders (will change while rendering).
        tempWorldTransform = new Transform(tempWorldCoords, tempWorldDimensions);
        tempScreenCoords = new Vector2f(0.0f, 0.0f);                                                                    // Values are placeholders (will change while rendering).

        // General setup.
        float headerDividerScreenPrimaryWindowLeftAdjustment = gp.getUiPrimaryMenuFrameS().getHeaderDividerScreenX()
                - gp.getUiPrimaryMenuFrameS().getPrimaryWindowScreenX();
        float headerDividerWorldPrimaryWindowLeftAdjustment =
                gp.getCamera().screenWidthToWorldWidth(headerDividerScreenPrimaryWindowLeftAdjustment);

        // Vertical divider coordinates and dimensions.
        float verticalDividerScreenHeaderDividerBottomAdjustment =
                gp.getCamera().worldHeightToScreenHeight(headerDividerWorldPrimaryWindowLeftAdjustment) / 2;
        float verticalDividerScreenHeight =
                (gp.getUiPrimaryMenuFrameS().getPrimaryWindowScreenY()
                        + gp.getUiPrimaryMenuFrameS().getPrimaryWindowScreenHeight()) -
                (gp.getUiPrimaryMenuFrameS().getHeaderDividerScreenY()
                        + gp.getUiPrimaryMenuFrameS().getHeaderDividerScreenHeight()) -
                (verticalDividerScreenHeaderDividerBottomAdjustment * 2);
        float verticalDividerWorldWidth = 0.96f;
        float verticalDividerScreenWidth = gp.getCamera().worldWidthToScreenWidth(verticalDividerWorldWidth);
        float verticalDividerScreenX = gp.getUiPrimaryMenuFrameS().getPrimaryWindowScreenX()
                + ((gp.getUiPrimaryMenuFrameS().getPrimaryWindowScreenWidth() / 34) * 16)
                - (verticalDividerScreenWidth / 2);
        float verticalDividerScreenY = gp.getUiPrimaryMenuFrameS().getHeaderDividerScreenY()
                + gp.getUiPrimaryMenuFrameS().getHeaderDividerScreenHeight()
                + verticalDividerScreenHeaderDividerBottomAdjustment;
        Vector2f verticalDividerScreenCoords = new Vector2f(verticalDividerScreenX, verticalDividerScreenY);
        Vector2f verticalDividerScreenDimensions = new Vector2f(verticalDividerScreenWidth, verticalDividerScreenHeight);
        verticalDividerScreenTransform = new Transform(verticalDividerScreenCoords, verticalDividerScreenDimensions);

        // Item slot dimensions.
        float itemSlotWorldWidth = gp.getGuiIconM().getIconById(6).getNativeSpriteWidth();
        float itemSlotScreenWidth = gp.getCamera().worldWidthToScreenWidth(itemSlotWorldWidth);
        float itemSlotWorldHeight = gp.getGuiIconM().getIconById(6).getNativeSpriteHeight();
        float itemSlotScreenHeight = gp.getCamera().worldHeightToScreenHeight(itemSlotWorldHeight);
        itemSlotScreenDimensions = new Vector2f(itemSlotScreenWidth, itemSlotScreenHeight);

        // Item slot coordinates (setup).
        float topLeftItemSlotScreenX =
                gp.getUiPrimaryMenuFrameS().getPrimaryWindowScreenX() + headerDividerScreenPrimaryWindowLeftAdjustment;
        float topLeftItemSlotScreenY = verticalDividerScreenY;
        topLeftItemSlotScreenCoords = new Vector2f(topLeftItemSlotScreenX, topLeftItemSlotScreenY);

        float itemSlotScreenTotalRowWidth = verticalDividerScreenX
                - headerDividerScreenPrimaryWindowLeftAdjustment
                - gp.getUiPrimaryMenuFrameS().getPrimaryWindowScreenX();
        float itemSlotScreenHorizontalSpacing =
                (itemSlotScreenTotalRowWidth - (maxNumItemSlotCols * itemSlotScreenWidth)) / (maxNumItemSlotCols + 1);
        float itemSlotWorldVerticalSpacing = gp.getCamera().screenWidthToWorldWidth(itemSlotScreenHorizontalSpacing);
        float itemSlotScreenVerticalSpacing = gp.getCamera().worldHeightToScreenHeight(itemSlotWorldVerticalSpacing);
        itemSlotScreenCoords = new LimitedArrayList<>(maxNumItemSlotRows * maxNumItemSlotCols);

        // Item slot quantity coordinates (setup).
        float itemSlotQuantityWorldSlotOffsetX = itemSlotWorldWidth * 0.9f;
        float itemSlotQuantityWorldSlotOffsetY = itemSlotWorldHeight * 0.9f;
        float itemSlotQuantityScreenSlotOffsetX =
                gp.getCamera().worldWidthToScreenWidth(itemSlotQuantityWorldSlotOffsetX);
        float itemSlotQuantityScreenSlotOffsetY =
                gp.getCamera().worldHeightToScreenHeight(itemSlotQuantityWorldSlotOffsetY);
        itemSlotQuantityScreenCoords = new LimitedArrayList<>(maxNumItemSlotRows * maxNumItemSlotCols);

        // Item slot and item slot quantity coordinates (assign).
        float itemSlotScreenX;
        float itemSlotScreenY;
        float itemSlotQuantityScreenX;
        float itemSlotQuantityScreenY;

        int row = 0;
        int col = 0;

        while (row < maxNumItemSlotRows) {

            itemSlotScreenY = topLeftItemSlotScreenCoords.y
                    + ((itemSlotScreenDimensions.y + itemSlotScreenVerticalSpacing) * row);

            while (col < maxNumItemSlotCols) {

                itemSlotScreenX = topLeftItemSlotScreenCoords.x
                        + ((itemSlotScreenDimensions.x + itemSlotScreenHorizontalSpacing) * col);
                itemSlotScreenCoords.add(new Vector2f(itemSlotScreenX, itemSlotScreenY));

                itemSlotQuantityScreenX = itemSlotScreenX + itemSlotQuantityScreenSlotOffsetX;
                itemSlotQuantityScreenY = itemSlotScreenY + itemSlotQuantityScreenSlotOffsetY;
                itemSlotQuantityScreenCoords.add(new Vector2f(itemSlotQuantityScreenX, itemSlotQuantityScreenY));

                col++;
            }
            col = 0;
            row++;
        }

        // Item icon coordinates.
        HashMap<Integer, Sprite> itemSprites = gp.getItemM().getAllItemSprites();
        Set<Integer> keySet = itemSprites.keySet();
        Integer[] keyArray = keySet.toArray(new Integer[keySet.size()]);
        itemIconScreenItemSlotOffset = new LimitedLinkedHashMap<>(keyArray.length);

        float itemIconWorldWidth;
        float itemIconWorldHeight;
        float itemIconScreenWidth;
        float itemIconScreenHeight;
        float itemIconScreenSlotIconOffsetX;
        float itemIconScreenSlotIconOffsetY;

        for (int key : keyArray) {

            itemIconWorldWidth = itemSprites.get(key).getNativeWidth();
            itemIconWorldHeight = itemSprites.get(key).getNativeHeight();
            itemIconScreenWidth = gp.getCamera().worldWidthToScreenWidth(itemIconWorldWidth);
            itemIconScreenHeight = gp.getCamera().worldHeightToScreenHeight(itemIconWorldHeight);

            itemIconScreenSlotIconOffsetX = (itemSlotScreenDimensions.x - itemIconScreenWidth) / 2;                     // Offset from slot icon coords to centers item icon horizontally in item slot.
            itemIconScreenSlotIconOffsetY = (itemSlotScreenDimensions.y - itemIconScreenHeight) / 2;                    // Offset from slot icon coords to centers item icon vertically in item slot.
            itemIconScreenItemSlotOffset.put(
                    key,
                    new Vector2f(itemIconScreenSlotIconOffsetX, itemIconScreenSlotIconOffsetY)
            );
        }

        // Selector coordinates and dimensions.
        float selectorWorldWidth = gp.getGuiIconM().getIconById(8).getNativeSpriteWidth();
        float selectorScreenWidth = gp.getCamera().worldWidthToScreenWidth(selectorWorldWidth);
        float selectorWorldHeight = gp.getGuiIconM().getIconById(8).getNativeSpriteHeight();
        float selectorScreenHeight = gp.getCamera().worldHeightToScreenHeight(selectorWorldHeight);
        selectorScreenDimensions = new Vector2f(selectorScreenWidth, selectorScreenHeight);

        // Selected item name coordinates.
        float itemNameTextScreenX =
                verticalDividerScreenX + verticalDividerScreenWidth + (itemSlotScreenHorizontalSpacing * 2);
        float itemNameTextScreenY = verticalDividerScreenY;
        itemNameTextScreenCoords = new Vector2f(itemNameTextScreenX, itemNameTextScreenY);

        // Selected item description coordinates.
        float itemDescriptionTextLineWorldVerticalSpacing = standardNormalCharWorldHeight * 1.75f;
        itemDescriptionTextLineScreenVerticalSpacing =
                gp.getCamera().worldHeightToScreenHeight(itemDescriptionTextLineWorldVerticalSpacing);

        itemDescriptionTextLineScreenMaxWidth = 1 - itemNameTextScreenX - headerDividerScreenPrimaryWindowLeftAdjustment
                - ((1 - gp.getUiPrimaryMenuFrameS().getPrimaryWindowScreenWidth()) / 2);

        float itemDescriptionTextScreenX = itemNameTextScreenX;
//        float itemDescriptionTextScreenY = itemNameTextScreenY + standardBoldCharScreenHeight
//                + verticalDividerScreenHeaderDividerBottomAdjustment;
        float itemDescriptionTextScreenY = itemNameTextScreenY + standardBoldCharScreenHeight * 2.25f;
        itemDescriptionTextScreenCoords = new Vector2f(itemDescriptionTextScreenX, itemDescriptionTextScreenY);

        // Empty text coordinates and content.
        emptyText = "(Empty)";
        float emptyTextScreenWidth = gp.getUi().calculateStringScreenWidth(
                emptyText,
                gp.getUi().getStandardFontScale(),
                gp.getUi().getStandardNormalFont()
        );
        float emptyTextScreenX = gp.getUiPrimaryMenuFrameS().getPrimaryWindowScreenX()
                + (gp.getUiPrimaryMenuFrameS().getPrimaryWindowScreenWidth() / 2) - (emptyTextScreenWidth / 2);
        float emptyTextScreenHeaderDividerBottomAdjustment = verticalDividerScreenHeaderDividerBottomAdjustment;
        float emptyTextScreenY = gp.getUiPrimaryMenuFrameS().getHeaderDividerScreenY()
                + gp.getUiPrimaryMenuFrameS().getHeaderDividerScreenHeight()
                + emptyTextScreenHeaderDividerBottomAdjustment;
        emptyTextScreenCoords = new Vector2f(emptyTextScreenX, emptyTextScreenY);
    }


    // GETTERS
    public int getMaxNumItemSlotCols() {
        return maxNumItemSlotCols;
    }

    public int getMaxNumItemSlotRows() {
        return maxNumItemSlotRows;
    }

    public int getItemColSelected() {
        return itemColSelected;
    }

    public int getItemRowSelected() {
        return itemRowSelected;
    }

    public int getInventoryIndexSelected() {
        return inventoryIndexSelected;
    }
}
