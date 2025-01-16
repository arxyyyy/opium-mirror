package we.devs.opium.asm.mixins;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.PlayerListHud;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.text.Style;
import net.minecraft.util.Formatting;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import we.devs.opium.api.manager.miscellaneous.UUIDManager;

import java.util.Collection;
import java.util.Objects;

@Mixin(PlayerListHud.class)
public class PlayerListHudMixin {
    @Inject(method = "render", at = @At("HEAD"))
    private void addCustomIconOrText(CallbackInfo ci) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client == null || client.player == null || client.world == null) return;

        // Get the player list
        Collection<PlayerListEntry> playerListEntries = Objects.requireNonNull(client.getNetworkHandler()).getPlayerList();

        // Iterate through the entries
        for (PlayerListEntry entry : playerListEntries) {
            // Check if the player's UUID is in the UUIDManager
            if (UUIDManager.isAdded(entry.getProfile().getId())) {
                // Create the player's name text
                MutableText playerName = Text.literal(entry.getProfile().getName());

                // Create the custom tag with color (only the tag is colored)
                Text customTag = Text.literal(" [Opium]").setStyle(Style.EMPTY.withColor(Formatting.DARK_GRAY).withBold(true));

                // Combine the name and the custom colored tag
                Text fullText = playerName.append(customTag);

                // Set the player's display name with the custom colored tag
                entry.setDisplayName(fullText);
            }
        }
    }
}
