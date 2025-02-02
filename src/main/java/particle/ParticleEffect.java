package particle;

import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import render.Renderer;
import render.drawable.Transform;
import render.enumeration.ZIndex;

/**
 * This class defines a particle effect.
 */
public class ParticleEffect {

    // FIELDS
    /**
     * Initial world position of each particle.
     * All particles have the same initial position.
     */
    private final Vector2f initialWorldPosition;

    /**
     * Initial world velocity of each particle.
     * Each particle may have a different initial velocity.
     */
    private final Vector2f[] initialWorldVelocities = new Vector2f[4];

    /**
     * World position of each particle.
     * Eac particle may have a different position.
     */
    private final Vector2f[] worldPositions = new Vector2f[4];

    /**
     * Color of each particle (r, g, b).
     */
    private final Vector4f color;

    /**
     * Maximum life of each particle (seconds).
     */
    private final float maximumTime = 0.75f;

    /**
     * Elapsed time since birth of each particle (seconds).
     */
    private float elapsedTime = 0;

    /**
     * Position and scale information used to render each particle.
     */
    private final Transform transform;

    /**
     * Acceleration of each particle in the y-direction.
     */
    private final float gravity = 120.0f;


    // CONSTRUCTOR
    /**
     * Constructs a ParticleEffect instance.
     *
     * @param initialWorldPosition initial world position of center of each particle
     * @param color color of each particle (r, g, b)
     * @param size world size of each particle
     */
    public ParticleEffect(Vector2f initialWorldPosition, Vector3f color, float size) {
        this.initialWorldPosition = new Vector2f(initialWorldPosition.x, initialWorldPosition.y);
        this.color = new Vector4f(color.x, color.y, color.z, 255);
        this.transform = new Transform(new Vector2f(), new Vector2f(size, size));
        initialWorldVelocities[0] = new Vector2f(20.0f, 20.0f);
        initialWorldVelocities[1] = new Vector2f(20.0f, -20.0f);
        initialWorldVelocities[2] = new Vector2f(-20.0f, -20.0f);
        initialWorldVelocities[3] = new Vector2f(-20.0f, 20.0f);
        for (int i = 0; i < worldPositions.length; i++) {
            worldPositions[i] = new Vector2f();
        }
    }


    // METHODS
    /**
     * Updates the state of this particle effect by one frame.
     *
     * @param dt time since last frame (seconds)
     */
    public void update(double dt) {

        elapsedTime += dt;

        if (elapsedTime < maximumTime) {

            for (int i = 0; i < worldPositions.length; i ++) {

                if (elapsedTime >= (maximumTime / 2)) {

                    color.w = (1 - ((elapsedTime - (maximumTime / 2)) / (maximumTime / 2))) * 255;
                }
                worldPositions[i].x = initialWorldPosition.x + (initialWorldVelocities[i].x * elapsedTime);
                worldPositions[i].y = initialWorldPosition.y + (initialWorldVelocities[i].y * elapsedTime)
                        + (0.5f * gravity * elapsedTime * elapsedTime);
            }
        }
    }


    /**
     * Adds this particle effect to the render pipeline.
     *
     * @param renderer Renderer instance
     */
    public void addToRenderPipeline(Renderer renderer) {

        if (elapsedTime < maximumTime) {

            for (int i = 0; i < worldPositions.length; i++) {
                transform.position.x = worldPositions[i].x - (transform.scale.x / 2);                                   // Adjust since world position represents center of particle (i.e., rectangle).
                transform.position.y = worldPositions[i].y - (transform.scale.y / 2);                                   // ^^^
                renderer.addRectangle(color, transform, ZIndex.THIRD_LAYER);
            }
        }
    }


    // GETTER
    public float getMaximumTime() {
        return maximumTime;
    }

    public float getElapsedTime() {
        return elapsedTime;
    }
}
