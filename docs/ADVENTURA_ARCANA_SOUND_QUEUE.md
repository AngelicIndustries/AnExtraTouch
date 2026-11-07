# Adventura Arcana sound review queue

Snapshot: 2026-11-01. Source: `../AdventuraArcana/pack/mods/*.pw.toml` and the installed jars in `../AdventuraArcana/run/client-java25/mods`. There are 123 manifest entries and 127 installed jars (including nested coremod jars).

**Next: 12 - Fether** (06 - Adventura Arcana Core and 07 - Biomic wait while they are in development). Growthcraft, Cooking for Blockheads, Spice of Life, Plant Mega Pack, Natura, Netherlicious and Aether Legacy Departure are accepted.

## Scope and workflow

- Work through one mod or closely related suite at a time. Status starts as queued; use in progress, ready for pack test, needs adjustment, and accepted as we proceed.
- Do not add mod dependencies or install these mods into the AET dev run. Read the pack's actual jars or matching local sources.
- The current implementation scope is sound definitions only, except the explicitly requested Growthcraft water-bag NBT detection and Cooking for Blockheads interaction cues. New NEI-style behaviors need code hooks; the candidates below are observations to investigate, not promised features or authorization to add hooks.
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
| 03 | Growthcraft suite | Accepted | All enabled modules: produce, honeycomb, cheese, bottles, buckets and brewing supplies | Brewing/tank/container actions; avoid duplicate native fluid sounds |
| 04 | Cooking for Blockheads | Accepted | Recipe books, toast and kitchen blocks by material | Recipe selection, sorting and confirmed crafting; screen transitions |
| 05 | Spice of Life - Carrot Edition | Accepted | Food journal, lunch bag and lunch box | Journal navigation and lunch-container interactions |
| 06 | Adventura Arcana Core | Queued | Bamboo shoots, pack-specific produce, worktable and compartments | Compartment tabs and worktable actions; inspect local build |
| 07 | Biomic | Queued | Boats, produce and special material variants; ordinary blocks may already fall back correctly | Biome-browser interactions are low priority; inspect actual survival-facing actions |
| 08 | Plant Mega Pack | Accepted | Berries, seeds, plant products and nonstandard block materials | Mostly mappings; config screens are out of scope |
| 09 | Natura | Accepted | Seed bags, food, sticks, bows, materials and wood variants | Inspect bag actions; ordinary furnace/workbench clicks may already work |
| 10 | Netherlicious | Accepted | Fungi, ingredients, meat, dyes, gear and Nether materials | Custom furnace, beacon and workbench actions |
| 11 | Aether Legacy Departure | Accepted | Eggs, petals, amber, darts, food, accessories, tools and armor | Accessory pages, lore/book controls and special crafting containers |
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

## Growthcraft review - 2026-11-02

- Audited installed `growthcraft-1.7.10-2.7.3-complete.jar`, checking registration
  classes, FluidFactory and BoozeRegistryHelper directly against the jar. Used
  matching local source for item behavior and checked representative textures.
- Modules: Core, Apples, Bamboo, Bees, Cellar, Grapes, Hops, Rice and Milk have
  definitions; Fishtrap has only block items already using material fallbacks.
  Cactus and Ender textures are bundled leftovers, with no corresponding module
  classes or registrations in this jar, so no invented mappings were added.
- 126 item/block IDs and 62 accents, 188 definitions total. Includes all 32 ordinary
  standalone registrations, seven booze bottle IDs, ten generic bottle IDs,
  51 booze buckets, nine generic liquid buckets and 17 selected block materials.
  The achievement icon item is excluded. No dependencies or new audio assets.
- Booze bottle metadata selects fermentation variant; buckets use numbered registry
  names. FluidFactory registers food bottles as `BottleFluid...`, despite the
  `FoodBottleFluid...` translation name. Ordinary milk uses vanilla's milk bucket;
  cheese-fluid entries use FEATURE_NONE and have no bottle/bucket items to map.
