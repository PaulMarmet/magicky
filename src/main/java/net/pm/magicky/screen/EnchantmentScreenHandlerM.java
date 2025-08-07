package net.pm.magicky.screen;

import com.mojang.datafixers.util.Pair;

import java.util.List;

import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.block.Blocks;
import net.minecraft.block.EnchantingTableBlock;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.EnchantmentLevelEntry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.screen.*;
import net.minecraft.screen.slot.Slot;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.collection.IndexedIterable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.pm.magicky.enchantment.EnchantingCatalyst;
import net.pm.magicky.enchantment.EnchantmentCatalysts;

public class EnchantmentScreenHandlerM extends ScreenHandler {
    static final Identifier EMPTY_LAPIS_SLOT_TEXTURE = Identifier.ofVanilla("item/empty_slot_lapis_lazulii");
    private final Inventory inventory;
    private final ScreenHandlerContext context;
    private final Property maxPower;
    public static final int MAX_ENCHANTMENTS_SIZE = 64;
    public final ArrayPropertyDelegate enchantmentIds;
    public final ArrayPropertyDelegate enchantmentLevels;

    public EnchantmentScreenHandlerM(int syncId, PlayerInventory playerInventory) {
        this(syncId, playerInventory, ScreenHandlerContext.EMPTY);
    }

