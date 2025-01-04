package we.devs.opium.client.commands;

import we.devs.opium.Opium;
import we.devs.opium.api.manager.command.Command;
import we.devs.opium.api.manager.command.RegisterCommand;
import we.devs.opium.api.utilities.ChatUtils;

@RegisterCommand(name="config", description="Let's you save or load your configuration without restarting the game.", syntax="config <save|load>", aliases={"configuration", "cfg"})
public class CommandConfig extends Command {
    @Override
    public void onCommand(String[] args) {
        if (args.length == 1) {
            if (args[0].equalsIgnoreCase("load")) {
                Opium.CONFIG_MANAGER.load();
                ChatUtils.sendMessage("Successfully loaded configuration.", "Config");
            } else if (args[0].equalsIgnoreCase("save")) {
                Opium.CONFIG_MANAGER.save();
                ChatUtils.sendMessage("Successfully saved configuration.", "Config");
            } else {
                this.sendSyntax();
            }
        } else {
            this.sendSyntax();
        }
    }
}