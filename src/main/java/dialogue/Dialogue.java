package dialogue;

/**
 * This class defines dialogue, which is a piece of text paired with a speaking entity name.
 */
public class Dialogue {

    // FIELDS
    /**
     * Name of the character delivering this piece of dialogue.
     */
    private String entityName;

    /**
     * Text content of this piece of dialogue (i.e., the dialogue itself).
     */
    private String text;


    // CONSTRUCTOR
    /**
     * Constructs a new Dialogue instance.
     */
    public Dialogue() {}


    // GETTERS
    public String getEntityName() {
        return entityName;
    }

    public String getText() {
        return text;
    }


    // SETTERS
    public void setEntityName(String entityName) {
        this.entityName = entityName;
    }

    public void setText(String text) {
        this.text = text;
    }
}
