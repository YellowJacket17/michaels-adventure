package dialogue;

import core.GamePanel;
import miscellaneous.GameState;
import utility.exceptions.ConversationNotFoundException;

/**
 * This class handles the reading of conversations/dialogue.
 */
public class DialogueReader {

    // FIELDS
    private final GamePanel gp;

    /**
     * Variable to track which piece of dialogue (by index) in a conversation should be read next.
     * This is NOT the current piece of dialogue being read.
     */
    private int nextDialogueIndex;

    /**
     * The current conversation being read.
     */
    private Conversation currentConv;

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
     * Number of seconds that must pass before the player can proceed after a piece of dialogue has been read.
     * The value of this variable will be set to `progressionCountdown` whenever a pause in player progression is
     * needed.
     */
    private final double stagedProgressionCountdown = 0.083;

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
     * Variable to store the number of seconds that must pass before the player can proceed after a piece of dialogue has
     * been read.
     * On each frame where `progressDialogue()` is called, this variable is decremented by the frame time if greater
     * than zero.
     */
    private double progressionCountdown = 0;

    /**
     * Variable to track which line of the dialogue window text is currently being printed on.
     * In practice, this will always have a value of either 1 or 2.
     */
    private int printLine = 1;

    /**
     * Variable to store the current piece of dialogue being read.
     */
    private String dialogueText = "";

    /**
     * Variable to store the name of entity delivering the current piece of dialogue being read.
     */
    private String dialogueEntityName = "";

    /**
     * Variable to store the characters that have been printed on the first/top line from the current piece of dialogue.
     */
    private String dialoguePrint1 = "";

    /**
     * Variable to store the characters that have been printed on the second/bottom line from the current piece of
     * dialogue.
     */
    private String dialoguePrint2 = "";

    /**
     * Variable to store the total characters that have been printed to the screen from the current piece of dialogue.
     */
    private String dialoguePrintTotal = "";

    /**
     * Boolean to track whether a piece of dialogue is actively being printed to the screen or not.
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
    public DialogueReader(GamePanel gp) {
        this.gp = gp;
    }


    // METHODS
    /**
     * Updates dialogue reading by one frame.
     *
     * @param dt time since last frame (seconds)
     */
    public void update(double dt) {

        if (currentConv != null) {

            if (readingConversation) {

                progressDialogue(dt);                                                                                   // Progress the current piece of dialogue that's being read.
            } else {

                if ((!currentConv.isPlayerInputToEnd()) && (gp.getGameState() == GameState.DIALOGUE)) {                 // Only trigger this when the game state is set to dialogue; it prevents this from triggering repeatedly if the conversation is not set to null after the first time.

                    if (currentConv.getConvId() == -2) {                                                                // Check if this is a sub-menu message.

                        gp.getSubMenuS().handlePostSubMenuPrompt();
                    } else if (currentConv.getConvId() == -4) {                                                         // Check is this is a noninteractive combat message.

                        gp.getCombatM().progressCombat();
                } else {

                        gp.getInteractionM().handlePostConversation(currentConv.getConvId());
                    }
                }
            }
        }
    }


    /**
     * Stages and initiates a single message to be read to the dialogue screen.
     * The temporary conversation this message is placed in is given an ID of -1.
     *
     * @param message text to be read
     */
    public void initiateStandardMessage(String message) {

        initiateStandardMessage(message, false);
    }


    /**
     * Stages and initiates a single message to be read to the dialogue screen.
     * The temporary conversation this message is placed in is given an ID of -1.
     *
     * @param message text to be read
     * @param showArrow whether the dialogue arrow should be drawn on screen (true) or not (false)
     */
    public void initiateStandardMessage(String message, boolean showArrow) {

        stageMessage(message, -1);                                                                                      // Instantiate a temporary conversation with an ID of -1 to indicate that this is a message.
        alwaysShowArrow = showArrow;                                                                                    // Set whether the dialogue arrow should be shown each time user input is required (ture) or not (false).
    }


    /**
     * Stages and initiates a single message to be read before the appearance of a corresponding sub-menu.
     * The temporary conversation this message is placed in is given an ID of -2.
     *
     * @param message text to be read
     */
    public void initiateSubMenuMessage(String message) {

        stageMessage(message, -2);                                                                                      // Instantiate a temporary conversation with an ID of -2 to indicate that this is a sub-menu prompt.
        currentConv.setPlayerInputToEnd(false);                                                                         // This is so the sub-menu appears instantly once the dialogue has finished being read without player input.
        alwaysShowArrow = false;                                                                                        // Ensure that the dialogue arrow is not shown each time user input is required.
    }


    /**
     * Stages and initiates a single message to be read to the dialogue screen during combat.
     * The temporary conversation this message is placed in is given an ID of -3.
     *
     * @param message text to be read
     */
    public void initiateInteractiveCombatMessage(String message) {

        initiateInteractiveCombatMessage(message, true);
    }


