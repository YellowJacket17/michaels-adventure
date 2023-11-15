package interaction.support;

import core.GamePanel;
import miscellaneous.GameState;
import miscellaneous.TransitionPhase;
import miscellaneous.TransitionType;
import entity.EntityBase;
import entity.EntityDirection;
import interaction.WarpTransitionType;
import utility.LimitedLinkedHashMap;

/**
 * This class contains methods to facilitate warping.
 */
public class WarpSupport {

    // FIELDS
    private final GamePanel gp;

    /**
     * Stored location to warp to.
     */
    private int stagedMapId, stagedCol, stagedRow;

    /**
     * Stored music track to swap in during a transition.
     * A value of -1 means the current music track will continue playing.
     */
    private int stagedTrackIndex = -1;

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
     *
     * @param mapId ID of the map that the player entity will be warped to
     * @param col column that the player entity will be warped to
     * @param row row that the player entity will be warped to
     */
    public void initiateWarp(int mapId, int col, int row) {

        boolean newMap = false;                                                                                         // Track whether we're warping to a new map (true) or a new point on the current map (false).

        if (mapId != gp.getLoadedMap().getMapId()) {                                                                    // Only load the map if it's different from the one the player is warping from.

            newMap = true;
            gp.loadMap(mapId);                                                                                          // Set the map the player is on.
        }
        gp.getPlayer().setCol(col);                                                                                     // Set the player's position in the world (x)
        gp.getPlayer().setRow(row);                                                                                     // Set the player's position in the world (y).

        if (gp.getPlayer().isMoving()) {                                                                                // If the player is moving, adjust the ending position to be correct.
            switch (gp.getPlayer().getDirectionCandidate()) {
                case UP:
                    gp.getPlayer().setRowEnd(gp.getPlayer().getRowEnd() - 1);
                    break;
                case DOWN:
                    gp.getPlayer().setRowEnd(gp.getPlayer().getRowEnd() + 1);
                    break;
                case LEFT:
                    gp.getPlayer().setColEnd(gp.getPlayer().getColEnd() - 1);
                    break;
                case RIGHT:
                    gp.getPlayer().setColEnd(gp.getPlayer().getColEnd() + 1);
                    break;
            }
        }

        if (newMap) {                                                                                                   // If a new map is loaded, warp all party members to the player and have them follow the player.

            gp.getInteractionM().breakFollowerChain(gp.getPlayer());                                                    // Reset the entities following the player.

            for (EntityBase entity : gp.getParty().values()) {

                if (entity != null) {

                    entity.cancelAction();                                                                              // Resets the number of pixels traversed from a cancelled movement.
                    entity.setDirectionCurrent(gp.getPlayer().getDirectionCandidate());                                 // Have the party member face the same direction as the player when warped.
                    entity.setCol(col);
                    entity.setRow(row);

                    gp.getInteractionM().setEntityFollowTarget(entity.getEntityId(), gp.getPlayer().getEntityId());     // Set the party member to follow the player.
                }
            }
        } else {                                                                                                        // If a new map is not loaded, warp any followers trailing the player along with the player.

            warpFollowersToPlayer(gp.getParty());                                                                       // Check party members.
            warpFollowersToPlayer(gp.getNpc());                                                                         // Check NPCs.
            warpFollowersToPlayer(gp.getObj());                                                                         // Check objects (just in case).
        }
    }