    public EnchantmentScreenHandlerM(int syncId, PlayerInventory playerInventory, ScreenHandlerContext context) {
        super(MagickyScreenHandlers.ENCHANTMENT_M, syncId);
        this.inventory = new SimpleInventory(2) {
            public void markDirty() {
                super.markDirty();
                EnchantmentScreenHandlerM.this.onContentChanged(this);
            }
        };
        this.maxPower = Property.create();
        this.context = context;
        //this needs a size...
        this.enchantmentIds = new ArrayPropertyDelegate(MAX_ENCHANTMENTS_SIZE);
        this.enchantmentLevels = new ArrayPropertyDelegate(MAX_ENCHANTMENTS_SIZE);
        for (int i = 0; i < MAX_ENCHANTMENTS_SIZE; i++) {
            this.enchantmentIds.set(i, -1);
            this.enchantmentLevels.set(i, -1);
        }

        //Primary Slot
        this.addSlot(new Slot(this.inventory, 0, 15, 47) {
            public int getMaxItemCount() {
                return 1;
            }
        });
        //Secondary Slot
        this.addSlot(new Slot(this.inventory, 1, 35, 47) {
            public boolean canInsert(ItemStack stack) {
                return EnchantmentCatalysts.catalysts.get(stack.getItem()) != null || stack.hasEnchantments();
            }

            public Pair<Identifier, Identifier> getBackgroundSprite() {
                return Pair.of(PlayerScreenHandler.BLOCK_ATLAS_TEXTURE, EnchantmentScreenHandlerM.EMPTY_LAPIS_SLOT_TEXTURE);
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
        this.addProperties(enchantmentIds);
        this.addProperties(enchantmentLevels);
    }

    public RegistryEntry<Enchantment> getEnchantment(World world, int index) {
        if (index > this.getEnchantmentCount()) return null;
        return world.getRegistryManager().get(RegistryKeys.ENCHANTMENT).getEntry(this.enchantmentIds.get(index)).orElse(null);
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

    private void setEnchantments(World world, List<EnchantmentLevelEntry> enchantmentList) {
        IndexedIterable<RegistryEntry<Enchantment>> indexedIterable = world.getRegistryManager().get(RegistryKeys.ENCHANTMENT).getIndexedEntries();
        for (int i = 0; i < Math.min(enchantmentList.size(), MAX_ENCHANTMENTS_SIZE); i++) {
            this.enchantmentIds.set(i, indexedIterable.getRawId(enchantmentList.get(i).enchantment));
            this.enchantmentLevels.set(i, enchantmentList.get(i).level);
        }
    }

    public EnchantingCatalyst getCatType() {
        ItemStack catalyst = inventory.getStack(1);
        return catalyst.hasEnchantments() ? new EnchantmentCatalysts.EnchantedCatalyst() : EnchantmentCatalysts.catalysts.get(catalyst.getItem());
    }

    public void onContentChanged(Inventory inventory) {
        if (inventory == this.inventory) {
            this.clearEnchantments();

            ItemStack itemStack = inventory.getStack(0);
            /*if the item is enchantable, not if the itemStack is enchantable
              this makes it so that an item with enchants can still be enchanted*/
            if (!itemStack.isEmpty() && itemStack.getItem().isEnchantable(itemStack) /*&& !catStack.isEmpty()*/) {
                this.context.run((world, pos) -> {
                    //get the table's enchant power
                    this.maxPower.set(getMagicPower(world, pos));
                    EnchantingCatalyst catType = getCatType();

                    //get all possible enchantments based off of catalyst
                    List<EnchantmentLevelEntry> enchantmentList;
                    if(catType != null) {
                        enchantmentList = catType.getAllEnchantments(this, world, pos);
                    } else {
                        return;
                    }
                    //trim to possible enchants only
                    enchantmentList = getPossibleEnchantments(enchantmentList, itemStack);

                    this.setEnchantments(world, enchantmentList);
                    this.sendContentUpdates();
                    this.updateToClient();
                });
            }
        }

    }

    public int getMagicPower(World world, BlockPos pos) {
        int magicPower = 0;

        for (BlockPos blockPos : EnchantingTableBlock.POWER_PROVIDER_OFFSETS) {
            if (EnchantingTableBlock.canAccessPowerProvider(world, pos, blockPos)) {
                ++magicPower;
            }
        }
        return magicPower;
    }

    public List<EnchantmentLevelEntry> getPossibleEnchantments(List<EnchantmentLevelEntry> available, ItemStack itemStack) {
        return available.stream().filter((enchantment) -> {
            //item supports the enchantment
            if (!enchantment.enchantment.value().isSupportedItem(itemStack)) return false;
            //next lowest level unless level step bypass is true
            if (EnchantmentHelper.getLevel(enchantment.enchantment, itemStack)+1 != enchantment.level && !getCatType().canSkipLevels()) return false;
            //no conflicting enchantments
            for (RegistryEntry<Enchantment> itemEnchant : itemStack.getEnchantments().getEnchantments()) if (!Enchantment.canBeCombined(itemEnchant, enchantment.enchantment) && itemEnchant != enchantment.enchantment) return false;

            return true;
        }).toList();
    }

    public boolean onButtonClick(PlayerEntity player, int id) {
        //if valid id in list
        if (id >= 0 && id < this.getEnchantmentCount()) {
            //get stacks
            ItemStack itemStack = this.inventory.getStack(0);
            ItemStack catStack = this.inventory.getStack(1);
            int level = this.enchantmentLevels.get(id);
            EnchantingCatalyst catType = getCatType();
            if (catType == null) return false;
            //catalyst & xp cost
            int catCost = catType.catCost(level, this.getEnchantment(player.getWorld(), id));
            int xpCost = catType.xpCost(level, this.getEnchantment(player.getWorld(), id));
            //if not enough catalyst or xp
            if ((catStack.getCount() < catCost || player.experienceLevel < xpCost) && !player.isInCreativeMode()) {
                return false;
            }
            //if invalid item
            if (itemStack.isEmpty()) {
                return false;
            }
            this.context.run((world, pos) -> {
                //new itemStack
                ItemStack newStack = itemStack;
                RegistryEntry<Enchantment> enchantment = this.getEnchantment(world, id);
                if (enchantment != null && level > 0) {
                    player.applyEnchantmentCosts(newStack, catCost);

                    //the enchant function
                    newStack = catType.enchant(this.inventory, newStack, enchantment, level);

                    catStack.decrementUnlessCreative(catCost, player);
                    if (catStack.isEmpty()) {
                        this.inventory.setStack(1, ItemStack.EMPTY);
                    }

                    player.incrementStat(Stats.ENCHANT_ITEM);
                    if (player instanceof ServerPlayerEntity) {
                        Criteria.ENCHANTED_ITEM.trigger((ServerPlayerEntity)player, newStack, xpCost);
                    }

                    this.inventory.markDirty();
                    //this.seed.set(player.getEnchantmentTableSeed());
                    world.playSound(null, pos, SoundEvents.BLOCK_ENCHANTMENT_TABLE_USE, SoundCategory.BLOCKS, 1.0F, world.random.nextFloat() * 0.1F + 0.9F);
                }
            });
            return true;
        } else {
            String var10000 = String.valueOf(player.getName());
            Util.error(var10000 + " pressed invalid button id: " + id);
            return false;
        }
    }

    public int getCatalystCount() {
        ItemStack itemStack = this.inventory.getStack(1);
        return itemStack.isEmpty() ? 0 : itemStack.getCount();
    }

    public void onClosed(PlayerEntity player) {
        super.onClosed(player);
        this.context.run((world, pos) -> {
            this.dropInventory(player, this.inventory);
        });
    }

    public boolean canUse(PlayerEntity player) {
        return canUse(this.context, player, Blocks.ENCHANTING_TABLE);
    }

    public ItemStack quickMove(PlayerEntity player, int slot) {
        ItemStack itemStack = ItemStack.EMPTY;
        Slot slot2 = this.slots.get(slot);
        if (slot2.hasStack()) {
            ItemStack itemStack2 = slot2.getStack();
            itemStack = itemStack2.copy();
            if (slot == 0) {
                if (!this.insertItem(itemStack2, 2, 38, true)) {
                    return ItemStack.EMPTY;
                }
            } else if (slot == 1) {
                if (!this.insertItem(itemStack2, 2, 38, true)) {
                    return ItemStack.EMPTY;
                }
            } else if (EnchantmentCatalysts.catalysts.get(itemStack2.getItem()) != null || (this.slots.getFirst()).hasStack() && (this.slots.get(1)).canInsert(itemStack2)) {
                if (!this.insertItem(itemStack2, 1, 2, true)) {
                    return ItemStack.EMPTY;
                }
            } else {
                if ((this.slots.getFirst()).hasStack() || !(this.slots.getFirst()).canInsert(itemStack2)) {
                    return ItemStack.EMPTY;
                }

                ItemStack itemStack3 = itemStack2.copyWithCount(1);
                itemStack2.decrement(1);
                (this.slots.getFirst()).setStack(itemStack3);
            }

            if (itemStack2.isEmpty()) {
                slot2.setStack(ItemStack.EMPTY);
            } else {
                slot2.markDirty();
            }

            if (itemStack2.getCount() == itemStack.getCount()) {
                return ItemStack.EMPTY;
            }

            slot2.onTakeItem(player, itemStack2);
        }

        return itemStack;
    }

}
