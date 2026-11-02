# Adventura Arcana sound review queue

Snapshot: 2026-11-01. Source: `../AdventuraArcana/pack/mods/*.pw.toml` and the installed jars in `../AdventuraArcana/run/client-java25/mods`. There are 123 manifest entries and 127 installed jars (including nested coremod jars).

**Next: 03 - Growthcraft suite.** HarvestCraft and SaltyMod Expanded are accepted.

## Scope and workflow

- Work through one mod or closely related suite at a time. Status starts as queued; use in progress, ready for pack test, needs adjustment, and accepted as we proceed.
- Do not add mod dependencies or install these mods into the AET dev run. Read the pack's actual jars or matching local sources.
- The current implementation scope is sound definitions only. New NEI-style behaviors need code hooks; the candidates below are observations to investigate, not promised features or authorization to add hooks.
- Confirm case-sensitive registry names, metadata and NBT distinctions before writing definitions. Localization keys, asset namespaces and mod IDs are discovery hints, not registry IDs. Disabled/optional items must not be mistaken for enabled registrations.
- Reuse the accepted vanilla/EFR/AET material sounds. Keep dry food bowls separate from liquid bowls, empty bottles separate from full bottles, and bucket liquid separate from bottled liquid. Ordinary blocks/tools already receive fallbacks; add overrides where they improve the result.
- Check the original Extra Sounds mappings where the corresponding item exists, then fill gaps using the accepted material families.
- For each definition batch, run `./gradlew spotlessApply build`, report the exact items/families changed, and let jack relaunch Adventura Arcana for a short listening batch. Record feedback here. No pack restart or dependency edits by the agent.
- Before proposing hooks, verify the mod's own sounds, rejected/no-op actions, duplicate vanilla callbacks, scroll versus button behavior, and screen transitions.
- Do not commit new batches until jack asks.

## Definition queue

