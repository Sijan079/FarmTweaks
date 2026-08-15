package com.sncial.farmtweaks;

final class HarvestFortunePolicy {
    private HarvestFortunePolicy() {}

    static boolean hasExtraBonus(boolean replantedWithSeed, int fortuneLevel) {
        return hasExtraBonus(replantedWithSeed, false, fortuneLevel);
    }

    static boolean hasExtraBonus(boolean replantedWithSeed, boolean cocoa, int fortuneLevel) {
        return replantedWithSeed && !cocoa && fortuneLevel > 0;
    }
}
