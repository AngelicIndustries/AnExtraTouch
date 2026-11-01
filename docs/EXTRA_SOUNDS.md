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

Biomes O' Plenty 2.1.0.2308 has explicit inventory aliases under its case-sensitive
`BiomesOPlenty` registry namespace. The inspected upstream Extra Sounds revision
(`5339849`) has no BOP mappings; unrecognized mod items receive its generic fallback.
AET reuses the vanilla/EFR sound families for produce, bowls, jars, dyes, gems,
darts, tools and clothing, with separate metadata entries for mixed item IDs.
Amethyst gear uses crystal sounds, mud gear uses packed mud, and scythes follow
their material. Special blocks reuse modern mud, honey, bamboo, cherry wood and
amethyst sounds; ordinary stone, wood and soil retain their block sounds.
Apples (including golden apples), melon slices (including glistering slices),
sweet/glow berries, EFR chorus fruit and related BOP fruit share
`item.category.fruit`. It uses two short real apple-contact excerpts by kvgarlic
(CC0), at volume 0.20 and pitch 2.0, about 70 ms per pickup. Placement retains
the normal 10% pitch reduction. Living coral uses wet grass; popped chorus fruit uses
the dry-food family. Other wet/slippery items retain their upstream mapping.
These are resource definitions only, with no BOP classes or required dependency.
Packs can replace the same per-item/metadata events or their shared target events.

Pam's HarvestCraft 1.3.15-GTNH has explicit definitions for all 787 standalone
item registrations, plus cakes, candles, cookware, churns and water containers. These use the exact `harvestcraft` IDs, including the case-sensitive `Item`
suffix. The inspected upstream Extra Sounds revision (`5339849`) has no
HarvestCraft mappings. The definitions were checked against the pack jar's
registration code and item textures, not just translated item names.

Fruit uses the accepted light apple contact; roots and vegetables follow carrots,
grains and seeds rustle, and fish/meat follow vanilla meat. Bread and soft baked
foods use vanilla bread; crisp biscuits follow cookies. Salads, rice, thick yogurt,
ice cream and dry bowls use the dry wooden bowl cue, while soups, stock and hot
mugs use the liquid bowl cue. Bottled juices, smoothies, soda, preserves and sauces
use the full bottle family. Fresh water, fresh milk and boiled water are wooden
buckets in this version: wood contact plus the separate bucket-liquid accent.
Water barrels, stone basins, clay jugs and brick wells layer that same liquid
accent over their wood, stone or ceramic contact. Churns use wood. Gummy bears
use the short fruit contact instead of wrapper rustling.
Metal pots and pans use the light lantern contact, without a liquid accent.
Small mushrooms use vanilla grass steps at the inventory fallback tuning.
Leather, cloth, wax, glass and stone utensils retain distinct material cues.
Ordinary machines, gardens, saplings and other blocks keep their block fallback.
No HarvestCraft dependency or custom interaction hooks are added.

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

The BOP 2.1.0.2308 inventory audit covered 127 registered item IDs and 357 variants,
with EFR present and absent. All resolved samples opened successfully, and every
non-block item had an explicit mapping. Checks covered bottle, crystal, mud and
cherry samples, distinct subtype events, shared and BOP-specific overrides, and
restoration after resource reload. The four BOP mapping tests bring the suite to
99 tests. These are routing/resource checks; final sound choices can still be
judged by ear in game.

Empty glass bottles, BOP empty jars and Adventure Backpack tank components use
two dry, hollow glass contacts from Clearwavsound's "Empty Bottle Clink"
(CC BY 3.0), at volume 0.7 and pitch 1.15. Filled jars, potions and juice bottles
keep their liquid cue. Attribution and editing details are in
`EXTRA_SOUND_CREDITS.txt`. Packs can replace `item.category.bottle_empty`.

BOP pixie dust and biome essence layer dust handling with BOP's pixie voice;
the pixie jar layers the same voice with empty-bottle handling. The accent uses
`biomesoplenty:mob.pixie.say` at volume 0.3 and follows pickup/placement pitch.
For inventory cues, an optional `<item-event>.accent` plays alongside the base
instead of competing as a random sounds.json alternative. Both use the Players
category and Extra Sounds volume/toggles. Packs can replace or silence either
event; an absent or empty accent, including when BOP is absent, is skipped.

Bucket inventory handling uses the lantern contact at volume 0.3 and pitch 1.35.
Full vanilla and BOP buckets add the selected Weighty liquid contact (volume
0.3, pitch 1.28) through `item.category.bucket_liquid`. NEI fluid display items
keep their bottle-fill cue through `item.category.fluid`. These do not change
world bucket filling or pouring sounds. BOP's small mushrooms follow the vanilla
small mushroom fallback, including EFR's block sound selection.

Dev client/server launches copy built runtime jars into a unique directory under
`.gradle/runtime-jars` before starting. This prevents later builds overwriting an
open jar and invalidating the JVM's cached ZIP offsets, which Dynamic Surroundings
reports as generic sound stream errors and caches as silence. An already affected
client must restart. Old snapshot directories can be removed when no instances
using them are running.

Left- and right-drag placement use the dragged item's cue at the normal
placement pitch (0.9), once when distribution finishes. This also covers
one-slot placements sent as drags after slight pointer movement. Empty or
unchanged drags stay silent.

Ghast tears use the selected Tiny wet contact (rubberduck's CC0 splash_09
excerpt) at volume 0.25, pitch 1.25. BOP ghastly souls (misc metadata 10)
use the selected Airy soul texture (modern soul-sand step) at volume 0.2,
pitch 1.35. They have independent item events so packs can replace either.

Typing uses the three Natural Mac key recordings at volume 0.18, pitch 1.36.
Erase/cut use 0.8 times the gain and 0.85 times the pitch; cursor movement
uses 0.55 times the gain and 1.15 times the pitch, matching the audition.
Paste shares the typing cue. These apply to chat, signs, books and text fields.
Recording credits and CC BY 3.0 attribution are in `EXTRA_SOUND_CREDITS.txt`.

BOP ambrosia (food metadata 10) shares the regular full-bottle/potion cue.
Fruit, veggie and shroom salads (4-6) and rice bowls (13) share vanilla
wooden-bowl handling, without the liquid recording used by soup bowls.
