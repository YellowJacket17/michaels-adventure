package combat;

/**
 * This enum defines the possible types of sub-menus that can be actioned during combat.
 */
public enum SubMenuType {

    /**
     * Select from a list of root combat options.
     */
    ROOT,

    /**
     * Select from a list of known moves that a player-side entity can use.
     */
    SKILL,

    /**
     * Select from a list of non-player-side entities to use a move on.
     */
    TARGET_SELECT,

    /**
     * Select from a list of all player-side entities (including the player entity) to manage.
     */
    PARTY,

    /**
     * Select from a list of management options for a player-side entity.
     */
    PLAYER_SIDE_MANAGE,

    /**
     * Select from a list of inactive party members to swap into combat.
     */
    PLAYER_SIDE_SWAP
}
