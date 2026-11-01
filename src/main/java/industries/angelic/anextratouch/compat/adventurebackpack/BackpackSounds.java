package industries.angelic.anextratouch.compat.adventurebackpack;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.WeakHashMap;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;

import com.darkona.adventurebackpack.block.TileAdventureBackpack;
import com.darkona.adventurebackpack.common.Constants;
import com.darkona.adventurebackpack.common.Constants.Source;
import com.darkona.adventurebackpack.inventory.ContainerAdventure;
import com.darkona.adventurebackpack.inventory.ContainerBackpack;
import com.darkona.adventurebackpack.inventory.ContainerCopter;
import com.darkona.adventurebackpack.inventory.IInventoryTanks;
import com.darkona.adventurebackpack.inventory.SlotFluid;
import com.darkona.adventurebackpack.item.ItemAdventureBackpack;
import com.darkona.adventurebackpack.item.ItemHose;

import industries.angelic.anextratouch.AnExtraTouch;
import industries.angelic.anextratouch.Config;
import industries.angelic.anextratouch.handlers.client.extrasounds.BackpackSoundState;
import industries.angelic.anextratouch.handlers.client.extrasounds.ExtraSounds;
import industries.angelic.anextratouch.mixins.late.adventurebackpack.AccessorBackpackContainer;
import industries.angelic.anextratouch.mixins.late.adventurebackpack.AccessorBackpackScreen;

/** Client intents followed by authoritative inventory/property updates. No extra packets are sent. */
public final class BackpackSounds {

    private static final WeakHashMap<EntityPlayer, BackpackSoundState> wearables = new WeakHashMap<>();
    private static final List<Pending> pending = new ArrayList<>();
    private static World world;
    private static boolean fluidClick;

    public static boolean takeFluidClick() {
        boolean value = fluidClick;
        fluidClick = false;
        return value;
    }

    private BackpackSounds() {}

    public static boolean isWornScreen(GuiScreen screen) {
        return screen instanceof AccessorBackpackScreen
            && ((AccessorBackpackScreen) screen).anextratouch$source() == Source.WEARING;
    }

    public static void tick() {
        Minecraft mc = Minecraft.getMinecraft();
        if (world != mc.theWorld) {
            world = mc.theWorld;
            wearables.clear();
            pending.clear();
        }
        pending.removeIf(p -> !p.valid() || !Config.extraInventoryClicks);
    }

    public static void wearable(int id, NBTTagCompound properties) {
        Minecraft mc = Minecraft.getMinecraft();
        World receivedWorld = mc.theWorld;
        if (receivedWorld == null || properties == null) return;
        Entity target = receivedWorld.getEntityByID(id);
        if (!(target instanceof EntityPlayer)) return;
        EntityPlayer player = (EntityPlayer) target;
        NBTTagCompound snapshot = (NBTTagCompound) properties.copy();
        mc.func_152344_a(() -> {
            if (mc.theWorld != receivedWorld || receivedWorld.getEntityByID(id) != player) return;
            tick();
            ItemStack stack = ItemStack.loadItemStackFromNBT(snapshot.getCompoundTag("wearable"));
            BackpackSoundState state = wearables.get(player);
            if (state == null) {
                state = new BackpackSoundState();
                wearables.put(player, state);
            }
            ItemStack changed = state.changed(stack);
            if (changed != null && Config.armorSoundsEnabled
                && AnExtraTouch.vic.armor.armorSoundEntities.contains(player.getClass())) {
                String kind = changed.getItem() instanceof ItemAdventureBackpack ? "leather" : "iron";
                receivedWorld.playSound(
                    player.posX,
                    player.posY - player.yOffset,
                    player.posZ,
                    "anextratouch:backpack." + kind + (stack == null ? ".unequip" : ".equip"),
                    Config.armorSoundVolume,
                    1,
                    false);
            }
            if (player == mc.thePlayer && stack != null) {
                confirm(Source.WEARING, null, stack.getTagCompound());
            }
        });
    }

    public static void tile(TileAdventureBackpack tile, NBTTagCompound data) {
        Minecraft mc = Minecraft.getMinecraft();
        NBTTagCompound snapshot = (NBTTagCompound) data.copy();
        mc.func_152344_a(() -> { if (tile.getWorldObj() == mc.theWorld) confirm(Source.TILE, tile, snapshot); });
    }