    /**
     * Stages and initiates a single message to be read to the dialogue screen during combat.
     * The temporary conversation this message is placed in is given an ID of -3.
     *
     * @param message text to be read
     * @param showArrow whether the dialogue arrow should be drawn on screen (true) or not (false)
     */
    public void initiateInteractiveCombatMessage(String message, boolean showArrow) {

        stageMessage(message, -3);                                                                                      // Instantiate a temporary conversation with an ID of -3 to indicate that this is an interactive combat message.
        alwaysShowArrow = showArrow;                                                                                    // Set whether the dialogue arrow should be shown each time user input is required (ture) or not (false).
    }


    /**
     * Stages and initiates a single message to be read to the dialogue screen during combat.
     * This message cannot be manually progressed by the player.
     * The temporary conversation this message is placed in is given an ID of -4.
     *
     * @param message text to be read
     */
    public void initiateNoninteractiveCombatMessage(String message) {

        initiateNoninteractiveCombatMessage(message, false);
    }


    /**
     * Stages and initiates a single message to be read to the dialogue screen during combat.
     * This message cannot be manually progressed by the player.
     * The temporary conversation this message is placed in is given an ID of -4.
     *
     * @param message text to be read
     * @param showArrow whether the dialogue arrow should be drawn on screen (true) or not (false)
     */
    public void initiateNoninteractiveCombatMessage(String message, boolean showArrow) {

        stageMessage(message, -4);                                                                                      // Instantiate a temporary conversation with an ID of -4 to indicate that this is a noninteractive combat message.
        currentConv.setPlayerInputToEnd(false);                                                                         // This is so logic following the dialogue once it has finished being read is run immediately without player input.
        alwaysShowArrow = showArrow;                                                                                    // Set whether the dialogue arrow should be shown each time user input is required (ture) or not (false).
    }


    /**
     * Stages and initiates a new conversation.
     *
     * @param convId ID of the conversation to be read
     * @throws ConversationNotFoundException if a conversation with the passed ID fails to be found
     */
    public void initiateConversation(int convId) {

        alwaysShowArrow = false;                                                                                        // Ensure the dialogue arrow is not shown each time user input is required.

        currentConv = gp.getConv().get(convId);                                                                         // Retrieve the appropriate conversation and stage it.

        if (currentConv != null) {

            readingConversation = true;
            nextDialogueIndex = 0;

            stageDialogue(currentConv.getDialogueList().get(nextDialogueIndex));                                        // Stage the first piece of dialogue in the conversation to be read.
            nextDialogueIndex++;                                                                                        // Iterate the variable tracking which piece of dialogue to read next.
        } else {

            throw new ConversationNotFoundException("Conversation with ID "
                    + convId
                    + " is not loaded");
        }
    }


