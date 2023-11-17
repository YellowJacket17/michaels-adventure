package core;

import ai.PathFinder;
import animation.AnimationManager;
import combat.CombatManager;
import cutscene.CutsceneManager;
import dialogue.Conversation;
import dialogue.DialogueArrow;
import dialogue.DialogueReader;
import event.support.SubMenuSupport;
import event.support.WarpSupport;
import miscellaneous.*;
import render.Renderer;
import render.Spritesheet;
import submenu.SelectionArrow;
import entity.EntityBase;
import entity.implementation.player.Player;
import environment.EnvironmentManager;
import event.support.CameraSupport;
import icon.EntityIconManager;
import icon.GuiIconManager;
import event.EventManager;
import landmark.LandmarkBase;
import landmark.LandmarkManager;
import map.Map;
import submenu.SubMenuHandler;
import tile.TileManager;
import utility.AssetPool;
import utility.JsonParser;
import utility.UtilityTool;
import utility.LimitedLinkedHashMap;

import java.util.*;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * Core class for the game that houses essential functions and configurations.
 */
public class GamePanel {

    // SCREEN SETTINGS
    /**
     * Native size of rendered tiles.
     * Tiles are the same width and height.
     */
    private static final int NATIVE_TILE_SIZE = 32;

    /**
     * Tiles per column in the screen space.
     */
    private static final int MAX_SCREEN_COL = 24;

    /**
     * Tiles per row in the screen space.
     */
    private static final int MAX_SCREEN_ROW = 14;

    /**
     * Native screen width as determined by the native tile size and number of columns.
     */
    private static final int NATIVE_SCREEN_WIDTH = NATIVE_TILE_SIZE * MAX_SCREEN_COL;

    /**
     * Native screen height as determined by the native tile size and number of rows.
     */
    private static final int NATIVE_SCREEN_HEIGHT = NATIVE_TILE_SIZE * MAX_SCREEN_ROW;


     // WORLD SETTINGS
    /**
     * Maximum number of tiles allowed in a map column.
     */
    private static final int MAX_WORLD_COL = 100;

    /**
     *  Maximum number of tiles allowed in a map row.
     */
    private static final int MAX_WORLD_ROW = 100;

    /**
     * Current loaded map.
     */
    private Map loadedMap;


    // SYSTEM
    private Camera camera;
    private final Renderer renderer = new Renderer(this);
    private final SubMenuHandler subMenuH = new SubMenuHandler(this);
    private final CollisionInspector collisionI = new CollisionInspector(this);
    private final DialogueReader dialogueR = new DialogueReader(this);
    private TileManager tileM;
    private final LandmarkManager landmarkM = new LandmarkManager(this);
    private GuiIconManager iconM;
    private final EntityIconManager entityIconM = new EntityIconManager(this);
    private final EnvironmentManager environmentM = new EnvironmentManager(this);
    private final CutsceneManager cutsceneM = new CutsceneManager(this);
    private final AnimationManager animationM = new AnimationManager();
    private final CombatManager combatM = new CombatManager(this);
    private final EventManager interactionM = new EventManager(this);
    private final CameraSupport cameraS = new CameraSupport(this);
    private final WarpSupport warpS = new WarpSupport(this);
    private final SubMenuSupport subMenuS = new SubMenuSupport(this);
    private final PathFinder pathF = new PathFinder(this);
    private final Sound audio = new Sound();
    private final Ui ui = new Ui(this);


    // ENTITY (PLAYER, NPC, OBJECT)
    /**
     * Player entity.
     */
    private Player player;

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
     * List to temporarily store all loaded entities (player, NPCs, objects, party members) when layering for rendering.
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
    private DialogueArrow dialogueA;


    // SUB-MENU
    /**
     * Arrow that appears when the player is required to make a sub-menu selection.
     */
    private SelectionArrow selectionA;


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
     * Variable to store phase of the current transition being performed.
     */
    private TransitionPhase activeTransitionPhase;

    /**
     * Boolean to flag whether a new transition phase was just entered.
     */
    private boolean transitionPhaseChanged = false;


    // CONSTRUCTOR
    /**
     * Constructs a GamePanel instance.
     */
    public GamePanel() {}


