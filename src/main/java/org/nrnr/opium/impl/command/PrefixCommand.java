package org.nrnr.opium.impl.command;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.command.CommandSource;
import org.nrnr.opium.api.command.Command;
import org.nrnr.opium.init.Managers;
import org.nrnr.opium.util.KeyboardUtil;
import org.nrnr.opium.util.chat.ChatUtil;

/**
 * @author chronos
 * @since 1.0
 */
public class PrefixCommand extends Command {

    public PrefixCommand() {
        super("Prefix", "Allows you to change the chat command prefix", literal("prefix"));
    }

    @Override
    public void buildCommand(LiteralArgumentBuilder<CommandSource> builder) {
        builder.then(argument("prefix", StringArgumentType.string()).executes(c -> {
            final String prefix = StringArgumentType.getString(c, "prefix");
            if (prefix.length() > 1) {
                ChatUtil.error("Prefix can only be one character!");
                return 0;
            }
            int keycode = KeyboardUtil.getKeyCode(prefix);
            Managers.COMMAND.setPrefix(prefix, keycode);
            ChatUtil.clientSendMessage("Command prefix changed to §s" + prefix);
            return 1;
        })).executes(c -> {
            ChatUtil.error("Please provide a new prefix!");
            return 1;
        });
    }
}
