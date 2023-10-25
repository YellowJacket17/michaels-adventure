package interaction.implementation.conversation;

import core.GamePanel;
import entity.EntityBase;
import interaction.InteractionConvBase;

/**
 * This class implements post-conversation logic for conversation with ID 2.
 */
public class Int_Conv002 extends InteractionConvBase {

    // CONSTRUCTOR
    public Int_Conv002(GamePanel gp) {
        super(gp);
    }


    // METHOD
    @Override
    public void run() {

        EntityBase entity = gp.getEntityById(5);

        if ((entity.isOnEntity())
                && (entity.getOnEntityId() == gp.getPlayer().getEntityId())) {

            gp.getInteractionM().breakFollowerChain(gp.getPlayer());
            gp.transferEntity(gp.getParty(), gp.getNpc(), entity.getEntityId());                                        // Remove an entity from the party.
            gp.transferEntity(gp.getParty(), gp.getNpc(), 6);
        }
        gp.getInteractionM().cleanupConversation(1);
    }
}
