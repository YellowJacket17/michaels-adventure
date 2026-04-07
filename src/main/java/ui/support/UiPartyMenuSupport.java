package ui.support;

import core.GamePanel;
import entity.EntityBase;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import render.Renderer;
import render.drawable.Transform;
import render.enumeration.ZIndex;
import ui.enumeration.PartyMenuSlot;
import utility.LimitedArrayList;

import java.util.ArrayList;
import java.util.Set;

/**
 * This class contains logic for rendering party menu user interface components.
 * This is part of the primary menu.
 */
public class UiPartyMenuSupport {

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

    private PartyMenuSlot partySlotSelected;

    private int partyMenuScrollLevel;

    private float slotIconContentsFontScale;

    private Transform tempWorldTransform;

    private Vector3f textColor;

    private Vector4f scrollIconInactiveColor;

    private Vector4f scrollIconActiveColor;

    private float slotIconScreenCenterY;

    private Vector2f slotIconScreenDimensions;

    private float slotIconScreenVerticalSpacing;

    private LimitedArrayList<Vector2f> slotIconScreenCoords;

    private LimitedArrayList<Vector2f> entityIconScreenCoords;

    private LimitedArrayList<Vector2f> entityNameTextScreenCoords;

    private LimitedArrayList<Vector2f> entityLifeBarScreenCoords;

    private LimitedArrayList<Vector2f> entityLifeLabelTextScreenCoords;

    private LimitedArrayList<Vector2f> entityLifeNumberTextScreenCoords;

    private LimitedArrayList<Vector2f> entitySkillBarScreenCoords;

    private LimitedArrayList<Vector2f> entitySkillLabelTextScreenCoords;

    private LimitedArrayList<Vector2f> entitySkillNumberTextScreenCoords;

    private float lifeBarScreenWidth;

    private float skillBarScreenWidth;

    private float scrollIconScreenVerticalSpacing;

    private Vector2f scrollIconScreenInactiveDimensions;

    private Vector2f scrollIconScreenActiveDimensions;

    private ArrayList<Transform> scrollIconActiveTransforms;

    private ArrayList<Transform> scrollIconInactiveTransforms;


    // CONSTRUCTOR
    /**
     * Constructs a UiPartyMenuSupport instance.
     *
     * @param gp GamePanel instance
     */
    public UiPartyMenuSupport(GamePanel gp) {
        this.gp = gp;
        init();
    }


