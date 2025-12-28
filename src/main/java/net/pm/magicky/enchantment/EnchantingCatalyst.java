package net.pm.magicky.enchantment;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentInstance;
import net.minecraft.world.level.Level;
import net.pm.magicky.screen.EnchantmentScreenHandlerM;

import java.util.List;

public interface EnchantingCatalyst {
    boolean canSkipLevels();
    boolean canApplyIncompatible();
    int catCost(int level, Holder<Enchantment> enchantment);

    int catCost(EnchantmentScreenHandlerM handler, int index, Level world);

    int xpCost(int level, Holder<Enchantment> enchantment);

    int xpCost(EnchantmentScreenHandlerM handler, int index, Level world);

    List<EnchantmentInstance> getAllEnchantments(EnchantmentScreenHandlerM handler, Level world, BlockPos pos);

    ItemStack enchant(Container inventory, ItemStack item, Holder<Enchantment> enchantment, int level);
}
