package render.drawable;

import org.joml.Vector2f;

/**
 * This class stores position and scale information.
 */
public class Transform {

    // FIELDS
    /**
     * Position (top-left coordinate).
     */
    public Vector2f position;

    /**
     * Scale (width and height).
     */
    public Vector2f scale;


    // CONSTRUCTORS
    /**
     * Constructs a Transform instance.
     */
    public Transform() {
        this.position = new Vector2f();
        this.scale = new Vector2f();
    }


    /**
     * Constructs a Transform instance.
     *
     * @param position position (top-left coordinate)
     * @param scale size (width and height)
     */
    public Transform(Vector2f position, Vector2f scale) {
        this.position = position;
        this.scale = scale;
    }


    // METHODS
    /**
     * Creates a deep copy of this transform.
     *
     * @return deep copy of this transform
     */
    public Transform copy() {

        return new Transform(new Vector2f(this.position), new Vector2f(this.scale));
    }


    /**
     * Deep copies this transform to the transform passed as argument.
     *
     * @param transform transform to deep copy to
     */
    public void copy(Transform transform) {

        transform.position.set(this.position);
        transform.scale.set(this.scale);
    }


    @Override
    public boolean equals(Object o) {

        if (o == null) {
            return false;
        }
        if (!(o instanceof Transform)) {
            return false;
        }
        Transform oTransform = (Transform)o;
        return oTransform.position.equals(this.position) && oTransform.scale.equals(this.scale);
    }
}
