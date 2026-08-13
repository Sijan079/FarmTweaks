package com.sncial.farmtweaks;

final class HoeOperationLimits {
    private HoeOperationLimits() {}

    static int maxActions(int configuredLimit, int remainingDurability, boolean creative) {
        return creative ? configuredLimit : Math.min(configuredLimit, Math.max(0, remainingDurability));
    }
}
