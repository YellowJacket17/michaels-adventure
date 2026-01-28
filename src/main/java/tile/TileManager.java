package tile;

import asset.AssetPool;
import core.GamePanel;
import org.joml.Vector2f;
import render.Renderer;
import asset.Sprite;
import render.enumeration.ZIndex;
import render.drawable.Drawable;
import render.drawable.Transform;
import utility.UtilityTool;
import utility.exceptions.AssetLoadException;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashSet;

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
    private Tile[] tiles = new Tile[179];

    /**
     * Array to store the drawables associated with each possible tile world position.
     */
    private final Drawable[][] drawables;

    /**
     * Set to store tile render errors.
     * If a tile render error occurs, the index associated with the tile will be added to this set.
     * This prevents a render error from that tile type being printed to the console again.
     */
    private final HashSet<Integer> renderErrors = new HashSet<>();


    // CONSTRUCTOR
    /**
     * Constructs a TileManager instance.
     *
     * @param gp GamePanel instance
     */
    public TileManager(GamePanel gp) {
        this.gp = gp;
        drawables = new Drawable[GamePanel.MAX_WORLD_COL][GamePanel.MAX_WORLD_ROW];
        loadTiles();
        initializeDrawablesArray();
    }


    // METHODS
    /**
     * Adds all tiles of the loaded map to the render pipeline.
     *
     * @param renderer Renderer instance
     */
    public void addToRenderPipeline(Renderer renderer) {

        if (gp.isRenderWorld() && !gp.getIllustrationS().isIllustrationActive()) {

            int worldCol = 0;
            int worldRow = 0;

            while ((worldCol < GamePanel.MAX_WORLD_COL) && (worldRow < GamePanel.MAX_WORLD_ROW)) {                      // Render each tile from left to right for each row, starting with the top row and working downwards.

                int tileNum;
                try {
                    tileNum = gp.getMapM().getLoadedMap().getMapTileNum()[worldCol][worldRow];                          // Determine which tile type to render from the loaded map data, determined by which map is currently being displayed.
                } catch (NullPointerException e) {
                    tileNum = defaultTile;                                                                              // If no map is loaded, just use the default tile.
                }
                int spriteNum = gp.getPassiveAnimationM().getSprite(
                        tiles[tileNum].getPassiveAnimationGroup(), worldCol, worldRow);                                 // Render appropriate tile in passive animation cycle, if applicable.

                if ((spriteNum < tiles[tileNum].getSprites().size())
                        && (spriteNum >= 0)
                        && (tiles[tileNum].getSprites().get(spriteNum) != null)) {

                    Sprite sprite = tiles[tileNum].getSprites().get(spriteNum);
                    drawables[worldCol][worldRow].setSprite(sprite);
                    renderer.addDrawable(drawables[worldRow][worldCol], ZIndex.THIRD_LAYER);

                } else if (!renderErrors.contains(tileNum)) {
                    UtilityTool.logError("Failed to add tile at index '"
                            + tileNum
                            + "' to the render pipeline: the map may contain a tile that does not exist or a tile may"
                            + " have been assigned an incorrect animation.");
                    renderErrors.add(tileNum);
                }

                worldCol++;                                                                                             // Iterate so that we can render the next tile.
                if (worldCol == GamePanel.MAX_WORLD_COL) {
                    worldCol = 0;
                    worldRow++;
                }
            }
        }
    }


    /**
     * Loads map tile data as tile IDs from a text file.
     *
     * @param mapId ID of the map whose tile data is to be loaded
     * @return loaded tile data
     * @throws AssetLoadException if an error occurs while loading map tile data
     */
    public int[][] loadMapTileData(int mapId) {

        resetDrawablesArray();
        String parsedMapId;

        if ((mapId > -1) && (mapId < 10)) {

            parsedMapId = "00" + mapId;
        } else if (mapId < 100) {

            parsedMapId = "0" + mapId;
        } else {

            parsedMapId = Integer.toString(mapId);
        }
        String completeFilePath = "/maps/map" + parsedMapId + "/map" + parsedMapId + "_tiles.txt";
        int[][] mapTileNum = new int[GamePanel.MAX_WORLD_COL][GamePanel.MAX_WORLD_ROW];                                 // Initialize an array to store loaded tile data.

        try (InputStream is = getClass().getResourceAsStream(completeFilePath);
             BufferedReader br = new BufferedReader(new InputStreamReader(is))) {                                       // We will read tile values from the map text file one value at a time.

            int col = 0;
            int row = 0;

            while (row < GamePanel.MAX_WORLD_ROW) {                                                                     // Read each row of the map data from the text file.

                String line = br.readLine();                                                                            // `readLine()` reads a line of text from the text file and puts it into the String variable `line`.

                if (line == null) {                                                                                     // Check to see if we've exceeded the number of rows specified in the loaded map data.

                    for (int i = 0; i <= GamePanel.MAX_WORLD_COL; i++) {

                        mapTileNum[col][row] = defaultTile;                                                             // Set the value of the current tile being read.
                    }
                } else {

                    String[] numbers = line.split(" ");                                                                 // The line of text read previously will be split into an array; `split(" ")` will split the string at a space.

                    while (col < GamePanel.MAX_WORLD_COL) {                                                             // Read each column of the given line of text.

                        if (col >= numbers.length) {                                                                    // Check to see if we've exceeded the number of columns specified in the loaded map data.

                            mapTileNum[col][row] = defaultTile;                                                         // Set a default value for this tile to zero since it isn't specified in the loaded map data.
                        } else {

                            int num = Integer.parseInt(numbers[col]);                                                   // Converting from String to int.
                            mapTileNum[col][row] = num;                                                                 // Set the value of the current tile being read.
                        }
                        col++;                                                                                          // Iterate to next column in the string of text.
                    }
                }
                col = 0;                                                                                                // Reset column value for the next line of data to be read.
                row++;                                                                                                  // Iterate to next row of data from the text file.
            }

        } catch (Exception e) {

            throw new AssetLoadException("Could not load tile data for map with ID '" + mapId + "' from '" + completeFilePath + "'");
        }
        return mapTileNum;
    }


    /**
     * Stages tile sprites to be loaded from resources directory.
     * Parameters for tile collision and animation are also set here.
     */
    private void loadTiles() {

        setup(0, AssetPool.getSpritesheet("tiles").getSprite(0), true);                                                 // default

        setup(1, AssetPool.getSpritesheet("tiles").getSprite(1), false);                                                // floor1

        setup(2, AssetPool.getSpritesheet("tiles").getSprite(2), false, 0);                                             // floor2_1

            addSprite(2, AssetPool.getSpritesheet("tiles").getSprite(3));                                               // floor2_2

            addSprite(2, AssetPool.getSpritesheet("tiles").getSprite(4));                                               // floor2_3

            addSprite(2, AssetPool.getSpritesheet("tiles").getSprite(1));                                               // floor2_4

        setup(3, AssetPool.getSpritesheet("tiles").getSprite(5), false, 0);                                             // floor3_1

            addSprite(3, AssetPool.getSpritesheet("tiles").getSprite(6));                                               // floor3_2

            addSprite(3, AssetPool.getSpritesheet("tiles").getSprite(7));                                               // floor3_3

            addSprite(3, AssetPool.getSpritesheet("tiles").getSprite(1));                                               // floor3_4

        setup(4, AssetPool.getSpritesheet("tiles").getSprite(8), false, 0);                                             // floor4_1

            addSprite(4, AssetPool.getSpritesheet("tiles").getSprite(9));                                               // floor4_2

            addSprite(4, AssetPool.getSpritesheet("tiles").getSprite(10));                                              // floor4_3

            addSprite(4, AssetPool.getSpritesheet("tiles").getSprite(1));                                               // floor4_4

        setup(5, AssetPool.getSpritesheet("tiles").getSprite(11), true);                                                // water1

        setup(6, AssetPool.getSpritesheet("tiles").getSprite(12), true, 0);                                             // water2_1

            addSprite(6, AssetPool.getSpritesheet("tiles").getSprite(13));                                              // water2_2

            addSprite(6, AssetPool.getSpritesheet("tiles").getSprite(14));                                              // water2_3

            addSprite(6, AssetPool.getSpritesheet("tiles").getSprite(11));                                              // water2_4

        setup(7, AssetPool.getSpritesheet("tiles").getSprite(15), true, 3);                                             // water3_1

            addSprite(7, AssetPool.getSpritesheet("tiles").getSprite(16));                                              // water3_2

            addSprite(7, AssetPool.getSpritesheet("tiles").getSprite(17));                                              // water3_3

            addSprite(7, AssetPool.getSpritesheet("tiles").getSprite(18));                                              // water3_4

        setup(8, AssetPool.getSpritesheet("tiles").getSprite(19), true);                                                // waterBottomStraightFloorEdge1

        setup(9, AssetPool.getSpritesheet("tiles").getSprite(20), true);                                                // waterBottomStraightFloorEdge2

        setup(10, AssetPool.getSpritesheet("tiles").getSprite(21), true);                                               // waterBottomStraightFloorEdge3

        setup(11, AssetPool.getSpritesheet("tiles").getSprite(22), true);                                               // waterTopStraightFloorEdge1

        setup(12, AssetPool.getSpritesheet("tiles").getSprite(23), true);                                               // waterTopStraightFloorEdge2

        setup(13, AssetPool.getSpritesheet("tiles").getSprite(24), true);                                               // waterTopStraightFloorEdge3

        setup(14, AssetPool.getSpritesheet("tiles").getSprite(25), true);                                               // waterLeftStraightFloorEdge1

        setup(15, AssetPool.getSpritesheet("tiles").getSprite(26), true);                                               // waterLeftStraightFloorEdge2

        setup(16, AssetPool.getSpritesheet("tiles").getSprite(27), true);                                               // waterLeftStraightFloorEdge3

        setup(17, AssetPool.getSpritesheet("tiles").getSprite(28), true);                                               // waterRightStraightFloorEdge1

        setup(18, AssetPool.getSpritesheet("tiles").getSprite(29), true);                                               // waterRightStraightFloorEdge2

        setup(19, AssetPool.getSpritesheet("tiles").getSprite(30), true);                                               // waterRightStraightFloorEdge3

        setup(20, AssetPool.getSpritesheet("tiles").getSprite(31), true);                                               // waterBottomLeftConcaveFloorEdge1

        setup(21, AssetPool.getSpritesheet("tiles").getSprite(32), true);                                               // waterBottomRightConcaveFloorEdge1

        setup(22, AssetPool.getSpritesheet("tiles").getSprite(33), true);                                               // waterTopLeftConcaveFloorEdge1

        setup(23, AssetPool.getSpritesheet("tiles").getSprite(34), true);                                               // waterTopRightConcaveFloorEdge1

        setup(24, AssetPool.getSpritesheet("tiles").getSprite(35), true);                                               // waterBottomLeftConvexFloorEdge1

        setup(25, AssetPool.getSpritesheet("tiles").getSprite(36), true);                                               // waterBottomLeftConvexFloorEdge2

        setup(26, AssetPool.getSpritesheet("tiles").getSprite(37), false);                                              // waterBottomLeftConvexFloorEdge3

        setup(27, AssetPool.getSpritesheet("tiles").getSprite(38), true);                                               // waterBottomLeftConvexFloorEdge4

        setup(28, AssetPool.getSpritesheet("tiles").getSprite(39), true);                                               // waterBottomRightConvexFloorEdge1

        setup(29, AssetPool.getSpritesheet("tiles").getSprite(40), true);                                               // waterBottomRightConvexFloorEdge2

        setup(30, AssetPool.getSpritesheet("tiles").getSprite(41), false);                                              // waterBottomRightConvexFloorEdge3

        setup(31, AssetPool.getSpritesheet("tiles").getSprite(42), true);                                               // waterBottomRightConvexFloorEdge4

        setup(32, AssetPool.getSpritesheet("tiles").getSprite(43), true);                                               // waterTopLeftConvexFloorEdge1

        setup(33, AssetPool.getSpritesheet("tiles").getSprite(44), true);                                               // waterTopLeftConvexFloorEdge2

        setup(34, AssetPool.getSpritesheet("tiles").getSprite(45), false);                                              // waterTopLeftConvexFloorEdge3

        setup(35, AssetPool.getSpritesheet("tiles").getSprite(46), true);                                               // waterTopRightConvexFloorEdge1

        setup(36, AssetPool.getSpritesheet("tiles").getSprite(47), true);                                               // waterTopRightConvexFloorEdge2

        setup(37, AssetPool.getSpritesheet("tiles").getSprite(48), false);                                              // waterTopRightConvexFloorEdge3

        setup(38, AssetPool.getSpritesheet("tiles").getSprite(49), true);                                               // floorBottomStraightWallEdge1

        setup(39, AssetPool.getSpritesheet("tiles").getSprite(50), true);                                               // floorTopStraightWallEdge1

        setup(40, AssetPool.getSpritesheet("tiles").getSprite(51), true);                                               // floorTopStraightWallEdge2

        setup(41, AssetPool.getSpritesheet("tiles").getSprite(52), true);                                               // floorTopStraightWallEdge3

        setup(42, AssetPool.getSpritesheet("tiles").getSprite(53), true);                                               // floorLeftStraightWallEdge1

        setup(43, AssetPool.getSpritesheet("tiles").getSprite(54), true);                                               // floorLeftStraightWallEdge2

        setup(44, AssetPool.getSpritesheet("tiles").getSprite(55), true);                                               // floorLeftStraightWallEdge3

        setup(45, AssetPool.getSpritesheet("tiles").getSprite(56), true);                                               // floorLeftStraightWallEdge4

        setup(46, AssetPool.getSpritesheet("tiles").getSprite(57), true);                                               // floorLeftStraightWallEdge5

        setup(47, AssetPool.getSpritesheet("tiles").getSprite(58), true);                                               // floorRightStraightWallEdge1

        setup(48, AssetPool.getSpritesheet("tiles").getSprite(59), true);                                               // floorRightStraightWallEdge2

        setup(49, AssetPool.getSpritesheet("tiles").getSprite(60), true);                                               // floorRightStraightWallEdge3

        setup(50, AssetPool.getSpritesheet("tiles").getSprite(61), true);                                               // floorRightStraightWallEdge4

        setup(51, AssetPool.getSpritesheet("tiles").getSprite(62), true);                                               // floorRightStraightWallEdge5

        setup(52, AssetPool.getSpritesheet("tiles").getSprite(63), true);                                               // floorBottomLeftConcaveWallEdge1

        setup(53, AssetPool.getSpritesheet("tiles").getSprite(64), true);                                               // floorBottomRightConcaveWallEdge1

        setup(54, AssetPool.getSpritesheet("tiles").getSprite(65), true);                                               // floorTopLeftConcaveWallEdge1

        setup(55, AssetPool.getSpritesheet("tiles").getSprite(66), true);                                               // floorTopRightConcaveWallEdge1

        setup(56, AssetPool.getSpritesheet("tiles").getSprite(67), true);                                               // floorBottomLeftConvexWallEdge1

        setup(57, AssetPool.getSpritesheet("tiles").getSprite(68), true);                                               // floorBottomRightConvexWallEdge1

        setup(58, AssetPool.getSpritesheet("tiles").getSprite(69), true);                                               // floorTopLeftConvexWallEdge1

        setup(59, AssetPool.getSpritesheet("tiles").getSprite(70), true);                                               // floorTopRightConvexWallEdge1

        setup(60, AssetPool.getSpritesheet("tiles").getSprite(71), true);                                               // waterBottomStraightWallEdge1

        setup(61, AssetPool.getSpritesheet("tiles").getSprite(72), true);                                               // waterTopStraightWallEdge1

        setup(62, AssetPool.getSpritesheet("tiles").getSprite(73), true);                                               // waterLeftStraightWallEdge1

        setup(63, AssetPool.getSpritesheet("tiles").getSprite(74), true);                                               // waterLeftStraightWallEdge2

        setup(64, AssetPool.getSpritesheet("tiles").getSprite(75), true);                                               // waterLeftStraightWallEdge3

        setup(65, AssetPool.getSpritesheet("tiles").getSprite(76), true);                                               // waterRightStraightWallEdge1

        setup(66, AssetPool.getSpritesheet("tiles").getSprite(77), true);                                               // waterRightStraightWallEdge2

        setup(67, AssetPool.getSpritesheet("tiles").getSprite(78), true);                                               // waterRightStraightWallEdge3

        setup(68, AssetPool.getSpritesheet("tiles").getSprite(79), true);                                               // waterBottomLeftConcaveWallEdge1

        setup(69, AssetPool.getSpritesheet("tiles").getSprite(80), true);                                               // waterBottomRightConcaveWallEdge1

        setup(70, AssetPool.getSpritesheet("tiles").getSprite(81), true);                                               // waterTopLeftConcaveWallEdge1

        setup(71, AssetPool.getSpritesheet("tiles").getSprite(82), true);                                               // waterTopLeftConcaveWallEdge2

        setup(72, AssetPool.getSpritesheet("tiles").getSprite(83), true);                                               // waterTopRightConcaveWallEdge1

        setup(73, AssetPool.getSpritesheet("tiles").getSprite(84), true);                                               // waterTopRightConcaveWallEdge2

        setup(74, AssetPool.getSpritesheet("tiles").getSprite(85), true);                                               // waterBottomLeftConvexWallEdge1

        setup(75, AssetPool.getSpritesheet("tiles").getSprite(86), true);                                               // waterBottomRightConvexWallEdge1

        setup(76, AssetPool.getSpritesheet("tiles").getSprite(87), true);                                               // waterTopLeftConvexWallEdge1

        setup(77, AssetPool.getSpritesheet("tiles").getSprite(88), true);                                               // waterTopRightConvexWallEdge1

        setup(78, AssetPool.getSpritesheet("tiles").getSprite(89), false);                                              // bridgeLatitude1

        setup(79, AssetPool.getSpritesheet("tiles").getSprite(90), false, 0);                                           // bridgeLatitude2_1

            addSprite(79, AssetPool.getSpritesheet("tiles").getSprite(91));                                             // bridgeLatitude2_2

            addSprite(79, AssetPool.getSpritesheet("tiles").getSprite(92));                                             // bridgeLatitude2_3

            addSprite(79, AssetPool.getSpritesheet("tiles").getSprite(89));                                             // bridgeLatitude2_4

        setup(80, AssetPool.getSpritesheet("tiles").getSprite(93), true);                                               // bridgeLatitude3

        setup(81, AssetPool.getSpritesheet("tiles").getSprite(94), true, 0);                                            // bridgeLatitude4_1

            addSprite(81, AssetPool.getSpritesheet("tiles").getSprite(95));                                             // bridgeLatitude4_2

            addSprite(81, AssetPool.getSpritesheet("tiles").getSprite(96));                                             // bridgeLatitude4_3

            addSprite(81, AssetPool.getSpritesheet("tiles").getSprite(93));                                             // bridgeLatitude4_4

        setup(82, AssetPool.getSpritesheet("tiles").getSprite(97), false);                                              // bridgeLatitude5

        setup(83, AssetPool.getSpritesheet("tiles").getSprite(98), false, 0);                                           // bridgeLatitude6_1

            addSprite(83, AssetPool.getSpritesheet("tiles").getSprite(99));                                             // bridgeLatitude6_2

            addSprite(83, AssetPool.getSpritesheet("tiles").getSprite(100));                                            // bridgeLatitude6_3

            addSprite(83, AssetPool.getSpritesheet("tiles").getSprite(97));                                             // bridgeLatitude6_4

        setup(84, AssetPool.getSpritesheet("tiles").getSprite(101), false);                                             // bridgeLatitude7

        setup(85, AssetPool.getSpritesheet("tiles").getSprite(102), false, 0);                                          // bridgeLatitude8_1

            addSprite(85, AssetPool.getSpritesheet("tiles").getSprite(103));                                            // bridgeLatitude8_2

            addSprite(85, AssetPool.getSpritesheet("tiles").getSprite(104));                                            // bridgeLatitude8_3

            addSprite(85, AssetPool.getSpritesheet("tiles").getSprite(101));                                            // bridgeLatitude8_4

        setup(86, AssetPool.getSpritesheet("tiles").getSprite(105), true);                                              // bridgeLatitude9

        setup(87, AssetPool.getSpritesheet("tiles").getSprite(106), true, 0);                                           // bridgeLatitude10_1

            addSprite(87, AssetPool.getSpritesheet("tiles").getSprite(107));                                            // bridgeLatitude10_2

            addSprite(87, AssetPool.getSpritesheet("tiles").getSprite(108));                                            // bridgeLatitude10_3

            addSprite(87, AssetPool.getSpritesheet("tiles").getSprite(105));                                            // bridgeLatitude10_4

        setup(88, AssetPool.getSpritesheet("tiles").getSprite(109), false);                                             // bridgeLatitude11

        setup(89, AssetPool.getSpritesheet("tiles").getSprite(110), false, 0);                                          // bridgeLatitude12_1

            addSprite(89, AssetPool.getSpritesheet("tiles").getSprite(111));                                            // bridgeLatitude12_2

            addSprite(89, AssetPool.getSpritesheet("tiles").getSprite(112));                                            // bridgeLatitude12_3

            addSprite(89, AssetPool.getSpritesheet("tiles").getSprite(109));                                            // bridgeLatitude12_4

        setup(90, AssetPool.getSpritesheet("tiles").getSprite(113), true);                                              // bridgeLatitude13

        setup(91, AssetPool.getSpritesheet("tiles").getSprite(114), true, 0);                                           // bridgeLatitude14_1

            addSprite(91, AssetPool.getSpritesheet("tiles").getSprite(115));                                            // bridgeLatitude14_2

            addSprite(91, AssetPool.getSpritesheet("tiles").getSprite(116));                                            // bridgeLatitude14_3

            addSprite(91, AssetPool.getSpritesheet("tiles").getSprite(113));                                            // bridgeLatitude14_4

        setup(92, AssetPool.getSpritesheet("tiles").getSprite(117), false);                                             // bridgeLatitude15

        setup(93, AssetPool.getSpritesheet("tiles").getSprite(118), false, 0);                                          // bridgeLatitude16_1

            addSprite(93, AssetPool.getSpritesheet("tiles").getSprite(119));                                            // bridgeLatitude16_2

            addSprite(93, AssetPool.getSpritesheet("tiles").getSprite(120));                                            // bridgeLatitude16_3

            addSprite(93, AssetPool.getSpritesheet("tiles").getSprite(117));                                            // bridgeLatitude16_4

        setup(94, AssetPool.getSpritesheet("tiles").getSprite(121), true);                                              // bridgeLatitude17

        setup(95, AssetPool.getSpritesheet("tiles").getSprite(122), true, 0);                                           // bridgeLatitude18_1

            addSprite(95, AssetPool.getSpritesheet("tiles").getSprite(123));                                            // bridgeLatitude18_2

            addSprite(95, AssetPool.getSpritesheet("tiles").getSprite(124));                                            // bridgeLatitude18_3

            addSprite(95, AssetPool.getSpritesheet("tiles").getSprite(121));                                            // bridgeLatitude18_4

        setup(96, AssetPool.getSpritesheet("tiles").getSprite(125), false);                                             // bridgeLatitudeBottomFloorEdge1

        setup(97, AssetPool.getSpritesheet("tiles").getSprite(126), false);                                             // bridgeLatitudeBottomFloorEdge2

        setup(98, AssetPool.getSpritesheet("tiles").getSprite(127), false);                                             // bridgeLatitudeBottomFloorEdge3

        setup(99, AssetPool.getSpritesheet("tiles").getSprite(128), false);                                             // bridgeLatitudeBottomFloorEdge4

        setup(100, AssetPool.getSpritesheet("tiles").getSprite(129), false);                                            // bridgeLatitudeBottomFloorEdge5

        setup(101, AssetPool.getSpritesheet("tiles").getSprite(130), false);                                            // bridgeLongitude1

        setup(102, AssetPool.getSpritesheet("tiles").getSprite(131), false, 0);                                         // bridgeLongitude2_1

            addSprite(102, AssetPool.getSpritesheet("tiles").getSprite(132));                                           // bridgeLongitude2_2

            addSprite(102, AssetPool.getSpritesheet("tiles").getSprite(133));                                           // bridgeLongitude2_3

            addSprite(102, AssetPool.getSpritesheet("tiles").getSprite(130));                                           // bridgeLongitude2_4

        setup(103, AssetPool.getSpritesheet("tiles").getSprite(134), true);                                             // bridgeLongitude3

        setup(104, AssetPool.getSpritesheet("tiles").getSprite(135), true, 0);                                          // bridgeLongitude4_1

            addSprite(104, AssetPool.getSpritesheet("tiles").getSprite(136));                                           // bridgeLongitude4_2

            addSprite(104, AssetPool.getSpritesheet("tiles").getSprite(137));                                           // bridgeLongitude4_3

            addSprite(104, AssetPool.getSpritesheet("tiles").getSprite(134));                                           // bridgeLongitude4_4

        setup(105, AssetPool.getSpritesheet("tiles").getSprite(138), false);                                            // bridgeLongitude5

        setup(106, AssetPool.getSpritesheet("tiles").getSprite(139), false, 0);                                         // bridgeLongitude6_1

            addSprite(106, AssetPool.getSpritesheet("tiles").getSprite(140));                                           // bridgeLongitude6_2

            addSprite(106, AssetPool.getSpritesheet("tiles").getSprite(141));                                           // bridgeLongitude6_3

            addSprite(106, AssetPool.getSpritesheet("tiles").getSprite(138));                                           // bridgeLongitude6_4

        setup(107, AssetPool.getSpritesheet("tiles").getSprite(142), false);                                            // bridgeLongitude7

        setup(108, AssetPool.getSpritesheet("tiles").getSprite(143), false, 0);                                         // bridgeLongitude8_1

            addSprite(108, AssetPool.getSpritesheet("tiles").getSprite(144));                                           // bridgeLongitude8_2

            addSprite(108, AssetPool.getSpritesheet("tiles").getSprite(145));                                           // bridgeLongitude8_3

            addSprite(108, AssetPool.getSpritesheet("tiles").getSprite(142));                                           // bridgeLongitude8_4

        setup(109, AssetPool.getSpritesheet("tiles").getSprite(146), true);                                             // bridgeLongitude9

        setup(110, AssetPool.getSpritesheet("tiles").getSprite(147), true, 0);                                          // bridgeLongitude10_1

            addSprite(110, AssetPool.getSpritesheet("tiles").getSprite(148));                                           // bridgeLongitude10_2

            addSprite(110, AssetPool.getSpritesheet("tiles").getSprite(149));                                           // bridgeLongitude10_3

            addSprite(110, AssetPool.getSpritesheet("tiles").getSprite(146));                                           // bridgeLongitude10_4

        setup(111, AssetPool.getSpritesheet("tiles").getSprite(150), false);                                            // bridgeLongitude11

        setup(112, AssetPool.getSpritesheet("tiles").getSprite(151), false, 0);                                         // bridgeLongitude12_1

            addSprite(112, AssetPool.getSpritesheet("tiles").getSprite(152));                                           // bridgeLongitude12_2

            addSprite(112, AssetPool.getSpritesheet("tiles").getSprite(153));                                           // bridgeLongitude12_3

            addSprite(112, AssetPool.getSpritesheet("tiles").getSprite(150));                                           // bridgeLongitude12_4

        setup(113, AssetPool.getSpritesheet("tiles").getSprite(154), true);                                             // bridgeLongitude13

        setup(114, AssetPool.getSpritesheet("tiles").getSprite(155), true, 0);                                          // bridgeLongitude14_1

            addSprite(114, AssetPool.getSpritesheet("tiles").getSprite(156));                                           // bridgeLongitude14_2

            addSprite(114, AssetPool.getSpritesheet("tiles").getSprite(157));                                           // bridgeLongitude14_3

            addSprite(114, AssetPool.getSpritesheet("tiles").getSprite(154));                                           // bridgeLongitude14_4

        setup(115, AssetPool.getSpritesheet("tiles").getSprite(158), false);                                            // bridgeLongitude15

        setup(116, AssetPool.getSpritesheet("tiles").getSprite(159), false, 0);                                         // bridgeLongitude16_1

            addSprite(116, AssetPool.getSpritesheet("tiles").getSprite(160));                                           // bridgeLongitude16_2

            addSprite(116, AssetPool.getSpritesheet("tiles").getSprite(161));                                           // bridgeLongitude16_3

            addSprite(116, AssetPool.getSpritesheet("tiles").getSprite(158));                                           // bridgeLongitude16_4

        setup(117, AssetPool.getSpritesheet("tiles").getSprite(162), true);                                             // bridgeLongitude17

        setup(118, AssetPool.getSpritesheet("tiles").getSprite(163), true, 0);                                          // bridgeLongitude18_1

            addSprite(118, AssetPool.getSpritesheet("tiles").getSprite(164));                                           // bridgeLongitude18_2

            addSprite(118, AssetPool.getSpritesheet("tiles").getSprite(165));                                           // bridgeLongitude18_3

            addSprite(118, AssetPool.getSpritesheet("tiles").getSprite(162));                                           // bridgeLongitude18_4

        setup(119, AssetPool.getSpritesheet("tiles").getSprite(166), false);                                            // bridgeLongitudeLeftFloorEdge1

        setup(120, AssetPool.getSpritesheet("tiles").getSprite(167), false);                                            // bridgeLongitudeLeftFloorEdge2

        setup(121, AssetPool.getSpritesheet("tiles").getSprite(168), false);                                            // bridgeLongitudeLeftFloorEdge3

        setup(122, AssetPool.getSpritesheet("tiles").getSprite(169), false);                                            // bridgeLongitudeLeftFloorEdge4

        setup(123, AssetPool.getSpritesheet("tiles").getSprite(170), false);                                            // bridgeLongitudeLeftFloorEdge5

        setup(124, AssetPool.getSpritesheet("tiles").getSprite(171), false);                                            // floorPortalLatitude1

        setup(125, AssetPool.getSpritesheet("tiles").getSprite(172), false);                                            // floorPortalLatitude2

        setup(126, AssetPool.getSpritesheet("tiles").getSprite(173), false);                                            // floorPortalLatitude3

        setup(127, AssetPool.getSpritesheet("tiles").getSprite(174), false);                                            // floorPortalLatitude4

        setup(128, AssetPool.getSpritesheet("tiles").getSprite(175), false);                                            // floorPortalLongitude1

        setup(129, AssetPool.getSpritesheet("tiles").getSprite(176), false);                                            // floorPortalLongitude2

        setup(130, AssetPool.getSpritesheet("tiles").getSprite(177), false);                                            // floorPortalLongitude3

        setup(131, AssetPool.getSpritesheet("tiles").getSprite(178), false);                                            // floorPortalLongitude4

        setup(132, AssetPool.getSpritesheet("tiles").getSprite(179), true);                                             // floorLedge1

        setup(133, AssetPool.getSpritesheet("tiles").getSprite(180), true);                                             // floorLedge2

        setup(134, AssetPool.getSpritesheet("tiles").getSprite(181), true);                                             // floorLedge3

        setup(135, AssetPool.getSpritesheet("tiles").getSprite(182), true);                                             // floorLedge4

        setup(136, AssetPool.getSpritesheet("tiles").getSprite(183), true);                                             // bridgeLatitude19

        setup(137, AssetPool.getSpritesheet("tiles").getSprite(184), true, 0);                                          // bridgeLatitude20_1

            addSprite(137, AssetPool.getSpritesheet("tiles").getSprite(185));                                           // bridgeLatitude20_2

            addSprite(137, AssetPool.getSpritesheet("tiles").getSprite(186));                                           // bridgeLatitude20_3

            addSprite(137, AssetPool.getSpritesheet("tiles").getSprite(183));                                           // bridgeLatitude20_4

        setup(138, AssetPool.getSpritesheet("tiles").getSprite(187), true);                                             // bridgeLongitude19

        setup(139, AssetPool.getSpritesheet("tiles").getSprite(188), true, 0);                                          // bridgeLongitude20_1

            addSprite(139, AssetPool.getSpritesheet("tiles").getSprite(189));                                           // bridgeLongitude20_2

            addSprite(139, AssetPool.getSpritesheet("tiles").getSprite(190));                                           // bridgeLongitude20_3

            addSprite(139, AssetPool.getSpritesheet("tiles").getSprite(187));                                           // bridgeLongitude20_4

        setup(140, AssetPool.getSpritesheet("tiles").getSprite(191), true);                                             // waterLeftStraightWallEdge4

        setup(141, AssetPool.getSpritesheet("tiles").getSprite(192), true);                                             // waterLeftStraightWallEdge5

        setup(142, AssetPool.getSpritesheet("tiles").getSprite(193), true);                                             // waterLeftStraightWallEdge6

        setup(143, AssetPool.getSpritesheet("tiles").getSprite(194), true);                                             // waterRightStraightWallEdge4

        setup(144, AssetPool.getSpritesheet("tiles").getSprite(195), true);                                             // waterRightStraightWallEdge5

        setup(145, AssetPool.getSpritesheet("tiles").getSprite(196), true);                                             // waterRightStraightWallEdge6

        setup(146, AssetPool.getSpritesheet("tiles").getSprite(197), true);                                             // floorBottomLeftConvexWallEdge2

        setup(147, AssetPool.getSpritesheet("tiles").getSprite(198), true);                                             // floorBottomRightConvexWallEdge2

        setup(148, AssetPool.getSpritesheet("tiles").getSprite(199), true);                                             // floorTopLeftConvexWallEdge2

        setup(149, AssetPool.getSpritesheet("tiles").getSprite(200), true);                                             // floorTopRightConvexWallEdge2

        setup(150, AssetPool.getSpritesheet("tiles").getSprite(201), true);                                             // floorBottomStraightWallEdge2

        setup(151, AssetPool.getSpritesheet("tiles").getSprite(202), true);                                             // floorLeftStraightWallEdge6

        setup(152, AssetPool.getSpritesheet("tiles").getSprite(203), true);                                             // floorRightStraightWallEdge6

        setup(153, AssetPool.getSpritesheet("tiles").getSprite(204), false);                                            // floorPortalLatitude5

        setup(154, AssetPool.getSpritesheet("tiles").getSprite(205), false);                                            // floorPortalLongitude5

        setup(155, AssetPool.getSpritesheet("tiles").getSprite(206), true);                                             // water4

        setup(156, AssetPool.getSpritesheet("tiles").getSprite(207), true, 0);                                          // water5_1

            addSprite(156, AssetPool.getSpritesheet("tiles").getSprite(208));                                           // water5_2

            addSprite(156, AssetPool.getSpritesheet("tiles").getSprite(209));                                           // water5_3

            addSprite(156, AssetPool.getSpritesheet("tiles").getSprite(206));                                           // water5_4

        setup(157, AssetPool.getSpritesheet("tiles").getSprite(210), true);                                             // waterBottomStraightFloorEdge4

        setup(158, AssetPool.getSpritesheet("tiles").getSprite(211), true);                                             // waterBottomStraightFloorEdge5

        setup(159, AssetPool.getSpritesheet("tiles").getSprite(212), true);                                             // waterBottomStraightFloorEdge6

        setup(160, AssetPool.getSpritesheet("tiles").getSprite(213), true);                                             // waterTopStraightFloorEdge4

        setup(161, AssetPool.getSpritesheet("tiles").getSprite(214), true);                                             // waterTopStraightFloorEdge5

        setup(162, AssetPool.getSpritesheet("tiles").getSprite(215), true);                                             // waterTopStraightFloorEdge6

        setup(163, AssetPool.getSpritesheet("tiles").getSprite(216), true);                                             // waterLeftStraightFloorEdge4

        setup(164, AssetPool.getSpritesheet("tiles").getSprite(217), true);                                             // waterLeftStraightFloorEdge5

        setup(165, AssetPool.getSpritesheet("tiles").getSprite(218), true);                                             // waterLeftStraightFloorEdge6

        setup(166, AssetPool.getSpritesheet("tiles").getSprite(219), true);                                             // waterRightStraightFloorEdge4

        setup(167, AssetPool.getSpritesheet("tiles").getSprite(220), true);                                             // waterRightStraightFloorEdge5

        setup(168, AssetPool.getSpritesheet("tiles").getSprite(221), true);                                             // waterRightStraightFloorEdge6

        setup(169, AssetPool.getSpritesheet("tiles").getSprite(222), true);                                             // waterBottomLeftConcaveFloorEdge2

        setup(170, AssetPool.getSpritesheet("tiles").getSprite(223), true);                                             // waterBottomRightConcaveFloorEdge2

        setup(171, AssetPool.getSpritesheet("tiles").getSprite(224), true);                                             // waterTopLeftConcaveFloorEdge3

        setup(172, AssetPool.getSpritesheet("tiles").getSprite(225), true);                                             // waterTopRightConcaveFloorEdge2

        setup(173, AssetPool.getSpritesheet("tiles").getSprite(226), true);                                             // waterBottomLeftConvexFloorEdge5

        setup(174, AssetPool.getSpritesheet("tiles").getSprite(227), true);                                             // waterBottomLeftConvexFloorEdge6

        setup(175, AssetPool.getSpritesheet("tiles").getSprite(228), true);                                             // waterBottomLeftConvexFloorEdge7

        setup(176, AssetPool.getSpritesheet("tiles").getSprite(229), true);                                             // waterBottomRightConvexFloorEdge5

        setup(177, AssetPool.getSpritesheet("tiles").getSprite(230), true);                                             // waterBottomRightConvexFloorEdge6

        setup(178, AssetPool.getSpritesheet("tiles").getSprite(231), true);                                             // waterBottomRightConvexFloorEdge7

        setup(179, AssetPool.getSpritesheet("tiles").getSprite(232), true);                                             // waterTopLeftConvexFloorEdge4

        setup(180, AssetPool.getSpritesheet("tiles").getSprite(233), true);                                             // waterTopLeftConvexFloorEdge5

        setup(181, AssetPool.getSpritesheet("tiles").getSprite(234), true);                                             // waterTopRightConvexFloorEdge4

        setup(182, AssetPool.getSpritesheet("tiles").getSprite(235), true);                                             // waterTopRightConvexFloorEdge5

        setup(183, AssetPool.getSpritesheet("tiles").getSprite(236), false);                                            // floorLit1

        setup(184, AssetPool.getSpritesheet("tiles").getSprite(237), false);                                            // floorLit2

        setup(185, AssetPool.getSpritesheet("tiles").getSprite(238), false);                                            // floorLit3

        setup(186, AssetPool.getSpritesheet("tiles").getSprite(239), false);                                            // floorLit4

        setup(187, AssetPool.getSpritesheet("tiles").getSprite(240), false);                                            // floorLit5

        setup(188, AssetPool.getSpritesheet("tiles").getSprite(241), false);                                            // floorLit6

        setup(189, AssetPool.getSpritesheet("tiles").getSprite(242), false);                                            // floorLit7

        setup(190, AssetPool.getSpritesheet("tiles").getSprite(243), false);                                            // floorLit8

        setup(191, AssetPool.getSpritesheet("tiles").getSprite(244), false);                                            // floorLit9
    }


    /**
     * Instantiates a new tile with specified parameters.
     * Note that the index that the tile occupies in the tile array matches the tile number of the tile to be rendered
     * in the map text files.
     * Essentially, the index of the tile in the tile array is its ID.
     *
     * @param index index that the tile will occupy in the array of tiles
     * @param sprite tile sprite
     * @param collision parameter for whether the tile will have collision (true) or not (false)
     */
    private void setup(int index, Sprite sprite, boolean collision) {

        if (index >= tiles.length) {

            expandTileArray((tiles.length - index) + 1);
        }
        tiles[index] = new Tile();
        tiles[index].setCollision(collision);
        addSprite(index, sprite);
    }


    /**
     * Instantiates a new tile with specified parameters.
     * Note that the index that the tile occupies in the tile array matches the tile number of the tile to be rendered
     * in the map text files.
     * Essentially, the index of the tile in the tile array is its ID.
     *
     * @param index index that the tile will occupy in the array of tiles
     * @param sprite tile sprite
     * @param collision parameter for whether the tile will have collision (true) or not (false)
     * @param group parameter for which passive animation group the tile belongs to
     */
    private void setup(int index, Sprite sprite, boolean collision, int group) {

        if (index >= tiles.length) {

            expandTileArray((tiles.length - index) + 1);
        }
        tiles[index] = new Tile();
        tiles[index].setCollision(collision);
        tiles[index].setPassiveAnimationGroup(group);
        addSprite(index, sprite);
    }


    /**
     * Adds a sprite to a tile that's already been instantiated.
     *
     * @param index index that the tile occupies in the array of tiles
     * @param sprite tile sprite
     */
    private void addSprite(int index, Sprite sprite) {

        tiles[index].addSprite(sprite);
    }


    /**
     * Expands the array of loaded tiles while retaining all existing tiles.
     *
     * @param numExpand number of indices to add
     */
    private void expandTileArray(int numExpand) {

        Tile[] temp = tiles;                                                                                            // Create a temporary array to store existing tiles in.
        int originalLength = tiles.length;                                                                              // Store the length of the original tile array.
        tiles = new Tile[originalLength + numExpand];                                                                   // Create a new tile array that has extra slots compared to the original.

        for (int i = 0; i < originalLength; i++) {                                                                      // Add original tiles back to the new tile array.

            tiles[i] = temp[i];
        }
    }


    /**
     * Initializes the array of drawables.
     */
    private void initializeDrawablesArray() {

        int col = 0;
        int row = 0;

        while ((col < GamePanel.MAX_WORLD_COL) && (row < GamePanel.MAX_WORLD_ROW)) {

            float worldX = col * GamePanel.NATIVE_TILE_SIZE;
            float worldY = row * GamePanel.NATIVE_TILE_SIZE;
            Sprite sprite = tiles[defaultTile].getSprites().get(0);                                                     // Initialize with default sprite.
            Drawable drawable = new Drawable(
                    new Transform(
                            new Vector2f(worldX, worldY),
                            new Vector2f(GamePanel.NATIVE_TILE_SIZE, GamePanel.NATIVE_TILE_SIZE)),                      // Sprite size must match native tile size.
                    sprite);
            drawables[col][row] = drawable;

            col++;

            if (col == GamePanel.MAX_WORLD_COL) {

                col = 0;
                row++;
            }
        }
    }


    /**
     * Resets the array of drawables to all contain the default sprite.
     */
    private void resetDrawablesArray() {

        int col = 0;
        int row = 0;

        while ((col < GamePanel.MAX_WORLD_COL) && (row < GamePanel.MAX_WORLD_ROW)) {

            drawables[col][row].setSprite(tiles[defaultTile].getSprites().get(0));
            col++;

            if (col == GamePanel.MAX_WORLD_COL) {

                col = 0;
                row++;
            }
        }
    }


    // GETTER
    public Tile[] getTiles() {
        return tiles;
    }
}
