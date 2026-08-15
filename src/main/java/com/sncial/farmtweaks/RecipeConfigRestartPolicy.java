package com.sncial.farmtweaks;

final class RecipeConfigRestartPolicy {
    private RecipeConfigRestartPolicy() {}

    static boolean requiresRestart(boolean originalPumpkinSlices, boolean originalOverrideRecipes,
                                   boolean savedPumpkinSlices, boolean savedOverrideRecipes) {
        return originalPumpkinSlices != savedPumpkinSlices || originalOverrideRecipes != savedOverrideRecipes;
    }

    static RecipeSettings settingsAfterDecision(boolean restart, boolean originalPumpkinSlices,
                                                boolean originalOverrideRecipes, boolean savedPumpkinSlices,
                                                boolean savedOverrideRecipes) {
        return restart
                ? new RecipeSettings(savedPumpkinSlices, savedOverrideRecipes)
                : new RecipeSettings(originalPumpkinSlices, originalOverrideRecipes);
    }

    record RecipeSettings(boolean pumpkinSlices, boolean overridePumpkinRecipes) {}
}
