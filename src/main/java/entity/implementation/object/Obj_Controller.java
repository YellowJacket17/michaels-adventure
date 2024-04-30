package entity.implementation.object;

import entity.EntityBase;
import entity.EntityType;
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
        setupSprite();
    }


    // METHOD
    /**
     * Sets loaded entity sprites.
     */
    private void setupSprite() {

        down1 = AssetPool.getSpritesheet("objects").getSprite(2);

        sprite = down1;
        transform.scale.x = sprite.getNativeWidth();
        transform.scale.y = sprite.getNativeHeight();
    }
}
