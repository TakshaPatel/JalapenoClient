# Jalapeno Client

A custom Minecraft client framework built on **MCP-Reborn** (Java 25 / Minecraft 26.2).
Annotation-driven module system, an event bus, a custom HUD, and 5 built-in modules.

> **Disclaimer:** For educational purposes only. Not affiliated with Mojang/Microsoft. See [DISCLAIMER.md](DISCLAIMER.md).

## Features

- **Annotation-based modules** — tag a class with `@ModuleInfo` and it auto-registers via class scanning; no manual wiring.
- **Event bus** — `EventBus` posts typed events (e.g. `KeyPressEvent`) to decoupled listeners.
- **Custom HUD** — right-side module list with keybinds and ON/OFF state (green/gray).
- **5 built-in modules.**

## Modules

| Module | Category | Key | Description |
|---|---|---|---|
| Fly | MOVEMENT | F | Creative-style flight with fall-damage cancellation |
| Criticals | COMBAT | G | No attack cooldown + always land critical hits |
| HealthPlus | PLAYER | H | Regen, fire resistance, 40 max hearts (`/effect` commands) |
| KeepInventory | PLAYER | Y | Keep inventory & XP on death (gamerule + server command) |
| Test | MISC | R | No-op test module |
| (Global) | — | X | Sends `nerd` in chat (meme/quick chat) |

## Architecture

| File | Role |
|---|---|
| `Jalapeno/Jalapeno.java` | `enum INSTANCE` singleton; init/tick/shutdown lifecycle, window title |
| `Jalapeno/module/Module.java` | abstract base with toggle, `onEnable`/`onDisable`/`onTick` |
| `Jalapeno/module/ModuleManager.java` | scans `.class` files for `@ModuleInfo`, registers, ticks |
| `Jalapeno/module/ModuleInfo.java` | `@interface` annotation (name, category, key, description) |
| `Jalapeno/module/Category.java` | enum: COMBAT, MOVEMENT, RENDER, PLAYER, MISC |
| `Jalapeno/event/Event.java`, `EventBus.java`, `KeyPressEvent.java` | typed event bus |

## Prerequisites

- **JDK 25** (Minecraft 26.2 mappings). Built/tested on macOS.
- No IDE required — the Gradle wrapper handles everything.

## Build & Run

First-time decompile (downloads & decompiles Minecraft 26.2 source; needs ~6GB heap):

```bash
_JAVA_OPTIONS=-Xmx6G ./gradlew setup
```

Run the client:

```bash
./gradlew runclient
```

> On machines without the official launcher assets, run `./gradlew copyAssets` once. The default build config reuses existing launcher assets from `~/Library/Application Support/minecraft/assets`.

## Custom development

### Adding a module

```java
@ModuleInfo(name = "MyModule", category = Category.MISC, key = GLFW.GLFW_KEY_M, description = "Does a thing")
public class MyModule extends Module {
    @Override protected void onEnable()   { /* run when toggled on */ }
    @Override protected void onDisable()  { /* run when toggled off */ }
    @Override protected void onTick()     { /* run every tick while enabled */ }
}
```

`ModuleManager` finds it automatically — no registration code needed.

### Key hook points in the decompiled source

The vanilla client files are patched at these integration points (the modified files themselves are **not** redistributable):

- `net.minecraft.client.Minecraft` — `init()`/`updateTitle()`/`tick()`/`close()` call `Jalapeno.INSTANCE`
- `net.minecraft.client.KeyboardHandler.keyPress` — posts `KeyPressEvent`
- `net.minecraft.client.gui.Hud` — renders the Jalapeno HUD
- `net.minecraft.client.player.LocalPlayer` — on-ground spoofing for Fly
- `net.minecraft.client.multiplayer.MultiPlayerGameMode` — critical-hit packets
- `net.minecraft.server.level.ServerPlayer` — keep-inventory death handling
- `net.minecraft.world.entity.player.Player` — crit/health/inventory logic

## Project layout

```
src/main/java/Jalapeno/            <- this framework
  event/    Event, EventBus, KeyPressEvent
  module/   Module, ModuleManager, ModuleInfo, Category
    combat/   Criticals
    movement/ Fly
    player/   HealthPlus, KeepInventory
    misc/     TestModule
build.gradle                       <- ForgeGradle patcher config (JDK 25)
```

## License & attribution

- Jalapeno code: MIT — see [LICENSE](LICENSE).
- Build toolchain derived from **MCP-Reborn** (MIT) by Hexeption: https://github.com/Hexeption/MCP-Reborn
- `MCP-License` governs the MCP (decompilation) toolchain usage.
- Decompiled Minecraft/Mojang source is **not** included in this repository.