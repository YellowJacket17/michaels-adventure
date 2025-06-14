package landmark.implementation;

import core.GamePanel;
import landmark.LandmarkBase;
import asset.AssetPool;

/**
 * This class defines a landmark (Waterfall2).
 */
public class Ldm_Waterfall2 extends LandmarkBase {

    // FIELDS
    private static final int ldmId = 2;


    // CONSTRUCTOR
    public Ldm_Waterfall2(GamePanel gp, int col, int row) {
        super(gp, ldmId, 1, 1, col, row);
    }


    // METHODS
    @Override
    protected void setTileCollision() {}


    @Override
    protected void setSprites() {

        sprites.add(AssetPool.getSpritesheet("landmarks").getSprite(21));
        sprites.add(AssetPool.getSpritesheet("landmarks").getSprite(22));
        sprites.add(AssetPool.getSpritesheet("landmarks").getSprite(23));
        sprites.add(AssetPool.getSpritesheet("landmarks").getSprite(24));
        sprites.add(AssetPool.getSpritesheet("landmarks").getSprite(25));
        sprites.add(AssetPool.getSpritesheet("landmarks").getSprite(26));
        sprites.add(AssetPool.getSpritesheet("landmarks").getSprite(27));
        sprites.add(AssetPool.getSpritesheet("landmarks").getSprite(28));
        sprites.add(AssetPool.getSpritesheet("landmarks").getSprite(29));
        sprites.add(AssetPool.getSpritesheet("landmarks").getSprite(30));
        sprites.add(AssetPool.getSpritesheet("landmarks").getSprite(31));
        sprites.add(AssetPool.getSpritesheet("landmarks").getSprite(32));
        sprites.add(AssetPool.getSpritesheet("landmarks").getSprite(33));
        sprites.add(AssetPool.getSpritesheet("landmarks").getSprite(34));
        sprites.add(AssetPool.getSpritesheet("landmarks").getSprite(35));
        sprites.add(AssetPool.getSpritesheet("landmarks").getSprite(36));
        sprites.add(AssetPool.getSpritesheet("landmarks").getSprite(37));
        sprites.add(AssetPool.getSpritesheet("landmarks").getSprite(38));
        sprites.add(AssetPool.getSpritesheet("landmarks").getSprite(39));
        sprites.add(AssetPool.getSpritesheet("landmarks").getSprite(40));
        sprites.add(AssetPool.getSpritesheet("landmarks").getSprite(41));

        sprite = sprites.get(0);
        transform.scale.x = sprite.getNativeWidth();
        transform.scale.y = sprite.getNativeHeight();
        passiveAnimationGroup = 1;

        setAlpha(180);
    }
}
