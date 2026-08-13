package com.sncial.farmtweaks;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class HoeOperationLimitsTest {
    @Test
    void durability_limits_a_non_creative_aoe_operation() {
        assertEquals(1, HoeOperationLimits.maxActions(20, 1, false));
        assertEquals(5, HoeOperationLimits.maxActions(20, 5, false));
    }

    @Test
    void creative_operations_keep_the_configured_limit() {
        assertEquals(20, HoeOperationLimits.maxActions(20, 1, true));
    }
}
