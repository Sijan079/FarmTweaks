package com.sncial.farmtweaks;

import net.neoforged.neoforge.common.ModConfigSpec;

/**
 * NeoForge config (TOML) generated under the user's config folder.
 *
 * This replaces the previous custom YAML parser so configs are self-documenting,
 * validated, and consistent with the usual Forge/NeoForge modpack workflow.
 */
public final class Config {
    private Config() {}

    public static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();
    public static final ModConfigSpec SPEC;

    // Feature switches (top-level "what parts of the mod are on")
    private static final ModConfigSpec.BooleanValue ENABLE_RIGHT_CLICK_HARVEST;
    private static final ModConfigSpec.BooleanValue ENABLE_FORTUNE_CROPS;
    private static final ModConfigSpec.BooleanValue ENABLE_AOE_TILLING;
    private static final ModConfigSpec.BooleanValue ENABLE_AOE_HARVEST;
    private static final ModConfigSpec.BooleanValue ENABLE_SEED_BAGS;
    private static final ModConfigSpec.BooleanValue ENABLE_FLOWER_SEEDS;
    private static final ModConfigSpec.BooleanValue ENABLE_CROP_XP_REWARDS;
    private static final ModConfigSpec.BooleanValue ENABLE_CUSTOM_WATER_HYDRATION_RANGE;
    private static final ModConfigSpec.BooleanValue INCLUDE_WATERLOGGED_HYDRATION_BLOCKS;
    private static final ModConfigSpec.ConfigValue<String> SEED_BAG_AOE_SHAPE;

    // Tunables (numbers / thresholds)
    private static final ModConfigSpec.IntValue XP_PER_CROP;
    private static final ModConfigSpec.IntValue WATER_HYDRATION_HORIZONTAL_RANGE;
    private static final ModConfigSpec.IntValue WATER_HYDRATION_VERTICAL_RANGE;
    private static final ModConfigSpec.IntValue AOE_TILLING_COUNT_STEP;
    private static final ModConfigSpec.IntValue AOE_HARVEST_COUNT_STEP;
    private static final ModConfigSpec.IntValue FLOWER_SEED_DROP_CHANCE_PERCENT;

