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

    private int partyMenuSlotScrollLevel;

    private int partyMenuMoveScrollLevel;

    private float slotIconContentsFontScale;

    private Transform tempWorldTransform;

    private Vector3f textColor;

    private Vector4f scrollIconInactiveColor;

    private Vector4f scrollIconActiveColor;

    private Vector4f verticalDividerColor;

    private Vector3f attributeHeaderTextColor;

    private Vector3f attributeLabelTextColor;

    private Vector3f attributeValueTextColor;

    private Vector3f moveDataHeaderTextColor;

    private Vector3f moveDataLabelTextColor;

    private Vector3f moveDataValueTextColor;

    private Vector3f emptyTextColor;

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

    private Transform leftVerticalDividerScreenTransform;

    private Transform rightVerticalDividerScreenTransform;

    private Vector2f attributeHeaderTextScreenCoords;

    private Vector2f attributeLifeLabelScreenCoords;

    private Vector2f attributeSkillLabelScreenCoords;

    private Vector2f attributeAttackLabelScreenCoords;

    private Vector2f attributeDefenseLabelScreenCoords;

    private Vector2f attributeMagicLabelScreenCoords;

    private Vector2f attributeAgilityLabelScreenCoords;

    private String attributeLifeLabelText;

    private String attributeSkillLabelText;

    private String attributeAttackLabelText;

    private String attributeDefenseLabelText;

    private String attributeMagicLabelText;

    private String attributeAgilityLabelText;

    private Vector2f attributeLifeValueScreenCoords;

    private Vector2f attributeSkillValueScreenCoords;

    private Vector2f attributeAttackValueScreenCoords;

    private Vector2f attributeDefenseValueScreenCoords;

    private Vector2f attributeMagicValueScreenCoords;

    private Vector2f attributeAgilityValueScreenCoords;

    private Vector2f moveDataHeaderTextScreenCoords;

    private Vector2f moveDataNameTextScreenCoords;

    private Vector2f moveDataCategoryLabelScreenCoords;

    private Vector2f moveDataPowerLabelScreenCoords;

    private Vector2f moveDataAccuracyLabelScreenCoords;

    private Vector2f moveDataSkillPointsLabelScreenCoords;

    private String moveDataCategoryLabelText;

    private String moveDataPowerLabelText;

    private String moveDataAccuracyLabelText;

    private String moveDataSkillPointsLabelText;

    private Vector2f moveDataCategoryValueScreenCoords;

    private Vector2f moveDataPowerValueScreenCoords;

    private Vector2f moveDataAccuracyValueScreenCoords;

    private Vector2f moveDataSkillPointsValueScreenCoords;

    private Vector2f moveDataDescriptionTextScreenCoords;

    private float moveDataDescriptionTextLineScreenVerticalSpacing;

    private float moveDataDescriptionTextLineScreenMaxWidth;

    private Vector2f emptyTextScreenCoords;

    private String emptyText;

    private Vector2f rightScrollArrowScreenCoords;

    private Vector2f leftScrollArrowScreenCoords;

