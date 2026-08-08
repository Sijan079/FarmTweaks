package com.sncial.farmtweaks;

import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.neoforged.fml.ModList;
import net.neoforged.fml.ModContainer;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.function.Consumer;

/**
 * Optional integration with Cloth Config.
 *
 * Everything is done via reflection so FarmTweaks does not hard-depend on Cloth Config at runtime.
 * If Cloth Config is present, the NeoForge mod list "Config" button will open a nicer UI.
 */
final class ClothConfigCompat {
    private ClothConfigCompat() {}

    static void registerConfigScreen(ModContainer modContainer) {
        // Only exists on the physical client. Using reflection avoids classloading issues on dedicated servers.
        final Class<?> configFactoryClass;
        try {
            configFactoryClass = Class.forName("net.neoforged.neoforge.client.gui.IConfigScreenFactory");
        } catch (Throwable t) {
            return;
        }

        Object factoryProxy = Proxy.newProxyInstance(
                ClothConfigCompat.class.getClassLoader(),
                new Class<?>[]{configFactoryClass},
                new ConfigScreenFactoryHandler()
        );

        // ModContainer#registerExtensionPoint is available on both sides; the extension point type is client-only.
        //noinspection unchecked,rawtypes
        modContainer.registerExtensionPoint((Class) configFactoryClass, (java.util.function.Supplier) () -> factoryProxy);
        FarmTweaks.LOGGER.info("Registered in-game config screen (Cloth Config optional).");
    }

