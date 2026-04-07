package ui.support;

import asset.Sprite;
import core.GamePanel;
import org.joml.Vector2f;
import org.joml.Vector3f;
import render.Renderer;
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

    private int maxNumItemSlotsCol;

    private int maxNumItemSlotsRow;

    private int itemColSelected;

    private int itemRowSelected;

    private int inventoryIndexSelected;

    private Vector3f itemNameTextColor;

//    private Vector3f itemQuantityTextColor;

    private Vector3f itemDescriptionTextColor;

    private float itemSlotQuantityFontScale;

    private Vector2f tempWorldCoords;

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

//    private Vector2f itemQuantityTextScreenCoords;

    private Vector2f itemDescriptionTextScreenCoords;


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
     *
     * @param renderer Renderer instance
     */
    public void addToRenderPipeline(Renderer renderer) {

        // Slot icons and content.
        int row = 0;
        int col = 0;
        int itemIndex = 0;
        int numItems = gp.getEntityM().getPlayer().getInventory().size();

        while ((row < maxNumItemSlotsRow) && (itemIndex < numItems)) {

            while ((col < maxNumItemSlotsCol) && (itemIndex < numItems)) {

                if (gp.getEntityM().getPlayer().getInventory().get(itemIndex).isStackable()) {

                    // Item slot.
                    gp.getGuiIconM().addToRenderPipeline(
                            renderer,
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
                            renderer,
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
                            renderer,
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
                            renderer,
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
                            renderer,
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
            gp.getCamera().screenCoordsToWorldCoords(itemNameTextScreenCoords, tempWorldCoords);
            renderer.addString(
                    gp.getEntityM().getPlayer().getInventory().get(inventoryIndexSelected).getName(),
                    tempWorldCoords.x,
                    tempWorldCoords.y,
                    gp.getUi().getStandardFontScale(),
                    itemNameTextColor,
                    gp.getUi().getStandardBoldFont(),
                    ZIndex.SECOND_LAYER);

            // Selected item quantity.
//            gp.getCamera().screenCoordsToWorldCoords(itemQuantityTextScreenCoords, tempWorldCoords);
//            renderer.addString(
//                    "Quantity: " + gp.getEntityM().getPlayer().getInventory().get(inventoryIndexSelected).getAmount(),
//                    tempWorldCoords.x,
//                    tempWorldCoords.y,
//                    gp.getUi().getStandardFontScale(),
//                    itemQuantityTextColor,
//                    gp.getUi().getStandardBoldFont(),
//                    ZIndex.SECOND_LAYER);

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


    /**
     * Set which column of the inventory menu is selected.
     * If the inputted column exceeds the maximum number of inventory columns or would cause the selected item index is
     * out of bounds of the number of items in the player entity's inventory (except for zero), then nothing will
     * happen.
     *
     * @param itemColSelected inventory menu column to select
     */
    public void setItemColSelected(int itemColSelected) {

        if ((itemColSelected >= 0) && (itemColSelected < maxNumItemSlotsCol)) {

            int numItems = gp.getEntityM().getPlayer().getInventory().size();
            int inventoryIndexCandidate = (maxNumItemSlotsRow * itemRowSelected) + (itemColSelected);

            if ((inventoryIndexCandidate < numItems) || (itemColSelected == 0)) {

                this.itemColSelected = itemColSelected;
                inventoryIndexSelected = (maxNumItemSlotsRow * itemRowSelected) + (itemColSelected);
            }
        }
    }


    /**
     * Set which row of the inventory menu is selected.
     * If the inputted row exceeds the maximum number of inventory rows or would cause the selected item index is out of
     * bounds of the number of items in the player entity's inventory (except for zero), then nothing will
     * happen.
     *
     * @param itemRowSelected inventory menu row to select
     */
    public void setItemRowSelected(int itemRowSelected) {

        if ((itemRowSelected >= 0) && (itemRowSelected < maxNumItemSlotsRow)) {

            int numItems = gp.getEntityM().getPlayer().getInventory().size();
            int inventoryIndexCandidate = (maxNumItemSlotsRow * itemRowSelected) + (itemColSelected);

            if ((inventoryIndexCandidate < numItems) || (itemRowSelected == 0)) {

                this.itemRowSelected = itemRowSelected;
                inventoryIndexSelected = (maxNumItemSlotsRow * itemRowSelected) + (itemColSelected);
            }
        }
    }


    /**
     * Initializes inventory menu user interface components that will not change while the game is running.
     * These user interface components are the "core" aspects of the inventory menu layout, such as the positioning of
     * the slot icons.
     */
    private void init() {

        // Selection management.
        maxNumItemSlotsRow = 5;
        maxNumItemSlotsCol = 5;

        itemColSelected = 0;
        itemRowSelected = 0;
        inventoryIndexSelected = 0;

        // Colors.
        itemNameTextColor = new Vector3f(121, 149, 255);
//        itemQuantityTextColor = new Vector3f(244, 154, 45);
        itemDescriptionTextColor = new Vector3f(255, 255, 255);

        // Text sizing.
        itemSlotQuantityFontScale = 0.12f;

        // Temporary coordinates.
        tempWorldCoords = new Vector2f(0.0f, 0.0f);                                                                     // Values are placeholders (will change while rendering).
        tempScreenCoords = new Vector2f(0.0f, 0.0f);                                                                    // Values are placeholders (will change while rendering).

        // Item slot dimensions.
        float itemSlotWorldWidth = gp.getGuiIconM().getIconById(6).getNativeSpriteWidth();
        float itemSlotScreenWidth = gp.getCamera().worldWidthToScreenWidth(itemSlotWorldWidth);
        float itemSlotWorldHeight = gp.getGuiIconM().getIconById(6).getNativeSpriteHeight();
        float itemSlotScreenHeight = gp.getCamera().worldHeightToScreenHeight(itemSlotWorldHeight);
        itemSlotScreenDimensions = new Vector2f(itemSlotScreenWidth, itemSlotScreenHeight);

        // Item slot coordinates (setup).
        float itemSlotWorldHorizontalSpacing = 23.0f;
        float itemSlotScreenHorizontalSpacing = gp.getCamera().worldWidthToScreenWidth(itemSlotWorldHorizontalSpacing);
        float itemSlotWorldVerticalSpacing = 21.6f;
        float itemSlotScreenVerticalSpacing = gp.getCamera().worldHeightToScreenHeight(itemSlotWorldVerticalSpacing);

        float itemSlotScreenPrimaryWindowLeftAdjustment = gp.getUiPrimaryMenuFrameS().getHeaderDividerScreenX()
                - gp.getUiPrimaryMenuFrameS().getPrimaryWindowScreenX();
        float itemSlotWorldPrimaryWindowLeftAdjustment =
                gp.getCamera().screenWidthToWorldWidth(itemSlotScreenPrimaryWindowLeftAdjustment);
        float headerDividerScreenHeight = gp.getUiPrimaryMenuFrameS().getHeaderDividerScreenHeight();
        float itemSlotScreenHeaderDividerBottomAdjustment =
                gp.getCamera().worldHeightToScreenHeight(itemSlotWorldPrimaryWindowLeftAdjustment) / 2;
        float topLeftItemSlotScreenX =
                gp.getUiPrimaryMenuFrameS().getPrimaryWindowScreenX() + itemSlotScreenPrimaryWindowLeftAdjustment;
        float topLeftItemSlotScreenY = gp.getUiPrimaryMenuFrameS().getHeaderDividerScreenY()
                + headerDividerScreenHeight + itemSlotScreenHeaderDividerBottomAdjustment;
        topLeftItemSlotScreenCoords = new Vector2f(topLeftItemSlotScreenX, topLeftItemSlotScreenY);

        itemSlotScreenCoords = new LimitedArrayList<>(maxNumItemSlotsRow * maxNumItemSlotsCol);

        // Item slot quantity coordinates (setup).
        float itemSlotQuantityWorldSlotOffsetX = itemSlotWorldWidth * 0.9f;
        float itemSlotQuantityWorldSlotOffsetY = itemSlotWorldHeight * 0.9f;
        float itemSlotQuantityScreenSlotOffsetX =
                gp.getCamera().worldWidthToScreenWidth(itemSlotQuantityWorldSlotOffsetX);
        float itemSlotQuantityScreenSlotOffsetY =
                gp.getCamera().worldHeightToScreenHeight(itemSlotQuantityWorldSlotOffsetY);

        itemSlotQuantityScreenCoords = new LimitedArrayList<>(maxNumItemSlotsRow * maxNumItemSlotsCol);

        // Item slot and item slot quantity coordinates (assign).
        float itemSlotScreenX;
        float itemSlotScreenY;
        float itemSlotQuantityScreenX;
        float itemSlotQuantityScreenY;

        int row = 0;
        int col = 0;

        while (row < maxNumItemSlotsRow) {

            itemSlotScreenY = topLeftItemSlotScreenCoords.y
                    + ((itemSlotScreenDimensions.y + itemSlotScreenVerticalSpacing) * row);

            while (col < maxNumItemSlotsCol) {

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

        // Selected item content (setup).
        float itemTextSectionWorldVerticalSpacing = 38.9f;
        float itemTextSectionScreenVerticalSpacing =
                gp.getCamera().worldHeightToScreenHeight(itemTextSectionWorldVerticalSpacing);

        // Selected item name.
        float itemNameTextScreenX = topLeftItemSlotScreenCoords.x
                + (itemSlotScreenDimensions.x * maxNumItemSlotsCol)
                + (itemSlotScreenHorizontalSpacing * (maxNumItemSlotsCol - 1))
                + itemSlotScreenPrimaryWindowLeftAdjustment;
        float itemNameTextScreenY = gp.getUiPrimaryMenuFrameS().getHeaderDividerScreenY()
                + headerDividerScreenHeight + itemSlotScreenHeaderDividerBottomAdjustment;
        itemNameTextScreenCoords = new Vector2f(itemNameTextScreenX, itemNameTextScreenY);

        // Selected item quantity.
//        float itemQuantityTextScreenX = itemNameTextScreenX;
//        float itemQuantityTextScreenY = itemNameTextScreenY + itemTextSectionScreenVerticalSpacing;
//        itemQuantityTextScreenCoords = new Vector2f(itemQuantityTextScreenX, itemQuantityTextScreenY);

        // Selected item description.
        float itemDescriptionTextLineWorldVerticalSpacing = 28.0f;
        itemDescriptionTextLineScreenVerticalSpacing =
                gp.getCamera().worldHeightToScreenHeight(itemDescriptionTextLineWorldVerticalSpacing);

        itemDescriptionTextLineScreenMaxWidth = 1 - itemNameTextScreenX - itemSlotScreenPrimaryWindowLeftAdjustment
                - ((1 - gp.getUiPrimaryMenuFrameS().getPrimaryWindowScreenWidth()) / 2);

        float itemDescriptionTextScreenX = itemNameTextScreenX;
        float itemDescriptionTextScreenY = itemNameTextScreenY + itemTextSectionScreenVerticalSpacing;
        itemDescriptionTextScreenCoords = new Vector2f(itemDescriptionTextScreenX, itemDescriptionTextScreenY);
    }


    // GETTERS
    public int getMaxNumItemSlotsCol() {
        return maxNumItemSlotsCol;
    }

    public int getMaxNumItemSlotsRow() {
        return maxNumItemSlotsRow;
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