    public static void sent(int window, short transaction) {
        for (Pending p : pending) {
            if (p.valid() && p.container.windowId == window && p.transaction == null) p.transaction = transaction;
        }
    }

    public static void acknowledged(int window, short transaction, boolean accepted) {
        for (Pending p : pending) {
            if (!p.valid() || p.container.windowId != window || p.transaction == null || p.transaction != transaction)
                continue;
            // An accepted click confirms its locally predicted transfer. Rejected predictions
            // wait for corrected slots/NBT instead, and expire if the server made no transfer.
            if (accepted) {
                p.amount = p.predictedAmount;
                p.result(p.predictedOutput);
            }
        }
        finish();
    }

    public static void slots(int window, int start, ItemStack[] stacks) {
        if (pending.isEmpty()) return;
        Minecraft mc = Minecraft.getMinecraft();
        EntityPlayer player = mc.thePlayer;
        if (player == null) return;
        for (Pending p : pending) {
            if (!p.valid()) continue;
            for (int i = 0; i < stacks.length; i++) {
                int index = start + i;
                if (window == p.container.windowId && index == p.output.slotNumber) {
                    p.result(stacks[i]);
                    if (p.result && p.expected != null && p.amount == null) {
                        p.amount = p.transfer.before + p.transfer.direction * p.transfer.minimum;
                    }
                }
                if (p.source != Source.HOLDING) continue;
                boolean parent = window == 0 ? index == 36 + p.heldSlot
                    : window == p.container.windowId && index >= 0
                        && index < p.container.inventorySlots.size()
                        && p.container.getSlot(index).inventory == player.inventory
                        && p.container.getSlot(index)
                            .getSlotIndex() == p.heldSlot;
                if (parent && stacks[i] != null) p.nbt(stacks[i].getTagCompound());
            }
        }
        finish();
    }

    private static void confirm(Source source, IInventoryTanks inventory, NBTTagCompound data) {
        for (Pending p : pending) {
            if (p.valid() && p.source == source && (inventory == null || p.inventory == inventory)) p.nbt(data);
        }
        finish();
    }

    private static void finish() {
        Iterator<Pending> it = pending.iterator();
        while (it.hasNext()) {
            Pending p = it.next();
            if (!p.valid() || !Config.extraInventoryClicks) {
                it.remove();
                continue;
            }
            String cue = p.amount == null ? null : p.transfer.confirm(p.amount, p.result, System.nanoTime());
            if (cue != null) {
                it.remove();
                ExtraSounds.ui(cue);
            }
        }
    }

    public static Click begin(ContainerAdventure container, EntityPlayer player, int slot, int button, int mode) {
        if (player != Minecraft.getMinecraft().thePlayer || !Config.extraInventoryClicks) return null;
        ItemStack input = player.inventory.getItemStack();
        if (mode == 1 && slot >= 0 && slot < container.inventorySlots.size()) input = container.getSlot(slot)
            .getStack();
        if (mode == 2 && button >= 0 && button < 9) input = player.inventory.getStackInSlot(button);
        return new Click(container, player, input);
    }

    public static final class Click {

        private final List<Pending> candidates = new ArrayList<>();

        Click(ContainerAdventure container, EntityPlayer player, ItemStack actionItem) {
            AccessorBackpackContainer access = (AccessorBackpackContainer) container;
            IInventoryTanks inventory = access.anextratouch$inventory();
            List<Slot> fluidSlots = new ArrayList<>();
            for (Object value : container.inventorySlots) if (value instanceof SlotFluid) fluidSlots.add((Slot) value);
            FluidTank[] tanks = inventory.getTanksArray();
            for (int i = 0; i + 1 < fluidSlots.size(); i += 2) {
                Slot in = fluidSlots.get(i), out = fluidSlots.get(i + 1);
                FluidTank tank = tanks[i / 2];
                candidates.add(new Pending(container, player, inventory, i / 2, in, out, tank, actionItem));
            }
        }

