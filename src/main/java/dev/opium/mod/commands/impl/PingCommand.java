package dev.opium.mod.commands.impl;

import dev.opium.Opium;
import dev.opium.api.events.eventbus.EventHandler;
import dev.opium.api.events.impl.PacketEvent;
import dev.opium.core.impl.CommandManager;
import dev.opium.mod.commands.Command;
import net.minecraft.network.packet.s2c.play.GameMessageS2CPacket;

import java.util.List;

public class PingCommand extends Command {

	public PingCommand() {
		super("ping", "");
	}

	private long sendTime;

	@Override
	public void runCommand(String[] parameters) {
		sendTime = System.currentTimeMillis();
		mc.getNetworkHandler().sendChatCommand("chat ");
		Opium.EVENT_BUS.subscribe(this);
	}


	@Override
	public String[] getAutocorrect(int count, List<String> seperated) {
		return null;
	}

	@EventHandler
	public void onPacketReceive(PacketEvent.Receive e) {
		if (e.getPacket() instanceof GameMessageS2CPacket packet) {
			if (packet.content().getString().contains("chat.use") || packet.content().getString().contains("命令") || packet.content().getString().contains("Bad command")|| packet.content().getString().contains("No such command") || packet.content().getString().contains("<--[HERE]") || packet.content().getString().contains("Unknown") || packet.content().getString().contains("帮助") || packet.content().getString().contains("执行错误")) {
				CommandManager.sendChatMessage("ping: " + (System.currentTimeMillis() - sendTime) + "ms");
				Opium.EVENT_BUS.unsubscribe(this);
			}
		}
	}
}
