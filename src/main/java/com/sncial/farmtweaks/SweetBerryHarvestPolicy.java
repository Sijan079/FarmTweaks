package com.sncial.farmtweaks;

final class SweetBerryHarvestPolicy {
    private static final int MATURE_AGE = 3;

    private SweetBerryHarvestPolicy() {
    }

    static boolean awardsExperience(int age) {
        return age >= MATURE_AGE;
    }
}
