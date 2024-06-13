package map;

import core.GamePanel;
import utility.JsonParser;

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
        JsonParser.loadEntitiesJson(gp, mapId);

        // Purge `conv` (hash)map.
        gp.getDialogueR().getConv().clear();

        // Load dialogue associated with new map.
        JsonParser.loadDialogueJson(gp, mapId);
    }


    // GETTER
    public Map getLoadedMap() {
        return loadedMap;
    }


    // SETTER
    public void setLoadedMap(Map loadedMap) {
        this.loadedMap = loadedMap;
    }
}
