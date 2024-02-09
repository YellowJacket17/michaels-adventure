package combat.implementation.action;

import combat.ActionBase;
import combat.MoveBase;
import core.GamePanel;

/**
 * This class defines an action (use a combat move).
 */
public class Act_UseMove extends ActionBase {

    // FIELDS
    private final MoveBase move;                                                                                        // Move to be used.
    private final int sourceEntityId;                                                                                   // ID of the entity using the attack.
    private final int targetEntityId;                                                                                   // ID of the entity targeted by the attack.


    // CONSTRUCTOR
    public Act_UseMove(GamePanel gp, MoveBase move, int sourceEntityId, int targetEntityId) {
        super(gp);
        this.move = move;
        this.sourceEntityId = sourceEntityId;
        this.targetEntityId = targetEntityId;
    }


    // METHOD
    @Override
    public void run() {

        // TODO : Needs work to pass appropriate arguments to `runEffects()` method.
        //  Said method also needs to be modified to accept said arguments.
        move.runEffects();
    }


    // GETTERS
    public MoveBase getMove() {
        return move;
    }

    public int getSourceEntityId() {
        return sourceEntityId;
    }

    public int getTargetEntityId() {
        return targetEntityId;
    }
}
