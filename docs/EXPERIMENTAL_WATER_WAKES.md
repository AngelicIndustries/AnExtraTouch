# Experimental shader water wakes

This mode simulates a persistent height field and adds its slopes to the shader
pack's water normals before reflections. The pack still owns water color,
transparency, lighting and background waves. No second water surface is drawn.

In Water Splash / Surface Trails:

- `experimentalWaterWakes`: off by default. Enable, then reload the shader pack.
  Reload after disabling it too, to remove the injected code completely.
- `experimentalWaterWakeQuality`: integer samples per block, from 4 to 16,
  default 6. Every integer is supported. The texture width is 32 times this value:
  4 = 128, 6 = 192, 7 = 224, 8 = 256, 12 = 384, 16 = 512. Changes apply live and
  reset the field. The old 0/1/2 presets migrate to 4/6/8 automatically.
- `experimentalWaterWakeStrength`: 0 to 3, default 1. Try 2, then 3 for more
  visible ripples. This affects both swimming and boats. Changes apply live
  without resetting the field or adding simulation cost.

All resolutions cover the same 32 x 32 blocks, centered on the render-view entity.
Higher densities retain smaller ripples; CPU work, memory and upload bandwidth
grow approximately with density squared. A density of 12 uses four times as many
cells as the default 6. Use strength, not quality, to increase visibility. The
simulation runs three fixed steps per 20 Hz game tick; the shader interpolates the two tick states. One RGBA16F texture
is uploaded only when the field changes, at most once per game tick. Sampling
cost is five texture reads for nearby, upward-facing water, with early exits for
inactive fields, distant water, other materials and other water levels.

Generation is bounded to 32 nearby entities. Moving living entities, boats and
dropped items push the surface; downward entries make splash ripples. Camera
rotation does not generate waves. Banks, water-level changes and the outer edge
absorb waves. Living entities bobbing at the surface also emit one outgoing
ripple per upward swimming stroke, even without horizontal travel. This uses
actual vertical movement, with a minimum stroke strength for smoothed remote
positions; standing on the bottom and local creative flight do not add strokes.
When movement and bobbing stop, existing ripples continue to spread and fade.
Propagation is 1.2 blocks per second so swimming leaves a concentrated wake. The displaced rim balances the depression to avoid a broad
raised patch, and steep normals compress smoothly instead of hitting a hard
per-axis limit. Teleports, world changes, quality changes and shader reloads
reset or discard the affected history. Calm fields skip simulation and uploads.
The shader takes no screen-space readback and needs no compute shaders.

## Pack support

Adapters recognize the preprocessed water shader's structure, not its filename
on disk. They currently target:

- Complementary Unbound and Reimagined r5.9.3
- Both with Euphoria Patches 1.10.5
- BSL v10.1.5

An unfamiliar structure is left untouched, with a log message, and ordinary AET
wakes remain available. Future pack releases can change the relevant shader code
and may require an adapter update. This is not a universal shader API.

Only exposed source-water surfaces participate. Flowing falls, other fluids,
distant-terrain replacements, and multiple pools stacked within one X/Z column
are outside this initial implementation. A column tracks one exposed surface;
height checks prevent it from shading a different pool above or below it.

The existing `waterWakesEnabled` controls ordinary wake trails. The experimental
switch is independent. On a supported active pack it replaces ordinary water
wake overlays; other fluids retain their existing rendering. It does not alter
coastal waves, rain ripples or splash particles.

## Validation

An isolated Angelica 2.2.43 dev client compiled and rendered all five variants
listed above with an active field. The checks exercised densities 4, 6 and 8,
shader reloads, and disabling the feature. Water programs had the field sampler
and nonzero origin/strength uniforms; each pack checkpoint reported GL error 0.
A follow-up stationary-swimmer probe verified migration from preset 1 to density
6, rendering at density 7, and resizing live to density 16 without shader reload.
Both checkpoints reported GL error 0. These were smoke tests, not GPU performance
benchmarks or a complete gameplay matrix. The disposable probe and its world/configs are outside the repository.

A standalone warmed-up CPU benchmark on the development machine, with a fully
wet field and one moving input, measured approximately 0.37 / 0.83 / 1.49 ms per
game tick at densities 4 / 6 / 8. These are simulation-only measurements, not
whole-frame costs or FPS guarantees.

Unit tests cover calm fields, propagation and decay, bounded moving inputs at every
integer density from 4 through 16, swimming in place (including slow bobs),
stopping strokes, camera
translation, teleports, banks, different water levels, and
shader adapter rejection of unknown or ambiguous programs.

Visual checks should include straight swimming, turns, stopping, jumping into a
pool, boats, other players, underwater viewing, first/third person, shorelines,
shader reload, quality changes and disabling the mode. Shader compilation alone
does not establish identical appearance between packs: each supplies its own
water material and reflections.
