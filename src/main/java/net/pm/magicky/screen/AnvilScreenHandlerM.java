package net.pm.magicky.screen;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.DataSlot;
import net.minecraft.world.inventory.ItemCombinerMenu;
import net.minecraft.world.inventory.ItemCombinerMenuSlotDefinition;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.minecraft.world.level.block.AnvilBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LevelEvent;
import net.minecraft.world.level.block.state.BlockState;
import net.pm.magicky.datagen.MagickyEnchantmentTags;
import net.pm.magicky.datagen.MagickyItemTags;

public class AnvilScreenHandlerM extends ItemCombinerMenu {
//    public static final int INPUT_1_ID = 0;
//    public static final int INPUT_2_ID = 1;
//    public static final int OUTPUT_ID = 2;
//    private static final Logger LOGGER = LogUtils.getLogger();
//    public static final int MAX_NAME_LENGTH = 50;
    private int repairItemUsage;
//    @Nullable
//    private String newItemName;
    private final DataSlot levelCost = DataSlot.standalone();
//    private static final int INPUT_1_X = 27;
//    private static final int INPUT_2_X = 76;
//    private static final int OUTPUT_X = 134;
//    private static final int SLOT_Y = 47;

    public AnvilScreenHandlerM(int syncId, Inventory inventory) {
        this(syncId, inventory, ContainerLevelAccess.NULL);
    }

    public AnvilScreenHandlerM(int syncId, Inventory inventory, ContainerLevelAccess context) {
        super(MagickyScreenHandlers.ANVIL_M, syncId, inventory, context, createInputSlotDefinitions());
        this.addDataSlot(this.levelCost);
    }

    public AnvilScreenHandlerM(int syncId, Inventory inventory, Object o) {
        this(syncId, inventory, ContainerLevelAccess.NULL);
    }

    private static ItemCombinerMenuSlotDefinition createInputSlotDefinitions() {
        return ItemCombinerMenuSlotDefinition.create().withSlot(0, 27, 47, stack -> true).withSlot(1, 76, 47, stack -> true).withResultSlot(2, 134, 47).build();
    }

    @Override
    protected boolean isValidBlock(BlockState state) {
        return state.is(BlockTags.ANVIL);
    }

    @Override
    protected boolean mayPickup(Player player, boolean present) {
        return (player.hasInfiniteMaterials() || player.experienceLevel >= this.levelCost.get()) && this.levelCost.get() > 0;
    }

    @Override
    protected void onTake(Player player, ItemStack stack) {
        if (!player.getAbilities().instabuild) {
            player.giveExperienceLevels(-this.levelCost.get());
        }

        this.inputSlots.setItem(0, ItemStack.EMPTY);
        if (this.repairItemUsage > 0) {
            ItemStack itemStack = this.inputSlots.getItem(1);
            if (!itemStack.isEmpty() && itemStack.getCount() > this.repairItemUsage) {
                itemStack.shrink(this.repairItemUsage);
                this.inputSlots.setItem(1, itemStack);
            } else {
                this.inputSlots.setItem(1, ItemStack.EMPTY);
            }
        } else {
            this.inputSlots.setItem(1, ItemStack.EMPTY);
        }

        this.levelCost.set(0);
        this.access.execute((world, pos) -> {
            BlockState blockState = world.getBlockState(pos);
            if (!player.hasInfiniteMaterials() && blockState.is(BlockTags.ANVIL) && player.getRandom().nextFloat() < 0.12F) {
                BlockState blockState2 = AnvilBlock.damage(blockState);
                if (blockState2 == null) {
                    world.removeBlock(pos, false);
                    world.levelEvent(LevelEvent.SOUND_ANVIL_BROKEN, pos, 0);
                } else {
                    world.setBlock(pos, blockState2, Block.UPDATE_CLIENTS);
                    world.levelEvent(LevelEvent.SOUND_ANVIL_USED, pos, 0);
                }
            } else {
                world.levelEvent(LevelEvent.SOUND_ANVIL_USED, pos, 0);
            }
        });
    }

