package industries.angelic.anextratouch.handlers.client.effects;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.EntityFX;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import industries.angelic.anextratouch.AnExtraTouch;

@SideOnly(Side.CLIENT)
public class ChestBubbleFX extends EntityFX {

    private static final ResourceLocation NEUTRAL_BUBBLE_TEXTURE = new ResourceLocation(
        AnExtraTouch.MODID,
        "textures/particles/bubble.png");

    private final Block sourceBlock;
    private final boolean waterTinted;

    public ChestBubbleFX(World world, double x, double y, double z) {
        this(world, x, y, z, null, 1.0F, 1.0F, 1.0F);
    }

    public ChestBubbleFX(World world, double x, double y, double z, Block sourceBlock, float red, float green,
        float blue) {
        super(world, x, y, z, 0.0D, 0.0D, 0.0D);
        this.sourceBlock = sourceBlock;
        this.waterTinted = sourceBlock != Blocks.ender_chest;
        this.prevPosX = this.posX;
        this.prevPosY = this.posY;
        this.prevPosZ = this.posZ;
        this.noClip = true;
        this.motionX = (double) ((world.rand.nextFloat() * 2.0F - 1.0F) * 0.02F);
        this.motionY = 0.025D + (double) (world.rand.nextFloat() * 0.02F);
        this.motionZ = (double) ((world.rand.nextFloat() * 2.0F - 1.0F) * 0.02F);
        this.particleRed = red;
        this.particleGreen = green;
        this.particleBlue = blue;
        if (this.waterTinted) {
            applyWaterTint();
        }
        this.setSize(0.02F, 0.02F);
        this.particleScale *= this.rand.nextFloat() * 0.6F + 0.2F;
        this.particleMaxAge = 20 + world.rand.nextInt(20);
    }

    @Override
    public void onUpdate() {
        this.prevPosX = this.posX;
        this.prevPosY = this.posY;
        this.prevPosZ = this.posZ;
        this.motionY += 0.002D;
        this.moveEntity(this.motionX, this.motionY, this.motionZ);
        this.motionX *= 0.8500000238418579D;
        this.motionY *= 0.8500000238418579D;
        this.motionZ *= 0.8500000238418579D;

        if (this.waterTinted) {
            applyWaterTint();
        }

        if (!isInWaterOrChest()) {
            this.setDead();
        }

        if (this.particleMaxAge-- <= 0) {
            this.setDead();
        }
    }

    @Override
    public int getFXLayer() {
        return 3;
    }

    @Override
    public void renderParticle(Tessellator tessellator, float partialTicks, float rotationX, float rotationZ,
        float rotationYZ, float rotationXY, float rotationXZ) {
        updateInterpPos(partialTicks);

        float scale = 0.1F * this.particleScale;
        float x = (float) (this.prevPosX + (this.posX - this.prevPosX) * (double) partialTicks - interpPosX);
        float y = (float) (this.prevPosY + (this.posY - this.prevPosY) * (double) partialTicks - interpPosY);
        float z = (float) (this.prevPosZ + (this.posZ - this.prevPosZ) * (double) partialTicks - interpPosZ);

        GL11.glPushAttrib(
            GL11.GL_ENABLE_BIT | GL11.GL_COLOR_BUFFER_BIT
                | GL11.GL_DEPTH_BUFFER_BIT
                | GL11.GL_LIGHTING_BIT
                | GL11.GL_CURRENT_BIT);
        try {
            GL11.glEnable(GL11.GL_BLEND);
            GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
            GL11.glDepthMask(false);
            GL11.glDisable(GL11.GL_LIGHTING);
            GL11.glEnable(GL11.GL_ALPHA_TEST);
            GL11.glAlphaFunc(GL11.GL_GREATER, 0.003921569F);

            Minecraft.getMinecraft()
                .getTextureManager()
                .bindTexture(NEUTRAL_BUBBLE_TEXTURE);
            tessellator.startDrawingQuads();
            tessellator.setBrightness(this.getBrightnessForRender(partialTicks));
            tessellator.setColorRGBA_F(this.particleRed, this.particleGreen, this.particleBlue, this.particleAlpha);
            tessellator.addVertexWithUV(
                (double) (x - rotationX * scale - rotationXY * scale),
                (double) (y - rotationZ * scale),
                (double) (z - rotationYZ * scale - rotationXZ * scale),
                1.0D,
                1.0D);
            tessellator.addVertexWithUV(
                (double) (x - rotationX * scale + rotationXY * scale),
                (double) (y + rotationZ * scale),
                (double) (z - rotationYZ * scale + rotationXZ * scale),
                1.0D,
                0.0D);
            tessellator.addVertexWithUV(
                (double) (x + rotationX * scale + rotationXY * scale),
                (double) (y + rotationZ * scale),
                (double) (z + rotationYZ * scale + rotationXZ * scale),
                0.0D,
                0.0D);
            tessellator.addVertexWithUV(
                (double) (x + rotationX * scale - rotationXY * scale),
                (double) (y - rotationZ * scale),
                (double) (z + rotationYZ * scale - rotationXZ * scale),
                0.0D,
                1.0D);
            tessellator.draw();
        } finally {
            GL11.glDepthMask(true);
            GL11.glPopAttrib();
        }
    }

    private void applyWaterTint() {
        float[] rgb = FallingWaterFX.getWaterColor(this.worldObj, this.posX, this.posY, this.posZ);
        this.setRBGColorF(rgb[0], rgb[1], rgb[2]);
    }

    private static void updateInterpPos(float partialTicks) {
        Entity viewer = Minecraft.getMinecraft().renderViewEntity;
        if (viewer == null) {
            return;
        }

        interpPosX = viewer.lastTickPosX + (viewer.posX - viewer.lastTickPosX) * (double) partialTicks;
        interpPosY = viewer.lastTickPosY + (viewer.posY - viewer.lastTickPosY) * (double) partialTicks;
        interpPosZ = viewer.lastTickPosZ + (viewer.posZ - viewer.lastTickPosZ) * (double) partialTicks;
    }

    private boolean isInWaterOrChest() {
        Block block = this.worldObj.getBlock(
            MathHelper.floor_double(this.posX),
            MathHelper.floor_double(this.posY),
            MathHelper.floor_double(this.posZ));
        return block.getMaterial() == Material.water || block == this.sourceBlock
            || block == Blocks.chest
            || block == Blocks.trapped_chest;
    }
}