    // METHODS
    /**
     * Initializes the game.
     */
    public void init() {

        // Load resources.
        loadResources();

        // Initialize remaining system classes.
        camera = new Camera(NATIVE_SCREEN_WIDTH, NATIVE_SCREEN_HEIGHT);
        tileM = new TileManager(this);
        iconM = new GuiIconManager(this);
        dialogueA = new DialogueArrow(this);
        selectionA = new SelectionArrow(this);
        player = new Player(this);

        // Load map along with associated entities and dialogue.
        loadMap(1);

        // Set camera to track player entity.
        cameraS.setTrackedEntity(player);

        // Force fade into the game.
        gameState = GameState.TRANSITION;
        activeTransitionType = TransitionType.WARP;
        activeTransitionPhase = TransitionPhase.LOADING;
        transitionPhaseChanged = false;

        // Other setup.
        environmentM.setup();
        playMusic(0);
    }


    /**
     * Progresses the state of the entire game by one frame.
     *
     * @param dt time since the last rendered frame (frame pacing)
     */
    public void update(double dt) {

        // Transition.
        updateTransition();

        // Dialogue.
        dialogueR.update();

        // Cutscene.
        cutsceneM.update();

        // Animation.
        animationM.update();                                                                                            // Run all animation logic to determine which images in animated assets should be drawn this frame.

        // Player input.
        player.updatePlayerInput();                                                                                     // Check for player input.

        // Player.
        player.update();                                                                                                // Update the player's position if an action is in progress.

        // Entities.
        updateEntities();

        // Environment.
//        environmentM.update();

        // Camera.
        cameraS.update();
    }


