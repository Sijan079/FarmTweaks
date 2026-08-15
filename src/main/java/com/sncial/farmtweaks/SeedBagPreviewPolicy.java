package com.sncial.farmtweaks;

final class SeedBagPreviewPolicy {
    private SeedBagPreviewPolicy() {}

    static boolean canPreview(boolean targetedFarmland, boolean targetAboveEmpty) {
        return targetedFarmland && targetAboveEmpty;
    }
}
