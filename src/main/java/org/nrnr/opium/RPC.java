package org.nrnr.opium;

import org.nrnr.opium.api.config.Config;
import org.nrnr.opium.api.config.setting.BooleanConfig;
import org.nrnr.opium.api.discord.DiscordEventHandlers;
import org.nrnr.opium.api.discord.DiscordRPC;
import org.nrnr.opium.api.discord.DiscordRichPresence;

import static org.nrnr.opium.util.Globals.mc;


public class RPC {
    public static String discordID = "1259952133025959976";
    public static org.nrnr.opium.api.discord.DiscordRichPresence discordRichPresence = new DiscordRichPresence();
    public static DiscordRPC discordRPC = DiscordRPC.INSTANCE;
    public static Config<Boolean> showIP = new BooleanConfig("ShowIP","sso", true);

    public static void startRPC() {

        DiscordEventHandlers eventHandlers = new DiscordEventHandlers();
        eventHandlers.disconnected = RPC::lambda$startRPC$0;
        discordRPC.Discord_Initialize(discordID, eventHandlers, true, null);
        RPC.discordRichPresence.startTimestamp = System.currentTimeMillis() / ((long)-2121370231 ^ 0xFFFFFFFF818E7661L);
        RPC.discordRichPresence.details = mc.getSession().getUsername() + " | UID: " + "0" ;
        RPC.discordRichPresence.largeImageKey = "neverdiesicon2";
        //RPC.discordRichPresence.largeImageKey = mc.player.networkHandler.getServerInfo().address;
        RPC.discordRichPresence.largeImageText = OpiumMod.MOD_VER;
        //RPC.discordRichPresence.state = "by Chronos + Mortex" ;
        discordRPC.Discord_UpdatePresence(discordRichPresence);
    }
    public static void stopRPC() {
        discordRPC.Discord_Shutdown();
        discordRPC.Discord_ClearPresence();
    }

    public static void lambda$startRPC$0(final int var1, final String var2) {
        System.out.println("Discord RPC disconnected, var1: " + var1 + ", var2: " + var2);
    }



}