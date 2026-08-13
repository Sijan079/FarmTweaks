package com.sncial.farmtweaks;

enum HoeTillingMode {
    TILL,
    REVERT_TO_DIRT;

    static HoeTillingMode fromTarget(boolean farmland) {
        return farmland ? REVERT_TO_DIRT : TILL;
    }

    boolean usesVanillaFarmlandReversion() {
        return this == REVERT_TO_DIRT;
    }
}
