package entity.implementation.object;

import entity.EntityBase;
import entity.EntityType;
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

        down1 = AssetPool.getSpritesheet(2).getSprite(1);

        sprite = down1;
        transform.scale.x = sprite.getNativeWidth();
        transform.scale.y = sprite.getNativeHeight();
    }
}
