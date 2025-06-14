package landmark.implementation;

import asset.AssetPool;
import core.GamePanel;
import landmark.LandmarkBase;

/**
 * This class defines a landmark (Foam2).
 */
public class Ldm_Foam2 extends LandmarkBase {

    // FIELDS
    private static final int ldmId = 5;


    // CONSTRUCTOR
    public Ldm_Foam2(GamePanel gp, int col, int row) {
        super(gp, ldmId, 3, 2, col, row);
    }


    // METHODS
    @Override
    protected void setTileCollision() {}


    @Override
    protected void setSprites() {

        sprites.add(AssetPool.getSpritesheet("landmarks").getSprite(67));
        sprites.add(AssetPool.getSpritesheet("landmarks").getSprite(68));
        sprites.add(AssetPool.getSpritesheet("landmarks").getSprite(69));
        sprites.add(AssetPool.getSpritesheet("landmarks").getSprite(70));

        sprite = sprites.get(0);
        transform.scale.x = sprite.getNativeWidth();
        transform.scale.y = sprite.getNativeHeight();
        passiveAnimationGroup = 2;
    }
}
