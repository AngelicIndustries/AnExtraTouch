# Adventure Backpack Extra Sounds

Implemented for GTNH AdventureBackpack2 1.4.28-GTNH, using the sound choices
approved on 2026-11-01. The audition page's 50% preview volume is not an extra
in-game multiplier; the existing AET volume settings remain in control.

## Selected cues

| Action | Recording / event | Adjustment | Setting |
| --- | --- | --- | --- |
| Backpack equip / unequip | Minecraft leather armor equip | Unequip pitch 0.9 | `armorSoundsEnabled`, `armorSoundVolume`, armor entity filter |
| Backpack item pickup / placement | Minecraft leather armor equip | Volume 0.7; placement pitch 0.9 | `extraInventoryClicks` |
| Copter / jetpack equip / unequip | Minecraft iron armor equip | Unequip pitch 0.9 | Same armor settings |
| Hose mode / tank selection | AET inventory scroll tick | Mode pitch 1.8; tank pitch 1.494 after alias multiplication | `extraScreenScroll` |
| Tool cycling on / off | Minecraft wooden button on / off | Native volume 0.3 and pitches 0.6 / 0.5 | `extraInventoryClicks` |
| Pour container into tank | Minecraft bucket empty | Volume 0.65 | `extraInventoryClicks` |
| Fill container from tank | Minecraft bucket fill | Volume 0.65 | `extraInventoryClicks` |
| Empty tank with hose | Minecraft bucket empty | Volume 0.65, pitch multiplier 0.9 | `extraInventoryClicks` |
| Cycle held tool | Existing AET item selection sound | No additional cue | Existing item-equip settings |
| Inventory / worn-pack navigation | Silent | No renewed GUI animation | Same navigation handling as Baubles |

UI accents use `extraSoundsVolume` and the Players sound category. Equipping uses
the existing armor volume and entity filtering, including nearby players.
Modern sounds prefer EFR's assets when available, then MCLib's downloaded assets,
with local fallbacks. Adventure Backpack and EFR both remain optional.

Inventory pickup/place definitions cover every registered Adventure Backpack
item and block, including all nine component metadata values:

- Campfire and machete handle: wood handling.
- Sleeping bag (component and block) and inflatable boat: cloth handling.
- Backpack tank and melon juice: the potion bottle-handling cue (bottle fill,
  not glass block placement, which resolves to stone samples).
- Machete: the same iron gear cue as an iron sword.
- Adventurer's hat, suit, pants and hose: leather handling.
- Piston boots, powered packs, nozzle, engine, blades and motorized boat: iron gear.
- Clockwork crossbow: the bow's generic gear cue.
- Backpack block: the backpack item's leather straps.

These are explicit per-item resource events, so custom block step sounds and
`RUGGED_IRON` / `RUGGED_LEATHER` material names do not send them to unrelated
fallbacks. Component metadata is one-based (sleeping bag 1, tank 2, through hydro
blades 9). Pickup/place retain the usual pitch difference.

The resource-pack events are `backpack.leather.equip`, `backpack.leather.unequip`,
`backpack.iron.equip`, `backpack.iron.unequip`, `backpack.hose.mode`,
`backpack.hose.tank`, `backpack.cycling.on`, `backpack.cycling.off`,
`backpack.tank.fill`, `backpack.tank.drain` and `backpack.tank.empty`, all in the
`anextratouch` namespace. They retain the auditioned event aliases and pitch /
volume multiplication; no Minecraft recordings are bundled in the AET jar.

## Behavior

- Equipping observes synchronized wearable identity, not fuel, contents or mode
  NBT. The first update establishes a silent baseline. Unequip waits for the
  backpack mod's delayed removal rather than sounding when the button is pressed.
- Hose and cycling controls compare the state before and after the local action.
  Unchanged and disabled actions stay silent. Existing engine, night-vision,
  sleeping-bag and Tinkers repair sounds are retained without extra accents.
- Tank actions begin with an actual change to fluid slots or predicted tank state.
  Sound waits for the corresponding accepted inventory transaction, authoritative
  filled/empty output, or synchronized tank and container data. Wrong-direction
  changes and boiler consumption alone do not confirm a container transfer.
- Fluid cues replace the ordinary inventory cue for that transaction. Separate
  clicks to pick up a resulting container still have their usual inventory cue.
  NEI's multiple clicks within one tick are coalesced per tank. Pending actions
  expire after two seconds and are canceled on container/player/world changes or
  when inventory accents are disabled. An already empty tank has no drain cue.
- Inventory and Baubles navigation to/from worn backpack, copter and jetpack
  screens is silent. Held and placed backpacks remain real container openings.
- No custom server packets or server-side AET installation are needed.

## Development dependency

`devOnlyNonPublishable("com.github.GTNewHorizons:AdventureBackpack2:1.4.28-GTNH:dev")`
puts the mod on compile and development runtime classpaths. It is neither bundled
nor published as a required dependency of AET.

## Validation

Unit tests cover silent initial synchronization, NBT/damage/count changes,
equip/unequip identity, transfer direction and minimum amounts, container-result
confirmation, one-shot playback, empty-tank no-ops, expiration and the approved
sound definitions. The existing sound-resource tests check modern fallbacks and
alias resolution.

An isolated dev-client probe exercised leather and powered-pack equip/unequip,
NBT-only updates, hose mode/tank selection, both cycling toggles, scroll muting,
held/worn/placed backpack filling, draining and hose emptying, and held jetpack
water transfers. Logs showed exactly one fluid cue for each successful transfer.
The navigation predicate was checked in both directions and rejects held-pack
screens. The probe is outside the repository and is not included in the jar.

The inventory material probe resolved actual audio samples for all 15 registered
items/blocks (99 creative variants, including all nine components). It explicitly
checked that tanks and melon juice resolve to bottle samples rather than the stone
samples behind Minecraft's glass-placement event.

A separate isolated launch without Adventure Backpack or EFR passed the optional
entry points and a vanilla inventory click. All eleven events resolved to actual
samples with the approved volume and pitch, using MCLib for modern assets. That
launch also excluded Do a Barrel Roll, which otherwise crashes when it accesses
EFR's absent elytra API. The normal dev dependencies were left unchanged.

These are automated client/integrated-server checks, not an ears-on acceptance
round. A separate remote server, latency stress and the full NEI drag/transfer
matrix remain useful gameplay checks.