    /**
     * Warps the player to a new location.
     * The warp is dressed with a fade-to-black transition.
     * The game state is set to transition.
     *
     * @param mapId ID of the map that the player entity will be warped to
     * @param col column that the player entity will be warped to
     * @param row row that the player entity will be warped to
     * @param type type of warp transition; see comments in the WarpTransitionType enum for definitions of different types
     * @param loadDirection direction that the player entity will be facing once the transition completes
     * @param trackIndex music track index in the `musicURL` array in the Sound class; input -1 if no music swap is desired
     */
    public void initiateWarp(int mapId, int col, int row, WarpTransitionType type,
                             EntityDirection loadDirection, int trackIndex) {

        gp.setGameState(GameState.TRANSITION);                                                                          // Set the game to a transition state.
        gp.setActiveTransitionType(TransitionType.WARP);                                                                // Set the overarching transition type as warp (different from WarpTransitionType).
        gp.setActiveTransitionPhase(TransitionPhase.FADING_TO);                                                         // Set the first phase of the transition (fade out to black); this will actually trigger the transition (fade-to-black effect) when the game state is in a transition state.
        activeWarpTransitionType = type;                                                                                // Set the warp current transition type being used.

        stagedMapId = mapId;                                                                                            // Store the requested map.
        stagedCol = col;                                                                                                // Store the requested player position (x).
        stagedRow = row;                                                                                                // Store the requested player position (y).

        if (trackIndex >= 0) {

            stagedTrackIndex = trackIndex;                                                                              // Set the music track to be swapped in during the transition.
        } else {

            stagedTrackIndex = -1;                                                                                      // A value less than zero means that the music track will not swap out during the transition.
        }

        switch (type) {
            case BASIC:
                gp.getPlayer().cancelAction();                                                                          // Cancel the player action that triggered the transition (for example, walking into a trigger tile).
                break;
            case STEP_PORTAL:
                gp.getPlayer().updateWarpTransitionStepPortal();                                                        // Initiate the first phase of this transition type for the player entity.
                gp.getPlayer().setDirectionCandidate(loadDirection);                                                    // Set the direction that the player entity will be facing when loaded into the new map.
                break;
        }
    }


    /**
     * Performs any loading that needs to be done once the screen fades to black during a warp transition.
     */
    public void handleWarpTransitionLoading() {

        initiateWarp(stagedMapId, stagedCol, stagedRow);

        switch (activeWarpTransitionType) {
            case BASIC:
                // Nothing here.
                break;
            case STEP_PORTAL:
                gp.getPlayer().updateWarpTransitionStepPortal();                                                        // Initiate the second phase of this transition type for the player entity.
                break;
        }

        if (stagedTrackIndex >= 0) {                                                                                    // Check to see if music is set to be swapped during the transition.

            gp.swapMusic(stagedTrackIndex);                                                                             // Swap music.
        }
    }


    /**
     * Closes out a warp transition that has completed all of its phases (i.e., tidies up any variables).
     * This is to be run once a warp transition has fully completed.
     * The game state is set to explore to return control to the player.
     */
    public void concludeWarpTransition() {

        reset();
        gp.setGameState(GameState.EXPLORE);
    }


    /**
     * Warps followers to the player entity.
     *
     * @param target entity map to be checked for entities following the player entity
     */
    public void warpFollowersToPlayer(LimitedLinkedHashMap<Integer, EntityBase> target) {

        for (EntityBase entity : target.values()) {

            if ((entity != null)
                    && (gp.getInteractionM().checkEntityChainUp(gp.getPlayer(), entity))) {

                entity.cancelAction();                                                                                  // Resets the number of pixels traversed from a cancelled movement.
                entity.setDirectionCurrent(gp.getPlayer().getDirectionCurrent());                                       // Have the party member face the same direction as the player when warped.
                entity.setCol(gp.getPlayer().getCol());
                entity.setRow(gp.getPlayer().getRow());
            }
        }
    }


    /**
     * Resets WarpSupport back to its default state.
     * Intended to be called to clean up after a warp transition has finished.
     */
    private void reset() {

        stagedMapId = 0;
        stagedCol = 0;
        stagedRow = 0;
        activeWarpTransitionType = null;
        stagedTrackIndex = -1;
    }


    // GETTERS
    public int getStagedMapId() {
        return stagedMapId;
    }

    public int getStagedCol() {
        return stagedCol;
    }

    public int getStagedRow() {
        return stagedRow;
    }

    public int getStagedTrackIndex() {
        return stagedTrackIndex;
    }

    public WarpTransitionType getActiveWarpTransitionType() {
        return activeWarpTransitionType;
    }
}