- Honey fluid, milk and thistle are enabled in the installed pack config. Their
  definitions do not enable those features when another pack disables them.
- Dry salt bottle/bucket accents are dust, not liquid. Water bag fill is in NBT,
  with 17 color metadata values, so all bags use leather contact. The requested
  follow-up reads the Forge FluidStack on each sound lookup and selects a filled
  event with a liquid accent only for positive registered fluid amounts. Explicit
  metadata resource overrides retain priority. NBT follow-up accepted by jack;
  no Growthcraft dependency is needed.
- Yogurt and ice cream use dry bowls; cheese/butter/rice balls use soft food;
  the honey jar returns a clay flower pot and uses the liquid bowl cue.
  Bamboo building items use the existing bamboo cue. Leaves, wooden presses,
  churns, barrels and bee/fish boxes retain their ordinary material fallbacks.
- Interaction audit: inventory screens use ordinary container handling. Cellar
  discard controls use GuiButton and server messages. Cheese presses already
  click; fruit press pistons already sound; brewing kettles and cheese vats have
  native fluid/machine feedback. Future transfer/discard/churn hooks must account
  for these and successful state changes. No new action hooks in this batch.
- Listening batch: grapes and seeds; rice vs rice ball; cheese/butter vs yogurt;
  yeast/starter culture; salt bottle/bucket vs saltwater bottle/bucket; cider/wine
  bottle vs bucket (try multiple fermentation variants); milk/cream buckets;
  honey jar, cheesecloth, colored water bag, bamboo raft/fence and metal kettle.
- Build validation: `./gradlew spotlessApply build` passed; 119 tests, no failures.
- Pack listening result: accepted by jack.

## Cooking for Blockheads review - 2026-11-02

- Audited installed `CookingForBlockheads-1.4.14-GTNH.jar`: registrations, item
  metadata, kitchen block materials, textures and recipe container/GUI code.
  Upstream Extra Sounds `5339849` has no corresponding definitions.
- Five aliases at first: `recipebook` uses paper across tiers (metadata 3, 0
  and 1); `toast` matches HarvestCraft toast with soft bread contact; `toaster`,
  `cookingoven` and `fridge` use the accepted light metal contact.
- Counters/cabinets and all 16 kitchen floors retain stone material fallbacks.
  The cooking table, sink and tool rack retain wood. Sink tanks can be empty;
  the pack enables `sinkRequiresWater`, so no unconditional liquid accent.
- Only recipebook and toast are standalone registered items. Bundled pot,
  saucepan, skillet and bakeware textures do not justify invented item IDs.
  Corner blocks are `countercorner`/`cabinetcorner`, not translation-key names.
- Interaction candidates: recipe selection and successful craft/smelt requests,
  custom scrollbar movement and screen transitions. Sorting/recipe arrows use
  ordinary GuiButton handling; future hooks must avoid doubled clicks and
  distinguish preview changes from server-confirmed crafting. No GUI hooks or
  dependencies added in the definition batch.
- Listening batch: all recipe-book tiers, toast, toaster, oven and fridge;
  compare a counter and a wooden tool rack as unchanged material baselines.
- Build validation (including water-bag follow-up): `./gradlew spotlessApply build`
  passed; 123 tests, no failures. Pack listening pending.

### Cooking for Blockheads interactions - 2026-11-02

- Requested follow-up: tool-rack interactions and an audit of other missing cues.
- Added one item-specific cue for rack insertion, removal and replacement. Removal
  uses inventory pickup pitch; insertion/replacement uses placement pitch. Empty
  clicks, clicks below the rack slots and rejected block items leave contents
  unchanged and stay silent. Replacements play only the incoming item's cue.
- The same direct inventory comparison covers oven-top cookware insertion and
  toaster bread insertion. Toaster activation returns false even when insertion
  succeeds, so checking its boolean return would miss valid actions.
- These optional client-only mixins compare contents around the local player's
  activation, not tile sync/update calls. They follow CFB's local prediction,
  just like normal inventory click feedback; they do not await server acceptance.
  Remote players, integrated-server execution, chunk loads and background machine
  changes cannot replay the cue. No CFB dependency was added.
