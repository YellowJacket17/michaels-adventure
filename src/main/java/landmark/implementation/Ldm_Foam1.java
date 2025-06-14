package landmark.implementation;

import asset.AssetPool;
import core.GamePanel;
import landmark.LandmarkBase;

/**
 * This class defines a landmark (Foam1).
 */
public class Ldm_Foam1 extends LandmarkBase {

    // FIELDS
    private static final int ldmId = 4;


    // CONSTRUCTOR
    public Ldm_Foam1(GamePanel gp, int col, int row) {
        super(gp, ldmId, 4, 2, col, row);
    }


    // METHODS
    @Override
    protected void setTileCollision() {}


    @Override
    protected void setSprites() {

        sprites.add(AssetPool.getSpritesheet("landmarks").getSprite(63));
        sprites.add(AssetPool.getSpritesheet("landmarks").getSprite(64));
        sprites.add(AssetPool.getSpritesheet("landmarks").getSprite(65));
        sprites.add(AssetPool.getSpritesheet("landmarks").getSprite(66));

        sprite = sprites.get(0);
        transform.scale.x = sprite.getNativeWidth();
        transform.scale.y = sprite.getNativeHeight();
        passiveAnimationGroup = 2;
    }
}
