package com.sncial.farmtweaks;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class HarvestFortunePolicyTest {
    @Test
    void grants_the_farmtweaks_bonus_only_to_crops_replanted_with_seed_items() {
        assertTrue(HarvestFortunePolicy.hasExtraBonus(true, 1));
        assertFalse(HarvestFortunePolicy.hasExtraBonus(false, 3));
    }

    @Test
    void grants_the_bonus_to_cocoa_when_its_vanilla_loot_ignores_fortune() {
        assertTrue(HarvestFortunePolicy.hasExtraBonus(false, true, 1));
        assertFalse(HarvestFortunePolicy.hasExtraBonus(false, true, 0));
    }

    @Test
    void does_not_add_a_bonus_without_fortune() {
        assertFalse(HarvestFortunePolicy.hasExtraBonus(true, 0));
    }
}
