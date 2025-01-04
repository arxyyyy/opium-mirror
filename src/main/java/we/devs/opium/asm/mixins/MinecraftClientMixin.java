package we.devs.opium.asm.mixins;

import we.devs.opium.Opium;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MinecraftClient.class)
public class MinecraftClientMixin {

    @Redirect(method = "handleBlockBreaking", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerEntity;isUsingItem()Z"))
    public boolean injectHandleBlockBreaking(ClientPlayerEntity clientPlayerEntity) {
        return !Opium.MODULE_MANAGER.isModuleEnabled("MultiTask") && clientPlayerEntity.isUsingItem();
    }

    @Redirect(method = "doItemUse", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerInteractionManager;isBreakingBlock()Z"))
    public boolean injectDoItemUse(ClientPlayerInteractionManager clientPlayerInteractionManager) {
        return !Opium.MODULE_MANAGER.isModuleEnabled("MultiTask") && clientPlayerInteractionManager.isBreakingBlock();
    }
}
