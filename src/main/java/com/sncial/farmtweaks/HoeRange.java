package com.sncial.farmtweaks;

final class HoeRange {
    private HoeRange() {}

    static int tillSideLength(int tierSideLength) {
        return Math.max(1, tierSideLength);
    }
}
