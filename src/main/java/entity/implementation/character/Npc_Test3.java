package entity.implementation.character;

import asset.AssetPool;
import core.GamePanel;
import entity.EntityBase;
import entity.enumeration.EntityType;

/**
 * This class defines a character entity (Test3).
 */
public class Npc_Test3 extends EntityBase {

    // CONSTRUCTOR
    public Npc_Test3(GamePanel gp, int entityId) {
        super(gp, entityId, EntityType.CHARACTER);
    }


    // METHOD
    @Override
    protected void setSprites() {

        idleDown = AssetPool.getSpritesheet("characters").getSprite(168);
        walkDown1 = AssetPool.getSpritesheet("characters").getSprite(169);
        walkDown2 = AssetPool.getSpritesheet("characters").getSprite(170);

        idleUp = AssetPool.getSpritesheet("characters").getSprite(171);
        walkUp1 = AssetPool.getSpritesheet("characters").getSprite(172);
        walkUp2 = AssetPool.getSpritesheet("characters").getSprite(173);

        idleLeft = AssetPool.getSpritesheet("characters").getSprite(174);
        walkLeft1 = AssetPool.getSpritesheet("characters").getSprite(175);
        walkLeft2 = AssetPool.getSpritesheet("characters").getSprite(176);

        idleRight = AssetPool.getSpritesheet("characters").getSprite(177);
        walkRight1 = AssetPool.getSpritesheet("characters").getSprite(178);
        walkRight2 = AssetPool.getSpritesheet("characters").getSprite(179);

        combatStanceLeft1 = AssetPool.getSpritesheet("characters").getSprite(180);
        combatStanceLeft2 = AssetPool.getSpritesheet("characters").getSprite(181);

        combatStanceRight1 = AssetPool.getSpritesheet("characters").getSprite(183);
        combatStanceRight2 = AssetPool.getSpritesheet("characters").getSprite(184);

        combatAttackLeft = AssetPool.getSpritesheet("characters").getSprite(182);
        combatAttackRight = AssetPool.getSpritesheet("characters").getSprite(185);

        combatFaintLeft1_1 = AssetPool.getSpritesheet("characters").getSprite(186);
        combatFaintLeft2_1 = AssetPool.getSpritesheet("characters").getSprite(188);
        combatFaintLeft3_1 = AssetPool.getSpritesheet("characters").getSprite(190);
        combatFaintLeft4_1 = AssetPool.getSpritesheet("characters").getSprite(192);
        combatFaintLeft5_1 = AssetPool.getSpritesheet("characters").getSprite(194);
        combatFaintLeft6_1 = AssetPool.getSpritesheet("characters").getSprite(196);

        combatFaintLeft1_2 = AssetPool.getSpritesheet("characters").getSprite(187);
        combatFaintLeft2_2 = AssetPool.getSpritesheet("characters").getSprite(189);
        combatFaintLeft3_2 = AssetPool.getSpritesheet("characters").getSprite(191);
        combatFaintLeft4_2 = AssetPool.getSpritesheet("characters").getSprite(193);
        combatFaintLeft5_2 = AssetPool.getSpritesheet("characters").getSprite(195);
        combatFaintLeft6_2 = AssetPool.getSpritesheet("characters").getSprite(197);

        combatFaintRight1_1 = AssetPool.getSpritesheet("characters").getSprite(198);
        combatFaintRight2_1 = AssetPool.getSpritesheet("characters").getSprite(200);
        combatFaintRight3_1 = AssetPool.getSpritesheet("characters").getSprite(202);
        combatFaintRight4_1 = AssetPool.getSpritesheet("characters").getSprite(204);
        combatFaintRight5_1 = AssetPool.getSpritesheet("characters").getSprite(206);
        combatFaintRight6_1 = AssetPool.getSpritesheet("characters").getSprite(208);

        combatFaintRight1_2 = AssetPool.getSpritesheet("characters").getSprite(199);
        combatFaintRight2_2 = AssetPool.getSpritesheet("characters").getSprite(201);
        combatFaintRight3_2 = AssetPool.getSpritesheet("characters").getSprite(203);
        combatFaintRight4_2 = AssetPool.getSpritesheet("characters").getSprite(205);
        combatFaintRight5_2 = AssetPool.getSpritesheet("characters").getSprite(207);
        combatFaintRight6_2 = AssetPool.getSpritesheet("characters").getSprite(209);

        sprite = idleDown;
        transform.scale.x = sprite.getNativeWidth();
        transform.scale.y = sprite.getNativeHeight();
    }
}
