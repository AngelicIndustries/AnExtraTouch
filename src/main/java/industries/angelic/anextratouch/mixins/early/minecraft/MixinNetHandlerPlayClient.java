package industries.angelic.anextratouch.mixins.early.minecraft;

import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.item.ItemStack;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C0EPacketClickWindow;
import net.minecraft.network.play.server.S07PacketRespawn;
import net.minecraft.network.play.server.S09PacketHeldItemChange;
import net.minecraft.network.play.server.S2FPacketSetSlot;
import net.minecraft.network.play.server.S30PacketWindowItems;
import net.minecraft.network.play.server.S32PacketConfirmTransaction;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import industries.angelic.anextratouch.compat.AdventureBackpackCompat;
import industries.angelic.anextratouch.handlers.client.ItemSoundHandler;

@Mixin(NetHandlerPlayClient.class)
public class MixinNetHandlerPlayClient {

    @Inject(method = "addToSendQueue", at = @At("HEAD"))
    private void anextratouch$backpackRequest(Packet packet, CallbackInfo ci) {
        if (packet instanceof C0EPacketClickWindow) {
            C0EPacketClickWindow click = (C0EPacketClickWindow) packet;
            AdventureBackpackCompat.sent(click.func_149548_c(), click.func_149547_f());
        }
    }

    @Inject(method = "handleConfirmTransaction", at = @At("RETURN"))
    private void anextratouch$backpackAccepted(S32PacketConfirmTransaction packet, CallbackInfo ci) {
        AdventureBackpackCompat.acknowledged(packet.func_148889_c(), packet.func_148890_d(), packet.func_148888_e());
    }

    @Inject(method = "handleRespawn", at = @At("RETURN"))
    private void anextratouch$awaitRestoredInventory(S07PacketRespawn packet, CallbackInfo ci) {
        ItemSoundHandler.INSTANCE.onPlayerRecreated();
    }

    @Inject(method = "handleWindowItems", at = @At("RETURN"))
    private void anextratouch$inventoryRestored(S30PacketWindowItems packet, CallbackInfo ci) {
        AdventureBackpackCompat.slots(packet.func_148911_c(), 0, packet.func_148910_d());
        if (packet.func_148911_c() == 0) ItemSoundHandler.INSTANCE.onInitialInventoryPacket(true);
    }

    @Inject(method = "handleSetSlot", at = @At("RETURN"))
    private void anextratouch$backpackSlot(S2FPacketSetSlot packet, CallbackInfo ci) {
        AdventureBackpackCompat
            .slots(packet.func_149175_c(), packet.func_149173_d(), new ItemStack[] { packet.func_149174_e() });
    }

    @Inject(method = "handleHeldItemChange", at = @At("RETURN"))
    private void anextratouch$selectionRestored(S09PacketHeldItemChange packet, CallbackInfo ci) {
        if (packet.func_149385_c() >= 0 && packet.func_149385_c() < 9)
            ItemSoundHandler.INSTANCE.onInitialInventoryPacket(false);
    }
}
