package com.sncial.farmtweaks;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class HoeTillingTierSettingsTest {
    @Test
    void defaults_define_one_added_ring_per_tool_tier() {
        assertEquals(
                new HoeTillingTierSettings.Lengths(0, 1, 2, 3, 4),
                HoeTillingTierSettings.normalize(0, 1, 2, 3, 4)
        );
    }

    @Test
    void higher_tiers_clamp_lower_tiers_to_a_strict_progression() {
        assertEquals(
                new HoeTillingTierSettings.Lengths(0, 1, 2, 3, 4),
                HoeTillingTierSettings.normalize(5, 6, 7, 8, 4)
        );
    }

    @Test
    void netherite_is_capped_at_nine() {
        assertEquals(9, HoeTillingTierSettings.normalize(0, 1, 2, 3, 12).netherite());
    }
}
