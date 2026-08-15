package com.sncial.farmtweaks;

final class HarvestExperiencePolicy {
    private HarvestExperiencePolicy() {
    }

    static int totalExperience(int experiencePerHarvest, int harvestedCount) {
        return Math.max(0, experiencePerHarvest) * Math.max(0, harvestedCount);
    }
}
