package com.sncial.farmtweaks;

final class SpecialHarvestYieldPolicy {
    private SpecialHarvestYieldPolicy() {
    }

    static int cocoaYield(int randomBit) {
        return 5 + Math.max(0, Math.min(1, randomBit));
    }

    static int sweetBerryYield(int randomBit) {
        return 3 + Math.max(0, Math.min(1, randomBit));
    }
}
