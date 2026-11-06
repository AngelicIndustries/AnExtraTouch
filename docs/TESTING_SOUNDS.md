# Test the Extra Sounds and armor changes

The AET dev client includes EFR, Biomes O' Plenty, EMB, and Angelica. Use the
**AET Extra Sounds test** world, or create a new creative superflat world with cheats.
The other saved worlds are unrelated to this test.

For this test run, all Extra Sounds toggles are enabled, including the normally-off
`extraEntityPoof`. EFR's `armorEquip` is disabled to avoid duplicate equip sounds.
Master and Players volume are both 100%; AET armor volume remains 0.3.
Original configs are backed up under `run/sound-test-backup-20261029-234343/`.

Open **Mods > An Extra Touch > Config > Sounds** for AET settings. After changing
settings, click Done to apply them. Compare a sound with its own toggle off/on;
vanilla and other mods can still make their own sounds.

## 1. Inventory actions

Use creative/NEI to take stone, planks, wool, sticks, arrows, iron ingots, a sword,
a potion, a book, and a chest. For ordinary inventory operations, switch to survival
with `/gamemode 0`, put the materials in the chest, and open it.

- [ ] Pick up a stack, place it in an empty slot, and right-click to place one item.
  Pickup and placement have different pitch; different items use different sounds.
- [ ] Swap the cursor's item with a different stack. Expect one extra click cue.
- [ ] Shift-click stacks between the chest and player inventory.
- [ ] Hover a slot and press a hotbar number to swap with that hotbar slot. Also test
  an empty hovered slot with an occupied hotbar slot.
- [ ] Hold a stack on the cursor, left-drag over several empty slots, then release.
  Expect a drag cue when the distribution completes, not an outside-drop sound.
- [ ] Repeat with right-drag to distribute one item per slot.
- [ ] Split identical items among slots, then double-click one to collect them.
  Expect the collection cue.
- [ ] Click an empty slot with an empty cursor, or attempt a shift-click when there
  is no room at the destination. No successful-action cue should play.
- [ ] Change `extraInventoryClicks` off and repeat. These added cues should stop.
  Restore it afterward.

## 2. Creative operations and drops

Return to creative with `/gamemode 1` and open the vanilla creative inventory.
Test its palette and player-inventory tab, not NEI's separate item browser.

- [ ] Middle-click an item to clone it. Expect the clone cue.
- [ ] Pick up a stack, then delete it in the creative inventory trash slot.
- [ ] Put disposable items in the inventory, then shift-click the trash slot.
  Expect delete-all. This actually clears that test inventory.
- [ ] Pick up a creative palette stack and reduce/discard it against the palette.
  Expect a partial-delete cue when the cursor stack shrinks.
- [ ] With an item selected in the hotbar, press Q; then drop a full stack with Ctrl+Q.
  A full stack has a lower drop pitch than one item. An unstackable item uses the
  single-item pitch.
- [ ] In a container, hover an occupied slot and press Q/Ctrl+Q. Then pick up a stack
  and click outside the window, with both left and right mouse buttons.
- [ ] Drop a copied item directly from the creative palette with Q.
- [ ] Disable `extraCreativeActions`: clone/delete cues stop, but pickup and drop
  cues still work. Restore it; disable `extraItemDrops` and verify drops become silent
  without muting inventory clicks. Restore that too.

## 3. Screens and scrolling

- [ ] Open/close player inventory, a chest, and an anvil. Each transition adds its
  own open/close cue. Existing chest/anvil sounds can also play.
- [ ] Open creative inventory: expect one open cue, not open-close-open.
- [ ] Open and close chat or the pause menu: no inventory open/close cue.
- [ ] Scroll the vanilla creative palette by wheel and by dragging its scrollbar.
  Cues track row changes; rapid scrolling is rate-limited.
- [ ] Scroll against the end of the list. No row change means no added cue.
- [ ] Disable `extraInventoryScreens` and `extraScreenScroll` individually and compare.
- [ ] With NEI present, scroll the item list and bookmark pages, then recipe pages
  and recipe tabs. Scroll and drag a recipe list's scrollbar. Expect the same cue
  and speed-dependent pitch as creative scrolling. A list with no movement stays
  silent; searching or refreshing the layout should not make a scroll cue.
- [ ] NEI's left/right page buttons and page hotkeys do not play the extra scroll cue.
- [ ] Open crafting and usage recipes, navigate between them, then return to the
  inventory. These transitions should not restart the GUI animation or play extra
  inventory open/close cues. Closing the inventory entirely still plays its close cue.
- [ ] Disable `extraScreenScroll` and repeat NEI scrolling, then restore it.

## 4. Typing, chat, and screenshots

- [ ] In chat, type letters; use backspace/delete; move with arrows/Home/End; select
  text; cut and paste your own test text. Type/move/erase/cut/paste have separate cues.
- [ ] Press backspace in an empty field or move past the end. No edit or cursor change
  means no cue. Copy alone should not make an edit cue.
