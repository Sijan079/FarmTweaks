package com.sncial.farmtweaks;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PumpkinDropPolicyTest {
    @Test
    void custom_pumpkin_slices_can_be_disabled() {
        assertTrue(PumpkinDropPolicy.useSlices(true));
        assertFalse(PumpkinDropPolicy.useSlices(false));
    }
}
