package environment;

import core.GamePanel;
import environment.lighting.LightManager;
import render.Renderer;

/**
 * This class manages all environmental filters, such as lighting, rain, fog, etc.
 */
public class EnvironmentManager {

    // FIELDS
    private final GamePanel gp;
    private LightManager lightManager;


    // CONSTRUCTOR
    /**
     * Constructs an EnvironmentManager instance.
     *
     * @param gp GamePanel instance
     */
    public EnvironmentManager(GamePanel gp) {
        this.gp = gp;
    }


    // METHODS
    /**
     * Updates environmental effects by one frame.
     */
    public void update() {

        if (lightManager != null) {

            lightManager.update();
        }
    }


    /**
     * Adds all environmental effects to the render pipeline.
     *
     * @param renderer Renderer instance
     */
    public void addToRenderPipeline(Renderer renderer) {

        if (lightManager != null) {

            lightManager.addToRenderPipeline(renderer);
        }
    }


    /**
     * Instantiates instances for classes representing different environmental effects.
     */
    public void setup() {

        lightManager = new LightManager(gp);
    }
}
