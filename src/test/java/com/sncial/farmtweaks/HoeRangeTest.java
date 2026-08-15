package com.sncial.farmtweaks;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class HoeRangeTest {
    @Test
    void added_range_keeps_the_vanilla_target_at_zero() {
        assertEquals(0, HoeRange.tillRadius(0));
        assertEquals(1, HoeRange.tillRadius(1));
        assertEquals(4, HoeRange.tillRadius(4));
    }

    @Test
    void added_range_never_becomes_negative() {
        assertEquals(0, HoeRange.tillRadius(-1));
    }
}
