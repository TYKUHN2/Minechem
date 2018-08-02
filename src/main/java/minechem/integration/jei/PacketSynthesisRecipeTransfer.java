package minechem.integration.jei;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

/**
 * @author p455w0rd
 *
 */
public class PacketSynthesisRecipeTransfer implements IMessage, IMessageHandler<PacketSynthesisRecipeTransfer, IMessage> {

	public Map<Integer, Integer> recipeMap;
	public List<Integer> craftingSlots;
	public List<Integer> inventorySlots;
	private boolean maxTransfer;
	private boolean requireCompleteSets;

	public PacketSynthesisRecipeTransfer() {

	}

	public PacketSynthesisRecipeTransfer(Map<Integer, Integer> recipeMap, List<Integer> craftingSlots, List<Integer> inventorySlots, boolean maxTransfer, boolean requireCompleteSets) {
		this.recipeMap = recipeMap;
		this.craftingSlots = craftingSlots;
		this.inventorySlots = inventorySlots;
		this.maxTransfer = maxTransfer;
		this.requireCompleteSets = requireCompleteSets;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		int recipeMapSize = buf.readInt();
		recipeMap = new HashMap<>(recipeMapSize);
		for (int i = 0; i < recipeMapSize; i++) {
			int slotIndex = buf.readInt();
			int recipeItem = buf.readInt();
			recipeMap.put(slotIndex, recipeItem);
		}

		int craftingSlotsSize = buf.readInt();
		craftingSlots = new ArrayList<>(craftingSlotsSize);
		for (int i = 0; i < craftingSlotsSize; i++) {
			int slotIndex = buf.readInt();
			craftingSlots.add(slotIndex);
		}

		int inventorySlotsSize = buf.readInt();
		inventorySlots = new ArrayList<>(inventorySlotsSize);
		for (int i = 0; i < inventorySlotsSize; i++) {
			int slotIndex = buf.readInt();
			inventorySlots.add(slotIndex);
		}
		maxTransfer = buf.readBoolean();
		requireCompleteSets = buf.readBoolean();
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeInt(recipeMap.size());
		for (Map.Entry<Integer, Integer> recipeMapEntry : recipeMap.entrySet()) {
			buf.writeInt(recipeMapEntry.getKey());
			buf.writeInt(recipeMapEntry.getValue());
		}

		buf.writeInt(craftingSlots.size());
		for (Integer craftingSlot : craftingSlots) {
			buf.writeInt(craftingSlot);
		}

		buf.writeInt(inventorySlots.size());
		for (Integer inventorySlot : inventorySlots) {
			buf.writeInt(inventorySlot);
		}

		buf.writeBoolean(maxTransfer);
		buf.writeBoolean(requireCompleteSets);
	}

	@Override
	public IMessage onMessage(PacketSynthesisRecipeTransfer message, MessageContext ctx) {
		SynthesisTransferHandlerServer.setItems(ctx.getServerHandler().player, message.recipeMap, message.craftingSlots, message.inventorySlots, message.maxTransfer, message.requireCompleteSets);
		return null;
	}

	//SynthesisTransferHandlerServer.setItems(player, recipeMap, craftingSlots, inventorySlots, maxTransfer, requireCompleteSets);

	public static class SynthesisTransferHandlerServer {
		private SynthesisTransferHandlerServer() {
		}

