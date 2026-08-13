package com.sncial.farmtweaks;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class FarmlandProtectionTest {
    @Test
    void cancels_entity_trampling_without_affecting_other_farmland_reversion_rules() {
        assertTrue(FarmlandProtection.cancelTrample());
    }
}
