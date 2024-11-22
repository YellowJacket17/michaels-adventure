package event.support;

import core.GamePanel;
import entity.EntityBase;
import entity.enumeration.EntityDirection;
import entity.enumeration.FadeEffectType;
import org.joml.Vector2i;
import utility.LimitedArrayList;
import utility.LimitedLinkedHashMap;
import utility.UtilityTool;

import java.util.ArrayList;

/**
 * This class contains methods to facilitate player party management.
 * The public methods in this class serve as primary endpoints to use when programming in-game events.
 */
public class PartySupport {

    // FIELDS
    private final GamePanel gp;

    /**
     * Fade effect duration (up and down, seconds).
     */
    private final double fadeEffectDuration = 0.20;

    /**
     * Map to temporarily store the staged tile position that an entity will go to.
     * Entity ID is the key, tile position (col, row) the value.
     * In practice, this is used to store the tile position that an entity will go to after it has completed a fade down
     * effect.
     * This is done to ensure that the entity retains its original tile position while fading out, only going to its new
     * tile position after hidden.
     * This is useful when swapping party member ordering around with fade effects.
     */
    private final LimitedLinkedHashMap<Integer, Vector2i> tempEntityTiles;

    /**
     * Map to temporarily store the staged direction that an entity will face.
     * Entity ID is the key, direction the value.
     * In practice, this is used to store the direction that an entity will face after it has completed a fade down
     * effect.
     * This is done to ensure that the entity retains its original direction while fading out, only facing its new
     * direction after hidden.
     * This is useful when swapping party member ordering around with fade effects.
     */
    private final LimitedLinkedHashMap<Integer, EntityDirection> tempEntityDirections;

    /**
     * Map to store staged entity fade up effects.
     * Target entity ID is the key, triggering entity ID the value.
     * In practice, this is used to store fade up effects that can only execute after a preceding fade down effect of
     * another entity has completed.
     * The entity that will fade up is the target entity, while the entity whose fade down effect must complete before
     * the target entity can fade up is the triggering entity.
     * This is useful when swapping party member ordering around with fade effects.
     */
    private final LimitedLinkedHashMap<Integer, Integer> stagedEntityFadeUpEffects;


    // TODO : Add lock to prevent party management while fades are occurring.


    // CONSTRUCTOR
    /**
     * Constructs a PartySupport instance.
     *
     * @param gp
     */
    public PartySupport(GamePanel gp) {
        this.gp = gp;
        this.tempEntityTiles = new LimitedLinkedHashMap<>(gp.getEntityM().getParty().maxCapacity());
        this.tempEntityDirections = new LimitedLinkedHashMap<>(gp.getEntityM().getParty().maxCapacity());
        this.stagedEntityFadeUpEffects = new LimitedLinkedHashMap<>(gp.getEntityM().getParty().maxCapacity());
    }


    // METHODS
    /**
     * Updates the state of any active party management process by one frame.
     *
     * @param dt time since last frame (seconds)
     */
    public void update(double dt) {

        // Set stored position and direction for faded down entity, if necessary.
        if (tempEntityTiles.size() > 0) {
            LimitedArrayList<Integer> entityIdsToRemove =
                    new LimitedArrayList<>(gp.getEntityM().getParty().maxCapacity());
            for (int entityId : tempEntityTiles.keySet()) {
                EntityBase entity = gp.getEntityM().getEntityById(entityId);
                if (entity.getActiveFadeEffect() != FadeEffectType.FADE_DOWN) {
                    entity.setCol(tempEntityTiles.get(entityId).x);
                    entity.setRow(tempEntityTiles.get(entityId).y);
                    entity.setDirectionCurrent(tempEntityDirections.get(entityId));
                    entityIdsToRemove.add(entityId);
                }
            }
            for (int entityId : entityIdsToRemove) {
                tempEntityTiles.remove(entityId);
                tempEntityDirections.remove(entityId);
            }
        }

        // Trigger staged fade up effect, if necessary.
        if (stagedEntityFadeUpEffects.size() > 0) {
            LimitedArrayList<Integer> entityIdsToRemove =
                    new LimitedArrayList<>(gp.getEntityM().getParty().maxCapacity());
            for (int targetEntityId : stagedEntityFadeUpEffects.keySet()) {
                if (gp.getEntityM().getEntityById(stagedEntityFadeUpEffects.get(targetEntityId)).getActiveFadeEffect()
                        != FadeEffectType.FADE_DOWN) {
                    gp.getEntityM().getEntityById(targetEntityId).initiateFadeEffect(
                            FadeEffectType.FADE_UP, fadeEffectDuration);
                    entityIdsToRemove.add(targetEntityId);
                }
            }
            for (int entityId : entityIdsToRemove) {
                stagedEntityFadeUpEffects.remove(entityId);
            }
        }
    }


