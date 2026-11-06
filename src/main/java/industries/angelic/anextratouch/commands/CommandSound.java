package industries.angelic.anextratouch.commands;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ResourceLocation;

import industries.angelic.anextratouch.AnExtraTouch;
import industries.angelic.anextratouch.handlers.client.extrasounds.SoundInspector;
import industries.angelic.anextratouch.mixins.early.minecraft.AccessorExtraSoundHandler;

/** Client command: shows what a sound event really plays, or traces sounds as they play. */
public class CommandSound extends CommandBase {

    @Override
    public String getCommandName() {
        return "aet_sound";
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "/aet_sound <event> | trace <text|all|off>";
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 0;
    }

    @Override
    public boolean canCommandSenderUseCommand(ICommandSender sender) {
        return true;
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) {
        if (args.length == 0) throw new WrongUsageException(getCommandUsage(sender));
        if ("trace".equals(args[0])) {
            String filter = args.length < 2 ? null : args[1];
            if (filter == null) {
                String current = SoundInspector.traceFilter();
                say(
                    sender,
                    current == null ? "Sound trace is off."
                        : current.isEmpty() ? "Tracing every sound to the log." : "Tracing sounds matching " + current);
            } else if ("off".equals(filter)) {
                SoundInspector.trace(null);
                say(sender, "Sound trace off.");
            } else if ("all".equals(filter)) {
                SoundInspector.trace("");
                say(sender, "Tracing every sound to the log (Sound trace: ...).");
            } else {
                SoundInspector.trace(filter);
                say(sender, "Tracing sounds whose event or file contains " + filter + ", in chat and the log.");
            }
            return;
        }
        for (String line : SoundInspector.describe(new ResourceLocation(args[0]))) {
            say(sender, line);
            AnExtraTouch.LOG.info("Sound: {}", line.replaceAll("§.", ""));
        }
    }

    @Override
    public List<String> addTabCompletionOptions(ICommandSender sender, String[] args) {
        if (args.length == 2 && "trace".equals(args[0])) return getListOfStringsMatchingLastWord(args, "off", "all");
        if (args.length != 1) return null;
        List<String> names = new ArrayList<>();
        names.add("trace");
        for (Object key : ((AccessorExtraSoundHandler) Minecraft.getMinecraft()
            .getSoundHandler()).anextratouch$soundRegistry()
                .getKeys())
            names.add(key.toString());
        return getListOfStringsMatchingLastWord(args, names.toArray(new String[0]));
    }

    private static void say(ICommandSender sender, String line) {
        sender.addChatMessage(new ChatComponentText(line));
    }
}
