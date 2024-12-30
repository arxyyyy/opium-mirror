package org.nrnr.opium.mixin.network;

import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.GameJoinS2CPacket;
import net.minecraft.network.packet.s2c.play.InventoryS2CPacket;
import org.nrnr.opium.Opium;
import org.nrnr.opium.impl.event.gui.chat.ChatMessageEvent;
import org.nrnr.opium.impl.event.network.GameJoinEvent;
import org.nrnr.opium.impl.event.network.InventoryEvent;
import org.nrnr.opium.impl.imixin.IClientPlayNetworkHandler;
import org.nrnr.opium.mixin.accessor.AccessorClientConnection;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * @author chronos
 * @since 1.0
 */
@Mixin(ClientPlayNetworkHandler.class)
public abstract class MixinClientPlayNetworkHandler implements IClientPlayNetworkHandler {
    @Shadow
    public abstract ClientConnection getConnection();

    /**
     * @param content
     * @param ci
     */
    @Inject(method = "sendChatMessage", at = @At(value = "HEAD"),
            cancellable = true)
    private void hookSendChatMessage(String content, CallbackInfo ci) {
        ChatMessageEvent.Server chatInputEvent =
                new ChatMessageEvent.Server(content);
        Opium.EVENT_HANDLER.dispatch(chatInputEvent);
        // prevent chat packet from sending
        if (chatInputEvent.isCanceled()) {
            ci.cancel();
        }
    }

    /**
     * @param packet
     * @param ci
     */
    @Inject(method = "onGameJoin", at = @At(value = "TAIL"))
    private void hookOnGameJoin(GameJoinS2CPacket packet, CallbackInfo ci) {
        GameJoinEvent gameJoinEvent = new GameJoinEvent();
        Opium.EVENT_HANDLER.dispatch(gameJoinEvent);
    }

    /**
     * @param packet
     * @param ci
     */
    @Inject(method = "onInventory", at = @At(value = "TAIL"))
    private void hookOnInventory(InventoryS2CPacket packet, CallbackInfo ci) {
        InventoryEvent inventoryEvent = new InventoryEvent(packet);
        Opium.EVENT_HANDLER.dispatch(inventoryEvent);
    }

    @Override
    public void sendQuietPacket(Packet<?> packet) {
        ((AccessorClientConnection) getConnection()).hookSendInternal(packet, null, true);
    }
}