- Recipe book/table wheel scrolling and scrollbar dragging use the existing
  rate-limited scroll cue only when the visible row changes. Recipe-arrow and
  sort buttons retain native button clicks. Layout refreshes and scroll limits
  stay silent. Wheel events outside the CFB panel do not add a second cue over
  NEI. Existing inventory-click, screen-scroll and volume toggles apply.
- Remaining candidates: sink fill/pour/rinse, toaster lever and completion,
  recipe selection and successful craft/smelt feedback. Sink interactions can
  drain/discard fluid or spawn particles without delivering a container; a
  future cue must check actual output/transfer, not just particles or true return.
  Craft/smelt operations are server-side and require matching result updates;
  clicking a preview is not proof that crafting succeeded. Toaster active state
  is not in its tile NBT, so generic tile-sync observation cannot detect cycles.
  Fridge/oven door feedback could also be distinct, but must avoid overlapping
  the existing screen-open/close cues. These candidates are documented, not added.
- Listening batch: rack place/take/swap and empty clicks; cookware on empty vs
  occupied oven-top positions; bread into empty vs full toaster; wheel and drag
  a recipe list, including its limits; verify sort/arrow clicks remain single.
- Validation: `./gradlew spotlessApply build` passed, 128 tests with no failures.
  Checked activation signatures and both scrollbar call sites against installed
  jar bytecode, plus inherited GUI field remapping in the production AET jar.
  Runtime mixin application and listening still need the pack relaunch.
- Pack listening result (2026-11-03): recipe books, toast, toaster, baselines,
  every rack, oven-top and toaster cue, recipe-list scrolling and single button
  clicks accepted by jack. The oven and fridge sounded like a bucket. They now
  drop their overrides and use their native metal block sound (soundTypeMetal).
- Follow-ups requested for Adventura Arcana Core, not AET, and written there
  (`kitchenSneakTake`, `toasterLever`, `recipeListScroll`): sneak-use with an
  empty hand takes cookware off the oven and bread out of the toaster; the
  recipe list scrolls only with the mouse over the list or scrollbar; the
  toaster's lever and bread move smoothly, its running state is saved and
  synced, and the server plays a lever-down sound on start and a pop on stop.
  Both CC0 by MTJohnson, picked by jack: "Toaster Pop.wav" and "Priming
  Toaster.wav", played about 8% higher. AET's change-based kitchen cue covers
  the sneak-take removals. This replaces the toaster lever/completion
  candidate above.
- Second pack check (2026-11-03): recipe-list scrolling accepted. Fixed three
  issues. CFB's recipe book turns a double click (mode 6) into an ordinary
  click that puts a single item back down; AET played the collect cue because
  slots changed. The collect cue now needs the cursor to gain items, otherwise
  the click is judged as ordinary. The toaster's client count ran ahead of a
  busy server, so the lever rose before the pop; with `toasterLever` the client
  now waits for the server's state. Toaster take-back started a second bread
  stack in the empty hand, which made NoHotbarNeeded (U05) show the bread's
  total on selection; it now tops up carried stacks first, like a pickup.
- Accepted by jack after the second check, with the AAC patches.

## Spice of Life - Carrot Edition review - 2026-11-03

- Audited installed `SpiceOfLife-2.2.12-carrot.jar`. `ModContent` registers three
  items unconditionally under the case-sensitive `SpiceOfLife` mod ID:
  `bookfoodjournal`, `lunchbox` and `lunchbag`. None use metadata. Upstream Extra
  Sounds `5339849` has no corresponding definitions.
- The journal is a plain `Item`, not `ItemBook`, so the book classifier missed it
  and it used generic contact. It now uses paper, matching CFB recipe books.
  The lunch bag is crafted from paper and uses paper. The lunch box is crafted
  from heavy weighted (iron) pressure plates and uses the accepted light metal
  contact.
