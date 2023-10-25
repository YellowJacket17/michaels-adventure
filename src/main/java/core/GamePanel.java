package core;

import ai.PathFinder;
import animation.AnimationManager;
import combat.CombatManager;
import cutscene.CutsceneManager;
import dialogue.Conversation;
import dialogue.DialogueArrow;
import dialogue.DialogueReader;
import interaction.support.SubMenuSupport;
import interaction.support.WarpSupport;
import submenu.SelectionArrow;
import entity.EntityBase;
import entity.implementation.player.Player;
import environment.EnvironmentManager;
import interaction.support.CameraSupport;
import icon.EntityIconManager;
import icon.MenuIconManager;
import interaction.InteractionManager;
import landmark.LandmarkBase;
import landmark.LandmarkManager;
import map.Map;
import submenu.SubMenuHandler;
import tile.TileManager;
import utility.JsonParser;
import utility.UtilityTool;
import utility.LimitedLinkedHashMap;

import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * Primary class for the game that houses the main game loop and essential configurations.
 */
public class GamePanel extends JPanel implements Runnable {

    // SCREEN SETTINGS
    /**
     * Tile size in pixels (32x32).
     * A tile is taken to be a square (equal width and height).
     * IT IS STRONGLY RECOMMENDED TO NOT CHANGE, WILL BREAK GAME!
     */
    private final int originalTileSize = 32;

    /**
    * Scale of the rendered window compared to native size.
     */
    private final int scale = 1;

    /**
     * Actual tile size on the rendered window (i.e., game screen).
     */
    private final int tileSize = originalTileSize * scale;

    /**
     * Tiles per column on game screen.
     */
    private final int maxScreenCol = 24;

    /**
     * Tiles per row on game screen.
     */
    private final int maxScreenRow = 14;

    /**
     * Screen width as determined by tile size and number of columns.
     */
    private final int screenWidth = tileSize * maxScreenCol;

    /**
     * Screen height as determined by tile size and number of rows.
     */
    private final int screenHeight = tileSize * maxScreenRow;


     // WORLD SETTINGS
    /**
     * Maximum number of tiles allowed in a map column.
     */
    private final int maxWorldCol = 100;

    /**
     *  Maximum number of tiles allowed in a map row.
     */
    private final int maxWorldRow = 100;

    /**
     * Current loaded map.
     */
    private Map loadedMap;


    // FRAME RATE
    /**
     * Target frame rate for the game; GAME SPEED IS TIED TO FRAME RATE (PROGRAMMED AT 60)!
     */
    private final int FPS = 60;

    /**
     * Variable to track the actual number of frames drawn per second as they're drawn.
     */
    private int drawCount;

    /**
     * Variable to track the actual frame rate (updated every second).
     */
    private int fpsTracker;


    // SYSTEM
    private Thread gameThread;
    private boolean running = false;
    private final JsonParser jsonP = new JsonParser(this);
    private final KeyHandler keyH = new KeyHandler();
    private final SubMenuHandler subMenuH = new SubMenuHandler(this);
    private final CollisionInspector collisionI = new CollisionInspector(this);
    private final DialogueReader dialogueR = new DialogueReader(this);
    private final TileManager tileM = new TileManager(this);
    private final LandmarkManager landmarkM = new LandmarkManager(this);
    private final MenuIconManager iconM = new MenuIconManager(this);
    private final EntityIconManager entityIconM = new EntityIconManager(this);
    private final EnvironmentManager environmentM = new EnvironmentManager(this);
    private final CutsceneManager cutsceneM = new CutsceneManager(this);
    private final AnimationManager animationM = new AnimationManager();
    private final CombatManager combatM = new CombatManager(this);
    private final InteractionManager interactionM = new InteractionManager(this);
    private final CameraSupport cameraS = new CameraSupport(this);
    private final WarpSupport warpS = new WarpSupport(this);
    private final SubMenuSupport subMenuS = new SubMenuSupport(this);
    private final PathFinder pathF = new PathFinder(this);
    private final Sound audio = new Sound();
    private final UI ui = new UI(this);


