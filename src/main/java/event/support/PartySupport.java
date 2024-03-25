package event.support;

import core.GamePanel;
import entity.EntityBase;
import entity.EntityDirection;
import org.joml.Vector2i;
import utility.LimitedArrayList;

/**
 * This class contains methods to facilitate player party management.
 * The public methods in this class serve as primary endpoints to use when programming in-game events.
 */
public class PartySupport {

    // FIELD
    private final GamePanel gp;


    // CONSTRUCTOR
    public PartySupport(GamePanel gp) {
        this.gp = gp;
    }


    // METHODS
    /**
     * Adds an NPC entity to the player's party.
     * Note that if the added NPC causes the number of party members to exceed the allowable number of active party
     * members, then the entity will become hidden.
     *
     * @param entityId ID of the entity to add
     */
    public void addEntityToParty(int entityId) {

        gp.transferEntity(gp.getNpc(), gp.getParty(), entityId);                                                        // Add target entity to party map.

        if (gp.getParty().size() <= gp.getNumActivePartyMembers()) {

            gp.getEventM().setEntityFollowTarget(entityId, gp.getPlayer().getEntityId());                               // Entity is in active party, so set to follow player entity.
        } else {

            gp.getParty().get(entityId).setHidden(true);                                                                // Entity is in reserve party, so hide.
        }
    }


    /**
     * Removes an entity from the player's party.
     * The removed entity becomes an NPC.
     * States of remaining party members (world positions, directions, and active/reserve status) will be adjusted
     * accordingly as those behind the removed party member are shifted up.
     *
     * @param entityId ID of the entity to remove
     * @param show sets removed party member as visible (true) or retains existing hidden state (false)
     */
    public void removeEntityFromParty(int entityId, boolean show) {

        LimitedArrayList<Vector2i> partyEntityTiles = new LimitedArrayList<>(gp.getParty().size());                     // List to store party entity world positions at each index in party map.
        LimitedArrayList<EntityDirection> partyEntityDirections = new LimitedArrayList<>(gp.getParty().size());         // List to store party entity directions at each index in party map.

        for (EntityBase entity : gp.getParty().values()) {                                                              // Store entity world positions and directions at each index in party map.

            partyEntityTiles.add(new Vector2i(entity.getCol(), entity.getRow()));
            partyEntityDirections.add(entity.getDirectionCurrent());
        }
        gp.getEventM().breakFollowerChain(gp.getPlayer());                                                              // Break chain of party entities following player entity (will rebuild later).
        gp.transferEntity(gp.getParty(), gp.getNpc(), entityId);                                                        // Remove target entity from party map.

        if (show) {

            gp.getNpc().get(entityId).setHidden(false);
        }
        int entityIndex = 0;                                                                                            // Iterator to track which index of party map is currently being worked on.

        for (EntityBase entity : gp.getParty().values()) {                                                              // Set party entity world positions and directions to match those of party entities originally at said index of party map.

            entity.setCol(partyEntityTiles.get(entityIndex).x);                                                         // Set entity to the same tile as entity previously in its position/index.
            entity.setRow(partyEntityTiles.get(entityIndex).y);                                                         // ^^^
            entity.setDirectionCurrent(partyEntityDirections.get(entityIndex));                                         // Set entity to face the same direction as entity previously in its position/index.

            if (entityIndex < gp.getNumActivePartyMembers()) {

                gp.getEventM().setEntityFollowTarget(entity.getEntityId(), gp.getPlayer().getEntityId());               // Set active party members as following player entity.
                entity.setHidden(false);                                                                                // Set active party members as visible.
            } else {

                entity.setHidden(true);
            }
            entityIndex++;
        }
    }


