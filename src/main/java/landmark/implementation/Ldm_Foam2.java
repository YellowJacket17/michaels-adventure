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
    private static final String ldmName = "Foam2";


    // CONSTRUCTOR
    public Ldm_Foam2(GamePanel gp) {
        super(gp, ldmId, 2, 3);
        name = ldmName;
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
    }
}
