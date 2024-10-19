package tile;

import core.GamePanel;
import org.joml.Vector2f;
import render.Renderer;
import asset.Sprite;
import render.enumeration.ZIndex;
import render.drawable.Drawable;
import render.drawable.Transform;
import asset.AssetPool;
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
    private Tile[] tiles = new Tile[0];

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
        getTileImage();                                                                                                 // Load tile sprites upon instantiation.
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
                int spriteNum = gp.getAnimationM().getSprite(tiles[tileNum].getAnimationGroup(), worldCol, worldRow);   // Render appropriate tile in animation cycle, if applicable.

                if ((tiles[tileNum].getSprites().get(spriteNum) != null)
                        && (spriteNum < tiles[tileNum].getSprites().size())) {

                    Sprite sprite = tiles[tileNum].getSprites().get(spriteNum);
                    drawables[worldCol][worldRow].setSprite(sprite);
                    renderer.addDrawable(drawables[worldRow][worldCol], ZIndex.THIRD_LAYER);

                } else if (!renderErrors.contains(tileNum)) {
                    UtilityTool.logError("Failed to add tile at index '"
                            + tileNum
                            + "' to the render pipeline: the map may contain a tile that does not exist or a tile may"
                            + " have been assigned to the incorrect animation group.");
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
     * Loads map tile data from a text file.
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
    private void getTileImage() {

        setup(0, AssetPool.getSpritesheet("tiles").getSprite(0), true);                                                 // Default.

        setup(1, AssetPool.getSpritesheet("tiles").getSprite(1), false);                                                // Grass.

        setup(2, AssetPool.getSpritesheet("tiles").getSprite(2), true);                                                 // Non-animated water.

        setup(3, AssetPool.getSpritesheet("tiles").getSprite(3), true, 0);                                              // Animated water (1).

            addSprite(3, AssetPool.getSpritesheet("tiles").getSprite(4));                                               // Animated water (2).

            addSprite(3, AssetPool.getSpritesheet("tiles").getSprite(5));                                               // Animated water (3).

            addSprite(3, AssetPool.getSpritesheet("tiles").getSprite(6));                                               // Animated water (4).

        setup(4, AssetPool.getSpritesheet("tiles").getSprite(7), true);                                                 // Rock.

        setup(5, AssetPool.getSpritesheet("tiles").getSprite(8), false);                                                // Portal.

        setup(6, AssetPool.getSpritesheet("tiles").getSprite(9), false);                                                // Dirt.

        setup(7, AssetPool.getSpritesheet("tiles").getSprite(10), false);                                               // Non-animated cave.

        setup(8, AssetPool.getSpritesheet("tiles").getSprite(10), false, 1);                                            // Animated cave (1).

            addSprite(8, AssetPool.getSpritesheet("tiles").getSprite(11));                                              // Animated cave (2).

            addSprite(8, AssetPool.getSpritesheet("tiles").getSprite(12));                                              // Animated cave (3).

            addSprite(8, AssetPool.getSpritesheet("tiles").getSprite(13));                                              // Animated cave (4).
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

        expandTileArray();
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
     * @param index index that the tile will occupy in the array of tiles (`tiles` array).
     * @param sprite tile sprite
     * @param collision parameter for whether the tile will have collision (true) or not (false)
     * @param group parameter for which animation group the tile belongs to
     */
    private void setup(int index, Sprite sprite, boolean collision, int group) {

        expandTileArray();
        tiles[index] = new Tile();
        tiles[index].setCollision(collision);
        tiles[index].setAnimationGroup(group);
        addSprite(index, sprite);
    }


    /**
     * Adds a sprite to a tile that's already been instantiated.
     *
     * @param index index that the tile occupies in the array of tiles (`tiles` array).
     * @param sprite tile sprite
     */
    private void addSprite(int index, Sprite sprite) {

        tiles[index].addSprite(sprite);
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
