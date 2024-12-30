package org.nrnr.opium.impl.module.world;

import net.minecraft.block.BlockState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.effect.StatusEffectUtil;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.EndCrystalItem;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.c2s.play.CloseHandledScreenC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.network.packet.c2s.play.UpdateSelectedSlotC2SPacket;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.*;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import org.nrnr.opium.api.config.Config;
import org.nrnr.opium.api.config.setting.BooleanConfig;
import org.nrnr.opium.api.config.setting.ColorConfig;
import org.nrnr.opium.api.config.setting.NumberConfig;
import org.nrnr.opium.api.event.EventStage;
import org.nrnr.opium.api.event.listener.EventListener;
import org.nrnr.opium.api.module.ModuleCategory;
import org.nrnr.opium.api.module.RotationModule;
import org.nrnr.opium.api.render.RenderManager;
import org.nrnr.opium.api.render.RenderManagerWorld;
import org.nrnr.opium.impl.event.config.ConfigUpdateEvent;
import org.nrnr.opium.impl.event.network.AttackBlockEvent;
import org.nrnr.opium.impl.event.network.PacketEvent;
import org.nrnr.opium.impl.event.network.PlayerTickEvent;
import org.nrnr.opium.impl.event.render.RenderWorldEvent;
import org.nrnr.opium.init.Managers;
import org.nrnr.opium.init.Modules;
import org.nrnr.opium.util.EvictingQueue;
import org.nrnr.opium.util.player.RotationUtil;
import org.nrnr.opium.util.world.ExplosionUtil;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.text.DecimalFormat;
import java.util.Deque;
import java.util.List;
import java.util.PriorityQueue;

/**
 * @author Chronos
 * @since 1.0
 */
public class AutoMineModule extends RotationModule {

    Config<Boolean> multitaskConfig = new BooleanConfig("Multitask", "Allows mining while using items", false);
    Config<Boolean> autoConfig = new BooleanConfig("Auto", "Automatically mines nearby players feet", false);
    Config<Boolean> autoRemineConfig = new BooleanConfig("AutoRemine", "Automatically remines mined blocks", true, () -> autoConfig.getValue());
    Config<Boolean> strictDirectionConfig = new BooleanConfig("StrictDirection", "Only mines on visible faces", false, () -> autoConfig.getValue());
    Config<Float> enemyRangeConfig = new NumberConfig<>("EnemyRange", "Range to search for targets", 1.0f, 5.0f, 10.0f, () -> autoConfig.getValue());
    Config<Boolean> doubleBreakConfig = new BooleanConfig("DoubleBreak", "Allows you to mine two blocks at once", false);
    Config<Boolean> tripleBreakConfig = new BooleanConfig("TripleBreak", "Allows you to mine three blocks at once", false);
    Config<Boolean> autoFishConfig = new BooleanConfig("AutoFish", "Mines block below players feet if in phase", false);
    Config<Boolean> antiFishConfig = new BooleanConfig("AntiFish", "Mines block above head", false);
    Config<Boolean> bomber = new BooleanConfig("Bomber", "Mines block above head", false);
    Config<Float> rangeConfig = new NumberConfig<>("Range", "The range to mine blocks", 0.1f, 4.0f, 5.0f);
    Config<Float> speedConfig = new NumberConfig<>("Speed", "The speed to mine blocks", 0.1f, 1.0f, 1.0f);
    Config<Boolean> rotateConfig = new BooleanConfig("Rotate", "Rotates when mining the block", true);
    Config<Boolean> switchResetConfig = new BooleanConfig("SwitchReset", "Resets mining after switching items", false);
    Config<Boolean> grimConfig = new BooleanConfig("Grim", "Uses grim block breaking speeds", false);
    Config<Boolean> swapConfig = new BooleanConfig("Alternative", "Uses chronos swap", false);
    Config<Boolean> instantConfig = new BooleanConfig("Instant", "Instant remines mined blocks", true);
    Config<Boolean> growRender = new BooleanConfig("GrowRenders", "Grow Renders mined blocks", true);
    Config<Color> colorConfig = new ColorConfig("Color", "Only white/purplish works for now", new Color(0, 0, 0, 50), true, false);
    Config<Boolean> lineConfig = new BooleanConfig("Line", "Line or Box?", true);
    Config<Boolean> damageConfig = new BooleanConfig("Damage", "Render Damage", true);


