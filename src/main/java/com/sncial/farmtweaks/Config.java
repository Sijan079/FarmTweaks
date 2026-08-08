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
    private static final ModConfigSpec.BooleanValue ENABLE_CROP_XP_REWARDS;
    private static final ModConfigSpec.BooleanValue ENABLE_CUSTOM_WATER_HYDRATION_RANGE;
    private static final ModConfigSpec.BooleanValue INCLUDE_WATERLOGGED_HYDRATION_BLOCKS;
    private static final ModConfigSpec.BooleanValue ENABLE_GENERIC_AGE_CROP_HARVEST;
    private static final ModConfigSpec.BooleanValue USE_HARVEST_WHITELIST_TAG;
    private static final ModConfigSpec.BooleanValue ENABLE_SERENE_SEASONS_FORTUNE_GATING;
    private static final ModConfigSpec.BooleanValue ENABLE_SERENE_SEASONS_XP_BOOST;

    // Tunables (numbers / thresholds)
    private static final ModConfigSpec.IntValue XP_PER_CROP;
    private static final ModConfigSpec.IntValue SERENE_SEASONS_XP_BOOST;
    private static final ModConfigSpec.IntValue WATER_HYDRATION_HORIZONTAL_RANGE;
    private static final ModConfigSpec.IntValue WATER_HYDRATION_VERTICAL_RANGE;
    private static final ModConfigSpec.IntValue AOE_TILLING_COUNT_STEP;
    private static final ModConfigSpec.IntValue AOE_HARVEST_COUNT_STEP;

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
        ENABLE_CROP_XP_REWARDS = BUILDER
                .comment("If false, disables crop XP rewards regardless of xpPerCrop.")
                .define("cropXpRewards", true);
        ENABLE_CUSTOM_WATER_HYDRATION_RANGE = BUILDER
                .comment("Enables configurable farmland hydration range for water and optionally waterlogged blocks.")
                .define("customWaterHydrationRange", false);
        INCLUDE_WATERLOGGED_HYDRATION_BLOCKS = BUILDER
                .comment("If true, waterlogged blocks count as hydration sources for the custom hydration range.")
                .define("includeWaterloggedHydrationBlocks", true);

        BUILDER.comment("Compatibility options").push("compat");
        ENABLE_GENERIC_AGE_CROP_HARVEST = BUILDER
                .comment("If true, treat blocks with an integer 'age' property as harvestable crops (helps many crop mods).")
                .define("genericAgeCropHarvest", true);
        USE_HARVEST_WHITELIST_TAG = BUILDER
                .comment("If true, generic age-crop harvest only applies to blocks in the farmtweaks:right_click_harvestable tag.")
                .define("harvestWhitelistTagOnly", false);
        ENABLE_SERENE_SEASONS_FORTUNE_GATING = BUILDER
                .comment("If true and Serene Seasons is installed, FarmTweaks' extra Fortune crop bonus only applies to in-season crops.")
                .define("sereneSeasonsFortuneOnlyInSeason", false);
        ENABLE_SERENE_SEASONS_XP_BOOST = BUILDER
                .comment("If true and Serene Seasons is installed, in-season crops receive an extra XP reward.")
                .define("sereneSeasonsInSeasonXpBoost", false);
        BUILDER.pop();
        BUILDER.pop();

        // Tunables come after feature switches.
        BUILDER.comment("Tuning values").push("tuning");
        XP_PER_CROP = BUILDER
                .comment("XP awarded per mature crop harvested (use cropXpRewards=false to disable XP).")
                .defineInRange("xpPerCrop", 1, 1, 1000);
        SERENE_SEASONS_XP_BOOST = BUILDER
                .comment("Extra XP awarded for in-season crops when sereneSeasonsInSeasonXpBoost is enabled.")
                .defineInRange("sereneSeasonsXpBoostAmount", 1, 0, 1000);
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

    public static boolean enableCustomWaterHydrationRange() {
        return ENABLE_CUSTOM_WATER_HYDRATION_RANGE.get();
    }

    public static boolean includeWaterloggedHydrationBlocks() {
        return INCLUDE_WATERLOGGED_HYDRATION_BLOCKS.get();
    }

    public static int xpPerCrop() {
        return ENABLE_CROP_XP_REWARDS.get() ? XP_PER_CROP.get() : 0;
    }

    public static int xpForCrop(boolean inSeason) {
        int xp = xpPerCrop();
        if (xp > 0 && inSeason && enableSereneSeasonsXpBoost()) {
            xp += SERENE_SEASONS_XP_BOOST.get();
        }
        return xp;
    }

    public static boolean enableSereneSeasonsFortuneGating() {
        return ENABLE_SERENE_SEASONS_FORTUNE_GATING.get();
    }

    public static boolean enableSereneSeasonsXpBoost() {
        return ENABLE_SERENE_SEASONS_XP_BOOST.get();
    }

    public static int sereneSeasonsXpBoostAmount() {
        return SERENE_SEASONS_XP_BOOST.get();
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

    public static boolean enableGenericAgeCropHarvest() {
        return ENABLE_GENERIC_AGE_CROP_HARVEST.get();
    }

    public static boolean useHarvestWhitelistTag() {
        return USE_HARVEST_WHITELIST_TAG.get();
    }

    // --- Mutable accessors for UI integrations (e.g., Cloth Config) ---
    // Keep these narrowly scoped so game logic continues to read via the typed getters above.
    static void setEnableRightClickHarvest(boolean v) { ENABLE_RIGHT_CLICK_HARVEST.set(v); }
    static void setEnableFortuneCrops(boolean v) { ENABLE_FORTUNE_CROPS.set(v); }
    static void setEnableAoETilling(boolean v) { ENABLE_AOE_TILLING.set(v); }
    static void setEnableAoEHarvest(boolean v) { ENABLE_AOE_HARVEST.set(v); }
    static void setEnableSeedBags(boolean v) { ENABLE_SEED_BAGS.set(v); }
    static void setEnableCropXpRewards(boolean v) { ENABLE_CROP_XP_REWARDS.set(v); }
    static void setEnableCustomWaterHydrationRange(boolean v) { ENABLE_CUSTOM_WATER_HYDRATION_RANGE.set(v); }
    static void setIncludeWaterloggedHydrationBlocks(boolean v) { INCLUDE_WATERLOGGED_HYDRATION_BLOCKS.set(v); }
    static void setEnableGenericAgeCropHarvest(boolean v) { ENABLE_GENERIC_AGE_CROP_HARVEST.set(v); }
    static void setUseHarvestWhitelistTag(boolean v) { USE_HARVEST_WHITELIST_TAG.set(v); }
    static void setEnableSereneSeasonsFortuneGating(boolean v) { ENABLE_SERENE_SEASONS_FORTUNE_GATING.set(v); }
    static void setEnableSereneSeasonsXpBoost(boolean v) { ENABLE_SERENE_SEASONS_XP_BOOST.set(v); }

    static void setXpPerCrop(int v) { XP_PER_CROP.set(v); }
    static void setSereneSeasonsXpBoost(int v) { SERENE_SEASONS_XP_BOOST.set(v); }
    static void setWaterHydrationHorizontalRange(int v) { WATER_HYDRATION_HORIZONTAL_RANGE.set(v); }
    static void setWaterHydrationVerticalRange(int v) { WATER_HYDRATION_VERTICAL_RANGE.set(v); }
    static void setAoeTillingCountStep(int v) { AOE_TILLING_COUNT_STEP.set(v); }
    static void setAoeHarvestCountStep(int v) { AOE_HARVEST_COUNT_STEP.set(v); }

    static void save() {
        // Writes back to farmtweaks.toml
        SPEC.save();
    }
}
