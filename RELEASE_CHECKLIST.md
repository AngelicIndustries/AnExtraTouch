# 0.0.13 preparation

Reviewed 2026-10-03. Release order: finish Past Footsteps, then AET. Even Mo' Bends is on hold pending the
WawelAuth player-model rework. The maintainer will push and publish.

## Implemented since 0.0.12

* Water/lava splashes, swimming ripples and wakes, coastal foam and optional Angelica shader-water rendering,
  with fixes for water depth, lighting, held-item occlusion and wake continuity.
* Cosmetic steam and lava jets, waterfall spawning limits and Minecraft particle-setting support.
* Modern Dynamic Surroundings thunder/item sounds, duplicate-sound prevention, shield and sword-blocking fixes,
  and optional animal-ambience muting.
* Independent camera-shake patterns, Shoulder Surfing perspective fixes and camera-relative horse steering.
* Config menus, feature documentation, artwork and Catalogue integration.
* Walking sounds, equipment movement, rain steps and footprints moved to Past Footsteps. AET retains armor
  equip/unequip, item sounds and its other effects. Portal inventory restoration no longer triggers equip sounds.
* Upstream keybinding translations from PR #18 are incorporated at `986f659`, preserving local editor strings.

## Work in progress

* Uncommitted experimental sound editor: searchable PF material cards with block previews, installed-sound
  audition and walking-sound replacement/revert through the local `AET Sound Edits` resource pack.
* The corresponding PF override API is also uncommitted. The current editor covers shared block materials and
  exact walking events. Actor-specific maps, every block state and other sound interactions are outside this slice.
* Pack-writing tests cover independent edits, reverting one material, preserving unrelated entries and detecting
  external changes. In-game acceptance must still cover both mods together, absent/older PF, pack precedence,
  save/revert/reopen and compact layouts before treating this feature as ready.

## Validation and cleanup

* `VERSION=0.0.13 ./gradlew build` passes with 35 tests and no failures or skips.
* Local PF integration is now an explicit `-PpastFootstepsJar=...` development option. A standalone checkout
  no longer references a sibling build. Checked both the standalone and explicit-jar runtime classpaths.
  PF remains optional for AET.
* The latest upstream keybinding translation commit was fast-forwarded into the local branch. The shared
  editor changes were preserved in the working tree for review.

## Before publishing

* Finish the shared sound-editor review and acceptance, then commit its current files in both repositories.
* Check the migration with a packaged PF build: one owner for footsteps/footprints, AET equip sounds intact,
  and useful behavior with either mod installed alone.
* Smoke-test client/server startup and representative water, shader, camera and sound combinations against the
  chosen release dependencies. The unit suite is not runtime acceptance of every visual feature.
* Reconcile the changelog, README/wiki, dependency versions and platform relations for the final release.
* Choose the release tag and publish after review. Existing platform projects and release workflows are present.

The detailed feature list is in [.changelogs/0.0.13.md](.changelogs/0.0.13.md). No new feature work is needed on
Even Mo' Bends to complete this AET/PF cleanup.
