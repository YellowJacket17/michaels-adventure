package landmark;

import core.GamePanel;
import landmark.implementation.*;
import utility.UtilityTool;
import utility.exceptions.AssetLoadException;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

/**
 * This class instantiates landmarks and handles related operations.
 */
public class LandmarkManager {

    // TODO : Consider pre-calculating collision on all tiles due to landmarks and storing it here in an boolean[][] the
    //  size of the map (will decrease calculations in CollisionInspector class).

    // FIELDS
    private final GamePanel gp;

    /**
     * Calculated collision at each tile location on the loaded map due to instantiated landmarks that have collision
     * (i.e., are solid) on some or all of the tiles that they occupy.
     * Data is stored in this array as [col][row].
     */
    private final boolean[][] calculatedGlobalLandmarkCollision
            = new boolean[GamePanel.MAX_WORLD_COL][GamePanel.MAX_WORLD_ROW];

    /**
     * Map to track which landmarks are currently undergoing interactive animations; landmark position ID is the key,
     * time passed since initiating the animation (seconds) is the value.
     */
    private final HashMap<Integer, Double> activeInteractiveAnimations = new HashMap<>();


    // CONSTRUCTOR
    /**
     * Constructs a LandmarkManager instance.
     *
     * @param gp GamePanel instance
     */
    public LandmarkManager(GamePanel gp) {
        this.gp = gp;
    }


    // METHODS
    /**
     * Updates the state of all landmarks by one frame.
     *
     * @param dt time since last frame (seconds)
     */
    public void update(double dt) {

        for (LandmarkBase landmark : gp.getMapM().getLoadedMap().getMapLandmarks()) {

            landmark.updateInteractiveAnimation(dt);
        }
    }



    /**
     * Loads map landmark data as landmark IDs from a text file.
     *
     * @param mapId ID of the map whose landmark data is to be loaded
     * @return loaded landmark data
     * @throws AssetLoadException if an error occurs while loading a landmark data file
     */
    public int[][] loadMapLandmarkData(int mapId) {

        String parsedMapId;

        if ((mapId > -1) && (mapId < 10)) {

            parsedMapId = "00" + mapId;
        } else if (mapId < 100) {

            parsedMapId = "0" + mapId;
        } else {

            parsedMapId = Integer.toString(mapId);
        }
        String completeFilePath = "/maps/map" + parsedMapId + "/map" + parsedMapId + "_landmarks.txt";
        int[][] mapLandmarkNum = new int[GamePanel.MAX_WORLD_COL][GamePanel.MAX_WORLD_ROW];                             // Initialize an array to store loaded landmark data.

        try (InputStream is = getClass().getResourceAsStream(completeFilePath);
             BufferedReader br = new BufferedReader(new InputStreamReader(is))) {                                       // We will read landmark values from the map text file one value at a time.

            int col = 0;
            int row = 0;

            while (row < GamePanel.MAX_WORLD_ROW) {                                                                     // Read each row of the map data from the text file.

                String line = br.readLine();                                                                            // `readLine()` reads a line of text from the text file and puts it into the String variable `line`.

                if (line == null) {                                                                                     // Check to see if we've exceeded the number of rows specified in the loaded map data.

                    for (int i = 0; i <= GamePanel.MAX_WORLD_COL; i++) {

                        mapLandmarkNum[col][row] = 0;                                                                   // Set the value of the current landmark being read to default zero (i.e., no landmark present).
                    }
                } else {

                    String[] numbers = line.split(" ");                                                                 // The line of text read previously will be split into an array; `split(" ")` will split the string at a space.

                    while (col < GamePanel.MAX_WORLD_COL) {                                                             // Read each column of the given line of text.

                        if (col >= numbers.length) {                                                                    // Check to see if we've exceeded the number of columns specified in the loaded map data.

                            mapLandmarkNum[col][row] = 0;                                                               // Set a default value for this landmark to zero since it isn't specified in the loaded map data.
                        } else {

                            int num = Integer.parseInt(numbers[col]);                                                   // Converting from String to int.
                            mapLandmarkNum[col][row] = num;                                                             // Set the value of the current landmark being read.
                        }
                        col++;                                                                                          // Iterate to next column in the string of text.
                    }
                }
                col = 0;                                                                                                // Reset column value for the next line of data to be read.
                row++;                                                                                                  // Iterate to next row of data from the text file.
            }

        } catch (Exception e) {

            throw new AssetLoadException("Could not load landmark data for map with ID '" + mapId + "' from resources '" + completeFilePath + "'");
        }
        return mapLandmarkNum;
    }


