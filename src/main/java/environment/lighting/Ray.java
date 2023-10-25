package environment.lighting;

import java.util.ArrayList;

/**
 * This class defines a ray of light, which is a collection of light nodes.
 */
public class Ray {

    // FIELDS
    /**
     * Array of all nodes affected this ray of light.
     */
    private ArrayList<Node> nodes = new ArrayList<>();


    // CONSTRUCTOR
    /**
     * Constructs a Ray instance.
     */
    public Ray() {}


    // GETTER
    public ArrayList<Node> getNodes() {
        return nodes;
    }
}
