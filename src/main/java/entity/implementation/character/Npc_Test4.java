package entity.implementation.character;

import asset.AssetPool;
import core.GamePanel;
import entity.EntityBase;
import entity.enumeration.EntityType;

/**
 * This class defines a character entity (Test4).
 */
public class Npc_Test4 extends EntityBase {

    // CONSTRUCTOR
    public Npc_Test4(GamePanel gp, int entityId) {
        super(gp, entityId, EntityType.CHARACTER);
    }


    // METHOD
    @Override
    protected void setSprites() {

        idleDown = AssetPool.getSpritesheet("characters").getSprite(126);
        walkDown1 = AssetPool.getSpritesheet("characters").getSprite(127);
        walkDown2 = AssetPool.getSpritesheet("characters").getSprite(128);

        idleUp = AssetPool.getSpritesheet("characters").getSprite(129);
        walkUp1 = AssetPool.getSpritesheet("characters").getSprite(130);
        walkUp2 = AssetPool.getSpritesheet("characters").getSprite(131);

        idleLeft = AssetPool.getSpritesheet("characters").getSprite(132);
        walkLeft1 = AssetPool.getSpritesheet("characters").getSprite(133);
        walkLeft2 = AssetPool.getSpritesheet("characters").getSprite(134);

        idleRight = AssetPool.getSpritesheet("characters").getSprite(135);
        walkRight1 = AssetPool.getSpritesheet("characters").getSprite(136);
        walkRight2 = AssetPool.getSpritesheet("characters").getSprite(137);

        combatStanceLeft1 = AssetPool.getSpritesheet("characters").getSprite(138);
        combatStanceLeft2 = AssetPool.getSpritesheet("characters").getSprite(139);

        combatStanceRight1 = AssetPool.getSpritesheet("characters").getSprite(141);
        combatStanceRight2 = AssetPool.getSpritesheet("characters").getSprite(142);

        combatAttackLeft = AssetPool.getSpritesheet("characters").getSprite(140);
        combatAttackRight = AssetPool.getSpritesheet("characters").getSprite(143);

        combatFaintLeft1_1 = AssetPool.getSpritesheet("characters").getSprite(144);
        combatFaintLeft2_1 = AssetPool.getSpritesheet("characters").getSprite(146);
        combatFaintLeft3_1 = AssetPool.getSpritesheet("characters").getSprite(148);
        combatFaintLeft4_1 = AssetPool.getSpritesheet("characters").getSprite(150);
        combatFaintLeft5_1 = AssetPool.getSpritesheet("characters").getSprite(152);
        combatFaintLeft6_1 = AssetPool.getSpritesheet("characters").getSprite(154);

        combatFaintLeft1_2 = AssetPool.getSpritesheet("characters").getSprite(145);
        combatFaintLeft2_2 = AssetPool.getSpritesheet("characters").getSprite(147);
        combatFaintLeft3_2 = AssetPool.getSpritesheet("characters").getSprite(149);
        combatFaintLeft4_2 = AssetPool.getSpritesheet("characters").getSprite(151);
        combatFaintLeft5_2 = AssetPool.getSpritesheet("characters").getSprite(153);
        combatFaintLeft6_2 = AssetPool.getSpritesheet("characters").getSprite(155);

        combatFaintRight1_1 = AssetPool.getSpritesheet("characters").getSprite(156);
        combatFaintRight2_1 = AssetPool.getSpritesheet("characters").getSprite(158);
        combatFaintRight3_1 = AssetPool.getSpritesheet("characters").getSprite(160);
        combatFaintRight4_1 = AssetPool.getSpritesheet("characters").getSprite(162);
        combatFaintRight5_1 = AssetPool.getSpritesheet("characters").getSprite(164);
        combatFaintRight6_1 = AssetPool.getSpritesheet("characters").getSprite(166);

        combatFaintRight1_2 = AssetPool.getSpritesheet("characters").getSprite(157);
        combatFaintRight2_2 = AssetPool.getSpritesheet("characters").getSprite(159);
        combatFaintRight3_2 = AssetPool.getSpritesheet("characters").getSprite(161);
        combatFaintRight4_2 = AssetPool.getSpritesheet("characters").getSprite(163);
        combatFaintRight5_2 = AssetPool.getSpritesheet("characters").getSprite(165);
        combatFaintRight6_2 = AssetPool.getSpritesheet("characters").getSprite(167);

        sprite = idleDown;
        transform.scale.x = sprite.getNativeWidth();
        transform.scale.y = sprite.getNativeHeight();
    }
}
