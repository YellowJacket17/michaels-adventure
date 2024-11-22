package event.implementation.conversation;

import core.GamePanel;
import event.EventConvBase;

/**
 * This class implements post-conversation logic for conversation with ID 6.
 */
public class Evt_Conv006 extends EventConvBase {

    // CONSTRUCTOR
    public Evt_Conv006(GamePanel gp) {
        super(gp);
    }


    // METHOD
    @Override
    public void run() {

        gp.getPartyS().addEntityToParty(7, true);
        gp.getEventM().cleanupConversation(1);
    }
}
