package ai;

/**
 * This class defines nodes to be used by the A* pathfinding algorithm.
 */
public class Node {

    // FIELDS
    /**
     * Parent node of the current node (used to link path).
     */
    private Node Parent;

    /**
     * Node column.
     */
    private final int col;

    /**
     * Node row.
     */
    private final int row;

    /**
     * Distance between the current node and the start node.
     */
    private int gCost;

    /**
     * Distance from the current node to the goal node.
     */
    private int hCost;

    /**
     * Sum of `gCost` and `hCost`.
     */
    private int fCost;

    /**
     * Boolean tracking whether the current node is open (i.e., on a tile that does not have collision).
     */
    private boolean open;

    /**
     * Boolean tracking whether the current node has been checked or not by the algorithm when finding a path.
     */
    private boolean checked;


    // CONSTRUCTOR
    /**
     * Constructs a node instance at the specified column and row intersection.
     *
     * @param col node column
     * @param row node row
     */
    public Node(int col, int row) {
        this.col = col;
        this.row = row;
    }


    // GETTERS
    public Node getParent() {
        return Parent;
    }

    public int getCol() {
        return col;
    }

    public int getRow() {
        return row;
    }

    public int getgCost() {
        return gCost;
    }

    public int gethCost() {
        return hCost;
    }

    public int getfCost() {
        return fCost;
    }

    public boolean isOpen() {
        return open;
    }

    public boolean isChecked() {
        return checked;
    }


    // SETTERS
    public void setParent(Node parent) {
        Parent = parent;
    }

    public void setgCost(int gCost) {
        this.gCost = gCost;
    }

    public void sethCost(int hCost) {
        this.hCost = hCost;
    }

    public void setfCost(int fCost) {
        this.fCost = fCost;
    }

    public void setOpen(boolean open) {
        this.open = open;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }
}
