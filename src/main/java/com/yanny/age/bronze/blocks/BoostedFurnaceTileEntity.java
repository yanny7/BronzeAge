package com.yanny.age.bronze.blocks;

import com.yanny.age.bronze.recipes.BoostedFurnaceRecipe;
import com.yanny.age.bronze.subscribers.TileEntitySubscriber;
import com.yanny.ages.api.utils.ItemStackUtils;
import net.minecraft.block.AbstractFurnaceBlock;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.IIntArray;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.wrapper.RecipeWrapper;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Optional;

public class BoostedFurnaceTileEntity extends TileEntity implements IInventoryInterface, ITickableTileEntity, INamedContainerProvider {
    public static final int ITEMS = 4;
    public static final int MAX_TEMPERATURE_PER_CHIMNEY = 300;
    public static final int MAX_NO_CHIMNEY_TEMPERATURE = 300;
    public static final int MAX_CHIMNEY_COUNT = 3;
    public static final int MELTING_TICKS = 200;

    private final NonNullList<ItemStack> stacks = NonNullList.withSize(ITEMS, ItemStack.EMPTY);
    private final IItemHandlerModifiable nonSidedItemHandler = createNonSidedInventoryHandler(stacks);
    private final LazyOptional<IItemHandlerModifiable> sidedInventoryHandler = LazyOptional.of(() -> createSidedInventoryHandler(stacks));
    private final LazyOptional<IItemHandlerModifiable> nonSidedInventoryHandler = LazyOptional.of(() -> nonSidedItemHandler);
    private final RecipeWrapper inventoryWrapper = new RecipeWrapper(nonSidedItemHandler);
    private final IItemHandlerModifiable tmpItemHandler = new ItemStackHandler(1);
    private final RecipeWrapper tmpItemHandlerWrapper = new RecipeWrapper(tmpItemHandler);
    private final IIntArray data = getData();

    private ItemStack result = ItemStack.EMPTY;
    private int burnTimeLeft = 0;
    private int burnTimeTotal = 0;
    private int meltingPoint = Integer.MAX_VALUE;
    private int temperature = 0;
    private int processLeft = MELTING_TICKS;
    private int chimneyCount = 0;

    private int tick = 0;
    private int roomTemperature = 0;

    public BoostedFurnaceTileEntity() {
        //noinspection ConstantConditions
        super(TileEntitySubscriber.boosted_furnace);
    }

    @Override
    public void tick() {
        assert world != null;

        tick++;
        if (tick % 20 == 0) {
            int oldChimneyCount = chimneyCount;
            chimneyCount = getChimneyCount(world);
            roomTemperature = Math.round((world.getBiome(pos).getTemperatureRaw(pos) - 0.15f) * 20);

            if (oldChimneyCount != chimneyCount && !world.isRemote) {
                world.notifyBlockUpdate(getPos(), getBlockState(), getBlockState(), 3);
            }
        }

        if (burnTimeLeft > 0) {
            burnTimeLeft--;

            if (!world.getBlockState(pos).get(BoostedFurnaceBlock.LIT) && !world.isRemote) {
                world.setBlockState(this.pos, this.world.getBlockState(this.pos).with(AbstractFurnaceBlock.LIT, Boolean.TRUE), 3);
            }
        } else {
            if (!world.isRemote) {
                if (!stacks.get(0).isEmpty()) {
                    ItemStack fuel = stacks.get(0).split(1);

                    burnTimeLeft = burnTimeTotal = ForgeHooks.getBurnTime(fuel);
                    world.setBlockState(this.pos, this.world.getBlockState(this.pos).with(BoostedFurnaceBlock.LIT, Boolean.TRUE), 3);
                    world.notifyBlockUpdate(getPos(), getBlockState(), getBlockState(), 3);
                } else {
                    if (world.getBlockState(pos).get(BoostedFurnaceBlock.LIT)) {
                        world.setBlockState(this.pos, this.world.getBlockState(this.pos).with(AbstractFurnaceBlock.LIT, Boolean.FALSE), 3);
                    }
                }
            }
        }

        if (burnTimeLeft > 0) {
            temperature++;
            temperature = Math.min(temperature, MAX_NO_CHIMNEY_TEMPERATURE + chimneyCount * MAX_TEMPERATURE_PER_CHIMNEY);
        } else {
            temperature--;
            temperature = Math.max(temperature, roomTemperature);
        }

        if (!stacks.get(1).isEmpty()) {
            if (result.isEmpty()) {
                setupFromRecipe(world);
            }
        }

        if (!result.isEmpty()) {
            if (stacks.get(1).isEmpty()) {
                processLeft = MELTING_TICKS;
                result = ItemStack.EMPTY;
                meltingPoint = Integer.MAX_VALUE;
            }

            if (!result.getItem().equals(stacks.get(1).getItem())) {
                setupFromRecipe(world);
            }

            if (processLeft > 0) {
                if (temperature >= meltingPoint) {
                    processLeft--;
                }
            } else {
                if (!world.isRemote) {
                    getRecipe(result).ifPresent(boostedFurnaceRecipe -> {
                        if (canInsertItem(stacks.get(2), boostedFurnaceRecipe.getRecipeOutput()) && canInsertItem(stacks.get(3), boostedFurnaceRecipe.getSecondResult())) {
                            if (stacks.get(2).isEmpty()) {
                                stacks.set(2, boostedFurnaceRecipe.getRecipeOutput().copy());
                            } else {
                                stacks.get(2).grow(boostedFurnaceRecipe.getRecipeOutput().getCount());
                            }

                            if (stacks.get(3).isEmpty()) {
                                stacks.set(3, boostedFurnaceRecipe.getSecondResult().copy());
                            } else {
                                stacks.get(3).grow(boostedFurnaceRecipe.getSecondResult().getCount());
                            }

                            stacks.get(1).shrink(1);

                            meltingPoint = Integer.MAX_VALUE;
                            result = ItemStack.EMPTY;
                            world.notifyBlockUpdate(getPos(), getBlockState(), getBlockState(), 3);
                        }
                    });
                }
            }
        }
    }