		/**
		 * Called server-side to actually put the items in place.
		 */
		public static void setItems(EntityPlayer player, Map<Integer, Integer> slotIdMap, List<Integer> craftingSlots, List<Integer> inventorySlots, boolean maxTransfer, boolean requireCompleteSets) {
			Container container = player.openContainer;

			// grab items from slots
			Map<Integer, ItemStack> slotMap = new HashMap<>(slotIdMap.size());
			for (Map.Entry<Integer, Integer> entry : slotIdMap.entrySet()) {
				Slot slot = container.getSlot(entry.getValue());
				final ItemStack slotStack = slot.getStack();
				if (slotStack.isEmpty()) {
					return;
				}
				ItemStack stack = slotStack.copy();
				//stack.setCount(1);
				slotMap.put(entry.getKey(), stack);
			}

			// Transfer as many items as possible only if it has been explicitly requested by the implementation
			// and a max-transfer operation has been requested by the player.
			boolean transferAsCompleteSets = requireCompleteSets || !maxTransfer;

			Map<Integer, ItemStack> toTransfer = removeItemsFromInventory(container, slotMap, craftingSlots, inventorySlots, transferAsCompleteSets, maxTransfer);

			if (toTransfer.isEmpty()) {
				return;
			}

			// clear the crafting grid
			List<ItemStack> clearedCraftingItems = new ArrayList<>();
			for (Integer craftingSlotNumber : craftingSlots) {
				Slot craftingSlot = container.getSlot(craftingSlotNumber);
				if (craftingSlot.getHasStack()) {
					ItemStack craftingItem = craftingSlot.decrStackSize(Integer.MAX_VALUE);
					clearedCraftingItems.add(craftingItem);
				}
			}

			// put items into the crafting grid
			for (Map.Entry<Integer, ItemStack> entry : toTransfer.entrySet()) {
				Integer craftNumber = entry.getKey();
				Integer slotNumber = craftingSlots.get(craftNumber);
				Slot slot = container.getSlot(slotNumber);

				ItemStack stack = entry.getValue();
				stack.setCount(stack.getCount());
				if (slot.isItemValid(stack)) {
					slot.putStack(stack);
				}
				else {
					clearedCraftingItems.add(stack);
				}
			}

			// put cleared items back into the inventory
			for (ItemStack oldCraftingItem : clearedCraftingItems) {
				int added = addStack(container, inventorySlots, oldCraftingItem);
				if (added < oldCraftingItem.getCount()) {
					if (!player.inventory.addItemStackToInventory(oldCraftingItem)) {
						player.dropItem(oldCraftingItem, false);
					}
				}
			}

			container.detectAndSendChanges();
		}

		@Nonnull
		private static Map<Integer, ItemStack> removeItemsFromInventory(Container container, Map<Integer, ItemStack> required, List<Integer> craftingSlots, List<Integer> inventorySlots, boolean transferAsCompleteSets, boolean maxTransfer) {

			// This map becomes populated with the resulting items to transfer and is returned by this method.
			final Map<Integer, ItemStack> result = new HashMap<>(required.size());

			loopSets: while (true) { // for each set

				// This map holds the original contents of a slot we have removed items from. This is used if we don't
				// have enough items to complete a whole set, we can roll back the items that were removed.
				Map<Slot, ItemStack> originalSlotContents = null;

				if (transferAsCompleteSets) {
					// We only need to create a new map for each set iteration if we're transferring as complete sets.
					originalSlotContents = new HashMap<>();
				}

				// This map holds items found for each set iteration. Its contents are added to the result map
				// after each complete set iteration. If we are transferring as complete sets, this allows
				// us to simply ignore the map's contents when a complete set isn't found.
				final Map<Integer, ItemStack> foundItemsInSet = new HashMap<>(required.size());

				// This flag is set to false if at least one item is found during the set iteration. It is used
				// to determine if iteration should continue and prevents an infinite loop if not transferring
				// as complete sets.
				boolean noItemsFound = true;

				for (Map.Entry<Integer, ItemStack> entry : required.entrySet()) { // for each item in set
					final ItemStack requiredStack = entry.getValue().copy();

					// Locate a slot that has what we need.
					final Slot slot = getSlotWithStack(container, requiredStack, craftingSlots, inventorySlots);

					boolean itemFound = (slot != null) && !slot.getStack().isEmpty();
					ItemStack resultItemStack = result.get(entry.getKey());
					boolean resultItemStackLimitReached = (resultItemStack != null) && (resultItemStack.getCount() == resultItemStack.getMaxStackSize());

					if (!itemFound || resultItemStackLimitReached) {
						// We can't find any more items to fulfill the requirements or the maximum stack size for this item
						// has been reached.

						if (transferAsCompleteSets) {
							// Since the full set requirement wasn't satisfied, we need to roll back any
							// slot changes we've made during this set iteration.
							for (Map.Entry<Slot, ItemStack> slotEntry : originalSlotContents.entrySet()) {
								ItemStack stack = slotEntry.getValue();
								slotEntry.getKey().putStack(stack);
							}
							break loopSets;
						}

					}
					else { // the item was found and the stack limit has not been reached

						// Keep a copy of the slot's original contents in case we need to roll back.
						if (originalSlotContents != null && !originalSlotContents.containsKey(slot)) {
							originalSlotContents.put(slot, slot.getStack().copy());
						}

						// Reduce the size of the found slot.
						ItemStack removedItemStack = slot.decrStackSize(1);
						foundItemsInSet.put(entry.getKey(), removedItemStack);

						noItemsFound = false;
					}
				}

				// Merge the contents of the temporary map with the result map.
				for (Map.Entry<Integer, ItemStack> entry : foundItemsInSet.entrySet()) {
					ItemStack resultItemStack = result.get(entry.getKey());

					if (resultItemStack == null) {
						result.put(entry.getKey(), entry.getValue());

					}
					else {
						resultItemStack.grow(1);
					}
				}

				if (!maxTransfer || noItemsFound) {
					// If max transfer is not requested by the player this will exit the loop after trying one set.
					// If no items were found during this iteration, we're done.
					break;
				}
			}

			return result;
		}