- Container contents (`Inventory`) and lid state (`Open`) are NBT, so they cannot
  select a definition. No unconditional food accent: containers are often empty.
- The lunch-container screen is an ordinary `GuiContainer`. Its `slotClick`
  refuses the held container's own slot without changing anything, so the
  existing change-based inventory cue stays silent there. Eating from an open
  container already uses vanilla eating audio.
- Interaction candidates, documented, not added:
  - Lid toggle (sneak-use) is silent and predicted on the client in
    `onItemRightClick`. Opening the screen also sets the flag, and closing it
    clears it, so a lid cue must not double the screen open/close cues. The
    pack disables left-click opening, which would be server-only anyway.
  - A food-content accent like the water bag would need an NBT-aware lookup.
  - Journal page arrows are ordinary `GuiButton`s with the vanilla click, the
    same as 1.7.10 books. A modern `item.book.page_turn` backport for
    `GuiScreenBook` could be shared with the journal.
  - Dumping into or pulling food from an inventory happens only on the server
    (`onItemUseFirst`). A cue would need confirmed slot changes.
- Held-item equip/swing: the journal and CFB recipe books still use utility
  sounds because they are not `ItemBook`. Without code, the pack can opt in with
  `itemSoundOverrides`, for example `SpiceOfLife:bookfoodjournal=book`.
- Lunch bag follow-up (jack): it deserved its own paper sound. From an audition
  of 4 vanilla and 16 CC0 Freesound paper bag options, jack picked
  MichaelGillighan's "03 - Bolsa llena.wav". A 0.33 s excerpt ships as
  `item.category.paper_bag` (volume 0.8), reusable for later paper bags.
- Listening batch: journal, lunch bag and lunch box pickup/place/drag, compared
  with vanilla paper and book. Open a lunch box and move food in and out, then
  click the held box's own slot, which must stay silent.
- Validation: `./gradlew spotlessApply build` passed, 133 tests with no failures,
  including the lunch bag follow-up.
- Pack listening result: accepted by jack.

## Interaction audit of supported mods - 2026-11-03

Requested by jack: check the mods AET already supports for GUIs and world
interactions that deserve a cue hook. Audited the installed HarvestCraft,
SaltyMod, Growthcraft and BOP jars: every right-click handler, its sound calls
and the GUI classes. EFR, Adventure Backpack, NEI and Baubles Expanded already
have AET hooks; their documented gaps are unchanged.

- Already voiced by the mods: SaltyMod stove ignition, stripping, shears, bee
  burrows and salt crystals; Growthcraft brew kettle, cheese press and vat, fruit
  presser and planting; EFR smithing table, buckets, bottles, composter, barrel,
  stripping, berry bush and beehive. EFR's stonecutter, loom and cartography
  table are decorative in 2.6.65 (no activation), so no GUI cues to add.
- Machine GUIs (HarvestCraft oven/churn/quern/presser/grinder/pot, SaltyMod
  pot/apiary/press/oven/fish farm, Growthcraft cellar devices) are ordinary
  containers already covered by the inventory cues.
- Added, behind `extraModInteractions`, server-detected and sent to players
  within 16 blocks:
  - Right-click harvesting (pack config enables HarvestCraft's crop and fruit
    harvesting): HarvestCraft crops, tree fruit, fruiting logs and gardens; BOP
    apple/persimmon leaves and berry bushes; Growthcraft apples, grapes, hops
    and dried curds. A cue needs the plant's block or metadata to change.
  - Bucket/bottle fill and empty: HarvestCraft sink (fill; washing leather
    armor plays the bottle pour), SaltyMod evaporator fill/drain, Growthcraft
    barrel/keg/jar transfers and paddy flooding, BOP honey jar and BOP buckets.
  - Salt grass tilling: SaltyMod's hook converts it and skips the hoe's till
    sound; the modern till now plays.
  - Growthcraft butter churn: a stroke cue, and a separate cue for the stroke
    that produces butter.