    // ENTITY (PLAYER, NPC, OBJECT)
    /**
     * Player entity.
     */
    private final Player player = new Player(this, keyH);

    /**
     * Map to store objects loaded into the game; entity ID is the key, entity is the value.
     */
    private final LimitedLinkedHashMap<Integer, EntityBase> obj = new LimitedLinkedHashMap<>(50);

    /**
     * Map to store NPCs loaded into the game; entity ID is the key, entity is the value.
     */
    private final LimitedLinkedHashMap<Integer, EntityBase> npc = new LimitedLinkedHashMap<>(50);

    /**
     * Map to store party members loaded into the game; entity ID is the key, entity is the value.
     */
    private final LimitedLinkedHashMap<Integer, EntityBase> party = new LimitedLinkedHashMap<>(2);

    /**
     * Map to store entities loaded into the game but not currently available on the loaded map; entity ID is the key,
     * entity is the value.
     */
    private final LimitedLinkedHashMap<Integer, EntityBase> standby = new LimitedLinkedHashMap<>(50);

    /**
     * List to temporarily store all loaded entities (player, NPCs, objects, party members) when drawing.
     */
    private final ArrayList<EntityBase> entityList = new ArrayList<>();

    /**
     * Set to store the IDs of all entities currently in a conversation.
     */
    private final HashSet<Integer> conversingEntities = new HashSet<>();

    /**
     * Set to store the IDs of all entities currently in combat.
     */
    private final HashSet<Integer> combatingEntities = new HashSet<>();

    /**
     * Set to store the IDs of all entities that should no longer be loaded on a map (ex. picked up an object, causing
     * is to disappear from the map).
     */
    private final HashSet<Integer> removedEntities = new HashSet<>();


    // DIALOGUE
    /**
     * Map to store all loaded conversations; conversation ID is the key, conversation is the value.
     */
    private final HashMap<Integer, Conversation> conv = new HashMap<>();

    /**
     * Arrow that appears when the player is required to progress a piece of dialogue.
     */
    private final DialogueArrow dialogueA = new DialogueArrow(this);


    // SUB-MENU
    /**
     * Arrow that appears when the player is required to make a sub-menu selection.
     */
    private final SelectionArrow selectionA = new SelectionArrow(this);


    // GAME STATE
    /**
     * Variable to store which state the game is currently in (the game state controls what is updated each frame).
     */
    private GameState gameState;

    /**
     * Boolean to set whether party members are drawn in the world or not.
     */
    private boolean partyVisible = true;

    /**
     * Boolean to set whether the game is in combat mode or not.
     */
    private boolean combatActive = false;

    /**
     * Boolean to set whether debug mode is active or not.
     */
    private boolean debugActive = false;


    // TRANSITION
    /**
     * Variable to store the current transition type being performed (null if none).
     */
    private TransitionType activeTransitionType;

    /**
     * Variable to store phase of the current transition being performed
     * (0 = default state, 1 = fading to black, 2 = loading while black, 3 = fading from black).
     */
    private int activeTransitionPhase;


    // CONSTRUCTOR
    /**
     * Constructs a GamePanel instance.
     */
    public GamePanel() {
        this.setPreferredSize(new Dimension(screenWidth, screenHeight));
        this.setBackground(Color.black);
        this.setDoubleBuffered(true);                                                                                   // Enabling this can improve the game's rendering performance.
        this.addKeyListener(keyH);
        this.setFocusable(true);                                                                                        // With this, GamePanel can be "focused" to receive key input.
    }


    // METHODS
    /**
     * Runs setup processes for the game (to be called before the main game loop starts).
     */
    public void setupGame() {

        // Fade into the game.
        gameState = GameState.TRANSITION;
        activeTransitionType = TransitionType.WARP;
        activeTransitionPhase = 2;

        // Load map along with associated entities and dialogue.
        loadMap(1);

        // Other setup.
        environmentM.setup();                                                                                           // Set environment effects; note that the setup for lighting is somewhat computationally intensive and may lag behind subsequent processes.
        playMusic(0);
    }


