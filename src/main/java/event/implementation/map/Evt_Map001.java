package event.implementation.map;

import combat.enumeration.EnterCombatTransitionType;
import core.GamePanel;
import entity.EntityBase;
import entity.enumeration.EntityDirection;
import event.*;
import event.enumeration.EventType;

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
            case 2:
                if (type == EventType.CLICK) {
                    if (gp.getEventM().pickupItem(0)) {
                        gp.getEntityM().removeEntity(gp.getEntityM().getObj(), target.getEntityId());
                    }
                    return true;
                }
                break;
            case 3:
                if (type == EventType.CLICK) {
                    if (gp.getEventM().pickupItem(1)) {
                        gp.getEntityM().removeEntity(gp.getEntityM().getObj(), target.getEntityId());
                    }
                    return true;
                }
                break;
            case 4:
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

        // Shadow encounter cutscene.
        if ((col == 47) && (row == 13) && (direction == EntityDirection.UP)) {
            gp.getCutsceneM().initiateCutscene(1);
//            gp.getCombatM().initiateCombat(43, 9, EnterCombatTransitionType.BASIC, "runningLate", 1);
        }
        return false;
    }
}
