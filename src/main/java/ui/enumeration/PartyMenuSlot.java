package ui.enumeration;

/**
 * This enum defines possible party menu slots that can be rendered at once.
 * Each enumeration has a corresponding integer value assigned to it.
 */
public enum PartyMenuSlot {
    SLOT_0(0),
    SLOT_1(1),
    SLOT_2(2);

    private final int value;

    private PartyMenuSlot(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
