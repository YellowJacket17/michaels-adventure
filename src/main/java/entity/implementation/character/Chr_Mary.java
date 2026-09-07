package entity.implementation.character;

import entity.EntityBase;
import entity.enumeration.EntityType;
import core.GamePanel;
import asset.AssetPool;

/**
 * This class defines a character entity (Mary).
 */
public class Chr_Mary extends EntityBase {

    // CONSTRUCTOR
    public Chr_Mary(GamePanel gp, int entityId) {
        super(gp, entityId, EntityType.CHARACTER);
    }


    // METHOD
    @Override
    protected void setSprites() {

        idleDown = AssetPool.getSpritesheet("characters").getSprite(0);
        walkDown1 = AssetPool.getSpritesheet("characters").getSprite(1);
        walkDown2 = AssetPool.getSpritesheet("characters").getSprite(2);

        idleUp = AssetPool.getSpritesheet("characters").getSprite(3);
        walkUp1 = AssetPool.getSpritesheet("characters").getSprite(4);
        walkUp2 = AssetPool.getSpritesheet("characters").getSprite(5);

        idleLeft = AssetPool.getSpritesheet("characters").getSprite(6);
        walkLeft1 = AssetPool.getSpritesheet("characters").getSprite(7);
        walkLeft2 = AssetPool.getSpritesheet("characters").getSprite(8);

        idleRight = AssetPool.getSpritesheet("characters").getSprite(9);
        walkRight1 = AssetPool.getSpritesheet("characters").getSprite(10);
        walkRight2 = AssetPool.getSpritesheet("characters").getSprite(11);

        combatStanceLeft1 = AssetPool.getSpritesheet("characters").getSprite(12);
        combatStanceLeft2 = AssetPool.getSpritesheet("characters").getSprite(13);

        combatStanceRight1 = AssetPool.getSpritesheet("characters").getSprite(15);
        combatStanceRight2 = AssetPool.getSpritesheet("characters").getSprite(16);

        combatAttackLeft = AssetPool.getSpritesheet("characters").getSprite(14);
        combatAttackRight = AssetPool.getSpritesheet("characters").getSprite(17);

        combatFaintLeft1_1 = AssetPool.getSpritesheet("characters").getSprite(18);
        combatFaintLeft2_1 = AssetPool.getSpritesheet("characters").getSprite(20);
        combatFaintLeft3_1 = AssetPool.getSpritesheet("characters").getSprite(22);
        combatFaintLeft4_1 = AssetPool.getSpritesheet("characters").getSprite(24);
        combatFaintLeft5_1 = AssetPool.getSpritesheet("characters").getSprite(26);
        combatFaintLeft6_1 = AssetPool.getSpritesheet("characters").getSprite(28);

        combatFaintLeft1_2 = AssetPool.getSpritesheet("characters").getSprite(19);
        combatFaintLeft2_2 = AssetPool.getSpritesheet("characters").getSprite(21);
        combatFaintLeft3_2 = AssetPool.getSpritesheet("characters").getSprite(23);
        combatFaintLeft4_2 = AssetPool.getSpritesheet("characters").getSprite(25);
        combatFaintLeft5_2 = AssetPool.getSpritesheet("characters").getSprite(27);
        combatFaintLeft6_2 = AssetPool.getSpritesheet("characters").getSprite(29);

        combatFaintRight1_1 = AssetPool.getSpritesheet("characters").getSprite(30);
        combatFaintRight2_1 = AssetPool.getSpritesheet("characters").getSprite(32);
        combatFaintRight3_1 = AssetPool.getSpritesheet("characters").getSprite(34);
        combatFaintRight4_1 = AssetPool.getSpritesheet("characters").getSprite(36);
        combatFaintRight5_1 = AssetPool.getSpritesheet("characters").getSprite(38);
        combatFaintRight6_1 = AssetPool.getSpritesheet("characters").getSprite(40);

        combatFaintRight1_2 = AssetPool.getSpritesheet("characters").getSprite(31);
        combatFaintRight2_2 = AssetPool.getSpritesheet("characters").getSprite(33);
        combatFaintRight3_2 = AssetPool.getSpritesheet("characters").getSprite(35);
        combatFaintRight4_2 = AssetPool.getSpritesheet("characters").getSprite(37);
        combatFaintRight5_2 = AssetPool.getSpritesheet("characters").getSprite(39);
        combatFaintRight6_2 = AssetPool.getSpritesheet("characters").getSprite(41);

        sprite = idleDown;
        transform.scale.x = sprite.getNativeWidth();
        transform.scale.y = sprite.getNativeHeight();
    }
}
