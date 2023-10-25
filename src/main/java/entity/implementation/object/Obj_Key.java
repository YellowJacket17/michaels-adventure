package entity.implementation.object;

import entity.EntityBase;
import entity.EntityType;
import core.GamePanel;

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
        down1 = setupImage("/objects/key.png");
    }
}