- Not added: HarvestCraft market purchases (server message, item entity, needs
  confirmation); Growthcraft's tank discard/switch buttons keep vanilla clicks.
- Harvest audition, picked by jack: fruit (and berries, hops, grapes, curds)
  uses kyles' CC0 stem snap (crop page 8b), replacing the berry pick he first
  chose. HarvestCraft crops by kind: grain uses three cuts of silencyo's sickle
  through wheat, leafy crops three cuts of lunchmoney's lettuce ripping, and the
  rest plus gardens alegemaate's grass tear. Churn strokes use two cuts of
  iggy1345's pasta stirring at pitch 0.8 with a +/-20% spread; the finishing
  stroke reuses it (no separate "done" pick). All ship in AET with credits.
  Per jack, HarvestCraft's berry, tomato and pepper crops use the fruit snap.
  Corn, a grass picked by its ears, stays with "other" crops.
- Pack check: right-clicking vanilla wheat was silent. Hunger Overhaul's
  `enableRightClickHarvesting` (on in the pack) harvests every BlockCrops,
  which includes HarvestCraft's BlockPamCrop, in its PlayerInteractEvent
  handler and resets the crop before the block's own handler runs, so the
  HarvestCraft crop hook never saw a change. Added a hook on Hunger Overhaul's
  handler with the same crop classification (vanilla wheat and Natura barley
  are grain). The HarvestCraft hook stays for packs without Hunger Overhaul;
  the two cannot double, as the crop is already reset when HarvestCraft runs.
  Netherlicious and Fether nether crops harvest in their own handlers and are
  not hooked yet.
- Wheat stayed silent after that. Debug prints showed the server received the
  click with the wheat at metadata 7, and by the last event listener it was 0
  and cancelled, without Hunger Overhaul's handler running: EnderCore's
  `RightClickCropHandler` (`allowCropRC`, crop list in
  `config/endercore/cropConfig.json`: wheat, carrots, potatoes, nether wart)
  harvests those on the server first and cancels the event. Its client side only
  swings the arm, so Hunger Overhaul still harvests them visually on the client.
  HarvestCraft crops are not in EnderCore's list, so Hunger Overhaul harvests
  them. Added an EnderCore hook with the same crop cues.
- Per jack, the pack now turns EnderCore's crop right-click off
  (`pack/config/endercore/endercore.cfg`, `allowCropRC=false`), so Hunger
  Overhaul harvests every crop. The AET hook stays for packs that keep it on.
- Validation: `./gradlew spotlessApply build` passed, 137 tests. Target
  signatures checked against the installed jars' bytecode; the production jar
  references only SRG Minecraft names. Pack check pending.

## Past Footsteps coverage of supported mods - 2026-11-03

- Source: jack's `/pf mappings all` and `/pf mappings unmapped` reports from the
  pack (3459 blocks; 850 still on primitive or native fallback, mostly Biomic).
  Mod mappings live in the pack's `resourcepacks/aa-footsteps.zip`; PF itself
  bundles vanilla mappings only.
- HarvestCraft, SaltyMod, Growthcraft, BOP and CFB were fully mapped. Their
  plants all have foliage or foliage_low channels; the remaining silent entries
  are fluids and candles, which is intended.
- Gaps found: 71 EFR blocks (nether woods and stems, nether plants and vines,
  azalea, moss, mangrove roots, dripstone, nylium, sculk, shroomlight, extra
  fences and gates), three Adventure Backpack blocks (the sleeping bag fell
  back to stone) and vanilla's nether brick fence.
- Added 57 EFR/Adventure Backpack keys to `aa-footsteps.zip` in the pack repo,
  following its conventions (doors/trapdoors bluntwood, plates wood + carpet,
  buttons/signs NOT_EMITTER, fence tops `.bigger` bluntwood, nether roots and
  sprouts foliage brush, fungus foliage_low plant_brush, vines leaves, moss
  softgrass, muddy roots mud, sculk organic, catalyst bone, sleeping bag rug).
  Packwiz index refreshed; `checkIndex` passes. Per jack, the vanilla nether
  brick fence went into PF's bundled map instead (one line; PF tests pass).
