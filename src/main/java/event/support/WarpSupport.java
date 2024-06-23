package event.support;

import asset.Sound;
import core.GamePanel;
import entity.EntityBase;
import entity.enumeration.EntityDirection;
import event.enumeration.TransitionType;
import event.enumeration.WarpTransitionType;
import utility.LimitedLinkedHashMap;

import java.util.HashMap;

/**
 * This class contains methods to facilitate warping.
 * The public methods in this class serve as primary endpoints to use when programming in-game events.
 */
public class WarpSupport {

    // FIELDS
    private final GamePanel gp;

    /**
     * Stored location to warp to.
     */
    private int stagedMapId, stagedMapState, stagedCol, stagedRow;

    /**
     * Stored track to swap in during a transition if map track is not overridden.
     */
    private String stagedTrackName = Sound.NO_TRACK;

    /**
     * Boolean indicating whether the track specified by a map is overridden (true) or not (false) during a transition.
     */
    private boolean overrideMapTrack = false;

    /**
     * Variable to store the current warp transition type being performed (null if none).
     */
    private WarpTransitionType activeWarpTransitionType;


    // CONSTRUCTOR
    /**
     * Constructs a WarpSupport instance.
     *
     * @param gp GamePanel instance
     */
    public WarpSupport(GamePanel gp) {
        this.gp = gp;
    }


    // METHODS
    /**
     * Warps the player to a new location.
     * Any followers (both party and non-party) will be warped as well.
     * Any additional entities in the `npc` and `obj` (hash)maps will be purged if warping to a new map.
     * To retain these additional entities, they should first be transferred to the `standby` (hash)map.
     *
     * @param mapId ID of the map that the player entity will be warped to
     * @param mapState state of the map that the player entity will be warped to
     * @param col column that the player entity will be warped to
     * @param row row that the player entity will be warped to
     */
    public void initiateWarp(int mapId, int mapState, int col, int row) {

        HashMap<Integer, Integer> nonPartyFollowers = new HashMap<>();                                                  // Key is entity ID of non-party player entity follower, value is source map (0 for `npc`, 1 for `obj`).

        boolean newMap = false;

        if (mapId != gp.getMapM().getLoadedMap().getMapId()) {                                                          // Only load the map if it's different from the one the player is warping from.

            newMap = true;

            for (EntityBase entity : gp.getEntityM().getNpc().values()) {                                               // Transfer any non-party NPC player entity followers to `standby` map.

                if (gp.getEventM().checkEntityChainUp(gp.getEntityM().getPlayer(), entity)) {

                    gp.getEntityM().transferEntity(
                            gp.getEntityM().getNpc(), gp.getEntityM().getStandby(), entity.getEntityId());
                    nonPartyFollowers.put(entity.getEntityId(), 0);
                }
            }

            for (EntityBase entity : gp.getEntityM().getObj().values()) {                                               // Transfer any non-party object player entity followers to `standby` map.

                if (gp.getEventM().checkEntityChainUp(gp.getEntityM().getPlayer(), entity)) {

                    gp.getEntityM().transferEntity(gp.getEntityM().getObj(), gp.getEntityM().getStandby(), entity.getEntityId());
                    nonPartyFollowers.put(entity.getEntityId(), 1);
                }
            }
            gp.getMapM().loadMap(mapId, mapState, !overrideMapTrack);                                                   // Load new map now that any non-party player entity followers have been transferred to `standby` map.
        }
        gp.getEntityM().getPlayer().setCol(col);                                                                        // Set the player entity's position in the world (x)
        gp.getEntityM().getPlayer().setRow(row);                                                                        // Set the player entity's position in the world (y).

        if (gp.getEntityM().getPlayer().isMoving()) {                                                                   // If the player entity is moving, adjust the ending position to be correct.
            switch (gp.getEntityM().getPlayer().getDirectionCandidate()) {
                case UP:
                    gp.getEntityM().getPlayer().setRowEnd(gp.getEntityM().getPlayer().getRowEnd() - 1);
                    break;
                case DOWN:
                    gp.getEntityM().getPlayer().setRowEnd(gp.getEntityM().getPlayer().getRowEnd() + 1);
                    break;
                case LEFT:
                    gp.getEntityM().getPlayer().setColEnd(gp.getEntityM().getPlayer().getColEnd() - 1);
                    break;
                case RIGHT:
                    gp.getEntityM().getPlayer().setColEnd(gp.getEntityM().getPlayer().getColEnd() + 1);
                    break;
            }
        }
        warpFollowersToFollowed(gp.getEntityM().getPlayer(), gp.getEntityM().getParty());                               // Check party members.

        if (newMap) {                                                                                                   // If a new map is loaded, warp all party members to the player and have them follow the player.

            warpFollowersToFollowed(gp.getEntityM().getPlayer(), gp.getEntityM().getStandby());                         // Check standby entities, since non-party followers were transferred here.
        } else {

            warpFollowersToFollowed(gp.getEntityM().getPlayer(), gp.getEntityM().getNpc());                             // Check NPCs.
            warpFollowersToFollowed(gp.getEntityM().getPlayer(), gp.getEntityM().getObj());                             // Check objects (just in case).
        }

        if (newMap) {

            for (int entityId : nonPartyFollowers.keySet()) {                                                           // Transfer non-party player entity followers back from `standby` map now that loading is complete.

                switch (nonPartyFollowers.get(entityId)) {
                    case 0:
                        gp.getEntityM().transferEntity(
                                gp.getEntityM().getStandby(), gp.getEntityM().getNpc(), entityId);
                        break;
                    case 1:
                        gp.getEntityM().transferEntity(
                                gp.getEntityM().getStandby(), gp.getEntityM().getObj(), entityId);
                        break;
                }
            }
        }
    }


