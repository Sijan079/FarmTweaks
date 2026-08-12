package com.sncial.farmtweaks;

enum SeedBagAoeShape {
    SQUARE,
    RADIAL;

    boolean includes(int dx, int dz, int radius) {
        return this == SQUARE || Math.abs(dx) + Math.abs(dz) <= radius;
    }

    static SeedBagAoeShape fromConfig(String value) {
        return "radial".equalsIgnoreCase(value) ? RADIAL : SQUARE;
    }
}
