package com.sncial.farmtweaks;

import java.util.LinkedHashSet;
import java.util.Set;

final class HoeTillingArea {
    private HoeTillingArea() {}

    static Set<FootprintBoundary.Cell> cells(int centerX, int centerZ, boolean sneaking, int radius) {
        Set<FootprintBoundary.Cell> cells = new LinkedHashSet<>();
        int resolvedRadius = sneaking ? 0 : Math.max(0, radius);
        for (int dx = -resolvedRadius; dx <= resolvedRadius; dx++) {
            for (int dz = -resolvedRadius; dz <= resolvedRadius; dz++) {
                cells.add(new FootprintBoundary.Cell(centerX + dx, centerZ + dz));
            }
        }
        return cells;
    }
}