        public void finish() {
            for (Pending p : candidates) {
                ItemStack in = p.input.getStack(), out = p.output.getStack();
                FluidTank tank = p.inventory.getTanksArray()[p.tank];
                if (ItemStack.areItemStacksEqual(p.beforeIn, in) && ItemStack.areItemStacksEqual(p.beforeOut, out)
                    && p.beforeAmount == tank.getFluidAmount()) continue;
                boolean discard = out != null && out.getItem() instanceof ItemHose;
                ItemStack item = FluidContainerRegistry.isContainer(in) ? in
                    : FluidContainerRegistry.isContainer(p.beforeIn) ? p.beforeIn : p.actionItem;
                boolean filled = FluidContainerRegistry.isFilledContainer(item);
                if (!discard && !FluidContainerRegistry.isContainer(item)) continue;
                FluidStack fluid = filled ? FluidContainerRegistry.getFluidForFilledItem(item) : p.beforeFluid;
                if (!discard && fluid == null) continue;
                int capacity = discard ? 1 : FluidContainerRegistry.getContainerCapacity(fluid, item);
                if (capacity <= 0) continue;
                p.expected = discard ? null
                    : filled ? FluidContainerRegistry.drainFluidContainer(item)
                        : FluidContainerRegistry.fillFluidContainer(fluid, item);
                p.result = p.expected == null;
                p.transfer = new BackpackSoundState.Transfer(
                    p.beforeAmount,
                    filled ? 1 : -1,
                    capacity,
                    discard,
                    System.nanoTime());
                p.predictedAmount = tank.getFluidAmount();
                p.predictedOutput = copy(out);
                fluidClick = true;
                // NEI may split one input into several window clicks in the same tick.
                if (pending.stream()
                    .anyMatch(q -> q.container == p.container && q.tank == p.tank && q.tick == p.tick)) continue;
                if (pending.size() >= 32) pending.remove(0);
                pending.add(p);
            }
        }
    }

    private static final class Pending {

        final Container container;
        final EntityPlayer player;
        final IInventoryTanks inventory;
        final Source source;
        final int tank, heldSlot, tick, beforeAmount;
        final Slot input, output;
        final ItemStack beforeIn, beforeOut, actionItem;
        final FluidStack beforeFluid;
        BackpackSoundState.Transfer transfer;
        ItemStack expected, predictedOutput;
        int predictedAmount;
        Short transaction;
        Integer amount;
        boolean result;

        Pending(ContainerAdventure container, EntityPlayer player, IInventoryTanks inventory, int tank, Slot input,
            Slot output, FluidTank fluid, ItemStack actionItem) {
            this.container = container;
            this.player = player;
            this.inventory = inventory;
            this.source = ((AccessorBackpackContainer) container).anextratouch$source();
            this.tank = tank;
            this.input = input;
            this.output = output;
            heldSlot = player.inventory.currentItem;
            tick = player.ticksExisted;
            beforeIn = copy(input.getStack());
            beforeOut = copy(output.getStack());
            this.actionItem = copy(actionItem);
            beforeAmount = fluid.getFluidAmount();
            beforeFluid = fluid.getFluid() == null ? null
                : fluid.getFluid()
                    .copy();
        }

        boolean valid() {
            Minecraft mc = Minecraft.getMinecraft();
            return player == mc.thePlayer && player.worldObj == mc.theWorld
                && player.openContainer == container
                && !transfer.expired(System.nanoTime());
        }

        void result(ItemStack stack) {
            if (expected != null && same(expected, stack)) {
                result = stack.stackSize > (same(expected, beforeOut) ? beforeOut.stackSize : 0);
            }
        }

        void nbt(NBTTagCompound data) {
            if (data == null) return;
            NBTTagCompound tag = data.getCompoundTag(Constants.TAG_WEARABLE_COMPOUND);
            String key = container instanceof ContainerBackpack
                ? tank == 0 ? Constants.TAG_LEFT_TANK : Constants.TAG_RIGHT_TANK
                : container instanceof ContainerCopter ? Constants.Copter.TAG_FUEL_TANK
                    : Constants.Jetpack.TAG_WATER_TANK;
            FluidStack fluid = FluidStack.loadFluidStackFromNBT(tag.getCompoundTag(key));
            amount = fluid == null ? 0 : fluid.amount;
            NBTTagList items = tag.getTagList(Constants.TAG_INVENTORY, 10);
            for (int i = 0; i < items.tagCount(); i++) {
                NBTTagCompound item = items.getCompoundTagAt(i);
                if (item.getByte(Constants.TAG_SLOT) == output.getSlotIndex())
                    result(ItemStack.loadItemStackFromNBT(item));
            }
        }
    }

    private static ItemStack copy(ItemStack stack) {
        return stack == null ? null : stack.copy();
    }

    private static boolean same(ItemStack a, ItemStack b) {
        return a != null && b != null && a.isItemEqual(b) && ItemStack.areItemStackTagsEqual(a, b);
    }
}