    @Override
    public void read(CompoundNBT tag) {
        CompoundNBT invTag = tag.getCompound("inv");
        ItemStackUtils.deserializeStacks(invTag, stacks);
        burnTimeLeft = tag.getInt("burnTimeLeft");
        burnTimeTotal = tag.getInt("burnTimeTotal");
        meltingPoint = tag.getInt("meltingPoint");
        temperature = tag.getInt("temperature");
        processLeft = tag.getInt("processLeft");
        chimneyCount = tag.getInt("chimneyCount");
        result = ItemStack.read(tag.getCompound("result"));
        super.read(tag);
    }

    @Override
    @Nonnull
    public CompoundNBT write(CompoundNBT tag) {
        tag.put("inv", ItemStackUtils.serializeStacks(stacks));
        tag.putInt("burnTimeLeft", burnTimeLeft);
        tag.putInt("burnTimeTotal", burnTimeTotal);
        tag.putInt("meltingPoint", meltingPoint);
        tag.putInt("temperature", temperature);
        tag.putInt("processLeft", processLeft);
        tag.putInt("chimneyCount", chimneyCount);
        CompoundNBT resTag = new CompoundNBT();
        result.write(resTag);
        tag.put("result", resTag);
        return super.write(tag);
    }

    @Override
    public IInventory getInventory() {
        return inventoryWrapper;
    }

    @Override
    @Nonnull
    public ITextComponent getDisplayName() {
        assert getType().getRegistryName() != null;
        return new StringTextComponent(getType().getRegistryName().getPath());
    }

    @Nullable
    @Override
    public Container createMenu(int id, @Nonnull PlayerInventory playerInventory, @Nonnull PlayerEntity playerEntity) {
        assert world != null;
        return new BoostedFurnaceContainer(id, pos, world, playerInventory, playerEntity, data);
    }

    @Nullable
    @Override
    public SUpdateTileEntityPacket getUpdatePacket() {
        return new SUpdateTileEntityPacket(getPos(), getType().hashCode(), getUpdateTag());
    }

    @Nonnull
    @Override
    public CompoundNBT getUpdateTag() {
        return write(new CompoundNBT());
    }

