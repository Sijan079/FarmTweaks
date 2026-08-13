package com.sncial.farmtweaks;

final class PumpkinRecipePolicy {
    private PumpkinRecipePolicy() {
    }

    static boolean replacesVanillaRecipe(String namespace, String path) {
        return "minecraft".equals(namespace) && "pumpkin_pie".equals(path);
    }
}
