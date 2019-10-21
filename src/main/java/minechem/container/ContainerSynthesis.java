package minechem.container;

import java.util.ArrayList;
import java.util.List;

import minechem.api.INoDecay;
import minechem.api.IRadiationShield;
import minechem.block.tile.TileSynthesis;
import minechem.init.ModItems;
import minechem.inventory.slot.*;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class ContainerSynthesis extends ContainerWithFakeSlots implements IRadiationShield, INoDecay {

	private final TileSynthesis synthesis;
	private final InventoryPlayer playerInventory;

	public ContainerSynthesis(final InventoryPlayer playerInventory, final TileSynthesis synthesis) {
		this.synthesis = synthesis;
		this.playerInventory = playerInventory;
		addSlotToContainer(new SlotChemistJournal(synthesis, TileSynthesis.SLOT_ID_CHEMISTS_JOURNAL, 26, 36));
		bindRecipeMatrixSlots();
		addSlotToContainer(new SlotSynthesisOutput(synthesis, TileSynthesis.SLOT_ID_OUTPUT_JOURNAL, 80, 84).setLocked(true));
		addSlotToContainer(new SlotSynthesisOutput(synthesis, TileSynthesis.SLOT_ID_OUTPUT_MATRIX, 133, 36));
		bindStorageSlots();
		bindPlayerInventory(playerInventory);
	}

	private void bindPlayerInventory(final InventoryPlayer inventoryPlayer) {
		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 9; j++) {
				addSlotToContainer(new Slot(inventoryPlayer, j + i * 9 + 9, 8 + j * 18, 160 + i * 18));
			}
		}

		for (int i = 0; i < 9; i++) {
			addSlotToContainer(new Slot(inventoryPlayer, i, 8 + i * 18, 218));
		}
	}

	private void bindRecipeMatrixSlots() {
		int slot = 0;
		for (int row = 0; row < 3; row++) {
			for (int col = 0; col < 3; col++) {
				addSlotToContainer(new SlotFake(synthesis, TileSynthesis.SLOT_IDS_MATRIX[0] + slot, 62 + col * 18, 18 + row * 18) {
					@Override
					public boolean isItemValid(final ItemStack itemstack) {
						return itemstack.getItem() == ModItems.element || itemstack.getItem() == ModItems.molecule;
					}
				});
				slot++;
			}
		}
	}

	private void bindStorageSlots() {
		int slot = 0;
		for (int row = 0; row < 2; row++) {
			for (int col = 0; col < 9; col++) {
				addSlotToContainer(new SlotChemical(synthesis, TileSynthesis.SLOT_IDS_STORAGE_BUFFER[0] + slot, 8 + col * 18, 114 + row * 18));
				slot++;
			}
		}
	}

	@Override
	public boolean canInteractWith(final EntityPlayer var1) {
		return synthesis.isUsableByPlayer(var1);
	}

	@Override
	public List<ItemStack> getPlayerInventory() {
		final List<ItemStack> playerInventoryStacks = new ArrayList<>();
		for (int i = 0; i < playerInventory.getSizeInventory(); i++) {
			playerInventoryStacks.add(playerInventory.getStackInSlot(i));
		}
		return playerInventoryStacks;
	}

	@Override
	public float getRadiationReductionFactor(final int baseDamage, final ItemStack itemstack, final EntityPlayer player) {
		return 0.4F;
	}

	@Override
	public List<ItemStack> getStorageInventory() {
		final List<ItemStack> storageInventory = new ArrayList<>();
		for (final int element : TileSynthesis.SLOT_IDS_STORAGE_BUFFER) {
			final ItemStack stack = getSlot(element).getStack();
			if (!stack.isEmpty()) {
				storageInventory.add(stack);
			}
		}
		return storageInventory;
	}

	@Override
	public ItemStack transferStackInSlot(final EntityPlayer entityPlayer, final int slot) {
		//System.out.println(slot);
		final Slot slotObject = inventorySlots.get(slot);
		if (slotObject != null && slotObject.getHasStack()) {
			System.out.println(slotObject.slotNumber);
			final ItemStack stackInSlot = slotObject.getStack();
			if (slot >= 30 && slot < 66) {
				if (stackInSlot.getItem() == ModItems.journal) {
					if (!mergeItemStack(stackInSlot, 0, 1, false)) {
						return ItemStack.EMPTY;
					}
				}
				if (stackInSlot.getItem() == ModItems.element || stackInSlot.getItem() == ModItems.molecule) {
					if (!mergeItemStack(stackInSlot, 12, 30, false)) {
						return ItemStack.EMPTY;
					}
				}
			}
			else {
				if (slot >= TileSynthesis.SLOT_IDS_STORAGE_BUFFER[0] && slot < TileSynthesis.SLOT_IDS_STORAGE_BUFFER[TileSynthesis.SLOT_IDS_STORAGE_BUFFER.length - 1]) {
					if (!mergeItemStack(stackInSlot, 30, 66, false)) {
						return ItemStack.EMPTY;
					}
				}
				if (slot == TileSynthesis.SLOT_ID_CHEMISTS_JOURNAL) {
					if (!mergeItemStack(stackInSlot, 30, 66, true)) {
						return ItemStack.EMPTY;
					}
				}
				if (slot == 11) {
					if (!mergeItemStack(stackInSlot, 30, 66, false)) {
						return ItemStack.EMPTY;
					}
				}
			}
		}
		return ItemStack.EMPTY;
	}

}