    /**
     * Sends necessary items to the render pipeline and renders them.
     *
     * @param dt time since the last rendered frame (frame pacing)
     */
    public void render(double dt) {

        // NOTE:
        // Objects are added to the render pipeline in the following order to control layering.

        // Tile.
        tileM.addToRenderPipeline(renderer);                                                                            // Render tile sprites as defined in the TileManager class.

        // Entity and landmark.
        for (EntityBase entity : obj.values()) {                                                                        // Add all objects in the current map to the list of entities.

            if (entity != null) {

                entityList.add(entity);
            }
        }

        for (EntityBase entity : npc.values()) {                                                                        // Add all NPCs in the current map to the list of entities.

            if (entity != null) {

                entityList.add(entity);
            }
        }

        if (partyVisible) {

            Set<Integer> keySet = party.keySet();
            Integer[] keyArray = keySet.toArray(new Integer[keySet.size()]);

            for (int i = (keySet.size() - 1); i >= 0; i--) {                                                            // Add all party members in the current map to the list of entities; iterates backwards.

                if (party.get(keyArray[i]) != null) {

                    entityList.add(party.get(keyArray[i]));
                }
            }
        }

        entityList.add(player);                                                                                         // Add player to the list of all entities.

        ArrayList<LandmarkBase> landmarkList;

        if (loadedMap != null) {

            landmarkList = loadedMap.getMapLandmarks();                                                                 // Get the list of landmarks on the loaded map.
        } else {

            landmarkList = new ArrayList<>();                                                                           // Fail-safe to have empty landmark array if no map is loaded.
        }

        for (int row = 0; row < MAX_WORLD_ROW; row++) {                                                                 // Render the entities and landmarks row-by-row, starting at the top.

            for (LandmarkBase landmark : landmarkList) {                                                                // Render all landmarks in the current row.

                if ((landmark.getRow() >= row)
                        && (landmark.getRow() < (row + 1))) {

                    landmark.addToRenderPipeline(renderer);
                }
            }

            for (EntityBase entity : entityList) {                                                                      // Render all entities in the current row.

                if ((entity.getWorldY() >= (row * NATIVE_TILE_SIZE))
                        && (entity.getWorldY() < ((row + 1) * NATIVE_TILE_SIZE))) {

                    entity.addToRenderPipeline(renderer);
                }
            }
        }

        // Environment.
//        environmentM.addToRenderPipeline(renderer);

        // UI.
        ui.addToRenderPipeline(renderer, dt);                                                                           // Render UI after everything else so that it appears on the top layer.

        // Cleanup.
        entityList.clear();                                                                                             // Reset the list of all entities by emptying it.

        // Flush the render pipeline to draw the frame.
        renderer.render();

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
        loadedMap = JsonParser.loadMapJson(this, mapId);

        // Clear conversing and combating entity lists.
        clearConversingEntities();
        clearCombatingEntities();

        // Purge `npc` and `obj` arrays.
        npc.clear();
        obj.clear();

        // Load entities on new map.
        JsonParser.loadEntitiesJson(this, mapId);

        // Purge `conv` array.
        conv.clear();

        // Load dialogue associated with new map.
        JsonParser.loadDialogueJson(this, mapId);
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
     * Updates a transition (i.e., performs any loading or other logic that needs to be run immediately after a new
     * transition phase is entered).
     */
    private void updateTransition() {

        if ((activeTransitionType != null) && (transitionPhaseChanged)) {

            switch (activeTransitionPhase) {
                case LOADING:
                    handleTransitionLoading();
                    break;
                case CLEANUP:
                    concludeTransition();
                    break;
            }
        }

        if (transitionPhaseChanged) {

            transitionPhaseChanged = false;
        }
    }


    /**
     * Performs any loading that needs to be done once the screen is black during a transition of any type.
     */
    private void handleTransitionLoading() {

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
    private void concludeTransition() {

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
        activeTransitionPhase = TransitionPhase.DEFAULT;
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


    /**
     * Loads and stores resources like shaders and spritesheets into memory.
     */
    private void loadResources() {

        // Shaders.
        AssetPool.getShader("/shaders/default.glsl");
        AssetPool.getShader("/shaders/rounded.glsl");
        AssetPool.getShader("/shaders/font.glsl");

        // Tiles spritesheet.
        String filePath = "/spritesheets/tiles.png";
        AssetPool.addSpritesheet(new Spritesheet(AssetPool.getTexture(filePath), 10, 32, 32, 1));

        // Characters spritesheet.
        filePath = "/spritesheets/characters.png";
        AssetPool.addSpritesheet(new Spritesheet(AssetPool.getTexture(filePath), 36, 32, 48, 0));

        // Objects spritesheet.
        filePath = "/spritesheets/objects.png";
        AssetPool.addSpritesheet(new Spritesheet(AssetPool.getTexture(filePath), 3, 32, 32, 0));

        // Landmarks spritesheet.
        filePath = "/spritesheets/landmarks.png";
        int[] widths = new int[] {62, 32};
        int[] heights = new int[] {90, 70};
        AssetPool.addSpritesheet(new Spritesheet(AssetPool.getTexture(filePath), 2, widths, heights, 1));

        // Items spritesheet.
        filePath = "/spritesheets/items.png";
        AssetPool.addSpritesheet(new Spritesheet(AssetPool.getTexture(filePath), 2, 32, 32, 0));

        // Icons spritesheet.
        filePath = "/spritesheets/icons.png";
        widths = new int[] {153, 153, 40, 36, 36, 28, 28, 28, 28, 28, 28};
        heights = new int[] {56, 56, 40, 36, 36, 28, 28, 28, 28, 28, 28};
        AssetPool.addSpritesheet(new Spritesheet(AssetPool.getTexture(filePath), 11, widths, heights, 0));

        // Miscellaneous spritesheet.
        filePath = "/spritesheets/miscellaneous.png";
        widths = new int[] {6, 10};
        heights = new int[] {10, 6};
        AssetPool.addSpritesheet(new Spritesheet(AssetPool.getTexture(filePath), 2, widths, heights, 0));
    }


    // GETTERS
    public int getNativeTileSize() {
        return NATIVE_TILE_SIZE;
    }

    public int getNativeScreenWidth() {
        return NATIVE_SCREEN_WIDTH;
    }

    public int getNativeScreenHeight() {
        return NATIVE_SCREEN_HEIGHT;
    }

    public int getMaxWorldCol() {
        return MAX_WORLD_COL;
    }

    public int getMaxWorldRow() {
        return MAX_WORLD_ROW;
    }

    public Map getLoadedMap() {
        return loadedMap;
    }

    public Camera getCamera() {
        return camera;
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

    public GuiIconManager getIconM() {
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

    public EventManager getInteractionM() {
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

    public Ui getUi() {
        return ui;
    }

    public Player getPlayer() {
        return player;
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

    public TransitionPhase getActiveTransitionPhase() {
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

    public void setActiveTransitionPhase(TransitionPhase activeTransitionPhase) {
        this.activeTransitionPhase = activeTransitionPhase;
        transitionPhaseChanged = true;
    }
}
