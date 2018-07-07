package minechem.block.tile;

import java.util.ArrayList;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import minechem.api.IDecomposerControl;
import minechem.init.ModConfig;
import minechem.init.ModNetworking;
import minechem.inventory.InventoryBounded;
import minechem.network.message.MessageDecomposerDumpFluid;
import minechem.potion.PotionChemical;
import minechem.recipe.RecipeDecomposer;
import minechem.recipe.RecipeDecomposerFluid;
import minechem.recipe.handler.RecipeHandlerDecomposer;
import minechem.utils.MinechemUtil;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.wrapper.SidedInvWrapper;

public class TileDecomposer extends TileMinechemEnergyBase implements ISidedInventory {

	public static final int[] inputSlots = {
			0
	};

	public static final int[] outputSlots = {
			1,
			2,
			3,
			4,
			5,
			6,
			7,
			8,
			9,
			10,
			11,
			12,
			13,
			14,
			15,
			16,
			17,
			18
	};
	private static final int TANK_CAPACITY = Fluid.BUCKET_VOLUME * 5;
	public FluidTank tank = new DecomposerFluidTank(TANK_CAPACITY);
	private final InventoryBounded inputInventory = new InventoryBounded(this, inputSlots);
	private NonNullList<ItemStack> outputBuffer = NonNullList.<ItemStack>withSize(outputSlots.length, ItemStack.EMPTY);
	private final InventoryBounded outputInventory = new InventoryBounded(this, outputSlots);
	private boolean isCooking = false;

	public TileDecomposer() {
		super(ModConfig.maxDecomposerStorage);
		inventory = NonNullList.<ItemStack>withSize(getSizeInventory(), ItemStack.EMPTY);
		outputBuffer = NonNullList.<ItemStack>withSize(outputSlots.length, ItemStack.EMPTY);
		tank.setTileEntity(this);
	}

	@Override
	public boolean hasCapability(@Nonnull Capability<?> capability, @Nullable EnumFacing facing) {
		return capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY || capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY || super.hasCapability(capability, facing);
	}

