package org.nrnr.opium.impl.command;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.command.CommandSource;
import org.nrnr.opium.api.command.Command;
import org.nrnr.opium.api.module.Module;
import org.nrnr.opium.api.module.ToggleModule;
import org.nrnr.opium.init.Managers;
import org.nrnr.opium.util.chat.ChatUtil;

/**
 * @author Neverdies
 * @since 1.0
 */
public class DisableAllCommand extends Command {
    /**
     *
     */
    public DisableAllCommand() {
        super("DisableAll", "Disables all enabled modules", literal("disableall"));
    }

    @Override
    public void buildCommand(LiteralArgumentBuilder<CommandSource> builder) {
        builder.executes(c -> {
            for (Module module : Managers.MODULE.getModules()) {
                if (module instanceof ToggleModule toggleModule && toggleModule.isEnabled()) {
                    toggleModule.disable();
                }
            }
            ChatUtil.clientSendMessage("All modules are disabled");
            return 1;
        });
    }
}
