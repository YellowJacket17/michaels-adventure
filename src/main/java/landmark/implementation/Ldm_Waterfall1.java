package landmark.implementation;

import core.GamePanel;
import landmark.LandmarkBase;
import asset.AssetPool;

/**
 * This class defines a landmark (Waterfall1).
 */
public class Ldm_Waterfall1 extends LandmarkBase {

    // FIELDS
    private static final int ldmId = 1;
    private static final String ldmName = "Waterfall1";


    // CONSTRUCTOR
    public Ldm_Waterfall1(GamePanel gp) {
        super(gp, ldmId, 1, 1);
        name = ldmName;
    }


    // METHODS
    @Override
    protected void setTileCollision() {}


    @Override
    protected void setSprites() {

        sprites.add(AssetPool.getSpritesheet("landmarks").getSprite(0));
        sprites.add(AssetPool.getSpritesheet("landmarks").getSprite(1));
        sprites.add(AssetPool.getSpritesheet("landmarks").getSprite(2));
        sprites.add(AssetPool.getSpritesheet("landmarks").getSprite(3));
        sprites.add(AssetPool.getSpritesheet("landmarks").getSprite(4));
        sprites.add(AssetPool.getSpritesheet("landmarks").getSprite(5));
        sprites.add(AssetPool.getSpritesheet("landmarks").getSprite(6));
        sprites.add(AssetPool.getSpritesheet("landmarks").getSprite(7));
        sprites.add(AssetPool.getSpritesheet("landmarks").getSprite(8));
        sprites.add(AssetPool.getSpritesheet("landmarks").getSprite(9));
        sprites.add(AssetPool.getSpritesheet("landmarks").getSprite(10));
        sprites.add(AssetPool.getSpritesheet("landmarks").getSprite(11));
        sprites.add(AssetPool.getSpritesheet("landmarks").getSprite(12));
        sprites.add(AssetPool.getSpritesheet("landmarks").getSprite(13));
        sprites.add(AssetPool.getSpritesheet("landmarks").getSprite(14));
        sprites.add(AssetPool.getSpritesheet("landmarks").getSprite(15));
        sprites.add(AssetPool.getSpritesheet("landmarks").getSprite(16));
        sprites.add(AssetPool.getSpritesheet("landmarks").getSprite(17));
        sprites.add(AssetPool.getSpritesheet("landmarks").getSprite(18));
        sprites.add(AssetPool.getSpritesheet("landmarks").getSprite(19));
        sprites.add(AssetPool.getSpritesheet("landmarks").getSprite(20));

        sprite = sprites.get(0);
        transform.scale.x = sprite.getNativeWidth();
        transform.scale.y = sprite.getNativeHeight();
        animationGroup = 1;

        setAlpha(180);
    }
}
