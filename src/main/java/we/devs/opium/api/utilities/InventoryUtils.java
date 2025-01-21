package we.devs.opium.api.utilities;

import net.minecraft.block.BlockState;
import net.minecraft.item.Items;
import we.devs.opium.Opium;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.c2s.play.UpdateSelectedSlotC2SPacket;
import net.minecraft.screen.slot.SlotActionType;

public class InventoryUtils implements IMinecraft {
    public static int getTargetSlot(String input) {
        int obsidianSlot = InventoryUtils.findBlock(Blocks.OBSIDIAN, 0, 9);
        int chestSlot = InventoryUtils.findBlock(Blocks.ENDER_CHEST, 0, 9);
        if (obsidianSlot == -1 && chestSlot == -1) {
            return -1;
        }
        if (obsidianSlot != -1 && chestSlot == -1) {
            return obsidianSlot;
        }
        if (obsidianSlot == -1) {
            return chestSlot;
        }
        if (input.equals("Obsidian")) {
            return obsidianSlot;
        }
        return chestSlot;
    }

    public static void switchSlot(int slot, boolean silent) {
        Opium.PLAYER_MANAGER.setSwitching(true);
        mc.player.networkHandler.sendPacket(new UpdateSelectedSlotC2SPacket(slot));
        if (!silent) {
            mc.player.getInventory().selectedSlot = slot;
        }
        Opium.PLAYER_MANAGER.setSwitching(false);
    }


    public static int findItem(Item item, int minimum, int maximum) {
        for (int i = minimum; i <= maximum; ++i) {
            ItemStack stack = mc.player.getInventory().getStack(i);
            if (stack.getItem() != item) continue;
            return i;
        }
        return -1;
    }

    public static int findBestTool(BlockState block, boolean onlyHotbar) {
        float bestMultiplier = Float.MIN_VALUE;
        int bestSlot = -1;
        for (int i = 0; i < (onlyHotbar ? 9 : 36); i++) {
            ItemStack stack = mc.player.getInventory().getStack(i);
            float mul = stack.getMiningSpeedMultiplier(block);
            if(stack.isSuitableFor(block) && mul > bestMultiplier) {
                bestMultiplier = mul;
                bestSlot = i;
            }
        }
        Opium.LOGGER.info("Best slot: {}", bestSlot);
        return bestSlot;
    }


    public static int getSlotByClass(Class<?> clss) {
        int itemSlot = -1;

        for (int i = 45; i > 0; --i) {
            if (mc.player.getInventory().getStack(i).getItem().getClass() == clss) {
                itemSlot = i;
                break;
            }
        }

        return itemSlot;
    }

    public static ItemStack get(int slot) {
        if (slot == -2) {
            return mc.player.getInventory().getStack(mc.player.getInventory().selectedSlot);
        }
        return mc.player.getInventory().getStack(slot);
    }

    public static int findItem(Item item) {
        for (int i = 9; i < 45; ++i) {
            if (get(i).getItem() != item) continue;
            return i;
        }
        return -1;
    }

    public static void offhandItem(Item item) {
        int slot = findItem(item);
        if (slot != -1) {
            mc.interactionManager.clickSlot(mc.player.currentScreenHandler.syncId, slot, 0, SlotActionType.PICKUP, mc.player);
            mc.interactionManager.clickSlot(mc.player.currentScreenHandler.syncId, 45, 0, SlotActionType.PICKUP, mc.player);
            mc.interactionManager.clickSlot(mc.player.currentScreenHandler.syncId, slot, 0, SlotActionType.PICKUP, mc.player);
            mc.interactionManager.tick();
        }
    }

    public static int findBlock(Block block, int minimum, int maximum) {
        for (int i = minimum; i <= maximum; ++i) {
            BlockItem item;
            ItemStack stack = mc.player.getInventory().getStack(i);
            if (!(stack.getItem() instanceof BlockItem) || (item = (BlockItem) stack.getItem()).getBlock() != block) continue;
            return i;
        }
        return -1;
    }

    public enum ItemModes {
        Obsidian,
        Chest
    }

    public enum SwitchModes {
        Normal,
        Silent,
        Strict
    }
}
