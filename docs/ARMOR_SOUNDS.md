# Armor equip categories

`armorNameRules` is in the existing `armor` config category and **Sounds > Armor**
in the config GUI. Each entry is `category:phrase`. Matching is a case-insensitive
substring search of both the item's unlocalized name and its `ArmorMaterial` name.
It does not use the translated display name.

Resolution order:

1. `armorCategoryOverrides` by item registry name, including `none` overrides.
2. Vanilla materials: cloth = light, chain = medium, iron/gold = heavy, diamond = crystal.
3. Name rules in EFR order: none, medium (chain), crystal (diamond), heavy (gold/iron),
   light (leather), netherite, elytra, turtle, generic.
4. `armorDefaultCategory`, still heavy by default.

The material words in parentheses, plus `netherite` and `elytra`, match without
custom entries. Turtle and generic use explicit rules. Rules within a category
have equal priority; rearranging the list does not change category priority.
For example, `IRONWOOD` matches iron before the default `turtle:wood` rule.
A `none` name rule does not override a vanilla material; use a registry override
when that is required. Invalid/empty rules are logged and skipped, and reload
replaces the old rules.

The default list translates EFR's `DEFAULT_CUSTOM_ARMOR_EQUIP_RULES`:
leather -> light, chain -> medium, gold/iron -> heavy, diamond -> crystal,
netherite -> netherite, turtle_helmet -> turtle, and elytra -> elytra.
Examples include copper/silver/steel -> heavy, amethyst/mithril -> crystal,
cultist -> medium, thermal_padding/wool -> light, heavyblaze/vulcanite -> netherite,
wood -> turtle, and hazmat -> elytra.

## Modern armor sounds

Netherite, turtle, generic, and elytra have AET events named
`anextratouch:armor.<category>_equip`. They use EFR's corresponding modern
`item.armor.equip_<category>` event when available, even with EFR `armorEquip=false`.
The running EFR version supplies its asset namespace; it is not hard-coded.
AET also requests these events through its bundled AssetDirector, so they work
without EFR. If neither source has loaded samples, netherite falls back to AET
heavy; turtle, generic, and elytra fall back to AET light. Resource packs can
replace the AET events as usual. Existing light/medium/heavy/crystal recordings,
entity whitelist, client-side equip/unequip detection, and armor volume are unchanged.
The Extra Sounds group toggle and volume do not control armor equip playback.

## Pack examples with default settings

These classifications were checked against local source/jars and resolver tests.

| Mod and armor | Category | Reason |
| --- | --- | --- |
| Aether Departure Gravitite | crystal | Reports vanilla DIAMOND material |
| Twilight Forest Ironwood | heavy | IRONWOOD contains iron; that precedes wood |
| Twilight Forest Steeleaf | heavy | Steeleaf matches the steel rule |
| Biomes O' Plenty Amethyst | crystal | AMETHYST material and helmetAmethyst name |
| Netherlicious Efrine | heavy | efrine rule |
| Netherlicious Heavy Blaze | netherite | heavyblaze rule |
| Mowzie's Mobs Wrought helmet | heavy | Reports vanilla IRON material |
| Mowzie's Mobs Barakoa masks | medium | getArmorMaterial reports CHAIN, despite constructing with CLOTH |

Unmatched armor still uses the configured default. This does not invent new
material mappings for every mod's armor.

Source for rule ordering/default phrases: Et Futurum Requiem's
`core/handlers/ServerEventHandler.java` and `configuration/configs/ConfigSounds.java`.

Validation: the dev client resolved all four modern equip events with EFR's
`armorEquip` disabled, verified their sample files, matched an actual modded
VULCANITE material, and classified all four registered BOP amethyst armor items
as crystal. Automated tests cover overrides, vanilla precedence, name/material
matching, rule priority, reload, invalid rules, locale-independent matching, and
all four modern categories with EFR absent or missing assets. The other pack
examples were checked from source/jars and test fixtures, not worn in-game.
