package event.implementation.conversation;

import core.GamePanel;
import event.EventConvBase;

/**
 * This class implements post-conversation logic for conversation with ID 4.
 */
public class Evt_Conv004 extends EventConvBase {

    // CONSTRUCTOR
    public Evt_Conv004(GamePanel gp) {
        super(gp);
    }


    // METHOD
    @Override
    public void run() {

        gp.getPartyS().addEntityToParty(5, true);
        gp.getEventM().cleanupConversation(1);
    }
}
