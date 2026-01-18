package event.implementation.map;

import core.GamePanel;
import entity.EntityBase;
import entity.enumeration.EntityDirection;
import event.*;
import event.enumeration.EventType;
import event.enumeration.WarpTransitionType;

/**
 * This class implements event logic for map with ID 3.
 */
public class Evt_Map003 extends EventMapBase {

    // CONSTRUCTOR
    public Evt_Map003(GamePanel gp) {
        super(gp);
    }


    // METHODS
    @Override
    public boolean objInteraction(double dt, EventType type, EntityBase target) {

        switch (target.getEntityId()) {
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

        // Map 2 warp.
        if ((col == 13) && ((row == 51)) && (direction == EntityDirection.LEFT)) {
            gp.getWarpS().initiateWarp(dt, 2, 0, 57, 49, WarpTransitionType.STEP_PORTAL, EntityDirection.LEFT);
            gp.getSoundS().playEffect("obtain");
            return true;
        }
        if ((col == 13) && ((row == 52)) && (direction == EntityDirection.LEFT)) {
            gp.getWarpS().initiateWarp(dt, 2, 0, 57, 50, WarpTransitionType.STEP_PORTAL, EntityDirection.LEFT);
            gp.getSoundS().playEffect("obtain");
            return true;
        }
        if ((col == 13) && ((row == 53)) && (direction == EntityDirection.LEFT)) {
            gp.getWarpS().initiateWarp(dt, 2, 0, 57, 51, WarpTransitionType.STEP_PORTAL, EntityDirection.LEFT);
            gp.getSoundS().playEffect("obtain");
            return true;
        }
        return false;
    }
}