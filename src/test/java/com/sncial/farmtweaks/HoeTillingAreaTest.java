package com.sncial.farmtweaks;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class HoeTillingAreaTest {
    @Test
    void normal_hoe_use_targets_the_requested_square_area() {
        var cells = HoeTillingArea.cells(10, 20, false, 5);

        assertEquals(25, cells.size());
        assertTrue(cells.contains(new FootprintBoundary.Cell(8, 18)));
        assertTrue(cells.contains(new FootprintBoundary.Cell(12, 22)));
    }

    @Test
    void sneaking_limits_the_hoe_to_the_targeted_block() {
        assertEquals(1, HoeTillingArea.cells(10, 20, true, 10).size());
    }

    @Test
    void even_sized_areas_start_at_the_clicked_block_or_one_block_before_it() {
        var stone = HoeTillingArea.cells(10, 20, false, 2);
        var diamond = HoeTillingArea.cells(10, 20, false, 4);

        assertTrue(stone.contains(new FootprintBoundary.Cell(10, 20)));
        assertTrue(stone.contains(new FootprintBoundary.Cell(11, 21)));
        assertTrue(diamond.contains(new FootprintBoundary.Cell(9, 19)));
        assertTrue(diamond.contains(new FootprintBoundary.Cell(12, 22)));
    }
}
