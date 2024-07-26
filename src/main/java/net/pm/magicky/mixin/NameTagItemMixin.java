package net.pm.magicky.mixin;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.NameTagItem;
import net.minecraft.stat.Stats;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import net.pm.magicky.screen.NameTagInt;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(NameTagItem.class)
public class NameTagItemMixin extends Item {
    public NameTagItemMixin(Settings settings) {
        super(settings);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack itemStack = user.getStackInHand(hand);
        ((NameTagInt)user).useNameTag(hand);
        user.incrementStat(Stats.USED.getOrCreateStat((NameTagItem)(Object)this));
        return TypedActionResult.success(itemStack, world.isClient());
    }
}
