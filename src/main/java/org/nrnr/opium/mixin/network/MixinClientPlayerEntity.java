package org.nrnr.opium.mixin.network;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.input.Input;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.MovementType;
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.util.Hand;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.nrnr.opium.Opium;
import org.nrnr.opium.api.event.EventStage;
import org.nrnr.opium.impl.event.entity.SwingEvent;
import org.nrnr.opium.impl.event.entity.player.PlayerMoveEvent;
import org.nrnr.opium.impl.event.entity.player.SendMovePacketPostEvent;
import org.nrnr.opium.impl.event.network.*;
import org.nrnr.opium.impl.imixin.IClientPlayerEntity;
import org.nrnr.opium.init.Managers;
import org.nrnr.opium.util.Globals;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * @author chronos
 * @see ClientPlayerEntity
 * @since 1.0
 */
@Mixin(ClientPlayerEntity.class)
public abstract class MixinClientPlayerEntity extends AbstractClientPlayerEntity implements Globals, IClientPlayerEntity {
    //
    @Shadow
    @Final
    public ClientPlayNetworkHandler networkHandler;
    @Shadow
    public double lastX;
    @Shadow
    public double lastBaseY;
    @Shadow
    public double lastZ;
    @Shadow
    public Input input;
    //
    @Shadow
    @Final
    protected MinecraftClient client;
    // Last tick values
    @Shadow
    private boolean lastSneaking;
    @Shadow
    private float lastYaw;
    @Shadow
    private float lastPitch;
    @Shadow
    private boolean lastOnGround;
    //
    @Shadow
    private int ticksSinceLastPositionPacketSent;
    @Shadow
    private boolean autoJumpEnabled;
    //
    @Unique
    private boolean ticking;

    /**
     *
     */
    public MixinClientPlayerEntity() {
        // Treating this class as ClientPlayerEntity with mc.player info works
        // Need a better solution
        super(MinecraftClient.getInstance().world,
                MinecraftClient.getInstance().player.getGameProfile());
    }

    //
    @Shadow
    protected abstract void sendSprintingPacket();

    //
    @Shadow
    public abstract boolean isSneaking();

    @Shadow
    protected abstract boolean isCamera();

    //
    @Shadow
    protected abstract void autoJump(float dx, float dz);

    //
    @Shadow
    public abstract void tick();

    //
    @Shadow
    protected abstract void sendMovementPackets();

