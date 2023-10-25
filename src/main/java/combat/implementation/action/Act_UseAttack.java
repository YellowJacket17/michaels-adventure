package combat.implementation.action;

import combat.ActionBase;
import combat.AttackBase;
import core.GamePanel;

/**
 * This class defines an action (use an attack).
 */
public class Act_UseAttack extends ActionBase {

    // FIELDS
    private final AttackBase attack;                                                                                    // Attack to be used.
    private final int sourceEntity;                                                                                     // ID of the entity using the attack.
    private final int targetEntity;                                                                                     // ID of the entity targeted by the attack.


    // CONSTRUCTOR
    public Act_UseAttack(GamePanel gp, AttackBase attack, int sourceEntity, int targetEntity) {
        super(gp);
        this.attack = attack;
        this.sourceEntity = sourceEntity;
        this.targetEntity = targetEntity;
    }


    // METHOD
    @Override
    public void run() {

        // TODO : Add logic to use attack here!
    }


    // GETTERS
    public AttackBase getAttack() {
        return attack;
    }

    public int getSourceEntity() {
        return sourceEntity;
    }

    public int getTargetEntity() {
        return targetEntity;
    }
}
