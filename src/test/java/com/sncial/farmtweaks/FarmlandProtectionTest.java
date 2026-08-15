package com.sncial.farmtweaks;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class FarmlandProtectionTest {
    @Test
    void cancels_trampling_only_when_protection_is_enabled() {
        assertTrue(FarmlandProtection.cancelTrample(true));
        assertFalse(FarmlandProtection.cancelTrample(false));
    }
}
