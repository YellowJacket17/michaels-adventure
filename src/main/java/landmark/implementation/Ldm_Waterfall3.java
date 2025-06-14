package landmark.implementation;

import asset.AssetPool;
import core.GamePanel;
import landmark.LandmarkBase;

/**
 * This class defines a landmark (Waterfall3).
 */
public class Ldm_Waterfall3 extends LandmarkBase {

    // FIELDS
    private static final int ldmId = 3;


    // CONSTRUCTOR
    public Ldm_Waterfall3(GamePanel gp, int col, int row) {
        super(gp, ldmId, 1, 1, col, row);
    }


    // METHODS
    @Override
    protected void setTileCollision() {}


    @Override
    protected void setSprites() {

        sprites.add(AssetPool.getSpritesheet("landmarks").getSprite(42));
        sprites.add(AssetPool.getSpritesheet("landmarks").getSprite(43));
        sprites.add(AssetPool.getSpritesheet("landmarks").getSprite(44));
        sprites.add(AssetPool.getSpritesheet("landmarks").getSprite(45));
        sprites.add(AssetPool.getSpritesheet("landmarks").getSprite(46));
        sprites.add(AssetPool.getSpritesheet("landmarks").getSprite(47));
        sprites.add(AssetPool.getSpritesheet("landmarks").getSprite(48));
        sprites.add(AssetPool.getSpritesheet("landmarks").getSprite(49));
        sprites.add(AssetPool.getSpritesheet("landmarks").getSprite(50));
        sprites.add(AssetPool.getSpritesheet("landmarks").getSprite(51));
        sprites.add(AssetPool.getSpritesheet("landmarks").getSprite(52));
        sprites.add(AssetPool.getSpritesheet("landmarks").getSprite(53));
        sprites.add(AssetPool.getSpritesheet("landmarks").getSprite(54));
        sprites.add(AssetPool.getSpritesheet("landmarks").getSprite(55));
        sprites.add(AssetPool.getSpritesheet("landmarks").getSprite(56));
        sprites.add(AssetPool.getSpritesheet("landmarks").getSprite(57));
        sprites.add(AssetPool.getSpritesheet("landmarks").getSprite(58));
        sprites.add(AssetPool.getSpritesheet("landmarks").getSprite(59));
        sprites.add(AssetPool.getSpritesheet("landmarks").getSprite(60));
        sprites.add(AssetPool.getSpritesheet("landmarks").getSprite(61));
        sprites.add(AssetPool.getSpritesheet("landmarks").getSprite(62));

        sprite = sprites.get(0);
        transform.scale.x = sprite.getNativeWidth();
        transform.scale.y = sprite.getNativeHeight();
        passiveAnimationGroup = 1;

        setAlpha(180);
    }
}
