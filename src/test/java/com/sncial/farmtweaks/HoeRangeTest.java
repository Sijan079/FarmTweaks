package com.sncial.farmtweaks;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class HoeRangeTest {
    @Test
    void tier_base_sizes_match_the_requested_ranges() {
        assertEquals(1, HoeRange.sideLength(1, 0));
        assertEquals(2, HoeRange.sideLength(2, 0));
        assertEquals(3, HoeRange.sideLength(3, 0));
        assertEquals(4, HoeRange.sideLength(4, 0));
        assertEquals(5, HoeRange.sideLength(5, 0));
    }

    @Test
    void efficiency_is_added_to_the_tier_side_length() {
        assertEquals(2, HoeRange.sideLength(1, 1));
        assertEquals(5, HoeRange.sideLength(3, 2));
        assertEquals(10, HoeRange.sideLength(5, 5));
    }
}