    /**
     * Swaps the positions of two entities in the player's party.
     * Swapped party members will assume each other's world position, direction, and hidden state.
     *
     * @param swapperEntityId ID of the primary entity to move
     * @param swappeePosition position (index) to move primary entity to (i.e., location of secondary entity that
     *                        primary entity will trade places with)
     */
    public void swapEntityInParty(int swapperEntityId, int swappeePosition) {

        LimitedArrayList<EntityBase> originalParty = new LimitedArrayList<>(gp.getParty().maxCapacity());               // Temporary list to store all party entities in party map, retaining original ordering.
        int swapperPosition = 0;
        int i = 0;                                                                                                      // Iterator to track which index of party map is currently being worked on.

        for (EntityBase entity : gp.getParty().values()) {                                                              // Store each party entity in the temporary list of party entities.

            originalParty.add(entity);

            if (entity.getEntityId() == swapperEntityId) {

                swapperPosition = i;                                                                                    // Original position/index of primary entity being put into target/secondary position/index.
            }
            i++;
        }

        if (swapperPosition != swappeePosition) {                                                                       // No need to run further logic if attempting to swap a party entity with itself.

            LimitedArrayList<Vector2i> partyEntityTiles = new LimitedArrayList<>(gp.getParty().size());                 // List to store party entity world positions at each index in party map.
            LimitedArrayList<EntityDirection> partyEntityDirections = new LimitedArrayList<>(gp.getParty().size());     // List to store party entity directions at each index in party map.

            for (EntityBase entity : gp.getParty().values()) {                                                          // Store entity world positions and directions at each index in party map.

                partyEntityTiles.add(new Vector2i(entity.getCol(), entity.getRow()));
                partyEntityDirections.add(entity.getDirectionCurrent());
            }
            gp.getEventM().breakFollowerChain(gp.getPlayer());                                                          // Break chain of party entities following player entity (will rebuild later).
            gp.getParty().clear();                                                                                      // Clear party map to re-add party entities in new ordering.

            for (int j = 0; j < originalParty.size(); j++) {                                                            // Add party entities back to party map in new ordering.

                if (j == swapperPosition) {                                                                             // Position/index to move secondary entity to.

                    gp.getParty().put(originalParty.get(swappeePosition).getEntityId(),
                            originalParty.get(swappeePosition));
                } else if (j == swappeePosition) {                                                                      // Position/index to move primary entity to.

                    gp.getParty().put(originalParty.get(swapperPosition).getEntityId(),
                            originalParty.get(swapperPosition));
                } else {                                                                                                // Party entities other than primary and secondary retain original positions/indices.

                    gp.getParty().put(originalParty.get(j).getEntityId(), originalParty.get(j));
                }
            }
            int entityIndex = 0;                                                                                        // Iterator to track which index of new party map is currently being worked on.

            for (EntityBase entity : gp.getParty().values()) {                                                          // Set party entity world positions and directions to match those of party entities originally at said index of party map.

                entity.setCol(partyEntityTiles.get(entityIndex).x);                                                     // Set entity to the same tile as entity previously in its position/index.
                entity.setRow(partyEntityTiles.get(entityIndex).y);                                                     // ^^^
                entity.setDirectionCurrent(partyEntityDirections.get(entityIndex));                                     // Set entity to face the same direction as entity previously in its position/index.

                if (entityIndex < gp.getNumActivePartyMembers()) {

                    gp.getEventM().setEntityFollowTarget(entity.getEntityId(), gp.getPlayer().getEntityId());           // Set active party members as following player entity.
                    entity.setHidden(false);                                                                            // Set active party members as visible.
                } else {

                    entity.setHidden(true);
                }
                entityIndex++;
            }
        }
    }


    /**
     * Removes all entities from the player's party.
     * The removed entities become NPCs.
     * Note that any removed hidden entities will remain hidden, and vice versa.
     * World positions and directions of removed entities will remain as-is.
     *
     * @param showAll sets all removed party members as visible (true) or retains their existing hidden state (false)
     */
    public void removeAllEntitiesFromParty(boolean showAll) {

        gp.getEventM().breakFollowerChain(gp.getPlayer());
        LimitedArrayList<Integer> fullParty = new LimitedArrayList<>(gp.getParty().size());

        for (Integer entityId : gp.getParty().keySet()) {

            fullParty.add(entityId);
        }

        for (int entityId : fullParty) {

            gp.transferEntity(gp.getParty(), gp.getNpc(), entityId);

            if (showAll) {

                gp.getNpc().get(entityId).setHidden(false);
            }
        }
    }


    /**
     * Sets all party members (both active and reserve) as visible.
     */
    public void showAllPartyMembers() {

        for (EntityBase entity : gp.getParty().values()) {

            entity.setHidden(false);
        }
    }


    /**
     * Sets all active party members as visible.
     */
    public void showActivePartyMembers() {

        int entityIndex = 0;

        for (EntityBase entity : gp.getParty().values()) {

            if (entityIndex < gp.getNumActivePartyMembers()) {

                entity.setHidden(false);
                entityIndex++;
            } else {

                break;
            }
        }
    }


    /**
     * Sets all active party members (both active as reserve) as hidden.
     */
    public void hideAllPartyMembers() {

        for (EntityBase entity : gp.getParty().values()) {

            entity.setHidden(true);
        }
    }
}
