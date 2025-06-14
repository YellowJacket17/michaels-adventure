package event.implementation.conversation;

import core.GamePanel;
import event.EventConvBase;

/**
 * This class implements post-conversation logic for conversation with ID 10.
 */
public class Evt_Conv010 extends EventConvBase {

    // CONSTRUCTOR
    public Evt_Conv010(GamePanel gp) {
        super(gp);
    }


    // METHOD
    @Override
    public void run() {

        if (gp.getEntityM().getParty().get(7) != null) {

            gp.getPartyS().removeEntityFromParty(7, true, true, true);
        }
        gp.getEventM().cleanupConversation(1);
    }
}
