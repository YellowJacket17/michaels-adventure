package dialogue;

import java.util.ArrayList;

/**
 * This class defines a conversation, which is a collection of pieces of dialogue to be read in sequence.
 */
public class Conversation {

    // FIELDS
    /**
     * Unique conversation ID.
     * To be clear, this ID is unique for each Conversation instance across the entire game.
     */
    private final int convId;

    /**
     * Conversation name (means of identifying it).
     */
    private String name;

    /**
     * Boolean determining whether player input is required to trigger post-conversation logic after the last piece of
     * dialogue has finished being read; input required by default.
     */
    private boolean playerInputToEnd = true;

    /**
     * List of all dialogue attached to this conversation.
     * Dialogue is stored in the order in which it should be read (first at index 0).
     */
    private final ArrayList<Dialogue> dialogueList = new ArrayList<>();


    // CONSTRUCTOR
    /**
     * Constructs a conversation instance.
     *
     * @param convId unique conversation ID
     */
    public Conversation(int convId) {
        this.convId = convId;
    }


    // GETTERS
    public int getConvId() {
        return convId;
    }

    public String getName() {
        return name;
    }

    public boolean isPlayerInputToEnd() {
        return playerInputToEnd;
    }

    public ArrayList<Dialogue> getDialogueList() {
        return dialogueList;
    }


    // SETTERS
    public void setName(String name) {
        this.name = name;
    }

    public void setPlayerInputToEnd(boolean playerInputToEnd) {
        this.playerInputToEnd = playerInputToEnd;
    }
}
