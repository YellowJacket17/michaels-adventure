package entity.implementation.object;

import entity.EntityBase;
import entity.enumeration.EntityType;
import core.GamePanel;
import asset.AssetPool;

/**
 * This class defines an object entity (Controller).
 */
public class Obj_Controller extends EntityBase {

    // FIELDS
    private static final String objName = "Controller";


    // CONSTRUCTOR
    public Obj_Controller(GamePanel gp, int entityId) {
        super(gp, entityId, EntityType.OBJECT);
        name = objName;                                                                                                 // Set name upon instantiation.
    }


    // METHOD
    @Override
    protected void setSprites() {

        idleDown = AssetPool.getSpritesheet("objects").getSprite(2);

        sprite = idleDown;
        transform.scale.x = sprite.getNativeWidth();
        transform.scale.y = sprite.getNativeHeight();
    }
}
