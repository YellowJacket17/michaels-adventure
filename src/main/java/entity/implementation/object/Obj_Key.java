package entity.implementation.object;

import entity.EntityBase;
import entity.EntityType;
import core.GamePanel;
import utility.AssetPool;

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
        setupSprite();
    }


    // METHOD
    /**
     * Sets loaded entity sprites.
     */
    private void setupSprite() {

        down1 = AssetPool.getSpritesheet(2).getSprite(0);

        sprite = down1;
        transform.scale.x = sprite.getNativeWidth();
        transform.scale.y = sprite.getNativeHeight();
    }
}
