package ai;

import entity.EntityBase;
import core.GamePanel;

import java.util.ArrayList;

/**
 * This class handles operations for the A* pathfinding algorithm.
 */
public class PathFinder {

    // FIELDS
    private final GamePanel gp;

    /**
     * Array of all nodes being checked (equal to world tile size).
     * Data is stored in this array as [col][row].
     */
    private Node[][] nodes;

    /**
     * List of open nodes.
     */
    private final ArrayList<Node> openList = new ArrayList<>();

    /**
     * List of nodes on the generated path in order from start (index 0) to end.
     */
    private final  ArrayList<Node> pathList = new ArrayList<>();

    /**
     * Start node of the pathfinding operation.
     */
    private Node startNode;

    /**
     * Goal node of the pathfinding operation.
     */
    private Node goalNode;

    /**
     * Current node being analyzed by the pathfinding operation.
     */
    private Node currentNode;

    /**
     * Boolean tracking whether the goal node has been reached or not.
     */
    private boolean goalReached = false;

    /**
     * Variable provided to track how many steps have been taken from the starting node when performing a pathfinding
     * operation.
     */
    private int step;

    /**
     * Entity for whom the pathfinding operation is being run for.
     */
    private EntityBase entity;


    // CONSTRUCTOR
    /**
     * Constructs a PathFinder instance.
     *
     * @param gp GamePanel instance
     */
    public PathFinder(GamePanel gp) {
        this.gp = gp;
        instantiateNodes();
    }


    // METHODS
    /**
     * Sets node configurations for a new pathfinding operation.
     *
     * @param startCol starting column of a pathfinding operation
     * @param startRow starting row of a pathfinding operation
     * @param goalCol goal column of a pathfinding operation
     * @param goalRow goal row of a pathfinding operation
     */
    public void setNodes(int startCol, int startRow, int goalCol, int goalRow) {

        resetNodes();

        startNode = nodes[startCol][startRow];
        currentNode = startNode;
        goalNode = nodes[goalCol][goalRow];
        openList.add(currentNode);

        int col = 0;
        int row = 0;
        int tileNum;

        while ((col < GamePanel.MAX_WORLD_COL) && (row < GamePanel.MAX_WORLD_ROW)) {

            tileNum = gp.getMapM().getLoadedMap().getMapTileNum()[col][row];

            if (gp.getTileM().getTiles()[tileNum].hasCollision()) {                                                     // Check whether the tile a node occupies is solid or not; set the node to solid if true.

                nodes[col][row].setSolid(true);
            }

            getCost(nodes[col][row]);                                                                                   // Set cost on this node.
            col++;

            if (col == GamePanel.MAX_WORLD_COL) {

                col = 0;
                row++;
            }
        }
    }


    /**
     * Searches for a path for an entity to follow using the set node configurations.
     * If the entity is already at the goal, no path will be found.
     * Note that the `setNodes()` method in the PathFinder class should be called first.
     *
     * @param entity entity that a path is being found for
     * @return whether a path was found (true) or not (false)
     */
    public boolean search(EntityBase entity) {

        this.entity = entity;

        if (entity.getCol() == goalNode.getCol() && entity.getRow() == goalNode.getRow()) {
            return false;                                                                                               // Entity is already at goal, so no need to calculate path.
        }

        int col;
        int row;
        int bestNodeIndex;
        int bestNodefCost;

        while ((!goalReached) && (step < 500)) {

            col = currentNode.getCol();
            row = currentNode.getRow();

            // Check the current node.
            currentNode.setChecked(true);
            openList.remove(currentNode);

            // Open the Up node.
            if ((row - 1) >= 0) {
                openNode(nodes[col][row - 1]);
            }

            // Open the Left node.
            if ((col - 1) >= 0) {
                openNode(nodes[col - 1][row]);
            }

            // Open the Down node.
            if ((row + 1) < GamePanel.MAX_WORLD_ROW) {
                openNode(nodes[col][row + 1]);
            }

            // Open the Right node.
            if ((col + 1) < GamePanel.MAX_WORLD_COL) {
                openNode(nodes[col + 1][row]);
            }

            // Find the best node by scanning the list of open nodes.
            bestNodeIndex = 0;                                                                                          // Initialize the index of the best node.
            bestNodefCost = 999;                                                                                        // Initialize to a high value since we'll be checking for lower values.

            for (int i = 0; i < openList.size(); i++) {

                // Check if this node's F cost is better.
                if (openList.get(i).getfCost() < bestNodefCost) {
                    bestNodeIndex = i;
                    bestNodefCost = openList.get(i).getfCost();
                }

                // If F cost is equal, check the G cost as a secondary criterion.
                else if (openList.get(i).getfCost() == bestNodefCost) {
                    if ((openList.get(i).getgCost()) < (openList.get(bestNodeIndex).getgCost())) {
                        bestNodeIndex = i;
                    }
                }
            }

            // If there is no node in the list of open nodes, end this loop.
            if (openList.size() == 0) {
                break;
            }

            // After the loop, `openList[bestNodeIndex]` is the next step (equals `currentNode`).
            currentNode = openList.get(bestNodeIndex);
            if (currentNode == goalNode) {
                goalReached = true;
                trackThePath();
            }
            step++;
        }
        return goalReached;
    }