    private Deque<MiningData> miningQueue = new EvictingQueue<>(2);
    private long lastBreak;
    private boolean manualOverride;

    public AutoMineModule() {
        super("AutoMine", "Automatically mines blocks", ModuleCategory.WORLD, 900);
    }

    @Override
    public String getModuleData() {
        if (!miningQueue.isEmpty()) {
            MiningData data = miningQueue.peek();
            return String.format("%.1f", Math.min(data.getBlockDamage(), 1.0f));
        }
        return super.getModuleData();
    }

    @Override
    public void onEnable() {
        if (doubleBreakConfig.getValue()) {
            miningQueue = new EvictingQueue<>(2);
        }
        else if (tripleBreakConfig.getValue()) {
            miningQueue = new EvictingQueue<>(3);
        } else {
            miningQueue = new EvictingQueue<>(1);
        }
    }

    @Override
    protected void onDisable() {
        miningQueue.clear();
        manualOverride = false;
        Managers.INVENTORY.syncToClient();
    }

    @EventListener
    public void onPlayerTick(final PlayerTickEvent event) {
        MiningData miningData = null;
        if (!miningQueue.isEmpty()) {
            miningData = miningQueue.getFirst();
        }
        if (autoConfig.getValue() && !manualOverride && (miningData == null || mc.world.isAir(miningData.getPos()))) {
            PlayerEntity playerTarget = null;
            double minDistance = Float.MAX_VALUE;
            for (PlayerEntity entity : mc.world.getPlayers()) {
                if (entity == mc.player || Managers.SOCIAL.isFriend(entity.getName())) {
                    continue;
                }
                double dist = mc.player.distanceTo(entity);
                if (dist > enemyRangeConfig.getValue()) {
                    continue;
                }
                if (dist < minDistance) {
                    minDistance = dist;
                    playerTarget = entity;
                }
            }
            if (playerTarget != null) {
                PriorityQueue<AutoMineCalc> miningPositions = getMiningPosition(playerTarget);
                PriorityQueue<AutoMineCalc> miningPositionsNoAir = getNoAir(miningPositions, playerTarget);
                PriorityQueue<AutoMineCalc> cityPositions = autoRemineConfig.getValue() ? miningPositions : miningPositionsNoAir;
                if (cityPositions.isEmpty()) {
                    return;
                }

                if (doubleBreakConfig.getValue()) {
                    final AutoMineCalc cityPos = cityPositions.poll();
                    if (cityPos != null) {
                        AutoMineCalc cityPos2 = null;
                        miningPositionsNoAir.removeIf(c -> c.pos().equals(cityPos.pos()));
                        if (!miningPositionsNoAir.isEmpty()) {
                            cityPos2 = miningPositionsNoAir.poll();
                        }
                        if (cityPos2 != null) {

                            if (!mc.world.isAir(cityPos.pos()) && !mc.world.isAir(cityPos2.pos()) && !isBlockDelayGrim()) {
                                MiningData data1 = new AutoMiningData(cityPos2.pos(),
                                        strictDirectionConfig.getValue() ? Managers.INTERACT.getPlaceDirectionGrim(cityPos2.pos()) : Direction.UP);
                                MiningData data2 = new AutoMiningData(cityPos.pos(),
                                        strictDirectionConfig.getValue() ? Managers.INTERACT.getPlaceDirectionGrim(cityPos.pos()) : Direction.UP);
                                MiningData fish = new AutoMiningData(mc.player.getBlockPos().up(1),
                                        strictDirectionConfig.getValue() ? Managers.INTERACT.getPlaceDirectionGrim(mc.player.getBlockPos().up(1)) : Direction.UP);

                                /*if (autoFishConfig.getValue()){
                                    if (mc.player.isCrawling()) {
                                        startMining(fish);
                                        startMining(data1);
                                    }
                                }
                               */ //else{
                                startMining(data1);
                                startMining(data2);
                                //}
                                /*if (autoFishConfig.getValue()){
                                    if (mc.player.isCrawling()){
                                        miningQueue.addFirst(fish);
                                        miningQueue.addFirst(data1);
                                    }
                                }
                               */// else {
                                miningQueue.addFirst(data1);
                                miningQueue.addFirst(data2);
                                // }

                            }
                        } else {
                            // If we are re-mining, bypass throttle check below
                            if (!mc.world.isAir(cityPos.pos()) && !isBlockDelayGrim()) {
                                MiningData data = new AutoMiningData(cityPos.pos(),
                                        strictDirectionConfig.getValue() ? Managers.INTERACT.getPlaceDirectionGrim(cityPos.pos()) : Direction.UP);
                                startMining(data);
                                miningQueue.addFirst(data);
                            }
                        }
                    }

                } else {
                    final AutoMineCalc cityBlockPos = cityPositions.poll();
                    if (cityBlockPos != null && !isBlockDelayGrim()) {
                        // If we are re-mining, bypass throttle check below
                        if (miningData instanceof AutoMiningData && miningData.isInstantRemine() && !mc.world.isAir(miningData.getPos()) && autoRemineConfig.getValue()) {
                            stopMining(miningData);
                        } else if (!mc.world.isAir(cityBlockPos.pos()) && !isBlockDelayGrim()) {
                            MiningData data = new AutoMiningData(cityBlockPos.pos(),
                                    strictDirectionConfig.getValue() ? Managers.INTERACT.getPlaceDirectionGrim(cityBlockPos.pos()) : Direction.UP);
                            startMining(data);
                            miningQueue.addFirst(data);
                        }
                    }
                }
            }
        }
        if (miningQueue.isEmpty()) {
            return;
        }
        for (MiningData data : miningQueue) {
            if (isDataPacketMine(data) && (data.getState().isAir() || data.getBlockDamage() >= 1.5f)) {
                Managers.INVENTORY.syncToClient();
                miningQueue.remove(data);
                return;
            }
            final float damageDelta = calcBlockBreakingDelta(
                    data.getState(), mc.world, data.getPos());
            data.damage(damageDelta);
            if (data.getBlockDamage() >= 1.0f && isDataPacketMine(data)) {
                if (mc.player.isUsingItem() && !multitaskConfig.getValue()) {
                    return;
                }
                if (data.getSlot() != -1) {
                    Managers.INVENTORY.setSlot(data.getSlot());
                }
            }
        }
        MiningData miningData2 = miningQueue.getFirst();
        if (miningData2 != null) {
            final double distance = mc.player.getEyePos().squaredDistanceTo(miningData2.getPos().toCenterPos());
            if (distance > ((NumberConfig<Float>) rangeConfig).getValueSq()) {
                //            abortMining(miningData);
                miningQueue.remove(miningData2);
                return;
            }
            if (miningData2.getState().isAir()) {
                // Once we broke the block that overrode that the auto city, we can allow the module
                // to auto mine "city" blocks
                if (manualOverride) {
                    manualOverride = false;
                    miningQueue.remove(miningData2);
                    return;
                }
                if (instantConfig.getValue()) {
                    if (miningData2 instanceof AutoMiningData && !autoRemineConfig.getValue()) {
                        miningQueue.remove(miningData2);
                        return;
                    }
                    miningData2.setInstantRemine();
                    miningData2.setDamage(1.0f);
                } else {
                    miningData2.resetDamage();
                }
                return;
            }
            if (miningData2.getBlockDamage() >= speedConfig.getValue() || miningData2.isInstantRemine()) {
                if (mc.player.isUsingItem() && !multitaskConfig.getValue()) {
                    return;
                }
                stopMining(miningData2);
            }
        }
    }

