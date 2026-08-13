package com.sncial.farmtweaks;

final class HoeEfficiencySpeed {
    private HoeEfficiencySpeed() {}

    static int bonus(int efficiencyLevel) {
        int level = Math.max(0, efficiencyLevel);
        return level == 0 ? 0 : level * level + 1;
    }

    static boolean needsCustomBonus(float baseDestroySpeed) {
        return baseDestroySpeed <= 1.0F;
    }
}
