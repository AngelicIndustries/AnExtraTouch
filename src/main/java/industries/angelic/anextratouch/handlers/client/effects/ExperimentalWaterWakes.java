package industries.angelic.anextratouch.handlers.client.effects;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.util.WeakHashMap;

import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityBoat;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import org.lwjgl.opengl.GL30;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import industries.angelic.anextratouch.Config;
import industries.angelic.anextratouch.compat.EtFuturumBoatCompat;
import industries.angelic.anextratouch.compat.ThaumicHorizonsBoatCompat;
import industries.angelic.anextratouch.handlers.client.effects.water.RippleField;
import industries.angelic.anextratouch.util.SwimBobTracker;

/** Owns the simulation and upload, without linking Angelica on clients that do not have it. */
@SideOnly(Side.CLIENT)
public final class ExperimentalWaterWakes {

    public static final ExperimentalWaterWakes INSTANCE = new ExperimentalWaterWakes();
    private final WeakHashMap<Entity, Motion> motions = new WeakHashMap<Entity, Motion>();
    private World world;
    private RippleField field;
    private FloatBuffer pixels;
    private ByteBuffer upload;
    private int texture;
    private int textureSize;
    private int quality = -1;
    private int scan;
    private boolean dirty;
    private boolean supported;
    private float partialTicks;

    private ExperimentalWaterWakes() {}

    public static boolean isActive() {
        return Config.experimentalWaterWakes && INSTANCE.supported && AngelicaShaderHelper.isShaderPackInUse();
    }

    public void setSupported(boolean value) {
        supported = value;
        if (!value) {
            reset();
            if (texture != 0) GL11.glDeleteTextures(texture);
            texture = textureSize = 0;
        }
    }

