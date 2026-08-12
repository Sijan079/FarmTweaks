package com.sncial.farmtweaks;

enum SeedBagTier {
    BASIC(4 * 64, 0),
    GOLD(9 * 64, 1, 0xFFFFF2A6, 0xFFF5C542, 0xFFC98718, 0xFF7A3E00),
    DIAMOND(18 * 64, 2, 0xFFE7FFFF, 0xFF59E1DD, 0xFF139AA6, 0xFF07536B);

    private final int capacity;
    private final int plantingRadius;
    private final int[] guiMarkerPixels;

    SeedBagTier(int capacity, int plantingRadius) {
        this(capacity, plantingRadius, new int[0]);
    }

    SeedBagTier(int capacity, int plantingRadius, int... guiMarkerPixels) {
        this.capacity = capacity;
        this.plantingRadius = plantingRadius;
        this.guiMarkerPixels = guiMarkerPixels;
    }

    int capacity() {
        return capacity;
    }

    int plantingRadius(boolean sneaking) {
        return sneaking ? 0 : plantingRadius;
    }

    int[] guiMarkerPixels() {
        return guiMarkerPixels.clone();
    }
}
