package render.drawable;

import org.joml.Vector2f;
import org.joml.Vector4f;
import render.Sprite;
import render.Texture;

/**
 * This class represents a game object that can be rendered to the screen.
 */
public class Drawable {

    // FIELDS
    /**
     * Drawable color (r, g, b, a).
     */
    protected final Vector4f color;

    /**
     * Stores the current position (top-left coordinate) and scale (width and height) of the quad that this
     * drawable is mapped to.
     * Modifying this variable will directly affect the position and scale of this drawable.
     */
    public Transform transform;

    /**
     * Active drawable sprite.
     */
    protected Sprite sprite;


    // CONSTRUCTORS
    /**
     * Constructs a Drawable instance.
     */
    public Drawable() {
        this.color = new Vector4f(255, 255, 255, 255);
        this.transform = new Transform();
        this.sprite = new Sprite();
    }


    /**
     * Constructs a Drawable instance.
     *
     * @param sprite sprite this drawable
     */
    public Drawable(Sprite sprite) {
        this.color = new Vector4f(255, 255, 255, 255);
        this.transform = new Transform();
        this.sprite = sprite;
    }


    /**
     * Constructs a Drawable instance.
     *
     * @param color color of this drawable (r, g, b, a)
     * @param transform position (top-left coordinate) and scale (width and height) of this drawable
     */
    public Drawable(Transform transform, Vector4f color) {
        this.color = color;
        this.transform = transform;
        this.sprite = new Sprite();
    }


    /**
     * Constructs a Drawable instance.
     *
     * @param sprite sprite of this drawable
     * @param transform position (top-left coordinate) and scale (width and height) of this drawable
     */
    public Drawable(Transform transform, Sprite sprite) {
        this.color = new Vector4f(255, 255, 255, 255);
        this.transform = transform;
        this.sprite = sprite;
    }


    /**
     * Creates a deep copy of this drawable.
     *
     * @return deep copy of this drawable.
     */
    public Drawable copy() {

        Drawable drawable = new Drawable();
        drawable.transform.position.set(this.transform.position);
        drawable.transform.scale.set(this.transform.scale);
        drawable.setColor(this.color);
        drawable.setSprite(this.sprite);
        return drawable;
    }


    /**
     * Deep copies this drawable to the drawable passed as argument.
     *
     * @param drawable drawable to deep copy to
     */
    public void copy(Drawable drawable) {

        drawable.transform.position.set(this.transform.position);
        drawable.transform.scale.set(this.transform.scale);
        drawable.setColor(this.color);
        drawable.setSprite(this.sprite);
    }


    // GETTERS
    public Vector4f getColor() {
        return color;
    }

    public Texture getTexture() {
        return sprite.getTexture();
    }

    public int getNativeSpriteWidth() {
        return sprite.getNativeWidth();
    }

    public int getNativeSpriteHeight() {
        return sprite.getNativeHeight();
    }

    public Vector2f[] getTextureCoords() {
        return sprite.getTextureCoords();
    }


    // SETTERS
    public void setColor(Vector4f color) {
        if (!this.color.equals(color)) {
            this.color.set(color);
        }
    }

    public void setSprite(Sprite sprite) {
        if (!this.sprite.equals(sprite)) {
            this.sprite = sprite;
        }
    }
}
