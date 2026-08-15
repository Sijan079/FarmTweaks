package com.sncial.farmtweaks;

import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.ConfirmScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.neoforged.neoforge.common.ModConfigSpec;

final class FarmTweaksClothConfigScreen {
    private FarmTweaksClothConfigScreen() {}

    static Screen create(Screen parent) {
        boolean originalPumpkinSlices = Config.enablePumpkinSlices();
        boolean originalOverridePumpkinRecipes = Config.overridePumpkinRecipes();
        ConfigBuilder builder = ConfigBuilder.create()
                .setParentScreen(parent)
                .setTitle(Component.translatable("config.farmtweaks.title"));
        builder.setSavingRunnable(() -> {
            Config.hoeTillingTierRanges();
            promptForRecipeRestart(parent, originalPumpkinSlices, originalOverridePumpkinRecipes);
        });
        ConfigEntryBuilder entries = builder.entryBuilder();

        ConfigCategory harvesting = category(builder, FarmTweaksConfigTab.HARVESTING);
        toggle(harvesting, entries, "rightClickHarvest", Config.ENABLE_RIGHT_CLICK_HARVEST, true);
        toggle(harvesting, entries, "pumpkinSlices", Config.ENABLE_PUMPKIN_SLICES, true);
        toggle(harvesting, entries, "overridePumpkinRecipes", Config.OVERRIDE_PUMPKIN_RECIPES, true);
        toggle(harvesting, entries, "preferCompatiblePumpkinSlice", Config.PREFER_COMPATIBLE_PUMPKIN_SLICE, true);

        ConfigCategory hoeActions = category(builder, FarmTweaksConfigTab.HOE_ACTIONS);
        toggle(hoeActions, entries, "fortuneCrops", Config.ENABLE_FORTUNE_CROPS, true);
        toggle(hoeActions, entries, "aoeHarvest", Config.ENABLE_AOE_HARVEST, true);
        slider(hoeActions, entries, "aoeHarvestCountStep", Config.AOE_HARVEST_COUNT_STEP, 0, 256, 4);
        toggle(hoeActions, entries, "aoeTilling", Config.ENABLE_AOE_TILLING, true);
        toggle(hoeActions, entries, "hoeMineableTag", Config.ENABLE_HOE_MINEABLE_TAG, true);
        slider(hoeActions, entries, "aoeTillingWoodRange", Config.AOE_TILLING_WOOD_RANGE, 0, 5, 0);
        slider(hoeActions, entries, "aoeTillingStoneRange", Config.AOE_TILLING_STONE_RANGE, 1, 6, 1);
        slider(hoeActions, entries, "aoeTillingIronGoldRange", Config.AOE_TILLING_IRON_GOLD_RANGE, 2, 7, 2);
        slider(hoeActions, entries, "aoeTillingDiamondRange", Config.AOE_TILLING_DIAMOND_RANGE, 3, 8, 3);
        slider(hoeActions, entries, "aoeTillingNetheriteRange", Config.AOE_TILLING_NETHERITE_RANGE, 4, 9, 4);

        ConfigCategory seedBags = category(builder, FarmTweaksConfigTab.SEED_BAGS);
        toggle(seedBags, entries, "seedBags", Config.ENABLE_SEED_BAGS, true);
        seedBags.addEntry(entries.startEnumSelector(label("seedBagAoeShape"), SeedBagAoeShape.class, Config.seedBagAoeShape())
                .setDefaultValue(SeedBagAoeShape.SQUARE).setTooltip(tooltip("seedBagAoeShape"))
                .setSaveConsumer(shape -> Config.SEED_BAG_AOE_SHAPE.set(shape.name().toLowerCase())).build());

        ConfigCategory flowers = category(builder, FarmTweaksConfigTab.FLOWERS);
        toggle(flowers, entries, "flowerSeeds", Config.ENABLE_FLOWER_SEEDS, true);
        slider(flowers, entries, "flowerSeedDropChancePercent", Config.FLOWER_SEED_DROP_CHANCE_PERCENT, 0, 100, 25);

        ConfigCategory hydration = category(builder, FarmTweaksConfigTab.HYDRATION);
        toggle(hydration, entries, "preventFarmlandTrampling", Config.PREVENT_FARMLAND_TRAMPLING, true);
        toggle(hydration, entries, "customWaterHydrationRange", Config.ENABLE_CUSTOM_WATER_HYDRATION_RANGE, false);
        toggle(hydration, entries, "includeWaterloggedHydrationBlocks", Config.INCLUDE_WATERLOGGED_HYDRATION_BLOCKS, true);
        slider(hydration, entries, "waterHydrationHorizontalRange", Config.WATER_HYDRATION_HORIZONTAL_RANGE, 0, 32, 4);
        slider(hydration, entries, "waterHydrationVerticalRange", Config.WATER_HYDRATION_VERTICAL_RANGE, 0, 8, 1);

        ConfigCategory rewards = category(builder, FarmTweaksConfigTab.REWARDS);
        toggle(rewards, entries, "xpPerCropEnabled", Config.ENABLE_CROP_XP_REWARDS, true);
        slider(rewards, entries, "xpPerCrop", Config.XP_PER_CROP, 1, 1000, 1);

        ConfigCategory controls = category(builder, FarmTweaksConfigTab.CONTROLS);
        toggle(controls, entries, "showModeHud", ClientConfig.SHOW_MODE_HUD, true);
        toggle(controls, entries, "enableModeScrolling", ClientConfig.ENABLE_MODE_SCROLLING, true);
        toggle(controls, entries, "invertModeScrollDirection", ClientConfig.INVERT_MODE_SCROLL_DIRECTION, false);
        return builder.build();
    }

