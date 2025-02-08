package dialogue;

import core.GamePanel;
import core.enumeration.PrimaryGameState;
import utility.JsonParser;
import utility.LimitedLinkedHashMap;
import utility.exceptions.ConversationNotFoundException;

import java.util.HashMap;

/**
 * This class handles the reading of conversations/dialogue.
 */
public class DialogueReader {

    // FIELDS
    private final GamePanel gp;

    /**
     * Map to store all loaded conversations; conversation ID is the key, conversation is the value.
     */
    private final HashMap<Integer, Conversation> conv = new HashMap<>();

    /**
     * Variable to track which piece of dialogue (by index) in a conversation should be read next.
     * This is NOT the current piece of dialogue being read.
     */
    private int nextDialogueIndex;

    /**
     * The current conversation being read.
     */
    private Conversation activeConv;

    /**
     * Boolean to track whether all dialogue in a conversation has been read or not.
     */
    private boolean readingConversation = false;

    /**
     * Default number of seconds between each character of dialogue that prints on screen.
     */
    private final double defaultPrintCountdown = 0.016;

    /**
     * Number of seconds between each character of dialogue that prints on screen.
     * In other words, this variable controls the speed at which dialogue is read.
     * The value of this variable will be set to `printCountdown` whenever a pause between characters is needed.
     * This variable can be modified if a certain piece of dialogue is desired to be read slower or faster than normal.
     */
    private double stagedPrintCountdown = defaultPrintCountdown;

    /**
     * Variable to store the number of seconds that must pass before the next character in a piece of dialogue can be
     * printed to the screen.
     * On each frame where `progressDialogue()` is called, this variable is decremented by the frame time if greater
     * than zero.
     * It's used with `stagedPrintCountdown` to control the speed at which dialogue is read.
     * For example if this has a value of zero, then a new character will print every frame.
     * If this has a value of one, then a new character will print every other second.
     */
    private double printCountdown = 0;

    /**
     * Number of seconds that must pass before the player can proceed with interaction after a piece of dialogue has
     * been read.
     */
    private final double stagedProgressionCountdown = 0.1;

    /**
     * Boolean setting whether all visible text will be printed to the screen character by character or whether it will
     * all be printed at once.
     */
    private boolean printCharByChar = true;

    /**
     * Variable to track which line of the dialogue window text is currently being printed on.
     * This value will always be a key from the `dialoguePrint` map.
     */
    private int activePrintLine = 0;

    /**
     * Variable to store the current piece of dialogue being read.
     */
    private String activeDialogueText = "";

    /**
     * Variable to store the name of entity delivering the current piece of dialogue being read.
     */
    private String activeDialogueEntityName = "";

    /**
     * Maximum number of printed dialogue lines that can be displayed at a time.
     */
    private final int maxNumPrintLines = 2;

    /**
     * Map to store the characters that have been printed on each line of dialogue; line number is the key, string of
     * printed characters is the value.
     * The first line of printed dialogue's key will be 0, the second will be 1, etc.
     */
    private final LimitedLinkedHashMap<Integer, String> dialoguePrint;

    /**
     * Variable to store the total characters that have been printed to the screen from the current piece of dialogue.
     */
    private String dialoguePrintTotal = "";

    /**
     * Boolean to track whether a piece of dialogue is actively being printed to the screen or not.
     * This can be used to control reading player inputs for initiating/progressing dialogue, etc.
     */
    private boolean readingDialogue = false;

    /**
     * Boolean to track whether a piece of dialogue has been paused while being read.
     * This occurs if a piece of dialogue is too long to fit on just two lines on the screen.
     */
    private boolean dialoguePaused = false;

    /**
     * Boolean to set whether the dialogue arrow should be displayed each time user input is required to progress (true)
     * or only on paused pieces of dialogue (false).
     */
    private boolean alwaysShowArrow = false;


    // CONSTRUCTOR
    /**
     * Constructs a DialogueReader instance.
     *
     * @param gp GamePanel instance
     */
    public DialogueReader(GamePanel gp) {
        this.gp = gp;
        this.dialoguePrint = new LimitedLinkedHashMap<>(maxNumPrintLines);
        for (int i = 0; i < maxNumPrintLines; i++) {
            dialoguePrint.put(i, "");
        }
    }