    /**
     * Instantiates loaded map landmark data.
     *
     * @param mapId ID of the map whose landmark data is to being instantiated
     * @param landmarkData loaded landmark data
     * @return instantiated map landmarks
     * @throws AssetLoadException if an error occurs while instantiating a landmark
     */
    public ArrayList<LandmarkBase> instantiateMapLandmarks(int mapId, int[][] landmarkData) {

        ArrayList<LandmarkBase> mapLandmarks = new ArrayList<>();                                                       // Initialize a list to store instantiated landmarks.

        try {

            int col = 0;
            int row = 0;

            while (row < GamePanel.MAX_WORLD_ROW) {                                                                     // Read each row of the loaded map landmark data.

                while (col < GamePanel.MAX_WORLD_COL) {                                                                 // Read each column of the given line of text.

                        int num = landmarkData[col][row];                                                               // Read landmark ID at this col/row position.

                        if (num != 0) {                                                                                 // A value of zero means that no landmark is present.

                            LandmarkBase landmark = setup(num, row, col);                                               // Instantiate and initialize the appropriate landmark.

                            if (landmark != null) {

                                mapLandmarks.add(landmark);
                            }
                        }

                    col++;                                                                                              // Iterate to next column.
                }
                col = 0;                                                                                                // Reset column value for the next line of data to be read.
                row++;                                                                                                  // Iterate to next row of data.
            }

        } catch (Exception e) {

            throw new AssetLoadException("Could not instantiate landmark data for map with ID '" + mapId);
        }
        return mapLandmarks;
    }


    /**
     * Calculates all tile locations on the loaded map that have collision (i.e., are solid) due to landmarks.
     * Landmark collision is pre-calculated to save compute resources (i.e., prevent it from being re-calculated each
     * time landmark collision is checked).
     * This method is to be called after all landmarks on the loaded map have been instantiated.
     */
    public void calculateGlobalLandmarkCollision() {

        for (boolean[] row : calculatedGlobalLandmarkCollision) {                                                       // Reset all tile locations to false.

            Arrays.fill(row, false);
        }
        int localCol = 0;
        int localRow = 0;
        int globalCol = 0;
        int globalRow = 0;

        for (LandmarkBase landmark : gp.getMapM().getLoadedMap().getMapLandmarks()) {

            while (localCol < landmark.getNumTilesCol()) {

                while (localRow < landmark.getNumTilesRow()) {

                    if (landmark.getCollision()[localCol][localRow]) {

                        try {

                            globalCol = landmark.getCol() + localCol;
                            globalRow = landmark.getRow() - localRow;
                            calculatedGlobalLandmarkCollision[globalCol][globalRow] = true;

                        } catch (ArrayIndexOutOfBoundsException e) {}
                    }
                    localRow++;
                }
                localRow = 0;
                localCol++;
            }
            localCol = 0;
        }
    }


    /**
     * Initiates an interactive animation for a landmark.
     * An interactive animation is intended to be triggered by a stock step interaction (i.e., a stock event that occurs
     * whenever any tile/landmark of a particular type is interacted with).
     * An example of an interactive animation is grass that only rustles when an entity walks through it.
     * This is not to be confused with a passive animation assigned to a landmark (i.e., passive animation group).
     * If an interactive animation for a landmark is already playing, then nothing will happen.
     *
     * @param landmarkCol column of the bottom-leftmost tile occupied by the landmark
     * @param landmarkRow row of the bottom-leftmost tile occupied by the landmark
     */
    public void initiateConditionalAnimation(int landmarkCol, int landmarkRow) {

        int positionId = Integer.parseInt(Integer.toString(landmarkCol) + landmarkRow);

        if (!activeInteractiveAnimations.containsKey(positionId)) {

            activeInteractiveAnimations.put(positionId, 0.0);
        }
    }


    /**
     * Instantiates and initializes a landmark.
     * Note that the number (`num`) of the landmark matches the landmark number of the landmark to be drawn in the map
     * text files.
     *
     * @param num number of the target landmark to be instantiated
     * @param row row of the tile that the bottom-left corner of landmark occupies
     * @param col column of the tile that the bottom-left corner of the landmark occupies
     * @return landmark
     */
    private LandmarkBase setup(int num, int row, int col) {

        LandmarkBase landmark;

        switch (num) {
            case 1:
                landmark = new Ldm_Waterfall1(gp, col, row);
                break;
            case 2:
                landmark = new Ldm_Waterfall2(gp, col, row);
                break;
            case 3:
                landmark = new Ldm_Waterfall3(gp, col, row);
                break;
            case 4:
                landmark = new Ldm_Foam1(gp, col, row);
                break;
            case 5:
                landmark = new Ldm_Foam2(gp, col, row);
                break;
            case 6:
                landmark = new Ldm_Grass1(gp, col, row);
                break;
            default:
                UtilityTool.logError("Attempted to instantiate a landmark type that does not exist.");
                return null;
        }
        return landmark;
    }


    // GETTERS
    public boolean[][] getCalculatedGlobalLandmarkCollision() {
        return calculatedGlobalLandmarkCollision;
    }

    public HashMap<Integer, Double> getActiveInteractiveAnimations() {
        return activeInteractiveAnimations;
    }
}
