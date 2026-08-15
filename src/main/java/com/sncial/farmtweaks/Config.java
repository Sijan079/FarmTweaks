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
    static final ModConfigSpec.BooleanValue ENABLE_RIGHT_CLICK_HARVEST;
    static final ModConfigSpec.BooleanValue ENABLE_FORTUNE_CROPS;
    static final ModConfigSpec.BooleanValue ENABLE_AOE_TILLING;
    static final ModConfigSpec.BooleanValue ENABLE_AOE_HARVEST;
    static final ModConfigSpec.BooleanValue ENABLE_SEED_BAGS;
    static final ModConfigSpec.BooleanValue ENABLE_FLOWER_SEEDS;
    static final ModConfigSpec.BooleanValue ENABLE_CROP_XP_REWARDS;
    static final ModConfigSpec.BooleanValue ENABLE_CUSTOM_WATER_HYDRATION_RANGE;
    static final ModConfigSpec.BooleanValue INCLUDE_WATERLOGGED_HYDRATION_BLOCKS;
    static final ModConfigSpec.BooleanValue ENABLE_PUMPKIN_SLICES;
    static final ModConfigSpec.BooleanValue OVERRIDE_PUMPKIN_RECIPES;
    static final ModConfigSpec.BooleanValue PREFER_COMPATIBLE_PUMPKIN_SLICE;
    static final ModConfigSpec.BooleanValue ENABLE_HOE_MINEABLE_TAG;
    static final ModConfigSpec.BooleanValue PREVENT_FARMLAND_TRAMPLING;
    static final ModConfigSpec.ConfigValue<String> SEED_BAG_AOE_SHAPE;

    // Tunables (numbers / thresholds)
    static final ModConfigSpec.IntValue XP_PER_CROP;
    static final ModConfigSpec.IntValue WATER_HYDRATION_HORIZONTAL_RANGE;
    static final ModConfigSpec.IntValue WATER_HYDRATION_VERTICAL_RANGE;
    static final ModConfigSpec.IntValue AOE_HARVEST_COUNT_STEP;
    static final ModConfigSpec.IntValue AOE_TILLING_WOOD_RANGE;
    static final ModConfigSpec.IntValue AOE_TILLING_STONE_RANGE;
    static final ModConfigSpec.IntValue AOE_TILLING_IRON_GOLD_RANGE;
    static final ModConfigSpec.IntValue AOE_TILLING_DIAMOND_RANGE;
    static final ModConfigSpec.IntValue AOE_TILLING_NETHERITE_RANGE;
    static final ModConfigSpec.IntValue FLOWER_SEED_DROP_CHANCE_PERCENT;

    static {
        BUILDER.comment("FarmTweaks configuration").translation("config.farmtweaks.title").push("farmtweaks");

        // Keep this section first: quick on/off switches for major features.
        BUILDER.comment("Feature switches (turn mod features on/off)").translation("config.farmtweaks.category.features").push("features");
        ENABLE_RIGHT_CLICK_HARVEST = BUILDER
                .comment("Enables right-click harvesting for mature crops.")
                .translation("config.farmtweaks.features.rightClickHarvest")
                .define("rightClickHarvest", true);
        ENABLE_FORTUNE_CROPS = BUILDER
                .comment("When harvesting seed-replanting crops with a hoe, applies a Fortune-style bonus to non-seed drops. Carrots and potatoes use vanilla Fortune only.")
                .translation("config.farmtweaks.features.fortuneCrops")
                .define("fortuneCrops", true);
        ENABLE_AOE_HARVEST = BUILDER
                .comment("Enables AoE harvesting when using a hoe (disabled while sneaking).")
                .translation("config.farmtweaks.features.aoeHarvest")
                .define("aoeHarvest", true);
        ENABLE_AOE_TILLING = BUILDER
                .comment("Enables AoE tilling when using a hoe (disabled while sneaking).")
                .translation("config.farmtweaks.features.aoeTilling")
                .define("aoeTilling", true);
        ENABLE_SEED_BAGS = BUILDER
                .comment("Enables Seed Bag items and their right-click behavior.")
                .translation("config.farmtweaks.features.seedBags")
                .define("seedBags", true);
        ENABLE_FLOWER_SEEDS = BUILDER
                .comment("Enables Flower Seeds and cultivable flower crops.")
                .translation("config.farmtweaks.features.flowerSeeds")
                .define("flowerSeeds", true);
        ENABLE_CROP_XP_REWARDS = BUILDER
                .comment("If false, disables crop XP rewards regardless of xpPerCrop.")
                .translation("config.farmtweaks.features.xpPerCropEnabled")
                .define("cropXpRewards", true);
        ENABLE_CUSTOM_WATER_HYDRATION_RANGE = BUILDER
                .comment("Enables configurable farmland hydration range for water and optionally waterlogged blocks.")
                .translation("config.farmtweaks.features.customWaterHydrationRange")
                .define("customWaterHydrationRange", false);
        INCLUDE_WATERLOGGED_HYDRATION_BLOCKS = BUILDER
                .comment("If true, waterlogged blocks count as hydration sources for the custom hydration range.")
                .translation("config.farmtweaks.features.includeWaterloggedHydrationBlocks")
                .define("includeWaterloggedHydrationBlocks", true);
        ENABLE_PUMPKIN_SLICES = BUILDER
                .comment("When enabled, pumpkins drop slices. When disabled, pumpkins use their vanilla block drop.")
                .translation("config.farmtweaks.features.pumpkinSlices")
                .define("pumpkinSlices", true);
        OVERRIDE_PUMPKIN_RECIPES = BUILDER.comment("When pumpkin slices are enabled, replaces the vanilla pumpkin pie recipe with the slice recipe.")
                .translation("config.farmtweaks.features.overridePumpkinRecipes").define("overridePumpkinRecipes", true);
        PREFER_COMPATIBLE_PUMPKIN_SLICE = BUILDER.comment("When another mod provides pumpkin slices, such as Farmer's Delight, drop that item instead of FarmTweaks' own slice.")
                .translation("config.farmtweaks.features.preferCompatiblePumpkinSlice").define("preferCompatiblePumpkinSlice", true);
        ENABLE_HOE_MINEABLE_TAG = BUILDER
                .comment("Enables FarmTweaks' hoe mineable blocks. Requires restarting the game to apply tag changes.")
                .translation("config.farmtweaks.features.hoeMineableTag")
                .define("hoeMineableTag", true);
        PREVENT_FARMLAND_TRAMPLING = BUILDER
                .comment("Prevents entities from trampling farmland into dirt.")
                .translation("config.farmtweaks.features.preventFarmlandTrampling")
                .define("preventFarmlandTrampling", true);

        BUILDER.pop();

        // Tunables come after feature switches.
        BUILDER.comment("Tuning values").translation("config.farmtweaks.category.tuning").push("tuning");
        XP_PER_CROP = BUILDER
                .comment("XP awarded per mature crop harvested (use cropXpRewards=false to disable XP).")
                .translation("config.farmtweaks.tuning.xpPerCrop")
                .defineInRange("xpPerCrop", 1, 1, 1000);
        SEED_BAG_AOE_SHAPE = BUILDER
                .comment("Shape used for charged Seed Bag planting: square or radial.")
                .translation("config.farmtweaks.tuning.seedBagAoeShape")
                .define("seedBagAoeShape", "square", value ->
                        value instanceof String shape
                                && ("square".equalsIgnoreCase(shape) || "radial".equalsIgnoreCase(shape)));
        WATER_HYDRATION_HORIZONTAL_RANGE = BUILDER
                .comment("Horizontal farmland hydration range for water when customWaterHydrationRange is enabled. Vanilla is 4.")
                .translation("config.farmtweaks.tuning.waterHydrationHorizontalRange")
                .defineInRange("waterHydrationHorizontalRange", 4, 0, 32);
        WATER_HYDRATION_VERTICAL_RANGE = BUILDER
                .comment("Upward vertical farmland hydration range for water when customWaterHydrationRange is enabled. Vanilla checks the farmland level and one block above.")
                .translation("config.farmtweaks.tuning.waterHydrationVerticalRange")
                .defineInRange("waterHydrationVerticalRange", 1, 0, 8);
        AOE_HARVEST_COUNT_STEP = BUILDER
                .comment("AoE harvest scaling: max crops = 1 + (Efficiency level * aoeHarvestCountStep).")
                .translation("config.farmtweaks.tuning.aoeHarvestCountStep")
                .defineInRange("aoeHarvestCountStep", 4, 0, 256);
        AOE_TILLING_WOOD_RANGE = tillingRange("aoeTillingWoodRange", 0, 0, 5, "Wood hoe");
        AOE_TILLING_STONE_RANGE = tillingRange("aoeTillingStoneRange", 1, 1, 6, "Stone hoe");
        AOE_TILLING_IRON_GOLD_RANGE = tillingRange("aoeTillingIronGoldRange", 2, 2, 7, "Iron and gold hoes");
        AOE_TILLING_DIAMOND_RANGE = tillingRange("aoeTillingDiamondRange", 3, 3, 8, "Diamond hoe");
        AOE_TILLING_NETHERITE_RANGE = tillingRange("aoeTillingNetheriteRange", 4, 4, 9, "Netherite hoe");
        FLOWER_SEED_DROP_CHANCE_PERCENT = BUILDER
                .comment("Chance for a broken vanilla small flower to drop its matching Flower Seed.")
                .translation("config.farmtweaks.tuning.flowerSeedDropChancePercent")
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

    public static int aoeHarvestCountStep() {
        return AOE_HARVEST_COUNT_STEP.get();
    }

    public static boolean enablePumpkinSlices() { return ENABLE_PUMPKIN_SLICES.get(); }
    public static boolean overridePumpkinRecipes() { return OVERRIDE_PUMPKIN_RECIPES.get(); }
    public static boolean preferCompatiblePumpkinSlice() { return PREFER_COMPATIBLE_PUMPKIN_SLICE.get(); }

    public static boolean enableHoeMineableTag() { return ENABLE_HOE_MINEABLE_TAG.get(); }

    public static boolean preventFarmlandTrampling() { return PREVENT_FARMLAND_TRAMPLING.get(); }

    public static HoeTillingTierSettings.Lengths hoeTillingTierRanges() {
        HoeTillingTierSettings.Lengths resolved = HoeTillingTierSettings.normalize(
                AOE_TILLING_WOOD_RANGE.get(),
                AOE_TILLING_STONE_RANGE.get(),
                AOE_TILLING_IRON_GOLD_RANGE.get(),
                AOE_TILLING_DIAMOND_RANGE.get(),
                AOE_TILLING_NETHERITE_RANGE.get()
        );
        AOE_TILLING_WOOD_RANGE.set(resolved.wood());
        AOE_TILLING_STONE_RANGE.set(resolved.stone());
        AOE_TILLING_IRON_GOLD_RANGE.set(resolved.ironGold());
        AOE_TILLING_DIAMOND_RANGE.set(resolved.diamond());
        AOE_TILLING_NETHERITE_RANGE.set(resolved.netherite());
        return resolved;
    }

    private static ModConfigSpec.IntValue tillingRange(String key, int defaultValue, int min, int max, String toolName) {
        return BUILDER.comment(toolName + " AoE tilling range added around the targeted block.")
                .translation("config.farmtweaks.tuning." + key)
                .defineInRange(key, defaultValue, min, max);
    }

    public static SeedBagAoeShape seedBagAoeShape() {
        return SeedBagAoeShape.fromConfig(SEED_BAG_AOE_SHAPE.get());
    }

    public static int flowerSeedDropChancePercent() {
        return FLOWER_SEED_DROP_CHANCE_PERCENT.get();
    }

}
