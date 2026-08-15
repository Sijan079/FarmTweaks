package com.sncial.farmtweaks;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import org.junit.jupiter.api.Test;

class HoeTillingModeTest {
    @Test
    void cycles_through_till_untill_and_harvest() {
        assertEquals(HoeTillingMode.UNTILL, HoeTillingMode.TILL.next());
        assertEquals(HoeTillingMode.HARVEST, HoeTillingMode.UNTILL.next());
        assertEquals(HoeTillingMode.TILL, HoeTillingMode.HARVEST.next());
    }

    @Test
    void cycles_backwards_through_till_untill_and_harvest() {
        assertEquals(HoeTillingMode.HARVEST, HoeTillingMode.TILL.previous());
        assertEquals(HoeTillingMode.TILL, HoeTillingMode.UNTILL.previous());
        assertEquals(HoeTillingMode.UNTILL, HoeTillingMode.HARVEST.previous());
    }

    @Test
    void parses_unknown_saved_values_as_till() {
        assertEquals(HoeTillingMode.TILL, HoeTillingMode.fromSerializedName("unknown"));
    }

    @Test
    void migrates_legacy_none_mode_to_harvest() {
        assertEquals(HoeTillingMode.HARVEST, HoeTillingMode.fromSerializedName("NONE"));
    }

    @Test
    void harvest_mode_disables_tilling() {
        assertFalse(HoeTillingMode.HARVEST.allowsTilling());
    }

    @Test
    void mode_colors_match_their_area_previews() {
        assertEquals(0x4DE633, HoeTillingMode.TILL.hudColor());
        assertEquals(0xF25933, HoeTillingMode.UNTILL.hudColor());
        assertEquals(0xFFFFFF, HoeTillingMode.HARVEST.hudColor());
    }
}
