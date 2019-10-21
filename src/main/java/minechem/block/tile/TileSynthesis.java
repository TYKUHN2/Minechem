package minechem.block.tile;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.apache.commons.lang3.tuple.Pair;

import minechem.api.recipe.ISynthesisRecipe;
import minechem.init.ModConfig;
import minechem.init.ModItems;
import minechem.network.message.MessageSyncSynthesisMachine;
import minechem.recipe.handler.RecipeHandlerSynthesis;
import minechem.utils.*;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.util.RecipeItemHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.*;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.NonNullList;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.wrapper.SidedInvWrapper;
import net.minecraftforge.oredict.OreDictionary;

public class TileSynthesis extends TileMinechemEnergyBase implements ISidedInventory {

	public static final int SLOT_ID_CHEMISTS_JOURNAL = 0;
	public static final int SLOT_ID_OUTPUT_JOURNAL = 1;
	public static final int SLOT_ID_OUTPUT_MATRIX = 2;
	//@formatter:off
	public static final int[] SLOT_IDS_MATRIX = { 3,4,5,6,7,8,9,10,11 };
	public static final int[] SLOT_IDS_STORAGE_BUFFER = {12,13,14,15,16,17,18,19,20,21,22,23,24,25,26,27,28,29,30};

	//private final NonNullList<ItemStack> cachedRecipeStacks =NonNullList.withSize(9, ItemStack.EMPTY);
	//@formatter:on
	private int timer = 0;

	public TileSynthesis() {
		super(ModConfig.maxSynthesizerStorage);
		inventory = NonNullList.<ItemStack>withSize(31, ItemStack.EMPTY);
	}

	@Override
	public boolean hasCapability(@Nonnull final Capability<?> capability, @Nullable final EnumFacing facing) {
		return capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY || super.hasCapability(capability, facing);
	}

