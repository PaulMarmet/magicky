package net.pm.magicky.screen;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.minecraft.block.AnvilBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ItemEnchantmentsComponent;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.screen.*;
import net.minecraft.screen.slot.ForgingSlotsManager;
import net.minecraft.world.WorldEvents;

public class AnvilScreenHandlerM extends ForgingScreenHandler {
//    public static final int INPUT_1_ID = 0;
//    public static final int INPUT_2_ID = 1;
//    public static final int OUTPUT_ID = 2;
//    private static final Logger LOGGER = LogUtils.getLogger();
//    public static final int MAX_NAME_LENGTH = 50;
    private int repairItemUsage;
//    @Nullable
//    private String newItemName;
    private final Property levelCost = Property.create();
//    private static final int INPUT_1_X = 27;
//    private static final int INPUT_2_X = 76;
//    private static final int OUTPUT_X = 134;
//    private static final int SLOT_Y = 47;

    public AnvilScreenHandlerM(int syncId, PlayerInventory inventory) {
        this(syncId, inventory, ScreenHandlerContext.EMPTY);
    }

    public AnvilScreenHandlerM(int syncId, PlayerInventory inventory, ScreenHandlerContext context) {
        super(MagickyScreenHandlers.ANVIL_M, syncId, inventory, context);
//        super(ScreenHandlerType.ANVIL, syncId, inventory, context);
        this.addProperty(this.levelCost);
    }

    public AnvilScreenHandlerM(int syncId, PlayerInventory inventory, Object o) {
        this(syncId, inventory, ScreenHandlerContext.EMPTY);
    }

    @Override
    protected ForgingSlotsManager getForgingSlotsManager() {
        return ForgingSlotsManager.create().input(0, 27, 47, stack -> true).input(1, 76, 47, stack -> true).output(2, 134, 47).build();
    }

    @Override
    protected boolean canUse(BlockState state) {
        return state.isIn(BlockTags.ANVIL);
    }

    @Override
    protected boolean canTakeOutput(PlayerEntity player, boolean present) {
        return (player.isInCreativeMode() || player.experienceLevel >= this.levelCost.get()) && this.levelCost.get() > 0;
    }

    @Override
    protected void onTakeOutput(PlayerEntity player, ItemStack stack) {
        if (!player.getAbilities().creativeMode) {
            player.addExperienceLevels(-this.levelCost.get());
        }

        this.input.setStack(0, ItemStack.EMPTY);
        if (this.repairItemUsage > 0) {
            ItemStack itemStack = this.input.getStack(1);
            if (!itemStack.isEmpty() && itemStack.getCount() > this.repairItemUsage) {
                itemStack.decrement(this.repairItemUsage);
                this.input.setStack(1, itemStack);
            } else {
                this.input.setStack(1, ItemStack.EMPTY);
            }
        } else {
            this.input.setStack(1, ItemStack.EMPTY);
        }

        this.levelCost.set(0);
        this.context.run((world, pos) -> {
            BlockState blockState = world.getBlockState(pos);
            if (!player.isInCreativeMode() && blockState.isIn(BlockTags.ANVIL) && player.getRandom().nextFloat() < 0.12F) {
                BlockState blockState2 = AnvilBlock.getLandingState(blockState);
                if (blockState2 == null) {
                    world.removeBlock(pos, false);
                    world.syncWorldEvent(WorldEvents.ANVIL_DESTROYED, pos, 0);
                } else {
                    world.setBlockState(pos, blockState2, Block.NOTIFY_LISTENERS);
                    world.syncWorldEvent(WorldEvents.ANVIL_USED, pos, 0);
                }
            } else {
                world.syncWorldEvent(WorldEvents.ANVIL_USED, pos, 0);
            }
        });
    }