    float calcBlockBreakingDelta(BlockState state, BlockView world, BlockPos pos) {
        float f = state.getHardness(world, pos);
        if (f == -1.0f) {
            return 0.0f;
        } else {
            int i = canHarvest(state) ? 30 : 100;
            return getBlockBreakingSpeed(state) / f / (float) i;
        }
    }

    private float getBlockBreakingSpeed(BlockState block) {
        int tool = Modules.AUTO_TOOL.getBestTool(block);
        float f = mc.player.getInventory().getStack(tool).getMiningSpeedMultiplier(block);
        if (f > 1.0F) {
            ItemStack stack = mc.player.getInventory().getStack(tool);
            int i = EnchantmentHelper.getLevel(Enchantments.EFFICIENCY, stack);
            if (i > 0 && !stack.isEmpty()) {
                f += (float) (i * i + 1);
            }
        }
        if (StatusEffectUtil.hasHaste(mc.player)) {
            f *= 1.0f + (float) (StatusEffectUtil.getHasteAmplifier(mc.player) + 1) * 0.2f;
        }
        if (mc.player.hasStatusEffect(StatusEffects.MINING_FATIGUE)) {
            float g = switch (mc.player.getStatusEffect(StatusEffects.MINING_FATIGUE).getAmplifier()) {
                case 0 -> 0.3f;
                case 1 -> 0.09f;
                case 2 -> 0.0027f;
                default -> 8.1e-4f;
            };
            f *= g;
        }
        if (mc.player.isSubmergedIn(FluidTags.WATER)
                && !EnchantmentHelper.hasAquaAffinity(mc.player)) {
            f /= 5.0f;
        }
        if (!mc.player.isOnGround()) {
            f /= 5.0f;
        }
        return f;
    }

