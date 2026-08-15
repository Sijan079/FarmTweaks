package com.sncial.farmtweaks;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class HarvestExperiencePolicyTest {
    @Test
    void sugar_cane_awards_crop_xp_per_harvested_upper_segment() {
        assertEquals(3, HarvestExperiencePolicy.totalExperience(3, 1));
        assertEquals(9, HarvestExperiencePolicy.totalExperience(3, 3));
    }

    @Test
    void disabled_crop_xp_awards_nothing() {
        assertEquals(0, HarvestExperiencePolicy.totalExperience(0, 3));
    }
}
