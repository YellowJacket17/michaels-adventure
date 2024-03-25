package event.implementation.conversation;

import core.GamePanel;
import entity.EntityBase;
import event.EventConvBase;

/**
 * This class implements post-conversation logic for conversation with ID 2.
 */
public class Evt_Conv002 extends EventConvBase {

    // CONSTRUCTOR
    public Evt_Conv002(GamePanel gp) {
        super(gp);
    }


    // METHOD
    @Override
    public void run() {

//        if (gp.getParty().get(7) != null) {
//            gp.getPartyS().removeEntityFromParty(7, true);
//        }
//
//        if (gp.getParty().get(6) != null) {
//            gp.getPartyS().removeEntityFromParty(6, false);
//        }
//
//        if (gp.getParty().get(5) != null) {
//            gp.getPartyS().removeEntityFromParty(5, false);
//        }
        gp.getPartyS().removeAllEntitiesFromParty(true);
        gp.getEventM().cleanupConversation(1);
    }
}
