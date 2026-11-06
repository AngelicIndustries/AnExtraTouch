package industries.angelic.anextratouch.mixins.early.minecraft;

import java.util.List;

import net.minecraft.client.resources.FallbackResourceManager;
import net.minecraft.client.resources.IResourcePack;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(FallbackResourceManager.class)
public interface AccessorFallbackResources {

    /** Lowest priority first; the last pack that has a file provides it. */
    @Accessor("resourcePacks")
    List<IResourcePack> anextratouch$resourcePacks();
}
