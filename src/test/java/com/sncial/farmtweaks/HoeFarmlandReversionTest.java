package com.sncial.farmtweaks;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class HoeFarmlandReversionTest {
    @Test
    void farmland_reversion_uses_the_vanilla_collision_safe_transition() {
        assertTrue(HoeTillingMode.UNTILL.usesVanillaFarmlandReversion());
    }
}
