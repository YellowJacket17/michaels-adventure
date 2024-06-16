package utility;

import asset.Sound;
import combat.MoveBase;
import combat.implementation.move.Mve_Tackle;
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

    // METHODS
    /**
     * Loads and instantiates a specified map from JSON data.
     *
     * @param gp GamePanel instance to load into
     * @param mapId ID of map to load
     * @return loaded map data
     * @throws JsonParseException if an error occurs while loading map from JSON
     */
    public static Map loadMapJson(GamePanel gp, int mapId) {

        JSONParser parser = new JSONParser();

        try (InputStream is = JsonParser.class.getResourceAsStream("/json/maps.json")) {

            String contents = readFromInputStream(is);

            Object maps = parser.parse(contents);
            JSONObject mapsJson = (JSONObject)maps;
            JSONObject mapJson = (JSONObject)mapsJson.get(Integer.toString(mapId));                                     // Retrieve the appropriate map from the loaded JSON data.

            return parseMap(gp, mapId, mapJson);

        } catch (Exception e) {

            throw new JsonParseException(e.getMessage());
        }
    }


    /**
     * Loads and instantiates conversations from JSON data for a specified map.
     *
     * @param gp GamePanel instance to load into
     * @param mapId ID of map where conversation with dialogue is being loaded
     * @throws JsonParseException if an error occurs while loading conversation/dialogue from JSON
     */
    public static void loadConversationsJson(GamePanel gp, int mapId) {

        JSONParser parser = new JSONParser();

        try (InputStream is = JsonParser.class.getResourceAsStream("/json/dialogue.json")) {

            String contents = readFromInputStream(is);

            Object conversations = parser.parse(contents);
            JSONObject conversationsJson = (JSONObject)conversations;

            boolean onMap;

            for (int i = 0; i < conversationsJson.size(); i++) {                                                        // Retrieve conversation (with "i" corresponding to conversation ID) from the loaded JSON data.

                JSONObject conversationJson = (JSONObject)conversationsJson.get(Integer.toString(i));

                // Check if the conversation is set to be loaded on the target map.
                // Map ID is optional, hence the try-catch statement.
                try {

                    onMap = true;                                                                                       // Reset to check the following conversation.
                    int conversationMapId = (int)((long)conversationJson.get("map"));

                    if (conversationMapId != mapId) {

                        onMap = false;                                                                                  // Conversation is not on the target map, so don't load it.
                    }

                    if (onMap && !checkConversationLoaded(gp, i)) {

                        parseConversation(gp, i, conversationJson);
                    }

                } catch (NullPointerException e) {}                                                                     // Do nothing (i.e., simply do not load the conversation).
            }

        } catch (Exception e) {

            throw new JsonParseException(e.getMessage());
        }
    }


    /**
     * Loads and instantiates a conversation from JSON data.
     * If the conversation is already loaded, nothing will happen.
     *
     * @param gp GamePanel instance to load into
     * @param convId ID of conversation with dialogue to load
     * @throws JsonParseException if an error occurs while loading conversation from JSON
     */
    public static void loadConversationJson(GamePanel gp, int convId) {

        JSONParser parser = new JSONParser();

        try (InputStream is = JsonParser.class.getResourceAsStream("/json/dialogue.json")) {

            String contents = readFromInputStream(is);

            Object conversations = parser.parse(contents);
            JSONObject conversationsJson = (JSONObject)conversations;

            if (!checkConversationLoaded(gp, convId)) {

                JSONObject conversationJson = (JSONObject)conversationsJson.get(Integer.toString(convId));
                parseConversation(gp, convId, conversationJson);
            }
        } catch (Exception e) {

            throw new JsonParseException(e.getMessage());
        }
    }


    /**
     * Loads and instantiates entities from JSON data for a specified map.
     * Any entities already loaded will be ignored.
     *
     * @param gp GamePanel instance to load into
     * @param mapId ID of map into which entities are being loaded
     * @throws JsonParseException if an error occurs while loading entities from JSON
     */
    public static void loadEntitiesJson(GamePanel gp, int mapId) {

        JSONParser parser = new JSONParser();

        try (InputStream is = JsonParser.class.getResourceAsStream("/json/entities.json")) {

            String contents = readFromInputStream(is);

            Object entities = parser.parse(contents);
            JSONObject entitiesJson = (JSONObject)entities;

            boolean onMap;

            for (int i = 1; i <= entitiesJson.size(); i++) {                                                            // Start at entity ID 1, since entity ID 0 is reserved for the player entity.

                JSONObject entityJson = (JSONObject)entitiesJson.get(Integer.toString(i));                              // Retrieve entity (with "i" corresponding to entity ID) from the loaded JSON data.

                // Check if the entity is set to be loaded on the target map.
                // Map ID is optional, hence the try-catch statement.
                try {

                    onMap = true;                                                                                       // Reset to check the following entity.
                    int entityMapId = (int)((long)entityJson.get("map"));

                    if (entityMapId != mapId) {

                        onMap = false;                                                                                  // Entity is not on the target map, so don't load it.
                    }

                    if (onMap && !checkEntityLoaded(gp, i)) {

                        parseEntity(gp, i, entityJson);
                    }

                } catch (NullPointerException e) {}                                                                     // Do nothing (i.e., simply do not load the entity).
            }

        } catch (Exception e) {

            throw new JsonParseException(e.getMessage());
        }
    }


    /**
     * Loads and instantiates a single entity from JSON data.
     * If the entity is already loaded, nothing will happen.
     *
     * @param gp GamePanel instance to load into
     * @param entityId ID of entity to load
     * @throws JsonParseException if an error occurs while loading entity from JSON
     */
    public static void loadEntityJson(GamePanel gp, int entityId) {

        JSONParser parser = new JSONParser();

        try (InputStream is = JsonParser.class.getResourceAsStream("/json/entities.json")) {

            String contents = readFromInputStream(is);

            Object entities = parser.parse(contents);
            JSONObject entitiesJson = (JSONObject)entities;

            if (!checkEntityLoaded(gp, entityId)) {                                                                     // See if the entity can be loaded.

                JSONObject entityJson = (JSONObject)entitiesJson.get(Integer.toString(entityId));
                parseEntity(gp, entityId, entityJson);
            }

        } catch (Exception e) {

            throw new JsonParseException(e.getMessage());
        }
    }


    /**
     * Parses a map from JSON data and returns it to be added to memory.
     *
     * @param gp GamePanel instance to load into
     * @param mapId ID of map to parse
     * @param mapJson JSON data representing map to parse
     */
    private static Map parseMap(GamePanel gp, int mapId, JSONObject mapJson) {

        Map map = new Map(gp, mapId);

        JSONObject tracksJson = (JSONObject)mapJson.get("tracks");

        for (int i = 0; i < tracksJson.size(); i++) {

            String track = (String)tracksJson.get(Integer.toString(i));                                                 // Retrieve next track, regardless of its index in the JSON file.

            if (track.equals("NO_TRACK")) {

                track = Sound.NO_TRACK;
            }
            map.getTracks().add(track);                                                                                 // Add track to next index in list.
        }
        return map;
    }


    /**
     * Parses a conversation from JSON data and adds it in memory.
     *
     * @param gp GamePanel instance to load into
     * @param convId ID of conversation with dialogue to parse
     * @param conversationJson JSON data representing conversation with dialogue to parse
     */
    private static void parseConversation(GamePanel gp, int convId, JSONObject conversationJson) {

        Conversation conversation = new Conversation(convId);

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

            JSONObject dialogueJson = (JSONObject)allDialogueJson.get(Integer.toString(j));                             // Retrieve next piece of dialogue, regardless of its index in the JSON file.

            Dialogue dialogue = new Dialogue();

            String speakerName = (String)dialogueJson.get("speakerName");
            String text = (String)dialogueJson.get("text");
            dialogue.setEntityName(speakerName);
            dialogue.setText(text);

            conversation.getDialogueList().add(dialogue);                                                               // Add dialogue to next index in list.
        }
        gp.getDialogueR().getConv().put(conversation.getConvId(), conversation);
    }


    /**
     * Parses an entity from JSON data and adds it in memory.
     *
     * @param gp GamePanel instance to load into
     * @param entityId ID of entity to parse
     * @param entityJson JSON data representing entity to parse
     */
    private static void parseEntity(GamePanel gp, int entityId, JSONObject entityJson) {

        // Entity class.
        String entityClass = (String)entityJson.get("class");                                                           // Get the class of the entity.

        EntityBase entity = null;

        switch (entityClass) {

            // Add additional entity subclasses here.
            case "Npc_Test1":
                entity = new Npc_Test1(gp, entityId);
                break;
            case "Npc_Test2":
                entity = new Npc_Test2(gp, entityId);
                break;
            case "Obj_Chest":
                entity = new Obj_Chest(gp, entityId);
                break;
            case "Obj_Controller":
                entity = new Obj_Controller(gp, entityId);
                break;
            case "Obj_Key":
                entity = new Obj_Key(gp, entityId);
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

        // Combat moves.
        JSONObject movesJson = (JSONObject)entityJson.get("moves");
        try {
            int moveId = (int)((long)movesJson.get("1"));
            MoveBase move = instantiateMove(gp, moveId);
            if (move != null) {
                entity.getMoves().add(move);
            }
        } catch (NullPointerException e) {
            // Nothing here.
        }
        try {
            int moveId = (int)((long)movesJson.get("2"));
            MoveBase move = instantiateMove(gp, moveId);
            if (move != null) {
                entity.getMoves().add(move);
            }
        } catch (NullPointerException e) {
            // Nothing here.
        }
        try {
            int moveId = (int)((long)movesJson.get("3"));
            MoveBase move = instantiateMove(gp, moveId);
            if (move != null) {
                entity.getMoves().add(move);
            }
        } catch (NullPointerException e) {
            // Nothing here.
        }
        try {
            int moveId = (int)((long)movesJson.get("4"));
            MoveBase move = instantiateMove(gp, moveId);
            if (move != null) {
                entity.getMoves().add(move);
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
                gp.getEntityM().getNpc().put(entity.getEntityId(), entity);
                break;
            case "object":
                gp.getEntityM().getObj().put(entity.getEntityId(), entity);
                break;
        }
    }


    /**
     * Checks to see if a conversation is already loaded.
     *
     * @param gp GamePanel instance to load into
     * @param convId ID of conversation with dialogue to check
     * @return whether the conversation is already loaded (true) or not (false)
     */
    private static boolean checkConversationLoaded(GamePanel gp, int convId) {

        for (int loadedId : gp.getDialogueR().getConv().keySet()) {

            if ((gp.getDialogueR().getConv().get(loadedId) != null)
                    && (loadedId == convId)) {

                return true;                                                                                            // Conversation is in the `conv` map, so avoid loading a duplicate.
            }
        }
        return false;
    }


    /**
     * Checks to see if an entity is already loaded.
     *
     * @param gp GamePanel instance to load into
     * @param entityId ID of entity to check
     * @return whether the entity is already loaded (true) or not (false)
     */
    private static boolean checkEntityLoaded(GamePanel gp, int entityId) {

        for (int loadedId : gp.getEntityM().getNpc().keySet()) {

            if ((gp.getEntityM().getNpc().get(loadedId) != null)
                    && (loadedId == entityId)) {

                return true;                                                                                            // Entity is in the `npc` map, so avoid loading a duplicate.
            }
        }

        for (int loadedId : gp.getEntityM().getObj().keySet()) {

            if ((gp.getEntityM().getObj().get(loadedId) != null)
                    && (loadedId == entityId)) {

                return true;                                                                                            // Entity is in the `obj` map, so avoid loading a duplicate.
            }
        }

        for (int loadedId : gp.getEntityM().getParty().keySet()) {

            if ((gp.getEntityM().getParty().get(loadedId) != null)
                    && (loadedId == entityId)) {

                return true;                                                                                            // Entity is in the `party` map, so avoid loading a duplicate.
            }
        }

        for (int loadedId : gp.getEntityM().getStandby().keySet()) {

            if ((gp.getEntityM().getStandby().get(loadedId) != null)
                    && (loadedId == entityId)) {

                return true;                                                                                            // Entity is in the `standby` map, so avoid loading a duplicate.
            }
        }

        if (gp.getEntityM().getRemovedEntities().contains(entityId)) {

            return true;                                                                                                // Entity is in the `removedEntities` set, so skip loading.
        }
        return false;
    }


    /**
     * Extracts file contents from an input stream and returns them as a string.
     *
     * @param inputStream file input stream
     * @return file contents
     * @throws IOException
     */
    private static String readFromInputStream(InputStream inputStream) throws IOException {

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
     * Instantiates an appropriate combat move subclass based on the inputted move ID.
     *
     * @param gp GamePanel instance to load into
     * @param moveId ID of move to be instantiated
     * @return attack
     */
    private static MoveBase instantiateMove(GamePanel gp, int moveId) {

        MoveBase move = null;

        switch (moveId) {
            case 1:
                move = new Mve_Tackle(gp);
        }
        return move;
    }
}
