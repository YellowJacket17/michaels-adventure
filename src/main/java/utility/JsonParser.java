package utility;

import combat.AttackBase;
import combat.implementation.attack.Atk_Tackle;
import dialogue.Conversation;
import dialogue.Dialogue;
import entity.EntityBase;
import entity.DefaultIdle;
import entity.implementation.character.Npc_Test1;
import entity.implementation.character.Npc_Test2;
import entity.implementation.object.Obj_Chest;
import entity.implementation.object.Obj_Controller;
import entity.implementation.object.Obj_Key;
import core.GamePanel;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.*;

import map.Map;
import utility.exceptions.JsonParseException;

/**
 * This class handles the loading and parsing of JSON data from file.
 */
public class JsonParser {

    // FIELD
    private final GamePanel gp;


    // CONSTRUCTOR
    /**
     * Constructs a JsonParser instance.
     *
     * @param gp GamePanel instance
     */
    public JsonParser(GamePanel gp) {
        this.gp = gp;
    }


    // METHODS
    /**
     * Loads and instantiates a specified map from JSON data.
     *
     * @param mapId ID of map to be loaded
     * @return loaded map data
     * @throws JsonParseException if an error occurs while loading map from JSON
     */
    public Map loadMapJson(int mapId) {

        JSONParser parser = new JSONParser();

        try (InputStream is = getClass().getResourceAsStream("/json/maps.json")) {

            String contents = readFromInputStream(is);

            Object maps = parser.parse(contents);
            JSONObject mapsJson = (JSONObject)maps;
            JSONObject mapJson = (JSONObject)mapsJson.get(Integer.toString(mapId));                                     // Retrieve the appropriate map from the loaded JSON data.

            Map map = new Map(gp, mapId);

            boolean dayNightCycle = (boolean)mapJson.get("dayNightCycle");
            map.setDayNightCycle(dayNightCycle);

            return map;

        } catch (Exception e) {

            throw new JsonParseException(e.getMessage());
        }
    }


