package com.sncial.farmtweaks;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class HoeTillingModeTest {
    @Test
    void targeted_farmland_selects_the_revert_to_dirt_mode() {
        assertEquals(HoeTillingMode.REVERT_TO_DIRT, HoeTillingMode.fromTarget(true));
    }

    @Test
    void non_farmland_targets_select_the_normal_tilling_mode() {
        assertEquals(HoeTillingMode.TILL, HoeTillingMode.fromTarget(false));
    }
}
