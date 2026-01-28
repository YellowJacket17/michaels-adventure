package landmark.implementation;

import asset.AssetPool;
import core.GamePanel;
import landmark.LandmarkBase;

/**
 * This class defines a landmark (Eye1).
 */
public class Ldm_Eye1 extends LandmarkBase {

    // FIELDS
    private static final int ldmId = 9;


    // CONSTRUCTOR
    public Ldm_Eye1(GamePanel gp, int col, int row) {
        super(gp, ldmId, 1, 1, col, row);
    }


    // METHODS
    @Override
    protected void setTileCollision() {}


    @Override
    protected void setSprites() {

        sprites.add(AssetPool.getSpritesheet("landmarks").getSprite(81));
        sprites.add(AssetPool.getSpritesheet("landmarks").getSprite(82));
        sprites.add(AssetPool.getSpritesheet("landmarks").getSprite(83));
        sprites.add(AssetPool.getSpritesheet("landmarks").getSprite(84));
        sprites.add(AssetPool.getSpritesheet("landmarks").getSprite(85));
        sprites.add(AssetPool.getSpritesheet("landmarks").getSprite(86));

        sprite = sprites.get(0);
        transform.scale.x = sprite.getNativeWidth();
        transform.scale.y = sprite.getNativeHeight();
        passiveAnimationGroup = 5;
    }
}
