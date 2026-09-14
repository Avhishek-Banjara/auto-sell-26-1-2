# Auto Sell for Fabric 26.1.2

A client-only Fabric mod that sends `/sell all` at a random interval of 80 to 90 seconds.
The timer starts fresh after every join or reconnect. No key press is required.

## Requirements
- Minecraft Java Edition 26.1.2
- Java 25
- Fabric Loader 0.18.6 or newer
- Fabric API compatible with 26.1.2

## Build
1. Install JDK 25.
2. Open this folder in IntelliJ IDEA 2025.3+ as a Gradle project, or run `gradle build` with Gradle installed.
3. Copy the normal JAR from `build/libs/` into `.minecraft/mods`. Do not use the `-sources` JAR.

## Change command or interval
Edit `AutoSellClient.java`:
- Command: `sendCommand("sell all")`
- Minimum: `MIN_DELAY_MS = 80_000L`
- Maximum: `MAX_DELAY_MS = 90_000L`

## Important
Verify that the multiplayer server allows automatic commands/macros. This mod intentionally waits 80-90 seconds after each reconnect before its first command.