    /**
     * Adds an NPC entity to the end of the player's party.
     * Note that if the added NPC causes the number of party members to exceed the allowable number of active party
     * members, then the entity will become hidden.
     * If not, the entity will begin following the player entity.
     * The hidden status of all pre-existing party members will not be changed.
     *
     * @param entityId ID of the entity to add
     * @param fade whether to perform a fade effect (true) or not (false)
     */
    public void addEntityToParty(int entityId, boolean fade) {

        ArrayList<Integer> nonPartyFollowers = gp.getEventM()
                .seekFollowers(gp.getEntityM().getPlayer(), true, true, false, false);
        gp.getEventM().breakFollowerChain(gp.getEntityM().getPlayer());                                                 // Break chain of all entities following player entity (will rebuild later).
        gp.getEntityM().transferEntity(gp.getEntityM().getNpc(), gp.getEntityM().getParty(), entityId);                 // Add target entity to party map.
        int entityIndex = 0;                                                                                            // Iterator to track which index of new party map is currently being worked on.

        for (EntityBase entity : gp.getEntityM().getParty().values()) {                                                 // Set party entity world positions and directions to match those of party entities originally at said index of party map.

            if (entityIndex < gp.getEntityM().getNumActivePartyMembers()) {

                gp.getEventM().setEntityFollowTarget(entity.getEntityId(), gp.getEntityM().getPlayer().getEntityId());  // Set active party members as following player entity.
            } else if (entity.getEntityId() == entityId) {

                if (!entity.isHidden()) {                                                                               // New entity is in reserve party, so hide.

                    if (checkActiveFadeEffect(entityId)) {

                        displayActiveFadeEffectWarning("add entity to party", entity.getEntityId());
                    }

                    if (fade) {

                        gp.getEntityM().getParty().get(entityId).initiateFadeEffect(
                                FadeEffectType.FADE_DOWN, fadeEffectDuration);
                    } else {

                        gp.getEntityM().getParty().get(entityId).setHidden(true);
                    }
                }
            }
            entityIndex++;
        }

        for (int nonPartyId : nonPartyFollowers) {                                                                      // Set non-party followers to follow player entity again.

            gp.getEventM().setEntityFollowTarget(nonPartyId, gp.getEntityM().getPlayer().getEntityId());
        }
    }


