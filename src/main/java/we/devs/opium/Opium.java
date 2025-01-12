package we.devs.opium;

import net.minecraft.client.MinecraftClient;
import we.devs.opium.api.manager.command.CommandManager;
import we.devs.opium.api.manager.element.ElementManager;
import we.devs.opium.api.manager.event.EventListener;
import we.devs.opium.api.manager.event.EventManager;
import we.devs.opium.api.manager.friend.FriendManager;
import we.devs.opium.api.manager.miscellaneous.ConfigManager;
import we.devs.opium.api.manager.miscellaneous.PlayerManager;
import we.devs.opium.api.manager.module.ModuleManager;
import we.devs.opium.api.utilities.TPSUtils;
import we.devs.opium.client.events.EventTick;
import we.devs.opium.client.gui.click.ClickGuiScreen;
import we.devs.opium.client.gui.hud.HudEditorScreen;
import net.fabricmc.api.ModInitializer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import we.devs.opium.api.manager.miscellaneous.FontManager;

import java.awt.*;
import java.util.Timer;
import java.util.TimerTask;

public class Opium implements ModInitializer {
    public static final String NAME = "0piumh4ck.cc";
    public static final String VERSION = "1.3.8-Beta";
    public static final Logger LOGGER = LoggerFactory.getLogger("Opium");

    public static Color COLOR_CLIPBOARD;
    public static CommandManager COMMAND_MANAGER;
    public static EventManager EVENT_MANAGER;
    public static FriendManager FRIEND_MANAGER;
    public static ModuleManager MODULE_MANAGER;
    public static ElementManager ELEMENT_MANAGER;
    public static PlayerManager PLAYER_MANAGER;
    public static ClickGuiScreen CLICK_GUI;
    public static HudEditorScreen HUD_EDITOR;
    public static ConfigManager CONFIG_MANAGER;
    public static FontManager FONT_MANAGER;
    private static final Timer configTimer = new Timer("Config timer", true);

    @Override
    public void onInitialize() {
        long startTime = System.currentTimeMillis();
        LOGGER.info("Initialization process for Opium has started!");

        EVENT_MANAGER = new EventManager();
        COMMAND_MANAGER = new CommandManager();
        FRIEND_MANAGER = new FriendManager();
        MODULE_MANAGER = new ModuleManager();
        ELEMENT_MANAGER = new ElementManager();
        PLAYER_MANAGER = new PlayerManager();
        CLICK_GUI = new ClickGuiScreen();
        HUD_EDITOR = new HudEditorScreen();
        CONFIG_MANAGER = new ConfigManager();
        CONFIG_MANAGER.load();
        CONFIG_MANAGER.attach();
        new TPSUtils();

        configTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                // save config every 30s
                Opium.CONFIG_MANAGER.save();
            }
        }, 30000, 30000);

        //To prevent the font renderer from fucking crashing (make this prettier if you can be bothered)
        EVENT_MANAGER.register(new EventListener() {
            private boolean fontsInitialized = false;

            @Override
            public void onTick(EventTick event) {
                if (!fontsInitialized && MinecraftClient.getInstance().getWindow() != null) {
                    FONT_MANAGER = new FontManager();
                    FONT_MANAGER.registerFonts();
                    fontsInitialized = true;
                    LOGGER.info("FontManager initialized successfully!");
                }
            }
        });



        long endTime = System.currentTimeMillis();
        LOGGER.info("Initialization process for Opium has finished! Took {} ms", endTime - startTime);
    }

}
