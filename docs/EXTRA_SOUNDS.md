# Extra Sounds

Client-only sounds adapted from Extra Sounds for Minecraft 1.7.10. Settings are under
**Sounds > Extra Sounds** in the config GUI, or `extra_sounds` in the config file.

| Setting | Action |
| --- | --- |
| `extraInventoryClicks` | Pickup, placement, shift-click, hotbar swap, completed drag distribution, double-click collection |
| `extraCreativeActions` | Middle-click clone and partial/all deletion in creative inventory |
| `extraItemDrops` | Q, stack drops, inventory drops, and creative palette drops |
| `extraInventoryScreens` | Container screen opening and closing |
| `extraScreenScroll` | Creative inventory row changes, including scrollbar dragging |
| `extraChat` | Received messages and mentions; own messages never count as mentions |
| `extraTyping` | Accepted text edits and cursor/selection changes in text fields, signs, and writable books |
| `extraScreenshots` | Successfully saved screenshots |
| `extraStatusEffects` | Beneficial/harmful potion additions and removals on the local player |
| `extraRepeaters` | Increasing delay and wrapping back to the first delay |
| `extraJukebox` | Ejecting a record |
| `extraEntityPoof` | Living entity death particles; **off by default** |
| `extraSoundsVolume` | Group multiplier, 0 to 2, default 1 |

All toggles except entity poof default to on. Player-category volume also applies to
UI and inventory sounds. Group settings do not change AET's existing sounds.
Potion state is baselined on joining and respawning, avoiding a burst of fake changes.
Inventory sounds follow successful local changes; server-side rejection cannot be
predicted by a client-only mod. Block interaction cues likewise follow the local attempt.

## Resource packs

Export the Extra Sounds tab in SoundWorkbench using namespace `anextratouch`.
Events retain the upstream names, including `item.pickup`, `item.category.*`,
`item.pickup_all`, `item.drag`, `item.clone`, `item.delete_partial`, `item.delete_all`,
`item.drop`, `inventory.open`, `inventory.close`, `generic.scroll`, `chat.message`,
`chat.mention`, `keyboard.*`, `effect.*`, `action.repeater.*`, `action.jukebox.eject`,
and `entity.poof`. `keyboard.screenshot` references `screenshot`; saving plays one cue.

Item clicks look for `item.pickup.<namespace>.<item>.<metadata>` first, then
`item.pickup.<namespace>.<item>`. Explicit pack/default events win. Unlisted items
fall back to AET's item classifier; block items use the block's step sound. Generated
fallbacks follow classifier changes and are discarded by sound resource reloads.
As in vanilla sounds.json, use `"replace": true` when replacing an existing event.
AET supports namespaced event aliases with volume and pitch, repairing 1.7.10's
alias handling for AET definitions only.

The 33 original recordings are included under CC0. AET includes a relocated copy
of MCLib 0.3.7.8 and requests all modern events it uses from Minecraft 1.21.10 through
AssetDirector. The client downloads the needed sound files from Mojang and caches
them; modern recordings and Minecraft jars are not bundled or downloaded wholesale.
EFR remains optional. Its available events are preferred, and AssetDirector shares
requests and cached files with other MCLib users. AET supplies missing events even
when EFR is absent. Resource-pack definitions of the requested event take precedence.

If downloaded assets are unavailable, local defaults use 1.7.10 and AET stand-ins.
The mapping and source revision are in
[EXTRA_SOUND_CREDITS.txt](../src/main/resources/assets/anextratouch/EXTRA_SOUND_CREDITS.txt).

## Scope

NEI scrolling is deferred, as in the handoff. Held-item selection, swings, bow draw,
armor equip, book page flips, footsteps, armor movement, and vanilla lever/button
clicks keep their existing implementations. Modern-only block actions are absent
in 1.7.10 and have no hooks.

## Validation for this port

`./gradlew spotlessApply build` passes, including 65 tests. Coverage includes
inventory changes and no-ops, independent toggles, typing, mentions, drop pitch,
scroll throttling, alias gain/pitch and cycles, resource integrity, modern asset
registration, and present/missing/downloaded sound resolution.

The guided dev session exercised inventory operations, creative actions, drops,
screens, typing, effects, blocks, armor, resource-pack overrides, and volume controls.
A second production client verified incoming chat, mentions, and remote armor.
The production client also launched without EFR and resolved all 54 requested
modern events. The 230 referenced files were checked against Mojang's asset hashes.
The dev resource audit opened samples for all 248 AET and 54 modern events, and
verified that resource-pack replacements still win and revert after a reload.
The temporary harness and its logs stay outside the source tree and published jars.

Listening checks for the newly downloaded sounds passed. A production server
rejection restored the inventory and empty cursor without a second sound. AET
gives immediate local inventory feedback; a later server correction cannot
retract a cue already played.

Armor equip classification is documented separately in [Armor equip categories](ARMOR_SOUNDS.md).
