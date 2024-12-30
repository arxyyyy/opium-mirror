package org.nrnr.opium.impl.module.combat;

import com.google.common.collect.Lists;
import net.minecraft.entity.Entity;
import net.minecraft.entity.decoration.EndCrystalEntity;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.*;
import net.minecraft.network.packet.s2c.play.HealthUpdateS2CPacket;
import net.minecraft.screen.slot.SlotActionType;
import org.nrnr.opium.api.config.Config;
import org.nrnr.opium.api.config.setting.BooleanConfig;
import org.nrnr.opium.api.config.setting.EnumConfig;
import org.nrnr.opium.api.config.setting.NumberConfig;
import org.nrnr.opium.api.event.listener.EventListener;
import org.nrnr.opium.api.module.ModuleCategory;
import org.nrnr.opium.api.module.ToggleModule;
import org.nrnr.opium.impl.event.network.PacketEvent;
import org.nrnr.opium.impl.event.network.PlayerTickEvent;
import org.nrnr.opium.init.Managers;
import org.nrnr.opium.util.player.InventoryUtil;
import org.nrnr.opium.util.player.PlayerUtil;
import org.nrnr.opium.util.world.ExplosionUtil;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * @author xgraza
 * @since 1.0
 */
public final class AutoTotemModule extends ToggleModule {
    EnumConfig<OffhandItem> itemConfig = new EnumConfig<>("Item", "The item to wield in your offhand", OffhandItem.TOTEM, OffhandItem.values());
    NumberConfig<Float> healthConfig = new NumberConfig<>("Health", "The health required to fall below before swapping to a totem", 0.0f, 14.0f, 20.0f);
    Config<Boolean> gappleConfig = new BooleanConfig("OffhandGapple", "If to equip a golden apple if holding down the item use button", true);
    Config<Boolean> crappleConfig = new BooleanConfig("Crapple", "If to use a normal golden apple if Absorption is present", true);
    Config<Boolean> lethalConfig = new BooleanConfig("Lethal", "Calculate lethal damage sources", false);
    Config<Boolean> fastConfig = new BooleanConfig("FastSwap", "Swaps items to offhand", true);
    Config<Boolean> debugConfig = new BooleanConfig("Debug", "If to debug on death", false);
    Config<Boolean> useHotbarSlot = new BooleanConfig("UseHotbarSlot", "Enable placing the totem in a specific hotbar slot", true);
    NumberConfig<Integer> hotbarSlotConfig = new NumberConfig<>("HotbarSlot", "The hotbar slot to place the totem in (0-8)", 0, 0, 8);
    Config<Boolean> dualModeConfig = new BooleanConfig("DualMode", "Enable both offhand and hotbar placement simultaneously", false);

    private int lastHotbarSlot, lastTotemCount;
    private Item lastHotbarItem;

    public AutoTotemModule() {
        super("AutoTotem", "Automatically replenishes the totem in your offhand or a hotbar slot", ModuleCategory.Combat);
    }

    @Override
    public String getModuleData() {
        return String.valueOf(Managers.INVENTORY.count(Items.TOTEM_OF_UNDYING));
    }

    @Override
    public void onDisable() {
        super.onDisable();
        lastHotbarSlot = -1;
        lastHotbarItem = null;
        lastTotemCount = 0;
    }

    @EventListener
    public void onPlayerTick(final PlayerTickEvent event) {
        final Item itemToWield = getItemToWield();

        // Handle offhand functionality
        if (mc.player.getOffHandStack().getItem() != itemToWield) {
            final int itemSlot = getSlotFor(itemToWield);
            if (itemSlot != -1) {
                if (fastConfig.getValue()) {
                    mc.interactionManager.clickSlot(mc.player.playerScreenHandler.syncId,
                            itemSlot < 9 ? itemSlot + 36 : itemSlot, 40, SlotActionType.SWAP, mc.player);
                } else {
                    mc.interactionManager.clickSlot(mc.player.playerScreenHandler.syncId,
                            itemSlot < 9 ? itemSlot + 36 : itemSlot, 0, SlotActionType.PICKUP, mc.player);
                    mc.interactionManager.clickSlot(mc.player.playerScreenHandler.syncId,
                            45, 0, SlotActionType.PICKUP, mc.player);
                    if (!mc.player.playerScreenHandler.getCursorStack().isEmpty()) {
                        mc.interactionManager.clickSlot(mc.player.playerScreenHandler.syncId,
                                itemSlot < 9 ? itemSlot + 36 : itemSlot, 0, SlotActionType.PICKUP, mc.player);
                    }
                }
                lastTotemCount = Managers.INVENTORY.count(Items.TOTEM_OF_UNDYING) - 1;
            }
        }

        // Handle hotbar functionality if enabled
        if (useHotbarSlot.getValue()) {
            final int hotbarSlot = hotbarSlotConfig.getValue();

            // Check if the item is already in the selected hotbar slot
            if (!mc.player.getInventory().getStack(hotbarSlot).getItem().equals(itemToWield)) {
                final int itemSlot = getSlotFor(itemToWield);
                if (itemSlot != -1) {
                    if (fastConfig.getValue()) {
                        mc.interactionManager.clickSlot(mc.player.playerScreenHandler.syncId,
                                itemSlot < 9 ? itemSlot + 36 : itemSlot, hotbarSlot, SlotActionType.SWAP, mc.player);
                    } else {
                        mc.interactionManager.clickSlot(mc.player.playerScreenHandler.syncId,
                                itemSlot < 9 ? itemSlot + 36 : itemSlot, 0, SlotActionType.PICKUP, mc.player);
                        mc.interactionManager.clickSlot(mc.player.playerScreenHandler.syncId,
                                hotbarSlot + 36, 0, SlotActionType.PICKUP, mc.player);
                        if (!mc.player.playerScreenHandler.getCursorStack().isEmpty()) {
                            mc.interactionManager.clickSlot(mc.player.playerScreenHandler.syncId,
                                    itemSlot < 9 ? itemSlot + 36 : itemSlot, 0, SlotActionType.PICKUP, mc.player);
                        }
                    }
                    lastTotemCount = Managers.INVENTORY.count(Items.TOTEM_OF_UNDYING) - 1;
                }
            }
        }
    }