    /**
     * Instantiates an array of nodes; essentially, it creates a node for every tile on the map.
     */
    private void instantiateNodes() {

        nodes = new Node[GamePanel.MAX_WORLD_COL][GamePanel.MAX_WORLD_ROW];

        int col = 0;
        int row = 0;

        while ((col < GamePanel.MAX_WORLD_COL) && (row < GamePanel.MAX_WORLD_ROW)) {

            nodes[col][row] = new Node(col, row);

            col++;

            if (col == GamePanel.MAX_WORLD_COL) {

                col = 0;
                row++;
            }
        }
    }


    /**
     * Resets all nodes to their default states.
     */
    private void resetNodes() {

        int col = 0;
        int row = 0;

        while ((col < GamePanel.MAX_WORLD_COL) && (row < GamePanel.MAX_WORLD_ROW)) {                                    // Reset each node.

            nodes[col][row].setOpen(false);                                                                             // Reset `open` for this node.
            nodes[col][row].setChecked(false);                                                                          // Reset `checked` for this node.
            nodes[col][row].setSolid(false);                                                                            // Reset `solid` for this node.

            col++;

            if (col == GamePanel.MAX_WORLD_COL) {

                col = 0;
                row++;
            }
        }

        openList.clear();                                                                                               // Reset list of open nodes.
        pathList.clear();                                                                                               // Reset list of path nodes.
        goalReached = false;                                                                                            // Reset goal boolean.
        step = 0;                                                                                                       // Reset steps taken during search.
        entity = null;                                                                                                  // Reset entity that path is being searched for.
    }


    /**
     * Opens a node if it is able to be opened.
     *
     * @param node node to be opened
     */
    private void openNode(Node node) {

        if ((!node.isOpen())
                && (!node.isChecked())
                && (!node.isSolid())
                && (!gp.getCollisionI().checkNode(node.getCol(), node.getRow(),
                goalNode.getCol(), goalNode.getRow(), entity, true))) {

            node.setOpen(true);
            node.setParent(currentNode);
            openList.add(node);                                                                                         // Add this opened node to the list of open nodes so that we can compare; we'll scan the nodes in this list to find the best one.
        }
    }


    /**
     * Calculates the cost (G, H, F) of a node.
     *
     * @param node node to calculate cost for
     */
    private void getCost(Node node) {

        // G cost.
        int xDistance = Math.abs(node.getCol() - startNode.getCol());
        int yDistance = Math.abs(node.getRow() - startNode.getRow());
        node.setgCost(xDistance + yDistance);

        // H cost.
        xDistance = Math.abs(node.getCol() - goalNode.getCol());
        yDistance = Math.abs(node.getRow() - goalNode.getRow());
        node.sethCost(xDistance + yDistance);

        // F cost.
        node.setfCost(node.getgCost() + node.gethCost());
    }


    /**
     * Tracks the path that is currently being followed.
     */
    private void trackThePath() {

        Node current = goalNode;

        while (current != startNode) {                                                                                  // Backtrack from goal node to start node.

            pathList.add(0, current);                                                                                   // With this list, entities can track the path they're moving on.                                                                       // Always adding to the first slot so that the last added node is in [0].
            current = current.getParent();
        }
    }


    // GETTER
    public ArrayList<Node> getPathList() {
        return pathList;
    }
}
