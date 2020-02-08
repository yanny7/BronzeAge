package com.yanny.age.bronze.blocks;

import com.yanny.age.bronze.ExampleMod;
import com.yanny.age.bronze.subscribers.BlockSubscriber;
import com.yanny.age.bronze.subscribers.ContainerSubscriber;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.IIntArray;
import net.minecraft.util.IWorldPosCallable;
import net.minecraft.util.IntArray;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;
import net.minecraftforge.items.wrapper.InvWrapper;

import javax.annotation.Nonnull;

import static com.yanny.age.bronze.blocks.BoostedFurnaceTileEntity.ITEMS;

public class BoostedFurnaceContainer extends Container {
    private final BoostedFurnaceTileEntity tile;
    private final PlayerEntity player;
    private final IItemHandler inventory;
    private final IIntArray data;

    public BoostedFurnaceContainer(int windowId, PlayerInventory inv, PacketBuffer extraData) {
        this(windowId, extraData.readBlockPos(), ExampleMod.proxy.getClientWorld(), inv, ExampleMod.proxy.getClientPlayer(), new IntArray(6));
    }

    public BoostedFurnaceContainer(int id, BlockPos pos, World world, PlayerInventory inventory, PlayerEntity player, IIntArray data) {
        super(ContainerSubscriber.boosted_furnace, id);
        tile = (BoostedFurnaceTileEntity) world.getTileEntity(pos);
        this.player = player;
        this.inventory = new InvWrapper(inventory);
        this.data = data;

        if (tile == null) {
            throw new IllegalStateException("TileEntity does not exists!");
        }

        tile.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).ifPresent(h -> {
            addSlot(new SlotItemHandler(h, 0, 26, 53));
            addSlot(new SlotItemHandler(h, 1, 26, 17));
            addSlot(new SlotItemHandler(h, 2, 134, 17));
            addSlot(new SlotItemHandler(h, 3, 134, 53));
        });

        layoutPlayerInventorySlots(8, 84);

        trackIntArray(data);
    }

    @Override
    public boolean canInteractWith(@Nonnull PlayerEntity playerIn) {
        if (tile == null || tile.getWorld() == null) {
            throw new IllegalStateException("Null pointer");
        }

        return isWithinUsableDistance(IWorldPosCallable.of(tile.getWorld(), tile.getPos()), player, BlockSubscriber.boosted_furnace);
    }

    @Nonnull
    @Override
    public ItemStack transferStackInSlot(PlayerEntity playerIn, int index) {
        Slot slot = inventorySlots.get(index);

        if (slot != null && slot.getHasStack()) {
            ItemStack stack = slot.getStack();
            ItemStack itemstack = stack.copy();

            if (index < ITEMS) {
                if (!mergeItemStack(stack, ITEMS + 1, ITEMS + 36, true)) {
                    return ItemStack.EMPTY;
                }

                slot.onSlotChange(stack, itemstack);
            } else {
                if (tile.isItemValid(stack) && !mergeItemStack(stack, 1, 2, false)) {
                    return ItemStack.EMPTY;
                }
                if (ForgeHooks.getBurnTime(stack) > 0 && !mergeItemStack(stack, 0, 1, false)) {
                    return ItemStack.EMPTY;
                }
            }

            if (stack.isEmpty()) {
                slot.putStack(ItemStack.EMPTY);
            } else {
                slot.onSlotChanged();
            }

            if (stack.getCount() == itemstack.getCount()) {
                return ItemStack.EMPTY;
            }

            slot.onTake(playerIn, stack);
        }

        return ItemStack.EMPTY;
    }

    private int addSlotRange(IItemHandler handler, int index, int x, int y, int amount, int dx) {
        for (int i = 0 ; i < amount ; i++) {
            addSlot(new SlotItemHandler(handler, index, x, y));
            x += dx;
            index++;
        }

        return index;
    }

    @SuppressWarnings("SameParameterValue")
    private void addSlotBox(IItemHandler handler, int index, int x, int y, int horAmount, int dx, int verAmount, int dy) {
        for (int j = 0 ; j < verAmount ; j++) {
            index = addSlotRange(handler, index, x, y, horAmount, dx);
            y += dy;
        }
    }

    @SuppressWarnings("SameParameterValue")
    private void layoutPlayerInventorySlots(int leftCol, int topRow) {
        addSlotBox(inventory, 9, leftCol, topRow, 9, 18, 3, 18);

        // Hotbar offset
        topRow += 58;
        addSlotRange(inventory, 0, leftCol, topRow, 9, 18);
    }

    public int getBurnTimeLeft() {
        return data.get(0);
    }

    public int getBurnTimeTotal() {
        return data.get(1);
    }

    public int getMeltingPoint() {
        return data.get(2);
    }

    public int getTemperature() {
        return data.get(3);
    }

    public int getProcessLeft() {
        return data.get(4);
    }

    public int getChimneyCount() {
        return data.get(5);
    }
}
