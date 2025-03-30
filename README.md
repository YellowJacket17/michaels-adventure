## About
**Developer:** Nicholas Kowalski

**IDE:** JetBrains IntelliJ IDEA

**Java version:** 17.0.8 (Eclipse Temurin)

**Dependency management:** Apache Maven

**Overview:** This project is a 2D game engine currently in development.

## Controls
**W:** Upward character movement and menu control.

**A:** Leftward character movement and menu control.

**S:** Downward character movement and menu control.

**D:** Rightward character movement and menu control.

**Enter:** Interaction with the world (talk to NPC, et cetera) and menu selection.

**Space:** Open/close the main menu.

**F11:** Enter full screen mode when full screen mode is disabled.

**Esc:** Exit full screen mode when full screen mode is enabled.

**Q:** Scroll left in main menu to change visible section (party, inventory, settings).

**E:** Scroll right in main menu to change visible section (party, inventory, settings).

**P:** Enable/disable debug mode.

**Up arrow:** Upward camera movement if debug mode is enabled.

**Down arrow:** Downward camera movement if debug mode is enabled.

**Left arrow:** Leftward camera movement if debug mode is enabled.

**Right arrow:** Rightward camera movement if debug mode is enabled.

## Audio
All `.ogg` files are omitted from the remote repository due to file size.
These files belong in the `sound` directory in the project root.
The application will not run without being able to load them.

## Illustrations
All illustrations are omitted from the remote repository due to file size.
These files belong in the `illustrations` directory within the `resources` directory.
The `illustrations` directory is omitted altogether from the remote repository.

## JSON Guidelines ##

In the below guidelines, all text contained within square brackets is meant to be replaced in actual JSON files.
Any lines/fields denoted with an asterisk "*" at the beginning are optional and may be omitted in a JSON element if not relevant.

### maps.json ###

```
{
    "<map_ID>": {
        "tracks": {
            "<first_map_state>": "<track_resource_name>",
            "<second_map_state>": "<track_resource_name>",
            ... up to unlimited ...
        }
    },
    ... up to unlimited ...
}
```

Map IDs MUST start at "0" in the JSON file, working down as 0, 1, 2, etc.
Map IDs are loaded into the game as the order in which they appear in the JSON file.
For example, even if the map ID at the top of the JSON file read "3", it would still be loaded into the game as map with ID "0".

Tracks must be specified for a map.
It is recommended to at minimum specify a track for a default map state "0".
In this case, game logic would only expect a map to have that one state (i.e., state "0").
If, during run time, it is attempted to set a map to a map state that it has no track for, an exception will occur.
If no track is to be played for a given map state, simply input "NO_TRACK" as the value.
Note that map state fields do not necessarily need to be listed as "0", "1", etc. (i.e., could be listed as "1", "3", etc.).

### dialogue.json ###

```
{
    "<conversation_ID>": {
        "map": <ID_of_map_to_load_conversation_into>,
        *"convName": "<conversation_name>",
        *"playerInputToEnd": <true/false>,
        "dialogue": {
            "0": {
                "speakerName": "<name_of_entity_delivering_first_piece_of_dialogue>",
                "text": "<content_of_first_piece_of_dialogue>"
            },
            "1": {
                "speakerName": "<name_of_entity_delivering_second_piece_of_dialogue>",
                "text": "<content_of_second_piece_of_dialogue>"
            },
            ... up to unlimited ...
        }
    },
    ... up to unlimited ...
}
```

Conversation IDs MUST start at "0" in the JSON file, working down as 0, 1, 2, etc.
Conversation IDs are loaded into the game as the order in which they appear in the JSON file.
For example, even if the conversation ID at the top of the JSON file read "3", it would still be loaded into the game as conversation with ID "0".

The "map" field indicates which map a conversation should automatically be loaded into.
In other words, when said map is loaded, the conversation will also be loaded and placed appropriately.
If a conversation is not tied to a particular map, simply omit this field and load it individually.

If the "convName" field is omitted from a JSON element, it will automatically be set as an empty string.

If the "playerInputToEnd" field is omitted from a JSON element, it will automatically be set as "true".

Note that the "playerInputToEnd" field determines whether game event logic will automatically progress upon reading the last piece of conversation dialogue (true) or not (false).

