package industries.angelic.anextratouch.handlers.client.extrasounds;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.client.event.sound.SoundEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;

import cpw.mods.fml.common.eventhandler.Event;
import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import industries.angelic.anextratouch.Config;
import industries.angelic.anextratouch.handlers.client.PlayerInventoryScreens;

/** Forge hooks for screen transitions, chat, potion changes and block interactions. */
public final class ExtraSoundHandler {

    public static final ExtraSoundHandler INSTANCE = new ExtraSoundHandler();
    private EntityPlayer player;
    private GuiScreen screen;
    private boolean screenChanged;
    private final Map<Integer, Boolean> effects = new HashMap<>();

    private ExtraSoundHandler() {}

    @SubscribeEvent
    public void source(SoundEvent.SoundSourceEvent event) {
        // Forge posts this after creating the source and before queuing playback.
        UiSoundSources.register(event.uuid, event.sound);
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void screen(GuiOpenEvent event) {
        screenChanged = true;
    }

    private void screenTick(Minecraft mc) {
        if (mc.theWorld == null || mc.thePlayer == null) {
            screen = null;
            screenChanged = false;
            return;
        }
        if (!screenChanged) return;
        screenChanged = false;
        // Creative inventory redirects through GuiInventory during the same call.
        // Use the final screen so that redirect does not play open, close, open.
        GuiScreen next = mc.currentScreen;
        if (Config.extraInventoryScreens && screen != next && !PlayerInventoryScreens.isPageSwitch(screen, next)) {
            if (screen instanceof GuiContainer) ExtraSounds.ui("inventory.close");
            if (next instanceof GuiContainer) ExtraSounds.ui("inventory.open");
        }
        screen = next;
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void chat(ClientChatReceivedEvent event) {
        Minecraft mc = Minecraft.getMinecraft();
        if (!Config.extraChat || mc.thePlayer == null) return;
        String text = EnumChatFormatting.getTextWithoutFormattingCodes(event.message.getUnformattedText());
        String name = mc.thePlayer.getCommandSenderName();
        boolean own = text.startsWith("<" + name + ">");
        if (event.message instanceof ChatComponentTranslation) {
            ChatComponentTranslation message = (ChatComponentTranslation) event.message;
            if (message.getKey()
                .startsWith("screenshot.")) return;
            Object[] args = message.getFormatArgs();
            if (message.getKey()
                .startsWith("chat.type.") && args.length > 0) {
                String sender = args[0] instanceof IChatComponent ? ((IChatComponent) args[0]).getUnformattedText()
                    : String.valueOf(args[0]);
                own |= name.equals(EnumChatFormatting.getTextWithoutFormattingCodes(sender));
            }
        }
        ExtraSounds.ui(mention(text, name, own) ? "chat.mention" : "chat.message");
    }

    public static boolean mention(String text, String name, boolean own) {
        if (own || name.isEmpty()) return false;
        String line = text.toLowerCase(Locale.ROOT), target = name.toLowerCase(Locale.ROOT);
        int from = 0, at;
        while ((at = line.indexOf(target, from)) >= 0) {
            int end = at + target.length();
            if ((at == 0 || !nameCharacter(line.charAt(at - 1)))
                && (end == line.length() || !nameCharacter(line.charAt(end)))) return true;
            from = end;
        }
        return false;
    }

    private static boolean nameCharacter(char c) {
        return Character.isLetterOrDigit(c) || c == '_';
    }

    @SubscribeEvent
    public void tick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        Minecraft mc = Minecraft.getMinecraft();
        screenTick(mc);
        EntityPlayer current = mc.thePlayer;
        Map<Integer, Boolean> next = new HashMap<>();
        if (current != null) {
            for (Object value : current.getActivePotionEffects()) {
                PotionEffect effect = (PotionEffect) value;
                int id = effect.getPotionID();
                Potion potion = id >= 0 && id < Potion.potionTypes.length ? Potion.potionTypes[id] : null;
                if (potion != null) next.put(id, potion.isBadEffect());
            }
        }
        // Joining, respawning and disconnecting establish a baseline, not an effect transition.
        if (current != null && current == player && Config.extraStatusEffects) {
            for (Map.Entry<Integer, Boolean> effect : next.entrySet()) {
                if (!effects.containsKey(effect.getKey())) effect("add", effect.getValue());
            }
            for (Map.Entry<Integer, Boolean> effect : effects.entrySet()) {
                if (!next.containsKey(effect.getKey())) effect("remove", effect.getValue());
            }
        }
        player = current;
        effects.clear();
        effects.putAll(next);
    }

    private static void effect(String change, boolean harmful) {
        ExtraSounds.ui("effect." + change + (harmful ? ".negative" : ".positive"));
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void interact(PlayerInteractEvent event) {
        Minecraft mc = Minecraft.getMinecraft();
        if (event.action != PlayerInteractEvent.Action.RIGHT_CLICK_BLOCK || event.entityPlayer != mc.thePlayer
            || !event.world.isRemote
            || event.useBlock == Event.Result.DENY) return;
        if (event.entityPlayer.isSneaking() && event.entityPlayer.getHeldItem() != null
            && !event.entityPlayer.getHeldItem()
                .getItem()
                .doesSneakBypassUse(event.world, event.x, event.y, event.z, event.entityPlayer))
            return;
        net.minecraft.block.Block block = event.world.getBlock(event.x, event.y, event.z);
        int meta = event.world.getBlockMetadata(event.x, event.y, event.z);
        String sound = null;
        if (Config.extraRepeaters && (block == Blocks.unpowered_repeater || block == Blocks.powered_repeater)
            && event.entityPlayer.capabilities.allowEdit)
            sound = ((meta >> 2) & 3) == 3 ? "action.repeater.reset" : "action.repeater.add";
        if (Config.extraJukebox && block == Blocks.jukebox && meta > 0) sound = "action.jukebox.eject";
        if (sound != null) ExtraSounds.at(sound, event.x + 0.5, event.y + 0.5, event.z + 0.5, 1, 1);
    }
}
