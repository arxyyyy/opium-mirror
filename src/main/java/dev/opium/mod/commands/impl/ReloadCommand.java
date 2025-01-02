package dev.opium.mod.commands.impl;

import dev.opium.Opium;
import dev.opium.core.impl.CommandManager;
import dev.opium.core.impl.ConfigManager;
import dev.opium.mod.commands.Command;

import java.util.List;

public class ReloadCommand extends Command {

	public ReloadCommand() {
		super("reload", "");
	}

	@Override
	public void runCommand(String[] parameters) {
		CommandManager.sendChatMessage("§fReloading..");
		Opium.CONFIG = new ConfigManager();
		Opium.PREFIX = Opium.CONFIG.getString("prefix", Opium.PREFIX);
		Opium.CONFIG.loadSettings();
		Opium.XRAY.read();
		Opium.TRADE.read();
		Opium.FRIEND.read();
	}

	@Override
	public String[] getAutocorrect(int count, List<String> seperated) {
		return null;
	}
}
