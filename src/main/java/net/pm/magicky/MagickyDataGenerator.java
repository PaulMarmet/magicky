package net.pm.magicky;

import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.pm.magicky.datagen.MagickyEnchantmentTags;
import net.pm.magicky.datagen.MagickyItemTags;

public class MagickyDataGenerator implements DataGeneratorEntrypoint {
    @Override
    public void onInitializeDataGenerator(FabricDataGenerator fabricDataGenerator) {
        FabricDataGenerator.Pack pack = fabricDataGenerator.createPack();

        pack.addProvider(MagickyItemTags::new);
        pack.addProvider(MagickyEnchantmentTags::new);
    }
}
