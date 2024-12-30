package org.nrnr.opium.impl.command;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.command.CommandSource;
import org.nrnr.opium.api.command.Command;
import org.nrnr.opium.api.module.Module;
import org.nrnr.opium.api.module.ToggleModule;
import org.nrnr.opium.init.Managers;
import org.nrnr.opium.util.chat.ChatUtil;

public class HideAllCommand extends Command {

    public HideAllCommand() {
        super("HideAll", "Hides all modules from the arraylist", literal("hideall"));
    }

    @Override
    public void buildCommand(LiteralArgumentBuilder<CommandSource> builder) {
        builder.executes(c -> {
            for (Module module : Managers.MODULE.getModules()) {
                if (module instanceof ToggleModule toggleModule && !toggleModule.isHidden()) {
                    toggleModule.setHidden(true);
                }
            }
            ChatUtil.clientSendMessage("All modules are hidden");
            return 1;
        });
    }
}
