package com.sncial.farmtweaks;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SweetBerryHarvestPolicyTest {
    @Test
    void awards_xp_only_for_a_mature_sweet_berry_bush() {
        assertFalse(SweetBerryHarvestPolicy.awardsExperience(2));
        assertTrue(SweetBerryHarvestPolicy.awardsExperience(3));
    }
}