- [ ] Repeat typing in the creative search field or an anvil rename field.
- [ ] Place a sign: type, move between lines, and erase text.
- [ ] Write in a book and quill, including newlines, erasing, and the signing title.
  Existing book page-flip sounds are a separate feature.
- [ ] Disable `extraTyping`, repeat one field/sign/book edit, and restore it.
- [ ] Send an ordinary chat line. Expect the message cue.
- [ ] Send your own name in your own chat message. It must not become a mention cue.
- [ ] For the default dev username, run `/tellraw @p {"text":"Hello Developer!"}`.
  Expect the mention cue. Replace Developer if your username differs.
- [ ] Run `/tellraw @p {"text":"Developer123"}`. That is not a whole-name mention.
- [ ] Disable `extraChat`, repeat the chat checks, then restore it.
- [ ] Press F2. After the screenshot saves, expect one screenshot cue. Disable
  `extraScreenshots` and repeat: saving still works without the added sound.

## 5. Effects, blocks, and death particles

Command feedback can also make chat sounds. Temporarily disable `extraChat` if it
makes the effect cues difficult to distinguish.

- [ ] Run `/effect @p 1 5 0`: beneficial effect added, then removed after five seconds.
- [ ] Run `/effect @p 19 5 0`: harmful effect added, then removed after five seconds.
- [ ] Add an effect again and use `/effect @p clear` to test explicit removal.
- [ ] Refresh an already active effect: refreshing its duration should not count as
  a new addition. Joining with existing effects should not announce all of them.
- [ ] Disable `extraStatusEffects`, repeat, and restore it.
- [ ] Place a repeater on a solid block. Right-click through all four settings.
  Three increments use the add cue; wrapping from delay four to one uses reset.
- [ ] Insert a record in a jukebox and eject it. An empty jukebox should not make
  the ejection cue. Disable `extraJukebox` and compare.
- [ ] Disable `extraRepeaters`, cycle the repeater, then restore it.
- [ ] Spawn and kill a pig. With `extraEntityPoof` enabled, the added cue occurs at
  the death particles, not at the initial damage. Disable it and repeat.
- [ ] Restore `extraChat` if you disabled it. Poof normally defaults to off; leave
  it at whichever setting you prefer after testing.

## 6. Armor equip and unequip

EFR's own armor-equip hook is already off. Keep AET `armorSoundsEnabled` on.
For a clearer comparison, temporarily disable `extraInventoryClicks` so inventory
pickup/place cues do not mask equip sounds. Equip and remove one piece at a time.

- [ ] Leather -> light; chain -> medium; iron/gold -> heavy; diamond -> crystal.
- [ ] Equip BOP amethyst armor -> crystal, rather than the unknown-armor default.
- [ ] Equip EFR elytra -> modern EFR elytra event.
- [ ] With Baubles Expanded, equip/remove elytra in its wings slot. Each change
  has one elytra cue, just like the chest slot; durability changes stay silent.
- [ ] Wear a piece while taking durability damage. Durability changes should not
  repeatedly play equip sounds. Opening inventory alone should not re-equip it.
- [ ] Turn `armorSoundsEnabled` off: these equip/unequip cues stop. Restore it.
- [ ] Change `armorSoundVolume` and verify equip volume changes independently of
  `extraSoundsVolume`.

To hear each new category without needing another armor mod, use:

```
/playsound anextratouch:armor.netherite_equip @p
/playsound anextratouch:armor.turtle_equip @p
/playsound anextratouch:armor.generic_equip @p
/playsound anextratouch:armor.elytra_equip @p
```

These commands test the sound events, not automatic armor detection or AET's armor
volume setting. To test automatic routing, temporarily add
`minecraft:diamond_helmet;netherite` to `armorCategoryOverrides`. Equip/remove a
diamond helmet. Repeat with turtle and generic, then remove the test override.

To test the new matcher and precedence:

- [ ] Add `light:amethyst` to `armorNameRules`. BOP amethyst still matches the earlier
  crystal category. Moving the entry higher in the list does not change priority.
- [ ] Add `none:amethyst`: BOP amethyst equip becomes silent.
- [ ] Add `BiomesOPlenty:helmetAmethyst;heavy` to `armorCategoryOverrides`: that helmet
  now uses heavy despite the none rule. The other amethyst pieces remain silent.
- [ ] Add `none:diamond` to `armorNameRules`: vanilla diamond still uses crystal,
  because vanilla material matching comes before name rules.
- [ ] Change the diamond registry override to `minecraft:diamond_helmet;none`:
  this does silence that vanilla helmet.
- [ ] Remove all temporary rules/overrides and verify normal behavior returns.
  Restore `extraInventoryClicks` if you disabled it.

Aether, Twilight Forest, Netherlicious, and Mowzie's Mobs are not in this dev
runtime. Their checked classifications are listed in [ARMOR_SOUNDS.md](ARMOR_SOUNDS.md);
actually wearing those items needs a client containing those mods.

