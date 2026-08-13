package com.sncial.farmtweaks;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class VanillaHarvestCropAgesTest {
    @Test
    void recognizesNetherWartOnlyAtItsFinalAge() {
        assertFalse(VanillaHarvestCropAges.isMature(2, VanillaHarvestCropAges.NETHER_WART_MAX_AGE));
        assertTrue(VanillaHarvestCropAges.isMature(3, VanillaHarvestCropAges.NETHER_WART_MAX_AGE));
    }

    @Test
    void recognizesCocoaOnlyAtItsFinalAge() {
        assertFalse(VanillaHarvestCropAges.isMature(1, VanillaHarvestCropAges.COCOA_MAX_AGE));
        assertTrue(VanillaHarvestCropAges.isMature(2, VanillaHarvestCropAges.COCOA_MAX_AGE));
    }
}
