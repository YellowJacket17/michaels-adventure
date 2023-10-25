package interaction;

import core.GamePanel;
import entity.EntityBase;
import entity.EntityDirection;

/**
 * This abstract class defines base logic for interactions on given maps.
 */
public abstract class InteractionMapBase {

    // FIELD
    protected final GamePanel gp;


    // CONSTRUCTOR
    /**
     * Constructs an InteractionMapBase instance.
     *
     * @param gp GamePanel instance
     */
    public InteractionMapBase(GamePanel gp) {
        this.gp = gp;
    }


    // METHODS
    /**
     * Checks if any interaction logic exists for the target object entity.
     *
     * @param type whether an interaction is triggered by a click or step
     * @param target ID of the entity being interacted with
     * @return whether an interaction occurred (true) or not (false)
     */
    public abstract boolean objInteraction(InteractionType type, EntityBase target);


    /**
     * Checks if any interaction logic exists for the target NPC entity.
     *
     * @param type whether an interaction is triggered by a click or step
     * @param target ID of the entity being interacted with
     * @return whether an interaction occurred (true) or not (false)
     */
    public abstract boolean npcInteraction(InteractionType type, EntityBase target);


    /**
     * Checks if any interaction logic exists for the target party member entity.
     *
     * @param type whether an interaction is triggered by a click or step
     * @param target ID of the entity being interacted with
     * @return whether an interaction occurred (true) or not (false)
     */
    public abstract boolean partyInteraction(InteractionType type, EntityBase target);


    /**
     * Checks if any interaction logic exists for the tile in front of the player entity.
     *
     * @param type whether an interaction is triggered by a click or step
     * @param col column of tile to check interaction on
     * @param row row of tile to check interaction on
     * @param direction current direction of the player entity
     * @return whether an interaction occurred (true) or not (false)
     */
    public abstract boolean tileInteraction(InteractionType type, int row, int col, EntityDirection direction);
}
