package org.nrnr.opium.impl.command;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.command.CommandSource;
import net.minecraft.util.Formatting;
import org.nrnr.opium.api.command.Command;
import org.nrnr.opium.api.module.Module;
import org.nrnr.opium.api.module.ToggleModule;
import org.nrnr.opium.init.Managers;
import org.nrnr.opium.util.chat.ChatUtil;

public class ModulesCommand extends Command {
    public ModulesCommand() {
        super("Modules", "Displays all client modules", literal("modules"));
    }

    @Override
    public void buildCommand(LiteralArgumentBuilder<CommandSource> builder) {
        builder.executes(c -> {
            StringBuilder modulesList = new StringBuilder();
            for (Module module : Managers.MODULE.getModules()) {
                String formatting = module instanceof ToggleModule t && t.isEnabled() ? "§s" : "§f";
                modulesList.append(formatting);
                modulesList.append(module.getName());
                modulesList.append(Formatting.RESET);
                // LOL

            }
            ChatUtil.clientSendMessageRaw(" §7Modules:§f " + modulesList);
            return 1;
        });
    }
}
