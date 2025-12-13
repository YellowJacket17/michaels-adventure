package core;

import ai.PathFinder;
import animation.PassiveAnimationManager;
import asset.AssetPool;
import asset.Illustration;
import combat.*;
import combat.support.CombatAnimationSupport;
import combat.support.CombatLoadSupport;
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
import particle.ParticleEffectManager;
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
import ui.UserInterface;
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
    private final PassiveAnimationManager passiveAnimationM = new PassiveAnimationManager();
    private final ParticleEffectManager particleEffectM = new ParticleEffectManager();
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
    private final CombatAnimationSupport combatAnimationS = new CombatAnimationSupport(this);
    private final CombatLoadSupport combatLoadS = new CombatLoadSupport(this);
    private final PathFinder pathF = new PathFinder(this);
    private final UserInterface ui = new UserInterface(this);


    // GAME STATE
    /**
     * Variable to store which primary state the game is currently in.
     * The primary game state drives how player inputs are registered.
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


    // IN-GAME SETTINGS
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
     * Background of the life/skill point meters that appear above an entity during combat.
     */
    private LifeSkillBannerBackground lifeSkillBannerBackground;

    /**
     * Background of the life meter that appears above an entity during combat.
     */
    private LifeBannerBackground lifeBannerBackground;

    /**
     * Guarding shield that appears next to an entities combat status banner when in a guarding state during combat.
     */
    private GuardingShield guardingShield;

    /**
     * Arrow that appears when the player is required to progress a piece of dialogue.
     */
    private DialogueArrow dialogueA;

    /**
     * Arrow that appears when the player is required to make a sub-menu selection.
     */
    private SelectionArrow selectionA;


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
        lifeSkillBannerBackground = new LifeSkillBannerBackground(this);
        lifeBannerBackground = new LifeBannerBackground(this);
        guardingShield = new GuardingShield(this);
        dialogueA = new DialogueArrow(this);
        selectionA = new SelectionArrow(this);

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
        cameraS.setTrackedEntity(entityM.getPlayer().getEntityId());

        // Initiate opening cutscene.
//        cutsceneM.initiateCutscene(0);
        fadeS.displayColor(new Vector3f(255, 255, 255));
        fadeS.initiateFadeFrom(0.5);

        // Other setup.
        environmentM.setup();

        // Set primary game state to player control.
        primaryGameState = PrimaryGameState.EXPLORE;

//        partyS.addEntityToParty(4, true);
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

        // Landmark.
        landmarkM.update(dt);

        // Animation.
        passiveAnimationM.update(dt);
        combatAnimationS.update(dt);

        // Particle Effect.
        particleEffectM.update(dt);

        // Entities.
        entityM.update(dt);

        // Entity icons.
        entityIconM.update(dt);

        // Environment.
