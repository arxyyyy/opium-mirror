package org.nrnr.opium.impl.command;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.command.CommandSource;
import org.nrnr.opium.api.command.Command;
import org.nrnr.opium.api.command.CommandArgumentType;
import org.nrnr.opium.init.Managers;
import org.nrnr.opium.util.chat.ChatUtil;

/**
 * @author chronos
 * @since 1.0
 */
public class HelpCommand extends Command {

    /**
     *
     */
    public HelpCommand() {
        super("Help", "Displays command functionality", literal("help"));
    }

    /**
     * @param command
     * @return
     */
    private static String toHelpMessage(Command command) {
        return String.format("%s %s- %s", command.getName(),
                command.getUsage(), command.getDescription());
    }

    @Override
    public void buildCommand(LiteralArgumentBuilder<CommandSource> builder) {
        builder.then(argument("command", CommandArgumentType.command()).executes(c -> {
            final Command command = CommandArgumentType.getCommand(c, "command");
            ChatUtil.clientSendMessage(toHelpMessage(command));
            return 1;
        })).executes(c -> {
            ChatUtil.clientSendMessageRaw("§s[Commands Help]");
            for (Command c1 : Managers.COMMAND.getCommands()) {
                if (c1 instanceof ModuleCommand) {
                    continue;
                }
                ChatUtil.clientSendMessageRaw(toHelpMessage(c1));
            }
            return 1;
        });
    }
}
