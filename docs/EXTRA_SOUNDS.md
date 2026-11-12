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

SaltyMod Expanded 2.0.8 has definitions for 139 food/material/gear item IDs,
including its optional Twilight Forest, BOP and Wild Mobs foods. Salted, unsalted,
raw and cooked metadata variants reuse the same material or container family.
Preserves and pickles use full bottles; salads, porridge, egg bowls and saltwort
meat bowls use dry bowls; soups and stews use liquid bowls. Loose salt, baking
soda and powdered milk rustle, while fruit, bread, cookies and meat follow the
accepted vanilla/HarvestCraft families. The four washable mud masks use the same
wet-mud contact as mineral mud balls; salt shards, crystals and the salt pickaxe
use glass contact. The cooking pot uses the
light metal cookware cue. Native wood/cloth storage and custom mud block sounds
remain in place. The debug tool is left alone, and definitions for disabled items
do not enable them. Upstream Extra Sounds `5339849` has no SaltyMod definitions;
these aliases were checked against the installed jar's registration/variant code
and textures. No dependency or custom interaction hook is added.

Growthcraft 2.7.3 has definitions across its Core, Apples, Bamboo, Bees, Cellar,
Grapes, Hops, Rice and Milk modules, using exact case-sensitive `Growthcraft|...`
registry IDs rather than asset namespaces. Grapes use light fruit contact; seeds,
yeast and starter culture rustle; cheese, butter and rice balls use soft food
contact; yogurt and ice cream use dry bowls. The honey jar is a clay pot and uses
the liquid bowl cue. Cloth, leather bags, wax, bamboo and metal tools retain their
material cues. Wooden machines and the Fishtrap module keep native block sounds.

All 51 generated booze buckets and nine generic fluid buckets use light metal
contact plus the bucket-liquid accent. Their 17 bottle IDs use the full bottle
cue. Salt bottles and buckets contain dry salt and instead add a quiet dust
rustle. The water bag stores fill level in NBT, while metadata selects color:
every color keeps leather contact, with a bucket-liquid accent only when its
Forge FluidStack tag contains a positive amount of registered fluid. Filling or
draining is checked on each inventory sound lookup, without cached fill state.
Explicit resource-pack metadata overrides still take precedence.
The inspected upstream Extra Sounds revision (`5339849`) has no Growthcraft
mappings. These definitions were checked against the installed complete jar's
registrations, fluid factories and textures; no Growthcraft dependency is added.

Cooking for Blockheads 1.4.14-GTNH uses paper for every recipe-book tier, soft
bread for toast, and light metal contact for the toaster. The oven, fridge and
other kitchen blocks retain native material sounds. Sinks do not receive unconditional
liquid accents: their placed tanks may be empty. The installed registrations and
textures were checked; upstream Extra Sounds `5339849` has no matching entries.
Optional client-only Cooking for Blockheads hooks add item-specific feedback
for tool-rack insert/remove/swap, oven-top cookware placement and toaster bread
insertion. They compare actual local inventory changes, so no-op clicks stay
silent, and reuse inventory pickup/placement pitches. Recipe-list wheel and
scrollbar movement use the existing scroll cue without adding sounds to sort
or recipe-arrow buttons. These respect `extraInventoryClicks`,
`extraScreenScroll`, Players volume and `extraSoundsVolume`; no dependency is
added.

