package industries.angelic.anextratouch.mixins.early.minecraft;

import java.util.Map;

import net.minecraft.client.resources.SimpleReloadableResourceManager;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(SimpleReloadableResourceManager.class)
public interface AccessorReloadableResources {

    @Accessor("domainResourceManagers")
    Map<String, ?> anextratouch$domainManagers();
}
