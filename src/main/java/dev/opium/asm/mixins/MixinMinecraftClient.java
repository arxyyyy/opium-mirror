package dev.opium.asm.mixins;

import dev.opium.Opium;
import dev.opium.api.events.Event;
import dev.opium.api.events.impl.GameLeftEvent;
import dev.opium.api.events.impl.OpenScreenEvent;
import dev.opium.api.events.impl.TickEvent;
import dev.opium.mod.gui.font.FontRenderers;
import dev.opium.mod.modules.impl.client.ClientSetting;
import dev.opium.mod.modules.impl.player.InteractTweaks;
import net.minecraft.SharedConstants;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.RunArgs;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.server.integrated.IntegratedServer;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.thread.ReentrantThreadExecutor;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.awt.*;

@Mixin(MinecraftClient.class)
public abstract class MixinMinecraftClient extends ReentrantThreadExecutor<Runnable> {
	@Inject(method = "<init>", at = @At("TAIL"))
	void postWindowInit(RunArgs args, CallbackInfo ci) {
		try {
			FontRenderers.createDefault(8f);
			FontRenderers.Calibri = FontRenderers.create("calibri", Font.BOLD, 11f);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Inject(method = "setScreen", at = @At("HEAD"), cancellable = true)
	private void onSetScreen(Screen screen, CallbackInfo info) {
		OpenScreenEvent event = new OpenScreenEvent(screen);
		Opium.EVENT_BUS.post(event);

		if (event.isCancelled()) info.cancel();
	}
	@Inject(method = "disconnect(Lnet/minecraft/client/gui/screen/Screen;)V", at = @At("HEAD"))
	private void onDisconnect(Screen screen, CallbackInfo info) {
		if (world != null) {
			Opium.EVENT_BUS.post(new GameLeftEvent());
		}
	}

	@Shadow
	@Final
	public InGameHud inGameHud;

	@Inject(method = "disconnect(Lnet/minecraft/client/gui/screen/Screen;)V", at = @At("HEAD"))
	private void clearTitleMixin(Screen screen, CallbackInfo info) {
		if (ClientSetting.INSTANCE.titleFix.getValue()) {
			inGameHud.clearTitle();
			inGameHud.setDefaultTitleFade();
		}
	}
	@Shadow
	public int attackCooldown;

	@Shadow
	public ClientPlayerEntity player;

	@Shadow
	public HitResult crosshairTarget;

	@Shadow
	public ClientPlayerInteractionManager interactionManager;

	@Final
	@Shadow
	public ParticleManager particleManager;

	@Inject(method = "handleBlockBreaking", at = @At("HEAD"), cancellable = true)
	private void handleBlockBreaking(boolean breaking, CallbackInfo ci) {
		if (this.attackCooldown <= 0 && this.player.isUsingItem() && InteractTweaks.INSTANCE.multiTask()) {
			if (breaking && this.crosshairTarget != null && this.crosshairTarget.getType() == HitResult.Type.BLOCK) {
				BlockHitResult blockHitResult = (BlockHitResult)this.crosshairTarget;
				BlockPos blockPos = blockHitResult.getBlockPos();
				if (!this.world.getBlockState(blockPos).isAir()) {
					Direction direction = blockHitResult.getSide();
					if (this.interactionManager.updateBlockBreakingProgress(blockPos, direction)) {
						this.particleManager.addBlockBreakingParticles(blockPos, direction);
						this.player.swingHand(Hand.MAIN_HAND);
					}
				}
			} else {
				this.interactionManager.cancelBlockBreaking();
			}
			ci.cancel();
		}
	}
	@Shadow
	public ClientWorld world;

	public MixinMinecraftClient(String string) {
		super(string);
	}

	@Inject(at = @At("HEAD"), method = "tick()V")
	public void tickHead(CallbackInfo info) {
		Opium.EVENT_BUS.post(new TickEvent(Event.Stage.Pre));
	}
	@Inject(at = @At("TAIL"), method = "tick()V")
	public void tickTail(CallbackInfo info) {
		Opium.EVENT_BUS.post(new TickEvent(Event.Stage.Post));
	}

	@Shadow
	private IntegratedServer server;

	@Shadow
	public ClientPlayNetworkHandler getNetworkHandler() {
		return null;
	}

	@Shadow
	public ServerInfo getCurrentServerEntry() {
		return null;
	}
}