    @Override
    public void createResult() {
        ItemStack itemStack = this.inputSlots.getItem(0);
        this.levelCost.set(1);
        int i = 0;
        int j = 0;
        //If there is an item
        if (!itemStack.isEmpty() && EnchantmentHelper.canStoreEnchantments(itemStack)) {
            //The output stack
            ItemStack itemStack2 = itemStack.copy();
            //Second input stack
            ItemStack itemStack3 = this.inputSlots.getItem(1);
            ItemEnchantments.Mutable builder = new ItemEnchantments.Mutable(EnchantmentHelper.getEnchantmentsForCrafting(itemStack2));
            //Amount of repair items to use
            this.repairItemUsage = 0;
            if (!itemStack3.isEmpty()) {
                //if sacrifice item has enchants
                boolean bl = itemStack3.has(DataComponents.STORED_ENCHANTMENTS);
                //if output item has durability and can be repaired by input 2
                if (itemStack2.isDamageableItem() && itemStack2.isValidRepairItem(itemStack3)) {
                    int k = Math.min(itemStack2.getDamageValue(), itemStack2.getMaxDamage() / 4);

                    //if durability is above max make impossible then no result
                    if (k <= 0) {
                        this.resultSlots.setItem(0, ItemStack.EMPTY);
                        this.levelCost.set(0);
                        return;
                    }

                    //if repair material is present, use one to fully repair item
                    if (0 < itemStack3.getCount()) {
                        //1 lvl per 20% damage fixed or 1 lvl if mending is present but not used
                        if (!EnchantmentHelper.hasTag(itemStack2, MagickyEnchantmentTags.MENDS) || itemStack3.is(MagickyItemTags.MENDING_AGENT)) i += (int) Math.max(1, Math.ceil(((float) itemStack2.getDamageValue() / itemStack2.getMaxDamage()) * 5));
                        else i ++;
                        itemStack2.setDamageValue(0);
                        this.repairItemUsage = 1;
                        //add one level
                        //i++;
                    }
                } else {
                    //no result if input 2 doesnt have enchants and either they are not the same item or the output cant be damaged and input 2 isnt a name tag
                    if (!bl && (!itemStack2.is(itemStack3.getItem()) || !itemStack2.isDamageableItem()) && !itemStack3.is(Items.NAME_TAG)) {
                        this.resultSlots.setItem(0, ItemStack.EMPTY);
                        this.levelCost.set(0);
                        return;
                    }

                    //if output & input 2 is damagable and doesnt have enchants
                    if (itemStack2.isDamageableItem() && itemStack3.isDamageableItem() && !bl) {
                        //input 1 durability
                        int kx = itemStack.getMaxDamage() - itemStack.getDamageValue();
                        //input 2 durability
                        int m = itemStack3.getMaxDamage() - itemStack3.getDamageValue();
                        //add input 2 durability + 25% of max to input 1 durability
                        int n = m + itemStack2.getMaxDamage() * 25 / 100;
                        int o = kx + n;
                        //convert back to damage and prevent over max durability
                        int p = itemStack2.getMaxDamage() - o;
                        if (p < 0) {
                            p = 0;
                        }

                        //repair item if possible
                        if (p < itemStack2.getDamageValue()) {
                            itemStack2.setDamageValue(p);
                            i += 2;
                        }
                    }

                    ItemEnchantments itemEnchantmentsComponent = EnchantmentHelper.getEnchantmentsForCrafting(itemStack3);
                    boolean bl2 = false;
                    boolean bl3 = false;

                    //enchantment section that i totally wont remove later
                    //for all the enchants on input 2
                    for (Object2IntMap.Entry<Holder<Enchantment>> entry : itemEnchantmentsComponent.entrySet()) {
                        Holder<Enchantment> registryEntry = (Holder<Enchantment>)entry.getKey();
                        //get level of output
                        int q = builder.getLevel(registryEntry);
                        //get level of input
                        int r = entry.getIntValue();
                        //if equal, add 1, otherwise, pick highest
                        r = q == r ? r + 1 : Math.max(r, q);
                        //if item supports the enchant, true
                        Enchantment enchantment = registryEntry.value();
                        boolean bl4 = enchantment.canEnchant(itemStack);
                        //or if in creative mode, or if the item its being added to is an enchant book
                        if (this.player.getAbilities().instabuild || itemStack.is(Items.ENCHANTED_BOOK)) {
                            bl4 = true;
                        }

                        //if theres an incompatible enchant, raise cost and turn false
                        for (Holder<Enchantment> registryEntry2 : builder.keySet()) {
                            if (!registryEntry2.equals(registryEntry) && !Enchantment.areCompatible(registryEntry, registryEntry2)) {
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
                        this.resultSlots.setItem(0, ItemStack.EMPTY);
                        this.levelCost.set(0);
                        return;
                    }

                    //if input 2 is a name tag or input 1 doesnt have a name
                    if (itemStack3.is(Items.NAME_TAG) || !itemStack.has(DataComponents.CUSTOM_NAME)) {
                        itemStack2.set(DataComponents.CUSTOM_NAME, itemStack3.getHoverName());
                        //if input 2 doesnt have a name, clear the custom name tag for the output(resetting the output's name to the item name)
                        if (!itemStack3.has(DataComponents.CUSTOM_NAME)) {
                            itemStack2.remove(DataComponents.CUSTOM_NAME);
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
                EnchantmentHelper.setEnchantments(itemStack2, builder.toImmutable());
            }

            //display output item
            this.resultSlots.setItem(0, itemStack2);
            this.broadcastChanges();
        } else {
            //no output
            this.resultSlots.setItem(0, ItemStack.EMPTY);
            this.levelCost.set(0);
        }
    }

    public int getLevelCost() {
        return this.levelCost.get();
    }
}
