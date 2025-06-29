package event.enumeration;

/**
 * These enum defines types of stock step interactions.
 * These types of events are intended to occur whenever any tile/landmark of a particular type is interacted with.
 * An example is a grass landmark rustling each time an entity takes a step through it.
 * Another example is a puddle tile making a splashing sound effect each time an entity takes a step through it.
 */
public enum StockStepInteractionType {

    GRASS_RUSTLE,
    LEDGE_HOP
}
