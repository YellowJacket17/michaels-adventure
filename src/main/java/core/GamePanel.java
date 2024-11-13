package core;

import ai.PathFinder;
import animation.AnimationManager;
import asset.AssetPool;
import asset.Illustration;
import combat.CombatManager;
import combat.TargetArrow;
import core.enumeration.PrimaryGameState;
import cutscene.CutsceneManager;
import dialogue.DialogueArrow;
import dialogue.DialogueReader;
import entity.EntityManager;
import event.support.*;
import item.ItemManager;
import map.MapManager;
import miscellaneous.*;
import org.joml.Vector3f;
import render.Camera;
import render.Renderer;
import asset.Spritesheet;
import submenu.SelectionArrow;
import entity.EntityBase;
import environment.EnvironmentManager;
import icon.EntityIconManager;
import icon.GuiIconManager;
import event.EventManager;
import landmark.LandmarkBase;
import landmark.LandmarkManager;
import submenu.SubMenuHandler;
import tile.TileManager;
import utility.*;

import java.util.*;

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
    private final EntityManager entityM = new EntityManager(this);
    private ItemManager itemM;
    private final MapManager mapM = new MapManager(this);
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
    private final FadeSupport fadeS = new FadeSupport(this);
    private final TransitionSupport transitionS = new TransitionSupport(this);
    private final IllustrationSupport illustrationS = new IllustrationSupport(this);
    private final PathFinder pathF = new PathFinder(this);
    private final UserInterface ui = new UserInterface(this);


    // GAME STATE
    /**
     * Variable to store which primary state the game is currently in.
     * The primary game state drives what is updated each frame, what is rendered, and how player inputs are registered.
     */
    private PrimaryGameState primaryGameState;

    /**
     * Boolean indicating whether player inputs are registered (true) or not (false).
     * Note that this only affects gameplay control (ex. system controls like enter/exit full screen are not affected).
     * Also note that some primary game states automatically lock/revoke player control.
     */
    private boolean lockPlayerControl = false;

    /**
     * Boolean indicating whether the overworld (tiles, entities, landmarks, and environmental effects) are rendered
     * (true) or not (false).
     */
    private boolean renderWorld = true;

    /**
     * Boolean to set whether debug mode is active (true) or not (false).
     */
    private boolean debugActive = false;


    // SETTINGS
    /**
     * List to store system settings available to the player.
     */
    private LimitedArrayList<Setting> systemSettings = new LimitedArrayList<>(10);


    // MISCELLANEOUS
    /**
     * List to temporarily store all loaded entities (player, NPCs, objects, party members) when layering for rendering.
     */
    private final ArrayList<EntityBase> entityList = new ArrayList<>();

    /**
     * Arrow that appears when the player is required to progress a piece of dialogue.
     */
    private DialogueArrow dialogueA;

    /**
     * Arrow that appears when the player is required to make a sub-menu selection.
     */
    private SelectionArrow selectionA;

    /**
     * Arrow that appears when the player is required to make a taret entity selection in combat.
     */
    private TargetArrow targetA;


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

        // Initialize remaining system classes requiring fully initialized GamePanel instance.
        camera = new Camera(NATIVE_SCREEN_WIDTH, NATIVE_SCREEN_HEIGHT);
        tileM = new TileManager(this);
        guiIconM = new GuiIconManager(this);
        itemM = new ItemManager(this);
        dialogueA = new DialogueArrow(this);
        selectionA = new SelectionArrow(this);
        targetA = new TargetArrow(this);

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

        Setting eEqualsEnterSetting = new Setting("E = Enter", "Sets the 'E' key to function the same as the 'Enter' key.");
        eEqualsEnterSetting.addOption("Disabled");
        eEqualsEnterSetting.addOption("Enabled");
        systemSettings.add(eEqualsEnterSetting);

        // Initialize player.
        entityM.initPlayer();

        // Load map along with associated entities and dialogue.
        mapM.loadMap(1, 0, true);

        // Set camera to track player entity.
        cameraS.setTrackedEntity(entityM.getPlayer());

        // Force fade into the game.
        fadeS.displayColor(new Vector3f(255, 255, 255));
        fadeS.initiateFadeFrom(0.5);

        // Other setup.
        environmentM.setup();

        // Set primary game state to player control.
        primaryGameState = PrimaryGameState.EXPLORE;
    }


    /**
     * Progresses the state of the entire game by one frame.
     *
     * @param dt time since last frame (seconds)
     */
    public void update(double dt) {

        // Player input.
        entityM.updateInput(dt);

        // Fade.
        fadeS.update(dt);

        // Transition.
        transitionS.update(dt);

        // Dialogue.
        dialogueR.update(dt);
        dialogueA.update(dt);

        // Cutscene.
        cutsceneM.update(dt);

        // Animation.
        animationM.update(dt);

        // Entities.
        entityM.update(dt);

        // Entity icons.
        entityIconM.update(dt);

        // Environment.
//        environmentM.update();

        // Camera.
        cameraS.update(dt);

        // Sound
        soundS.update(dt);
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
        for (EntityBase entity : entityM.getObj().values()) {                                                           // Add all objects in the current map to the list of entities.
            if (entity != null) {
                entityList.add(entity);
            }
        }

        for (EntityBase entity : entityM.getNpc().values()) {                                                           // Add all loaded NPCs to the list of entities.
            if (entity != null) {
                entityList.add(entity);
            }
        }

        Set<Integer> keySet = entityM.getParty().keySet();
        Integer[] keyArray = keySet.toArray(new Integer[keySet.size()]);
        for (int i = (entityM.getParty().size() - 1); i >= 0; i--) {                                                    // Add all loaded party members (active and inactive) to the list of entities; iterates backwards.
            if (entityM.getParty().get(keyArray[i]) != null) {
                entityList.add(entityM.getParty().get(keyArray[i]));
            }
        }

        entityList.add(entityM.getPlayer());                                                                            // Add player entity to the list of all entities.

        ArrayList<LandmarkBase> landmarkList;
        if (mapM.getLoadedMap() != null) {
            landmarkList = mapM.getLoadedMap().getMapLandmarks();                                                       // Get the list of landmarks on the loaded map.
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

        // Illustration.
        illustrationS.addToRenderPipeline(renderer);

        // UI.
        ui.addToRenderPipeline(renderer, dt);

        // Flush the render pipeline to draw the frame.
        renderer.render();

        // Cleanup.
        entityList.clear();                                                                                             // Reset the list of all entities by emptying it.
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
        AssetPool.addSpritesheet("tiles", new Spritesheet(AssetPool.getTexture(filePath), 179, 32, 32, 1));

        // Characters spritesheet (spritesheet 1).
        filePath = "/spritesheets/characters.png";
        AssetPool.addSpritesheet("characters", new Spritesheet(AssetPool.getTexture(filePath), 108, 32, 60, 1));

        // Objects spritesheet (spritesheet 2).
        filePath = "/spritesheets/objects.png";
        AssetPool.addSpritesheet("objects", new Spritesheet(AssetPool.getTexture(filePath), 3, 32, 32, 1));

        // Landmarks spritesheet (spritesheet 3).
        filePath = "/spritesheets/landmarks.png";
        int[] widths = new int[] {32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32,
                32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32,
                32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 128, 128, 128, 128, 96, 96, 96, 96};
        int[] heights = new int[] {32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32,
                32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32,
                32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 64, 64, 64, 64, 64, 64, 64, 64};
        AssetPool.addSpritesheet("landmarks", new Spritesheet(AssetPool.getTexture(filePath), 71, widths, heights, 1));

        // Items spritesheet (spritesheet 4).
        filePath = "/spritesheets/items.png";
        AssetPool.addSpritesheet("items", new Spritesheet(AssetPool.getTexture(filePath), 2, 32, 32, 1));

        // Icons spritesheet (spritesheet 5).
        filePath = "/spritesheets/icons.png";
        widths = new int[] {152, 152, 40, 36, 36, 28, 28, 28, 28, 28, 28, 6, 6, 6, 6};
        heights = new int[] {58, 58, 40, 36, 36, 28, 28, 28, 28, 28, 28, 10, 10, 10, 10};
        AssetPool.addSpritesheet("icons", new Spritesheet(AssetPool.getTexture(filePath), 15, widths, heights, 1));

        // Miscellaneous spritesheet (spritesheet 6).
        filePath = "/spritesheets/miscellaneous.png";
        widths = new int[] {6, 10, 12};
        heights = new int[] {10, 6, 8};
        AssetPool.addSpritesheet("miscellaneous", new Spritesheet(AssetPool.getTexture(filePath), 3, widths, heights, 1));

        // Sounds.
        AssetPool.addSound("testTrack1", "sound/tracks/testTrack1.ogg", "sound/tracks/testTrack1.ogg");
        AssetPool.addSound("testTrack2", "sound/tracks/testIntro.ogg", "sound/tracks/testLoop.ogg");
        AssetPool.addSound("testTrack3", "sound/tracks/testTrack3.ogg", "sound/tracks/testTrack3.ogg");
        AssetPool.addSound("testEffect1", "sound/effects/testEffect1.ogg");

        // Illustrations.
        filePath = "/illustrations/illustration1.png";
        AssetPool.addIllustration("illustration1", new Illustration(AssetPool.getTexture(filePath)));
    }


    /**
     * Performs necessary initializations when switching to a primary new game state.
     *
     * @param currentPrimaryGameState primary game state being switched from
     * @param newPrimaryGameState primary game state being switched to
     */
    private void primaryGameStateInit(PrimaryGameState currentPrimaryGameState,
                                      PrimaryGameState newPrimaryGameState) {

        // Tidy up the state being switching from.
        switch (currentPrimaryGameState) {

            case PARTY_MENU:
                guiIconM.getIconById(0).setSelected(false);                                                             // Deselect the party menu icon.
                ui.setPartySlotSelected(0);                                                                             // Set the selected party member stat icon back to its default.
                ui.setPartyMenuScrollLevel(0);                                                                          // Set the list of party members back to its default scroll level.
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
        switch (newPrimaryGameState) {

            case PARTY_MENU:
                guiIconM.getIconById(0).setSelected(true);                                                              // Select the party menu icon.
                entityIconM.createPartyEntityIcons();                                                                   // Create entity icons for the party members.
                entityIconM.getEntityIconById(entityM.getPlayer().getEntityId()).setSelected(true);                     // Set the player icon as being selected (will animate the player icon).
                guiIconM.getIconById(3).setSelected(true);                                                              // Set the background icon for the player as being selected (will darken the background).
                ui.setPartySlotSelected(0);                                                                             // Set the player entity's party member stat icon as being selected in the UI.
                ui.setPartyMenuScrollLevel(0);                                                                          // Set the list of party members back to the top.
                break;

            case INVENTORY_MENU:
                guiIconM.getIconById(1).setSelected(true);                                                              // Select the inventory menu icon.
                int numItems = entityM.getPlayer().getInventory().size();
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


    // GETTERS
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

    public EntityManager getEntityM() {
        return entityM;
    }

    public ItemManager getItemM() {
        return itemM;
    }

    public MapManager getMapM() {
        return mapM;
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

    public FadeSupport getFadeS() {
        return fadeS;
    }

    public TransitionSupport getTransitionS() {
        return transitionS;
    }

    public IllustrationSupport getIllustrationS() {
        return illustrationS;
    }

    public PathFinder getPathF() {
        return pathF;
    }

    public UserInterface getUi() {
        return ui;
    }

    public PrimaryGameState getPrimaryGameState() {
        return primaryGameState;
    }

    public boolean isLockPlayerControl() {
        return lockPlayerControl;
    }

    public boolean isRenderWorld() {
        return renderWorld;
    }

    public boolean isDebugActive() {
        return debugActive;
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

    public DialogueArrow getDialogueA() {
        return dialogueA;
    }

    public SelectionArrow getSelectionA() {
        return selectionA;
    }

    public TargetArrow getTargetA() {
        return targetA;
    }


    // SETTERS
    public void setPrimaryGameState(PrimaryGameState primaryGameState) {
        primaryGameStateInit(this.primaryGameState, primaryGameState);
        this.primaryGameState = primaryGameState;
    }

    public void setLockPlayerControl(boolean lockPlayerControl) {
        this.lockPlayerControl = lockPlayerControl;
    }

    public void setRenderWorld(boolean renderWorld) {
        this.renderWorld = renderWorld;
    }

    public void setDebugActive(boolean debugActive) {
        this.debugActive = debugActive;
    }
}
