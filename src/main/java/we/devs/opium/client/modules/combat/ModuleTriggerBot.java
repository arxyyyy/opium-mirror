package we.devs.opium.client.modules.combat;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import org.lwjgl.glfw.GLFW;
import we.devs.opium.api.manager.module.Module;
import we.devs.opium.api.manager.module.RegisterModule;
import we.devs.opium.api.utilities.ChatUtils;

@RegisterModule(name = "Triggerbot", description = "Automatically attacks when an entity is in range.", category = Module.Category.COMBAT)
public class ModuleTriggerBot extends Module {
    private final MinecraftClient client = MinecraftClient.getInstance();
    private KeyBinding triggerKey;
    private long lastClickTime = 0;
    private long delay = 200;  // Default delay (in milliseconds)

    public void onInitialize() {
        triggerKey = new KeyBinding(
                "key.opium.triggerbot",
                GLFW.GLFW_KEY_F,             // Default keybinding (F key)
                "category.opium"
        );
        KeyBindingHelper.registerKeyBinding(triggerKey);
        // Register the client tick event to handle key press detection and apply delay logic
        ClientTickEvents.END_CLIENT_TICK.register(client -> onClientTick());
    }
    @Override
    public void onEnable() {
        super.onEnable();
        if (client.player == null || client.world == null) {
            this.disable(false);
            return;
        }
        ChatUtils.sendMessage("Triggerbot Enabled. Automatically attacking entities in range.", "Triggerbot");
    }
    @Override
    public void onDisable() {
        super.onDisable();
        ChatUtils.sendMessage("Triggerbot Disabled.", "Triggerbot");
    }
    private void onClientTick() {
        if (client.player == null || client.world == null) return;
        // Check if enough time has passed since the last attack
        if (System.currentTimeMillis() - lastClickTime > getWeaponDelay()) {
            lastClickTime = System.currentTimeMillis();
            triggerAction();
        }
    }
    private void triggerAction() {
        if (client.targetedEntity != null && client.player.canSee(client.targetedEntity)) {
            client.player.attack(client.targetedEntity);
            ChatUtils.sendMessage("Triggered attack on: " + client.targetedEntity.getName().getString(), "Triggerbot");
        }
    }

    private long getWeaponDelay() {
        if (client.player == null || client.player.getMainHandStack() == null) {
            return delay;
        }
        String itemName = client.player.getMainHandStack().getItem().getTranslationKey();
        if (itemName.contains("sword")) {
            return 600;
        } else if (itemName.contains("axe")) {
            return 1000;
        } else {
            return delay;
        }
    }

    // Method to set the delay (please finish someone)
    public void setDelay(long delay) {
        this.delay = delay;
        ChatUtils.sendMessage("Triggerbot delay set to: " + delay + " ms.", "Triggerbot");
    }
}