    private boolean canHarvest(BlockState state) {
        if (state.isToolRequired()) {
            int tool = Modules.AUTO_TOOL.getBestTool(state);
            return mc.player.getInventory().getStack(tool).isSuitableFor(state);
        }
        return true;
    }

    @EventListener
    public void onAttackBlock(final AttackBlockEvent event) {
        // Do not try to break unbreakable blocks
        if (event.getState().getBlock().getHardness() == -1.0f || event.getState().isAir() || mc.player.isCreative()) {
            return;
        }
        event.cancel();
        int queueSize = miningQueue.size();
        if (queueSize == 0) {
            attemptMine(event.getPos(), event.getDirection());
        } else if (queueSize == 1) {
            MiningData data = miningQueue.getFirst();
            if (data.getPos().equals(event.getPos())) {
//              abortMining(miningData);
                return;
            }
            // Only count as an override if AutoCity is doing something
            if (data instanceof AutoMiningData) {
                manualOverride = true;
            }
            attemptMine(event.getPos(), event.getDirection());
        } else if (queueSize == 2) {
            MiningData data1 = miningQueue.getFirst();
            MiningData data2 = miningQueue.getLast();
            if (data1.getPos().equals(event.getPos()) || data2.getPos().equals(event.getPos())) {
//              abortMining(miningData);
                return;
            }
            if (data1 instanceof AutoMiningData || data2 instanceof AutoMiningData) {
                manualOverride = true;
            }
            attemptMine(event.getPos(), event.getDirection());
        }
        mc.player.swingHand(Hand.MAIN_HAND);
    }

    @EventListener
    public void onPacketOutbound(PacketEvent.Outbound event) {
        if (event.getPacket() instanceof UpdateSelectedSlotC2SPacket && switchResetConfig.getValue()) {
            for (MiningData data : miningQueue) {
                data.resetDamage();
            }
        }
    }

    @EventListener
    public void onRenderWorld(final RenderWorldEvent event) {
        for (MiningData data : miningQueue) {
            renderMiningData(event.getMatrices(), data);
        }
    }