    /**
     * Removes an entity from the player's party.
     * The removed entity becomes an NPC and will no longer follow the player entity.
     * The world position, direction, and hidden state of remaining party members will not change.
     *
     * @param entityId ID of the entity to remove
     * @param showRemoved sets removed party member as visible (true) or retains existing hidden state (false)
     * @param showActiveParty sets the active party members (post-target entity removal) as visible (true) or retains
     *                        existing hidden state (false)
     * @param fade whether to perform a fade effect (true) or not (false)
     */
    public void removeEntityFromParty(int entityId, boolean showRemoved, boolean showActiveParty, boolean fade) {

        ArrayList<Integer> nonPartyFollowers = gp.getEventM()
                .seekFollowers(gp.getEntityM().getPlayer(), true, true, false, false);
        LimitedArrayList<Vector2i> partyEntityTiles = new LimitedArrayList<>(gp.getEntityM().getParty().size());        // List to store party entity world positions at each index in party map.
        LimitedArrayList<EntityDirection> partyEntityDirections = new LimitedArrayList<>(
                gp.getEntityM().getParty().size());                                                                     // List to store party entity directions at each index in party map.

        for (EntityBase entity : gp.getEntityM().getParty().values()) {                                                 // Store entity world positions and directions at each index in party map.

            partyEntityTiles.add(new Vector2i(entity.getCol(), entity.getRow()));
            partyEntityDirections.add(entity.getDirectionCurrent());
        }
        gp.getEventM().breakFollowerChain(gp.getEntityM().getPlayer());                                                 // Break chain of all entities following player entity (will rebuild later).
        gp.getEntityM().transferEntity(gp.getEntityM().getParty(), gp.getEntityM().getNpc(), entityId);                 // Remove target entity from party map.

        if (showRemoved) {

            if (gp.getEntityM().getNpc().get(entityId).isHidden() && fade) {

                gp.getEntityM().getNpc().get(entityId).initiateFadeEffect(FadeEffectType.FADE_UP, fadeEffectDuration);
            } else {

                gp.getEntityM().getNpc().get(entityId).setHidden(false);
            }
        }
        int entityIndex = 0;                                                                                            // Iterator to track which index of party map is currently being worked on.

        for (EntityBase entity : gp.getEntityM().getParty().values()) {

            if (entityIndex < gp.getEntityM().getNumActivePartyMembers()) {

                gp.getEventM().setEntityFollowTarget(entity.getEntityId(), gp.getEntityM().getPlayer().getEntityId());  // Set active party members as following player entity.

                if (showActiveParty && entity.isHidden()) {                                                             // Show active party members, if applicable.

                    if (checkActiveFadeEffect(entityId)) {

                        displayActiveFadeEffectWarning("remove entity from party", entity.getEntityId());
                    }

                    if (fade) {

                        entity.initiateFadeEffect(FadeEffectType.FADE_UP, fadeEffectDuration);
                    } else {

                        entity.setHidden(false);
                    }
                }
            }
            entityIndex++;
        }

        for (int nonPartyId : nonPartyFollowers) {                                                                      // Set non-party followers to follow player entity again.

            gp.getEventM().setEntityFollowTarget(nonPartyId, gp.getEntityM().getPlayer().getEntityId());
        }
    }


