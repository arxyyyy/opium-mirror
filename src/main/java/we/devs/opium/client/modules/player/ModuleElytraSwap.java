package we.devs.opium.client.modules.player;

import net.minecraft.item.ElytraItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Hand;
import we.devs.opium.api.manager.module.Module;

public class ModuleElytraSwap extends Module {

    private boolean inHotbar;

    @Override public void onEnable() {
        Item chestPiece;
        if (!(mc.player.getInventory().getArmorStack(2).getItem() instanceof ElytraItem)) {
            chestPiece = Items.ELYTRA;
        } else {chestPiece = Items.NETHERITE_CHESTPLATE;}

        int elytraSlot = -1;
        for(int i = 0; i <= 44; i++) {
            assert mc.player != null;
            Item item = mc.player.getInventory().getStack(i).getItem();
            if (item == chestPiece || (chestPiece == Items.NETHERITE_CHESTPLATE && item == Items.DIAMOND_CHESTPLATE)) {
                if (i < 9) {
                    inHotbar = true;
                }
                else {
                    inHotbar = false;
                }
                elytraSlot = i;
            }
        }
        if (inHotbar) {
            InventoryManager.setSlot(elytraSlot);
            //final SequencedPacketCreator o = id -> new PlayerInteractItemC2SPacket(Hand.MAIN_HAND, id, mc.player.getYaw(), mc.player.getPitch());
            //NetworkManager.sendSequencedPacket(o);
            mc.interactionManager.interactItem(mc.player, Hand.MAIN_HAND);
            InventoryManager.syncToClient();
        }
        else {
            ItemStack elytraStack = mc.player.getInventory().getArmorStack(2);

            InventoryManager.pickupSlot(elytraSlot);
            boolean rt = !elytraStack.isEmpty();
            InventoryManager.pickupSlot(6);
            if (rt) {
                InventoryManager.pickupSlot(elytraSlot);
            }
        }
        toggle(true);
    }
}

