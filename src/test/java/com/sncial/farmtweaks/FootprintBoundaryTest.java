package com.sncial.farmtweaks;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Set;

import org.junit.jupiter.api.Test;

class FootprintBoundaryTest {
    @Test
    void square_footprint_has_only_its_outer_edges() {
        Set<FootprintBoundary.Cell> cells = Set.of(
                new FootprintBoundary.Cell(0, 0), new FootprintBoundary.Cell(1, 0),
                new FootprintBoundary.Cell(0, 1), new FootprintBoundary.Cell(1, 1));

        assertEquals(8, FootprintBoundary.edges(cells).size());
    }

    @Test
    void adjacent_cells_do_not_draw_an_internal_edge() {
        Set<FootprintBoundary.Cell> cells = Set.of(
                new FootprintBoundary.Cell(0, 0), new FootprintBoundary.Cell(1, 0));

        assertEquals(6, FootprintBoundary.edges(cells).size());
    }
}
