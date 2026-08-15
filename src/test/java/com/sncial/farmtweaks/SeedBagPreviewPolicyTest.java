package com.sncial.farmtweaks;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SeedBagPreviewPolicyTest {
    @Test
    void preview_requires_empty_farmland_as_the_target() {
        assertTrue(SeedBagPreviewPolicy.canPreview(true, true));
        assertFalse(SeedBagPreviewPolicy.canPreview(false, true));
        assertFalse(SeedBagPreviewPolicy.canPreview(true, false));
    }
}
