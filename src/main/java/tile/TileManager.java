package tile;

import core.GamePanel;
import utility.UtilityTool;
import utility.exceptions.AssetLoadException;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.Set;

/**
 * This class instantiates tiles and handles related operations.
 */
public class TileManager {

    /*
     * WARNING: Attempting to instantiate a tile in a tile array index that's already occupied will overwrite the
     * existing tile.
     */

    // FIELDS
    private final GamePanel gp;

    /**
     * The index of the default tile that will load on a map if no data exists for a point in said map file.
     */
    private final int defaultTile = 0;

    /**
     * Array of tiles to be used in the game.
     * The index of each tile is essentially a tile type's ID.
     */
    private Tile[] tiles = new Tile[0];

    /**
     * Set to store tile draw errors. If a tile draw error occurs, the index associated with the tile will be added to
     * this set. This prevents a draw error from that tile type being printed to the console again.
     */
    private final Set<Integer> drawErrors = new HashSet<>();


    // CONSTRUCTOR
    /**
     * Constructs a TileManager instance.
     *
     * @param gp GamePanel instance
     */
    public TileManager(GamePanel gp) {
        this.gp = gp;
        getTileImage();                                                                                                 // Load tile sprites upon instantiation.
    }


    // METHODS
    /**
     * Draws tiles for the loaded map.
     *
     * @param g2 Graphics2D instance
     */
    public void draw(Graphics2D g2) {

        int worldCol = 0;
        int worldRow = 0;

        while ((worldCol < gp.getMaxWorldCol()) && (worldRow < gp.getMaxWorldRow())) {                                  // Draw each tile from left to right for each row, starting with the top row and working downwards.

            int tileNum;

            try {

                tileNum = gp.getLoadedMap().getMapTileNum()[worldCol][worldRow];                                        // Determine which tile type to draw from the loaded map data, determined by which map is currently being displayed.

            } catch (NullPointerException e) {

                tileNum = defaultTile;                                                                                  // If no map is loaded, just use the default tile.
            }
            int worldX = worldCol * gp.getTileSize();
            int worldY = worldRow * gp.getTileSize();
            int screenX = worldX - gp.getPlayer().getWorldX() + gp.getPlayer().getPlayerScreenX();                      // Determine where on the screen to draw tile (x)
            int screenY = worldY - gp.getPlayer().getWorldY() + gp.getPlayer().getPlayerScreenY();                      // ^^^

            int centerScreenX = gp.getPlayer().getCenterScreenX();
            int centerScreenY = gp.getPlayer().getCenterScreenY();
            int cameraOffsetX = gp.getPlayer().getCameraOffsetX();
            int cameraOffsetY = gp.getPlayer().getCameraOffsetY();

            // Improve rendering efficiency; only draw tiles visible on the screen.
            if (worldX + gp.getTileSize() > gp.getPlayer().getWorldX() - centerScreenX - cameraOffsetX &&                               // Left side of screen; in words: if ((world x position of tile plus an additional tile's length) > (left bound of the visible screen area))
                    worldX - gp.getTileSize() < gp.getPlayer().getWorldX() + (gp.getScreenWidth() - centerScreenX) - cameraOffsetX &&   // Right side of screen.
                    worldY + gp.getTileSize() > gp.getPlayer().getWorldY() - centerScreenY - cameraOffsetY &&                           // Top side of screen.
                    worldY - gp.getTileSize() < gp.getPlayer().getWorldY() + (gp.getScreenHeight() - centerScreenY) - cameraOffsetY) {  // Bottom side of screen.

                int image = 0;                                                                                          // Initialize which image from the tile will be drawn.

                if (tiles[tileNum].getImages().size() != 1) {                                                           // If this statement is false, the tile only has one image attached to it, hence it cannot be animated.

                    switch (tiles[tileNum].getAnimationGroup()) {
                        case 0:                                                                                         // Animation group 0: water1 animation.
                            image = gp.getAnimationM().getImage(0);
                            break;
                    }
                }

                if ((tiles[tileNum].getImages().get(image) != null) && (image < tiles[tileNum].getImages().size())) {

                    g2.drawImage(tiles[tileNum].getImages().get(image), screenX, screenY, null);
                } else if (!drawErrors.contains(tileNum)) {

                    UtilityTool.logError("Failed to draw tile at index "
                            + tileNum
                            + ": the map may contain a tile that does not exist or a tile may have been assigned to the incorrect animation group.");
                    drawErrors.add(tileNum);
                }
            }
            worldCol++;                                                                                                 // Iterate so that we can draw the next tile.

            if (worldCol == gp.getMaxWorldCol()) {
                worldCol = 0;
                worldRow++;
            }
        }

        // DEBUG.
        // Draw path found by A* pathfinding algorithm.
//        if (gp.isDebugVisible() == true) {
//
//            g2.setColor(new Color(255, 0, 0, 70));
//
//            for (int i = 0; i < gp.getPathF().getPathList().size(); i++) {
//
//                int worldX = gp.getPathF().getPathList().get(i).getCol() * gp.getTileSize();
//                int worldY = gp.getPathF().getPathList().get(i).getRow() * gp.getTileSize();
//                int screenX = worldX - gp.getPlayer().getWorldX() + gp.getPlayer().getScreenX();                        // Determine where on the screen to draw tile (x).
//                int screenY = worldY - gp.getPlayer().getWorldY() + gp.getPlayer().getScreenY();                        // Determine where on the screen to draw tile (y).
//
//                g2.fillRect(screenX, screenY, gp.getTileSize(), gp.getTileSize());
//            }
//        }
    }


