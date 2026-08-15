package com.sncial.farmtweaks;

enum SeedBagTier {
    BASIC(4 * 64, 0, 0),
    GOLD(9 * 64, 1, 2, 0xFFFFF2A6, 0xFFF5C542, 0xFFC98718, 0xFF7A3E00),
    DIAMOND(18 * 64, 2, 3, 0xFFE7FFFF, 0xFF59E1DD, 0xFF139AA6, 0xFF07536B);

    private final int capacity;
    private final int squarePlantingRadius;
    private final int radialPlantingRadius;
    private final int[] guiMarkerPixels;

    SeedBagTier(int capacity, int squarePlantingRadius, int radialPlantingRadius) {
        this(capacity, squarePlantingRadius, radialPlantingRadius, new int[0]);
    }

    SeedBagTier(int capacity, int squarePlantingRadius, int radialPlantingRadius, int... guiMarkerPixels) {
        this.capacity = capacity;
        this.squarePlantingRadius = squarePlantingRadius;
        this.radialPlantingRadius = radialPlantingRadius;
        this.guiMarkerPixels = guiMarkerPixels;
    }

    int capacity() {
        return capacity;
    }

    int plantingRadius(boolean sneaking, SeedBagAoeShape shape) {
        if (sneaking) {
            return 0;
        }
        return shape == SeedBagAoeShape.RADIAL ? radialPlantingRadius : squarePlantingRadius;
    }

    int[] guiMarkerPixels() {
        return guiMarkerPixels.clone();
    }
}
