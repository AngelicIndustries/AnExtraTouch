# 0.0.13 preparation

Reviewed 2026-10-19. The user authorized `0.0.13-angelic-snapshot` after the AngelLib rebrand and editor
extraction. Final `0.0.13` acceptance remains below. Even Mo' Bends is on hold pending the WawelAuth
player-model rework.

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

## Sound editor moved to Sound Manager

On 2026-10-19 the experimental sound editor, translations, and pack-writing tests moved to the standalone
`SoundManager/` project (mod ID `soundmanager`). AET no longer contains or opens the editor. Its unfinished
redesign and Past Footsteps integration acceptance are tracked in Sound Manager's release checklist.

## Validation and cleanup

* After the editor extraction, `VERSION=0.0.13 ./gradlew build` passes with 31 tests and no failures or skips.
  The four pack-writing tests now belong to Sound Manager and also pass.
* Local PF integration is now an explicit `-PpastFootstepsJar=...` development option. A standalone checkout
  no longer references a sibling build. Checked both the standalone and explicit-jar runtime classpaths.
  PF remains optional for AET.
* The latest upstream keybinding translation commit was fast-forwarded into the local branch. The editor
  prototype and its tests were preserved in the separate Sound Manager checkout.

## Before the final 0.0.13 release

* Coordinate consumers of the renamed `industries.angelic.anextratouch` API, including MobRewind's camera-shake
  integration. Review Past Footsteps' explicitly legacy AET mixins before changing their target names.
* Check the migration with a packaged PF build: one owner for footsteps/footprints, AET equip sounds intact,
  and useful behavior with either mod installed alone.
* Smoke-test client/server startup and representative water, shader, camera and sound combinations against the
  chosen release dependencies. The unit suite is not runtime acceptance of every visual feature.
* Reconcile the changelog, README/wiki, dependency versions and platform relations for the final release.
* Choose the release tag and publish after review. Existing platform projects and release workflows are present.

The detailed feature list is in [.changelogs/0.0.13.md](.changelogs/0.0.13.md). No new feature work is needed on
Even Mo' Bends to complete this AET/PF cleanup.

## Angelic Industries migration — 2026-10-19

* Main and test packages, generated Tags, proxy/GUI/coremod/mixin class references, and Maven group now use
  `industries.angelic.anextratouch`. Artifact ID `AnExtraTouch`, mod ID `anextratouch`, and existing config/resource
  names stay the same. Imports of the old API must be updated by consumers.
* Sound interfaces and utilities use released `industries.angelic.angellib:AngelLib:1.0.0`. Forge, Maven, and
  platform publishing configuration require AngelLib, GTNHLib, and UniMixins.
* Updated author/source/issue/wiki/Maven links and POM organization/SCM metadata. Upstream credits, artwork,
  and historical asset-license notices are preserved.
* GTNH build conventions updated to `2.0.34`; Gradle remains `9.7.1`. Other retained dependency versions are
  unchanged. Removed the optional WawelAuth `1.0.5` and VintageResourcify `1.0.5` development dependencies:
  those artifacts still load legacy FentLib classes and cannot run with AngelLib alone.
* Clean build, formatting/checkstyle, and all **35 tests** passed. JAR, generated POM, and Gradle metadata checks
  confirmed the new identity and AngelLib dependency.
* Packaged Java 8 client/server started with AET, AngelLib `1.0.0`, GTNHLib `0.11.49`, and UniMixins `0.3.1` only;
  the client joined and handled a thunder sound request without linkage errors. Normal development server
  startup also passed. These are smoke checks, not acceptance of the unfinished sound editor or every visual effect.
* The stock Forge mod list rendered black after scrolling in the isolated client; Escape returned to the menu.
  No crash was logged. Its cause has not been isolated; check it during the remaining UI acceptance pass.
* The rebrand and extraction are included in `0.0.13-angelic-snapshot` preparation. The snapshot workflow
  publishes GitHub, Maven, and 67Minecraft builds; Modrinth and CurseForge uploads are skipped for snapshots.
  Refresh platform descriptions from the final README when preparing the final release.
* Backup, build/runtime logs, screenshots, and artifact checks: `/tmp/anextratouch-angelic-migration-20261019/`.
