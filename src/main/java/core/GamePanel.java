package core;

import ai.PathFinder;
import animation.AnimationManager;
import asset.AssetPool;
import combat.CombatManager;
import combat.TargetArrow;
import cutscene.CutsceneManager;
import dialogue.Conversation;
import dialogue.DialogueArrow;
import dialogue.DialogueReader;
import event.support.*;
import miscellaneous.*;
import render.Camera;
import render.Renderer;
import asset.Spritesheet;
import submenu.SelectionArrow;
import entity.EntityBase;
import entity.implementation.player.Player;
import environment.EnvironmentManager;
import icon.EntityIconManager;
import icon.GuiIconManager;
import event.EventManager;
import landmark.LandmarkBase;
import landmark.LandmarkManager;
import map.Map;
import submenu.SubMenuHandler;
import tile.TileManager;
import utility.*;
import utility.exceptions.EntityTransferException;

import java.util.*;
import java.util.List;

/**
 * Core class for the game that houses essential functions and configurations.
 */
public class GamePanel {

    // SCREEN SETTINGS
    /**
     * Native size of rendered tiles.
     * Tiles are the same width and height.
     */
    public static final int NATIVE_TILE_SIZE = 32;

    /**
     * Native screen width.
     */
    public static final int NATIVE_SCREEN_WIDTH = 768;

    /**
     * Native screen height.
     */
    public static final int NATIVE_SCREEN_HEIGHT = 432;


     // WORLD SETTINGS
    /**
     * Maximum number of tiles allowed in a map column.
     */
    public static final int MAX_WORLD_COL = 100;

    /**
     *  Maximum number of tiles allowed in a map row.
     */
    public static final int MAX_WORLD_ROW = 100;

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
    private GuiIconManager guiIconM;
    private final EntityIconManager entityIconM = new EntityIconManager(this);
    private final EnvironmentManager environmentM = new EnvironmentManager(this);
    private final CutsceneManager cutsceneM = new CutsceneManager(this);
    private final AnimationManager animationM = new AnimationManager();
    private final CombatManager combatM = new CombatManager(this);
    private final EventManager eventM = new EventManager(this);
    private final CameraSupport cameraS = new CameraSupport(this);
    private final WarpSupport warpS = new WarpSupport(this);
    private final SubMenuSupport subMenuS = new SubMenuSupport(this);
    private final PartySupport partyS = new PartySupport(this);
    private final SoundSupport soundS = new SoundSupport(this);
    private final PathFinder pathF = new PathFinder(this);
    private final UserInterface ui = new UserInterface(this);


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
     * The number of entities at the front of the map (indices 0, 1, etc.) according to the `numActivePartyMembers` field are the active
     * party members.
     */
    private final LimitedLinkedHashMap<Integer, EntityBase> party = new LimitedLinkedHashMap<>(5);

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

    /**
     * Sets the number of active party members allowed at a time.
     * Active party members are those that actively follow the player entity, actively participate in combat, etc.
     * This is in contrast to reserve party members.
     */
    private final int numActivePartyMembers = 2;


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


    // COMBAT
    /**
     * Arrow that appears when the player is required to make a taret entity selection in combat.
     */
    private TargetArrow targetA;


    // GAME STATE
    /**
     * Variable to store which state the game is currently in (the game state controls what is updated each frame).
     */
    private GameState gameState;

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

    /**
     * Core time counter (seconds) for controlling transition fade to/fade from effect.
     */
    private double transitionCounter;

    /**
     * Duration of fading to for transition (seconds).
     */
    private final double transitionCounterFadingToMax = 0.5;

    /**
     * Duration of loading for transition (seconds).
     */
    private final double transitionCounterLoadingMax = 0.25;

    /**
     * Duration of fading from for transition (seconds).
     */
    private final double transitionCounterFadingFromMax = 0.5;


    // SETTINGS
    /**
     * List to store system settings available to the player.
     */
    private LimitedArrayList<Setting> systemSettings = new LimitedArrayList<>(10);


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
        guiIconM = new GuiIconManager(this);
        dialogueA = new DialogueArrow(this);
        selectionA = new SelectionArrow(this);
        targetA = new TargetArrow(this);
        player = new Player(this);

        // Initialize system settings.
        Setting vSyncSetting = new Setting("VSync", "Syncs frame rate with monitor refresh rate to prevent screen tearing.");
        vSyncSetting.addOption("Disabled");
        vSyncSetting.addOption("Enabled");
        systemSettings.add(vSyncSetting);

        Setting FrameRateLimitSetting = new Setting("Frame Rate Limit", "Maximum frame rate that the game will run at.");
        systemSettings.add(FrameRateLimitSetting);

