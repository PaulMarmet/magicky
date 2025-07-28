package net.pm.magicky.enchantment;

import net.minecraft.block.ChiseledBookshelfBlock;
import net.minecraft.block.EnchantingTableBlock;
import net.minecraft.block.entity.ChiseledBookshelfBlockEntity;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ItemEnchantmentsComponent;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentLevelEntry;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.EnchantmentTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.pm.magicky.screen.EnchantmentScreenHandlerM;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EnchantmentCatalysts {

    public static Map<Item, EnchantingCatalyst> catalysts = new HashMap<>();

    static {
        EnchantmentCatalysts.catalysts.put(Items.LAPIS_LAZULI, new EnchantmentCatalysts.LapisCatalyst());
    }

    public static final class LapisCatalyst implements EnchantingCatalyst {
        @Override
        public int catCost(EnchantmentScreenHandlerM handler, int index, World world) {
            return catCost(handler.enchantmentLevels.get(index), handler.getEnchantment(world, index));
        }
        @Override
        public int xpCost(EnchantmentScreenHandlerM handler, int index, World world) {
            return xpCost(handler.enchantmentLevels.get(index), handler.getEnchantment(world, index));
        }
        @Override
        public int catCost(int level, RegistryEntry<Enchantment> enchantment) {
            return (level > -1 && enchantment != null) ? ((2 * level) - 1) * (enchantment.isIn(EnchantmentTags.TREASURE) ? 2 : 1) : 0;
        }
        @Override
        public int xpCost(int level, RegistryEntry<Enchantment> enchantment) {
            return (level > -1 && enchantment != null) ? level * (enchantment.isIn(EnchantmentTags.TREASURE) ? 2 : 1) : 0;
        }
        @Override
        public List<EnchantmentLevelEntry> getAllEnchantments(World world, BlockPos pos) {
            List<EnchantmentLevelEntry> availableList = new ArrayList<>();
            //TODO: check that bookshelf has line of sight maybe
            //TODO: add the "basic enchantments" however i end up implementing those
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
    }
}
