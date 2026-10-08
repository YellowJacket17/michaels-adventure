package event.support;

import entity.enumeration.EntityDirection;

/**
 * This class holds data to be used for a staged fade up effect in support of the PartySupport class.
 */
public class StagedFadeUpEntityData {

    // FIELDS
    /**
     * Column that the target entity will occupy upon fade up.
     */
    private final int col;

    /**
     * Row that the target entity will occupy upon fade up.
     */
    private final int row;

    /**
     * Direction that the target entity will face upon fade up.
     */
    private final EntityDirection direction;

    /**
     * ID of the triggering entity whose fade down must complete before the target entity can begin fading up.
     */
    private final int triggeringEntityId;

    /**
     * Whether the target entity will be set to follow the player entity (true) or not (false) upon fade up.
     */
    private final boolean followPlayer;


    // CONSTRUCTOR
    /**
     * Constructs a StagedFadeUpEntityData instance.
     *
     * @param col column that the target entity will occupy upon fade up.
     * @param row row that the target entity will occupy upon fade up.
     * @param direction direction that the target entity will face upon fade up.
     * @param triggeringEntityId ID of the triggering entity whose fade down must complete before the target entity can
     *                           begin fading up
     * @param followPlayer whether the target entity will be set to follow the player entity (true) or not (false) upon
     *                     fade up.
     */
    public StagedFadeUpEntityData(int col, int row, EntityDirection direction,
                                  int triggeringEntityId, boolean followPlayer) {
        this.col = col;
        this.row = row;
        this.direction = direction;
        this.triggeringEntityId = triggeringEntityId;
        this.followPlayer = followPlayer;
    }


    // GETTERS
    public int getCol() {
        return col;
    }

    public boolean isFollowPlayer() {
        return followPlayer;
    }

    public int getTriggeringEntityId() {
        return triggeringEntityId;
    }

    public EntityDirection getDirection() {
        return direction;
    }

    public int getRow() {
        return row;
    }
}
