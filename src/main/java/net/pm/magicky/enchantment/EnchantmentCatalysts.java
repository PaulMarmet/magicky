package net.pm.magicky.enchantment;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.EnchantmentTags;
import net.minecraft.world.Container;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.EnchantmentInstance;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.ChiseledBookShelfBlock;
import net.minecraft.world.level.block.EnchantingTableBlock;
import net.minecraft.world.level.block.entity.ChiseledBookShelfBlockEntity;
import net.pm.magicky.screen.EnchantmentScreenHandlerM;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EnchantmentCatalysts {

    public static Map<Item, EnchantingCatalyst> catalysts = new HashMap<>();

    static {
        catalysts.put(Items.LAPIS_LAZULI, new LapisCatalyst());
        catalysts.put(Items.ECHO_SHARD, new EchoCatalyst());
    }

    public static final class LapisCatalyst implements EnchantingCatalyst {
        @Override
        public boolean canSkipLevels() {return false;}
        @Override
        public boolean canApplyIncompatible() {return false;}
        @Override
        public int catCost(EnchantmentScreenHandlerM handler, int index, Level world) {
            return catCost(handler.enchantmentLevels.get(index), handler.getEnchantment(world, index));
        }
        @Override
        public int xpCost(EnchantmentScreenHandlerM handler, int index, Level world) {
            return xpCost(handler.enchantmentLevels.get(index), handler.getEnchantment(world, index));
        }
        @Override
        public int catCost(int level, Holder<Enchantment> enchantment) {
            return (level > -1 && enchantment != null) ? ((2 * level) - 1) * (enchantment.is(EnchantmentTags.TREASURE) ? 2 : 1) : 0;
        }
        @Override
        public int xpCost(int level, Holder<Enchantment> enchantment) {
            return (level > -1 && enchantment != null) ? level * (enchantment.is(EnchantmentTags.TREASURE) ? 2 : 1) : 0;
        }
        @Override
        public List<EnchantmentInstance> getAllEnchantments(EnchantmentScreenHandlerM handler, Level world, BlockPos pos) {
            List<EnchantmentInstance> availableList = new ArrayList<>();
            //TODO: check that bookshelf has line of sight maybe
            //TODO: add the "basic enchantments" however i end up implementing those
            //foreach position
            for (BlockPos offsetPos : EnchantingTableBlock.BOOKSHELF_OFFSETS) {
                BlockPos blockPos = pos.offset(offsetPos);
                //if it's a chiseled bookshelf
                if (world.getBlockState(blockPos).getBlock() instanceof ChiseledBookShelfBlock && world.getBlockEntity(blockPos) instanceof ChiseledBookShelfBlockEntity blockEntity) {
                    //all the slots
                    for (int i = 0; i < blockEntity.getContainerSize(); i++) {
                        //for all the enchantments (if any are present)
                        for (Holder<Enchantment> enchantment : blockEntity.getItem(i).getEnchantments().keySet()) {
                            EnchantmentInstance entry = new EnchantmentInstance(enchantment, blockEntity.getItem(i).getEnchantments().getLevel(enchantment));
                            //add only new ones
                            boolean present = false;
                            for (EnchantmentInstance listEntry : availableList) if (listEntry.enchantment() == entry.enchantment() && listEntry.level() == entry.level()) {
                                present = true;
                                break;
                            }
                            if (!present) availableList.add(entry);
                        }
                        //same with stored enchantements
                        for (Holder<Enchantment> enchantment : blockEntity.getItem(i).getComponents().getOrDefault(DataComponents.STORED_ENCHANTMENTS, ItemEnchantments.EMPTY).keySet()) {
                            EnchantmentInstance entry = new EnchantmentInstance(enchantment, blockEntity.getItem(i).getComponents().getOrDefault(DataComponents.STORED_ENCHANTMENTS, ItemEnchantments.EMPTY).getLevel(enchantment));
                            //add only new ones
                            boolean present = false;
                            for (EnchantmentInstance listEntry : availableList) if (listEntry.enchantment() == entry.enchantment() && listEntry.level() == entry.level()) {
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

        @Override
        public ItemStack enchant(Container inventory, ItemStack item, Holder<Enchantment> enchantment, int level) {
            if (item.is(Items.BOOK)) {
                item = inventory.getItem(0).transmuteCopy(Items.ENCHANTED_BOOK);
                inventory.setItem(0, item);
            }

            item.enchant(enchantment, level);
            return item;
        }
    }

    public static final class EchoCatalyst implements EnchantingCatalyst {
        @Override
        public boolean canSkipLevels() {return true;}
        @Override
        public boolean canApplyIncompatible() {return true;}
        @Override
        public int catCost(EnchantmentScreenHandlerM handler, int index, Level world) {
            return catCost(handler.enchantmentLevels.get(index), handler.getEnchantment(world, index));
        }
        @Override
        public int xpCost(EnchantmentScreenHandlerM handler, int index, Level world) {
            return xpCost(handler.enchantmentLevels.get(index), handler.getEnchantment(world, index));
        }
        @Override
        public int catCost(int level, Holder<Enchantment> enchantment) {
            return (level > -1 && enchantment != null) ? 1 : 0;
        }
        @Override
        public int xpCost(int level, Holder<Enchantment> enchantment) {
            return (level > -1 && enchantment != null) ? level * (enchantment.is(EnchantmentTags.TREASURE) ? 4 : 2) : 0;
        }
        @Override
        public List<EnchantmentInstance> getAllEnchantments(EnchantmentScreenHandlerM handler, Level world, BlockPos pos) {
            List<EnchantmentInstance> availableList = new ArrayList<>();
            //TODO: check that bookshelf has line of sight maybe
            //TODO: add the "basic enchantments" however i end up implementing those
            //foreach position
            for (BlockPos offsetPos : EnchantingTableBlock.BOOKSHELF_OFFSETS) {
                BlockPos blockPos = pos.offset(offsetPos);
                //if it's a chiseled bookshelf
                if (world.getBlockState(blockPos).getBlock() instanceof ChiseledBookShelfBlock && world.getBlockEntity(blockPos) instanceof ChiseledBookShelfBlockEntity blockEntity) {
                    //all the slots
                    for (int i = 0; i < blockEntity.getContainerSize(); i++) {
                        //for all the enchantments (if any are present)
                        for (Holder<Enchantment> enchantment : blockEntity.getItem(i).getEnchantments().keySet()) {
                            EnchantmentInstance entry = new EnchantmentInstance(enchantment, blockEntity.getItem(i).getEnchantments().getLevel(enchantment));
                            //add only new ones
                            boolean present = false;
                            for (EnchantmentInstance listEntry : availableList) if (listEntry.enchantment() == entry.enchantment() && listEntry.level() == entry.level()) {
                                present = true;
                                break;
                            }
                            if (!present) availableList.add(entry);
                        }
                        //same with stored enchantements
                        for (Holder<Enchantment> enchantment : blockEntity.getItem(i).getComponents().getOrDefault(DataComponents.STORED_ENCHANTMENTS, ItemEnchantments.EMPTY).keySet()) {
                            EnchantmentInstance entry = new EnchantmentInstance(enchantment, blockEntity.getItem(i).getComponents().getOrDefault(DataComponents.STORED_ENCHANTMENTS, ItemEnchantments.EMPTY).getLevel(enchantment));
                            //add only new ones
                            boolean present = false;
                            for (EnchantmentInstance listEntry : availableList) if (listEntry.enchantment() == entry.enchantment() && listEntry.level() == entry.level()) {
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

        @Override
        public ItemStack enchant(Container inventory, ItemStack item, Holder<Enchantment> enchantment, int level) {
            if (item.is(Items.BOOK)) {
                item = inventory.getItem(0).transmuteCopy(Items.ENCHANTED_BOOK);
                inventory.setItem(0, item);
            }

            item.enchant(enchantment, level);
            return item;
        }
    }

    public static final class EnchantedCatalyst implements EnchantingCatalyst {
        @Override
        public boolean canSkipLevels() {return true;}
        @Override
        public boolean canApplyIncompatible() {return false;}
        @Override
        public int catCost(EnchantmentScreenHandlerM handler, int index, Level world) {
            return 0;
        }
        @Override
        public int xpCost(EnchantmentScreenHandlerM handler, int index, Level world) {
            return xpCost(handler.enchantmentLevels.get(index), handler.getEnchantment(world, index));
        }
        @Override
        public int catCost(int level, Holder<Enchantment> enchantment) {
            return 0;
        }
        @Override
        public int xpCost(int level, Holder<Enchantment> enchantment) {
            return (level > -1 && enchantment != null) ? level * (enchantment.is(EnchantmentTags.TREASURE) ? 4 : 2) : 0;
        }
        @Override
        public List<EnchantmentInstance> getAllEnchantments(EnchantmentScreenHandlerM handler, Level world, BlockPos pos) {
            List<EnchantmentInstance> availableList = new ArrayList<>();

            ItemStack catalyst = handler.getSlot(1).getItem();

            for (Holder<Enchantment> enchantment : catalyst.getEnchantments().keySet()) {
                EnchantmentInstance entry = new EnchantmentInstance(enchantment, catalyst.getEnchantments().getLevel(enchantment));
                //add only new ones
                boolean present = false;
                for (EnchantmentInstance listEntry : availableList) if (listEntry.enchantment() == entry.enchantment() && listEntry.level() == entry.level()) {
                    present = true;
                    break;
                }
                if (!present) availableList.add(entry);
            }

            return availableList;
        }

        @Override
        public ItemStack enchant(Container inventory, ItemStack item, Holder<Enchantment> newEnchantment, int level) {
            //TODO: deal with enchanted books and enchanting books
            ItemStack catStack = inventory.getItem(1);
            EnchantmentHelper.updateEnchantments(catStack, components -> components.removeIf(enchantment -> enchantment.is(Identifier.parse(newEnchantment.getRegisteredName()))));
            inventory.setItem(1, catStack);

            if (item.is(Items.BOOK)) {
                item = inventory.getItem(0).transmuteCopy(Items.ENCHANTED_BOOK);
                inventory.setItem(0, item);
            }

            item.enchant(newEnchantment, level);

            return item;
        }
    }
}
