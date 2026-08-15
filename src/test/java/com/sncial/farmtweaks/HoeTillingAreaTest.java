package com.sncial.farmtweaks;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class HoeTillingAreaTest {
    @Test
    void normal_hoe_use_targets_the_requested_square_area() {
        var cells = HoeTillingArea.cells(10, 20, false, 2);

        assertEquals(25, cells.size());
        assertTrue(cells.contains(new FootprintBoundary.Cell(8, 18)));
        assertTrue(cells.contains(new FootprintBoundary.Cell(12, 22)));
    }

    @Test
    void sneaking_limits_the_hoe_to_the_targeted_block() {
        assertEquals(1, HoeTillingArea.cells(10, 20, true, 10).size());
    }

    @Test
    void one_added_ring_is_centered_on_the_clicked_block() {
        var cells = HoeTillingArea.cells(10, 20, false, 1);

        assertEquals(9, cells.size());
        assertTrue(cells.contains(new FootprintBoundary.Cell(9, 19)));
        assertTrue(cells.contains(new FootprintBoundary.Cell(10, 20)));
        assertTrue(cells.contains(new FootprintBoundary.Cell(11, 21)));
        assertFalse(cells.contains(new FootprintBoundary.Cell(12, 22)));
    }
}
