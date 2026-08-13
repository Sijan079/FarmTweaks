package com.sncial.farmtweaks;

import java.util.HashSet;
import java.util.Set;

final class FootprintBoundary {
    private FootprintBoundary() {}

    static Set<Edge> edges(Set<Cell> cells) {
        Set<Edge> edges = new HashSet<>();
        for (Cell cell : cells) {
            addIfExposed(edges, cells, cell, 0, -1, new Edge(cell.x, cell.z, cell.x + 1, cell.z));
            addIfExposed(edges, cells, cell, 1, 0, new Edge(cell.x + 1, cell.z, cell.x + 1, cell.z + 1));
            addIfExposed(edges, cells, cell, 0, 1, new Edge(cell.x + 1, cell.z + 1, cell.x, cell.z + 1));
            addIfExposed(edges, cells, cell, -1, 0, new Edge(cell.x, cell.z + 1, cell.x, cell.z));
        }
        return edges;
    }

    private static void addIfExposed(Set<Edge> edges, Set<Cell> cells, Cell cell, int dx, int dz, Edge edge) {
        if (!cells.contains(new Cell(cell.x + dx, cell.z + dz))) {
            edges.add(edge);
        }
    }

    record Cell(int x, int z) {}

    record Edge(int startX, int startZ, int endX, int endZ) {}
}
