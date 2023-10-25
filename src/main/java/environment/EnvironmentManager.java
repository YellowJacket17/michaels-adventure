package environment;

import core.GamePanel;
import environment.lighting.LightManager;

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
     * Draws environmental effects.
     *
     * @param g2 Graphics2D instance
     */
    public void draw(Graphics2D g2) {

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

        lighting = new Lighting(gp, 275 * gp.getScale());                                                               // NOTE: `circleSize` should not be greater than the `screenWidth` or `screenHeight` in GamePanel.
        lightManager = new LightManager(gp);
    }


    // GETTER
    public Lighting getLighting() {
        return lighting;
    }
}