    private static final class ConfigScreenFactoryHandler implements InvocationHandler {
        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            String name = method.getName();
            if ("createScreen".equals(name) || "create".equals(name)) {
                Screen parent = (Screen) args[1];
                return createConfigScreen(parent);
            }
            // Default Object methods
            if ("toString".equals(name)) return "FarmTweaks ClothConfig Compat Factory";
            if ("hashCode".equals(name)) return System.identityHashCode(proxy);
            if ("equals".equals(name)) return proxy == args[0];
            return null;
        }
    }

    static Screen createConfigScreen(Screen parent) {
        if (!isClothConfigLoaded()) {
            return null;
        }

        try {
            // me.shedaniel.clothconfig2.api.ConfigBuilder
            Class<?> configBuilderClz = Class.forName("me.shedaniel.clothconfig2.api.ConfigBuilder");
            Object builder = configBuilderClz.getMethod("create").invoke(null);

            invoke(builder, "setParentScreen", new Class<?>[]{Screen.class}, new Object[]{parent});
            invoke(builder, "setTitle", new Class<?>[]{Component.class}, new Object[]{Component.translatable("config.farmtweaks.title")});

            Object entryBuilder = invoke(builder, "entryBuilder");

            Object catFeatures = invoke(builder, "getOrCreateCategory",
                    new Class<?>[]{Component.class},
                    new Object[]{Component.translatable("config.farmtweaks.category.features")});
            Object catCompat = invoke(builder, "getOrCreateCategory",
                    new Class<?>[]{Component.class},
                    new Object[]{Component.translatable("config.farmtweaks.category.compat")});
            Object catTuning = invoke(builder, "getOrCreateCategory",
                    new Class<?>[]{Component.class},
                    new Object[]{Component.translatable("config.farmtweaks.category.tuning")});

            // Features
            addBool(entryBuilder, catFeatures,
                    "config.farmtweaks.features.rightClickHarvest",
                    Config.enableRightClickHarvest(),
                    true,
                    "config.farmtweaks.features.rightClickHarvest.tt",
                    Config::setEnableRightClickHarvest);
            addBool(entryBuilder, catFeatures,
                    "config.farmtweaks.features.fortuneCrops",
                    Config.enableFortuneCrops(),
                    true,
                    "config.farmtweaks.features.fortuneCrops.tt",
                    Config::setEnableFortuneCrops);
            addBool(entryBuilder, catFeatures,
                    "config.farmtweaks.features.aoeHarvest",
                    Config.enableAoEHarvest(),
                    true,
                    "config.farmtweaks.features.aoeHarvest.tt",
                    Config::setEnableAoEHarvest);
            addBool(entryBuilder, catFeatures,
                    "config.farmtweaks.features.aoeTilling",
                    Config.enableAoETilling(),
                    true,
                    "config.farmtweaks.features.aoeTilling.tt",
                    Config::setEnableAoETilling);
            addBool(entryBuilder, catFeatures,
                    "config.farmtweaks.features.seedBags",
                    Config.enableSeedBags(),
                    true,
                    "config.farmtweaks.features.seedBags.tt",
                    Config::setEnableSeedBags);

            addBool(entryBuilder, catFeatures,
                    "config.farmtweaks.features.xpPerCropEnabled",
                    Config.enableCropXpRewards(),
                    true,
                    "config.farmtweaks.features.xpPerCropEnabled.tt",
                    enabled -> {
                        Config.setEnableCropXpRewards(enabled);
                        // Keep the stored tuning value sane when re-enabling via the feature toggle.
                        if (enabled && Config.xpPerCrop() <= 0) {
                            Config.setXpPerCrop(1);
                        }
                    });
            addBool(entryBuilder, catFeatures,
                    "config.farmtweaks.features.customWaterHydrationRange",
                    Config.enableCustomWaterHydrationRange(),
                    false,
                    "config.farmtweaks.features.customWaterHydrationRange.tt",
                    Config::setEnableCustomWaterHydrationRange);
            addBool(entryBuilder, catFeatures,
                    "config.farmtweaks.features.includeWaterloggedHydrationBlocks",
                    Config.includeWaterloggedHydrationBlocks(),
                    true,
                    "config.farmtweaks.features.includeWaterloggedHydrationBlocks.tt",
                    Config::setIncludeWaterloggedHydrationBlocks);

            // Compat
            addBool(entryBuilder, catCompat,
                    "config.farmtweaks.compat.genericAgeCropHarvest",
                    Config.enableGenericAgeCropHarvest(),
                    true,
                    "config.farmtweaks.compat.genericAgeCropHarvest.tt",
                    Config::setEnableGenericAgeCropHarvest);
            addBool(entryBuilder, catCompat,
                    "config.farmtweaks.compat.harvestWhitelistTagOnly",
                    Config.useHarvestWhitelistTag(),
                    false,
                    "config.farmtweaks.compat.harvestWhitelistTagOnly.tt",
                    Config::setUseHarvestWhitelistTag);
            addBool(entryBuilder, catCompat,
                    "config.farmtweaks.compat.sereneSeasonsFortuneOnlyInSeason",
                    Config.enableSereneSeasonsFortuneGating(),
                    false,
                    "config.farmtweaks.compat.sereneSeasonsFortuneOnlyInSeason.tt",
                    Config::setEnableSereneSeasonsFortuneGating);
            addBool(entryBuilder, catCompat,
                    "config.farmtweaks.compat.sereneSeasonsInSeasonXpBoost",
                    Config.enableSereneSeasonsXpBoost(),
                    false,
                    "config.farmtweaks.compat.sereneSeasonsInSeasonXpBoost.tt",
                    Config::setEnableSereneSeasonsXpBoost);

            // Tuning
            addInt(entryBuilder, catTuning,
                    "config.farmtweaks.tuning.xpPerCrop",
                    Config.xpPerCrop(),
                    1,
                    1, 1000,
                    "config.farmtweaks.tuning.xpPerCrop.tt",
                    Config::setXpPerCrop);
            addInt(entryBuilder, catTuning,
                    "config.farmtweaks.tuning.sereneSeasonsXpBoostAmount",
                    Config.sereneSeasonsXpBoostAmount(),
                    1,
                    0, 1000,
                    "config.farmtweaks.tuning.sereneSeasonsXpBoostAmount.tt",
                    Config::setSereneSeasonsXpBoost);
            addInt(entryBuilder, catTuning,
                    "config.farmtweaks.tuning.waterHydrationHorizontalRange",
                    Config.waterHydrationHorizontalRange(),
                    4,
                    0, 32,
                    "config.farmtweaks.tuning.waterHydrationHorizontalRange.tt",
                    Config::setWaterHydrationHorizontalRange);
            addInt(entryBuilder, catTuning,
                    "config.farmtweaks.tuning.waterHydrationVerticalRange",
                    Config.waterHydrationVerticalRange(),
                    1,
                    0, 8,
                    "config.farmtweaks.tuning.waterHydrationVerticalRange.tt",
                    Config::setWaterHydrationVerticalRange);
            addInt(entryBuilder, catTuning,
                    "config.farmtweaks.tuning.aoeHarvestCountStep",
                    Config.aoeHarvestCountStep(),
                    4,
                    0, 256,
                    "config.farmtweaks.tuning.aoeHarvestCountStep.tt",
                    Config::setAoeHarvestCountStep);
            addInt(entryBuilder, catTuning,
                    "config.farmtweaks.tuning.aoeTillingCountStep",
                    Config.aoeTillingCountStep(),
                    6,
                    0, 256,
                    "config.farmtweaks.tuning.aoeTillingCountStep.tt",
                    Config::setAoeTillingCountStep);

            // On save, write back the TOML.
            // ConfigBuilder has setSavingRunnable(Runnable). Keep it best-effort.
            try {
                invoke(builder, "setSavingRunnable", new Class<?>[]{Runnable.class}, new Object[]{(Runnable) Config::save});
            } catch (Throwable ignored) {
                // Some older Cloth Config versions used setSavingRunnable but we already did best-effort.
            }

            return (Screen) invoke(builder, "build");
        } catch (Throwable t) {
            FarmTweaks.LOGGER.warn("Cloth Config detected, but failed to build config screen: {}", t.toString());
            return null;
        }
    }

    private static boolean isClothConfigLoaded() {
        try {
            return ModList.get().isLoaded("cloth_config");
        } catch (Throwable t) {
            return false;
        }
    }

    private static void addBool(
            Object entryBuilder,
            Object category,
            String nameKey,
            boolean currentValue,
            boolean defaultValue,
            String tooltipKey,
            Consumer<Boolean> saveConsumer
    ) throws Exception {
        Object optionBuilder = invoke(entryBuilder, "startBooleanToggle",
                new Class<?>[]{Component.class, boolean.class},
                new Object[]{Component.translatable(nameKey), currentValue});

        invoke(optionBuilder, "setDefaultValue", new Class<?>[]{boolean.class}, new Object[]{defaultValue});
        invoke(optionBuilder, "setTooltip", new Class<?>[]{Component[].class}, new Object[]{new Component[]{Component.translatable(tooltipKey)}});
        invoke(optionBuilder, "setSaveConsumer", new Class<?>[]{Consumer.class}, new Object[]{saveConsumer});

        Object entry = invoke(optionBuilder, "build");
        invoke(category, "addEntry", new Class<?>[]{Class.forName("me.shedaniel.clothconfig2.api.AbstractConfigListEntry")}, new Object[]{entry});
    }

    private static void addInt(
            Object entryBuilder,
            Object category,
            String nameKey,
            int currentValue,
            int defaultValue,
            int min,
            int max,
            String tooltipKey,
            Consumer<Integer> saveConsumer
    ) throws Exception {
        Object optionBuilder = invoke(entryBuilder, "startIntField",
                new Class<?>[]{Component.class, int.class},
                new Object[]{Component.translatable(nameKey), currentValue});

        // Best-effort: these methods exist on most Cloth Config versions.
        try { invoke(optionBuilder, "setMin", new Class<?>[]{int.class}, new Object[]{min}); } catch (Throwable ignored) {}
        try { invoke(optionBuilder, "setMax", new Class<?>[]{int.class}, new Object[]{max}); } catch (Throwable ignored) {}
        invoke(optionBuilder, "setDefaultValue", new Class<?>[]{int.class}, new Object[]{defaultValue});
        invoke(optionBuilder, "setTooltip", new Class<?>[]{Component[].class}, new Object[]{new Component[]{Component.translatable(tooltipKey)}});
        invoke(optionBuilder, "setSaveConsumer", new Class<?>[]{Consumer.class}, new Object[]{saveConsumer});

        Object entry = invoke(optionBuilder, "build");
        invoke(category, "addEntry", new Class<?>[]{Class.forName("me.shedaniel.clothconfig2.api.AbstractConfigListEntry")}, new Object[]{entry});
    }

    private static Object invoke(Object target, String methodName) throws Exception {
        Method m = target.getClass().getMethod(methodName);
        return m.invoke(target);
    }

    private static Object invoke(Object target, String methodName, Class<?>[] paramTypes, Object[] args) throws Exception {
        Method m = target.getClass().getMethod(methodName, paramTypes);
        return m.invoke(target, args);
    }
}