    /**
     * Loads and instantiates entities from JSON data for a specified map.
     *
     * @param mapId ID of map where entities are being loaded
     * @throws JsonParseException if an error occurs while loading entities from JSON
     */
    public void loadEntitiesJson(int mapId) {

        JSONParser parser = new JSONParser();

        try (InputStream is = getClass().getResourceAsStream("/json/entities.json")) {

            String contents = readFromInputStream(is);

            Object entities = parser.parse(contents);
            JSONObject entitiesJson = (JSONObject)entities;

            for (int i = 1; i <= entitiesJson.size(); i++) {

                JSONObject entityJson = (JSONObject)entitiesJson.get(Integer.toString(i));                              // Retrieve entity i from the loaded JSON data.

                boolean valid = true;                                                                                   // A check to see if the entity can be loaded.

                // Check if the entity is on the target map.
                int entityMapId = (int)((long)entityJson.get("map"));

                if (entityMapId != mapId) {

                    valid = false;                                                                                      // Entity is not on the target map, so don't load it.
                }

                // Check if the entity is in the `party` map in GamePanel.
                if (valid) {

                    for (int entityId : gp.getParty().keySet()) {

                        if ((gp.getParty().get(entityId) != null)
                                && (entityId == i)) {

                            valid = false;                                                                              // Entity is in the `party` map, so avoid loading a duplicate.
                            break;
                        }
                    }
                }

                // Check if the entity is in the `standby` map in GamePanel.
                if (valid) {

                    for (int entityId : gp.getStandby().keySet()) {

                        if ((gp.getStandby().get(entityId) != null)
                                && (entityId == i)) {

                            valid = false;                                                                              // Entity is in the `standby` map, so avoid loading a duplicate.
                            break;
                        }
                    }
                }

                // Check if the entity ID is in the `removedEntities` set in GamePanel.
                if (valid) {

                    if (gp.getRemovedEntities().contains(i)) {

                        valid = false;                                                                                  // Entity is in the `removedEntities` set, so skip loading.
                    }
                }

                if (valid) {                                                                                            // See if the entity can be loaded.

                    // Entity class.
                    String entityClass = (String)entityJson.get("class");                                               // Get the class of the entity.

                    EntityBase entity = null;

                    switch (entityClass) {

                        // Add additional entity subclasses here.

                        case "Npc_Test1":
                            entity = new Npc_Test1(gp, i);
                            break;
                        case "Npc_Test2":
                            entity = new Npc_Test2(gp, i);
                            break;
                        case "Obj_Chest":
                            entity = new Obj_Chest(gp, i);
                            break;
                        case "Obj_Controller":
                            entity = new Obj_Controller(gp, i);
                            break;
                        case "Obj_Key":
                            entity = new Obj_Key(gp, i);
                            break;
                    }

                    // Collision.
                    // Collision is optional, hence the try-catch statement.
                    boolean collision;
                    try {
                        collision = (boolean)entityJson.get("collision");
                    } catch (NullPointerException e) {
                        collision = true;
                    }
                    entity.setCollision(collision);

                    // Hidden.
                    // Hidden is optional, hence the try-catch statement.
                    boolean hidden;
                    try {
                        hidden = (boolean)entityJson.get("hidden");
                    } catch (NullPointerException e) {
                        hidden = false;
                    }
                    entity.setHidden(hidden);

                    // Default position.
                    JSONObject positionJson = (JSONObject)entityJson.get("position");
                    int defaultCol = (int)((long)positionJson.get("defaultCol"));
                    int defaultRow = (int)((long)positionJson.get("defaultRow"));
                    entity.setCol(defaultCol);
                    entity.setRow(defaultRow);

                    // Attributes.
                    // All attributes are optional, hence the try-catch statements.
                    JSONObject attributesJson;
                    try {
                        attributesJson = (JSONObject)entityJson.get("attributes");
                    } catch (NullPointerException e) {
                        attributesJson = null;
                    }

                    String name = "";
                    try {
                        name = (String)attributesJson.get("name");
                    } catch (NullPointerException e) {
                        // Nothing here.
                    }
                    int speed;
                    try {
                        speed = (int)((long)attributesJson.get("speed"));
                    } catch (NullPointerException e) {
                        speed = 2;
                    }
                    int maxLife;
                    try {
                        maxLife = (int)((long)attributesJson.get("maxLife"));
                    } catch (NullPointerException e) {
                        maxLife = 0;
                    }
                    int life;
                    try {
                        life = (int)((long)attributesJson.get("life"));
                    } catch (NullPointerException e) {
                        life = 0;
                    }
                    int maxSkillPoints;
                    try {
                        maxSkillPoints = (int)((long)attributesJson.get("maxSkillPoints"));
                    } catch (NullPointerException e) {
                        maxSkillPoints = 0;
                    }
                    int skillPoints;
                    try {
                        skillPoints = (int)((long)attributesJson.get("skillPoints"));
                    } catch (NullPointerException e) {
                        skillPoints = 0;
                    }
                    int baseAttack;
                    try {
                        baseAttack = (int)((long)attributesJson.get("baseAttack"));
                    } catch (NullPointerException e) {
                        baseAttack = 0;
                    }
                    int baseDefense;
                    try {
                        baseDefense = (int)((long)attributesJson.get("baseDefense"));
                    } catch (NullPointerException e) {
                        baseDefense = 0;
                    }
                    int baseMagic;
                    try {
                        baseMagic = (int)((long)attributesJson.get("baseMagic"));
                    } catch (NullPointerException e) {
                        baseMagic = 0;
                    }
                    int baseAgility;
                    try {
                        baseAgility = (int)((long)attributesJson.get("baseAgility"));
                    } catch (NullPointerException e) {
                        baseAgility = 0;
                    }
                    int exp;
                    try {
                        exp = (int)((long)attributesJson.get("exp"));
                    } catch (NullPointerException e) {
                        exp = 0;
                    }
                    int level;
                    try {
                        level = (int)((long)attributesJson.get("level"));
                    } catch (NullPointerException e) {
                        level = 0;
                    }
                    int nextLevelExp;
                    try {
                        nextLevelExp = (int)((long)attributesJson.get("nextLevelExp"));
                    } catch (NullPointerException e) {
                        nextLevelExp = 0;
                    }

                    if (!name.equals("")) {
                        entity.setName(name);
                    }
                    entity.setSpeed(speed);
                    entity.setMaxLife(maxLife);
                    entity.setLife(life);
                    entity.setMaxSkillPoints(maxSkillPoints);
                    entity.setSkillPoints(skillPoints);
                    entity.setBaseAttack(baseAttack);
                    entity.setBaseDefense(baseDefense);
                    entity.setBaseMagic(baseMagic);
                    entity.setBaseAgility(baseAgility);
                    entity.setExp(exp);
                    entity.setLevel(level);
                    entity.setNextLevelExp(nextLevelExp);

                    // Attacks.
                    JSONObject attacksJson = (JSONObject)entityJson.get("attacks");
                    try {
                        int attackId = (int)((long)attacksJson.get("slot1"));
                        AttackBase attack = instantiateAttack(attackId);
                        if (attack != null) {
                            entity.getAttacks().add(attack);
                        }
                    } catch (NullPointerException e) {
                        // Nothing here.
                    }
                    try {
                        int attackId = (int)((long)attacksJson.get("slot2"));
                        AttackBase attack = instantiateAttack(attackId);
                        if (attack != null) {
                            entity.getAttacks().add(attack);
                        }
                    } catch (NullPointerException e) {
                        // Nothing here.
                    }
                    try {
                        int attackId = (int)((long)attacksJson.get("slot3"));
                        AttackBase attack = instantiateAttack(attackId);
                        if (attack != null) {
                            entity.getAttacks().add(attack);
                        }
                    } catch (NullPointerException e) {
                        // Nothing here.
                    }
                    try {
                        int attackId = (int)((long)attacksJson.get("slot4"));
                        AttackBase attack = instantiateAttack(attackId);
                        if (attack != null) {
                            entity.getAttacks().add(attack);
                        }
                    } catch (NullPointerException e) {
                        // Nothing here.
                    }

                    // Idle action.
                    String defaultAction = (String)entityJson.get("defaultAction");

                    switch (defaultAction) {
                        case "randomSteps":
                            entity.setDefaultAction(DefaultIdle.RANDOM_STEPS);
                            break;
                        case "randomTurns":
                            entity.setDefaultAction(DefaultIdle.RANDOM_TURNS);
                            break;
                        default:
                            entity.setDefaultAction(DefaultIdle.STATIC);
                    }

                    // Type.
                    String type = (String)entityJson.get("type");

                    switch (type) {
                        case "character":
                            gp.getNpc().put(entity.getEntityId(), entity);
                            break;
                        case "object":
                            gp.getObj().put(entity.getEntityId(), entity);
                            break;
                    }
                }
            }

        } catch (Exception e) {

            throw new JsonParseException(e.getMessage());
        }
    }