    private void renderMiningData(MatrixStack matrixStack, MiningData data) {
        if (data != null && !mc.player.isCreative() && data.getBlockDamage() > 0.01f) {
            float miningSpeed = isDataPacketMine(data) ? 1.0f : speedConfig.getValue();
            BlockPos mining = data.getPos();
            VoxelShape outlineShape = VoxelShapes.fullCube();
            if (!data.isInstantRemine()) {
                outlineShape = data.getState().getOutlineShape(mc.world, mining);
                outlineShape = outlineShape.isEmpty() ? VoxelShapes.fullCube() : outlineShape;
            }
            Box render1 = outlineShape.getBoundingBox();
            Box render = new Box(mining.getX() + render1.minX, mining.getY() + render1.minY,
                    mining.getZ() + render1.minZ, mining.getX() + render1.maxX,
                    mining.getY() + render1.maxY, mining.getZ() + render1.maxZ);
            Vec3d center = render.getCenter();
            float scale = MathHelper.clamp(data.getBlockDamage() / miningSpeed, 0, 1.0f);
            double dx = (render1.maxX - render1.minX) / 2.0;
            double dy = (render1.maxY - render1.minY) / 2.0;
            double dz = (render1.maxZ - render1.minZ) / 2.0;
            final Box scaled = new Box(center, center).expand(dx * scale, dy * scale, dz * scale);
            if (!growRender.getValue())
            {
                if (lineConfig.getValue()){
                    RenderManagerWorld.renderBoundingBox(matrixStack, scaled,
                            5f, data.getBlockDamage() > (0.95f * miningSpeed) ? colorConfig.getValue().getRGB() : colorConfig.getValue().getRGB()   );
                }
                else{
                    RenderManagerWorld.renderBox(matrixStack, scaled,
                            data.getBlockDamage() > (0.95f * miningSpeed) ? colorConfig.getValue().getRGB() : colorConfig.getValue().getRGB());

                    RenderManagerWorld.renderBoundingBox(matrixStack, scaled,
                            2.5F, data.getBlockDamage() > (0.95f * miningSpeed) ? colorConfig.getValue().getRGB() : colorConfig.getValue().getRGB()   );
                }

                // ChatUtil.clientSendMessage(String.valueOf(data.getBlockDamage()));
            }
            else{
                if (lineConfig.getValue()){

                    RenderManagerWorld.renderBoundingBox(matrixStack, scaled,
                            5f, data.getBlockDamage() > (0.95f * miningSpeed) ? 0x6000ff00 : 0x60ff0000);
                }
                else{
                    RenderManagerWorld.renderBox(matrixStack, scaled,
                            data.getBlockDamage() > (0.95f * miningSpeed) ? 0x6000ff00 : 0x60ff0000);
                    RenderManagerWorld.renderBoundingBox(matrixStack, scaled,
                            2.5f, data.getBlockDamage() > (0.95f * miningSpeed) ? 0x6000ff00 : 0x60ff0000);
                }


            }

            if (damageConfig.getValue()) {
                DecimalFormat format = new DecimalFormat("0");
                Vec3d boxCenter = scaled.getCenter();
                RenderManager.post(() -> {
                    float damagePercent = data.getBlockDamage() / this.speedConfig.getValue().floatValue() * 100.0f;
                    String damagePercentage = format.format(damagePercent > 100.0f ? 100.0 : (double)damagePercent) + "%";
                    RenderManager.renderSign(matrixStack, damagePercentage, boxCenter);
                });
            }

        }
    }

    @EventListener
    public void onConfigUpdate(ConfigUpdateEvent event) {
        if (event.getStage() == EventStage.POST && event.getConfig() == doubleBreakConfig) {
            if (doubleBreakConfig.getValue()) {
                miningQueue = new EvictingQueue<>(2);
            } else {
                miningQueue = new EvictingQueue<>(1);
            }
        }
    }

    // LOL
    private PriorityQueue<AutoMineCalc> getNoAir(PriorityQueue<AutoMineCalc> calcs, PlayerEntity player) {
        PriorityQueue<AutoMineCalc> noAir = new PriorityQueue<>();
        for (AutoMineCalc calc : calcs) {
            if (mc.world.isAir(calc.pos())) {
                continue;
            }
            noAir.add(calc);
        }
        noAir.removeIf(c -> c.pos().equals(player.getBlockPos()));
        return noAir;
    }

