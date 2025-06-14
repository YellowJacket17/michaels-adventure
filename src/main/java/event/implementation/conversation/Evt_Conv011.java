package event.implementation.conversation;

import core.GamePanel;
import event.EventConvBase;

/**
 * This class implements post-conversation logic for conversation with ID 11.
 */
public class Evt_Conv011 extends EventConvBase {

    // CONSTRUCTOR
    public Evt_Conv011(GamePanel gp) {
        super(gp);
    }


    // METHOD
    @Override
    public void run() {

        gp.getPartyS().addEntityToParty(6, true);
        gp.getEventM().cleanupConversation(1);
    }
}
