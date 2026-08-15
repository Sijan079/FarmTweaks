package com.sncial.farmtweaks;

enum FarmTweaksConfigTab {
    HARVESTING,
    HOE_ACTIONS,
    SEED_BAGS,
    FLOWERS,
    HYDRATION,
    REWARDS,
    CONTROLS;

    String translationKey() {
        return "config.farmtweaks.tab." + name().toLowerCase();
    }
}