    /**
     * Swaps the positions of two entities in the player's party.
     * Swapped party members will assume each other's world position, direction, and hidden state.
     *
     * @param primaryEntityId ID of the primary entity to move
     * @param secondaryEntityId ID of the secondary entity to move
     * @param fade whether to perform a fade effect (true) or not (false)
     */
    public void swapEntityInParty(int primaryEntityId, int secondaryEntityId, boolean fade) {

        if (primaryEntityId != secondaryEntityId) {                                                                     // No need to run further logic if attempting to swap a party entity with itself.

            if (checkEntityConflictingFadeOperation(primaryEntityId)) {

                displayStagedFadeEffectWarning("swap", primaryEntityId);
                return;                                                                                                 // Abort operation, as entity is already staged to execute a fade effect.
            }

            if (checkEntityConflictingFadeOperation(secondaryEntityId)) {

                displayStagedFadeEffectWarning("swap", secondaryEntityId);
                return;                                                                                                 // Abort operation, as entity is already staged to execute a fade effect.
            }
            ArrayList<Integer> nonPartyFollowers = gp.getEventM()
                    .seekFollowers(gp.getEntityM().getPlayer(), true, true, false, false);
            LimitedArrayList<EntityBase> originalParty = new LimitedArrayList<>(
                    gp.getEntityM().getParty().maxCapacity());                                                          // Temporary list to store all party entities in party map, retaining original ordering.
            int primaryPosition = 0;
            int secondaryPosition = 0;
            boolean primaryEntityHidden = false;                                                                        // Store whether primary entity is initially hidden to assign it to secondary entity later.
            boolean secondaryEntityHidden = false;                                                                      // Store whether secondary entity is initially hidden to assign it to primary entity later.
            int entityIndex = 0;                                                                                        // Iterator to track which index of party map is currently being worked on.

            for (EntityBase entity : gp.getEntityM().getParty().values()) {                                             // Store each party entity in the temporary list of party entities.

                originalParty.add(entity);

                if (entity.getEntityId() == primaryEntityId) {

                    primaryPosition = entityIndex;                                                                      // Original position/index of primary entity being put into secondary position/index.

                    if (entity.isHidden()) {

                        primaryEntityHidden = true;
                    }
                } else if (entity.getEntityId() == secondaryEntityId) {

                    secondaryPosition = entityIndex;                                                                    // Original position/index of secondary entity being put into primary position/index.

                    if (entity.isHidden()) {

                        secondaryEntityHidden = true;
                    }
                }
                entityIndex++;
            }
            LimitedArrayList<Vector2i> partyEntityTiles = new LimitedArrayList<>(gp.getEntityM().getParty().size());    // List to store party entity world positions at each index in party map.
            LimitedArrayList<EntityDirection> partyEntityDirections = new LimitedArrayList<>(
                    gp.getEntityM().getParty().size());                                                                 // List to store party entity directions at each index in party map.

            for (EntityBase entity : gp.getEntityM().getParty().values()) {                                             // Store entity world positions and directions at each index in party map.

                partyEntityTiles.add(new Vector2i(entity.getCol(), entity.getRow()));
                partyEntityDirections.add(entity.getDirectionCurrent());
            }
            gp.getEventM().breakFollowerChain(gp.getEntityM().getPlayer());                                             // Break chain of all entities following player entity (will rebuild later).
            gp.getEntityM().getParty().clear();                                                                         // Clear party map to re-add party entities in new ordering.

            for (int j = 0; j < originalParty.size(); j++) {                                                            // Add party entities back to party map in new ordering.

                if (j == primaryPosition) {                                                                             // Position/index to move secondary entity to.

                    gp.getEntityM().getParty().put(originalParty.get(secondaryPosition).getEntityId(),
                            originalParty.get(secondaryPosition));
                } else if (j == secondaryPosition) {                                                                    // Position/index to move primary entity to.

                    gp.getEntityM().getParty().put(originalParty.get(primaryPosition).getEntityId(),
                            originalParty.get(primaryPosition));
                } else {                                                                                                // Party entities other than primary and secondary retain original positions/indices.

                    gp.getEntityM().getParty().put(originalParty.get(j).getEntityId(), originalParty.get(j));
                }
            }
            entityIndex = 0;                                                                                            // Iterator to track which index of new party map is currently being worked on.

            for (EntityBase entity : gp.getEntityM().getParty().values()) {

                if (entityIndex < gp.getEntityM().getNumActivePartyMembers()) {

                    gp.getEventM().setEntityFollowTarget(
                            entity.getEntityId(), gp.getEntityM().getPlayer().getEntityId());                           // Set active party members as following player entity.
                }

                if (entity.getEntityId() == primaryEntityId) {

                    if (secondaryEntityHidden && !primaryEntityHidden && fade) {

                        stageFadeDownEntity(entity,
                                partyEntityTiles.get(entityIndex).x, partyEntityTiles.get(entityIndex).y,
                                partyEntityDirections.get(entityIndex));
                    } else if (!secondaryEntityHidden && primaryEntityHidden && fade) {

                        stageFadeUpEntity(entity, secondaryEntityId,
                                partyEntityTiles.get(entityIndex).x, partyEntityTiles.get(entityIndex).y,
                                partyEntityDirections.get(entityIndex));
                    } else if (!secondaryEntityHidden && !primaryEntityHidden && fade) {

                        stageFadeDownFadeUpEntity(entity,
                                partyEntityTiles.get(entityIndex).x, partyEntityTiles.get(entityIndex).y,
                                partyEntityDirections.get(entityIndex));
                    } else {

                        entity.setCol(partyEntityTiles.get(entityIndex).x);                                             // Set entity to the same tile as entity previously in its position/index.
                        entity.setRow(partyEntityTiles.get(entityIndex).y);                                             // ^^^
                        entity.setDirectionCurrent(partyEntityDirections.get(entityIndex));                             // Set entity to face the same direction as entity previously in its position/index.
                        entity.setHidden(secondaryEntityHidden);
                    }
                } else if (entity.getEntityId() == secondaryEntityId) {

                    if (primaryEntityHidden && !secondaryEntityHidden && fade) {

                        stageFadeDownEntity(entity,
                                partyEntityTiles.get(entityIndex).x, partyEntityTiles.get(entityIndex).y,
                                partyEntityDirections.get(entityIndex));
                    } else if (!primaryEntityHidden && secondaryEntityHidden && fade) {

                        stageFadeUpEntity(entity, primaryEntityId,
                                partyEntityTiles.get(entityIndex).x, partyEntityTiles.get(entityIndex).y,
                                partyEntityDirections.get(entityIndex));
                    } else if (!primaryEntityHidden && !secondaryEntityHidden && fade) {

                        stageFadeDownFadeUpEntity(entity,
                                partyEntityTiles.get(entityIndex).x, partyEntityTiles.get(entityIndex).y,
                                partyEntityDirections.get(entityIndex));
                    } else {

                        entity.setCol(partyEntityTiles.get(entityIndex).x);                                             // Set entity to the same tile as entity previously in its position/index.
                        entity.setRow(partyEntityTiles.get(entityIndex).y);                                             // ^^^
                        entity.setDirectionCurrent(partyEntityDirections.get(entityIndex));                             // Set entity to face the same direction as entity previously in its position/index.
                        entity.setHidden(primaryEntityHidden);
                    }
                }
                entityIndex++;
            }

            for (int nonPartyId : nonPartyFollowers) {                                                                  // Set non-party followers to follow player entity again.

                gp.getEventM().setEntityFollowTarget(nonPartyId, gp.getEntityM().getPlayer().getEntityId());
            }
        }
    }


