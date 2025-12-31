package net.pm.magicky.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.world.item.Items;
import net.pm.magicky.Magicky;

import java.util.concurrent.CompletableFuture;

public class MagickyRecipes extends FabricRecipeProvider {
    public MagickyRecipes(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
        super(output, registriesFuture);
    }

    @Override
    protected RecipeProvider createRecipeProvider(HolderLookup.Provider provider, RecipeOutput output) {
        return new RecipeProvider(provider, output) {
            @Override
            public void buildRecipes() {
                shaped(RecipeCategory.MISC, Items.NAME_TAG)
                        .pattern("O==")
                        .define('O', Items.STRING)
                        .define('=', Items.PAPER)
                        .group("name_tag")
                        .unlockedBy("has_material", has(Items.PAPER))
                        .save(output,  "name_tag_0");
                shaped(RecipeCategory.MISC, Items.NAME_TAG)
                        .pattern("==O")
                        .define('O', Items.STRING)
                        .define('=', Items.PAPER)
                        .group("name_tag")
                        .unlockedBy("has_material", has(Items.PAPER))
                        .save(output, "name_tag_1");
            }
        };
    }

    @Override
    public String getName() {
        return Magicky.MOD_ID+"_recipe";
    }
}
