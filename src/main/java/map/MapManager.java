package map;

import core.GamePanel;
import utility.JsonParser;

import java.util.HashMap;

/**
 * This class is used to manage loaded maps in the game.
 */
public class MapManager {

    // FIELDS
    private final GamePanel gp;

    /**
     * Current loaded map.
     */
    private Map loadedMap;

    /**
     * Map to store map states; map ID is the key, map state is the value.
     * Each time a map is loaded out, a snapshot of its current state is saved for future reference, as needed.
     */
    private final HashMap<Integer, Integer> savedMapStates = new HashMap<>();


    // CONSTRUCTOR
    /**
     * Constructs a MapManager instance.
     *
     * @param gp GamePanel instance
     */
    public MapManager(GamePanel gp) {
        this.gp = gp;
    }


    // METHOD
    /**
     * Loads a new map into memory and sets it as the current map to render.
     * The following will be purged before loading the new map: prior loaded map, prior NPCs in `npc` (hash)map, prior
     * objects in `obj` (hash)map, and prior conversations in `conv` (hash)map.
     * New NPCs, objects, and conversations will be loaded with the new map.
     * To retain an NPC or object between map loads, it should first be transferred to the `standby` (hash)map.
     *
     * @param mapId ID of map to load
     * @param mapState state in which to load map
     * @param swapTrack whether to swap to play the track specified by the map being loaded (true) or not (false)
     */
    public void loadMap(int mapId, int mapState, boolean swapTrack) {

        // Save state of outgoing map.
        if (loadedMap != null) {
            savedMapStates.put(loadedMap.getMapId(), loadedMap.getMapState());
        }

        // Set new map.
        loadedMap = JsonParser.loadMapJson(gp, mapId);

        // Set map state and swap track, if applicable.
        loadedMap.setMapState(mapState, swapTrack);

        // Clear conversing and combating entity lists.
        gp.getEntityM().clearConversingEntities();
        gp.getEntityM().clearCombatingEntities();

        // Purge `npc` and `obj` (hash)maps.
        gp.getEntityM().getNpc().clear();
        gp.getEntityM().getObj().clear();

        // Load entities on new map.
        JsonParser.loadEntitiesJson(gp, mapId, mapState);

        // Purge `conv` (hash)map.
        gp.getDialogueR().getConv().clear();

        // Load dialogue associated with new map.
        JsonParser.loadConversationsJson(gp, mapId);
    }


    // GETTERS
    public Map getLoadedMap() {
        return loadedMap;
    }

    public int getSavedMapState(int mapId) {
        return savedMapStates.get(mapId);
    }


    // SETTER
    public void setLoadedMap(Map loadedMap) {
        this.loadedMap = loadedMap;
    }
}
