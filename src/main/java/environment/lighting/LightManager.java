package environment.lighting;

import core.GamePanel;
import org.joml.Vector2f;
import org.joml.Vector4f;
import render.Renderer;
import render.drawable.Transform;

import java.awt.*;
import java.util.ArrayList;

import static java.lang.Float.compare;

/**
 * This class handles operations for the lighting system.
 * Rays of light are generated using Bresenham's line generation algorithm.
 */
public class LightManager {

    // FIELDS
    private final GamePanel gp;

    /**
     * Array of all nodes being checked [subCol][subRow].
     */
    private Node[][] nodes;

    /**
     * List to store all calculated rays of light.
     */
    private final ArrayList<Ray> rays = new ArrayList<>();

    /**
     * Node size in pixels.
     * A node is taken to be a square (equal width and height).
     * Note that this value should be a factor of the native tile size.
     */
    private final int nodeSize = 8; // 4

    private final int nodesPerTile;


    // CONSTRUCTOR
    /**
     * Constructs a LightManager instance.
     *
     * @param gp GamePanel instance
     */
    public LightManager(GamePanel gp) {
        this.gp = gp;
        nodesPerTile = gp.getNativeTileSize() / nodeSize;
        instantiateNodes();
    }


    // METHODS
    public void update() {

        resetNodes();
        // TODO : Perhaps an update only needs to occur if a light source changes location.
        //  This would save on computing resources.
        calculate(92, 100); // 184, 200
    }


    public void calculate(int sourceSubCol, int sourceSubRow) {

        // TEST POINTS : sourceSubCol = 368
        //               sourceSubRow = 400
        //               This should be the top-right of the tiles that the player entity spawns on.

        // TODO : These boundary will be calculated dynamically, relative to the source.
//        ArrayList<Integer> boundaryNodesSubCol = new ArrayList<>();
//        ArrayList<Integer> boundaryNodesSubRow = new ArrayList<>();
//
//        // Right-bottom.
//        boundaryNodesSubCol.add(sourceSubCol + 6);
//        boundaryNodesSubRow.add(sourceSubRow + 6);
//
//        // Right-top.
//        boundaryNodesSubCol.add(sourceSubCol + 6);
//        boundaryNodesSubRow.add(sourceSubRow - 6);
//
//        // Left-bottom.
//        boundaryNodesSubCol.add(sourceSubCol - 6);
//        boundaryNodesSubRow.add(sourceSubRow + 6);
//
//        // Left-top.
//        boundaryNodesSubCol.add(sourceSubCol - 6);
//        boundaryNodesSubRow.add(sourceSubRow - 6);

        ArrayList<Node> boundaryNodes = new ArrayList<>();
        boundaryNodes.add(nodes[sourceSubCol + 96][sourceSubRow + 96]);         // Right-bottom.
//        boundaryNodes.add(nodes[sourceSubCol + 96][sourceSubRow - 96]);         // Right-top.
//        boundaryNodes.add(nodes[sourceSubCol - 96][sourceSubRow + 96]);         // Left-bottom.
//        boundaryNodes.add(nodes[sourceSubCol - 96][sourceSubRow - 96]);         // Left-top.

        // Loop through each boundary node and calculate a ray of light from the source to it.
        // TODO : Remove code repetition between (delX > delY) and (delY > delX) branches.
        for (Node boundaryNode : boundaryNodes) {

            Ray ray = new Ray();

            int curX = sourceSubCol;  // Current X being calculated.
            int curY = sourceSubRow;  // Current Y being calculated.

            int tarX = boundaryNode.getSubCol();  // Target X.
            int tarY = boundaryNode.getSubRow();  // Target Y.

            int delX = Math.abs(boundaryNode.getSubCol() - sourceSubCol);  // Difference in nodes between source and target; only care about absolute values.
            int delY = Math.abs(boundaryNode.getSubRow() - sourceSubRow);  // ^^^

            if (delX > delY) {

                int p = (2 * delY) - delX;  // Initial decision variable.

                while (curX < tarX) {

                    nodes[curX][curY].setAlpha(0.0f);  // Make the node transparent; it is part of a ray of light.
                    ray.getNodes().add(nodes[curX][curY]);
                    curX++;

                    if (p < 0) {

                        p = p + (2 * delY);  // Calculate next decision variable.
                    } else {

                        p = p + (2 *  delY) - (2 * delX);  // Calculate next decision variable.
                        curY++;
                    }
                }
            } else {

                int p = (2 * delX) - delY;  // Initial decision variable.

                while (curY < tarY) {

                    nodes[curX][curY].setAlpha(0.0f);  // Make the node transparent; it is part of a ray of light.
//                    System.out.println("subCol: " + curX + "  subRow: " + curY);
                    ray.getNodes().add(nodes[curX][curY]);
                    curY++;

                    if (p < 0) {

                        p = p + (2 * delX);  // Calculate next decision variable.
                    } else {

                        p = p + (2 * delX) - (2 * delY);  // Calculate next decision variable.
                        curX++;
                    }
                }
            }
//            } else {
//
//                while (curX < tarX) {
//
//                    nodes[curX][curY].setAlpha(0.0f);  // Make the node transparent; it is part of a ray of light.
////                    System.out.println("subCol: " + curX + "  subRow: " + curY);
//                    curX++;
//                    curY++;
//                }
//            }
//            System.out.println("----------------------------------------------------------------");
        }
    }