    // METHODS
    /**
     * Updates dialogue reading by one frame.
     *
     * @param dt time since last frame (seconds)
     */
    public void update(double dt) {

        if (activeConv != null) {

            if (readingConversation) {

                progressDialogue(dt);                                                                                   // Progress the current piece of dialogue that's being read.
            } else {

                if ((!activeConv.isPlayerInputToEnd()) && (gp.getPrimaryGameState() == PrimaryGameState.DIALOGUE)) {    // Only trigger this when the primary game state is set to dialogue; it prevents this from triggering repeatedly if the conversation is not set to null after the first time.

                    if (activeConv.getConvId() == -2) {                                                                 // Check if this is a sub-menu message.

                        gp.getSubMenuS().handlePostSubMenuPrompt();
                    } else if (activeConv.getConvId() == -4) {                                                          // Check if this is a noninteractive combat message.

                        convertToPlaceholderMessage();                                                                  // Convert to placeholder message to ensure that `progressCombat()` is only triggered once by this message.
                        gp.getCombatM().progressCombat();
                } else if (activeConv.getConvId() != -5) {                                                              // Ensure the active conversation isn't a placeholder message.

                        gp.getEventM().handlePostConversation(activeConv.getConvId());
                    }
                }
            }
        }
    }


    /**
     * Loads any new conversation into memory, regardless of whether it's tied to the loaded map or not.
     * If the conversation is already loaded, nothing will happen.
     *
     * @param convId ID of conversation with dialogue to load
     */
    public void loadConversation(int convId) {

        JsonParser.loadConversationJson(gp, convId);
    }


    /**
     * Stages and initiates a single message to be read to the dialogue screen.
     * The temporary conversation this message is placed in is given an ID of -1.
     * The primary game state is set to dialogue.
     *
     * @param message text to be read
     * @param charByChar whether visible text will be printed character by character (true) or all at once (false)
     */
    public void initiateStandardMessage(String message, boolean charByChar) {

        initiateStandardMessage(message, charByChar, false);
    }


    /**
     * Stages and initiates a single message to be read to the dialogue screen.
     * The temporary conversation this message is placed in is given an ID of -1.
     * The primary game state is set to dialogue.
     *
     * @param message text to be read
     * @param charByChar whether visible text will be printed character by character (true) or all at once (false)
     * @param showArrow whether the dialogue arrow should be drawn on screen (true) or not (false), regardless of pause
     */
    public void initiateStandardMessage(String message, boolean charByChar, boolean showArrow) {

        gp.setPrimaryGameState(PrimaryGameState.DIALOGUE);
        stageMessage(message, -1);                                                                                      // Instantiate a temporary conversation with an ID of -1 to indicate that this is a message.
        printCharByChar = charByChar;                                                                                   // Set whether the visible text will be printed character by character (true) or all a once (false).
        alwaysShowArrow = showArrow;                                                                                    // Set whether the dialogue arrow should be shown each time user input is required (ture) or not (false).
    }


    /**
     * Stages and initiates a single message to be read before the appearance of a corresponding sub-menu.
     * The temporary conversation this message is placed in is given an ID of -2.
     * The primary game state is set to dialogue.
     *
     * @param message text to be read
     * @param charByChar whether visible text will be printed character by character (true) or all at once (false)
     */
    public void initiateSubMenuMessage(String message, boolean charByChar) {

        gp.setPrimaryGameState(PrimaryGameState.DIALOGUE);
        stageMessage(message, -2);                                                                                      // Instantiate a temporary conversation with an ID of -2 to indicate that this is a sub-menu prompt.
        activeConv.setPlayerInputToEnd(false);                                                                          // This is so the sub-menu appears instantly once the dialogue has finished being read without player input.
        printCharByChar = charByChar;                                                                                   // Set whether the visible text will be printed character by character (true) or all a once (false).
        alwaysShowArrow = false;                                                                                        // Ensure that the dialogue arrow is not shown each time user input is required.
    }


    /**
     * Stages and initiates a single message to be read to the dialogue screen during combat.
     * The temporary conversation this message is placed in is given an ID of -3.
     * The primary game state is set to dialogue.
     *
     * @param message text to be read
     * @param charByChar whether visible text will be printed character by character (true) or all at once (false)
     */
    public void initiateInteractiveCombatMessage(String message, boolean charByChar) {

        initiateInteractiveCombatMessage(message, charByChar, true);
    }


