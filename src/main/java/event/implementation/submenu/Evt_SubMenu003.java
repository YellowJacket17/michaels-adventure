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
        Set<Integer> keySet = gp.getEntityM().getParty().keySet();                                                      // Extract keys from party map.
        Integer[] keyArray = keySet.toArray(new Integer[keySet.size()]);                                                // Convert set of keys to array of keys.
        int selectedEntityId =
                gp.getEntityM().getParty().get(keyArray[
                        gp.getUi().getPartyMenuScrollLevel() + (gp.getUi().getPartySlotSelected() - 1)]).getEntityId();

        for (EntityBase candidateEntity : gp.getEntityM().getParty().values()) {

            if (candidateEntity.getEntityId() != selectedEntityId) {

                options.add(candidateEntity.getEntityId());
            }
        }

        if (selectedIndex < options.size()) {

            gp.getPartyS().swapEntityInParty(selectedEntityId, options.get(selectedIndex), true);
            gp.getUi().refreshSelectedPartyMenuEntity();
        }
        gp.setPrimaryGameState(PrimaryGameState.PARTY_MENU);
        gp.getEventM().cleanupSubmenu(2);
    }
}
