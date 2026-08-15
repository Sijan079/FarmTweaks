package com.sncial.farmtweaks;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SpecialHarvestYieldPolicyTest {
    @Test
    void cocoa_yield_is_five_to_six_before_replanting() {
        assertEquals(5, SpecialHarvestYieldPolicy.cocoaYield(0));
        assertEquals(6, SpecialHarvestYieldPolicy.cocoaYield(1));
    }

    @Test
    void sweet_berry_yield_is_three_to_four() {
        assertEquals(3, SpecialHarvestYieldPolicy.sweetBerryYield(0));
        assertEquals(4, SpecialHarvestYieldPolicy.sweetBerryYield(1));
    }
}