    /**
     * Loads map tile data from a text file.
     *
     * @param mapId ID of the map whose tile data is to be loaded
     * @return loaded tile data
     * @throws AssetLoadException if an error occurs while loading map tile data
     */
    public int[][] loadMapTileData(int mapId) {

        String parsedMapId;

        if ((mapId > -1) && (mapId < 10)) {

            parsedMapId = "00" + mapId;
        } else if (mapId < 100) {

            parsedMapId = "0" + mapId;
        } else {

            parsedMapId = Integer.toString(mapId);
        }
        String completeFilePath = "/maps/map" + parsedMapId + "/map" + parsedMapId + "_tiles.txt";
        int[][] mapTileNum = new int[gp.getMaxWorldCol()][gp.getMaxWorldRow()];                                         // Initialize an array to store loaded tile data.

        try (InputStream is = getClass().getResourceAsStream(completeFilePath);
             BufferedReader br = new BufferedReader(new InputStreamReader(is))) {                                       // We will read tile values from the map text file one value at a time.

            int col = 0;
            int row = 0;

            while (row < gp.getMaxWorldRow()) {                                                                         // Read each row of the map data from the text file.

                String line = br.readLine();                                                                            // `readLine()` reads a line of text from the text file and puts it into the String variable `line`.

                if (line == null) {                                                                                     // Check to see if we've exceeded the number of rows specified in the loaded map data.

                    for (int i = 0; i <= gp.getMaxWorldCol(); i++) {

                        mapTileNum[col][row] = defaultTile;                                                             // Set the value of the current tile being read.
                    }
                    row++;
                } else {

                    String[] numbers = line.split(" ");                                                                 // The line of text read previously will be split into an array; `split(" ")` will split the string at a space.

                    while (col < gp.getMaxWorldCol()) {                                                                 // Read each column of the given line of text.

                        if (col >= numbers.length) {                                                                    // Check to see if we've exceeded the number of columns specified in the loaded map data.

                            mapTileNum[col][row] = defaultTile;                                                         // Set a default value for this tile to zero since it isn't specified in the loaded map data.
                        } else {

                            int num = Integer.parseInt(numbers[col]);                                                   // Converting from String to int.
                            mapTileNum[col][row] = num;                                                                 // Set the value of the current tile being read.
                        }
                        col++;                                                                                          // Iterate to next column in the string of text.
                    }
                    col = 0;                                                                                            // Reset column value for the next line of data to be read.
                    row++;                                                                                              // Iterate to next row of data from the text file.
                }
            }

        } catch (Exception e) {

            throw new AssetLoadException("Could not load tile data for map with ID " + mapId + " from " + completeFilePath);
        }
        return mapTileNum;
    }


