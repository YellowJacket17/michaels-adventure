package landmark.implementation;

import asset.AssetPool;
import core.GamePanel;
import landmark.LandmarkBase;

/**
 * This class defines a landmark (Rock1).
 */
public class Ldm_Rock1 extends LandmarkBase {

    // FIELDS
    private static final int ldmId = 9;


    // CONSTRUCTOR
    public Ldm_Rock1(GamePanel gp, int col, int row) {
        super(gp, ldmId, 2, 1, col, row);
    }


    // METHODS
    @Override
    protected void setTileCollision() {}


    @Override
    protected void setSprites() {

        sprites.add(AssetPool.getSpritesheet("landmarks").getSprite(77));
        sprites.add(AssetPool.getSpritesheet("landmarks").getSprite(78));
        sprites.add(AssetPool.getSpritesheet("landmarks").getSprite(79));
        sprites.add(AssetPool.getSpritesheet("landmarks").getSprite(80));

        sprite = sprites.get(0);
        transform.scale.x = sprite.getNativeWidth();
        transform.scale.y = sprite.getNativeHeight();
        passiveAnimationGroup = 2;
    }
}
