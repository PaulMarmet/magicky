package net.pm.magicky.screen;

import com.mojang.datafixers.util.Pair;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.block.Blocks;
import net.minecraft.block.ChiseledBookshelfBlock;
import net.minecraft.block.EnchantingTableBlock;
import net.minecraft.block.entity.ChiseledBookshelfBlockEntity;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ItemEnchantmentsComponent;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.EnchantmentLevelEntry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.EnchantmentTags;
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
import net.pm.magicky.Magicky;
import net.pm.magicky.datagen.MagickyItemTags;

public class EnchantmentScreenHandlerM extends ScreenHandler {
    static final Identifier EMPTY_LAPIS_SLOT_TEXTURE = Identifier.ofVanilla("item/empty_slot_lapis_lazulii");
    private final Inventory inventory;
    private final ScreenHandlerContext context;
    private final Property maxPower;
    private final List<EnchantmentLevelEntry> enchantments;
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
        //this.random = Random.create();
        //this.seed = Property.create();
        this.maxPower = Property.create();
        this.enchantments = new ArrayList<>();
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
                return stack.isIn(MagickyItemTags.ENCHANTING_CATALYST) || stack.hasEnchantments();
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

    public void onContentChanged(Inventory inventory) {
        if (inventory == this.inventory) {
            this.enchantments.clear();

            ItemStack itemStack = inventory.getStack(0);
            ItemStack catStack = inventory.getStack(1);
            /*if the item is enchantable, not if the itemStack is enchantable
              this makes it so that an item with enchants can still be enchanted*/
            if (!itemStack.isEmpty() && itemStack.getItem().isEnchantable(itemStack) && !catStack.isEmpty()) {
                this.context.run((world, pos) -> {
                    //get the table's enchant power
                    IndexedIterable<RegistryEntry<Enchantment>> indexedIterable = world.getRegistryManager().get(RegistryKeys.ENCHANTMENT).getIndexedEntries();

                    this.maxPower.set(getMagicPower(world, pos));

                    //get all possible enchantments based off of catalyst
                    List<EnchantmentLevelEntry> enchantList;
                    if(catStack.hasEnchantments()) {
                        enchantList = getEnchantmentsOn(catStack);
                    } else if(catStack.isIn(MagickyItemTags.INSCRIBING_CATALYST) || catStack.isIn(MagickyItemTags.CLONING_CATALYST)) {
                        enchantList = getBookshelfEnchantments(world, pos);
                    } else {
                        return;
                    }
                    //trim to possible enchants only
                    enchantList = getPossibleEnchantments(enchantList, itemStack);
                    StringBuilder names = new StringBuilder();
                    for (EnchantmentLevelEntry enchant : enchantList) names.append(enchant.enchantment.value()).append(" ").append(enchant.level).append(", ");
                    Magicky.LOGGER.info(" Enchants:"+names);

                    this.enchantments.addAll(enchantList);

                    //get the random enchantments w/ random costs
                    //this.random.setSeed((long)this.seed.get());

                    //enchant slots initialization
//                    int i;
//                    for(i = 0; i < 3; ++i) {
//                        this.enchantmentPower[i] = EnchantmentHelper.calculateRequiredExperienceLevel(this.random, i, maxPower, itemStack);
//                        this.enchantmentId[i] = -1;
//                        this.enchantmentLevel[i] = -1;
//                        if (this.enchantmentPower[i] < i + 1) {
//                            this.enchantmentPower[i] = 0;
//                        }
//                    }
                    EnchantmentLevelEntry enchantment;
                    for (int i = 0; i < MAX_ENCHANTMENTS_SIZE; i++) {
                        enchantment = null;
                        if (i < this.enchantments.size()) enchantment = this.enchantments.get(i);
                        if (enchantment != null) {
                            this.enchantmentIds.set(i, indexedIterable.getRawId(enchantment.enchantment));
                            this.enchantmentLevels.set(i, enchantment.level);
                        } else {
                            this.enchantmentIds.set(i, -1);
                            this.enchantmentLevels.set(i, -1);
                        }
                    }
                    Magicky.LOGGER.info("Total:"+this.getEnchantmentCount());
                    this.sendContentUpdates();
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

    public int getEnchantmentPower(World world, int index) {
        return (this.enchantmentLevels.get(index) > -1) ? ((this.enchantmentLevels.get(index) * (world.getRegistryManager().get(RegistryKeys.ENCHANTMENT).getEntry(this.enchantmentIds.get(index)).get().isIn(EnchantmentTags.TREASURE) ? 4 : 1))*2) - 1 : 0; //maybe remove the *2 -1?
    }

    public List<EnchantmentLevelEntry> getBookshelfEnchantments(World world, BlockPos pos) {
        List<EnchantmentLevelEntry> availableList = new ArrayList<>();
        //TODO: check that bookshelf has line of sight
        //foreach position
        for (BlockPos offsetPos : EnchantingTableBlock.POWER_PROVIDER_OFFSETS) {
            BlockPos blockPos = pos.add(offsetPos);
            //if it's a chiseled bookshelf
            if (world.getBlockState(blockPos).getBlock() instanceof ChiseledBookshelfBlock && world.getBlockEntity(blockPos) instanceof ChiseledBookshelfBlockEntity blockEntity) {
                //all the slots
                for (int i = 0; i < blockEntity.size(); i++) {
                    //for all the enchantments (if any are present)
                    for (RegistryEntry<Enchantment> enchantment : blockEntity.getStack(i).getEnchantments().getEnchantments()) {
                        EnchantmentLevelEntry entry = new EnchantmentLevelEntry(enchantment, blockEntity.getStack(i).getEnchantments().getLevel(enchantment));
                        //add only new ones
                        boolean present = false;
                        for (EnchantmentLevelEntry listEntry : availableList) if (listEntry.enchantment == entry.enchantment && listEntry.level == entry.level) {
                            present = true;
                            break;
                        }
                        if (!present) availableList.add(entry);
                    }
                    //same with stored enchantements
                    for (RegistryEntry<Enchantment> enchantment : blockEntity.getStack(i).getComponents().getOrDefault(DataComponentTypes.STORED_ENCHANTMENTS, ItemEnchantmentsComponent.DEFAULT).getEnchantments()) {
                        EnchantmentLevelEntry entry = new EnchantmentLevelEntry(enchantment, blockEntity.getStack(i).getComponents().getOrDefault(DataComponentTypes.STORED_ENCHANTMENTS, ItemEnchantmentsComponent.DEFAULT).getLevel(enchantment));
                        //add only new ones
                        boolean present = false;
                        for (EnchantmentLevelEntry listEntry : availableList) if (listEntry.enchantment == entry.enchantment && listEntry.level == entry.level) {
                            present = true;
                            break;
                        }
                        if (!present) availableList.add(entry);
                    }
                }
            }
        }
        return availableList;
    }

    public List<EnchantmentLevelEntry> getEnchantmentsOn (ItemStack itemStack) {
        List<EnchantmentLevelEntry> enchantments = new ArrayList<>();
        //for all the enchantments (if any are present)
        for (RegistryEntry<Enchantment> enchantment : itemStack.getEnchantments().getEnchantments()) {
            EnchantmentLevelEntry entry = new EnchantmentLevelEntry(enchantment, itemStack.getEnchantments().getLevel(enchantment));
            //add only new ones
            if (!enchantments.contains(entry)) enchantments.add(entry);
        }
        return enchantments;
    }

    public List<EnchantmentLevelEntry> getPossibleEnchantments(List<EnchantmentLevelEntry> available, ItemStack itemStack) {
        return available.stream().filter((enchantment) -> {
            //item supports the enchantment
            if (!enchantment.enchantment.value().isSupportedItem(itemStack)) return false;
            //next lowest level
            if (EnchantmentHelper.getLevel(enchantment.enchantment, itemStack)+1 != enchantment.level) return false;
            //no conflicting enchantments
            for (RegistryEntry<Enchantment> itemEnchant : itemStack.getEnchantments().getEnchantments()) if (!Enchantment.canBeCombined(itemEnchant, enchantment.enchantment) && itemEnchant != enchantment.enchantment) return false;

            return true;
        }).toList();
    }

    public int getEnchantmentCount() {
        for (int i = 0; i < MAX_ENCHANTMENTS_SIZE; i++) {
            if (this.enchantmentIds.get(i) <= -1) return i;
        }
        return MAX_ENCHANTMENTS_SIZE;
    }

    public EnchantmentLevelEntry getEnchantment(World world, int index) {
        if (index > this.getEnchantmentCount()) return null;
        Optional<RegistryEntry.Reference<Enchantment>> enchantment = world.getRegistryManager().get(RegistryKeys.ENCHANTMENT).getEntry(this.enchantmentIds.get(index));
        return enchantment.map(enchantmentReference -> new EnchantmentLevelEntry(enchantmentReference, this.enchantmentLevels.get(index))).orElse(null);
    }

    public boolean onButtonClick(PlayerEntity player, int id) {
        //if valid id in list
        if (id >= 0 && id < this.enchantments.size()) {
            //get stacks
            ItemStack itemStack = this.inventory.getStack(0);
            ItemStack catStack = this.inventory.getStack(1);
            //catalyst cost (double if treasure enchantment)
            int catCost = (this.enchantments.get(id).level) * (this.enchantments.get(id).enchantment.isIn(EnchantmentTags.TREASURE) ? 2 : 1);
            //if not enough catalyst
            if ((catStack.isEmpty() || catStack.getCount() < catCost) && !player.isInCreativeMode()) {
                return false;
            }
            //if not enough xp or invalid item/enchantment
            else if (this.enchantments.get(id).level <= 0 || itemStack.isEmpty() || (player.experienceLevel < catCost || player.experienceLevel < this.enchantments.get(id).level) && !player.getAbilities().creativeMode) {
                return false;
            } else {
                this.context.run((world, pos) -> {
                    //new itemStack
                    ItemStack newStack = itemStack;
                    List<EnchantmentLevelEntry> list = this.generateEnchantments(world.getRegistryManager(), newStack, id, this.enchantments.get(id).level);
                    if (!list.isEmpty()) {
                        player.applyEnchantmentCosts(newStack, catCost);
                        if (newStack.isOf(Items.BOOK)) {
                            newStack = itemStack.withItem(Items.ENCHANTED_BOOK);
                            this.inventory.setStack(0, newStack);
                        }

                        Iterator var10 = list.iterator();

                        while(var10.hasNext()) {
                            EnchantmentLevelEntry enchantmentLevelEntry = (EnchantmentLevelEntry)var10.next();
                            newStack.addEnchantment(enchantmentLevelEntry.enchantment, enchantmentLevelEntry.level);
                        }

                        catStack.decrementUnlessCreative(catCost, player);
                        if (catStack.isEmpty()) {
                            this.inventory.setStack(1, ItemStack.EMPTY);
                        }

                        player.incrementStat(Stats.ENCHANT_ITEM);
                        if (player instanceof ServerPlayerEntity) {
                            Criteria.ENCHANTED_ITEM.trigger((ServerPlayerEntity)player, newStack, catCost);
                        }

                        this.inventory.markDirty();
                        //this.seed.set(player.getEnchantmentTableSeed());
                        this.onContentChanged(this.inventory);
                        world.playSound((PlayerEntity)null, pos, SoundEvents.BLOCK_ENCHANTMENT_TABLE_USE, SoundCategory.BLOCKS, 1.0F, world.random.nextFloat() * 0.1F + 0.9F);
                    }

                });
                return true;
            }
        } else {
            String var10000 = String.valueOf(player.getName());
            Util.error(var10000 + " pressed invalid button id: " + id);
            return false;
        }
    }

    private List<EnchantmentLevelEntry> generateEnchantments(DynamicRegistryManager registryManager, ItemStack stack, int slot, int level) {
//        this.random.setSeed((long)(this.seed.get() + slot));
//        Optional<RegistryEntryList.Named<Enchantment>> optional = registryManager.get(RegistryKeys.ENCHANTMENT).getEntryList(EnchantmentTags.IN_ENCHANTING_TABLE);
//        if (optional.isEmpty()) {
//            return List.of();
//        } else {
//            List<EnchantmentLevelEntry> list = EnchantmentHelper.generateEnchantments(this.random, stack, level, ((RegistryEntryList.Named)optional.get()).stream());
//            if (stack.isOf(Items.BOOK) && list.size() > 1) {
//                list.remove(this.random.nextInt(list.size()));
//            }
//
//            return list;
//        }
        return List.of(this.enchantments.get(slot));
    }

    public int getCatalystCount() {
        ItemStack itemStack = this.inventory.getStack(1);
        return itemStack.isEmpty() ? 0 : itemStack.getCount();
    }

    public int getSeed() {
        return 0;//this.seed.get();
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
        Slot slot2 = (Slot)this.slots.get(slot);
        if (slot2 != null && slot2.hasStack()) {
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
            } else if (itemStack2.isIn(MagickyItemTags.ENCHANTING_CATALYST)) {
                if (!this.insertItem(itemStack2, 1, 2, true)) {
                    return ItemStack.EMPTY;
                }
            } else {
                if (((Slot)this.slots.get(0)).hasStack() || !((Slot)this.slots.get(0)).canInsert(itemStack2)) {
                    return ItemStack.EMPTY;
                }

                ItemStack itemStack3 = itemStack2.copyWithCount(1);
                itemStack2.decrement(1);
                ((Slot)this.slots.get(0)).setStack(itemStack3);
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