Dialogue IDs MUST start at "0" in the JSON file, working down as 0, 1, 2, etc.

### entities.json ###

```
{
    "<entity_ID>": {
        "type": "<character/object>",
        "class": "<name_of_entity_subclass>",
        "defaultAction": <action_name>,
        *"collision": <true/false>,
        *"hidden": <true/false>,
        *"map": <ID_of_map_to_load_entity_into>,
        *"mapStates": [
            <first_map_state>,
            <_second_map_state>,
            ... up to unlimited ...
        ],
        "position": {
            "defaultCol": <default_overworld_tile_column>,
            "defaultRow": <default_overworld_row_column>
        },
        *"attributes": {
            *"name": "<entity_name>",
            *"speed": <entity_overworld_walking_speed>,
            *"maxLife": <maximum_life_points>,
            *"life": <remaining_life_points>,
            *"maxSkill": <maximum_skill_points>,
            *"skill": <remaining_skill_points>,
            *"baseAttack": <base_attack_points>,
            *"baseDefense": <base_defense_points>,
            *"baseMagic": <base_magic_points>,
            *"baseAgility": <base_agility_points>,
            *"exp": <accumulated_experience_points>,
            *"level": <level>,
            *"nextLevelExp": <experience_points_required_to_level_up>
        },
        *"moves": {
            "1": <move_ID>,
            "2": <move_ID>,
            ... up to "4" ...
        }
    }
}
```

Entity IDs MUST start at "1" in the JSON file, working down as 1, 2, 3, etc.
Entity IDs are loaded into the game as the order in which they appear in the JSON file.
For example, even if the entity ID at the top of the JSON file read "3", it would still be loaded into the game as entity with ID "1".
Note that entity ID "0" is reserved for the player entity, which is not present in the JSON file.
This is why entity IDs start at "1" in the JSON file, not "0".

The two options for the "type" field are "character" and "object".
If anything else is put, the entity will not be loaded from JSON data.

The "class" field determines what subclass an entity is.
Essentially, this determines an entity's appearance.
For example, if the name of a subclass is "Npc_Test1", then "Npc_Test1" is what would be written as the value.

The "defaultAction" field determines an entity's default action/behavior in the overworld.
The options are:
+ "static"
+ "randomSteps"
+ "randomTurns"

Anything other than the above three will automatically result in "static" as a default value.

The "collision" field determines whether an entity is solid or not.
For example, if set to "false", other entities will be able to walk through the entity.
If set to "true", other entities will not be able to walk through the entity.

The "hidden" field determines whether an entity will be invisible by default.

The "map" field indicates which map an entity should automatically be loaded into.
In other words, when said map is loaded, the entity will also be loaded and placed appropriately.
If an entity is not tied to a particular map, simply omit this field and load the entity individually.

If the "mapStates" array is omitted, then the entity will be loaded into the map specified in the "map" field regardless of map state.
This field is not used if an entity is loaded individually.

Any omitted attributes will be assigned a default value of "0", with the exceptions of "name" and "speed".
The "name" field will be assigned an empty string by default.
The "speed" field will be assigned a value of "2" by default.
As an aside, the "attributes" field may be omitted altogether, which is useful when creating objects, not NPCs.

The "exp", "level", and "nextLevelExp" attribute fields are not used in the game.
It is recommended that they be omitted from the JSON file.

Up to four moves under the "moves" field may be included.  
If any more than four are included, they will simply be ignored when loading JSON data.

## Notes ##
Able to compile with Windows x64, macOS x64, and macOS arm64 natives.
Note that the program has only been fully tested on Windows x64 and runs fine there.
On macOS x64, a window will appear and the game loop will start with music playing, but no visuals will draw.
No testing has been done with macOS arm64.

Within system settings, a setting labeled "Tether Game Speed" exists.
When enabled, this setting will tie game update logic to the frame rate limit.
In other words, if the actual frame rate dips below the target frame rate set in the "Frame Rate Limit" setting, then
the entire game will appear to slow down. Conversely, if the actual frame rate hits above the target frame rate, then
the entire game will appear to speed up.
Despite these quirks, tethering the game speed to the target frame rate can help the game to feel/run smoother.
This is due to more consistent frame times when updating game logic.
Each time game logic is updated, the exact same frame time will be used, equal to one over the target frame rate.