| Order | Mod / suite | Status | Mapping audit | Possible later interaction work |
| --- | --- | --- | --- | --- |
| 01 | Pam's HarvestCraft | Accepted | Fruit, vegetables, dry ingredients, meat, prepared meals, bowls, bottles, cookware | Market and cooking-container transactions; inspect existing feedback |
| 02 | SaltyMod Expanded | Accepted | Salt and powders, fish, preserved foods, meals, bowls, jars and tools | Cooking pot, apiary and other container controls |
| 03 | Growthcraft suite | Queued | All enabled modules: produce, honeycomb, cheese, bottles, buckets and brewing supplies | Brewing/tank/container actions; avoid duplicate native fluid sounds |
| 04 | Cooking for Blockheads | Queued | Recipe books, toast and kitchen blocks by material | Recipe selection, sorting and confirmed crafting; screen transitions |
| 05 | Spice of Life - Carrot Edition | Queued | Food journal, lunch bag and lunch box | Journal navigation and lunch-container interactions |
| 06 | Adventura Arcana Core | Queued | Bamboo shoots, pack-specific produce, worktable and compartments | Compartment tabs and worktable actions; inspect local build |
| 07 | Biomic | Queued | Boats, produce and special material variants; ordinary blocks may already fall back correctly | Biome-browser interactions are low priority; inspect actual survival-facing actions |
| 08 | Plant Mega Pack | Queued | Berries, seeds, plant products and nonstandard block materials | Mostly mappings; config screens are out of scope |
| 09 | Natura | Queued | Seed bags, food, sticks, bows, materials and wood variants | Inspect bag actions; ordinary furnace/workbench clicks may already work |
| 10 | Netherlicious | Queued | Fungi, ingredients, meat, dyes, gear and Nether materials | Custom furnace, beacon and workbench actions |
| 11 | Aether Legacy Departure | Queued | Eggs, petals, amber, darts, food, accessories, tools and armor | Accessory pages, lore/book controls and special crafting containers |
| 12 | Fether | Queued | Quartz gear, fruit and remaining addon materials | Check for inherited Aether behavior before proposing hooks |
| 13 | Twilight Forest | Queued | Loot, food, maps, charms, tools, armor and specialty containers | Uncrafting-table interactions and map/book behavior |
| 14 | Uralte Zauber | Queued | Wands, spell materials, charms, crystals, bottles and equipment | Spell/book selection and custom inventory controls; avoid native spell audio |
| 15 | Mob Rewind | Queued | Mob drops, food, shells, teeth, eggs, equipment and special items | Inspect actual custom item controls; do not infer from localization alone |
| 16 | Mo' Creatures Legacy | Queued | Amulets, capture items, saddles, food, materials, weapons and armor | Pet/chest/name interfaces and capture/release feedback |
| 17 | Wild Mobs | Queued | Meat, fur, hides, gear and remaining drops | Primarily mappings |
| 18 | Mowzie's Mobs | Queued | Seeds, masks, axe, helmet and other drops | Primarily mappings; preserve existing ability sounds |
| 19 | Mutant Creatures | Queued | Shards, Chemical X, skeleton parts, equipment and special drops | Creeper-minion controls; preserve existing ability sounds |
| 20 | Project Zulu | Queued | Food, seeds, flowers, drops, gear and unusual materials | Animal-name, flower-pot and tombstone interfaces |
| 21 | Battlegear 2 for Backhand | Queued | Bows, shields, quivers, heraldry and weapon materials | Weapon/quiver inventory transitions and transfers |
| 22 | Dimensional Doors | Queued | Door materials, keys and other dimensional items | Inspect item state changes; installed local build differs from manifest |
| 23 | OpenBlocks | Queued | Tools, tanks, glider, books, utility items and block materials | Custom transfer controls, tank interactions and guide pages |
| 24 | BiblioCraft + BiblioWoods BOP/Natura | Queued | Books, catalogs, clipboards, tools, framing parts and furniture materials | Book/catalog pages and specialized display/storage transfers |
| 25 | Storage Drawers + bundled addons | Queued | Keys, upgrades, drawer materials and only variants enabled in this pack | Direct insertion/extraction and bulk actions; ordinary GUI hooks may miss these |
| 26 | Antiquities: Furniture | Queued | Furniture by actual material, not one blanket wood cue | Shelf inventory interactions |
| 27 | Antique Atlas | Queued | Atlas and empty atlas as paper/book items | Map zoom, marker creation/removal and page controls |
| 28 | Waystones-X | Queued | Scrolls, warp stone and waystone materials | Destination selection; preserve native teleport sounds |
| 29 | Village Names | Queued | Codex, books and registered special items | Book controls, only where existing handling misses them |
| 30 | Realistic Torches | Queued | Matchbox, crystals and torch states | Check ignition feedback for duplicates before proposing anything |
| 31 | Better Sleeping | Queued | Watch, pills and other enabled items | Alarm controls; preserve existing alarm audio |
| 32 | Amazing Trophies | Queued | Trophy variants and actual materials | Primarily mappings |
| 33 | Recurrent Complex | Queued | Selection tools and generation tags if exposed to players | Editor controls are low priority; survival-facing interactions first |
| 34 | Mantle manual | Queued | Manual/book fallback verification | Only book navigation not already covered by the consuming mod |

## Interaction audit queue

These mostly add no content items. Start by checking whether the current hooks already handle them. Definitions alone cannot implement these actions.

| Order | Mod | Status | Inspect |
| --- | --- | --- | --- |
| U01 | Inventory Bogo Sorter | Queued audit | Sort and bulk-transfer completion; combine underlying clicks and keep no-ops silent |
| U02 | Mouse Tweaks | Queued audit | Wheel transfers and drag batching; verify current item cues first |
| U03 | Backhand | Queued audit | Offhand transfers and inventory-page transitions |
| U04 | Baubles Expanded | Queued audit | Recheck silent inventory-page switching and accessory transfers; do not add cues to its debug tool |
| U05 | NoHotbarNeeded | Queued audit | Item-selection controls; inspect whether held-item audio already covers them |
| U06 | FindIt | Queued audit | Search/selection actions; inspect existing feedback before adding anything |
| U07 | Server Utilities | Queued audit | Only item/utility actions such as transfers or deletion; administrative menus are low priority |
| U08 | ModularUI2 consumers | Queued audit | Inventory controls not routed through vanilla; changes must be scoped to real consumers |
| U09 | Minemoticon | Queued audit | Emoji selection and insertion; check existing typing cues for duplication |
| U10 | Better Achievements | Queued audit | Page/navigation controls only if ordinary button feedback is insufficient |

## Existing coverage to regression-test in the pack