		@Nullable
		private static Slot getSlotWithStack(Container container, ItemStack stack, List<Integer> craftingSlots, List<Integer> inventorySlots) {
			Slot slot = getSlotWithStack(container, craftingSlots, stack);
			if (slot == null) {
				slot = getSlotWithStack(container, inventorySlots, stack);
			}

			return slot;
		}

		private static int addStack(Container container, Collection<Integer> slotIndexes, ItemStack stack) {
			int added = 0;
			// Add to existing stacks first
			for (final Integer slotIndex : slotIndexes) {
				if (slotIndex >= 0 && slotIndex < container.inventorySlots.size()) {
					final Slot slot = container.getSlot(slotIndex);
					final ItemStack inventoryStack = slot.getStack();
					// Check that the slot's contents are stackable with this stack
					if (!inventoryStack.isEmpty() && inventoryStack.isStackable() && inventoryStack.isItemEqual(stack) && ItemStack.areItemStackTagsEqual(inventoryStack, stack)) {

						final int remain = stack.getCount() - added;
						final int maxStackSize = Math.min(slot.getItemStackLimit(inventoryStack), inventoryStack.getMaxStackSize());
						final int space = maxStackSize - inventoryStack.getCount();
						if (space > 0) {

							// Enough space
							if (space >= remain) {
								inventoryStack.grow(remain);
								return stack.getCount();
							}

							// Not enough space
							inventoryStack.setCount(inventoryStack.getMaxStackSize());

							added += space;
						}
					}
				}
			}

			if (added >= stack.getCount()) {
				return added;
			}

			for (final Integer slotIndex : slotIndexes) {
				if (slotIndex >= 0 && slotIndex < container.inventorySlots.size()) {
					final Slot slot = container.getSlot(slotIndex);
					final ItemStack inventoryStack = slot.getStack();
					if (inventoryStack.isEmpty()) {
						ItemStack stackToAdd = stack.copy();
						stackToAdd.setCount(stack.getCount() - added);
						slot.putStack(stackToAdd);
						return stack.getCount();
					}
				}
			}

			return added;
		}

		/**
		 * Get the slot which contains a specific itemStack.
		 *
		 * @param container   the container to search
		 * @param slotNumbers the slots in the container to search
		 * @param itemStack   the itemStack to find
		 * @return the slot that contains the itemStack. returns null if no slot contains the itemStack.
		 */
		@Nullable
		private static Slot getSlotWithStack(Container container, Iterable<Integer> slotNumbers, ItemStack itemStack) {
			for (Integer slotNumber : slotNumbers) {
				if (slotNumber >= 0 && slotNumber < container.inventorySlots.size()) {
					Slot slot = container.getSlot(slotNumber);
					ItemStack slotStack = slot.getStack();
					if (ItemStack.areItemsEqual(itemStack, slotStack) && ItemStack.areItemStackTagsEqual(itemStack, slotStack)) {
						return slot;
					}
				}
			}
			return null;
		}
	}

}