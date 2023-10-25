package interaction.implementation.conversation;

import core.GamePanel;
import entity.EntityBase;
import interaction.InteractionConvBase;

/**
 * This class implements post-conversation logic for conversation with ID 1.
 */
public class Int_Conv001 extends InteractionConvBase {

    // CONSTRUCTOR
    public Int_Conv001(GamePanel gp) {
        super(gp);
    }


    // METHOD
    @Override
    public void run() {

        EntityBase entity = gp.getEntityById(5);

        if (!entity.isOnEntity()) {

            gp.transferEntity(gp.getNpc(), gp.getParty(), 5);                                                           // Add an entity to the party.
            gp.transferEntity(gp.getNpc(), gp.getParty(),6);
            gp.getInteractionM().setEntityFollowTarget(entity.getEntityId(), 0);                                        // Set an entity to follow the player character.
            gp.getInteractionM().setEntityFollowTarget(6, 0);
            gp.getInteractionM().setEntityFollowTarget(7, 0);
        }
        gp.getInteractionM().cleanupConversation(1);
    }
}