    /**
     * @param ci
     */
    @Inject(method = "sendMovementPackets", at = @At(value = "HEAD"), cancellable = true)
    private void hookSendMovementPackets(CallbackInfo ci) {
        PlayerUpdateEvent playerUpdateEvent = new PlayerUpdateEvent();
        playerUpdateEvent.setStage(EventStage.PRE);
        Opium.EVENT_HANDLER.dispatch(playerUpdateEvent);
        // Rotation spoof
        MovementPacketsEvent movementPacketsEvent = new MovementPacketsEvent(mc.player.getX(), mc.player.getY(),
                mc.player.getZ(), mc.player.getYaw(), mc.player.getPitch(), mc.player.isOnGround());
        Opium.EVENT_HANDLER.dispatch(movementPacketsEvent);
        double x = movementPacketsEvent.getX();
        double y = movementPacketsEvent.getY();
        double z = movementPacketsEvent.getZ();
        float yaw = movementPacketsEvent.getYaw();
        float pitch = movementPacketsEvent.getPitch();
        boolean ground = movementPacketsEvent.getOnGround();
        if (movementPacketsEvent.isCanceled()) {
            ci.cancel();
            sendSprintingPacket();
            boolean bl = isSneaking();
            if (bl != lastSneaking) {
                ClientCommandC2SPacket.Mode mode = bl ? ClientCommandC2SPacket.Mode.PRESS_SHIFT_KEY :
                        ClientCommandC2SPacket.Mode.RELEASE_SHIFT_KEY;
                networkHandler.sendPacket(new ClientCommandC2SPacket(this, mode));
                lastSneaking = bl;
            }
            if (isCamera()) {
                double d = x - lastX;
                double e = y - lastBaseY;
                double f = z - lastZ;
                double g = yaw - lastYaw;
                double h = pitch - lastPitch;
                ++ticksSinceLastPositionPacketSent;
                boolean bl2 = MathHelper.squaredMagnitude(d, e, f) > MathHelper.square(2.0E-4) || ticksSinceLastPositionPacketSent >= 20;
                boolean bl3 = g != 0.0 || h != 0.0;
                if (hasVehicle()) {
                    Vec3d vec3d = getVelocity();
                    networkHandler.sendPacket(new PlayerMoveC2SPacket.Full(vec3d.x, -999.0, vec3d.z, getYaw(), getPitch(), ground));
                    bl2 = false;
                } else if (bl2 && bl3) {
                    networkHandler.sendPacket(new PlayerMoveC2SPacket.Full(x, y, z, yaw, pitch, ground));
                } else if (bl2) {
                    networkHandler.sendPacket(new PlayerMoveC2SPacket.PositionAndOnGround(x, y, z, ground));
                } else if (bl3) {
                    networkHandler.sendPacket(new PlayerMoveC2SPacket.LookAndOnGround(yaw, pitch, ground));
                } else if (lastOnGround != isOnGround()) {
                    networkHandler.sendPacket(new PlayerMoveC2SPacket.OnGroundOnly(ground));
                }
                if (bl2) {
                    lastX = x;
                    lastBaseY = y;
                    lastZ = z;
                    ticksSinceLastPositionPacketSent = 0;
                }
                if (bl3) {
                    lastYaw = yaw;
                    lastPitch = pitch;
                }
                lastOnGround = ground;
                autoJumpEnabled = client.options.getAutoJump().getValue();
            }
        }
        playerUpdateEvent.setStage(EventStage.POST);
        Opium.EVENT_HANDLER.dispatch(playerUpdateEvent);
    }

    /**
     * @param ci
     */
    @Inject(method = "tick", at = @At(value = "INVOKE", target = "Lnet/" +
            "minecraft/client/network/AbstractClientPlayerEntity;tick()V",
            shift = At.Shift.BEFORE, ordinal = 0))
    private void hookTickPre(CallbackInfo ci) {
        PlayerTickEvent playerTickEvent = new PlayerTickEvent();
        Opium.EVENT_HANDLER.dispatch(playerTickEvent);
        Managers.ROTATION.onUpdate();
    }

    /**
     * @param ci
     */
    @Inject(method = "tick", at = @At(value = "INVOKE", target = "Lnet/" +
            "minecraft/client/network/ClientPlayerEntity;sendMovementPackets" +
            "()V", ordinal = 0, shift = At.Shift.AFTER))
    private void hookTick(CallbackInfo ci) {
        if (ticking) {
            return;
        }
        TickMovementEvent tickMovementEvent = new TickMovementEvent();
        Opium.EVENT_HANDLER.dispatch(tickMovementEvent);
        if (tickMovementEvent.isCanceled()) {
            for (int i = 0; i < tickMovementEvent.getIterations(); i++) {
                ticking = true;
                tick();
                ticking = false;
                sendMovementPackets();
            }
        }
    }

    /**
     * @param ci
     */
    @Inject(method = "tickMovement", at = @At(value = "FIELD", target =
            "Lnet/minecraft/client/network/ClientPlayerEntity;" +
                    "ticksLeftToDoubleTapSprint:I", shift = At.Shift.AFTER))
    private void hookTickMovementPost(CallbackInfo ci) {
        MovementSlowdownEvent movementUpdateEvent =
                new MovementSlowdownEvent(input);
        Opium.EVENT_HANDLER.dispatch(movementUpdateEvent);
    }

