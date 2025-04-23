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
import net.minecraft.component.ComponentType;
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
import net.minecraft.registry.entry.RegistryEntryList;
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
import net.minecraft.util.math.random.Random;
import net.pm.magicky.Magicky;

public class EnchantmentScreenHandlerM extends ScreenHandler {
    static final Identifier EMPTY_LAPIS_SLOT_TEXTURE = Identifier.ofVanilla("item/empty_slot_lapis_lazulii");
    private final Inventory inventory;
    private final ScreenHandlerContext context;
    private final Random random;
    private final Property seed;
    public final int[] enchantmentPower;
    public final int[] enchantmentId;
    public final int[] enchantmentLevel;

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
        this.random = Random.create();
        this.seed = Property.create();
        this.enchantmentPower = new int[3];
        this.enchantmentId = new int[]{-1, -1, -1};
        this.enchantmentLevel = new int[]{-1, -1, -1};
        this.context = context;
        //Primary Slot
        this.addSlot(new Slot(this.inventory, 0, 15, 47) {
            public int getMaxItemCount() {
                return 1;
            }
        });
        //Secondary Slot
        this.addSlot(new Slot(this.inventory, 1, 35, 47) {
            public boolean canInsert(ItemStack stack) {
                return stack.isOf(Items.LAPIS_LAZULI);
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

        this.addProperty(Property.create(this.enchantmentPower, 0));
        this.addProperty(Property.create(this.enchantmentPower, 1));
        this.addProperty(Property.create(this.enchantmentPower, 2));
        this.addProperty(this.seed).set(playerInventory.player.getEnchantmentTableSeed());
        this.addProperty(Property.create(this.enchantmentId, 0));
        this.addProperty(Property.create(this.enchantmentId, 1));
        this.addProperty(Property.create(this.enchantmentId, 2));
        this.addProperty(Property.create(this.enchantmentLevel, 0));
        this.addProperty(Property.create(this.enchantmentLevel, 1));
        this.addProperty(Property.create(this.enchantmentLevel, 2));
    }

    public void onContentChanged(Inventory inventory) {
        if (inventory == this.inventory) {
            ItemStack itemStack = inventory.getStack(0);
            /*if the item is enchantable, not if the itemStack is enchantable
              this makes it so that an item with enchants can still be enchanted*/
            if (!itemStack.isEmpty() && itemStack.getItem().isEnchantable(itemStack)) {
                this.context.run((world, pos) -> {
                    //get the table's enchant power
                    IndexedIterable<RegistryEntry<Enchantment>> indexedIterable = world.getRegistryManager().get(RegistryKeys.ENCHANTMENT).getIndexedEntries();
                    int enchantPower = 0;

                    for (BlockPos blockPos : EnchantingTableBlock.POWER_PROVIDER_OFFSETS) {
                        if (EnchantingTableBlock.canAccessPowerProvider(world, pos, blockPos)) {
                            ++enchantPower;
                        }
                    }
                    Magicky.LOGGER.info("Power:"+enchantPower);

                    //get enchants in chiseledBookshelves
                    List<EnchantmentLevelEntry> enchantList = new ArrayList<>();
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
                                    if (!enchantList.contains(entry)) enchantList.add(entry);
                                }
                                //same with stored enchantements
                                for (RegistryEntry<Enchantment> enchantment : blockEntity.getStack(i).getComponents().getOrDefault(DataComponentTypes.STORED_ENCHANTMENTS, ItemEnchantmentsComponent.DEFAULT).getEnchantments()) {
                                    EnchantmentLevelEntry entry = new EnchantmentLevelEntry(enchantment, blockEntity.getStack(i).getComponents().getOrDefault(DataComponentTypes.STORED_ENCHANTMENTS, ItemEnchantmentsComponent.DEFAULT).getLevel(enchantment));
                                    //add only new ones
                                    if (!enchantList.contains(entry)) enchantList.add(entry);
                                }
                            }
                        }
                    }
                    //trim to possible enchants only
                    enchantList = enchantList.stream().filter((enchantment) -> {
                        //item supports the enchantment
                        if (!enchantment.enchantment.value().isSupportedItem(itemStack)) return false;
                        //next lowest level
                        if (EnchantmentHelper.getLevel(enchantment.enchantment, itemStack)+1 != enchantment.level) return false;
                        //no conflicting enchantments
                        for (RegistryEntry<Enchantment> itemEnchant : itemStack.getEnchantments().getEnchantments()) if (!Enchantment.canBeCombined(itemEnchant, enchantment.enchantment) && itemEnchant != enchantment.enchantment) return false;

                        return true;
                    }).toList();

                    StringBuilder names = new StringBuilder();
                    for (EnchantmentLevelEntry enchant : enchantList) names.append(enchant.enchantment.value()).append(" ").append(enchant.level).append(", ");
                    Magicky.LOGGER.info("Enchants:"+names);

                    //get the random enchantments w/ random costs
                    this.random.setSeed((long)this.seed.get());

                    int j;
                    for(j = 0; j < 3; ++j) {
                        this.enchantmentPower[j] = EnchantmentHelper.calculateRequiredExperienceLevel(this.random, j, enchantPower, itemStack);
                        this.enchantmentId[j] = -1;
                        this.enchantmentLevel[j] = -1;
                        if (this.enchantmentPower[j] < j + 1) {
                            this.enchantmentPower[j] = 0;
                        }
                    }

                    for(j = 0; j < 3; ++j) {
                        if (this.enchantmentPower[j] > 0) {
                            List<EnchantmentLevelEntry> list = this.generateEnchantments(world.getRegistryManager(), itemStack, j, this.enchantmentPower[j]);
                            if (list != null && !list.isEmpty()) {
                                EnchantmentLevelEntry enchantmentLevelEntry = (EnchantmentLevelEntry)list.get(this.random.nextInt(list.size()));
                                this.enchantmentId[j] = indexedIterable.getRawId(enchantmentLevelEntry.enchantment);
                                this.enchantmentLevel[j] = enchantmentLevelEntry.level;
                            }
                        }
                    }

                    this.sendContentUpdates();
                });
            } else {
                for(int i = 0; i < 3; ++i) {
                    this.enchantmentPower[i] = 0;
                    this.enchantmentId[i] = -1;
                    this.enchantmentLevel[i] = -1;
                }
            }
        }

    }

    public boolean onButtonClick(PlayerEntity player, int id) {
        if (id >= 0 && id < this.enchantmentPower.length) {
            ItemStack itemStack = this.inventory.getStack(0);
            ItemStack itemStack2 = this.inventory.getStack(1);
            int i = id + 1;
            if ((itemStack2.isEmpty() || itemStack2.getCount() < i) && !player.isInCreativeMode()) {
                return false;
            } else if (this.enchantmentPower[id] <= 0 || itemStack.isEmpty() || (player.experienceLevel < i || player.experienceLevel < this.enchantmentPower[id]) && !player.getAbilities().creativeMode) {
                return false;
            } else {
                this.context.run((world, pos) -> {
                    ItemStack itemStack3 = itemStack;
                    List<EnchantmentLevelEntry> list = this.generateEnchantments(world.getRegistryManager(), itemStack3, id, this.enchantmentPower[id]);
                    if (!list.isEmpty()) {
                        player.applyEnchantmentCosts(itemStack3, i);
                        if (itemStack3.isOf(Items.BOOK)) {
                            itemStack3 = itemStack.withItem(Items.ENCHANTED_BOOK);
                            this.inventory.setStack(0, itemStack3);
                        }

                        Iterator var10 = list.iterator();

                        while(var10.hasNext()) {
                            EnchantmentLevelEntry enchantmentLevelEntry = (EnchantmentLevelEntry)var10.next();
                            itemStack3.addEnchantment(enchantmentLevelEntry.enchantment, enchantmentLevelEntry.level);
                        }

                        itemStack2.decrementUnlessCreative(i, player);
                        if (itemStack2.isEmpty()) {
                            this.inventory.setStack(1, ItemStack.EMPTY);
                        }

                        player.incrementStat(Stats.ENCHANT_ITEM);
                        if (player instanceof ServerPlayerEntity) {
                            Criteria.ENCHANTED_ITEM.trigger((ServerPlayerEntity)player, itemStack3, i);
                        }

                        this.inventory.markDirty();
                        this.seed.set(player.getEnchantmentTableSeed());
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
        this.random.setSeed((long)(this.seed.get() + slot));
        Optional<RegistryEntryList.Named<Enchantment>> optional = registryManager.get(RegistryKeys.ENCHANTMENT).getEntryList(EnchantmentTags.IN_ENCHANTING_TABLE);
        if (optional.isEmpty()) {
            return List.of();
        } else {
            List<EnchantmentLevelEntry> list = EnchantmentHelper.generateEnchantments(this.random, stack, level, ((RegistryEntryList.Named)optional.get()).stream());
            if (stack.isOf(Items.BOOK) && list.size() > 1) {
                list.remove(this.random.nextInt(list.size()));
            }

            return list;
        }
    }

    public int getLapisCount() {
        ItemStack itemStack = this.inventory.getStack(1);
        return itemStack.isEmpty() ? 0 : itemStack.getCount();
    }

    public int getSeed() {
        return this.seed.get();
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
            } else if (itemStack2.isOf(Items.LAPIS_LAZULI)) {
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