- Fences, gates and walls otherwise map only `.bigger`, as the pack does for
  vanilla; their primitive normal surface is intentional. Monster egg
  metadata 3-15 is unused.

## Remaining interaction candidates on supported mods - 2026-11-06

Per jack: quick wins, server-confirmed buttons and CFB doors (AAC skipped while
in development). All under `extraModInteractions` unless noted.

- CFB sink: fill and pour use the bucket/bottle cues; rinsing an item by a sink
  recipe (coloured wool and carpet, dyed leather armor, milk buckets, potions),
  even when nothing changes, plays a rinse cue, and so does the tap CFB runs for
  an empty hand or any item without a sink use (only with a bucket's worth of
  water when `sinkRequiresWater`). Dye recolouring stays silent. Per jack's check:
  unsupported items showed the tap's particles silently; now they splash too.
- CFB doors: fridges, ovens, counters and cabinets sound as their door starts to
  open and when it shuts, on every nearby client (like chests), and replace the
  generic screen open/close sounds for those screens.
- CFB cooking table: one cue when a craft returns a result (shift-crafts once),
  one when an ingredient goes into an oven. Previews stay silent.
- Spice of Life: sneak-use lid open/close cues for the lunch box and bag; the
  screen-opening path keeps its screen sound.
- Book page arrows (vanilla books and the food journal) play modern
  `item.book.page_turn` instead of the button click (`extraBookPages`).
- HarvestCraft market: a cue when the bought item spawns.
- SaltyMod evaporator drain button and Growthcraft discard buttons: a pour when a
  tank with fluid is emptied; the brew kettle's switch button: a cue when the
  tanks' contents actually change. Growthcraft's packets are hooked, not
  `clearTank`, because switching clears both tanks internally.
- Mod packet handlers run on network threads, so cues raised there are queued
  and sent on the next server tick.
- Picks from the interaction audition page: rinse uses vanilla fish swimming
  (RIN1), the kettle switch an iron trapdoor closing (SW5), purchases a cash
  register (BUY12, CapsLok), oven smelting a sizzle (SM7, roboroo), the lunch
  bag a paper crumple pitched up to open and down to fold (BAG8, esperri),
  fridges open with a recorded fridge door (FR7, naxxfish), ovens use copper
  doors (OV4) and counters and cabinets wooden trapdoors (CAB1).
- From the follow-up page: cooking table crafts play a knife chop (CR17,
  egomassive), the lunch box lid keeps a quiet, high iron trapdoor (BOX14) and
  fridges close with bolkmar's fridge door (FR14), since FR7 only records the
  opening.
- jack's in-game check, 2026-11-06: CFB doors, cooking table craft/smelt,
  Spice of Life lunch box/bag lids, the CFB sink (after the tap fix), the
  market purchase, book page arrows, the SaltyMod drain plug (a hidden 3x3
  spot under the evaporator's fluid gauge), the brew kettle's discard and
  switch buttons, the drop flutter and `/aet_sound` work.

## Plant Mega Pack review - 2026-11-07

- Source: the pack's `plantmegapack-4.33-1.7.10-1492.jar` (decompiled
  registrations and item textures) and `config/plantmegapack/settings.cfg`
  (crops and aquatic content on, extra wall brackets off).
  `plantmegapackfix-1.3.1.jar` only patches world generation.
- Previously every PMP item fell back to the generic classifier; jack flagged
  the tomato. Now 275 explicit definitions cover all 17 seeds, 91 foods, 12
  powders, 6 salves, 8 flower and leaf items and 9 coral fragments, plus the
  block forms with misleading step sounds.
- Item families follow HarvestCraft's accepted mappings (see EXTRA_SOUNDS.md).
  Containers come from the textures: stemmed glasses for the twelve drinks,
  jars for jelly and peanut butter, liquid bowls for the four soups, dry bowls
  for the berry bowl, sorbet, custard, cereal and stir fry.
