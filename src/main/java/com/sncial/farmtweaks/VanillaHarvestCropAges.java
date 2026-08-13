package com.sncial.farmtweaks;

final class VanillaHarvestCropAges {
    static final int NETHER_WART_MAX_AGE = 3;
    static final int COCOA_MAX_AGE = 2;

    private VanillaHarvestCropAges() {
    }

    static boolean isMature(int age, int maxAge) {
        return age >= maxAge;
    }
}