    // METHODS
    /**
     * Refreshes party menu user interface components that may change while the game is running.
     * These user interface elements are the "core" aspects of the party menu layout.
     * In this case, a refresh must be done if the number of party members changes.
     * Note that a refresh is not necessary if the contents of the user interface components change (e.g., which
     * entity is occupying a slot icon or the corresponding summary).
     */
    public void refresh() {

        // Reset coordinates and dimensions.
        slotIconScreenCoords.clear();
        slotIconScreenCoords.clear();
        entityIconScreenCoords.clear();
        entityNameTextScreenCoords.clear();
        entityLifeBarScreenCoords.clear();
        entityLifeLabelTextScreenCoords.clear();
        entityLifeNumberTextScreenCoords.clear();
        entitySkillBarScreenCoords.clear();
        entitySkillLabelTextScreenCoords.clear();
        entitySkillNumberTextScreenCoords.clear();
        scrollIconActiveTransforms.clear();
        scrollIconInactiveTransforms.clear();

        // Slot icon coordinates and content.
        float slotIconScreenX = gp.getUiPrimaryMenuFrameS().getHeaderDividerScreenX();
        float slotIconScreenY;

        switch (gp.getEntityM().getParty().size()) {
            case 0:
                slotIconScreenY = slotIconScreenCenterY - (slotIconScreenDimensions.y / 2);
                slotIconScreenCoords.add(new Vector2f(slotIconScreenX, slotIconScreenY));
                refreshSlotIconContents(PartyMenuSlot.SLOT_0.getValue());
                break;
            case 1:
                slotIconScreenY =
                        slotIconScreenCenterY - (slotIconScreenDimensions.y) - (slotIconScreenVerticalSpacing / 2);
                slotIconScreenCoords.add(new Vector2f(slotIconScreenX, slotIconScreenY));
                refreshSlotIconContents(PartyMenuSlot.SLOT_0.getValue());
                slotIconScreenY += slotIconScreenVerticalSpacing + slotIconScreenDimensions.y;
                slotIconScreenCoords.add(new Vector2f(slotIconScreenX, slotIconScreenY));
                refreshSlotIconContents(PartyMenuSlot.SLOT_1.getValue());
                break;
            default:
                slotIconScreenY =
                        slotIconScreenCenterY - (slotIconScreenDimensions.y * 1.5f) - (slotIconScreenVerticalSpacing);
                slotIconScreenCoords.add(new Vector2f(slotIconScreenX, slotIconScreenY));
                refreshSlotIconContents(PartyMenuSlot.SLOT_0.getValue());
                slotIconScreenY += slotIconScreenVerticalSpacing + slotIconScreenDimensions.y;
                slotIconScreenCoords.add(new Vector2f(slotIconScreenX, slotIconScreenY));
                refreshSlotIconContents(PartyMenuSlot.SLOT_1.getValue());
                slotIconScreenY += slotIconScreenVerticalSpacing + slotIconScreenDimensions.y;
                slotIconScreenCoords.add(new Vector2f(slotIconScreenX, slotIconScreenY));
                refreshSlotIconContents(PartyMenuSlot.SLOT_2.getValue());
        }

        // Scroll icon coordinates.
        int numScrollIcons = gp.getEntityM().getParty().size() + 1;

        float scrollIconScreenInactiveX =
                ((gp.getUiPrimaryMenuFrameS().getPrimaryWindowScreenX() + slotIconScreenX) / 2)
                        - (scrollIconScreenInactiveDimensions.x / 2);

        float scrollIconScreenY = slotIconScreenCenterY - (((scrollIconScreenInactiveDimensions.y * numScrollIcons)
                + (scrollIconScreenVerticalSpacing * (numScrollIcons - 1))) / 2);                                       // Topmost scroll icon.

        float scrollIconScreenActiveOffsetX =
                (scrollIconScreenActiveDimensions.x / 2) - (scrollIconScreenInactiveDimensions.x / 2);
        float scrollIconScreenActiveOffsetY =
                (scrollIconScreenActiveDimensions.y / 2) - (scrollIconScreenInactiveDimensions.y / 2);

        for (int i = 0; i < numScrollIcons; i++) {

            scrollIconActiveTransforms.add(new Transform(
                    new Vector2f(
                            scrollIconScreenInactiveX - scrollIconScreenActiveOffsetX,
                            scrollIconScreenY - scrollIconScreenActiveOffsetY
                    ),
                    new Vector2f(scrollIconScreenActiveDimensions.x, scrollIconScreenActiveDimensions.y)
            ));

            scrollIconInactiveTransforms.add(new Transform(
                    new Vector2f(scrollIconScreenInactiveX, scrollIconScreenY),
                    new Vector2f(scrollIconScreenInactiveDimensions.x, scrollIconScreenInactiveDimensions.y)
            ));

            scrollIconScreenY += scrollIconScreenVerticalSpacing + scrollIconScreenInactiveDimensions.y;
        }

        // Dirty flag.
        dirty = false;
    }


