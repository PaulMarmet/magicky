package net.pm.magicky.enchantment;

import net.minecraft.advancements.criterion.*;
import net.minecraft.core.*;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.*;
import net.minecraft.util.random.WeightedList;
import net.minecraft.util.valueproviders.ConstantFloat;
import net.minecraft.util.valueproviders.UniformFloat;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.*;
import net.minecraft.world.item.enchantment.effects.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicate;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.*;
import net.minecraft.world.level.storage.loot.providers.number.EnchantmentLevelProvider;
import net.minecraft.world.phys.Vec3;
import net.pm.magicky.Magicky;
import net.pm.magicky.datagen.MagickyEnchantmentTags;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;

public class MagickyEnchantments {
    public static ResourceKey<Enchantment> RECOIL = ResourceKey.create(Registries.ENCHANTMENT, Identifier.fromNamespaceAndPath(Magicky.MOD_ID, "recoil"));
    public static ResourceKey<Enchantment> MAGIC_PROTECTION = ResourceKey.create(Registries.ENCHANTMENT, Identifier.fromNamespaceAndPath(Magicky.MOD_ID, "magic_protection"));

    public static void register() {
        Registry.register(BuiltInRegistries.ENCHANTMENT_ENTITY_EFFECT_TYPE, Identifier.fromNamespaceAndPath(Magicky.MOD_ID, "recoil"), RecoilEnchantmentEffect.CODEC);
    }

    public static void bootstrap(BootstrapContext<Enchantment> bootstrapContext) {
        register(bootstrapContext, RECOIL, Enchantment.enchantment(Enchantment.definition(
                bootstrapContext.lookup(Registries.ITEM).getOrThrow(ItemTags.CROSSBOW_ENCHANTABLE),
                5, //Weight
                2, //Levels
                Enchantment.dynamicCost(10, 15), //Min cost
                Enchantment.dynamicCost(30, 25), //Max cost
                2, //Anvil cost
                EquipmentSlotGroup.MAINHAND
                ))
                .withEffect(EnchantmentEffectComponents.PROJECTILE_SPAWNED,
                new RecoilEnchantmentEffect(LevelBasedValue.perLevel(0.3f)))
                );

        register(bootstrapContext, MAGIC_PROTECTION, Enchantment.enchantment(Enchantment.definition(
                        bootstrapContext.lookup(Registries.ITEM).getOrThrow(ItemTags.ARMOR_ENCHANTABLE),
                        3, //Weight
                        2, //Levels
                        Enchantment.dynamicCost(15, 18), //Min cost
                        Enchantment.dynamicCost(30, 18), //Max cost
                        8, //Anvil cost
                        EquipmentSlotGroup.ARMOR
                ))
                .exclusiveWith(bootstrapContext.lookup(Registries.ENCHANTMENT).getOrThrow(MagickyEnchantmentTags.SPECIAL_PROTECTION))
                .withEffect(EnchantmentEffectComponents.DAMAGE_PROTECTION,
                        new AddValue(LevelBasedValue.perLevel(2.0f)),
                        DamageSourceCondition.hasDamageSource(DamageSourcePredicate.Builder.damageType()
                                .tag(TagPredicate.is(DamageTypeTags.WITCH_RESISTANT_TO))
                                .tag(TagPredicate.isNot(DamageTypeTags.BYPASSES_INVULNERABILITY))))
                );


        bootstrapVanillins(bootstrapContext);
    }

