package event.implementation.conversation;

import core.GamePanel;
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

        if (gp.getEntityM().getParty().get(5) != null) {

            gp.getPartyS().removeEntityFromParty(5, true, true, true);
        }
        gp.getEventM().cleanupConversation(1);
    }
}
