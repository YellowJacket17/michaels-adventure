package interaction.implementation.map;

import combat.EnterCombatTransitionType;
import core.GamePanel;
import entity.EntityBase;
import entity.EntityDirection;
import interaction.*;
import item.implementation.Itm_Controller;
import item.implementation.Itm_Key;
import item.ItemBase;

/**
 * This class implements interaction logic for map with ID 1.
 */
public class Int_Map001 extends InteractionMapBase {

    // CONSTRUCTOR
    public Int_Map001(GamePanel gp) {
        super(gp);
    }


    // METHODS
    @Override
    public boolean objInteraction(InteractionType type, EntityBase target) {

        switch (target.getEntityId()) {
            case 1:
                if (type == InteractionType.CLICK) {
                    ItemBase item = new Itm_Controller(gp);
                    boolean added = gp.getInteractionM().pickupItem(item);
                    if (added) {
                        gp.removeEntity(gp.getObj(), target.getEntityId());                                             // Add the object to the array of removed entities so that it no longer gets loaded onto the map.
                    }
                    return true;                                                                                        // An object is being interacted with.
                }
                break;
            case 2:
                if (type == InteractionType.CLICK) {
                    ItemBase item = new Itm_Key(gp);
                    boolean added = gp.getInteractionM().pickupItem(item);
                    if (added) {
                        gp.removeEntity(gp.getObj(), target.getEntityId());
                    }
                    return true;
                }
                break;
        }
        return false;
    }


    @Override
    public boolean npcInteraction(InteractionType type, EntityBase target) {

        switch (target.getEntityId()) {
            case 4:
                if (type == InteractionType.CLICK) {
                    gp.getInteractionM().talkToNpc(target, gp.getPlayer().getDirectionCurrent(), 0);
                    return true;
                }
                break;
            case 8:
                if (type == InteractionType.STEP) {
                    gp.getInteractionM().talkToNpc(target, gp.getPlayer().getDirectionCurrent(), 4);
                    return true;
                }
                break;
        }
        return false;
    }


    @Override
    public boolean partyInteraction(InteractionType type, EntityBase target) {

        switch (target.getEntityId()) {
            case 5:
                if ((type == InteractionType.CLICK)
                        && (target.isOnEntity())
                        && (target.getOnEntityId() == gp.getPlayer().getEntityId())) {

                    gp.getInteractionM().talkToNpc(target, gp.getPlayer().getDirectionCurrent(), 3);
                    return true;
                }
                break;
        }
        return false;
    }


    @Override
    public boolean tileInteraction(InteractionType type, int col, int row, EntityDirection direction) {

        // Trigger a message.
        if ((type == InteractionType.CLICK) && (col == 22) && (row == 28)) {

            gp.getInteractionM().displayMessage("Looks like something will happen if this tile is stepped on...");
            return true;
        }

        // Warp to map 0.
        if ((type == InteractionType.STEP) && (col == 25) && (row == 25)) {

            gp.getWarpS().initiateWarp(0, 1, 1, WarpTransitionType.STEP_PORTAL, EntityDirection.DOWN, 1);

            gp.playSE(0);
            return true;
        }

        // Trigger a cutscene.
        if ((type == InteractionType.STEP) && (col == 22) && (row == 28)) {

            gp.getCutsceneM().triggerCutscene(1);
            return true;
        }

        // Trigger combat.
        if ((type == InteractionType.STEP) && (col == 25) && (row == 28)) {

            gp.getCombatM().initiateCombat(18, 18, EnterCombatTransitionType.BASIC, 2, gp.getEntityById(4), gp.getEntityById(8));
            return true;
        }

        return false;
    }
}