- Blocks: bamboo stalks, poles, blocks, stairs, slabs, doors, fences, gates,
  ladders and bamboo wall brackets use modern bamboo (they were wood, like
  Growthcraft bamboo before its mapping); the 9 cacti use the crunchy plant cue
  (wood); the 9 corals and coral fragments use wet coral (gravel); the 3 shelf
  fungi use mushroom stems (wood); wooden brackets use wood and the iron and
  gold curl brackets small metal (all brackets had the default stone). The
  other 315 plants and the 36 hanging planters keep their grass step.
- Interaction: ripe berry bushes drop berries and reset on use without a sound;
  they now play the fruit snap, like BOP's bushes. Nothing else in PMP acts on
  use.
- Past Footsteps: the pack's `aa-footsteps.zip` already maps every PMP block
  except the bamboo stairs, which follow the pack's convention of leaving all
  stairs to Past Footsteps' fallback (wood here). No change.
- jack's in-game check, 2026-11-07: produce, meals and containers, the block
  forms and the berry bush cue work. Accepted.

## Natura review - 2026-11-07

- Source: the pack's `Natura-2.8.24.jar` (decompiled `NContent` registrations,
  block sounds and item classes) and `config/Natura.cfg` (blaze rails, hoppers,
  nether glass, furnaces, obelisks and flint and blaze enabled).
- Wood blocks (planks, logs, workbenches, bookshelves, stairs, slabs, fences,
  gates, trapdoors, plates, buttons, doors) set the wood sound already and keep
  their fallback. Ghostwood tools already resolve to wood.
- 67 definitions: wooden tools whose tiers misled the classifier (darkwood
  stone, fusewood iron, bloodwood custom), imp armor (custom material, now
  leather), the `barleyFood` crafting items by metadata (barley hay, flours,
  sulfur, fletching, flame string and dye dust, cotton like HarvestCraft's,
  imp leather), sticks, bowls, stews, berry medley, berries, potash apple,
  saguaro fruit, imp meat, water drop, seed and bone bags, spawn eggs, flint and
  blaze and the door item, which all fell back to the generic pickup.
- Blocks: the bloodwood log used Natura's metal sound (now wood); blaze rails
  set no sound and fell to stone (now rails); large glowshrooms were wood (now
  mushroom blocks); leaves, saplings, bluebells, thornvines and berry bushes
  follow BOP's foliage; the saguaro follows PMP's cacti.
- Interaction: ripe berry bushes, overworld and Nether, hand over berries on
  either click without a sound of their own (the berry's pickup pop only); both
  now play the fruit snap. Seed bags already play the crops' placing sound; the
  bone bag shows bone meal particles silently, like vanilla bone meal in 1.7.10.
  Crops were covered earlier through Hunger Overhaul.
- Past Footsteps: the pack's map covers every Natura block except stairs, which
  follow the pack's convention. No change.
- jack's in-game check, 2026-11-07: tools and gear, food and materials and the
  block forms work. Per jack, cactus juice (`waterdrop`) now uses the meat cue
  instead of a soft liquid. The berry bush cue is untested: bonemeal only grows
  Natura's bushes to full size and stacks them, never to ripe (by design; they
  ripen on random ticks in light). The creative tab lists ripe bushes, but
  the item always places the youngest stage (metadata mod 4); test with
  `/setblock ~1 ~ ~ Natura:BerryBush 12` (or `Natura:NetherBerryBush`, 12-15).
- Berry bush cue confirmed with set-ripe bushes on both clicks, overworld and
  Nether. Accepted.

## Netherlicious review - 2026-11-07

- Source: the pack's `netherlicious-3.2.8.jar` (decompiled `ModItems`,
  `ModBlocks`, `ModSounds` and item textures) and AAC's startup log, which
  pairs 89 Netherlicious items with Et Futurum namesakes. AAC hides those from
  the creative tabs and NEI and makes them drop as the namesake; Netherlicious'
  biomes still place its own blocks.
