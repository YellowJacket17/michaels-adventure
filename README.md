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
All `.wav` files are ommitted from the remote repository due to file size.
These files belong in the `src/main/resources/sound/` directory.
The application will run with them missing, but some warnings and errors will be printed to console.
In the `src/main/resources/sound/tracks/` directory, the application expects files named `testTrack2.wav`, `testTrack3.wav`, and `testTrack4.wav`.
In the `src/main/resources/sound/effects/` directory, the application expects a file named `testEffect1.wav`.

## Notes ##
Special thanks to RhiSnow and his YouTube series on Java game development, which provided essential foundations for getting this project off the ground.