	@Override
	@Nullable
	public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing facing) {
		if (capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY) {
			return CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY.cast(tank);
		}
		if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
			return CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.cast(new SidedInvWrapper(this, facing));
		}
		return super.getCapability(capability, facing);
	}

	public boolean isCooking() {
		return isCooking;
	}

	private boolean isOutputBufferEmpty() {
		for (ItemStack stack : outputBuffer) {
			if (!stack.isEmpty()) {
				return false;
			}
		}
		return true;
	}

	private int getCurrentOutputBufferProcessingSlot() {
		int currentSlotBeingProcessed = outputBuffer.size() - 1;
		int lastPossibleSlot = currentSlotBeingProcessed;
		if (!isOutputBufferEmpty()) {
			for (int i = lastPossibleSlot; i >= 0; i--) {
				if (!outputBuffer.get(i).isEmpty()) {
					currentSlotBeingProcessed = i;
					break;
				}
			}
		}
		else {
			currentSlotBeingProcessed = -1;
		}
		return currentSlotBeingProcessed;
	}

	private boolean canStackBeAddedToOutputInventory(ItemStack stack) {
		if (outputInventory.isEmpty()) {
			return true;
		}
		for (int i = 0; i < outputInventory.getSizeInventory(); i++) {
			ItemStack tmpStack = outputInventory.getStackInSlot(i);
			if (outputInventory.getStackInSlot(i).isEmpty() || areStacksEqualIgnoreSize(tmpStack, stack)) {
				if (tmpStack.getCount() < tmpStack.getMaxStackSize()) {
					return true;
				}
			}
		}
		return false;
	}

	private boolean areStacksEqualIgnoreSize(ItemStack stack1, ItemStack stack2) {
		ItemStack tmpStack1 = stack1.copy();
		ItemStack tmpStack2 = stack2.copy();
		tmpStack1.setCount(1);
		tmpStack2.setCount(1);
		boolean areEqual = ItemStack.areItemStacksEqual(tmpStack1, tmpStack2);
		return areEqual;
	}

	private void processInput() {
		if (canDecomposeInput()) {
			RecipeDecomposer recipe = null;
			ItemStack inputStack = inputInventory.getStackInSlot(0);
			if (!inputStack.isEmpty()) {
				recipe = RecipeDecomposer.get(inputStack);
			}
			else if (tank.getFluidAmount() > 0) {
				recipe = RecipeDecomposer.get(tank.getFluid());
			}
			if (recipe != null && useEnergy(getEnergyRequired()) && !isOutputInventoryFull()) {
				ArrayList<PotionChemical> output = recipe.getOutput();
				if (output != null) {
					isCooking = true;
					NonNullList<ItemStack> stacks = MinechemUtil.convertChemicalsIntoItemStacks(getBrokenOutput(output, inputStack.isEmpty() ? 1.0D : getDecompositionMultiplier(inputStack)));
					if (!inputStack.isEmpty()) {
						outputBuffer = stacks;//addToOutputBuffer(stacks);
						inputStack.shrink(1);
						if (inputStack.getCount() <= 0) {
							inputInventory.setInventorySlotContents(0, ItemStack.EMPTY);
						}
					}
					else if (tank.getFluidAmount() > 0) {
						int fluidAmount = ((RecipeDecomposerFluid) recipe).inputFluid.amount;
						if (tank.getFluidAmount() >= fluidAmount) {
							outputBuffer = stacks;
							tank.drain(fluidAmount, true);
						}
					}
					shouldUpdate = true;
				}
			}
		}
	}

	private boolean processOutputBuffer() {
		if (!isOutputBufferEmpty()) {
			int currentSlot = getCurrentOutputBufferProcessingSlot();
			if (currentSlot >= 0) {
				isCooking = true;
				ItemStack currentBufferStack = outputBuffer.get(currentSlot);
				if (canStackBeAddedToOutputInventory(currentBufferStack)) {
					ItemStack outputStack = currentBufferStack.copy();
					outputStack.setCount(1);
					currentBufferStack.shrink(1);
					if (currentBufferStack.getCount() <= 0) {
						currentBufferStack = ItemStack.EMPTY;
					}
					addStackToOutput(outputStack);
				}
			}
			else {
				isCooking = false;
			}
		}
		else {
			isCooking = false;
		}
		shouldUpdate = true;
		return isCooking;
	}

	private boolean canDecomposeInput() {
		if (!hasRequiredEnergy() || isCooking || isOutputInventoryFull()) {
			return false;
		}
		ItemStack inputStack = getStackInSlot(inputSlots[0]);
		RecipeDecomposer recipe = null;
		if (!inputStack.isEmpty()) {
			recipe = RecipeDecomposer.get(inputStack);
		}
		else if (tank.getFluidAmount() > 0) {
			recipe = RecipeDecomposer.get(tank.getFluid());
		}
		if (recipe != null) {
			NonNullList<ItemStack> recipeList = MinechemUtil.convertChemicalsIntoItemStacks(recipe.getOutputAsArray());
			for (ItemStack currentStack : recipeList) {
				for (int i = 0; i < outputInventory.getSizeInventory(); i++) {
					ItemStack tmpStack = outputInventory.getStackInSlot(i);
					if (tmpStack.isEmpty()) {
						return true;
					}
					if (areStacksEqualIgnoreSize(currentStack, tmpStack)) {
						int availSpace = tmpStack.getMaxStackSize() - tmpStack.getCount();
						if ((tmpStack.getMaxStackSize() - tmpStack.getCount()) <= availSpace) {
							return true;
						}
					}
				}
			}
		}
		return false;
	}

	public boolean isOutputInventoryFull() {
		boolean isOutputInvFull = true;
		for (int i = 0; i < outputInventory.getSizeInventory(); i++) {
			ItemStack tmpStack = outputInventory.getStackInSlot(i);
			if (tmpStack.isEmpty()) {
				isOutputInvFull = false;
				break;
			}
			if (tmpStack.getCount() < tmpStack.getMaxStackSize()) {
				isOutputInvFull = false;
				ItemStack currentBufferStack = ItemStack.EMPTY;
				for (int j = outputBuffer.size() - 1; j >= 0; j--) {
					if (!outputBuffer.get(j).isEmpty()) {
						currentBufferStack = outputBuffer.get(j);
						//for (int j = 0; j < outputInventory.getSizeInventory(); j++) {
						if (!currentBufferStack.isEmpty()) {
							if (areStacksEqualIgnoreSize(currentBufferStack, tmpStack)) {
								isOutputInvFull = false;
								break;
							}
						}
					}
				}
				isOutputInvFull = true;
				//}
			}
		}
		return isOutputInvFull;
	}

	private void addStackToOutput(ItemStack stack) {
		for (int i = 0; i < outputInventory.getSizeInventory(); i++) {
			if (outputInventory.getStackInSlot(i).isEmpty()) {
				outputInventory.setInventorySlotContents(i, stack);
				return;
			}
			else if (areStacksEqualIgnoreSize(outputInventory.getStackInSlot(i), stack) && outputInventory.getStackInSlot(i).getCount() < outputInventory.getStackInSlot(i).getMaxStackSize()) {
				outputInventory.getStackInSlot(i).grow(1);
				return;
			}
		}
	}

	@Override
	public void update() {
		super.update();
		if (world.isRemote) {
			return;
		}
		if (getState() != State.jammed && isOutputBufferEmpty()) {
			shouldUpdate = true;
		}
		if (!shouldUpdate) {
			return;
		}
		if (isCooking) {
			processOutputBuffer();
		}
		else {
			processInput();
		}
		IBlockState iblockstate = getWorld().getBlockState(getPos());
		if (iblockstate != null) {
			getWorld().notifyBlockUpdate(pos, iblockstate, iblockstate, 3);
		}
	}

	private boolean hasRequiredEnergy() {
		return getEnergyStored() >= ModConfig.costDecomposition || !ModConfig.powerUseEnabled;
	}

	@Override
	public int[] getSlotsForFace(EnumFacing enumFacing) {
		return enumFacing == EnumFacing.UP ? inputSlots : outputSlots;
	}

	@Override
	public boolean canExtractItem(int i, ItemStack itemstack, EnumFacing j) {
		boolean useableSide = false;
		for (EnumFacing tmpFacing : EnumFacing.HORIZONTALS) {
			if (j.equals(tmpFacing)) {
				useableSide = true;
				break;
			}
		}
		return i > 0 && useableSide;
	}

	@Override
	public boolean canInsertItem(int i, ItemStack itemstack, EnumFacing j) {
		return j == EnumFacing.UP && i == 0 && RecipeDecomposer.get(itemstack) != null && !isOutputInventoryFull();
	}

	public void closeInventory() {

	}

	private double getDecompositionMultiplier(ItemStack stack) {
		if (stack.getItem() instanceof IDecomposerControl) {
			return ((IDecomposerControl) stack.getItem()).getDecomposerMultiplier(stack);
		}
		else if (!stack.hasTagCompound()) {
			return 1.0D;
		}
		else if (stack.getTagCompound().hasKey("damage", 3)) {
			return 1 - (stack.getTagCompound().getInteger("damage")) / 100D;
		}
		else if (stack.getTagCompound().hasKey("broken", 1)) {
			return stack.getTagCompound().getBoolean("broken") ? 0.0D : 1.0D;
		}
		return 1.0D;
	}

	private PotionChemical[] getBrokenOutput(ArrayList<PotionChemical> output, double mult) {
		if (mult == 1) {
			return output.toArray(new PotionChemical[output.size()]);
		}
		if (mult <= 0) {
			return new PotionChemical[0];
		}
		ArrayList<PotionChemical> result = new ArrayList<PotionChemical>();
		for (PotionChemical chemical : output) {
			PotionChemical addChemical = chemical.copy();
			addChemical.amount *= mult;
			result.add(addChemical);
		}
		return result.toArray(new PotionChemical[result.size()]);
	}

	public int[] getAccessibleSlotsFromSide(int var1) {

		if (var1 == 1) {
			return TileDecomposer.inputSlots;
		}

		return TileDecomposer.outputSlots;

	}

	@Override
	public int getSizeInventory() {
		return 19;
	}

	@Nullable
	@Override
	public ItemStack removeStackFromSlot(int i) {
		return ItemStack.EMPTY;
	}

	public int[] getSizeInventorySide(int side) {
		switch (side) {
		case 1:
			return inputSlots;
		default:
			return outputSlots;
		}
	}

	public boolean isFluidValidForDecomposer(Fluid fluid) {
		return RecipeDecomposer.get(new FluidStack(fluid, 1)) != null;
	}

	@Override
	public boolean isItemValidForSlot(int i, ItemStack itemstack) {
		if (i == inputSlots[0]) {
			RecipeDecomposer recipe = RecipeHandlerDecomposer.instance.getRecipe(itemstack);
			if (recipe != null) {
				return true;
			}
		}
		return false;
	}

	@Override
	public int getField(int i) {
		return 0;
	}

	@Override
	public void setField(int i, int i1) {

	}

	@Override
	public int getFieldCount() {
		return 0;
	}

	@Override
	public void clear() {

	}

	@Override
	public int getEnergyRequired() {
		return ModConfig.powerUseEnabled ? ModConfig.costDecomposition : 0;
	}

	@Override
	public void setInventorySlotContents(int slot, ItemStack itemstack) {
		if (slot == TileDecomposer.outputSlots[0]) {
			ItemStack oldStack = inventory.get(TileDecomposer.outputSlots[0]);
			if (!oldStack.isEmpty() && !itemstack.isEmpty() && oldStack.getItemDamage() == itemstack.getItemDamage()) {
				if (oldStack.getItem() == itemstack.getItem()) {
					if (oldStack.getCount() > itemstack.getCount()) {
						decrStackSize(slot, oldStack.getCount() - itemstack.getCount());
					}
				}
			}
		}

		if (!itemstack.isEmpty() && itemstack.getCount() > getInventoryStackLimit()) {
			itemstack.setCount(getInventoryStackLimit());
		}

		inventory.set(slot, itemstack);
	}

	@Override
	public boolean isUsableByPlayer(EntityPlayer entityPlayer) {
		return false;
	}

	@Override
	public void openInventory(EntityPlayer entityPlayer) {

	}

	@Override
	public void closeInventory(EntityPlayer entityPlayer) {

	}

	@Override
	public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newSate) {
		return oldState.getBlock() != newSate.getBlock();
	}

	/*
		public void updateStateHandler() {
			if (state != oldState || oldEnergyStored != getEnergyStored() || tankUpdate) {
				oldState = state;
				oldEnergyStored = getEnergyStored();
				bufferChanged = true;
				tankUpdate = false;
				// Notify minecraft that the inventory items in this machine have changed.
				//DecomposerUpdateMessage message = new DecomposerUpdateMessage(this);
				//MessageHandler.INSTANCE.sendToAllAround(message, new NetworkRegistry.TargetPoint(world.provider.getDimension(), pos.getX(), pos.getY(), pos.getZ(), ModConfig.UpdateRadius));
			}
			IBlockState iblockstate = getWorld().getBlockState(getPos());
			if (iblockstate != null) {
				getWorld().notifyBlockUpdate(pos, iblockstate, iblockstate, 3);
			}
		}
	*/
	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		super.readFromNBT(nbt);
		tank.readFromNBT(nbt);
		NBTTagList inventoryTagList = nbt.getTagList("inventory", Constants.NBT.TAG_COMPOUND);
		NBTTagList buffer = nbt.getTagList("buffer", Constants.NBT.TAG_COMPOUND);
		outputBuffer = MinechemUtil.readTagListToItemStackList(buffer);
		inventory = MinechemUtil.readTagListToItemStackList(inventoryTagList);
		isCooking = nbt.getBoolean("cooking");
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
		super.writeToNBT(nbt);
		tank.writeToNBT(nbt);
		NBTTagList inventoryTagList = MinechemUtil.writeItemStackListToTagList(inventory);
		NBTTagList buffer = MinechemUtil.writeItemStackListToTagList(outputBuffer);
		nbt.setTag("inventory", inventoryTagList);
		nbt.setTag("buffer", buffer);
		nbt.setBoolean("cooking", isCooking);
		return nbt;
	}

	@Override
	public NBTTagCompound getUpdateTag() {
		return writeToNBT(new NBTTagCompound());
	}

	@Override
	@Nullable
	public SPacketUpdateTileEntity getUpdatePacket() {
		return new SPacketUpdateTileEntity(getPos(), 255, getUpdateTag());
	}

	@Override
	public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt) {
		readFromNBT(pkt.getNbtCompound());
	}

	public String getStateString() {
		return "TEMP";//(activeStack.isEmpty() || RecipeDecomposer.get(activeStack) == null) ? "No Recipe" : hasRequiredEnergy() ? "Active" : "No Power";
	}

	public State getState() {
		if (!outputBuffer.isEmpty()) {
			if (isOutputInventoryFull()) {
				return State.jammed;
			}
		}
		if (canDecomposeInput()) {
			if (!ModConfig.powerUseEnabled || getEnergyStored() > ModConfig.costDecomposition) {
				return State.finished;
			}
			return State.idle;
		}
		return State.active;
	}

	public void dumpFluid() {
		tank = new FluidTank(TANK_CAPACITY);
		if (world.isRemote) {
			ModNetworking.INSTANCE.sendToServer(new MessageDecomposerDumpFluid(this));
		}
	}

	public int getTankCapacity() {
		return TANK_CAPACITY;
	}

	@Override
	public String getName() {
		return "container.decomposer";
	}

	@Override
	public boolean isEmpty() {
		for (int i = 0; i < inventory.size(); i++) {
			if (!inventory.get(i).isEmpty()) {
				return false;
			}
		}
		return true;
	}

	@Override
	public boolean hasCustomName() {
		return false;
	}

	public static class DecomposerFluidTank extends FluidTank {

		public DecomposerFluidTank(int capacity) {
			super(capacity);
		}

		@Override
		public boolean canFillFluidType(FluidStack fluid) {
			return RecipeDecomposer.get(fluid) != null;
		}

		@Override
		protected void onContentsChanged() {
			if (tile != null) {
				((TileDecomposer) tile).shouldUpdate = true;
			}
		}

	}

	public enum State {
			idle, active, finished, jammed
	}

}
