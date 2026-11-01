# Extra Sounds

Client-only sounds adapted from Extra Sounds for Minecraft 1.7.10. Settings are under
**Sounds > Extra Sounds** in the config GUI, or `extra_sounds` in the config file.

| Setting | Action |
| --- | --- |
| `extraInventoryClicks` | Pickup, placement, shift-click, hotbar swap, completed drag distribution, double-click collection |
| `extraCreativeActions` | Middle-click clone and partial/all deletion in creative inventory and NEI |
| `extraItemDrops` | Q, stack drops, inventory drops, and creative palette drops |
| `extraInventoryScreens` | Container screen opening and closing |
| `extraScreenScroll` | Creative inventory rows, NEI item/bookmark pages, and NEI recipe scrolling, including scrollbar dragging |
| `extraBookmarks` | Adding/removing NEI bookmarks |
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
Switching Baubles inventory pages or opening/returning from NEI recipe views does
not play extra screen sounds or restart the GUI animation.
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
fall back to AET's item classifier and inventory-specific families (boats,
minecarts, saddles/leads, maps/name tags, dyes/seeds, records, fishing rods, stew,
eggs, buckets and experience bottles); block items use the block's step sound.
Legacy fish/dye metadata and renamed 1.7.10 items point to the corresponding
modern definitions. Hoes use their actual tool material. Generated
fallbacks follow classifier changes and are discarded by sound resource reloads.
As in vanilla sounds.json, use `"replace": true` when replacing an existing event.
AET supports namespaced event aliases with volume and pitch, repairing 1.7.10's
alias handling for AET definitions only.

EFR items reuse these modern definitions through a namespace/name translation.
For example, `etfuturum:mutton_raw` uses `item.pickup.minecraft.mutton`, while raw
ore and dye metadata translate to individual modern items. Existing EFR-specific
pack events still win, followed by modern per-item events, then the upstream item
families (including netherite gear, boats, bottles, cloth and leather). EFR's basic
metadata-aware block sound registry supplies variant sounds for planks, slabs,
tuff and other shared block IDs. World-dependent sound handlers keep the block's
normal step sound in inventories. Generated subtype events have separate cache
entries so one variant cannot overwrite another's cue.
Wood handling and bamboo use shared modern events as well; no EFR-specific sound
recordings or copies of the modern item definitions are bundled.

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

NEI navigation uses the same scroll event, speed-dependent pitch, and 20 ms limiter
as the creative inventory. Stationary scroll attempts and layout/search refreshes
are silent. NEI page buttons and page hotkeys do not play the scroll cue.
NEI bookmarks play `bookmark.add` or the softer `bookmark.remove` click. Duplicate
adds, missing removals and loading saved lists stay silent. A group action plays
once. These cues have their own `extraBookmarks` toggle and use the usual group and
Players volumes. Resource packs can replace both events. Preferred-recipe favorites
keep NEI's own sound; AET does not add a cue there.

New bookmarks also show NEI's green favorite heart over their final slot. The heart
makes two eased beats and fades over 0.82 seconds, independently of frame rate and
item rendering cache mode. It follows NEI's bookmark-animation setting, independently
of sound toggles. Loading saved bookmarks and adding recipe ingredients do not pulse.

NEI item acquisition and drag pickup use the item's inventory sound. Placement uses
that sound at the same lower pitch as vanilla inventory placement. Middle-clicking
a panel item uses `item.clone`. Deleting one item, a stack or all matching stacks
uses `item.delete_partial`; clearing the inventory uses `item.delete_all`.
Both events use Vintage Resourcify's pack-delete recording at the same pitch and
gain, including deletion in the vanilla creative inventory. The recording is
bundled in AET; Vintage Resourcify does not need to be installed. These
share `extraInventoryClicks` and `extraCreativeActions` with vanilla inventory cues.
Cancelling a dragged preview is silent, and preview cloning does not imply that a
real inventory item has been created.

Server-driven give, placement and deletion requests wait for a matching inventory
change (up to two seconds). Unchanged requests, full slots and empty deletion stay
silent. Leaving the container or player discards pending cues. NEI fast transfers
combine their underlying vanilla clicks into one cue and stay silent if the
inventory ends up unchanged. Shift-trash combines cursor and matching-slot deletion.
NEI remains an optional integration.

Held-item selection, swings, bow draw,
armor equip, book page flips, footsteps, armor movement, and vanilla lever/button
clicks keep their existing implementations. Modern-only block actions are absent
in 1.7.10 and have no hooks.

## Validation for this port

`./gradlew spotlessApply build` passes, including 69 tests. Coverage includes
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

The EFR inventory bridge passed a separate 2.6.65-GTNH dev audit covering 507 item
IDs and 825 variants. All resolved to samples; only EFR's debugging item retained
the generic click. The probe opened the resolved audio files and checked cherry,
bamboo and tuff metadata, modern/EFR resource override priority, reloads and
independent subtype events. Unit coverage now includes the identity translation
and category fallback rules (95 tests total).