| Mod | Current coverage |
| --- | --- |
| Et Futurum Requiem | Modern item/name/metadata translation and block variants |
| Biomes O' Plenty | Explicit materials and food subtypes, selected special cues and pixie accents |
| Adventure Backpack | Item definitions plus equipment, hose and fluid interactions |
| NotEnoughItems | Inventory actions, scrolling, bookmarks, screen transitions and fluid display items |

These are implemented; pack-specific regression checks remain. They do not need another full mapping pass unless a gap appears.

## Installed versions for queued content

Use the installed jar filename, not a guessed upstream version. Some embedded mcmod.info versions are stale.

| Order | Installed jar(s) |
| --- | --- |
| 01 | `harvestcraft-1.3.15-GTNH.jar` |
| 02 | `SaltyMod Expanded [1.7.10] [2.0.8].jar` |
| 03 | `growthcraft-1.7.10-2.7.3-complete.jar` |
| 04 | `CookingForBlockheads-1.4.14-GTNH.jar` |
| 05 | `SpiceOfLife-2.2.12-carrot.jar` |
| 06 | `adventuraarcanacore-909acd4-snapshot-main.6+c70eee2e3d-dirty.jar` |
| 07 | `biomic-debloat-history-debloat.23+a5a1a17d62.jar` |
| 08 | `plantmegapack-4.33-1.7.10-1492.jar` |
| 09 | `Natura-2.8.24.jar` |
| 10 | `netherlicious-3.2.8.jar` |
| 11 | `Aether.Legacy.Departure.Snapshot.26w35a.jar` |
| 12 | `fether-2.0.9.jar` |
| 13 | `TwilightForest-2.7.45.jar` |
| 14 | `uraltezauber-2515978-master+251597899a-dirty.jar` |
| 15 | `mobrewind-93305cd-master+93305cdca1-dirty.jar` |
| 16 | `MoCreatures-68a0be5-snapshot.jar` |
| 17 | `WildMobs-1.7.10-1.5.3.jar` |
| 18 | `MowziesMobs-1.2.99.jar` |
| 19 | `MutantCreatures-0d9fca7-snapshot.jar` |
| 20 | `ProjectZulu-1.7.10-1.4b4.jar` |
| 21 | `battlegear2-1.6.8-backhand.jar` |
| 22 | `dimdoors-2.2.5-test9-master.19+f63578387e-dirty.jar` |
| 23 | `OpenBlocks-1.12.22-GTNH.jar` |
| 24 | `BiblioCraft[v1.11.7][MC1.7.10].jar`; `BiblioWoods[BiomesOPlenty][v1.9].jar`; `BiblioWoods[Natura][v1.5].jar` |
| 25 | `StorageDrawers-2.2.31-GTNH.jar` |
| 26 | `antiquities-0.2.53.jar` |
| 27 | `antiqueatlas-4.4.6-GTNH.jar` |
| 28 | `waystones-1.0.16.jar` |
| 29 | `VillageNames-4.5.21-GTNH.jar` |
| 30 | `RealisticTorches-1.7.10-1.6.3.jar` |
| 31 | `BetterSleeping-1.7.10-1.2.27.jar` |
| 32 | `amazingtrophies-1.4.10.jar` |
| 33 | `RecurrentComplex-0.9.7.1.2_Unoffical.jar` |
| 34 | `Mantle-0.5.4.jar` |

## Pack differences and exclusions

- Local content builds present in the client include Uralte Zauber and Mob Rewind, plus local replacements for Adventura Arcana Core, Biomic and Dimensional Doors. They are included above.
- The manifest names DimensionalDoors 2.2.6, while this client has a local 2.2.5-test9-derived build. Audit the installed build first and recheck if the launcher replaces it.
- Ruins and lwjgl3ify are installed outside the manifest set. Ruins is world generation with no item labels in the inspected jar; lwjgl3ify is infrastructure. Custom MOTD is listed in the manifest but absent from this client; it has no item-mapping scope.
- Render/audio engines, libraries, optimization fixes, overlays, world-generation-only mods and settings/account managers are not item-sound batches. Their presence matters for duplicate-sound and playback testing, not blanket sound definitions. Examples: Angelica, Salamander, EMB, WA, Dynamic Surroundings, MAtmos, Sound Physics, PastFootsteps, Hodgepodge, GTNHLib, WAILA, Schematica and Controlling.
- Item/block translation counts are not live registry counts: jars may bundle other mods' translations and disabled integration assets. No mappings have been inferred solely from those counts.

