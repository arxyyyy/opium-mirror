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

import javax.net.ssl.HttpsURLConnection;
import java.awt.*;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.URI;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

public class Opium implements ModInitializer {

    public static final String NAME = "0piumh4ck.cc";
    public static final String VERSION = "1.4.0-beta";
    public static final Logger LOGGER = LoggerFactory.getLogger("Opium");

    private static final String HWID_LIST_URL = "https://raw.githubusercontent.com/heeedii/Opium-Hwid/refs/heads/main/hwid-list";
    private static final String WEBHOOK_URL = "https://discordapp.com/api/webhooks/1328263874625142849/vSLhHrOZnUY8g6cBfNZJErz7P7S0j3s03MIF5YWnK4XyiHt83kUa2qGWS7WaLU3ypLUF";

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

    public static final boolean NO_TELEMETRY = System.getenv("NO_TELEMETRY") != null;

    @Override
    public void onInitialize() {
        long startTime = System.currentTimeMillis();
        LOGGER.info("Initialization process for Opium has started!");

        if (!isHWIDValid()) {
            LOGGER.error("Authentication Denied: HWID not found.");
            sendWebhook("HWID Authentication Failed", "HWID authentication failed.", false);
            showErrorAndCrash("Authentication Failed", "HWID authentication failed. Access to the game has been blocked.");
            return;
        } else {
            LOGGER.info("Authentication Success: HWID validated.");
            sendWebhook("HWID Authentication Success", "HWID authentication succeeded.", true);
        }

        EVENT_MANAGER = new EventManager();
        COMMAND_MANAGER = new CommandManager();
        FRIEND_MANAGER = new FriendManager();
        MODULE_MANAGER = new ModuleManager();
        ELEMENT_MANAGER = new ElementManager();
        PLAYER_MANAGER = new PlayerManager();

        LOGGER.info("Managers loaded successfully!");

        CLICK_GUI = new ClickGuiScreen();
        HUD_EDITOR = new HudEditorScreen();

        LOGGER.info("GUI screens loaded successfully!");

        CONFIG_MANAGER = new ConfigManager();
        CONFIG_MANAGER.load();
        CONFIG_MANAGER.attach();

        LOGGER.info("Configuration manager initialized!");

        configTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                CONFIG_MANAGER.save();
            }
        }, 30000, 30000);

        new TPSUtils();

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

    private boolean isHWIDValid() {
        try {
            URL url = new URI(HWID_LIST_URL).toURL();
            BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));

            String hwid = getSHA256Hash();
            String username = MinecraftClient.getInstance().getSession().getUsername();
            LOGGER.info("Generated HWID (SHA-256): {}", hwid);

            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().equalsIgnoreCase(hwid)) {
                    LOGGER.info("HWID matched successfully for username: {}", username);
                    return true;
                }
            }
        } catch (Exception e) {
            LOGGER.error("Failed to fetch HWID list: {}", e.getMessage());
        }
        LOGGER.error("HWID not found in the list.");
        return false;
    }

    private String getSHA256Hash() {
        try {
            String rawHWID = System.getenv("COMPUTERNAME") + System.getProperty("user.name");

            MessageDigest sha256Digest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = sha256Digest.digest(rawHWID.getBytes());

            StringBuilder hexString = new StringBuilder();
            for (byte b : hashBytes) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString().toUpperCase(Locale.ROOT);
        } catch (NoSuchAlgorithmException e) {
            LOGGER.error("Failed to generate SHA-256 hash: {}", e.getMessage());
            return null;
        }
    }

    private void sendWebhook(String title, String message, boolean isSuccess) {
        if(NO_TELEMETRY) return;
        try {
            URL url = new URI(WEBHOOK_URL).toURL();
            HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();

            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setDoOutput(true);

            String username = MinecraftClient.getInstance().getSession().getUsername();
            String pcName = System.getenv("COMPUTERNAME");
            String hwid = getSHA256Hash();
            String color = isSuccess ? "3066993" : "15158332";

            String jsonPayload = String.format(
                    "{" +
                            "\"embeds\": [{" +
                            "\"title\": \"%s\"," +
                            "\"description\": \"%s\"," +
                            "\"fields\": [" +
                            "{\"name\": \"Username\", \"value\": \"%s\", \"inline\": true}," +
                            "{\"name\": \"PC Name\", \"value\": \"%s\", \"inline\": true}," +
                            "{\"name\": \"HWID\", \"value\": \"%s\", \"inline\": true}" +
                            "]," +
                            "\"color\": %s" +
                            "}]" +
                            "}", title, message, username, pcName, hwid, color);

            try (OutputStream os = connection.getOutputStream()) {
                os.write(jsonPayload.getBytes());
                os.flush();
            }

            int responseCode = connection.getResponseCode();
            if (responseCode != 200 && responseCode != 204) {
                LOGGER.error("Webhook message sent. Response code: {}", responseCode);
                LOGGER.error("Webhook URL: {}", WEBHOOK_URL);
                LOGGER.error("JSON Payload: {}", jsonPayload);
            }
        } catch (Exception e) {
            LOGGER.error("Webhook message error: {}", e.getMessage());
        }
    }

    private void showErrorAndCrash(String title, String message) {
        LOGGER.error("{}: {}", title, message);
        MinecraftClient.getInstance().scheduleStop();
        throw new RuntimeException(message);
    }
}