    /**
     * Removes all entities from the player's party.
     * The removed entities become NPCs and will no longer follow the player entity.
     * Note that any removed hidden entities will remain hidden, and vice versa.
     * World positions and directions of removed entities will remain as-is.
     *
     * @param showAll sets all removed party members as visible (true) or retains their existing hidden state (false)
     * @param fade whether to perform a fade effect (true) or not (false)
     */
    public void removeAllEntitiesFromParty(boolean showAll, boolean fade) {

        ArrayList<Integer> nonPartyFollowers = gp.getEventM()
                .seekFollowers(gp.getEntityM().getPlayer(), true, true, false, false);
        gp.getEventM().breakFollowerChain(gp.getEntityM().getPlayer());
        LimitedArrayList<Integer> fullParty = new LimitedArrayList<>(gp.getEntityM().getParty().size());

        for (Integer entityId : gp.getEntityM().getParty().keySet()) {

            fullParty.add(entityId);
        }

        for (int entityId : fullParty) {

            gp.getEntityM().transferEntity(gp.getEntityM().getParty(), gp.getEntityM().getNpc(), entityId);

            if (showAll) {

                if (gp.getEntityM().getNpc().get(entityId).isHidden()) {

                    if (checkActiveFadeEffect(entityId)) {

                        displayActiveFadeEffectWarning("remove all entities from party", entityId);
                    }

                    if (fade) {

                        gp.getEntityM().getNpc().get(entityId).initiateFadeEffect(
                                FadeEffectType.FADE_UP, fadeEffectDuration);
                    } else {

                        gp.getEntityM().getNpc().get(entityId).setHidden(false);
                    }

                }
            }
        }

        for (int nonPartyId : nonPartyFollowers) {                                                                      // Set non-party followers to follow player entity again.

            gp.getEventM().setEntityFollowTarget(nonPartyId, gp.getEntityM().getPlayer().getEntityId());
        }
    }


