package landmark.implementation;

import asset.AssetPool;
import core.GamePanel;
import landmark.LandmarkBase;
import org.joml.Vector4f;

import java.util.HashMap;

/**
 * This class defines a landmark (TallGrass1).
 */
public class Ldm_TallGrass1 extends LandmarkBase {

    // FIELDS
    private static final int ldmId = 6;

    /**
     * Static map to store all Ldm_Grass1 landmarks that are temporarily animating due to a triggered event; landmark
     * position ID is the key, time passed since beginning to animate is the value.
     */
    private static HashMap<Integer, Double> eventAnimatingLandmarks = new HashMap<>();


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
//        setColor(new Vector4f(255, 231, 168, 255));  //yellow
//        setColor(new Vector4f(146, 214, 233, 255));  //blue
//        setColor(new Vector4f(241, 100, 75, 255));  //red
        setColor(new Vector4f(134, 255, 194, 255));  //green
    }


    // GETTER
    public int getPositionId() {
        return positionId;
    }
}