## 7. Resource-pack overrides and volume

An optional **AET-Sound-Override-Test** pack is installed in the dev client's
resourcepacks folder, but is not enabled. Enable it above other sound packs.

- [ ] Pick up stone: it becomes a low harp note.
- [ ] Pick up white wool: low bass-attack note. Red wool (metadata 14) instead uses
  a higher pling. This tests metadata-specific overrides before the general item event.
- [ ] Press F2: screenshot becomes a pling.
- [ ] Play/equip the netherite category: it becomes a low bass note.
- [ ] Disable the pack and apply the change: original sounds return. Re-enable it
  after clicking those items once to test replacement of generated item fallbacks.
- [ ] With `extraSoundsVolume=0`, Extra Sounds hooks are silent. Armor equip remains
  controlled by its own volume. Restore Extra Sounds to 1, then compare 0.5 and 1.
- [ ] Lower Minecraft's Players volume: the new UI/inventory sounds get quieter.
  Restore it after testing.

The pack tests namespaced event aliases, pitch/volume settings, replacement,
per-item and per-metadata selection, and sound reloads without editing the mod jar.
Direct `/playsound` commands bypass AET's feature toggles/group multiplier.

## Find out what really plays

Definitions alone don't say what you hear: resource packs can replace a file
(Rhapsodia replaces `random/bow.ogg`, which the item-drop cue uses) or redefine an
event, and AET aliases and modern-sound fallbacks resolve at runtime. The client
command `/aet_sound` reads the live sound registry and resource packs instead:

* `/aet_sound <event>`, for example `/aet_sound anextratouch:item.drop`, lists every
  entry the event can play, with nested events and AET aliases followed, each file's
  chance, volume and pitch, the resource pack (or mod jar) that provides the file and
  which packs' `sounds.json` define the event. Tab completes registered events.
* `/aet_sound trace <text>` prints, for every sound whose event or file contains the
  text, the file the sound manager actually picked as it plays, with its pack,
  volume and pitch. `trace all` logs every sound (to the log only, as `Sound trace:`),
  and `trace off` stops.

## Remaining separate-environment checks

EFR-absent fallbacks have automated tests, but a full no-EFR game launch is a
separate test. Do not remove EFR from a populated pack world just for this check.
Likewise, multiplayer testing needs a second client: incoming chat/mentions,
remote players' armor changes, and server-rejected inventory actions. Production
mixins need the obfuscated jar in a production instance; this session is the dev build.

For any problem, note the action, game mode, item, enabled test pack, relevant
setting, and approximate time. This session's launcher log is
`/tmp/aet-user-sound-test.log`; Minecraft also writes `run/client/logs/latest.log`.

### NEI bookmarks and item actions

- Bookmark an item, remove it, and repeat by dragging it into the bookmark panel.
  Expect one higher click on add and one lower click on remove.
- Add/remove a recipe or group of bookmarks. Expect one cue per operation, not one
  per ingredient. Duplicate additions and removals that change nothing are silent.
- Toggle a preferred recipe. AET adds no cue; NEI keeps its own sound.
  Loading saved bookmarks and automatically generated favorites stays silent.
- Disable `extraBookmarks` and repeat. Both cues are silent; the operations still
  work. Restore the toggle and check Extra Sounds and Players volume controls.

- Take an item from NEI and drag another into an inventory slot. Pickup uses the
  per-item cue; placement has the same lower pitch as vanilla placement. Full or
  invalid slots and rejected requests are silent.
- Middle-click a panel item: one clone cue. Cancel the preview: no deletion cue.
- Delete one item, a stack, all matching items and the whole inventory. Expect one
  partial/all cue as appropriate. Empty deletion is silent; shift-trash does not
  double the cursor and slot deletion sounds.
- Use NEI scroll transfer and mass transfer. Expect one cue per action, not a burst
  from its internal clicks. A transfer that returns to the original state is silent.
- Disable `extraInventoryClicks` and `extraCreativeActions` independently and repeat
  the corresponding actions. Restore both toggles afterwards.

- Compare single-item, stack, matching-item and full-inventory deletion in creative
  inventory and NEI. All use Vintage Resourcify's pack-delete sound at the same
  pitch and gain. Check that Players volume, Extra Sounds volume and
  `extraCreativeActions` still control it.

### NEI bookmark heart

- Bookmark an item. Its destination slot shows the same green heart as NEI's
  favorite-recipe button, with two soft beats followed by a fade.
- Add several different bookmarks quickly. Each pulse stays at its own item slot.
  Duplicate additions, removals and loading saved bookmarks do not start a pulse.
- Check another bookmark page and resize the GUI. Hearts follow the final visible
  slots rather than the mouse position or a guessed next-slot position.
- Check with NEI item caching on and off. The pulse should animate in both modes.
  Turning NEI's bookmark animations off also disables the heart.
