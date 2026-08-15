package com.sncial.farmtweaks;

final class HoeInteractionPolicy {
    private HoeInteractionPolicy() {}

    static boolean ownsVanillaTillingAction(boolean aoeTillingEnabled, boolean isTillingTarget) {
        return aoeTillingEnabled && isTillingTarget;
    }
}
