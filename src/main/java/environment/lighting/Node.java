package environment.lighting;

/**
 * This class defines nodes to be used by the lighting system.
 */
public class Node {

    // FIELDS
    /**
     * Node sub-column.
     * Note that this is a different column/row system from the one that tiles use.
     */
    private final int subCol;

    /**
     * Node sub-row.
     * Note that this is a different column/row system from the one that tiles use.
     */
    private final int subRow;

    /**
     * Alpha value of node.
     */
    private float alpha;

    /**
     * Boolean tracking whether the current node is the end of a ray of light.
     */
    private boolean endOfRay;

    /**
     * Boolean tracking whether the current node has been checked or not by the algorithm when calculating lighting.
     */
    private boolean checked;


    // CONSTRUCTOR
    /**
     * Constructs a node instance at the specified sub-column and sub-row intersection.
     *
     * @param subCol node sub-column
     * @param subRow node sub-row
     */
    public Node(int subCol, int subRow) {
        this.subCol = subCol;
        this.subRow = subRow;
    }


    // GETTERS
    public int getSubCol() {
        return subCol;
    }

    public int getSubRow() {
        return subRow;
    }

    public float getAlpha() {
        return alpha;
    }

    public boolean isEndOfRay() {
        return endOfRay;
    }

    public boolean isChecked() {
        return checked;
    }


    // SETTERS
    public void setAlpha(float alpha) {
        this.alpha = alpha;
    }

    public void setEndOfRay(boolean endOfRay) {
        this.endOfRay = endOfRay;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }
}
