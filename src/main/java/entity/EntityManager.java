package entity;

import core.GamePanel;
import entity.implementation.player.Player;
import utility.JsonParser;
import utility.LimitedLinkedHashMap;
import utility.exceptions.EntityTransferException;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/**
 * This class is used to manage loaded entities in the game, including the player entity.
 */
public class EntityManager {

    // FIELDS
    private final GamePanel gp;

    /**
     * Player entity.
     */
    private Player player;

    /**
     * Map to store objects loaded into the game; entity ID is the key, entity is the value.
     */
    private final LimitedLinkedHashMap<Integer, EntityBase> obj = new LimitedLinkedHashMap<>(50);

    /**
     * Map to store NPCs loaded into the game; entity ID is the key, entity is the value.
     */
    private final LimitedLinkedHashMap<Integer, EntityBase> npc = new LimitedLinkedHashMap<>(50);

    /**
     * Map to store party members loaded into the game; entity ID is the key, entity is the value.
     * The number of entities at the front of the map (indices 0, 1, etc.) according to the 'numActivePartyMembers' field are the active
     * party members.
     */
    private final LimitedLinkedHashMap<Integer, EntityBase> party = new LimitedLinkedHashMap<>(5);

    /**
     * Map to store entities loaded into the game but not currently available; entity ID is the key,
     * entity is the value.
     * Note that entities in this map are neither updated nor rendered.
     * Entities in this map may be of either entity type.
     */
    private final LimitedLinkedHashMap<Integer, EntityBase> standby = new LimitedLinkedHashMap<>(50);

    /**
     * Set to store the IDs of all entities that should no longer be loaded on a map (ex. picked up an object, causing
     * is to disappear from the map).
     */
    private final HashSet<Integer> removedEntities = new HashSet<>();

    /**
     * Sets the number of active party members allowed at a time.
     * Active party members are those that actively follow the player entity, actively participate in combat, etc.
     * This is in contrast to reserve party members.
     * This number does NOT include the player entity.
     */
    private final int numActivePartyMembers = 2;


    // CONSTRUCTOR
    /**
     * Constructs an EntityManager instance.
     *
     * @param gp GamePanel instance
     */
    public EntityManager(GamePanel gp) {
        this.gp = gp;
    }


    // METHODS
    /**
     * Updates the state of all entities (including the player entity) by one frame.
     *
     * @param dt time since last frame (seconds)
     */
    public void update(double dt) {

        // Player.
        player.update(dt);

        // Object.
        for (EntityBase entity : obj.values()) {
            if (entity != null) {
                entity.update(dt);
            }
        }

        // NPC.
        for (EntityBase entity : npc.values()) {
            if (entity != null) {
                entity.update(dt);
            }
        }

        // Party.
        for (EntityBase entity : party.values()) {
            if (entity != null) {
                entity.update(dt);
            }
        }
    }


    /**
     * Registers player input per the current frame.
     *
     * @param dt time since last frame (seconds)
     */
    public void updateInput(double dt) {

        player.updateInput(dt);
    }


    /**
     * Initializes/constructs the player entity.
     */
    public void initPlayer() {

        if (player == null) {

            player = new Player(gp);
        }
    }


    /**
     * Loads any new entity into memory, regardless of whether it's tied to the loaded map or not.
     * If the entity is already loaded, nothing will happen.
     *
     * @param entityId ID of entity to load
     */
    public void loadEntity(int entityId) {

        JsonParser.loadEntityJson(gp, entityId);
    }


    /**
     * Retrieves any loaded entity by its ID.
     *
     * @param entityId ID of the entity being retrieved
     * @return retrieved entity OR null if no matching entity was found
     */
    public EntityBase getEntityById(int entityId) {

        EntityBase entity;

        // Check player.
        if (player != null) {
            if (player.getEntityId() == entityId) {
                return player;
            }
        }

        // Check party.
        entity = party.get(entityId);
        if (entity != null) {
            return entity;
        }

        // Check NPCs.
        entity = npc.get(entityId);
        if (entity != null) {
            return entity;
        }

        // Check standby.
        entity = standby.get(entityId);
        if (entity != null) {
            return entity;
        }

        // Check objects.
        entity = obj.get(entityId);

        return entity;
    }