    /**
     * Sets all party members (both active and reserve) as visible.
     *
     * @param fade whether to perform a fade effect (true) or not (false)
     */
    public void showAllPartyMembers(boolean fade) {

        for (EntityBase entity : gp.getEntityM().getParty().values()) {

            if (entity.isHidden()) {

                if (checkActiveFadeEffect(entity.getEntityId())) {

                    displayActiveFadeEffectWarning("show all party members", entity.getEntityId());
                }

                if (fade) {

                    entity.initiateFadeEffect(FadeEffectType.FADE_UP, fadeEffectDuration);
                } else {

                    entity.setHidden(false);
                }
            }
        }
    }


    /**
     * Sets all active party members as visible.
     *
     * @param fade whether to perform a fade effect (true) or not (false)
     */
    public void showActivePartyMembers(boolean fade) {

        int entityIndex = 0;

        for (EntityBase entity : gp.getEntityM().getParty().values()) {

            if (entityIndex < gp.getEntityM().getNumActivePartyMembers()) {

                if (entity.isHidden()) {

                    if (checkActiveFadeEffect(entity.getEntityId())) {

                        displayActiveFadeEffectWarning("show active party members", entity.getEntityId());
                    }

                    if (fade) {

                        entity.initiateFadeEffect(FadeEffectType.FADE_UP, fadeEffectDuration);
                    } else {

                        entity.setHidden(false);
                    }
                }
                entityIndex++;
            } else {

                break;
            }
        }
    }


    /**
     * Sets all active party members (both active as reserve) as hidden.
     *
     * @param fade whether to perform a fade effect (true) or not (false)
     */
    public void hideAllPartyMembers(boolean fade) {

        for (EntityBase entity : gp.getEntityM().getParty().values()) {

            if (!entity.isHidden()) {

                if (checkActiveFadeEffect(entity.getEntityId())) {

                    displayActiveFadeEffectWarning("hide all party members", entity.getEntityId());
                }

                if (fade) {

                    entity.initiateFadeEffect(FadeEffectType.FADE_DOWN, fadeEffectDuration);
                } else {

                    entity.setHidden(true);
                }
            }
        }
    }


    /**
     * Sets all inactive party members as hidden.
     *
     * @param fade whether to perform a fade effect (true) or not (false)
     */
    public void hideInactivePartyMembers(boolean fade) {

        int entityIndex = 0;

        for (EntityBase entity : gp.getEntityM().getParty().values()) {

            if (entityIndex >= gp.getEntityM().getNumActivePartyMembers()) {

                if (!entity.isHidden()) {

                    if (checkActiveFadeEffect(entity.getEntityId())) {

                        displayActiveFadeEffectWarning("hide inactive party members", entity.getEntityId());
                    }

                    if (fade) {

                        entity.initiateFadeEffect(FadeEffectType.FADE_DOWN, fadeEffectDuration);
                    } else {

                        entity.setHidden(true);
                    }
                }
            }
            entityIndex++;
        }
    }


    /**
     * Stages an entity to perform a fade down effect.
     * The entity will retain its current position and direction until the fade down effect is complete.
     * After the fade down effect is complete, it will snap to the position and direction passed as argument.
     *
     * @param targetEntity target entity that will fade down
     * @param postFadeDownCol post-fade down position of entity (column)
     * @param postFadeDownRow post-fade down position of entity (row)
     * @param postFadeDownDirection post-fade down direction of entity
     */
    private void stageFadeDownEntity(EntityBase targetEntity,
                                     int postFadeDownCol, int postFadeDownRow,
                                     EntityDirection postFadeDownDirection) {

        tempEntityTiles.put(
                targetEntity.getEntityId(),
                new Vector2i(postFadeDownCol, postFadeDownRow));                                                        // Keep entity in its original position temporarily as it fades down.
        tempEntityDirections.put(targetEntity.getEntityId(), postFadeDownDirection);                                    // Keep entity in its original direction temporarily as it fades down.
        targetEntity.initiateFadeEffect(FadeEffectType.FADE_DOWN, fadeEffectDuration);
    }


