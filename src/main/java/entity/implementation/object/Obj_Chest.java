package entity.implementation.object;

import entity.EntityBase;
import entity.EntityType;
import core.GamePanel;

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
        down1 = setupImage("/objects/chest.png");
    }
}