    private static void promptForRecipeRestart(Screen parent, boolean originalPumpkinSlices, boolean originalOverridePumpkinRecipes) {
        if (!RecipeConfigRestartPolicy.requiresRestart(
                originalPumpkinSlices,
                originalOverridePumpkinRecipes,
                Config.enablePumpkinSlices(),
                Config.overridePumpkinRecipes())) {
            return;
        }

        Minecraft minecraft = Minecraft.getInstance();
        minecraft.tell(() -> minecraft.setScreen(new ConfirmScreen(
                restart -> {
                    if (restart) {
                        minecraft.stop();
                        return;
                    }
                    RecipeConfigRestartPolicy.RecipeSettings restored = RecipeConfigRestartPolicy.settingsAfterDecision(
                            false,
                            originalPumpkinSlices,
                            originalOverridePumpkinRecipes,
                            Config.enablePumpkinSlices(),
                            Config.overridePumpkinRecipes()
                    );
                    Config.ENABLE_PUMPKIN_SLICES.set(restored.pumpkinSlices());
                    Config.OVERRIDE_PUMPKIN_RECIPES.set(restored.overridePumpkinRecipes());
                    minecraft.setScreen(parent);
                },
                Component.translatable("config.farmtweaks.restartRecipes.title"),
                Component.translatable("config.farmtweaks.restartRecipes.message")
        )));
    }

    private static ConfigCategory category(ConfigBuilder builder, FarmTweaksConfigTab tab) {
        return builder.getOrCreateCategory(Component.translatable(tab.translationKey()));
    }

    private static void toggle(ConfigCategory category, ConfigEntryBuilder entries, String key, ModConfigSpec.BooleanValue value, boolean defaultValue) {
        category.addEntry(entries.startBooleanToggle(label(key), value.get()).setDefaultValue(defaultValue)
                .setTooltip(tooltip(key)).setSaveConsumer(value::set).build());
    }

    private static void slider(ConfigCategory category, ConfigEntryBuilder entries, String key, ModConfigSpec.IntValue value, int min, int max, int defaultValue) {
        category.addEntry(entries.startIntSlider(label(key), value.get(), min, max).setDefaultValue(defaultValue)
                .setTooltip(tooltip(key)).setSaveConsumer(value::set).build());
    }

    private static Component label(String key) { return Component.translatable(labelKey(key)); }
    private static Component tooltip(String key) { return Component.translatable(labelKey(key) + ".tt"); }
    private static String labelKey(String key) {
        return "config.farmtweaks." + switch (key) {
            case "aoeHarvest", "aoeTilling", "rightClickHarvest", "fortuneCrops", "seedBags", "flowerSeeds",
                    "xpPerCropEnabled", "customWaterHydrationRange", "includeWaterloggedHydrationBlocks", "pumpkinSlices", "overridePumpkinRecipes", "preferCompatiblePumpkinSlice", "hoeMineableTag", "preventFarmlandTrampling" -> "features.";
            case "showModeHud", "enableModeScrolling", "invertModeScrollDirection" -> "controls.";
            default -> "tuning.";
        } + key;
    }
}