    public static void bootstrapVanillins(BootstrapContext<Enchantment> bootstrapContext) {
        HolderGetter<DamageType> damageTypeGetter = bootstrapContext.lookup(Registries.DAMAGE_TYPE);
        HolderGetter<Enchantment> enchantmentGetter = bootstrapContext.lookup(Registries.ENCHANTMENT);
        HolderGetter<Item> itemGetter = bootstrapContext.lookup(Registries.ITEM);
        HolderGetter<Block> blockGetter = bootstrapContext.lookup(Registries.BLOCK);
        HolderGetter<EntityType<?>> entityTypeGetter = bootstrapContext.lookup(Registries.ENTITY_TYPE);
        register(bootstrapContext, Enchantments.PROTECTION, Enchantment.enchantment(Enchantment.definition(
                itemGetter.getOrThrow(ItemTags.ARMOR_ENCHANTABLE),
                10,
                4,
                Enchantment.dynamicCost(1, 11),
                Enchantment.dynamicCost(12, 11),
                1,
                EquipmentSlotGroup.ARMOR
                ))
                .exclusiveWith(enchantmentGetter.getOrThrow(EnchantmentTags.ARMOR_EXCLUSIVE))
                .withEffect(EnchantmentEffectComponents.DAMAGE_PROTECTION,
                        new AddValue(LevelBasedValue.perLevel(1.0F)),
                        DamageSourceCondition.hasDamageSource(DamageSourcePredicate.Builder.damageType()
                                .tag(TagPredicate.isNot(DamageTypeTags.BYPASSES_INVULNERABILITY)))));
        register(bootstrapContext, Enchantments.FIRE_PROTECTION, Enchantment.enchantment(Enchantment.definition(
                itemGetter.getOrThrow(ItemTags.ARMOR_ENCHANTABLE),
                5,
                4,
                Enchantment.dynamicCost(10, 8),
                Enchantment.dynamicCost(18, 8),
                2,
                EquipmentSlotGroup.ARMOR))
                .exclusiveWith(enchantmentGetter.getOrThrow(EnchantmentTags.ARMOR_EXCLUSIVE))
                .withEffect(EnchantmentEffectComponents.DAMAGE_PROTECTION,
                        new AddValue(LevelBasedValue.perLevel(2.0F)),
                        AllOfCondition.allOf(
                                DamageSourceCondition.hasDamageSource(DamageSourcePredicate.Builder.damageType()
                                        .tag(TagPredicate.is(DamageTypeTags.IS_FIRE))
                                        .tag(TagPredicate.isNot(DamageTypeTags.BYPASSES_INVULNERABILITY)))))
                .withEffect(EnchantmentEffectComponents.ATTRIBUTES,
                        new EnchantmentAttributeEffect(Identifier.withDefaultNamespace("enchantment.fire_protection"),
                                Attributes.BURNING_TIME,
                                LevelBasedValue.perLevel(-0.15F),
                                AttributeModifier.Operation.ADD_MULTIPLIED_BASE)));
        register(bootstrapContext, Enchantments.FEATHER_FALLING, Enchantment.enchantment(Enchantment.definition(
                itemGetter.getOrThrow(ItemTags.FOOT_ARMOR_ENCHANTABLE),
                5,
                4,
                Enchantment.dynamicCost(5, 6),
                Enchantment.dynamicCost(11, 6),
                2, EquipmentSlotGroup.ARMOR))
                .withEffect(EnchantmentEffectComponents.DAMAGE_PROTECTION,
                        new AddValue(LevelBasedValue.perLevel(3.0F)),
                        DamageSourceCondition.hasDamageSource(DamageSourcePredicate.Builder.damageType()
                                .tag(TagPredicate.is(DamageTypeTags.IS_FALL))
                                .tag(TagPredicate.isNot(DamageTypeTags.BYPASSES_INVULNERABILITY)))));
        register(bootstrapContext, Enchantments.BLAST_PROTECTION, Enchantment.enchantment(Enchantment.definition(
                itemGetter.getOrThrow(ItemTags.ARMOR_ENCHANTABLE),
                2,
                4,
                Enchantment.dynamicCost(5, 8),
                Enchantment.dynamicCost(13, 8),
                4, EquipmentSlotGroup.ARMOR))
                .exclusiveWith(enchantmentGetter.getOrThrow(EnchantmentTags.ARMOR_EXCLUSIVE))
                .withEffect(EnchantmentEffectComponents.DAMAGE_PROTECTION,
                        new AddValue(LevelBasedValue.perLevel(2.0F)),
                        DamageSourceCondition.hasDamageSource(DamageSourcePredicate.Builder.damageType()
                                .tag(TagPredicate.is(DamageTypeTags.IS_EXPLOSION))
                                .tag(TagPredicate.isNot(DamageTypeTags.BYPASSES_INVULNERABILITY))))
                .withEffect(EnchantmentEffectComponents.ATTRIBUTES,
                        new EnchantmentAttributeEffect(Identifier.withDefaultNamespace("enchantment.blast_protection"),
                                Attributes.EXPLOSION_KNOCKBACK_RESISTANCE,
                                LevelBasedValue.perLevel(0.15F),
                                AttributeModifier.Operation.ADD_VALUE)));
        register(bootstrapContext, Enchantments.PROJECTILE_PROTECTION, Enchantment.enchantment(Enchantment.definition(
                itemGetter.getOrThrow(ItemTags.ARMOR_ENCHANTABLE),
                5,
                4,
                Enchantment.dynamicCost(3, 6),
                Enchantment.dynamicCost(9, 6),
                2,
                EquipmentSlotGroup.ARMOR))
                .exclusiveWith(enchantmentGetter.getOrThrow(EnchantmentTags.ARMOR_EXCLUSIVE))
                .withEffect(EnchantmentEffectComponents.DAMAGE_PROTECTION,
                        new AddValue(LevelBasedValue.perLevel(2.0F)),
                        DamageSourceCondition.hasDamageSource(DamageSourcePredicate.Builder.damageType()
                                .tag(TagPredicate.is(DamageTypeTags.IS_PROJECTILE))
                                .tag(TagPredicate.isNot(DamageTypeTags.BYPASSES_INVULNERABILITY)))));
        register(bootstrapContext, Enchantments.RESPIRATION, Enchantment.enchantment(Enchantment.definition(
                itemGetter.getOrThrow(ItemTags.HEAD_ARMOR_ENCHANTABLE),
                2,
                3,
                Enchantment.dynamicCost(10, 10),
                Enchantment.dynamicCost(40, 10),
                4,
                EquipmentSlotGroup.HEAD))
                .withEffect(EnchantmentEffectComponents.ATTRIBUTES,
                        new EnchantmentAttributeEffect(Identifier.withDefaultNamespace("enchantment.respiration"),
                                Attributes.OXYGEN_BONUS,
                                LevelBasedValue.perLevel(1.0F),
                                AttributeModifier.Operation.ADD_VALUE)));
        register(bootstrapContext, Enchantments.AQUA_AFFINITY, Enchantment.enchantment(Enchantment.definition(
                itemGetter.getOrThrow(ItemTags.HEAD_ARMOR_ENCHANTABLE),
                2,
                1,
                Enchantment.constantCost(1),
                Enchantment.constantCost(41),
                4,
                EquipmentSlotGroup.HEAD))
                .withEffect(EnchantmentEffectComponents.ATTRIBUTES,
                        new EnchantmentAttributeEffect(Identifier.withDefaultNamespace("enchantment.aqua_affinity"),
                                Attributes.SUBMERGED_MINING_SPEED,
                                LevelBasedValue.perLevel(4.0F),
                                AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)));
        register(bootstrapContext, Enchantments.THORNS, Enchantment.enchantment(Enchantment.definition(
                itemGetter.getOrThrow(ItemTags.ARMOR_ENCHANTABLE),
                itemGetter.getOrThrow(ItemTags.CHEST_ARMOR_ENCHANTABLE),
                1,
                3,
                Enchantment.dynamicCost(10, 20),
                Enchantment.dynamicCost(60, 20),
                8,
                EquipmentSlotGroup.ANY))
                .withEffect(EnchantmentEffectComponents.POST_ATTACK,
                        EnchantmentTarget.VICTIM,
                        EnchantmentTarget.ATTACKER,
                        AllOf.entityEffects(
                                new DamageEntity(
                                        LevelBasedValue.constant(1.0F),
                                        LevelBasedValue.constant(5.0F),
                                        damageTypeGetter.getOrThrow(DamageTypes.THORNS)),
                                new ChangeItemDamage(LevelBasedValue.constant(2.0F))),
                        LootItemRandomChanceCondition.randomChance(EnchantmentLevelProvider.forEnchantmentLevel(LevelBasedValue.perLevel(0.15F)))));
        register(bootstrapContext, Enchantments.DEPTH_STRIDER, Enchantment.enchantment(Enchantment.definition(
                itemGetter.getOrThrow(ItemTags.FOOT_ARMOR_ENCHANTABLE),
                2,
                3,
                Enchantment.dynamicCost(10, 10),
                Enchantment.dynamicCost(25, 10),
                4,
                EquipmentSlotGroup.FEET))
                .exclusiveWith(enchantmentGetter.getOrThrow(EnchantmentTags.BOOTS_EXCLUSIVE))
                .withEffect(EnchantmentEffectComponents.ATTRIBUTES,
                        new EnchantmentAttributeEffect(Identifier.withDefaultNamespace("enchantment.depth_strider"),
                                Attributes.WATER_MOVEMENT_EFFICIENCY,
                                LevelBasedValue.perLevel(0.33333334F),
                                AttributeModifier.Operation.ADD_VALUE)));
        register(bootstrapContext, Enchantments.FROST_WALKER, Enchantment.enchantment(Enchantment.definition(
                itemGetter.getOrThrow(ItemTags.FOOT_ARMOR_ENCHANTABLE),
                2,
                2,
                Enchantment.dynamicCost(10, 10),
                Enchantment.dynamicCost(25, 10),
                4,
                EquipmentSlotGroup.FEET))
                .exclusiveWith(enchantmentGetter.getOrThrow(EnchantmentTags.BOOTS_EXCLUSIVE))
                .withEffect(EnchantmentEffectComponents.DAMAGE_IMMUNITY,
                        DamageImmunity.INSTANCE,
                        DamageSourceCondition.hasDamageSource(DamageSourcePredicate.Builder.damageType()
                                .tag(TagPredicate.is(DamageTypeTags.BURN_FROM_STEPPING))
                                .tag(TagPredicate.isNot(DamageTypeTags.BYPASSES_INVULNERABILITY))))
                .withEffect(EnchantmentEffectComponents.LOCATION_CHANGED,
                        new ReplaceDisk(
                                new LevelBasedValue.Clamped(
                                        LevelBasedValue.perLevel(3.0F, 1.0F),
                                        0.0F,
                                        16.0F),
                                LevelBasedValue.constant(1.0F),
                                new Vec3i(0, -1, 0),
                                Optional.of(BlockPredicate.allOf(
                                        BlockPredicate.matchesTag(
                                                new Vec3i(0, 1, 0), BlockTags.AIR),
                                        BlockPredicate.matchesBlocks(Blocks.WATER),
                                        BlockPredicate.matchesFluids(Fluids.WATER),
                                        BlockPredicate.unobstructed())),
                                BlockStateProvider.simple(Blocks.FROSTED_ICE),
                                Optional.of(GameEvent.BLOCK_PLACE)),
                        AllOfCondition.allOf(
                                LootItemEntityPropertyCondition.hasProperties(
                                        LootContext.EntityTarget.THIS,
                                        EntityPredicate.Builder.entity().flags(EntityFlagsPredicate.Builder.flags().setOnGround(true))),
                                InvertedLootItemCondition.invert(
                                        LootItemEntityPropertyCondition.hasProperties(
                                                LootContext.EntityTarget.THIS,
                                                EntityPredicate.Builder.entity().vehicle(EntityPredicate.Builder.entity()))))));
        register(bootstrapContext, Enchantments.BINDING_CURSE, Enchantment.enchantment(Enchantment.definition(
                itemGetter.getOrThrow(ItemTags.EQUIPPABLE_ENCHANTABLE),
                1,
                1,
                Enchantment.constantCost(25),
                Enchantment.constantCost(50),
                8,
                EquipmentSlotGroup.ARMOR))
                .withEffect(EnchantmentEffectComponents.PREVENT_ARMOR_CHANGE));
        EntityPredicate.Builder builder = net.minecraft.advancements.criterion.EntityPredicate.Builder.entity().periodicTick(5).flags(net.minecraft.advancements.criterion.EntityFlagsPredicate.Builder.flags().setIsFlying(false).setOnGround(true)).moving(MovementPredicate.horizontalSpeed(MinMaxBounds.Doubles.atLeast(9.999999747378752E-6))).movementAffectedBy(net.minecraft.advancements.criterion.LocationPredicate.Builder.location().setBlock(net.minecraft.advancements.criterion.BlockPredicate.Builder.block().of(blockGetter, BlockTags.SOUL_SPEED_BLOCKS)));
        AllOfCondition.Builder builder2 = AllOfCondition.allOf(new LootItemCondition.Builder[]{InvertedLootItemCondition.invert(LootItemEntityPropertyCondition.hasProperties(LootContext.EntityTarget.THIS, net.minecraft.advancements.criterion.EntityPredicate.Builder.entity().vehicle(net.minecraft.advancements.criterion.EntityPredicate.Builder.entity()))), AnyOfCondition.anyOf(new LootItemCondition.Builder[]{AllOfCondition.allOf(new LootItemCondition.Builder[]{EnchantmentActiveCheck.enchantmentActiveCheck(), LootItemEntityPropertyCondition.hasProperties(LootContext.EntityTarget.THIS, net.minecraft.advancements.criterion.EntityPredicate.Builder.entity().flags(net.minecraft.advancements.criterion.EntityFlagsPredicate.Builder.flags().setIsFlying(false))), AnyOfCondition.anyOf(new LootItemCondition.Builder[]{LootItemEntityPropertyCondition.hasProperties(LootContext.EntityTarget.THIS, net.minecraft.advancements.criterion.EntityPredicate.Builder.entity().movementAffectedBy(net.minecraft.advancements.criterion.LocationPredicate.Builder.location().setBlock(net.minecraft.advancements.criterion.BlockPredicate.Builder.block().of(blockGetter, BlockTags.SOUL_SPEED_BLOCKS)))), LootItemEntityPropertyCondition.hasProperties(LootContext.EntityTarget.THIS, net.minecraft.advancements.criterion.EntityPredicate.Builder.entity().flags(net.minecraft.advancements.criterion.EntityFlagsPredicate.Builder.flags().setOnGround(false)).build())})}), AllOfCondition.allOf(new LootItemCondition.Builder[]{EnchantmentActiveCheck.enchantmentInactiveCheck(), LootItemEntityPropertyCondition.hasProperties(LootContext.EntityTarget.THIS, net.minecraft.advancements.criterion.EntityPredicate.Builder.entity().movementAffectedBy(net.minecraft.advancements.criterion.LocationPredicate.Builder.location().setBlock(net.minecraft.advancements.criterion.BlockPredicate.Builder.block().of(blockGetter, BlockTags.SOUL_SPEED_BLOCKS))).flags(net.minecraft.advancements.criterion.EntityFlagsPredicate.Builder.flags().setIsFlying(false)))})})});
        register(bootstrapContext, Enchantments.SOUL_SPEED, Enchantment.enchantment(Enchantment.definition(
                itemGetter.getOrThrow(ItemTags.FOOT_ARMOR_ENCHANTABLE),
                1,
                3,
                Enchantment.dynamicCost(10, 10),
                Enchantment.dynamicCost(25, 10),
                8,
                EquipmentSlotGroup.FEET))
                .withEffect(EnchantmentEffectComponents.LOCATION_CHANGED,
                        AllOf.locationBasedEffects(
                                new EnchantmentAttributeEffect(Identifier.withDefaultNamespace("enchantment.soul_speed"),
                                        Attributes.MOVEMENT_SPEED,
                                        LevelBasedValue.perLevel(0.0405F, 0.0105F),
                                        AttributeModifier.Operation.ADD_VALUE),
                                new EnchantmentAttributeEffect(Identifier.withDefaultNamespace("enchantment.soul_speed"),
                                        Attributes.MOVEMENT_EFFICIENCY,
                                        LevelBasedValue.constant(1.0F),
                                        AttributeModifier.Operation.ADD_VALUE)),
                        builder2)
                .withEffect(EnchantmentEffectComponents.LOCATION_CHANGED,
                        new ChangeItemDamage(LevelBasedValue.constant(1.0F)),
                        AllOfCondition.allOf(
                                LootItemRandomChanceCondition.randomChance(
                                        EnchantmentLevelProvider.forEnchantmentLevel(
                                                LevelBasedValue.constant(0.04F))),
                                LootItemEntityPropertyCondition.hasProperties(LootContext.EntityTarget.THIS,
                                        EntityPredicate.Builder.entity().flags(EntityFlagsPredicate.Builder.flags().setOnGround(true)).movementAffectedBy(LocationPredicate.Builder.location().setBlock(net.minecraft.advancements.criterion.BlockPredicate.Builder.block().of(blockGetter, BlockTags.SOUL_SPEED_BLOCKS))))))
                .withEffect(EnchantmentEffectComponents.TICK,
                        new SpawnParticlesEffect(ParticleTypes.SOUL,
                                SpawnParticlesEffect.inBoundingBox(),
                                SpawnParticlesEffect.offsetFromEntityPosition(0.1F),
                                SpawnParticlesEffect.movementScaled(-0.2F),
                                SpawnParticlesEffect.fixedVelocity(ConstantFloat.of(0.1F)),
                                ConstantFloat.of(1.0F)),
                        LootItemEntityPropertyCondition.hasProperties(LootContext.EntityTarget.THIS, builder))
                .withEffect(EnchantmentEffectComponents.TICK,
                        new PlaySoundEffect(List.of(SoundEvents.SOUL_ESCAPE),
                                ConstantFloat.of(0.6F),
                                UniformFloat.of(0.6F, 1.0F)),
                        AllOfCondition.allOf(
                                LootItemRandomChanceCondition.randomChance(0.35F),
                                LootItemEntityPropertyCondition.hasProperties(LootContext.EntityTarget.THIS, builder))));
        register(bootstrapContext, Enchantments.SWIFT_SNEAK, Enchantment.enchantment(Enchantment.definition(
                itemGetter.getOrThrow(ItemTags.LEG_ARMOR_ENCHANTABLE),
                1,
                3,
                Enchantment.dynamicCost(25, 25),
                Enchantment.dynamicCost(75, 25),
                8,
                EquipmentSlotGroup.LEGS))
                .withEffect(EnchantmentEffectComponents.ATTRIBUTES,
                        new EnchantmentAttributeEffect(Identifier.withDefaultNamespace("enchantment.swift_sneak"),
                                Attributes.SNEAKING_SPEED,
                                LevelBasedValue.perLevel(0.15F),
                                AttributeModifier.Operation.ADD_VALUE)));
        register(bootstrapContext, Enchantments.SHARPNESS, Enchantment.enchantment(Enchantment.definition(
                itemGetter.getOrThrow(ItemTags.SHARP_WEAPON_ENCHANTABLE),
                itemGetter.getOrThrow(ItemTags.MELEE_WEAPON_ENCHANTABLE),
                10,
                5,
                Enchantment.dynamicCost(1, 11),
                Enchantment.dynamicCost(21, 11),
                1,
                EquipmentSlotGroup.MAINHAND))
                .exclusiveWith(enchantmentGetter.getOrThrow(EnchantmentTags.DAMAGE_EXCLUSIVE))
                .withEffect(EnchantmentEffectComponents.DAMAGE,
                        new AddValue(
                                LevelBasedValue.perLevel(1.0F, 0.5F))));
        register(bootstrapContext, Enchantments.SMITE, Enchantment.enchantment(Enchantment.definition(
                itemGetter.getOrThrow(ItemTags.WEAPON_ENCHANTABLE),
                itemGetter.getOrThrow(ItemTags.MELEE_WEAPON_ENCHANTABLE),
                5,
                5,
                Enchantment.dynamicCost(5, 8),
                Enchantment.dynamicCost(25, 8),
                2,
                EquipmentSlotGroup.MAINHAND))
                .exclusiveWith(enchantmentGetter.getOrThrow(EnchantmentTags.DAMAGE_EXCLUSIVE))
                .withEffect(EnchantmentEffectComponents.DAMAGE,
                        new AddValue(LevelBasedValue.perLevel(2.5F)),
                        LootItemEntityPropertyCondition.hasProperties(
                                LootContext.EntityTarget.THIS,
                                net.minecraft.advancements.criterion.EntityPredicate.Builder.entity().entityType(EntityTypePredicate.of(entityTypeGetter, EntityTypeTags.SENSITIVE_TO_SMITE)))));
        register(bootstrapContext, Enchantments.BANE_OF_ARTHROPODS, Enchantment.enchantment(Enchantment.definition(
                itemGetter.getOrThrow(ItemTags.WEAPON_ENCHANTABLE),
                itemGetter.getOrThrow(ItemTags.MELEE_WEAPON_ENCHANTABLE),
                5,
                5,
                Enchantment.dynamicCost(5, 8),
                Enchantment.dynamicCost(25, 8),
                2,
                EquipmentSlotGroup.MAINHAND))
                .exclusiveWith(enchantmentGetter.getOrThrow(EnchantmentTags.DAMAGE_EXCLUSIVE))
                .withEffect(EnchantmentEffectComponents.DAMAGE,
                        new AddValue(LevelBasedValue.perLevel(2.5F)),
                        LootItemEntityPropertyCondition.hasProperties(
                                LootContext.EntityTarget.THIS,
                                net.minecraft.advancements.criterion.EntityPredicate.Builder.entity().entityType(EntityTypePredicate.of(entityTypeGetter, EntityTypeTags.SENSITIVE_TO_BANE_OF_ARTHROPODS))))
                .withEffect(EnchantmentEffectComponents.POST_ATTACK,
                        EnchantmentTarget.ATTACKER,
                        EnchantmentTarget.VICTIM,
                        new ApplyMobEffect(HolderSet.direct(MobEffects.SLOWNESS),
                                LevelBasedValue.constant(1.5F),
                                LevelBasedValue.perLevel(1.5F, 0.5F),
                                LevelBasedValue.constant(3.0F),
                                LevelBasedValue.constant(3.0F)),
                        LootItemEntityPropertyCondition.hasProperties(
                                LootContext.EntityTarget.THIS,
                                net.minecraft.advancements.criterion.EntityPredicate.Builder.entity().entityType(EntityTypePredicate.of(entityTypeGetter, EntityTypeTags.SENSITIVE_TO_BANE_OF_ARTHROPODS)))
                                .and(DamageSourceCondition.hasDamageSource(DamageSourcePredicate.Builder.damageType().isDirect(true)))));
        register(bootstrapContext, Enchantments.KNOCKBACK, Enchantment.enchantment(Enchantment.definition(
                itemGetter.getOrThrow(ItemTags.MELEE_WEAPON_ENCHANTABLE),
                5,
                2,
                Enchantment.dynamicCost(5, 20),
                Enchantment.dynamicCost(55, 20),
                2,
                EquipmentSlotGroup.MAINHAND))
                .withEffect(EnchantmentEffectComponents.KNOCKBACK,
                        new AddValue(LevelBasedValue.perLevel(1.0F))));
        register(bootstrapContext, Enchantments.FIRE_ASPECT, Enchantment.enchantment(Enchantment.definition(
                itemGetter.getOrThrow(ItemTags.FIRE_ASPECT_ENCHANTABLE),
                itemGetter.getOrThrow(ItemTags.MELEE_WEAPON_ENCHANTABLE),
                2,
                2,
                Enchantment.dynamicCost(10, 20),
                Enchantment.dynamicCost(60, 20),
                4,
                EquipmentSlotGroup.MAINHAND))
                .withEffect(EnchantmentEffectComponents.POST_ATTACK,
                        EnchantmentTarget.ATTACKER,
                        EnchantmentTarget.VICTIM,
                        new Ignite(LevelBasedValue.perLevel(4.0F)),
                        DamageSourceCondition.hasDamageSource(DamageSourcePredicate.Builder.damageType().isDirect(true))));
        register(bootstrapContext, Enchantments.LOOTING, Enchantment.enchantment(Enchantment.definition(
                itemGetter.getOrThrow(ItemTags.MELEE_WEAPON_ENCHANTABLE),
                2,
                3,
                Enchantment.dynamicCost(15, 9),
                Enchantment.dynamicCost(65, 9),
                4,
                EquipmentSlotGroup.MAINHAND))
                .withEffect(EnchantmentEffectComponents.EQUIPMENT_DROPS,
                        EnchantmentTarget.ATTACKER,
                        EnchantmentTarget.VICTIM,
                        new AddValue(LevelBasedValue.perLevel(0.01F)),
                        LootItemEntityPropertyCondition.hasProperties(
                                LootContext.EntityTarget.ATTACKER,
                                net.minecraft.advancements.criterion.EntityPredicate.Builder.entity().entityType(EntityTypePredicate.of(entityTypeGetter, EntityType.PLAYER)))));
        register(bootstrapContext, Enchantments.SWEEPING_EDGE, Enchantment.enchantment(Enchantment.definition(
                itemGetter.getOrThrow(ItemTags.SWEEPING_ENCHANTABLE),
                2,
                3,
                Enchantment.dynamicCost(5, 9),
                Enchantment.dynamicCost(20, 9),
                4,
                EquipmentSlotGroup.MAINHAND))
                .withEffect(EnchantmentEffectComponents.ATTRIBUTES,
                        new EnchantmentAttributeEffect(Identifier.withDefaultNamespace("enchantment.sweeping_edge"),
                                Attributes.SWEEPING_DAMAGE_RATIO,
                                new LevelBasedValue.Fraction(
                                        LevelBasedValue.perLevel(1.0F),
                                        LevelBasedValue.perLevel(2.0F, 1.0F)), AttributeModifier.Operation.ADD_VALUE)));
        register(bootstrapContext, Enchantments.EFFICIENCY, Enchantment.enchantment(Enchantment.definition(
                itemGetter.getOrThrow(ItemTags.MINING_ENCHANTABLE),
                10,
                5,
                Enchantment.dynamicCost(1, 10),
                Enchantment.dynamicCost(51, 10),
                1,
                EquipmentSlotGroup.MAINHAND))
                .withEffect(EnchantmentEffectComponents.ATTRIBUTES,
                        new EnchantmentAttributeEffect(Identifier.withDefaultNamespace("enchantment.efficiency"),
                                Attributes.MINING_EFFICIENCY,
                                new LevelBasedValue.LevelsSquared(1.0F),
                                AttributeModifier.Operation.ADD_VALUE)));
        register(bootstrapContext, Enchantments.SILK_TOUCH, Enchantment.enchantment(Enchantment.definition(
                itemGetter.getOrThrow(ItemTags.MINING_LOOT_ENCHANTABLE),
                1,
                1,
                Enchantment.constantCost(15),
                Enchantment.constantCost(65),
                8,
                EquipmentSlotGroup.MAINHAND))
                .exclusiveWith(enchantmentGetter.getOrThrow(EnchantmentTags.MINING_EXCLUSIVE))
                .withEffect(EnchantmentEffectComponents.BLOCK_EXPERIENCE,
                        new SetValue(LevelBasedValue.constant(0.0F))));
        register(bootstrapContext, Enchantments.UNBREAKING, Enchantment.enchantment(Enchantment.definition(
                itemGetter.getOrThrow(ItemTags.DURABILITY_ENCHANTABLE),
                5,
                3,
                Enchantment.dynamicCost(5, 8),
                Enchantment.dynamicCost(55, 8),
                2,
                EquipmentSlotGroup.ANY))
                .withEffect(EnchantmentEffectComponents.ITEM_DAMAGE,
                        new RemoveBinomial(
                                new LevelBasedValue.Fraction(
                                        LevelBasedValue.perLevel(2.0F),
                                        LevelBasedValue.perLevel(10.0F, 5.0F))),
                        MatchTool.toolMatches(net.minecraft.advancements.criterion.ItemPredicate.Builder.item().of(itemGetter, ItemTags.ARMOR_ENCHANTABLE)))
                .withEffect(EnchantmentEffectComponents.ITEM_DAMAGE,
                        new RemoveBinomial(
                                new LevelBasedValue.Fraction(
                                        LevelBasedValue.perLevel(1.0F),
                                        LevelBasedValue.perLevel(2.0F, 1.0F))),
                        InvertedLootItemCondition.invert(MatchTool.toolMatches(net.minecraft.advancements.criterion.ItemPredicate.Builder.item().of(itemGetter, ItemTags.ARMOR_ENCHANTABLE)))));
        register(bootstrapContext, Enchantments.FORTUNE, Enchantment.enchantment(Enchantment.definition(
                itemGetter.getOrThrow(ItemTags.MINING_LOOT_ENCHANTABLE),
                2,
                3,
                Enchantment.dynamicCost(15, 9),
                Enchantment.dynamicCost(65, 9),
                4,
                EquipmentSlotGroup.MAINHAND))
                .exclusiveWith(enchantmentGetter.getOrThrow(EnchantmentTags.MINING_EXCLUSIVE)));
        register(bootstrapContext, Enchantments.POWER, Enchantment.enchantment(Enchantment.definition(
                itemGetter.getOrThrow(ItemTags.BOW_ENCHANTABLE),
                10,
                5,
                Enchantment.dynamicCost(1, 10),
                Enchantment.dynamicCost(16, 10),
                1,
                EquipmentSlotGroup.MAINHAND))
                .withEffect(EnchantmentEffectComponents.DAMAGE,
                        new AddValue(
                                LevelBasedValue.perLevel(1.0F, 0.5F)),
                        LootItemEntityPropertyCondition.hasProperties(
                                LootContext.EntityTarget.DIRECT_ATTACKER,
                                net.minecraft.advancements.criterion.EntityPredicate.Builder.entity().of(entityTypeGetter, EntityTypeTags.ARROWS).build())));
        register(bootstrapContext, Enchantments.PUNCH, Enchantment.enchantment(Enchantment.definition(
                itemGetter.getOrThrow(ItemTags.BOW_ENCHANTABLE),
                2,
                2,
                Enchantment.dynamicCost(12, 20),
                Enchantment.dynamicCost(37, 20),
                4,
                EquipmentSlotGroup.MAINHAND))
                .withEffect(EnchantmentEffectComponents.KNOCKBACK,
                        new AddValue(
                                LevelBasedValue.perLevel(1.0F)),
                        LootItemEntityPropertyCondition.hasProperties(
                                LootContext.EntityTarget.DIRECT_ATTACKER,
                                net.minecraft.advancements.criterion.EntityPredicate.Builder.entity().of(entityTypeGetter, EntityTypeTags.ARROWS).build())));
        register(bootstrapContext, Enchantments.FLAME, Enchantment.enchantment(Enchantment.definition(
                itemGetter.getOrThrow(ItemTags.BOW_ENCHANTABLE),
                2,
                1,
                Enchantment.constantCost(20),
                Enchantment.constantCost(50),
                4,
                EquipmentSlotGroup.MAINHAND))
                .withEffect(EnchantmentEffectComponents.PROJECTILE_SPAWNED,
                        new Ignite(
                                LevelBasedValue.constant(100.0F))));
        register(bootstrapContext, Enchantments.INFINITY, Enchantment.enchantment(Enchantment.definition(
                itemGetter.getOrThrow(ItemTags.BOW_ENCHANTABLE),
                1,
                1,
                Enchantment.constantCost(20),
                Enchantment.constantCost(50),
                8,
                EquipmentSlotGroup.MAINHAND))
                .exclusiveWith(enchantmentGetter.getOrThrow(EnchantmentTags.BOW_EXCLUSIVE))
                .withEffect(EnchantmentEffectComponents.AMMO_USE,
                        new SetValue(LevelBasedValue.constant(0.0F)),
                        MatchTool.toolMatches(net.minecraft.advancements.criterion.ItemPredicate.Builder.item().of(itemGetter, Items.ARROW))));
        register(bootstrapContext, Enchantments.LUCK_OF_THE_SEA, Enchantment.enchantment(Enchantment.definition(
                itemGetter.getOrThrow(ItemTags.FISHING_ENCHANTABLE),
                2,
                3,
                Enchantment.dynamicCost(15, 9),
                Enchantment.dynamicCost(65, 9),
                4,
                EquipmentSlotGroup.MAINHAND))
                .withEffect(EnchantmentEffectComponents.FISHING_LUCK_BONUS,
                        new AddValue(LevelBasedValue.perLevel(1.0F))));
        register(bootstrapContext, Enchantments.LURE, Enchantment.enchantment(Enchantment.definition(
                itemGetter.getOrThrow(ItemTags.FISHING_ENCHANTABLE),
                2,
                3,
                Enchantment.dynamicCost(15, 9),
                Enchantment.dynamicCost(65, 9),
                4,
                EquipmentSlotGroup.MAINHAND))
                .withEffect(EnchantmentEffectComponents.FISHING_TIME_REDUCTION,
                        new AddValue(
                                LevelBasedValue.perLevel(5.0F))));
        register(bootstrapContext, Enchantments.LOYALTY, Enchantment.enchantment(Enchantment.definition(
                itemGetter.getOrThrow(ItemTags.TRIDENT_ENCHANTABLE),
                5,
                3,
                Enchantment.dynamicCost(12, 7),
                Enchantment.constantCost(50),
                2,
                EquipmentSlotGroup.MAINHAND))
                .withEffect(EnchantmentEffectComponents.TRIDENT_RETURN_ACCELERATION,
                        new AddValue(
                                LevelBasedValue.perLevel(1.0F))));
        register(bootstrapContext, Enchantments.IMPALING, Enchantment.enchantment(Enchantment.definition(
                itemGetter.getOrThrow(ItemTags.TRIDENT_ENCHANTABLE),
                2,
                5,
                Enchantment.dynamicCost(1, 8),
                Enchantment.dynamicCost(21, 8),
                4,
                EquipmentSlotGroup.MAINHAND))
                .exclusiveWith(enchantmentGetter.getOrThrow(EnchantmentTags.DAMAGE_EXCLUSIVE))
                .withEffect(EnchantmentEffectComponents.DAMAGE,
                        new AddValue(LevelBasedValue.perLevel(2.5F)),
                        LootItemEntityPropertyCondition.hasProperties(LootContext.EntityTarget.THIS, net.minecraft.advancements.criterion.EntityPredicate.Builder.entity().entityType(EntityTypePredicate.of(entityTypeGetter, EntityTypeTags.SENSITIVE_TO_IMPALING)).build())));
        register(bootstrapContext, Enchantments.RIPTIDE, Enchantment.enchantment(Enchantment.definition(
                itemGetter.getOrThrow(ItemTags.TRIDENT_ENCHANTABLE),
                2,
                3,
                Enchantment.dynamicCost(17, 7),
                Enchantment.constantCost(50),
                4,
                EquipmentSlotGroup.HAND))
                .exclusiveWith(enchantmentGetter.getOrThrow(EnchantmentTags.RIPTIDE_EXCLUSIVE))
                .withSpecialEffect(EnchantmentEffectComponents.TRIDENT_SPIN_ATTACK_STRENGTH,
                        new AddValue(LevelBasedValue.perLevel(1.5F, 0.75F)))
                .withSpecialEffect(EnchantmentEffectComponents.TRIDENT_SOUND,
                        List.of(SoundEvents.TRIDENT_RIPTIDE_1,
                                SoundEvents.TRIDENT_RIPTIDE_2,
                                SoundEvents.TRIDENT_RIPTIDE_3)));
        register(bootstrapContext, Enchantments.LUNGE, Enchantment.enchantment(Enchantment.definition(
                itemGetter.getOrThrow(ItemTags.LUNGE_ENCHANTABLE),
                5,
                3,
                Enchantment.dynamicCost(5, 8),
                Enchantment.dynamicCost(25, 8),
                2,
                EquipmentSlotGroup.HAND))
                .withEffect(EnchantmentEffectComponents.POST_PIERCING_ATTACK,
                        AllOf.entityEffects(
                                new ChangeItemDamage(
                                        new LevelBasedValue.Constant(1.0F)),
                                new ApplyExhaustion(
                                        LevelBasedValue.perLevel(4.0F)),
                                new ApplyEntityImpulse(
                                        new Vec3(0.0, 0.0, 1.0),
                                        new Vec3(1.0, 0.0, 1.0),
                                        LevelBasedValue.perLevel(0.458F)),
                                new PlaySoundEffect(List.of(
                                        SoundEvents.LUNGE_1,
                                        SoundEvents.LUNGE_2,
                                        SoundEvents.LUNGE_3),
                                        ConstantFloat.of(1.0F),
                                        ConstantFloat.of(1.0F))),
                        AllOfCondition.allOf(
                                InvertedLootItemCondition.invert(LootItemEntityPropertyCondition.hasProperties(
                                        LootContext.EntityTarget.THIS,
                                        EntityPredicate.Builder.entity().vehicle(EntityPredicate.Builder.entity()))),
                                LootItemEntityPropertyCondition.hasProperties(
                                        LootContext.EntityTarget.THIS,
                                        EntityPredicate.Builder.entity().flags(EntityFlagsPredicate.Builder.flags().setIsFallFlying(false))),
                                LootItemEntityPropertyCondition.hasProperties(
                                        LootContext.EntityTarget.THIS,
                                        EntityPredicate.Builder.entity().flags(EntityFlagsPredicate.Builder.flags().setIsInWater(false))))));
        register(bootstrapContext, Enchantments.CHANNELING, Enchantment.enchantment(Enchantment.definition(
                itemGetter.getOrThrow(ItemTags.TRIDENT_ENCHANTABLE),
                1,
                1,
                Enchantment.constantCost(25),
                Enchantment.constantCost(50),
                8,
                EquipmentSlotGroup.MAINHAND))
                .withEffect(EnchantmentEffectComponents.POST_ATTACK,
                        EnchantmentTarget.ATTACKER,
                        EnchantmentTarget.VICTIM,
                        AllOf.entityEffects(
                                new SummonEntityEffect(HolderSet.direct(EntityType.LIGHTNING_BOLT.builtInRegistryHolder()), false),
                                new PlaySoundEffect(
                                        List.of(SoundEvents.TRIDENT_THUNDER),
                                        ConstantFloat.of(5.0F),
                                        ConstantFloat.of(1.0F))),
                        AllOfCondition.allOf(
                                WeatherCheck.weather().setThundering(true),
                                LootItemEntityPropertyCondition.hasProperties(
                                        LootContext.EntityTarget.THIS,
                                        EntityPredicate.Builder.entity().located(LocationPredicate.Builder.location().setCanSeeSky(true))),
                                LootItemEntityPropertyCondition.hasProperties(
                                        LootContext.EntityTarget.DIRECT_ATTACKER,
                                        EntityPredicate.Builder.entity().of(entityTypeGetter, EntityType.TRIDENT))))
                .withEffect(EnchantmentEffectComponents.HIT_BLOCK,
                        AllOf.entityEffects(
                                new SummonEntityEffect(
                                        HolderSet.direct(EntityType.LIGHTNING_BOLT.builtInRegistryHolder()), false),
                                new PlaySoundEffect(
                                        List.of(SoundEvents.TRIDENT_THUNDER),
                                        ConstantFloat.of(5.0F), ConstantFloat.of(1.0F))),
                        AllOfCondition.allOf(
                                WeatherCheck.weather().setThundering(true),
                                LootItemEntityPropertyCondition.hasProperties(
                                        LootContext.EntityTarget.THIS,
                                        EntityPredicate.Builder.entity().of(entityTypeGetter, EntityType.TRIDENT)),
                                LocationCheck.checkLocation(LocationPredicate.Builder.location().setCanSeeSky(true).setBlock(net.minecraft.advancements.criterion.BlockPredicate.Builder.block().of(blockGetter, BlockTags.LIGHTNING_RODS))))));
        register(bootstrapContext, Enchantments.MULTISHOT, Enchantment.enchantment(Enchantment.definition(
                itemGetter.getOrThrow(ItemTags.CROSSBOW_ENCHANTABLE),
                2,
                1,
                Enchantment.constantCost(20),
                Enchantment.constantCost(50),
                4,
                EquipmentSlotGroup.MAINHAND))
                .exclusiveWith(enchantmentGetter.getOrThrow(EnchantmentTags.CROSSBOW_EXCLUSIVE))
                .withEffect(EnchantmentEffectComponents.PROJECTILE_COUNT,
                        new AddValue(LevelBasedValue.perLevel(2.0F)))
                .withEffect(EnchantmentEffectComponents.PROJECTILE_SPREAD,
                        new AddValue(LevelBasedValue.perLevel(10.0F))));
        register(bootstrapContext, Enchantments.QUICK_CHARGE, Enchantment.enchantment(Enchantment.definition(
                itemGetter.getOrThrow(ItemTags.CROSSBOW_ENCHANTABLE),
                5,
                3,
                Enchantment.dynamicCost(12, 20),
                Enchantment.constantCost(50),
                2,
                EquipmentSlotGroup.MAINHAND,
                EquipmentSlotGroup.OFFHAND))
                .withSpecialEffect(EnchantmentEffectComponents.CROSSBOW_CHARGE_TIME,
                        new AddValue(
                                LevelBasedValue.perLevel(-0.25F)))
                .withSpecialEffect(EnchantmentEffectComponents.CROSSBOW_CHARGING_SOUNDS,
                        List.of(
                                new CrossbowItem.ChargingSounds(
                                        Optional.of(SoundEvents.CROSSBOW_QUICK_CHARGE_1), Optional.empty(),
                                        Optional.of(SoundEvents.CROSSBOW_LOADING_END)),
                                new CrossbowItem.ChargingSounds(
                                        Optional.of(SoundEvents.CROSSBOW_QUICK_CHARGE_2),
                                        Optional.empty(),
                                        Optional.of(SoundEvents.CROSSBOW_LOADING_END)),
                                new CrossbowItem.ChargingSounds(
                                        Optional.of(SoundEvents.CROSSBOW_QUICK_CHARGE_3),
                                        Optional.empty(),
                                        Optional.of(SoundEvents.CROSSBOW_LOADING_END)))));
        register(bootstrapContext, Enchantments.PIERCING, Enchantment.enchantment(Enchantment.definition(
                itemGetter.getOrThrow(ItemTags.CROSSBOW_ENCHANTABLE),
                10,
                4,
                Enchantment.dynamicCost(1, 10),
                Enchantment.constantCost(50),
                1,
                EquipmentSlotGroup.MAINHAND))
                .exclusiveWith(enchantmentGetter.getOrThrow(EnchantmentTags.CROSSBOW_EXCLUSIVE))
                .withEffect(EnchantmentEffectComponents.PROJECTILE_PIERCING,
                        new AddValue(
                                LevelBasedValue.perLevel(1.0F))));
        register(bootstrapContext, Enchantments.DENSITY, Enchantment.enchantment(Enchantment.definition(
                itemGetter.getOrThrow(ItemTags.MACE_ENCHANTABLE),
                5,
                5,
                Enchantment.dynamicCost(5, 8),
                Enchantment.dynamicCost(25, 8),
                2,
                EquipmentSlotGroup.MAINHAND))
                .exclusiveWith(enchantmentGetter.getOrThrow(EnchantmentTags.DAMAGE_EXCLUSIVE))
                .withEffect(EnchantmentEffectComponents.SMASH_DAMAGE_PER_FALLEN_BLOCK,
                        new AddValue(
                                LevelBasedValue.perLevel(0.5F))));
        register(bootstrapContext, Enchantments.BREACH, Enchantment.enchantment(Enchantment.definition(
                itemGetter.getOrThrow(ItemTags.MACE_ENCHANTABLE), 2, 4, Enchantment.dynamicCost(15, 9), Enchantment.dynamicCost(65, 9), 4, new EquipmentSlotGroup[]{EquipmentSlotGroup.MAINHAND})).exclusiveWith(enchantmentGetter.getOrThrow(EnchantmentTags.DAMAGE_EXCLUSIVE)).withEffect(EnchantmentEffectComponents.ARMOR_EFFECTIVENESS, new AddValue(LevelBasedValue.perLevel(-0.15F))));
        register(bootstrapContext, Enchantments.WIND_BURST, Enchantment.enchantment(Enchantment.definition(
                itemGetter.getOrThrow(ItemTags.MACE_ENCHANTABLE),
                2,
                3,
                Enchantment.dynamicCost(15, 9),
                Enchantment.dynamicCost(65, 9),
                4,
                EquipmentSlotGroup.MAINHAND))
                .withEffect(EnchantmentEffectComponents.POST_ATTACK,
                        EnchantmentTarget.ATTACKER,
                        EnchantmentTarget.ATTACKER,
                        new ExplodeEffect(
                                false,
                                Optional.empty(),
                                Optional.of(
                                        LevelBasedValue.lookup(
                                                List.of(1.2F, 1.75F, 2.2F),
                                                LevelBasedValue.perLevel(1.5F, 0.35F))),
                                blockGetter.get(BlockTags.BLOCKS_WIND_CHARGE_EXPLOSIONS).map(Function.identity()),
                                Vec3.ZERO,
                                LevelBasedValue.constant(3.5F),
                                false,
                                Level.ExplosionInteraction.TRIGGER,
                                ParticleTypes.GUST_EMITTER_SMALL,
                                ParticleTypes.GUST_EMITTER_LARGE,
                                WeightedList.of(),
                                SoundEvents.WIND_CHARGE_BURST),
                        LootItemEntityPropertyCondition.hasProperties(
                                LootContext.EntityTarget.DIRECT_ATTACKER,
                                net.minecraft.advancements.criterion.EntityPredicate.Builder.entity().flags(net.minecraft.advancements.criterion.EntityFlagsPredicate.Builder.flags().setIsFlying(false)).moving(MovementPredicate.fallDistance(MinMaxBounds.Doubles.atLeast(1.5))))));
        register(bootstrapContext, Enchantments.MENDING, Enchantment.enchantment(Enchantment.definition(
                itemGetter.getOrThrow(ItemTags.DURABILITY_ENCHANTABLE),
                2,
                1,
                Enchantment.dynamicCost(25, 25),
                Enchantment.dynamicCost(75, 25),
                4,
                EquipmentSlotGroup.ANY))
                .withEffect(EnchantmentEffectComponents.REPAIR_WITH_XP,
                        new MultiplyValue(
                                LevelBasedValue.constant(2.0F))));
        register(bootstrapContext, Enchantments.VANISHING_CURSE, Enchantment.enchantment(Enchantment.definition(
                itemGetter.getOrThrow(ItemTags.VANISHING_ENCHANTABLE),
                1,
                1,
                Enchantment.constantCost(25),
                Enchantment.constantCost(50),
                8,
                EquipmentSlotGroup.ANY))
                .withEffect(EnchantmentEffectComponents.PREVENT_EQUIPMENT_DROP));
    }

    private static void register(BootstrapContext<Enchantment> bootstrapContext, ResourceKey<Enchantment> resourceKey, Enchantment.Builder builder) {
        bootstrapContext.register(resourceKey, builder.build(resourceKey.identifier()));
    }

}
