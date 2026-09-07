package combat.implementation.load;

import combat.CombatLoadBase;
import combat.implementation.action.Act_GenerateTutorial;
import core.GamePanel;
import core.enumeration.PrimaryGameState;
import entity.EntityBase;
import entity.enumeration.EntityDirection;
import utility.JsonParser;

public class Cbl_Scenario001 extends CombatLoadBase {

    // CONSTRUCTOR
    public Cbl_Scenario001(GamePanel gp) {
        super(gp);
    }


    // FIELDS
    @Override
    public void handleEnterCombatTransitionLoading() {

        if (!gp.getEntityM().getEntityById(5).isHidden() && !gp.getEntityM().getParty().containsKey(5)) {               // Ensure that Nick is hidden if not in party.

            gp.getEntityM().getEntityById(5).setHidden(true);
        }
    }


    @Override
    public void concludeEnterCombatTransition() {

        if (gp.getMapM().getLoadedMap().getMapState() == 0) {

            generateTutorialPage1();
            generateTutorialPage2();
            generateTutorialPage3();
            generateTutorialPage4();
            generateTutorialPage5();
            generateTutorialPage6();
            generateTutorialPage7();
            generateTutorialPage8();
            generateTutorialPage9();
        }
    }


    @Override
    public void handleExitCombatTransitionLoading(boolean combatLost) {

        if (!combatLost) {

            gp.getEntityM().getPlayer().setCol(43);
            gp.getEntityM().getPlayer().setRow(9);
            gp.getEntityM().getPlayer().setDirectionCurrent(EntityDirection.RIGHT);

            gp.getEntityM().getEntityById(4).setCol(45);
            gp.getEntityM().getEntityById(4).setRow(9);
            gp.getEntityM().getEntityById(4).setDirectionCurrent(EntityDirection.LEFT);

            gp.getEntityM().getEntityById(5).setCol(49);
            gp.getEntityM().getEntityById(5).setRow(9);
            gp.getEntityM().getEntityById(5).setDirectionCurrent(EntityDirection.LEFT);

            JsonParser.loadEntityJson(gp, 5);                                                                           // Load Nick for the post-combat cutscene.
            gp.getMapM().getLoadedMap().setMapState(2, false);                                                          // Set map to its post-combat (win) state.
            gp.getPartyS().hideAllPartyMembers(false);                                                                  // Just in case there are party members for whatever reason.

            gp.getCameraS().setOverrideEntityTracking(true);
            gp.getCameraS().setCameraSnap(1424, 288);
        } else {

            gp.getEntityM().getPlayer().setCol(47);
            gp.getEntityM().getPlayer().setRow(25);
            gp.getEntityM().getPlayer().setDirectionCurrent(EntityDirection.UP);

            gp.getEntityM().getEntityById(4).setCol(47);
            gp.getEntityM().getEntityById(4).setRow(8);
            gp.getEntityM().getEntityById(4).setDirectionCurrent(EntityDirection.UP);
            gp.getEntityM().getEntityById(4).resetPrimaryAttributes();                                                  // Restore primary attributes for re-fight.

            gp.getEntityM().getEntityById(5).setCol(47);
            gp.getEntityM().getEntityById(5).setRow(6);
            gp.getEntityM().getEntityById(5).setDirectionCurrent(EntityDirection.DOWN);

            if (gp.getEntityM().getEntityById(5).isHidden() && !gp.getEntityM().getParty().containsKey(5)) {            // Ensure that Joe is not hidden if not in party.

                gp.getEntityM().getEntityById(5).setHidden(false);
            }

            gp.getMapM().getLoadedMap().setMapState(1, false);                                                          // Set map to its post-combat (lose) state.
        }
        gp.getEntityM().getPlayer().resetPrimaryAttributes();                                                           // Note: Secondary attributes of all combating entities is automatically reset in the CombatManager class.

        for (int entityId : gp.getEntityM().getParty().keySet()) {                                                      // Just in case there are party members for whatever reason.

            gp.getEntityM().getParty().get(entityId).resetPrimaryAttributes();
        }
    }


    @Override
    public void concludeExitCombatTransition(boolean combatLost) {

        if (!combatLost) {

            gp.getCutsceneM().initiateCutscene(5);
        } else {

            gp.setPrimaryGameState(PrimaryGameState.EXPLORE);
        }
    }


    /**
     * Generates and displays the combat tutorial (page 1).
     */
    private void generateTutorialPage1() {

        String title = "Combat Tutorial";
        String subtitle = "Time to fight!";
        String content = "This tutorial will teach you the rules of combat.";
        int currentPageNumber = 1;
        int totalPageNumbers = 9;
        gp.getCombatM().addQueuedActionBack(
                new Act_GenerateTutorial(gp, title, subtitle, content, currentPageNumber, totalPageNumbers));
    }


    /**
     * Generates and displays the combat tutorial (page 2).
     */
    private void generateTutorialPage2() {

        String title = "Combat Tutorial";
        String subtitle = "Basic Rules";
        String content = "Combat is turn-based."
                + " Combatants will take turns exchanging attacks, with the goal of depleting opposing combatants' HP."
                + " When a combatant's HP reaches zero, they faint and can no longer participate in combat."
                + " Combat is won if all enemy combatants faint."
                + " Combat is lost if all ally combatants faint."
                + " If Mary faints, combat will be lost even if allies remain.";
        int currentPageNumber = 2;
        int totalPageNumbers = 9;
        gp.getCombatM().addQueuedActionBack(
                new Act_GenerateTutorial(gp, title, subtitle, content, currentPageNumber, totalPageNumbers));
    }


