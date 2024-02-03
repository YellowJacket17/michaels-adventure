package environment.lighting;

import core.GamePanel;
import org.joml.Vector2f;
import org.joml.Vector4f;
import render.Renderer;
import render.Sprite;
import render.ZIndex;
import render.drawable.Drawable;
import render.drawable.Transform;
import utility.AssetPool;

import java.util.ArrayList;

import static java.lang.Float.compare;

/**
 * This class handles operations for the lighting system.
 * Rays of light are generated using Bresenham's line generation algorithm.
 * The radius of a light source is generated using Bresenham's circle generation algorithm.
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
     * Native size of rendered nodes.
     * Nodes are the same width and height.
     * Note that this value must be a factor of the native tile size in GamePanel.
     */
    private final int nodeSize = 8;

    /**
     * Maximum number of sub-tiles allowed in a map column.
     */
    private final int maxWorldSubCol;

    /**
     * Maximum number of sub-tiles allowed in a map row.
     */
    private final int maxWorldSubRow;

    /**
     * Array to store the drawables associated with each possible node world position.
     */
    private final Drawable[][] drawables;


    // CONSTRUCTOR
    /**
     * Constructs a LightManager instance.
     *
     * @param gp GamePanel instance
     */
    public LightManager(GamePanel gp) {
        this.gp = gp;
        if ((GamePanel.NATIVE_TILE_SIZE % nodeSize) != 0) {
            throw new IllegalStateException("Tile size must be evenly divisible by light node size.");
        }
        maxWorldSubCol = GamePanel.MAX_WORLD_COL * (GamePanel.NATIVE_TILE_SIZE / nodeSize);
        maxWorldSubRow = GamePanel.MAX_WORLD_ROW * (GamePanel.NATIVE_TILE_SIZE / nodeSize);
        drawables = new Drawable[maxWorldSubCol][maxWorldSubRow];
        instantiateNodes();
        initializeDrawablesArray();
    }


    // METHODS
    /**
     * Updates the state of all light nodes by one frame.
     */
    public void update() {

        resetNodes();
        // TODO : Perhaps an update only needs to occur if a light source changes location.
        //  This would save on computing resources.
        calculateRays(92, 100); // 184, 200 for node size of 4
    }


    /**
     * Calculates all rays of light emanating from a light source.
     *
     * @param sourceSubCol sub-column of light source
     * @param sourceSubRow sub-rol of light source
     */
    public void calculateRays(int sourceSubCol, int sourceSubRow) {

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

        // TODO : If target sub-col/sub-row is out of bounds of boundaryNodes array, account for that.
//        ArrayList<Node> boundaryNodes = new ArrayList<>();                                                              // List of all boundary nodes for rays of light emanating from source.
//        boundaryNodes.add(nodes[sourceSubCol + 96][sourceSubRow + 96]);         // Right-bottom.
//        boundaryNodes.add(nodes[sourceSubCol + 96][sourceSubRow - 96]);         // Right-top.
//        boundaryNodes.add(nodes[sourceSubCol - 96][sourceSubRow + 96]);         // Left-bottom.
//        boundaryNodes.add(nodes[sourceSubCol - 96][sourceSubRow - 96]);         // Left-top.

        ArrayList<Node> boundaryNodes = calculateBoundaries(sourceSubCol, sourceSubRow, 50);

        // Loop through each boundary node and calculate a new ray of light from the source to it.
        for (Node boundaryNode : boundaryNodes) {

            Ray ray = new Ray();

            int curX = sourceSubCol;  // Current X (sub-column) being calculated.
            int curY = sourceSubRow;  // Current Y (sub-row) being calculated.

            int tarX = boundaryNode.getSubCol();  // Target X (sub-column).
            int tarY = boundaryNode.getSubRow();  // Target Y (sub-column).

            int delX = Math.abs(boundaryNode.getSubCol() - sourceSubCol);  // Difference in nodes between source and target; only care about absolute values.
            int delY = Math.abs(boundaryNode.getSubRow() - sourceSubRow);  // ^^^

            // TODO : Alleviate code repetition in all following logic.
            if (delX > delY) {

                int p = (2 * delY) - delX;  // Initial decision variable.

                if (curX < tarX) {

                    while (curX < tarX) {

                        nodes[curX][curY].setAlpha(0);  // Make the node transparent; it is part of a ray of light.
                        ray.getNodes().add(nodes[curX][curY]);
                        curX++;

                        if (p < 0) {

                            p = p + (2 * delY);  // Calculate next decision variable.
                        } else {

                            p = p + (2 *  delY) - (2 * delX);  // Calculate next decision variable.

                            if (curY < tarY) {

                                curY++;
                            } else {

                                curY--;
                            }
                        }
                    }
                } else {

                    // TODO : Fix ArrayOutOfBoundsException.
//                    while (curX > tarX) {
//
//                        nodes[curX][curY].setAlpha(0);  // Make the node transparent; it is part of a ray of light.
//                        ray.getNodes().add(nodes[curX][curY]);
//                        curX++;
//
//                        if (p < 0) {
//
//                            p = p + (2 * delY);  // Calculate next decision variable.
//                        } else {
//
//                            p = p + (2 *  delY) - (2 * delX);  // Calculate next decision variable.
//
//                            if (curY < tarY) {
//
//                                curY++;
//                            } else {
//
//                                curY--;
//                            }
//                        }
//                    }
                }
            } else {

                int p = (2 * delX) - delY;  // Initial decision variable.

                if (curY < tarY) {

                    while (curY < tarY) {

                        nodes[curX][curY].setAlpha(0);  // Make the node transparent; it is part of a ray of light.
                        ray.getNodes().add(nodes[curX][curY]);
                        curY++;

                        if (p < 0) {

                            p = p + (2 * delX);  // Calculate next decision variable.
                        } else {

                            p = p + (2 * delX) - (2 * delY);  // Calculate next decision variable.

                            if (curX < tarX) {

                                curX++;
                            } else {

                                curX--;
                            }
                        }
                    }
                } else {

                    while (curY > tarY) {

                        nodes[curX][curY].setAlpha(0);  // Make the node transparent; it is part of a ray of light.
                        ray.getNodes().add(nodes[curX][curY]);
                        curY--;

                        if (p < 0) {

                            p = p + (2 * delX);  // Calculate next decision variable.
                        } else {

                            p = p + (2 * delX) - (2 * delY);  // Calculate next decision variable.

                            if (curX < tarX) {

                                curX++;
                            } else {

                                curX--;
                            }
                        }
                    }
                }
            }
        }
    }


    /**
     * Calculates circular boundary (i.e., maximum reach/intensity) of rays of light emanating from a source light.
     *
     * @param sourceSubCol sub-column of light source
     * @param sourceSubRow sub-rol of light source
     * @param radius radius (i.e., maximum reach/intensity) of light source
     * @return list of boundary nodes
     */
    private ArrayList<Node> calculateBoundaries(int sourceSubCol, int sourceSubRow, int radius) {

        ArrayList<Node> boundaryNodes = new ArrayList<>();

        int curX = 0;  // Current X (sub-column) being calculated; circle taken to be centered at origin.
        int curY = radius;  // Current Y (sub-row) being calculated; circle taken to be centered at origin.
        int p = 3 - (2 * radius); // Initial decision variable.
        addBoundaryNode(sourceSubCol, sourceSubRow, curX, curY, boundaryNodes);

        while (curY >= curX) {

            curX++;

            if (p < 0) {

                p = p + 4 * curX + 6;
            } else {

                curY--;
                p = p + 4 * (curX - curY) + 10;
            }
            addBoundaryNode(sourceSubCol, sourceSubRow, curX, curY, boundaryNodes);
        }
        return boundaryNodes;
    }


    /**
     * Adds all relevant boundary nodes for a given iteration Bresenham's circle generation algorithm.
     * Note that the actual algorithm only calculates a single octant of a circle, assumed to be centered at the origin.
     * This is why each calculated node is mirrored eight times, to complete the circle since it's symmetrical.
     *
     * @param sourceSubCol
     * @param sourceSubRow
     * @param curX
     * @param curY
     * @param boundaryNodes
     */
    private void addBoundaryNode(int sourceSubCol, int sourceSubRow, int curX, int curY, ArrayList<Node> boundaryNodes) {

        boundaryNodes.add(nodes[sourceSubCol + curX][sourceSubRow + curY]);
        boundaryNodes.add(nodes[sourceSubCol - curX][sourceSubRow + curY]);
        boundaryNodes.add(nodes[sourceSubCol + curX][sourceSubRow - curY]);
        boundaryNodes.add(nodes[sourceSubCol - curX][sourceSubRow - curY]);
        boundaryNodes.add(nodes[sourceSubCol + curY][sourceSubRow + curX]);
        boundaryNodes.add(nodes[sourceSubCol - curY][sourceSubRow + curX]);
        boundaryNodes.add(nodes[sourceSubCol + curY][sourceSubRow - curX]);
        boundaryNodes.add(nodes[sourceSubCol - curY][sourceSubRow - curX]);

//        nodes[sourceSubCol + curX][sourceSubRow + curY].setAlpha(0.0f);
//        nodes[sourceSubCol - curX][sourceSubRow + curY].setAlpha(0.0f);
//        nodes[sourceSubCol + curX][sourceSubRow - curY].setAlpha(0.0f);
//        nodes[sourceSubCol - curX][sourceSubRow - curY].setAlpha(0.0f);
//        nodes[sourceSubCol + curY][sourceSubRow + curX].setAlpha(0.0f);
//        nodes[sourceSubCol - curY][sourceSubRow + curX].setAlpha(0.0f);
//        nodes[sourceSubCol + curY][sourceSubRow - curX].setAlpha(0.0f);
//        nodes[sourceSubCol - curY][sourceSubRow - curX].setAlpha(0.0f);
    }


    /**
     * Adds lighting effects to the render pipeline.
     *
     * @param renderer Renderer instance
     */
    public void addToRenderPipeline(Renderer renderer) {

        int subCol = 0;
        int subRow = 0;

        while ((subCol < maxWorldSubCol)
                && (subRow < maxWorldSubRow)) {                                                                         // Render each node from left to right for each sub-row, starting with the top row and working downwards.

            // Only render light nodes in visible screen to greatly improve performance.
            if ((drawables[subCol][subRow].transform.position.x >= gp.getCamera().getPositionMatrix().x - nodeSize)                                     // Left side of screen.
                    && (drawables[subCol][subRow].transform.position.x <= gp.getCamera().getPositionMatrix().x + gp.getCamera().getScreenWidth())       // Right side of screen.
                    && (drawables[subCol][subRow].transform.position.y >= gp.getCamera().getPositionMatrix().y - nodeSize)                              // Top side of screen.
                    && (drawables[subCol][subRow].transform.position.y <= gp.getCamera().getPositionMatrix().y + gp.getCamera().getScreenHeight())) {   // Bottom side of screen.

                drawables[subCol][subRow].setColor(new Vector4f(0, 0, 0, nodes[subCol][subRow].getAlpha()));
                renderer.addDrawable(drawables[subCol][subRow], ZIndex.THIRD_LAYER);
            }
            subCol++;                                                                                                   // Iterate so that we can draw the next node.

            if (subCol == maxWorldSubCol) {
                subCol = 0;
                subRow++;
            }
        }
    }


    /**
     * Instantiates an array of nodes.
     */
    private void instantiateNodes() {

        nodes = new Node[maxWorldSubCol][maxWorldSubRow];

        int subCol = 0;
        int subRow = 0;

        while ((subCol < maxWorldSubCol) && (subRow < maxWorldSubRow)) {

            nodes[subCol][subRow] = new Node(subCol, subRow);

            subCol++;

            if (subCol == maxWorldSubCol) {

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

        while ((subCol < maxWorldSubCol) && (subRow < maxWorldSubRow)) {

            nodes[subCol][subRow].setAlpha(255);                                                                        // Reset `alpha` to the base default value for this node.
            nodes[subCol][subRow].setEndOfRay(false);                                                                   // Reset `endOfRay` for this node.
            nodes[subCol][subRow].setChecked(false);                                                                    // Reset `checked` for this node.
            subCol++;

            if (subCol == maxWorldSubCol) {

                subCol = 0;
                subRow++;
            }
        }
        rays.clear();                                                                                                   // Purge all previously calculated rays of light.
    }


    /**
     * Initializes the array of drawables.
     */
    private void initializeDrawablesArray() {

        int subCol = 0;
        int subRow = 0;

        while ((subCol < maxWorldSubCol) && (subRow < maxWorldSubRow)) {

            float worldX = subCol * nodeSize;
            float worldY = subRow * nodeSize;
            Drawable drawable = new Drawable(
                    new Transform(
                            new Vector2f(worldX, worldY),
                            new Vector2f(nodeSize, nodeSize)),
                    new Vector4f(0, 0, 0, 255));
            drawables[subCol][subRow] = drawable;

            subCol++;

            if (subCol == maxWorldSubCol) {

                subCol = 0;
                subRow++;
            }
        }
    }
}