    /**
     * Starts (i.e., instantiates) the main game thread.
     */
    public void startGameThread() {

        gameThread = new Thread(this);
        gameThread.start();
    }


    /**
     * Starts the main game loop, which is the core of the game.
     */
    @Override
    public void run() {

        // Initializations for frame rate.
        double drawInterval = 1000000000 / FPS;                                                                         // 1 second (i.e., 1 billion nanoseconds) divided by our FPS; this tells the program how frequently to redraw the screen.
        double delta = 0;
        long lastTime = System.nanoTime();
        long currentTime;

        // Initialization for FPS display counter.
        long timer = 0;

        // Set main game loop to a running state.
        running = true;

        // Main game loop.
        while (running) {

            currentTime = System.nanoTime();
            delta += (currentTime - lastTime) / drawInterval;
            timer += (currentTime - lastTime);
            lastTime = currentTime;

            if (delta >= 1) {                                                                                           // Determine if enough time has passed to redraw the screen.

                // 1. UPDATE: Update information such as character positions.
                update();                                                                                               // Call `update()` method.

                // 2. DRAW: Draw the screen with the updated information.
                repaint();                                                                                              // Call `paintComponent()` method.

                // 3. DECREMENT: Decrement time passed until the next redraw.
                delta--;                                                                                                // Reset `delta`.
            }

            if (timer >= 1000000000) {                                                                                  // Every 1 second, the number of times the screen was actually redrawn will be recorded to compare to the target FPS.
                fpsTracker = drawCount;                                                                                 // Update to frame rate tracker to how many frames were drawn over the last second.
                drawCount = 0;                                                                                          // Reset the draw count.
                timer = 0;                                                                                              // Reset the timer.
            }
        }
    }


