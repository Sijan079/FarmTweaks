package com.sncial.farmtweaks;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class PumpkinSliceDropsTest {
    @Test
    void uses_a_melon_style_count_with_a_nine_slice_cap() {
        assertEquals(3, PumpkinSliceDrops.count(0, 0));
        assertEquals(7, PumpkinSliceDrops.count(4, 0));
        assertEquals(9, PumpkinSliceDrops.count(4, 5));
    }

    @Test
    void prefers_the_farmers_delight_item_when_available() {
        assertEquals(PumpkinSliceDrops.Source.FARM_TWEAKS, PumpkinSliceDrops.source(false));
        assertEquals(PumpkinSliceDrops.Source.FARMERS_DELIGHT, PumpkinSliceDrops.source(true));
    }
}
