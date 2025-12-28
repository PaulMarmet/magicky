package net.pm.magicky.screen;

import java.util.List;
import net.minecraft.util.Util;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.IdMap;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.DataSlot;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.EnchantmentInstance;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.EnchantingTableBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.ChiseledBookShelfBlockEntity;
import net.pm.magicky.enchantment.EnchantingCatalyst;
import net.pm.magicky.enchantment.EnchantmentCatalysts;

public class EnchantmentScreenHandlerM extends AbstractContainerMenu {
    static final Identifier EMPTY_LAPIS_SLOT_TEXTURE = Identifier.withDefaultNamespace("item/empty_slot_lapis_lazuli");
    private final Container inventory;
    private final ContainerLevelAccess context;
    private final DataSlot maxPower;
    public static final int MAX_ENCHANTMENTS_SIZE = 64;
    public final SimpleContainerData enchantmentIds;
    public final SimpleContainerData enchantmentLevels;

    public EnchantmentScreenHandlerM(int syncId, Inventory playerInventory) {
        this(syncId, playerInventory, ContainerLevelAccess.NULL);
    }

    public EnchantmentScreenHandlerM(int syncId, Inventory playerInventory, ContainerLevelAccess context) {
        super(MagickyScreenHandlers.ENCHANTMENT_M, syncId);
        this.inventory = new SimpleContainer(2) {
            public void setChanged() {
                super.setChanged();
                EnchantmentScreenHandlerM.this.slotsChanged(this);
            }
        };
        this.maxPower = DataSlot.standalone();
        this.context = context;
        //this needs a size...
        this.enchantmentIds = new SimpleContainerData(MAX_ENCHANTMENTS_SIZE);
        this.enchantmentLevels = new SimpleContainerData(MAX_ENCHANTMENTS_SIZE);
        for (int i = 0; i < MAX_ENCHANTMENTS_SIZE; i++) {
            this.enchantmentIds.set(i, -1);
            this.enchantmentLevels.set(i, -1);
        }

        //Primary Slot
        this.addSlot(new Slot(this.inventory, 0, 15, 47) {
            public int getMaxStackSize() {
                return 1;
            }
        });
        //Secondary Slot
        this.addSlot(new Slot(this.inventory, 1, 35, 47) {
            public boolean mayPlace(ItemStack stack) {
                return EnchantmentCatalysts.catalysts.get(stack.getItem()) != null || stack.isEnchanted();
            }

            public Identifier getNoItemIcon() {
                return EnchantmentScreenHandlerM.EMPTY_LAPIS_SLOT_TEXTURE;
            }
        });
        //Tertiary Slot
//        this.addSlot(new Slot(this.inventory, 2, 25, 27) {});

        int i;
        for(i = 0; i < 3; ++i) {
            for(int j = 0; j < 9; ++j) {
                this.addSlot(new Slot(playerInventory, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
            }
        }

        for(i = 0; i < 9; ++i) {
            this.addSlot(new Slot(playerInventory, i, 8 + i * 18, 142));
        }

        //this.addProperty(this.seed).set(playerInventory.player.getEnchantmentTableSeed());
        this.addDataSlots(enchantmentIds);
        this.addDataSlots(enchantmentLevels);
    }

    public Holder<Enchantment> getEnchantment(Level world, int index) {
        if (index > this.getEnchantmentCount()) return null;
        return world.registryAccess().lookupOrThrow(Registries.ENCHANTMENT).get(this.enchantmentIds.get(index)).orElse(null);
    }
    public int getEnchantmentLevel(int index) {
        if (index > this.getEnchantmentCount()) return -1;
        return this.enchantmentLevels.get(index);
    }

    public int getEnchantmentCount() {
        for (int i = 0; i < MAX_ENCHANTMENTS_SIZE; i++) {
            if (this.enchantmentIds.get(i) <= -1) return i;
        }
        return MAX_ENCHANTMENTS_SIZE;
    }

    private void clearEnchantments() {
        for (int i = 0; i < MAX_ENCHANTMENTS_SIZE; i++) {
            this.enchantmentIds.set(i, -1);
            this.enchantmentLevels.set(i, -1);
        }
    }

    private void setEnchantments(Level world, List<EnchantmentInstance> enchantmentList) {
        IdMap<Holder<Enchantment>> indexedIterable = world.registryAccess().lookupOrThrow(Registries.ENCHANTMENT).asHolderIdMap();
        for (int i = 0; i < Math.min(enchantmentList.size(), MAX_ENCHANTMENTS_SIZE); i++) {
            this.enchantmentIds.set(i, indexedIterable.getId(enchantmentList.get(i).enchantment()));
            this.enchantmentLevels.set(i, enchantmentList.get(i).level());
        }
    }

    public EnchantingCatalyst getCatType() {
        ItemStack catalyst = inventory.getItem(1);
        return catalyst.isEnchanted() ? new EnchantmentCatalysts.EnchantedCatalyst() : EnchantmentCatalysts.catalysts.get(catalyst.getItem());
    }

    public void slotsChanged(Container inventory) {
        if (inventory == this.inventory) {
            this.clearEnchantments();

            ItemStack itemStack = inventory.getItem(0);
            /*if the item is enchantable, not if the itemStack is enchantable
              this makes it so that an item with enchants can still be enchanted*/
            if (!itemStack.isEmpty() && itemStack.isEnchantable() /*&& !catStack.isEmpty()*/) {
                this.context.execute((world, pos) -> {
                    //get the table's enchant power
                    this.maxPower.set(getMagicPower(world, pos));
                    EnchantingCatalyst catType = getCatType();

                    //get all possible enchantments based off of catalyst
                    List<EnchantmentInstance> enchantmentList;
                    if(catType != null) {
                        enchantmentList = catType.getAllEnchantments(this, world, pos);
                    } else {
                        return;
                    }
                    //trim to possible enchants only
                    enchantmentList = getPossibleEnchantments(enchantmentList, itemStack);

                    this.setEnchantments(world, enchantmentList);
                    this.broadcastChanges();
                    this.broadcastFullState();
                });
            }
        }

    }

    public int getMagicPower(Level world, BlockPos pos) {
        float magicPower = 0;

        for (BlockPos blockPos : EnchantingTableBlock.BOOKSHELF_OFFSETS) {
            if (EnchantingTableBlock.isValidBookShelf(world, pos, blockPos)) {
                BlockEntity blockEntity = world.getBlockEntity(blockPos);
                if (!(blockEntity instanceof ChiseledBookShelfBlockEntity)) magicPower++;
                else {
                    for (int i = 0; i < ((ChiseledBookShelfBlockEntity)blockEntity).getContainerSize(); i++) {
                        if (!((ChiseledBookShelfBlockEntity)blockEntity).getItem(i).isEmpty()) {
                            magicPower += ((ChiseledBookShelfBlockEntity)blockEntity).getItem(i).isEnchanted() ? 2f/3 : 1f/3;
                        }
                    }
                }
            }
        }
        return (int) magicPower;
    }

    public List<EnchantmentInstance> getPossibleEnchantments(List<EnchantmentInstance> available, ItemStack itemStack) {
        return available.stream().filter((enchantment) -> {
            //item supports the enchantment
            if (!enchantment.enchantment().value().isSupportedItem(itemStack) && !getCatType().canApplyIncompatible()) return false;
            //next lowest level unless level step bypass is true
            if (EnchantmentHelper.getItemEnchantmentLevel(enchantment.enchantment(), itemStack)+1 != enchantment.level() && !getCatType().canSkipLevels()) return false;
            //no conflicting enchantments
            for (Holder<Enchantment> itemEnchant : itemStack.getEnchantments().keySet()) if (!Enchantment.areCompatible(itemEnchant, enchantment.enchantment()) && itemEnchant != enchantment.enchantment()) return false;

            return true;
        }).toList();
    }

    public boolean clickMenuButton(Player player, int id) {
        //if valid id in list
        if (id >= 0 && id < this.getEnchantmentCount()) {
            //get stacks
            ItemStack itemStack = this.inventory.getItem(0);
            ItemStack catStack = this.inventory.getItem(1);
            int level = this.enchantmentLevels.get(id);
            EnchantingCatalyst catType = getCatType();
            if (catType == null) return false;
            //catalyst & xp cost
            int catCost = catType.catCost(level, this.getEnchantment(player.level(), id));
            int xpCost = catType.xpCost(level, this.getEnchantment(player.level(), id));
            //if not enough catalyst or xp
            if ((catStack.getCount() < catCost || player.experienceLevel < xpCost) && !player.hasInfiniteMaterials()) {
                return false;
            }
            //if invalid item
            if (itemStack.isEmpty()) {
                return false;
            }
            this.context.execute((world, pos) -> {
                //new itemStack
                ItemStack newStack = itemStack;
                Holder<Enchantment> enchantment = this.getEnchantment(world, id);
                if (enchantment != null && level > 0) {
                    player.onEnchantmentPerformed(newStack, catCost);

                    //the enchant function
                    newStack = catType.enchant(this.inventory, newStack, enchantment, level);

                    catStack.consume(catCost, player);
                    if (catStack.isEmpty()) {
                        this.inventory.setItem(1, ItemStack.EMPTY);
                    }

                    player.awardStat(Stats.ENCHANT_ITEM);
                    if (player instanceof ServerPlayer) {
                        CriteriaTriggers.ENCHANTED_ITEM.trigger((ServerPlayer)player, newStack, xpCost);
                    }

                    this.inventory.setChanged();
                    //this.seed.set(player.getEnchantmentTableSeed());
                    world.playSound(null, pos, SoundEvents.ENCHANTMENT_TABLE_USE, SoundSource.BLOCKS, 1.0F, world.random.nextFloat() * 0.1F + 0.9F);
                }
            });
            return true;
        } else {
            String var10000 = String.valueOf(player.getName());
            Util.logAndPauseIfInIde(var10000 + " pressed invalid button id: " + id);
            return false;
        }
    }

    public int getCatalystCount() {
        ItemStack itemStack = this.inventory.getItem(1);
        return itemStack.isEmpty() ? 0 : itemStack.getCount();
    }

    public void removed(Player player) {
        super.removed(player);
        this.context.execute((world, pos) -> {
            this.clearContainer(player, this.inventory);
        });
    }

    public boolean stillValid(Player player) {
        return stillValid(this.context, player, Blocks.ENCHANTING_TABLE);
    }

    public ItemStack quickMoveStack(Player player, int slot) {
        ItemStack itemStack = ItemStack.EMPTY;
        Slot slot2 = this.slots.get(slot);
        if (slot2.hasItem()) {
            ItemStack itemStack2 = slot2.getItem();
            itemStack = itemStack2.copy();
            if (slot == 0) {
                if (!this.moveItemStackTo(itemStack2, 2, 38, true)) {
                    return ItemStack.EMPTY;
                }
            } else if (slot == 1) {
                if (!this.moveItemStackTo(itemStack2, 2, 38, true)) {
                    return ItemStack.EMPTY;
                }
            } else if (EnchantmentCatalysts.catalysts.get(itemStack2.getItem()) != null || (this.slots.getFirst()).hasItem() && (this.slots.get(1)).mayPlace(itemStack2)) {
                if (!this.moveItemStackTo(itemStack2, 1, 2, true)) {
                    return ItemStack.EMPTY;
                }
            } else {
                if ((this.slots.getFirst()).hasItem() || !(this.slots.getFirst()).mayPlace(itemStack2)) {
                    return ItemStack.EMPTY;
                }

                ItemStack itemStack3 = itemStack2.copyWithCount(1);
                itemStack2.shrink(1);
                (this.slots.getFirst()).setByPlayer(itemStack3);
            }

            if (itemStack2.isEmpty()) {
                slot2.setByPlayer(ItemStack.EMPTY);
            } else {
                slot2.setChanged();
            }

            if (itemStack2.getCount() == itemStack.getCount()) {
                return ItemStack.EMPTY;
            }

            slot2.onTake(player, itemStack2);
        }

        return itemStack;
    }

}