    private PriorityQueue<AutoMineCalc> getMiningPosition(PlayerEntity entity) {
        List<BlockPos> entityIntersections = Modules.SURROUND.getSurroundEntities(entity);
        PriorityQueue<AutoMineCalc> miningPositions = new PriorityQueue<>();
        for (BlockPos blockPos : entityIntersections) {
            double dist = mc.player.getEyePos().squaredDistanceTo(blockPos.toCenterPos());
            if (dist > ((NumberConfig<Float>) rangeConfig).getValueSq()) {
                continue;
            }
            if (!mc.world.getBlockState(blockPos).isReplaceable()) {
                miningPositions.add(new AutoMineCalc(blockPos, Double.MAX_VALUE));
            }
        }
        List<BlockPos> surroundBlocks = Modules.SURROUND.getEntitySurroundNoSupport(entity);
        for (BlockPos blockPos : surroundBlocks) {
            double dist = mc.player.getEyePos().squaredDistanceTo(blockPos.toCenterPos());
            if (dist > ((NumberConfig<Float>) rangeConfig).getValueSq()) {
                continue;
            }
            double damage = ExplosionUtil.getDamageTo(entity, blockPos.toCenterPos().subtract(0.0, -0.5, 0.0), true);
            miningPositions.add(new AutoMineCalc(blockPos, damage));
        }
        return miningPositions;
    }

    private record AutoMineCalc(BlockPos pos, double entityDamage) implements Comparable<AutoMineCalc> {

        @Override
        public int compareTo(@NotNull AutoMineCalc o) {
            return Double.compare(-entityDamage(), -o.entityDamage());
        }
    }

    private void attemptMine(BlockPos pos, Direction direction) {
        if (isBlockDelayGrim()) {
            return;
        }
        MiningData miningData = new MiningData(pos, direction);
        startMining(miningData);
        miningQueue.addFirst(miningData);
    }

    private void placeCrystal(BlockPos pos) {

        if (rotateConfig.getValue()) {
            float rotations[] = RotationUtil.getRotationsTo(mc.player.getEyePos(), pos.toCenterPos());
            setRotationSilent(rotations[0], rotations[1]);
        }
        Managers.INVENTORY.syncToClient();
    }

    private int getCrystalSlot() {
        int slot = -1;
        for (int i = 0; i < 9; i++) {
            ItemStack stack = mc.player.getInventory().getStack(i);
            if (stack.getItem() instanceof EndCrystalItem) {
                slot = i;
                break;
            }
        }
        return slot;
    }


    public int prevslot = 0;

    private void startMining(MiningData data) {
        assert mc.player != null;
        prevslot = mc.player.getInventory().selectedSlot;
        if (data.getState().isAir() || data.isStarted()) {
            return;
        }

        if (bomber.getValue()) {
            placeCrystal(data.getPos());
        }

        Managers.NETWORK.sendSequencedPacket(id -> new PlayerActionC2SPacket(
                PlayerActionC2SPacket.Action.STOP_DESTROY_BLOCK, data.getPos(), data.getDirection(), id));
        Managers.NETWORK.sendSequencedPacket(id -> new PlayerActionC2SPacket(
                PlayerActionC2SPacket.Action.START_DESTROY_BLOCK, data.getPos(), data.getDirection(), id));
        // packet mine
        if (doubleBreakConfig.getValue()) {
            Managers.NETWORK.sendSequencedPacket(id -> new PlayerActionC2SPacket(
                    PlayerActionC2SPacket.Action.STOP_DESTROY_BLOCK, data.getPos(), data.getDirection(), id));
        }

        data.setStarted();
    }



    private void abortMining(MiningData data) {
        if (!data.isStarted() || data.getState().isAir() || data.isInstantRemine() || data.getBlockDamage() >= 1.0f) {
            return;
        }
        Managers.NETWORK.sendSequencedPacket(id -> new PlayerActionC2SPacket(
                PlayerActionC2SPacket.Action.ABORT_DESTROY_BLOCK, data.getPos(), data.getDirection(), id));
        Managers.INVENTORY.syncToClient();
    }