    /**
     * Retrieves all instantiated entities, including the player entity.
     *
     * @return list of all instantiated entities
     */
    public List<EntityBase> getAllEntities() {

        // Initialize a list that will hold all entities.
        List<EntityBase> allEntities = new ArrayList<>();

        // Add player entity.
        allEntities.add(player);

        // Add all non-null party member entities.
        for (EntityBase entity : party.values()) {
            if (entity != null) {
                allEntities.add(entity);
            }
        }

        // Add all non-null NPC entities.
        for (EntityBase entity : npc.values()) {
            if (entity != null) {
                allEntities.add(entity);
            }
        }

        // Add all non-null standby entities.
        for (EntityBase entity : standby.values()) {
            if (entity != null) {
                allEntities.add(entity);
            }
        }

        // Add all non-null object entities.
        for (EntityBase entity : obj.values()) {
            if (entity != null) {
                allEntities.add(entity);
            }
        }

        return allEntities;
    }


    /**
     * Transfers an entity from a source entity map to a target entity map.
     *
     * @param source source entity map being transferred from
     * @param target target entity map being transferred to
     * @param entityId ID of the entity to be transferred
     * @throws EntityTransferException if transferring an entity fails
     */
    public void transferEntity(LimitedLinkedHashMap<Integer, EntityBase> source,
                               LimitedLinkedHashMap<Integer, EntityBase> target,
                               int entityId) {

        EntityBase entity = source.get(entityId);

        if (entity != null) {

            try {

                target.put(entityId, entity);

            } catch (IllegalStateException e) {

                throw new EntityTransferException("Failed to transfer entity "
                        + (((entity.getName() != null) && (!entity.getName().equals("")))
                        ? "'" + (entity.getName() + "' ") : "")
                        + "with ID '"
                        + entityId
                        + "' from source map to target map: target map full");
            }
            source.remove(entityId);
        } else {

            throw new EntityTransferException("Failed to transfer entity with ID '"
                    + entityId
                    + "' from source map to target map - no such entity found in source map");
        }
    }


    /**
     * Removes an entity from a source entity map and blacklists it to not be loaded again.
     *
     * @param source source entity map to remove the entity from
     * @param entityId ID of the entity to be removed
     */
    public void removeEntity(LimitedLinkedHashMap<Integer, EntityBase> source, int entityId) {

        source.remove(entityId);
        removedEntities.add(entityId);
    }


    public ArrayList<Integer> getCombatingEntities() {

        ArrayList<Integer> combatingEntities = new ArrayList<>();

        for (EntityBase candidate : getAllEntities()) {

            if (candidate.isCombating()) {

                combatingEntities.add(candidate.getEntityId());
            }
        }
        return combatingEntities;
    }


    public ArrayList<Integer> getConversingEntities() {

        ArrayList<Integer> conversingEntities = new ArrayList<>();

        for (EntityBase candidate : getAllEntities()) {

            if (candidate.isCombating()) {

                conversingEntities.add(candidate.getEntityId());
            }
        }
        return conversingEntities;
    }


    /**
     * Removes all entities from a state of combating.
     */
    public void clearCombatingEntities() {

        for (EntityBase candidate : getAllEntities()) {

            if (candidate.isCombating()) {

                candidate.setCombating(false);
            }
        }
    }


    /**
     * Removes all entities from a state of conversing.
     */
    public void clearConversingEntities() {

        for (EntityBase candidate : getAllEntities()) {

            if (candidate.isConversing()) {

                candidate.setConversing(false);
            }
        }
    }


    // GETTERS
    public Player getPlayer() {
        return player;
    }

    public LimitedLinkedHashMap<Integer, EntityBase> getObj() {
        return obj;
    }

    public LimitedLinkedHashMap<Integer, EntityBase> getNpc() {
        return npc;
    }

    public LimitedLinkedHashMap<Integer, EntityBase> getParty() {
        return party;
    }

    public LimitedLinkedHashMap<Integer, EntityBase> getStandby() {
        return standby;
    }

    public HashSet<Integer> getRemovedEntities() {
        return removedEntities;
    }

    public int getNumActivePartyMembers() {
        return numActivePartyMembers;
    }
}