    /**
     * Warps the player to a new location.
     * The warp is dressed with a fade-to-black transition.
     * The track specified by the map being warped to will not automatically play.
     * Any additional entities in the `npc` and `obj` (hash)maps will be purged if warping to a new map.
     * To retain these additional entities, they should first be transferred to the `standby` (hash)map.
     *
     * @param dt time since last frame (seconds)
     * @param mapId ID of the map that the player entity will be warped to
     * @param mapState state of the map that the player entity will be warped to
     * @param col column that the player entity will be warped to
     * @param row row that the player entity will be warped to
     * @param type type of warp transition
     * @param loadDirection direction that the player entity will be facing once the transition completes
     * @param trackName name/title of track to be swapped in during transition (Sound.NO_TRACK to swap to no track
     *                  playing, Sound.RETAIN_TRACK to retain current track playing)
     */
    public void initiateWarp(double dt, int mapId, int mapState, int col, int row, WarpTransitionType type,
                             EntityDirection loadDirection, String trackName) {

        gp.getTransitionS().initiateTransition(TransitionType.WARP);
        activeWarpTransitionType = type;                                                                                // Set the warp current transition type being used.
        stagedMapId = mapId;                                                                                            // Store the requested map.
        stagedMapState = mapState;                                                                                      // Store the requested map state.
        stagedCol = col;                                                                                                // Store the requested player position (x).
        stagedRow = row;                                                                                                // Store the requested player position (y).
        stagedTrackName = trackName;                                                                                    // Set the track to swap in during transition.
        overrideMapTrack = true;                                                                                        // Set to ensure that, when `gp.loadMap()` is called, the loaded map's track is not automatically swapped in.

        switch (type) {
            case BASIC:
                gp.getEntityM().getPlayer().cancelAction();                                                             // Cancel the player action that triggered the transition (for example, walking into a trigger tile).
                break;
            case STEP_PORTAL:
                gp.getEntityM().getPlayer().updateWarpTransitionStepPortal(dt);                                         // Initiate the first phase of this transition type for the player entity.
                gp.getEntityM().getPlayer().setDirectionCandidate(loadDirection);                                       // Set the direction that the player entity will be facing when loaded into the new map.
                break;
        }
    }


