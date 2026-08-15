package com.sncial.farmtweaks;

enum SeedBagAoeShape {
    SQUARE,
    RADIAL;

    boolean includes(int dx, int dz, int radius) {
        return this == SQUARE || Math.abs(dx) + Math.abs(dz) <= radius;
    }

    SeedBagAoeShape cycle(boolean forward) {
        return this == SQUARE ? RADIAL : SQUARE;
    }

    String translationKey() {
        return "tooltip.farmtweaks.seed_bag_shape." + name().toLowerCase();
    }

    static SeedBagAoeShape fromConfig(String value) {
        return "radial".equalsIgnoreCase(value) ? RADIAL : SQUARE;
    }
}
