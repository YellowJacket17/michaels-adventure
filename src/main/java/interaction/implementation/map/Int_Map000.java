package interaction.implementation.map;

import core.GamePanel;
import entity.EntityBase;
import entity.EntityDirection;
import interaction.InteractionMapBase;
import interaction.InteractionType;
import interaction.WarpTransitionType;

/**
 * This class implements interaction logic for map with ID 0.
 */
public class Int_Map000 extends InteractionMapBase {

    // CONSTRUCTOR
    public Int_Map000(GamePanel gp) {
        super(gp);
    }


    // METHODS
    @Override
    public boolean objInteraction(InteractionType type, EntityBase target) {

        return false;
    }


    @Override
    public boolean npcInteraction(InteractionType type, EntityBase target) {

        switch (target.getEntityId()) {
            case 5:
                if ((type == InteractionType.CLICK) && (!target.isOnEntity())) {

                    gp.getInteractionM().talkToNpc(target, gp.getPlayer().getDirectionCurrent(), 1);                    // Initiate the conversation with the NPC.
                    return true;                                                                                        // An NPC is being interacted with.
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

                    gp.getInteractionM().talkToNpc(target, gp.getPlayer().getDirectionCurrent(), 2);                    // Initiate the conversation with the NPC.
                    return true;                                                                                        // A party member is being interacted with.
                }
                break;
        }
        return false;
    }


    @Override
    public boolean tileInteraction(InteractionType type, int col, int row, EntityDirection direction) {

        // Warp to map 1.
        if ((type == InteractionType.STEP) && ((col == 1) || (col == 2)) && (row == 0) && (direction == EntityDirection.UP)) {
            gp.getWarpS().initiateWarp(1, 25, 25, WarpTransitionType.STEP_PORTAL, EntityDirection.DOWN, 0);
            gp.playSE(0);
            return true;
        }

        // Test basic warp,
        if ((type == InteractionType.STEP) && (col == 14) && (row == 1) && (direction == EntityDirection.RIGHT)) {
            gp.getWarpS().initiateWarp(0, 1, 1);
            return true;
        }

        return false;
    }
}