    /**
     * Stages and initiates a single message to be read to the dialogue screen during combat.
     * The temporary conversation this message is placed in is given an ID of -3.
     * The primary game state is set to dialogue.
     *
     * @param message text to be read
     * @param charByChar whether visible text will be printed character by character (true) or all at once (false)
     * @param showArrow whether the dialogue arrow should be drawn on screen (true) or not (false), regardless of pause
     */
    public void initiateInteractiveCombatMessage(String message, boolean charByChar, boolean showArrow) {

        gp.setPrimaryGameState(PrimaryGameState.DIALOGUE);
        stageMessage(message, -3);                                                                                      // Instantiate a temporary conversation with an ID of -3 to indicate that this is an interactive combat message.
        printCharByChar = charByChar;                                                                                   // Set whether the visible text will be printed character by character (true) or all a once (false).
        alwaysShowArrow = showArrow;                                                                                    // Set whether the dialogue arrow should be shown each time user input is required (ture) or not (false).
    }


    /**
     * Stages and initiates a single message to be read to the dialogue screen during combat.
     * This message cannot be manually progressed by the player.
     * The temporary conversation this message is placed in is given an ID of -4.
     * The primary game state is set to dialogue.
     *
     * @param message text to be read
     * @param charByChar whether visible text will be printed character by character (true) or all at once (false)
     */
    public void initiateNoninteractiveCombatMessage(String message, boolean charByChar) {

        initiateNoninteractiveCombatMessage(message, charByChar, false);
    }


    /**
     * Stages and initiates a single message to be read to the dialogue screen during combat.
     * This message cannot be manually progressed by the player.
     * The temporary conversation this message is placed in is given an ID of -4.
     * The primary game state is set to dialogue.
     *
     * @param message text to be read
     * @param charByChar whether visible text will be printed character by character (true) or all at once (false)
     * @param showArrow whether the dialogue arrow should be drawn on screen (true) or not (false), regardless of pause
     */
    public void initiateNoninteractiveCombatMessage(String message, boolean charByChar, boolean showArrow) {

        gp.setPrimaryGameState(PrimaryGameState.DIALOGUE);
        stageMessage(message, -4);                                                                                      // Instantiate a temporary conversation with an ID of -4 to indicate that this is a noninteractive combat message.
        activeConv.setPlayerInputToEnd(false);                                                                          // This is so logic following the dialogue once it has finished being read is run immediately without player input.
        printCharByChar = charByChar;                                                                                   // Set whether the visible text will be printed character by character (true) or all a once (false).
        alwaysShowArrow = showArrow;                                                                                    // Set whether the dialogue arrow should be shown each time user input is required (ture) or not (false).
    }


    /**
     * Stages and initiates a single message to be read to the dialogue screen.
     * This message cannot be manually progressed by the player, and no automatic logic or cleanup is run after the
     * message has been read; the message will remain displayed indefinitely until the conversation is cleaned up by
     * logic run elsewhere.
     * The temporary conversation this message is placed in is given an ID of -5.
     * The primary game state is set to dialogue.
     *
     * @param message text to be read
     * @param charByChar whether visible text will be printed character by character (true) or all at once (false)
     */
    public void initiatePlaceholderMessage(String message, boolean charByChar) {

        initiatePlaceholderMessage(message, charByChar, false);
    }


    /**
     * Stages and initiates a single message to be read to the dialogue screen.
     * This message cannot be manually progressed by the player, and no automatic logic or cleanup is run after the
     * message has been read; the message will remain displayed indefinitely until the conversation is cleaned up by
     * logic run elsewhere.
     * The temporary conversation this message is placed in is given an ID of -5.
     * The primary game state is set to dialogue.
     *
     * @param message text to be read
     * @param charByChar whether visible text will be printed character by character (true) or all at once (false)
     * @param showArrow whether the dialogue arrow should be drawn on screen (true) or not (false), regardless of pause
     */
    public void initiatePlaceholderMessage(String message, boolean charByChar, boolean showArrow) {

        gp.setPrimaryGameState(PrimaryGameState.DIALOGUE);
        stageMessage(message, -5);                                                                                      // Instantiate a temporary conversation with an ID of -5 to indicate that this is a placeholder message.
        activeConv.setPlayerInputToEnd(false);                                                                          // This is so logic following the dialogue once it has finished being read is run immediately without player input.
        printCharByChar = charByChar;                                                                                   // Set whether the visible text will be printed character by character (true) or all a once (false).
        alwaysShowArrow = showArrow;                                                                                    // Set whether the dialogue arrow should be shown each time user input is required (ture) or not (false).
    }


