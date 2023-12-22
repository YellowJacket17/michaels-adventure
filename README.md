## About
**Developer:** Nicholas Kowalski

**IDE:** JetBrains IntelliJ IDEA

**Java version:** 11.0.13 (Eclipse Temurin)

**Dependency management:** Apache Maven

**Overview:** This project is a 2D game engine currently in development.

## Controls
**W:** Upward character movement and menu control.

**A:** Leftward character movement and menu control.

**S:** Downward character movement and menu control.

**D:** Rightward character movement and menu control.

**Enter:** Interaction with the world (talk to NPC, et cetera) and menu selection.

**Space:** Open/close the main menu.

**1:** Switch to the party section of the main menu.

**2:** Switch to the inventory section of the main menu.

**3:** Switch to the settings section of the main menu.

**Q:** Enable/disable debug mode.

**Up arrow:** Upward camera movement if debug mode is enabled.

**Down arrow:** Downward camera movement if debug mode is enabled.

**Left arrow:** Leftward camera movement if debug mode is enabled.

**Right arrow:** Rightward camera movement if debug mode is enabled.

## Audio
All `.wav` files are omitted from the remote repository due to file size.
These files belong in the `src/main/resources/sound/` directory.
The application will run with them missing, but some warnings and errors will be printed to console.
In the `src/main/resources/sound/tracks/` directory, the application expects files named `testTrack2.wav`, `testTrack3.wav`, and `testTrack4.wav`.
In the `src/main/resources/sound/effects/` directory, the application expects a file named `testEffect1.wav`.

## Notes ##
Compatible with Windows x64, macOS x64, and macOS arm64.

The combat system is incomplete.
When entering combat, the only menu options that currently function are "Skill" and "Flee".
All other options will break the combat loop.
Once selecting an attack within the "Skill" option, the player will be looped back to the menu options.

Within system settings, a setting labeled "Tether Game Speed" exists.
When enabled, this setting will tie game update logic to the frame rate limit.
In other words, if the actual frame rate dips below the target frame rate set in the "Frame Rate Limit" setting, then
the entire game will appear to slow down. Conversely, if the actual frame rate hits above the target frame rate, then
the entire game will appear to speed up.
Despite these quirks, tethering the game speed to the target frame rate can help the game to feel/run smoother.
This is due to more consistent frame times when updating game logic.
Each time game logic is updated, the exact same frame time will be used, equal to one over the target frame rate.
