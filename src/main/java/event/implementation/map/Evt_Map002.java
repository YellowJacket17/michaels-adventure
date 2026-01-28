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
 * This class implements event logic for map with ID 2.
 */
public class Evt_Map002 extends EventMapBase {

    // CONSTRUCTOR
    public Evt_Map002(GamePanel gp) {
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

        // Map 1 warp.
        if ((col == 58) && ((row == 5)) && (direction == EntityDirection.RIGHT)) {
            gp.getWarpS().initiateWarp(dt, 1, 0, 49, 8, WarpTransitionType.STEP_PORTAL, EntityDirection.LEFT);
            gp.getSoundS().playEffect("obtain");
            return true;
        }
        if ((col == 58) && ((row == 6)) && (direction == EntityDirection.RIGHT)) {
            gp.getWarpS().initiateWarp(dt, 1, 0, 49, 9, WarpTransitionType.STEP_PORTAL, EntityDirection.LEFT);
            gp.getSoundS().playEffect("obtain");
            return true;
        }

        // Inner warp points (first set).
        if ((col == 49) && (row == 11)) {
            gp.getWarpS().initiateWarp(
                    dt, 2, gp.getMapM().getLoadedMap().getMapState(), 46, 4,
                    WarpTransitionType.STEP_PORTAL, EntityDirection.DOWN);
            gp.getSoundS().playEffect("obtain");
            return true;
        }
        if ((col == 43) && (row == 5)) {
            gp.getWarpS().initiateWarp(
                    dt, 2, gp.getMapM().getLoadedMap().getMapState(), 45, 15,
                    WarpTransitionType.STEP_PORTAL, EntityDirection.DOWN);
            gp.getSoundS().playEffect("obtain");
            return true;
        }

        // Inner warp points (second set).
        if ((col == 56) && (row == 24)) {
            gp.getWarpS().initiateWarp(
                    dt, 2, gp.getMapM().getLoadedMap().getMapState(), 46, 4,
                    WarpTransitionType.STEP_PORTAL, EntityDirection.DOWN);
            gp.getSoundS().playEffect("obtain");
            return true;
        }
        if ((col == 32) && (row == 30)) {
            gp.getWarpS().initiateWarp(
                    dt, 2, gp.getMapM().getLoadedMap().getMapState(), 46, 4,
                    WarpTransitionType.STEP_PORTAL, EntityDirection.DOWN);
            gp.getSoundS().playEffect("obtain");
            return true;
        }
        if ((col == 38) && (row == 25)) {
            gp.getWarpS().initiateWarp(
                    dt, 2, gp.getMapM().getLoadedMap().getMapState(), 17, 4,
                    WarpTransitionType.STEP_PORTAL, EntityDirection.DOWN);
            gp.getSoundS().playEffect("obtain");
            return true;
        }

        // Inner warp points (third set).
        if ((col == 2) && (row == 21)) {
            gp.getWarpS().initiateWarp(
                    dt, 2, gp.getMapM().getLoadedMap().getMapState(), 46, 4,
                    WarpTransitionType.STEP_PORTAL, EntityDirection.DOWN);
            gp.getSoundS().playEffect("obtain");
            return true;
        }
        if ((col == 39) && (row == 5)) {
            gp.getWarpS().initiateWarp(
                    dt, 2, gp.getMapM().getLoadedMap().getMapState(), 46, 4,
                    WarpTransitionType.STEP_PORTAL, EntityDirection.DOWN);
            gp.getSoundS().playEffect("obtain");
            return true;
        }
        if ((col == 10) && (row == 20)) {
            gp.getWarpS().initiateWarp(
                    dt, 2, gp.getMapM().getLoadedMap().getMapState(), 30, 24,
                    WarpTransitionType.STEP_PORTAL, EntityDirection.UP);
            gp.getSoundS().playEffect("obtain");
            return true;
        }

        // Inner warp points (fourth set).
        if ((col == 35) && (row == 19)) {
            gp.getWarpS().initiateWarp(
                    dt, 2, gp.getMapM().getLoadedMap().getMapState(), 46, 4,
                    WarpTransitionType.STEP_PORTAL, EntityDirection.DOWN);
            gp.getSoundS().playEffect("obtain");
            return true;
        }
        if ((col == 18) && (row == 30)) {
            gp.getWarpS().initiateWarp(
                    dt, 2, gp.getMapM().getLoadedMap().getMapState(), 46, 4,
                    WarpTransitionType.STEP_PORTAL, EntityDirection.DOWN);
            gp.getSoundS().playEffect("obtain");
            return true;
        }
        if ((col == 15) && (row == 18)) {
            gp.getWarpS().initiateWarp(
                    dt, 2, gp.getMapM().getLoadedMap().getMapState(), 6, 30,
                    WarpTransitionType.STEP_PORTAL, EntityDirection.DOWN);
            gp.getSoundS().playEffect("obtain");
            return true;
        }

        // Inner warp points (fifth set).
        if ((col == 3) && (row == 57)) {
            gp.getWarpS().initiateWarp(
                    dt, 2, gp.getMapM().getLoadedMap().getMapState(), 34, 12,
                    WarpTransitionType.STEP_PORTAL, EntityDirection.UP);
            gp.getSoundS().playEffect("obtain");
            return true;
        }
        if ((col == 34) && (row == 12)) {
            gp.getWarpS().initiateWarp(
                    dt, 2, gp.getMapM().getLoadedMap().getMapState(), 3, 57,
                    WarpTransitionType.STEP_PORTAL, EntityDirection.LEFT);
            gp.getSoundS().playEffect("obtain");
            return true;
        }
        if ((col == 55) && (row == 48)) {
            gp.getWarpS().initiateWarp(
                    dt, 2, gp.getMapM().getLoadedMap().getMapState(), 46, 4,
                    WarpTransitionType.STEP_PORTAL, EntityDirection.DOWN);
            gp.getSoundS().playEffect("obtain");
            return true;
        }

        // Map 3 warp.
        if ((col == 58) && ((row == 49)) && (direction == EntityDirection.RIGHT)) {
            Ldm_TallGrass1.setInstantiationColor(TallGrassColor.GREEN);
            gp.getWarpS().initiateWarp(dt, 3, 0, 14, 51, WarpTransitionType.STEP_PORTAL, EntityDirection.RIGHT);
            gp.getSoundS().playEffect("obtain");
            return true;
        }
        if ((col == 58) && ((row == 50)) && (direction == EntityDirection.RIGHT)) {
            Ldm_TallGrass1.setInstantiationColor(TallGrassColor.GREEN);
            gp.getWarpS().initiateWarp(dt, 3, 0, 14, 52, WarpTransitionType.STEP_PORTAL, EntityDirection.RIGHT);
            gp.getSoundS().playEffect("obtain");
            return true;
        }
        if ((col == 58) && ((row == 51)) && (direction == EntityDirection.RIGHT)) {
            Ldm_TallGrass1.setInstantiationColor(TallGrassColor.GREEN);
            gp.getWarpS().initiateWarp(dt, 3, 0, 14, 53, WarpTransitionType.STEP_PORTAL, EntityDirection.RIGHT);
            gp.getSoundS().playEffect("obtain");
            return true;
        }
        return false;
    }
}