    /**
     * Converts the current piece of dialogue being read to a placeholder message.
     * The current piece of dialogue is converted exactly as currently displayed at the time of calling this method;
     * for example, if the dialogue is mid-print when this message is called, then only what has been printed thus far
     * will be converted to the placeholder message.
     * This message cannot be manually progressed by the player, and no automatic logic or cleanup is run after the
     * message has been read; the message will remain displayed indefinitely until the conversation is cleaned up by
     * logic run elsewhere.
     * The temporary conversation this message is placed in is given an ID of -5.
     * The primary game state is set to dialogue.
     */
    public void convertToPlaceholderMessage() {

        if (activeConv != null) {

            gp.setPrimaryGameState(PrimaryGameState.DIALOGUE);
            printCharByChar = false;                                                                                    // Instantly "freeze" the printed dialogue as-is at the time of calling this method.
            alwaysShowArrow = false;                                                                                    // Do not display the dialogue arrow.
            Conversation conversation = new Conversation(-5);                                                           // Instantiate a temporary conversation with the passed ID.
            Dialogue dialogue = new Dialogue();                                                                         // Instantiate a temporary piece of dialogue to add to the temporary conversation.
            dialogue.setText(dialoguePrintTotal);                                                                       // Set the dialogue text as the text that had already been read out at the time of calling this method.
            conversation.getDialogueList().add(dialogue);                                                               // Add the dialogue to the temporary conversation.
            activeConv = conversation;                                                                                  // Stage the temporary conversation as the current conversation being read.
            activeConv.setPlayerInputToEnd(false);                                                                      // The player cannot manually progress this dialogue.
            readingConversation = false;                                                                                // If reading, stop reading conversation to keep dialogue displayed exactly as it was at the time of calling this method.
            readingDialogue = false;                                                                                    // If reading, stop reading dialogue.
            dialoguePaused = false;                                                                                     // If paused, unpause dialogue.
            nextDialogueIndex = 1;                                                                                      // Housekeeping; only one piece of dialogue in this conversation, so set next dialogue index as if already iterated.
        }
    }


    /**
     * Stages and initiates a new conversation.
     * The primary game state is set to dialogue.
     *
     * @param convId ID of the conversation to be read
     * @throws ConversationNotFoundException if a conversation with the passed ID fails to be found
     */
    public void initiateConversation(int convId) {

        gp.setPrimaryGameState(PrimaryGameState.DIALOGUE);
        alwaysShowArrow = false;                                                                                        // Ensure the dialogue arrow is not shown each time user input is required.
        activeConv = conv.get(convId);                                                                                  // Retrieve the appropriate conversation and stage it.

        if (activeConv != null) {

            readingConversation = true;
            nextDialogueIndex = 0;

            stageDialogue(activeConv.getDialogueList().get(nextDialogueIndex));                                         // Stage the first piece of dialogue in the conversation to be read.
            nextDialogueIndex++;                                                                                        // Iterate the variable tracking which piece of dialogue to read next.
        } else {

            throw new ConversationNotFoundException("Conversation with ID '"
                    + convId
                    + "' is not loaded");
        }
    }


    /**
     * Progresses the staged conversation to either the next piece of dialogue or the next line in the staged piece of dialogue.
     */
    public void progressConversation() {

        if (readingConversation) {                                                                                      // A precaution to ensure that there is currently a staged conversation.

            if (dialoguePaused) {

                for (int key = 0; key < dialoguePrint.keySet().size() - 1; key++) {                                     // Shift each print line "down" a level, setting the topmost line as an empty string to be freshly written to.

                    dialoguePrint.replace(key, dialoguePrint.get(key + 1));
                }
                dialoguePrint.replace(maxNumPrintLines - 1, "");
                activePrintLine = maxNumPrintLines - 1;                                                                 // Set the active print line to the topmost (maximum) line.
                printCountdown = 0;                                                                                     // Zero character print countdown for the next line to be printed out.
                readingDialogue = true;                                                                                 // Resume reading the rest of the currently staged piece of dialogue.
                dialoguePaused = false;
            } else {

                stageDialogue(activeConv.getDialogueList().get(nextDialogueIndex));                                     // Stage the next piece of dialogue in the conversation to be read.
                nextDialogueIndex++;                                                                                    // Iterate the variable tracking which piece of dialogue to read next.
            }
        }
    }


