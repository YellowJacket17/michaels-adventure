package event.implementation.map;

import combat.enumeration.EnterCombatTransitionType;
import core.GamePanel;
import entity.EntityBase;
import entity.enumeration.EntityDirection;
import event.*;
import event.enumeration.EventType;
import event.enumeration.WarpTransitionType;
import item.implementation.Itm_Controller;
import item.implementation.Itm_Key;
import item.ItemBase;
import org.joml.Vector3f;

/**
 * This class implements event logic for map with ID 1.
 */
public class Evt_Map001 extends EventMapBase {

    // CONSTRUCTOR
    public Evt_Map001(GamePanel gp) {
        super(gp);
    }


    // METHODS
    @Override
    public boolean objInteraction(double dt, EventType type, EntityBase target) {

        switch (target.getEntityId()) {
            case 1:
                if (type == EventType.CLICK) {
                    boolean added = gp.getEventM().pickupItem(1);
                    if (added) {
                        gp.getEntityM().removeEntity(gp.getEntityM().getObj(), target.getEntityId());                   // Add the object to the array of removed entities so that it no longer gets loaded onto the map.
                    }
                    return true;                                                                                        // An object is being interacted with.
                }
                break;
            case 2:
                if (type == EventType.CLICK) {
                    boolean added = gp.getEventM().pickupItem(0);
                    if (added) {
                        gp.getEntityM().removeEntity(gp.getEntityM().getObj(), target.getEntityId());
                    }
                    return true;
                }
                break;
            case 3:
                if (type == EventType.CLICK) {
                    if (gp.getEntityM().getPlayer().searchItemInventory(0)) {
                        if (!gp.getIllustrationS().isIllustrationActive()) {
                            gp.getCutsceneM().initiateCutscene(2);
                        }
                    } else {
                        gp.getDialogueR().initiateStandardMessage("A key is needed to open this chest.", true);
                    }
                    return true;
                }
                break;
        }
        return false;
    }


    @Override
    public boolean npcInteraction(double dt, EventType type, EntityBase target) {

        switch (target.getEntityId()) {
            case 8:
                if (type == EventType.CLICK) {
                    gp.getEventM().talkToNpc(target, 7);
                    return true;
                }
                break;
        }
        return false;
    }


    @Override
    public boolean partyInteraction(double dt, EventType type, EntityBase target) {

        switch (target.getEntityId()) {
            case 5:
                if ((type == EventType.CLICK)
                        && (target.isOnEntity())
                        && (target.getOnEntityId() == gp.getEntityM().getPlayer().getEntityId())) {

                    gp.getEventM().talkToNpc(target, 6);
                    return true;
                }
                break;
            case 7:
                if ((type == EventType.CLICK)
                        && (target.isOnEntity())
                        && (target.getOnEntityId() == gp.getEntityM().getPlayer().getEntityId())) {

                    gp.getEventM().talkToNpc(target, 13);
                    return true;
                }
                break;
            case 6:
                if ((type == EventType.CLICK)
                        && (target.isOnEntity())
                        && (target.getOnEntityId() == gp.getEntityM().getPlayer().getEntityId())) {

                    gp.getEventM().talkToNpc(target, 14);
                    return true;
                }
                break;
        }
        return false;
    }


    @Override
    public boolean tileInteraction(double dt, EventType type, int col, int row, EntityDirection direction) {

        // Warp to map 0.
        if ((type == EventType.STEP) && (col == 25) && (row == 25)) {

            gp.getWarpS().initiateWarp(dt, 0, 0, 1, 1, WarpTransitionType.STEP_PORTAL, EntityDirection.DOWN);
            gp.getSoundS().playEffect("testEffect1");
            return true;
        }

        return false;
    }
}