    /**
     * Adds party menu user interface components to the render pipeline.
     * Note that the contents of the user interface components will automatically reflect their latest states.
     *
     * @param renderer Renderer instance
     */
    public void addToRenderPipeline(Renderer renderer) {

        // Slot icons and content.
        // TODO : Consider refactoring the key array in the future (heavy on memory usage each frame).
        Set<Integer> tempEntityKeySet = gp.getEntityM().getParty().keySet();
        Integer[] entityKeyArray = tempEntityKeySet.toArray(new Integer[tempEntityKeySet.size()]);

        for (int i = 0; i < slotIconScreenCoords.size(); i++) {

            if (i == 0) {

                gp.getGuiIconM().addToRenderPipeline(
                        renderer, 3, slotIconScreenCoords.get(i).x, slotIconScreenCoords.get(0).y);

                if (partyMenuScrollLevel == 0) {

                    addSlotIconContentToRenderPipeline(
                            renderer,
                            i,
                            gp.getEntityM().getPlayer()
                    );
                } else {

                    addSlotIconContentToRenderPipeline(
                            renderer,
                            i,
                            gp.getEntityM().getParty().get(entityKeyArray[partyMenuScrollLevel - 1])
                    );
                }
            } else if (i == 1) {

                gp.getGuiIconM().addToRenderPipeline(
                        renderer, 4, slotIconScreenCoords.get(i).x, slotIconScreenCoords.get(1).y);

                addSlotIconContentToRenderPipeline(
                        renderer,
                        i,
                        gp.getEntityM().getParty().get(entityKeyArray[partyMenuScrollLevel])
                );
            } else if (i == 2) {

                gp.getGuiIconM().addToRenderPipeline(
                        renderer, 5, slotIconScreenCoords.get(i).x, slotIconScreenCoords.get(2).y);

                addSlotIconContentToRenderPipeline(
                        renderer,
                        i,
                        gp.getEntityM().getParty().get(entityKeyArray[partyMenuScrollLevel + 1])
                );
            } else {

                break;                                                                                                  // Just in case.
            }
        }

        // Scroll icons.
        for (int i = 0; i < scrollIconActiveTransforms.size(); i++) {                                                   // Doesn't matter whether active or inactive is used since both will be of the same size.

            if (i == (partySlotSelected.getValue() + partyMenuScrollLevel)) {

                gp.getCamera().screenCoordsToWorldCoords(
                        scrollIconActiveTransforms.get(i).position, tempWorldTransform.position);
                gp.getCamera().screenDimensionsToWorldDimensions(
                        scrollIconActiveTransforms.get(i).scale, tempWorldTransform.scale);
                renderer.addRectangle(
                        scrollIconActiveColor,
                        tempWorldTransform,
                        ZIndex.SECOND_LAYER);
            } else {

                gp.getCamera().screenCoordsToWorldCoords(
                        scrollIconInactiveTransforms.get(i).position, tempWorldTransform.position);
                gp.getCamera().screenDimensionsToWorldDimensions(
                        scrollIconInactiveTransforms.get(i).scale, tempWorldTransform.scale);
                renderer.addRectangle(
                        scrollIconInactiveColor,
                        tempWorldTransform,
                        ZIndex.SECOND_LAYER);
            }
        }
    }


    /**
     * Retrieves the party member (including the player entity) that is selected in the party menu screen.
     * Returns '-1' if the party menu scroll level and selected slot combination produces an entry beyond the number of
     * party members.
     *
     * @return ID of selected entity
     */
    public int getSelectedPartyMenuEntity() {

        int selectedEntityId;

        if ((partyMenuScrollLevel == 0) && (partySlotSelected == PartyMenuSlot.SLOT_0)) {

            selectedEntityId = gp.getEntityM().getPlayer().getEntityId();
        } else {

            Set<Integer> keySet = gp.getEntityM().getParty().keySet();                                                  // Extract keys from party map.
            Integer[] keyArray = keySet.toArray(new Integer[keySet.size()]);                                            // Convert set of keys to array of keys.
            int selectedIndex = partyMenuScrollLevel + (partySlotSelected.getValue() - 1);

            if (selectedIndex < keyArray.length) {

                selectedEntityId = gp.getEntityM().getParty().get(keyArray[
                        partyMenuScrollLevel + (partySlotSelected.getValue() - 1)]).getEntityId();
            } else {

                selectedEntityId = -1;
            }
        }
        return selectedEntityId;
    }


    /**
     * Increments the party menu slot selected by one.
     * If the party menu slot cannot increase, then nothing will happen.
     */
    public void incrementPartyMenuSlotSelected() {

        switch (partySlotSelected) {
            case SLOT_0:
                setPartyMenuSlotSelected(PartyMenuSlot.SLOT_1);
                break;
            case SLOT_1:
                setPartyMenuSlotSelected(PartyMenuSlot.SLOT_2);
                break;
        }
    }