    @Override
    public void updateResult() {
        ItemStack itemStack = this.input.getStack(0);
        this.levelCost.set(1);
        int i = 0;
        int j = 0;
        //If there is an item
        if (!itemStack.isEmpty() && EnchantmentHelper.canHaveEnchantments(itemStack)) {
            //The output stack
            ItemStack itemStack2 = itemStack.copy();
            //Second input stack
            ItemStack itemStack3 = this.input.getStack(1);
            ItemEnchantmentsComponent.Builder builder = new ItemEnchantmentsComponent.Builder(EnchantmentHelper.getEnchantments(itemStack2));
            //Amount of repair items to use
            this.repairItemUsage = 0;
            if (!itemStack3.isEmpty()) {
                //if sacrifice item has enchants
                boolean bl = itemStack3.contains(DataComponentTypes.STORED_ENCHANTMENTS);
                //if output item has durability and can be repaired by input 2
                if (itemStack2.isDamageable() && itemStack2.getItem().canRepair(itemStack, itemStack3)) {
                    int k = Math.min(itemStack2.getDamage(), itemStack2.getMaxDamage() / 4);

                    //if durability is above max make impossible then no result
                    if (k <= 0) {
                        this.output.setStack(0, ItemStack.EMPTY);
                        this.levelCost.set(0);
                        return;
                    }

                    //if repair material is present, use one to fully repair item
                    if (0 < itemStack3.getCount()) {
                        //1 lvl per 20% damage fixed
                        i += (int) Math.max(1, Math.ceil(((float) itemStack2.getDamage() / itemStack2.getMaxDamage()) * 5));
                        itemStack2.setDamage(0);
                        this.repairItemUsage = 1;
                        //add one level
                        //i++;
                    }
                } else {
                    //no result if input 2 doesnt have enchants and either they are not the same item or the output cant be damaged and input 2 isnt a name tag
                    if (!bl && (!itemStack2.isOf(itemStack3.getItem()) || !itemStack2.isDamageable()) && !itemStack3.isOf(Items.NAME_TAG)) {
                        this.output.setStack(0, ItemStack.EMPTY);
                        this.levelCost.set(0);
                        return;
                    }

                    //if output & input 2 is damagable and doesnt have enchants
                    if (itemStack2.isDamageable() && itemStack3.isDamageable() && !bl) {
                        //input 1 durability
                        int kx = itemStack.getMaxDamage() - itemStack.getDamage();
                        //input 2 durability
                        int m = itemStack3.getMaxDamage() - itemStack3.getDamage();
                        //add input 2 durability + 10% of max to input 1 durability
                        int n = m + itemStack2.getMaxDamage() * 10 / 100;
                        int o = kx + n;
                        //convert back to damage and prevent over max durability
                        int p = itemStack2.getMaxDamage() - o;
                        if (p < 0) {
                            p = 0;
                        }

                        //repair item if possible
                        if (p < itemStack2.getDamage()) {
                            itemStack2.setDamage(p);
                            i += 2;
                        }
                    }

                    ItemEnchantmentsComponent itemEnchantmentsComponent = EnchantmentHelper.getEnchantments(itemStack3);
                    boolean bl2 = false;
                    boolean bl3 = false;

                    //enchantment section that i totally wont remove later
                    //for all the enchants on input 2
                    for (Object2IntMap.Entry<RegistryEntry<Enchantment>> entry : itemEnchantmentsComponent.getEnchantmentEntries()) {
                        RegistryEntry<Enchantment> registryEntry = (RegistryEntry<Enchantment>)entry.getKey();
                        //get level of output
                        int q = builder.getLevel(registryEntry);
                        //get level of input
                        int r = entry.getIntValue();
                        //if equal, add 1, otherwise, pick highest
                        r = q == r ? r + 1 : Math.max(r, q);
                        //if item supports the enchant, true
                        Enchantment enchantment = registryEntry.value();
                        boolean bl4 = enchantment.isAcceptableItem(itemStack);
                        //or if in creative mode, or if the item its being added to is an enchant book
                        if (this.player.getAbilities().creativeMode || itemStack.isOf(Items.ENCHANTED_BOOK)) {
                            bl4 = true;
                        }

                        //if theres an incompatible enchant, raise cost and turn false
                        for (RegistryEntry<Enchantment> registryEntry2 : builder.getEnchantments()) {
                            if (!registryEntry2.equals(registryEntry) && !Enchantment.canBeCombined(registryEntry, registryEntry2)) {
                                bl4 = false;
                                i++;
                            }
                        }

                        //if item cant have the enchant, mark for output to be removed, otherwise
                        if (!bl4) {
                            bl3 = true;
                        } else {
                            bl2 = true;
                            //prevent overleveled enchants
                            if (r > enchantment.getMaxLevel()) {
                                r = enchantment.getMaxLevel();
                            }

                            //add the enchant to output item
                            builder.set(registryEntry, r);

                            //math for the xp cost
                            int s = enchantment.getAnvilCost();
                            if (bl) {
                                s = Math.max(1, s);
                            }

                            //add to xp cost base enchant cost / 2 times the level
                            i += s * r;
                            //if multiple items are present, multiply cost by number of items
                            if (itemStack.getCount() > 1) {
                                i = i * itemStack.getCount();
                            }
                        }
                    }

                    //if item cant have enchant, remove the output
                    if (bl3 && !bl2) {
                        this.output.setStack(0, ItemStack.EMPTY);
                        this.levelCost.set(0);
                        return;
                    }

                    //if input 2 is a name tag or input 1 doesnt have a name
                    if (itemStack3.isOf(Items.NAME_TAG) || !itemStack.contains(DataComponentTypes.CUSTOM_NAME)) {
                        itemStack2.set(DataComponentTypes.CUSTOM_NAME, itemStack3.getName());
                        //if input 2 doesnt have a name, clear the custom name tag for the output(resetting the output's name to the item name)
                        if (!itemStack3.contains(DataComponentTypes.CUSTOM_NAME)) {
                            itemStack2.remove(DataComponentTypes.CUSTOM_NAME);
                        }
                        i += 1;
                    }
                }
            }

            this.levelCost.set(i);
            //if level cost is 0, remove output
            if (i <= 0) {
                itemStack2 = ItemStack.EMPTY;
            }

            //if there is still an output
            if (!itemStack2.isEmpty()) {
                //set new enchants for output
                EnchantmentHelper.set(itemStack2, builder.build());
            }

            //display output item
            this.output.setStack(0, itemStack2);
            this.sendContentUpdates();
        } else {
            //no output
            this.output.setStack(0, ItemStack.EMPTY);
            this.levelCost.set(0);
        }
    }

    public int getLevelCost() {
        return this.levelCost.get();
    }
}
