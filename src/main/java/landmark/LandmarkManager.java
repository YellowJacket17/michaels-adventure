package landmark;

import core.GamePanel;
import landmark.implementation.Ldm_Tree1;
import landmark.implementation.Ldm_Tree2;
import utility.UtilityTool;
import utility.exceptions.AssetLoadException;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

/**
 * This class instantiates landmarks and handles related operations.
 */
public class LandmarkManager {

    // FIELD
    private final GamePanel gp;


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
     * Loads map landmark data from a text file.
     *
     * @param mapId ID of the map whose landmark data is to be loaded
     * @return loaded landmark data
     * @throws AssetLoadException if an error occurs while loading a landmark data file
     */
    public ArrayList<LandmarkBase> loadMapLandmarkData(int mapId) {

        String parsedMapId;

        if ((mapId > -1) && (mapId < 10)) {

            parsedMapId = "00" + mapId;
        } else if (mapId < 100) {

            parsedMapId = "0" + mapId;
        } else {

            parsedMapId = Integer.toString(mapId);
        }

        String completeFilePath = "/maps/map" + parsedMapId + "/map" + parsedMapId + "_landmarks.txt";
        ArrayList<LandmarkBase> mapLandmarks = new ArrayList<>();                                                       // Initialize a list to store instantiated landmarks.

        try (InputStream is = getClass().getResourceAsStream(completeFilePath);
             BufferedReader br = new BufferedReader(new InputStreamReader(is))) {                                       // We will read landmark values from the map text file one value at a time.

            int col = 0;
            int row = 0;

            while (row < gp.getMaxWorldRow()) {                                                                         // Read each row of the map data from the text file.

                String line = br.readLine();                                                                            // `readLine()` reads a line of text from the text file and puts it into the String variable `line`.

                if (line != null) {                                                                                     // Check to see if we've exceeded the number of rows specified  in the loaded map data.

                    String[] numbers = line.split(" ");                                                                 // The line of text read previously will be split into an array; `split(" ")` will split the string at a space.

                    while (col < gp.getMaxWorldCol()) {                                                                 // Read each column of the given line of text.

                        if (col < numbers.length) {

                            int num = Integer.parseInt(numbers[col]);                                                   // Converting from String to int.

                            if (num != 0) {                                                                             // A value of zero in the text file means that no landmark is present.

                                LandmarkBase landmark = setup(num, row, col);                                           // Instantiate and initialize the appropriate landmark.

                                if (landmark != null) {

                                    mapLandmarks.add(landmark);
                                }
                            }
                        }
                        col++;                                                                                          // Iterate to next column in the string of text.
                    }
                    col = 0;                                                                                            // Reset column value for the next line of data to be read.
                }
                row++;                                                                                                  // Iterate to next row of data from the text file.
            }

        } catch (Exception e) {

            throw new AssetLoadException("Could not load landmark data for map with ID " + mapId + " from " + completeFilePath);
        }
        return mapLandmarks;
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
                landmark = new Ldm_Tree1(gp);
                break;
            case 2:
                landmark = new Ldm_Tree2(gp);
                break;
            default:
                UtilityTool.logError("Attempted to instantiate a landmark type that does not exist.");
                return null;
        }

        landmark.setCol(col);
        landmark.setRow(row);
        return landmark;
    }
}
