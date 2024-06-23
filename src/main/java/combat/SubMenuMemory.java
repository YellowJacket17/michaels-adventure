package combat;

import combat.enumeration.SubMenuType;

import java.util.List;

/**
 * This class stores data about a past sub-menu that was displayed in combat.
 */
public class SubMenuMemory {

    // FIELDS
    /**
     * List of stored sub-menu options that were displayed.
     */
    private final List<String> options;

    /**
     * Type of sub-menu that was actioned.
     */
    private final SubMenuType type;

    /**
     * Index of the sub-menu option that was selected.
     * A value of -1 means that no options has been selected.
     * This value must be between zero (inclusive) and the size of the list of options that was displayed (exclusive).
     */
    private int selectedOption = -1;

    /**
     * Boolean indicating whether the sub-menu in this memory was the last to appear during a combating entity's turn.
     * It is false by default.
     */
    private boolean lastOfTurn = false;


    // CONSTRUCTORS
    /**
     * Constructs a SubMenuMemory instance.
     *
     * @param options list of sub-menu options that were displayed
     * @param type type of sub-menu that was actioned
     */
    public SubMenuMemory(List<String> options, SubMenuType type) {
        this.options = options;
        this.type = type;
    }


    /**
     * Constructs a SubMenuMemory instance.
     *
     * @param options list of sub-menu options that were displayed
     * @param type type of sub-menu that was actioned
     * @param selectedOption index of sub-menu option that was selected
     */
    public SubMenuMemory(List<String> options, SubMenuType type, int selectedOption) {
        this.options = options;
        this.type = type;
        this.selectedOption = selectedOption;
    }


    // GETTERS
    public List<String> getOptions() {
        return options;
    }

    public SubMenuType getType() {
        return type;
    }

    public int getSelectedOption() {
        return selectedOption;
    }

    public boolean isLastOfTurn() {
        return lastOfTurn;
    }


    // SETTERS
    public void setSelectedOption(int selectedOption) {
        if ((selectedOption < 0) || (selectedOption >= options.size())) {
            this.selectedOption = -1;
        } else {
            this.selectedOption = selectedOption;
        }
    }

    public void setLastOfTurn(boolean lastOfTurn) {
        this.lastOfTurn = lastOfTurn;
    }
}
