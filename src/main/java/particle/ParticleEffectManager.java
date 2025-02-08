package particle;

import org.joml.Vector2f;
import org.joml.Vector3f;
import render.Renderer;
import utility.LimitedArrayList;

import java.util.Iterator;
import java.util.UUID;

/**
 * This class instantiates particle effects and handles their lifecycles.
 */
public class ParticleEffectManager {

    // FIELD
    /**
     * List to store all instantiated particle effects.
     */
    private final LimitedArrayList<ParticleEffect> particleEffects = new LimitedArrayList<>(10);


    // CONSTRUCTOR
    /**
     * Constructs a ParticleEffectManager instance.
     */
    public ParticleEffectManager() {}


    // METHODS
    /**
     * Updates the state of each active particle effect by one frame.
     *
     * @param dt time since last frame (seconds)
     */
    public void update(double dt) {

        Iterator<ParticleEffect> iter = particleEffects.iterator();
        ParticleEffect particleEffect;

        while (iter.hasNext()) {

            particleEffect = iter.next();
            particleEffect.update(dt);

            if (particleEffect.getElapsedTime() >= particleEffect.getMaximumTime())

                iter.remove();
        }
    }


    /**
     * Adds each active particle effect to the render pipeline.
     *
     * @param renderer Renderer instance
     */
    public void addToRenderPipeline(Renderer renderer) {

        for (ParticleEffect particleEffect : particleEffects) {

            particleEffect.addToRenderPipeline(renderer);
        }
    }


    /**
     * Instantiates a new particle effect.
     *
     * @param worldOriginPosition world initial position of center of each particle
     * @param color color of each particle (r, g, b)
     * @param size world size of each particle
     */
    public void addParticleEffect(Vector2f worldOriginPosition, Vector3f color, float size) {

        particleEffects.add(new ParticleEffect(worldOriginPosition, color, size));
    }


    /**
     * Retrieves a particle effect instance by UUID.
     *
     * @param uuid uuid of target particle effect
     * @return particle effect (null if no match found)
     */
    public ParticleEffect getParticleEffectByUuid(UUID uuid) {

        for (ParticleEffect particleEffect : particleEffects) {

            if (particleEffect.getUuid().equals(uuid)) {

                return particleEffect;
            }
        }
        return null;
    }
}
