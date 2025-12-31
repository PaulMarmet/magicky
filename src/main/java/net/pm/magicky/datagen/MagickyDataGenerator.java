package net.pm.magicky.datagen;

import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.Registries;
import net.pm.magicky.enchantment.MagickyEnchantments;

public class MagickyDataGenerator implements DataGeneratorEntrypoint {
    @Override
    public void onInitializeDataGenerator(FabricDataGenerator fabricDataGenerator) {
        FabricDataGenerator.Pack pack = fabricDataGenerator.createPack();

        pack.addProvider(MagickyDynamicRegistries::new);
        pack.addProvider(MagickyItemTags::new);
        pack.addProvider(MagickyEnchantmentTags::new);
        pack.addProvider(MagickyRecipes::new);
    }

    @Override
    public void buildRegistry(RegistrySetBuilder registryBuilder) {
        registryBuilder.add(Registries.ENCHANTMENT, MagickyEnchantments::bootstrap);
    }
}
