package com.sncial.farmtweaks;

final class PumpkinSliceDrops {
    enum Source {
        FARM_TWEAKS,
        FARMERS_DELIGHT
    }

    private PumpkinSliceDrops() {}

    static int count(int baseRoll, int fortuneRoll) {
        return Math.min(9, 3 + Math.max(0, baseRoll) + Math.max(0, fortuneRoll));
    }

    static Source source(boolean farmersDelightAvailable) {
        return farmersDelightAvailable ? Source.FARMERS_DELIGHT : Source.FARM_TWEAKS;
    }
}
