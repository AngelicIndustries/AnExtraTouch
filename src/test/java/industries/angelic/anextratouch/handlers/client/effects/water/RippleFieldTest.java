package industries.angelic.anextratouch.handlers.client.effects.water;

import static org.junit.Assert.*;

import org.junit.Test;

import industries.angelic.anextratouch.util.SwimBobTracker;

public class RippleFieldTest {

    private RippleField pond(int quality) {
        RippleField field = new RippleField(quality, 0, 0);
        for (int z = 0; z < 32; z++) {
            for (int x = 0; x < 32; x++) field.setWater(x, z, 63.875F);
        }
        return field;
    }

    @Test
    public void calmWaterStaysCalmAtEveryQuality() {
        for (int quality = 4; quality <= 16; quality++) {
            RippleField field = pond(quality);
            for (int step = 0; step < 120; step++) field.step();
            assertFalse(field.isActive());
        }
    }

    @Test
    public void wavesPropagatePersistAndDissipateWithoutNewInput() {
        RippleField field = pond(4);
        field.disturb(16, 16, 63.875F, 0, 0, 3, 0.4F);
        for (int step = 0; step < 60; step++) field.step();
        assertTrue(field.isActive());
        assertTrue(Math.abs(field.heightAt(16 * 4 + 7, 16 * 4)) > 0.00001F);
        float energy = energy(field);
        for (int step = 0; step < 1200; step++) field.step();
        assertTrue(energy(field) < energy * 0.01F);
    }

    @Test
    public void scrollKeepsTheWakeAtItsWorldPositionAndTeleportClearsIt() {
        for (int density = 4; density <= 16; density++) {
            RippleField field = pond(density);
            assertEquals(density, field.samplesPerBlock);
            assertEquals(32 * density, field.size);
            field.disturb(16, 16, 63.875F, 2, 1, 0, 0.4F);
            field.step();
            float height = field.heightAt(16 * density, 16 * density);
            assertNotEquals(0, height, 0);
            field.moveTo(1, -1);
            assertEquals(height, field.heightAt(15 * density, 17 * density), 0);
            assertEquals(63.875F, field.waterAt(16, 16), 0);
            field.moveTo(1000, 1000);
            assertEquals(0, energy(field), 0);
            assertEquals(-1, field.waterAt(1000, 1000), 0);
        }
    }

    @Test
    public void banksAndDifferentWaterLevelsDoNotTransmitWaves() {
        RippleField field = pond(4);
        for (int z = 0; z < 32; z++) {
            field.setWater(16, z, -1);
            for (int x = 17; x < 32; x++) field.setWater(x, z, 70);
        }
        field.disturb(15, 16, 63.875F, 0, 0, 4, 0.4F);
        for (int step = 0; step < 180; step++) field.step();
        for (int z = 0; z < field.size; z++) {
            for (int x = 64; x < field.size; x++) assertEquals(0, field.heightAt(x, z), 0);
        }
    }

    @Test
    public void movingInputsStayFiniteAndBoundedAtEveryQuality() {
        for (int quality = 4; quality <= 16; quality++) {
            RippleField field = pond(quality);
            for (int step = 0; step < 180; step++) {
                field.disturb(8 + step * 0.05, 16, 63.875F, 3, 0, 0, 0.4F);
                field.step();
            }
            assertTrue(field.isActive());
            for (int z = 0; z < field.size; z++) {
                for (int x = 0; x < field.size; x++) {
                    float h = field.heightAt(x, z);
                    assertTrue(Float.isFinite(h));
                    assertTrue(Math.abs(h) < 0.3F);
                }
            }
        }
    }

