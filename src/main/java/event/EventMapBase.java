package event;

import core.GamePanel;
import entity.EntityBase;
import entity.enumeration.EntityDirection;
import event.enumeration.EventType;

/**
 * This abstract class defines base logic for events on given maps.
 */
public abstract class EventMapBase {

    // FIELD
    protected final GamePanel gp;


    // CONSTRUCTOR
    /**
     * Constructs an EventMapBase instance.
     *
     * @param gp GamePanel instance
     */
    public EventMapBase(GamePanel gp) {
        this.gp = gp;
    }


    // METHODS
    /**
     * Checks if any event logic exists for the target object entity.
     *
     * @param dt time since last frame (seconds)
     * @param type whether an event is triggered by a click or step
     * @param target ID of the entity being interacted with
     * @return whether an event occurred (true) or not (false)
     */
    public abstract boolean objInteraction(double dt, EventType type, EntityBase target);


    /**
     * Checks if any event logic exists for the target NPC entity.
     *
     * @param dt time since last frame (seconds)
     * @param type whether an event is triggered by a click or step
     * @param target ID of the entity being interacted with
     * @return whether an event occurred (true) or not (false)
     */
    public abstract boolean npcInteraction(double dt, EventType type, EntityBase target);


    /**
     * Checks if any event logic exists for the target party member entity.
     *
     * @param dt time since last frame (seconds)
     * @param type whether an event is triggered by a click or step
     * @param target ID of the entity being interacted with
     * @return whether an event occurred (true) or not (false)
     */
    public abstract boolean partyInteraction(double dt, EventType type, EntityBase target);


    /**
     * Checks if any event logic exists for the tile in front of the player entity.
     *
     * @param dt time since last frame (seconds)
     * @param type whether an event is triggered by a click or step
     * @param col column of tile to check event on
     * @param row row of tile to check event on
     * @param direction current direction of the player entity
     * @return whether an event occurred (true) or not (false)
     */
    public abstract boolean tileInteraction(double dt, EventType type, int row, int col, EntityDirection direction);
}