    /**
     * Decrements the party menu slot selected by one.
     * If the party menu slot cannot decrease, then nothing will happen.
     */
    public void decrementPartyMenuSlotSelected() {

        switch (partySlotSelected) {
            case SLOT_2:
                setPartyMenuSlotSelected(PartyMenuSlot.SLOT_1);
                break;
            case SLOT_1:
                setPartyMenuSlotSelected(PartyMenuSlot.SLOT_0);
                break;
        }
    }


    /**
     * Sets which party member slot is active in the party menu.
     * If the new party member slot is unavailable, then nothing will happen.
     *
     * @param partyMenuSlot party menu slot to select
     */
    public void setPartyMenuSlotSelected(PartyMenuSlot partyMenuSlot) {

        PartyMenuSlot lastPartySlotSelected = partySlotSelected;                                                        // Temporarily store the party slot being swapped from in case it must be reverted.
        partySlotSelected = partyMenuSlot;                                                                              // Preemptively set party slot to new one to "test" whether it is a possibility.

        int selectedPartyMenuEntityId = getSelectedPartyMenuEntity();

        if (selectedPartyMenuEntityId != -1) {

            gp.getGuiIconM().getIconById(3).setSelected(partySlotSelected == PartyMenuSlot.SLOT_0 ? true : false);
            gp.getGuiIconM().getIconById(4).setSelected(partySlotSelected == PartyMenuSlot.SLOT_1 ? true : false);
            gp.getGuiIconM().getIconById(5).setSelected(partySlotSelected == PartyMenuSlot.SLOT_2 ? true : false);

            gp.getEntityIconM().getEntityIconById(gp.getEntityM().getPlayer().getEntityId())
                    .setSelected(gp.getEntityM().getPlayer().getEntityId() == selectedPartyMenuEntityId ? true : false);

            for (int entityId : gp.getEntityM().getParty().keySet()) {

                gp.getEntityIconM().getEntityIconById(entityId)
                        .setSelected(entityId == selectedPartyMenuEntityId ? true : false);
            }
        } else {

            partySlotSelected = lastPartySlotSelected;                                                                  // Revert the party slot selected.
        }
    }


    /**
     * Sets the scroll level of the party menu.
     *
     * @param partyMenuScrollLevel party menu scroll level to set
     */
    public void setPartyMenuScrollLevel(int partyMenuScrollLevel) {

        if ((partyMenuScrollLevel < 0) || (gp.getEntityM().getParty().size() < 3)) {

            this.partyMenuScrollLevel = 0;
        } else if (partyMenuScrollLevel > (gp.getEntityM().getParty().size() - 2)) {

            this.partyMenuScrollLevel = gp.getEntityM().getParty().size() - 2;
        } else {

            this.partyMenuScrollLevel = partyMenuScrollLevel;
        }
        setPartyMenuSlotSelected(partySlotSelected);
    }


    /**
     * Marks this user interface component as "dirty" to indicate that it must be refreshed to display updated
     * information.
     * In this case, a refresh must be done if the number of party members changes.
     * Note that a refresh is not necessary if the contents of the user interface components change (e.g., which
     * entity is occupying a slot icon or the corresponding summary).
     */
    public void markDirty() {

        dirty = true;
    }


