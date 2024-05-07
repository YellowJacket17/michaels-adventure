package event.implementation.conversation;

import core.GamePanel;
import entity.EntityBase;
import event.EventConvBase;

/**
 * This class implements post-conversation logic for conversation with ID 1.
 */
public class Evt_Conv001 extends EventConvBase {

    // CONSTRUCTOR
    public Evt_Conv001(GamePanel gp) {
        super(gp);
    }


    // METHOD
    @Override
    public void run() {

        gp.getPartyS().addEntityToParty(5);
        gp.getPartyS().addEntityToParty(6);
//        gp.getPartyS().addEntityToParty(7);
        gp.getEventM().setEntityFollowTarget(7, 0);
        gp.getEventM().cleanupConversation(1);
    }
}
