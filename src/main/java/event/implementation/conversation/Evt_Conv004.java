package event.implementation.conversation;

import combat.enumeration.EnterCombatTransitionType;
import core.GamePanel;
import event.EventConvBase;

/**
 * This class implements post-conversation logic for conversation with ID 7.
 */
public class Evt_Conv004 extends EventConvBase {

    // CONSTRUCTOR
    public Evt_Conv004(GamePanel gp) {
        super(gp);
    }


    // METHOD
    @Override
    public void run() {

        gp.getEventM().cleanupConversation(1);
        gp.getCombatM().initiateCombat(43, 9, EnterCombatTransitionType.BASIC, "runningLate",
                gp.getEntityM().getEntityById(1));
    }
}