//    private Transform centerMoveDividerScreenTransform;
//
//    private Transform topMoveDividerScreenTransform;
//
//    private Transform bottomMoveDividerScreenTransform;


    // CONSTRUCTOR
    /**
     * Constructs a UiPartyMenuSupport instance.
     *
     * @param gp GamePanel instance
     * @param renderer Renderer instance
     */
    public UiPartyMenuSupport(GamePanel gp, Renderer renderer) {
        this.gp = gp;
        init(renderer);
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

                if (partyMenuSlotScrollLevel == 0) {

                    addSlotIconContentToRenderPipeline(
                            renderer,
                            i,
                            gp.getEntityM().getPlayer()
                    );
                } else {

                    addSlotIconContentToRenderPipeline(
                            renderer,
                            i,
                            gp.getEntityM().getParty().get(entityKeyArray[partyMenuSlotScrollLevel - 1])
                    );
                }
            } else if (i == 1) {

                gp.getGuiIconM().addToRenderPipeline(
                        renderer, 4, slotIconScreenCoords.get(i).x, slotIconScreenCoords.get(1).y);

                addSlotIconContentToRenderPipeline(
                        renderer,
                        i,
                        gp.getEntityM().getParty().get(entityKeyArray[partyMenuSlotScrollLevel])
                );
            } else if (i == 2) {

                gp.getGuiIconM().addToRenderPipeline(
                        renderer, 5, slotIconScreenCoords.get(i).x, slotIconScreenCoords.get(2).y);

                addSlotIconContentToRenderPipeline(
                        renderer,
                        i,
                        gp.getEntityM().getParty().get(entityKeyArray[partyMenuSlotScrollLevel + 1])
                );
            } else {

                break;                                                                                                  // Just in case.
            }
        }

        // Scroll icons.
        for (int i = 0; i < scrollIconActiveTransforms.size(); i++) {                                                   // Doesn't matter whether active or inactive is used since both will be of the same size.

            if (i == (partySlotSelected.getValue() + partyMenuSlotScrollLevel)) {

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

        // Left vertical divider.
        gp.getCamera().screenCoordsToWorldCoords(
                leftVerticalDividerScreenTransform.position, tempWorldTransform.position);
        gp.getCamera().screenDimensionsToWorldDimensions(
                leftVerticalDividerScreenTransform.scale, tempWorldTransform.scale);
        renderer.addRectangle(verticalDividerColor, tempWorldTransform, ZIndex.SECOND_LAYER);

        // Right vertical divider.
        gp.getCamera().screenCoordsToWorldCoords(
                rightVerticalDividerScreenTransform.position, tempWorldTransform.position);
        gp.getCamera().screenDimensionsToWorldDimensions(
                rightVerticalDividerScreenTransform.scale, tempWorldTransform.scale);
        renderer.addRectangle(verticalDividerColor, tempWorldTransform, ZIndex.SECOND_LAYER);

        // Selected party member attribute header.
        gp.getCamera().screenCoordsToWorldCoords(
                attributeHeaderTextScreenCoords, tempWorldTransform.position);
        renderer.addString(
                gp.getEntityM().getEntityById(getSelectedPartyMenuEntity()).getName(),
                tempWorldTransform.position.x,
                tempWorldTransform.position.y,
                gp.getUi().getStandardFontScale(),
                attributeHeaderTextColor,
                gp.getUi().getStandardBoldFont(),
                ZIndex.SECOND_LAYER);

        // Selected party member attributes.
        gp.getCamera().screenCoordsToWorldCoords(
                attributeLifeLabelScreenCoords, tempWorldTransform.position);
        renderer.addString(
                attributeLifeLabelText,
                tempWorldTransform.position.x,
                tempWorldTransform.position.y,
                gp.getUi().getStandardFontScale(),
                attributeLabelTextColor,
                gp.getUi().getStandardNormalFont(),
                ZIndex.SECOND_LAYER);

        gp.getCamera().screenCoordsToWorldCoords(
                attributeSkillLabelScreenCoords, tempWorldTransform.position);
        renderer.addString(
                attributeSkillLabelText,
                tempWorldTransform.position.x,
                tempWorldTransform.position.y,
                gp.getUi().getStandardFontScale(),
                attributeLabelTextColor,
                gp.getUi().getStandardNormalFont(),
                ZIndex.SECOND_LAYER);

        gp.getCamera().screenCoordsToWorldCoords(
                attributeAttackLabelScreenCoords, tempWorldTransform.position);
        renderer.addString(
                attributeAttackLabelText,
                tempWorldTransform.position.x,
                tempWorldTransform.position.y,
                gp.getUi().getStandardFontScale(),
                attributeLabelTextColor,
                gp.getUi().getStandardNormalFont(),
                ZIndex.SECOND_LAYER);

        gp.getCamera().screenCoordsToWorldCoords(
                attributeDefenseLabelScreenCoords, tempWorldTransform.position);
        renderer.addString(
                attributeDefenseLabelText,
                tempWorldTransform.position.x,
                tempWorldTransform.position.y,
                gp.getUi().getStandardFontScale(),
                attributeLabelTextColor,
                gp.getUi().getStandardNormalFont(),
                ZIndex.SECOND_LAYER);

        gp.getCamera().screenCoordsToWorldCoords(
                attributeMagicLabelScreenCoords, tempWorldTransform.position);
        renderer.addString(
                attributeMagicLabelText,
                tempWorldTransform.position.x,
                tempWorldTransform.position.y,
                gp.getUi().getStandardFontScale(),
                attributeLabelTextColor,
                gp.getUi().getStandardNormalFont(),
                ZIndex.SECOND_LAYER);

        gp.getCamera().screenCoordsToWorldCoords(
                attributeAgilityLabelScreenCoords, tempWorldTransform.position);
        renderer.addString(
                attributeAgilityLabelText,
                tempWorldTransform.position.x,
                tempWorldTransform.position.y,
                gp.getUi().getStandardFontScale(),
                attributeLabelTextColor,
                gp.getUi().getStandardNormalFont(),
                ZIndex.SECOND_LAYER);

        gp.getCamera().screenCoordsToWorldCoords(
                attributeLifeValueScreenCoords, tempWorldTransform.position);
        renderer.addString(
                gp.getEntityM().getEntityById(getSelectedPartyMenuEntity()).getLife()
                        + "/" + gp.getEntityM().getEntityById(getSelectedPartyMenuEntity()).getMaxLife(),
                tempWorldTransform.position.x,
                tempWorldTransform.position.y,
                gp.getUi().getStandardFontScale(),
                attributeValueTextColor,
                gp.getUi().getStandardNormalFont(),
                ZIndex.SECOND_LAYER);

        gp.getCamera().screenCoordsToWorldCoords(
                attributeSkillValueScreenCoords, tempWorldTransform.position);
        renderer.addString(
                gp.getEntityM().getEntityById(getSelectedPartyMenuEntity()).getSkill()
                        + "/" + gp.getEntityM().getEntityById(getSelectedPartyMenuEntity()).getMaxSkill(),
                tempWorldTransform.position.x,
                tempWorldTransform.position.y,
                gp.getUi().getStandardFontScale(),
                attributeValueTextColor,
                gp.getUi().getStandardNormalFont(),
                ZIndex.SECOND_LAYER);

        gp.getCamera().screenCoordsToWorldCoords(
                attributeAttackValueScreenCoords, tempWorldTransform.position);
        renderer.addString(
                Integer.toString(gp.getEntityM().getEntityById(getSelectedPartyMenuEntity()).getBaseAttack()),
                tempWorldTransform.position.x,
                tempWorldTransform.position.y,
                gp.getUi().getStandardFontScale(),
                attributeValueTextColor,
                gp.getUi().getStandardNormalFont(),
                ZIndex.SECOND_LAYER);

        gp.getCamera().screenCoordsToWorldCoords(
                attributeDefenseValueScreenCoords, tempWorldTransform.position);
        renderer.addString(
                Integer.toString(gp.getEntityM().getEntityById(getSelectedPartyMenuEntity()).getBaseDefense()),
                tempWorldTransform.position.x,
                tempWorldTransform.position.y,
                gp.getUi().getStandardFontScale(),
                attributeValueTextColor,
                gp.getUi().getStandardNormalFont(),
                ZIndex.SECOND_LAYER);

        gp.getCamera().screenCoordsToWorldCoords(
                attributeMagicValueScreenCoords, tempWorldTransform.position);
        renderer.addString(
                Integer.toString(gp.getEntityM().getEntityById(getSelectedPartyMenuEntity()).getBaseMagic()),
                tempWorldTransform.position.x,
                tempWorldTransform.position.y,
                gp.getUi().getStandardFontScale(),
                attributeValueTextColor,
                gp.getUi().getStandardNormalFont(),
                ZIndex.SECOND_LAYER);

        gp.getCamera().screenCoordsToWorldCoords(
                attributeAgilityValueScreenCoords, tempWorldTransform.position);
        renderer.addString(
                Integer.toString(gp.getEntityM().getEntityById(getSelectedPartyMenuEntity()).getBaseAgility()),
                tempWorldTransform.position.x,
                tempWorldTransform.position.y,
                gp.getUi().getStandardFontScale(),
                attributeValueTextColor,
                gp.getUi().getStandardNormalFont(),
                ZIndex.SECOND_LAYER);

        if (gp.getEntityM().getEntityById(getSelectedPartyMenuEntity()).getMoves().size() == 0) {

            gp.getCamera().screenCoordsToWorldCoords(emptyTextScreenCoords, tempWorldTransform.position);
            renderer.addString(
                    emptyText,
                    tempWorldTransform.position.x,
                    tempWorldTransform.position.y,
                    gp.getUi().getStandardFontScale(),
                    emptyTextColor,
                    gp.getUi().getStandardNormalFont(),
                    ZIndex.SECOND_LAYER);
        } else {

            // Selected party member move data header.
            gp.getCamera().screenCoordsToWorldCoords(
                    moveDataHeaderTextScreenCoords, tempWorldTransform.position);
            renderer.addString(
                    gp.getEntityM().getEntityById(getSelectedPartyMenuEntity())
                            .getMoves().get(partyMenuMoveScrollLevel).getName(),
                    tempWorldTransform.position.x,
                    tempWorldTransform.position.y,
                    gp.getUi().getStandardFontScale(),
                    moveDataHeaderTextColor,
                    gp.getUi().getStandardBoldFont(),
                    ZIndex.SECOND_LAYER);

            // Selected party member move data.
            gp.getCamera().screenCoordsToWorldCoords(
                    moveDataCategoryLabelScreenCoords, tempWorldTransform.position);
            renderer.addString(
                    moveDataCategoryLabelText,
                    tempWorldTransform.position.x,
                    tempWorldTransform.position.y,
                    gp.getUi().getStandardFontScale(),
                    moveDataLabelTextColor,
                    gp.getUi().getStandardNormalFont(),
                    ZIndex.SECOND_LAYER);

            gp.getCamera().screenCoordsToWorldCoords(
                    moveDataPowerLabelScreenCoords, tempWorldTransform.position);
            renderer.addString(
                    moveDataPowerLabelText,
                    tempWorldTransform.position.x,
                    tempWorldTransform.position.y,
                    gp.getUi().getStandardFontScale(),
                    moveDataLabelTextColor,
                    gp.getUi().getStandardNormalFont(),
                    ZIndex.SECOND_LAYER);

            gp.getCamera().screenCoordsToWorldCoords(
                    moveDataAccuracyLabelScreenCoords, tempWorldTransform.position);
            renderer.addString(
                    moveDataAccuracyLabelText,
                    tempWorldTransform.position.x,
                    tempWorldTransform.position.y,
                    gp.getUi().getStandardFontScale(),
                    moveDataLabelTextColor,
                    gp.getUi().getStandardNormalFont(),
                    ZIndex.SECOND_LAYER);

            gp.getCamera().screenCoordsToWorldCoords(
                    moveDataSkillPointsLabelScreenCoords, tempWorldTransform.position);
            renderer.addString(
                    moveDataSkillPointsLabelText,
                    tempWorldTransform.position.x,
                    tempWorldTransform.position.y,
                    gp.getUi().getStandardFontScale(),
                    moveDataLabelTextColor,
                    gp.getUi().getStandardNormalFont(),
                    ZIndex.SECOND_LAYER);

            gp.getCamera().screenCoordsToWorldCoords(
                    moveDataCategoryValueScreenCoords, tempWorldTransform.position);
            renderer.addString(
                    gp.getEntityM().getEntityById(getSelectedPartyMenuEntity())
                            .getMoves().get(partyMenuMoveScrollLevel).getCategory() + "",
                    tempWorldTransform.position.x,
                    tempWorldTransform.position.y,
                    gp.getUi().getStandardFontScale(),
                    moveDataValueTextColor,
                    gp.getUi().getStandardNormalFont(),
                    ZIndex.SECOND_LAYER);

            gp.getCamera().screenCoordsToWorldCoords(
                    moveDataPowerValueScreenCoords, tempWorldTransform.position);
            renderer.addString(
                    Integer.toString(gp.getEntityM().getEntityById(getSelectedPartyMenuEntity())
                            .getMoves().get(partyMenuMoveScrollLevel).getPower()),
                    tempWorldTransform.position.x,
                    tempWorldTransform.position.y,
                    gp.getUi().getStandardFontScale(),
                    moveDataValueTextColor,
                    gp.getUi().getStandardNormalFont(),
                    ZIndex.SECOND_LAYER);

            gp.getCamera().screenCoordsToWorldCoords(
                    moveDataAccuracyValueScreenCoords, tempWorldTransform.position);
            renderer.addString(
                    Integer.toString(gp.getEntityM().getEntityById(getSelectedPartyMenuEntity())
                            .getMoves().get(partyMenuMoveScrollLevel).getAccuracy()),
                    tempWorldTransform.position.x,
                    tempWorldTransform.position.y,
                    gp.getUi().getStandardFontScale(),
                    moveDataValueTextColor,
                    gp.getUi().getStandardNormalFont(),
                    ZIndex.SECOND_LAYER);

            gp.getCamera().screenCoordsToWorldCoords(
                    moveDataSkillPointsValueScreenCoords, tempWorldTransform.position);
            renderer.addString(
                    Integer.toString(gp.getEntityM().getEntityById(getSelectedPartyMenuEntity())
                            .getMoves().get(partyMenuMoveScrollLevel).getSkillPoints()),
                    tempWorldTransform.position.x,
                    tempWorldTransform.position.y,
                    gp.getUi().getStandardFontScale(),
                    moveDataValueTextColor,
                    gp.getUi().getStandardNormalFont(),
                    ZIndex.SECOND_LAYER);

            gp.getUi().addStringBlockToRenderPipeline(
                    gp.getEntityM().getEntityById(getSelectedPartyMenuEntity())
                            .getMoves().get(partyMenuMoveScrollLevel).getDescription(),
                    moveDataDescriptionTextScreenCoords.x,
                    moveDataDescriptionTextScreenCoords.y,
                    moveDataDescriptionTextLineScreenMaxWidth,
                    moveDataDescriptionTextLineScreenVerticalSpacing,
                    gp.getUi().getStandardFontScale(),
                    moveDataValueTextColor,
                    gp.getUi().getStandardNormalFont(),
                    ZIndex.SECOND_LAYER,
                    true
            );

            // Move data scroll arrows.
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
        }
    }


    /**
     * Retrieves the party member (including the player entity) that is selected in the party menu screen.
     * Returns '-1' if the party menu slot scroll level and selected slot combination produces an entry beyond the number of
     * party members.
     *
     * @return ID of selected entity
     */
    public int getSelectedPartyMenuEntity() {

        int selectedEntityId;

        if ((partyMenuSlotScrollLevel == 0) && (partySlotSelected == PartyMenuSlot.SLOT_0)) {

            selectedEntityId = gp.getEntityM().getPlayer().getEntityId();
        } else {

            Set<Integer> keySet = gp.getEntityM().getParty().keySet();                                                  // Extract keys from party map.
            Integer[] keyArray = keySet.toArray(new Integer[keySet.size()]);                                            // Convert set of keys to array of keys.
            int selectedIndex = partyMenuSlotScrollLevel + (partySlotSelected.getValue() - 1);

            if (selectedIndex < keyArray.length) {

                selectedEntityId = gp.getEntityM().getParty().get(keyArray[
                        partyMenuSlotScrollLevel + (partySlotSelected.getValue() - 1)]).getEntityId();
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
            setPartyMenuMoveScrollLevel(0);                                                                             // Reset back to first move of selected entity.
        } else {

            partySlotSelected = lastPartySlotSelected;                                                                  // Revert the party slot selected.
        }
    }


    /**
     * Sets the slot scroll level of the party menu.
     *
     * @param partyMenuSlotScrollLevel party menu slot scroll level to set
     */
    public void setPartyMenuSlotScrollLevel(int partyMenuSlotScrollLevel) {

        if ((partyMenuSlotScrollLevel < 0) || (gp.getEntityM().getParty().size() < 3)) {

            this.partyMenuSlotScrollLevel = 0;
        } else if (partyMenuSlotScrollLevel > (gp.getEntityM().getParty().size() - 2)) {

            this.partyMenuSlotScrollLevel = gp.getEntityM().getParty().size() - 2;
        } else {

            this.partyMenuSlotScrollLevel = partyMenuSlotScrollLevel;
        }
        setPartyMenuSlotSelected(partySlotSelected);
    }


    /**
     * Sets the move scroll level of the party menu.
     *
     * @param partyMenuMoveScrollLevel party menu move scroll level to set
     */
    public void setPartyMenuMoveScrollLevel(int partyMenuMoveScrollLevel) {

        if ((partyMenuMoveScrollLevel < 0)
                || (gp.getEntityM().getEntityById(getSelectedPartyMenuEntity()).getMoves().size() == 0)) {

            this.partyMenuMoveScrollLevel = 0;
        } else if (partyMenuMoveScrollLevel
                >= (gp.getEntityM().getEntityById(getSelectedPartyMenuEntity()).getMoves().size())) {

            this.partyMenuMoveScrollLevel =
                    gp.getEntityM().getEntityById(getSelectedPartyMenuEntity()).getMoves().size() - 1;
        } else {

            this.partyMenuMoveScrollLevel = partyMenuMoveScrollLevel;
        }
        refreshMoveDataScrollArrows();
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
     *
     * @param renderer Renderer instance
     */
    private void init(Renderer renderer) {

        // Selection and scroll management.
        partySlotSelected = PartyMenuSlot.SLOT_0;
        partyMenuSlotScrollLevel = 0;
        partyMenuMoveScrollLevel = 0;

        // Temporary world coordinates and dimensions.
        Vector2f tempWorldCoords = new Vector2f(0.0f, 0.0f);
        Vector2f tempWorldDimensions = new Vector2f(0.0f, 0.0f);
        tempWorldTransform = new Transform(tempWorldCoords, tempWorldDimensions);

        // Colors.
        textColor = new Vector3f(255, 255, 255);
        scrollIconInactiveColor = new Vector4f(174, 231, 255, 255);
        scrollIconActiveColor = new Vector4f(100, 193, 255, 255);
        verticalDividerColor = new Vector4f(147, 182, 220, 255);
        attributeHeaderTextColor = new Vector3f(121, 255, 218);
        attributeLabelTextColor = new Vector3f(121, 149, 255);
        attributeValueTextColor = new Vector3f(255, 255, 255);
        moveDataHeaderTextColor = new Vector3f(155, 121, 255);
        moveDataLabelTextColor = new Vector3f(121, 149, 255);
        moveDataValueTextColor = new Vector3f(255, 255, 255);
        emptyTextColor = new Vector3f(160, 160, 160);

        // Text sizing.
        slotIconContentsFontScale = 0.12f;
        float standardNormalCharWorldHeight = renderer.getFont(gp.getUi().getStandardNormalFont())
                .getCharacter('A').getHeight() * gp.getUi().getStandardFontScale();                                     // It doesn't matter which character is used, since all characters in a font have the same height.

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

        // Vertical divider coordinates and dimensions (both, setup).
        float headerDividerScreenPrimaryWindowLeftAdjustment = gp.getUiPrimaryMenuFrameS().getHeaderDividerScreenX()
                - gp.getUiPrimaryMenuFrameS().getPrimaryWindowScreenX();
        float headerDividerWorldPrimaryWindowLeftAdjustment =
                gp.getCamera().screenWidthToWorldWidth(headerDividerScreenPrimaryWindowLeftAdjustment);
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
        float verticalDividerScreenY = gp.getUiPrimaryMenuFrameS().getHeaderDividerScreenY()
                + gp.getUiPrimaryMenuFrameS().getHeaderDividerScreenHeight()
                + verticalDividerScreenHeaderDividerBottomAdjustment;
        Vector2f verticalDividerScreenDimensions = new Vector2f(verticalDividerScreenWidth, verticalDividerScreenHeight);

        // Left vertical divider coordinates.
        float leftVerticalDividerWorldSlotIconRightAdjustment = 11.5f;
        float leftVerticalDividerScreenSlotIconRightAdjustment =
                gp.getCamera().worldWidthToScreenWidth(leftVerticalDividerWorldSlotIconRightAdjustment);
        float leftVerticalDividerScreenX = gp.getUiPrimaryMenuFrameS().getHeaderDividerScreenX()
                + slotIconScreenWidth + leftVerticalDividerScreenSlotIconRightAdjustment;
        Vector2f leftVerticalDividerScreenCoords = new Vector2f(leftVerticalDividerScreenX, verticalDividerScreenY);
        leftVerticalDividerScreenTransform =
                new Transform(leftVerticalDividerScreenCoords, verticalDividerScreenDimensions);

        // Right vertical divider coordinates.
        float rightVerticalDividerScreenX = leftVerticalDividerScreenX +
                (gp.getUiPrimaryMenuFrameS().getPrimaryWindowScreenWidth() / 4) - (verticalDividerScreenWidth / 2);
        Vector2f rightVerticalDividerScreenCoords = new Vector2f(rightVerticalDividerScreenX, verticalDividerScreenY);
        rightVerticalDividerScreenTransform =
                new Transform(rightVerticalDividerScreenCoords, verticalDividerScreenDimensions);

        // Selected party member attribute header coordinates.
        float attributeHeaderTextWorldLeftVerticalDividerLeftAdjustment = 10.0f;
        float attributeHeaderTextScreenLeftVerticalDividerLeftAdjustment =
                gp.getCamera().worldWidthToScreenWidth(attributeHeaderTextWorldLeftVerticalDividerLeftAdjustment);
        float attributeHeaderTextScreenX = leftVerticalDividerScreenX + verticalDividerScreenWidth
                + attributeHeaderTextScreenLeftVerticalDividerLeftAdjustment;
        float attributeHeaderTextScreenY = verticalDividerScreenY;
        attributeHeaderTextScreenCoords = new Vector2f(attributeHeaderTextScreenX, attributeHeaderTextScreenY);

        // Selected party member attributes label content.
        attributeLifeLabelText = "HP: ";
        attributeSkillLabelText = "SP: ";
        attributeAttackLabelText = "Attack: ";
        attributeDefenseLabelText = "Defense: ";
        attributeMagicLabelText = "Magic: ";
        attributeAgilityLabelText = "Agility: ";

        // Selected party member attributes coordinates.
        float attributeWorldLeftVerticalDividerLeftAdjustment = 10.0f;
        float attributeScreenLeftVerticalDividerLeftAdjustment =
                gp.getCamera().worldWidthToScreenWidth(attributeWorldLeftVerticalDividerLeftAdjustment);
        float attributeLabelScreenX = leftVerticalDividerScreenX + verticalDividerScreenWidth
                + attributeScreenLeftVerticalDividerLeftAdjustment;
        float attributeWorldVerticalSpacing = standardNormalCharWorldHeight * 2.25f;
        float attributeScreenVerticalSpacing =
                gp.getCamera().worldHeightToScreenHeight(attributeWorldVerticalSpacing);
        float topAttributeScreenY = attributeHeaderTextScreenY + attributeScreenVerticalSpacing;

        attributeLifeLabelScreenCoords = new Vector2f(
                attributeLabelScreenX,
                topAttributeScreenY
        );
        attributeSkillLabelScreenCoords = new Vector2f(
                attributeLabelScreenX,
                topAttributeScreenY + attributeScreenVerticalSpacing
        );
        attributeAttackLabelScreenCoords = new Vector2f(
                attributeLabelScreenX,
                topAttributeScreenY + (attributeScreenVerticalSpacing * 2)
        );
        attributeDefenseLabelScreenCoords = new Vector2f(
                attributeLabelScreenX,
                topAttributeScreenY + (attributeScreenVerticalSpacing * 3)
        );
        attributeMagicLabelScreenCoords = new Vector2f(
                attributeLabelScreenX,
                topAttributeScreenY + (attributeScreenVerticalSpacing * 4)
        );
        attributeAgilityLabelScreenCoords = new Vector2f(
                attributeLabelScreenX,
                topAttributeScreenY + (attributeScreenVerticalSpacing * 5)
        );

        float attributeLifeLabelScreenWidth = gp.getUi().calculateStringScreenWidth(
                attributeLifeLabelText,
                gp.getUi().getStandardFontScale(),
                gp.getUi().getStandardNormalFont()
        );
        float attributeSkillLabelScreenWidth = gp.getUi().calculateStringScreenWidth(
                attributeSkillLabelText,
                gp.getUi().getStandardFontScale(),
                gp.getUi().getStandardNormalFont()
        );
        float attributeAttackLabelScreenWidth = gp.getUi().calculateStringScreenWidth(
                attributeAttackLabelText,
                gp.getUi().getStandardFontScale(),
                gp.getUi().getStandardNormalFont()
        );
        float attributeDefenseLabelScreenWidth = gp.getUi().calculateStringScreenWidth(
                attributeDefenseLabelText,
                gp.getUi().getStandardFontScale(),
                gp.getUi().getStandardNormalFont()
        );
        float attributeMagicLabelScreenWidth = gp.getUi().calculateStringScreenWidth(
                attributeMagicLabelText,
                gp.getUi().getStandardFontScale(),
                gp.getUi().getStandardNormalFont()
        );
        float attributeAgilityLabelScreenWidth = gp.getUi().calculateStringScreenWidth(
                attributeAgilityLabelText,
                gp.getUi().getStandardFontScale(),
                gp.getUi().getStandardNormalFont()
        );

        attributeLifeValueScreenCoords = new Vector2f(
                attributeLabelScreenX + attributeLifeLabelScreenWidth,
                topAttributeScreenY
        );
        attributeSkillValueScreenCoords = new Vector2f(
                attributeLabelScreenX + attributeSkillLabelScreenWidth,
                topAttributeScreenY + attributeScreenVerticalSpacing
        );
        attributeAttackValueScreenCoords = new Vector2f(
                attributeLabelScreenX + attributeAttackLabelScreenWidth,
                topAttributeScreenY + (attributeScreenVerticalSpacing * 2)
        );
        attributeDefenseValueScreenCoords = new Vector2f(
                attributeLabelScreenX + attributeDefenseLabelScreenWidth,
                topAttributeScreenY + (attributeScreenVerticalSpacing * 3)
        );
        attributeMagicValueScreenCoords = new Vector2f(
                attributeLabelScreenX + attributeMagicLabelScreenWidth,
                topAttributeScreenY + (attributeScreenVerticalSpacing * 4)
        );
        attributeAgilityValueScreenCoords = new Vector2f(
                attributeLabelScreenX + attributeAgilityLabelScreenWidth,
                topAttributeScreenY + (attributeScreenVerticalSpacing * 5)
        );

        // Selected party member move data header coordinates.
        float moveDataHeaderTextWorldRightVerticalDividerLeftAdjustment = 10.0f;
        float moveDataHeaderTextScreenRightVerticalDividerLeftAdjustment =
                gp.getCamera().worldWidthToScreenWidth(moveDataHeaderTextWorldRightVerticalDividerLeftAdjustment);
        float moveDataHeaderTextScreenX = rightVerticalDividerScreenX + verticalDividerScreenWidth
                + moveDataHeaderTextScreenRightVerticalDividerLeftAdjustment;
        float moveDataHeaderTextScreenY = verticalDividerScreenY;
        moveDataHeaderTextScreenCoords = new Vector2f(moveDataHeaderTextScreenX, moveDataHeaderTextScreenY);

        // Selected party member move data label content.
        moveDataCategoryLabelText = "Category: ";
        moveDataPowerLabelText = "Power: ";
        moveDataAccuracyLabelText = "Accuracy: ";
        moveDataSkillPointsLabelText = "SP: ";

        // Selected party member move data coordinates.
        float moveDataWorldRightVerticalDividerLeftAdjustment = 10.0f;
        float moveDataScreenRightVerticalDividerLeftAdjustment =
                gp.getCamera().worldWidthToScreenWidth(moveDataWorldRightVerticalDividerLeftAdjustment);
        float moveDataScreenX = rightVerticalDividerScreenX + verticalDividerScreenWidth
                + moveDataScreenRightVerticalDividerLeftAdjustment;
        float moveDataWorldVerticalSpacing = standardNormalCharWorldHeight * 2.25f;
        float moveDataScreenVerticalSpacing =
                gp.getCamera().worldHeightToScreenHeight(moveDataWorldVerticalSpacing);
        float topMoveDataTextScreenY = moveDataHeaderTextScreenY + moveDataScreenVerticalSpacing;

        moveDataCategoryLabelScreenCoords = new Vector2f(
                moveDataScreenX,
                topMoveDataTextScreenY
        );
        moveDataPowerLabelScreenCoords = new Vector2f(
                moveDataScreenX,
                topMoveDataTextScreenY + moveDataScreenVerticalSpacing
        );
        moveDataAccuracyLabelScreenCoords = new Vector2f(
                moveDataScreenX,
                topMoveDataTextScreenY + (moveDataScreenVerticalSpacing * 2)
        );
        moveDataSkillPointsLabelScreenCoords = new Vector2f(
                moveDataScreenX,
                topMoveDataTextScreenY + (moveDataScreenVerticalSpacing * 3)
        );
        moveDataDescriptionTextScreenCoords = new Vector2f(
                moveDataScreenX,
                topMoveDataTextScreenY + (moveDataScreenVerticalSpacing * 4)
        );

        float moveDataCategoryLabelScreenWidth = gp.getUi().calculateStringScreenWidth(
                moveDataCategoryLabelText,
                gp.getUi().getStandardFontScale(),
                gp.getUi().getStandardNormalFont()
        );
        float moveDataPowerLabelScreenWidth = gp.getUi().calculateStringScreenWidth(
                moveDataPowerLabelText,
                gp.getUi().getStandardFontScale(),
                gp.getUi().getStandardNormalFont()
        );
        float moveDataAccuracyLabelScreenWidth = gp.getUi().calculateStringScreenWidth(
                moveDataAccuracyLabelText,
                gp.getUi().getStandardFontScale(),
                gp.getUi().getStandardNormalFont()
        );
        float moveDataSkillPointsLabelScreenWidth = gp.getUi().calculateStringScreenWidth(
                moveDataSkillPointsLabelText,
                gp.getUi().getStandardFontScale(),
                gp.getUi().getStandardNormalFont()
        );

        moveDataCategoryValueScreenCoords = new Vector2f(
                moveDataScreenX + moveDataCategoryLabelScreenWidth,
                topMoveDataTextScreenY
        );
        moveDataPowerValueScreenCoords = new Vector2f(
                moveDataScreenX + moveDataPowerLabelScreenWidth,
                topMoveDataTextScreenY + moveDataScreenVerticalSpacing
        );
        moveDataAccuracyValueScreenCoords = new Vector2f(
                moveDataScreenX + moveDataAccuracyLabelScreenWidth,
                topMoveDataTextScreenY + (moveDataScreenVerticalSpacing * 2)
        );
        moveDataSkillPointsValueScreenCoords = new Vector2f(
                moveDataScreenX + moveDataSkillPointsLabelScreenWidth,
                topMoveDataTextScreenY + (moveDataScreenVerticalSpacing * 3)
        );

        float moveDataDescriptionTextLineWorldVerticalSpacing = standardNormalCharWorldHeight * 1.75f;
        moveDataDescriptionTextLineScreenVerticalSpacing =
                gp.getCamera().worldHeightToScreenHeight(moveDataDescriptionTextLineWorldVerticalSpacing);

        moveDataDescriptionTextLineScreenMaxWidth = (gp.getUiPrimaryMenuFrameS().getHeaderDividerScreenX()
                + gp.getUiPrimaryMenuFrameS().getHeaderDividerScreenWidth()) - moveDataScreenX;

        // Empty text coordinates and content.
        emptyText = "(No Skills)";
        float emptyTextScreenWidth = gp.getUi().calculateStringScreenWidth(
                emptyText,
                gp.getUi().getStandardFontScale(),
                gp.getUi().getStandardNormalFont()
        );
        float emptyTextScreenX = rightVerticalDividerScreenX + verticalDividerScreenWidth
                + (((gp.getUiPrimaryMenuFrameS().getHeaderDividerScreenX() + gp.getUiPrimaryMenuFrameS().getHeaderDividerScreenWidth()) - (rightVerticalDividerScreenX
                + verticalDividerScreenWidth)) / 2) - (emptyTextScreenWidth / 2);
        float emptyTextScreenHeaderDividerBottomAdjustment = verticalDividerScreenHeaderDividerBottomAdjustment;
        float emptyTextScreenY = gp.getUiPrimaryMenuFrameS().getHeaderDividerScreenY()
                + gp.getUiPrimaryMenuFrameS().getHeaderDividerScreenHeight()
                + emptyTextScreenHeaderDividerBottomAdjustment;
        emptyTextScreenCoords = new Vector2f(emptyTextScreenX, emptyTextScreenY);

        // Move data scroll arrow dimensions.
        float scrollArrowWorldWidth = gp.getGuiIconM().getIconById(9).getNativeSpriteWidth();                           // Both the left and right scroll arrows have the same width, so either can be referenced.
        float scrollArrowScreenWidth = gp.getCamera().worldWidthToScreenWidth(scrollArrowWorldWidth);
        float scrollArrowWorldHeight = gp.getGuiIconM().getIconById(9).getNativeSpriteHeight();                         // Both the left and right scroll arrows have the same height, so either can be referenced.
        float scrollArrowScreenHeight = gp.getCamera().worldHeightToScreenHeight(scrollArrowWorldHeight);

        // Move data scroll arrow coordinates.
        float scrollArrowScreenCenterX = rightVerticalDividerScreenX + verticalDividerScreenWidth
                + (((gp.getUiPrimaryMenuFrameS().getHeaderDividerScreenX()
                + gp.getUiPrimaryMenuFrameS().getHeaderDividerScreenWidth()) - (rightVerticalDividerScreenX
                + verticalDividerScreenWidth)) / 2);
        float scrollArrowWorldCenterlineAdjustment = 7.7f;
        float scrollArrowScreenCenterlineAdjustment =
                gp.getCamera().worldWidthToScreenWidth(scrollArrowWorldCenterlineAdjustment);
        float rightScrollArrowScreenX = scrollArrowScreenCenterX + scrollArrowScreenCenterlineAdjustment;
        float leftScrollArrowScreenX =
                scrollArrowScreenCenterX - scrollArrowScreenCenterlineAdjustment - scrollArrowScreenWidth;
        float scrollArrowScreenY = verticalDividerScreenY + verticalDividerScreenHeight - scrollArrowScreenHeight;
        rightScrollArrowScreenCoords = new Vector2f(rightScrollArrowScreenX, scrollArrowScreenY);
        leftScrollArrowScreenCoords = new Vector2f(leftScrollArrowScreenX, scrollArrowScreenY);
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
     * Refreshes the move data scroll arrows (both left and right) in the party menu.
     * This affects whether each appears as active or not.
     */
    private void refreshMoveDataScrollArrows() {

        int numMoves = gp.getEntityM().getEntityById(getSelectedPartyMenuEntity()).getMoves().size();

        if (numMoves > 0) {

            if (partyMenuMoveScrollLevel > 0) {                                                                         // Left arrow.

                gp.getGuiIconM().getIconById(9).setSelected(true);
            } else {

                gp.getGuiIconM().getIconById(9).setSelected(false);
            }

            if (partyMenuMoveScrollLevel < numMoves - 1) {                                                              // Right arrow.

                gp.getGuiIconM().getIconById(10).setSelected(true);
            } else {

                gp.getGuiIconM().getIconById(10).setSelected(false);
            }
        }
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

    public int getPartyMenuSlotScrollLevel() {
        return partyMenuSlotScrollLevel;
    }

    public int getPartyMenuMoveScrollLevel() {
        return partyMenuMoveScrollLevel;
    }
}