    private void stopMining(MiningData data) {
        if (!data.isStarted() || data.getState().isAir()) {
            return;
        }
        int slot = data.getSlot();

        boolean canSwap = data.getSlot() != -1;

        if (canSwap & !swapConfig.getValue()) {
            Managers.INVENTORY.setSlot(data.getSlot());
        }

        if (swapConfig.getValue() || slot >= 9) {
            switchTo(slot, -1);
        }
        if (rotateConfig.getValue()) {
            float rotations[] = RotationUtil.getRotationsTo(mc.player.getEyePos(), data.getPos().toCenterPos());
            setRotationSilent(rotations[0], rotations[1]);
        }
        Managers.NETWORK.sendSequencedPacket(id -> new PlayerActionC2SPacket(
                PlayerActionC2SPacket.Action.STOP_DESTROY_BLOCK, data.getPos(), data.getDirection(), id));
        lastBreak = System.currentTimeMillis();
        if (canSwap & !swapConfig.getValue()) {
            Managers.INVENTORY.syncToClient();
        }
        if (swapConfig.getValue() || slot >= 9) {
            switchTo(prevslot, slot);
        }
        if (rotateConfig.getValue()) {
            Managers.ROTATION.setRotationSilentSync(true);
        }
    }

    private void switchTo(int slot, int from) {
        if (swapConfig.getValue() || slot >= 9) {
            if (from == -1)
                clickSlot(slot < 9 ? slot + 36 : slot, mc.player.getInventory().selectedSlot, SlotActionType.SWAP);
            else
                clickSlot(from < 9 ? from + 36 : from, mc.player.getInventory().selectedSlot, SlotActionType.SWAP);
            closeScreen();
            ;
        }
    }

    protected void sendPacket(Packet<?> packet) {
        if (mc.getNetworkHandler() == null) return;

        mc.getNetworkHandler().sendPacket(packet);
    }
    public static void clickSlot(int id, int button, SlotActionType type) {
        if (id == -1 || mc.interactionManager == null || mc.player == null) return;
        mc.interactionManager.clickSlot(mc.player.currentScreenHandler.syncId, id, button, type, mc.player);
    }


    private void closeScreen() {
        if (mc.player == null) return;

        sendPacket(new CloseHandledScreenC2SPacket(mc.player.currentScreenHandler.syncId));
    }

    private boolean isDataPacketMine(MiningData data) {
        return miningQueue.size() == 2 && data == miningQueue.getLast();
    }

    // https://github.com/GrimAnticheat/Grim/blob/2.0/src/main/java/ac/grim/grimac/checks/impl/misc/FastBreak.java#L80
    public boolean isBlockDelayGrim() {
        return System.currentTimeMillis() - lastBreak <= 280 && grimConfig.getValue();
    }

    public static class AutoMiningData extends MiningData {

        public AutoMiningData(BlockPos pos, Direction direction) {
            super(pos, direction);
        }
    }

    public static class MiningData {

        private final BlockPos pos;
        private final Direction direction;
        private float blockDamage;
        private boolean instantRemine;
        private boolean started;

        public MiningData(BlockPos pos, Direction direction) {
            this.pos = pos;
            this.direction = direction;
        }

        public boolean isInstantRemine() {
            return instantRemine;
        }

        public void setInstantRemine() {
            this.instantRemine = true;
        }

        public float damage(final float dmg) {
            blockDamage += dmg;
            return blockDamage;
        }

        public void setDamage(float blockDamage) {
            this.blockDamage = blockDamage;
        }

        public void resetDamage() {
            instantRemine = false;
            blockDamage = 0.0f;
        }

        public BlockPos getPos() {
            return pos;
        }

        public Direction getDirection() {
            return direction;
        }

        public int getSlot() {
            return Modules.AUTO_TOOL.getBestToolNoFallback(getState());
        }

        public BlockState getState() {
            return mc.world.getBlockState(pos);
        }

        public boolean isStarted() {
            return started;
        }

        public void setStarted() {
            this.started = true;
        }

        public float getBlockDamage() {
            return blockDamage;
        }
    }
}