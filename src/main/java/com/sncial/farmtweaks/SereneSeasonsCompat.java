package com.sncial.farmtweaks;

import java.lang.reflect.Method;

import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.fml.ModList;

final class SereneSeasonsCompat {
    private static final String MODID = "sereneseasons";
    private static Method isCropFertileMethod;
    private static boolean lookedUpFertilityMethod;

    private SereneSeasonsCompat() {}

    static boolean isLoaded() {
        try {
            return ModList.get().isLoaded(MODID);
        } catch (Throwable t) {
            return false;
        }
    }

    static boolean isCropInSeason(ServerLevel level, BlockPos pos, BlockState state) {
        if (!isLoaded()) {
            return true;
        }

        Method method = getIsCropFertileMethod();
        if (method == null) {
            return true;
        }

        ResourceLocation blockId = BuiltInRegistries.BLOCK.getKey(state.getBlock());
        if (blockId == null) {
            return true;
        }

        try {
            Object result = method.invoke(null, blockId.toString(), level, pos);
            return result instanceof Boolean inSeason ? inSeason : true;
        } catch (Throwable t) {
            FarmTweaks.LOGGER.warn("Failed to query Serene Seasons crop fertility for {} at {}: {}", blockId, pos, t.toString());
            return true;
        }
    }

    private static Method getIsCropFertileMethod() {
        if (lookedUpFertilityMethod) {
            return isCropFertileMethod;
        }

        lookedUpFertilityMethod = true;
        try {
            Class<?> fertilityClass = Class.forName("sereneseasons.init.ModFertility");
            isCropFertileMethod = fertilityClass.getMethod("isCropFertile", String.class, net.minecraft.world.level.Level.class, BlockPos.class);
        } catch (Throwable t) {
            FarmTweaks.LOGGER.warn("Serene Seasons is loaded, but FarmTweaks could not find ModFertility#isCropFertile: {}", t.toString());
            isCropFertileMethod = null;
        }
        return isCropFertileMethod;
    }
}