    @SubscribeEvent
    public void tick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        Minecraft mc = Minecraft.getMinecraft();
        if (!isActive() || mc.theWorld == null || mc.renderViewEntity == null) {
            reset();
            return;
        }
        if (mc.isGamePaused()) return;
        Entity viewer = mc.renderViewEntity;
        int x = MathHelper.floor_double(viewer.posX) - RippleField.WIDTH / 2;
        int z = MathHelper.floor_double(viewer.posZ) - RippleField.WIDTH / 2;
        boolean fresh = world != mc.theWorld || field == null || quality != Config.experimentalWaterWakeQuality;
        if (fresh) {
            reset();
            world = mc.theWorld;
            quality = Config.experimentalWaterWakeQuality;
            field = new RippleField(quality, x, z);
            upload = BufferUtils.createByteBuffer(field.size * field.size * 16);
            pixels = upload.asFloatBuffer();
        }
        field.moveTo(x, z);
        // A bounded rolling refresh also detects edited banks. Never requests unloaded chunks.
        int count = fresh ? RippleField.WIDTH * RippleField.WIDTH : 128;
        for (int i = 0; i < count; i++) {
            int column = scan++ & (RippleField.WIDTH * RippleField.WIDTH - 1);
            refreshColumn(x + column % RippleField.WIDTH, z + column / RippleField.WIDTH, (int) viewer.posY);
        }
        boolean wasActive = field.isActive();
        field.beginTick();
        // Local player first, then at most 31 nearby emitters. Work stays bounded in mob farms.
        int budget = 32;
        if (mc.thePlayer != null && sample(mc.thePlayer)) budget--;
        for (Object object : world.loadedEntityList) {
            if (budget == 0) break;
            if (object instanceof Entity && object != mc.thePlayer && sample((Entity) object)) budget--;
        }
        if (field.isActive()) {
            for (int i = 0; i < 3; i++) field.step();
        }
        dirty |= fresh || wasActive || field.isActive();
    }

    private void refreshColumn(int x, int z, int nearY) {
        float y = -1;
        if (world.blockExists(x, Math.max(0, Math.min(255, nearY)), z)) {
            float old = field.waterAt(x, z);
            if (old >= 0) y = surface(x, (int) old, z);
            if (y < 0) {
                for (int d = 0; d <= 8 && y < 0; d++) {
                    y = surface(x, nearY - d, z);
                    if (y < 0 && d > 0) y = surface(x, nearY + d, z);
                }
            }
        }
        field.setWater(x, z, y);
    }

    private float surface(int x, int y, int z) {
        if (y < 0 || y > 254 || !world.blockExists(x, y, z)) return -1;
        if (world.getBlock(x, y, z)
            .getMaterial() != Material.water || world.getBlock(x, y + 1, z)
                .getMaterial()
                .isLiquid())
            return -1;
        // Flowing falls need a different surface parameterization. Keep their existing rendering.
        if (world.getBlockMetadata(x, y, z) != 0) return -1;
        return (float) WetnessFluidHelper.getWettableFluidSurfaceY(world, x, y, z);
    }

    private boolean sample(Entity entity) {
        boolean boat = entity instanceof EntityBoat || EtFuturumBoatCompat.isBoat(entity)
            || ThaumicHorizonsBoatCompat.isBoat(entity);
        if (entity.isDead || entity.ridingEntity != null
            || !(boat || entity instanceof EntityLivingBase || entity instanceof EntityItem)) return false;
        int x = MathHelper.floor_double(entity.posX), z = MathHelper.floor_double(entity.posZ);
        if (x < field.originX + 2 || x >= field.originX + RippleField.WIDTH - 2
            || z < field.originZ + 2
            || z >= field.originZ + RippleField.WIDTH - 2) return false;
        Motion motion = motions.get(entity);
        if (motion == null) {
            motion = new Motion(entity);
            motions.put(entity, motion);
        }
        double dx = entity.posX - motion.x, dz = entity.posZ - motion.z;
        double dy = entity.boundingBox.minY - motion.y;
        float y = -1;
        for (int by = (int) Math.floor(entity.boundingBox.minY - 0.5); by
            <= Math.min(255, Math.ceil(entity.boundingBox.maxY)); by++) {
            y = surface(x, by, z);
            if (y >= 0) break;
        }
        boolean contact = y >= 0 && entity.boundingBox.maxY > y && entity.boundingBox.minY < y + 0.4;
        if (contact && dx * dx + dz * dz + dy * dy < 16) {
            // Populate the contact neighborhood immediately, independently of the rolling bank scan.
            for (int oz = -2; oz <= 2; oz++) {
                for (int ox = -2; ox <= 2; ox++) refreshColumn(x + ox, z + oz, (int) y);
            }
            float impact = !motion.contact ? (float) Math.max(0, -dy * 20 - 1) : 0;
            float radius = boat ? 0.65F : entity instanceof EntityItem ? 0.18F : 0.4F;
            boolean swimming = !boat && entity instanceof EntityLivingBase
                && !entity.onGround
                && entity.boundingBox.minY < y
                && !(entity instanceof EntityPlayer && ((EntityPlayer) entity).capabilities.isFlying);
            if (swimming && motion.contact) {
                field.swimStroke(entity.posX, entity.posZ, y, motion.swimBob.update(dy), radius);
            } else {
                motion.swimBob.reset();
            }
            // Three positions bridge each game tick. Teleports and initial tracking never draw a trail.
            for (int i = 1; i <= 3; i++) {
                double fraction = i / 3.0;
                field.disturb(
                    motion.x + dx * fraction,
                    motion.z + dz * fraction,
                    y,
                    (float) dx * 20,
                    (float) dz * 20,
                    impact / 3,
                    radius);
            }
        } else {
            motion.swimBob.reset();
        }
        motion.x = entity.posX;
        motion.z = entity.posZ;
        motion.y = entity.boundingBox.minY;
        motion.contact = contact;
        return contact;
    }

    @SubscribeEvent
    public void renderTick(TickEvent.RenderTickEvent event) {
        if (event.phase != TickEvent.Phase.START) return;
        if (!Minecraft.getMinecraft()
            .isGamePaused()) partialTicks = event.renderTickTime;
        if (!isActive() || Minecraft.getMinecraft().theWorld != world) reset();
    }

    private void reset() {
        if (world == null && field == null) return;
        world = null;
        field = null;
        pixels = null;
        upload = null;
        motions.clear();
        dirty = false;
        scan = 0;
        if (texture != 0) GL11.glDeleteTextures(texture);
        texture = textureSize = 0;
    }

    public RippleField field() {
        return field;
    }

    public float interpolation() {
        return partialTicks;
    }

    /** Called by Angelica's sampler supplier; restore the texture binding it inherited. */
    public int texture() {
        int size = field == null ? 1 : field.size;
        if (texture != 0 && !dirty && textureSize == size) return texture;
        int binding = GL11.glGetInteger(GL11.GL_TEXTURE_BINDING_2D);
        try {
            if (texture == 0) texture = GL11.glGenTextures();
            GL11.glBindTexture(GL11.GL_TEXTURE_2D, texture);
            if (textureSize != size) {
                GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
                GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
                GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL12.GL_CLAMP_TO_EDGE);
                GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL12.GL_CLAMP_TO_EDGE);
                GL11.glTexImage2D(
                    GL11.GL_TEXTURE_2D,
                    0,
                    GL30.GL_RGBA16F,
                    size,
                    size,
                    0,
                    GL11.GL_RGBA,
                    GL11.GL_FLOAT,
                    (FloatBuffer) null);
                textureSize = size;
            }
            ByteBuffer data = upload;
            if (field == null) data = BufferUtils.createByteBuffer(16);
            else field.writeTexture(pixels);
            GL11.glTexSubImage2D(GL11.GL_TEXTURE_2D, 0, 0, 0, size, size, GL11.GL_RGBA, GL11.GL_FLOAT, data);
            dirty = false;
            return texture;
        } finally {
            GL11.glBindTexture(GL11.GL_TEXTURE_2D, binding);
        }
    }

    private static final class Motion {

        double x, y, z;
        boolean contact;
        final SwimBobTracker swimBob = new SwimBobTracker();

        Motion(Entity entity) {
            x = entity.posX;
            y = entity.boundingBox.minY;
            z = entity.posZ;
        }
    }
}