    /**
     * Warps the player to a new location.
     * The warp is dressed with a fade-to-black transition.
     * The track specified by the map being warped to will automatically play.
     * Any additional entities in the `npc` and `obj` (hash)maps will be purged if warping to a new map.
     * To retain these additional entities, they should first be transferred to the `standby` (hash)map.
     *
     * @param dt time since last frame (seconds)
     * @param mapId ID of the map that the player entity will be warped to
     * @param mapState state of the map that the player entity will be warped to
     * @param col column that the player entity will be warped to
     * @param row row that the player entity will be warped to
     * @param type type of warp transition
     * @param loadDirection direction that the player entity will be facing once the transition completes
     */
    public void initiateWarp(double dt, int mapId, int mapState, int col, int row, WarpTransitionType type,
                             EntityDirection loadDirection) {

        gp.getTransitionS().initiateTransition(TransitionType.WARP);
        activeWarpTransitionType = type;                                                                                // Set the warp current transition type being used.
        stagedMapId = mapId;                                                                                            // Store the requested map.
        stagedMapState = mapState;                                                                                      // Store the requested map state.
        stagedCol = col;                                                                                                // Store the requested player position (x).
        stagedRow = row;                                                                                                // Store the requested player position (y).

        switch (type) {
            case BASIC:
                gp.getEntityM().getPlayer().cancelAction();                                                             // Cancel the player action that triggered the transition (for example, walking into a trigger tile).
                break;
            case STEP_PORTAL:
                gp.getEntityM().getPlayer().updateWarpTransitionStepPortal(dt);                                         // Initiate the first phase of this transition type for the player entity.
                gp.getEntityM().getPlayer().setDirectionCandidate(loadDirection);                                       // Set the direction that the player entity will be facing when loaded into the new map.
                break;
        }
    }


    /**
     * Performs any loading that needs to be done once the screen fades to black during a warp transition.
     *
     * @param dt time since last frame (seconds)
     */
    public void handleWarpTransitionLoading(double dt) {

        switch (activeWarpTransitionType) {
            case BASIC:
                // Nothing here.
                break;
            case STEP_PORTAL:
                gp.getEntityM().getPlayer().updateWarpTransitionStepPortal(dt);                                         // Initiate the second phase of this transition type for the player entity.
                break;
        }
        initiateWarp(stagedMapId, stagedMapState, stagedCol, stagedRow);

        if (overrideMapTrack) {

            if (stagedTrackName.equals(Sound.NO_TRACK)) {

                gp.getSoundS().stopTrack(true);
            } else if (!stagedTrackName.equals(Sound.RETAIN_TRACK)) {

                gp.getSoundS().swapTrack(stagedTrackName, true);
            }
        }
    }


    /**
     * Closes out a warp transition that has completed all of its phases (i.e., tidies up any variables).
     * This is to be run once a warp transition has fully completed.
     */
    public void concludeWarpTransition() {

        reset();
    }


    /**
     * Warps all followers in the specified entity map to the followed entity's position.
     * Warped followers will be placed on the same tile as the followed, facing the same direction as the followed.
     * Any action that the followed were performing will be cancelled.
     *
     * @param followed followed entity to warp followers to
     * @param entityMap entity map to be checked for entities following the player entity
     */
    public void warpFollowersToFollowed(EntityBase followed, LimitedLinkedHashMap<Integer, EntityBase> entityMap) {

        for (EntityBase entity : entityMap.values()) {

            if ((entity != null)
                    && (gp.getEventM().checkEntityChainUp(followed, entity))) {

                entity.cancelAction();                                                                                  // Resets the number of pixels traversed from a cancelled movement.
                entity.setDirectionCurrent(followed.getDirectionCurrent());
                entity.setCol(followed.getCol());
                entity.setRow(followed.getRow());
            }
        }
    }


    /**
     * Resets WarpSupport back to its default state.
     * Intended to be called to clean up after a warp transition has finished.
     */
    private void reset() {

        stagedMapId = 0;
        stagedMapState = 0;
        stagedCol = 0;
        stagedRow = 0;
        activeWarpTransitionType = null;
        stagedTrackName = Sound.NO_TRACK;
        overrideMapTrack = false;
    }
}