    /**
     * Stages an entity to perform a fade up effect.
     * Before fading up, the entity will snap to the position and direction passed as argument.
     *
     * @param targetEntity target entity that will fade up
     * @param triggeringEntityId triggering entity whose fade down must complete before target entity can fade up
     * @param newCol new position of entity (column)
     * @param newRow new position of entity (row)
     * @param newDirection new direction of entity
     */
    private void stageFadeUpEntity(EntityBase targetEntity,
                                   int triggeringEntityId,
                                   int newCol, int newRow,
                                   EntityDirection newDirection) {

        targetEntity.setCol(newCol);                                                                                    // Set entity to the same tile as entity previously in its position/index.
        targetEntity.setRow(newRow);                                                                                    // ^^^
        targetEntity.setDirectionCurrent(newDirection);                                                                 // Set entity to face the same direction as entity previously in its position/index.
        stagedEntityFadeUpEffects.put(targetEntity.getEntityId(), triggeringEntityId);
    }


    /**
     * Stages an entity to perform a fade down effect followed immediately by a fade up effect
     * The entity will retain its current position and direction until the fade down effect is complete.
     * After the fade down effect is complete, it will snap to the position and direction passed as argument.
     * The entity will fade up in this new position and direction.
     *
     * @param targetEntity target entity that will fade down then up
     * @param postFadeDownCol post-fade down position of entity (column)
     * @param postFadeDownRow post-fade down position of entity (row)
     * @param postFadeDownDirection post-fade down direction of entity
     */
    private void stageFadeDownFadeUpEntity(EntityBase targetEntity,
                                           int postFadeDownCol, int postFadeDownRow,
                                           EntityDirection postFadeDownDirection) {

        stageFadeDownEntity(targetEntity, postFadeDownCol, postFadeDownRow, postFadeDownDirection);
        stagedEntityFadeUpEffects.put(targetEntity.getEntityId(), targetEntity.getEntityId());
    }


    /**
     * Checks if attempting to perform a party management operation on an entity that is already staged to execute a
     * potentially conflicting fade effect from a separate operation.
     *
     * @param entityId ID of affected entity
     * @return whether a potential conflicting operation is executing (true) or not (false)
     */
    private boolean checkEntityConflictingFadeOperation(int entityId) {

        if ((tempEntityTiles.containsKey(entityId))
                || (tempEntityDirections.containsKey(entityId))
                || stagedEntityFadeUpEffects.containsKey(entityId)) {

            return true;
        }
        return false;
    }


    /**
     * Displays a warning message related to attempting to perform a party management operation on an entity that is
     * already staged to execute a potentially conflicting fade effect from a separate operation.
     *
     * @param actionType type of management action attempted (add, swap, etc.)
     * @param entityId ID of affected entity
     */
    private void displayStagedFadeEffectWarning(String actionType, int entityId) {

        UtilityTool.logWarning("Attempted to execute a party management operation ("
                + actionType
                + ") on entity with ID '"
                + entityId
                + "' that is already staged to execute a fade effect from a separate operation; action aborted.");
    }


    /**
     * Checks if attempting to change the hidden state of an entity that is already executing a fade effect.
     * Attempting such an action can cause unexpected outcomes.
     *
     * @param entityId ID of affected entity
     */
    public boolean checkActiveFadeEffect(int entityId) {

        if (gp.getEntityM().getEntityById(entityId).getActiveFadeEffect() != null) {

            return true;
        }
        return false;
    }


    /**
     * Displays a warning message related to attempting to change the hidden state of an entity that is already
     * executing a fade effect.
     * Attempting such an action can cause unexpected outcomes.
     *
     * @param actionType type of management action attempted (add, swap, etc.)
     * @param entityId ID of affected entity
     */
    private void displayActiveFadeEffectWarning(String actionType, int entityId) {

        UtilityTool.logWarning("Attempted to change hidden state via a party management operation ("
                + actionType
                + ") on entity with ID '"
                + entityId
                + "' that is already executing a fade effect; unexpected outcomes may occur.");
    }
}
