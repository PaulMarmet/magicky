package net.pm.magicky.enchantment;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentLevelEntry;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.pm.magicky.screen.EnchantmentScreenHandlerM;

import java.util.List;

public interface EnchantingCatalyst {
    boolean canEnchantEnchanted = true;

    int catCost(int level, RegistryEntry<Enchantment> enchantment);

    int catCost(EnchantmentScreenHandlerM handler, int index, World world);

    int xpCost(int level, RegistryEntry<Enchantment> enchantment);

    int xpCost(EnchantmentScreenHandlerM handler, int index, World world);

    List<EnchantmentLevelEntry> getAllEnchantments(World world, BlockPos pos);
}
