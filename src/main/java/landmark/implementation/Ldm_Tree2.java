package landmark.implementation;

import core.GamePanel;
import landmark.LandmarkBase;
import asset.AssetPool;

/**
 * This class defines a landmark (Tree2).
 */
public class Ldm_Tree2 extends LandmarkBase {

    // FIELDS
    private static final int ldmId = 1;
    private static final String ldmName = "Tree2";


    // CONSTRUCTOR
    public Ldm_Tree2(GamePanel gp) {
        super(gp, ldmId, 3, 2);
        name = ldmName;
    }


    // METHODS
    @Override
    protected void setTileCollision() {

        // Bottom row of tiles, left to right.
        collision[0][0] = true;
        collision[0][1] = true;

        // Middle row of tiles, left to right.
        collision[1][0] = false;
        collision[1][1] = false;

        // Top row of tiles, left to right.
        collision[2][0] = false;
        collision[2][1] = false;
    }


    @Override
    protected void setSprites() {

        sprites.add(AssetPool.getSpritesheet("landmarks").getSprite(0));

        sprite = sprites.get(0);
        transform.scale.x = sprite.getNativeWidth();
        transform.scale.y = sprite.getNativeHeight();
    }
}
