package com.sncial.farmtweaks;

final class SugarCaneHarvestPolicy {
    private SugarCaneHarvestPolicy() {
    }

    static int segmentCount(int baseY, int clickedY, int topY) {
        int firstHarvestY = Math.max(baseY + 1, clickedY);
        return Math.max(0, topY - firstHarvestY + 1);
    }
}