    /**
     * Progresses the staged conversation to either the next piece of dialogue or the next line in the staged piece of dialogue.
     */
    public void progressConversation() {

        if (readingConversation) {                                                                                      // A precaution to ensure that there is currently a staged conversation.

            if (dialoguePaused) {

                readingDialogue = true;                                                                                 // Resume reading the rest of the currently staged piece of dialogue.
                stagedPrintCountdown = defaultPrintCountdown;                                                           // Reset to the default number of seconds passed between each printed character.
            } else {

                stageDialogue(currentConv.getDialogueList().get(nextDialogueIndex));                                    // Stage the next piece of dialogue in the conversation to be read.
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

            if (dialoguePaused) {

                dialoguePrint1 = dialoguePrint2;                                                                        // If a new line of dialogue is needed after two have already been printed, replace the first line with the existing second.
                dialoguePrint2 = "";                                                                                    // Reset the second line of dialogue so that new characters can be printed to it.
                printLine = 2;                                                                                          // Set the active print line to line 2.
                dialoguePaused = false;                                                                                 // Resume reading the rest of the current piece of dialogue as normal now that the lines have been moved up (i.e., exit paused state).
            }

            if (printCountdown > 0) {

                printCountdown -= dt;                                                                                   // Decrease character print countdown by frame time each time a new frame is drawn.
            }

            if (progressionCountdown > 0) {

                progressionCountdown -= dt;                                                                             // Decrease progression countdown by frame time each time a new frame is drawn.
            } else {

                while ((printCountdown <= 0) && (progressionCountdown <= 0)) {

                    int i = dialoguePrintTotal.length();                                                                // Fetch the index of the next character to be printed.
                    i = checkNextCharacter(i);

                    if (printLine == 1) {

                        dialoguePrint1 += dialogueText.charAt(i);                                                       // Add the next dialogue character to be printed (line 1).
                    } else if (printLine == 2) {

                        dialoguePrint2 += dialogueText.charAt(i);                                                       // Add the next dialogue character to be printed (line 2).
                    }
                    dialoguePrintTotal += dialogueText.charAt(i);
                    printCountdown += stagedPrintCountdown;                                                             // Iterate `printCounter` to wait a number of seconds until the next character is printed; if negative after iteration, the next character must immediately be printed.

                    if (dialoguePrintTotal.length() == dialogueText.length()) {

                        progressionCountdown = stagedProgressionCountdown;                                              // Force the player to wait a number of seconds determined by `stagedProgressionCountdown` before progressing (prevents accidental skipping of dialogue).
                        printCountdown = stagedPrintCountdown;                                                          // Reset character print countdown for the next batch to be printed out.
                    }
                }
            }

            if ((dialoguePrintTotal.length() == dialogueText.length()) && (progressionCountdown <= 0)) {                // All dialogue must be printed to the screen and there must be no time buffer on progression.

                readingDialogue = false;
                gp.getDialogueA().reset();                                                                              // Reset the dialogue arrow to its default state (i.e., default position).

                if (nextDialogueIndex >= currentConv.getDialogueList().size()){

                    readingConversation = false;                                                                        // All dialogue in the conversation has finished being read.
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
        currentConv = null;
        readingConversation = false;
        stagedPrintCountdown = defaultPrintCountdown;
        progressionCountdown = 0;
        printCountdown = 0;
        printLine = 1;
        dialogueText = "";
        dialogueEntityName = "";
        dialoguePrint1 = "";
        dialoguePrint2 = "";
        dialoguePrintTotal = "";
        readingDialogue = false;
        dialoguePaused = false;
        alwaysShowArrow = false;
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
        currentConv = conversation;                                                                                     // Stage the temporary conversation as the current conversation being read.

        readingConversation = true;

        stageDialogue(currentConv.getDialogueList().get(0));                                                            // Stage the message to be read.

        nextDialogueIndex++;                                                                                            // Necessary to have so that the conversation will be flagged as complete once the single staged piece dialogue has been read.
    }


    /**
     * Stages a new piece of dialogue to be read out.
     *
     * @param dialogue
     */
    private void stageDialogue(Dialogue dialogue) {

        this.stagedPrintCountdown = defaultPrintCountdown;
        this.progressionCountdown = 0;
        this.printCountdown = 0;
        this.printLine = 1;
        this.dialogueText = dialogue.getText();
        this.dialogueEntityName = dialogue.getEntityName();
        this.dialoguePrint1 = "";
        this.dialoguePrint2 = "";
        this.dialoguePrintTotal = "";
        this.readingDialogue = true;
        this.dialoguePaused = false;
    }


    /**
     * Checks if the next text character in the staged piece of dialogue is a space.
     * If it is and the next word in the text exceeds the maximum allowed character length for a line of text printed
     * to the screen, then a new line is initiated.
     *
     * @param i character index in the staged dialogue to be checked
     * @return next character index to print
     */
    private int checkNextCharacter(int i) {

        if (dialogueText.charAt(i) == ' ') {                                                                            // Check if the next character to be printed is a space.

            StringBuilder nextWord = new StringBuilder();                                                               // Initialize a string for the next word in the dialogue.
            int nextWordCounter = i + 1;                                                                                // Skip over the space to get to the next word.
            boolean readingNextWord = true;
            String dialoguePrintTemp;                                                                                   // Initialize a temporary string to store the current line of dialogue that we're checking.

            if (printLine == 1) {

                dialoguePrintTemp = dialoguePrint1;
            } else {

                dialoguePrintTemp = dialoguePrint2;
            }

            while (readingNextWord) {

                if (dialogueText.charAt(nextWordCounter) == ' ') {

                    readingNextWord = false;                                                                            // We have reached the next space, signaling that we've reached the end of the next word.
                } else {

                    nextWord.append(dialogueText.charAt(nextWordCounter));
                    nextWordCounter++;

                    if (nextWordCounter >= dialogueText.length() ) {

                        readingNextWord = false;                                                                        // We have reached the end of the dialogue, signaling that we've reached the end of the next word.
                    }
                }
            }

            if ((dialoguePrintTemp + " " + nextWord).length() > (int)(3.4 * (GamePanel.NATIVE_SCREEN_WIDTH / GamePanel.NATIVE_TILE_SIZE))) {

                printLine++;                                                                                            // Start printing character on the next line of the dialogue window.

                if (printLine > 2) {

                    readingDialogue = false;
                    dialoguePaused = true;                                                                              // Pause the current piece of dialogue until the player progresses to the next line (i.e., enter paused state).
                    gp.getDialogueA().reset();                                                                          // Reset the dialogue arrow to its default state (i.e., default position).
                } else {

                    dialoguePrintTotal += ' ';                                                                          // Add a space to the total dialogue printed to compensate for skipping over the space when printing the next character.
                    i++;                                                                                                // Skip over the space when printing the next character.
                }
            }
        }
        return i;                                                                                                       // Return the index of the next character to be printed; this may have increased by 1 from the inputted value if a space (and hence a new word) was detected.
    }


    // GETTERS
    public Conversation getCurrentConv() {
        return currentConv;
    }

    public boolean isReadingConversation() {
        return readingConversation;
    }

    public String getDialogueText() {
        return dialogueText;
    }

    public String getDialogueEntityName() {
        return dialogueEntityName;
    }

    public String getDialoguePrint1() {
        return dialoguePrint1;
    }

    public String getDialoguePrint2() {
        return dialoguePrint2;
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
