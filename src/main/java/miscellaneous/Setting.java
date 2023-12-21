package miscellaneous;

import utility.LimitedArrayList;

/**
 * This class defines a setting in the settings menu section of the in-game menu.
 */
public class Setting {

    // FIELDS
    /**
     * Setting field label.
     */
    private final String label;

    /**
     * Setting field description.
     */
    private final String description;

    /**
     * Setting field options.
     */
    private final LimitedArrayList<String> options = new LimitedArrayList<>(10);

    /**
     * Active option index.
     */
    private int activeOption;


    // CONSTRUCTOR
    /**
     * Constructs a Setting instance.
     *
     * @param label setting field label
     * @param description setting field description
     */
    public Setting(String label, String description) {
        this.label = label;
        this.description = description;
    }


    // METHODS
    /**
     * Adds a new options to the list of available options for this setting.
     *
     * @param option item to add
     */
    public void addOption(String option) {

        options.add(option);
    }


    /**
     * Removes the specified option from the list of available options in this setting.
     * If no match is found, nothing will happen.
     *
     * @param option item to remove
     */
    public void removeOption(String option) {

        for (int i = 0; i < options.size(); i++) {

            if (options.get(i).equals(option)) {

                options.remove(i);
            }
        }
    }


    /**
     * Removes all options from the list of available options in this setting.
     */
    public void removeAllOptions() {

        for (int i = 0; i < options.size(); i++) {

            options.remove(i);
        }
    }


    // GETTERS
    public String getLabel() {
        return label;
    }

    public String getDescription() {
        return description;
    }

    public int getOptionsSize() {
        return options.size();
    }

    public String getOption(int option) {
        if ((option < options.size()) && (option >= 0)) {
            return options.get(option);
        }
        return null;
    }

    public int getActiveOption() {
        return activeOption;
    }


    // SETTER
    public void setActiveOption(int activeOption) {

        if ((activeOption < options.size()) && (activeOption >= 0)) {
            this.activeOption = activeOption;
        }
    }
}