Mod world interactions that make no sound of their own play modern vanilla
cues (`extraModInteractions`). Picking fruit plays a short stem snap: HarvestCraft
tree fruit and fruiting logs, Biomes O' Plenty apple and persimmon leaves and
berry bushes, Growthcraft apples, grapes, hops and dried curds, Plant Mega Pack's
ripe berry bushes, Natura's overworld and Nether berry bushes (on either click),
and HarvestCraft's berry, tomato and pepper crops. Other HarvestCraft
crops sound by kind: grain (barley, oats, rye, rice, sesame, mustard) is cut with
a sickle, leafy crops (lettuce, cabbage, spinach, brassicas, celery, rhubarb,
spice/curry/tea leaves, seaweed and the leafy garden) are torn, and other crops
and gardens tear like grass. Each kind picks one of up to three CC0 recordings
at random. Hunger Overhaul's right-click harvesting, which takes over every
`BlockCrops` (vanilla wheat, carrots and potatoes, HarvestCraft, Natura and
Netherlicious crops) when enabled, plays the same cues; vanilla wheat, Natura
barley and Netherlicious abyssal oat count as grain. Netherlicious' own
replanting harvest of abyssal oat and devilish maize plays them too. Fether's right-click
harvest of ripe crops and ignis fruit does too: blood leaf tears like leafy crops,
marrow berries and ignis fruit snap like fruit, flesh root and glow flowers tear
like other crops. So does EnderCore's right-click harvesting, which handles the crops in
its own crop config (vanilla wheat, carrots, potatoes and nether wart by default)
before Hunger Overhaul does; a sickle's area harvest plays one cue.
Bucket and bottle fill/empty cues cover the HarvestCraft sink (including washing
leather armor), the SaltyMod evaporator, Growthcraft tanks and paddies, BOP honey
jars and BOP buckets. Hoeing SaltyMod salt grass, which skips the hoe's own till
sound, plays the modern till, and each Growthcraft butter-churn stroke plays a
low, wet stir at a widely varying pitch, including the stroke that makes butter. Most of these run
only on the server, which detects an actual change (the plant's block or
metadata, the held container, a successful tank transfer) and sends the cue to
players within 16 blocks. Each client plays it under its own toggle, the Blocks
volume and `extraSoundsVolume`, so clients without AET simply receive nothing.
These hooks need AET on the server; no mod dependency is added.

The same toggle covers kitchen and shop interactions. A Cooking for Blockheads
sink rinsing an item, or running its tap for an empty hand or an item it has no
use for, plays vanilla's fish swimming splash; a cooking table craft plays a knife chop (CC0, egomassive), once
per craft, and putting an ingredient into an oven plays a short sizzle (CC0,
roboroo). HarvestCraft market
purchases play a cash register (CC0, CapsLok). SaltyMod's drain button and
Growthcraft's discard buttons pour when a tank with fluid is emptied, and the brew
kettle's switch button closes like an iron trapdoor when the tanks' contents
change. Spice of Life's sneak-use lid opens and folds the lunch bag with a paper
crumple (CC0, esperri) and flips the lunch box with a quiet, high iron trapdoor. Cooking for Blockheads doors play
on every nearby client as they start to open and when they shut, instead of the
generic screen sounds: fridges open and close with recorded fridge doors (CC0,
naxxfish and bolkmar),
ovens sound like copper doors and counters and cabinets like wooden trapdoors.
These recordings are credited in `EXTRA_SOUND_CREDITS.txt`. Book page arrows, in
vanilla books, Spice of Life's food journal and the Aether lore book, turn a page
instead of clicking (`extraBookPages`).

Plant Mega Pack 4.33 has definitions for its 275 enabled items and unusual
blocks, checked against the pack jar's registrations and textures. Its produce
and meals follow the accepted HarvestCraft families: berries, tomatoes and prickly
pears use the fruit contact; vegetables, roots and peanut butter cookies are
crunchy; leafy greens, herbs, flowers, salves (leaf-wrapped) and the plain seaweed
wrap rustle like leaves; seeds, grains, flour and powders rustle as dust; mozuku
seaweed is wet. Soups use the liquid bowl and other bowls the dry bowl; the
stemmed-glass drinks, jelly and peanut butter use full bottles; sandwiches, corn
bread, muffins and corn wraps use bread; pastries, stuffed peppers, cooked rice and
filled seaweed wraps use dry food. Bamboo stalks and all bamboo building blocks use
modern bamboo instead of wood, cacti use the crunchy plant cue instead of wood,
corals and coral fragments use wet coral instead of gravel, shelf fungi use
mushroom stems, and wooden and iron wall brackets stop sounding like stone. Other
plants keep their grass step. The extra wall brackets are disabled in the pack and
the stone bracket keeps its stone.

Natura 2.8.24 has 67 definitions for the items that fell back to the generic
pickup and the blocks with misleading sounds. Its wood blocks already sound like
wood and keep that. Ghostwood, bloodwood, darkwood and fusewood tools and kamas
sound wooden, though Natura gives darkwood and fusewood the stone and iron tiers
and bloodwood its own material; nether quartz tools keep stone and bows keep the
vanilla bow's cue. Imp armor sounds like leather, sticks, bowls and flint and
blaze like vanilla's, and the redwood-family doors and the bloodwood log (metal
in Natura) like wood. Berries, potash apples and saguaro fruit use the fruit
contact; flour, sulfur, fletching, flame string, blue dye, seed bags and the bone
bag rustle as dust, barley like hay, cotton softly like HarvestCraft's cotton, and
imp leather like leather. Stews use the liquid bowl, the berry medley the dry
bowl, and imp meat and cactus juice (the water drop) the meat cue. Leaves, saplings,
bluebells, thornvines and berry bushes rustle like BOP's foliage, the saguaro
matches Plant Mega Pack's cacti, large glowshrooms sound like mushroom blocks
(they were wood), and blaze rails like rails (they were stone).

