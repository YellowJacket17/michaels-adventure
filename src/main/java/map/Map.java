package map;

import core.GamePanel;
import landmark.LandmarkBase;

import java.util.ArrayList;

/**
 * This class defines a map.
 */
public class Map {

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
    private int state;

    /**
     * Boolean setting whether this map has a day/night cycle.
     */
    private boolean dayNightCycle;


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

    public int getState() {
        return state;
    }

    public boolean hasDayNightCycle() {
        return dayNightCycle;
    }


    // SETTERS
    public void setState(int state) {
        this.state = state;
    }

    public void setDayNightCycle(boolean dayNightCycle) {
        this.dayNightCycle = dayNightCycle;
    }
}
