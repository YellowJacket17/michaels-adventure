package landmark.implementation;

import core.GamePanel;
import landmark.LandmarkBase;
import asset.AssetPool;

/**
 * This class defines a landmark (Tree1).
 */
public class Ldm_Tree1 extends LandmarkBase {

    // FIELDS
    private static final int ldmId = 0;
    private static final String ldmName = "Tree1";


    // CONSTRUCTOR
    public Ldm_Tree1(GamePanel gp) {
        super(gp, ldmId, 2, 1);
        name = ldmName;
    }


    // METHODS
    @Override
    protected void setTileCollision() {

        // Bottom row of tiles, left to right.
        collision[0][0] = true;

        // Top row of tiles, left to right.
        collision[1][0] = false;
    }


    @Override
    protected void setSprites() {

        sprites.add(AssetPool.getSpritesheet("landmarks").getSprite(1));

        sprite = sprites.get(0);
        transform.scale.x = sprite.getNativeWidth();
        transform.scale.y = sprite.getNativeHeight();
    }
}