        Setting tetherGameSpeedSetting = new Setting("Tether Game Speed", "Tethers game speed to the frame rate limit.");
        tetherGameSpeedSetting.addOption("Disabled");
        tetherGameSpeedSetting.addOption("Enabled");
        systemSettings.add(tetherGameSpeedSetting);

        Setting fullScreenSetting = new Setting("Full Screen", "Sets the game viewport to occupy the full monitor screen.");
        fullScreenSetting.addOption("Disabled");
        fullScreenSetting.addOption("Enabled");
        systemSettings.add(fullScreenSetting);

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
        soundS.playTrack("sound/tracks/testTrack1.ogg");
    }


    /**
     * Progresses the state of the entire game by one frame.
     *
     * @param dt time since last frame (seconds)
     */
    public void update(double dt) {

        // Player input.
        player.updatePlayerInput(dt);

        // Transition.
        updateTransition(dt);

        // Dialogue.
        dialogueR.update(dt);
        dialogueA.update(dt);

        // Cutscene.
        cutsceneM.update(dt);

        // Animation.
        animationM.update(dt);

        // Player.
        player.update(dt);

        // Entities.
        updateEntities(dt);

        // Entity icons.
        entityIconM.update(dt);

        // Environment.
//        environmentM.update();

        // Camera.
        cameraS.update(dt);
    }


    /**
     * Sends necessary items to the render pipeline and renders them.
     *
     * @param dt time since last frame (seconds)
     */
    public void render(double dt) {

        // NOTE: Drawables are added to the render pipeline in the following order to control layering.

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

        Set<Integer> keySet = party.keySet();
        Integer[] keyArray = keySet.toArray(new Integer[keySet.size()]);
        int numRenderedPartyMembers;

        if (keySet.size() > numActivePartyMembers) {

            numRenderedPartyMembers = numActivePartyMembers;
        } else {

            numRenderedPartyMembers = keySet.size();
        }

        for (int i = (numRenderedPartyMembers - 1); i >= 0; i--) {                                                      // Add active party members in the current map to the list of entities; iterates backwards.

            if (party.get(keyArray[i]) != null) {

                entityList.add(party.get(keyArray[i]));
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
        ui.addToRenderPipeline(renderer, dt);

        // Cleanup.
        entityList.clear();                                                                                             // Reset the list of all entities by emptying it.

        // Flush the render pipeline to draw the frame.
        renderer.render();

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
     * @throws EntityTransferException if transferring an entity fails
     */
    public void transferEntity(LimitedLinkedHashMap<Integer, EntityBase> source,
                               LimitedLinkedHashMap<Integer, EntityBase> target,
                               int entityId) {

        EntityBase entity = source.get(entityId);

        if (entity != null) {

            try {

                target.put(entityId, entity);

            } catch (IllegalStateException e) {

                throw new EntityTransferException("Failed to transfer entity "
                        + (((entity.getName() != null) && (!entity.getName().equals("")))
                        ? (entity.getName() + " ") : "")
                        + "with ID "
                        + entityId
                        + " from source map to target map - target map full");
            }
            source.remove(entityId);
        } else {

            throw new EntityTransferException("Failed to transfer entity with ID "
                    + entityId
                    + " from source map to target map - no such entity found in source map");
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
     * Initiates a transition.
     * The game state is set to transition.
     * Remember to also set a sub-transition type directly after calling this method if applicable.
     *
     * @param transitionType transition type
     */
    public void initiateTransition(TransitionType transitionType) {

        setGameState(GameState.TRANSITION);                                                                             // Set the game to a transition state.
        activeTransitionType = transitionType;                                                                          // Set the overarching transition type.
        transitionPhaseChanged = true;
        activeTransitionPhase = TransitionPhase.FADING_TO;                                                              // All transitions start with a fade to.
    }


    /**
     * Updates the state of all entities by one frame.
     *
     * @param dt time since last frame (seconds)
     */
    private void updateEntities(double dt) {

        // Object.
        for (EntityBase entity : obj.values()) {
            if ((entity != null)
                    && (!conversingEntities.contains(entity.getEntityId()))
                    && (!combatingEntities.contains(entity.getEntityId()))) {                                           // Only update objects neither in a conversation nor in combat.
                entity.update(dt);
            }
        }

        // NPC.
        for (EntityBase entity : npc.values()) {
            if ((entity != null)
                    && (!conversingEntities.contains(entity.getEntityId()))
                    && (!combatingEntities.contains(entity.getEntityId()))) {                                           // Only update NPCs neither in a conversation nor in combat.
                entity.update(dt);
            }
        }

        // Party.
        for (EntityBase entity : party.values()) {
            if ((entity != null)
                    && (!conversingEntities.contains(entity.getEntityId()))
                    && (!combatingEntities.contains(entity.getEntityId()))) {                                           // Only update party members neither in a conversation nor in combat.
                entity.update(dt);
            }
        }
    }


    /**
     * Updates a transition (i.e., performs any loading or other logic that needs to be run immediately after a new
     * transition phase is entered).
     *
     * @param dt time since last frame (seconds)
     */
    private void updateTransition(double dt) {

        switch (activeTransitionPhase) {
            case FADING_TO:                                                                                             // Phase 1: Fade screen to black.
                transitionCounter += dt;
                if (transitionCounter >= transitionCounterFadingToMax) {
                    transitionCounter -= transitionCounterFadingToMax;                                                  // Rollback counter to prepare it for the second phase.
                    activeTransitionPhase = TransitionPhase.LOADING;                                                    // Proceed to the next (second) phase of the transition.
                    transitionPhaseChanged = true;
                }
                break;
            case LOADING:                                                                                               // Phase 2: Wait on black screen.
                transitionCounter += dt;
                if (transitionCounter >= transitionCounterLoadingMax) {
                    transitionCounter -= transitionCounterLoadingMax;                                                   // Rollback counter to prepare it for the final (third) phase.
                    activeTransitionPhase = TransitionPhase.FADING_FROM;                                                // Proceed to the final (third) phase of the transition.
                    transitionPhaseChanged = true;
                }
                break;
            case FADING_FROM:                                                                                           // Phase 3: Fade from black.
                transitionCounter += dt;
                if (transitionCounter >= transitionCounterFadingFromMax) {
                    transitionCounter = 0;                                                                              // Reset counter to its default state since the transition is complete.
                    activeTransitionPhase = TransitionPhase.CLEANUP;
                    transitionPhaseChanged = true;
                }
                break;
        }

        if ((activeTransitionType != null) && (transitionPhaseChanged)) {

            switch (activeTransitionPhase) {
                case LOADING:
                    handleTransitionLoading(dt);
                    System.gc();                                                                                        // Now is a good time for garbage collection by the JVM.
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
     *
     * @param dt time since last frame (seconds)
     */
    private void handleTransitionLoading(double dt) {

        switch (activeTransitionType) {
            case WARP:
                warpS.handleWarpTransitionLoading(dt);
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
                guiIconM.getIconById(0).setSelected(false);                                                             // Deselect the party menu icon.
                ui.setPartySlotSelected(0);                                                                             // Set the selected party member stat icon back to its default.
                entityIconM.purgeAllEntityIcons();
                break;

            case INVENTORY_MENU:
                guiIconM.getIconById(1).setSelected(false);                                                             // Deselected the inventory menu icon.
                ui.setItemColSelected(0);                                                                               // Set the item slot back to its default column.
                ui.setItemRowSelected(0);                                                                               // Set the item slot back to its default row.
                for (int row = 0; row < ui.getMaxNumItemRow(); row++) {                                                 // Set all entries in the array of occupied item slots to false.
                    for (int col = 0; col < ui.getMaxNumItemCol(); col++) {
                        ui.getOccupiedItemSlots()[row][col] = false;
                    }
                }
                break;

            case SETTINGS_MENU:
                guiIconM.getIconById(2).setSelected(false);                                                             // Deselect the settings menu icon.
                ui.setSystemSettingSelected(0);                                                                         // Reset selected setting to default.
                ui.setSystemOptionSelected(systemSettings.get(0).getActiveOption());                                    // Reset selected option to default (i.e., active option of the default setting).
                break;
        }

        // Prepare for the state being switching to.
        switch (newGameState) {

            case PARTY_MENU:
                guiIconM.getIconById(0).setSelected(true);                                                              // Select the party menu icon.
                entityIconM.createPartyEntityIcons();                                                                   // Create entity icons for the party members.
                entityIconM.getEntityIconById(player.getEntityId()).setSelected(true);                                  // Set the player icon as being selected (will animate the player icon).
                guiIconM.getIconById(3).setSelected(true);                                                              // Set the background icon for the player as being selected (will darken the background).
                ui.setPartySlotSelected(0);                                                                             // Set the player's party member stat icon as being selected in the UI.
                break;

            case INVENTORY_MENU:
                guiIconM.getIconById(1).setSelected(true);                                                              // Select the inventory menu icon.
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
                ui.setItemColSelected(0);                                                                               // Set the top-left item icon as being selected.
                ui.setItemRowSelected(0);                                                                               // ^^^
                break;

            case SETTINGS_MENU:
                guiIconM.getIconById(2).setSelected(true);                                                              // Select the settings menu icon.
                ui.setSystemSettingSelected(0);
                ui.setSystemOptionSelected(systemSettings.get(0).getActiveOption());
                break;
        }
    }


    /**
     * Loads resources like shaders and spritesheets into memory.
     */
    private void loadResources() {

        // Shaders.
        AssetPool.getShader("/shaders/default.glsl");
        AssetPool.getShader("/shaders/rounded.glsl");
        AssetPool.getShader("/shaders/font.glsl");

        // Tiles spritesheet (spritesheet 0).
        String filePath = "/spritesheets/tiles.png";
        AssetPool.addSpritesheet(new Spritesheet(AssetPool.getTexture(filePath), 10, 32, 32, 1));

        // Characters spritesheet (spritesheet 1).
        filePath = "/spritesheets/characters.png";
        AssetPool.addSpritesheet(new Spritesheet(AssetPool.getTexture(filePath), 36, 32, 48, 0));

        // Objects spritesheet (spritesheet 2).
        filePath = "/spritesheets/objects.png";
        AssetPool.addSpritesheet(new Spritesheet(AssetPool.getTexture(filePath), 3, 32, 32, 0));

        // Landmarks spritesheet (spritesheet 3).
        filePath = "/spritesheets/landmarks.png";
        int[] widths = new int[] {62, 32};
        int[] heights = new int[] {90, 70};
        AssetPool.addSpritesheet(new Spritesheet(AssetPool.getTexture(filePath), 2, widths, heights, 1));

        // Items spritesheet (spritesheet 4).
        filePath = "/spritesheets/items.png";
        AssetPool.addSpritesheet(new Spritesheet(AssetPool.getTexture(filePath), 2, 32, 32, 0));

        // Icons spritesheet (spritesheet 5).
        filePath = "/spritesheets/icons.png";
        widths = new int[] {152, 152, 40, 36, 36, 28, 28, 28, 28, 28, 28, 6, 6, 6, 6};
        heights = new int[] {56, 56, 40, 36, 36, 28, 28, 28, 28, 28, 28, 10, 10, 10, 10};
        AssetPool.addSpritesheet(new Spritesheet(AssetPool.getTexture(filePath), 15, widths, heights, 0));

        // Miscellaneous spritesheet (spritesheet 6).
        filePath = "/spritesheets/miscellaneous.png";
        widths = new int[] {6, 10, 12};
        heights = new int[] {10, 6, 8};
        AssetPool.addSpritesheet(new Spritesheet(AssetPool.getTexture(filePath), 3, widths, heights, 0));

        // Sounds.
        AssetPool.addSound("sound/tracks/testTrack1.ogg", true);
        AssetPool.addSound("sound/tracks/testTrack2.ogg", true);
        AssetPool.addSound("sound/tracks/testTrack4.ogg", true);
        AssetPool.addSound("sound/effects/testEffect1.ogg", false);
    }


    // GETTERS
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

    public GuiIconManager getGuiIconM() {
        return guiIconM;
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

    public EventManager getEventM() {
        return eventM;
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

    public PartySupport getPartyS() {
        return partyS;
    }

    public SoundSupport getSoundS() {
        return soundS;
    }

    public PathFinder getPathF() {
        return pathF;
    }

    public UserInterface getUi() {
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

    public int getNumActivePartyMembers() {
        return numActivePartyMembers;
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

    public TargetArrow getTargetA() {
        return targetA;
    }

    public GameState getGameState() {
        return gameState;
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

    public double getTransitionCounter() {
        return transitionCounter;
    }

    public double getTransitionCounterFadingToMax() {
        return transitionCounterFadingToMax;
    }

    public double getTransitionCounterLoadingMax() {
        return transitionCounterLoadingMax;
    }

    public double getTransitionCounterFadingFromMax() {
        return transitionCounterFadingFromMax;
    }

    public int getSystemSettingsSize() {
        return systemSettings.size();
    }

    public Setting getSystemSetting(int setting) {
        if ((setting < systemSettings.size()) && (setting >= 0)) {
            return systemSettings.get(setting);
        }
        return null;
    }


    // SETTERS
    public void setGameState(GameState gameState) {
        gameStateInitialization(this.gameState, gameState);
        this.gameState = gameState;
    }

    public void setCombatActive(boolean combatActive) {
        this.combatActive = combatActive;
    }

    public void setDebugActive(boolean debugActive) {
        this.debugActive = debugActive;
    }
}