	@Override
	@Nullable
	public <T> T getCapability(@Nonnull final Capability<T> capability, @Nullable final EnumFacing facing) {
		if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
			return CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.cast(new SidedInvWrapper(this, facing));
		}
		return super.getCapability(capability, facing);
	}

	@Override
	public ItemStack decrStackSize(final int slot, final int amount) {

		if (slot == SLOT_ID_CHEMISTS_JOURNAL) {
			onTakeJournal();
		}
		if (slot >= SLOT_IDS_MATRIX[0] && slot <= SLOT_IDS_MATRIX[8]) {
			//checkRecipe();
		}
		if (!inventory.get(slot).isEmpty()) {
			ItemStack itemstack = ItemStack.EMPTY;
			if (slot == SLOT_ID_OUTPUT_MATRIX) {
				return super.decrStackSize(slot, amount);
			}
			else if (inventory.get(slot).getCount() <= amount) {
				itemstack = inventory.get(slot);
				inventory.set(slot, ItemStack.EMPTY);
				return itemstack;
			}
			else {
				itemstack = inventory.get(slot).splitStack(amount);
				if (inventory.get(slot).getCount() == 0) {
					inventory.set(slot, ItemStack.EMPTY);
				}
				return itemstack;
			}
		}
		return ItemStack.EMPTY;
	}

	@Override
	public int getSizeInventory() {
		return 31;
	}

	@Override
	public boolean isUsableByPlayer(final EntityPlayer entityPlayer) {
		return true;
	}

	@Override
	public void openInventory(final EntityPlayer player) {

	}

	@Override
	public void closeInventory(final EntityPlayer player) {

	}

	@Override
	public void readFromNBT(final NBTTagCompound nbt) {
		super.readFromNBT(nbt);
		final NBTTagList inventoryTagList = nbt.getTagList("inventory", Constants.NBT.TAG_COMPOUND);
		inventory = MinechemUtil.readTagListToItemStackList(inventoryTagList);
	}

	private void onTakeJournal() {
		//setInventorySlotContents(SLOT_ID_OUTPUT_JOURNAL, ItemStack.EMPTY);
		checkRecipe();
	}

	private void onPutJournal(@Nonnull final ItemStack stack) {
		final ItemStack activeItem = ModItems.journal.getActiveStack(stack);
		if (!activeItem.isEmpty()) {
			final ISynthesisRecipe recipe = RecipeHandlerSynthesis.getRecipeFromOutput(activeItem);
			if (recipe != null) {
				fillMatrixWithRecipe(recipe);
			}
		}
	}

	private void fillMatrixWithRecipe(final ISynthesisRecipe recipe) {
		final NonNullList<ItemStack> molecules = RecipeUtil.getRecipeAsStackList(recipe);
		clearMatrix();
		for (int i = 0; i < molecules.size(); i++) {
			if (molecules.get(i) != null) {
				final ItemStack stack = molecules.get(i);
				if (!stack.isEmpty()) {
					setInventorySlotContents(SLOT_IDS_MATRIX[i], stack);
				}
				else {
					setInventorySlotContents(SLOT_IDS_MATRIX[i], ItemStack.EMPTY);
				}
			}
			else {
				setInventorySlotContents(SLOT_IDS_MATRIX[i], ItemStack.EMPTY);
			}
		}
	}

	private void clearMatrix() {
		for (final int element : SLOT_IDS_MATRIX) {
			inventory.set(element, ItemStack.EMPTY);
		}
	}

	private NonNullList<ItemStack> getMatrixStackList() {
		return getMatrixStackList(false);
	}

	private NonNullList<ItemStack> getMatrixStackList(final boolean excludeEmptyStacks) {
		final NonNullList<ItemStack> matrixStacks = excludeEmptyStacks ? NonNullList.create() : NonNullList.withSize(9, ItemStack.EMPTY);
		for (int i = 0; i < SLOT_IDS_MATRIX.length; i++) {
			final ItemStack stack = getStackInSlot(SLOT_IDS_MATRIX[i]);
			if (!stack.isEmpty()) {
				if (excludeEmptyStacks) {
					matrixStacks.add(stack);
				}
				else {
					matrixStacks.set(i, stack);
				}
			}
		}
		return matrixStacks;
	}

	/*private boolean recipeHasChanged() {
		final NonNullList<ItemStack> matrixStacks = getMatrixStackList();
		for (int i = 0; i < 9; i++) {
			final ItemStack matrixStack = matrixStacks.get(i);
			if (!ItemStack.areItemStacksEqual(matrixStack, cachedRecipeStacks.get(i))) {
				return true;
			}
		}
		return false;
	}

	private void updateCachedRecipeStacks() {
		for (int i = 0; i < 9; i++) {
			cachedRecipeStacks.set(i, inventory.get(SLOT_IDS_MATRIX[i]));
		}
	}*/

	public InventoryCraftingFake getCraftingInv() {
		return new InventoryCraftingFake(getMatrixStackList());
	}

	private void updateRecipeOutput() {
		final ISynthesisRecipe recipe = getCurrentRecipe();
		if (recipe != null) {
			inventory.set(SLOT_ID_OUTPUT_JOURNAL, recipe.getRecipeOutput());
		}
		else {
			inventory.set(SLOT_ID_OUTPUT_JOURNAL, ItemStack.EMPTY);
		}
	}

	public ISynthesisRecipe getCurrentRecipe() {
		return RecipeHandlerSynthesis.findMatchingRecipe(getCraftingInv(), world);
	}

	/*private boolean isCurrentRecipeShaped() {
		return getCurrentRecipe() == null ? false : RecipeHandlerSynthesis.isShaped(getCurrentRecipe());
	}

	private boolean isCurrentRecipeShapeless() {
		return getCurrentRecipe() == null ? false : RecipeHandlerSynthesis.isShapeless(getCurrentRecipe());
	}*/

	private Map<ItemStack, Integer> getRecipeStackCosts() {
		final Map<ItemStack, Integer> returnData = new HashMap<>();;
		final Map<Integer, Pair<ItemStack, Integer>> data = new HashMap<>();
		final NonNullList<ItemStack> matrixList = getMatrixStackList();
		for (int i = 0; i < matrixList.size(); i++) {
			final ItemStack stack = matrixList.get(i);
			int count = stack.getCount();

			final ItemStack tmpStack = stack.copy();
			tmpStack.setCount(1);

			//if (!isStackAdded(data, tmpStack)) {
			if (data.get(i) == null) {
				data.put(i, Pair.of(tmpStack, count));
			}
			else {
				count += data.get(i).getRight();
				data.put(i, Pair.of(tmpStack, count));
			}
		}
		for (int i = 0; i < data.size(); i++) {
			if (!data.get(i).getLeft().isEmpty()) {
				returnData.put(data.get(i).getLeft(), data.get(i).getRight());
			}
		}
		return returnData;
	}

	private Map<ItemStack, Integer> getStorageStackAmounts() {
		final Map<ItemStack, Integer> data = new HashMap<>();
		final NonNullList<ItemStack> storageList = getStorageBuffer(true);
		for (final ItemStack stack : storageList) {
			int count = stack.getCount();
			final ItemStack tmpStack = RadiationUtil.getStackWithoutRadiation(stack.copy());
			tmpStack.setCount(1);

			if (!isStackAdded(data, tmpStack)) {
				data.put(tmpStack, count);
			}
			else {
				count += getCount(data, tmpStack);
				data.put(tmpStack, count);
			}
		}
		return data;
	}

	private int getCount(final Map<ItemStack, Integer> data, final ItemStack scompStack) {
		for (final ItemStack stack : data.keySet()) {
			if (ItemStack.areItemStacksEqual(RadiationUtil.getStackWithoutRadiation(stack), RadiationUtil.getStackWithoutRadiation(scompStack))) {
				return data.get(RadiationUtil.getStackWithoutRadiation(stack));
			}
		}
		return 0;
	}

	private boolean isStackAdded(final Map<ItemStack, Integer> data, final ItemStack scompStack) {
		for (final ItemStack stack : data.keySet()) {

			if (ItemStack.areItemStacksEqual(stack, scompStack)) {
				return true;
			}
		}
		return false;
	}

	private boolean doesStorageContainEnoughToCraft() {
		if (getStorageBuffer(true).size() > 0) {
			final Map<ItemStack, Integer> storageAmounts = getStorageStackAmounts();
			final Map<ItemStack, Integer> recipeCosts = getRecipeStackCosts();
			for (final ItemStack stack : recipeCosts.keySet()) {
				if (!isStackAdded(storageAmounts, stack) || getCount(storageAmounts, stack) < getCount(recipeCosts, stack)) {
					return false;
				}
			}
		}
		return true;
	}

	private NonNullList<ItemStack> getStorageBuffer(final boolean excludeEmptyStacks) {
		final int size = SLOT_IDS_STORAGE_BUFFER.length;
		final NonNullList<ItemStack> stackList = NonNullList.create();
		for (int i = 0; i < size; i++) {
			final ItemStack currentStack = getStackInSlot(SLOT_IDS_STORAGE_BUFFER[i]);
			if (!excludeEmptyStacks) {
				stackList.add(currentStack);
			}
			else if (excludeEmptyStacks && !currentStack.isEmpty()) {
				stackList.add(currentStack);
			}
		}
		return stackList;
	}

	public void checkRecipe() {
		//if (recipeHasChanged()) {
		//updateCachedRecipeStacks();
		updateRecipeOutput();
		//}
	}

	private boolean canMergeRecipeOutputToMatrixOutputSlot() {
		if (getCurrentRecipe() != null) {
			final ItemStack recipeOutput = getCurrentRecipe().getRecipeOutput();
			final ItemStack stackInMatrixOutput = getStackInSlot(TileSynthesis.SLOT_ID_OUTPUT_MATRIX);
			if (stackInMatrixOutput.isEmpty()) {
				return true;
			}
			else {
				return recipeOutput.isItemEqual(stackInMatrixOutput) && stackInMatrixOutput.getCount() + recipeOutput.getCount() <= stackInMatrixOutput.getMaxStackSize();
			}
		}
		return false;
	}

	/*private boolean areValuesAllZero(final Map<ItemStack, Integer> amounts) {
		for (final ItemStack stack : amounts.keySet()) {
			if (amounts.get(stack) != 0) {
				return false;
			}
		}
		return true;
	}*/

	private boolean testAndConsumeCraftingItems(final Map<Integer, ItemStack> undo, final boolean strictDamage) {
		final int keep = 0;
		final InventoryCrafting inventory = getCraftingInv();

		for (int i = 0; i < inventory.getSizeInventory(); i++) {
			final ItemStack stack = inventory.getStackInSlot(i);
			if (!stack.isEmpty()) {
				int count = stack.getCount();
				for (final int element : TileSynthesis.SLOT_IDS_STORAGE_BUFFER) {
					final int slotIdx = element;
					final ItemStack input = getStackInSlot(slotIdx);
					if (!input.isEmpty() && input.getCount() > keep) {
						if (match(stack, input, strictDamage)) {
							int ss = count;
							if (input.getCount() - ss < keep) {
								ss = input.getCount() - keep;
							}
							count -= ss;
							if (!undo.containsKey(slotIdx)) {
								undo.put(slotIdx, input.copy());
							}
							input.splitStack(ss);
							if (input.isEmpty()) {
								setInventorySlotContents(slotIdx, ItemStack.EMPTY);
							}
						}
					}
					if (count == 0) {
						break;
					}
				}
				if (count > 0) {
					return false;
				}
			}
			else {

			}
		}
		return RecipeHandlerSynthesis.findMatchingRecipe(getCraftingInv(), getWorld()) != null;
	}

	private static boolean match(final ItemStack target, final ItemStack input, final boolean strictDamage) {
		if (strictDamage) {
			return OreDictionary.itemMatches(target, input, false);
		}
		else {
			if (input.isEmpty() && !target.isEmpty() || !input.isEmpty() && target.isEmpty()) {
				return false;
			}
			return target.getItem() == input.getItem();
		}
	}

	private void undo(final Map<Integer, ItemStack> undo) {
		for (final Map.Entry<Integer, ItemStack> entry : undo.entrySet()) {
			setInventorySlotContents(entry.getKey(), entry.getValue());
		}
		undo.clear();
	}

	private void tryToCraft() {
		if (doesStorageContainEnoughToCraft() && canMergeRecipeOutputToMatrixOutputSlot() && hasEnoughPower()) {
			final Map<Integer, ItemStack> undo = new HashMap<>();

			if (!testAndConsumeCraftingItems(undo, true)) {
				undo(undo);
				if (!testAndConsumeCraftingItems(undo, false)) {
					undo(undo);
					return;
				}
			}
			else {
				if (ModConfig.powerUseEnabled) {
					final int cost = getCurrentRecipe().getEnergyCost();
					setEnergy(getEnergyStored() - cost);
				}
			}

			final ItemStack matrixOutputStack = getStackInSlot(SLOT_ID_OUTPUT_MATRIX);
			if (matrixOutputStack.isEmpty()) {
				setInventorySlotContents(SLOT_ID_OUTPUT_MATRIX, getCurrentRecipe().getRecipeOutput());
			}
			else {
				matrixOutputStack.grow(1);
			}
		}
	}

	@Override
	public void markDirty() {
		super.markDirty();
		if (getWorld() != null && !getWorld().isRemote) {
			minechem.init.ModNetworking.INSTANCE.sendToAll(new MessageSyncSynthesisMachine(this));
			final IBlockState state = getWorld().getBlockState(pos);
			if (state != null) {
				getWorld().notifyBlockUpdate(pos, state, state, 3);
			}
		}
	}

	@Override
	public void update() {
		super.update();
		tryToCraft();
		if (timer < 10) {
			timer++;
		}
		else {
			timer = 0;
			checkRecipe();
		}
	}

	@Override
	public void setInventorySlotContents(final int slot, @Nonnull final ItemStack stack) {
		inventory.set(slot, stack.copy());
		if (slot == SLOT_ID_CHEMISTS_JOURNAL && !stack.isEmpty()) {
			onPutJournal(stack);
		}
		if (slot >= SLOT_IDS_MATRIX[0] && slot <= SLOT_IDS_MATRIX[8]) {
			checkRecipe();
		}
	}

	@Override
	public NBTTagCompound writeToNBT(final NBTTagCompound nbt) {
		super.writeToNBT(nbt);
		final NBTTagList inventoryTagList = MinechemUtil.writeItemStackListToTagList(inventory);
		nbt.setTag("inventory", inventoryTagList);
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
	public void onDataPacket(final NetworkManager net, final SPacketUpdateTileEntity pkt) {
		readFromNBT(pkt.getNbtCompound());
	}

	@Override
	public String getName() {
		return "container.synthesis";
	}

	public boolean hasEnoughPower() {
		if (!ModConfig.powerUseEnabled) {
			return true;
		}
		if (getCurrentRecipe() != null) {
			return getEnergyStored() >= getCurrentRecipe().getEnergyCost();
		}
		return true;
	}

	@Override
	public int getEnergyRequired() {
		if (getCurrentRecipe() != null && ModConfig.powerUseEnabled) {
			return getCurrentRecipe().getEnergyCost();
		}
		return 0;
	}

	@Override
	public boolean hasCustomName() {
		return false;
	}

	@Override
	public boolean isItemValidForSlot(final int i, final ItemStack itemstack) {
		return true;
	}

	@Override
	public int getField(final int i) {
		return 0;
	}

	@Override
	public void setField(final int i, final int i1) {

	}

	@Override
	public int getFieldCount() {
		return 0;
	}

	@Override
	public void clear() {

	}

	@Override
	public int[] getSlotsForFace(final EnumFacing facing) {
		final int[] slots = new int[SLOT_IDS_STORAGE_BUFFER.length + 1];
		slots[0] = SLOT_ID_OUTPUT_MATRIX;
		for (int i = 1; i < SLOT_IDS_STORAGE_BUFFER.length; i++) {
			slots[i] = SLOT_IDS_STORAGE_BUFFER[i];
		}
		return slots;
	}

	@Override
	public boolean canInsertItem(final int slot, final ItemStack itemstack, final EnumFacing facing) {
		return ModConfig.AllowAutomation && (itemstack.getItem() == ModItems.element || itemstack.getItem() == ModItems.molecule) && slot >= TileSynthesis.SLOT_IDS_STORAGE_BUFFER[0] && slot < TileSynthesis.SLOT_IDS_STORAGE_BUFFER[0] + TileSynthesis.SLOT_IDS_STORAGE_BUFFER.length;
	}

	@Override
	public boolean canExtractItem(final int slot, final ItemStack itemstack, final EnumFacing facing) {
		return ModConfig.AllowAutomation && slot == 2;
	}

	@Override
	public boolean isEmpty() {
		return inventory.isEmpty();
	}

	public static class InventoryCraftingFake extends InventoryCrafting {

		private final NonNullList<ItemStack> invList;

		public InventoryCraftingFake(final NonNullList<ItemStack> invList) {
			super(null, 3, 3);
			this.invList = invList;
		}

		@Override
		public int getSizeInventory() {
			return invList.size();
		}

		@Override
		public boolean isEmpty() {
			for (final ItemStack itemstack : invList) {
				if (!itemstack.isEmpty()) {
					return false;
				}
			}

			return true;
		}

		@Override
		public ItemStack getStackInSlot(final int index) {
			return index >= getSizeInventory() ? ItemStack.EMPTY : (ItemStack) invList.get(index);
		}

		@Override
		public ItemStack removeStackFromSlot(final int index) {
			return ItemStackHelper.getAndRemove(invList, index);
		}

		@Override
		public ItemStack decrStackSize(final int index, final int count) {
			final ItemStack itemstack = ItemStackHelper.getAndSplit(invList, index, count);
			return itemstack;
		}

		@Override
		public void setInventorySlotContents(final int index, final ItemStack stack) {
			invList.set(index, stack);
		}

		@Override
		public void clear() {
			invList.clear();
		}

		@Override
		public void fillStackedContents(final RecipeItemHelper helper) {
			for (final ItemStack itemstack : invList) {
				helper.accountStack(itemstack);
			}
		}
	}

}
