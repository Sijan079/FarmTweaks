package com.sncial.farmtweaks;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;

import org.junit.jupiter.api.Test;

class SeedBagTierTest {
    @Test
    void tiers_define_their_storage_capacities_and_planting_radii() {
        assertEquals(4 * 64, SeedBagTier.BASIC.capacity());
        assertEquals(0, SeedBagTier.BASIC.plantingRadius(false));

        assertEquals(9 * 64, SeedBagTier.GOLD.capacity());
        assertEquals(1, SeedBagTier.GOLD.plantingRadius(false));

        assertEquals(18 * 64, SeedBagTier.DIAMOND.capacity());
        assertEquals(2, SeedBagTier.DIAMOND.plantingRadius(false));
    }

    @Test
    void sneaking_reduces_every_tier_to_single_block_planting() {
        assertEquals(0, SeedBagTier.BASIC.plantingRadius(true));
        assertEquals(0, SeedBagTier.GOLD.plantingRadius(true));
        assertEquals(0, SeedBagTier.DIAMOND.plantingRadius(true));

        assertEquals(0, SeedBagTier.BASIC.plantingRadius(false));
        assertEquals(1, SeedBagTier.GOLD.plantingRadius(false));
        assertEquals(2, SeedBagTier.DIAMOND.plantingRadius(false));
    }

    @Test
    void square_and_radial_shapes_include_the_expected_positions() {
        assertEquals(true, SeedBagAoeShape.SQUARE.includes(1, 1, 1));
        assertEquals(true, SeedBagAoeShape.RADIAL.includes(1, 0, 1));
        assertEquals(false, SeedBagAoeShape.RADIAL.includes(1, 1, 1));
    }

    @Test
    void upgraded_tiers_define_shiny_gui_marker_palettes() {
        assertArrayEquals(new int[]{0xFFFFF2A6, 0xFFF5C542, 0xFFC98718, 0xFF7A3E00}, SeedBagTier.GOLD.guiMarkerPixels());
        assertArrayEquals(new int[]{0xFFE7FFFF, 0xFF59E1DD, 0xFF139AA6, 0xFF07536B}, SeedBagTier.DIAMOND.guiMarkerPixels());
    }
}
