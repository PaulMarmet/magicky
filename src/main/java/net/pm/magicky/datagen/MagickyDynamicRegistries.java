package net.pm.magicky.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricDynamicRegistryProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.pm.magicky.Magicky;

import java.util.concurrent.CompletableFuture;

public class MagickyDynamicRegistries extends FabricDynamicRegistryProvider {
    public MagickyDynamicRegistries(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
        super(output, registriesFuture);
    }

    @Override
    protected void configure(HolderLookup.Provider provider, Entries entries) {
        entries.addAll(provider.lookupOrThrow(Registries.ENCHANTMENT));
    }

    @Override
    public String getName() {
        return Magicky.MOD_ID;
    }
}
