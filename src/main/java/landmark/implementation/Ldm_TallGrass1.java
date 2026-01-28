package landmark.implementation;

import asset.AssetPool;
import core.GamePanel;
import landmark.LandmarkBase;
import landmark.enumeration.TallGrassColor;
import org.joml.Vector4f;

import java.util.HashMap;

/**
 * This class defines a landmark (TallGrass1).
 */
public class Ldm_TallGrass1 extends LandmarkBase {

    // FIELDS
    private static final int ldmId = 6;

    /**
     * Static variable to set what color instantiated landmarks should be.
     */
    private static TallGrassColor instantiationColor = TallGrassColor.DEFAULT;


    // CONSTRUCTOR
    public Ldm_TallGrass1(GamePanel gp, int col, int row) {
        super(gp, ldmId, 1, 3, col, row);
    }


    // METHODS
    @Override
    public void updateInteractiveAnimation(double dt) {

        if (gp.getLandmarkM().getActiveInteractiveAnimations().containsKey(positionId)) {

            gp.getLandmarkM().getActiveInteractiveAnimations().put(
                    positionId, gp.getLandmarkM().getActiveInteractiveAnimations().get(positionId) + dt);

            if ((gp.getLandmarkM().getActiveInteractiveAnimations().get(positionId) < 0.25)) {

                sprite = sprites.get(1);
            } else {

                exitConditionalAnimation();
            }
        }
    }


    @Override
    protected void setTileCollision() {}


    @Override
    protected void setSprites() {

        sprites.add(AssetPool.getSpritesheet("landmarks").getSprite(71));
        sprites.add(AssetPool.getSpritesheet("landmarks").getSprite(72));

        sprite = sprites.get(0);
        transform.scale.x = sprite.getNativeWidth();
        transform.scale.y = sprite.getNativeHeight();

        switch (instantiationColor) {
            case DEFAULT:
                setColor(new Vector4f(255, 255, 255, 255));
                break;
            case GREEN:
                setColor(new Vector4f(134, 255, 194, 255));
                break;
            case YELLOW:
                setColor(new Vector4f(255, 231, 168, 255));
                break;
            case BLUE:
                setColor(new Vector4f(146, 214, 233, 255));
                break;
            case RED:
                setColor(new Vector4f(241, 100, 75, 255));
                break;
        }
    }


    // GETTER
    public int getPositionId() {
        return positionId;
    }


    // SETTER
    public static void setInstantiationColor(TallGrassColor tallGrassColor) {
        instantiationColor = tallGrassColor;
    }
}
