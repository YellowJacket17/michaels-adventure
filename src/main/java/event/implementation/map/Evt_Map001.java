package event.implementation.map;

import core.GamePanel;
import entity.EntityBase;
import entity.enumeration.EntityDirection;
import event.*;
import event.enumeration.EventType;
import event.enumeration.WarpTransitionType;

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
                    if (gp.getEventM().pickupItem(0)) {
                        gp.getEntityM().removeEntity(gp.getEntityM().getObj(), target.getEntityId());
                    }
                    return true;
                }
                break;
            case 2:
                if (type == EventType.CLICK) {
                    if (gp.getEventM().pickupItem(1)) {
                        gp.getEntityM().removeEntity(gp.getEntityM().getObj(), target.getEntityId());
                    }
                    return true;
                }
                break;
            case 3:
                if (type == EventType.CLICK) {
                    if (gp.getEventM().pickupItem(2)) {
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
        }
        return false;
    }


    @Override
    public boolean partyInteraction(double dt, EventType type, EntityBase target) {

        switch (target.getEntityId()) {
        }
        return false;
    }


    @Override
    public boolean tileInteraction(double dt, EventType type, int col, int row, EntityDirection direction) {

        // Shadow encounter.
        if ((col == 47) && (row == 13) && (direction == EntityDirection.UP)) {
            if (gp.getMapM().getLoadedMap().getMapState() == 0) {
//                gp.getCutsceneM().initiateCutscene(4);
                gp.getCombatM().initiateCombat(1, 43, 9, "runningLate", 4);
                return true;
            } else if (gp.getMapM().getLoadedMap().getMapState() == 1) {
                gp.getCombatM().initiateCombat(1, 43, 9, "runningLate", 4);
                return true;
            }
        }

        // Map 2 warp.
        if ((col == 50) && ((row == 8)) && (direction == EntityDirection.RIGHT)) {
            gp.getWarpS().initiateWarp(dt, 2, 57, 5, WarpTransitionType.STEP_PORTAL, EntityDirection.LEFT);
            gp.getSoundS().playEffect("footsteps");
            return true;
        }
        if ((col == 50) && ((row == 9)) && (direction == EntityDirection.RIGHT)) {
            gp.getWarpS().initiateWarp(dt, 2, 57, 6, WarpTransitionType.STEP_PORTAL, EntityDirection.LEFT);
            gp.getSoundS().playEffect("footsteps");
            return true;
        }
        return false;
    }
}