    /**
     * Stages tile sprites to be loaded from resources directory.
     * Parameters for tile collision and animation are also set here.
     */
    private void getTileImage() {

        setup(0, "000_default.png", true);

        setup(1, "001_grass1.png", false);

        setup(2, "002_water1.png", true);

        setup(3, "003_water2-1.png", true, 0);

            addImage(3, "003_water2-2.png");

            addImage(3, "003_water2-3.png");

            addImage(3, "003_water2-4.png");

        setup(4, "004_rock1.png", true);

        setup(5, "005_portal.png", false);

        setup(6, "006_dirt1.png", false);
    }


    /**
     * Instantiates a new tile with specified parameters.
     * Note that the index that the tile occupies in the tile array matches the tile number of the tile to be drawn
     * in the map text files.
     * Essentially, the index of the tile in the tile array is its ID.
     *
     * @param index index that the tile will occupy in the array of tiles
     * @param fileName file name of tile sprite, located in resources/tiles directory
     * @param collision parameter for whether the tile will have collision (true) or not (false)
     */
    private void setup(int index, String fileName, boolean collision) {

        expandTileArray();
        tiles[index] = new Tile();
        tiles[index].setCollision(collision);
        addImage(index, fileName);
    }


    /**
     * Instantiates a new tile with specified parameters.
     * Note that the index that the tile occupies in the tile array matches the tile number of the tile to be drawn
     * in the map text files.
     * Essentially, the index of the tile in the tile array is its ID.
     *
     * @param index index that the tile will occupy in the array of tiles (`tiles` array).
     * @param fileName file name of tile sprite, located in resources/tiles directory
     * @param collision parameter for whether the tile will have collision (true) or not (false)
     * @param group parameter for which animation group the tile belongs to
     */
    private void setup(int index, String fileName, boolean collision, int group) {

        expandTileArray();
        tiles[index] = new Tile();
        tiles[index].setCollision(collision);
        tiles[index].setAnimationGroup(group);
        addImage(index, fileName);
    }


    /**
     * Adds a sprite to a tile that's already been instantiated by loading and scaling the sprite.
     * Recommended file format is PNG.
     *
     * @param index index that the tile occupies in the array of tiles (`tiles` array).
     * @param fileName file name of tile sprite, located in resources/tiles directory
     * @throws AssetLoadException if an error occurs while loading a tile sprite
     */
    private void addImage(int index, String fileName) {

        String completeFilePath = "/tiles/" + fileName;

        try (InputStream is = getClass().getResourceAsStream(completeFilePath)) {

            BufferedImage image = ImageIO.read(is);
            tiles[index].addImage(UtilityTool.scaleImage(image, gp.getTileSize(), gp.getTileSize()));

        } catch (IOException | IllegalArgumentException e) {

            throw new AssetLoadException("Could not load tile sprite from " + completeFilePath);

        } catch (NullPointerException e) {

            UtilityTool.logError("Attempted to add an image to a tile instance at index "
                    + index
                    + " that does not exist.");
        }
    }


    /**
     * Expand the array of loaded tiles by one index while retaining all existing tiles.
     */
    private void expandTileArray() {

        Tile[] temp = tiles;                                                                                            // Create a temporary array to store existing tiles in.
        int originalLength = tiles.length;                                                                              // Store the length of the original tile array.
        tiles = new Tile[originalLength + 1];                                                                           // Create a new tile array that has one extra slot compared to the original.

        for (int i = 0; i < originalLength; i++) {                                                                      // Add original tiles back to the new tile array.

            tiles[i] = temp[i];
        }
    }


    // GETTER
    public Tile[] getTiles() {
        return tiles;
    }
}
