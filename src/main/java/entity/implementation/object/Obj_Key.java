package entity.implementation.object;

import entity.EntityBase;
import entity.enumeration.EntityType;
import core.GamePanel;
import asset.AssetPool;

/**
 * This class defines an object entity (Key).
 */
public class Obj_Key extends EntityBase {

    // FIELD
    private static final String objName = "Key";


    // CONSTRUCTOR
    public Obj_Key(GamePanel gp, int entityId) {
        super(gp, entityId, EntityType.OBJECT);
        name = objName;                                                                                                 // Set name upon instantiation.
    }


    // METHOD
    @Override
    protected void setSprites() {

        idleDown = AssetPool.getSpritesheet("objects").getSprite(0);

        sprite = idleDown;
        transform.scale.x = sprite.getNativeWidth();
        transform.scale.y = sprite.getNativeHeight();
    }
}
