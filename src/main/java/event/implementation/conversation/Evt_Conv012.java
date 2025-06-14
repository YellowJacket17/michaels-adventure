package event.implementation.conversation;

import core.GamePanel;
import event.EventConvBase;

/**
 * This class implements post-conversation logic for conversation with ID 12.
 */
public class Evt_Conv012 extends EventConvBase {

    // CONSTRUCTOR
    public Evt_Conv012(GamePanel gp) {
        super(gp);
    }


    // METHOD
    @Override
    public void run() {

        if (gp.getEntityM().getParty().get(6) != null) {

            gp.getPartyS().removeEntityFromParty(6, true, true, true);
        }
        gp.getEventM().cleanupConversation(1);
    }
}