    /**
     * Stages text characters from the staged piece of dialogue to be drawn by the UI class.
     * Characters are added one at a time to give a letter-by-letter printing effect.
     *
     * @param dt time since last frame (seconds)
     */
    public void progressDialogue(double dt) {

        if (readingDialogue) {

            if (printCountdown > 0) {

                printCountdown -= dt;                                                                                   // Decrease character print countdown by frame time each time a new frame is drawn.
            }
            String nextWord;

            while ((printCountdown <= 0) && readingDialogue) {

                int i = dialoguePrintTotal.length();                                                                    // Fetch the index of the next character to be printed.
                nextWord = checkNextWord(i);

                if (!nextWord.equals("")
                        && gp.getUi().calculateStringScreenLength(
                            (dialoguePrint.get(activePrintLine) + " " + nextWord),
                            gp.getUi().getStandardFontScale(),
                            gp.getUi().getStandardNormalFont())
                        > (1 - gp.getCamera().worldWidthToScreenWidth(46))) {                                           // 46 is 2x the value of `mainTextScreenLeftPadding` in the `renderDialogueScreen()` method in the UserInterface class.

                    activePrintLine++;                                                                                  // Start printing character on the next line, if next line exists.
                    dialoguePrintTotal += ' ';                                                                          // Add a space to the total dialogue printed to compensate for skipping over the space when printing the next character on the next line.
                    i++;                                                                                                // Skip over the space when printing the next character.
                }

                if (activePrintLine < maxNumPrintLines) {

                    dialoguePrint.replace(
                            activePrintLine, dialoguePrint.get(activePrintLine) + activeDialogueText.charAt(i));        // Add the next dialogue character to be printed.
                    dialoguePrintTotal += activeDialogueText.charAt(i);
                } else {                                                                                                // Dialogue is long enough to spill beyond maximum number of allowable printed lines.

                    readingDialogue = false;
                    dialoguePaused = true;                                                                              // Pause the current piece of dialogue until the player progresses to the next line (i.e., enter paused state).
                    gp.getEntityM().getPlayer().setInteractionCountdown(stagedProgressionCountdown);                    // Force the player to wait before further progressing the dialogue screen or any other interaction (prevents instantly progressing next menu action, for example).
                }

                if (dialoguePrintTotal.length() == activeDialogueText.length()) {                                       // If the entire piece of dialogue has been read, stop printing characters.

                    readingDialogue = false;
                    gp.getEntityM().getPlayer().setInteractionCountdown(stagedProgressionCountdown);                    // Force the player to wait before further progressing the dialogue screen or any other interaction (prevents instantly progressing next menu action, for example).

                    if (nextDialogueIndex >= activeConv.getDialogueList().size()){

                        readingConversation = false;                                                                    // All dialogue in the conversation has finished being read.
                    }
                } else if (printCharByChar) {

                    printCountdown += stagedPrintCountdown;                                                             // Iterate `printCountdown` to wait a number of seconds until the next character is printed; if negative after iteration, the next character must immediately be printed.
                }
            }
        }
    }


    /**
     * Resets DialogueReader back to its default state.
     * Intended to be called to clean up after a conversation has finished being read.
     */
    public void reset() {

        nextDialogueIndex = 0;
        activeConv = null;
        readingConversation = false;
        stagedPrintCountdown = defaultPrintCountdown;
        printCountdown = 0;
        printCharByChar = true;
        activePrintLine = 0;
        activeDialogueText = "";
        activeDialogueEntityName = "";
        dialoguePrintTotal = "";
        readingDialogue = false;
        dialoguePaused = false;
        alwaysShowArrow = false;
        for (int key : dialoguePrint.keySet()) {
            dialoguePrint.replace(key, "");
        }
    }


