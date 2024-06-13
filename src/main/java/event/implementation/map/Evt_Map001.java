package event.implementation.map;

import combat.EnterCombatTransitionType;
import core.GamePanel;
import entity.EntityBase;
import entity.EntityDirection;
import event.*;
import event.support.WarpTransitionType;
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
                    ItemBase item = new Itm_Controller(gp);
                    boolean added = gp.getEventM().pickupItem(item);
                    if (added) {
                        gp.getEntityM().removeEntity(gp.getEntityM().getObj(), target.getEntityId());                   // Add the object to the array of removed entities so that it no longer gets loaded onto the map.
                    }
                    return true;                                                                                        // An object is being interacted with.
                }
                break;
            case 2:
                if (type == EventType.CLICK) {
                    ItemBase item = new Itm_Key(gp);
                    boolean added = gp.getEventM().pickupItem(item);
                    if (added) {
                        gp.getEntityM().removeEntity(gp.getEntityM().getObj(), target.getEntityId());
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
            case 4:
                if (type == EventType.CLICK) {
                    gp.getEventM().talkToNpc(target, gp.getEntityM().getPlayer().getDirectionCurrent(), 0);
                    return true;
                }
                break;
            case 8:
                if (type == EventType.STEP) {
                    gp.getEventM().talkToNpc(target, gp.getEntityM().getPlayer().getDirectionCurrent(), 4);
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

                    gp.getEventM().talkToNpc(target, gp.getEntityM().getPlayer().getDirectionCurrent(), 3);
                    return true;
                }
                break;
        }
        return false;
    }


    @Override
    public boolean tileInteraction(double dt, EventType type, int col, int row, EntityDirection direction) {

        // Trigger a message.
        if ((type == EventType.CLICK) && (col == 22) && (row == 28)) {

            gp.getEventM().displayMessage("Looks like something will happen if this tile is stepped on...");
            return true;
        }

        // Warp to map 0.
        if ((type == EventType.STEP) && (col == 25) && (row == 25)) {

            gp.getWarpS().initiateWarp(dt, 0, 0, 1, 1, WarpTransitionType.STEP_PORTAL, EntityDirection.DOWN);
            gp.getSoundS().playEffect("testEffect1");
            return true;
        }

        // Trigger a cutscene.
        if ((type == EventType.STEP) && (col == 22) && (row == 28)) {

            gp.getCutsceneM().initiateCutscene(1);
//            gp.getCameraS().setCameraScroll(0, 0, 5);
            gp.getFadeS().initiateFlash(0.15, 0.15, 0.02, new Vector3f(255, 255, 255));
            return true;
        }

        // Trigger combat.
        if ((type == EventType.STEP) && (col == 25) && (row == 28)) {

            gp.getCombatM().initiateCombat(18, 18, EnterCombatTransitionType.BASIC, "testTrack4",
                    gp.getEntityM().getEntityById(4), gp.getEntityM().getEntityById(8));
            return true;
        }

        return false;
    }
}
