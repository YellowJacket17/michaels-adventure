package entity.implementation.character;

import entity.EntityBase;
import entity.EntityType;
import core.GamePanel;

/**
 * This class defines a character entity (Test2).
 */
public class Npc_Test2 extends EntityBase {

    // CONSTRUCTOR
    public Npc_Test2(GamePanel gp, int entityId) {
        super(gp, entityId, EntityType.CHARACTER);
        getImage();
    }


    // METHOD
    /**
     * Stages entity sprites to load from resources directory.
     */
    private void getImage() {

        up1 = setupImage("/characters/test_npc_2/up1.png");
        up2 = setupImage("/characters/test_npc_2/up2.png");
        up3 = setupImage("/characters/test_npc_2/up3.png");

        down1 = setupImage("/characters/test_npc_2/down1.png");
        down2 = setupImage("/characters/test_npc_2/down2.png");
        down3 = setupImage("/characters/test_npc_2/down3.png");

        left1 = setupImage("/characters/test_npc_2/left1.png");
        left2 = setupImage("/characters/test_npc_2/left2.png");
        left3 = setupImage("/characters/test_npc_2/left3.png");

        right1 = setupImage("/characters/test_npc_2/right1.png");
        right2 = setupImage("/characters/test_npc_2/right2.png");
        right3 = setupImage("/characters/test_npc_2/right3.png");
    }
}