//        environmentM.update();

        // Party.
        partyS.update(dt);

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
            for (EntityBase entity : entityList) {                                                                      // Render all entities in the current row.
                if (Math.ceil(entity.getWorldY() / NATIVE_TILE_SIZE) == row) {                                          // Calculate row this way to accommodate proper layering for tall grass landmark.
                    entity.addToRenderPipeline(renderer);
                }
            }
            for (LandmarkBase landmark : landmarkList) {                                                                // Render all landmarks in the current row.
                if (landmark.getRow() == row) {
                    landmark.addToRenderPipeline(renderer);
                }
            }
        }

        // Particle Effect.
        particleEffectM.addToRenderPipeline(renderer);

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
        AssetPool.addSpritesheet("tiles", new Spritesheet(AssetPool.getTexture(filePath), 204, 32, 32, 1));

        // Characters spritesheet (spritesheet 1).
        filePath = "/spritesheets/characters.png";
        AssetPool.addSpritesheet("characters", new Spritesheet(AssetPool.getTexture(filePath), 281, 32, 60, 1));

        // Landmarks spritesheet (spritesheet 2).
        filePath = "/spritesheets/landmarks.png";
        int[] widths = new int[] {32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32,
                32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32,
                32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 128, 128, 128, 128, 96, 96, 96, 96, 32,
                32};
        int[] heights = new int[] {32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32,
                32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32,
                32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 64, 64, 64, 64, 64, 64, 64, 64, 72, 72};
        AssetPool.addSpritesheet("landmarks", new Spritesheet(AssetPool.getTexture(filePath), 73, widths, heights, 1));

        // Items spritesheet (spritesheet 3).
        filePath = "/spritesheets/items.png";
        AssetPool.addSpritesheet("items", new Spritesheet(AssetPool.getTexture(filePath), 6, 32, 32, 1));

        // Icons spritesheet (spritesheet 4).
        filePath = "/spritesheets/icons.png";
        widths = new int[] {152, 152, 10, 10, 40, 36, 36, 28, 28, 28, 28, 28, 28, 6, 6, 6, 6};
        heights = new int[] {58, 58, 8, 8, 40, 36, 36, 28, 28, 28, 28, 28, 28, 10, 10, 10, 10};
        AssetPool.addSpritesheet("icons", new Spritesheet(AssetPool.getTexture(filePath), 17, widths, heights, 1));

        // Miscellaneous spritesheet (spritesheet 5).
        filePath = "/spritesheets/miscellaneous.png";
        widths = new int[] {48, 48, 14, 6, 10, 12, 24};
        heights = new int[] {20, 10, 18, 10, 6, 8, 12};
        AssetPool.addSpritesheet("miscellaneous", new Spritesheet(AssetPool.getTexture(filePath), 7, widths, heights, 1));

        // Sounds.
        AssetPool.addSound("endOfTheLine", "sound/tracks/endOfTheLine_intro.ogg", "sound/tracks/endOfTheLine_loop.ogg");
        AssetPool.addSound("dissipate", "sound/tracks/dissipate_intro.ogg", "sound/tracks/dissipate_loop.ogg");
        AssetPool.addSound("carvingCanyons", "sound/tracks/carvingCanyons_intro.ogg", "sound/tracks/carvingCanyons_loop.ogg");
        AssetPool.addSound("runningLate", "sound/tracks/runningLate_intro.ogg", "sound/tracks/runningLate_loop.ogg");
        AssetPool.addSound("riftInTime", "sound/tracks/riftInTime_intro.ogg", "sound/tracks/riftInTime_loop.ogg");
        AssetPool.addSound("yesteryear", "sound/tracks/yesteryear_intro.ogg", "sound/tracks/yesteryear_loop.ogg");
        AssetPool.addSound("pickpocket", "sound/effects/pickpocket.ogg");
        AssetPool.addSound("butterflyBlade", "sound/effects/butterflyBlade.ogg");
        AssetPool.addSound("burningDagger", "sound/effects/burningDagger.ogg");
        AssetPool.addSound("sneakstrike", "sound/effects/sneakstrike.ogg");
        AssetPool.addSound("heal", "sound/effects/heal.ogg");
        AssetPool.addSound("attributeDecrease", "sound/effects/attributeDecrease.ogg");
        AssetPool.addSound("hop", "sound/effects/hop.ogg");
        AssetPool.addSound("obtain", "sound/effects/obtain.ogg");

        // Illustrations.
        filePath = "/illustrations/illustration1.png";
        AssetPool.addIllustration("illustration1", new Illustration(AssetPool.getTexture(filePath)));
        filePath = "/illustrations/illustration2.png";
        AssetPool.addIllustration("illustration2", new Illustration(AssetPool.getTexture(filePath)));
        filePath = "/illustrations/illustration3.png";
        AssetPool.addIllustration("illustration3", new Illustration(AssetPool.getTexture(filePath)));
        filePath = "/illustrations/illustration5.png";
        AssetPool.addIllustration("illustration5", new Illustration(AssetPool.getTexture(filePath)));
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

    public PassiveAnimationManager getPassiveAnimationM() {
        return passiveAnimationM;
    }

    public ParticleEffectManager getParticleEffectM() {
        return particleEffectM;
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

    public CombatAnimationSupport getCombatAnimationS() {
        return combatAnimationS;
    }

    public CombatLoadSupport getCombatLoadS() {
        return combatLoadS;
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

    public combat.LifeSkillBannerBackground getLifeSkillBannerBackground() {
        return lifeSkillBannerBackground;
    }

    public LifeBannerBackground getLifeBannerBackground() {
        return lifeBannerBackground;
    }

    public GuardingShield getGuardingShield() {
        return guardingShield;
    }

    public DialogueArrow getDialogueA() {
        return dialogueA;
    }

    public SelectionArrow getSelectionA() {
        return selectionA;
    }


    // SETTERS
    public void setPrimaryGameState(PrimaryGameState primaryGameState) {
        this.primaryGameState = primaryGameState;
    }

    public void setLockPlayerControl(boolean lockPlayerControl) {
        if (UtilityTool.VERBOSE_LOGGING) {
            if (lockPlayerControl) {
                UtilityTool.logInfo("Locking player control.");
            } else {
                UtilityTool.logInfo("Unlocking player control.");
            }
        }
        this.lockPlayerControl = lockPlayerControl;
    }

    public void setRenderWorld(boolean renderWorld) {
        this.renderWorld = renderWorld;
    }

    public void setDebugActive(boolean debugActive) {
        this.debugActive = debugActive;
    }
}