- Blocks keep their sounds: Netherlicious defines modern sound types (nylium,
  stem, nether wood, basalt, lantern, chain, bone, moss, roots, sprouts, wart,
  shroomlight, amethyst and more) and uses stone where modern Minecraft does
  (blackstone, quartz, crying obsidian, magma). The nether beacon falls to
  stone, which is how vanilla's beacon steps in 1.7.10.
- 58 item definitions (see EXTRA_SOUNDS.md). Notable: Efrine and heavy blaze
  gear used custom materials (generic); Netherlicious' seeds are plain items,
  not seeds; devilish popcorn extends `ItemSoup` and would have sloshed.
- Interaction: doors, gates, trapdoors, buttons, stripping, planting, igniting,
  hellderberry bushes and glistering vines already play sounds. Abyssal oat and
  devilish maize harvest and replant silently on use; Hunger Overhaul usually
  harvests them first (they are `BlockCrops`) and its cue now cuts abyssal oat
  like grain; a hook on their own harvest covers the rest. Barrels, the
  blackstone furnace, nether crafting table and beacon open screens with the
  generic screen sound.
- Past Footsteps: the pack's map covers every Netherlicious block except
  stairs (the pack's convention) and brittle bedrock, which falls back to its
  stone sound. No change.
- jack's in-game check, 2026-11-07: gear, materials, food and both crop
  harvest cues work. Accepted.

## Aether Legacy Departure review - 2026-11-07

- Source: the local `AetherLegacyDeparture` checkout for classes and textures,
  checked against the pack's `Aether.Legacy.Departure.Snapshot.26w35a.jar`
  registries (the jar adds `palladium_eye`, `skyroot_door_item`,
  `tempest_insignia`, `aether_cactus` and the stratos aerclouds and drops
  `zephyroo_leather` and `elysian_totem`; the jar's lists were used).
- 496 items, 464 block registrations. Tools and armor use vanilla tool and
  armor tiers, so the classifier gave tier sounds (skyroot stone, Neptune and
  obsidian diamond, gravitite and valkyrie emerald). 483 definitions map gear by
  the material in its name, accessories by type and material, and materials,
  food and misc items by vanilla family (see EXTRA_SOUNDS.md). 46 named special
  weapons and bows keep the classifier's tier sound.
- Blocks: aerclouds cloth, quicksoil sand, skyroot wood, holystone and dungeon
  stone and metal blocks metal are right and keep their fallback. Auralite and
  aceninum clusters and blocks used glass, which steps like stone in 1.7.10;
  they now chime like amethyst. Leaves and saplings follow BOP's foliage.
- Interaction: berry bushes, orange and grape trees give fruit when broken
  (break sound). Chests, mimics and the elysian totem play their own sounds.
  Not handled yet: the ancient enchanter (altar) takes ambrosium and items and
  drops them back silently, and the lore book's page buttons click; both are
  candidates for cues.
- Past Footsteps: the pack's map has 424 Aether keys and covers every block
  except stairs. No change.
- jack's in-game check, 2026-11-07: gear, food, crystal blocks and foliage
  work. Rings (and pendants, which share the cue) sound far too heavy with the
  nugget sound; golden amber as honeycomb is questioned (it is resin from
  golden oak logs, but looks like a gem). Both on an audition page
  (`/tmp/aet-aether-sounds/index.html`).
- Picks: golden amber stays honeycomb (AMB1). No vanilla ring option was light
  enough; from a CC0 page jack picked MaGs_007's jewelry recordings, JW21 for
  rings and JW18 for necklaces and amulets. They ship as `item.category.ring`
  and `item.category.amulet` for later mods' jewelry (Twilight Forest charms,
  Mo' Creatures amulets); Aether's metal rings, pendants, medals and insignias
  use them. Per jack, auralite pendants layer the amulet chain with the
  amethyst shard ding (an `.accent` event).
- jack's in-game check: rings, pendants and medals and the layered auralite
  pendant work. Accepted.

