# ModPanel

Client-side Fabric mod (Minecraft 1.21.1) that generates configuration GUIs from verified mod settings.

## Usage
- Install Fabric Loader, Fabric API, Cloth Config, and (optional) Mod Menu / Text Placeholder API.
- Launch game, press M or use Mod Menu to open ModPanel.
- Select a supported mod (Lithium supported) and edit settings.

## Supported integrations
- Lithium (properties config)
- Generic Properties/JSON/TOML (read-only)

## Build
`./gradlew build` (Java 21 target, Gradle 9.7.1 with Loom 1.18.4).