    /**
     * Initializes party menu user interface components that will not change while the game is running.
     * These user interface elements are the "core" aspects of the party menu layout, such as the centerline positioning
     * of slot icons.
     */
    private void init() {

        // Selection and scroll management.
        partySlotSelected = PartyMenuSlot.SLOT_0;
        partyMenuScrollLevel = 0;

        // Temporary world coordinates and dimensions.
        Vector2f tempWorldCoords = new Vector2f(0.0f, 0.0f);
        Vector2f tempWorldDimensions = new Vector2f(0.0f, 0.0f);
        tempWorldTransform = new Transform(tempWorldCoords, tempWorldDimensions);

        // Colors.
        textColor = new Vector3f(255, 255, 255);
        scrollIconInactiveColor = new Vector4f(174, 231, 255, 255);
        scrollIconActiveColor = new Vector4f(100, 193, 255, 255);

        // Text sizing.
        slotIconContentsFontScale = 0.12f;

        // Slot icon coordinates and dimensions.
        float slotIconWorldVerticalSpacing = 40.0f;
        slotIconScreenVerticalSpacing = gp.getCamera().worldHeightToScreenHeight(slotIconWorldVerticalSpacing);

        float slotIconWorldWidth = gp.getGuiIconM().getIconById(3).getNativeSpriteWidth();                              // It doesn't matter which of the slot icons is used here, since all are the same width.
        float slotIconScreenWidth = gp.getCamera().worldWidthToScreenWidth(slotIconWorldWidth);
        float slotIconWorldHeight = gp.getGuiIconM().getIconById(3).getNativeSpriteHeight();                            // It doesn't matter which of the slot icons is used here, since all are the same height.
        float slotIconScreenHeight = gp.getCamera().worldHeightToScreenHeight(slotIconWorldHeight);
        slotIconScreenDimensions = new Vector2f(slotIconScreenWidth, slotIconScreenHeight);

        slotIconScreenCenterY =
                (gp.getUiPrimaryMenuFrameS().getHeaderDividerScreenY()
                        + gp.getUiPrimaryMenuFrameS().getHeaderDividerScreenHeight())
                        + ((1 - gp.getUiPrimaryMenuFrameS().getHeaderDividerScreenY()
                        - gp.getUiPrimaryMenuFrameS().getHeaderDividerScreenHeight()
                        - ((1 - gp.getUiPrimaryMenuFrameS().getPrimaryWindowScreenHeight()) / 2)) / 2);                 // Horizontal center line around which to render the column (i.e., group) of slot icons.

        // Array initialization.
        slotIconScreenCoords = new LimitedArrayList<>(3);
        entityIconScreenCoords = new LimitedArrayList<>(3);
        entityNameTextScreenCoords = new LimitedArrayList<>(3);
        entityLifeBarScreenCoords = new LimitedArrayList<>(3);
        entityLifeLabelTextScreenCoords = new LimitedArrayList<>(3);
        entityLifeNumberTextScreenCoords = new LimitedArrayList<>(3);
        entitySkillBarScreenCoords = new LimitedArrayList<>(3);
        entitySkillLabelTextScreenCoords = new LimitedArrayList<>(3);
        entitySkillNumberTextScreenCoords = new LimitedArrayList<>(3);
        scrollIconActiveTransforms = new ArrayList<>();
        scrollIconInactiveTransforms = new ArrayList<>();

        // Slot icon life bar dimensions.
        float lifeBarWorldWidth = 30.0f;
        lifeBarScreenWidth = gp.getCamera().worldWidthToScreenWidth(lifeBarWorldWidth);

        // Slot icon skill bar dimensions.
        float skillBarWorldWidth = 30.0f;
        skillBarScreenWidth = gp.getCamera().worldWidthToScreenWidth(skillBarWorldWidth);

        // Scroll icon dimensions.
        float scrollIconWorldVerticalSpacing = 12.0f;
        scrollIconScreenVerticalSpacing =
                gp.getCamera().worldHeightToScreenHeight(scrollIconWorldVerticalSpacing);

        float scrollIconWorldInactiveWidthHeight = 4.0f;
        float scrollIconScreenInactiveWidth = gp.getCamera().worldWidthToScreenWidth(scrollIconWorldInactiveWidthHeight);
        float scrollIconScreenInactiveHeight = gp.getCamera().worldHeightToScreenHeight(scrollIconWorldInactiveWidthHeight);
        scrollIconScreenInactiveDimensions = new Vector2f(scrollIconScreenInactiveWidth, scrollIconScreenInactiveHeight);

        float scrollIconWorldActiveWidthHeight = 6.0f;
        float scrollIconScreenActiveWidth = gp.getCamera().worldWidthToScreenWidth(scrollIconWorldActiveWidthHeight);
        float scrollIconScreenActiveHeight = gp.getCamera().worldHeightToScreenHeight(scrollIconWorldActiveWidthHeight);
        scrollIconScreenActiveDimensions = new Vector2f(scrollIconScreenActiveWidth, scrollIconScreenActiveHeight);
    }


