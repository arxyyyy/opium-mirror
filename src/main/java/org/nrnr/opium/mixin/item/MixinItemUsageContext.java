package org.nrnr.opium.mixin.item;

import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import org.nrnr.opium.init.Managers;
import org.nrnr.opium.util.Globals;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ItemUsageContext.class)
public final class MixinItemUsageContext implements Globals {
    @Inject(method = "getStack", at = @At("RETURN"), cancellable = true)
    public void hookGetStack(final CallbackInfoReturnable<ItemStack> info) {
        if (mc.player != null && info.getReturnValue().equals(mc.player.getMainHandStack()) && Managers.INVENTORY.isDesynced()) {
            info.setReturnValue(Managers.INVENTORY.getServerItem());
        }
    }
}
