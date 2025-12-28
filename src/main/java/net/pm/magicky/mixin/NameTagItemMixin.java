package net.pm.magicky.mixin;

import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.NameTagItem;
import net.minecraft.world.level.Level;
import net.pm.magicky.screen.NameTagInt;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(NameTagItem.class)
public class NameTagItemMixin extends Item {
    public NameTagItemMixin(Properties settings) {
        super(settings);
    }

    @Override
    public InteractionResult use(Level world, Player user, InteractionHand hand) {
        ItemStack itemStack = user.getItemInHand(hand);
        ((NameTagInt)user).magicky$useNameTag(hand);
        user.awardStat(Stats.ITEM_USED.get((NameTagItem)(Object)this));
        return InteractionResult.SUCCESS;
    }
}