    /**
     * Generates and displays the combat tutorial (page 3).
     */
    private void generateTutorialPage3() {

        String title = "Combat Tutorial";
        String subtitle = "Attributes";
        String content = "HP - Health points. Combatant can no longer fight when zero.\n"
                + "SP - Skill points. Exchange for use of skills.\n"
                + "Attack - Affects power of outgoing physical attacks.\n"
                + "Magic - Affects power of outgoing magic attacks.\n"
                + "Defense - Affects power of incoming physical / magic attacks.\n"
                + "Agility - Determines turn order. Higher agility, higher priority.";
        int currentPageNumber = 3;
        int totalPageNumbers = 9;
        gp.getCombatM().addQueuedActionBack(
                new Act_GenerateTutorial(gp, title, subtitle, content, currentPageNumber, totalPageNumbers));
    }

    /**
     * Generates and displays the combat tutorial (page 4).
     */
    private void generateTutorialPage4() {

        String title = "Combat Tutorial";
        String subtitle = "Weaknesses";
        String content = "Each combatant has a weakness to either physical or magic attacks."
                + " (In rare cases, a combatant may have no weakness.)"
                + " A combatant will flash red and receive 50% more damage if their weakness is hit.";
        int currentPageNumber = 4;
        int totalPageNumbers = 9;
        gp.getCombatM().addQueuedActionBack(
                new Act_GenerateTutorial(gp, title, subtitle, content, currentPageNumber, totalPageNumbers));
    }


    /**
     * Generates and displays the combat tutorial (page 5).
     */
    private void generateTutorialPage5() {

        String title = "Combat Tutorial";
        String subtitle = "Buffs and Debuffs";
        String content = "Attack, magic, defense, and agility attributes can be increased or decreased during combat by various skills."
                + " Attributes are increased and decreased in increments of 25%."
                + " Each increment is referred to as a stage."
                + " Attributes can be buffed a maximum of four stages and debuffed a maximum of two stages."
                + " If a combatant faints, attribute buffs and debuffs will be removed."
                + " However, if a combatant swaps out of active combat, attribute buffs and debuffs will remain.";
        int currentPageNumber = 5;
        int totalPageNumbers = 9;
        gp.getCombatM().addQueuedActionBack(
                new Act_GenerateTutorial(gp, title, subtitle, content, currentPageNumber, totalPageNumbers));
    }


    /**
     * Generates and displays the combat tutorial (page 6).
     */
    private void generateTutorialPage6() {

        String title = "Combat Tutorial";
        String subtitle = "Guarding";
        String content = "In the root combat menu, the 'Guard' option will place the selected combatant in a guarding state."
//                + " A guarding state is indicated by a shield next to an entity's HP bar."
                + " When in a guarding state, a combatant will receive 50% less damage from incoming attacks, both physical and magic."
                + " Once a combatant's turn comes around again, they will exit a guarding state."
                + " If a combatant faints or swaps out of active combat, they will be removed from a guarding state.";
        int currentPageNumber = 6;
        int totalPageNumbers = 9;
        gp.getCombatM().addQueuedActionBack(
                new Act_GenerateTutorial(gp, title, subtitle, content, currentPageNumber, totalPageNumbers));
    }


    /**
     * Generates and displays the combat tutorial (page 7).
     */
    private void generateTutorialPage7() {

        String title = "Combat Tutorial";
        String subtitle = "Basic Attack";
        String content = "In the root combat menu, the 'Attack' option will cause the selected combatant to perform a basic attack."
                + " A basic attack is physical and uses no skill points.";
        int currentPageNumber = 7;
        int totalPageNumbers = 9;
        gp.getCombatM().addQueuedActionBack(
                new Act_GenerateTutorial(gp, title, subtitle, content, currentPageNumber, totalPageNumbers));
    }


    /**
     * Generates and displays the combat tutorial (page 8).
     */
    private void generateTutorialPage8() {

        String title = "Combat Tutorial";
        String subtitle = "Skills";
        String content = "In the root combat menu, the 'Skill' option will allow the selected combatant to perform one of their skills."
                + " Skills are categorized under physical, magic, or support."
//                + " Physical skills use a combatant's physical attribute, and magic skills use a combatant's magic attribute."
                + " Physical and magic skills directly attack a combatant and may also apply effects (e.g., steal HP)."
                + " Support skills do not directly attack a combatant, instead focusing on applying effects.";
        int currentPageNumber = 8;
        int totalPageNumbers = 9;
        gp.getCombatM().addQueuedActionBack(
                new Act_GenerateTutorial(gp, title, subtitle, content, currentPageNumber, totalPageNumbers));
    }


    /**
     * Generates and displays the combat tutorial (page 9).
     */
    private void generateTutorialPage9() {

        String title = "Combat Tutorial";
        String subtitle = "Managing Party";
        String content = "In the root combat menu, the 'Party' option will allow the player to view and manage all ally combatants."
                + " This option will become more useful as the number of allies grows.";
        int currentPageNumber = 9;
        int totalPageNumbers = 9;
        gp.getCombatM().addQueuedActionBack(
                new Act_GenerateTutorial(gp, title, subtitle, content, currentPageNumber, totalPageNumbers));
    }
}