    /**
     * Draws the next frame of the game on the game screen.
     *
     * @param g Graphics instance
     */
    @Override
    public void paintComponent(Graphics g) {                                                                            // Graphics is a class that has many functions to draw objects on the screen (imagine it as a pencil or paint brush).

        try {

            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D)g;                                                                              // The Graphics2D class extends the Graphics class to provide more sophisticated control over geometry, coordinate transformations, color management, and text layout.

            // Tile.
            tileM.draw(g2);                                                                                             // Draw tile sprite as defined in the TileManager class.

            // Entity and landmark.
            for (EntityBase entity : obj.values()) {                                                                    // Add all objects in the current map to the list of entities.

                if (entity != null) {

                    entityList.add(entity);
                }
            }

            for (EntityBase entity : npc.values()) {                                                                    // Add all NPCs in the current map to the list of entities.

                if (entity != null) {

                    entityList.add(entity);
                }
            }

            if (partyVisible) {

                Set<Integer> keySet = party.keySet();
                Integer[] keyArray = keySet.toArray(new Integer[keySet.size()]);

                for (int i = (keySet.size() - 1); i >= 0; i--) {                                                        // Add all party members in the current map to the list of entities; iterates backwards.

                    if (party.get(keyArray[i]) != null) {

                        entityList.add(party.get(keyArray[i]));
                    }
                }
            }

            entityList.add(player);                                                                                     // Add player to the list of all entities.

            ArrayList<LandmarkBase> landmarkList;

            if (loadedMap != null) {

                landmarkList = loadedMap.getMapLandmarks();                                                             // Get the list of landmarks on the loaded map.
            } else {

                landmarkList = new ArrayList<>();                                                                       // Fail-safe to have empty landmark array if no map is loaded.
            }

            for (int row = 0; row < maxWorldRow; row++) {                                                               // Draw the entities and landmarks row-by-row, starting at the top.

                for (LandmarkBase landmark : landmarkList) {                                                            // Draw all landmarks in the current row.

                    if ((landmark.getRow() >= row)
                            && (landmark.getRow() < (row + 1))) {

                        landmark.draw(g2);
                    }
                }

                for (EntityBase entity : entityList) {                                                                  // Draw all entities in the current row.

                    if ((entity.getWorldY() >= (row * tileSize))
                            && (entity.getWorldY() < ((row + 1) * tileSize))) {

                        entity.draw(g2);
                    }
                }
            }

            // Environment.
            environmentM.draw(g2);                                                                                      // NOTE: Drawing environmental effects is a somewhat intensive process computationally.

            // Cutscene.
            cutsceneM.draw(g2);

            // UI.
            ui.draw(g2);                                                                                                // Draw UI after everything else so that it appears on the top layer.

            // Cleanup.
            entityList.clear();                                                                                         // Reset the list of all entities by emptying it.
            g2.dispose();                                                                                               // Dispose of this graphics context and release any system resources that it is using.
            drawCount++;                                                                                                // Add 1 to the variable tracking how many times we've redrawn the screen in the past 1 second.

        } catch (Exception e) {

            running = false;
            UtilityTool.logError("An unhandled exception has occurred.");
            UtilityTool.logStackTrace(e);
            UtilityTool.writeCrashLog();
        }
    }


    /**
     * Plays a music track.
     *
     * @param trackIndex music track index in the `musicURL` array of the Sound class
     */
    public void playMusic(int trackIndex) {


        if (audio.isMusicFading()) {                                                                                    // If a fade is currently in progress, this track will be staged as the next to play (does nothing if the fade is to completely stop music from playing instead of transitioning between tracks).

            audio.setStagedMusicOverwrite(trackIndex);                                                                  // Force this new track to be played after the current fade is complete.

        } else if (!audio.isAdditionalThreadRunning()) {                                                                // Only run if there isn't already an additional thread running.

            CompletableFuture.runAsync(() -> {                                                                          // Music file is set and played asynchronously to prevent program stutter.
                audio.setAdditionalThreadRunning(true);                                                                 // Indicate that an additional thread has been created; ensure another additional can't be created until this one is complete.
                if ((!audio.isMusicClipNull()) && (audio.isMusicPlaying())) {                                           // Only stop if a music track is currently staged and playing.
                    audio.stopMusicTrack(false);
                }
                audio.setMusicTrackFile(trackIndex);                                                                    // Only stage the track if we haven't overwritten the original one meant to be staged from a current fade.
                if ((!audio.isMusicClipNull()) && (!audio.isStagingMusicFailed())) {                                    // Only attempt to play and loop a track if there is one currently staged.
                    audio.playMusicTrack();
                    audio.loopMusicTrack();
                }
                if (audio.isStagingMusicFailed()) {
                    audio.setStagingMusicFailed(false);                                                                 // We no longer need to worry about if that last staging attempt was successful or not.
                }
                audio.setAdditionalThreadRunning(false);                                                                // Indicate that the additional thread is being closed.
            });
        }
    }


    /**
     * Stops the current music track from playing.
     *
     * @param fade determines whether the music track instantly stops (false) or fades out (true)
     */
    public void stopMusic(boolean fade) {

        if ((audio.isMusicPlaying()) && (!audio.isMusicFading())) {                                                     // Only stop if there is currently something playing AND if fade is not currently in effect.

            if (fade) {

                if (!audio.isAdditionalThreadRunning()) {

                    CompletableFuture.runAsync(() -> {                                                                  // Music is faded asynchronously so that it does not halt the entire program.
                        audio.setAdditionalThreadRunning(true);
                        audio.stopMusicTrack(true);
                        audio.setAdditionalThreadRunning(false);
                    });
                }
            } else {

                audio.stopMusicTrack(false);
            }
        }
    }


    // TODO : Possibly add method to resume a stopped piece of music.


    // TODO : Possibly add a method to change the volume of the music.


    /**
     * Transitions the music track that's currently playing out for a new one.
     *
     * @param trackIndex index in the `musicURL` array of the Sound class of the new music track to be transitioned in
     */
    public void swapMusic(int trackIndex) {

        if (audio.isMusicPlaying()) {

            if (!audio.isMusicFading()) {

                if (!audio.isAdditionalThreadRunning()) {

                    CompletableFuture.runAsync(() -> {                                                                  // Music is swapped asynchronously so that fade does not halt entire program; setting and playing new file asynchronously prevents program stutter.
                        audio.setAdditionalThreadRunning(true);
                        audio.setStagedMusicOverwrite(trackIndex);
                        audio.stopMusicTrack(true);
                        audio.setMusicTrackFile(trackIndex);
                        if ((!audio.isMusicClipNull()) && (!audio.isStagingMusicFailed())) {
                            audio.playMusicTrack();
                            audio.loopMusicTrack();
                        }
                        if (audio.isStagingMusicFailed()) {
                            audio.setStagingMusicFailed(false);
                        }
                        audio.setAdditionalThreadRunning(false);
                    });
                }
            } else {

                audio.setStagedMusicOverwrite(trackIndex);                                                              // Force this new track to be played after the current fade is complete.
            }
        } else {                                                                                                        // There is currently nothing playing to transition out of, so we'll just start playing the new track as normal.

            playMusic(trackIndex);
        }
    }


    /**
     * Retrieves the music track that's currently loaded/staged to play.
     * If a fading effect is current in effect, the music track staged to play after the fade will be retrieved instead.
     *
     * @return index in the `musicURL` array of the Sound class of the staged music track
     */
    public int getStagedMusic() {

        if (audio.isMusicFading()) {

            return audio.getStagedMusicOverwrite();
        } else {

            return audio.getStagedMusic();
        }
    }


    /**
     * Plays a sound effect.
     *
     * @param effectIndex sound effect index of the `effectURL` array in the Sound class
     */
    public void playSE(int effectIndex) {

        int effectChannel = audio.setSoundEffectFile(effectIndex);

        if (effectChannel != -1) {

            audio.playSoundEffect(effectChannel);
        }
    }


    /**
     * Loads a new map and sets it as the current map being rendered.
     *
     * @param mapId ID of the map being loaded
     */
    public void loadMap(int mapId) {

        // Set new map.
        loadedMap = jsonP.loadMapJson(mapId);

        // Clear conversing and combating entity lists.
        clearConversingEntities();
        clearCombatingEntities();

        // Purge `npc` and `obj` arrays.
        npc.clear();
        obj.clear();

        // Load entities on new map.
        jsonP.loadEntitiesJson(mapId);

        // Purge `conv` array.
        conv.clear();

        // Load dialogue associated with new map.
        jsonP.loadDialogueJson(mapId);
    }


    /**
     * Performs any loading that needs to be done once the screen is black during a transition of any type.
     */
    public void handleTransitionLoading() {

        switch (activeTransitionType) {
            case WARP:
                warpS.handleWarpTransitionLoading();
                break;
            case ENTER_COMBAT:
                combatM.handleEnterCombatTransitionLoading();
                break;
            case EXIT_COMBAT:
                combatM.handleExitCombatTransitionLoading();
                break;
        }
    }


    /**
     * Closes out a transition of any type that has completed all of its phases (i.e., tidies up any variables).
     */
    public void concludeTransition() {

        switch (activeTransitionType) {
            case WARP:
                warpS.concludeWarpTransition();
                break;
            case ENTER_COMBAT:
                combatM.concludeEnterCombatTransition();
                break;
            case EXIT_COMBAT:
                combatM.concludeExitCombatTransition();
                break;
        }

        // Reset transition variables.
        activeTransitionType = null;
        activeTransitionPhase = 0;
    }


    /**
     * Clears the list of conversing entities and makes them exit a state of conversing.
     */
    public void clearConversingEntities() {

        conversingEntities.clear();
    }


    /**
     * Clears the list of combating entities and makes them exit a state of combat.
     */
    public void clearCombatingEntities() {

        combatingEntities.clear();
    }


    /**
     * Retrieves any loaded entity by its ID.
     *
     * @param entityId ID of the entity being retrieved
     * @return retrieved entity OR null if no matching entity was found
     */
    public EntityBase getEntityById(int entityId) {

        EntityBase entity;

        // Check player.
        if (player != null) {
            if (player.getEntityId() == entityId) {
                return player;
            }
        }

        // Check party.
        entity = party.get(entityId);
        if (entity != null) {
            return entity;
        }

        // Check NPCs.
        entity = npc.get(entityId);
        if (entity != null) {
            return entity;
        }

        // Check standby.
        entity = standby.get(entityId);
        if (entity != null) {
            return entity;
        }

        // Check objects.
        entity = obj.get(entityId);

        return entity;
    }


    /**
     * Retrieves all instantiated entities.
     *
     * @return list of all instantiated entities
     */
    public List<EntityBase> getAllEntities() {

        // Initialize a list that will hold all entities.
        List<EntityBase> allEntities = new ArrayList<>();

        // Add player entity.
        allEntities.add(player);

        // Add all non-null party member entities.
        for (EntityBase entity : party.values()) {
            if (entity != null) {
                allEntities.add(entity);
            }
        }

        // Add all non-null NPC entities.
        for (EntityBase entity : npc.values()) {
            if (entity != null) {
                allEntities.add(entity);
            }
        }

        // Add all non-null standby entities.
        for (EntityBase entity : standby.values()) {
            if (entity != null) {
                allEntities.add(entity);
            }
        }

        // Add all non-null object entities.
        for (EntityBase entity : obj.values()) {
            if (entity != null) {
                allEntities.add(entity);
            }
        }

        return allEntities;
    }


    /**
     * Transfers an entity from a source entity map to a target entity map.
     *
     * @param source source entity map being transferred from
     * @param target target entity map being transferred to
     * @param entityId ID of the entity to be transferred
     */
    public void transferEntity(LimitedLinkedHashMap<Integer, EntityBase> source,
                               LimitedLinkedHashMap<Integer, EntityBase> target,
                               int entityId) {

        EntityBase entity = source.get(entityId);

        if (entity != null) {

            try {

                target.put(entityId, entity);

            } catch (IllegalStateException e) {

                UtilityTool.logWarning("The target map is full: failed to transfer entity "
                        + (((entity.getName() != null) && (!entity.getName().equals("")))
                            ? (entity.getName() + " ") : "")
                        + "with ID "
                        + entityId
                        + " from the source map to the target map.");
                return;
            }

            source.remove(entityId);
        } else {
            UtilityTool.logWarning("No matching entity was found loaded in the source map: failed to transfer entity with ID "
                    + entityId
                    + " from the source map to the target map.");
        }
    }


    /**
     * Removes an entity from a source entity map and blacklists it to not be loaded again.
     *
     * @param source source entity map to remove the entity from
     * @param entityId ID of the entity to be removed
     */
    public void removeEntity(LimitedLinkedHashMap<Integer, EntityBase> source, int entityId) {

        source.remove(entityId);
        removedEntities.add(entityId);
    }


    /**
     * Progresses the state of the entire game by one frame.
     */
    private void update() {

        try {

            // Camera.
            cameraS.update();

            // Dialogue.
            dialogueR.update();

            // Animation.
            animationM.update();                                                                                        // Run all animation logic to determine which images in animated assets should be drawn this frame.

            // Player input.
            player.updatePlayerInput();                                                                                 // Check for player input.

            // Player.
            player.update();                                                                                            // Update the player's position if an action is in progress.

            // Entities.
            updateEntities();

            // Environment.
            environmentM.update();

        } catch (Exception e) {

            running = false;
            UtilityTool.logError("An unhandled exception has occurred.");
            UtilityTool.logStackTrace(e);
            UtilityTool.writeCrashLog();
        }
    }


    /**
     * Updates the state of all entities by one frame.
     */
    private void updateEntities() {

        // Object.
        for (EntityBase entity : obj.values()) {
            if ((entity != null)
                    && (!conversingEntities.contains(entity.getEntityId()))
                    && (!combatingEntities.contains(entity.getEntityId()))) {                                           // Only update objects neither in a conversation nor in combat.
                entity.update();
            }
        }

        // NPC.
        for (EntityBase entity : npc.values()) {
            if ((entity != null)
                    && (!conversingEntities.contains(entity.getEntityId()))
                    && (!combatingEntities.contains(entity.getEntityId()))) {                                           // Only update NPCs neither in a conversation nor in combat.
                entity.update();
            }
        }

        // Party.
        for (EntityBase entity : party.values()) {
            if ((entity != null)
                    && (!conversingEntities.contains(entity.getEntityId()))
                    && (!combatingEntities.contains(entity.getEntityId()))) {                                           // Only update party members neither in a conversation nor in combat.
                entity.update();
            }
        }
    }


    /**
     * Performs necessary initializations when switching to a new game state.
     *
     * @param currentGameState game state being switched from
     * @param newGameState game state being switched to
     */
    private void gameStateInitialization(GameState currentGameState, GameState newGameState) {

        // Tidy up the state being switching from.
        switch (currentGameState) {

            case PARTY_MENU:
                iconM.getIconById(0).setSelected(false);                                                                // Deselect the party menu icon.
                ui.setPartySlotSelected(0);                                                                             // Set the selected party member stat icon back to its default.
                entityIconM.purgeAllEntityIcons();
                break;

            case INVENTORY_MENU:
                iconM.getIconById(1).setSelected(false);                                                                // Deselected the inventory menu icon.
                ui.setItemColSelected(0);                                                                               // Set the item slot back to its default column.
                ui.setItemRowSelected(0);                                                                               // Set the item slot back to its default row.
                for (int row = 0; row < ui.getMaxNumItemRow(); row++) {                                                 // Set all entries in the array of occupied item slots to false.
                    for (int col = 0; col < ui.getMaxNumItemCol(); col++) {
                        ui.getOccupiedItemSlots()[row][col] = false;
                    }
                }
                break;

            case SETTINGS_MENU:
                iconM.getIconById(2).setSelected(false);                                                                // Deselect the settings menu icon.
                break;
        }

        // Prepare for the state being switching to.
        switch (newGameState) {

            case PARTY_MENU:
                iconM.getIconById(0).setSelected(true);                                                                 // Select the party menu icon.
                entityIconM.createPartyEntityIcons();                                                                   // Create entity icons for the party members.
                entityIconM.getEntityIconById(player.getEntityId()).setSelected(true);                                  // Set the player icon as being selected (will animate the player icon).
                iconM.getIconById(3).setSelected(true);                                                                 // Set the background icon for the player as being selected (will darken the background).
                ui.setPartySlotSelected(0);                                                                             // Set the player's party member stat icon as being selected in the UI.
                break;

            case INVENTORY_MENU:
                iconM.getIconById(1).setSelected(true);                                                                 // Select the inventory menu icon.
                int numItems = player.getInventory().size();
                int itemIndex = 0;                                                                                      // Variable to track how many item slots in the player's inventory have been assigned to an array slot.
                for (int row = 0; row < ui.getMaxNumItemRow(); row++) {                                                 // Set the array of occupied item slots (inventory is displayed as a grid).
                    for (int col = 0; col < ui.getMaxNumItemCol(); col++) {
                        if (itemIndex < numItems) {
                            ui.getOccupiedItemSlots()[row][col] = true;
                        } else {
                            ui.getOccupiedItemSlots()[row][col] = false;
                        }
                        itemIndex++;
                    }
                }
                ui.setItemRowSelected(0);                                                                               // Set the top-left item icon as being selected.
                ui.setItemColSelected(0);                                                                               // ^^^
                break;

            case SETTINGS_MENU:
                iconM.getIconById(2).setSelected(true);                                                                 // Select the settings menu icon.
                break;
        }
    }


    // GETTERS
    public int getScale() {
        return scale;
    }

    public int getTileSize() {
        return tileSize;
    }

    public int getScreenWidth() {
        return screenWidth;
    }

    public int getScreenHeight() {
        return screenHeight;
    }

    public int getMaxWorldCol() {
        return maxWorldCol;
    }

    public int getMaxWorldRow() {
        return maxWorldRow;
    }

    public Map getLoadedMap() {
        return loadedMap;
    }

    public int getFPS() {
        return FPS;
    }

    public int getFpsTracker() {
        return fpsTracker;
    }

    public SubMenuHandler getSubMenuH() {
        return subMenuH;
    }

    public CollisionInspector getCollisionI() {
        return collisionI;
    }

    public DialogueReader getDialogueR() {
        return dialogueR;
    }

    public TileManager getTileM() {
        return tileM;
    }

    public LandmarkManager getLandmarkM() {
        return landmarkM;
    }

    public MenuIconManager getIconM() {
        return iconM;
    }

    public EntityIconManager getEntityIconM() {
        return entityIconM;
    }

    public EnvironmentManager getEnvironmentM() {
        return environmentM;
    }

    public CutsceneManager getCutsceneM() {
        return cutsceneM;
    }

    public AnimationManager getAnimationM() {
        return animationM;
    }

    public CombatManager getCombatM() {
        return combatM;
    }

    public InteractionManager getInteractionM() {
        return interactionM;
    }

    public CameraSupport getCameraS() {
        return cameraS;
    }

    public WarpSupport getWarpS() {
        return warpS;
    }

    public SubMenuSupport getSubMenuS() {
        return subMenuS;
    }

    public PathFinder getPathF() {
        return pathF;
    }

    public UI getUi() {
        return ui;
    }

    public Player getPlayer() {
        return player;
    }

    public int getOriginalTileSize() {
        return originalTileSize;
    }

    public LimitedLinkedHashMap<Integer, EntityBase> getObj() {
        return obj;
    }

    public LimitedLinkedHashMap<Integer, EntityBase> getNpc() {
        return npc;
    }

    public LimitedLinkedHashMap<Integer, EntityBase> getParty() {
        return party;
    }

    public LimitedLinkedHashMap<Integer, EntityBase> getStandby() {
        return standby;
    }

    public HashSet<Integer> getConversingEntities() {
        return conversingEntities;
    }

    public HashSet<Integer> getCombatingEntities() {
        return combatingEntities;
    }

    public HashSet<Integer> getRemovedEntities() {
        return removedEntities;
    }

    public HashMap<Integer, Conversation> getConv() {
        return conv;
    }

    public DialogueArrow getDialogueA() {
        return dialogueA;
    }

    public SelectionArrow getSelectionA() {
        return selectionA;
    }

    public GameState getGameState() {
        return gameState;
    }

    public boolean isPartyVisible() {
        return partyVisible;
    }

    public boolean isCombatActive() {
        return combatActive;
    }

    public boolean isDebugActive() {
        return debugActive;
    }

    public TransitionType getActiveTransitionType() {
        return activeTransitionType;
    }

    public int getActiveTransitionPhase() {
        return activeTransitionPhase;
    }


    // SETTERS
    public void setGameState(GameState gameState) {
        gameStateInitialization(this.gameState, gameState);
        this.gameState = gameState;
    }

    public void setPartyVisible(boolean partyVisible) {
        this.partyVisible = partyVisible;
    }

    public void setCombatActive(boolean combatActive) {
        this.combatActive = combatActive;
    }

    public void setDebugActive(boolean debugActive) {
        this.debugActive = debugActive;
    }

    public void setActiveTransitionType(TransitionType activeTransitionType) {
        this.activeTransitionType = activeTransitionType;
    }

    public void setActiveTransitionPhase(int activeTransitionPhase) {
        if ((activeTransitionPhase >= 0) && (activeTransitionPhase <= 3)) {
            this.activeTransitionPhase = activeTransitionPhase;
        } else {
            throw new IllegalArgumentException("Attempted to set a transition phase outside of allowed range");
        }
    }
}