    @EventListener
    public void onPacketInbound(final PacketEvent.Inbound event) {
        if (event.getPacket() instanceof HealthUpdateS2CPacket packet && packet.getHealth() <= 0.0f && debugConfig.getValue()) {
            final Set<String> reasons = new LinkedHashSet<>();

            if (lastTotemCount <= 0) {
                reasons.add("no_totems");
            }

            if (mc.player.currentScreenHandler.syncId != 0) {
                reasons.add("gui_fail(" + mc.player.currentScreenHandler.syncId + ")");
            }

            if (!mc.player.currentScreenHandler.getCursorStack().isEmpty()) {
                reasons.add("cursor_stack=" + mc.player.currentScreenHandler.getCursorStack().getItem());
            }

            if (!reasons.isEmpty()) {
                sendModuleMessage("Possible failure reasons: " + String.join(", ", reasons));
            } else {
                final int totemCount = Managers.INVENTORY.count(Items.TOTEM_OF_UNDYING);
                sendModuleMessage("Could not figure out possible reasons. meta:{totemCount=" + totemCount + ", matchesCache=" + (totemCount == lastTotemCount) + ", cached=" + lastTotemCount + "}");
            }
        }
    }

    private int getSlotFor(final Item item) {
        for (int slot = 36; slot >= 0; slot--) {
            final ItemStack itemStack = mc.player.getInventory().getStack(slot);
            if (!itemStack.isEmpty() && itemStack.getItem().equals(item)) {
                return slot;
            }
        }
        return -1;
    }

    private Item getItemToWield() {
        final float health = PlayerUtil.getLocalPlayerHealth();
        if (health <= healthConfig.getValue()) {
            return Items.TOTEM_OF_UNDYING;
        }
        if (PlayerUtil.computeFallDamage(mc.player.fallDistance, 1.0f) + 0.5f > mc.player.getHealth()) {
            return Items.TOTEM_OF_UNDYING;
        }
        if (lethalConfig.getValue()) {
            final List<Entity> entities = Lists.newArrayList(mc.world.getEntities());
            for (Entity e : entities) {
                if (e == null || !e.isAlive() || !(e instanceof EndCrystalEntity crystal)) {
                    continue;
                }
                if (mc.player.squaredDistanceTo(e) > 144.0) {
                    continue;
                }
                double potential = ExplosionUtil.getDamageTo(mc.player, crystal.getPos());
                if (health + 0.5 > potential) {
                    continue;
                }
                return Items.TOTEM_OF_UNDYING;
            }
        }
        if (gappleConfig.getValue() && mc.options.useKey.isPressed() && (mc.player.getMainHandStack().getItem() instanceof SwordItem
                || mc.player.getMainHandStack().getItem() instanceof TridentItem || mc.player.getMainHandStack().getItem() instanceof AxeItem)) {
            return getGoldenAppleType();
        }
        return itemConfig.getValue().getItem();
    }

    private Item getGoldenAppleType() {
        if (crappleConfig.getValue()
                && mc.player.hasStatusEffect(StatusEffects.ABSORPTION)
                && InventoryUtil.hasItemInInventory(Items.GOLDEN_APPLE, true)) {
            return Items.GOLDEN_APPLE;
        }
        return Items.ENCHANTED_GOLDEN_APPLE;
    }

    private enum OffhandItem {
        TOTEM(Items.TOTEM_OF_UNDYING),
        GAPPLE(Items.ENCHANTED_GOLDEN_APPLE),
        CRYSTAL(Items.END_CRYSTAL);

        private final Item item;

        OffhandItem(Item item) {
            this.item = item;
        }

        public Item getItem() {
            return item;
        }
    }
}
