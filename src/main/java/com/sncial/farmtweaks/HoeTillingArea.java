package com.sncial.farmtweaks;

import java.util.LinkedHashSet;
import java.util.Set;

final class HoeTillingArea {
    private HoeTillingArea() {}

    static Set<FootprintBoundary.Cell> cells(int centerX, int centerZ, boolean sneaking, int sideLength) {
        Set<FootprintBoundary.Cell> cells = new LinkedHashSet<>();
        int size = sneaking ? 1 : Math.max(1, sideLength);
        int startOffset = -((size - 1) / 2);
        for (int dx = startOffset; dx < startOffset + size; dx++) {
            for (int dz = startOffset; dz < startOffset + size; dz++) {
                cells.add(new FootprintBoundary.Cell(centerX + dx, centerZ + dz));
            }
        }
        return cells;
    }
}
