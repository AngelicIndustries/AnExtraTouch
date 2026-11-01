package industries.angelic.anextratouch.handlers.client.effects.water;

import java.nio.FloatBuffer;
import java.util.Arrays;

/** Bounded, world-aligned height field. All distances are in blocks, time in seconds. */
public final class RippleField {

    public static final int WIDTH = 32;
    private static final float STEP = 1.0F / 60.0F;
    private static final float WAVE_SPEED = 1.2F;
    private static final float HEIGHT_DECAY = 0.996F;
    public final int size;
    public final int samplesPerBlock;
    public int originX;
    public int originZ;
    private float[] height;
    private float[] previous;
    private float[] next;
    private float[] tickPrevious;
    private float[] surface;
    private final float[] scratch;
    private float energy;

    /** Resolution is an integer density, not a preset index. Whole cells keep block scrolling exact. */
    public RippleField(int density, int x, int z) {
        samplesPerBlock = Math.max(4, Math.min(16, density));
        size = WIDTH * samplesPerBlock;
        originX = x;
        originZ = z;
        height = new float[size * size];
        previous = new float[size * size];
        next = new float[size * size];
        tickPrevious = new float[size * size];
        surface = new float[size * size];
        Arrays.fill(surface, -1);
        scratch = new float[size * size];
    }

    public void moveTo(int x, int z) {
        int dx = (x - originX) * samplesPerBlock;
        int dz = (z - originZ) * samplesPerBlock;
        if (dx == 0 && dz == 0) return;
        shift(height, dx, dz, 0);
        shift(previous, dx, dz, 0);
        shift(tickPrevious, dx, dz, 0);
        shift(surface, dx, dz, -1);
        originX = x;
        originZ = z;
    }

    private void shift(float[] data, int dx, int dz, float empty) {
        Arrays.fill(scratch, empty);
        if (Math.abs(dx) < size && Math.abs(dz) < size) {
            int start = Math.max(0, -dx), end = Math.min(size, size - dx);
            for (int z = Math.max(0, -dz); z < Math.min(size, size - dz); z++) {
                System.arraycopy(data, (z + dz) * size + start + dx, scratch, z * size + start, end - start);
            }
        }
        System.arraycopy(scratch, 0, data, 0, data.length);
    }

    public void setWater(int blockX, int blockZ, float y) {
        int x0 = (blockX - originX) * samplesPerBlock;
        int z0 = (blockZ - originZ) * samplesPerBlock;
        if (x0 < 0 || z0 < 0 || x0 >= size || z0 >= size) return;
        for (int z = z0; z < z0 + samplesPerBlock; z++) {
            for (int x = x0; x < x0 + samplesPerBlock; x++) {
                int i = z * size + x;
                if (Math.abs(surface[i] - y) > 0.25F) height[i] = previous[i] = tickPrevious[i] = 0;
                surface[i] = y;
            }
        }
    }

    public float waterAt(int blockX, int blockZ) {
        int x = (blockX - originX) * samplesPerBlock;
        int z = (blockZ - originZ) * samplesPerBlock;
        return x < 0 || z < 0 || x >= size || z >= size ? -1 : surface[z * size + x];
    }

    public void beginTick() {
        System.arraycopy(height, 0, tickPrevious, 0, height.length);
    }

    /** A moving depression and displaced rim, plus a downward impact on entry. */
    public void disturb(double worldX, double worldZ, float y, float vx, float vz, float impact, float radius) {
        disturb(worldX, worldZ, y, vx, vz, impact, radius, 0);
    }

    /** One outgoing ripple per detected upward swimming stroke, including swimming in place. */
    public void swimStroke(double worldX, double worldZ, float y, double riseSpeed, float radius) {
        if (!(riseSpeed > 0) || !Double.isFinite(riseSpeed)) return;
        // Remote smoothing can spread a stroke across several ticks. Keep slow, real bobs visible.
        float stroke = (float) Math.max(5, Math.min(8, riseSpeed * 100));
        disturb(worldX, worldZ, y, 0, 0, 0, radius, stroke);
    }

