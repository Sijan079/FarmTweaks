package com.sncial.farmtweaks;

final class HoeTillingTierSettings {
    private HoeTillingTierSettings() {}

    static Lengths normalize(int wood, int stone, int ironGold, int diamond, int netherite) {
        int resolvedNetherite = clamp(netherite, 4, 9);
        int resolvedDiamond = Math.min(clamp(diamond, 3, 8), resolvedNetherite - 1);
        int resolvedIronGold = Math.min(clamp(ironGold, 2, 7), resolvedDiamond - 1);
        int resolvedStone = Math.min(clamp(stone, 1, 6), resolvedIronGold - 1);
        int resolvedWood = Math.min(clamp(wood, 0, 5), resolvedStone - 1);
        return new Lengths(resolvedWood, resolvedStone, resolvedIronGold, resolvedDiamond, resolvedNetherite);
    }

    private static int clamp(int value, int min, int max) {
        return Math.max(min, Math.min(max, value));
    }

    record Lengths(int wood, int stone, int ironGold, int diamond, int netherite) {}
}
