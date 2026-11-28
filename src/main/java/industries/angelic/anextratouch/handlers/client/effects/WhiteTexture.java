package industries.angelic.anextratouch.handlers.client.effects;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.util.ResourceLocation;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import industries.angelic.anextratouch.AnExtraTouch;

/**
 * A white texel for colored quads with no texture of their own. Shader mods such as SwanSong sample the bound
 * texture even with GL_TEXTURE_2D off, at whatever coordinates the vertices carry, so plain colored quads came out as
 * atlas noise. Quads bound to this and pointed at its texel give every program just their vertex color.
 */
@SideOnly(Side.CLIENT)
final class WhiteTexture {

    static final double U = 0.5D;
    static final double V = 0.5D;

    private static final ResourceLocation LOCATION = new ResourceLocation(AnExtraTouch.MODID, "white");
    private static DynamicTexture texture;

    private WhiteTexture() {}

    static void bind(Minecraft mc) {
        if (texture == null) {
            texture = new DynamicTexture(1, 1);
            texture.getTextureData()[0] = 0xFFFFFFFF;
            texture.updateDynamicTexture();
            mc.getTextureManager()
                .loadTexture(LOCATION, texture);
        }
        mc.getTextureManager()
            .bindTexture(LOCATION);
    }
}
