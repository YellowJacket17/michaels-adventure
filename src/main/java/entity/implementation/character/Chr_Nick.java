package entity.implementation.character;

import entity.EntityBase;
import entity.enumeration.EntityType;
import core.GamePanel;
import asset.AssetPool;

/**
 * This class defines a character entity (Nick).
 */
public class Chr_Nick extends EntityBase {

    // CONSTRUCTOR
    public Chr_Nick(GamePanel gp, int entityId) {
        super(gp, entityId, EntityType.CHARACTER);
    }


    // METHOD
    @Override
    protected void setSprites() {

        idleDown = AssetPool.getSpritesheet("characters").getSprite(42);
        walkDown1 = AssetPool.getSpritesheet("characters").getSprite(43);
        walkDown2 = AssetPool.getSpritesheet("characters").getSprite(44);

        idleUp = AssetPool.getSpritesheet("characters").getSprite(45);
        walkUp1 = AssetPool.getSpritesheet("characters").getSprite(46);
        walkUp2 = AssetPool.getSpritesheet("characters").getSprite(47);

        idleLeft = AssetPool.getSpritesheet("characters").getSprite(48);
        walkLeft1 = AssetPool.getSpritesheet("characters").getSprite(49);
        walkLeft2 = AssetPool.getSpritesheet("characters").getSprite(50);

        idleRight = AssetPool.getSpritesheet("characters").getSprite(51);
        walkRight1 = AssetPool.getSpritesheet("characters").getSprite(52);
        walkRight2 = AssetPool.getSpritesheet("characters").getSprite(53);

        combatStanceLeft1 = AssetPool.getSpritesheet("characters").getSprite(54);
        combatStanceLeft2 = AssetPool.getSpritesheet("characters").getSprite(55);

        combatStanceRight1 = AssetPool.getSpritesheet("characters").getSprite(57);
        combatStanceRight2 = AssetPool.getSpritesheet("characters").getSprite(58);

        combatAttackLeft = AssetPool.getSpritesheet("characters").getSprite(56);
        combatAttackRight = AssetPool.getSpritesheet("characters").getSprite(59);

        combatFaintLeft1_1 = AssetPool.getSpritesheet("characters").getSprite(60);
        combatFaintLeft2_1 = AssetPool.getSpritesheet("characters").getSprite(62);
        combatFaintLeft3_1 = AssetPool.getSpritesheet("characters").getSprite(64);
        combatFaintLeft4_1 = AssetPool.getSpritesheet("characters").getSprite(66);
        combatFaintLeft5_1 = AssetPool.getSpritesheet("characters").getSprite(68);
        combatFaintLeft6_1 = AssetPool.getSpritesheet("characters").getSprite(70);

        combatFaintLeft1_2 = AssetPool.getSpritesheet("characters").getSprite(61);
        combatFaintLeft2_2 = AssetPool.getSpritesheet("characters").getSprite(63);
        combatFaintLeft3_2 = AssetPool.getSpritesheet("characters").getSprite(65);
        combatFaintLeft4_2 = AssetPool.getSpritesheet("characters").getSprite(67);
        combatFaintLeft5_2 = AssetPool.getSpritesheet("characters").getSprite(69);
        combatFaintLeft6_2 = AssetPool.getSpritesheet("characters").getSprite(71);

        combatFaintRight1_1 = AssetPool.getSpritesheet("characters").getSprite(72);
        combatFaintRight2_1 = AssetPool.getSpritesheet("characters").getSprite(74);
        combatFaintRight3_1 = AssetPool.getSpritesheet("characters").getSprite(76);
        combatFaintRight4_1 = AssetPool.getSpritesheet("characters").getSprite(78);
        combatFaintRight5_1 = AssetPool.getSpritesheet("characters").getSprite(88);
        combatFaintRight6_1 = AssetPool.getSpritesheet("characters").getSprite(82);

        combatFaintRight1_2 = AssetPool.getSpritesheet("characters").getSprite(73);
        combatFaintRight2_2 = AssetPool.getSpritesheet("characters").getSprite(75);
        combatFaintRight3_2 = AssetPool.getSpritesheet("characters").getSprite(77);
        combatFaintRight4_2 = AssetPool.getSpritesheet("characters").getSprite(79);
        combatFaintRight5_2 = AssetPool.getSpritesheet("characters").getSprite(81);
        combatFaintRight6_2 = AssetPool.getSpritesheet("characters").getSprite(83);

        sprite = idleDown;
        transform.scale.x = sprite.getNativeWidth();
        transform.scale.y = sprite.getNativeHeight();
    }
}