    /**
     * Loads and instantiates conversations with dialogue from JSON data for a specified map.
     *
     * @param mapId ID of map where dialogue is being loaded
     * @throws JsonParseException if an error occurs while loading dialogue from JSON
     */
    public void loadDialogueJson(int mapId) {

        JSONParser parser = new JSONParser();

        try (InputStream is = getClass().getResourceAsStream("/json/dialogue.json")) {

            String contents = readFromInputStream(is);

            Object conversations = parser.parse(contents);
            JSONObject conversationsJson = (JSONObject)conversations;

            for (int i = 0; i < conversationsJson.size(); i++) {

                JSONObject conversationJson = (JSONObject)conversationsJson.get(Integer.toString(i));

                int conversationMapId = (int)((long)conversationJson.get("map"));

                if (conversationMapId == mapId) {

                    Conversation conversation = new Conversation(i);

                    // Conversation name is optional, hence the try-catch statement.
                    String convName;
                    try {
                        convName = (String)conversationJson.get("convName");
                    } catch (NullPointerException e) {
                        convName = "";
                    }
                    conversation.setName(convName);

                    // Player input to end is optional, hence the try-catch statement.
                    boolean playerInputToEnd;
                    try {
                        playerInputToEnd = (boolean)conversationJson.get("playerInputToEnd");
                    } catch (NullPointerException e) {
                        playerInputToEnd = true;
                    }
                    conversation.setPlayerInputToEnd(playerInputToEnd);

                    JSONObject allDialogueJson = (JSONObject)conversationJson.get("dialogue");

                    for (int j = 0; j < allDialogueJson.size(); j++) {

                        JSONObject dialogueJson = (JSONObject)allDialogueJson.get(Integer.toString(j));

                        Dialogue dialogue = new Dialogue();

                        String speakerName = (String)dialogueJson.get("speakerName");
                        String text = (String)dialogueJson.get("text");
                        dialogue.setEntityName(speakerName);
                        dialogue.setText(text);

                        conversation.getDialogueList().add(dialogue);
                    }
                    gp.getConv().put(conversation.getConvId(), conversation);
                }
            }

        } catch (Exception e) {

            throw new JsonParseException(e.getMessage());
        }
    }


    /**
     * Extracts file contents from an input stream and returns them as a string.
     *
     * @param inputStream file input stream
     * @return file contents
     * @throws IOException
     */
    private String readFromInputStream(InputStream inputStream) throws IOException {

        StringBuilder resultStringBuilder = new StringBuilder();

        try (BufferedReader br = new BufferedReader(new InputStreamReader(inputStream))) {

            String line;

            while ((line = br.readLine()) != null) {

                resultStringBuilder.append(line).append("\n");
            }
        }
        return resultStringBuilder.toString();
    }


    /**
     * Instantiates an appropriate attack subclass based on the inputted attack ID.
     *
     * @param attackId ID of attack to be instantiated
     * @return attack
     */
    private AttackBase instantiateAttack(int attackId) {

        AttackBase attack = null;

        switch (attackId) {
            case 0:
                attack = new Atk_Tackle(gp);
        }
        return attack;
    }
}
