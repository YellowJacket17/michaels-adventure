package event.implementation.map;

import core.GamePanel;
import entity.EntityBase;
import entity.EntityDirection;
import event.EventMapBase;
import event.EventType;
import event.WarpTransitionType;

/**
 * This class implements event logic for map with ID 0.
 */
public class Evt_Map000 extends EventMapBase {

    // CONSTRUCTOR
    public Evt_Map000(GamePanel gp) {
        super(gp);
    }


    // METHODS
    @Override
    public boolean objInteraction(double dt, EventType type, EntityBase target) {

        return false;
    }


    @Override
    public boolean npcInteraction(double dt, EventType type, EntityBase target) {

        switch (target.getEntityId()) {
            case 5:
                if ((type == EventType.CLICK) && (!target.isOnEntity())) {

                    gp.getEventM().talkToNpc(target, gp.getPlayer().getDirectionCurrent(), 1);                          // Initiate the conversation with the NPC.
                    return true;                                                                                        // An NPC is being interacted with.
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
                        && (target.getOnEntityId() == gp.getPlayer().getEntityId())) {

                    gp.getEventM().talkToNpc(target, gp.getPlayer().getDirectionCurrent(), 2);                          // Initiate the conversation with the NPC.
                    return true;                                                                                        // A party member is being interacted with.
                }
                break;
        }
        return false;
    }


    @Override
    public boolean tileInteraction(double dt, EventType type, int col, int row, EntityDirection direction) {

        // Warp to map 1.
        if ((type == EventType.STEP) && ((col == 1) || (col == 2)) && (row == 0) && (direction == EntityDirection.UP)) {
            gp.getWarpS().initiateWarp(dt, 1, 25, 25, WarpTransitionType.STEP_PORTAL, EntityDirection.DOWN, 0);
            gp.playSE(0);
            return true;
        }

        // Test basic warp,
        if ((type == EventType.STEP) && (col == 14) && (row == 1) && (direction == EntityDirection.RIGHT)) {
            gp.getWarpS().initiateWarp(0, 1, 1);
            return true;
        }

        return false;
    }
}
