package com.sncial.farmtweaks;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class HoeEfficiencySpeedTest {
    @Test
    void efficiency_uses_the_vanilla_quadratic_speed_bonus() {
        assertEquals(0, HoeEfficiencySpeed.bonus(0));
        assertEquals(2, HoeEfficiencySpeed.bonus(1));
        assertEquals(5, HoeEfficiencySpeed.bonus(2));
        assertEquals(26, HoeEfficiencySpeed.bonus(5));
    }

    @Test
    void custom_bonus_only_fills_the_gap_for_blocks_a_hoe_is_not_effective_against() {
        assertTrue(HoeEfficiencySpeed.needsCustomBonus(1.0F));
        assertFalse(HoeEfficiencySpeed.needsCustomBonus(2.0F));
        assertFalse(HoeEfficiencySpeed.needsCustomBonus(9.0F));
    }
}
