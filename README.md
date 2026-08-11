# Elemental Creepers for Better than Adventure

**Elemental Creepers 1.4** for Minecraft b1.7.3, ported to **BTA 8.0.1**.

Nine creepers that do something other than blow a hole in the ground.

| | |
|---|---|
| **Water** | Floods the ground around it. |
| **Fire** | Scatters fire across open ground. Immune to fire itself. |
| **Ice** | Freezes water to ice, lava to obsidian, snow over everything else. The widest effect of the nine. |
| **Electric** | Calls a lightning bolt down on every nearby creature. Other creepers are spared. |
| **Earth** | Buries its surroundings in dirt. |
| **Psychic** | Throws everything nearby into the sky and does no damage at all. The landing is the dangerous part. |
| **Cookie** | Harmless. Showers the area in cookies. Rare. |
| **Magma** | Leaves a trail of fire wherever it walks and pools lava where it dies. Nether only. |
| **Ghost** | See-through, and left behind when another elemental creeper dies. Its blast passes through walls without breaking them. |

They spawn naturally in every biome, from any mod, and work in singleplayer and on a dedicated
server.

## Requires your own copy of the original mod

**This mod ships none of the original's art.** The eight creeper skins are not this port's work, so
they are not in the repository and not in the released jar.

Download the original:

**https://b2.mcarchive.net/file/mcarchive/340befa2684a7336d8d5e7f418cc6e88c945f5596ab3edf73b4e157b527e09e7/ElementalCreepers_v1.4.zip**

`ElementalCreepers_v1.4.zip` — 95 KB, sha256 `340befa2684a7336d8d5e7f418cc6e88c945f5596ab3edf73b4e157b527e09e7`

Drop it anywhere under your game directory and it will be found and used automatically. It does not
have to be in `mods/`, does not have to keep its name, and does not have to be zipped — an unpacked
folder works too. Nothing is downloaded by the mod; the file has to already be on your disk. It is
only needed on the client.

Without it the mod still runs and every creeper behaves exactly as it should — the eight simply wear
the vanilla creeper skin, which makes them hard to tell apart until one goes off.

## Summoning

```
/summon elementalcreepers:firecreeper
```

**The namespace is required.** BTA only accepts a bare entity id for its own `minecraft:` mobs — for
anything else `/summon firecreeper` is rejected as an invalid entity. The nine ids are
`watercreeper` `firecreeper` `icecreeper` `electriccreeper` `earthcreeper` `psychiccreeper`
`cookiecreeper` `magmacreeper` `ghostcreeper`, and they are also printed at startup under
`Elemental Creepers summon ids:`.

## Playing with other mods

Designed to sit quietly next to anything else. It **registers no blocks, no items, no biomes, no
dimensions and no world types**, so the id space where conflicts actually happen is untouched. It
uses **no mixins**, so there is nothing to conflict with there either.

What it does register is nine entities under its own `elementalcreepers:` namespace. BTA keys
entities by namespaced string id and sends the server's numeric mapping to each client on join, so
mod load order cannot desynchronise them between a client and a server.

Spawn entries are added by sweeping the biome registry after every mod has registered, so other
mods' biomes are picked up automatically — including their Nether biomes, which the magma creeper
needs. Existing entries are never removed or reordered, and a biome whose monster list is already
empty is left completely alone, on the grounds that another mod meant it that way.

Tested alongside Biomes O' Plenty and Twilight Forest.

## Configuration

`config/Elemental Creepers.toml`, written on first launch. Spawn weights and effect sizes, one entry
per value the original exposed, at the original's defaults.

## Building

```bash
./gradlew build
```

Java 17 toolchain, Gradle wrapper included. The jar lands in `build/libs/`.

`check` runs a guard that fails the build if any of the original's art has found its way into the
tree. Run it alone with:

```bash
python tools/strip_sidecar_assets.py --check
```

## Requirements

- Better than Adventure 8.0.1
- HalpLibe 6.1.4+8.0 (bundled in the jar — a newer one you install yourself still wins)

## Credits

Elemental Creepers is somebody else's mod; the archive carries no author credit and this port does
not invent one. This is an unaffiliated port. The port's own Java is this repository's; the original
mod's art is not, and is read from your copy rather than redistributed.
