package com.sncial.farmtweaks;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class SeedBagAoeShapeTest {
    @Test
    void cycles_shapes_in_both_directions() {
        assertEquals(SeedBagAoeShape.RADIAL, SeedBagAoeShape.SQUARE.cycle(true));
        assertEquals(SeedBagAoeShape.SQUARE, SeedBagAoeShape.RADIAL.cycle(true));
        assertEquals(SeedBagAoeShape.RADIAL, SeedBagAoeShape.SQUARE.cycle(false));
        assertEquals(SeedBagAoeShape.SQUARE, SeedBagAoeShape.RADIAL.cycle(false));
    }
}
