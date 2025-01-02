package dev.opium.mod.commands.impl;

import dev.opium.Opium;
import dev.opium.core.Manager;
import dev.opium.core.impl.CommandManager;
import dev.opium.core.impl.ConfigManager;
import dev.opium.mod.commands.Command;

import java.util.List;

public class SaveCommand extends Command {

	public SaveCommand() {
		super("save", "");
	}

	@Override
	public void runCommand(String[] parameters) {
		if (parameters.length == 1) {
			CommandManager.sendChatMessage("§fSaving config named " + parameters[0]);
			ConfigManager.options = Manager.getFile(parameters[0] + ".cfg");
			Opium.save();
			ConfigManager.options = Manager.getFile("options.txt");
		} else {
			CommandManager.sendChatMessage("§fSaving..");
		}
		Opium.save();
	}

	@Override
	public String[] getAutocorrect(int count, List<String> seperated) {
		return null;
	}
}
