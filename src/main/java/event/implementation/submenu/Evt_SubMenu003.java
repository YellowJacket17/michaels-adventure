package event.implementation.submenu;

import core.GamePanel;
import core.enumeration.PrimaryGameState;
import entity.EntityBase;
import event.EventSubMenuBase;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * This class implements post-selection event logic for sub-menu with ID 3.
 * Note that a sub-menu ID of 3 is used for swapping entities in the party menu screen.
 */
public class Evt_SubMenu003 extends EventSubMenuBase {

    // CONSTRUCTOR
    public Evt_SubMenu003(GamePanel gp) {
        super(gp);
    }


    // METHOD
    @Override
    public void run(int selectedIndex) {

        List<Integer> options = new ArrayList<>();

        for (EntityBase candidateEntity : gp.getEntityM().getParty().values()) {

            if (candidateEntity.getEntityId() != gp.getUiPartyMenuS().getSelectedPartyMenuEntity()) {

                options.add(candidateEntity.getEntityId());
            }
        }

        if (selectedIndex < options.size()) {

            gp.getPartyS().swapEntityInParty(
                    gp.getUiPartyMenuS().getSelectedPartyMenuEntity(), options.get(selectedIndex), true);
            gp.getUiPartyMenuS().setPartyMenuSlotSelected(gp.getUiPartyMenuS().getPartySlotSelected());
        }
        gp.setPrimaryGameState(PrimaryGameState.PARTY_MENU);
        gp.getEventM().cleanupSubmenu(2);
    }
}
