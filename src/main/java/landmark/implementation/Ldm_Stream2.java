package landmark.implementation;

import asset.AssetPool;
import core.GamePanel;
import landmark.LandmarkBase;

/**
 * This class defines a landmark (Stream2).
 */
public class Ldm_Stream2 extends LandmarkBase {

    // FIELDS
    private static final int ldmId = 8;


    // CONSTRUCTOR
    public Ldm_Stream2(GamePanel gp, int col, int row) {
        super(gp, ldmId, 2, 1, col, row);
    }


    // METHODS
    @Override
    protected void setTileCollision() {}


    @Override
    protected void setSprites() {

        sprites.add(AssetPool.getSpritesheet("landmarks").getSprite(75));
        sprites.add(AssetPool.getSpritesheet("landmarks").getSprite(76));

        sprite = sprites.get(0);
        transform.scale.x = sprite.getNativeWidth();
        transform.scale.y = sprite.getNativeHeight();
        passiveAnimationGroup = 4;
    }
}