    @Override
    public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket pkt) {
        super.onDataPacket(net, pkt);
        read(pkt.getNbtCompound());
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        if (cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            if (side != null) {
                return sidedInventoryHandler.cast();
            } else {
                return nonSidedInventoryHandler.cast();
            }
        }
        return super.getCapability(cap, side);
    }

    @Override
    public void remove() {
        sidedInventoryHandler.invalidate();
        nonSidedInventoryHandler.invalidate();
        super.remove();
    }

    public boolean isItemValid(ItemStack itemStack) {
        return getRecipe(itemStack).isPresent();
    }

    private void setupFromRecipe(World world) {
        ItemStack itemStack = stacks.get(1);
        Optional<BoostedFurnaceRecipe> recipe = getRecipe(itemStack);

        recipe.ifPresent(boostedFurnaceRecipe -> {
            result = itemStack.copy();
            meltingPoint = boostedFurnaceRecipe.getMeltingPoint();
            processLeft = MELTING_TICKS;

            if (!world.isRemote) {
                world.notifyBlockUpdate(getPos(), getBlockState(), getBlockState(), 3);
            }
        });

        if (!recipe.isPresent()) {
            result = ItemStack.EMPTY;
            meltingPoint = Integer.MAX_VALUE;
            processLeft = MELTING_TICKS;
        }
    }

    private boolean canInsertItem(ItemStack itemStack, ItemStack inserted) {
        if (itemStack.isEmpty() || inserted.isEmpty()) {
            return true;
        }

        return itemStack.getCount() + inserted.getCount() <= itemStack.getMaxStackSize() && itemStack.getItem().equals(inserted.getItem());
    }

    private int getChimneyCount(World world) {
        int count = 0;

        for (int i = 0; i < MAX_CHIMNEY_COUNT; i++) {
            if (world.getBlockState(pos.up(i + 1)).getBlock().equals(Blocks.AIR)) {
                count++;
            } else {
                break;
            }
        }

        return count;
    }

    private IItemHandlerModifiable createNonSidedInventoryHandler(@Nonnull NonNullList<ItemStack> stacks) {
        return new ItemStackHandler(stacks) {
            @Nonnull
            @Override
            public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
                switch (slot) {
                    case 0:
                        if (ForgeHooks.getBurnTime(stack) > 0) {
                            return super.insertItem(slot, stack, simulate);
                        }

                        return stack;
                    case 1:
                        if (BoostedFurnaceTileEntity.this.isItemValid(stack)) {
                            return super.insertItem(slot, stack, simulate);
                        }

                        return stack;
                    default:
                        return stack;
                }
            }

            @Override
            protected void onContentsChanged(int slot) {
                assert world != null;
                markDirty();
                world.notifyBlockUpdate(getPos(), getBlockState(), getBlockState(), 3);
            }
        };
    }

    private IItemHandlerModifiable createSidedInventoryHandler(@Nonnull NonNullList<ItemStack> stacks) {
        return new ItemStackHandler(stacks) {
            @Nonnull
            @Override
            public ItemStack extractItem(int slot, int amount, boolean simulate) {
                switch (slot) {
                    case 2:
                    case 3:
                        return super.extractItem(slot, amount, simulate);
                    default:
                        return ItemStack.EMPTY;
                }
            }

            @Nonnull
            @Override
            public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
                switch (slot) {
                    case 0:
                        if (ForgeHooks.getBurnTime(stack) > 0) {
                            return super.insertItem(slot, stack, simulate);
                        }

                        return stack;
                    case 1:
                        if (BoostedFurnaceTileEntity.this.isItemValid(stack)) {
                            return super.insertItem(slot, stack, simulate);
                        }

                        return stack;
                    default:
                        return stack;
                }
            }

            @Override
            protected void onContentsChanged(int slot) {
                assert world != null;
                markDirty();
                world.notifyBlockUpdate(getPos(), getBlockState(), getBlockState(), 3);
            }
        };
    }

    @Nonnull
    private Optional<BoostedFurnaceRecipe> getRecipe(@Nonnull ItemStack item) {
        assert world != null;
        tmpItemHandler.setStackInSlot(0, item);
        return world.getRecipeManager().getRecipe(BoostedFurnaceRecipe.boosted_furnace, tmpItemHandlerWrapper, world);
    }

    @Nonnull
    private IIntArray getData() {
        return new IIntArray() {
            @Override
            public int get(int index) {
                switch (index) {
                    case 0:
                        return burnTimeLeft;
                    case 1:
                        return burnTimeTotal;
                    case 2:
                        return meltingPoint;
                    case 3:
                        return temperature;
                    case 4:
                        return processLeft;
                    case 5:
                        return chimneyCount;
                    default:
                        throw new IllegalArgumentException("Index out of range");
                }
            }

            @Override
            public void set(int index, int value) {
                switch (index) {
                    case 0:
                        burnTimeLeft = value;
                        break;
                    case 1:
                        burnTimeTotal = value;
                        break;
                    case 2:
                        meltingPoint = value;
                        break;
                    case 3:
                        temperature = value;
                        break;
                    case 4:
                        processLeft = value;
                        break;
                    case 5:
                        chimneyCount = value;
                        break;
                    default:
                        throw new IllegalArgumentException("Index out of range");
                }
            }

            @Override
            public int size() {
                return 6;
            }
        };
    }
}