    static {
        BUILDER.comment("FarmTweaks configuration").push("farmtweaks");

        // Keep this section first: quick on/off switches for major features.
        BUILDER.comment("Feature switches (turn mod features on/off)").push("features");
        ENABLE_RIGHT_CLICK_HARVEST = BUILDER
                .comment("Enables right-click harvesting for mature crops.")
                .define("rightClickHarvest", true);
        ENABLE_FORTUNE_CROPS = BUILDER
                .comment("When harvesting with a hoe, applies a Fortune-style bonus to non-seed drops.")
                .define("fortuneCrops", true);
        ENABLE_AOE_HARVEST = BUILDER
                .comment("Enables AoE harvesting when using a hoe (disabled while sneaking).")
                .define("aoeHarvest", true);
        ENABLE_AOE_TILLING = BUILDER
                .comment("Enables AoE tilling when using a hoe (disabled while sneaking).")
                .define("aoeTilling", true);
        ENABLE_SEED_BAGS = BUILDER
                .comment("Enables Seed Bag items and their right-click behavior.")
                .define("seedBags", true);
        ENABLE_FLOWER_SEEDS = BUILDER
                .comment("Enables Flower Seeds and cultivable flower crops.")
                .define("flowerSeeds", true);
        ENABLE_CROP_XP_REWARDS = BUILDER
                .comment("If false, disables crop XP rewards regardless of xpPerCrop.")
                .define("cropXpRewards", true);
        ENABLE_CUSTOM_WATER_HYDRATION_RANGE = BUILDER
                .comment("Enables configurable farmland hydration range for water and optionally waterlogged blocks.")
                .define("customWaterHydrationRange", false);
        INCLUDE_WATERLOGGED_HYDRATION_BLOCKS = BUILDER
                .comment("If true, waterlogged blocks count as hydration sources for the custom hydration range.")
                .define("includeWaterloggedHydrationBlocks", true);

        BUILDER.pop();

        // Tunables come after feature switches.
        BUILDER.comment("Tuning values").push("tuning");
        XP_PER_CROP = BUILDER
                .comment("XP awarded per mature crop harvested (use cropXpRewards=false to disable XP).")
                .defineInRange("xpPerCrop", 1, 1, 1000);
        SEED_BAG_AOE_SHAPE = BUILDER
                .comment("Shape used for charged Seed Bag planting: square or radial.")
                .define("seedBagAoeShape", "square", value ->
                        value instanceof String shape
                                && ("square".equalsIgnoreCase(shape) || "radial".equalsIgnoreCase(shape)));
        WATER_HYDRATION_HORIZONTAL_RANGE = BUILDER
                .comment("Horizontal farmland hydration range for water when customWaterHydrationRange is enabled. Vanilla is 4.")
                .defineInRange("waterHydrationHorizontalRange", 4, 0, 32);
        WATER_HYDRATION_VERTICAL_RANGE = BUILDER
                .comment("Upward vertical farmland hydration range for water when customWaterHydrationRange is enabled. Vanilla checks the farmland level and one block above.")
                .defineInRange("waterHydrationVerticalRange", 1, 0, 8);
        AOE_HARVEST_COUNT_STEP = BUILDER
                .comment("AoE harvest scaling: max crops = 1 + (Efficiency level * aoeHarvestCountStep).")
                .defineInRange("aoeHarvestCountStep", 4, 0, 256);
        AOE_TILLING_COUNT_STEP = BUILDER
                .comment("AoE tilling scaling: max tilled blocks = 1 + (Efficiency level * aoeTillingCountStep).")
                .defineInRange("aoeTillingCountStep", 6, 0, 256);
        FLOWER_SEED_DROP_CHANCE_PERCENT = BUILDER
                .comment("Chance for a broken vanilla small flower to drop its matching Flower Seed.")
                .defineInRange("flowerSeedDropChancePercent", 25, 0, 100);
        BUILDER.pop();

        BUILDER.pop(); // farmtweaks
        SPEC = BUILDER.build();
    }

    public static boolean enableRightClickHarvest() {
        return ENABLE_RIGHT_CLICK_HARVEST.get();
    }

    public static boolean enableFortuneCrops() {
        return ENABLE_FORTUNE_CROPS.get();
    }

    public static boolean enableAoETilling() {
        return ENABLE_AOE_TILLING.get();
    }

    public static boolean enableAoEHarvest() {
        return ENABLE_AOE_HARVEST.get();
    }

    public static boolean enableSeedBags() {
        return ENABLE_SEED_BAGS.get();
    }

    public static boolean enableCropXpRewards() {
        return ENABLE_CROP_XP_REWARDS.get();
    }

    public static boolean enableFlowerSeeds() {
        return ENABLE_FLOWER_SEEDS.get();
    }

    public static boolean enableCustomWaterHydrationRange() {
        return ENABLE_CUSTOM_WATER_HYDRATION_RANGE.get();
    }

    public static boolean includeWaterloggedHydrationBlocks() {
        return INCLUDE_WATERLOGGED_HYDRATION_BLOCKS.get();
    }

    public static int xpPerCrop() {
        return ENABLE_CROP_XP_REWARDS.get() ? XP_PER_CROP.get() : 0;
    }

    public static int waterHydrationHorizontalRange() {
        return WATER_HYDRATION_HORIZONTAL_RANGE.get();
    }

    public static int waterHydrationVerticalRange() {
        return WATER_HYDRATION_VERTICAL_RANGE.get();
    }

    public static int aoeTillingCountStep() {
        return AOE_TILLING_COUNT_STEP.get();
    }

    public static int aoeHarvestCountStep() {
        return AOE_HARVEST_COUNT_STEP.get();
    }

    public static SeedBagAoeShape seedBagAoeShape() {
        return SeedBagAoeShape.fromConfig(SEED_BAG_AOE_SHAPE.get());
    }

    public static int flowerSeedDropChancePercent() {
        return FLOWER_SEED_DROP_CHANCE_PERCENT.get();
    }

}
