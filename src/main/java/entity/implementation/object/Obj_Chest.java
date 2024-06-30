package entity.implementation.object;

import entity.EntityBase;
import entity.enumeration.EntityType;
import core.GamePanel;
import asset.AssetPool;

/**
 * This class defines an object entity (Chest).
 */
public class Obj_Chest extends EntityBase {

    // FIELD
    private static final String objName = "Chest";


    // CONSTRUCTOR
    public Obj_Chest(GamePanel gp, int entityId) {
        super(gp, entityId, EntityType.OBJECT);
        name = objName;                                                                                                 // Set name upon instantiation.
        setupSprite();
    }


    // METHOD
    /**
     * Sets loaded entity sprites.
     */
    private void setupSprite() {

        idleDown = AssetPool.getSpritesheet("objects").getSprite(1);

        sprite = idleDown;
        transform.scale.x = sprite.getNativeWidth();
        transform.scale.y = sprite.getNativeHeight();
    }
}