    /**
     * @param movementType
     * @param movement
     * @param ci
     */
    @Inject(method = "move", at = @At(value = "HEAD"), cancellable = true)
    private void hookMove(MovementType movementType, Vec3d movement,
                          CallbackInfo ci) {
        final PlayerMoveEvent playerMoveEvent =
                new PlayerMoveEvent(movementType, movement);
        Opium.EVENT_HANDLER.dispatch(playerMoveEvent);
        if (playerMoveEvent.isCanceled()) {
            ci.cancel();
            double d = getX();
            double e = getZ();
            super.move(movementType, playerMoveEvent.getMovement());
            autoJump((float) (getX() - d), (float) (getZ() - e));
        }
    }

    @Unique
    private static boolean fullNullCheck() {
        return mc.player == null || mc.world == null;
    }


    @Inject(method = "sendMovementPackets", at = @At("RETURN"), cancellable = true)
    private void sendMovementPacketsPostHook(CallbackInfo info) {
        if (fullNullCheck()) return;
        SendMovePacketPostEvent playerUpdatePostEvent = new SendMovePacketPostEvent();
        Opium.EVENT_HANDLER.dispatch(playerUpdatePostEvent);
        if (playerUpdatePostEvent.isCanceled()) {
            info.cancel();
        }
    }

    /**
     * @param x
     * @param z
     * @param ci
     */
    @Inject(method = "pushOutOfBlocks", at = @At(value = "HEAD"),
            cancellable = true)
    private void onPushOutOfBlocks(double x, double z, CallbackInfo ci) {
        PushOutOfBlocksEvent pushOutOfBlocksEvent = new PushOutOfBlocksEvent();
        Opium.EVENT_HANDLER.dispatch(pushOutOfBlocksEvent);
        if (pushOutOfBlocksEvent.isCanceled()) {
            ci.cancel();
        }
    }

    /**
     * @param hand
     * @param ci
     */
    @Inject(method = "setCurrentHand", at = @At(value = "HEAD"))
    private void hookSetCurrentHand(Hand hand, CallbackInfo ci) {
        SetCurrentHandEvent setCurrentHandEvent = new SetCurrentHandEvent(hand);
        Opium.EVENT_HANDLER.dispatch(setCurrentHandEvent);
    }

    /**
     * @param instance
     * @param b
     */
    @Redirect(method = "tickMovement", at = @At(value = "INVOKE", target =
            "Lnet/minecraft/client/network/ClientPlayerEntity;setSprinting(Z)V",
            ordinal = 3))
    private void hookSetSprinting(ClientPlayerEntity instance, boolean b) {
        final SprintCancelEvent sprintEvent = new SprintCancelEvent();
        Opium.EVENT_HANDLER.dispatch(sprintEvent);
        if (sprintEvent.isCanceled()) {
            instance.setSprinting(true);
        } else {
            instance.setSprinting(b);
        }
    }

    /**
     * @param instance
     * @return
     */
    @Redirect(method = "updateNausea", at = @At(value = "FIELD", target = "Lnet" +
            "/minecraft/client/MinecraftClient;currentScreen:Lnet/minecraft/" +
            "client/gui/screen/Screen;"))
    private Screen hookCurrentScreen(MinecraftClient instance) {
        //
        return null;
    }

    /**
     * @param cir
     */
    @Inject(method = "getMountJumpStrength", at = @At(value = "HEAD"),
            cancellable = true)
    private void hookGetMountJumpStrength(CallbackInfoReturnable<Float> cir) {
        MountJumpStrengthEvent mountJumpStrengthEvent =
                new MountJumpStrengthEvent();
        Opium.EVENT_HANDLER.dispatch(mountJumpStrengthEvent);
        if (mountJumpStrengthEvent.isCanceled()) {
            cir.cancel();
            cir.setReturnValue(mountJumpStrengthEvent.getJumpStrength());
        }
    }

    /**
     * @param hand
     * @param ci
     */
    @Inject(method = "swingHand", at = @At(value = "RETURN"))
    private void hookSwingHand(Hand hand, CallbackInfo ci) {
        SwingEvent swingEvent = new SwingEvent(hand);
        Opium.EVENT_HANDLER.dispatch(swingEvent);
    }

    @Override
    public float getLastSpoofedYaw() {
        return lastYaw;
    }

    @Override
    public float getLastSpoofedPitch() {
        return lastPitch;
    }
}
