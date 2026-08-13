package com.sncial.farmtweaks;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class HoeRangeTest {
    @Test
    void tier_base_sizes_match_the_requested_ranges() {
        assertEquals(1, HoeRange.tillSideLength(1));
        assertEquals(2, HoeRange.tillSideLength(2));
        assertEquals(3, HoeRange.tillSideLength(3));
        assertEquals(4, HoeRange.tillSideLength(4));
        assertEquals(5, HoeRange.tillSideLength(5));
    }

    @Test
    void tilling_side_length_ignores_efficiency() {
        assertEquals(1, HoeRange.tillSideLength(1));
        assertEquals(3, HoeRange.tillSideLength(3));
        assertEquals(5, HoeRange.tillSideLength(5));
    }
}
