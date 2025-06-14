package map;

import core.GamePanel;
import landmark.LandmarkBase;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * This class defines a map.
 */
public class Map {

    /*
     * Maps may have different states (0, 1, 2, ...).
     * Map states are intended to control how a map looks and acts based on which state is active.
     * For example, map state may be used to determine what track will play, what entities will be present, or which
     * environmental effects will be applied.
     * In other words, the map state may be referenced by various events in the game to determine what logic to run.
     * It is recommended that the most common (base/default) map state be zero.
     *
     * Note that maps have tracks tied to them for various states.
     * If a map state is changed via the setter in this class, then the track will automatically change accordingly.
     */

    // FIELDS
    private final GamePanel gp;

    /**
     * Unique map ID.
     * To be clear, this ID is unique for each Map instance across the entire game.
     */
    private final int mapId;

    /**
     * Array to store tile data for this map.
     * Tiles are stored in this array using their IDs.
     * Data is stored in this array as [col][row].
     */
    private final int[][] mapTileNum;

    /**
     * Array to store landmark data for this map.
     * Landmarks are stored in this array using their IDs.
     * Data is stored in this array as [col][row].
     */
    private final int[][] mapLandmarkNum;

    /**
     * Array to store landmark data for this map.
     * Instantiated landmarks are stored in this array.
     */
    private final ArrayList<LandmarkBase> mapLandmarks;

    /**
     * Map state.
     */
    private int mapState = 0;

    /**
     * Names/titles of tracks to play at specified map states; map state is the key, track name/title is the value.
     */
    private final HashMap<Integer, String> tracks = new HashMap<>();


    // CONSTRUCTOR
    /**
     * Constructs a Map instance.
     *
     * @param gp GamePanel instance
     * @param mapId unique map ID
     */
    public Map(GamePanel gp, int mapId) {
        this.gp = gp;
        this.mapId = mapId;
        mapTileNum = gp.getTileM().loadMapTileData(mapId);                                                              // Load tile data from file.
        mapLandmarkNum = gp.getLandmarkM().loadMapLandmarkData(mapId);                                                  // Load landmark data from file.
        mapLandmarks = gp.getLandmarkM().instantiateMapLandmarks(mapId, mapLandmarkNum);                                // Instantiate map landmarks.
    }


    // GETTERS
    public int getMapId() {
        return mapId;
    }

    public int[][] getMapTileNum() {
        return mapTileNum;
    }

    public int[][] getMapLandmarkNum() {
        return mapLandmarkNum;
    }

    public ArrayList<LandmarkBase> getMapLandmarks() {
        return mapLandmarks;
    }

    public int getMapState() {
        return mapState;
    }

    public String getTrack(int mapState) {
        return tracks.get(mapState);
    }


    // SETTERS
    public void setMapState(int mapState, boolean swapTrack) {
        this.mapState = mapState;
        if (swapTrack) {
            gp.getSoundS().swapTrack(tracks.get(mapState), true);
        }
    }

    public void setTrack(int mapState, String resourceName) {
        tracks.put(mapState, resourceName);
    }
}