    private void disturb(double worldX, double worldZ, float y, float vx, float vz, float impact, float radius,
        float stroke) {
        float x0 = (float) (worldX - originX), z0 = (float) (worldZ - originZ);
        float speed = (float) Math.sqrt(vx * vx + vz * vz);
        float drive = Math.min(speed / 3, 1.5F);
        float extent = radius * 3;
        int minX = Math.max(1, (int) ((x0 - extent) * samplesPerBlock));
        int maxX = Math.min(size - 2, (int) ((x0 + extent) * samplesPerBlock));
        int minZ = Math.max(1, (int) ((z0 - extent) * samplesPerBlock));
        int maxZ = Math.min(size - 2, (int) ((z0 + extent) * samplesPerBlock));
        for (int z = minZ; z <= maxZ; z++) {
            for (int x = minX; x <= maxX; x++) {
                int i = z * size + x;
                if (surface[i] < 0 || Math.abs(surface[i] - y) > 0.25F) continue;
                float dx = (x + 0.5F) / samplesPerBlock - x0;
                float dz = (z + 0.5F) / samplesPerBlock - z0;
                float d = (float) Math.sqrt(dx * dx + dz * dz) / radius;
                float dip = (float) Math.exp(-d * d * 2);
                float rim = (float) Math.exp(-(d - 1.25F) * (d - 1.25F) * 9);
                float front = speed < 0.01F ? 0 : (dx * vx + dz * vz) / (speed * (radius * d + 0.01F));
                // Balance the integrated rim against the depression. A net positive input
                // builds a broad mound instead of retaining contrast in the departing crests.
                float impulse = 0.0030F * drive * (rim * 0.34F * (1.0F + 0.4F * front) - dip)
                    + 0.0030F * stroke * (rim * 0.34F - dip)
                    - 0.003F * Math.min(impact, 8) * dip;
                // Velocity impulse: resolution-independent acceleration at a fixed simulation step.
                previous[i] -= impulse;
                energy = Math.max(energy, Math.abs(impulse));
            }
        }
    }

    public void step() {
        float c2 = WAVE_SPEED * STEP * samplesPerBlock;
        c2 *= c2;
        float max = 0;
        Arrays.fill(next, 0);
        for (int z = 1; z < size - 1; z++) {
            for (int x = 1; x < size - 1; x++) {
                int i = z * size + x;
                if (surface[i] < 0) continue;
                float h = height[i];
                float sum = 0.5F
                    * (neighbor(i, i - 1) + neighbor(i, i + 1) + neighbor(i, i - size) + neighbor(i, i + size))
                    + 0.25F * (neighbor(i, i - size - 1) + neighbor(i, i - size + 1)
                        + neighbor(i, i + size - 1)
                        + neighbor(i, i + size + 1));
                // A nine-point Laplacian avoids strongly square wavefronts. Damping acts on velocity.
                float value = h + (h - previous[i]) * 0.995F + c2 * (sum - 3 * h);
                int edge = Math.min(Math.min(x, z), Math.min(size - 1 - x, size - 1 - z));
                float absorb = Math.min(1, edge / (2.0F * samplesPerBlock));
                value *= 0.90F + 0.10F * absorb;
                next[i] = Math.max(-0.3F, Math.min(0.3F, value)) * HEIGHT_DECAY;
                max = Math.max(max, Math.abs(next[i]) + Math.abs(next[i] - h));
            }
        }
        float[] spare = previous;
        previous = height;
        // Decay both time levels equally so settled depressions cannot leave a permanent offset.
        for (int i = 0; i < previous.length; i++) previous[i] *= HEIGHT_DECAY;
        height = next;
        next = spare;
        energy = max;
    }

    private float neighbor(int center, int neighbor) {
        // Absorb at banks and level changes instead of carrying waves across dry blocks or stacked pools.
        return surface[neighbor] >= 0 && Math.abs(surface[neighbor] - surface[center]) < 0.25F ? height[neighbor] : 0;
    }

    public boolean isActive() {
        return energy > 0.00002F;
    }

    public float heightAt(int x, int z) {
        return height[z * size + x];
    }

    public void writeTexture(FloatBuffer data) {
        data.clear();
        for (int i = 0; i < height.length; i++) {
            data.put(height[i])
                .put(tickPrevious[i])
                .put(Math.max(0, surface[i]))
                .put(surface[i] < 0 ? 0 : 1);
        }
        data.flip();
    }
}