    @Test
    public void swimmingAndBoatingLeavePronouncedTrailsWithoutFlattenedHeightPeaks() {
        for (int quality : new int[] { 4, 6, 8 }) {
            for (float speed : new float[] { 2.2F, 6.0F }) {
                RippleField field = pond(quality);
                double px = 8;
                for (int tick = 0; tick < Math.round(12 / speed * 20); tick++) {
                    field.beginTick();
                    for (int input = 1; input <= 3; input++) {
                        field.disturb(px + speed / 20 * input / 3, 16, 63.875F, speed, 0, 0, speed > 3 ? 0.65F : 0.4F);
                    }
                    px += speed / 20;
                    for (int step = 0; step < 3; step++) field.step();
                }
                // The departing trail must have visible slopes, not merely a raised, flat body patch.
                assertTrue(trailSlope(field, px) > (speed > 3 ? 0.12 : 0.04));
                for (int z = 0; z < field.size; z++) {
                    for (int x = 0; x < field.size; x++) assertTrue(Math.abs(field.heightAt(x, z)) < 0.29F);
                }
                for (int step = 0; step < 180; step++) field.step();
                assertTrue(trailSlope(field, px) > (speed > 3 ? 0.025 : 0.003));
            }
        }
    }

    @Test
    public void swimmingInPlaceProducesOutgoingRipplesAcrossResolutions() {
        for (int quality : new int[] { 4, 6, 8, 11, 16 }) {
            for (double bob : new double[] { 0.006, 0.035, 0.08 }) {
                RippleField field = pond(quality);
                SwimBobTracker tracker = new SwimBobTracker();
                double slopeSum = 0;
                int samples = 0;
                for (int tick = 0; tick < 200; tick++) {
                    field.beginTick();
                    // No horizontal travel or entry splash. Include smoothed remote-player bobs.
                    double stroke = tracker.update(tick % 20 < 10 ? bob : -bob);
                    field.swimStroke(16, 16, 63.875F, stroke, 0.4F);
                    for (int step = 0; step < 3; step++) field.step();
                    for (int z = 1; z < field.size - 1; z++) {
                        for (int x = 1; x < field.size - 1; x++) {
                            assertTrue(Math.abs(field.heightAt(x, z)) < 0.25F);
                            double radius = Math
                                .hypot((x + 0.5) / field.samplesPerBlock - 16, (z + 0.5) / field.samplesPerBlock - 16);
                            if (tick < 100 || radius < 1 || radius > 3) continue;
                            double dx = (field.heightAt(x - 1, z) - field.heightAt(x + 1, z)) * field.samplesPerBlock
                                * 0.5;
                            double dz = (field.heightAt(x, z - 1) - field.heightAt(x, z + 1)) * field.samplesPerBlock
                                * 0.5;
                            slopeSum += dx * dx + dz * dz;
                            samples++;
                        }
                    }
                }
                // Require ripples outside the body, not only a local depression beneath it.
                assertTrue(Math.sqrt(slopeSum / samples) > 0.02);
                float activeEnergy = energy(field);
                for (int tick = 0; tick < 400; tick++) {
                    field.swimStroke(16, 16, 63.875F, tracker.update(0), 0.4F);
                    for (int step = 0; step < 3; step++) field.step();
                }
                assertTrue(energy(field) < activeEnergy * 0.01F);
            }
        }
    }

    @Test
    public void absentOrInvalidSwimmingStrokesLeaveStillWaterAlone() {
        RippleField field = pond(4);
        for (double stroke : new double[] { 0, -0.01, Double.NaN, Double.POSITIVE_INFINITY }) {
            field.swimStroke(16, 16, 63.875F, stroke, 0.4F);
            field.step();
            assertFalse(field.isActive());
            assertEquals(0, energy(field), 0);
        }
    }

    private double trailSlope(RippleField field, double headX) {
        double sum = 0;
        int count = 0;
        for (int z = 1; z < field.size - 1; z++) {
            for (int x = 1; x < field.size - 1; x++) {
                float wx = x / (float) field.samplesPerBlock;
                float wz = z / (float) field.samplesPerBlock;
                if (wx > headX - 1 || wx < headX - 8 || Math.abs(wz - 16) > 4) continue;
                float dx = (field.heightAt(x - 1, z) - field.heightAt(x + 1, z)) * field.samplesPerBlock * 0.5F;
                float dz = (field.heightAt(x, z - 1) - field.heightAt(x, z + 1)) * field.samplesPerBlock * 0.5F;
                sum += dx * dx + dz * dz;
                count++;
            }
        }
        return Math.sqrt(sum / count);
    }

    private float energy(RippleField field) {
        float energy = 0;
        for (int z = 0; z < field.size; z++) {
            for (int x = 0; x < field.size; x++) energy += Math.abs(field.heightAt(x, z));
        }
        return energy;
    }
}
