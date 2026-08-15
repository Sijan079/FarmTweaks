package com.sncial.farmtweaks;

final class HoeRange {
    private HoeRange() {}

    static int tillRadius(int tierRange) {
        return Math.max(0, tierRange);
    }
}