    /**
     * Refreshes slot icon content of party menu user interface components that may change while the game is running.
     *
     * @param partyMenuSlot party menu slot to refresh
     */
    private void refreshSlotIconContents(int partyMenuSlot) {

        // Base coordinates from which to perform calculations.
        Vector2f baseWorldCoords = new Vector2f(0.0f, 0.0f);
        gp.getCamera().screenCoordsToWorldCoords(slotIconScreenCoords.get(partyMenuSlot), baseWorldCoords);

        // Working coordinates for calculations.
        Vector2f workingScreenCoords = new Vector2f(0.0f, 0.0f);
        Vector2f workingWorldCoords = new Vector2f(0.0f, 0.0f);

        // Entity icon coordinates.
        workingWorldCoords.x = baseWorldCoords.x + 6.0f;
        workingWorldCoords.y = baseWorldCoords.y - 4.0f;
        gp.getCamera().worldCoordsToScreenCoords(workingWorldCoords, workingScreenCoords);
        entityIconScreenCoords.add(new Vector2f(workingScreenCoords.x, workingScreenCoords.y));

        // Entity name coordinates.
        workingWorldCoords.x = baseWorldCoords.x + 45.5f;
        workingWorldCoords.y = baseWorldCoords.y + 7.0f;
        gp.getCamera().worldCoordsToScreenCoords(workingWorldCoords, workingScreenCoords);
        entityNameTextScreenCoords.add(new Vector2f(workingScreenCoords.x, workingScreenCoords.y));

        // Life bar coordinates.
        workingWorldCoords.x = baseWorldCoords.x + 64.0f;
        workingWorldCoords.y = baseWorldCoords.y + 26.0f;
        gp.getCamera().worldCoordsToScreenCoords(workingWorldCoords, workingScreenCoords);
        entityLifeBarScreenCoords.add(new Vector2f(workingScreenCoords.x, workingScreenCoords.y));

        // Life bar label coordinates.
        workingWorldCoords.x = baseWorldCoords.x + 45.5f;
        workingWorldCoords.y = baseWorldCoords.y + 23.0f;
        gp.getCamera().worldCoordsToScreenCoords(workingWorldCoords, workingScreenCoords);
        entityLifeLabelTextScreenCoords.add(new Vector2f(workingScreenCoords.x, workingScreenCoords.y));

        // Life bar number coordinates.
        workingWorldCoords.x = baseWorldCoords.x + 95.5f;
        workingWorldCoords.y = baseWorldCoords.y + 23.0f;
        gp.getCamera().worldCoordsToScreenCoords(workingWorldCoords, workingScreenCoords);
        entityLifeNumberTextScreenCoords.add(new Vector2f(workingScreenCoords.x, workingScreenCoords.y));

        // Skill bar coordinates.
        workingWorldCoords.x = baseWorldCoords.x + 64.0f;
        workingWorldCoords.y = baseWorldCoords.y + 42.0f;
        gp.getCamera().worldCoordsToScreenCoords(workingWorldCoords, workingScreenCoords);
        entitySkillBarScreenCoords.add(new Vector2f(workingScreenCoords.x, workingScreenCoords.y));

        // Skill bar label coordinates.
        workingWorldCoords.x = baseWorldCoords.x + 45.5f;
        workingWorldCoords.y = baseWorldCoords.y + 39.0f;
        gp.getCamera().worldCoordsToScreenCoords(workingWorldCoords, workingScreenCoords);
        entitySkillLabelTextScreenCoords.add(new Vector2f(workingScreenCoords.x, workingScreenCoords.y));

        // Skill bar number coordinates.
        workingWorldCoords.x = baseWorldCoords.x + 95.5f;
        workingWorldCoords.y = baseWorldCoords.y + 39.0f;
        gp.getCamera().worldCoordsToScreenCoords(workingWorldCoords, workingScreenCoords);
        entitySkillNumberTextScreenCoords.add(new Vector2f(workingScreenCoords.x, workingScreenCoords.y));
    }


