package we.devs.opium.client.modules.client;

import net.arikia.dev.drpc.DiscordRPC;
import net.arikia.dev.drpc.DiscordRichPresence;
import net.arikia.dev.drpc.DiscordEventHandlers;
import net.minecraft.text.Text;
import we.devs.opium.api.manager.module.Module;
import we.devs.opium.api.manager.module.RegisterModule;
import we.devs.opium.api.utilities.ChatUtils;

@RegisterModule(name = "Discord RPC", description = "Displayes Opium As Your Dc-RPC", category = Module.Category.CLIENT)
public class ModuleRPC extends Module {

    private boolean isRpcRunning = false;

    @Override
    public void onEnable() {
        super.onEnable();
        startRPC();
    }

    @Override
    public void onDisable() {
        super.onDisable();
        stopRPC();
    }

    @Override
    public void onTick() {
        super.onTick();
        // Optional: Update RPC data dynamically if needed
        if (isRpcRunning) {
            // updateRPC(); // You can update the presence dynamically here if needed
        }
    }

    private void startRPC() {
        if (isRpcRunning) {
            return; // Prevent multiple initializations
        }

        isRpcRunning = true;

        // Create and configure DiscordEventHandlers
        DiscordEventHandlers handlers = new DiscordEventHandlers.Builder()
                .setReadyEventHandler(user -> {
                    assert mc.player != null;
                    ChatUtils.sendMessage("Discord RPC Connected To [" +  user.username + "]");
                })
                .build();

        // Initialize Discord RPC with handlers
        DiscordRPC.discordInitialize("1328117873276752006", handlers, true);

        // Create and set an initial presence
        DiscordRichPresence presence = new DiscordRichPresence.Builder("")
                .setDetails("")
                .setStartTimestamps(System.currentTimeMillis() / 1000L)
                .setBigImage("title", "")
                .setSmallImage("ken", "")
                .build();
        DiscordRPC.discordUpdatePresence(presence);

        // Start a new thread to handle Discord RPC events
        new Thread(() -> {
            while (isRpcRunning) {
                DiscordRPC.discordRunCallbacks();
                try {
                    Thread.sleep(2000); // Sleep for 2 seconds between callbacks
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }, "Discord-RPC-Callback-Thread").start();
    }

    private void stopRPC() {
        if (!isRpcRunning) {
            return; // Prevent stopping if it's not running
        }

        isRpcRunning = false;
        DiscordRPC.discordShutdown();
        
    }


    private void updateRPC() {
        // Example: Update the presence dynamically
        DiscordRichPresence presence = new DiscordRichPresence.Builder("Still Playing Minecraft")
                .setDetails("Modding with Opium Client")
                .setStartTimestamps(System.currentTimeMillis() / 1000L)
                .setBigImage("large_image_key", "Large Image Text")
                .setSmallImage("small_image_key", "Small Image Text")
                .build();
        DiscordRPC.discordUpdatePresence(presence);
    }
}
