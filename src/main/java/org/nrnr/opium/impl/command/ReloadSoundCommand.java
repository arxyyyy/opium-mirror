package org.nrnr.opium.impl.command;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.command.CommandSource;
import org.nrnr.opium.api.command.Command;
import org.nrnr.opium.util.chat.ChatUtil;

public class ReloadSoundCommand extends Command {
    public ReloadSoundCommand() {
        super("ReloadSound", "Reloads the Minecraft sound system", literal("reloadsound"));
    }

    @Override
    public void buildCommand(LiteralArgumentBuilder<CommandSource> builder) {
        builder.executes(context -> {
            mc.getSoundManager().reloadSounds();
            ChatUtil.clientSendMessage("Reloaded the SoundSystem");
            return 1;
        });
    }
}
