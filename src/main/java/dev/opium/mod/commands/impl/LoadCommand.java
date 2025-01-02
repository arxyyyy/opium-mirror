package dev.opium.mod.commands.impl;

import dev.opium.Opium;
import dev.opium.core.Manager;
import dev.opium.core.impl.CommandManager;
import dev.opium.core.impl.ConfigManager;
import dev.opium.mod.commands.Command;

import java.util.List;

public class LoadCommand extends Command {

	public LoadCommand() {
		super("load", "[config]");
	}

	@Override
	public void runCommand(String[] parameters) {
		if (parameters.length == 0) {
			sendUsage();
			return;
		}
		CommandManager.sendChatMessage("§fLoading..");
		ConfigManager.options = Manager.getFile(parameters[0] + ".cfg");
		Opium.CONFIG = new ConfigManager();
		Opium.PREFIX = Opium.CONFIG.getString("prefix", Opium.PREFIX);
		Opium.CONFIG.loadSettings();
        ConfigManager.options = Manager.getFile("options.txt");
		Opium.save();
	}

	@Override
	public String[] getAutocorrect(int count, List<String> seperated) {
		return null;
	}
}
