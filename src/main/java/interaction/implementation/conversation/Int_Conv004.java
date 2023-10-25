package interaction.implementation.conversation;

import combat.EnterCombatTransitionType;
import core.GamePanel;
import interaction.InteractionConvBase;

/**
 * This class implements post-conversation logic for conversation with ID 4.
 */
public class Int_Conv004 extends InteractionConvBase {

    // CONSTRUCTOR
    public Int_Conv004(GamePanel gp) {
        super(gp);
    }


    // METHOD
    @Override
    public void run() {

        gp.getCombatM().initiateCombat(18, 18, EnterCombatTransitionType.BASIC, 2, gp.getEntityById(8));
    }
}