    /**
     * Adds slot icon content user interface components to the render pipeline.
     *
     * @param renderer Renderer instance
     * @param partyMenuSlot party menu slot to add to render pipeline
     * @param entity entity occupying the party menu slot
     */
    private void addSlotIconContentToRenderPipeline(Renderer renderer, int partyMenuSlot,
                                                    EntityBase entity) {

        // Entity icon.
        gp.getEntityIconM().addToRenderPipeline(
                renderer,
                entity.getEntityId(),
                entityIconScreenCoords.get(partyMenuSlot).x,
                entityIconScreenCoords.get(partyMenuSlot).y
        );

        // Entity name.
        gp.getUi().addStringShadowToRenderPipeline(
                entity.getName(),
                entityNameTextScreenCoords.get(partyMenuSlot).x,
                entityNameTextScreenCoords.get(partyMenuSlot).y,
                slotIconContentsFontScale,
                textColor,
                gp.getUi().getStandardBoldFont(),
                ZIndex.SECOND_LAYER
        );

        // Life bar.
        gp.getUi().addLifeBarToRenderPipeline(
                entity.getLife(),
                entity.getMaxLife(),
                lifeBarScreenWidth,
                entityLifeBarScreenCoords.get(partyMenuSlot).x,
                entityLifeBarScreenCoords.get(partyMenuSlot).y
        );

        // Life bar label.
        gp.getUi().addStringShadowToRenderPipeline(
                "HP",
                entityLifeLabelTextScreenCoords.get(partyMenuSlot).x,
                entityLifeLabelTextScreenCoords.get(partyMenuSlot).y,
                slotIconContentsFontScale,
                textColor,
                gp.getUi().getStandardBoldFont(),
                ZIndex.SECOND_LAYER
        );

        // Life bar number.
        gp.getUi().addStringShadowToRenderPipeline(
                entity.getLife() + "/" + entity.getMaxLife(),
                entityLifeNumberTextScreenCoords.get(partyMenuSlot).x,
                entityLifeNumberTextScreenCoords.get(partyMenuSlot).y,
                slotIconContentsFontScale,
                textColor,
                gp.getUi().getStandardBoldFont(),
                ZIndex.SECOND_LAYER
        );

        // Skill bar.
        gp.getUi().addSkillBarToRenderPipeline(
                entity.getSkill(),
                entity.getMaxSkill(),
                skillBarScreenWidth,
                entitySkillBarScreenCoords.get(partyMenuSlot).x,
                entitySkillBarScreenCoords.get(partyMenuSlot).y
        );

        // Skill bar label.
        gp.getUi().addStringShadowToRenderPipeline(
                "SP",
                entitySkillLabelTextScreenCoords.get(partyMenuSlot).x,
                entitySkillLabelTextScreenCoords.get(partyMenuSlot).y,
                slotIconContentsFontScale,
                textColor,
                gp.getUi().getStandardBoldFont(),
                ZIndex.SECOND_LAYER
        );

        // Skill bar number.
        gp.getUi().addStringShadowToRenderPipeline(
                entity.getSkill() + "/" + entity.getMaxSkill(),
                entitySkillNumberTextScreenCoords.get(partyMenuSlot).x,
                entitySkillNumberTextScreenCoords.get(partyMenuSlot).y,
                slotIconContentsFontScale,
                textColor,
                gp.getUi().getStandardBoldFont(),
                ZIndex.SECOND_LAYER
        );
    }


    // GETTERS
    public boolean isDirty() {
        return dirty;
    }

    public PartyMenuSlot getPartySlotSelected() {
        return partySlotSelected;
    }

    public int getPartyMenuScrollLevel() {
        return partyMenuScrollLevel;
    }
}
