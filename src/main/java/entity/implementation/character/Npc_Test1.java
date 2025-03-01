package entity.implementation.character;

import entity.EntityBase;
import entity.enumeration.EntityType;
import core.GamePanel;
import asset.AssetPool;

/**
 * This class defines a character entity (Test1).
 */
public class Npc_Test1 extends EntityBase {

    // CONSTRUCTOR
    public Npc_Test1(GamePanel gp, int entityId) {
        super(gp, entityId, EntityType.CHARACTER);
    }


    // METHOD
    @Override
    protected void setSprites() {

        idleDown = AssetPool.getSpritesheet("characters").getSprite(210);
        walkDown1 = AssetPool.getSpritesheet("characters").getSprite(211);
        walkDown2 = AssetPool.getSpritesheet("characters").getSprite(212);

        idleUp = AssetPool.getSpritesheet("characters").getSprite(213);
        walkUp1 = AssetPool.getSpritesheet("characters").getSprite(214);
        walkUp2 = AssetPool.getSpritesheet("characters").getSprite(215);

        idleLeft = AssetPool.getSpritesheet("characters").getSprite(216);
        walkLeft1 = AssetPool.getSpritesheet("characters").getSprite(217);
        walkLeft2 = AssetPool.getSpritesheet("characters").getSprite(218);

        idleRight = AssetPool.getSpritesheet("characters").getSprite(219);
        walkRight1 = AssetPool.getSpritesheet("characters").getSprite(220);
        walkRight2 = AssetPool.getSpritesheet("characters").getSprite(221);

        combatStanceLeft1 = AssetPool.getSpritesheet("characters").getSprite(222);
        combatStanceLeft2 = AssetPool.getSpritesheet("characters").getSprite(223);

        combatStanceRight1 = AssetPool.getSpritesheet("characters").getSprite(225);
        combatStanceRight2 = AssetPool.getSpritesheet("characters").getSprite(226);

        combatAttackLeft = AssetPool.getSpritesheet("characters").getSprite(224);
        combatAttackRight = AssetPool.getSpritesheet("characters").getSprite(227);

        combatFaintLeft1_1 = AssetPool.getSpritesheet("characters").getSprite(228);
        combatFaintLeft2_1 = AssetPool.getSpritesheet("characters").getSprite(230);
        combatFaintLeft3_1 = AssetPool.getSpritesheet("characters").getSprite(232);
        combatFaintLeft4_1 = AssetPool.getSpritesheet("characters").getSprite(234);
        combatFaintLeft5_1 = AssetPool.getSpritesheet("characters").getSprite(236);
        combatFaintLeft6_1 = AssetPool.getSpritesheet("characters").getSprite(238);

        combatFaintLeft1_2 = AssetPool.getSpritesheet("characters").getSprite(229);
        combatFaintLeft2_2 = AssetPool.getSpritesheet("characters").getSprite(231);
        combatFaintLeft3_2 = AssetPool.getSpritesheet("characters").getSprite(233);
        combatFaintLeft4_2 = AssetPool.getSpritesheet("characters").getSprite(235);
        combatFaintLeft5_2 = AssetPool.getSpritesheet("characters").getSprite(237);
        combatFaintLeft6_2 = AssetPool.getSpritesheet("characters").getSprite(239);

        combatFaintRight1_1 = AssetPool.getSpritesheet("characters").getSprite(240);
        combatFaintRight2_1 = AssetPool.getSpritesheet("characters").getSprite(242);
        combatFaintRight3_1 = AssetPool.getSpritesheet("characters").getSprite(244);
        combatFaintRight4_1 = AssetPool.getSpritesheet("characters").getSprite(246);
        combatFaintRight5_1 = AssetPool.getSpritesheet("characters").getSprite(248);
        combatFaintRight6_1 = AssetPool.getSpritesheet("characters").getSprite(250);

        combatFaintRight1_2 = AssetPool.getSpritesheet("characters").getSprite(241);
        combatFaintRight2_2 = AssetPool.getSpritesheet("characters").getSprite(243);
        combatFaintRight3_2 = AssetPool.getSpritesheet("characters").getSprite(245);
        combatFaintRight4_2 = AssetPool.getSpritesheet("characters").getSprite(247);
        combatFaintRight5_2 = AssetPool.getSpritesheet("characters").getSprite(249);
        combatFaintRight5_2 = AssetPool.getSpritesheet("characters").getSprite(251);

        sprite = idleDown;
        transform.scale.x = sprite.getNativeWidth();
        transform.scale.y = sprite.getNativeHeight();
    }
}