Netherlicious 3.2.8 ships modern sounds for its blocks (nylium, stems, nether
wood, basalt, lanterns, chains, amethyst clusters and more), so its block items
keep them. 58 definitions cover its plain items, which fell back to the generic
pickup: Efrine armor and tools sound like iron, heavy blaze armor like netherite,
the glasses like glass; signs and doors sound like wood; the soul, foxfire and
shadow flint and steel like vanilla's. The Efrine ingot and nuggets use metal,
crystal shards and void quartz the amethyst shard, shield scrap netherite scrap
and the spore membrane the phantom membrane. Dyes, gloomstone dust, foxfire
powder, warts and seeds rustle as dust; abyssal oats sound like hay, devilish
maize and hellderberry cookies are crunchy, hellderberries use the fruit contact,
strider flank the meat cue, the ghastly pie vanilla's pumpkin pie, abyssal bread
bread, the hellderberry juice bottle a full bottle, and devilish popcorn, a soup
item, the dry bowl. The pack hides Netherlicious' 89 Et Futurum duplicates (see
Adventura Arcana Core); those that are items share these definitions.

Aether Legacy Departure (the pack's 26w35a snapshot) has 483 definitions. Its
tools and armor reuse vanilla tiers that misrepresent them (skyroot tools are
stone tier, tipped holystone and zanite iron, gravitite and valkyrie emerald,
Neptune and obsidian armor diamond), so gear sounds like its material instead:
skyroot wood, holystone stone, arkenium and divineral iron, valkyrie and phoenix
gold, Neptune chain, obsidian and heavy blaze netherite, agility leather, and
the gemstone families (zanite, continuum, gravitite, ascensite, elysian)
diamond. Gloves and shields follow the same table and capes sound like cloth
banners. Rings play a light ring clink and pendants, medals and insignias a
small chain, two CC0 excerpts from MaGs_007's jewelry recordings that other
mods' jewelry can reuse as `item.category.ring` and `item.category.amulet`;
bone rings sound like bone, ice jewelry and auralite rings like crystal, and
auralite pendants chain with an amethyst ding on top.
Gems and ingots use metal, nuggets small metal, raw gravitite and arkenium
chunks loose metal, ambrosium and other crystal shards the amethyst shard, cores
and orbs the heart of the sea, swets and gummy swets slime, aercloud globules
and parachutes snow, dungeon keys the key cue, berries and apples fruit, and
aerwhale and thunderlo meat. Named special weapons (slayers, hammers, the
elemental swords, vampire blade) and bows keep their tier sound. Crystal
clusters and blocks chime like amethyst instead of stepping like stone, leaves
and saplings rustle like BOP's; aerclouds, quicksoil, holystone and skyroot
blocks already sound like their material.

Fether 2.1.0 (Food from the Nether, GTNH's port of Pam's Harvest the Nether)
has 25 definitions. Its quartz armor and tools, whose `QUARTZ` materials fell
back to generic gear, sound like diamond gear, and the quartz ingot uses metal.
Ignis fruit and marrow berries use the fruit contact, blood leaf, leaves and
saplings rustle like leaves, flesh root, weevils and their soft eggs use the meat
cue, seeds rustle as dust, scrambled weevil egg sounds like dry food and the
Nether bed like cloth.

Spice of Life - Carrot Edition 2.2.12 uses paper for the food journal, light
metal contact for the iron lunch box, and `item.category.paper_bag` for the lunch
bag: a short crinkle excerpted from MichaelGillighan's CC0 "03 - Bolsa llena.wav"
(credited in `EXTRA_SOUND_CREDITS.txt`). The journal is not an
`ItemBook`, so it needs an explicit definition. Lunch-container contents and lid
state are NBT and do not change the cue. The installed registrations, recipes
and textures were checked; upstream Extra Sounds `5339849` has no matching entries.

Dropping items plays a short, light flutter (a CC0 excerpt from Aquafeniz's
"Flutter.wav") instead of Extra Sounds' bow shot, which at pitch 1.5-2.0 sounded
harsh, and which resource packs replacing `random/bow.ogg` change unpredictably.
A single or unstackable item plays at pitch 1.2, a full stack at 0.9, and every
drop varies by up to 6%.

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
