package org.nrnr.opium.impl.command;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.command.CommandSource;
import org.nrnr.opium.Opium;
import org.nrnr.opium.api.command.Command;
import org.nrnr.opium.util.chat.ChatUtil;

import java.awt.*;
import java.io.IOException;

/**
 * @author chronos
 * @since 1.0
 */
public class OpenFolderCommand extends Command {

    /**
     *
     */
    public OpenFolderCommand() {
        super("OpenFolder", "Opens the client configurations folder", literal("openfolder"));
    }

    @Override
    public void buildCommand(LiteralArgumentBuilder<CommandSource> builder) {
        builder.executes(c -> {
            try {
                Desktop.getDesktop().open(Opium.CONFIG.getClientDirectory().toFile());
            } catch (IOException e) {
                e.printStackTrace();
                ChatUtil.error("Failed to open client folder!");
            }
            return 1;
        });
    }
}
