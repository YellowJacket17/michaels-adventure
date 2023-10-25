package entity.implementation.object;

import entity.EntityBase;
import entity.EntityType;
import core.GamePanel;

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
        down1 = setupImage("/objects/controller.png");
    }
}