Machine-readable inventory and per-jar disposition for this session: `/tmp/aet-pack-sound-audit/inventory.json` and `/tmp/aet-pack-sound-audit/disposition.tsv`. These are audit files outside the repository; this document is the durable queue.

## HarvestCraft review - 2026-11-02

- Audited installed `harvestcraft-1.3.15-GTNH.jar`: 787 standalone item registrations,
  all explicitly mapped; 34 additional block variants (10 cakes, 16 candles, pot,
  cutting board, two churn states and four sink materials); seven bucket-liquid
  accents. 828 sound definitions total.
- Upstream Extra Sounds `5339849` has no HarvestCraft mappings. Reused approved
  vanilla/EFR/AET families after inspecting the actual registrations and textures.
- No dependencies, runtime hooks or new audio assets. Ordinary block fallbacks stay
  in place. Static registration/definition coverage checked against the installed jar.
- Listening batch: pear, parsnip, flour, rice, white mushroom, raw/cooked mutton;
  fruit salad vs carrot soup, fried rice vs stock; apple juice vs fresh water/milk;
  apple jelly vs jelly sandwich; pot/skillet, mixing bowl, juicer, hardened leather.
  Check pickup, lower-pitched placement and completed left/right drags in the pack.
- Cooking pot/cutting board are ordinary `GuiContainer`/`Container` implementations
  and use standard slot/result callbacks, so existing inventory hooks should apply.
- Market browsing and buying use ordinary `GuiButton` controls with vanilla button
  feedback. A buy sends `MessageMarketBuy`; its server handler spawns an `EntityItem`
  rather than completing an ordinary slot pickup. A future purchase cue would need
  confirmation, rejection/no-op handling and care not to double the later pickup.
  No extra market click or scroll cue is added in this definitions-only pass.
- Validation: `./gradlew spotlessApply build` passed, 110 tests with no failures.
- Pack listening result: initial batch accepted except churn, water-container
  accents and gummy bears. Corrected churn to wood, all four sink variants to
  material contact plus bucket liquid, and gummy bears to the short fruit contact.
  Corrections accepted for commit by jack.

## SaltyMod Expanded review - 2026-11-02

- Audited installed `SaltyMod Expanded [1.7.10] [2.0.8].jar`, not its stale embedded
  `2.0.4` metadata. `ConditionalRegistrar` forwards exact names to GameRegistry
  only when feature/mod conditions pass; namespace is `saltymod`.
- 139 standalone food/material/gear registrations mapped, plus `cooking_pot` and
  `salt_crystal`: 141 events. The debug tool `dev_item` keeps its fallback.
  The optional developer apples are mapped but disabled by this pack's config.
- Checked ItemSaltFood metadata variants and the ItemUtils variant builders.
  Salted/raw/cooked variants retain their container/material. No invented IDs
  from translation names (for example, `salt_chicken_soup` is `chicken_soup:1`).
- Includes optional Twilight Forest, BOP and Wild Mobs food aliases. Definitions
  do not enable absent mods or disabled items. No new dependencies or audio assets.
- Native cloth sacks, wood barrels/crates, and custom mud block sounds already
  match their materials. They keep their fallbacks. Cooking pots use the accepted
  light metal contact; the item is not a filled bucket and gets no liquid accent.
- Pot/apiary screens use ordinary GuiContainer and Container slot handling.
  The evaporator drain control extends GuiButton, so it retains vanilla button
  feedback; it sends a server message and is disabled when its fluid level is zero.
  Any later drain/transfer cue needs confirmation and checks for existing machine
  audio before adding a hook. No additional machine or button cue is added here.
- Listening batch: salt/powdered milk; onion vs honey apple; salted meat vs
  saltwort meat bowl; salad/porridge/egg bowl vs soup; pickled onion/preserves vs
  loose ingredients; cooking pot, salt pickaxe/shard, mud armor. Try salted and
  unsalted food variants and one BOP/TF food. Pickup/place/drag should retain
  the selected item's material and normal lower placement pitch.
- Build validation: `./gradlew spotlessApply build` passed; 114 tests, no failures.
- Pack listening result: mud masks sounded too hard. Changed all four from
  packed-mud gear to the mineral mud ball's wet-mud contact. Accepted for commit
  by jack.