    public void addToRenderPipeline(Renderer renderer) {

        int worldSubCol = 0;
        int worldSubRow = 0;
        Vector2f size = new Vector2f(nodeSize, nodeSize);

        while ((worldSubCol < (gp.getMaxWorldCol() * nodesPerTile))
                && (worldSubRow < (gp.getMaxWorldRow() * nodesPerTile))) {                                              // Draw each node from left to right for each sub-row, starting with the top row and working downwards.

            Vector2f worldCoords = new Vector2f(worldSubCol * nodeSize, worldSubRow * nodeSize);
            Transform transform = new Transform(worldCoords, size);

            // Only render light nodes in visible screen to greatly improve performance.
            if ((worldCoords.x >= gp.getCamera().getPositionMatrix().x - nodeSize)
                    && (worldCoords.x <= gp.getCamera().getPositionMatrix().x + gp.getCamera().getScreenWidth())
                    && (worldCoords.y >= gp.getCamera().getPositionMatrix().y - nodeSize)
                    && (worldCoords.y <= gp.getCamera().getPositionMatrix().y + gp.getCamera().getScreenHeight())) {

                if (compare(nodes[worldSubCol][worldSubRow].getAlpha(), 0.0f) == 0) {
                    renderer.addRectangle(new Vector4f(0, 0, 0, 0), transform);
                } else {
                    renderer.addRectangle(new Vector4f(0, 0, 0, 255), transform);
                }
            }
            worldSubCol++;                                                                                              // Iterate so that we can draw the next tile.

            if (worldSubCol == (gp.getMaxWorldCol() * nodesPerTile)) {
                worldSubCol = 0;
                worldSubRow++;
            }
        }
    }


    /**
     * Instantiates an array of nodes.
     */
    private void instantiateNodes() {

        nodes = new Node[gp.getMaxWorldCol() * nodesPerTile][gp.getMaxWorldRow() * nodesPerTile];                       // Each overworld tile is broken into a 16x16 grid of sub-tiles.

        int subCol = 0;
        int subRow = 0;

        while ((subCol < (gp.getMaxWorldCol() * nodesPerTile)) && (subRow < (gp.getMaxWorldRow() * nodesPerTile))) {

            nodes[subCol][subRow] = new Node(subCol, subRow);

            subCol++;

            if (subCol == (gp.getMaxWorldCol() * nodesPerTile)) {

                subCol = 0;
                subRow++;
            }
        }
        // TODO : Is this next line needed to reset?
        resetNodes();
    }


    /**
     * Resets all nodes to their default states.
     */
    public void resetNodes() {

        int subCol = 0;
        int subRow = 0;

        while ((subCol < (gp.getMaxWorldCol() * nodesPerTile)) && (subRow < (gp.getMaxWorldRow() * nodesPerTile))) {

            nodes[subCol][subRow].setAlpha(0.98f);                                                                      // Reset `alpha` to the base default value for this node.
            nodes[subCol][subRow].setEndOfRay(false);                                                                   // Reset `endOfRay` for this node.
            nodes[subCol][subRow].setChecked(false);                                                                    // Reset `checked` for this node.

            subCol++;

            if (subCol == (gp.getMaxWorldCol() * nodesPerTile)) {

                subCol = 0;
                subRow++;
            }
        }

        rays.clear();                                                                                                   // Purge all previously calculated rays of light.
    }
}
