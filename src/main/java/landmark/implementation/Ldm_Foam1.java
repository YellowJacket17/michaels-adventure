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
    private static final String ldmName = "Foam1";


    // CONSTRUCTOR
    public Ldm_Foam1(GamePanel gp) {
        super(gp, ldmId, 2, 4);
        name = ldmName;
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
    }
}