    /**
     * Stages and initiates a single message to be read to the dialogue screen.
     *
     * @param message text to be read
     * @param assignedId ID to be assigned to the temporary conversation containing the message
     */
    private void stageMessage(String message, int assignedId) {

        Conversation conversation = new Conversation(assignedId);                                                       // Instantiate a temporary conversation with the passed ID.
        Dialogue dialogue = new Dialogue();                                                                             // Instantiate a temporary piece of dialogue to add to the temporary conversation.
        dialogue.setText(message);                                                                                      // Set the dialogue text as the message to be read.
        conversation.getDialogueList().add(dialogue);                                                                   // Add the dialogue to the temporary conversation.
        activeConv = conversation;                                                                                      // Stage the temporary conversation as the current conversation being read.

        readingConversation = true;

        stageDialogue(activeConv.getDialogueList().get(0));                                                             // Stage the message to be read.

        nextDialogueIndex++;                                                                                            // Necessary to have so that the conversation will be flagged as complete once the single staged piece dialogue has been read.
    }


    /**
     * Stages a new piece of dialogue to be read out.
     *
     * @param dialogue
     */
    private void stageDialogue(Dialogue dialogue) {

        this.stagedPrintCountdown = defaultPrintCountdown;
        this.printCountdown = 0;
        this.activePrintLine = 0;
        this.activeDialogueText = dialogue.getText();
        this.activeDialogueEntityName = dialogue.getEntityName();
        this.dialoguePrintTotal = "";
        this.readingDialogue = true;
        this.dialoguePaused = false;
        for (int key : dialoguePrint.keySet()) {
            dialoguePrint.replace(key, "");
        }
    }


    /**
     * Checks to see if the index passed as argument represents a space character in the staged piece of dialogue.
     * If it does, then the next word following the index / space character will be extracted and returned.
     * Words are assumed to be surrounded by a single space character both in front and behind, or in the case of the
     * last word in a piece, a single space character in front.
     * If the index passed as argument does not represent a space character, then an empty string will be returned.
     * If the index passed as argument represents a space character that is immediately followed by another space
     * character, then a string containing a single space character will be returned.
     *
     * @param i character index in the staged piece of dialogue to be checked
     * @return next word, if applicable
     */
    private String checkNextWord(int i) {

        if ((activeDialogueText.charAt(i) == ' ') && (i < activeDialogueText.length() - 1)) {                           // Check if the next character to be printed is a space AND is not the last character in the piece of dialogue.

            StringBuilder nextWord = new StringBuilder();                                                               // Initialize a string for the next word in the dialogue.
            int nextCharacterCounter = i + 1;                                                                           // Skip over the space to get to the next word.
            boolean readingNextWord = true;

            while (readingNextWord) {

                if (nextCharacterCounter >= (activeDialogueText.length())                                               // If true, have reached the end of the piece of dialogue.
                        || (activeDialogueText.charAt(nextCharacterCounter) == ' ')) {                                  // If true, have reached the next space, signaling that the end of the word being read has been reached.

                    readingNextWord = false;
                } else {

                    nextWord.append(activeDialogueText.charAt(nextCharacterCounter));
                    nextCharacterCounter++;
                }
            }

            if (nextWord.isEmpty()) {                                                                                   // If true, there must have been two adjacent spaces; so, make `nextWord` a single space.

                nextWord.append(' ');
            }
            return nextWord.toString();
        }
        return "";
    }


    // GETTERS
    public HashMap<Integer, Conversation> getConv() {
        return conv;
    }

    public Conversation getActiveConv() {
        return activeConv;
    }

    public boolean isReadingConversation() {
        return readingConversation;
    }

    public String getActiveDialogueEntityName() {
        return activeDialogueEntityName;
    }

    public int getMaxNumPrintLines() {
        return maxNumPrintLines;
    }

    public String getDialoguePrint(int key) {
        return dialoguePrint.get(key);
    }

    public boolean isReadingDialogue() {
        return readingDialogue;
    }

    public boolean isDialoguePaused() {
        return dialoguePaused;
    }

    public boolean isAlwaysShowArrow() {
        return alwaysShowArrow;
    }


    // SETTERS
    public void setStagedPrintCountdown(double stagedPrintCountdown) {
        this.stagedPrintCountdown = stagedPrintCountdown;
    }
}
