package event.implementation.conversation;

import core.GamePanel;
import event.EventConvBase;

/**
 * This class implements post-conversation logic for conversation with ID 8.
 */
public class Evt_Conv008 extends EventConvBase {

    // CONSTRUCTOR
    public Evt_Conv008(GamePanel gp) {
        super(gp);
    }


    // METHOD
    @Override
    public void run() {

        gp.getPartyS().addEntityToParty(6, true);
        gp.getEventM().cleanupConversation(1);
    }
}
