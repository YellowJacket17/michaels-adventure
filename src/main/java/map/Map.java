package map;

import core.GamePanel;
import landmark.LandmarkBase;

import java.util.ArrayList;

/**
 * This class defines a map.
 */
public class Map {

    /*
     * Maps may have different states (0, 1, 2, ...).
     * Map states are intended to control how a map looks and acts based on which state is active.
     * For example, changing map state may change what track plays upon loading the map, what entities are present, or
     * what environmental effects are applied.
     * It is recommended that the most common (base/default) map state be zero.
     */

    // FIELDS
    /**
     * Unique map ID.
     * To be clear, this ID is unique for each Map instance across the entire game.
     */
    private final int mapId;

    /**
     * Array to store tile data for this map.
     * Tiles are stored in this array using their IDs.
     */
    private final int[][] mapTileNum;

    /**
     * Array to store landmark data for this map.
     * Landmarks are stored in this array using their IDs.
     */
    private final ArrayList<LandmarkBase> mapLandmarks;

    /**
     * Map state.
     */
    private int mapState = 0;

    /**
     * Array of names/titles of tracks to play at specified map states.
     * Array indices correspond to map states.
     */
    private final ArrayList<String> tracks = new ArrayList<>();


    // CONSTRUCTOR
    /**
     * Constructs a Map instance.
     *
     * @param gp GamePanel instance
     * @param mapId unique map ID
     */
    public Map(GamePanel gp, int mapId) {
        this.mapId = mapId;
        mapTileNum = gp.getTileM().loadMapTileData(mapId);                                                              // Load tile data from file.
        mapLandmarks = gp.getLandmarkM().loadMapLandmarkData(mapId);                                                    // Load landmark data from file.
    }


    // GETTERS
    public int getMapId() {
        return mapId;
    }

    public int[][] getMapTileNum() {
        return mapTileNum;
    }

    public ArrayList<LandmarkBase> getMapLandmarks() {
        return mapLandmarks;
    }

    public int getMapState() {
        return mapState;
    }

    public ArrayList<String> getTracks() {
        return tracks;
    }

    // SETTER
    public void setMapState(int mapState) {
        this.mapState = mapState;
    }
}
