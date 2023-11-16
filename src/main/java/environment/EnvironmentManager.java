package environment;

import core.GamePanel;
import environment.lighting.LightManager;
import render.Renderer;

import java.awt.*;

/**
 * This class manages all environmental filters, such as lighting, rain, fog, etc.
 */
public class EnvironmentManager {

    // FIELDS
    private final GamePanel gp;
    private Lighting lighting;
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

//        if ((gp.getLoadedMap().hasDayNightCycle()) && (lighting != null)) {                                             // Check if the current map has a day/night cycle.
//
//            lighting.update();
//        }

//        if (lightManager != null) {
//
//            lightManager.update();
//        }
    }


    /**
     * Adds all environmental effects to the render pipeline.
     *
     * @param renderer Renderer instance
     */
    public void addToRenderPipeline(Renderer renderer) {

//        if ((gp.getLoadedMap() != null)
//                && (lighting != null)
//                && (gp.getLoadedMap().hasDayNightCycle())) {
//
//            lighting.draw(g2);
//        }

//        if (lightManager != null) {
//
//            lightManager.draw(g2);
//        }
    }


    /**
     * Instantiates instances for classes representing different environmental effects.
     */
    public void setup() {

        lighting = new Lighting(gp, 275);                                                                               // NOTE: `circleSize` should not be greater than the `screenWidth` or `screenHeight` in GamePanel.
        lightManager = new LightManager(gp);
    }


    // GETTER
    public Lighting getLighting() {
        return lighting;
    }
}
