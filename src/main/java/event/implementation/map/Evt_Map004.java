package event.implementation.map;

import core.GamePanel;
import entity.EntityBase;
import entity.enumeration.EntityDirection;
import event.*;
import event.enumeration.EventType;
import event.enumeration.WarpTransitionType;
import landmark.enumeration.TallGrassColor;
import landmark.implementation.Ldm_TallGrass1;

/**
 * This class implements event logic for map with ID 4.
 */
public class Evt_Map004 extends EventMapBase {

    // CONSTRUCTOR
    public Evt_Map004(GamePanel gp) {
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

        // Map 3 warp.
        if ((col == 6) && ((row == 58)) && (direction == EntityDirection.DOWN)) {
            Ldm_TallGrass1.setInstantiationColor(TallGrassColor.GREEN);
            gp.getWarpS().initiateWarp(dt, 3, 0, 21, 11, WarpTransitionType.STEP_PORTAL, EntityDirection.DOWN);
            gp.getSoundS().playEffect("obtain");
            return true;
        }
        if ((col == 7) && ((row == 58)) && (direction == EntityDirection.DOWN)) {
            Ldm_TallGrass1.setInstantiationColor(TallGrassColor.GREEN);
            gp.getWarpS().initiateWarp(dt, 3, 0, 22, 11, WarpTransitionType.STEP_PORTAL, EntityDirection.DOWN);
            gp.getSoundS().playEffect("obtain");
            return true;
        }
        return false;
    }
}
