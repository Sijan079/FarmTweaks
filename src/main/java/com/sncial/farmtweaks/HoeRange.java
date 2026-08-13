package com.sncial.farmtweaks;

final class HoeRange {
    private HoeRange() {}

    static int sideLength(int tierSideLength, int efficiencyLevel) {
        return Math.max(1, tierSideLength) + Math.max(0, efficiencyLevel);
    }
}
